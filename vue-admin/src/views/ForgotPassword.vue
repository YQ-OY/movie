<template>
  <div class="login">
    <div class="login-background">
      <div class="login-background__circle login-background__circle--1"></div>
      <div class="login-background__circle login-background__circle--2"></div>
      <div class="login-background__circle login-background__circle--3"></div>
    </div>

    <div class="login-card">
      <div class="login-card__header">
        <img class="login-logo" src="../assets/img/logo.png" alt="厅有独钟" />
        <div class="login-title">厅有独钟</div>
        <div class="login-subtitle">找回密码</div>
      </div>

      <div class="login-card__body">
        <el-steps :active="step - 1" finish-status="success" align-center class="forgot-steps">
          <el-step title="身份验证" />
          <el-step title="重置密码" />
        </el-steps>

        <!-- 第一步：身份验证 -->
        <div v-show="step === 1" class="step-content">
          <el-form :model="form" :rules="step1Rules" ref="step1FormRef">
            <el-form-item prop="role">
              <el-select v-model="form.role" placeholder="请选择账号身份" size="large" style="width: 100%">
                <el-option label="系统管理员" value="admin" />
                <el-option label="工作人员" value="worker" />
              </el-select>
            </el-form-item>

            <el-form-item prop="phone">
              <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" prefix-icon="Phone" size="large" />
            </el-form-item>

            <el-form-item prop="captchaInput">
              <div class="input-row">
                <el-input v-model="form.captchaInput" placeholder="请输入图中数字" maxlength="4" prefix-icon="Picture"
                  size="large" class="flex-1" />
                <div class="captcha-box" @click="refreshCaptcha" title="点击刷新验证码"
                  :class="{ 'captcha-box--loading': captchaLoading }">
                  <div v-if="captchaSvg" class="captcha-svg" v-html="captchaSvg"></div>
                  <span v-else class="captcha-loading">加载中</span>
                </div>
                <el-button class="refresh-btn" @click="refreshCaptcha" :disabled="captchaLoading" circle>
                  <el-icon>
                    <Refresh />
                  </el-icon>
                </el-button>
              </div>
            </el-form-item>

            <el-form-item prop="smsCode">
              <div class="input-row">
                <el-input v-model="form.smsCode" placeholder="请输入短信验证码" maxlength="6" prefix-icon="Message" size="large"
                  class="flex-1" />
                <el-button class="sms-btn" :disabled="smsCountdown > 0 || sendingCode" :loading="sendingCode"
                  @click="handleSendCode">
                  {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
          </el-form>

          <el-alert v-if="demoHintVisible" type="info" :closable="false" show-icon title="演示环境短信验证码为 123456"
            class="demo-alert" />

          <el-button type="primary" size="large" class="submit-btn" :loading="verifying" @click="goToStep2">
            下一步
          </el-button>
        </div>

        <!-- 第二步：重置密码 -->
        <div v-show="step === 2" class="step-content">
          <div class="verified-badge">
            <el-icon class="verified-icon">
              <CircleCheck />
            </el-icon>
            <span>已验证手机号 {{ maskedPhone }}</span>
          </div>

          <el-form :model="form" :rules="step2Rules" ref="step2FormRef">
            <el-form-item prop="newPassword">
              <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password
                prefix-icon="Lock" size="large" />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password
                prefix-icon="Lock" size="large" @keyup.enter="submitReset" />
            </el-form-item>
          </el-form>

          <div class="button-group">
            <el-button class="back-btn" size="large" @click="backToStep1">
              上一步
            </el-button>
            <el-button type="primary" size="large" class="submit-btn" :loading="submitting" @click="submitReset">
              重置密码
            </el-button>
          </div>
        </div>
      </div>

      <div class="login-card__footer">
        <router-link class="back-link" to="/login">返回登录页</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { getForgotCaptcha, sendForgotCode, verifyForgotSms, resetPasswordByPhone } from "@/api/user"
import { isValidMobileCN } from "@/utils/validate"
import { Refresh, CircleCheck, Phone, Message, Lock, Picture } from "@element-plus/icons-vue"

export default {
  components: { Refresh, CircleCheck, Phone, Message, Lock, Picture },
  data() {
    const validatePhone = (rule, value, callback) => {
      if (!isValidMobileCN(value)) {
        callback(new Error('请输入11位有效中国大陆手机号'))
      } else {
        callback()
      }
    }

    const validateCaptcha = (rule, value, callback) => {
      if (!/^\d{4}$/.test(value)) {
        callback(new Error('请输入4位数字验证码'))
      } else {
        callback()
      }
    }

    const validateSmsCode = (rule, value, callback) => {
      if (!/^\d{6}$/.test(value)) {
        callback(new Error('请输入6位数字验证码'))
      } else {
        callback()
      }
    }

    const validatePassword = (rule, value, callback) => {
      if (!value || value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      step: 1,
      form: {
        role: 'admin',
        phone: '',
        captchaInput: '',
        smsCode: '',
        newPassword: '',
        confirmPassword: ''
      },
      step1Rules: {
        role: [
          { required: true, message: '请选择账号身份', trigger: 'change' }
        ],
        phone: [
          { required: true, validator: validatePhone, trigger: 'blur' }
        ],
        captchaInput: [
          { required: true, validator: validateCaptcha, trigger: 'blur' }
        ],
        smsCode: [
          { required: true, validator: validateSmsCode, trigger: 'blur' }
        ]
      },
      step2Rules: {
        newPassword: [
          { required: true, validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      maskedPhone: '',
      captchaKey: '',
      captchaSvg: '',
      captchaLoading: false,
      sendingCode: false,
      verifying: false,
      submitting: false,
      smsCountdown: 0,
      demoHintVisible: false,
      smsSent: false,
      countdownTimer: null
    }
  },
  mounted() {
    this.refreshCaptcha()
  },
  beforeUnmount() {
    if (this.countdownTimer) clearInterval(this.countdownTimer)
  },
  watch: {
    'form.role'() {
      this.refreshCaptcha()
    }
  },
  methods: {
    maskPhone(phone) {
      const p = String(phone || "").trim()
      if (p.length < 7) return p
      return `${p.slice(0, 3)}****${p.slice(-4)}`
    },
    async refreshCaptcha() {
      if (!this.form.role) return
      this.captchaLoading = true
      this.form.captchaInput = ""
      try {
        const res = await getForgotCaptcha(this.form.role)
        if (res?.success && res.data) {
          this.captchaKey = res.data.captchaKey
          this.captchaSvg = res.data.captchaSvg
        }
      } finally {
        this.captchaLoading = false
      }
    },
    startCountdown() {
      this.smsCountdown = 60
      if (this.countdownTimer) clearInterval(this.countdownTimer)
      this.countdownTimer = setInterval(() => {
        if (this.smsCountdown <= 1) {
          this.smsCountdown = 0
          clearInterval(this.countdownTimer)
          this.countdownTimer = null
        } else {
          this.smsCountdown -= 1
        }
      }, 1000)
    },
    async handleSendCode() {
      // 先验证表单
      const valid = await this.$refs.step1FormRef.validateField(['role', 'phone', 'captchaInput'])
      if (!valid) return

      this.sendingCode = true
      try {
        const res = await sendForgotCode(this.form.role, {
          phone: this.form.phone.trim(),
          captchaKey: this.captchaKey,
          captchaInput: this.form.captchaInput,
        })
        if (res?.success) {
          this.demoHintVisible = true
          this.smsSent = true
          this.maskedPhone = res.data?.maskedPhone || this.maskPhone(this.form.phone)
          this.startCountdown()
          this.$message.success('验证码已发送')
        } else if (res?.msg?.includes("图形验证码")) {
          await this.refreshCaptcha()
        }
      } catch {
        await this.refreshCaptcha()
      } finally {
        this.sendingCode = false
      }
    },
    async goToStep2() {
      // 先验证表单
      const valid = await this.$refs.step1FormRef.validate()
      if (!valid) return

      if (!this.smsSent) {
        this.$message.warning("请先点击「获取验证码」")
        return
      }

      this.verifying = true
      try {
        const res = await verifyForgotSms(this.form.role, {
          phone: this.form.phone.trim(),
          smsCode: this.form.smsCode.trim(),
        })
        if (res?.success) {
          this.maskedPhone = this.maskedPhone || this.maskPhone(this.form.phone)
          this.step = 2
        }
      } finally {
        this.verifying = false
      }
    },
    backToStep1() {
      this.step = 1
      this.form.newPassword = ""
      this.form.confirmPassword = ""
      this.$refs.step2FormRef?.resetFields()
    },
    async submitReset() {
      // 先验证表单
      const valid = await this.$refs.step2FormRef.validate()
      if (!valid) return

      this.submitting = true
      try {
        const res = await resetPasswordByPhone(this.form.role, {
          phone: this.form.phone.trim(),
          smsCode: this.form.smsCode.trim(),
          newPassword: this.form.newPassword,
        })
        if (res?.success) {
          this.$message.success('密码重置成功，请使用新密码登录')
          this.$router.push("/login")
        }
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
/* 登录页面背景（与登录页完全一致） */
.login {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 50%, #bae6fd 100%);
  position: relative;
}

.login-background {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.login-background__circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(99, 102, 241, 0.05));
  animation: float 20s ease-in-out infinite;
}

.login-background__circle--1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -200px;
  animation-delay: 0s;
}

.login-background__circle--2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
  animation-delay: -5s;
}

.login-background__circle--3 {
  width: 300px;
  height: 300px;
  top: 50%;
  left: 20%;
  animation-delay: -10s;
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }

  50% {
    transform: translateY(-30px) rotate(10deg);
  }
}

/* 登录卡片（与登录页完全一致） */
.login-card {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 480px;
  padding: 40px 50px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.8);
  animation: cardEnter 0.6s ease-out;
}

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translate(-50%, -45%);
  }

  to {
    opacity: 1;
    transform: translate(-50%, -50%);
  }
}

/* 卡片头部（与登录页完全一致） */
.login-card__header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  width: 64px;
  height: 64px;
  margin-bottom: 12px;
  animation: logoBounce 2s ease-in-out infinite;
}

@keyframes logoBounce {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-5px);
  }
}

.login-title {
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 4px;
  letter-spacing: 2px;
}

.login-subtitle {
  font-size: 14px;
  color: #64748b;
  letter-spacing: 1px;
}

/* 卡片主体 */
.login-card__body {
  margin-bottom: 24px;
}

.step-content {
  transition: all 0.3s ease;
}

/* 步骤条样式 */
.forgot-steps {
  margin-bottom: 32px;
}

.forgot-steps :deep(.el-step__title) {
  font-size: 13px;
  font-weight: 600;
}

/* 输入框样式（与登录页完全一致） */
.login-card__body :deep(.el-input__wrapper) {
  height: 52px;
  border-radius: 12px;
  box-shadow: none;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.login-card__body :deep(.el-input__wrapper:hover) {
  border-color: #93c5fd;
}

.login-card__body :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.login-card__body :deep(.el-input__inner) {
  font-size: 15px;
  padding-left: 8px;
}

.login-card__body :deep(.el-select__wrapper) {
  height: 52px;
  border-radius: 12px;
  box-shadow: none;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.login-card__body :deep(.el-select__wrapper:hover) {
  border-color: #93c5fd;
}

.login-card__body :deep(.el-select__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

/* 输入行布局 */
.input-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.flex-1 {
  flex: 1;
}

/* 验证码区域 */
.captcha-box {
  width: 120px;
  height: 52px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.captcha-box:hover {
  border-color: #93c5fd;
}

.captcha-box--loading {
  cursor: not-allowed;
  opacity: 0.7;
}

.captcha-svg :deep(svg) {
  display: block;
  width: 120px;
  height: 52px;
}

.captcha-loading {
  font-size: 12px;
  color: #94a3b8;
}

.refresh-btn {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.refresh-btn:hover:not(:disabled) {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #409eff;
}

/* 短信验证码按钮 */
.sms-btn {
  width: 120px;
  height: 52px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
  font-weight: 500;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.sms-btn:hover:not(:disabled) {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #409eff;
}

.sms-btn:disabled {
  background: #f8fafc;
  border-color: #e2e8f0;
  color: #94a3b8;
}

/* 演示提示 */
.demo-alert {
  margin: 16px 0;
  border-radius: 12px;
}

/* 验证成功提示 */
.verified-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  margin-bottom: 24px;
  border-radius: 12px;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
  color: #059669;
  font-size: 14px;
  font-weight: 500;
}

.verified-icon {
  font-size: 18px;
}

/* 按钮组 */
.button-group {
  display: flex;
  gap: 12px;
}

.back-btn {
  flex: 0 0 120px;
  height: 52px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #334155;
}

/* 提交按钮（与登录页完全一致） */
.submit-btn {
  flex: 1;
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
  border: none;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(64, 158, 255, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 卡片底部 */
.login-card__footer {
  text-align: center;
}

.back-link {
  color: #409eff;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.2s ease;
}

.back-link:hover {
  color: #337ecc;
}

/* 响应式适配 */
@media (max-width: 576px) {
  .login-card {
    width: 90%;
    padding: 30px 24px;
  }

  .login-title {
    font-size: 24px;
  }

  .login-subtitle {
    font-size: 13px;
  }

  .input-row {
    flex-wrap: wrap;
  }

  .captcha-box,
  .refresh-btn,
  .sms-btn {
    width: calc(50% - 6px);
  }

  .button-group {
    flex-direction: column;
  }

  .back-btn {
    flex: none;
    width: 100%;
  }
}
</style>
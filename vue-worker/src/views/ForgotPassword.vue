<template>
  <div class="login">
    <div class="login-form">
      <div class="login-form-header">
        <img style="width: 75px; height: 75px;float: left;padding-right: 30px;" src="../assets/img/logo.png" alt="" />
        <div class="login-form-text">厅有独钟 - 客服系统</div>
      </div>

      <div class="forgot-body">
        <p class="forgot-title">找回密码</p>
        <p class="forgot-sub">{{ step === 1 ? '验证手机号与验证码' : '设置新的登录密码' }}</p>

        <el-steps :active="step - 1" finish-status="finish" align-center class="forgot-steps">
          <el-step title="身份验证" />
          <el-step title="重置密码" />
        </el-steps>

        <!-- 第一步 -->
        <div v-show="step === 1">
          <p class="field-label">手机号</p>
          <el-input class="login-form-input" v-model="phone" placeholder="请输入11位手机号" maxlength="11" />

          <p class="field-label">图形验证码</p>
          <div class="captcha-row">
            <div class="captcha-img-wrap" @click="refreshCaptcha" title="点击刷新">
              <div v-if="captchaSvg" class="captcha-svg" v-html="captchaSvg" />
              <span v-else class="captcha-loading">加载中…</span>
            </div>
            <el-button class="btn-soft" @click="refreshCaptcha" :disabled="captchaLoading">
              <el-icon><Refresh /></el-icon>
            </el-button>
            <el-input v-model="captchaInput" class="login-form-input captcha-input" placeholder="请输入图中数字" maxlength="4" />
          </div>

          <p class="field-label">短信验证码</p>
          <div class="sms-row">
            <el-input class="login-form-input" v-model="smsCode" placeholder="请输入短信验证码" maxlength="6" />
            <el-button class="btn-soft sms-btn" :disabled="smsCountdown > 0 || sendingCode" :loading="sendingCode" @click="handleSendCode">
              {{ smsCountdown > 0 ? `${smsCountdown}秒后重发` : '获取验证码' }}
            </el-button>
          </div>

          <el-alert v-if="demoHintVisible" type="info" :closable="false" show-icon title="演示环境短信验证码为 123456" class="demo-alert" />

          <el-button :loading="verifying" @click="goToStep2" class="login-form-button" type="primary">下一步</el-button>
        </div>

        <!-- 第二步 -->
        <div v-show="step === 2">
          <div class="verified-phone">
            <el-icon><CircleCheck /></el-icon>
            <span>已验证手机号 {{ maskedPhone }}</span>
          </div>

          <p class="field-label">新密码</p>
          <el-input class="login-form-input" v-model="newPassword" type="password" placeholder="至少6位" show-password />

          <p class="field-label">确认密码</p>
          <el-input class="login-form-input" v-model="confirmPassword" type="password" placeholder="再次输入新密码" show-password @keyup.enter="submitReset" />

          <div class="step2-actions">
            <el-button class="btn-soft step2-back" @click="backToStep1">上一步</el-button>
            <el-button :loading="submitting" @click="submitReset" class="login-form-button step2-submit" type="primary">重置密码</el-button>
          </div>
        </div>
      </div>

      <div class="login-form-footer">
        <router-link class="back-link" to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { getForgotCaptcha, sendForgotCode, verifyForgotSms, resetPasswordByPhone } from "@/api/worker"
import { isValidMobileCN } from "@/utils/validate"
import { Refresh, CircleCheck } from "@element-plus/icons-vue"

export default {
  components: { Refresh, CircleCheck },
  data() {
    return {
      step: 1,
      phone: "",
      maskedPhone: "",
      captchaKey: "",
      captchaSvg: "",
      captchaInput: "",
      captchaLoading: false,
      smsCode: "",
      newPassword: "",
      confirmPassword: "",
      sendingCode: false,
      verifying: false,
      submitting: false,
      smsCountdown: 0,
      demoHintVisible: false,
      smsSent: false,
      countdownTimer: null,
    }
  },
  mounted() {
    this.refreshCaptcha()
  },
  beforeUnmount() {
    if (this.countdownTimer) clearInterval(this.countdownTimer)
  },
  methods: {
    maskPhone(phone) {
      const p = String(phone || "").trim()
      if (p.length < 7) return p
      return `${p.slice(0, 3)}****${p.slice(-4)}`
    },
    async refreshCaptcha() {
      this.captchaLoading = true
      this.captchaInput = ""
      try {
        const res = await getForgotCaptcha()
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
      if (!isValidMobileCN(this.phone)) {
        this.$message.warning("请输入11位有效中国大陆手机号")
        return
      }
      if (!/^\d{4}$/.test(this.captchaInput)) {
        this.$message.warning("请输入图中4位数字")
        return
      }
      this.sendingCode = true
      try {
        const res = await sendForgotCode({
          phone: this.phone.trim(),
          captchaKey: this.captchaKey,
          captchaInput: this.captchaInput,
        })
        if (res?.success) {
          this.demoHintVisible = true
          this.smsSent = true
          this.maskedPhone = res.data?.maskedPhone || this.maskPhone(this.phone)
          this.startCountdown()
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
      if (!isValidMobileCN(this.phone)) {
        this.$message.warning("请输入11位有效中国大陆手机号")
        return
      }
      if (!this.smsSent) {
        this.$message.warning("请先点击「获取验证码」")
        return
      }
      if (!this.smsCode?.trim()) {
        this.$message.warning("请输入短信验证码")
        return
      }
      this.verifying = true
      try {
        const res = await verifyForgotSms({
          phone: this.phone.trim(),
          smsCode: this.smsCode.trim(),
        })
        if (res?.success) {
          this.maskedPhone = this.maskedPhone || this.maskPhone(this.phone)
          this.step = 2
        }
      } finally {
        this.verifying = false
      }
    },
    backToStep1() {
      this.step = 1
      this.newPassword = ""
      this.confirmPassword = ""
    },
    submitReset() {
      if (!this.newPassword || this.newPassword.length < 6) {
        this.$message.warning("新密码不能少于6位")
        return
      }
      if (this.newPassword !== this.confirmPassword) {
        this.$message.warning("两次输入的密码不一致")
        return
      }
      this.submitting = true
      resetPasswordByPhone({
        phone: this.phone.trim(),
        smsCode: this.smsCode.trim(),
        newPassword: this.newPassword,
      })
        .then((res) => {
          if (res?.success) {
            this.$router.push("/login")
          }
        })
        .finally(() => {
          this.submitting = false
        })
    },
  },
}
</script>

<style scoped>
.login {
  width: 100%;
  height: 100%;
  background: #FFFFFF;
}

.login-form {
  width: 520px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -52%);
  letter-spacing: 1px;
}

.login-form-header {
  height: 20px;
  padding-left: 40px;
  padding-bottom: 72px;
}

.login-form-text {
  color: #000000;
  font-weight: bold;
  font-size: 30px;
  padding-top: 15px;
}

.forgot-body {
  color: #91949c;
  font-weight: bolder;
}

.forgot-title {
  margin: 0 0 6px;
  color: #303133;
  font-size: 18px;
  font-weight: 700;
}

.forgot-sub {
  margin: 0 0 16px;
  font-size: 13px;
  font-weight: 500;
  color: #909399;
}

.field-label {
  margin: 12px 0 6px;
  font-size: 14px;
  color: #606266;
  font-weight: 600;
}

.login-form-input {
  margin-bottom: 4px;
}

.login-form-button {
  border-radius: 3px;
  width: 100%;
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 2px;
  height: 52px;
  background: #5a84fd;
  box-shadow: 0 5px 30px rgb(0 66 8.5%);
  margin-top: 16px;
  border: none;
}

.login-form-footer {
  font-weight: bolder;
  color: #91949c;
  padding-top: 20px;
  text-align: center;
}

.back-link {
  color: #5a84fd;
  font-size: 14px;
  text-decoration: none;
}

:deep(.el-input__inner) {
  height: 48px;
}

.forgot-steps {
  margin-bottom: 20px;
  --el-color-primary: #5a84fd;
}

.forgot-steps :deep(.el-step__title) {
  font-size: 13px;
  font-weight: 600;
}

.forgot-steps :deep(.el-step__head.is-wait .el-step__icon),
.forgot-steps :deep(.el-step__head.is-wait .el-step__icon.is-text) {
  background: #f0f2f5 !important;
  border-color: #dcdfe6 !important;
  color: #909399 !important;
}

.forgot-steps :deep(.el-step__head.is-wait .el-step__icon-inner) {
  color: #909399 !important;
}

.forgot-steps :deep(.el-step__title.is-wait) {
  color: #909399;
}

.forgot-steps :deep(.el-step__head.is-process .el-step__icon),
.forgot-steps :deep(.el-step__head.is-process .el-step__icon.is-text),
.forgot-steps :deep(.el-step__head.is-finish .el-step__icon),
.forgot-steps :deep(.el-step__head.is-finish .el-step__icon.is-text) {
  background: #5a84fd !important;
  border-color: #5a84fd !important;
  color: #fff !important;
}

.forgot-steps :deep(.el-step__head.is-process .el-step__icon-inner),
.forgot-steps :deep(.el-step__head.is-finish .el-step__icon-inner) {
  color: #fff !important;
}

.forgot-steps :deep(.el-step__title.is-process),
.forgot-steps :deep(.el-step__title.is-finish) {
  color: #5a84fd;
  font-weight: 700;
}

.forgot-steps :deep(.el-step__head.is-finish .el-step__line) {
  background-color: #5a84fd;
}

.btn-soft {
  background-color: #f0f2f5 !important;
  border: 1px solid #dcdfe6 !important;
  color: #606266 !important;
  font-weight: 600;
}

.btn-soft:hover:not(:disabled) {
  background-color: #e8eaed !important;
  border-color: #c0c4cc !important;
  color: #303133 !important;
}

.captcha-row,
.sms-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.captcha-img-wrap {
  width: 120px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.captcha-svg :deep(svg) {
  display: block;
  width: 120px;
  height: 40px;
}

.captcha-loading {
  font-size: 12px;
  color: #909399;
}

.captcha-input {
  flex: 1;
  margin-bottom: 0 !important;
}

.sms-row .login-form-input {
  flex: 1;
  margin-bottom: 0 !important;
}

.sms-btn {
  flex-shrink: 0;
  height: 48px;
  white-space: nowrap;
}

.demo-alert {
  margin: 10px 0;
}

.verified-phone {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  margin-bottom: 12px;
  border-radius: 6px;
  background: rgba(90, 132, 253, 0.08);
  border: 1px solid rgba(90, 132, 253, 0.2);
  color: #5a84fd;
  font-size: 13px;
  font-weight: 600;
}

.step2-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.step2-back {
  flex: 0 0 100px;
  height: 52px;
}

.step2-submit {
  flex: 1;
  margin-top: 0 !important;
}
</style>

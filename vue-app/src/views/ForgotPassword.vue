<template>
  <div class="auth-page">
    <div class="auth-bg" aria-hidden="true" />
    <div class="auth-shell">
      <div class="auth-brand">
        <div class="auth-brand__glow" />
        <img class="auth-brand__logo" src="../assets/img/logo.png" alt="厅有独钟" />
        <h1 class="auth-brand__title">厅有独钟</h1>
        <p class="auth-brand__sub">安全验证 · 找回您的账号</p>
      </div>

      <div class="auth-card">
        <div class="auth-card__head">
          <h2 class="auth-card__title">找回密码</h2>
          <p class="auth-card__hint">{{ step === 1 ? '验证手机号与验证码' : '设置新的登录密码' }}</p>
        </div>

        <el-steps :active="step - 1" finish-status="finish" align-center class="auth-steps">
          <el-step title="身份验证" />
          <el-step title="重置密码" />
        </el-steps>

        <!-- 第一步：手机号 + 图形码 + 短信码 -->
        <el-form
          v-show="step === 1"
          class="auth-form"
          label-position="top"
          @submit.prevent="goToStep2"
        >
          <el-form-item label="手机号">
            <el-input
              v-model="phone"
              size="large"
              placeholder="请输入11位手机号"
              clearable
              maxlength="11"
            >
              <template #prefix>
                <el-icon class="auth-input-icon"><Iphone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="图形验证码">
            <div class="captcha-row">
              <div class="captcha-img-wrap" @click="refreshCaptcha" title="点击刷新">
                <div v-if="captchaSvg" class="captcha-svg" v-html="captchaSvg" />
                <span v-else class="captcha-loading">加载中…</span>
              </div>
              <el-button class="auth-btn-soft captcha-refresh" @click="refreshCaptcha" :disabled="captchaLoading">
                <el-icon><Refresh /></el-icon>
              </el-button>
              <el-input
                v-model="captchaInput"
                size="large"
                placeholder="图中数字"
                maxlength="4"
                class="captcha-input"
              />
            </div>
          </el-form-item>

          <el-form-item label="短信验证码">
            <div class="sms-row">
              <el-input
                v-model="smsCode"
                size="large"
                placeholder="请输入短信验证码"
                maxlength="6"
                clearable
              >
                <template #prefix>
                  <el-icon class="auth-input-icon"><Message /></el-icon>
                </template>
              </el-input>
              <el-button
                size="large"
                class="auth-btn-soft sms-btn"
                :disabled="smsCountdown > 0 || sendingCode"
                :loading="sendingCode"
                @click="handleSendCode"
              >
                {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <el-alert
            v-if="demoHintVisible"
            type="info"
            :closable="false"
            show-icon
            class="demo-alert"
            title="演示环境短信验证码为 123456"
          />

          <el-button
            class="auth-submit"
            type="primary"
            size="large"
            :loading="verifying"
            @click="goToStep2"
          >
            下一步
          </el-button>
        </el-form>

        <!-- 第二步：新密码 -->
        <el-form
          v-show="step === 2"
          class="auth-form"
          label-position="top"
          @submit.prevent="submitReset"
        >
          <div class="verified-phone">
            <el-icon><CircleCheck /></el-icon>
            <span>已验证手机号 {{ maskedPhone }}</span>
          </div>

          <el-form-item label="新密码">
            <el-input
              v-model="newPassword"
              size="large"
              type="password"
              placeholder="至少 6 位"
              show-password
              clearable
            >
              <template #prefix>
                <el-icon class="auth-input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="确认密码">
            <el-input
              v-model="confirmPassword"
              size="large"
              type="password"
              placeholder="再次输入新密码"
              show-password
              clearable
              @keyup.enter="submitReset"
            >
              <template #prefix>
                <el-icon class="auth-input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="step2-actions">
            <el-button class="auth-btn-soft step2-back" size="large" @click="backToStep1">
              上一步
            </el-button>
            <el-button
              class="auth-submit step2-submit"
              type="primary"
              size="large"
              :loading="submitting"
              @click="submitReset"
            >
              重置密码
            </el-button>
          </div>
        </el-form>

        <div class="auth-card__foot">
          <span class="auth-muted">想起密码了？</span>
          <router-link class="auth-link auth-link--strong" to="/login">
            返回登录
            <el-icon><Right /></el-icon>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getForgotCaptcha, sendForgotCode, verifyForgotSms, resetPasswordByPhone } from "@/api/user"
import { isValidMobileCN } from "@/utils/validate"
import { Right, Iphone, Lock, Message, Refresh, CircleCheck } from "@element-plus/icons-vue"

export default {
  components: { Right, Iphone, Lock, Message, Refresh, CircleCheck },
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
.auth-page {
  min-height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 16px 48px;
  box-sizing: border-box;
  overflow: auto;
}

.auth-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  background: url("../assets/img/login.jpg") center / cover no-repeat;
}

.auth-bg::after {
  content: "";
  position: absolute;
  inset: 0;
  background:
    linear-gradient(120deg, rgba(12, 16, 28, 0.62) 0%, rgba(18, 24, 42, 0.42) 45%, rgba(10, 14, 26, 0.55) 100%),
    radial-gradient(ellipse 70% 50% at 15% 20%, rgba(90, 132, 253, 0.18), transparent 55%);
  pointer-events: none;
}

.auth-shell {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 960px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: clamp(24px, 5vw, 48px);
  align-items: center;
}

@media (max-width: 820px) {
  .auth-shell {
    grid-template-columns: 1fr;
    max-width: 420px;
  }
  .auth-brand {
    text-align: center;
    padding: 8px 0 0;
  }
  .auth-brand__logo {
    margin: 0 auto;
  }
}

.auth-brand {
  color: #fff;
  padding: 0 clamp(8px, 3vw, 24px);
  position: relative;
}

.auth-brand__glow {
  position: absolute;
  width: 200px;
  height: 200px;
  left: 50%;
  top: 40%;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(90, 132, 253, 0.45) 0%, transparent 70%);
  filter: blur(40px);
  pointer-events: none;
  opacity: 0.9;
}

.auth-brand__logo {
  width: 88px;
  height: 88px;
  display: block;
  border-radius: 20px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.35);
  position: relative;
  z-index: 1;
}

.auth-brand__title {
  margin: 24px 0 8px;
  font-size: clamp(28px, 4vw, 36px);
  font-weight: 700;
  letter-spacing: 0.12em;
  position: relative;
  z-index: 1;
  text-shadow: 0 2px 16px rgba(0, 0, 0, 0.45);
}

.auth-brand__sub {
  margin: 0;
  font-size: 14px;
  opacity: 0.92;
  letter-spacing: 0.08em;
  line-height: 1.6;
  position: relative;
  z-index: 1;
  text-shadow: 0 1px 10px rgba(0, 0, 0, 0.4);
}

.auth-card {
  background: rgba(255, 255, 255, 0.48);
  border: 1px solid rgba(255, 255, 255, 0.42);
  border-radius: 22px;
  padding: clamp(32px, 4.5vw, 44px) clamp(28px, 4.5vw, 40px);
  box-shadow: 0 28px 72px rgba(0, 0, 0, 0.28), inset 0 1px 0 rgba(255, 255, 255, 0.55);
  backdrop-filter: saturate(1.18) blur(26px);
  -webkit-backdrop-filter: saturate(1.18) blur(26px);
}

.auth-card__head {
  margin-bottom: 4px;
}

.auth-card__title {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 700;
  color: #1a1d28;
  letter-spacing: 0.06em;
}

.auth-card__hint {
  margin: 0 0 16px;
  font-size: 14px;
  color: #4a5160;
  font-weight: 500;
  letter-spacing: 0.04em;
}

.auth-steps {
  margin-bottom: 24px;
  --el-color-primary: #2563eb;
  --el-text-color-primary: #1a1d28;
  --el-text-color-placeholder: #4a5160;
}

.auth-steps :deep(.el-step__title) {
  font-size: 14px;
  font-weight: 600;
  line-height: 1.4;
}

/* 未激活步骤 */
.auth-steps :deep(.el-step__head.is-wait) {
  color: #3d4654;
  border-color: #8b95a8;
}

.auth-steps :deep(.el-step__head.is-wait .el-step__icon),
.auth-steps :deep(.el-step__head.is-wait .el-step__icon.is-text) {
  background: rgba(255, 255, 255, 0.88) !important;
  border-color: #8b95a8 !important;
  color: #3d4654 !important;
}

.auth-steps :deep(.el-step__head.is-wait .el-step__icon-inner) {
  color: #3d4654 !important;
  font-weight: 700;
}

.auth-steps :deep(.el-step__title.is-wait) {
  color: #3d4654;
  font-weight: 600;
}

.auth-steps :deep(.el-step__head.is-wait .el-step__line) {
  background-color: #9aa3b5;
}

/* 进行中 / 已完成步骤 */
.auth-steps :deep(.el-step__head.is-process) {
  color: #1d4ed8;
  border-color: #2563eb;
}

.auth-steps :deep(.el-step__head.is-process .el-step__icon),
.auth-steps :deep(.el-step__head.is-process .el-step__icon.is-text) {
  background: #2563eb !important;
  border-color: #2563eb !important;
  color: #fff !important;
}

.auth-steps :deep(.el-step__head.is-process .el-step__icon-inner) {
  color: #fff !important;
  font-weight: 700;
}

.auth-steps :deep(.el-step__title.is-process) {
  color: #1d4ed8;
  font-weight: 700;
}

.auth-steps :deep(.el-step__head.is-finish) {
  color: #1d4ed8;
  border-color: #2563eb;
}

.auth-steps :deep(.el-step__head.is-finish .el-step__icon),
.auth-steps :deep(.el-step__head.is-finish .el-step__icon.is-text) {
  background: #2563eb !important;
  border-color: #2563eb !important;
  color: #fff !important;
}

.auth-steps :deep(.el-step__head.is-finish .el-step__icon-inner) {
  color: #fff !important;
  font-weight: 700;
}

.auth-steps :deep(.el-step__title.is-finish) {
  color: #1d4ed8;
  font-weight: 700;
}

.auth-steps :deep(.el-step__head.is-finish .el-step__line) {
  background-color: #2563eb;
}

.auth-steps :deep(.el-step__head.is-process .el-step__line) {
  background-color: #9aa3b5;
}

/* 覆盖 success 绿色（Element Plus 部分版本仍可能残留） */
.auth-steps :deep(.el-step__head.is-success .el-step__icon),
.auth-steps :deep(.el-step__head.is-success .el-step__icon.is-text) {
  background: #2563eb !important;
  border-color: #2563eb !important;
  color: #fff !important;
}

.auth-steps :deep(.el-step__head.is-success .el-step__icon-inner) {
  color: #fff !important;
  font-weight: 700;
}

.auth-steps :deep(.el-step__title.is-success) {
  color: #1d4ed8 !important;
  font-weight: 700;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.auth-form :deep(.el-form-item__label) {
  color: #4a4f5c;
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.06em;
  padding-bottom: 8px;
}

.auth-input-icon {
  color: #4a6cf7;
  font-size: 19px;
}

.auth-form :deep(.el-input__prefix .el-icon),
.auth-form :deep(.el-input__suffix .el-icon) {
  color: #4a6cf7;
  font-size: 19px;
}

.auth-form :deep(.el-input__wrapper) {
  border-radius: 14px;
  min-height: 50px;
  padding: 4px 14px;
  background-color: rgba(255, 255, 255, 0.55) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.5) inset !important;
  transition: background-color 0.2s, box-shadow 0.2s;
}

.auth-form :deep(.el-input__wrapper:hover) {
  background-color: rgba(255, 255, 255, 0.68) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.65) inset !important;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  background-color: rgba(255, 255, 255, 0.74) !important;
  box-shadow: 0 0 0 1px #5a84fd inset, 0 0 0 4px rgba(90, 132, 253, 0.14) !important;
}

.auth-form :deep(.el-input__inner) {
  color: #303133 !important;
  font-size: 15px;
  height: 42px;
  line-height: 42px;
}

.auth-form :deep(.el-input__inner::placeholder) {
  color: #a8abb2 !important;
}

.auth-form :deep(.el-input__inner::placeholder) {
  color: #8b93a1 !important;
}

/* 浅灰辅助按钮（刷新、获取验证码、上一步） */
.auth-btn-soft {
  background-color: rgba(255, 255, 255, 0.72) !important;
  border: 1px solid #9aa3b5 !important;
  color: #3d4654 !important;
  font-weight: 600;
}

.auth-btn-soft:hover:not(:disabled) {
  background-color: rgba(255, 255, 255, 0.88) !important;
  border-color: #6b7588 !important;
  color: #1a1d28 !important;
}

.auth-btn-soft:disabled {
  background-color: rgba(255, 255, 255, 0.5) !important;
  color: #8b93a1 !important;
}

.captcha-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.captcha-img-wrap {
  flex-shrink: 0;
  width: 120px;
  height: 40px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #e4e7ed;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
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

.captcha-refresh {
  flex-shrink: 0;
  padding: 8px 12px;
  height: 40px;
}

.captcha-input {
  flex: 1;
  min-width: 0;
}

.sms-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.sms-row .el-input {
  flex: 1;
  min-width: 0;
}

.sms-btn {
  flex-shrink: 0;
  white-space: nowrap;
  height: 50px;
}

.demo-alert {
  margin-bottom: 16px;
}

.demo-alert :deep(.el-alert__title) {
  color: #3d4654;
  font-weight: 600;
}

.verified-phone {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  margin-bottom: 18px;
  border-radius: 12px;
  background: rgba(37, 99, 235, 0.1);
  border: 1px solid rgba(37, 99, 235, 0.28);
  color: #1d4ed8;
  font-size: 14px;
  font-weight: 600;
}

.verified-phone .el-icon {
  font-size: 18px;
}

.step2-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.step2-back {
  flex: 0 0 100px;
  height: 52px;
  border-radius: 14px;
}

.step2-submit {
  flex: 1;
  margin-top: 0 !important;
}

.auth-submit {
  width: 100%;
  margin-top: 8px;
  height: 52px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.2em;
  background: linear-gradient(135deg, #5a84fd 0%, #3d5afe 100%);
  border: none;
  box-shadow: 0 10px 28px rgba(61, 90, 254, 0.35);
}

.auth-submit:hover {
  filter: brightness(1.05);
}

.auth-link {
  font-size: 13px;
  color: #5a84fd;
  font-weight: 600;
  letter-spacing: 0.04em;
}

.auth-link--strong {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.auth-muted {
  font-size: 14px;
  color: #4a5160;
  font-weight: 500;
  margin-right: 6px;
}

.auth-card__foot {
  margin-top: 24px;
  padding-top: 22px;
  border-top: 1px solid rgba(255, 255, 255, 0.35);
  text-align: center;
}
</style>

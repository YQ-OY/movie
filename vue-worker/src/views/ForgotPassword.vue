<template>
  <div class="forgot-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="deco-blob deco-blob--top-right"></div>
      <div class="deco-blob deco-blob--bottom-left"></div>
      <div class="deco-dots"></div>
    </div>

    <!-- 卡片容器 -->
    <div class="forgot-card">
      <!-- 头部 -->
      <div class="card-header">
        <div class="logo-wrap">
          <img class="logo-img" src="../assets/img/logo.png" alt="厅有独钟" />
        </div>
        <h1 class="sys-title">厅有独钟 · 客服系统</h1>
        <p class="page-title">找回密码</p>
        <p class="page-subtitle">{{ step === 1 ? '验证手机号与验证码' : '设置新的登录密码' }}</p>
      </div>

      <!-- 步骤条 -->
      <el-steps :active="step - 1" finish-status="finish" align-center class="forgot-steps">
        <el-step title="身份验证" />
        <el-step title="重置密码" />
      </el-steps>

      <!-- 第一步：验证身份 -->
      <div v-show="step === 1" class="step-body">
        <!-- 手机号 -->
        <div class="input-group">
          <label class="input-label">手机号</label>
          <el-input v-model="phone" placeholder="请输入11位手机号" maxlength="11" class="clean-input">
            <template #prefix>
              <el-icon class="input-prefix-icon">
                <Phone />
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 图形验证码 -->
        <div class="input-group">
          <label class="input-label">图形验证码</label>
          <div class="captcha-row">
            <div class="captcha-img-box" @click="refreshCaptcha" title="点击刷新">
              <div v-if="captchaSvg" class="captcha-svg" v-html="captchaSvg" />
              <span v-else class="captcha-placeholder">点击获取</span>
            </div>
            <el-button class="refresh-btn" @click="refreshCaptcha" :disabled="captchaLoading" :icon="Refresh" circle />
            <el-input v-model="captchaInput" placeholder="图中4位数字" maxlength="4" class="clean-input captcha-input" />
          </div>
        </div>

        <!-- 短信验证码 -->
        <div class="input-group">
          <label class="input-label">短信验证码</label>
          <div class="sms-row">
            <el-input v-model="smsCode" placeholder="请输入短信验证码" maxlength="6" class="clean-input sms-input">
              <template #prefix>
                <el-icon class="input-prefix-icon">
                  <Message />
                </el-icon>
              </template>
            </el-input>
            <el-button class="sms-btn" :disabled="smsCountdown > 0 || sendingCode" :loading="sendingCode"
              @click="handleSendCode">
              {{ smsCountdown > 0 ? `${smsCountdown}秒后` : '获取验证码' }}
            </el-button>
          </div>
        </div>

        <!-- 演示提示 -->
        <el-alert v-if="demoHintVisible" type="info" :closable="false" show-icon title="演示环境短信验证码为 123456"
          class="demo-alert" />

        <!-- 下一步按钮 -->
        <el-button type="primary" class="submit-btn" :loading="verifying" @click="goToStep2">
          下一步
        </el-button>
      </div>

      <!-- 第二步：重置密码 -->
      <div v-show="step === 2" class="step-body">
        <!-- 已验证手机号提示 -->
        <div class="verified-banner">
          <el-icon class="verified-icon">
            <CircleCheck />
          </el-icon>
          <span>已验证手机号 {{ maskedPhone }}</span>
        </div>

        <!-- 新密码 -->
        <div class="input-group">
          <label class="input-label">新密码</label>
          <el-input v-model="newPassword" type="password" placeholder="至少6位" show-password class="clean-input">
            <template #prefix>
              <el-icon class="input-prefix-icon">
                <Lock />
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 确认密码 -->
        <div class="input-group">
          <label class="input-label">确认密码</label>
          <el-input v-model="confirmPassword" type="password" placeholder="再次输入新密码" show-password class="clean-input"
            @keyup.enter="submitReset">
            <template #prefix>
              <el-icon class="input-prefix-icon">
                <Lock />
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 操作按钮 -->
        <div class="step2-actions">
          <el-button class="back-btn" @click="backToStep1">上一步</el-button>
          <el-button type="primary" class="submit-btn step2-submit" :loading="submitting" @click="submitReset">
            重置密码
          </el-button>
        </div>
      </div>

      <!-- 底部链接 -->
      <div class="card-footer">
        <router-link to="/login" class="back-login-link">← 返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, CircleCheck, Phone, Message, Lock } from '@element-plus/icons-vue'
import {
  getForgotCaptcha,
  sendForgotCode,
  verifyForgotSms,
  resetPasswordByPhone
} from '@/api/worker'
import { isValidMobileCN } from '@/utils/validate'

const router = useRouter()

// ==================== 状态 ====================
const step = ref(1)
const phone = ref('')
const maskedPhone = ref('')
const captchaKey = ref('')
const captchaSvg = ref('')
const captchaInput = ref('')
const captchaLoading = ref(false)
const smsCode = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const sendingCode = ref(false)
const verifying = ref(false)
const submitting = ref(false)
const smsCountdown = ref(0)
const demoHintVisible = ref(false)
const smsSent = ref(false)
let countdownTimer = null

// ==================== 工具函数 ====================
function maskPhone(phoneStr) {
  const p = String(phoneStr || '').trim()
  if (p.length < 7) return p
  return `${p.slice(0, 3)}****${p.slice(-4)}`
}

function startCountdown() {
  smsCountdown.value = 60
  if (countdownTimer) clearInterval(countdownTimer)
  countdownTimer = setInterval(() => {
    if (smsCountdown.value <= 1) {
      smsCountdown.value = 0
      clearInterval(countdownTimer)
      countdownTimer = null
    } else {
      smsCountdown.value -= 1
    }
  }, 1000)
}

// ==================== 图形验证码 ====================
async function refreshCaptcha() {
  captchaLoading.value = true
  captchaInput.value = ''
  try {
    const res = await getForgotCaptcha()
    if (res?.success && res.data) {
      captchaKey.value = res.data.captchaKey
      captchaSvg.value = res.data.captchaSvg
    }
  } catch {
    // 错误由拦截器统一处理
  } finally {
    captchaLoading.value = false
  }
}

// ==================== 发送短信验证码 ====================
async function handleSendCode() {
  if (!isValidMobileCN(phone.value)) {
    ElMessage.warning('请输入11位有效中国大陆手机号')
    return
  }
  if (!/^\d{4}$/.test(captchaInput.value)) {
    ElMessage.warning('请输入图中4位数字')
    return
  }
  sendingCode.value = true
  try {
    const res = await sendForgotCode({
      phone: phone.value.trim(),
      captchaKey: captchaKey.value,
      captchaInput: captchaInput.value
    })
    if (res?.success) {
      demoHintVisible.value = true
      smsSent.value = true
      maskedPhone.value = res.data?.maskedPhone || maskPhone(phone.value)
      startCountdown()
    } else if (res?.msg?.includes('图形验证码')) {
      await refreshCaptcha()
    }
  } catch {
    await refreshCaptcha()
  } finally {
    sendingCode.value = false
  }
}

// ==================== 验证身份并跳转第二步 ====================
async function goToStep2() {
  if (!isValidMobileCN(phone.value)) {
    ElMessage.warning('请输入11位有效中国大陆手机号')
    return
  }
  if (!smsSent.value) {
    ElMessage.warning('请先点击「获取验证码」')
    return
  }
  if (!smsCode.value?.trim()) {
    ElMessage.warning('请输入短信验证码')
    return
  }
  verifying.value = true
  try {
    const res = await verifyForgotSms({
      phone: phone.value.trim(),
      smsCode: smsCode.value.trim()
    })
    if (res?.success) {
      maskedPhone.value = maskedPhone.value || maskPhone(phone.value)
      step.value = 2
    }
  } finally {
    verifying.value = false
  }
}

function backToStep1() {
  step.value = 1
  newPassword.value = ''
  confirmPassword.value = ''
}

// ==================== 提交新密码 ====================
function submitReset() {
  if (!newPassword.value || newPassword.value.length < 6) {
    ElMessage.warning('新密码不能少于6位')
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  submitting.value = true
  resetPasswordByPhone({
    phone: phone.value.trim(),
    smsCode: smsCode.value.trim(),
    newPassword: newPassword.value
  })
    .then(res => {
      if (res?.success) {
        ElMessage.success('密码重置成功，请重新登录')
        router.push('/login')
      }
    })
    .finally(() => {
      submitting.value = false
    })
}

// ==================== 生命周期 ====================
onMounted(() => {
  refreshCaptcha()
})

onBeforeUnmount(() => {
  if (countdownTimer) clearInterval(countdownTimer)
})
</script>

<style scoped>
/* ==================== 整体布局 ==================== */
.forgot-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #f0f5ff 0%, #f8fafc 40%, #f1f4f9 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.deco-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.28;
}

.deco-blob--top-right {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.22), transparent 70%);
  top: -100px;
  right: -80px;
  animation: floatBlob 12s ease-in-out infinite;
}

.deco-blob--bottom-left {
  width: 340px;
  height: 340px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.18), transparent 70%);
  bottom: -90px;
  left: -70px;
  animation: floatBlob 10s ease-in-out infinite reverse;
}

@keyframes floatBlob {

  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }

  33% {
    transform: translate(18px, -25px) scale(1.05);
  }

  66% {
    transform: translate(-12px, 18px) scale(0.95);
  }
}

.deco-dots {
  position: absolute;
  top: 12%;
  right: 8%;
  width: 110px;
  height: 110px;
  background-image: radial-gradient(circle, #cbd5e1 1.2px, transparent 1.2px);
  background-size: 16px 16px;
  opacity: 0.45;
}

/* ==================== 卡片容器 ==================== */
.forgot-card {
  position: relative;
  z-index: 1;
  width: 480px;
  padding: 40px 44px 32px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-radius: 28px;
  box-shadow: 0 8px 32px rgba(15, 23, 42, 0.06), 0 2px 8px rgba(15, 23, 42, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.7);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.forgot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08), 0 4px 12px rgba(15, 23, 42, 0.04);
}

/* 头部 */
.card-header {
  text-align: center;
  margin-bottom: 28px;
}

.logo-wrap {
  display: inline-block;
  padding: 10px;
  border-radius: 18px;
  background: rgba(59, 130, 246, 0.06);
  margin-bottom: 12px;
}

.logo-img {
  width: 56px;
  height: 56px;
  display: block;
}

.sys-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: 0.3px;
}

.page-title {
  margin: 8px 0 4px;
  font-size: 18px;
  font-weight: 800;
  color: #1e293b;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  font-weight: 450;
}

/* 步骤条 */
.forgot-steps {
  margin-bottom: 28px;
}

.forgot-steps :deep(.el-step__title) {
  font-size: 13px;
  font-weight: 600;
}

.forgot-steps :deep(.el-step__head.is-wait .el-step__icon) {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #94a3b8;
}

.forgot-steps :deep(.el-step__head.is-process .el-step__icon),
.forgot-steps :deep(.el-step__head.is-finish .el-step__icon) {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
}

.forgot-steps :deep(.el-step__title.is-process),
.forgot-steps :deep(.el-step__title.is-finish) {
  color: #3b82f6;
  font-weight: 700;
}

.forgot-steps :deep(.el-step__head.is-finish .el-step__line) {
  background-color: #3b82f6;
}

/* 表单区域 */
.step-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.input-group {
  margin-bottom: 6px;
}

.input-label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

/* 统一输入框样式 */
.clean-input :deep(.el-input__wrapper) {
  height: 48px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1.5px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
  padding: 0 16px;
}

.clean-input :deep(.el-input__wrapper:hover) {
  border-color: #94a3b8;
  background: #fff;
}

.clean-input :deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
}

.input-prefix-icon {
  font-size: 18px;
  color: #94a3b8;
  margin-right: 4px;
}

/* 图形验证码行 */
.captcha-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-img-box {
  width: 120px;
  height: 48px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1.5px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: border-color 0.3s;
}

.captcha-img-box:hover {
  border-color: #3b82f6;
}

.captcha-svg :deep(svg) {
  display: block;
  width: 120px;
  height: 48px;
}

.captcha-placeholder {
  font-size: 12px;
  color: #94a3b8;
}

.refresh-btn {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #64748b;
  transition: all 0.2s;
}

.refresh-btn:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
  color: #334155;
}

.captcha-input {
  flex: 1;
}

/* 短信验证码行 */
.sms-row {
  display: flex;
  gap: 10px;
}

.sms-input {
  flex: 1;
}

.sms-btn {
  flex-shrink: 0;
  height: 48px;
  border-radius: 14px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #334155;
  font-weight: 600;
  font-size: 13px;
  transition: all 0.2s;
  white-space: nowrap;
}

.sms-btn:hover:not(:disabled) {
  background: #e2e8f0;
  border-color: #cbd5e1;
  color: #1e293b;
}

.sms-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 演示提示 */
.demo-alert {
  margin-top: 10px;
  border-radius: 12px;
  background: rgba(59, 130, 246, 0.06);
  border: 1px solid rgba(59, 130, 246, 0.15);
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.3);
  margin-top: 18px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(59, 130, 246, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
}

/* 第二步验证成功横幅 */
.verified-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  margin-bottom: 16px;
  border-radius: 14px;
  background: rgba(16, 185, 129, 0.08);
  border: 1px solid rgba(16, 185, 129, 0.2);
  color: #059669;
  font-size: 14px;
  font-weight: 600;
}

.verified-icon {
  font-size: 20px;
  color: #10b981;
}

/* 第二步按钮组 */
.step2-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.back-btn {
  flex: 0 0 110px;
  height: 52px;
  border-radius: 14px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #334155;
  font-weight: 600;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
  color: #1e293b;
}

.step2-submit {
  flex: 1;
  margin-top: 0;
}

/* 底部返回链接 */
.card-footer {
  margin-top: 24px;
  text-align: center;
}

.back-login-link {
  font-size: 14px;
  color: #64748b;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.back-login-link:hover {
  color: #3b82f6;
}

/* ==================== 响应式 ==================== */
@media (max-width: 540px) {
  .forgot-card {
    width: 92%;
    padding: 32px 24px 28px;
    border-radius: 22px;
  }

  .captcha-row {
    flex-wrap: wrap;
  }

  .captcha-img-box {
    width: 100px;
    height: 44px;
  }

  .sms-btn {
    font-size: 12px;
    padding: 0 12px;
  }
}
</style>
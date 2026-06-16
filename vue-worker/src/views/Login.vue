<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="deco-blob deco-blob--top-right"></div>
      <div class="deco-blob deco-blob--bottom-left"></div>
      <div class="deco-dots"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 头部 Logo 与标题 -->
      <div class="login-header">
        <div class="logo-wrapper">
          <img class="logo-img" src="../assets/img/logo.png" alt="厅有独钟" />
        </div>
        <h1 class="system-title">厅有独钟</h1>
        <p class="system-subtitle">客服系统 · 登录</p>
      </div>

      <!-- 表单区域 -->
      <el-form class="login-form-body" @submit.prevent="postLogin">
        <!-- 用户名 -->
        <div class="input-group">
          <label class="input-label">用户名</label>
          <el-input v-model="username" placeholder="请输入账号" :class="{ 'is-error': usernameError }"
            @focus="usernameError = false">
            <template #prefix>
              <el-icon class="input-icon">
                <User />
              </el-icon>
            </template>
          </el-input>
          <p v-if="usernameError" class="error-tip">
            <el-icon>
              <WarningFilled />
            </el-icon> 请输入不少于6位的用户名
          </p>
        </div>

        <!-- 密码 -->
        <div class="input-group">
          <label class="input-label">密码</label>
          <el-input v-model="password" type="password" placeholder="请输入密码" show-password
            :class="{ 'is-error': passwordError }" @focus="passwordError = false">
            <template #prefix>
              <el-icon class="input-icon">
                <Lock />
              </el-icon>
            </template>
          </el-input>
          <p v-if="passwordError" class="error-tip">
            <el-icon>
              <WarningFilled />
            </el-icon> 请输入不少于6位的密码
          </p>
        </div>

        <!-- 记住密码 & 忘记密码 -->
        <div class="login-options">
          <el-checkbox v-model="remember" class="remember-check">记住密码</el-checkbox>
          <router-link to="/forgot-password" class="forgot-link">忘记密码?</router-link>
        </div>

        <!-- 登录按钮 -->
        <el-button type="primary" class="login-btn" :loading="loading" @click="postLogin">
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
      </el-form>

      <!-- 底部版权 -->
      <div class="login-footer">
        <p>Copyright © 2021 厅有独钟 - 客服系统</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, WarningFilled } from '@element-plus/icons-vue'
import { Login } from '@/api/user'

// ==================== 状态 ====================
const loading = ref(false)
const username = ref('')
const password = ref('')
const remember = ref(false)
const usernameError = ref(false)
const passwordError = ref(false)

const router = useRouter()

// ==================== 登录方法 ====================
function postLogin() {
  // 重置错误状态
  usernameError.value = false
  passwordError.value = false

  const loginData = {
    username: username.value.trim(),
    password: password.value,
    remember: remember.value
  }

  // 前端校验
  if (loginData.username.length < 6) {
    usernameError.value = true
    return
  }
  if (loginData.password.length < 6) {
    passwordError.value = true
    return
  }

  loading.value = true

  Login(loginData)
    .then(res => {
      if (res.success === false) {
        ElMessage.error(res?.msg || '账号或密码错误')
      } else {
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('wid', res.data.worker.id)
        ElMessage.success('登录成功，正在跳转...')
        setTimeout(() => {
          router.push('/')
        }, 300)
      }
    })
    .catch(() => {
      ElMessage.error('登录失败，请稍后重试')
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style scoped>
/* ==================== 页面基底 ==================== */
.login-page {
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

/* ==================== 背景装饰 ==================== */
.background-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.deco-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}

.deco-blob--top-right {
  width: 420px;
  height: 420px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.25), transparent 70%);
  top: -120px;
  right: -100px;
  animation: floatBlob 12s ease-in-out infinite;
}

.deco-blob--bottom-left {
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.2), transparent 70%);
  bottom: -100px;
  left: -80px;
  animation: floatBlob 10s ease-in-out infinite reverse;
}

@keyframes floatBlob {

  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }

  33% {
    transform: translate(20px, -30px) scale(1.05);
  }

  66% {
    transform: translate(-15px, 20px) scale(0.95);
  }
}

.deco-dots {
  position: absolute;
  top: 15%;
  right: 10%;
  width: 120px;
  height: 120px;
  background-image: radial-gradient(circle, #cbd5e1 1.2px, transparent 1.2px);
  background-size: 16px 16px;
  opacity: 0.5;
}

/* ==================== 登录卡片 ==================== */
.login-card {
  position: relative;
  z-index: 1;
  width: 440px;
  padding: 44px 48px 36px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-radius: 28px;
  box-shadow:
    0 8px 32px rgba(15, 23, 42, 0.06),
    0 2px 8px rgba(15, 23, 42, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.7);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-2px);
  box-shadow:
    0 16px 40px rgba(15, 23, 42, 0.08),
    0 4px 12px rgba(15, 23, 42, 0.04);
}

/* ==================== 头部 ==================== */
.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-wrapper {
  display: inline-block;
  padding: 12px;
  border-radius: 20px;
  background: rgba(59, 130, 246, 0.06);
  margin-bottom: 16px;
}

.logo-img {
  width: 64px;
  height: 64px;
  display: block;
}

.system-title {
  margin: 0;
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: 0.5px;
}

.system-subtitle {
  margin: 6px 0 0;
  font-size: 14px;
  color: #64748b;
  font-weight: 450;
}

/* ==================== 表单主体 ==================== */
.login-form-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.input-group {
  margin-bottom: 4px;
}

.input-label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  letter-spacing: 0.2px;
}

/* 输入框美化 */
.input-group :deep(.el-input__wrapper) {
  height: 50px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1.5px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
  padding: 0 16px;
}

.input-group :deep(.el-input__wrapper:hover) {
  border-color: #94a3b8;
  background: #fff;
}

.input-group :deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
}

.input-group :deep(.el-input.is-error .el-input__wrapper) {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.06);
}

.input-group :deep(.el-input__inner) {
  font-size: 15px;
  color: #1e293b;
}

.input-icon {
  font-size: 18px;
  color: #94a3b8;
  margin-right: 4px;
}

/* 错误提示 */
.error-tip {
  display: flex;
  align-items: center;
  gap: 5px;
  margin: 6px 0 0;
  font-size: 13px;
  color: #ef4444;
  font-weight: 500;
  animation: fadeInDown 0.25s ease;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-6px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 记住密码 & 忘记密码 */
.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
  margin-bottom: 8px;
}

.remember-check {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.remember-check :deep(.el-checkbox__label) {
  color: #64748b;
}

.forgot-link {
  font-size: 14px;
  color: #64748b;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: #3b82f6;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 54px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-top: 14px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(59, 130, 246, 0.4);
}

.login-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
}

/* ==================== 底部版权 ==================== */
.login-footer {
  margin-top: 32px;
  text-align: center;
}

.login-footer p {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
  letter-spacing: 0.2px;
}

/* ==================== 响应式适配 ==================== */
@media (max-width: 520px) {
  .login-card {
    width: 90%;
    padding: 32px 24px 28px;
    border-radius: 22px;
  }

  .system-title {
    font-size: 22px;
  }

  .logo-img {
    width: 52px;
    height: 52px;
  }

  .login-btn {
    height: 48px;
    font-size: 15px;
  }
}
</style>
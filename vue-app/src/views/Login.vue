<template>
  <div class="auth-page">
    <div class="auth-bg" aria-hidden="true" />
    <div class="auth-shell">
      <div class="auth-brand">
        <div class="auth-brand__glow" />
        <img class="auth-brand__logo" src="../assets/img/logo.png" alt="厅有独钟" />
        <h1 class="auth-brand__title">厅有独钟</h1>
        <p class="auth-brand__sub">选座观影 · 一站式票务体验</p>
      </div>

      <div class="auth-card">
        <div class="auth-card__head">
          <h2 class="auth-card__title">欢迎回来</h2>
          <p class="auth-card__hint">登录后继续购票与评论</p>
        </div>

        <el-form class="auth-form" label-position="top" @submit.prevent="postLogin">
          <el-form-item label="账号">
            <el-input
              v-model="username"
              size="large"
              placeholder="请输入用户名"
              clearable
              @keyup.enter="postLogin"
            >
              <template #prefix>
                <el-icon class="auth-input-icon"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="password"
              size="large"
              type="password"
              placeholder="请输入密码"
              show-password
              clearable
              @keyup.enter="postLogin"
            >
              <template #prefix>
                <el-icon class="auth-input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="auth-row">
            <el-checkbox v-model="remember">记住我</el-checkbox>
            <a class="auth-link" href="#" @click.prevent="$router.push('/forgot-password')">忘记密码？</a>
          </div>

          <el-button
            class="auth-submit"
            type="primary"
            size="large"
            :loading="loading"
            @click="postLogin"
          >
            登 录
          </el-button>
        </el-form>

        <div class="auth-card__foot">
          <span class="auth-muted">还没有账号？</span>
          <router-link class="auth-link auth-link--strong" to="/register">
            立即注册
            <el-icon><Right /></el-icon>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Login } from "../api/user"
import { Right, User, Lock } from "@element-plus/icons-vue"

export default {
  components: { Right, User, Lock },
  data() {
    return {
      username: "",
      password: "",
      remember: false,
      loading: false,
    }
  },
  methods: {
    postLogin() {
      if (!this.username?.trim() || !this.password) {
        this.$message.warning("请输入账号和密码")
        return
      }
      this.loading = true
      Login({
        username: this.username.trim(),
        password: this.password,
        remember: this.remember,
      })
        .then((res) => {
          if (!res?.success || !res.data) {
            return
          }
          const { user, token } = res.data
          if (user?.id != null) {
            localStorage.setItem("uid", user.id)
          }
          if (token) {
            localStorage.setItem("token", token)
          }
          this.$router.push({ path: "/", replace: true })
        })
        .finally(() => {
          this.loading = false
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
  background: linear-gradient(145deg, #0f1117 0%, #1c2333 38%, #2a3f6f 72%, #3d5afe 100%);
}

.auth-bg::after {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse 80% 50% at 50% -20%, rgba(90, 132, 253, 0.35), transparent 55%),
    radial-gradient(ellipse 60% 40% at 100% 60%, rgba(108, 92, 231, 0.2), transparent 50%);
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
}

.auth-brand__sub {
  margin: 0;
  font-size: 14px;
  opacity: 0.88;
  letter-spacing: 0.08em;
  line-height: 1.6;
  position: relative;
  z-index: 1;
}

.auth-card {
  background: rgba(255, 255, 255, 0.66);
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 20px;
  padding: clamp(28px, 4vw, 40px) clamp(24px, 4vw, 36px);
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.22), inset 0 1px 0 rgba(255, 255, 255, 0.6);
  backdrop-filter: saturate(1.12) blur(20px);
  -webkit-backdrop-filter: saturate(1.12) blur(20px);
}

.auth-card__head {
  margin-bottom: 8px;
}

.auth-card__title {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 700;
  color: #1a1d28;
  letter-spacing: 0.06em;
}

.auth-card__hint {
  margin: 0 0 20px;
  font-size: 13px;
  color: #909399;
  letter-spacing: 0.04em;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.auth-form :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.06em;
  padding-bottom: 6px;
}

.auth-input-icon {
  color: #909399;
  font-size: 18px;
}

.auth-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.82) !important;
  box-shadow: 0 0 0 1px rgba(220, 223, 230, 0.95) inset !important;
  padding-left: 12px;
  transition: background-color 0.2s, box-shadow 0.2s;
}

.auth-form :deep(.el-input__wrapper:hover) {
  background-color: #fff !important;
  box-shadow: 0 0 0 1px #c0c4cc inset !important;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  background-color: #fff !important;
  box-shadow: 0 0 0 1px #5a84fd inset, 0 0 0 3px rgba(90, 132, 253, 0.15) !important;
}

.auth-form :deep(.el-input__inner) {
  color: #303133 !important;
}

.auth-form :deep(.el-input__inner::placeholder) {
  color: #a8abb2 !important;
}

.auth-form :deep(.el-input__count-inner) {
  background: transparent;
  color: #909399;
}

.auth-form :deep(.el-input__inner:-webkit-autofill),
.auth-form :deep(.el-input__inner:-webkit-autofill:hover),
.auth-form :deep(.el-input__inner:-webkit-autofill:focus) {
  -webkit-box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.92) inset !important;
  -webkit-text-fill-color: #303133 !important;
  caret-color: #303133;
  transition: background-color 5000s ease-in-out 0s;
}

.auth-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  flex-wrap: wrap;
  gap: 8px;
}
.auth-row ::v-deep .el-checkbox__input:not(.is-checked) .el-checkbox__inner {
  border-color: #d3d3d3;      /* 浅灰色边框 */
  background-color: #ffffff;   /* 保持白色背景，与默认一致 */
}
.auth-row :deep(.el-checkbox) {
  --el-checkbox-font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.auth-link {
  font-size: 13px;
  color: #5a84fd;
  font-weight: 600;
  letter-spacing: 0.04em;
}

.auth-link:hover {
  color: #3d5afe;
}

.auth-link--strong {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.auth-muted {
  font-size: 13px;
  color: #909399;
  margin-right: 6px;
}

.auth-submit {
  width: 100%;
  margin-top: 12px;
  height: 48px;
  border-radius: 12px;
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

.auth-card__foot {
  margin-top: 28px;
  padding-top: 22px;
  border-top: 1px solid rgba(255, 255, 255, 0.35);
  text-align: center;
}
</style>

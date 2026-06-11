<template>
  <div class="auth-page">
    <div class="auth-bg" aria-hidden="true" />
    <div class="auth-shell">
      <div class="auth-brand">
        <div class="auth-brand__glow" />
        <img class="auth-brand__logo" src="../assets/img/logo.png" alt="厅有独钟" />
        <h1 class="auth-brand__title">厅有独钟</h1>
        <p class="auth-brand__sub">加入我们 · 开启观影之旅</p>
      </div>

      <div class="auth-card">
        <div class="auth-card__head">
          <h2 class="auth-card__title">创建账号</h2>
          <p class="auth-card__hint">填写信息完成注册，即可登录购票</p>
        </div>

        <el-form class="auth-form" label-position="top" @submit.prevent="submitRegister">
          <el-form-item label="账号">
            <el-input
              v-model="username"
              size="large"
              placeholder="至少 6 位用户名"
              clearable
              maxlength="32"
              show-word-limit
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
              placeholder="设置登录密码"
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
              v-model="checkPassword"
              size="large"
              type="password"
              placeholder="再次输入密码"
              show-password
              clearable
              @keyup.enter="submitRegister"
            >
              <template #prefix>
                <el-icon class="auth-input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
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

          <div class="auth-agree">
            <el-checkbox v-model="agree">
              我已阅读并同意
              <span class="auth-agree__name">《用户注册协议》</span>
            </el-checkbox>
          </div>

          <el-button
            class="auth-submit auth-submit--accent"
            type="primary"
            size="large"
            :loading="loading"
            @click="submitRegister"
          >
            注 册
          </el-button>
        </el-form>

        <div class="auth-card__foot">
          <span class="auth-muted">已有账号？</span>
          <router-link class="auth-link auth-link--strong" to="/login">
            去登录
            <el-icon><Right /></el-icon>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Register } from "@/api/user"
import { isValidMobileCN } from "@/utils/validate"
import { Right, User, Lock, Iphone } from "@element-plus/icons-vue"

export default {
  components: { Right, User, Lock, Iphone },
  data() {
    return {
      username: "",
      password: "",
      checkPassword: "",
      phone: "",
      agree: false,
      loading: false,
    }
  },
  methods: {
    submitRegister() {
      if (this.username.length < 6) {
        this.$message.warning("请输入不少于 6 位的用户名")
        return
      }
      if (!this.agree) {
        this.$message.warning("请勾选同意用户注册协议")
        return
      }
      if (this.password !== this.checkPassword) {
        this.$message.warning("两次输入的密码不一致")
        return
      }
      if (!this.password) {
        this.$message.warning("请输入密码")
        return
      }
      if (!isValidMobileCN(this.phone)) {
        this.$message.warning("请输入11位有效中国大陆手机号")
        return
      }
      this.loading = true
      Register({
        username: this.username.trim(),
        password: this.password,
        phone: this.phone.trim(),
        remember: false,
      })
        .then((res) => {
          if (res.success) {
            this.$router.push("/login")
          }
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
  background: linear-gradient(145deg, #12141a 0%, #1e2636 42%, #3d2a5c 78%, #6c5ce7 100%);
}

.auth-bg::after {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse 75% 45% at 20% 0%, rgba(108, 92, 231, 0.35), transparent 50%),
    radial-gradient(ellipse 50% 40% at 90% 80%, rgba(90, 132, 253, 0.22), transparent 45%);
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
  width: 220px;
  height: 220px;
  left: 50%;
  top: 42%;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(108, 92, 231, 0.5) 0%, transparent 68%);
  filter: blur(42px);
  pointer-events: none;
  opacity: 0.85;
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
  opacity: 0.9;
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
  margin-bottom: 16px;
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
  box-shadow: 0 0 0 1px #6c5ce7 inset, 0 0 0 3px rgba(108, 92, 231, 0.15) !important;
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

.auth-agree {
  margin-bottom: 6px;
}
.auth-row ::v-deep .el-checkbox__input:not(.is-checked) .el-checkbox__inner,
.auth-agree ::v-deep .el-checkbox__input:not(.is-checked) .el-checkbox__inner {
  border-color: #d3d3d3;      /* 浅灰色边框 */
  background-color: #ffffff;   /* 保持白色背景 */
}
.auth-agree :deep(.el-checkbox) {
  align-items: flex-start;
  white-space: normal;
  line-height: 1.5;
  --el-checkbox-font-size: 13px;
  color: #666062;
}

.auth-agree__name {
  color: #6c5ce7;
  font-weight: 600;
}

.auth-submit {
  width: 100%;
  margin-top: 10px;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.2em;
  border: none;
  box-shadow: 0 10px 28px rgba(108, 92, 231, 0.32);
}

.auth-submit--accent {
  background: linear-gradient(135deg, #7c6ae8 0%, #6c5ce7 55%, #5a4fcf 100%);
}

.auth-submit--accent:hover {
  filter: brightness(1.06);
}

.auth-link {
  font-size: 13px;
  color: #6c5ce7;
  font-weight: 600;
  letter-spacing: 0.04em;
}

.auth-link:hover {
  color: #5a4fcf;
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

.auth-card__foot {
  margin-top: 24px;
  padding-top: 22px;
  border-top: 1px solid rgba(255, 255, 255, 0.35);
  text-align: center;
}
</style>

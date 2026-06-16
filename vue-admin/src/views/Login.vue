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
                <div class="login-subtitle">后台管理系统</div>
            </div>

            <div class="login-card__body">
                <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
                    <el-form-item prop="username">
                        <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large"
                            @input="clearError('username')" />
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock"
                            show-password size="large" @input="clearError('password')" />
                    </el-form-item>

                    <el-form-item prop="role">
                        <el-select v-model="loginForm.role" placeholder="请选择登录身份" size="large" style="width: 100%"
                            @change="clearError('role')">
                            <el-option label="系统管理员" value="admin" />
                            <el-option label="工作人员" value="worker" />
                        </el-select>
                    </el-form-item>

                    <div class="login-options">
                        <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
                        <router-link class="forgot-link" to="/forgot-password">忘记密码?</router-link>
                    </div>

                    <el-form-item>
                        <el-button type="primary" size="large" class="login-button" :loading="loading"
                            @click="handleLogin">
                            登 录
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>

            <div class="login-card__footer">
                <p>Copyright © 2021 厅有独钟 - 后台管理系统</p>
            </div>
        </div>
    </div>
</template>

<script>
import { Login } from "@/api/user"
import { saveWorkerPermissionsFromToken, clearWorkerPermissions } from '@/utils/workerPermissions'
import { User, Lock } from '@element-plus/icons-vue'

export default {
    name: 'Login',
    components: { User, Lock },
    data() {
        const validateUsername = (rule, value, callback) => {
            if (!value || value.length < 6) {
                callback(new Error('用户名长度不能少于6位'))
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

        const validateRole = (rule, value, callback) => {
            if (!value) {
                callback(new Error('请选择登录身份'))
            } else {
                callback()
            }
        }

        return {
            loading: false,
            loginForm: {
                username: '',
                password: '',
                role: '',
                remember: false
            },
            loginRules: {
                username: [
                    { required: true, validator: validateUsername, trigger: 'blur' }
                ],
                password: [
                    { required: true, validator: validatePassword, trigger: 'blur' }
                ],
                role: [
                    { required: true, validator: validateRole, trigger: 'change' }
                ]
            }
        }
    },
    methods: {
        clearError(field) {
            this.$refs.loginFormRef?.clearValidate(field)
        },
        handleLogin() {
            this.$refs.loginFormRef.validate(async (valid) => {
                if (!valid) return

                this.loading = true
                try {
                    const res = await Login(this.loginForm.role, {
                        username: this.loginForm.username,
                        password: this.loginForm.password,
                        remember: this.loginForm.remember,
                    })

                    if (res.success) {
                        // 登录成功
                        localStorage.setItem("token", res.data.token)
                        if (this.loginForm.role === 'worker') {
                            localStorage.setItem("wid", res.data.worker.id)
                            saveWorkerPermissionsFromToken(res.data.token)
                        } else {
                            clearWorkerPermissions()
                        }
                        localStorage.setItem("role", this.loginForm.role)
                        localStorage.setItem("username", this.loginForm.username)

                        this.$message.success('登录成功')
                        this.$router.push("/")
                    }
                } catch {
                    // 全局请求拦截器已处理错误提示
                } finally {
                    this.loading = false
                }
            })
        }
    }
}
</script>

<style scoped>
/* 登录页面整体背景 */
.login {
    width: 100vw;
    height: 100vh;
    overflow: hidden;
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 50%, #bae6fd 100%);
    position: relative;
}

/* 背景装饰圆圈 */
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

/* 登录卡片 */
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

/* 卡片头部 */
.login-card__header {
    text-align: center;
    margin-bottom: 40px;
}

.login-logo {
    width: 80px;
    height: 80px;
    margin-bottom: 16px;
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
    font-size: 32px;
    font-weight: 800;
    color: #0f172a;
    margin-bottom: 8px;
    letter-spacing: 2px;
}

.login-subtitle {
    font-size: 16px;
    color: #64748b;
    letter-spacing: 1px;
}

/* 卡片主体 */
.login-card__body {
    margin-bottom: 30px;
}

.login-form {
    width: 100%;
}

.login-form :deep(.el-form-item) {
    margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
    height: 52px;
    border-radius: 12px;
    box-shadow: none;
    border: 1px solid #e2e8f0;
    transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
    border-color: #93c5fd;
}

.login-form :deep(.el-input__wrapper.is-focus) {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.login-form :deep(.el-input__inner) {
    font-size: 15px;
    padding-left: 8px;
}

.login-form :deep(.el-select__wrapper) {
    height: 52px;
    border-radius: 12px;
    box-shadow: none;
    border: 1px solid #e2e8f0;
    transition: all 0.3s ease;
}

.login-form :deep(.el-select__wrapper:hover) {
    border-color: #93c5fd;
}

.login-form :deep(.el-select__wrapper.is-focus) {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

/* 登录选项 */
.login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    font-size: 14px;
}

.forgot-link {
    color: #64748b;
    text-decoration: none;
    transition: color 0.2s ease;
}

.forgot-link:hover {
    color: #409eff;
}

/* 登录按钮 */
.login-button {
    width: 100%;
    height: 52px;
    border-radius: 12px;
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 4px;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
    transition: all 0.3s ease;
    border: none;
}

.login-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 32px rgba(64, 158, 255, 0.4);
}

.login-button:active {
    transform: translateY(0);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 卡片底部 */
.login-card__footer {
    text-align: center;
    color: #94a3b8;
    font-size: 13px;
}

/* 响应式适配 */
@media (max-width: 576px) {
    .login-card {
        width: 90%;
        padding: 30px 24px;
    }

    .login-title {
        font-size: 28px;
    }

    .login-subtitle {
        font-size: 14px;
    }

    .login-form :deep(.el-input__wrapper),
    .login-form :deep(.el-select__wrapper),
    .login-button {
        height: 48px;
    }
}
</style>
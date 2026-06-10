import axios from "axios"
import config from "../config"
import router from '../router'
import { ElMessage } from 'element-plus'
import { clearWorkerPermissions } from '@/utils/workerPermissions'

function isSessionInvalid403(res) {
    return res && res.code === 403 && typeof res.msg === 'string' &&
        (res.msg.includes('令牌') || res.msg.includes('身份认证'))
}

let lastSessionHandleAt = 0
const SESSION_HANDLE_DEBOUNCE_MS = 1200

function clearSessionAndGoLogin(msg) {
    const now = Date.now()
    if (now - lastSessionHandleAt < SESSION_HANDLE_DEBOUNCE_MS) return
    lastSessionHandleAt = now
    localStorage.removeItem('token')
    clearWorkerPermissions()
    const path = router.currentRoute.value?.path || ''
    if (path !== '/login') {
        ElMessage.warning(msg || '登录已失效，请重新登录')
        router.replace('/login')
    }
}

function notifySuccess(res, config) {
    if (config?.silent || res?.msg == null) return
    ElMessage.success(res.msg)
}

function notifyError(res, config) {
    if (config?.silent) return
    ElMessage.error(res?.msg || '请求失败')
}

const service = axios.create({
    baseURL: config.API_URL,
    timeout: 5000,
});

service.interceptors.request.use(cfg => {
    const token = localStorage.getItem("token")
    cfg.headers = cfg.headers || {}
    if (token) cfg.headers.Authorization = token
    return cfg
})

service.interceptors.response.use(
    response => {
        const res = response.data;
        const config = response.config

        if (res.code === 403 && isSessionInvalid403(res)) {
            clearSessionAndGoLogin(res.msg)
            return res
        }

        if (res.code === 403) {
            setTimeout(() => {
                router.push("/403")
            }, 700)
            return res
        }

        if (res.success === true) {
            notifySuccess(res, config)
        } else {
            notifyError(res, config)
        }
        return res
    },

    error => {
        console.log('err' + error);
        if (!error.config?.silent) {
            ElMessage.error(error?.response?.data?.msg || '网络异常，请稍后重试')
        }
        return Promise.reject(error)
    }
);

export default service

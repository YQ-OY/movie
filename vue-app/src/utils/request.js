import axios from "axios"
import config from "@/config"
import router from "@/router"
import { ElNotification, ElMessage } from 'element-plus'

const service = axios.create({
    baseURL: config.API_URL,
});

/** 后端用 HTTP 200 + code 403 表示 JWT 失效等，需重新登录（见 AuthorizationFilter） */
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
    localStorage.removeItem('uid')
    const path = router.currentRoute.value?.path || ''
    if (path !== '/login' && path !== '/register') {
        ElMessage.warning(msg || '登录已失效，请重新登录')
        router.replace('/login')
    }
}

service.interceptors.request.use(cfg => {
    const token = localStorage.getItem('token')
    cfg.headers = cfg.headers || {}
    if (token) cfg.headers.Authorization = token
    return cfg
})

service.interceptors.response.use(
    response => {
        const res = response.data;

        if (res.success === true) {
            if (res.msg !== null) {
                ElNotification.success({
                    title: 'Success ',
                    message: res.msg,
                    type: 'success'
                });
            }

        } else {
            if (isSessionInvalid403(res)) {
                clearSessionAndGoLogin(res.msg)
            } else {
                ElNotification.error({
                    title: '错误提示: ' + res.code,
                    message: res.msg
                });
            }
        }
        return res
    },

    error => {
        console.log('err' + error);
        return Promise.reject(error)
    }
);

export default service

import service from "../utils/request";

export function Login(role, data, options = {}) {
    return service({
        url: '/' + role + '/login',
        method: 'post',
        data: data,
        silent: options.silent,
    })
}

export function FindAllUser() {
    return service({
        url: "/user",
        method: "get"
    })
}

export function UpdateUser(data) {
    return service({
        url: "/user",
        method: "put",
        data: data
    })
}

// 分页查询用户
export function listUserPage(params) {
  return service({
    url: '/user/page',
    method: 'get',
    params
  })
}

export function getForgotCaptcha(role) {
  return service({
    url: `/${role}/forgot/captcha`,
    method: 'get',
  })
}

export function sendForgotCode(role, data) {
  return service({
    url: `/${role}/forgot/send-code`,
    method: 'post',
    data,
  })
}

export function verifyForgotSms(role, data) {
  return service({
    url: `/${role}/forgot/verify-sms`,
    method: 'post',
    data,
  })
}

export function resetPasswordByPhone(role, data) {
  return service({
    url: `/${role}/forgot/reset`,
    method: 'post',
    data,
  })
}

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

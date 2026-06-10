import service from "@/utils/request";

export function findById(id) {
    return service({
        url: '/user/' + id,
        method: 'get'
    })
}

export function Login(data, options = {}) {
    return service({
        url: '/user/login',
        method: 'post',
        data: data,
        silent: options.silent,
    })
}

export function Register(data){
    return service({
        url: '/user/register',
        method: 'post',
        data: data
    })
}

export function updateUser(data, options = {}) {
    return service({
        url: '/user',
        method: 'put',
        data: data,
        silent: options.silent,
    })
}

export function uploadAvatar(data) {
    return service({
        url: "/upload",
        method: 'post',
        data: data,
    })
}

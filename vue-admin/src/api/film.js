import service from "../utils/request";

// 获取所有电影
export function ListAllFilm() {
    return service({
        url: '/film',
        method: 'get'
    })
}

// 电影分页查询
export function listFilmPage(params) {
  return service({
    url: '/film/page',
    method: 'get',
    params
  })
}

// 根据id删除电影
export function DeleteById(id){
    return service({
        url: '/film/' + id,
        method: 'delete'
    })
}

// 添加电影
export function AddFilm(data){
    return service({
        url: '/film',
        method: 'post',
        data: data
    })
}

// 更新电影
export function UpdateFilm(data){
    return service({
        url: '/film',
        method: 'put',
        data: data
    })
}

// 添加电影排片
export function AddArrangement(data){
    return service({
        url: '/arrangement',
        method: 'post',
        data: data
    })
}

// 更新电影排片
export function UpdateArrangement(data){
    return service({
        url: '/arrangement',
        method: 'put',
        data: data
    })
}

// 获取所有电影排片
export function FindAllArrangement(){
    return service({
        url: '/arrangement',
        method: 'get'
    })
}

// 根据id删除电影排片
export function DeleteArrangement(id){
    return service({
        url: '/arrangement?id=' + id,
        method: 'delete'
    })
}

// 添加电影海报
export function AddPoster(data){
    return service({
        url: '/poster',
        method: 'post',
        data: data
    })
}

// 获取所有电影海报
export function ListAllPoster(){
    return service({
        url: "/poster",
        method: "get"
    })
}

// 删除所有电影海报
export function DeleteAllPoster(){
    return service({
        url: "/poster",
        method: "delete"
    })
}

// 根据id删除电影海报
export function DeletePosterById(id){
    return service({
        url: "/poster/" + id,
        method: "delete"
    })
}

// 更新电影海报
export function UpdatePoster(data){
    return service({
        url: "/poster",
        method: "put",
        data: data
    })
}
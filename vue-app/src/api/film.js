import service from "../utils/request";

export function ListHots() {
    return service({
        url: '/film/hot/10',
        method: 'get'
    })
}

export function ListAllFilm() {
    return service({
        url: '/film',
        method: 'get'
    })
}

/** 已有排片的电影（首页「正在热播」） */
export function ListFilmScheduled() {
    return service({
        url: '/film/scheduled',
        method: 'get'
    })
}

export function SearchFilm(name) {
    return service({
        url: '/film/name/' + name,
        method: 'get'
    })
}

export function FindFilmByRegionAndType(region, type) {
    return service({
        url: '/film?region=' + region + '&type=' + type,
        method: 'get'
    })
}

export function FindFilmById(id) {
    return service({
        url: '/film/' + id,
        method: 'get'
    })
}

export function AddFilmEvaluate(data) {
    return service({
        url: '/fe',
        method: 'post',
        data: data
    })
}


export function FindAllArrangement() {
    return service({
        url: '/arrangement',
        method: 'get'
    })
}


export function FindFilmEvaluateByFId(id) {
    return service({
        url: '/fe?fid=' + id,
        method: 'get'
    })
}

/** 该片 t_film_evaluate 星级均值，后端已按 0.5 步长取整 */
export function GetFilmEvaluateAvg(fid) {
    return service({
        url: '/fe/avg?fid=' + fid,
        method: 'get'
    })
}

/** fid → 平均分，后端已按 0.5 步长取整 */
export function GetFilmEvaluateAvgMap() {
    return service({
        url: '/fe/avgs',
        method: 'get'
    })
}

export function ListAllPoster() {
    return service({
        url: "/poster?status=true",
        method: "get"
    })
}

export function FindArrangements(id) {
    return service({
        url: "/arrangement/film/" + id,
        method: "get"
    })
}

export function FindArrangementById(id) {
    return service({
        url: "/arrangement/" + id,
        method: "get"
    })
}

export function GetArrangementSeats(id) {
    return service({
        url: "/arrangement/getSeats?id=" + id,
        method: "get"
    })
}
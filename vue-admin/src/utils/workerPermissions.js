/**
 * 与后端 Roles.java、t_role.value、JWT claims「roles」一致。
 * 员工登录后 JWT 中始终包含 ROLE_WORKER（基础权限）；另三类为扩展：
 * ROLE_FILM_MANAGE（电影 + 排片 + 海报）、ROLE_USER_VIEW、ROLE_ORDER_MANAGE。
 */

/** 员工基础角色（每日工作、设置等；与后端注入 JWT 一致） */
export const ROLE_WORKER = 'ROLE_WORKER'

const STORAGE_KEY = 'workerJwtRoles'

export function parseJwtPayload(token) {
  if (!token || typeof token !== 'string') return {}
  const parts = token.split('.')
  if (parts.length < 2) return {}
  try {
    let b64 = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    const pad = b64.length % 4 === 0 ? '' : '='.repeat(4 - (b64.length % 4))
    const json = atob(b64 + pad)
    return JSON.parse(json)
  } catch {
    return {}
  }
}

export function saveWorkerPermissionsFromToken(token) {
  const payload = parseJwtPayload(token)
  const raw = payload.roles
  const list = Array.isArray(raw) ? raw.filter(Boolean) : []
  const unique = [...new Set(list)]
  localStorage.setItem(STORAGE_KEY, JSON.stringify(unique))
}

export function clearWorkerPermissions() {
  localStorage.removeItem(STORAGE_KEY)
}

export function getWorkerJwtRoles() {
  try {
    const s = localStorage.getItem(STORAGE_KEY)
    if (!s) return []
    const arr = JSON.parse(s)
    return Array.isArray(arr) ? arr : []
  } catch {
    return []
  }
}

function hasAnyOf(roles, required) {
  if (!required || required.length === 0) return false
  return required.some((r) => roles.includes(r))
}

/** 影视管理：电影增删改查、院线排片、轮播海报 */
const ROLE_FILM = 'ROLE_FILM_MANAGE'
const ROLE_USER_VIEW = 'ROLE_USER_VIEW'
const ROLE_ORDER = 'ROLE_ORDER_MANAGE'

export function hasFilmManage() {
  return getWorkerJwtRoles().includes(ROLE_FILM)
}

/**
 * 路径 -> 所需「任一」权限
 */
export const WORKER_ROUTE_ACCESS = [
  { path: '/film/list', anyOf: [ROLE_FILM] },
  { path: '/film/add', anyOf: [ROLE_FILM] },
  { path: '/film/arrange', anyOf: [ROLE_FILM] },
  { path: '/film/poster', anyOf: [ROLE_FILM] },
  { path: '/user/list', anyOf: [ROLE_USER_VIEW] },
  { path: '/order/list', anyOf: [ROLE_ORDER] },
  { path: '/order/exception', anyOf: [ROLE_ORDER] },
  { path: '/stats/dashboard', anyOf: [ROLE_ORDER] },
]

const WORKER_ALWAYS = ['/setting', '/worker/daily', '/403', '/404']

const WORKER_ADMIN_ONLY = ['/worker/list', '/worker/add', '/api']

export function workerCanAccessPath(path) {
  if (WORKER_ALWAYS.includes(path)) return true
  if (WORKER_ADMIN_ONLY.includes(path)) return false
  const rule = WORKER_ROUTE_ACCESS.find((r) => r.path === path)
  if (!rule) return false
  return hasAnyOf(getWorkerJwtRoles(), rule.anyOf)
}

export function workerShowFilmMenu() {
  return hasFilmManage()
}

export function workerShowFilmItem(path) {
  const rule = WORKER_ROUTE_ACCESS.find((r) => r.path === path)
  if (!rule || !path.startsWith('/film/')) return false
  return hasAnyOf(getWorkerJwtRoles(), rule.anyOf)
}

export function workerShowUserMenu() {
  return workerCanAccessPath('/user/list')
}

export function workerShowOrderMenu() {
  return workerCanAccessPath('/order/list') || workerCanAccessPath('/order/exception')
}

export function workerDefaultHomePath() {
  const order = [
    '/film/list',
    '/user/list',
    '/film/arrange',
    '/film/add',
    '/film/poster',
    '/order/list',
    '/stats/dashboard',
    '/worker/daily',
  ]
  for (const p of order) {
    if (workerCanAccessPath(p)) return p
  }
  return '/worker/daily'
}

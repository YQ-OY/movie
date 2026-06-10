/** 个人中心抽屉 Tab：setting | order | cart */
export const PROFILE_TABS = ['setting', 'order', 'cart']

/**
 * 打开 Header 抽屉式个人中心（通过路由 query 触发，Layout 内 Header 监听）
 */
export function openProfileCenter(router, tab = 'setting') {
  if (!PROFILE_TABS.includes(tab)) {
    tab = 'setting'
  }
  const route = router.currentRoute.value
  const standalonePaths = ['/login', '/register', '/pay', '/pay/success']
  const path = standalonePaths.includes(route.path) ? '/' : route.path
  const query = { ...route.query, profile: tab }
  router.push({ path, query })
}

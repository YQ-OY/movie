/**
 * 开发环境：Chrome 的 ResizeObserver 与布局连锁更新会触发无害的 window error，
 * webpack-dev-server 红屏 overlay 会误报。在捕获阶段拦截，避免遮挡页面。
 * 必须在 main.js 第一行 import。
 */
if (process.env.NODE_ENV === 'development') {
  const re = /ResizeObserver loop (?:completed with undelivered notifications|limit exceeded)/i
  window.addEventListener(
    'error',
    (event) => {
      const msg = (event && event.message) || (event && event.error && event.error.message) || ''
      if (re.test(msg)) {
        event.stopImmediatePropagation()
      }
    },
    true
  )
}

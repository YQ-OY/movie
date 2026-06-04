/**
 * 开发环境：忽略 ResizeObserver 无害 window error，避免 webpack-dev-server 红屏。
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

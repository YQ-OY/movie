const { defineConfig } = require('@vue/cli-service')

/**
 * Chrome 在 ResizeObserver 与布局连锁更新时会抛出该信息，并非业务错误。
 * Element Plus 表格、侧栏等易触发，开发环境 overlay 过滤以免遮挡界面。
 */
function allowRuntimeErrorInOverlay(error) {
  if (!error) return true
  var msg = typeof error.message === 'string' ? error.message : String(error)
  if (msg.indexOf('ResizeObserver loop') !== -1) return false
  return true
}

module.exports = defineConfig({
  devServer: {
    client: {
      overlay: {
        runtimeErrors: allowRuntimeErrorInOverlay,
      },
    },
  },
})

const { defineConfig } = require('@vue/cli-service')

/**
 * Chrome 在 ResizeObserver 与布局连锁更新时会抛出该信息，并非业务错误。
 * Element Plus 表格、侧栏等易触发，开发环境 overlay 过滤以免遮挡界面。
 */
// 避免可选链等语法在 dev-server 对函数字符串反序列化时偶发异常，保持 ES5 兼容写法
function allowRuntimeErrorInOverlay(error) {
  if (!error) return true
  var msg = typeof error.message === 'string' ? error.message : String(error)
  if (msg.indexOf('ResizeObserver loop') !== -1) return false
  return true
}

module.exports = defineConfig({
  devServer: {
    port: 8081,
    client: {
      overlay: {
        runtimeErrors: allowRuntimeErrorInOverlay,
      },
    },
  },
})

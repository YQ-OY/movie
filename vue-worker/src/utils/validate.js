/** 与后端 ValidationUtil 一致：中国大陆手机号 */

const MOBILE_CN_RE = /^1[3-9]\d{9}$/

export function isValidMobileCN(s) {
  if (s == null || String(s).trim() === '') return false
  return MOBILE_CN_RE.test(String(s).trim())
}

/** 允许为空；非空须为合法中国大陆 11 位手机号 */
export function isValidPhoneOptional(s) {
  if (s == null || String(s).trim() === '') return true
  return isValidMobileCN(s)
}

export const phoneFormRules = [
  { required: true, message: '请输入联系电话', trigger: 'blur' },
  {
    validator: (_rule, value, callback) => {
      if (!isValidMobileCN(value)) {
        callback(new Error('请输入11位有效中国大陆手机号'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }
]

export const phoneOptionalFormRules = [
  {
    validator: (_rule, value, callback) => {
      if (!isValidPhoneOptional(value)) {
        callback(new Error('手机号格式不正确'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }
]

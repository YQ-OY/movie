export const FILM_TYPE_OPTIONS = [
  '爱情', '喜剧', '科幻', '动画', "惊悚",'恐怖', '悬疑', '冒险', '动作', '犯罪',
  '历史', '古装', '战争', '纪录片', '家庭', '传记', '武侠', '儿童', '短片', '其他',
]

/** 解析库内类型字符串为数组 */
export function splitFilmTypes(type) {
  if (!type || typeof type !== 'string') return []
  return type
    .split(/[,，、\s]+/)
    .map((item) => item.trim())
    .filter(Boolean)
}

/** 多选数组拼接为库内存储格式 */
export function joinFilmTypes(types) {
  if (!Array.isArray(types)) return ''
  return types.map((item) => String(item).trim()).filter(Boolean).join(',')
}

/** 展示：爱情,喜剧 → 爱情 / 喜剧 */
export function formatFilmTypes(type, separator = ' / ') {
  const list = splitFilmTypes(type)
  return list.length ? list.join(separator) : (type || '')
}

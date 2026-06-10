/** 解析库内类型字符串为数组 */
export function splitFilmTypes(type) {
  if (!type || typeof type !== 'string') return []
  return type
    .split(/[,，、\s]+/)
    .map((item) => item.trim())
    .filter(Boolean)
}

/** 展示：爱情,喜剧 → 爱情 / 喜剧 */
export function formatFilmTypes(type, separator = ' / ') {
  const list = splitFilmTypes(type)
  return list.length ? list.join(separator) : (type || '')
}

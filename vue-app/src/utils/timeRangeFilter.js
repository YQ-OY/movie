export const TIME_RANGE_OPTIONS = [
  { key: 'all', label: '全部' },
  { key: '3d', label: '近三天' },
  { key: '1w', label: '近一周' },
  { key: '1m', label: '近一个月' },
  { key: '1y', label: '近一年' },
]

export function parseCreateAt(createAt) {
  if (!createAt) return 0
  const normalized = String(createAt).trim().replace(/-/g, '/')
  const time = Date.parse(normalized)
  return Number.isNaN(time) ? 0 : time
}

export function getRangeStartMs(key) {
  const now = Date.now()
  const day = 24 * 60 * 60 * 1000
  switch (key) {
    case '3d':
      return now - 3 * day
    case '1w':
      return now - 7 * day
    case '1m':
      return now - 30 * day
    case '1y':
      return now - 365 * day
    default:
      return 0
  }
}

export function filterAndSortByCreateAt(list, getCreateAt, rangeKey) {
  const items = Array.isArray(list) ? list.slice() : []
  const start = getRangeStartMs(rangeKey)
  const filtered = rangeKey === 'all'
    ? items
    : items.filter(item => parseCreateAt(getCreateAt(item)) >= start)
  return filtered.sort((a, b) => parseCreateAt(getCreateAt(b)) - parseCreateAt(getCreateAt(a)))
}

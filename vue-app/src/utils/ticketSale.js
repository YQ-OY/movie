/** 与后端 ArrangementScheduleUtil.TICKET_SALES_CLOSE_BEFORE_MINUTES 一致 */
export const TICKET_CLOSE_BEFORE_MINUTES = 20

/** 购票页日期筛选：含今天在内的连续天数 */
export const TICKET_FILTER_DAY_COUNT = 5

const WEEK_LABELS = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

/** 统一为 YYYY-MM-DD，便于与排片 date 字段比较 */
export function normalizeDateKey(dateStr) {
  if (!dateStr) return ''
  const match = String(dateStr).trim().match(/^(\d{4})-(\d{1,2})-(\d{1,2})/)
  if (!match) return String(dateStr).trim()
  const [, y, mo, d] = match
  return `${y}-${String(mo).padStart(2, '0')}-${String(d).padStart(2, '0')}`
}

/**
 * @param {string} dateStr YYYY-MM-DD
 * @param {string} startHms HH:mm:ss
 * @returns {number}
 */
export function parseArrangementStartMs(dateStr, startHms) {
  if (!dateStr || !startHms) return NaN
  const dateMatch = String(dateStr).trim().match(/^(\d{4})-(\d{1,2})-(\d{1,2})/)
  if (!dateMatch) return NaN
  const parts = String(startHms).split(':')
  const h = Number(parts[0])
  const m = Number(parts[1] ?? 0)
  const s = Number(parts[2] ?? 0)
  if (Number.isNaN(h) || Number.isNaN(m) || Number.isNaN(s)) return NaN
  const y = Number(dateMatch[1])
  const mo = Number(dateMatch[2])
  const d = Number(dateMatch[3])
  const startMs = new Date(y, mo - 1, d, h, m, s).getTime()
  return Number.isNaN(startMs) ? NaN : startMs
}

export function formatDateKey(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

/** 含今天在内的连续 N 天，用于购票页日期筛选 */
export function buildTicketDayOptions(dayCount = TICKET_FILTER_DAY_COUNT) {
  const options = []
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  for (let i = 0; i < dayCount; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    const dateKey = formatDateKey(date)
    let label = `${date.getMonth() + 1}月${date.getDate()}日`
    if (i === 0) label = '今天'
    else if (i === 1) label = '明天'
    options.push({
      dateKey,
      label,
      weekLabel: WEEK_LABELS[date.getDay()],
    })
  }
  return options
}

/**
 * 过滤未开场、且在指定日期（5 天窗口内）的排片
 * @param {Array} arrangements
 * @param {string} dateKey YYYY-MM-DD
 * @param {number} dayCount
 */
export function filterUpcomingArrangements(arrangements, dateKey, dayCount = TICKET_FILTER_DAY_COUNT) {
  const allowedDates = new Set(buildTicketDayOptions(dayCount).map(item => item.dateKey))
  const selectedKey = dateKey ? normalizeDateKey(dateKey) : ''
  const items = Array.isArray(arrangements) ? arrangements : []

  return items
    .filter(item => {
      const itemDate = normalizeDateKey(item.date)
      if (!allowedDates.has(itemDate)) return false
      if (selectedKey && itemDate !== selectedKey) return false
      return isBeforeShowStart(item.date, item.startTime)
    })
    .sort((a, b) => {
      const diff = parseArrangementStartMs(a.date, a.startTime) - parseArrangementStartMs(b.date, b.startTime)
      if (diff !== 0) return diff
      return String(a.startTime).localeCompare(String(b.startTime))
    })
}

/**
 * 是否仍可购票：当前时间须早于「开场时间 − TICKET_CLOSE_BEFORE_MINUTES 分钟」
 * @param {string} dateStr YYYY-MM-DD
 * @param {string} startHms HH:mm:ss
 */
export function isTicketSaleAllowed(dateStr, startHms) {
  const startMs = parseArrangementStartMs(dateStr, startHms)
  if (Number.isNaN(startMs)) return false
  const stopSalesAt = startMs - TICKET_CLOSE_BEFORE_MINUTES * 60 * 1000
  return Date.now() < stopSalesAt
}

/**
 * 是否尚未开场（放映开始前才可退款 / 购票页展示）
 * @param {string} dateStr YYYY-MM-DD
 * @param {string} startHms HH:mm:ss
 */
export function isBeforeShowStart(dateStr, startHms) {
  const startMs = parseArrangementStartMs(dateStr, startHms)
  if (Number.isNaN(startMs)) return false
  return Date.now() < startMs
}

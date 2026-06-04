/** 与后端 ArrangementScheduleUtil.TICKET_SALES_CLOSE_BEFORE_MINUTES 一致 */
export const TICKET_CLOSE_BEFORE_MINUTES = 20

/**
 * 是否仍可购票：当前时间须早于「开场时间 − TICKET_CLOSE_BEFORE_MINUTES 分钟」
 * @param {string} dateStr YYYY-MM-DD
 * @param {string} startHms HH:mm:ss
 */
export function isTicketSaleAllowed(dateStr, startHms) {
  if (!dateStr || !startHms) return false
  const parts = String(startHms).split(':')
  const h = Number(parts[0])
  const m = Number(parts[1] ?? 0)
  const s = Number(parts[2] ?? 0)
  if (Number.isNaN(h) || Number.isNaN(m) || Number.isNaN(s)) return false
  const pad = (n) => String(n).padStart(2, '0')
  const iso = `${String(dateStr).trim()}T${pad(h)}:${pad(m)}:${pad(s)}`
  const startMs = new Date(iso).getTime()
  if (Number.isNaN(startMs)) return false
  const stopSalesAt = startMs - TICKET_CLOSE_BEFORE_MINUTES * 60 * 1000
  return Date.now() < stopSalesAt
}

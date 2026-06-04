/**
 * 根据放映日、开始时间 (HH:mm:ss)、时长（分钟）计算结束时间 HH:mm:ss（与后端逻辑一致，可跨午夜仅体现在钟点上）。
 * 未选放映日时仍可根据开始时间+片长算出钟点，便于表单即时展示（提交后以后端按真实日期为准）。
 */
const FALLBACK_DATE = '2000-01-01'

export function computeEndTimeHms(dateStr, startHms, durationMinutes) {
  if (!startHms || durationMinutes == null || durationMinutes <= 0) {
    return ''
  }
  const d = dateStr && String(dateStr).trim() ? String(dateStr).trim() : FALLBACK_DATE
  const parts = String(startHms).split(':')
  const h = Number(parts[0])
  const m = Number(parts[1] ?? 0)
  const s = Number(parts[2] ?? 0)
  if (Number.isNaN(h) || Number.isNaN(m) || Number.isNaN(s)) return ''
  const pad = (n) => String(n).padStart(2, '0')
  const iso = `${d}T${pad(h)}:${pad(m)}:${pad(s)}`
  const base = new Date(iso)
  if (Number.isNaN(base.getTime())) return ''
  const end = new Date(base.getTime() + durationMinutes * 60000)
  return `${pad(end.getHours())}:${pad(end.getMinutes())}:${pad(end.getSeconds())}`
}

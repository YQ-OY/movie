export const RELEASE_PHASE = {
  SHOWING: 'showing',
  UPCOMING: 'upcoming',
}

const CN_PATTERN = /^(\d{4})年(\d{1,2})月(\d{1,2})日$/

function todayKey() {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

/** 统一为 YYYY-MM-DD；无法解析返回 null */
export function normalizeReleaseTime(releaseTime) {
  if (!releaseTime || typeof releaseTime !== 'string') return null
  const trimmed = releaseTime.trim()
  if (/^\d{4}-\d{2}-\d{2}$/.test(trimmed)) return trimmed
  const m = trimmed.match(CN_PATTERN)
  if (m) {
    const y = m[1]
    const mo = String(m[2]).padStart(2, '0')
    const d = String(m[3]).padStart(2, '0')
    return `${y}-${mo}-${d}`
  }
  return null
}

export function isShowing(releaseTime) {
  const key = normalizeReleaseTime(releaseTime)
  if (!key) return false
  return key <= todayKey()
}

export function isUpcoming(releaseTime) {
  const key = normalizeReleaseTime(releaseTime)
  if (!key) return false
  return key > todayKey()
}

/** 展示用：2026-03-15 → 3月15日 */
export function formatReleaseDisplay(releaseTime) {
  const key = normalizeReleaseTime(releaseTime)
  if (!key) return releaseTime || ''
  const [, month, day] = key.split('-')
  return `${Number(month)}月${Number(day)}日`
}

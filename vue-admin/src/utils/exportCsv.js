function escapeCsvCell(value) {
  return `"${String(value ?? '').replace(/"/g, '""')}"`
}

export function downloadCsv(filename, headers, rows) {
  const lines = []
  if (Array.isArray(headers) && headers.length) {
    lines.push(headers.map(escapeCsvCell).join(','))
  }
  ;(rows || []).forEach((row) => {
    const values = Array.isArray(row) ? row : headers.map((key) => row?.[key])
    lines.push(values.map(escapeCsvCell).join(','))
  })
  const blob = new Blob(['\ufeff' + lines.join('\n')], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = filename
  link.click()
  URL.revokeObjectURL(link.href)
}

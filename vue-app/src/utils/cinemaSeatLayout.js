/** 影院座位布局：每排左 5 + 过道 + 右 5，共 10 座/排 */
export const SEATS_PER_ROW = 20
export const AISLE_AFTER = 10

export function getSeatStatus(seatNo, { userSelectSeats, soldSeats, lockedSeats }) {
  if (userSelectSeats.includes(seatNo)) return 'selected'
  if (soldSeats.includes(seatNo)) return 'sold'
  if (lockedSeats.includes(seatNo)) return 'locked'
  return 'available'
}

export function buildSeatRows(seatNumber, statusContext) {
  const rows = []
  let seatNo = 1
  while (seatNo <= seatNumber) {
    const cells = []
    for (let col = 0; col < SEATS_PER_ROW && seatNo <= seatNumber; col++) {
      if (col === AISLE_AFTER) {
        cells.push({ type: 'aisle' })
      }
      cells.push({
        type: 'seat',
        seatNo,
        status: getSeatStatus(seatNo, statusContext),
      })
      seatNo++
    }
    rows.push({ rowIndex: rows.length + 1, cells })
  }
  return rows
}

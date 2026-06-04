import service from '../utils/request'

export function getDashboard() {
  return service({
    url: '/stats/dashboard',
    method: 'get',
    timeout: 25000,
  })
}

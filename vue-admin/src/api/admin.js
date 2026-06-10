import service from "@/utils/request";

// 获取当前管理员信息
export function getCurrentAdmin() {
  return service({
    url: '/admin/current',
    method: 'get'
  });
}

// 更新管理员信息（使用现有 POST 接口）
export function updateAdmin(data, options = {}) {
  return service({
    url: '/admin/update',
    method: 'post',
    data,
    silent: options.silent,
  });
}
// 疫苗相关API
import request from '@/utils/request'

// 分页查询疫苗
export function getVaccinePage(params) {
  return request.get('/vaccine/page', { params })
}

// 获取疫苗详情
export function getVaccineById(id) {
  return request.get(`/vaccine/${id}`)
}

// 获取所有疫苗
export function getVaccineList(params) {
  return request.get('/vaccine/list', { params })
}

// 新增/修改疫苗
export function saveVaccine(data) {
  return request.post('/vaccine', data)
}

// 删除疫苗
export function deleteVaccine(id) {
  return request.delete(`/vaccine/${id}`)
}

// 更新疫苗状态
export function updateVaccineStatus(id, status) {
  return request.put(`/vaccine/${id}/status/${status}`)
}

// 预约相关API
import request from '@/utils/request'

// 分页查询预约
export function getAppointmentPage(params) {
  return request.get('/appointment/page', { params })
}

// 获取预约详情
export function getAppointmentById(id) {
  return request.get(`/appointment/${id}`)
}

// 我的预约列表
export function getMyAppointments() {
  return request.get('/appointment/my')
}

// 创建预约
export function createAppointment(data) {
  return request.post('/appointment', data)
}

// 审核预约
export function auditAppointment(data) {
  return request.post('/appointment/audit', data)
}

// 支付预约
export function payAppointment(id) {
  return request.post(`/appointment/${id}/pay`)
}

// 确认接种
export function confirmVaccinate(id) {
  return request.post(`/appointment/${id}/vaccinate`)
}

// 取消预约
export function cancelAppointment(id) {
  return request.post(`/appointment/${id}/cancel`)
}

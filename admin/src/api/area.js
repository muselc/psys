import request from '@/utils/request'

export function listArea(query) {
  return request({
    url: '/area/list',
    method: 'get',
    params: query
  })
}

export function listSubArea(query) {
  return request({
    url: '/area/clist',
    method: 'get',
    params: query
  })
}

export function getAreaData(query) {
  return request({
    url: '/area/areaData',
    method: 'get'
  })
}

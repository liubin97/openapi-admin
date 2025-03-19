/**
 * API请求工具类
 */

// 基础URL，可以根据环境配置
const BASE_URL = ''

/**
 * 获取请求头
 * @returns {Object} 请求头对象
 */
const getHeaders = () => {
  const headers = {
    'Content-Type': 'application/json'
  }
  
  const token = localStorage.getItem('token')
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  
  return headers
}

/**
 * 发送GET请求
 * @param {string} url - 请求地址
 * @param {Object} params - 查询参数
 * @returns {Promise} 请求结果
 */
export const get = async (url, params = {}) => {
  try {
    // 构建查询参数
    const queryParams = new URLSearchParams()
    Object.keys(params).forEach(key => {
      if (params[key] !== undefined && params[key] !== '') {
        queryParams.append(key, params[key])
      }
    })
    
    const queryString = queryParams.toString()
    const requestUrl = `${BASE_URL}${url}${queryString ? `?${queryString}` : ''}`
    
    const response = await fetch(requestUrl, {
      method: 'GET',
      headers: getHeaders()
    })
    
    if (!response.ok) {
      throw new Error(`请求失败: ${response.status}`)
    }
    
    return await response.json()
  } catch (error) {
    console.error('GET请求错误:', error)
    throw error
  }
}

/**
 * 发送POST请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求数据
 * @returns {Promise} 请求结果
 */
export const post = async (url, data = {}) => {
  try {
    const response = await fetch(`${BASE_URL}${url}`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data)
    })
    
    if (!response.ok) {
      throw new Error(`请求失败: ${response.status}`)
    }
    
    return await response.json()
  } catch (error) {
    console.error('POST请求错误:', error)
    throw error
  }
}

/**
 * 发送PUT请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求数据
 * @returns {Promise} 请求结果
 */
export const put = async (url, data = {}) => {
  try {
    const response = await fetch(`${BASE_URL}${url}`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(data)
    })
    
    if (!response.ok) {
      throw new Error(`请求失败: ${response.status}`)
    }
    
    return await response.json()
  } catch (error) {
    console.error('PUT请求错误:', error)
    throw error
  }
}

/**
 * 发送DELETE请求
 * @param {string} url - 请求地址
 * @returns {Promise} 请求结果
 */
export const del = async (url) => {
  try {
    const response = await fetch(`${BASE_URL}${url}`, {
      method: 'DELETE',
      headers: getHeaders()
    })
    
    if (!response.ok) {
      throw new Error(`请求失败: ${response.status}`)
    }
    
    return await response.json()
  } catch (error) {
    console.error('DELETE请求错误:', error)
    throw error
  }

}
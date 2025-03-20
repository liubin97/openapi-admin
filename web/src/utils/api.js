/**
 * API请求工具类
 */

// 基础URL，可以根据环境配置
const BASE_URL = '/api'

// 请求拦截器
const requestInterceptor = async (config) => {
  // 显示全局加载动画
  document.getElementById('global-loader').style.display = 'block';
  
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
}

// 响应拦截器
const responseInterceptor = async (response) => {
  // 隐藏加载动画
  document.getElementById('global-loader').style.display = 'none';

  try {
    const data = await response.json();
    
    if (!response.ok) {
      const errorMessage = data.message || `请求失败: ${response.status}`;
      showErrorModal(errorMessage);
      throw new Error(errorMessage);
    }
    
    // 统一处理业务错误
    if (data.code && data.code !== 200) {
      const errorMessage = data.message || '操作失败';
      showErrorModal(errorMessage);
      throw new Error(errorMessage);
    }
    return data.data
  } catch (error) {
    const errorMessage = error.message || '请求失败';
    showErrorModal(errorMessage);
    throw error;
  }
}

/**
 * 获取请求头
 * @returns {Object} 请求头对象
 */
const getHeaders = () => {
  return {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
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
    
    const config = {
      method: 'GET',
      headers: getHeaders()
    }
    
    const finalConfig = await requestInterceptor(config)
    const response = await fetch(requestUrl, finalConfig)
    return await responseInterceptor(response)
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
    const config = {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data)
    }
    
    const finalConfig = await requestInterceptor(config)
    const response = await fetch(`${BASE_URL}${url}`, finalConfig)
    return await responseInterceptor(response)
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
    const config = {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(data)
    }
    
    const finalConfig = await requestInterceptor(config)
    const response = await fetch(`${BASE_URL}${url}`, finalConfig)
    return await responseInterceptor(response)
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
    const config = {
      method: 'DELETE',
      headers: getHeaders()
    }
    
    const finalConfig = await requestInterceptor(config)
    const response = await fetch(`${BASE_URL}${url}`, finalConfig)
    return await responseInterceptor(response)
  } catch (error) {
    console.error('DELETE请求错误:', error)
    throw error
  }
}

// 新增错误提示方法
const showErrorModal = (message) => {
  const modal = document.createElement('div');
  modal.className = 'error-modal';
  modal.innerHTML = `
    <div class="modal-content">
      <h3>请求错误</h3>
      <p>${message}</p>
      <button onclick="this.parentElement.parentElement.remove()">关闭</button>
    </div>
  `;
  document.body.appendChild(modal);
};
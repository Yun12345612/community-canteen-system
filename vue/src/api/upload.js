// src/api/upload.js
import request from '@/utils/request';

/**
 * 通用文件上传接口
 * @param {File} file - 要上传的文件对象
 * @returns {Promise} 包含文件URL的Promise
 */
export function uploadFile(file) {
  // 创建FormData对象
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 批量文件上传（可选）
 * @param {Array<File>} files - 文件数组
 * @returns {Promise} 包含文件URL数组的Promise
 */
export function uploadFiles(files) {
  const formData = new FormData();
  files.forEach((file, index) => {
    formData.append('files', file);
  });
  
  return request({
    url: '/common/upload/batch', // 如果后端支持批量上传
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}
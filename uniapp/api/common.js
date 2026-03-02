const baseUrl = 'http://127.0.0.1:8080'

// 文件上传接口
export function uploadFile(filePath) {
  const token = uni.getStorageSync('token') || ''
  
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: baseUrl + '/common/upload',
      filePath: filePath,
      name: 'file',
      header: {
        token: token
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const data = JSON.parse(res.data)
          resolve(data)
        } else {
          reject(res)
        }
      },
      fail: reject
    })
  })
}

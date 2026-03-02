"use strict";
const common_vendor = require("../common/vendor.js");
const baseUrl = "http://127.0.0.1:8080";
function uploadFile(filePath) {
  const token = common_vendor.index.getStorageSync("token") || "";
  return new Promise((resolve, reject) => {
    common_vendor.index.uploadFile({
      url: baseUrl + "/common/upload",
      filePath,
      name: "file",
      header: {
        token
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const data = JSON.parse(res.data);
          resolve(data);
        } else {
          reject(res);
        }
      },
      fail: reject
    });
  });
}
exports.uploadFile = uploadFile;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/common.js.map

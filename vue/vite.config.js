import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  // 路径别名配置（保留原有，无需修改）
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  // 核心：添加Vite代理转发配置
  server: {
    proxy: {
      // 匹配所有以/api开头的请求（自定义匹配规则，建议统一接口前缀）
      '/api': {
        target: 'http://localhost:8080', // 后端真实接口基础地址（必须正确）
        changeOrigin: true, // 关键：开启跨域源模拟，让后端认为请求来自合法源
        rewrite: (path) => path.replace(/^\/api/, '') // 关键：重写请求路径，移除/api前缀
      }
    }
  }
})
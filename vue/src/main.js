import { createApp } from 'vue'
// 全局导入 Element Plus 组件和样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 导入路由实例
import router from './router'
// 导入 Element Plus 所有图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 导入根组件
import App from './App.vue'

// 新增1：导入 Pinia 核心方法（必须加）
import { createPinia } from 'pinia'

// 核心：只创建1次Vue应用实例（全局唯一）
const app = createApp(App)

// 步骤1：挂载路由（必须先挂载路由，保证页面跳转正常）
app.use(router)

// 新增2：创建 Pinia 实例并全局挂载（核心配置，放在路由后、Element Plus 前均可）
const pinia = createPinia()
app.use(pinia)

// 步骤2：挂载Element Plus组件库
app.use(ElementPlus)
// 步骤3：全局注册所有Element Plus图标（注册到唯一的app实例上）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 步骤4：最终挂载到DOM节点（仅执行1次mount，放在所有配置最后）
app.mount('#app')
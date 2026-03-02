import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
// 直接导入views下的所有页面
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Home from '@/views/Home.vue'
import HolidaySetting from '@/views/HolidaySetting.vue'
import DishManager from '@/views/DishManager.vue'
import MerchantOrder from '@/views/MerchantOrder.vue'
import Evaluation from '@/views/Evaluation.vue'

// 路由规则
const routes = [
  {
    path: '/',
    redirect: '/login' // 默认访问重定向到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: Login // 登录页路由
  },
  {
    path: '/register',
    name: 'Register',
    component: Register // 注册页路由
  },
  {
    path: '/home',
    name: 'Home',
    component: Home, // 后台首页路由
    meta: { requiresAuth: true }, // 标记：需要登录才能访问
    // 子路由配置，专门匹配后台内的子页面
    children: [
      {
        path: 'shop/holiday-setting', // 子路由相对路径
        name: 'HolidaySetting',
        component: HolidaySetting, // 指向views下的HolidaySetting.vue
        meta: { requiresAuth: true } // 继承登录鉴权
      },
      {
       path: 'shop/dish-manager', // 子路由相对路径，
        name: 'DishManager', // 路由名称，与组件名一致，便于编程式导航
        component: DishManager, // 指向菜品管理页面组件
        meta: { requiresAuth: true, title: '菜品管理' } 
      },
       {
        path: 'shop/order-manager', // 子路由相对路径（与菜品管理同级，统一shop前缀）
        name: 'MerchantOrder', // 路由名称，与组件名一致，便于编程式导航
        component: MerchantOrder, // 指向订单管理页面组件
        meta: { requiresAuth: true, title: '订单管理' } // 鉴权+页面标题，与菜品管理一致
      },
      // 评价管理路由
      {
        path: 'shop/evaluation-manager', // 子路由相对路径（与菜品管理同级，统一shop前缀）
        name: 'Evaluation', // 路由名称，与组件名一致，便于编程式导航
        component: Evaluation, // 指向评价管理页面组件
        meta: { requiresAuth: true, title: '评价管理' } // 鉴权+页面标题，与菜品管理一致
      }
    ]
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(), // HTML5历史模式（无#号）
  routes
})

// 全局路由守卫：登录鉴权
router.beforeEach((to, from, next) => {
  // 判断目标路由是否需要登录
  if (to.meta.requiresAuth) {
    // 从本地缓存获取登录信息
    const merchantInfo = localStorage.getItem('merchantInfo')
    if (merchantInfo) {
      // 已登录，直接放行
      next()
    } else {
      // 未登录，重定向到登录页并提示
      ElMessage.warning('请先登录后再访问后台！')
      next('/login')
    }
  } else {
    // 无需登录的路由（登录/注册），直接放行
    next()
  }
})

export default router
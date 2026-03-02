<template>
  <div class="admin-app">
    <!-- Element Plus 布局容器：整体后台布局 -->
    <el-container style="height: 100vh">
      <!-- 侧边导航栏：可折叠 -->
      <el-aside width="200px" class="admin-aside">
        <div class="aside-logo">社区食堂管理系统</div>
        <!-- 侧边导航组件：绑定菜单选择事件 -->
        <el-menu
          default-active="1"
          class="admin-menu"
          background-color="#2e3b4e"
          text-color="#fff"
          active-text-color="#ffd04b"
          unique-opened
          @select="handleMenuSelect"
        >
          <el-menu-item index="1">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span>菜品管理</span>
            </template>
            <el-menu-item index="2-1">菜品列表</el-menu-item>
            <el-menu-item index="2-4">订单管理</el-menu-item>
          </el-sub-menu>

          <!-- 评价管理（父菜单） -->
          <el-sub-menu index="3">
            <template #title>
              <el-icon><ChatDotRound /></el-icon>
              <span>评价管理</span>
            </template>
            <!-- 只保留一个子菜单：评价列表 -->
            <el-menu-item index="3-1">评价列表</el-menu-item>
          </el-sub-menu>

          <!-- 用户管理（index改为4） -->
          <el-sub-menu index="4">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="4-1">管理员列表</el-menu-item>
            <el-menu-item index="4-2">用户信息</el-menu-item>
          </el-sub-menu>

          <!-- 系统设置（index改为5） -->
          <el-sub-menu index="5">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="5-1">基础配置</el-menu-item>
            <el-menu-item index="5-2">权限管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 右侧主内容区域：顶部导航 + 首页完整内容 -->
      <el-container>
        <!-- 顶部导航栏 -->
        <el-header class="admin-header">
          <div class="header-left">
            <!-- 折叠侧边栏按钮 -->
            <el-icon @click="toggleAside" class="toggle-btn"
              ><MenuFold
            /></el-icon>
          </div>
          <div class="header-right">
            <!-- 搜索框 -->
            <el-input placeholder="搜索功能" class="search-input" size="small">
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <!-- 通知图标 -->
            <el-icon class="header-icon"><Bell /></el-icon>
            <!-- 用户信息下拉 -->
            <el-dropdown trigger="click">
              <div class="user-info">
                <el-avatar
                  src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
                  size="small"
                />
                <span>{{ merchantName || "管理员" }}</span>
                <el-icon class="arrow-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>个人中心</el-dropdown-item>
                  <el-dropdown-item>修改密码</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout"
                    >退出登录</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容区：首页完整内容 + 子路由渲染容器 -->
        <el-main class="admin-main">
          <!-- 动态面包屑导航 -->
          <el-breadcrumb class="breadcrumb" separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }"
              >首页</el-breadcrumb-item
            >
            <el-breadcrumb-item
              v-if="$route.meta.title && $route.name !== 'Home'"
            >
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>

          <!-- 子路由渲染容器：菜品管理/休假设置在此渲染 -->
          <router-view />

          <!-- 仅当路由为 /home 根路径时，才显示首页的所有模块 -->
          <template v-if="$route.path === '/home'">
            <!-- 1. 核心数据卡片区域 -->
            <div class="card-container">
              <el-card class="stat-card" shadow="hover">
                <div class="card-content">
                  <div class="card-title">今日订单</div>
                  <div class="card-num">128</div>
                  <div class="card-trend positive">+12.5%</div>
                </div>
                <el-icon class="card-icon"><ShoppingCart /></el-icon>
              </el-card>
              <el-card class="stat-card" shadow="hover">
                <div class="card-content">
                  <div class="card-title">今日营收</div>
                  <div class="card-num">¥2680</div>
                  <div class="card-trend negative">-2.1%</div>
                </div>
                <el-icon class="card-icon"><Wallet /></el-icon>
              </el-card>
              <el-card class="stat-card" shadow="hover">
                <div class="card-content">
                  <div class="card-title">菜品总数</div>
                  <div class="card-num">86</div>
                  <div class="card-trend positive">+5.2%</div>
                </div>
                <el-icon class="card-icon"><Goods /></el-icon>
              </el-card>
              <el-card class="stat-card" shadow="hover">
                <div class="card-content">
                  <div class="card-title">总用户数</div>
                  <div class="card-num">526</div>
                  <div class="card-trend positive">+8.8%</div>
                </div>
                <el-icon class="card-icon"><UserFilled /></el-icon>
              </el-card>
              <el-card class="stat-card" shadow="hover">
                <div class="card-content">
                  <div class="card-title">上架菜品数</div>
                  <div class="card-num">79</div>
                  <div class="card-trend positive">+3.1%</div>
                </div>
                <el-icon class="card-icon"><Check /></el-icon>
              </el-card>
              <el-card class="stat-card" shadow="hover">
                <div class="card-content">
                  <div class="card-title">复购率</div>
                  <div class="card-num">42.8%</div>
                  <div class="card-trend positive">+1.5%</div>
                </div>
                <el-icon class="card-icon"><Refresh /></el-icon>
              </el-card>
            </div>

            <!-- 2. 营业状态设置模块 -->
            <el-card class="business-setting-card" shadow="hover">
              <div class="card-header">
                <h3>店铺营业状态管理</h3>
                <el-tag
                  :type="currentBusinessStatus === 0 ? 'success' : 'danger'"
                >
                  {{
                    currentBusinessStatus === 0
                      ? "当前：营业中"
                      : `当前：休假中（${holidayRemainingTime}）`
                  }}
                </el-tag>
              </div>
              <el-form
                :model="holidayForm"
                :rules="holidayRules"
                ref="formRef"
                label-width="120px"
                class="holiday-form"
              >
                <el-form-item label="营业状态" prop="isHoliday">
                  <el-radio-group v-model="holidayForm.isHoliday">
                    <el-radio label="0">恢复营业</el-radio>
                    <el-radio label="1">设置休假</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item
                  label="休假开始时间"
                  prop="holidayStart"
                  v-if="holidayForm.isHoliday === '1'"
                >
                  <el-date-picker
                    v-model="holidayForm.holidayStart"
                    type="date"
                    placeholder="选择开始日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                    :disabled-date="disabledBeforeToday"
                  />
                </el-form-item>
                <el-form-item
                  label="休假结束时间"
                  prop="holidayEnd"
                  v-if="holidayForm.isHoliday === '1'"
                >
                  <el-date-picker
                    v-model="holidayForm.holidayEnd"
                    type="date"
                    placeholder="选择结束日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                    :disabled-date="
                      (time) =>
                        disabledBeforeStart(time, holidayForm.holidayStart)
                    "
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    @click="handleSubmit"
                    :loading="loading"
                    >确认提交</el-button
                  >
                  <el-button
                    type="default"
                    @click="handleReset"
                    style="margin-left: 10px"
                    >重置</el-button
                  >
                </el-form-item>
              </el-form>
            </el-card>

            <!-- 3. 近期订单概览模块 -->
            <el-card
              class="recent-orders-card"
              shadow="hover"
              style="margin-top: 20px"
            >
              <div class="card-header">
                <h3>近期订单概览</h3>
                <el-button type="text" @click="goToOrderList"
                  >查看全部</el-button
                >
              </div>
              <el-table
                :data="recentOrders"
                border
                stripe
                size="small"
                style="width: 100%"
              >
                <el-table-column
                  prop="orderNo"
                  label="订单号"
                  width="180"
                  align="center"
                />
                <el-table-column
                  prop="userName"
                  label="用户名称"
                  min-width="120"
                />
                <el-table-column
                  prop="orderTime"
                  label="下单时间"
                  width="180"
                  align="center"
                />
                <el-table-column
                  prop="amount"
                  label="订单金额(¥)"
                  width="120"
                  align="center"
                />
                <el-table-column
                  prop="status"
                  label="订单状态"
                  width="120"
                  align="center"
                >
                  <template #default="scope">
                    <el-tag
                      :type="
                        scope.row.status === '已完成'
                          ? 'success'
                          : scope.row.status === '待支付'
                          ? 'warning'
                          : 'info'
                      "
                    >
                      {{ scope.row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </template>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
// 导入Vue3核心API
import { ref, reactive, onMounted } from "vue";
// 导入Element Plus组件、图标、路由
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";

// 导入商家接口
import { merchantSetHoliday } from "@/api/merchant";

// 路由实例
const router = useRouter();
// 商家名称（从缓存获取）
const merchantName = ref("");
// 侧边栏折叠状态
const isAsideFold = ref(false);
// 表单相关配置
const formRef = ref(null);
const loading = ref(false);
const currentBusinessStatus = ref(0); // 0-营业中，1-休假中
const holidayRemainingTime = ref(""); // 休假剩余时间

// 营业状态设置表单数据
const merchantInfo = JSON.parse(localStorage.getItem("merchantInfo") || "{}");
const holidayForm = reactive({
  isHoliday: "0",
  holidayStart: "",
  holidayEnd: "",
});

// 日期校验：结束时间晚于开始时间
const validateEndAfterStart = (rule, value, callback) => {
  if (!value) return callback();
  if (!holidayForm.holidayStart) {
    callback(new Error("请先选择休假开始时间"));
    return;
  }
  const start = new Date(holidayForm.holidayStart);
  const end = new Date(value);
  if (end < start) {
    callback(new Error("休假结束时间不能早于开始时间"));
  } else {
    callback();
  }
};

// 禁用今天之前的日期
const disabledBeforeToday = (time) => {
  return time < new Date(Date.now() - 8.64e7);
};

// 禁用开始时间之前的日期
const disabledBeforeStart = (time, startDate) => {
  if (!startDate) return true;
  return time < new Date(startDate);
};

// 表单校验规则
const holidayRules = reactive({
  isHoliday: [
    { required: true, message: "请选择店铺营业状态", trigger: "change" },
  ],
  holidayStart: [
    { required: true, message: "请选择休假开始时间", trigger: "blur" },
  ],
  holidayEnd: [
    { required: true, message: "请选择休假结束时间", trigger: "blur" },
    { validator: validateEndAfterStart, trigger: "blur" },
  ],
});

// 近期订单模拟数据
const recentOrders = ref([
  {
    orderNo: "ORDER20240520001",
    userName: "张三",
    orderTime: "2024-05-20 12:30:25",
    amount: 45.0,
    status: "已完成",
  },
  {
    orderNo: "ORDER20240520002",
    userName: "李四",
    orderTime: "2024-05-20 18:15:42",
    amount: 32.5,
    status: "已完成",
  },
  {
    orderNo: "ORDER20240520003",
    userName: "王五",
    orderTime: "2024-05-20 20:08:16",
    amount: 58.0,
    status: "待支付",
  },
  {
    orderNo: "ORDER20240521001",
    userName: "赵六",
    orderTime: "2024-05-21 09:45:33",
    amount: 28.0,
    status: "已完成",
  },
  {
    orderNo: "ORDER20240521002",
    userName: "孙七",
    orderTime: "2024-05-21 11:22:57",
    amount: 68.5,
    status: "配送中",
  },
]);

// 页面挂载初始化
onMounted(() => {
  // 获取商家名称
  try {
    const info = JSON.parse(localStorage.getItem("merchantInfo") || "{}");
    merchantName.value = info.merchantName || info.name || "";
  } catch (e) {
    merchantName.value = "";
    console.error("获取商家信息失败：", e);
  }
  // 获取当前营业状态
  getCurrentBusinessStatus();
});

// 切换侧边栏折叠
const toggleAside = () => {
  isAsideFold.value = !isAsideFold.value;
};

// 退出登录
const handleLogout = () => {
  localStorage.removeItem("merchantInfo");
  ElMessage.success("退出登录成功！");
  router.push("/login");
};

// 侧边栏菜单选择事件 - 核心路由跳转逻辑
const handleMenuSelect = (index) => {
  switch (index) {
    case "1":
      router.push("/home");
      break;
    case "2-1":
    case "2-2":
      router.push({ name: "DishManager" });
      break;
    case "2-4":
      router.push({ name: "MerchantOrder" });
      break;
    case "3-1": // 评价列表
      router.push({ name: "Evaluation" });
      break;
    case "4-1": // 用户管理菜单项
      ElMessage.info("用户管理功能暂未开发！");
      break;
    case "5-1": // 系统设置 -> 休假设置
      router.push({ name: "HolidaySetting" });
      break;
    default:
      ElMessage.info("该功能暂未开发，敬请期待！");
      break;
  }
};

// 获取当前营业状态（模拟接口）
const getCurrentBusinessStatus = () => {
  setTimeout(() => {
    currentBusinessStatus.value = 0;
    holidayForm.isHoliday = currentBusinessStatus.value + "";
  }, 500);
};

// 提交营业状态设置
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  const confirmText =
    holidayForm.isHoliday === "1"
      ? `确认设置休假（${holidayForm.holidayStart} 至 ${holidayForm.holidayEnd}）？休假期间菜品将不可购买！`
      : "确认恢复营业？恢复后菜品将重新开放购买！";

  try {
    await ElMessageBox.confirm(confirmText, "操作确认", {
      confirmButtonText: "确认",
      cancelButtonText: "取消",
      type: holidayForm.isHoliday === "1" ? "warning" : "info",
    });
  } catch (err) {
    ElMessage.info("已取消操作");
    return;
  }

  loading.value = true;
  try {
    const submitData = {
      isHoliday: Number(holidayForm.isHoliday),
      holidayStart: holidayForm.holidayStart,
      holidayEnd: holidayForm.holidayEnd,
    };
    // 直接调用独立函数
    const res = await merchantSetHoliday(submitData);
    ElMessage.success(
      res.message ||
        (holidayForm.isHoliday === "1" ? "休假设置成功" : "恢复营业成功")
    );
    currentBusinessStatus.value = Number(holidayForm.isHoliday);
    if (currentBusinessStatus.value === 1) {
      const endTime = new Date(holidayForm.holidayEnd);
      const now = new Date();
      const diffDay = Math.ceil((endTime - now) / (1000 * 60 * 60 * 24));
      holidayRemainingTime.value =
        diffDay > 0 ? `剩余${diffDay}天` : "今日恢复";
    } else {
      holidayRemainingTime.value = "";
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || "操作失败，请稍后重试");
    console.error("营业状态设置失败：", error);
  } finally {
    loading.value = false;
  }
};

// 重置表单
const handleReset = () => {
  formRef.value.resetFields();
  holidayForm.isHoliday = currentBusinessStatus.value + "";
};

// 跳转到订单列表（预留）
const goToOrderList = () => {
  ElMessage.info("订单列表功能暂未开发！");
  // router.push('/home/order/list')
};
</script>

<style scoped>
/* 全局容器 */
.admin-app {
  height: 100%;
  margin: 0;
  padding: 0;
}

/* 侧边栏 */
.admin-aside {
  background-color: #2e3b4e;
  color: #fff;
  transition: width 0.3s ease;
}
.aside-logo {
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  padding: 20px 0;
  border-bottom: 1px solid #404e62;
}
.admin-menu {
  height: calc(100% - 60px);
  border-right: none;
}

/* 顶部导航 */
.admin-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  padding: 0 10px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.search-input {
  width: 200px;
}
.header-icon {
  font-size: 18px;
  cursor: pointer;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.arrow-icon {
  font-size: 14px;
}

/* 主内容区 */
.admin-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
.breadcrumb {
  margin-bottom: 20px;
}

/* 数据卡片容器 */
.card-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.stat-card {
  flex: 1;
  min-width: 180px;
  position: relative;
  height: 120px;
}
.card-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
  padding-right: 40px;
}
.card-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}
.card-num {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}
.card-trend {
  font-size: 12px;
}
.positive {
  color: #67c23a;
}
.negative {
  color: #f56c6c;
}
.card-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 36px;
  color: #e5eaf3;
}

/* 营业状态设置卡片 */
.business-setting-card {
  margin-bottom: 20px;
}
.business-setting-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.business-setting-card .card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}
.holiday-form {
  max-width: 600px;
}
.el-form-item {
  margin-bottom: 20px;
}

/* 近期订单卡片 */
.recent-orders-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.recent-orders-card .card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}
</style>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html,
body {
  height: 100%;
}
</style>
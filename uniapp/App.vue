<script>
export default {
  globalData: {
    // 仅保留必要全局数据，购物车逻辑移至工具类
    merchantId: 1
  },
  onLaunch() {
    console.log("=== App 实例已初始化 ===")
  }
}
</script>

<style>
page {
  background-color: #f7f8fa;
}
button::after {
  border: none;
}
</style>
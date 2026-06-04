<template>
  <div class="me">
    <div class="main">
      <aside class="aside">
        <div class="aside-header">
          <div class="aside-icon-wrap" aria-hidden="true">
            <el-icon class="aside-header-icon"><UserFilled /></el-icon>
          </div>
          <div class="aside-title-wrap">
            <div class="aside-title">个人中心</div>
            <div class="aside-sub">账户与订单</div>
          </div>
        </div>
        <el-menu
            router
            :default-active="activePath"
            class="me-menu"
        >
          <el-menu-item index="/me/cart">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </el-menu-item>
          <el-menu-item index="/me/order">
            <el-icon><Tickets /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/me/setting">
            <el-icon><Setting /></el-icon>
            <span>基本设置</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <div class="content">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MeIndex',
  computed: {
    activePath() {
      return this.$route.path
    },
  },
}
</script>

<style scoped>
.me {
  padding: 70px 180px;
  box-sizing: border-box;
}

.main {
  display: flex;
  min-height: 900px;
  background: #ffffff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #ebeef5;
  overflow: hidden;
}

.aside {
  flex: 0 0 240px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  border-right: 1px solid #e4e7ed;
}

.aside-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px 16px;
  border-bottom: 1px solid #ebeef5;
  background: rgba(255, 255, 255, 0.6);
}

.aside-icon-wrap {
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
  box-shadow: 0 1px 2px rgba(64, 158, 255, 0.12);
}

.aside-header-icon {
  font-size: 24px;
}

.aside-title-wrap {
  min-width: 0;
}

.aside-title {
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 2px;
  color: #303133;
  line-height: 1.3;
}

.aside-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
  letter-spacing: 1px;
}

.me-menu {
  flex: 1;
  border-right: none;
  background: transparent !important;
  padding: 12px 8px 24px;
}

.me-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin-bottom: 4px;
  border-radius: 8px;
  letter-spacing: 1px;
  color: #606266;
}

.me-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

.me-menu :deep(.el-menu-item:hover) {
  background-color: #ecf5ff !important;
  color: #409eff;
}

.me-menu :deep(.el-menu-item.is-active) {
  background-color: #ecf5ff !important;
  color: #409eff !important;
  font-weight: 600;
}

.me-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 22px;
  background: #409eff;
  border-radius: 0 2px 2px 0;
}

.content {
  flex: 1;
  min-width: 0;
  padding: 24px 40px 40px;
  background: #ffffff;
}
</style>

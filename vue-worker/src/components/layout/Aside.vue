<template>
  <div class="aside" :class="{ 'is-collapse': isCollapse }">
    <!-- 侧边栏头部 -->
    <div class="aside-header">
      <img class="aside-logo" src="../../assets/img/logo.png" alt="" />
      <div class="aside-title" v-show="!isCollapse">厅有独钟 - 客服</div>
    </div>

    <!-- 菜单滚动区 -->
    <div class="aside-menu-scroll">
      <el-menu class="menu" :default-active="$route.path" :collapse="isCollapse" :collapse-transition="false"
        background-color="#ffffff" text-color="#334155" active-text-color="#409eff">
        <el-menu-item index="/leaving" @click="navigate('/leaving')">
          <el-icon>
            <Promotion />
          </el-icon>
          <span>影院留言</span>
        </el-menu-item>

        <el-menu-item index="/phone" @click="navigate('/phone')">
          <el-icon>
            <Phone />
          </el-icon>
          <span>电话回访</span>
        </el-menu-item>

        <el-menu-item index="/activity" @click="navigate('/activity')">
          <el-icon>
            <Flag />
          </el-icon>
          <span>活动安排</span>
        </el-menu-item>

        <el-menu-item index="/info" @click="navigate('/info')">
          <el-icon>
            <DataAnalysis />
          </el-icon>
          <span>信息统计</span>
        </el-menu-item>

        <el-menu-item index="/evaluate" @click="navigate('/evaluate')">
          <el-icon>
            <Management />
          </el-icon>
          <span>我的评价</span>
        </el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<script>
import { Promotion, Phone, Flag, DataAnalysis, Management, Setting } from '@element-plus/icons-vue'

export default {
  name: "Aside",
  components: { Promotion, Phone, Flag, DataAnalysis, Management, Setting },
  props: {
    isCollapse: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    navigate(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.aside {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-right: 1px solid #eaeef2;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.02);
  box-sizing: border-box;
  transition: width 0.3s ease;
  width: 250px;
}

.aside.is-collapse {
  width: 64px;
}

.aside-header {
  height: 60px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0px 22px;
  font-size: 18px;
  font-weight: 500;
  background-color: #ffffff;
  overflow: hidden;
  white-space: nowrap;
}

.aside.is-collapse .aside-header {
  justify-content: center;
  padding: 0;
}

.aside-logo {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  border-radius: 8px;
}

.aside-title {
  line-height: 1.25;
  padding-top: 2px;
  color: #1f2f3d;
  font-weight: 600;
  letter-spacing: 1px;
}

.aside-menu-scroll {
  flex: 1;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 12px 0;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e0 #f1f5f9;
}

.aside-menu-scroll::-webkit-scrollbar {
  width: 4px;
  height: 4px;
}

.aside-menu-scroll::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.aside-menu-scroll::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 4px;
}

.aside-menu-scroll::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.menu {
  --el-menu-bg-color: #ffffff;
  --el-menu-text-color: #334155;
  --el-menu-active-color: #409eff;
  --el-menu-hover-bg-color: #f1f5f9;
  --el-menu-item-height: 44px;
  --el-menu-sub-item-height: 40px;
  --el-menu-icon-width: 24px;
  border-right: none;
  background-color: transparent;
}

.menu:not(.el-menu--collapse) {
  width: 100%;
}

.menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  margin: 4px 12px;
  border-radius: 10px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

.menu .el-menu-item .el-icon {
  margin-right: 12px;
  font-size: 18px;
  transition: transform 0.2s ease;
}

.menu .el-menu-item:hover {
  background-color: #f1f5f9 !important;
  transform: translateX(2px);
  color: #1e293b;
}

.menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #ecf5ff 0%, #e0edff 100%);
  color: #409eff;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.12);
  border-left: 3px solid #409eff;
  border-radius: 10px 6px 6px 10px;
}

.menu .el-menu-item.is-active .el-icon {
  color: #409eff;
}

.menu .el-menu-item:active {
  transform: scale(0.98);
}
</style>
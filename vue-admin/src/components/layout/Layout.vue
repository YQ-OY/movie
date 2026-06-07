<template>
  <el-container class="layout-root">
    <el-aside :width="isCollapse ? '64px' : '250px'" class="app-aside" :class="{ 'is-collapse': isCollapse }">
      <Aside :is-collapse="isCollapse" />
    </el-aside>
    <el-container class="layout-main-column">
      <el-header shadow="always" class="app-header">
        <Header :is-collapse="isCollapse" @toggle-collapse="toggleCollapse" />
      </el-header>
      <el-main class="app-main">
        <div class="content">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import Header from "@/components/layout/Header";
import Aside from "@/components/layout/Aside";

export default {
  name: "Layout",
  components: {
    Header,
    Aside
  },
  data() {
    return {
      isCollapse: false   // 侧边栏折叠状态
    };
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse;
    }
  }
};
</script>

<style scoped>
/* 整页占满视口，避免缩窄窗口时侧栏被 flex 压扁、右侧大块留白 */
.layout-root {
  height: 100vh;
  min-height: 100vh;
  width: 100%;
  max-width: 100%;
  overflow: hidden;
  box-sizing: border-box;
}

.app-aside {
  flex-shrink: 0 !important;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  /* 侧边栏背景改为白色，与之前改造一致 */
  border-right: 1px solid #eaeef2;
  transition: width 0.3s ease;
  box-sizing: border-box;
}

.layout-main-column {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-header {
  flex-shrink: 0;
  height: 60px !important;
  padding: 0;
  box-sizing: border-box;
  background: #ffffff;
  border-bottom: 1px solid #eaeef2;
}

.app-main {
  flex: 1;
  height: 0;
  overflow: hidden;
  padding: 0px;
  box-sizing: border-box;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  min-height: 0;
  width: 100%;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
</style>
<template>
  <el-container class="layout-root">
    <!-- 侧边栏：支持折叠，白色背景，带边框和阴影 -->
    <el-aside :width="isCollapse ? '64px' : '250px'" class="app-aside" :class="{ 'is-collapse': isCollapse }">
      <Aside :is-collapse="isCollapse" />
    </el-aside>

    <el-container class="layout-main-column">
      <!-- 头部：固定高度，渐变背景，带折叠触发按钮（由Header组件实现） -->
      <el-header shadow="always" class="app-header">
        <Header :is-collapse="isCollapse" @toggle-collapse="toggleCollapse" />
      </el-header>

      <!-- 主内容区：填充剩余空间，内部 content 卡片化 -->
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
/* 整页占满视口，避免缩窄窗口时侧栏被 flex 压扁 */
.layout-root {
  height: 100vh;
  min-height: 100vh;
  width: 100%;
  max-width: 100%;
  overflow: hidden;
  box-sizing: border-box;
}

/* 侧边栏：白色现代风格，带折叠过渡 */
.app-aside {
  flex-shrink: 0 !important;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-right: 1px solid #eaeef2;
  transition: width 0.3s ease;
  box-sizing: border-box;
}

/* 主内容列：填满剩余空间 */
.layout-main-column {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部：纯白底色，底部淡灰边框，与侧边栏协调 */
.app-header {
  flex-shrink: 0;
  height: 60px !important;
  padding: 0;
  box-sizing: border-box;
  background: #ffffff;
  border-bottom: 1px solid #eaeef2;
}

/* 主内容区：无内边距（内容区自行控制），背景柔和 */
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

/* 内容卡片：白色背景，圆角，轻阴影，内部滚动 */
.content {
  flex: 1;
  min-height: 0;
  width: 100%;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: auto;
}
</style>
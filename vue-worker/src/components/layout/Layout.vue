<template>
  <el-container class="layout-root">
    <el-aside width="250px" class="app-aside">
      <Aside/>
    </el-aside>
    <el-container class="layout-main-column">
      <el-header shadow="always" class="app-header">
        <Header/>
      </el-header>
      <el-main class="app-main">
        <div class="content">
          <router-view/>
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
}
</script>

<style scoped>
.layout-root {
  height: 100vh;
  min-height: 100vh;
  width: 100%;
  max-width: 100%;
  overflow: hidden;
  box-sizing: border-box;
}

.app-aside {
  width: 250px !important;
  min-width: 250px !important;
  max-width: 250px !important;
  flex-shrink: 0 !important;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: #242930;
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
  padding: 0 20px;
  box-sizing: border-box;
}

.app-main {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  padding: 20px;
  box-sizing: border-box;
  background: #f5f6f7;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  min-height: 0;
  width: 100%;
  background: #ffffff;
  border-radius: 4px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: auto;
}
</style>

<template>
  <el-container class="app-layout">
    <el-header class="app-header">
      <Header/>
    </el-header>
    <el-main class="app-main">
      <router-view/>
    </el-main>
    <el-footer class="app-footer">
      <Footer/>
    </el-footer>
  </el-container>
</template>

<script>
import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

export default {
  name: "Layout",
  components: {Footer, Header},
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: var(--app-bg);
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 200;
  padding: 0;
  height: 72px;
  background: var(--app-surface-header);
  box-shadow: var(--app-header-shadow);
  border-bottom: 1px solid var(--app-border);
}

.app-main {
  padding: 0;
  overflow: auto;
  background: var(--app-bg);
}

.app-footer {
  padding: 0;
  background: var(--app-surface-header);
  border-top: 1px solid var(--app-border);
}
</style>

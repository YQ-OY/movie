<template>
  <div class="aside">
    <div class="aside-header">
      <img class="aside-logo" src="../../assets/img/logo.png" alt=""/>
      <div class="aside-title">厅有独钟 - 客服</div>
    </div>
    <div class="aside-menu-scroll">
      <el-menu
          class="menu"
          background-color="#242930"
          text-color="#FFFFFF"
          active-text-color="#409EFF">

        <router-link to="/leaving">
          <el-menu-item style="padding-left: 65px" index="1">
            <el-icon><Promotion /></el-icon>
            <span>影院留言</span>
          </el-menu-item>
        </router-link>

        <router-link to="/phone">
          <el-menu-item style="padding-left: 65px" index="2">
            <el-icon><Phone /></el-icon>
            <span>电话回访</span>
          </el-menu-item>
        </router-link>

        <router-link to="/activity">
          <el-menu-item style="padding-left: 65px" index="3">
            <el-icon><Flag /></el-icon>
            <span>活动安排</span>
          </el-menu-item>
        </router-link>

        <router-link to="/info">
          <el-menu-item style="padding-left: 65px" index="4">
            <el-icon><DataAnalysis /></el-icon>
            <span>信息统计</span>
          </el-menu-item>
        </router-link>

        <router-link to="/evaluate">
          <el-menu-item style="padding-left: 65px" index="5">
            <el-icon><Management /></el-icon>
            <span>我的评价</span>
          </el-menu-item>
        </router-link>

        <router-link to="/setting">
          <el-menu-item style="padding-left: 65px" index="6">
            <el-icon><Setting /></el-icon>
            <span>个人设置</span>
          </el-menu-item>
        </router-link>

      </el-menu>
    </div>
  </div>
</template>

<script>
import { Promotion, Phone, Flag, DataAnalysis, Management, Setting } from '@element-plus/icons-vue'

export default {
  name: "Aside",
  components: { Promotion, Phone, Flag, DataAnalysis, Management, Setting }
}
</script>

<style scoped>
.aside {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  color: #ffffff;
  box-sizing: border-box;
}

.aside-header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 21px;
  padding-top: 28px;
  font-size: 18px;
  font-weight: 500;
  overflow: hidden;
}

.aside-logo {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.aside-title {
  line-height: 1.25;
  padding-top: 2px;
}

.aside-menu-scroll {
  flex: 1;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  padding-top: 8px;
}

.menu {
  font-size: 14px;
  border-right: 0;
  letter-spacing: 1px;
  font-weight: bold;
  font-family: "Helvetica Neue", Helvetica, Arial, "Microsoft Yahei", "Hiragino Sans GB", "Heiti SC", "WenQuanYi Micro Hei", sans-serif;
}
</style>

<template>
  <div class="aside">
    <div class="aside-header">
      <img class="aside-logo" src="../../assets/img/logo.png" alt=""/>
      <div class="aside-title">后台管理</div>
    </div>
    <div class="aside-menu-scroll">
      <el-menu
          class="menu"
          background-color="#242930"
          text-color="#FFFFFF"
          active-text-color="#409EFF">

        <!-- 系统管理员：全部菜单 -->
        <template v-if="isAdmin">
          <el-sub-menu index="2">
            <template #title>
              <el-icon><VideoCamera /></el-icon>
              <span>影视管理</span>
            </template>
            <el-menu-item-group>
              <router-link to="/film/list">
                <el-menu-item style="padding-left: 70px" index="2-1">电影列表</el-menu-item>
              </router-link>
              <router-link to="/film/arrange">
                <el-menu-item style="padding-left: 70px" index="2-4">院线排片</el-menu-item>
              </router-link>
              <router-link to="/film/add">
                <el-menu-item style="padding-left: 70px" index="2-2">新增电影</el-menu-item>
              </router-link>
              <router-link to="/film/poster">
                <el-menu-item style="padding-left: 70px" index="2-5">轮播海报</el-menu-item>
              </router-link>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="1">
            <template #title>
              <el-icon><UserFilled /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item-group>
              <router-link to="/user/list">
                <el-menu-item style="padding-left: 70px" index="1-1">用户列表</el-menu-item>
              </router-link>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="6">
            <template #title>
              <el-icon><Tickets /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item-group>
              <router-link to="/order/list">
                <el-menu-item style="padding-left: 70px" index="6-1">订单列表</el-menu-item>
              </router-link>
              <router-link to="/order/exception">
                <el-menu-item style="padding-left: 70px" index="6-2">异常订单</el-menu-item>
              </router-link>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="7">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>数据统计</span>
            </template>
            <el-menu-item-group>
              <router-link to="/stats/dashboard">
                <el-menu-item style="padding-left: 70px" index="7-1">运营概览</el-menu-item>
              </router-link>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon><Trophy /></el-icon>
              <span>员工管理</span>
            </template>
            <el-menu-item-group>
              <router-link to="/worker/list">
                <el-menu-item style="padding-left: 70px" index="3-1">员工列表</el-menu-item>
              </router-link>
              <router-link to="/worker/daily">
                <el-menu-item style="padding-left: 70px" index="3-2">每日工作</el-menu-item>
              </router-link>
              <router-link to="/worker/add">
                <el-menu-item style="padding-left: 70px" index="3-3">新增员工</el-menu-item>
              </router-link>
            </el-menu-item-group>
          </el-sub-menu>

          <router-link to="/api">
            <el-menu-item index="4">
              <el-icon><Document /></el-icon>
              <span>Api接口</span>
            </el-menu-item>
          </router-link>
        </template>

        <!-- 工作人员：菜单随 t_role 写入 JWT 的权限变化（与 workerPermissions.js 一致） -->
        <template v-else-if="isWorker">
          <!-- 影视管理：电影 + 院线排片 + 轮播海报（同一 ROLE_FILM_MANAGE） -->
          <el-sub-menu v-if="workerFilmMenu" index="w-film">
            <template #title>
              <el-icon><VideoCamera /></el-icon>
              <span>影视管理</span>
            </template>
            <el-menu-item-group>
              <router-link v-if="wFilmList" to="/film/list">
                <el-menu-item style="padding-left: 70px" index="w-2-1">电影列表</el-menu-item>
              </router-link>
              <router-link v-if="wFilmAdd" to="/film/add">
                <el-menu-item style="padding-left: 70px" index="w-2-2">新增电影</el-menu-item>
              </router-link>
              <router-link v-if="wFilmArrange" to="/film/arrange">
                <el-menu-item style="padding-left: 70px" index="w-2-4">院线排片</el-menu-item>
              </router-link>
              <router-link v-if="wFilmPoster" to="/film/poster">
                <el-menu-item style="padding-left: 70px" index="w-2-5">轮播海报</el-menu-item>
              </router-link>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu v-if="workerUserMenu" index="w-user">
            <template #title>
              <el-icon><UserFilled /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item-group>
              <router-link to="/user/list">
                <el-menu-item style="padding-left: 70px" index="w-1-1">用户列表</el-menu-item>
              </router-link>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu v-if="workerOrderMenu" index="w-order">
            <template #title>
              <el-icon><Tickets /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item-group>
              <router-link to="/order/list">
                <el-menu-item style="padding-left: 70px" index="w-6-1">订单列表</el-menu-item>
              </router-link>
              <router-link to="/order/exception">
                <el-menu-item style="padding-left: 70px" index="w-6-2">异常订单</el-menu-item>
              </router-link>
              <router-link to="/stats/dashboard">
                <el-menu-item style="padding-left: 70px" index="w-6-3">运营概览</el-menu-item>
              </router-link>
            </el-menu-item-group>
          </el-sub-menu>

          <router-link to="/worker/daily">
            <el-menu-item index="w-daily">
              <el-icon><List /></el-icon>
              <span>每日工作</span>
            </el-menu-item>
          </router-link>
          <router-link to="/setting">
            <el-menu-item index="w-setting">
              <el-icon><Setting /></el-icon>
              <span>个人设置</span>
            </el-menu-item>
          </router-link>
        </template>

      </el-menu>
    </div>
  </div>
</template>

<script>
import {
  VideoCamera,
  UserFilled,
  Tickets,
  Trophy,
  Document,
  Setting,
  List,
  DataAnalysis,
} from '@element-plus/icons-vue'
import {
  workerShowFilmMenu,
  workerShowFilmItem,
  workerShowUserMenu,
  workerShowOrderMenu,
} from '@/utils/workerPermissions'

export default {
  components: {
    VideoCamera,
    UserFilled,
    Tickets,
    Trophy,
    Document,
    Setting,
    List,
    DataAnalysis,
  },
  computed: {
    isAdmin() {
      return localStorage.getItem('role') === 'admin'
    },
    isWorker() {
      return localStorage.getItem('role') === 'worker'
    },
    workerFilmMenu() {
      return workerShowFilmMenu()
    },
    workerUserMenu() {
      return workerShowUserMenu()
    },
    workerOrderMenu() {
      return workerShowOrderMenu()
    },
    wFilmList() {
      return workerShowFilmItem('/film/list')
    },
    wFilmArrange() {
      return workerShowFilmItem('/film/arrange')
    },
    wFilmAdd() {
      return workerShowFilmItem('/film/add')
    },
    wFilmPoster() {
      return workerShowFilmItem('/film/poster')
    },
  }
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
  /* 隐藏滚动条，仍可滚轮/触控滚动 */
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.aside-menu-scroll::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
}

.menu {
  font-size: 14px;
  border-right: 0;
  letter-spacing: 1px;
  font-weight: bold;
  font-family: "Helvetica Neue", Helvetica, Arial, "Microsoft Yahei", "Hiragino Sans GB", "Heiti SC", "WenQuanYi Micro Hei", sans-serif;
}
</style>

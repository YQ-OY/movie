<template>
  <div class="aside" :class="{ 'is-collapse': isCollapse }">
    <div class="aside-header">
      <img class="aside-logo" src="../../assets/img/logo.png" alt="" />
      <div class="aside-title" v-show="!isCollapse">后台管理</div>
    </div>
    <div class="aside-menu-scroll">
      <el-menu class="menu" :default-active="$route.path" :collapse="isCollapse" :collapse-transition="false">
        <!-- 原有菜单内容保持不变 -->
        <!-- 系统管理员菜单 -->
        <template v-if="isAdmin">
          <!-- 影视管理 -->
          <el-sub-menu index="/film">
            <template #title>
              <el-icon>
                <VideoCamera />
              </el-icon>
              <span>影视管理</span>
            </template>
            <el-menu-item index="/film/list" @click="navigate('/film/list')">
              <i class="iconfont icon-dianyingliebiao" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>电影列表</template>
            </el-menu-item>
            <el-menu-item index="/film/arrange" @click="navigate('/film/arrange')">
              <i class="iconfont icon-paipian" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>院线排片</template>
            </el-menu-item>
            <el-menu-item index="/film/add" @click="navigate('/film/add')">
              <i class="iconfont icon-dianyingshipin" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>新增电影</template>
            </el-menu-item>
            <el-menu-item index="/film/poster" @click="navigate('/film/poster')">
              <i class="iconfont icon-haibaofenxiang" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>轮播海报</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 用户管理 -->
          <el-sub-menu index="/user">
            <template #title>
              <el-icon>
                <UserFilled />
              </el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/user/list" @click="navigate('/user/list')">
              <i class="iconfont icon-yonghu" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>用户列表</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 订单管理 -->
          <el-sub-menu index="/order">
            <template #title>
              <el-icon>
                <Tickets />
              </el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="/order/list" @click="navigate('/order/list')">
              <i class="iconfont icon-6" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>订单列表</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 数据统计 -->
          <el-sub-menu index="/stats">
            <template #title>
              <el-icon>
                <DataAnalysis />
              </el-icon>
              <span>数据统计</span>
            </template>
            <el-menu-item index="/stats/dashboard" @click="navigate('/stats/dashboard')">
              <i class="iconfont icon-yunyingfenxi" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>运营概览</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 员工管理 -->
          <el-sub-menu index="/worker">
            <template #title>
              <el-icon>
                <Trophy />
              </el-icon>
              <span>员工管理</span>
            </template>
            <el-menu-item index="/worker/list" @click="navigate('/worker/list')">
              <i class="iconfont icon-yuangongguanli" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>员工列表</template>
            </el-menu-item>
            <el-menu-item index="/worker/daily" @click="navigate('/worker/daily')">
              <i class="iconfont icon-meirigongzuo" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>每日工作</template>
            </el-menu-item>
            <el-menu-item index="/worker/add" @click="navigate('/worker/add')">
              <i class="iconfont icon-xinzengyuangong" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>新增员工</template>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/api" @click="navigate('/api')"
            style="padding-left: 20px !important; margin: 0 !important;">
            <el-icon>
              <Document />
            </el-icon>
            <span>Api接口</span>
          </el-menu-item>
        </template>

        <!-- 工作人员菜单（权限跟随 JWT）-->
        <template v-else-if="isWorker">
          <!-- 影视管理 -->
          <el-sub-menu v-if="workerFilmMenu" index="/film">
            <template #title>
              <el-icon>
                <VideoCamera />
              </el-icon>
              <span>影视管理</span>
            </template>
            <el-menu-item v-if="wFilmList" index="/film/list" @click="navigate('/film/list')">
              <i class="iconfont icon-dianyingliebiao" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>电影列表</template>
            </el-menu-item>
            <el-menu-item v-if="wFilmArrange" index="/film/arrange" @click="navigate('/film/arrange')">
              <i class="iconfont icon-paipian" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>院线排片</template>
            </el-menu-item>
            <el-menu-item v-if="wFilmAdd" index="/film/add" @click="navigate('/film/add')">
              <i class="iconfont icon-dianyingshipin" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>新增电影</template>
            </el-menu-item>
            <el-menu-item v-if="wFilmPoster" index="/film/poster" @click="navigate('/film/poster')">
              <i class="iconfont icon-haibaofenxiang" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>轮播海报</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 用户管理 -->
          <el-sub-menu v-if="workerUserMenu" index="/user">
            <template #title>
              <el-icon>
                <UserFilled />
              </el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/user/list" @click="navigate('/user/list')">
              <i class="iconfont icon-yonghu" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>用户列表</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 订单管理 -->
          <el-sub-menu v-if="workerOrderMenu" index="/order">
            <template #title>
              <el-icon>
                <Tickets />
              </el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="/order/list" @click="navigate('/order/list')">
              <i class="iconfont icon-6" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>订单列表</template>
            </el-menu-item>
            <el-menu-item index="/stats/dashboard" @click="navigate('/stats/dashboard')">
              <i class="iconfont icon-yunyingfenxi" style="margin-right: 8px; font-size: 18px;"></i>
              <template #title>运营概览</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 每日工作 -->
          <el-menu-item index="/worker/daily" @click="navigate('/worker/daily')"
            style="padding-left: 20px !important; margin: 0 !important;">
            <el-icon>
              <List />
            </el-icon>
            <span>每日工作</span>
          </el-menu-item>
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
  props: {
    isCollapse: {
      type: Boolean,
      default: false
    }
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

.menu .el-menu-item,
.menu .el-sub-menu .el-sub-menu__title {
  height: 44px;
  line-height: 44px;
  margin: 4px 12px;
  border-radius: 10px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

.menu .el-menu-item .el-icon,
.menu .el-sub-menu__title .el-icon {
  margin-right: 12px;
  font-size: 18px;
  transition: transform 0.2s ease;
}

.menu .el-menu-item:hover,
.menu .el-sub-menu__title:hover {
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

.menu .el-sub-menu .el-sub-menu__title .el-icon.el-sub-menu__icon-arrow {
  transition: transform 0.25s ease;
  font-size: 14px;
  right: 16px;
  color: #94a3b8;
}

.menu .el-sub-menu.is-opened .el-sub-menu__title .el-icon.el-sub-menu__icon-arrow {
  transform: rotate(180deg);
}

.menu .el-menu--inline .el-menu-item {
  padding-left: 52px !important;
  height: 40px;
  line-height: 40px;
  margin: 2px 12px 2px 12px;
  font-size: 13px;
  font-weight: normal;
  border-radius: 8px;
}

.menu .el-menu--inline .el-menu-item:hover {
  background-color: #f8fafc !important;
  transform: translateX(2px);
}

.menu .el-menu--inline .el-menu-item.is-active {
  background: linear-gradient(135deg, #ecf5ff 0%, #e8f0fe 100%);
  border-left: 3px solid #409eff;
  border-radius: 8px 4px 4px 8px;
  font-weight: 500;
}

.menu .el-sub-menu .el-menu--inline {
  background-color: transparent;
}

.menu .el-menu-item:active,
.menu .el-sub-menu__title:active {
  transform: scale(0.98);
}
</style>
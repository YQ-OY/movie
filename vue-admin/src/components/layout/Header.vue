<template>
  <div class="header">
    <div class="header-left">
      <!-- 收缩/展开侧边栏按钮 -->
      <div class="header-icon" @click="toggleCollapse">
        <el-icon :size="20">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
      <!-- 刷新按钮 -->
      <div class="header-icon" @click="refreshPage">
        <el-icon :size="20">
          <Refresh />
        </el-icon>
      </div>
      <!-- 面包屑标题 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }"></el-breadcrumb-item>
        <el-breadcrumb-item v-for="(item, idx) in breadcrumbs" :key="idx">
          {{ item }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="header-right">
      <!-- 通知 -->
      <div class="header-icon">
        <el-dropdown>
          <el-badge value="new" class="item">
            <el-icon>
              <Bell />
            </el-icon>
          </el-badge>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="item in list" :key="item">{{ item.content }}</el-dropdown-item>
              <el-dropdown-item v-if="!list || list.length === 0">今日工作还没有上传</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <!-- 提示信息 -->
      <div class="header-icon">
        <el-tooltip content="厅有独钟 - 管理系统" placement="bottom">
          <el-icon>
            <Warning />
          </el-icon>
        </el-tooltip>
      </div>
      <!-- 用户头像/下拉菜单 -->
      <div class="header-avatar">
        <el-dropdown>
          <el-button link>
            <div class="header-name">Admin</div>
            <el-avatar style="margin-top: 5px" :size="29"
              src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png">
            </el-avatar>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>
                <el-link :underline="false" href="/setting">
                  <el-icon>
                    <Setting />
                  </el-icon>个人设置
                </el-link>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-button link @click="handleLogout">
                  <el-icon>
                    <SwitchButton />
                  </el-icon>退出登录
                </el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
import { ListDailyWork } from "@/api/worker";
import { Setting, SwitchButton, Bell, Warning, Refresh, Fold, Expand } from '@element-plus/icons-vue'
import { clearWorkerPermissions } from '@/utils/workerPermissions'

export default {
  name: "Header",
  components: { Setting, SwitchButton, Bell, Warning, Refresh, Fold, Expand },
  props: {
    isCollapse: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      list: []
    }
  },
  computed: {
    // 生成面包屑数据（优先使用路由 meta.title，否则根据路径映射）
    breadcrumbs() {
      const matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      if (matched.length) {
        return matched.map(item => item.meta.title)
      }
      // 简单的路径映射后备
      const path = this.$route.path
      const segments = path.split('/').filter(seg => seg && seg !== '')
      if (segments.length === 0) return ['仪表盘']
      const map = {
        film: '影视管理',
        user: '用户管理',
        order: '订单管理',
        stats: '数据统计',
        worker: '员工管理',
        api: 'Api接口',
        setting: '个人设置'
      }
      const first = map[segments[0]] || segments[0]
      const second = segments[1] ? (segments[1] === 'dashboard' ? '运营概览' : segments[1]) : null
      return second ? [first, second] : [first]
    }
  },
  mounted() {
    ListDailyWork().then(res => {
      setTimeout(() => {
        const data = res && res.data
        this.list = Array.isArray(data) ? data : []
      }, 700)
    }).catch(() => {
      this.list = []
    })
  },
  methods: {
    toggleCollapse() {
      this.$emit('toggle-collapse')  // 通知父组件切换折叠状态
    },
    refreshPage() {
      window.location.reload()
    },
    handleLogout() {
      localStorage.removeItem("token")
      localStorage.removeItem("role")
      localStorage.removeItem("wid")
      clearWorkerPermissions()
      this.$router.push("/login")
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 24px;
  background-color: rgb(250, 251, 252);
  border-bottom: 1px solid #eaeef2;
  box-sizing: border-box;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.header-icon:hover {
  background-color: #f1f5f9;
}

.header-avatar {
  margin-left: 8px;
}

.header-name {
  float: right;
  padding-top: 12px;
  font-size: 15px;
  padding-right: 10px;
  padding-left: 9px;
}

.el-button {
  padding-top: 8px;
  color: #000000;
  letter-spacing: 2px;
}

.breadcrumb {
  margin-left: 4px;
  font-size: 14px;
  line-height: 32px;
}

:deep(.el-breadcrumb__inner) {
  color: #5a6874;
}

:deep(.el-breadcrumb__inner a) {
  color: #409eff;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #1f2f3d;
  font-weight: 500;
}
</style>
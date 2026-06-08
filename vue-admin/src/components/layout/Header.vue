<template>
  <div class="header">
    <div class="header-left">
      <button class="header-icon icon-button" @click="toggleCollapse" type="button" aria-label="切换侧边栏">
        <el-icon :size="20">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </button>
      <button class="header-icon icon-button" @click="refreshPage" type="button" aria-label="刷新页面">
        <el-icon :size="20">
          <Refresh />
        </el-icon>
      </button>
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-for="(item, idx) in breadcrumbs" :key="idx">
          {{ item }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="header-right">
      <el-dropdown trigger="click" placement="bottom-end">
        <button class="header-icon icon-button" type="button" aria-label="通知">
          <span class="iconfont icon-tongzhi header-iconfont"></span>
          <el-badge is-dot class="badge-dot" />
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="item in list" :key="item.id || item.content">{{ item.content }}</el-dropdown-item>
            <el-dropdown-item v-if="!list || list.length === 0">今日工作还没有上传</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-tooltip content="厅有独钟 - 管理系统" placement="bottom">
        <button class="header-icon icon-button" type="button" aria-label="系统提醒">
          <span class="iconfont icon-zhuyi header-iconfont"></span>
        </button>
      </el-tooltip>
      <button class="avatar-trigger" type="button" aria-label="打开个人信息" @click="drawerVisible = true">
        <el-avatar class="user-avatar" :size="38" :src="avatarUrl" />
      </button>
    </div>
  </div>

  <el-drawer v-model="drawerVisible" direction="rtl" size="530px" :with-header="false" class="profile-drawer"
    :class-name="'my-drawer'">
    <div class="drawer-content">
      <div class="drawer-header">
        <el-upload class="avatar-uploader" :show-file-list="false" :before-upload="beforeAvatarUpload"
          :on-success="handleAvatarSuccess" :action="uploadAction" :headers="uploadHeaders">
          <el-avatar class="drawer-avatar" :size="72" :src="adminInfo.avatar || defaultAvatar" />
          <div class="avatar-upload-tip">点击更换头像</div>
        </el-upload>
        <div class="drawer-user">
          <div class="drawer-name">{{ adminInfo.username || userName }}</div>
          <div class="drawer-role">{{ userRoleText }}</div>
        </div>
      </div>

      <el-form :model="adminInfo" label-width="80px" class="drawer-form">
        <el-form-item>
          <template #label>
            <span><i class="iconfont icon-nicheng"></i> 昵称</span>
          </template>
          <el-input v-model="adminInfo.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item>
          <template #label>
            <span><i class="iconfont icon-shoujihao"></i> 手机号</span>
          </template>
          <el-input v-model="adminInfo.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <template #label>
            <span><i class="iconfont icon-youxiang"></i> 邮箱</span>
          </template>
          <el-input v-model="adminInfo.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item>
          <template #label>
            <span><i class="iconfont icon-shengri"></i> 生日</span>
          </template>
          <el-date-picker v-model="adminInfo.birthday" type="date" value-format="YYYY-MM-DD" placeholder="选择生日"
            style="width: 100%" />
        </el-form-item>
        <el-form-item>
          <template #label>
            <span><i class="iconfont icon-xingbie"></i> 性别</span>
          </template>
          <el-select v-model="adminInfo.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="保密" value="保密" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <template #label>
            <span><i class="iconfont icon-gerenjianjiexiao"></i> 个人简介</span>
          </template>
          <el-input v-model="adminInfo.info" type="textarea" :rows="3" placeholder="介绍一下自己" />
        </el-form-item>
      </el-form>

      <div class="drawer-actions">
        <el-button type="primary" class="drawer-save-button" @click="saveProfile">保存资料</el-button>
        <el-button class="drawer-logout-button" @click="handleLogout">退出登录</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import { ListDailyWork } from "@/api/worker";
import { getCurrentAdmin, updateAdmin } from "@/api/admin"; // 新增 API
import { Fold, Expand } from '@element-plus/icons-vue'
import { clearWorkerPermissions } from '@/utils/workerPermissions'
import config from "@/config";

export default {
  name: "Header",
  components: { Fold, Expand },
  props: { isCollapse: Boolean },
  data() {
    return {
      list: [],
      drawerVisible: false,
      adminInfo: {
        id: '',
        username: '',
        nickname: '',
        phone: '',
        email: '',
        birthday: '',
        gender: '',
        info: '',
        avatar: ''
      },
      defaultAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }
  },
  computed: {
    breadcrumbs() {
      const matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      if (matched.length) return matched.map(item => item.meta.title)
      const segments = this.$route.path.split('/').filter(seg => seg && seg !== '')
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
      const second = segments[1] ? this.getPageTitle(segments[0], segments[1]) : null
      return second ? [first, second] : [first]
    },
    userName() {
      return this.adminInfo.username || localStorage.getItem('username') || 'Super'
    },
    userEmail() {
      return this.adminInfo.email || localStorage.getItem('email') || ''
    },
    avatarUrl() {
      return this.adminInfo.avatar || localStorage.getItem('avatar') || this.defaultAvatar
    },
    userRoleText() {
      const role = localStorage.getItem('role')
      return role === 'worker' ? '员工' : '管理员'
    },
    uploadAction() {
      return config.API_URL + '/upload'
    },
    uploadHeaders() {
      const token = localStorage.getItem('token')
      return token ? { Authorization: token } : {}
    },
  },
  watch: {
    drawerVisible(val) {
      if (val) {
        this.fetchAdminInfo()
      }
    }
  },
  mounted() {
    ListDailyWork().then(res => {
      this.list = Array.isArray(res?.data) ? res.data : []
    }).catch(() => { this.list = [] })
  },
  methods: {
    getPageTitle(parent, child) {
      const titleMap = {
        film: { list: '影片列表', arrange: '影片排片', add: '新增影片', poster: '海报管理' },
        user: { list: '用户列表' },
        order: { list: '订单列表', exception: '异常订单' },
        stats: { dashboard: '运营概览' },
        worker: { list: '员工列表', daily: '每日工作', add: '新增员工' }
      }
      return (titleMap[parent] && titleMap[parent][child]) || child
    },
    toggleCollapse() {
      this.$emit('toggle-collapse')
    },
    refreshPage() {
      window.location.reload()
    },
    async fetchAdminInfo() {
      try {
        const res = await getCurrentAdmin()
        if (res.success) {
          this.adminInfo = { ...res.data }
          // 同步 localStorage
          localStorage.setItem('avatar', this.adminInfo.avatar || '')
          localStorage.setItem('name', this.adminInfo.nickname || this.adminInfo.username)
          localStorage.setItem('email', this.adminInfo.email || '')
        }
      } catch (error) {
        this.$message.error('获取个人信息失败')
      }
    },
    async saveProfile() {
      try {
        const res = await updateAdmin(this.adminInfo)
        if (res.success) {
          this.$message.success('资料已保存')
          // 更新 localStorage 中的信息
          localStorage.setItem('avatar', this.adminInfo.avatar || '')
          localStorage.setItem('name', this.adminInfo.nickname || this.adminInfo.username)
          localStorage.setItem('email', this.adminInfo.email || '')
          this.drawerVisible = false
        } else {
          this.$message.error(res.msg || '保存失败')
        }
      } catch (error) {
        this.$message.error('保存失败')
      }
    },
    handleLogout() {
      localStorage.removeItem("token")
      localStorage.removeItem("role")
      localStorage.removeItem("wid")
      clearWorkerPermissions()
      this.$router.push("/login")
    },
    beforeAvatarUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].includes(file.type)
      if (!isImage) {
        this.$message.error('只能上传 JPG/PNG 格式')
        return false
      }
      const isLt50M = file.size / 1024 / 1024 < 50
      if (!isLt50M) {
        this.$message.error('图片大小不能超过 50MB')
        return false
      }
      return true
    },
    handleAvatarSuccess(res) {
      const url = typeof res === 'string' ? res : (res?.data || '')
      if (url) {
        this.adminInfo.avatar = url
        localStorage.setItem('avatar', url)
      }
    }

  }
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 22px;
  background: linear-gradient(180deg, #fbfcfe 0%, #f7f9fc 100%);
  border-bottom: 1px solid #e8edf3;
  box-sizing: border-box;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
}

.header-left {
  gap: 10px;
}

.header-right {
  gap: 12px;
}

.header-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border: 0;
  background: transparent;
  color: #667085;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.header-icon:hover {
  background: #eef4ff;
  color: #3b82f6;
  transform: translateY(-1px);
}

.icon-button {
  position: relative;
}

.header-iconfont {
  font-size: 20px;
  line-height: 1;
}

.badge-dot {
  position: absolute;
  top: 7px;
  right: 7px;
}

.breadcrumb {
  margin-left: 8px;
  font-size: 14px;
  line-height: 32px;
}

:deep(.el-breadcrumb__inner) {
  color: #667085;
  font-weight: 400;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #1d2939;
  font-weight: 600;
}

.avatar-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.12);
}

:deep(.profile-drawer.el-drawer) {
  background: transparent;
  box-shadow: none;
}

:deep(.profile-drawer .el-drawer__header) {
  display: none;
}

:deep(.profile-drawer .el-drawer__body) {
  padding: 0;
  background: transparent;
}

.drawer-content {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 24px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.5) 0%, rgba(248, 250, 252, 0.5) 100%),
    url('@/assets/img/background.png') center/cover no-repeat;
  overflow: hidden;
}

.drawer-content::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.14), transparent 38%),
    radial-gradient(circle at bottom left, rgba(16, 185, 129, 0.12), transparent 34%);
  pointer-events: none;
}

.drawer-content>* {
  position: relative;
  z-index: 1;
}

.drawer-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px;
  margin-bottom: 18px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(12px);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
}

.drawer-name {
  font-size: 22px;
  font-weight: 700;
  color: #101828;
}

.drawer-role {
  margin-top: 6px;
  color: #667085;
  font-size: 13px;
}

.drawer-info {
  padding: 20px 0;
  display: grid;
  gap: 14px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #eef2f7;
}

.info-label {
  color: #667085;
  font-size: 14px;
}

.info-value {
  color: #101828;
  font-weight: 500;
  word-break: break-all;
  text-align: right;
  margin-left: 14px;
}

.drawer-actions {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.drawer-save-button,
.drawer-logout-button {
  width: 100%;
  height: 44px;
}

.drawer-logout-button {
  color: #d92d20;
}

:deep(.el-button + .el-button) {
  margin-left: 0;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
}

.avatar-upload-tip {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 12px;
  text-align: center;
  border-radius: 0 0 36px 36px;
  padding: 4px 0;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-uploader:hover .avatar-upload-tip {
  opacity: 1;
}

.drawer-form {
  margin: 20px 0;
  max-height: calc(100vh - 360px);
  overflow-y: auto;
  padding-right: 8px;

  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(12px);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
}

.drawer-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.drawer-actions {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.drawer-save-button,
.drawer-logout-button {
  width: 100%;
  height: 44px;
}

.drawer-save-button {
  box-shadow: 0 10px 24px rgba(59, 130, 246, 0.25);
}

.drawer-logout-button {
  color: #d92d20;
  border-color: rgba(217, 45, 32, 0.2);
  background: rgba(255, 255, 255, 0.85);
}

.drawer-logout-button:hover {
  color: #d92d20;
  background: rgba(217, 45, 32, 0.06);
  border-color: rgba(217, 45, 32, 0.35);
}

:deep(.el-button + .el-button) {
  margin-left: 0;
}
</style>

<style>
/* 全局样式，用于覆盖抽屉的内边距 */
.profile-drawer {
  --el-drawer-padding-primary: 0px !important;
}

/* 或者直接针对 body 设置（备选） */
.profile-drawer .el-drawer__body {
  padding: 0 !important;
}
</style>
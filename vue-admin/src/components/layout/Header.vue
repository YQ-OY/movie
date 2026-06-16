<template>
  <div>
    <div class="header">
      <!-- 左侧 -->
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
          <el-breadcrumb-item v-for="(item, idx) in breadcrumbs" :key="idx">
            {{ item }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <!-- 右侧 -->
      <div class="header-right">
        <el-dropdown trigger="click" placement="bottom-end">
          <button class="header-icon icon-button" type="button" aria-label="通知">
            <el-icon :size="20">
              <Bell />
            </el-icon>
            <el-badge is-dot class="badge-dot" />
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="item in list" :key="item.id || item.content">
                {{ item.content }}
              </el-dropdown-item>
              <el-dropdown-item v-if="!list || list.length === 0">
                今日工作还没有上传
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-tooltip content="厅有独钟 - 管理系统" placement="bottom">
          <button class="header-icon icon-button" type="button" aria-label="系统提醒">
            <el-icon :size="20">
              <InfoFilled />
            </el-icon>
          </button>
        </el-tooltip>
        <button class="avatar-trigger" type="button" aria-label="打开个人信息" @click="drawerVisible = true">
          <el-avatar class="user-avatar" :size="38" :src="avatarUrl" />
        </button>
      </div>
    </div>

    <el-drawer v-model="drawerVisible" direction="rtl" size="530px" :with-header="false" class="profile-drawer"
      :close-on-click-modal="true">
      <div class="drawer-content">
        <div class="drawer-main">
          <div class="drawer-header">
            <el-upload v-if="!isWorkerLogin || settingSubTab === 'profile'" class="avatar-uploader"
              :show-file-list="false" :before-upload="beforeAvatarUpload" :on-success="handleAvatarSuccess"
              :action="uploadAction" :headers="uploadHeaders">
              <el-avatar class="drawer-avatar" :size="72" :src="profileAvatar" />
              <div class="avatar-upload-tip">点击更换头像</div>
            </el-upload>
            <el-avatar v-else class="drawer-avatar" :size="72" :src="profileAvatar" />
            <div class="drawer-user">
              <div class="drawer-name">{{ profileName }}</div>
              <div class="drawer-role">{{ userRoleText }}</div>
            </div>
          </div>

          <div v-if="isWorkerLogin" class="drawer-panel">
            <div class="setting-sub-tabs">
              <button type="button" class="setting-sub-tab"
                :class="{ 'setting-sub-tab--active': settingSubTab === 'profile' }"
                @click="switchSettingSubTab('profile')">
                个人资料
              </button>
              <button type="button" class="setting-sub-tab"
                :class="{ 'setting-sub-tab--active': settingSubTab === 'account' }"
                @click="switchSettingSubTab('account')">
                账号设置
              </button>
            </div>

            <template v-if="settingSubTab === 'profile'">
              <el-form :model="workerInfo" label-width="48px" class="drawer-form drawer-form--panel">
                <el-form-item label="用户名">
                  <el-input v-model="workerInfo.username" disabled placeholder="用户名" />
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="workerInfo.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="workerInfo.phone" placeholder="请输入手机号" maxlength="11" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-select v-model="workerInfo.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                  </el-select>
                </el-form-item>
                <el-form-item label="部门">
                  <el-select v-model="workerInfo.department" placeholder="请选择部门" style="width: 100%">
                    <el-option label="客服部" value="客服部" />
                    <el-option label="运营部" value="运营部" />
                    <el-option label="宣传部" value="宣传部" />
                    <el-option label="策划部" value="策划部" />
                    <el-option label="人事部" value="人事部" />
                    <el-option label="监管部" value="监管部" />
                  </el-select>
                </el-form-item>
              </el-form>
              <div class="drawer-actions drawer-actions--panel">
                <el-button type="primary" class="drawer-save-button" @click="saveProfile">
                  保存资料
                </el-button>
                <el-button class="drawer-logout-button" @click="handleLogout">
                  退出登录
                </el-button>
              </div>
            </template>

            <template v-else>
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="48px"
                class="drawer-form drawer-form--panel password-form" autocomplete="off">
                <el-form-item label="用户名">
                  <el-input v-model="workerInfo.username" disabled autocomplete="off" />
                </el-form-item>
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password
                    autocomplete="off" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password
                    autocomplete="new-password" />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password
                    autocomplete="new-password" />
                </el-form-item>
              </el-form>
              <div class="drawer-actions drawer-actions--panel">
                <el-button type="primary" class="drawer-save-button" @click="changePassword">
                  修改密码
                </el-button>
              </div>
            </template>
          </div>

          <template v-else>
            <el-form :model="adminInfo" label-width="80px" class="drawer-form">
              <el-form-item label="昵称">
                <el-input v-model="adminInfo.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="adminInfo.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="adminInfo.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="生日">
                <el-date-picker v-model="adminInfo.birthday" type="date" value-format="YYYY-MM-DD" placeholder="选择生日"
                  style="width: 100%" />
              </el-form-item>
              <el-form-item label="性别">
                <el-select v-model="adminInfo.gender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                  <el-option label="保密" value="保密" />
                </el-select>
              </el-form-item>
              <el-form-item label="个人简介">
                <el-input v-model="adminInfo.info" type="textarea" :rows="3" placeholder="介绍一下自己" maxlength="200"
                  show-word-limit />
              </el-form-item>
            </el-form>
          </template>
        </div>

        <div v-if="!isWorkerLogin" class="drawer-actions">
          <el-button type="primary" class="drawer-save-button" @click="saveProfile">
            保存资料
          </el-button>
          <el-button class="drawer-logout-button" @click="handleLogout">
            退出登录
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { ListDailyWork, FindWorkerById, UpdateWorker } from "@/api/worker";
import { getCurrentAdmin, updateAdmin } from "@/api/admin";
import { Login } from "@/api/user";
import { Fold, Expand, Refresh, Bell, InfoFilled, User, Phone, Message, Location, Calendar, EditPen } from '@element-plus/icons-vue'
import { clearWorkerPermissions } from '@/utils/workerPermissions'
import config from "@/config";

export default {
  name: "Header",
  components: { Fold, Expand, Refresh, Bell, InfoFilled, User, Phone, Message, Location, Calendar, EditPen },
  props: { isCollapse: Boolean },
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的新密码不一致'))
      } else {
        callback()
      }
    }
    return {
      list: [],
      drawerVisible: false,
      settingSubTab: 'profile',
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
      workerInfo: {
        id: '',
        username: '',
        nickname: '',
        phone: '',
        gender: '',
        avatar: '',
        department: '',
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' },
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' },
        ],
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
      return this.profileAvatar
    },
    userRoleText() {
      const role = localStorage.getItem('role')
      return role === 'worker' ? '员工' : '管理员'
    },
    isWorkerLogin() {
      return localStorage.getItem('role') === 'worker'
    },
    profileAvatar() {
      if (this.isWorkerLogin) {
        return this.workerInfo.avatar || this.defaultAvatar
      }
      return this.adminInfo.avatar || localStorage.getItem('avatar') || this.defaultAvatar
    },
    profileName() {
      if (this.isWorkerLogin) {
        return this.workerInfo.nickname || this.workerInfo.username || '员工'
      }
      return this.adminInfo.nickname || this.adminInfo.username || this.userName
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
        this.settingSubTab = 'profile'
        this.resetPasswordForm()
        this.fetchProfileInfo()
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
    switchSettingSubTab(tab) {
      if (this.settingSubTab === tab) return
      this.settingSubTab = tab
      if (tab === 'account') {
        this.resetPasswordForm()
      }
    },
    resetPasswordForm() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
      }
      this.$refs.passwordFormRef?.resetFields()
    },
    async fetchProfileInfo() {
      try {
        if (this.isWorkerLogin) {
          const wid = localStorage.getItem('wid')
          if (!wid) return
          const res = await FindWorkerById(wid)
          if (res.data) {
            const { password, ...profile } = res.data
            this.workerInfo = { ...this.workerInfo, ...profile }
            localStorage.setItem('avatar', this.workerInfo.avatar || '')
            localStorage.setItem('name', this.workerInfo.nickname || this.workerInfo.username)
          }
          return
        }
        const res = await getCurrentAdmin()
        if (res.success) {
          this.adminInfo = { ...res.data }
          localStorage.setItem('avatar', this.adminInfo.avatar || '')
          localStorage.setItem('name', this.adminInfo.nickname || this.adminInfo.username)
          localStorage.setItem('email', this.adminInfo.email || '')
        }
      } catch {
        // 全局 request 拦截器已提示
      }
    },
    async saveProfile() {
      try {
        if (this.isWorkerLogin) {
          const { password, ...payload } = this.workerInfo
          const res = await UpdateWorker(payload, { silent: true })
          if (res.success) {
            this.$message.success('资料已保存')
            localStorage.setItem('avatar', this.workerInfo.avatar || '')
            localStorage.setItem('name', this.workerInfo.nickname || this.workerInfo.username)
            this.drawerVisible = false
          }
          return
        }
        const res = await updateAdmin(this.adminInfo, { silent: true })
        if (res.success) {
          this.$message.success('资料已保存')
          localStorage.setItem('avatar', this.adminInfo.avatar || '')
          localStorage.setItem('name', this.adminInfo.nickname || this.adminInfo.username)
          localStorage.setItem('email', this.adminInfo.email || '')
          this.drawerVisible = false
        }
      } catch {
        // 网络异常由请求拦截器统一提示
      }
    },
    changePassword() {
      this.$refs.passwordFormRef.validate(valid => {
        if (!valid) return
        Login('worker', {
          username: this.workerInfo.username,
          password: this.passwordForm.oldPassword,
          remember: false,
        }, { silent: true }).then(res => {
          if (!res?.success) {
            this.$message.error(res?.msg || '当前密码不正确')
            return
          }
          UpdateWorker({
            id: this.workerInfo.id,
            password: this.passwordForm.newPassword,
          }, { silent: true }).then(res => {
            if (res.success) {
              this.$message.success('密码修改成功')
              this.resetPasswordForm()
            } else {
              this.$message.error(res?.msg || '修改失败')
            }
          })
        })
      })
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
      if (!url) return
      if (this.isWorkerLogin) {
        this.workerInfo.avatar = url
      } else {
        this.adminInfo.avatar = url
      }
      localStorage.setItem('avatar', url)
    }
  }
}
</script>

<style scoped>
/* 头部导航样式 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  box-sizing: border-box;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
}

.header-left {
  gap: 12px;
}

.header-right {
  gap: 16px;
}

.header-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: 0;
  background: transparent;
  color: #64748b;
  cursor: pointer;
  border-radius: 10px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.header-icon:hover {
  background: #f1f5f9;
  color: #409eff;
  transform: translateY(-1px);
}

.icon-button {
  position: relative;
}

.badge-dot {
  position: absolute;
  top: 8px;
  right: 8px;
}

.breadcrumb {
  margin-left: 12px;
  font-size: 14px;
  color: #64748b;
}

.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #334155;
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
  border: 2px solid #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.25s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

/* 抽屉样式 */
:deep(.profile-drawer.el-drawer) {
  background: transparent;
  box-shadow: none;
}

:deep(.profile-drawer .el-drawer__header) {
  display: none;
}

:deep(.profile-drawer .el-drawer__body) {
  padding: 0 !important;
  background: transparent;
}

.drawer-content {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 24px;
  background:
    linear-gradient(180deg, rgba(248, 251, 255, 0.9) 0%, rgba(238, 242, 247, 0.95) 100%),
    url('@/assets/img/background.png') center/cover no-repeat;
  overflow: hidden;
}

.drawer-content::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top right, rgba(64, 158, 255, 0.15), transparent 40%),
    radial-gradient(circle at 20% 80%, rgba(99, 102, 241, 0.1), transparent 35%),
    radial-gradient(circle at bottom left, rgba(16, 185, 129, 0.08), transparent 40%);
  pointer-events: none;
}

.drawer-content>* {
  position: relative;
  z-index: 1;
}

.drawer-main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 20px;
  min-height: 0;
  flex: 1;
}

.drawer-panel {
  display: flex;
  flex-direction: column;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.setting-sub-tabs {
  display: flex;
  gap: 8px;
  padding: 16px 20px 0;
  flex-shrink: 0;
}

.setting-sub-tab {
  flex: 1;
  height: 38px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.9);
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
}

.setting-sub-tab:hover:not(.setting-sub-tab--active) {
  color: #334155;
  border-color: #cbd5e1;
  background: #ffffff;
}

.setting-sub-tab--active {
  color: #ffffff;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25);
}

.drawer-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.drawer-avatar {
  border: 3px solid #ffffff;
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.2);
  transition: all 0.3s ease;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
}

.avatar-uploader:hover .drawer-avatar {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.25);
}

.avatar-upload-tip {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 6px 0;
  border-radius: 0 0 36px 36px;
  background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.7) 100%);
  color: #ffffff;
  font-size: 12px;
  text-align: center;
  opacity: 0;
  transform: translateY(4px);
  transition: all 0.25s ease;
}

.avatar-uploader:hover .avatar-upload-tip {
  opacity: 1;
  transform: translateY(0);
}

.drawer-user {
  min-width: 0;
}

.drawer-name {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
  word-break: break-word;
}

.drawer-role {
  display: inline-flex;
  align-items: center;
  margin-top: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
  font-size: 13px;
  font-weight: 600;
}

.drawer-form {
  margin: 0;
  padding: 20px 24px 10px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  max-height: calc(100vh - 360px);
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}

.drawer-form::-webkit-scrollbar {
  width: 6px;
}

.drawer-form::-webkit-scrollbar-track {
  background: transparent;
}

.drawer-form::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.drawer-form--panel {
  border: 0;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
  max-height: calc(100vh - 420px);
}

.drawer-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.drawer-form :deep(.el-form-item__label) {
  color: #334155;
  font-weight: 500;
}

/* 统一输入框样式 */
.drawer-form :deep(.el-input__wrapper),
.drawer-form :deep(.el-select__wrapper),
.drawer-form :deep(.el-textarea__inner) {
  border-radius: 10px;
  box-shadow: none;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.drawer-form :deep(.el-input__wrapper:hover),
.drawer-form :deep(.el-select__wrapper:hover),
.drawer-form :deep(.el-textarea__inner:hover) {
  border-color: #93c5fd;
}

.drawer-form :deep(.el-input__wrapper.is-focus),
.drawer-form :deep(.el-select__wrapper.is-focus),
.drawer-form :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.drawer-form :deep(.el-textarea__inner) {
  min-height: 92px !important;
  resize: none;
}

.drawer-actions {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.drawer-actions--panel {
  margin-top: 0;
  padding: 0 24px 20px;
}

/* 统一按钮样式 */
.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 40px;
  padding: 0 18px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  box-shadow: none;
}

.btn-primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #337ecc 0%, #409eff 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
}

.drawer-save-button,
.drawer-logout-button {
  width: 100%;
  height: 46px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 15px;
}

.drawer-save-button {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #ffffff;
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.25);
}

.drawer-save-button:hover {
  background: linear-gradient(135deg, #337ecc 0%, #409eff 100%);
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
}

.drawer-logout-button {
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.2);
  background: rgba(255, 255, 255, 0.9);
}

.drawer-logout-button:hover {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.06);
  border-color: rgba(239, 68, 68, 0.3);
  transform: translateY(-1px);
}

:deep(.el-button + .el-button) {
  margin-left: 0;
}
</style>

<style>
.profile-drawer {
  --el-drawer-padding-primary: 0px !important;
}
</style>
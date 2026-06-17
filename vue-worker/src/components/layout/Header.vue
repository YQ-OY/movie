<template>
  <div class="header">
    <div class="header-left"></div>
    <div class="header-right">
      <!-- 通知图标 -->
      <el-dropdown trigger="click" placement="bottom-end">
        <button class="header-icon icon-button" type="button" aria-label="通知">
          <span class="iconfont icon-tongzhi header-iconfont"></span>
          <el-badge is-dot class="badge-dot" />
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="item in list" :key="item.id || item.content">
              {{ item.content }}
            </el-dropdown-item>
            <el-dropdown-item v-if="!list || list.length === 0">暂无通知</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <!-- 系统提示图标 -->
      <el-tooltip content="厅有独钟 - 客服系统" placement="bottom">
        <button class="header-icon icon-button" type="button" aria-label="系统提醒">
          <span class="iconfont icon-zhuyi header-iconfont"></span>
        </button>
      </el-tooltip>

      <!-- 员工头像 + 下拉菜单 -->
      <button class="avatar-trigger" type="button" aria-label="打开个人信息" @click="drawerVisible = true">
        <div class="user-info">
          <span class="user-name">{{ worker.nickname || '客服' }}</span>
          <el-avatar class="user-avatar" :size="38" :src="worker.avatar" />
        </div>
      </button>
    </div>

    <!-- 个人设置抽屉 -->
    <el-drawer v-model="drawerVisible" direction="rtl" size="530px" :with-header="false" class="profile-drawer">
      <div class="drawer-content">
        <div class="drawer-main">
          <div class="drawer-header">
            <el-upload v-if="settingSubTab === 'profile'" class="avatar-uploader" :show-file-list="false"
              :before-upload="beforeAvatarUpload" :on-success="handleAvatarSuccess" :action="uploadAction"
              :headers="uploadHeaders">
              <el-avatar class="drawer-avatar" :size="72" :src="worker.avatar || defaultAvatar" />
              <div class="avatar-upload-tip">点击更换头像</div>
            </el-upload>
            <el-avatar v-else class="drawer-avatar" :size="72" :src="worker.avatar || defaultAvatar" />
            <div class="drawer-user">
              <div class="drawer-name">{{ worker.username || '客服' }}</div>
              <div class="drawer-role">客服人员</div>
            </div>
          </div>

          <div class="drawer-panel">
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
              <el-form :model="worker" label-width="48px" class="drawer-form drawer-form--panel">
                <el-form-item>
                  <template #label>
                    <i class="iconfont icon-yonghu" />
                  </template>
                  <el-input v-model="worker.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item>
                  <template #label>
                    <i class="iconfont icon-nicheng drawer-icon-nickname" />
                  </template>
                  <el-input v-model="worker.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item>
                  <template #label>
                    <i class="iconfont icon-shoujihao" />
                  </template>
                  <el-input v-model="worker.phone" placeholder="请输入手机号" maxlength="11" show-word-limit />
                </el-form-item>
                <el-form-item>
                  <template #label>
                    <i class="iconfont icon-xingbie" />
                  </template>
                  <el-select v-model="worker.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                  </el-select>
                </el-form-item>
              </el-form>
              <div class="drawer-actions drawer-actions--panel">
                <el-button type="primary" class="drawer-save-button" @click="saveProfile">保存资料</el-button>
                <el-button class="drawer-logout-button" @click="handleLogout">退出登录</el-button>
              </div>
            </template>

            <template v-else>
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="48px"
                class="drawer-form drawer-form--panel password-form" autocomplete="off">
                <el-form-item>
                  <template #label>
                    <i class="iconfont icon-yonghu" />
                  </template>
                  <el-input v-model="worker.username" disabled autocomplete="off" />
                </el-form-item>
                <el-form-item prop="oldPassword">
                  <template #label>
                    <i class="iconfont icon-suo" />
                  </template>
                  <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password
                    autocomplete="off" name="profile-verify-pwd" :readonly="oldPasswordReadonly"
                    @focus="oldPasswordReadonly = false" />
                </el-form-item>
                <el-form-item prop="newPassword">
                  <template #label>
                    <i class="iconfont icon-suo" />
                  </template>
                  <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password
                    autocomplete="new-password" />
                </el-form-item>
                <el-form-item prop="confirmPassword">
                  <template #label>
                    <i class="iconfont icon-suo" />
                  </template>
                  <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password
                    autocomplete="new-password" />
                </el-form-item>
              </el-form>
              <div class="drawer-actions drawer-actions--panel">
                <el-button type="primary" class="drawer-save-button" @click="changePassword">修改密码</el-button>
              </div>
            </template>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { FindWorkerById, UpdateWorker, ListDailyWork } from "@/api/worker";
import { Login } from "@/api/user";
import config from "@/config";

export default {
  name: "Header",
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的新密码不一致'))
      } else {
        callback()
      }
    }
    return {
      worker: {
        id: '',
        username: '',
        nickname: '',
        phone: '',
        gender: '',
        avatar: '',
      },
      defaultAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      list: [],
      drawerVisible: false,
      settingSubTab: 'profile',
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
      },
      oldPasswordReadonly: true,
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
      uploadAction: config.API_URL + '/upload',
      uploadHeaders: { Authorization: localStorage.getItem('token') },
    }
  },
  watch: {
    drawerVisible(val) {
      if (val) {
        this.settingSubTab = 'profile'
        this.resetPasswordForm()
        this.fetchWorkerInfo()
      }
    },
  },
  mounted() {
    this.fetchWorkerInfo()
    ListDailyWork().then(res => {
      this.list = Array.isArray(res?.data) ? res.data : [];
    }).catch(() => { this.list = [] });
  },
  methods: {
    fetchWorkerInfo() {
      const wid = localStorage.getItem("wid");
      if (!wid) return
      FindWorkerById(wid).then(res => {
        if (res.data) {
          const { password, ...profile } = res.data
          this.worker = { ...this.worker, ...profile }
        }
      }).catch(() => {
        this.worker = { nickname: '客服', avatar: '' };
      });
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
      this.oldPasswordReadonly = true
      this.$refs.passwordFormRef?.resetFields()
    },
    // 保存个人资料
    async saveProfile() {
      try {
        const { password, ...payload } = this.worker
        const res = await UpdateWorker(payload, { silent: true });
        if (res.success) {
          this.$message.success('资料已保存');
          this.drawerVisible = false;
          localStorage.setItem('name', this.worker.nickname);
        }
      } catch {
        // 网络异常由请求拦截器统一提示
      }
    },
    changePassword() {
      this.$refs.passwordFormRef.validate(valid => {
        if (!valid) return
        Login({
          username: this.worker.username,
          password: this.passwordForm.oldPassword,
          remember: false,
        }, { silent: true }).then(res => {
          if (!res?.success) {
            this.$message.error(res?.msg || '当前密码不正确')
            return
          }
          UpdateWorker({
            id: this.worker.id,
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
      localStorage.removeItem("wid");
      localStorage.removeItem("token");
      this.$router.push('/login');
    },
    beforeAvatarUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].includes(file.type);
      if (!isImage) {
        this.$message.error('只能上传 JPG/PNG 格式');
        return false;
      }
      const isLt50M = file.size / 1024 / 1024 < 50;
      if (!isLt50M) {
        this.$message.error('图片大小不能超过 50MB');
        return false;
      }
      return true;
    },
    handleAvatarSuccess(res) {
      const url = typeof res === 'string' ? res : (res?.data || '');
      if (url) {
        this.worker.avatar = url;
        this.$message.success('头像上传成功，请点击保存');
      }
    }
  }
}
</script>

<style scoped>
/* 头部样式（与后台管理统一） */
.header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 64px;
  padding: 0 22px;
  background: linear-gradient(180deg, #fbfcfe 0%, #f7f9fc 100%);
  border-bottom: 1px solid #e8edf3;
  box-sizing: border-box;
}

.header-right {
  display: flex;
  align-items: center;
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

.avatar-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 8px;
  height: 38px;
  border-radius: 40px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f1f5f9;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  line-height: 1;
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #334155;
  text-decoration: none;
  font-size: 14px;
  width: 100%;
}

.dropdown-link .el-icon {
  font-size: 16px;
}

.dropdown-logout {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #ef4444;
  width: 100%;
  justify-content: flex-start;
}

:deep(.el-dropdown-menu__item) {
  min-width: 160px;
  padding: 8px 16px;
}

/* 抽屉样式（完全复用后台管理的样式） */
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
  justify-content: center;
  padding: 22px;
  background:
    linear-gradient(180deg, rgba(248, 251, 255, 0.58) 0%, rgba(238, 242, 247, 0.72) 100%),
    url('@/assets/img/background.png') center/cover no-repeat;
  overflow: hidden;
}

.drawer-content::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.16), transparent 34%),
    radial-gradient(circle at 20% 80%, rgba(99, 102, 241, 0.12), transparent 28%),
    radial-gradient(circle at bottom left, rgba(16, 185, 129, 0.1), transparent 34%);
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
  gap: 18px;
  min-height: 0;
  flex: 1;
}

.drawer-panel {
  display: flex;
  flex-direction: column;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(16px);
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
  overflow: hidden;
}

.setting-sub-tabs {
  display: flex;
  gap: 6px;
  padding: 14px 18px 0;
  flex-shrink: 0;
}

.setting-sub-tab {
  flex: 1;
  height: 36px;
  border: 1px solid #e4e7ec;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.88);
  color: #667085;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.setting-sub-tab:hover:not(.setting-sub-tab--active) {
  color: #344054;
  border-color: #d0d5dd;
  background: #fff;
}

.setting-sub-tab--active {
  color: #fff;
  font-weight: 600;
  background: #3b82f6;
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.24);
}

.drawer-form--panel {
  border: 0;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
  max-height: calc(100vh - 420px);
}

.drawer-actions--panel {
  margin-top: 0;
  padding: 0 18px 18px;
}

.drawer-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(16px);
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
}

.drawer-avatar {
  border: 3px solid rgba(255, 255, 255, 0.95);
  box-shadow: 0 10px 24px rgba(59, 130, 246, 0.18);
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
}

.avatar-upload-tip {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 5px 0;
  border-radius: 0 0 36px 36px;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.08) 0%, rgba(15, 23, 42, 0.72) 100%);
  color: #fff;
  font-size: 12px;
  text-align: center;
  opacity: 0;
  transform: translateY(4px);
  transition: all 0.2s ease;
}

.avatar-uploader:hover .avatar-upload-tip {
  opacity: 1;
  transform: translateY(0);
}

.drawer-user {
  min-width: 0;
}

.drawer-name {
  font-size: 22px;
  font-weight: 700;
  color: #101828;
  line-height: 1.2;
  word-break: break-word;
}

.drawer-role {
  display: inline-flex;
  align-items: center;
  margin-top: 8px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  font-size: 12px;
  font-weight: 600;
}

.drawer-form {
  margin: 0;
  padding: 18px 18px 10px;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(16px);
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
  max-height: calc(100vh - 360px);
  overflow-y: auto;
  scrollbar-width: thin;
}

.drawer-form :deep(.el-form-item) {
  margin-bottom: 18px;
  align-items: center;
}

.drawer-form :deep(.el-form-item__label) {
  color: #344054;
  line-height: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: normal;
  text-align: center;
  padding-right: 0;
}

.drawer-form :deep(.el-form-item__label .iconfont) {
  color: #3b82f6;
  flex-shrink: 0;
  font-size: 18px;
  width: 18px;
  text-align: center;
}

.drawer-form :deep(.el-form-item__content) {
  min-width: 0;
  margin-left: 0 !important;
}

.drawer-form :deep(.el-input__wrapper),
.drawer-form :deep(.el-select__wrapper),
.drawer-form :deep(.el-textarea__inner) {
  border-radius: 14px;
  box-shadow: 0 1px 2px rgba(16, 24, 40, 0.04);
}

.drawer-actions {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.drawer-save-button,
.drawer-logout-button {
  width: 100%;
  height: 46px;
  border-radius: 14px;
  font-weight: 600;
}

.drawer-save-button {
  box-shadow: 0 12px 26px rgba(59, 130, 246, 0.24);
}

.drawer-logout-button {
  color: #d92d20;
  border-color: rgba(217, 45, 32, 0.2);
  background: rgba(255, 255, 255, 0.88);
}

.drawer-logout-button:hover {
  color: #d92d20;
  background: rgba(217, 45, 32, 0.06);
  border-color: rgba(217, 45, 32, 0.35);
}

:deep(.el-button + .el-button) {
  margin-left: 0;
}

.drawer-form :deep(.drawer-icon-nickname) {
  margin-left: -20px;
}
</style>

<style>
/* 全局覆盖抽屉内边距 */
.profile-drawer {
  --el-drawer-padding-primary: 0px !important;
}
</style>
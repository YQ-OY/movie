<template>
  <el-drawer
    v-model="visible"
    direction="rtl"
    size="560px"
    :with-header="false"
    class="profile-drawer"
    :class-name="'profile-drawer-wrap'"
    @open="handleOpen"
  >
    <div class="drawer-content">
      <div class="drawer-main">
        <div class="drawer-header">
          <el-upload
            v-if="activeTab === 'setting' && settingSubTab === 'profile' && profileEditing"
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :on-success="handleAvatarSuccess"
            :action="uploadAction"
            :headers="uploadHeaders"
          >
            <el-avatar class="drawer-avatar" :size="72" :src="userInfo.avatar || defaultAvatar" />
            <div class="avatar-upload-tip">点击更换头像</div>
          </el-upload>
          <el-avatar
            v-else
            class="drawer-avatar"
            :size="72"
            :src="userInfo.avatar || defaultAvatar"
          />
          <div class="drawer-user">
            <div class="drawer-name">{{ userInfo.nickname || '用户' }}</div>
            <div class="drawer-role">影迷会员</div>
          </div>
        </div>

        <div class="drawer-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            type="button"
            class="drawer-tab"
            :class="{ 'drawer-tab--active': activeTab === tab.key }"
            @click="activeTab = tab.key"
          >
            <el-icon><component :is="tab.icon" /></el-icon>
            <span>{{ tab.label }}</span>
          </button>
        </div>

        <div class="drawer-panel">
          <!-- 个人设置 -->
          <template v-if="activeTab === 'setting'">
            <div class="setting-sub-tabs">
              <button
                type="button"
                class="setting-sub-tab"
                :class="{ 'setting-sub-tab--active': settingSubTab === 'profile' }"
                @click="switchSettingSubTab('profile')"
              >
                个人资料
              </button>
              <button
                type="button"
                class="setting-sub-tab"
                :class="{ 'setting-sub-tab--active': settingSubTab === 'account' }"
                @click="switchSettingSubTab('account')"
              >
                账号设置
              </button>
            </div>

            <!-- 个人资料 - 查看 -->
            <template v-if="settingSubTab === 'profile' && !profileEditing">
              <div class="profile-view">
                <div class="profile-info-item">
                  <span class="profile-info-label">
                    <el-icon class="drawer-form-icon"><Avatar /></el-icon>
                    用户名
                  </span>
                  <span class="profile-info-value">{{ displayValue(userInfo.username) }}</span>
                </div>
                <div class="profile-info-item">
                  <span class="profile-info-label">
                    <el-icon class="drawer-form-icon"><User /></el-icon>
                    昵称
                  </span>
                  <span class="profile-info-value">{{ displayValue(userInfo.nickname) }}</span>
                </div>
                <div class="profile-info-item">
                  <span class="profile-info-label">
                    <el-icon class="drawer-form-icon"><Iphone /></el-icon>
                    手机号
                  </span>
                  <span class="profile-info-value">{{ displayValue(userInfo.phone) }}</span>
                </div>
                <div class="profile-info-item">
                  <span class="profile-info-label">
                    <el-icon class="drawer-form-icon"><Calendar /></el-icon>
                    生日
                  </span>
                  <span class="profile-info-value">{{ displayValue(userInfo.birthday) }}</span>
                </div>
                <div class="profile-info-item">
                  <span class="profile-info-label">
                    <el-icon class="drawer-form-icon"><UserFilled /></el-icon>
                    性别
                  </span>
                  <span class="profile-info-value">{{ displayValue(userInfo.gender) }}</span>
                </div>
                <div class="profile-info-item profile-info-item--block">
                  <span class="profile-info-label">
                    <el-icon class="drawer-form-icon"><EditPen /></el-icon>
                    个人简介
                  </span>
                  <span class="profile-info-value profile-info-value--multiline">{{ displayValue(userInfo.info) }}</span>
                </div>
              </div>
              <div class="drawer-actions">
                <el-button type="primary" class="drawer-save-button" @click="startProfileEdit">
                  <el-icon><Edit /></el-icon>
                  修改资料
                </el-button>
                <el-button class="drawer-logout-button" @click="handleLogout">退出登录</el-button>
              </div>
            </template>

            <!-- 个人资料 - 编辑 -->
            <template v-else-if="settingSubTab === 'profile' && profileEditing">
              <el-form :model="profileDraft" :rules="profileRules" ref="profileFormRef" label-width="48px" class="drawer-form">
                <el-form-item>
                  <template #label>
                    <el-icon class="drawer-form-icon"><Avatar /></el-icon>
                  </template>
                  <el-input v-model="profileDraft.username" disabled placeholder="用户名" />
                </el-form-item>
                <el-form-item>
                  <template #label>
                    <el-icon class="drawer-form-icon"><User /></el-icon>
                  </template>
                  <el-input v-model="profileDraft.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item prop="phone">
                  <template #label>
                    <el-icon class="drawer-form-icon"><Iphone /></el-icon>
                  </template>
                  <el-input v-model="profileDraft.phone" placeholder="请输入手机号" maxlength="11" />
                </el-form-item>
                <el-form-item>
                  <template #label>
                    <el-icon class="drawer-form-icon"><Calendar /></el-icon>
                  </template>
                  <el-date-picker
                    v-model="profileDraft.birthday"
                    type="date"
                    value-format="YYYY-MM-DD"
                    placeholder="选择生日"
                    style="width: 100%"
                  />
                </el-form-item>
                <el-form-item>
                  <template #label>
                    <el-icon class="drawer-form-icon"><UserFilled /></el-icon>
                  </template>
                  <el-select v-model="profileDraft.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option label="男生" value="男生" />
                    <el-option label="女生" value="女生" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <template #label>
                    <el-icon class="drawer-form-icon"><EditPen /></el-icon>
                  </template>
                  <el-input v-model="profileDraft.info" type="textarea" :rows="3" placeholder="介绍一下自己" />
                </el-form-item>
              </el-form>
              <div class="drawer-actions drawer-actions--row">
                <el-button class="drawer-cancel-button" @click="cancelProfileEdit">取消</el-button>
                <el-button type="primary" class="drawer-save-button drawer-save-button--inline" @click="saveProfile">保存资料</el-button>
              </div>
            </template>

            <!-- 账号设置 -->
            <template v-else>
              <el-form
                :model="passwordForm"
                :rules="passwordRules"
                ref="passwordFormRef"
                label-width="48px"
                class="drawer-form password-form"
                autocomplete="off"
              >
                <el-form-item>
                  <template #label>
                    <el-icon class="drawer-form-icon"><Avatar /></el-icon>
                  </template>
                  <el-input v-model="userInfo.username" disabled autocomplete="off" />
                </el-form-item>
                <el-form-item prop="oldPassword">
                  <template #label>
                    <el-icon class="drawer-form-icon"><Lock /></el-icon>
                  </template>
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入当前密码"
                    show-password
                    autocomplete="off"
                    name="profile-verify-pwd"
                    :readonly="oldPasswordReadonly"
                    @focus="oldPasswordReadonly = false"
                  />
                </el-form-item>
                <el-form-item prop="newPassword">
                  <template #label>
                    <el-icon class="drawer-form-icon"><Key /></el-icon>
                  </template>
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                    autocomplete="new-password"
                  />
                </el-form-item>
                <el-form-item prop="confirmPassword">
                  <template #label>
                    <el-icon class="drawer-form-icon"><Key /></el-icon>
                  </template>
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                    autocomplete="new-password"
                  />
                </el-form-item>
              </el-form>
              <div class="drawer-actions">
                <el-button type="primary" class="drawer-save-button" @click="changePassword">修改密码</el-button>
              </div>
            </template>
          </template>

          <!-- 我的订单 -->
          <div v-else-if="activeTab === 'order'" class="drawer-sub-page">
            <Order />
          </div>

          <!-- 购物车 -->
          <div v-else-if="activeTab === 'cart'" class="drawer-sub-page">
            <Cart />
          </div>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import { findById, updateUser, Login } from '@/api/user'
import { phoneOptionalFormRules } from '@/utils/validate'
import config from '@/config'
import Order from '@/views/me/Order.vue'
import Cart from '@/views/me/Cart.vue'
import { PROFILE_TABS } from '@/utils/profileCenter'

export default {
  name: 'ProfileDrawer',
  components: { Order, Cart },
  props: {
    modelValue: { type: Boolean, default: false },
    initialTab: { type: String, default: 'setting' },
  },
  emits: ['update:modelValue', 'logout', 'user-updated'],
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的新密码不一致'))
      } else {
        callback()
      }
    }
    return {
      activeTab: 'setting',
      settingSubTab: 'profile',
      profileEditing: false,
      userInfo: {
        id: '',
        username: '',
        nickname: '',
        phone: '',
        birthday: '',
        gender: '',
        info: '',
        avatar: '',
      },
      profileDraft: {
        id: '',
        username: '',
        nickname: '',
        phone: '',
        birthday: '',
        gender: '',
        info: '',
        avatar: '',
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
      },
      oldPasswordReadonly: true,
      defaultAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      profileRules: {
        phone: phoneOptionalFormRules,
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
      tabs: [
        { key: 'setting', label: '个人设置', icon: 'Setting' },
        { key: 'order', label: '我的订单', icon: 'Tickets' },
        { key: 'cart', label: '购物车', icon: 'ShoppingCart' },
      ],
    }
  },
  watch: {
    initialTab(tab) {
      if (this.visible && PROFILE_TABS.includes(tab)) {
        this.activeTab = tab
      }
    },
  },
  computed: {
    visible: {
      get() {
        return this.modelValue
      },
      set(val) {
        this.$emit('update:modelValue', val)
      },
    },
    uploadAction() {
      return config.API_URL + '/upload'
    },
    uploadHeaders() {
      const token = localStorage.getItem('token')
      return token ? { Authorization: token } : {}
    },
  },
  methods: {
    handleOpen() {
      this.activeTab = PROFILE_TABS.includes(this.initialTab) ? this.initialTab : 'setting'
      this.settingSubTab = 'profile'
      this.profileEditing = false
      this.resetPasswordForm()
      this.fetchUserInfo()
    },
    switchSettingSubTab(tab) {
      if (this.settingSubTab === tab) return
      if (this.profileEditing) {
        this.profileEditing = false
      }
      this.settingSubTab = tab
      if (tab === 'account') {
        this.resetPasswordForm()
      }
    },
    displayValue(value) {
      return value && String(value).trim() ? value : '未填写'
    },
    startProfileEdit() {
      this.profileDraft = { ...this.userInfo }
      this.profileEditing = true
      this.$nextTick(() => {
        this.$refs.profileFormRef?.clearValidate()
      })
    },
    cancelProfileEdit() {
      this.profileEditing = false
      this.profileDraft = { ...this.userInfo }
      this.$refs.profileFormRef?.resetFields()
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
    fetchUserInfo() {
      const uid = localStorage.getItem('uid')
      if (!uid) return
      findById(uid).then(res => {
        if (res.data) {
          const { password, ...profile } = res.data
          this.userInfo = { ...this.userInfo, ...profile }
          if (!this.profileEditing) {
            this.profileDraft = { ...this.userInfo }
          }
        }
      }).catch(() => {})
    },
    saveProfile() {
      this.$refs.profileFormRef.validate(valid => {
        if (!valid) return
        const { password, ...payload } = this.profileDraft
        updateUser(payload, { silent: true }).then(res => {
          if (res.success) {
            const { password: _, ...profile } = res.data || {}
            this.userInfo = { ...this.userInfo, ...profile }
            this.profileDraft = { ...this.userInfo }
            this.profileEditing = false
            this.$message.success('资料已保存')
            this.$emit('user-updated', this.userInfo)
          }
        })
      })
    },
    changePassword() {
      this.$refs.passwordFormRef.validate(valid => {
        if (!valid) return
        Login({
          username: this.userInfo.username,
          password: this.passwordForm.oldPassword,
          remember: false,
        }, { silent: true }).then(res => {
          if (!res?.success) {
            this.$message.error(res?.msg || '当前密码不正确')
            return
          }
          updateUser({
            id: this.userInfo.id,
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
      this.visible = false
      this.$emit('logout')
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
      const { password, ...payload } = { ...this.userInfo, avatar: url }
      updateUser(payload).then(res => {
        if (res.success) {
          const { password: _, ...profile } = res.data || {}
          this.userInfo = { ...this.userInfo, ...profile, avatar: url }
          this.$message.success('头像上传成功')
          this.$emit('user-updated', this.userInfo)
        }
      })
    },
  },
}
</script>

<style scoped>
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
  padding: 22px;
  background:
    linear-gradient(180deg, rgba(17, 17, 17, 0.42) 0%, rgba(12, 12, 12, 0.38) 100%),
    url('@/assets/img/background.jpg') center/cover no-repeat;
  overflow: hidden;
  box-sizing: border-box;
}

.drawer-content::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top right, rgba(0, 204, 54, 0.08), transparent 34%),
    radial-gradient(circle at 80% 20%, rgba(0, 204, 54, 0.05), transparent 28%);
  pointer-events: none;
}

.drawer-content > * {
  position: relative;
  z-index: 1;
}

.drawer-main {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  min-height: 0;
}

.drawer-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(24, 24, 24, 0.58);
  backdrop-filter: blur(20px) saturate(1.2);
  -webkit-backdrop-filter: blur(20px) saturate(1.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.28);
  flex-shrink: 0;
}

.drawer-avatar {
  border: 3px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.35);
  flex-shrink: 0;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
  flex-shrink: 0;
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
  color: var(--app-title-strong);
  line-height: 1.2;
  word-break: break-word;
}

.drawer-role {
  display: inline-flex;
  align-items: center;
  margin-top: 8px;
  padding: 4px 10px;
  border-radius: 999px;
  background: var(--app-primary-bg);
  color: var(--app-primary);
  font-size: 12px;
  font-weight: 600;
}

.drawer-tabs {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.drawer-tab {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 42px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  background: rgba(24, 24, 24, 0.52);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  color: var(--app-text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.drawer-tab:hover:not(.drawer-tab--active) {
  background: rgba(40, 40, 40, 0.62);
  color: var(--app-text);
  border-color: rgba(255, 255, 255, 0.16);
}

.drawer-tab--active {
  background: var(--app-gradient-brand);
  color: #ffffff;
  font-weight: 600;
  box-shadow: 0 6px 16px var(--app-primary-shadow-strong);
  border-color: transparent;
}

.drawer-tab--active .el-icon {
  color: #ffffff;
}

.drawer-panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(24, 24, 24, 0.55);
  backdrop-filter: blur(20px) saturate(1.2);
  -webkit-backdrop-filter: blur(20px) saturate(1.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.28);
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
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  background: rgba(24, 24, 24, 0.48);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: var(--app-text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.setting-sub-tab:hover:not(.setting-sub-tab--active) {
  color: var(--app-text);
  background: rgba(40, 40, 40, 0.58);
  border-color: rgba(255, 255, 255, 0.14);
}

.setting-sub-tab--active {
  color: #ffffff;
  font-weight: 600;
  background: var(--app-gradient-brand);
  border-color: transparent;
  box-shadow: 0 4px 12px var(--app-primary-shadow);
}

.drawer-form :deep(.el-input.is-disabled .el-input__wrapper) {
  background: rgba(24, 24, 24, 0.45);
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.08) inset;
}

.profile-view {
  flex: 1;
  overflow-y: auto;
  padding: 18px;
  scrollbar-width: thin;
}

.profile-info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--app-border);
}

.profile-info-item:last-child {
  border-bottom: none;
}

.profile-info-item--block {
  flex-direction: column;
  gap: 8px;
}

.profile-info-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  flex: 0 0 108px;
  font-size: 13px;
  font-weight: 600;
  color: var(--app-text-secondary);
}

.profile-info-value {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  color: var(--app-title);
  line-height: 1.6;
  word-break: break-word;
}

.profile-info-value--multiline {
  white-space: pre-wrap;
}

.drawer-actions--row {
  flex-direction: row;
}

.drawer-cancel-button,
.drawer-save-button--inline {
  flex: 1;
  height: 46px;
  border-radius: 14px;
  font-weight: 600;
}

.drawer-save-button--inline {
  box-shadow: 0 12px 26px var(--app-primary-shadow);
}

.drawer-form {
  margin: 0;
  padding: 18px 18px 10px;
  flex: 1;
  overflow-y: auto;
  scrollbar-width: thin;
}

.drawer-form :deep(.el-form-item) {
  margin-bottom: 18px;
  align-items: center;
}

.drawer-form :deep(.el-form-item__label) {
  color: var(--app-text-secondary);
  line-height: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-right: 0;
}

.drawer-form-icon {
  color: var(--app-primary-light);
  font-size: 18px;
}

.drawer-form :deep(.el-form-item__content) {
  min-width: 0;
  margin-left: 0 !important;
}

.drawer-form :deep(.el-input__wrapper),
.drawer-form :deep(.el-select__wrapper),
.drawer-form :deep(.el-textarea__inner) {
  border-radius: 14px;
  background: rgba(24, 24, 24, 0.45) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.08) inset !important;
}

.drawer-form :deep(.el-textarea__inner) {
  min-height: 92px !important;
  resize: none;
}

.drawer-actions {
  padding: 0 18px 18px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-shrink: 0;
}

.drawer-save-button,
.drawer-logout-button {
  width: 100%;
  height: 46px;
  border-radius: 14px;
  font-weight: 600;
}

.drawer-save-button {
  background: var(--app-gradient-brand) !important;
  border: none !important;
  box-shadow: 0 12px 26px var(--app-primary-shadow);
}

.drawer-logout-button {
  color: #ff6b6b;
  border-color: rgba(255, 107, 107, 0.25);
  background: rgba(24, 24, 24, 0.5);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.drawer-logout-button:hover {
  color: #ff5252;
  background: rgba(255, 82, 82, 0.1);
  border-color: rgba(255, 82, 82, 0.35);
}

.drawer-sub-page {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 16px;
  scrollbar-width: thin;
}

.drawer-sub-page :deep(.ticket-page__title) {
  display: none;
}

.drawer-sub-page :deep(.ticket-page) {
  width: 100%;
}

.drawer-sub-page :deep(.ticket-card) {
  border-radius: 14px;
  background: rgba(24, 24, 24, 0.52) !important;
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}

.drawer-sub-page :deep(.ticket-checkout) {
  position: sticky;
  bottom: 0;
  background: rgba(24, 24, 24, 0.62);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 14px;
  margin-top: 12px;
  padding: 12px 14px;
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

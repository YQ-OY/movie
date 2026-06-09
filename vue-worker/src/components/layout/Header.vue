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
            <el-upload class="avatar-uploader" :show-file-list="false" :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess" :action="uploadAction" :headers="uploadHeaders">
              <el-avatar class="drawer-avatar" :size="72" :src="worker.avatar || defaultAvatar" />
              <div class="avatar-upload-tip">点击更换头像</div>
            </el-upload>
            <div class="drawer-user">
              <div class="drawer-name">{{ worker.username || '客服' }}</div>
              <div class="drawer-role">{{ worker.department || '未设置部门' }}</div>
            </div>
          </div>

          <el-form :model="worker" label-width="48px" class="drawer-form">
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
            <el-form-item>
              <template #label>
                <i class="iconfont icon-yuangongguanli" />
              </template>
              <el-select v-model="worker.department" placeholder="请选择部门" style="width: 100%">
                <el-option label="客服部" value="客服部" />
                <el-option label="运营部" value="运营部" />
                <el-option label="宣传部" value="宣传部" />
                <el-option label="策划部" value="策划部" />
                <el-option label="人事部" value="人事部" />
                <el-option label="监管部" value="监管部" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <template #label>
                <i class="iconfont icon-suo" />
              </template>
              <el-button link type="primary" @click="openPasswordModal">
                <el-icon>
                  <Lock />
                </el-icon>修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="drawer-actions">
          <el-button type="primary" class="drawer-save-button" @click="saveProfile">保存资料</el-button>
          <el-button class="drawer-logout-button" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" v-model="passwordDialogVisible" width="30%" align-center class="film-dialog">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="至少6位" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPassword">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { FindWorkerById, UpdateWorker } from "@/api/worker";
import { ListDailyWork } from "@/api/worker";
import { Setting, SwitchButton, Lock } from '@element-plus/icons-vue'
import config from "@/config";

export default {
  name: "Header",
  components: { Setting, SwitchButton, Lock },
  data() {
    return {
      worker: {
        id: '',
        username: '',
        nickname: '',
        phone: '',
        gender: '',
        avatar: '',
        department: '',
      },
      defaultAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      list: [],           // 通知列表（可扩展）
      drawerVisible: false,
      passwordDialogVisible: false,
      passwordForm: { newPassword: '' },
      uploadAction: config.API_URL + '/upload',
      uploadHeaders: { Authorization: localStorage.getItem('token') },
    }
  },
  mounted() {
    const wid = localStorage.getItem("wid");
    if (wid) {
      FindWorkerById(wid).then(res => {
        this.worker = res.data;
      }).catch(() => {
        this.worker = { nickname: '客服', avatar: '' };
      });
    }
    ListDailyWork().then(res => {
      this.list = Array.isArray(res?.data) ? res.data : [];
    }).catch(() => { this.list = [] });
  },
  methods: {
    // 保存个人资料
    async saveProfile() {
      try {
        const res = await UpdateWorker(this.worker);
        if (res.success) {
          this.$message.success('资料已保存');
          this.drawerVisible = false;
          // 同步更新头部显示
          localStorage.setItem('name', this.worker.nickname);
        } else {
          this.$message.error(res.msg || '保存失败');
        }
      } catch (error) {
        this.$message.error('保存失败');
      }
    },
    // 打开修改密码弹窗
    openPasswordModal() {
      this.passwordForm.newPassword = '';
      this.passwordDialogVisible = true;
    },
    async submitPassword() {
      if (!this.passwordForm.newPassword || this.passwordForm.newPassword.length < 6) {
        this.$message.warning('密码长度至少6位');
        return;
      }
      const updatedWorker = { ...this.worker, password: this.passwordForm.newPassword };
      try {
        const res = await UpdateWorker(updatedWorker);
        if (res.success) {
          this.$message.success('密码修改成功');
          this.passwordDialogVisible = false;
        } else {
          this.$message.error(res.msg || '修改失败');
        }
      } catch (error) {
        this.$message.error('修改失败');
      }
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

/* 对话框样式（用于修改密码） */
.film-dialog {
  border-radius: 18px;
  overflow: hidden;
}

.film-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 14px;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-bottom: 1px solid #e2e8f0;
}

.film-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}

.film-dialog :deep(.el-dialog__body) {
  padding: 20px 24px 12px;
  background: #fbfdff;
}

.film-dialog :deep(.el-dialog__footer) {
  padding: 14px 22px 22px;
  background: #fbfdff;
  border-top: 1px solid #e2e8f0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer .el-button {
  min-width: 92px;
  height: 38px;
  border-radius: 10px;
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
<template>
  <div class="film-list">

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">新增员工</div>
          <div class="page-subtitle">填写员工基本信息，上传头像后即可创建账号</div>
        </div>
      </div>
    </div>

    <!-- 表单卡片（左右两列布局） -->
    <div class="table-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px" class="film-form">
        <div class="film-edit-layout--two-col">
          <!-- 左侧：头像上传区域 -->
          <div class="film-cover-panel">
            <div class="film-cover-panel__title">员工头像</div>
            <el-upload v-if="!avatarPreview" class="film-cover-uploader" accept=".png,.jpg,.jpeg" :headers="header"
              :action="uploadAction" :on-success="handleUploadSuccess" :show-file-list="false"
              :before-upload="beforeAvatarUpload">
              <div class="film-cover-box film-cover-box--empty">
                <div class="film-cover-box__placeholder">
                  <el-icon class="film-cover-box__icon">
                    <Upload />
                  </el-icon>
                  <div class="film-cover-box__text">点击上传头像</div>
                  <div class="film-cover-box__hint">支持 JPG / PNG，建议 1:1 比例</div>
                </div>
              </div>
            </el-upload>
            <div v-else class="film-cover-box film-cover-box--preview">
              <el-image class="film-cover-box__image" :src="avatarPreview" fit="cover" />
              <el-button class="film-cover-box__delete" circle size="small" type="danger" @click.stop="removeAvatar">
                <el-icon>
                  <Delete />
                </el-icon>
              </el-button>
            </div>
          </div>

          <!-- 右侧：表单区域 -->
          <div class="film-form-panel">
            <div class="film-form-grid">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" placeholder="请输入用户名" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="form.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                </el-select>
              </el-form-item>
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" maxlength="11" placeholder="请输入手机号" show-word-limit />
              </el-form-item>
              <el-form-item label="入职部门" prop="department" class="full-row">
                <el-radio-group v-model="form.department">
                  <el-radio label="客服部">客服部</el-radio>
                  <el-radio label="运营部">运营部</el-radio>
                  <el-radio label="宣传部">宣传部</el-radio>
                  <el-radio label="策划部">策划部</el-radio>
                  <el-radio label="人事部">人事部</el-radio>
                  <el-radio label="监管部">监管部</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="form-footer">
          <el-button @click="onCancel">取消</el-button>
          <el-button type="primary" @click="onSubmit">立即创建</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { CreateWorker } from "@/api/worker";
import config from "@/config";
import { Upload, Delete } from '@element-plus/icons-vue'
import { phoneFormRules } from '@/utils/validate'

export default {
  components: { Upload, Delete },
  data() {
    return {
      header: { Authorization: localStorage.getItem("token") },
      uploadAction: config.API_URL + '/upload',
      loading: false,
      form: {
        username: '',
        nickname: '',
        password: '',
        gender: '',
        avatar: '',
        phone: '',
        department: '',
      },
      avatarPreview: '',
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        phone: phoneFormRules,
        department: [{ required: true, message: '请选择入职部门', trigger: 'change' }],
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (!valid) return
        this.loading = true
        this.form.avatar = this.avatarPreview
        CreateWorker(this.form).then(() => {
          this.$message.success('添加员工成功')
          this.$router.push("/worker/list")
        }).catch(() => {
          this.$message.error('添加失败')
        }).finally(() => {
          this.loading = false
        })
      })
    },
    onCancel() {
      this.$router.back()
    },
    handleUploadSuccess(res) {
      const url = typeof res === 'string' ? res : (res?.data || '')
      if (url) {
        this.avatarPreview = url
      }
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
      const reader = new FileReader()
      reader.onload = (e) => { this.avatarPreview = e.target?.result || '' }
      reader.readAsDataURL(file)
      return true
    },
    removeAvatar() {
      this.avatarPreview = ''
      this.form.avatar = ''
    }
  }
}
</script>

<style scoped>
/* ===== 整体布局（复用电影管理页面样式） ===== */
.film-list {
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
  padding: 20px;
  box-sizing: border-box;
  background: rgb(250, 251, 252);
}

.page-header {
  flex: 0 0 auto;
  padding: 20px 22px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.page-search-bar__title {
  min-width: 220px;
}

.page-title {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: 0.2px;
}

.page-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

/* 表单卡片 */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  overflow-y: auto;
}

.film-edit-layout--two-col {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 24px;
}

/* 左侧头像面板 */
.film-cover-panel {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
  padding: 16px;
}

.film-cover-panel__title {
  font-size: 14px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 12px;
}

.film-cover-uploader {
  display: block;
  width: 100%;
}

.film-cover-box {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 14px;
  overflow: hidden;
  border: 1px dashed #cbd5e1;
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.film-cover-box--empty {
  cursor: pointer;
}

.film-cover-box--preview {
  border-style: solid;
}

.film-cover-box:hover .film-cover-box__delete {
  opacity: 1;
  transform: scale(1);
}

.film-cover-box__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.film-cover-box__delete {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  opacity: 0;
  transform: scale(0.92);
  transition: all 0.18s ease;
}

.film-cover-box__placeholder {
  text-align: center;
  padding: 16px;
  color: #64748b;
}

.film-cover-box__icon {
  font-size: 30px;
  color: #3b82f6;
}

.film-cover-box__text {
  margin-top: 10px;
  font-size: 14px;
  font-weight: 700;
  color: #334155;
}

.film-cover-box__hint {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.5;
}

/* 右侧表单面板 */
.film-form-panel {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
  padding: 20px 24px 8px;
}

.film-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 2px 20px;
}

.film-form-grid :deep(.el-form-item) {
  margin-bottom: 18px;
}

/* 部门选项需要占满整行 */
.full-row {
  grid-column: 1 / -1;
}

/* 统一输入框宽度 */
.full-width-input {
  width: 100%;
}

/* 聚焦蓝色边框（与 List.vue 一致） */
.film-form .el-input_wrapper.is-focus,
.film-form .el-input_wrapper.is-focused,
.film-form .el-select_wrapper.is-focus,
.film-form .el-select_wrapper.is-focused,
.film-form .el-textarea__inner:focus,
.film-form .el-date-editor .el-input_wrapper.is-focus,
.film-form .el-date-editor .el-input_wrapper.is-focused,
.film-form .el-time-picker .el-input_wrapper.is-focus,
.film-form .el-time-picker .el-input_wrapper.is-focused,
.film-form .el-input-number_wrapper.is-focus,
.film-form .el-input-number_wrapper.is-focused {
  border-color: #409eff !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) !important;
}

/* 底部按钮 */
.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.form-footer .el-button {
  min-width: 98px;
  height: 40px;
  border-radius: 40px;
  font-weight: 600;
  transition: all 0.2s;
}

.form-footer .el-button--primary {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border: none;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
}

.form-footer .el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(59, 130, 246, 0.35);
}

.form-footer .el-button:not(.el-button--primary) {
  background: #f8fafc;
  border-color: #e2e8f0;
  color: #334155;
}

.form-footer .el-button:not(.el-button--primary):hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  transform: translateY(-1px);
}
</style>
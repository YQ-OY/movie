<template>
  <div class="film-list">

    <!-- 页面头部（与电影管理页面一致） -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">新增电影</div>
          <div class="page-subtitle">填写电影信息，上传封面后即可上架</div>
        </div>
      </div>
    </div>

    <!-- 表单卡片（左右两列布局） -->
    <div class="table-card">
      <el-form ref="form" :model="form" label-width="92px" class="film-form">
        <div class="film-edit-layout--two-col">
          <!-- 左侧：封面上传区域 -->
          <div class="film-cover-panel">
            <div class="film-cover-panel__title">电影封面</div>
            <el-upload v-if="!coverPreview" class="film-cover-uploader" accept=".png,.jpg,.jpeg" :headers="header"
              :action="uploadAction" :on-success="handleUploadSuccess" :show-file-list="false"
              :before-upload="beforeCoverUpload">
              <div class="film-cover-box film-cover-box--empty">
                <div class="film-cover-box__placeholder">
                  <el-icon class="film-cover-box__icon">
                    <Upload />
                  </el-icon>
                  <div class="film-cover-box__text">点击上传封面</div>
                  <div class="film-cover-box__hint">支持 JPG / PNG，建议 2:3 海报比例</div>
                </div>
              </div>
            </el-upload>
            <div v-else class="film-cover-box film-cover-box--preview">
              <el-image class="film-cover-box__image" :src="coverPreview" fit="cover" :preview-src-list="[coverPreview]"
                :initial-index="0" preview-teleported />
              <el-button class="film-cover-box__delete" circle size="small" type="danger" @click.stop="removeCover">
                <el-icon>
                  <Delete />
                </el-icon>
              </el-button>
            </div>
          </div>

          <!-- 右侧：表单区域 -->
          <div class="film-form-panel">
            <div class="film-form-grid">
              <el-form-item label="电影名称">
                <el-input v-model="form.name" placeholder="请输入电影名称" class="full-width-input" />
              </el-form-item>

              <el-form-item label="上映地区">
                <el-select v-model="form.region" placeholder="请选择上映区域" class="full-width-input">
                  <el-option label="中国大陆" value="中国大陆" />
                  <el-option label="中国香港" value="中国香港" />
                  <el-option label="中国台湾" value="中国台湾" />
                  <el-option label="日本" value="日本" />
                  <el-option label="美国" value="美国" />
                  <el-option label="韩国" value="韩国" />
                  <el-option label="英国" value="英国" />
                  <el-option label="法国" value="法国" />
                  <el-option label="印度" value="印度" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>

              <el-form-item label="全片时长">
                <el-input-number v-model="form.duration" :min="10" :max="550" class="full-width-input" />
              </el-form-item>

              <el-form-item label="上映时间">
                <el-date-picker v-model="form.releaseTime" type="date" value-format="YYYY-MM-DD" placeholder="选择日期"
                  class="full-width-input" />
              </el-form-item>

              <el-form-item label="即时上架">
                <el-switch v-model="form.status" class="bordered-switch" />
              </el-form-item>

              <el-form-item label="电影类型" class="film-type-item">
                <el-checkbox-group v-model="form.typeList" class="film-type-checkbox-group">
                  <el-checkbox
                    v-for="item in filmTypeOptions"
                    :key="item"
                    :label="item"
                  />
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="内容简介" class="introduction-item introduction-item--right">
                <el-input rows="8" type="textarea" v-model="form.introduction" placeholder="请输入内容简介"
                  class="full-width-input" />
              </el-form-item>
            </div>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="form-footer">
          <el-button @click="onCancel">取消</el-button>
          <el-button type="primary" @click="onSubmit">确认提交</el-button>
        </div>
      </el-form>
      <div class="form-footer-hint">
        <el-divider content-position="center">
          <span class="hint-text">请确保电影信息真实有效，封面上传后将在首页展示</span>
        </el-divider>
      </div>
    </div>
  </div>
</template>

<script>
import { AddFilm } from "@/api/film";
import config from "@/config";
import { Upload, Delete } from '@element-plus/icons-vue'
import { FILM_TYPE_OPTIONS, joinFilmTypes } from '@/utils/filmType'

export default {
  components: { Upload, Delete },
  data() {
    return {
      header: { Authorization: localStorage.getItem("token") },
      uploadAction: config.API_URL + '/upload',
      url: '',
      coverPreview: '',
      form: {
        cover: '',
        name: '',
        region: '',
        releaseTime: '',
        duration: 120,
        introduction: '',
        typeList: [],
        status: true,
      },
      filmTypeOptions: FILM_TYPE_OPTIONS,
    }
  },
  methods: {
    onSubmit() {
      if (!this.form.name) {
        this.$message.warning('请填写电影名称');
        return;
      }
      if (!this.form.region) {
        this.$message.warning('请选择上映地区');
        return;
      }
      if (!this.form.typeList.length) {
        this.$message.warning('请至少选择一个电影类型');
        return;
      }
      this.form.cover = this.url;
      const payload = {
        ...this.form,
        type: joinFilmTypes(this.form.typeList),
      }
      AddFilm(payload).then(res => {
        if (!res?.success) return
        this.$message.success('电影添加成功!');
        this.$router.push("/film/list");
      }).catch(() => {});
    },
    onCancel() {
      this.$router.back();
    },
    handleUploadSuccess(res) {
      this.url = typeof res === 'string' ? res : (res && typeof res.data === 'string' ? res.data : '');
      this.form.cover = this.url;
      this.coverPreview = this.url;
    },
    beforeCoverUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].includes(file.type);
      if (!isImage) {
        this.$message.error('只能上传 JPG/PNG 格式的封面图片');
        return false;
      }
      const isLt50M = file.size / 1024 / 1024 < 50;
      if (!isLt50M) {
        this.$message.error('封面图片大小不能超过 50MB');
        return false;
      }
      const reader = new FileReader();
      reader.onload = (e) => {
        this.coverPreview = e.target?.result || '';
      };
      reader.readAsDataURL(file);
      return true;
    },
    removeCover() {
      this.url = '';
      this.coverPreview = '';
      this.form.cover = '';
    },
  }
}
</script>

<style scoped>
/* 整体布局（复用电影管理页面样式） */
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

/* 左侧封面面板 */
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
  aspect-ratio: 2 / 3;
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
  cursor: pointer;
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

.introduction-item--right,
.film-type-item {
  grid-column: 1 / -1;
}

.film-type-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 16px;
}

.film-type-checkbox-group :deep(.el-checkbox) {
  margin-right: 0;
}

/* 统一输入框宽度 */
.full-width-input {
  width: 100%;
}

/* 开关样式（与 List.vue 一致） */
.bordered-switch :deep(.el-switch__core) {
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.2);
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

.form-footer-hint {
  margin-top: 20px;
}

.hint-text {
  font-size: 12px;
  color: #94a3b8;
}
</style>
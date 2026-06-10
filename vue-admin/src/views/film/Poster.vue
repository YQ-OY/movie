<template>
  <div class="film-list">

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">轮播海报管理</div>
          <div class="page-subtitle">支持上传、管理首页轮播海报，可自定义上架/下架状态</div>
        </div>

        <div class="search-form">
          <el-button type="primary" class="search-submit-btn" @click="dialogFormVisible = true">
            <el-icon>
              <Plus />
            </el-icon>上传轮播海报
          </el-button>
          <el-button type="danger" class="search-reset-btn" @click="deleteAllPoster">
            <el-icon>
              <Delete />
            </el-icon>删除所有海报
          </el-button>
        </div>
      </div>
    </div>

    <!-- 轮播图预览卡片 -->
    <div class="poster-carousel" v-if="paginatedList.length">
      <el-carousel height="400px" indicator-position="outside" :interval="5000" arrow="always">
        <el-carousel-item v-for="(item, idx) in paginatedList" :key="idx">
          <img :src="item.url" class="carousel-image" />
        </el-carousel-item>
      </el-carousel>
    </div>
    <div v-else class="empty-carousel">
      <el-empty description="暂无轮播海报，请点击上方按钮上传" :image-size="120" />
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="paginatedList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <el-table-column label="上传时间" prop="createAt" min-width="180" align="center" />
        <el-table-column label="标题" prop="title" min-width="180" align="center" />
        <el-table-column label="地址预览" min-width="280" show-overflow-tooltip>
          <template #default="scope">
            <a :href="scope.row.url" target="_blank" class="preview-link">{{ scope.row.url }}</a>
          </template>
        </el-table-column>
        <el-table-column label="上架状态" min-width="180" align="center">
          <template #default="scope">
            <div class="status-cell">
              <el-tag v-if="scope.row.status" type="success" effect="light">上架</el-tag>
              <el-tag v-else type="warning" effect="light">下架</el-tag>
              <el-button v-if="!scope.row.status" link type="success" class="status-action-btn"
                @click="changePosterStatus(scope.row, true)">
                <el-icon>
                  <UploadFilled />
                </el-icon>上架
              </el-button>
              <el-button v-else link type="warning" class="status-action-btn"
                @click="changePosterStatus(scope.row, false)">
                <el-icon>
                  <Download />
                </el-icon>下架
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="danger" plain @click="openDeleteDialog(scope.$index, scope.row)">
              <el-icon>
                <Delete />
              </el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" />
      </div>
    </div>

    <!-- 上传/编辑对话框 -->
    <el-dialog title="上传轮播海报" v-model="dialogFormVisible" width="40%" align-center
      class="film-dialog film-dialog--edit">
      <el-form :model="uploadPoster" label-width="100px" class="film-dialog__form">
        <el-form-item label="海报标题">
          <el-input v-model="uploadPoster.title" placeholder="请输入海报标题" />
        </el-form-item>
        <el-form-item label="上架状态">
          <el-select v-model="uploadPoster.status" placeholder="请选择状态">
            <el-option label="上架" :value="true" />
            <el-option label="下架" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择图片">
          <el-upload class="poster-upload" drag accept=".png,.jpg,.jpeg" :headers="header" :action="uploadAction"
            :on-success="handleUploadSuccess" :show-file-list="false" :before-upload="beforeUpload">
            <el-icon class="el-icon--upload">
              <Upload />
            </el-icon>
            <div class="el-upload__text">将图片拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">只能上传 JPG/PNG 文件，且不超过 10MB</div>
            </template>
          </el-upload>
          <div v-if="uploadPoster.url" class="upload-preview">
            <img :src="uploadPoster.url" class="preview-thumb" />
            <el-button link type="danger" @click="removeImage">移除图片</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框（统一风格） -->
    <el-dialog title="删除海报" v-model="dialogDeleteVisible" width="28%" align-center
      class="film-dialog film-dialog--delete">
      <div class="delete-dialog-body">
        <div class="delete-dialog-body__icon">
          <el-icon>
            <WarningFilled />
          </el-icon>
        </div>
        <div class="delete-dialog-body__content">
          <div class="delete-dialog-body__title">确认删除该海报？</div>
          <div class="delete-dialog-body__desc">删除后将无法恢复，海报标题：<strong>{{ deleteTarget.title }}</strong></div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer dialog-footer--danger">
          <el-button @click="dialogDeleteVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDeletePoster">确定删除</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除所有海报确认对话框 -->
    <el-dialog title="删除所有海报" v-model="dialogDeleteAllVisible" width="28%" align-center
      class="film-dialog film-dialog--delete">
      <div class="delete-dialog-body">
        <div class="delete-dialog-body__icon">
          <el-icon>
            <WarningFilled />
          </el-icon>
        </div>
        <div class="delete-dialog-body__content">
          <div class="delete-dialog-body__title">确认删除所有海报？</div>
          <div class="delete-dialog-body__desc">此操作将清空所有轮播海报，且无法恢复，请谨慎操作。</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer dialog-footer--danger">
          <el-button @click="dialogDeleteAllVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDeleteAllPoster">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import config from "@/config";
import { AddPoster, DeleteAllPoster, DeletePosterById, ListAllPoster, UpdatePoster } from "@/api/film";
import { Upload, Delete, UploadFilled, Download, Plus, WarningFilled } from '@element-plus/icons-vue'

export default {
  name: "PosterManage",
  components: { Upload, Delete, UploadFilled, Download, Plus, WarningFilled },
  data() {
    return {
      header: { Authorization: localStorage.getItem("token") },
      uploadAction: config.API_URL + '/upload',
      loading: false,
      posterList: [],
      currentPage: 1,
      pageSize: 5,
      dialogFormVisible: false,
      dialogDeleteAllVisible: false,
      uploadPoster: {
        url: '',
        title: '',
        status: true,
        createAt: '',
      },
      // 删除对话框相关
      dialogDeleteVisible: false,
      deleteTarget: { id: null, title: '' }
    }
  },
  computed: {
    totalCount() {
      return this.posterList.length
    },
    paginatedList() {
      const start = (this.currentPage - 1) * this.pageSize
      return this.posterList.slice(start, start + this.pageSize)
    }
  },
  mounted() {
    this.loadPosters()
  },
  methods: {
    loadPosters() {
      this.loading = true
      ListAllPoster().then(res => {
        if (!res?.success) return
        this.posterList = res.data || []
        this.currentPage = 1
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
    },
    beforeUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].includes(file.type)
      if (!isImage) {
        this.$message.error('只能上传 JPG/PNG 格式')
        return false
      }
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error('图片大小不能超过 10MB')
        return false
      }
      return true
    },
    handleUploadSuccess(res) {
      const url = typeof res === 'string' ? res : (res?.data || '')
      if (url) {
        this.uploadPoster.url = url
      } else {
        this.$message.error('上传失败，未获取到图片地址')
      }
    },
    removeImage() {
      this.uploadPoster.url = ''
    },
    submitUpload() {
      if (!this.uploadPoster.url) {
        this.$message.warning('请先上传图片')
        return
      }
      if (!this.uploadPoster.title) {
        this.$message.warning('请填写海报标题')
        return
      }
      AddPoster(this.uploadPoster).then(res => {
        if (!res?.success) return
        this.$message.success('上传成功')
        this.dialogFormVisible = false
        this.uploadPoster = { url: '', title: '', status: true, createAt: '' }
        this.loadPosters()
      }).catch(() => {})
    },
    deleteAllPoster() {
      this.dialogDeleteAllVisible = true
    },
    confirmDeleteAllPoster() {
      DeleteAllPoster().then(res => {
        if (!res?.success) return
        this.$message.success('已删除所有海报')
        this.dialogDeleteAllVisible = false
        this.loadPosters()
      }).catch(() => {})
    },
    changePosterStatus(poster, status) {
      const updated = { ...poster, status }
      UpdatePoster(updated).then(res => {
        if (!res?.success) return
        poster.status = status
        this.$message.success(`已${status ? '上架' : '下架'}海报`)
      }).catch(() => {})
    },
    // 打开删除对话框
    openDeleteDialog(index, row) {
      this.deleteTarget = {
        id: row.id,
        title: row.title
      }
      this.dialogDeleteVisible = true
    },
    // 确认删除海报
    confirmDeletePoster() {
      DeletePosterById(this.deleteTarget.id).then(res => {
        if (!res?.success) return
        const targetIndex = this.posterList.findIndex(item => item.id === this.deleteTarget.id)
        if (targetIndex !== -1) this.posterList.splice(targetIndex, 1)
        if (this.paginatedList.length === 1 && this.currentPage > 1) {
          this.currentPage--
        }
        this.dialogDeleteVisible = false
        this.$message.success('删除成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
/* 整体布局 —— 复用电影管理样式 */
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

.page-search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  flex-wrap: wrap;
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

.search-form {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  flex: 1;
  justify-content: flex-end;
}

.search-submit-btn,
.search-reset-btn {
  border-radius: 6px;
}

/* 轮播图卡片 */
.poster-carousel {
  flex-shrink: 0;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  margin-bottom: 4px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.empty-carousel {
  flex-shrink: 0;
  background: #ffffff;
  border-radius: 20px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

/* 表格卡片 */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 18px 18px 0px 18px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.film-table {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  border-radius: 16px;
}

/* 表头样式 */
.film-table :deep(.el-table__header-wrapper th) {
  border-bottom: 1px solid #e2e8f0;
  font-size: 15px;
  font-weight: 700;
  text-align: center;
  height: 58px;
}

/* 单元格居中 */
.film-table :deep(.cell) {
  text-align: center;
}

.film-table :deep(.el-table__cell:first-child .cell) {
  text-align: center;
}

/* 状态单元格内按钮组 */
.status-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.status-action-btn {
  font-size: 13px;
}

.status-action-btn .el-icon {
  margin-right: 4px;
}

/* 预览链接 */
.preview-link {
  color: #3b82f6;
  text-decoration: none;
  word-break: break-all;
}

.preview-link:hover {
  text-decoration: underline;
}

/* 上传组件拖拽区 */
.poster-upload :deep(.el-upload-dragger) {
  width: 100%;
  border-radius: 14px;
  border: 1px dashed #cbd5e1;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  padding: 20px 16px;
}

.upload-preview {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.preview-thumb {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

/* 分页样式 */
.pagination-container {
  flex-shrink: 0;
  margin-top: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #e2e8f0;
  padding: 15px;
}

.pagination-container :deep(.el-pagination) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
  width: 100%;
}

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

/* 弹窗样式复用 film-dialog */
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

.film-dialog__form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.film-dialog__form :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
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

/* 按钮图标与文字对齐 */
.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}

.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.film-dialog--delete :deep(.el-dialog__body) {
  padding: 20px 22px 8px;
}

.delete-dialog-body {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.delete-dialog-body__icon {
  width: 42px;
  height: 42px;
  flex: 0 0 auto;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fef2f2;
  color: #ef4444;
  font-size: 22px;
}

.delete-dialog-body__content {
  flex: 1;
  min-width: 0;
}

.delete-dialog-body__title {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.delete-dialog-body__desc {
  font-size: 13px;
  line-height: 1.7;
  color: #64748b;
}

.dialog-footer--danger .el-button:last-child {
  min-width: 108px;
}
</style>
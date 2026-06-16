<template>
  <div class="film-list">
    <!-- 轮播图预览卡片 -->
    <div class="poster-carousel" v-if="posterList.length">
      <el-carousel height="400px" indicator-position="outside" :interval="5000" arrow="always" :loop="true">
        <el-carousel-item v-for="(item, idx) in posterList" :key="item.id || idx">
          <div class="carousel-item-container">
            <img :src="item.url" class="carousel-image" :alt="item.title" />
            <div class="carousel-overlay">
              <div class="carousel-title">{{ item.title }}</div>
              <el-tag :type="item.status ? 'success' : 'warning'" effect="light" class="carousel-status-tag">
                {{ item.status ? '已上架' : '已下架' }}
              </el-tag>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div v-else class="empty-carousel">
      <el-empty description="暂无轮播海报，请点击上方按钮上传" :image-size="120" />
    </div>

    <!-- 页面头部 - 统一风格 -->
    <div class="table-card">
      <!-- 顶部操作按钮区（原page-header内容） -->
      <div class="table-header-actions">
        <div class="search-container">
          <!-- 左侧：空区域（保持布局对称） -->
          <div class="search-filters"></div>

          <!-- 右侧：操作按钮组 -->
          <div class="search-actions">
            <el-button type="primary" class="btn-primary btn-create" @click="dialogFormVisible = true">
              <el-icon class="btn-icon">
                <Plus />
              </el-icon>
              <span>上传轮播海报</span>
            </el-button>
            <el-button class="btn-secondary btn-delete-all" @click="deleteAllPoster">
              <el-icon class="btn-icon">
                <Delete />
              </el-icon>
              <span>删除所有海报</span>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="paginatedList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <el-table-column label="上传时间" prop="createAt" min-width="180" align="center" />
        <el-table-column label="标题" prop="title" min-width="200" align="center" show-overflow-tooltip />
        <el-table-column label="海报预览" min-width="180" align="center">
          <template #default="scope">
            <el-image class="table-poster-thumb" :src="scope.row.url" fit="cover" :preview-src-list="[scope.row.url]"
              :initial-index="0" preview-teleported />
          </template>
        </el-table-column>
        <el-table-column label="地址预览" min-width="280" show-overflow-tooltip>
          <template #default="scope">
            <a :href="scope.row.url" target="_blank" class="preview-link">{{ scope.row.url }}</a>
          </template>
        </el-table-column>
        <el-table-column label="上架状态" min-width="140" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.status" style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              :loading="statusLoading[scope.row.id]"
              @change="(newStatus) => changePosterStatus(scope.row, newStatus)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="danger" plain @click="openDeleteDialog(scope.$index, scope.row)">
                <el-icon>
                  <Delete />
                </el-icon>
                <span>删除</span>
              </el-button>
            </div>
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
    <el-dialog title="上传轮播海报" v-model="dialogFormVisible" width="45%" align-center
      class="film-dialog film-dialog--edit">
      <el-form :model="uploadPoster" label-width="100px" class="film-dialog__form">
        <el-form-item label="海报标题">
          <el-input v-model="uploadPoster.title" placeholder="请输入海报标题" class="full-width-input" />
        </el-form-item>
        <el-form-item label="上架状态">
          <el-switch v-model="uploadPoster.status"
            style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" />
        </el-form-item>
        <el-form-item label="选择图片">
          <el-upload class="poster-upload" drag accept=".png,.jpg,.jpeg" :headers="header" :action="uploadAction"
            :on-success="handleUploadSuccess" :show-file-list="false" :before-upload="beforeUpload">
            <el-icon class="el-icon--upload">
              <Upload />
            </el-icon>
            <div class="el-upload__text">将图片拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">
                支持 JPG/PNG 格式，建议尺寸 1920×400px，文件大小不超过 10MB
              </div>
            </template>
          </el-upload>
          <div v-if="uploadPoster.url" class="upload-preview">
            <el-image class="preview-thumb" :src="uploadPoster.url" fit="cover" :preview-src-list="[uploadPoster.url]"
              preview-teleported />
            <div class="preview-actions">
              <el-button link type="primary" @click="previewImage">预览大图</el-button>
              <el-button link type="danger" @click="removeImage">移除图片</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload">确定上传</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
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
          <div class="delete-dialog-body__desc">
            删除后将无法恢复，海报标题：<strong>{{ deleteTarget.title }}</strong>
          </div>
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
          <div class="delete-dialog-body__desc">
            此操作将清空所有轮播海报，且无法恢复，请谨慎操作。
          </div>
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
import { Upload, Delete, Plus, WarningFilled, View } from '@element-plus/icons-vue'

export default {
  name: "PosterManage",
  components: { Upload, Delete, Plus, WarningFilled, View },
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
      dialogDeleteVisible: false,
      deleteTarget: { id: null, title: '' },
      statusLoading: {}, // 控制开关加载状态
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
      }).catch(() => {
        // 全局拦截器已处理
      }).finally(() => {
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
        this.$message.error('只能上传 JPG/PNG 格式的图片')
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
        this.$message.success('图片上传成功')
      } else {
        this.$message.error('上传失败，未获取到图片地址')
      }
    },
    removeImage() {
      this.uploadPoster.url = ''
    },
    previewImage() {
      // 点击预览大图会自动触发el-image的预览功能
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
        this.$message.success('海报上传成功')
        this.dialogFormVisible = false
        this.uploadPoster = { url: '', title: '', status: true, createAt: '' }
        this.loadPosters()
      }).catch(() => {
        // 全局拦截器已处理
      })
    },
    deleteAllPoster() {
      if (!this.posterList.length) {
        this.$message.info('没有可删除的海报')
        return
      }
      this.dialogDeleteAllVisible = true
    },
    confirmDeleteAllPoster() {
      DeleteAllPoster().then(res => {
        if (!res?.success) return
        this.$message.success('已删除所有海报')
        this.dialogDeleteAllVisible = false
        this.loadPosters()
      }).catch(() => {
        // 全局拦截器已处理
      })
    },
    async changePosterStatus(poster, newStatus) {
      if (this.statusLoading[poster.id]) return

      this.statusLoading[poster.id] = true
      try {
        const updated = { ...poster, status: newStatus }
        const res = await UpdatePoster(updated)

        if (res?.success) {
          this.$message.success(`海报已${newStatus ? '上架' : '下架'}`)
        } else {
          // 回滚状态
          poster.status = !newStatus
          this.$message.error(res?.message || '状态更新失败')
        }
      } catch (error) {
        // 回滚状态
        poster.status = !newStatus
        this.$message.error('网络错误，状态更新失败')
      } finally {
        this.statusLoading[poster.id] = false
      }
    },
    openDeleteDialog(index, row) {
      this.deleteTarget = {
        id: row.id,
        title: row.title
      }
      this.dialogDeleteVisible = true
    },
    async confirmDeletePoster() {
      try {
        const res = await DeletePosterById(this.deleteTarget.id)
        if (!res?.success) return

        const targetIndex = this.posterList.findIndex(item => item.id === this.deleteTarget.id)
        if (targetIndex !== -1) this.posterList.splice(targetIndex, 1)

        if (this.paginatedList.length === 0 && this.currentPage > 1) {
          this.currentPage--
        }

        this.dialogDeleteVisible = false
        this.$message.success('海报删除成功')
      } catch (error) {
        // 全局拦截器已处理
      }
    }
  }
}
</script>

<style scoped>
/* 整体布局 - 统一风格 */
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

/* 搜索区整体容器 - 统一风格 */
.table-header-actions {
  padding: 10px 20px;
  border-bottom: 1px solid #e2e8f0;
  background: #ffffff;
  border-radius: 18px 18px 0 0;
}

/* 搜索区域主容器 - 左右分栏 */
.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

/* 左侧搜索输入区域 */
.search-filters {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  flex: 1;
  min-width: 0;
}

/* 右侧操作按钮区域 */
.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

/* 基础按钮样式 - 统一风格 */
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

/* 按钮图标样式 */
.btn-icon {
  font-size: 16px;
  flex-shrink: 0;
}

/* 主要按钮样式 */
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

.btn-primary:active {
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.3);
}

/* 次要按钮样式 */
.btn-secondary {
  background: #f8fafc;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f1f5f9;
  color: #334155;
  border-color: #cbd5e1;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.btn-secondary:active {
  transform: translateY(0);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

/* 新增按钮特殊样式 */
.btn-create {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.25);
}

.btn-create:hover {
  background: linear-gradient(135deg, #059669 0%, #10b981 100%);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.35);
}

/* 删除所有按钮特殊样式 */
.btn-delete-all {
  color: #ef4444;
  border-color: #fecaca;
}

.btn-delete-all:hover {
  background: #fef2f2;
  border-color: #fca5a5;
  color: #dc2626;
}

/* 轮播图卡片 */
.poster-carousel {
  flex-shrink: 0;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  position: relative;
}

.carousel-item-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24px 32px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.carousel-title {
  font-size: 24px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.carousel-status-tag {
  font-size: 14px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 8px;
}

.empty-carousel {
  flex-shrink: 0;
  background: #ffffff;
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
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

/* 表格海报缩略图 */
.table-poster-thumb {
  width: 120px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.table-poster-thumb:hover {
  transform: scale(1.05);
}

/* 预览链接 */
.preview-link {
  color: #3b82f6;
  text-decoration: none;
  word-break: break-all;
  transition: color 0.2s ease;
}

.preview-link:hover {
  color: #2563eb;
  text-decoration: underline;
}

/* 上传组件拖拽区 */
.poster-upload :deep(.el-upload-dragger) {
  width: 100%;
  border-radius: 14px;
  border: 1px dashed #cbd5e1;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  padding: 30px 20px;
  transition: all 0.2s ease;
}

.poster-upload :deep(.el-upload-dragger:hover) {
  border-color: #409eff;
  background: #f0f7ff;
}

.upload-preview {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.preview-thumb {
  width: 160px;
  height: 90px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
}

.preview-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
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

/* 弹窗样式 - 统一风格 */
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

/* 删除对话框样式 */
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

/* 操作按钮组 */
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.action-buttons :deep(.el-button) {
  border-radius: 10px;
  min-width: 76px;
  height: 34px;
  font-size: 13px;
  flex: 0 0 auto;
}

/* 输入框样式 - 统一风格 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  border-color: #93c5fd;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.full-width-input {
  width: 100%;
}

/* 按钮内图标和文字对齐 */
.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}
</style>
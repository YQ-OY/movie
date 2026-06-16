<template>
  <div class="film-list">
    <!-- 搜索区 - 统一风格 -->
    <div class="page-header">
      <div class="search-container">
        <!-- 左侧：搜索筛选区域 -->
        <div class="search-filters">
          <el-input v-model="searchKeyword" clearable placeholder="用户名 / 部门" class="search-item search-item--name"
            @keyup.enter="handleSearch" />
        </div>

        <!-- 右侧：操作按钮区域 -->
        <div class="search-actions">
          <el-button type="primary" class="btn-primary btn-search" @click="handleSearch">
            <el-icon class="btn-icon">
              <Search />
            </el-icon>
            <span>搜索</span>
          </el-button>
          <el-button class="btn-secondary btn-reset" @click="handleResetSearch">
            <el-icon class="btn-icon">
              <Refresh />
            </el-icon>
            <span>重置</span>
          </el-button>
          <div class="btn-divider"></div>
          <el-button type="primary" class="btn-primary btn-create" @click="openCreateDialog">
            <el-icon class="btn-icon">
              <Plus />
            </el-icon>
            <span>新增员工</span>
          </el-button>
          <el-button class="btn-secondary btn-export" @click="exportWorkers">
            <el-icon class="btn-icon">
              <Download />
            </el-icon>
            <span>导出报表</span>
          </el-button>


        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="workerList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <el-table-column label="员工 ID" min-width="200" show-overflow-tooltip prop="id" />
        <el-table-column label="用户名" min-width="120" prop="username" />
        <el-table-column label="所在部门" min-width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.department" type="primary" effect="light" :disable-transitions="true">
              {{ scope.row.department }}
            </el-tag>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="联系电话" min-width="140" prop="phone" />
        <el-table-column label="入职时间" min-width="180" prop="createAt" />
        <el-table-column label="状态" min-width="140" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.entry" active-text="在职" inactive-text="离职"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="openEditDialog(scope.row)">
                <el-icon>
                  <Edit />
                </el-icon>
                <span>编辑</span>
              </el-button>
              <el-button v-if="!scope.row.entry" size="small" type="danger" plain @click="handleDeleteWork(scope.row)">
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
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" :pager-count="5"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 新增/编辑员工信息对话框 -->
    <el-dialog :title="workerDialogMode === 'create' ? '新增员工' : '编辑员工信息'" v-model="workerDialogVisible" width="72%"
      align-center class="film-dialog film-dialog--edit">
      <el-form :model="editForm" label-width="92px" class="film-dialog__form">
        <div class="film-edit-layout--two-col">
          <!-- 左侧：头像区域 -->
          <div class="film-cover-panel">
            <div class="film-cover-panel__title">员工头像</div>
            <el-upload v-if="!avatarPreview" class="film-cover-uploader" accept=".png,.jpg,.jpeg" :headers="header"
              :action="uploadAction" :on-success="handleAvatarUploadSuccess" :show-file-list="false"
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
              <el-image class="film-cover-box__image" :src="avatarPreview" fit="cover"
                :preview-src-list="[avatarPreview]" preview-teleported />
              <el-button class="film-cover-box__delete" circle size="small" type="danger" @click.stop="removeAvatar">
                <el-icon>
                  <Delete />
                </el-icon>
              </el-button>
            </div>
          </div>

          <!-- 右侧：表单 + 额外操作 -->
          <div class="film-form-panel">
            <div class="film-form-grid">
              <el-form-item label="用户名">
                <el-input v-model="editForm.username" placeholder="用户名" :disabled="workerDialogMode === 'edit'" />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="editForm.nickname" placeholder="昵称" />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="editForm.phone" placeholder="联系电话" maxlength="11" show-word-limit />
              </el-form-item>
              <el-form-item label="所在部门">
                <el-input v-model="editForm.department" placeholder="部门名称" />
              </el-form-item>
              <el-form-item label="性别">
                <el-select v-model="editForm.gender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                  <el-option label="保密" value="保密" />
                </el-select>
              </el-form-item>
              <el-form-item label="入职时间">
                <el-date-picker v-model="editForm.createAt" type="date" value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="入职时间" disabled />
              </el-form-item>
            </div>

            <!-- 额外操作区域 -->
            <div class="edit-extra-actions">
              <el-button link type="primary" @click="openPasswordModal">
                <el-icon>
                  <Lock />
                </el-icon>
                <span>修改密码</span>
              </el-button>
              <el-button v-if="isAdmin && editForm.entry" link type="info" @click="openRoleDialog(editForm)">
                <el-icon>
                  <Setting />
                </el-icon>
                <span>权限管理</span>
              </el-button>
            </div>
          </div>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="workerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="workerDialogMode === 'create' ? submitCreate() : submitEdit()">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" v-model="passwordDialogVisible" width="420px" align-center class="film-dialog">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="至少6位" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPassword">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 员工权限对话框 -->
    <el-dialog title="员工权限" v-model="roleDialogVisible" width="60%" align-center class="film-dialog film-dialog--role">
      <div class="role-tip">
        每名员工默认拥有 <strong>ROLE_WORKER</strong>（基础权限，不可删除）
        <el-button v-if="isAdmin" type="primary" size="small" plain style="float: right;"
          @click="openAddRoleFromRoleDialog">
          <el-icon>
            <Plus />
          </el-icon>
          <span>添加权限</span>
        </el-button>
      </div>

      <el-table :data="roleList" class="film-table role-table" stripe style="width: 100%"
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '50px' }"
        :row-style="{ height: '60px' }">
        <el-table-column property="value" label="权限" min-width="280" align="center">
          <template #default="scope">
            <span>{{ scope.row.value }}</span>
            <el-tag v-if="scope.row.value === BASE_WORKER_ROLE" type="info" size="small" class="role-tag"
              effect="light">
              基础
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="createAt" label="创建时间" min-width="200" align="center" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-popconfirm v-if="scope.row.value !== BASE_WORKER_ROLE"
              @confirm="handleDeleteRole(scope.$index, scope.row.id)" title="确定删除此权限吗？">
              <template #reference>
                <el-button size="small" type="danger" plain>
                  <el-icon>
                    <Delete />
                  </el-icon>
                  <span>删除</span>
                </el-button>
              </template>
            </el-popconfirm>
            <span v-else class="role-fixed">—</span>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加权限对话框 -->
    <el-dialog title="新增权限" v-model="addRoleDialogVisible" width="420px" align-center class="film-dialog">
      <el-form :model="addRoleForm" label-width="100px">
        <el-form-item label="权限列表">
          <el-select v-model="addRoleForm.value" placeholder="请选择新增的权限" style="width: 100%" clearable>
            <el-option v-for="item in systemRoles" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addRoleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddRole">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  CreateWorker,
  listWorkerPage,
  CreateWorkerRole,
  DeleteRoleByWorkerId,
  DeleteWorker,
  FindWorkerRoles,
  ListSystemRoles,
  UpdateWorker
} from "@/api/worker";
import {
  Edit, Delete, Search, Refresh, Upload, Lock, Setting,
  Download, Plus, View
} from '@element-plus/icons-vue'
import { downloadCsv } from '@/utils/exportCsv'
import config from "@/config";
import { isValidMobileCN } from '@/utils/validate'

const BASE_WORKER_ROLE = 'ROLE_WORKER'

export default {
  name: 'WorkerManage',
  components: {
    Edit, Delete, Search, Refresh, Upload, Lock, Setting,
    Download, Plus, View
  },
  data() {
    return {
      BASE_WORKER_ROLE,
      loading: false,
      workerList: [],
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      workerDialogVisible: false,
      workerDialogMode: 'create',
      editForm: {},
      avatarPreview: '',
      header: { Authorization: localStorage.getItem('token') },
      uploadAction: config.API_URL + '/upload',
      passwordDialogVisible: false,
      passwordForm: { wid: '', newPassword: '' },
      roleDialogVisible: false,
      roleList: [],
      addRoleDialogVisible: false,
      addRoleForm: { wid: '', value: '' },
      systemRoles: [],
      currentWorkerForRole: null,
    }
  },
  computed: {
    isAdmin() {
      return localStorage.getItem('role') === 'admin'
    }
  },
  mounted() {
    this.loadWorkers()
    if (this.isAdmin) {
      ListSystemRoles().then(res => {
        this.systemRoles = Array.isArray(res?.data) ? res.data : []
      })
    }
  },
  methods: {
    async loadWorkers() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword?.trim() || null,
        }
        const res = await listWorkerPage(params)
        if (!res?.success) return
        this.workerList = res.data.rows || []
        this.totalCount = res.data.total || 0
        this.currentPage = res.data.page || this.currentPage
      } catch {
        // 全局 request 拦截器已提示
      } finally {
        this.loading = false
      }
    },
    exportWorkers() {
      const rows = (this.workerList || []).map((item) => ([
        item.id ?? '—',
        item.username ?? '—',
        item.nickname ?? '—',
        item.department ?? '—',
        item.phone ?? '—',
        item.gender ?? '—',
        item.entry ? '在职' : '离职',
        item.createAt ?? '—',
      ]))
      downloadCsv(
        `员工列表_${new Date().toISOString().slice(0, 10)}.csv`,
        ['ID', '用户名', '昵称', '部门', '电话', '性别', '状态', '入职时间'],
        rows
      )
    },
    handleSearch() {
      this.currentPage = 1
      this.loadWorkers()
    },
    handleResetSearch() {
      this.searchKeyword = ''
      this.currentPage = 1
      this.loadWorkers()
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
      this.loadWorkers()
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadWorkers()
    },
    async handleStatusChange(worker) {
      try {
        const res = await UpdateWorker(worker)
        if (!res?.success) return
        this.$message.success('状态已更新')
        this.loadWorkers()
      } catch {
        // 全局 request 拦截器已提示
      }
    },
    openCreateDialog() {
      this.workerDialogMode = 'create'
      this.editForm = {
        id: '',
        username: '',
        password: '',
        nickname: '',
        gender: '保密',
        phone: '',
        avatar: '',
        entry: true,
        department: '',
        createAt: '',
        updateAt: '',
      }
      this.avatarPreview = ''
      this.workerDialogVisible = true
    },
    openEditDialog(worker) {
      this.workerDialogMode = 'edit'
      this.editForm = { ...worker }
      this.avatarPreview = worker.avatar || ''
      this.workerDialogVisible = true
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
    handleAvatarUploadSuccess(res) {
      const url = typeof res === 'string' ? res : (res?.data || '')
      if (url) {
        this.editForm.avatar = url
        this.avatarPreview = url
      }
    },
    removeAvatar() {
      this.editForm.avatar = ''
      this.avatarPreview = ''
    },
    async submitEdit() {
      if (!isValidMobileCN(this.editForm.phone)) {
        this.$message.warning('请输入11位有效中国大陆手机号')
        return
      }
      try {
        const res = await UpdateWorker(this.editForm)
        if (!res?.success) return
        this.$message.success('信息修改成功')
        this.workerDialogVisible = false
        this.loadWorkers()
      } catch {
        // 全局 request 拦截器已提示
      }
    },
    async submitCreate() {
      if (!this.editForm.username) {
        this.$message.warning('请输入用户名')
        return
      }
      if (!isValidMobileCN(this.editForm.phone)) {
        this.$message.warning('请输入11位有效中国大陆手机号')
        return
      }
      if (!this.editForm.password || this.editForm.password.length < 6) {
        this.$message.warning('密码长度至少6位')
        return
      }
      try {
        const res = await CreateWorker(this.editForm)
        if (!res?.success) return
        this.$message.success('员工创建成功')
        this.workerDialogVisible = false
        this.loadWorkers()
      } catch {
        // 全局 request 拦截器已提示
      }
    },
    openPasswordModal() {
      if (!this.editForm.id) return
      this.passwordForm = { wid: this.editForm.id, newPassword: '' }
      this.passwordDialogVisible = true
    },
    async submitPassword() {
      if (!this.passwordForm.newPassword || this.passwordForm.newPassword.length < 6) {
        this.$message.warning('密码长度至少6位')
        return
      }
      const updated = { ...this.editForm, password: this.passwordForm.newPassword }
      try {
        const res = await UpdateWorker(updated)
        if (!res?.success) return
        this.$message.success('密码修改成功')
        this.passwordDialogVisible = false
        this.loadWorkers()
      } catch {
        // 全局 request 拦截器已提示
      }
    },
    async openRoleDialog(worker) {
      if (!this.isAdmin) {
        this.$message.warning('仅系统管理员可查看员工权限')
        return
      }
      this.currentWorkerForRole = worker
      this.roleDialogVisible = true
      const res = await FindWorkerRoles(worker.id)
      this.roleList = Array.isArray(res?.data) ? res.data : []
    },
    async handleDeleteRole(index, roleId) {
      const res = await DeleteRoleByWorkerId(roleId)
      if (res.success) {
        this.roleList.splice(index, 1)
        this.$message.success('权限已删除')
      }
    },
    handleDeleteWork(worker) {
      this.$confirm(`确定删除员工 ${worker.username} 吗？`, '提示', { type: 'warning' })
        .then(async () => {
          const res = await DeleteWorker(worker.id)
          if (!res?.success) return
          this.$message.success('删除成功')
          if (this.workerList.length === 1 && this.currentPage > 1) {
            this.currentPage--
          }
          this.loadWorkers()
        })
        .catch(() => { })
    },
    openAddRoleFromRoleDialog() {
      if (!this.currentWorkerForRole) return
      this.addRoleForm = {
        wid: this.currentWorkerForRole.id,
        value: ''
      }
      this.addRoleDialogVisible = true
    },
    async submitAddRole() {
      if (!this.addRoleForm.value) {
        this.$message.warning('请选择权限')
        return
      }
      try {
        const res = await CreateWorkerRole(this.addRoleForm)
        if (!res?.success) return
        this.$message.success('权限添加成功')
        this.addRoleDialogVisible = false
        if (this.roleDialogVisible && this.currentWorkerForRole && this.currentWorkerForRole.id === this.addRoleForm.wid) {
          const roleRes = await FindWorkerRoles(this.currentWorkerForRole.id)
          this.roleList = Array.isArray(roleRes?.data) ? roleRes.data : []
        }
      } catch {
        // 全局 request 拦截器已提示
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
.page-header {
  margin-bottom: 0;
  padding: 20px 24px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
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

/* 按钮分隔线 */
.btn-divider {
  width: 1px;
  height: 32px;
  background-color: #e2e8f0;
  margin: 0 4px;
}

/* 搜索项样式统一 */
.search-item {
  height: 40px;
}

.search-item--name {
  width: 280px;
}

/* 输入框和选择框样式优化 - 统一风格 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #93c5fd;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
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

/* 表格卡片 */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 18px 18px 0;
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
  min-width: 72px;
  height: 34px;
  font-size: 13px;
  flex: 0 0 auto;
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

.pagination-container :deep(.el-pagination__total),
.pagination-container :deep(.el-pagination__sizes),
.pagination-container :deep(.el-pagination__jump) {
  margin: 0;
  color: #64748b;
}

.pagination-container :deep(.el-pager li),
.pagination-container :deep(.btn-prev),
.pagination-container :deep(.btn-next) {
  border-radius: 8px;
  min-width: 32px;
  margin: 0 3px;
}

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

/* 对话框通用样式 */
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

/* 编辑对话框布局 */
.film-edit-layout--two-col {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 24px;
}

.film-cover-panel,
.film-form-panel {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.film-cover-panel {
  padding: 20px;
}

.film-cover-panel__title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 16px;
}

.film-cover-uploader {
  display: block;
  width: 100%;
}

.film-cover-box {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 16px;
  overflow: hidden;
  border: 2px dashed #cbd5e1;
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.film-cover-box--empty {
  cursor: pointer;
}

.film-cover-box--empty:hover {
  border-color: #93c5fd;
  background: linear-gradient(180deg, #f0f7ff 0%, #ffffff 100%);
}

.film-cover-box--preview {
  border-style: solid;
  border-color: #e2e8f0;
}

.film-cover-box__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.film-cover-box__delete {
  position: absolute;
  top: 12px;
  right: 12px;
  opacity: 0;
  transition: all 0.2s ease;
}

.film-cover-box:hover .film-cover-box__delete {
  opacity: 1;
}

.film-cover-box__placeholder {
  text-align: center;
  padding: 20px;
  color: #64748b;
}

.film-cover-box__icon {
  font-size: 36px;
  color: #3b82f6;
}

.film-cover-box__text {
  margin-top: 12px;
  font-size: 14px;
  font-weight: 600;
}

.film-cover-box__hint {
  margin-top: 8px;
  font-size: 12px;
  color: #94a3b8;
}

.film-form-panel {
  padding: 20px 24px 4px;
}

.film-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 20px;
}

.edit-extra-actions {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 24px;
}

/* 权限对话框样式 */
.role-tip {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 16px;
  line-height: 1.5;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 10px;
  border-left: 4px solid #409eff;
}

.role-tag {
  margin-left: 12px;
  vertical-align: middle;
}

.role-fixed {
  color: #94a3b8;
  font-size: 13px;
}

/* 按钮内图标和文字对齐 */
.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .search-container {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .search-filters {
    justify-content: center;
  }

  .search-actions {
    justify-content: center;
  }

  .film-edit-layout--two-col {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 16px;
  }

  .search-item--name {
    width: 240px;
  }

  .btn-divider {
    display: none;
  }

  .film-form-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .film-list {
    padding: 12px;
  }

  .search-actions {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .el-button {
    width: 100%;
    padding: 0 12px;
    font-size: 13px;
  }

  .film-cover-panel,
  .film-form-panel {
    padding: 16px;
  }
}

/* 修复权限对话框表格样式 */
.role-table {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.role-table :deep(.el-table__header-wrapper th) {
  border-bottom: 1px solid #e2e8f0;
}

.role-table :deep(.el-table__body tr:last-child td) {
  border-bottom: none;
}

.role-table :deep(.cell) {
  text-align: center;
}

.role-table :deep(.el-button) {
  border-radius: 8px;
  min-width: 64px;
  height: 28px;
  font-size: 12px;
  padding: 0 10px;
}

/* 优化提示条样式 */
.role-tip {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 20px;
  line-height: 1.5;
  padding: 14px 18px;
  background: #f8fafc;
  border-radius: 12px;
  border-left: 4px solid #409eff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.role-tip strong {
  color: #334155;
  font-weight: 700;
}

.role-tag {
  margin-left: 12px;
  vertical-align: middle;
}

.role-fixed {
  color: #94a3b8;
  font-size: 13px;
}
</style>
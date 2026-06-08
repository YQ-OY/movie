<template>
  <div class="film-list">

    <!-- 搜索区 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">员工管理</div>
          <div class="page-subtitle">支持按用户名 / 部门搜索，管理员工信息与权限</div>
        </div>

        <div class="search-form">
          <el-input v-model="searchKeyword" clearable placeholder="用户名 / 部门" class="search-item search-item--name"
            @keyup.enter="handleSearch" />
          <el-button type="primary" class="search-submit-btn" @click="handleSearch">
            <el-icon>
              <Search />
            </el-icon>
            <span>搜索</span>
          </el-button>
          <el-button class="search-reset-btn" @click="handleResetSearch">
            <el-icon>
              <Refresh />
            </el-icon>
            <span>重置</span>
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
        <el-table-column label="用户名" min-width="80" prop="username" />
        <el-table-column label="所在部门" min-width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.department" type="primary" effect="plain">{{ scope.row.department }}</el-tag>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="联系电话" min-width="80" prop="phone" />
        <el-table-column label="入职时间" min-width="100" prop="createAt" />
        <el-table-column label="状态" min-width="100" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.entry" active-text="在职" inactive-text="离职"
              @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="openEditDialog(scope.row)">
                <el-icon>
                  <Edit />
                </el-icon>编辑
              </el-button>
              <el-button v-if="!scope.row.entry" size="small" type="danger" plain @click="handleDeleteWork(scope.row)">
                <el-icon>
                  <Delete />
                </el-icon>删除
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

    <!-- 编辑员工信息对话框（仿 List.vue 两列布局） -->
    <el-dialog title="编辑员工信息" v-model="dialogEditVisible" width="72%" align-center
      class="film-dialog film-dialog--edit">
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
              <el-image class="film-cover-box__image" :src="avatarPreview" fit="cover" />
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
                <el-input v-model="editForm.username" placeholder="用户名" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="editForm.nickname" placeholder="昵称" />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="editForm.phone" placeholder="联系电话" />
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
                </el-icon>修改密码
              </el-button>
              <el-button v-if="isAdmin && editForm.entry" link type="info" @click="openRoleDialog(editForm)">
                <el-icon>
                  <Setting />
                </el-icon>权限管理
              </el-button>
            </div>
          </div>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogEditVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框（简单弹窗） -->
    <el-dialog title="修改密码" v-model="passwordDialogVisible" width="30%" align-center class="film-dialog">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="至少6位" />
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
    <el-dialog title="员工权限" v-model="roleDialogVisible" width="50%" align-center class="film-dialog">
      <div class="role-tip">
        每名员工默认拥有 <strong>ROLE_WORKER</strong>（基础权限，不可删除）
        <el-button v-if="isAdmin" type="primary" size="small" plain style="float: right; margin-top: -2px;"
          @click="openAddRoleFromRoleDialog">
          <el-icon>
            <Plus />
          </el-icon>添加权限
        </el-button>
      </div>
      <el-table :data="roleList" class="film-table" stripe>
        <el-table-column property="value" label="权限" min-width="280">
          <template #default="scope">
            <span>{{ scope.row.value }}</span>
            <el-tag v-if="scope.row.value === BASE_WORKER_ROLE" type="info" size="small" class="role-tag">基础</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="createAt" label="创建时间" min-width="200" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-popconfirm v-if="scope.row.value !== BASE_WORKER_ROLE"
              @confirm="handleDeleteRole(scope.$index, scope.row.id)" title="确定删除此权限吗？">
              <template #reference>
                <el-button size="small" type="danger" plain :icon="Delete">删除</el-button>
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

    <!-- 添加权限对话框（从权限管理中的添加按钮触发） -->
    <el-dialog title="新增权限" v-model="addRoleDialogVisible" width="30%" align-center class="film-dialog">
      <el-form :model="addRoleForm" label-width="100px">
        <el-form-item label="权限列表">
          <el-select v-model="addRoleForm.value" placeholder="请选择新增的权限" class="full-width-input">
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
  listWorkerPage,
  CreateWorkerRole,
  DeleteRoleByWorkerId,
  DeleteWorker,
  FindAllWorker,
  FindWorkerRoles,
  ListSystemRoles,
  UpdateWorker
} from "@/api/worker";
import { Edit, Delete, Search, Refresh, Upload, Lock, Setting } from '@element-plus/icons-vue'
import config from "@/config";

const BASE_WORKER_ROLE = 'ROLE_WORKER'

export default {
  name: 'WorkerManage',
  components: { Edit, Delete, Search, Refresh, Upload, Lock, Setting },
  data() {
    return {
      BASE_WORKER_ROLE,
      loading: false,
      workerList: [],
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      dialogEditVisible: false,
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
          keyword: this.searchKeyword || null,
        }
        const res = await listWorkerPage(params)
        if (res.success) {
          this.workerList = res.data.rows || []
          this.totalCount = res.data.total || 0
          this.currentPage = res.data.page || this.currentPage
        } else {
          this.$message.error(res.msg || '加载失败')
        }
      } catch (error) {
        this.$message.error('加载员工列表失败')
      } finally {
        this.loading = false
      }
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

    // 状态切换（使用el-switch时触发）
    async handleStatusChange(worker) {
      try {
        await UpdateWorker(worker)
        this.$message.success('状态已更新')
        // 刷新当前页
        this.loadWorkers()
      } catch {
        this.$message.error('更新失败')
      }
    },
    // 打开编辑对话框
    openEditDialog(worker) {
      this.editForm = { ...worker }
      this.avatarPreview = worker.avatar || ''
      this.dialogEditVisible = true
    },
    // 头像上传
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
    // 编辑提交后刷新
    async submitEdit() {
      try {
        await UpdateWorker(this.editForm)
        this.$message.success('信息修改成功')
        this.dialogEditVisible = false
        this.loadWorkers()
      } catch {
        this.$message.error('修改失败')
      }
    },
    // 修改密码相关
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
        await UpdateWorker(updated)
        this.$message.success('密码修改成功')
        this.passwordDialogVisible = false
        this.loadWorkers()
      } catch {
        this.$message.error('修改失败')
      }
    },
    // 权限管理（查看权限列表）
    async openRoleDialog(worker) {
      this.roleDialogVisible = true
      const res = await FindWorkerRoles(worker.id)
      this.roleList = Array.isArray(res?.data) ? res.data : []
    },
    // 删除权限
    async handleDeleteRole(index, roleId) {
      const res = await DeleteRoleByWorkerId(roleId)
      if (res.success) {
        this.roleList.splice(index, 1)
        this.$message.success('权限已删除')
      }
    },
    // 添加权限（在权限管理对话框中点击添加按钮时触发，这里我们单独做一个弹窗）
    // 但为了方便，我们在权限管理对话框中增加一个“添加权限”按钮，用户要求合并到编辑中，我们也可以放在编辑对话框的额外操作里
    // 为了完整体验，提供一个快捷添加方式：在权限管理对话框中添加一个按钮
    // 我们在编辑对话框的额外操作中已经有一个“权限管理”按钮，点击后会打开权限对话框，在权限对话框内再添加一个“新增权限”按钮。
    // 为简洁，下面补充一个方法供权限对话框调用。
    // 但因为模板中权限对话框没有添加按钮，我们在下面补充一个打开添加权限弹窗的方法。
    openAddRole(worker) {
      this.addRoleForm = { wid: worker.id, value: '' }
      this.addRoleDialogVisible = true
    },
    async submitAddRole() {
      if (!this.addRoleForm.value) {
        this.$message.warning('请选择权限')
        return
      }
      await CreateWorkerRole(this.addRoleForm)
      this.$message.success('权限添加成功')
      this.addRoleDialogVisible = false
      // 刷新权限列表
      if (this.editForm.id) {
        const res = await FindWorkerRoles(this.editForm.id)
        this.roleList = Array.isArray(res?.data) ? res.data : []
      }
    },
    // 删除员工
    handleDeleteWork(worker) {
      this.$confirm(`确定删除员工 ${worker.username} 吗？`, '提示', { type: 'warning' })
        .then(async () => {
          await DeleteWorker(worker.id)
          this.$message.success('删除成功')
          // 如果当前页只剩一条数据且不是第一页，则跳转到上一页
          if (this.workerList.length === 1 && this.currentPage > 1) {
            this.currentPage--
          }
          this.loadWorkers()
        })
        .catch(() => { })
    },
    // 从权限管理对话框打开添加权限弹窗
    openAddRoleFromRoleDialog() {
      // 注意：此时需要知道当前正在查看的员工 ID，我们可以在打开权限对话框时保存当前员工对象
      if (!this.currentWorkerForRole) return
      this.addRoleForm = {
        wid: this.currentWorkerForRole.id,
        value: ''
      }
      this.addRoleDialogVisible = true
    },

    // 修改原有的 openRoleDialog 方法，保存当前员工
    async openRoleDialog(worker) {
      if (!this.isAdmin) {
        this.$message.warning('仅系统管理员可查看员工权限')
        return
      }
      this.currentWorkerForRole = worker   // 保存当前员工
      this.roleDialogVisible = true
      const res = await FindWorkerRoles(worker.id)
      this.roleList = Array.isArray(res?.data) ? res.data : []
    },

    // 修改 submitAddRole，添加成功后刷新权限列表（如果添加的权限属于当前正在查看的员工）
    async submitAddRole() {
      if (!this.addRoleForm.value) {
        this.$message.warning('请选择权限')
        return
      }
      try {
        await CreateWorkerRole(this.addRoleForm)
        this.$message.success('权限添加成功')
        this.addRoleDialogVisible = false
        // 如果当前有打开的权限对话框，且添加的员工 ID 与当前显示的一致，则刷新权限列表
        if (this.roleDialogVisible && this.currentWorkerForRole && this.currentWorkerForRole.id === this.addRoleForm.wid) {
          const res = await FindWorkerRoles(this.currentWorkerForRole.id)
          this.roleList = Array.isArray(res?.data) ? res.data : []
        }
      } catch (error) {
        this.$message.error('添加失败')
      }
    }
  }
}
</script>

<style scoped>
/* 整体布局（完全复用电影管理页面样式） */
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
  gap: 10px;
  flex-wrap: wrap;
  flex: 1;
  justify-content: flex-end;
}

.search-item {
  width: 200px;
}

.search-item--name {
  width: 260px;
}

.search-submit-btn,
.search-reset-btn {
  border-radius: 6px;
}

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
  letter-spacing: 0.2px;
  text-align: center;
  height: 58px;
}

/* 单元格居中 */
.film-table :deep(.cell) {
  text-align: center;
}

.film-table :deep(.el-table__cell:first-child .cell) {
  text-align: left;
}

.film-table :deep(.el-table__expanded-cell .cell) {
  text-align: left;
}

/* 行悬浮效果 */
.film-table :deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

.film-table :deep(.el-table__body tr:hover > td) {
  background-color: rgba(148, 163, 184, 0.14) !important;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  border-radius: 10px;
  min-width: 74px;
  height: 32px;
  font-size: 12px;
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

/* 对话框样式（统一复用 film-dialog） */
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

.film-dialog :deep(.el-dialog__headerbtn) {
  top: 18px;
  right: 18px;
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

/* 权限对话框内的提示和标签 */
.role-tip {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 12px;
  line-height: 1.5;
}

.role-tag {
  margin-left: 8px;
  vertical-align: middle;
}

.role-fixed {
  color: #c0c4cc;
  font-size: 13px;
}

/* 通用辅助类 */
.full-width-input {
  width: 100%;
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


.film-edit-layout--two-col {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 18px;
}

.film-cover-panel,
.film-form-panel {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.film-cover-panel {
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

.film-cover-box__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.film-cover-box__delete {
  position: absolute;
  top: 10px;
  right: 10px;
  opacity: 0;
  transition: all 0.18s ease;
}

.film-cover-box:hover .film-cover-box__delete {
  opacity: 1;
  transform: scale(1);
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
}

.film-cover-box__hint {
  margin-top: 6px;
  font-size: 12px;
}

.film-form-panel {
  padding: 16px 18px 4px;
}

.film-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 2px 16px;
}

.edit-extra-actions {
  margin-top: 20px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 20px;
}

.action-buttons .el-button {
  border-radius: 10px;
  min-width: 74px;
  height: 32px;
  font-size: 12px;
}

.role-tip {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 12px;
}

.role-tag {
  margin-left: 8px;
}

.role-fixed {
  color: #c0c4cc;
}

.full-width-input {
  width: 100%;
}

/* 操作按钮组 - 完全对齐 List.vue */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.action-buttons .el-button {
  border-radius: 10px;
  min-width: 72px;
  height: 34px;
  font-size: 13px;
  padding: 0 12px;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}


/* 确保图标和文字间距 */
.action-buttons .el-button .el-icon {
  font-size: 16px;
  margin-right: 4px;
}
</style>
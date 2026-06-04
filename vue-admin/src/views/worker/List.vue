<template>
  <div class="main page-list">

    <el-table
        :data="workerList"
        style="width: 100%">
      <el-table-column type="expand" width="48">
        <template #default="props">
          <div class="expand-detail expand-detail--with-aside">
            <div class="expand-detail__aside">
              <div class="expand-detail__full-title">员工头像</div>
              <div class="expand-detail__avatar-col">
                <template v-if="isAvatarUrl(props.row.avatar)">
                  <el-image
                    class="expand-detail__avatar-img"
                    :src="props.row.avatar"
                    fit="cover"
                    :preview-src-list="[props.row.avatar]"
                    preview-teleported
                  />
                </template>
                <span v-else class="expand-detail__value expand-detail__value--muted">尚未上传头像</span>
              </div>
            </div>
            <div class="expand-detail__body">
              <div class="expand-detail__grid">
                <div class="expand-detail__item">
                  <span class="expand-detail__label">用户名</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.username) }}</span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">昵称</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.nickname) }}</span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">密码</span>
                  <span class="expand-detail__value expand-detail__value--inline">
                    <el-button link type="primary" @click="openUpdatePassword(props.row)">
                      <el-icon><Edit /></el-icon>编辑密码
                    </el-button>
                  </span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">联系电话</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.phone) }}</span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">性别</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.gender) }}</span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">入职部门</span>
                  <span class="expand-detail__value expand-detail__value--inline">
                    <el-tag v-if="props.row.department" type="primary" effect="plain" size="small">
                      {{ props.row.department }}
                    </el-tag>
                    <span v-else class="expand-detail__value--muted">—</span>
                  </span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">状态</span>
                  <span class="expand-detail__value expand-detail__value--inline">
                    <el-tag v-if="props.row.entry" type="success" size="small">在职</el-tag>
                    <el-tag v-else type="info" size="small">离职</el-tag>
                    <el-button
                      v-if="props.row.entry"
                      link
                      type="warning"
                      size="small"
                      @click="changeEntry(props.$index, props.row)"
                    >员工已离职？</el-button>
                  </span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">更新时间</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.updateAt) }}</span>
                </div>
              </div>
              <div class="expand-detail__actions">
              <el-button
                :disabled="!props.row.entry"
                type="info"
                size="small"
                plain
                @click="handleListRole(props.row)"
              >查看权限</el-button>
              <el-button
                :disabled="!props.row.entry"
                type="primary"
                size="small"
                plain
                @click="handleAddRole(props.row)"
              >添加权限</el-button>
              <el-button
                :disabled="props.row.entry"
                type="danger"
                size="small"
                plain
                @click="handleDeleteWork(props.$index, props.row.id)"
              >删除员工</el-button>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column
          label="员工 ID"
          min-width="220"
          show-overflow-tooltip
          prop="id">
      </el-table-column>
      <el-table-column
          label="用户名"
          width="200"
          prop="username">
      </el-table-column>
      <el-table-column
          label="所在部门"
          width="150">
        <template #default="scope">
          <el-tag>{{ scope.row.department }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          label="联系电话"
          width="240"
          prop="phone">
      </el-table-column>
      <el-table-column
          label="入职时间"
          prop="createAt">
      </el-table-column>

    </el-table>

    <el-dialog title="员工权限" v-model="roleDialogVisible">
      <p class="role-tip">每名员工默认拥有 <strong>ROLE_WORKER</strong>（基础权限，不可删除）；下表含库中记录与扩展权限。</p>
      <el-table v-loading="loading" :data="roleList">
        <el-table-column property="value" label="权限" width="280">
          <template #default="scope">
            <span>{{ scope.row.value }}</span>
            <el-tag v-if="scope.row.value === BASE_WORKER_ROLE" type="info" size="small" class="role-tag">基础</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="createAt" label="创建时间" width="300"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-popconfirm
                v-if="scope.row.value !== BASE_WORKER_ROLE"
                @confirm="handleDeleteRole(scope.$index, scope.row.id)"
                :title="'确定删除权限 < ' + scope.row.value +' > 吗？'"
            >
              <template #reference>
                <el-button size="small" type="danger" :icon="Delete" plain>删除</el-button>
              </template>
            </el-popconfirm>
            <span v-else class="role-fixed">—</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog width="450px" title="新增权限" v-model="dialogFormVisible">
      <el-form :model="addRoleForm">
        <el-form-item label="权限列表" label-width="120px">
          <el-select v-model="addRoleForm.value" placeholder="请选择新增的权限">
            <el-option v-for="(item, index) in systemRoles"
                       :key="index"
                       :label="item"
                       :value="item">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitAddRole">确 定</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import {
  CreateWorkerRole,
  DeleteRoleByWorkerId,
  DeleteWorker,
  FindAllWorker,
  FindWorkerRoles,
  ListSystemRoles,
  UpdateWorker
} from "@/api/worker";
import { Edit, Delete } from '@element-plus/icons-vue'

const BASE_WORKER_ROLE = 'ROLE_WORKER'

export default {
  components: { Edit, Delete },

  data() {
    return {
      BASE_WORKER_ROLE,
      loading: false,
      addRoleForm: {
        wid: '',
        value: '',
      },
      workerList: [],
      systemRoles: [],
      roleDialogVisible: false,
      roleList: [],
      dialogFormVisible: false,
    }
  },

  mounted() {
    FindAllWorker().then(res => {
      this.workerList = Array.isArray(res?.data) ? res.data : []
    })
    // /role/system 仅 ROLE_ADMIN 可访问；工作人员登录后台时不要请求，否则会 403 Access Denied
    if (localStorage.getItem('role') === 'admin') {
      ListSystemRoles().then(res => {
        this.systemRoles = Array.isArray(res?.data) ? res.data : []
      })
    }
  },

  methods: {
    formatCell(val) {
      if (val == null) return '—'
      const s = String(val).trim()
      if (s === '' || s === 'null') return '—'
      return s
    },

    isAvatarUrl(avatar) {
      if (avatar == null) return false
      const s = String(avatar).trim()
      if (s === '' || s === 'null') return false
      return /^https?:\/\//i.test(s) || s.startsWith('/')
    },

    openUpdatePassword(worker) {
      this.$prompt('请输入新的密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({value}) => {
        if (!value) {
          this.$message({
            message: '密码不能为空',
            type: 'danger',
          });
          return
        }
        if (value.length < 6) {
          this.$message({
            message: '请输入不少于6位的密码',
            type: 'danger',
          });
          return
        }
        worker.password = value
        UpdateWorker(worker).then(res => {
          this.$message({
            message: '账号密码修改成功',
            type: 'success',
          });
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        });
      });
    },

    changeEntry(index, worker) {
      this.$confirm('此操作将该员工状态永久修改为离职, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        worker.entry = false
        UpdateWorker(worker).then(res => {
          this.$message({
            type: 'success',
            message: '员工离职成功!'
          });
        })
      }).catch(() => {
        /* 取消或关闭弹窗时 confirm 会 reject('cancel')，须捕获以免 dev overlay 报错 */
      })
    },

    handleDeleteWork(index, id) {
      DeleteWorker(id).then(res => {
        this.workerList.splice(index, 1)
        this.$message({
          type: 'success',
          message: '员工信息删除成功!'
        });
      })
    },

    handleListRole(worker) {
      if (localStorage.getItem('role') !== 'admin') {
        this.$message.warning('仅系统管理员可查看员工权限')
        return
      }
      this.roleDialogVisible = true;
      this.loading = true;
      FindWorkerRoles(worker.id).then(res => {
        setTimeout(() => {
          this.roleList = Array.isArray(res?.data) ? res.data : []
          this.loading = false;
        }, 700)
      })
    },

    handleDeleteRole(index, id) {
      DeleteRoleByWorkerId(id).then(res => {
        if (res.success === true) {
          this.roleList.splice(index, 1)
          this.$message({
            type: 'success',
            message: '员工权限删除成功!'
          });
        }
      })
    },

    handleAddRole(worker) {
      if (localStorage.getItem('role') !== 'admin') {
        this.$message.warning('仅系统管理员可为员工添加权限')
        return
      }
      const open = () => {
        this.addRoleForm.wid = worker.id
        this.dialogFormVisible = true
      }
      if (this.systemRoles.length) {
        open()
        return
      }
      ListSystemRoles().then(res => {
        this.systemRoles = Array.isArray(res?.data) ? res.data : []
        open()
      })
    },

    submitAddRole() {
      CreateWorkerRole(this.addRoleForm).then(res => {
        if (res.success) {
          this.dialogFormVisible = false;
          this.addRoleForm.wid = '';
          this.addRoleForm.value = '';
          this.$message({
            type: 'success',
            message: '员工权限添加成功!'
          });
        }
      })
    },

  },

}
</script>

<style scoped>
.page-list {
  padding: 24px;
}

.role-tip {
  font-size: 13px;
  color: #606266;
  margin: 0 0 12px;
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
</style>
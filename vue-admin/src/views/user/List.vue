<template>
  <div class="main page-list">
    <el-table :data="userList" style="width: 100%">
      <el-table-column type="expand" width="48">
        <template #default="props">
          <div class="expand-detail expand-detail--with-aside">
            <div class="expand-detail__aside">
              <div class="expand-detail__full-title">用户头像</div>
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
                <span v-else class="expand-detail__value expand-detail__value--muted">未上传头像</span>
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
                  <span class="expand-detail__label">手机号</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.phone) }}</span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">性别</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.gender) }}</span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">生日</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.birthday) }}</span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">个人简介</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.info) }}</span>
                </div>
                <div class="expand-detail__item">
                  <span class="expand-detail__label">更新时间</span>
                  <span class="expand-detail__value">{{ formatCell(props.row.updateAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="用户 ID" min-width="220" show-overflow-tooltip prop="id" />
      <el-table-column label="用户名" width="200" prop="username" />
      <el-table-column label="昵称" width="200" prop="nickname" />
      <el-table-column label="注册时间" min-width="180" prop="createAt" />
    </el-table>
  </div>
</template>

<script>
import { FindAllUser, UpdateUser } from '@/api/user'
import { Edit } from '@element-plus/icons-vue'

export default {
  components: { Edit },

  data() {
    return {
      userList: [],
    }
  },

  mounted() {
    FindAllUser().then((res) => {
      this.userList = res.data
    })
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

    openUpdatePassword(user) {
      this.$prompt('请输入新的密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      })
        .then(({ value }) => {
          if (!value) {
            this.$message({
              type: 'danger',
              message: '密码不能为空',
            })
            return
          }
          if (value.length < 6) {
            this.$message({
              type: 'danger',
              message: '请输入不少于6位的密码',
            })
            return
          }
          user.password = value
          UpdateUser(user).then(() => {
            this.$message({
              message: '账号密码修改成功',
              type: 'success',
            })
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '取消修改',
          })
        })
    },
  },
}
</script>

<style scoped>
.page-list {
  padding: 24px;
}
</style>

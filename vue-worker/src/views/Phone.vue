<template>
  <div class="film-list">

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">活跃用户统计</div>
          <div class="page-subtitle">按留言活跃度排序的用户列表，支持查看用户信息</div>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="paginatedList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <el-table-column label="用户" min-width="220">
          <template #default="scope">
            <div class="user-cell">
              <el-image class="user-avatar" :src="scope.row.user.avatar" fit="cover">
                <template #error>
                  <div class="avatar-placeholder">?</div>
                </template>
              </el-image>
              <span class="user-name">{{ scope.row.user.nickname || '匿名用户' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="手机号" min-width="150" prop="user.phone" align="center">
          <template #default="scope">
            <span>{{ scope.row.user.phone || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="生日" min-width="150" prop="user.birthday" align="center">
          <template #default="scope">
            <span>{{ scope.row.user.birthday || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="性别" min-width="100" prop="user.gender" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.user.gender === '男'" type="primary" effect="plain" size="small">男</el-tag>
            <el-tag v-else-if="scope.row.user.gender === '女'" type="success" effect="plain" size="small">女</el-tag>
            <span v-else>—</span>
          </template>
        </el-table-column>

        <el-table-column label="活跃排名" min-width="140" align="center">
          <template #default="scope">
            <div class="rank-badge" :class="getRankClass(scope.$index)">
              <span class="rank-number">TOP {{ scope.$index + 1 }}</span>
              <span class="rank-stars">
                <el-icon v-for="i in getStarCount(scope.$index)" :key="i">
                  <StarFilled />
                </el-icon>
              </span>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="totalCount > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" />
      </div>
    </div>
  </div>
</template>

<script>
import { ListActiveUser } from "@/api/leaving";
import { StarFilled } from '@element-plus/icons-vue'

export default {
  name: 'ActiveUser',
  components: { StarFilled },
  data() {
    return {
      loading: false,
      allList: [],     // 全量数据（由后端排序）
      currentPage: 1,
      pageSize: 10,
    }
  },
  computed: {
    totalCount() {
      return this.allList.length
    },
    paginatedList() {
      const start = (this.currentPage - 1) * this.pageSize
      return this.allList.slice(start, start + this.pageSize)
    }
  },
  mounted() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      ListActiveUser()
        .then(res => {
          if (res.success) {
            this.allList = res.data || []
            this.currentPage = 1
          } else {
            this.$message.error(res.msg || '加载失败')
          }
        })
        .catch(() => {
          this.$message.error('加载活跃用户失败')
        })
        .finally(() => {
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
    getStarCount(index) {
      if (index === 0) return 3
      if (index === 1) return 2
      if (index === 2) return 1
      return 0
    },
    getRankClass(index) {
      if (index === 0) return 'rank--gold'
      if (index === 1) return 'rank--silver'
      if (index === 2) return 'rank--bronze'
      return 'rank--normal'
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

/* 用户信息单元格（头像 + 昵称） */
.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #eef2f8;
  background: #f1f5f9;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #94a3b8;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

/* 排名徽章样式 */
.rank-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 40px;
  font-weight: 600;
  font-size: 13px;
}

.rank--gold {
  background: linear-gradient(135deg, #fef3c7, #fffbeb);
  color: #d97706;
  border: 1px solid #fcd34d;
}

.rank--silver {
  background: linear-gradient(135deg, #f1f5f9, #ffffff);
  color: #475569;
  border: 1px solid #cbd5e1;
}

.rank--bronze {
  background: linear-gradient(135deg, #fef3c7, #fff7ed);
  color: #b45309;
  border: 1px solid #fed7aa;
}

.rank--normal {
  background: #f8fafc;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.rank-stars {
  display: inline-flex;
  gap: 2px;
  color: #fbbf24;
}

.rank-stars .el-icon {
  font-size: 14px;
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
</style>
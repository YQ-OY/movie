<template>
  <div class="active-user-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-row">
          <span class="title-icon">🔥</span>
          <h1 class="page-title">活跃用户统计</h1>
        </div>
        <p class="page-subtitle">按留言活跃度排序的用户列表，支持查看用户信息</p>
      </div>
      <!-- 装饰性统计小卡片 -->
      <div class="header-stats">
        <div class="mini-stat">
          <span class="mini-stat-value">{{ totalCount }}</span>
          <span class="mini-stat-label">活跃用户</span>
        </div>
        <div class="mini-stat">
          <span class="mini-stat-value">{{ topThreeCount }}</span>
          <span class="mini-stat-label">TOP 3</span>
        </div>
      </div>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="paginatedList" class="active-table" stripe :header-cell-style="{
        background: 'linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%)',
        color: '#1e293b',
        fontWeight: 700,
        fontSize: '14px',
        textAlign: 'center',
        height: '56px',
        borderBottom: '2px solid #e2e8f0'
      }" :row-style="{
          height: '80px',
          transition: 'all 0.2s ease'
        }" highlight-current-row @row-click="handleRowClick">
        <!-- 用户列 -->
        <el-table-column label="用户" min-width="220">
          <template #default="scope">
            <div class="user-cell">
              <div class="avatar-wrapper">
                <el-image class="user-avatar" :src="scope.row.user.avatar" fit="cover">
                  <template #error>
                    <div class="avatar-placeholder">
                      {{ (scope.row.user.nickname || '?')[0] }}
                    </div>
                  </template>
                </el-image>
                <div class="avatar-ring"></div>
              </div>
              <span class="user-name">{{ scope.row.user.nickname || '匿名用户' }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 手机号 -->
        <el-table-column label="手机号" min-width="150" prop="user.phone" align="center">
          <template #default="scope">
            <span class="info-text">{{ scope.row.user.phone || '—' }}</span>
          </template>
        </el-table-column>

        <!-- 生日 -->
        <el-table-column label="生日" min-width="150" prop="user.birthday" align="center">
          <template #default="scope">
            <span class="info-text">{{ scope.row.user.birthday || '—' }}</span>
          </template>
        </el-table-column>

        <!-- 性别 -->
        <el-table-column label="性别" min-width="110" prop="user.gender" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.user.gender === '男'" type="primary" effect="light" size="small" class="gender-tag">
              <el-icon class="gender-icon">
                <Male />
              </el-icon> 男
            </el-tag>
            <el-tag v-else-if="scope.row.user.gender === '女'" type="success" effect="light" size="small"
              class="gender-tag">
              <el-icon class="gender-icon">
                <Female />
              </el-icon> 女
            </el-tag>
            <span v-else class="info-text">—</span>
          </template>
        </el-table-column>

        <!-- 活跃排名 -->
        <el-table-column label="活跃排名" min-width="160" align="center">
          <template #default="scope">
            <div class="rank-badge" :class="getRankClass(scope.$index)">
              <span class="rank-number">
                <el-icon v-if="scope.$index === 0">
                  <TrophyBase />
                </el-icon>
                TOP {{ scope.$index + 1 }}
              </span>
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
          @current-change="handleCurrentChange" :pager-count="5" background />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ListActiveUser } from '@/api/leaving'
import { StarFilled, TrophyBase, Male, Female } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const allList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const totalCount = computed(() => allList.value.length)

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return allList.value.slice(start, start + pageSize.value)
})

const topThreeCount = computed(() => {
  return Math.min(3, allList.value.length)
})

// 方法
function load() {
  loading.value = true
  ListActiveUser()
    .then(res => {
      if (!res?.success) return
      allList.value = res.data || []
      currentPage.value = 1
    })
    .catch(() => { })
    .finally(() => {
      loading.value = false
    })
}

function handleSizeChange(newSize) {
  pageSize.value = newSize
  currentPage.value = 1
}

function handleCurrentChange(newPage) {
  currentPage.value = newPage
}

function getStarCount(index) {
  if (index === 0) return 3
  if (index === 1) return 2
  if (index === 2) return 1
  return 0
}

function getRankClass(index) {
  if (index === 0) return 'rank--gold'
  if (index === 1) return 'rank--silver'
  if (index === 2) return 'rank--bronze'
  return 'rank--normal'
}

// 行点击事件（可选，用于展示更详细的用户信息）
function handleRowClick(row) {
  // 可在此处添加交互逻辑
}

// 生命周期
onMounted(() => {
  load()
})
</script>

<style scoped>
/* ==================== 全局页面容器 ==================== */
.active-user-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 22px 24px;
  box-sizing: border-box;
  background: #f7f9fc;
  background-image:
    radial-gradient(ellipse at 15% 20%, rgba(59, 130, 246, 0.04) 0%, transparent 55%),
    radial-gradient(ellipse at 85% 75%, rgba(99, 102, 241, 0.03) 0%, transparent 55%);
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
}

/* ==================== 页面头部 ==================== */
.page-header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22px 28px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  box-shadow:
    0 8px 28px rgba(15, 23, 42, 0.05),
    0 2px 6px rgba(15, 23, 42, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.7);
  transition: box-shadow 0.3s ease;
}

.page-header:hover {
  box-shadow:
    0 12px 36px rgba(15, 23, 42, 0.08),
    0 4px 12px rgba(15, 23, 42, 0.03);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 28px;
  line-height: 1;
  filter: drop-shadow(0 4px 6px rgba(245, 158, 11, 0.3));
}

.page-title {
  margin: 0;
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.3px;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  font-weight: 450;
  padding-left: 38px;
  /* 与图标对齐 */
}

/* 头部迷你统计卡片 */
.header-stats {
  display: flex;
  gap: 14px;
}

.mini-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 12px 20px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.mini-stat:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.mini-stat-value {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1;
}

.mini-stat-label {
  font-size: 12px;
  font-weight: 500;
  color: #64748b;
  margin-top: 4px;
  letter-spacing: 0.3px;
}

/* ==================== 表格卡片 ==================== */
.table-card {
  flex: 1;
  min-height: 0;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border-radius: 22px;
  padding: 20px 22px 0;
  box-shadow:
    0 8px 28px rgba(15, 23, 42, 0.05),
    0 2px 6px rgba(15, 23, 42, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.7);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Element Plus 表格全局样式覆盖 */
.active-table {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  border-radius: 16px;
}

.active-table :deep(.el-table__header-wrapper th) {
  border-bottom: 2px solid #e2e8f0;
  letter-spacing: 0.3px;
  text-transform: uppercase;
  font-size: 13px;
}

/* 表头单元格微调 */
.active-table :deep(.el-table__header-wrapper .cell) {
  padding: 0 12px;
}

/* 行悬停效果 */
.active-table :deep(.el-table__body tr:hover > td) {
  background-color: rgba(241, 245, 249, 0.7) !important;
  transition: background-color 0.2s ease;
}

/* 斑马条纹微调 */
.active-table :deep(.el-table__body tr.el-table__row--striped td) {
  background-color: #fafbfd;
}

.active-table :deep(.el-table__body tr.el-table__row--striped:hover td) {
  background-color: rgba(241, 245, 249, 0.8) !important;
}

/* 单元格内边距与垂直居中 */
.active-table :deep(.el-table__cell .cell) {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 16px;
  text-align: center;
}

/* 第一列（用户列）内容左对齐 */
.active-table :deep(.el-table__cell:first-child .cell) {
  justify-content: flex-start;
  text-align: left;
}

/* 滚动条美化 */
.active-table :deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
  height: 6px;
}

.active-table :deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: transparent;
}

.active-table :deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #dce1e8;
  border-radius: 10px;
}

.active-table :deep(.el-table__body-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #bcc5d0;
}

/* ==================== 用户单元格 ==================== */
.user-cell {
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar-wrapper {
  position: relative;
  width: 44px;
  height: 44px;
  flex-shrink: 0;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ffffff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.06);
  display: block;
  background: #f1f5f9;
}

.avatar-ring {
  position: absolute;
  inset: -3px;
  border-radius: 50%;
  border: 2px solid transparent;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0.45;
  pointer-events: none;
}

.avatar-placeholder {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e2e8f0, #cbd5e1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  text-transform: uppercase;
  border: 2px solid #ffffff;
}

.user-name {
  font-size: 15px;
  font-weight: 650;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 普通信息文本 */
.info-text {
  color: #475569;
  font-size: 14px;
  font-weight: 500;
}

/* ==================== 性别标签 ==================== */
.gender-tag {
  font-weight: 600;
  border-radius: 20px;
  padding: 4px 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s ease;
  border: 1px solid transparent !important;
}

.gender-tag .gender-icon {
  font-size: 14px;
}

/* 男性标签 */
.gender-tag.el-tag--primary {
  background-color: #eff6ff !important;
  color: #2563eb !important;
  border-color: #bfdbfe !important;
}

/* 女性标签 */
.gender-tag.el-tag--success {
  background-color: #ecfdf5 !important;
  color: #059669 !important;
  border-color: #a7f3d0 !important;
}

/* ==================== 排名徽章 ==================== */
.rank-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 6px 18px;
  border-radius: 40px;
  font-weight: 700;
  font-size: 13px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  letter-spacing: 0.3px;
}

.rank-badge .el-icon {
  font-size: 16px;
}

/* 金、银、铜、普通样式 */
.rank--gold {
  background: linear-gradient(135deg, #fef3c7 0%, #fffbeb 100%);
  color: #b45309;
  border: 1px solid #fcd34d;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.2);
}

.rank--silver {
  background: linear-gradient(135deg, #f1f5f9 0%, #ffffff 100%);
  color: #334155;
  border: 1px solid #cbd5e1;
  box-shadow: 0 4px 10px rgba(148, 163, 184, 0.15);
}

.rank--bronze {
  background: linear-gradient(135deg, #fff7ed 0%, #fffbeb 100%);
  color: #c2410c;
  border: 1px solid #fed7aa;
  box-shadow: 0 4px 10px rgba(234, 88, 12, 0.15);
}

.rank--normal {
  background: #f8fafc;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

/* 悬停动效 */
.rank-badge:hover {
  transform: scale(1.04);
}

.rank-stars {
  display: inline-flex;
  gap: 2px;
  color: #fbbf24;
}

.rank-stars .el-icon {
  font-size: 14px;
  filter: drop-shadow(0 1px 2px rgba(245, 158, 11, 0.3));
}

/* ==================== 分页容器 ==================== */
.pagination-container {
  flex-shrink: 0;
  margin-top: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #e8edf2;
  padding: 16px 8px;
  background: transparent;
}

.pagination-container :deep(.el-pagination) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination-container :deep(.el-pagination__total),
.pagination-container :deep(.el-pagination__sizes),
.pagination-container :deep(.el-pagination__jump) {
  color: #475569;
  font-weight: 500;
}

.pagination-container :deep(.el-pager li),
.pagination-container :deep(.btn-prev),
.pagination-container :deep(.btn-next) {
  border-radius: 10px;
  min-width: 34px;
  height: 34px;
  line-height: 34px;
  margin: 0 3px;
  font-weight: 600;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #334155;
  transition: all 0.2s ease;
}

.pagination-container :deep(.el-pager li:hover),
.pagination-container :deep(.btn-prev:hover),
.pagination-container :deep(.btn-next:hover) {
  background: #f1f5f9;
  border-color: #94a3b8;
  color: #1e293b;
}

.pagination-container :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  color: #ffffff;
  border: none;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.35);
}

/* 分页选择器美化 */
.pagination-container :deep(.el-select .el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

/* ==================== 响应式适配 ==================== */
@media (max-width: 992px) {
  .active-user-page {
    padding: 16px;
    gap: 14px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 18px 20px;
  }

  .header-stats {
    width: 100%;
    justify-content: flex-start;
  }

  .page-title {
    font-size: 22px;
  }

  .table-card {
    padding: 14px 16px 0;
  }
}

@media (max-width: 768px) {
  .active-user-page {
    padding: 10px;
    gap: 10px;
  }

  .page-header {
    padding: 14px 16px;
    border-radius: 18px;
  }

  .title-icon {
    font-size: 24px;
  }

  .page-title {
    font-size: 20px;
  }

  .page-subtitle {
    padding-left: 32px;
  }

  .mini-stat {
    padding: 8px 14px;
  }

  .mini-stat-value {
    font-size: 18px;
  }

  .table-card {
    padding: 10px 8px 0;
    border-radius: 18px;
  }

  .rank-badge {
    padding: 4px 12px;
    font-size: 12px;
  }

  .pagination-container :deep(.el-pager li) {
    min-width: 28px;
    height: 28px;
    line-height: 28px;
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .header-stats {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
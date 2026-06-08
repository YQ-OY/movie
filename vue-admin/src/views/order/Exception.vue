<template>
  <div class="film-list">

    <!-- 搜索区 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">异常订单管理</div>
          <div class="page-subtitle">支持按订单 ID / 上报人搜索，快速处理异常订单</div>
        </div>

        <div class="search-form">
          <el-input v-model="searchKeyword" clearable placeholder="订单 ID / 上报人" class="search-item search-item--name"
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
      <el-table v-loading="loading" :data="orderList" class="film-table" stripe row-key="id"
        :expand-row-keys="expandedRows"
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <!-- 展开行：显示详细信息 -->
        <el-table-column type="expand" width="48">
          <template #default="props">
            <div class="expand-detail expand-detail--with-aside">
              <div class="expand-detail__body">
                <div class="expand-detail__grid">
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">订单 ID</span>
                    <span class="expand-detail__value">{{ props.row.oid }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">异常原因</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.reason) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">上报人</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.reviewer) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">处理结果</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.result) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">上报时间</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.createAt) }}</span>
                  </div>
                  <div class="expand-detail__item">
                    <span class="expand-detail__label">处理时间</span>
                    <span class="expand-detail__value">{{ formatCell(props.row.endAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="订单 ID" min-width="280" show-overflow-tooltip prop="oid" />
        <el-table-column label="上报人" min-width="140" prop="reviewer" />
        <el-table-column label="异常处理状态" min-width="140" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === true" type="success" effect="light">处理成功</el-tag>
            <el-tag v-else type="danger" effect="light">等待处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain :icon="CircleCheck"
                @click="openHandleDialog(scope.$index, scope.row)">
                处理异常
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

    <!-- 异常处理对话框（样式与电影对话框统一） -->
    <el-dialog title="异常处理结果" v-model="dialogFormVisible" width="40%" align-center
      class="film-dialog film-dialog--edit">
      <el-form :model="form" label-width="100px" class="film-dialog__form">
        <el-form-item label="处理结果">
          <el-input v-model="form.result" autocomplete="off" placeholder="请输入处理结果" />
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="form.status" placeholder="请选择处理状态">
            <el-option label="处理完成" :value="true" />
            <el-option label="还未完成" :value="false" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="submitHandle">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { listOrderExceptionPage, HandleOrderException } from "@/api/order";
import { CircleCheck, Search, Refresh } from '@element-plus/icons-vue'

export default {
  data() {
    return {
      loading: false,
      orderList: [],
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      dialogFormVisible: false,
      form: { id: null, status: false, result: '' },
    }
  },
  mounted() {
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword || null,
        }
        const res = await listOrderExceptionPage(params)
        console.log('res:', res);
        this.orderList = res.data.rows || []
        this.totalCount = res.data.total || 0
        this.currentPage = res.data.page || this.currentPage
      } catch (error) {
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadOrders()
    },
    handleResetSearch() {
      this.searchKeyword = ''
      this.currentPage = 1
      this.loadOrders()
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
      this.loadOrders()
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadOrders()
    },
    async submitHandle() {
      if (!this.form.result) {
        this.$message.warning('请填写处理结果')
        return
      }
      try {
        const res = await HandleOrderException(this.form)
        if (res.success) {
          this.$message.success('订单处理成功')
          this.dialogFormVisible = false
          this.loadOrders()   // 刷新列表
        }
      } catch (error) {
        this.$message.error('处理失败')
      }
    },
    formatCell(val) {
      if (val == null) return '—';
      const s = String(val).trim();
      if (s === '' || s === 'null') return '—';
      return s;
    },
  }
}
</script>

<style scoped>
/* ===== 整体布局（与电影管理页面完全一致） ===== */
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
  width: 240px;
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

/* 单元格居中（除第一列展开列外） */
.film-table :deep(.cell) {
  text-align: center;
}

.film-table :deep(.el-table__cell:first-child .cell) {
  text-align: left;
}

.film-table :deep(.el-table__expanded-cell .cell) {
  text-align: left;
}

/* 展开行内部样式（美化） */
.expand-detail--with-aside {
  padding: 16px 24px;
  background: #fbfdff;
  border-radius: 16px;
}

.expand-detail__grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 20px;
}

.expand-detail__item {
  display: flex;
  align-items: baseline;
  font-size: 13px;
  border-bottom: 1px dashed #edf2f7;
  padding-bottom: 8px;
}

.expand-detail__label {
  width: 90px;
  font-weight: 600;
  color: #64748b;
  flex-shrink: 0;
}

.expand-detail__value {
  flex: 1;
  color: #1e293b;
  word-break: break-word;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-buttons .el-button {
  border-radius: 10px;
  min-width: 92px;
  height: 34px;
  font-size: 13px;
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
</style>
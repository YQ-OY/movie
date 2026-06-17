<template>
  <div class="arrange-page">
    <div class="page-header">
      <div class="search-container">
        <!-- 左侧：搜索筛选区域 -->
        <div class="search-filters">
          <el-input v-model="searchForm.name" clearable placeholder="输入电影名称搜索" class="search-item search-item--name"
            @keyup.enter="handleSearch" />
          <el-date-picker v-model="searchForm.dateRange" type="daterange" unlink-panels range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
            class="search-item search-item--range" />
        </div>

        <!-- 右侧：操作按钮区域 -->
        <div class="search-actions">
          <el-button type="primary" class="search-btn btn-primary btn-search" @click="handleSearch">
            <el-icon class="btn-icon">
              <Search />
            </el-icon>
            <span>搜索</span>
          </el-button>
          <el-button class="search-btn btn-secondary btn-reset" @click="handleResetSearch">
            <el-icon class="btn-icon">
              <Refresh />
            </el-icon>
            <span>重置</span>
          </el-button>
        </div>
      </div>
    </div>

    <el-dialog title="修改电影排片" v-model="dialog" width="70%" align-center class="film-dialog film-dialog--arrange">
      <el-form ref="form" :model="arrangement" label-width="92px" class="film-dialog__form film-dialog__form--arrange">
        <div class="arrange-layout">
          <div class="arrange-layout__header">
            <div class="arrange-movie-card">
              <div class="arrange-movie-card__label">排片电影</div>
              <div class="arrange-movie-card__name">{{ arrangement.name || '未选择电影' }}</div>
              <div class="arrange-movie-card__meta">片长 {{ arrangement.filmDuration }} 分钟 · 电影 ID {{ arrangement.fid ||
                '-' }}</div>
            </div>

            <div class="arrange-summary-card">
              <div class="arrange-summary-card__item">
                <span class="arrange-summary-card__label">结束时间</span>
                <span class="arrange-summary-card__value">{{ arrangement.endTime || '自动计算' }}</span>
              </div>
              <div class="arrange-summary-card__item">
                <span class="arrange-summary-card__label">座位数</span>
                <span class="arrange-summary-card__value">{{ arrangement.seatNumber }}</span>
              </div>
              <div class="arrange-summary-card__item">
                <span class="arrange-summary-card__label">票价</span>
                <span class="arrange-summary-card__value">¥{{ arrangement.price }}</span>
              </div>
            </div>
          </div>

          <div class="arrange-layout__body">
            <div class="arrange-form-grid">
              <el-form-item label="开放座位">
                <el-select v-model="arrangement.seatNumber" placeholder="请选择座位数" class="full-width-input">
                  <el-option label="60" :value="60" />
                  <el-option label="80" :value="80" />
                  <el-option label="100" :value="100" />
                  <el-option label="120" :value="120" />
                  <el-option label="140" :value="140" />
                </el-select>
              </el-form-item>

              <el-form-item label="放映类型">
                <el-select v-model="arrangement.type" placeholder="请选择放映类型" class="full-width-input ">
                  <el-option label="2D 放映" value="2D放映" />
                  <el-option label="3D 放映" value="3D放映" />
                </el-select>
              </el-form-item>

              <el-form-item label="放映日期">
                <el-date-picker class="full-width-input " type="date" placeholder="选择日期" v-model="arrangement.date"
                  value-format="YYYY-MM-DD" />
              </el-form-item>

              <el-form-item label="放映时间">
                <div class="time-range-group">
                  <el-time-picker class="full-width-input " placeholder="开始时间" value-format="HH:mm:ss"
                    v-model="arrangement.startTime" />
                  <span class="time-range-separator">至</span>
                  <el-input class="full-width-input " disabled :model-value="arrangement.endTime"
                    placeholder="结束时间自动计算" />
                </div>
              </el-form-item>

              <el-form-item label="片长" class="arrange-form-item--compact">
                <div class="info-badge info-badge--subtle">
                  <span class="info-badge__main">{{ arrangement.filmDuration }}</span>
                  <span class="info-badge__sub">分钟</span>
                </div>
              </el-form-item>

              <el-form-item label="电影票价">
                <el-input-number class="full-width-input full-width-input--compact " v-model="arrangement.price"
                  :precision="2" :step="0.1" :max="999.99" />
              </el-form-item>

              <el-form-item label="排片人" class="arrange-form-grid__full">
                <el-input class="full-width-input " type="textarea" :rows="3" v-model="arrangement.founder"
                  placeholder="请输入排片人" />
              </el-form-item>
            </div>
          </div>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialog = false">取 消</el-button>
          <el-button type="primary" @click="submitEditArrange">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <div class="table-card">
      <el-table v-loading="loading" :data="arrangementList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '72px' }" style="width: 100%">
        <el-table-column label="电影名" prop="name" min-width="180" />

        <el-table-column label="放映日期" prop="date" min-width="140" align="center" />

        <el-table-column label="开放座位" prop="seatNumber" min-width="120" align="center" />

        <el-table-column label="放映类型" min-width="120" align="center">
          <template #default="scope">
            <el-tag effect="light" :style="getTypeTagStyle(scope.row.type)" :disable-transitions="true">
              {{ scope.row.type === '3D放映' ? '3D 放映' : '2D 放映' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="票房统计" prop="boxOffice" min-width="120" align="center" />

        <el-table-column label="结束时间" prop="endTime" min-width="140" align="center" />

        <el-table-column label="本场票价" min-width="120" align="center">
          <template #default="scope">
            <el-tag class="table-chip table-chip--price" effect="plain">
              {{ scope.row.price }} 元
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="220" align="right" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="handleEdit(scope.$index, scope.row)">
                <el-icon>
                  <Edit />
                </el-icon>
                <span>修改</span>
              </el-button>
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

      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[8, 16, 24, 32]"
          :total="totalCount" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" :pager-count="5" />
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <el-dialog title="删除排片" v-model="dialogDelete" width="28%" align-center class="film-dialog film-dialog--delete">
      <div class="delete-dialog-body">
        <div class="delete-dialog-body__icon">
          <el-icon>
            <WarningFilled />
          </el-icon>
        </div>
        <div class="delete-dialog-body__content">
          <div class="delete-dialog-body__title">确认删除该排片？</div>
          <div class="delete-dialog-body__desc">删除后将无法恢复，电影《{{ deleteTarget.name }}》的此场次排片信息会被移除。</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer dialog-footer--danger">
          <el-button @click="dialogDelete = false">取消</el-button>
          <el-button type="danger" @click="confirmDeleteArrangement">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { listArrangementPage, FindAllArrangement, DeleteArrangement, UpdateArrangement, ListAllFilm } from "@/api/film";
import { computeEndTimeHms } from "@/utils/arrangementEnd";
import { Edit, Delete, WarningFilled, Search, Refresh } from '@element-plus/icons-vue'

export default {
  name: "Arrange",
  components: { Edit, Delete, WarningFilled, Search, Refresh },
  data() {
    return {
      dialog: false,
      searchForm: {
        name: '',
        dateRange: [],
      },
      arrangementList: [],   // 当前页数据
      loading: false,
      currentPage: 1,
      pageSize: 8,
      totalCount: 0,
      arrangement: {
        name: '',
        fid: '',
        seatNumber: 40,
        price: 30.5,
        date: '',
        startTime: '',
        endTime: '',
        founder: '',
        type: '2D放映',
        filmDuration: 120,
      },
      filmList: [],
      dialogDelete: false,          // 删除对话框开关
      deleteTarget: {               // 待删除的排片信息
        id: null,
        index: -1,
        name: '',
      },
    }
  },

  mounted() {
    this.loadArrangementList();
    ListAllFilm().then((res) => {
      if (res.success && res.data) this.filmList = res.data;
    });
  },

  watch: {
    'arrangement.date'() { this.syncArrangementEnd() },
    'arrangement.startTime'() { this.syncArrangementEnd() },
    'arrangement.filmDuration'() { this.syncArrangementEnd() },
  },

  methods: {
    getTypeTagStyle(type) {
      return type === '3D放映'
        ? { background: '#dbeafe', color: '#1d4ed8', borderColor: '#93c5fd' }
        : { background: '#dcfce7', color: '#166534', borderColor: '#86efac' }
    },

    syncArrangementEnd() {
      const { date, startTime, filmDuration } = this.arrangement
      if (date && startTime && filmDuration) {
        this.arrangement.endTime = computeEndTimeHms(date, startTime, filmDuration)
      } else {
        this.arrangement.endTime = ''
      }
    },

    handleSearch() {
      this.currentPage = 1;
      this.loadArrangementList();
    },

    handleResetSearch() {
      this.searchForm = {
        name: '',
        dateRange: [],
      };
      this.currentPage = 1;
      this.loadArrangementList();
    },

    handleSizeChange(newSize) {
      this.pageSize = newSize;
      this.currentPage = 1;
      this.loadArrangementList();
    },

    handleCurrentChange(newPage) {
      this.currentPage = newPage;
      this.loadArrangementList();
    },

    handleEdit(index, row) {
      this.dialog = true
      const fd = this.filmList.find((f) => f.id === row.fid)
      const filmDuration = fd && fd.duration > 0 ? fd.duration : 120
      if (!(fd && fd.duration > 0)) {
        this.$message.warning('未找到该电影时长，已按 120 分钟计算，请在电影管理中维护时长')
      }
      this.arrangement = { ...row, filmDuration }
      this.$nextTick(() => this.syncArrangementEnd())
    },

    submitEditArrange() {
      this.syncArrangementEnd();
      const payload = { ...this.arrangement };
      delete payload.filmDuration;
      UpdateArrangement(payload).then(res => {
        if (res.success) {
          this.dialog = false;
          this.$message.success('排片保存成功');
          this.loadArrangementList();   // 刷新当前页
        }
      });
    },

    // 打开删除确认对话框
    openDeleteDialog(index, row) {
      this.deleteTarget = {
        id: row.id,
        index: index,          // 注意：由于分页，index 是当前页中的索引，删除后需要重新加载列表，因此 index 可只用作参考，实际删除按 id
        name: row.name,
      }
      this.dialogDelete = true
    },

    // 确认删除排片
    async confirmDeleteArrangement() {
      try {
        const res = await DeleteArrangement(this.deleteTarget.id)
        if (!res?.success) return
        this.dialogDelete = false
        this.$message.success(`排片《${this.deleteTarget.name}》删除成功！`)
        if (this.arrangementList.length === 1 && this.currentPage > 1) {
          this.currentPage--
        }
        this.loadArrangementList()
      } catch (error) {
        console.error('删除排片失败', error)
      }
    },

    async loadArrangementList() {
      this.loading = true;
      try {
        const [startDate, endDate] = this.searchForm.dateRange || [];
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          name: this.searchForm.name || null,
          startDate: startDate || null,
          endDate: endDate || null,
        };
        const res = await listArrangementPage(params);
        if (!res?.success) return
        this.arrangementList = res.data.rows || [];
        this.totalCount = res.data.total || 0;
        this.currentPage = res.data.page || this.currentPage;
      } catch (error) {
        console.error('加载排片列表失败', error);
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
.arrange-page {
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

.search-item--name {
  width: 280px;
}

.search-item--range {
  width: 360px;
}

/* 输入框和选择框样式优化 */
:deep(.el-input__wrapper),
:deep(.el-date-editor .el-input__wrapper) {
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

:deep(.el-input__wrapper:hover),
:deep(.el-date-editor .el-input__wrapper:hover) {
  border-color: #93c5fd;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-date-editor .el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

/* 基础按钮样式 */
.search-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 35px;
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

/* 搜索按钮特殊样式 */
.btn-search {
  padding: 0 22px;
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
  width: 240px;
}

.search-item--name {
  width: 280px;
}

.search-item--range {
  width: 320px;
}

.search-submit-btn,
.search-reset-btn {
  border-radius: 6px;
}

.table-card {
  min-height: 0;
  display: flex;
  flex: 1;
  flex-direction: column;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 18px 18px 0;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  overflow: hidden;
}

.film-table {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  /* 内容过多时出现滚动条 */
  border-radius: 16px;
}

.film-table :deep(.el-table__inner-wrapper) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.film-table :deep(.el-table__body-wrapper) {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.table-search {
  width: 100%;
}

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
  padding: 18px 22px 8px;
  background: #fbfdff;
}

.film-dialog :deep(.el-dialog__footer) {
  padding: 14px 22px 22px;
  background: #fbfdff;
  border-top: 1px solid #e2e8f0;
}

.film-dialog__form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.film-dialog__form :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
}

.arrange-layout {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.arrange-layout__header {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 0.8fr);
  gap: 18px;
}

.arrange-movie-card,
.arrange-summary-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.arrange-movie-card {
  padding: 18px;
}

.arrange-movie-card__label {
  font-size: 13px;
  font-weight: 700;
  color: #64748b;
}

.arrange-movie-card__name {
  margin-top: 10px;
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
}

.arrange-movie-card__meta {
  margin-top: 8px;
  font-size: 13px;
  color: #64748b;
}

.arrange-summary-card {
  padding: 16px 18px;
  display: grid;
  gap: 12px;
}

.arrange-summary-card__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.arrange-summary-card__label {
  font-size: 13px;
  color: #64748b;
}

.arrange-summary-card__value {
  font-size: 14px;
  font-weight: 800;
  color: #0f172a;
}

.arrange-layout__body {
  padding: 16px 18px 8px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.arrange-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 2px 16px;
}

.arrange-form-grid :deep(.el-form-item) {
  margin-bottom: 14px;
}

.arrange-form-item--compact {
  align-items: center;
}

.arrange-form-item--compact :deep(.el-form-item__content) {
  min-height: 40px;
  display: flex;
  align-items: center;
}

.arrange-form-grid__full {
  grid-column: 1 / -1;
}

.arrange-form-grid :deep(.el-textarea__inner) {
  min-height: 130px;
}

.full-width-input {
  width: 100%;
}

.full-width-input--compact :deep(.el-input__wrapper) {
  min-height: 30px;
}

.time-range-group {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 10px;
}

.time-range-separator {
  color: #94a3b8;
  font-size: 14px;
  padding: 0 4px;
}

.info-badge {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;
  padding: 0px 12px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #334155;
}

.info-badge__main {
  font-size: 18px;
  font-weight: 800;
}

.info-badge__sub {
  font-size: 12px;
  color: #64748b;
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

.pagination-container :deep(.el-pagination .el-select .el-input__wrapper),
.pagination-container :deep(.el-pagination .el-input__wrapper) {
  border-radius: 10px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
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

.table-chip--price {
  min-width: 88px;
  padding: 10px 16px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #334155;
  font-weight: 700;
}

.film-table :deep(.el-tag) {
  border-radius: 8px;
}

/* 删除对话框专用样式 */
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
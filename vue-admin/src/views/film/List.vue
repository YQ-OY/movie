<template>
  <div class="film-list">

    <!-- 搜索区 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">电影管理</div>
          <div class="page-subtitle">支持按电影名称、电影类型、电影地区、上架状态筛选</div>
        </div>

        <div class="search-form">
          <el-input v-model="searchForm.name" clearable placeholder="电影名称" class="search-item search-item--name" />
          <!-- 多选 电影类型 -->
          <el-select v-model="searchForm.type" clearable filterable placeholder="电影类型" multiple collapse-tags
            collapse-tags-tooltip class="search-item">
            <el-option label="爱情" value="爱情" />
            <el-option label="喜剧" value="喜剧" />
            <el-option label="科幻" value="科幻" />
            <el-option label="动画" value="动画" />
            <el-option label="恐怖" value="恐怖" />
            <el-option label="悬疑" value="悬疑" />
            <el-option label="冒险" value="冒险" />
            <el-option label="动作" value="动作" />
            <el-option label="犯罪" value="犯罪" />
            <el-option label="历史" value="历史" />
            <el-option label="古装" value="古装" />
            <el-option label="战争" value="战争" />
            <el-option label="纪录片" value="纪录片" />
            <el-option label="家庭" value="家庭" />
            <el-option label="传记" value="传记" />
            <el-option label="武侠" value="武侠" />
            <el-option label="儿童" value="儿童" />
            <el-option label="短片" value="短片" />
            <el-option label="其他" value="其他" />
          </el-select>

          <!-- 多选 电影地区 -->
          <el-select v-model="searchForm.region" clearable filterable placeholder="电影地区" multiple collapse-tags
            collapse-tags-tooltip class="search-item">
            <el-option label="中国大陆" value="中国大陆" />
            <el-option label="中国香港" value="中国香港" />
            <el-option label="中国台湾" value="中国台湾" />
            <el-option label="日本" value="日本" />
            <el-option label="美国" value="美国" />
            <el-option label="韩国" value="韩国" />
            <el-option label="英国" value="英国" />
            <el-option label="法国" value="法国" />
            <el-option label="印度" value="印度" />
            <el-option label="其他" value="其他" />
          </el-select>

          <!-- 单选 上架状态 -->
          <el-select v-model="searchForm.status" clearable placeholder="上架状态" class="search-item">
            <el-option label="上架" :value="true" />
            <el-option label="下架" :value="false" />
          </el-select>
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

    <!-- 编辑电影信息的对话框 -->
    <el-dialog title="电影信息修改" v-model="dialog1" width="72%" align-center class="film-dialog film-dialog--edit">

      <el-form ref="form" class="film-dialog__form film-dialog__form--edit" :model="form" label-width="92px">
        <div class="film-edit-layout film-edit-layout--two-col">
          <div class="film-cover-panel">
            <div class="film-cover-panel__title">电影封面</div>
            <el-upload v-if="!coverPreview" class="film-cover-uploader" accept=".png,.jpg,.jpeg" :headers="header"
              :action="uploadAction" :on-success="handleUploadSuccess" :show-file-list="false"
              :before-upload="beforeCoverUpload">
              <div class="film-cover-box film-cover-box--empty">
                <div class="film-cover-box__placeholder">
                  <el-icon class="film-cover-box__icon">
                    <Upload />
                  </el-icon>
                  <div class="film-cover-box__text">点击上传封面</div>
                  <div class="film-cover-box__hint">支持 JPG / PNG，建议 2:3 海报比例</div>
                </div>
              </div>
            </el-upload>

            <div v-else class="film-cover-box film-cover-box--preview">
              <el-image class="film-cover-box__image" :src="coverPreview" fit="cover" :preview-src-list="[coverPreview]"
                :initial-index="0" preview-teleported />
              <el-button class="film-cover-box__delete" circle size="small" type="danger" @click.stop="removeCover">
                <el-icon>
                  <Delete />
                </el-icon>
              </el-button>
            </div>
          </div>

          <div class="film-form-panel">
            <div class="film-form-grid">
              <el-form-item label="电影名称">
                <el-input v-model="form.name" placeholder="请输入电影名称" class="" />
              </el-form-item>

              <el-form-item label="上映地区">
                <el-select v-model="form.region" placeholder="请选择上映区域" class="full-width-input ">
                  <el-option label="中国大陆" value="中国大陆" />
                  <el-option label="中国香港" value="中国香港" />
                  <el-option label="中国台湾" value="中国台湾" />
                  <el-option label="日本" value="日本" />
                  <el-option label="美国" value="美国" />
                  <el-option label="韩国" value="韩国" />
                  <el-option label="英国" value="英国" />
                  <el-option label="法国" value="法国" />
                  <el-option label="印度" value="印度" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>

              <el-form-item label="全片时长">
                <el-input-number v-model="form.duration" :min="10" :max="550" class="full-width-input" />
              </el-form-item>

              <el-form-item label="上映时间">
                <el-date-picker v-model="form.releaseTime" type="date" value-format="YYYY年MM月DD日" placeholder="选择日期"
                  style="width: 100%;" class="" />
              </el-form-item>

              <el-form-item label="即时上架">
                <el-switch v-model="form.status" class="bordered-switch" />
              </el-form-item>

              <el-form-item label="电影类型">
                <el-select v-model="form.type" placeholder="请选择电影类型" class="full-width-input  film-type-select"
                  filterable>
                  <el-option v-for="item in filmTypeOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>

              <el-form-item label="内容简介" class="introduction-item introduction-item--right">
                <el-input rows="8" type="textarea" v-model="form.introduction" placeholder="请输入内容简介"
                  class=""></el-input>
              </el-form-item>
            </div>
          </div>
        </div>

      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialog1 = false">取 消</el-button>
          <el-button type="primary" @click="submitUpdate()">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增电影排片的对话框 -->
    <el-dialog title="新增电影排片" v-model="dialog2" width="70%" align-center class="film-dialog film-dialog--arrange">

      <el-form ref="form" class="film-dialog__form film-dialog__form--arrange" :model="arrangement" label-width="92px">
        <div class="arrange-layout">
          <!-- 头部 -->
          <div class="arrange-layout__header">
            <!-- 电影基本信息 -->
            <div class="arrange-movie-card">
              <div class="arrange-movie-card__label">排片电影</div>
              <div class="arrange-movie-card__name">{{ arrangement.name || '未选择电影' }}</div>
              <div class="arrange-movie-card__meta">片长 {{ arrangement.filmDuration }} 分钟 · 电影 ID {{ arrangement.fid }}
              </div>
            </div>
            <!-- 排片摘要 -->
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

          <!-- 表单区域 -->
          <div class="arrange-layout__body">
            <div class="arrange-form-grid">
              <el-form-item label="开放座位">
                <el-input-number class="full-width-input " v-model="arrangement.seatNumber" :min="10" :max="100" />
              </el-form-item>

              <el-form-item label="放映类型">
                <el-select v-model="arrangement.type" placeholder="请选择放映类型" class="full-width-input ">
                  <el-option label="2D 放映" value="2D放映"></el-option>
                  <el-option label="3D 放映" value="3D放映"></el-option>
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
          <el-button @click="dialog2 = false">取 消</el-button>
          <el-button type="primary" @click="submitArrange()">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog title="删除电影" v-model="dialogDelete" width="28%" align-center class="film-dialog film-dialog--delete">
      <div class="delete-dialog-body">
        <div class="delete-dialog-body__icon">
          <el-icon>
            <WarningFilled />
          </el-icon>
        </div>
        <div class="delete-dialog-body__content">
          <div class="delete-dialog-body__title">确认删除该电影？</div>
          <div class="delete-dialog-body__desc">删除后将无法恢复，电影《{{ deleteTarget.name }}》及其相关信息会被移除。</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer dialog-footer--danger">
          <el-button @click="dialogDelete = false">取 消</el-button>
          <el-button type="danger" @click="confirmDeleteMovie">确定删除</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 电影表格 -->
    <div class="table-card">

      <!-- 电影表格 -->
      <el-table v-loading="tableLoading" :data="filmList" class="film-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: 700, fontSize: '15px', textAlign: 'center', height: '58px' }"
        :row-style="{ height: '112px' }" style="width: 100%">

        <el-table-column label="电影名称" min-width="380">
          <template #default="scope">
            <div class="film-cell">
              <el-image class="film-cover" :src="scope.row.cover" fit="cover">
              </el-image>
              <div class="film-meta">
                <div class="film-name">{{ scope.row.name }}</div>
                <div class="film-subtitle">{{ scope.row.type }} · {{ scope.row.region }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="上映时间" prop="releaseTime" min-width="150" align="center">
          <template #default="scope">
            <div class="table-chip table-chip--date">
              <div class="table-chip__main">{{ scope.row.releaseTime }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="类型" prop="type" min-width="110" align="center">
          <template #default="scope">
            <el-tag class="table-chip table-chip--type" :style="getTypeTagStyle(scope.row.type)" effect="light"
              :disable-transitions="true">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="上架状态" min-width="110" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === true" class="table-chip table-chip--status"
              :style="getStatusTagStyle(true)" effect="light" :disable-transitions="true">上架</el-tag>
            <el-tag v-else class="table-chip table-chip--status" :style="getStatusTagStyle(false)" effect="light"
              :disable-transitions="true">下架</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="地区" prop="region" min-width="120" align="center">
          <template #default="scope">
            <el-tag class="table-chip table-chip--region" :style="getRegionTagStyle(scope.row.region)" effect="plain"
              :disable-transitions="true">
              {{ scope.row.region }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="电影时长" min-width="120" align="center">
          <template #default="scope">
            <div class="table-chip table-chip--duration">
              <div class="table-chip__main">{{ scope.row.duration }}</div>
              <div class="table-chip__sub">分钟</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="260" align="right" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="handleEdit(scope.$index, scope.row)">
                <el-icon>
                  <Edit />
                </el-icon>
                <span>修改</span>
              </el-button>
              <el-button size="small" type="success" plain @click="handleArrange(scope.$index, scope.row)">
                <el-icon>
                  <CirclePlus />
                </el-icon>
                <span>排片</span>
              </el-button>
              <el-button size="small" type="danger" plain class="delete-action-btn"
                @click="openDeleteDialog(scope.$index, scope.row)">
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
          @current-change="handleCurrentChange" />
      </div>
    </div>

  </div>
</template>

<script>
import { listFilmPage, DeleteById, UpdateFilm, AddArrangement } from "@/api/film";
import config from "@/config";
import { computeEndTimeHms } from "@/utils/arrangementEnd";
import { Upload, Edit, CirclePlus, Delete, WarningFilled, Search, Refresh } from '@element-plus/icons-vue'

export default {
  components: { Upload, Edit, CirclePlus, Delete, WarningFilled, Search, Refresh },
  data() {
    return {
      header: {
        "Authorization": localStorage.getItem("token")
      },
      uploadAction: config.API_URL + '/upload',
      url: '',
      tableLoading: false,
      filmList: [],          // 当前页数据（从后端返回的 rows）
      currentPage: 1,
      pageSize: 5,
      totalCount: 0,
      searchForm: {
        name: '',
        type: '',
        region: '',
        status: '',         // 注意后端需要接收 boolean 或 null
      },
      form: {
        cover: 'null',
        name: '',
        region: '',
        releaseTime: '',
        duration: 120,
        introduction: '',
        type: '',
        status: true,
      },
      coverPreview: '',
      filmTypeOptions: ['爱情', '喜剧', '科幻', '动画', '恐怖', '悬疑', '冒险', '动作', '犯罪', '历史', '古装', '战争', '纪录片', '家庭', '传记', '武侠', '儿童', '短片', '其他'],
      arrangement: {
        name: '',
        fid: '',
        seatNumber: 40,
        price: 30.50,
        date: '',
        startTime: '',
        endTime: '',
        founder: '',
        type: '2D放映',
        filmDuration: 120,
      },
      dialog1: false,
      dialog2: false,
      dialogDelete: false,
      deleteTarget: {
        id: null,
        index: -1,
        name: '',
      },
    }
  },

  mounted() {
    this.loadFilmList()
  },

  watch: {
    'arrangement.date'() { this.syncArrangementEnd() },
    'arrangement.startTime'() { this.syncArrangementEnd() },
    'arrangement.filmDuration'() { this.syncArrangementEnd() },
  },

  methods: {

    getTypeTagStyle(type) {
      const map = {
        '爱情': { background: '#ffe4e6', color: '#be123c', borderColor: '#fda4af' },
        '喜剧': { background: '#fef3c7', color: '#b45309', borderColor: '#fcd34d' },
        '科幻': { background: '#e0e7ff', color: '#4338ca', borderColor: '#a5b4fc' },
        '动画': { background: '#ede9fe', color: '#7c3aed', borderColor: '#c4b5fd' },
        '恐怖': { background: '#fee2e2', color: '#b91c1c', borderColor: '#fca5a5' },
        '悬疑': { background: '#f1f5f9', color: '#334155', borderColor: '#cbd5e1' },
        '冒险': { background: '#d1fae5', color: '#047857', borderColor: '#86efac' },
        '动作': { background: '#dbeafe', color: '#1d4ed8', borderColor: '#93c5fd' },
        '犯罪': { background: '#e2e8f0', color: '#475569', borderColor: '#cbd5e1' },
        '历史': { background: '#f5f5f4', color: '#57534e', borderColor: '#d6d3d1' },
        '古装': { background: '#fce7f3', color: '#be185d', borderColor: '#f9a8d4' },
        '战争': { background: '#fee2e2', color: '#991b1b', borderColor: '#fca5a5' },
        '纪录片': { background: '#cffafe', color: '#0e7490', borderColor: '#67e8f9' },
        '家庭': { background: '#dcfce7', color: '#166534', borderColor: '#86efac' },
        '传记': { background: '#fae8ff', color: '#a21caf', borderColor: '#f0abfc' },
        '武侠': { background: '#fed7aa', color: '#c2410c', borderColor: '#fdba74' },
        '儿童': { background: '#e0f2fe', color: '#0369a1', borderColor: '#7dd3fc' },
        '短片': { background: '#ede9fe', color: '#5b21b6', borderColor: '#c4b5fd' },
        '其他': { background: '#e2e8f0', color: '#475569', borderColor: '#cbd5e1' }
      }
      return map[type] || map['其他']
    },

    getRegionTagStyle(region) {
      const map = {
        '中国大陆': { background: '#eff6ff', color: '#2563eb', borderColor: '#93c5fd' },
        '中国香港': { background: '#fce7f3', color: '#db2777', borderColor: '#f9a8d4' },
        '中国台湾': { background: '#f3e8ff', color: '#9333ea', borderColor: '#d8b4fe' },
        '日本': { background: '#fef3c7', color: '#b45309', borderColor: '#fcd34d' },
        '美国': { background: '#dbeafe', color: '#1d4ed8', borderColor: '#93c5fd' },
        '韩国': { background: '#dcfce7', color: '#166534', borderColor: '#86efac' },
        '英国': { background: '#e2e8f0', color: '#475569', borderColor: '#cbd5e1' },
        '法国': { background: '#e0f2fe', color: '#0284c7', borderColor: '#7dd3fc' },
        '印度': { background: '#fff7ed', color: '#c2410c', borderColor: '#fdba74' },
        '其他': { background: '#f1f5f9', color: '#475569', borderColor: '#cbd5e1' }
      }
      return map[region] || map['其他']
    },

    getStatusTagStyle(status) {
      return status
        ? { background: '#ecfdf5', color: '#15803d', borderColor: '#86efac' }
        : { background: '#fff7ed', color: '#c2410c', borderColor: '#fdba74' }
    },

    // 加载电影列表（分页+后端过滤）
    async loadFilmList() {
      this.tableLoading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          name: this.searchForm.name || null,
          type: this.searchForm.type && this.searchForm.type.length ? this.searchForm.type : null,
          region: this.searchForm.region && this.searchForm.region.length ? this.searchForm.region : null,
          status: this.searchForm.status === '' ? null : this.searchForm.status
        }
        const res = await listFilmPage(params)
        // 假设后端返回格式为 { data: { rows, total, page, size, totalPages } }
        this.filmList = res.data.rows || []
        this.totalCount = res.data.total || 0
        // 可选：同步页码（防止后端返回的 page 不一致）
        this.currentPage = res.data.page || this.currentPage
      } catch (error) {
        console.error('加载电影列表失败', error)
        this.$message.error('加载失败，请重试')
      } finally {
        this.tableLoading = false
      }
    },

    // 搜索按钮
    handleSearch() {
      this.currentPage = 1   // 重置页码
      this.loadFilmList()
    },

    // 重置搜索
    handleResetSearch() {
      this.searchForm = {
        name: '',
        type: '',
        region: '',
        status: '',
      }
      this.currentPage = 1
      this.loadFilmList()
    },

    // 分页：每页数量变化
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.currentPage = 1
      this.loadFilmList()
    },

    // 分页：页码变化
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadFilmList()
    },

    // 提交修改（更新后重新加载当前页）
    async submitUpdate() {
      this.form.cover = this.url || this.form.cover
      try {
        await UpdateFilm(this.form)
        this.dialog1 = false
        this.$message.success('修改成功')
        this.loadFilmList()   // 刷新当前页
      } catch (error) {
        this.$message.error('修改失败')
      }
    },

    // 新增排片成功后刷新列表（因为排片可能影响电影“已有排片”状态，但当前列表不需要，可选择性刷新）
    async submitArrange() {
      this.syncArrangementEnd()
      const payload = { ...this.arrangement }
      delete payload.filmDuration
      try {
        await AddArrangement(payload)
        this.dialog2 = false
        this.$message.success('排片成功')
        // 如果电影列表需要展示“已有排片”标识，可刷新当前页
        // this.loadFilmList()
      } catch (error) {
        this.$message.error('排片失败')
      }
    },

    handleEdit(index, row) {
      this.dialog1 = true
      this.form = { ...row }
      this.url = row.cover || ''
      this.coverPreview = row.cover || ''
    },

    handleArrange(index, row) {
      this.dialog2 = true
      const dur = row.duration != null && row.duration > 0 ? row.duration : 120
      if (!(row.duration > 0)) {
        this.$message.warning('该电影未设置有效时长，已按 120 分钟计算结束时间，请在电影管理中维护时长')
      }
      this.arrangement = {
        name: row.name,
        fid: row.id,
        seatNumber: 40,
        price: 30.50,
        date: '',
        startTime: '',
        endTime: '',
        founder: '',
        type: '2D放映',
        filmDuration: dur,
      }
      this.syncArrangementEnd()
    },

    openDeleteDialog(index, row) {
      this.deleteTarget = {
        id: row.id,
        index,
        name: row.name,
      }
      this.dialogDelete = true
    },

    async confirmDeleteMovie() {
      try {
        await DeleteById(this.deleteTarget.id)
        this.dialogDelete = false
        this.filmList.splice(this.deleteTarget.index, 1)
        this.$message({
          message: '电影《' + this.deleteTarget.name + '》删除成功！',
          type: 'success'
        })
      } catch (error) {
        this.$message.error('删除失败')
      }
    },

    handleUploadSuccess(res) {
      this.url = typeof res === 'string' ? res : (res && typeof res.data === 'string' ? res.data : '')
      this.form.cover = this.url
      this.coverPreview = this.url
    },

    beforeCoverUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].includes(file.type)
      if (!isImage) {
        this.$message.error('只能上传 JPG/PNG 格式的封面图片')
        return false
      }
      const isLt50M = file.size / 1024 / 1024 < 50
      if (!isLt50M) {
        this.$message.error('封面图片大小不能超过 50MB')
        return false
      }
      const reader = new FileReader()
      reader.onload = (e) => {
        this.coverPreview = e.target?.result || ''
      }
      reader.readAsDataURL(file)
      return true
    },

    removeCover() {
      this.url = ''
      this.coverPreview = ''
      this.form.cover = ''
    },

    decreaseDuration() {
      if (this.form.duration > 10) this.form.duration -= 1
    },

    increaseDuration() {
      if (this.form.duration < 550) this.form.duration += 1
    },

    syncArrangementEnd() {
      const { date, startTime, filmDuration } = this.arrangement
      // 只有三个值都存在时才计算结束时间
      if (date && startTime && filmDuration) {
        this.arrangement.endTime = computeEndTimeHms(date, startTime, filmDuration)
      } else {
        this.arrangement.endTime = ''
      }
    },
  },
}
</script>

<style scoped>
.film-list {
  height: 100vh;
  /* 固定高度为视口高度，而非最小高度 */
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
  /* 禁止整个页面滚动，让内部表格滚动 */
  padding: 20px;
  box-sizing: border-box;
  background: rgb(250, 251, 252);
}

.page-header {
  margin-bottom: 16px;
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
  width: 160px;
}

.search-item--name {
  width: 220px;
}

.search-submit-btn,
.search-reset-btn {
  border-radius: 6px;
}

.page-header {
  flex: 0 0 auto;
}

.table-card {
  display: flex;
  flex-direction: column;
  flex: 1;
  /* 占满剩余空间 */
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 18px 18px 0px 18px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  min-height: 0;
}

.film-table {
  flex: 1;
  /* 让表格自动填满剩余空间 */
  min-height: 0;
  /* 允许压缩 */
  overflow-y: auto;
  /* 内容过多时出现滚动条 */
  border-radius: 16px;
}

.film-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.film-table :deep(.el-table__header-wrapper th) {
  border-bottom: 1px solid #e2e8f0;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.2px;
  text-align: center;
  height: 58px;
}

.film-table :deep(.el-table__cell) {
  padding-top: 18px;
  padding-bottom: 18px;
}

.film-table :deep(.el-table__cell:nth-child(n+2):nth-child(-n+6) .cell) {
  font-size: 14px;
}

.film-table :deep(.el-table_1_column_2 .cell),
.film-table :deep(.el-table_1_column_3 .cell),
.film-table :deep(.el-table_1_column_4 .cell),
.film-table :deep(.el-table_1_column_5 .cell),
.film-table :deep(.el-table_1_column_6 .cell) {
  display: flex;
  align-items: center;
  justify-content: center;
}

.film-table :deep(.el-table__cell:first-child .cell) {
  text-align: left;
}

.film-table :deep(.cell) {
  text-align: center;
}

.film-table :deep(.el-table__row) {
  transition: background-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.film-table :deep(.el-table__body tr:hover > td) {
  background-color: rgba(148, 163, 184, 0.14) !important;

}

.film-cell {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.film-cover {
  width: 60px;
  height: 84px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.14);
  flex-shrink: 0;
  background: #f1f5f9;
}

.film-meta {
  min-width: 0;
}

.film-name {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.35;
}

.film-subtitle {
  margin-top: 7px;
  font-size: 12px;
  color: #64748b;
}

.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
  white-space: nowrap;
}

.action-buttons :deep(.el-button) {
  border-radius: 10px;
  min-width: 72px;
  height: 34px;
  font-size: 13px;
  flex: 0 0 auto;
}

.table-chip {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  min-width: 76px;
  padding: 10px 14px;
  border-radius: 10px;
  line-height: 1;
  font-size: 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #334155;
}

.table-chip__main {
  font-size: 16px;
  font-weight: 700;
  color: inherit;
}

.table-chip__sub {
  font-size: 11px;
  color: inherit;
  opacity: 0.75;
}

.table-chip--date {
  min-width: 92px;
}

.table-chip--type {
  min-width: 72px;
  padding: 10px 16px;
  font-weight: 700;
}

.table-chip--status {
  min-width: 72px;
  padding: 10px 16px;
  font-weight: 700;
}

.table-chip--region {
  min-width: 78px;
  padding: 10px 16px;
  font-weight: 700;
}

.table-chip--duration {
  min-width: 78px;
  padding: 10px 16px;
}

.table-chip--type-love {
  background: #ffe4e6;
  color: #be123c;
}

.table-chip--type-comedy {
  background: #fef3c7;
  color: #b45309;
}

.table-chip--type-sci-fi {
  background: #e0e7ff;
  color: #4338ca;
}

.table-chip--type-animate {
  background: #ede9fe;
  color: #7c3aed;
}

.table-chip--type-horror {
  background: #fee2e2;
  color: #b91c1c;
}

.table-chip--type-suspense {
  background: #f1f5f9;
  color: #334155;
}

.table-chip--type-adventure {
  background: #d1fae5;
  color: #047857;
}

.table-chip--type-action {
  background: #dbeafe;
  color: #1d4ed8;
}

.table-chip--type-crime {
  background: #e2e8f0;
  color: #475569;
}

.table-chip--type-history {
  background: #f5f5f4;
  color: #57534e;
}

.table-chip--type-costume {
  background: #fce7f3;
  color: #be185d;
}

.table-chip--type-war {
  background: #fee2e2;
  color: #991b1b;
}

.table-chip--type-doc {
  background: #cffafe;
  color: #0e7490;
}

.table-chip--type-family {
  background: #dcfce7;
  color: #166534;
}

.table-chip--type-biography {
  background: #fae8ff;
  color: #a21caf;
}

.table-chip--type-martial {
  background: #fed7aa;
  color: #c2410c;
}

.table-chip--type-child {
  background: #e0f2fe;
  color: #0369a1;
}

.table-chip--type-short {
  background: #ede9fe;
  color: #5b21b6;
}

.table-chip--type-other {
  background: #e2e8f0;
  color: #475569;
}

.table-chip--status-on {
  background: #ecfdf5;
  color: #15803d;
}

.table-chip--status-off {
  background: #fff7ed;
  color: #c2410c;
}

.table-chip--region-mainland {
  background: #eff6ff;
  color: #2563eb;
}

.table-chip--region-hk {
  background: #fce7f3;
  color: #db2777;
}

.table-chip--region-tw {
  background: #f3e8ff;
  color: #9333ea;
}

.table-chip--region-jp {
  background: #fef3c7;
  color: #b45309;
}

.table-chip--region-us {
  background: #dbeafe;
  color: #1d4ed8;
}

.table-chip--region-kr {
  background: #dcfce7;
  color: #166534;
}

.table-chip--region-uk {
  background: #e2e8f0;
  color: #475569;
}

.table-chip--region-fr {
  background: #e0f2fe;
  color: #0284c7;
}

.table-chip--region-in {
  background: #fff7ed;
  color: #c2410c;
}

.table-chip--region-other {
  background: #f1f5f9;
  color: #475569;
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
  aspect-ratio: 2 / 3;
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

.film-cover-box:hover .film-cover-box__delete {
  opacity: 1;
  transform: scale(1);
}

.film-cover-box__image {
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.film-cover-box__delete {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  opacity: 0;
  transform: scale(0.92);
  transition: all 0.18s ease;
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
  color: #334155;
}

.film-cover-box__hint {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.5;
}

.film-form-panel {
  padding: 16px 18px 4px;
}

.film-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 2px 16px;
}

.film-form-grid :deep(.el-form-item) {
  margin-bottom: 14px;
}



.bordered-switch :deep(.el-switch__core) {
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.2);
}

.introduction-item--right {
  grid-column: 1 / -1;
}

.duration-stepper {
  width: 100%;
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr) 44px;
  align-items: center;
  border: 1px solid #dbe4f0;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
}

.duration-stepper__btn {
  height: 40px;
  border: none;
  border-radius: 0;
  font-size: 20px;
}

.duration-stepper__value {
  text-align: center;
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
}

.film-upload {
  width: 100%;
}

.film-upload :deep(.el-upload-dragger) {
  width: 100%;
  border-radius: 14px;
  border: 1px dashed #cbd5e1;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  padding: 20px 16px;
}

.film-upload :deep(.el-upload__text) {
  font-size: 14px;
  color: #475569;
}

.film-upload :deep(.el-upload__tip) {
  color: #94a3b8;
  margin-top: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer--danger .el-button:last-child {
  min-width: 108px;
}

.dialog-footer .el-button {
  min-width: 92px;
  height: 38px;
  border-radius: 10px;
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

.film-popconfirm :deep(.el-popconfirm__main) {
  padding: 16px 16px 10px;
  background: #fff;
}

.film-popconfirm :deep(.el-popconfirm__title) {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.5;
}

.film-popconfirm :deep(.el-popconfirm__action) {
  padding: 0 16px 16px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  background: #fff;
}

.film-popconfirm :deep(.el-button) {
  min-width: 72px;
  border-radius: 10px;
}

.delete-action-btn {
  box-shadow: none;
}

.film-table :deep(.el-tag) {
  border-radius: 8px;
}

.film-type-item :deep(.el-form-item__content) {
  align-items: flex-start;
  line-height: 1.5;
}

.film-type-radios {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 20px;
  max-width: 100%;
}

.film-type-radios :deep(.el-radio) {
  margin-right: 0;
  height: auto;
  align-items: center;
}

.film-type-radios :deep(.el-radio__label) {
  line-height: 1;
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

.arrange-layout {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.arrange-layout__header {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 18px;
}

.arrange-movie-card,
.arrange-summary-card,
.arrange-layout__body {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.arrange-movie-card {
  padding: 18px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 132px;
}

.arrange-movie-card__label,
.arrange-summary-card__label {
  font-size: 12px;
  font-weight: 700;
  color: #64748b;
  letter-spacing: 0.4px;
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
  padding: 14px 18px;
  display: grid;
  gap: 12px;
}

.arrange-summary-card__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  border: 1px solid #e2e8f0;
}

.arrange-summary-card__value {
  font-size: 15px;
  font-weight: 800;
  color: #0f172a;
}

.arrange-layout__body {
  padding: 16px 18px 8px;
}

.arrange-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 2px 16px;
}

.arrange-form-grid :deep(.el-form-item) {
  margin-bottom: 14px;
}

.arrange-form-grid :deep(.arrange-form-item--compact) {
  align-items: center;
}

.arrange-form-grid :deep(.arrange-form-item--compact .el-form-item__content) {
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

.pagination-container {
  flex-shrink: 0;
  /* 新增，防止被挤压 */
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

/* 统一按钮内图标和文字的对齐方式 */
.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}

/* 如果只想针对特定区域的按钮，可以用更明确的选择器 */
.search-submit-btn .el-icon,
.search-reset-btn .el-icon,
.el-icon,
.action-buttons .el-button .el-icon {
  font-size: 16px;
  vertical-align: middle;
}

/* 让按钮内的文字和图标垂直居中 */
.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>

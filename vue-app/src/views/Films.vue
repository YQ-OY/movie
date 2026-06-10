<template>
  <div class="films-page">
    <div class="phase-tabs">
      <button
        type="button"
        class="phase-tab"
        :class="{ 'phase-tab--active': releasePhase === 'showing' }"
        @click="switchPhase('showing')"
      >
        正在上映
      </button>
      <button
        type="button"
        class="phase-tab"
        :class="{ 'phase-tab--active': releasePhase === 'upcoming' }"
        @click="switchPhase('upcoming')"
      >
        待上映
      </button>
    </div>

    <div class="tags-panel">
      <ul class="tags-lines">
        <li class="tags-line">
          <div class="tags-title">类型 :</div>
          <ul class="tags">
            <li
              v-for="item in typeList"
              :key="'t-' + item"
              :class="selectType === item ? 'active' : ''"
            >
              <router-link :to="buildFilterLink(item, null)">{{ item }}</router-link>
            </li>
          </ul>
        </li>
        <li class="tags-line">
          <div class="tags-title">地区 :</div>
          <ul class="tags">
            <li
              v-for="item in regionList"
              :key="'r-' + item"
              :class="selectRegion === item ? 'active' : ''"
            >
              <router-link :to="buildFilterLink(null, item)">{{ item }}</router-link>
            </li>
          </ul>
        </li>
      </ul>
    </div>

    <div class="films-toolbar">
      <div class="films-toolbar__info">
        <span class="films-toolbar__count">共 <em>{{ totalCount }}</em> 部电影</span>
        <span v-if="hasActiveFilter" class="films-toolbar__filter">
          {{ selectType }} · {{ selectRegion }}
        </span>
      </div>
    </div>

    <div v-loading="loading" class="film-grid-section">
      <el-empty
        v-if="!loading && filmList.length === 0"
        class="films-empty"
        description="暂无符合条件的电影"
      />

      <div v-else class="film-grid-wrap">
        <router-link
          v-for="item in filmList"
          :key="item.id"
          class="film-card-link"
          :to="'/film/info?fid=' + item.id"
        >
          <el-card class="film-card" shadow="hover">
            <div class="poster-frame">
              <img class="poster-img" :src="item.cover" :alt="item.name">
              <span v-if="releasePhase === 'showing'" class="poster-score">{{ filmAvgDisplay(item) }}</span>
              <span v-else class="poster-badge">即将上映</span>
            </div>
            <div class="film-name" :title="item.name">{{ item.name }}</div>
          </el-card>
        </router-link>
      </div>
    </div>

    <div v-if="totalCount > 0" class="films-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[16, 32, 48, 64]"
        :total="totalCount"
        :disabled="loading"
        :pager-count="5"
        background
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { listFilmPage, GetFilmEvaluateAvgMap } from '@/api/film'
import { RELEASE_PHASE } from '@/utils/filmRelease'

export default {
  name: 'Films',

  data() {
    return {
      loading: false,
      releasePhase: RELEASE_PHASE.SHOWING,
      selectType: '全部',
      selectRegion: '全部',
      typeList: ['全部', '家庭', '惊悚', '科幻', '爱情', '动作', '喜剧', '恐怖', '悬疑', '犯罪', '冒险', '战争', '历史', '武侠', '传记', '古装', '其他'],
      regionList: ['全部', '中国大陆', '中国香港', '中国台湾', '美国', '韩国', '日本', '泰国', '印度', '法国', '英国', '德国', '其他'],
      filmList: [],
      avgByFid: {},
      currentPage: 1,
      pageSize: 16,
      totalCount: 0,
    }
  },

  computed: {
    hasActiveFilter() {
      return this.selectType !== '全部' || this.selectRegion !== '全部'
    },
  },

  watch: {
    '$route.query': {
      immediate: true,
      handler(newQuery, oldQuery) {
        const region = newQuery.region || '全部'
        const type = newQuery.type || '全部'
        const phase = newQuery.phase === RELEASE_PHASE.UPCOMING
          ? RELEASE_PHASE.UPCOMING
          : RELEASE_PHASE.SHOWING
        const filterChanged = !oldQuery
          || region !== (oldQuery.region || '全部')
          || type !== (oldQuery.type || '全部')
          || phase !== (oldQuery.phase === RELEASE_PHASE.UPCOMING ? RELEASE_PHASE.UPCOMING : RELEASE_PHASE.SHOWING)
        this.selectRegion = region
        this.selectType = type
        this.releasePhase = phase
        if (filterChanged) {
          this.currentPage = 1
        }
        this.loadFilmList()
      },
    },
  },

  mounted() {
    GetFilmEvaluateAvgMap().then(res => {
      const m = res?.data
      this.avgByFid = m && typeof m === 'object' && !Array.isArray(m) ? m : {}
    })
  },

  methods: {
    buildFilterLink(type, region) {
      return {
        path: '/films',
        query: {
          region: region ?? this.selectRegion,
          type: type ?? this.selectType,
          phase: this.releasePhase,
        },
      }
    },

    switchPhase(phase) {
      if (this.releasePhase === phase) return
      this.$router.push({
        path: '/films',
        query: {
          region: this.selectRegion,
          type: this.selectType,
          phase,
        },
      })
    },

    async loadFilmList() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          status: true,
          releasePhase: this.releasePhase,
        }
        if (this.selectType !== '全部') {
          params.type = this.selectType
        }
        if (this.selectRegion !== '全部') {
          params.region = this.selectRegion
        }
        const res = await listFilmPage(params)
        if (!res?.success) {
          return
        }
        const total = Number(res.data?.total) || 0
        const rows = res.data?.rows || []
        const backendPage = Number(res.data?.page) || this.currentPage
        const totalPages = Number(res.data?.totalPages) || (total ? Math.ceil(total / this.pageSize) : 0)

        if (total > 0 && backendPage > totalPages) {
          this.currentPage = totalPages
          await this.loadFilmList()
          return
        }

        this.filmList = rows
        this.totalCount = total
        this.currentPage = backendPage

        if (total > 0 && rows.length === 0 && this.currentPage > 1) {
          this.currentPage -= 1
          await this.loadFilmList()
        }
      } catch {
        // 网络异常由请求拦截器统一提示
      } finally {
        this.loading = false
      }
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.loadFilmList()
      this.scrollToGrid()
    },

    handleCurrentChange(page) {
      this.currentPage = page
      this.loadFilmList()
      this.scrollToGrid()
    },

    scrollToGrid() {
      this.$nextTick(() => {
        const el = this.$el.querySelector('.film-grid-section')
        el?.scrollIntoView({ behavior: 'smooth', block: 'start' })
      })
    },

    filmAvgDisplay(item) {
      if (!item?.id) return '暂无评分'
      const v = this.avgByFid[item.id]
      if (v === undefined || v === null || Number.isNaN(Number(v))) return '暂无评分'
      return Number(v).toFixed(1)
    },
  },
}
</script>

<style scoped>
@import "../assets/css/movie-list.css";

:deep(.tags li.active) {
  background: var(--app-gradient-brand);
  color: #fff;
}

:deep(.tags a:hover) {
  color: var(--app-primary);
}

.films-page {
  padding: clamp(16px, 2vw, 28px) clamp(12px, 2vw, 36px) 48px;
  box-sizing: border-box;
  width: 100%;
  max-width: 1680px;
  margin: 0 auto;
  min-height: calc(100vh - 72px);
  background: var(--app-gradient-page);
}

.phase-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.phase-tab {
  padding: 10px 28px;
  border: 1px solid var(--app-border);
  border-radius: 999px;
  background: var(--app-surface);
  color: var(--app-text-muted);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, border-color 0.2s ease;
}

.phase-tab:hover {
  color: var(--app-primary-light);
  border-color: var(--app-primary-border-soft);
}

.phase-tab--active {
  background: var(--app-gradient-brand);
  border-color: transparent;
  color: #fff;
}

.poster-badge {
  position: absolute;
  right: 8px;
  top: 8px;
  z-index: 2;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  pointer-events: none;
}

.films-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24px;
  padding: 0 4px;
}

.films-toolbar__info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.films-toolbar__count {
  font-size: 16px;
  color: var(--app-text-muted);
}

.films-toolbar__count em {
  font-style: normal;
  font-weight: 700;
  color: var(--app-highlight);
  font-size: 18px;
}

.films-toolbar__filter {
  padding: 5px 14px;
  border-radius: 999px;
  background: var(--app-primary-bg);
  border: 1px solid var(--app-primary-border-soft);
  color: var(--app-primary-light);
  font-size: 14px;
}

.film-grid-section {
  min-height: 200px;
}

.films-empty {
  padding: 48px 0;
}

.film-grid-wrap {
  display: grid;
  grid-template-columns: repeat(8, minmax(0, 1fr));
  column-gap: 16px;
  row-gap: 22px;
  width: 100%;
  padding-top: 20px;
}

.film-card-link {
  display: block;
  color: inherit;
  text-decoration: none;
  min-width: 0;
}

.film-card {
  border-radius: 8px;
  overflow: hidden;
  height: 100%;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.film-card-link:hover .film-card {
  transform: translateY(-4px);
}

:deep(.el-card__body) {
  padding: 0 0 12px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.poster-frame {
  position: relative;
  width: 100%;
  aspect-ratio: 2 / 3;
  overflow: hidden;
  border-radius: 4px 4px 0 0;
  background: var(--app-surface-muted);
}

.poster-score {
  position: absolute;
  right: 8px;
  top: 8px;
  z-index: 2;
  font-size: clamp(12px, 2.6vw, 15px);
  font-weight: 700;
  letter-spacing: 0.02em;
  color: #ffeb3b;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.85), 0 0 8px rgba(0, 0, 0, 0.45);
  pointer-events: none;
}

.poster-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  display: block;
}

.film-name {
  margin-top: 10px;
  padding: 0 8px 4px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.04em;
  color: var(--app-title);
  text-align: center;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.films-pagination {
  display: flex;
  justify-content: center;
  margin-top: 36px;
  padding: 20px 16px;
  border-radius: 14px;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.films-pagination :deep(.el-pagination) {
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.films-pagination :deep(.el-pagination__total),
.films-pagination :deep(.el-pagination__sizes),
.films-pagination :deep(.el-pagination__jump) {
  color: var(--app-text-muted);
}

.films-pagination :deep(.el-pager li),
.films-pagination :deep(.btn-prev),
.films-pagination :deep(.btn-next) {
  border-radius: 8px;
}

.films-pagination :deep(.el-pager li.is-active) {
  background: var(--app-gradient-brand) !important;
  border-color: transparent;
}

.films-pagination :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background: var(--app-gradient-brand);
}

.films-pagination :deep(.el-pagination .el-select .el-input__wrapper),
.films-pagination :deep(.el-pagination .el-input__wrapper) {
  border-radius: 8px;
}

:deep(.tags-panel) {
  padding: 16px 22px !important;
}

:deep(.tags-line) {
  padding: 12px 0 !important;
}

:deep(.tags-title) {
  font-size: 17px;
  font-weight: 600;
  color: var(--app-title) !important;
  line-height: 28px;
  height: auto;
}

:deep(.tags li) {
  padding: 5px 12px;
}

:deep(.tags li a) {
  font-size: 16px;
}

@media (max-width: 1400px) {
  .film-grid-wrap {
    grid-template-columns: repeat(6, minmax(0, 1fr));
  }
}

@media (max-width: 1100px) {
  .film-grid-wrap {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .film-grid-wrap {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .films-pagination {
    padding: 16px 10px;
  }

  .films-pagination :deep(.el-pagination) {
    width: 100%;
  }
}
</style>

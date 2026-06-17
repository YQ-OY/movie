<template>
  <div class="app">

    <el-carousel height="600px" indicator-position="outside">
      <el-carousel-item v-for="(item, index) in posterList" :key="index">
        <div class="banner-slide">
          <img class="banner-slide__img" alt="" :src="item.url">
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="content-row">
    <div class="main">
      <div class="movie-grid">
        <div class="panel">
          <div class="panel-header panel-header--with-more">
            <span class="panel-title">
              <span class="textcolor_title">正在上映  ( {{ showingList.length }} )</span>
            </span>
            <router-link class="panel-view-all" :to="filmsLink('showing')">
              查看全部<span class="panel-view-all__arrow" aria-hidden="true">&gt;</span>
            </router-link>
          </div>
          <div class="panel-content">
            <dl class="movie-list">
              <dd v-for="item in showingList" :key="'showing-' + item.id">
                <router-link :to="'/film/info?fid=' + item.id">
                  <div class="movie-item">
                    <div class="movie-poster">
                      <img class="poster-default" alt=""
                           :src="item.cover">
                      <img class="movie-poster-img" alt=""
                           :src="item.cover">
                      <div class="movie-overlay movie-overlay-bg">
                        <div class="movie-info">
                          <div class="movie-title" title="">{{ item.name }}</div>
                        </div>
                      </div>
                      <div class="poster-score">{{ filmAvgDisplay(item) }}</div>
                    </div>
                    <div class="movie-detail movie-wish"><span class="stonefont">{{item.duration
                      }}</span>人想看</div>
                    <div class="movie-ver"></div>
                  </div>
                  <div class="movie-detail movie-rt">{{ releaseDisplay(item.releaseTime) }} 上映</div>
                </router-link>
              </dd>
            </dl>
          </div>
        </div>

        <div class="panel panel--upcoming">
          <div class="panel-header panel-header--with-more">
            <span class="panel-title">
              <span class="textcolor_title">待上映  ( {{ upcomingList.length }} )</span>
            </span>
            <router-link class="panel-view-all" :to="filmsLink('upcoming')">
              查看全部<span class="panel-view-all__arrow" aria-hidden="true">&gt;</span>
            </router-link>
          </div>
          <div class="panel-content">
            <dl class="movie-list">
              <dd v-for="item in upcomingList" :key="'upcoming-' + item.id">
                <router-link :to="'/film/info?fid=' + item.id">
                  <div class="movie-item">
                    <div class="movie-poster">
                      <img class="poster-default" alt=""
                           :src="item.cover">
                      <img class="movie-poster-img" alt=""
                           :src="item.cover">
                      <div class="movie-overlay movie-overlay-bg">
                        <div class="movie-info">
                          <div class="movie-title" title="">{{ item.name }}</div>
                        </div>
                      </div>
                      <div class="poster-badge">即将上映</div>
                    </div>
                    <div class="movie-detail movie-wish"><span class="stonefont">{{ item.duration }}</span>分钟</div>
                    <div class="movie-ver"></div>
                  </div>
                  <div class="movie-detail movie-rt">{{ releaseDisplay(item.releaseTime) }} 上映</div>
                </router-link>
              </dd>
            </dl>
          </div>
        </div>
      </div>
    </div>

    <div class="as">

      <div class="most-expect-wrapper">
        <div class="panel">
          <div class="panel-header">
            <span class="panel-more">
              <span class="panel-arrow panel-arrow-blue"></span>
            </span>
            <span class="panel-title">
              <span class="textcolor_title">热门榜单Top10</span>
            </span>
          </div>
          <div class="panel-content">
            <ul class="ranking-wrapper ranking-mostExpect">
              <li class="ranking-item ranking-top ranking-index-1">
                <router-link :to="'/film/info?fid=' + top1.id" target="_blank">
                  <div class="ranking-top-left">
                    <i class="ranking-top-icon"></i>
                    <img class="ranking-img default-img ranking-poster" :alt="top1.name" :src="top1.cover">
                  </div>
                  <div class="ranking-top-right">
                    <div class="ranking-top-right-main">
                      <span class="ranking-top-moive-name">{{ top1.name }}</span>

                      <p class="ranking-release-time">上映时间：{{ top1.releaseTime }}</p>

                      <p class="ranking-top-wish">
                        <span class="stonefont">热度值: </span>{{ top1.hot }}
                      </p>
                    </div>
                  </div>
                </router-link>
              </li>

              <li class="ranking-item ranking-index-2">
                <router-link :to="'/film/info?fid=' + top2.id" target="_blank">
                  <i class="ranking-index">2</i>
                  <span class="img-link">
                    <img class="ranking-img default-img ranking-poster ranking-poster--thumb" :alt="top2.name" :src="top2.cover"></span>
                  <div class="name-link ranking-movie-name">{{ top2.name }}</div>
                  <span class="ranking-num-info"><span class="stonefont">热度值: </span>{{ top2.hot }}</span>
                </router-link>
              </li>

              <li class="ranking-item ranking-index-3">
                <router-link :to="'/film/info?fid=' + top3.id" target="_blank">
                  <i class="ranking-index">3</i>
                  <span class="img-link">
                    <img class="ranking-img default-img ranking-poster ranking-poster--thumb" :alt="top3.name" :src="top3.cover"></span>
                  <div class="name-link ranking-movie-name">{{ top3.name }}</div>
                  <span class="ranking-num-info"><span class="stonefont">热度值: </span>{{ top3.hot }}</span>
                </router-link>
              </li>

              <li v-for="(item, index) in topList" :key="index" class="ranking-item ranking-index-4">
                <router-link :to="'/film/info?fid=' + item.id" target="_blank">
                  <span class="normal-link">
                    <i class="ranking-index">{{ index + 4 }}</i>
                    <span class="ranking-movie-name">{{ item.name }}</span>
                    <span class="ranking-num-info"><span class="stonefont">热度值: </span>{{ item.hot }}</span>
                  </span>
                </router-link>
              </li>

            </ul>
          </div>
        </div>
      </div>

    </div>
    </div>

  </div>
</template>

<script>
import { listFilmPage, ListAllPoster, ListHots, GetFilmEvaluateAvgMap } from "@/api/film";
import { RELEASE_PHASE, formatReleaseDisplay } from "@/utils/filmRelease";

export default {

  data() {
    return {
      top1: {},
      top2: {},
      top3: {},
      topList: [],
      showingList: [],
      upcomingList: [],
      posterList: [],
      /** fid → 评分（0.5 步长） */
      avgByFid: {}
    }
  },

  methods: {
    filmAvgDisplay(item) {
      if (!item || !item.id) return '暂无评分'
      const v = this.avgByFid[item.id]
      if (v === undefined || v === null || Number.isNaN(Number(v))) return '暂无评分'
      return Number(v).toFixed(1)
    },

    releaseDisplay(releaseTime) {
      return formatReleaseDisplay(releaseTime) || releaseTime || ''
    },

    filmsLink(phase) {
      return {
        path: '/films',
        query: {
          phase,
          region: '全部',
          type: '全部',
        },
      }
    },
  },

  mounted() {
    Promise.all([
      listFilmPage({ status: true, releasePhase: RELEASE_PHASE.SHOWING, page: 1, size: 10 }),
      listFilmPage({ status: true, releasePhase: RELEASE_PHASE.UPCOMING, page: 1, size: 10 }),
      GetFilmEvaluateAvgMap()
    ]).then(([showingRes, upcomingRes, avgRes]) => {
      this.showingList = showingRes?.data?.rows || []
      this.upcomingList = upcomingRes?.data?.rows || []
      const m = avgRes?.data
      this.avgByFid = m && typeof m === 'object' && !Array.isArray(m) ? m : {}
    })
    ListAllPoster().then(res => {
      this.posterList = Array.isArray(res?.data) ? res.data : []
    })
    ListHots().then(res => {
      const list = Array.isArray(res?.data) ? res.data : []
      this.top1 = list[0] || {}
      this.top2 = list[1] || {}
      this.top3 = list[2] || {}
      this.topList = list.slice(3)
    })
  }
}
</script>

<style scoped>
@import "../assets/css/home.css";

.app {
  padding: clamp(16px, 2vw, 32px) clamp(12px, 2.5vw, 40px);
  box-sizing: border-box;
  max-width: 1680px;
  margin: 0 auto;
  overflow-x: hidden;
  background: var(--app-bg);
  min-height: calc(100vh - 72px);
}

.content-row {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding-top: 40px;
  width: 100%;
}

.main {
  flex: 1;
  min-width: 0;
  order: 1;
}

.as {
  flex: 0 0 360px;
  width: 360px;
  order: 2;
  overflow: hidden;
}

.most-expect-wrapper {
  padding-top: 0;
}

/* 轮播：裁切填充，避免拉伸变形 */
.banner-slide {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #111;
}

.banner-slide__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center top;
  display: block;
}

/* 正在热播：一排 5 个，统一 2:3 海报 */
:deep(.movie-grid) {
  margin-top: 0 !important;
}

:deep(.movie-grid .panel) {
  width: 100% !important;
  max-width: none;
  margin-top: 0 !important;
}

:deep(.movie-grid .panel-header) {
  width: 100% !important;
}

.panel-header--with-more {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: auto;
  min-height: 36px;
  line-height: 1.4;
  margin-bottom: 4px;
}

.panel-view-all {
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 500;
  color: #38ef38;
  text-decoration: none;
  letter-spacing: 0.04em;
  transition: opacity 0.2s ease;
}

.panel-view-all:hover {
  opacity: 0.82;
  text-decoration: none;
}

.panel-view-all__arrow {
  margin-left: 2px;
  font-size: 13px;
}

:deep(.movie-list) {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  column-gap: 14px;
  row-gap: 18px;
  margin: 0 !important;
}

:deep(.movie-list dd) {
  margin: 0 !important;
  display: block;
  width: 100%;
  min-width: 0;
}

:deep(.movie-list .movie-item) {
  width: 100%;
  max-width: none;
  margin: 0;
}

:deep(.movie-list .movie-poster) {
  width: 100% !important;
  max-width: none;
  aspect-ratio: 2 / 3 !important;
  height: auto !important;
  margin: 0;
}

:deep(.movie-rt),
:deep(.movie-wish) {
  font-size: 13px;
}

:deep(.movie-grid .panel-title) {
  font-size: 26px;
}

.panel--upcoming {
  margin-top: 36px;
}

.poster-badge {
  position: absolute;
  right: 6px;
  top: 6px;
  z-index: 4;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  pointer-events: none;
}

@media (max-width: 1400px) {
  :deep(.movie-list) {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 1100px) {
  .content-row {
    flex-direction: column;
  }

  .main,
  .as {
    width: 100%;
    flex: none;
  }

  :deep(.movie-list) {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  :deep(.movie-list) {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

:deep(.movie-list .movie-rt) {
  width: 100%;
}

:deep(.movie-list .movie-poster img) {
  object-fit: cover !important;
  object-position: center !important;
}

/* 榜单海报：固定尺寸，避免撑破侧边栏布局 */
:deep(.ranking-mostExpect) {
  overflow: hidden;
}

:deep(.ranking-mostExpect .ranking-index-1) {
  overflow: hidden;
}

:deep(.ranking-mostExpect .ranking-top-left) {
  aspect-ratio: 2 / 3 !important;
  width: 140px !important;
  height: auto !important;
  max-height: 194px;
  overflow: hidden;
  float: left;
}

:deep(.ranking-mostExpect .ranking-index-1 .ranking-poster) {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
  object-position: center !important;
}

:deep(.ranking-mostExpect .ranking-index-2),
:deep(.ranking-mostExpect .ranking-index-3) {
  width: calc(50% - 10px) !important;
  max-width: 170px;
  box-sizing: border-box;
  float: left;
}

:deep(.ranking-mostExpect .ranking-index-3) {
  float: right;
}

:deep(.ranking-mostExpect .ranking-index-2 .img-link),
:deep(.ranking-mostExpect .ranking-index-3 .img-link) {
  display: block;
  width: 100%;
  aspect-ratio: 2 / 3;
  height: auto;
  overflow: hidden;
}

:deep(.ranking-mostExpect .ranking-index-2 .ranking-poster--thumb),
:deep(.ranking-mostExpect .ranking-index-3 .ranking-poster--thumb) {
  width: 100% !important;
  height: 100% !important;
  min-height: 0 !important;
  object-fit: cover !important;
  object-position: center !important;
}

/* 封面左下角评分（盖住海报区域底层，叠在遮罩之上） */
.poster-score {
  position: absolute;
  right: 6px;
  top: 6px;
  left: auto;
  bottom: auto;
  z-index: 4;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
  color: #ffeb3b;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.85), 0 0 8px rgba(0, 0, 0, 0.45);
  pointer-events: none;
}
</style>
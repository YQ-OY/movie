<template>
  <div class="films-page">
    <div class="tags-panel">
      <ul class="tags-lines">
        <li class="tags-line">
          <div class="tags-title">类型 :</div>
          <ul class="tags">
            <li v-for="item in typeList" :key="'t-' + item" :class="selectType===item ? 'active' : ''" @click="handleTypeSelect(item)">
              <router-link :to=" '/films?region='+ selectRegion + '&type=' + item ">{{ item }}</router-link>
            </li>
          </ul>
        </li>
        <li class="tags-line">
          <div class="tags-title">地区 :</div>
          <ul class="tags">
            <li v-for="item in regionList" :key="'r-' + item" :class="selectRegion===item ? 'active' : ''"
                @click="handleRegionSelect(item)">
              <router-link :to=" '/films?region='+ item + '&type=' + selectType ">{{ item }}</router-link>
            </li>
          </ul>
        </li>
      </ul>
    </div>

    <div class="film-grid-wrap">
      <router-link
        v-for="item in filmList"
        :key="item.id"
        class="film-card-link"
        :to="'/film/info?fid=' + item.id"
      >
        <el-card class="film-card" shadow="hover">
          <div class="poster-frame">
            <img class="poster-img" :src="item.cover" :alt="item.name">
            <span class="poster-score">{{ filmAvgDisplay(item) }}</span>
          </div>
          <div class="film-name" :title="item.name">{{ item.name }}</div>
        </el-card>
      </router-link>
    </div>
  </div>
</template>

<script>

import {FindFilmByRegionAndType, ListAllFilm, GetFilmEvaluateAvgMap} from "@/api/film";

export default {

  name: "Films",

  data() {
    return {
      selectType: '全部',
      selectRegion: '全部',
      typeList: ["全部", "家庭", "惊悚", "科幻", "爱情", "动作", "喜剧", "恐怖", "悬疑", "犯罪", "冒险", "战争", "历史", "武侠", "传记", "古装", "其他"],
      regionList: ["全部", "内地", "香港", "台湾", "美国", "韩国", "日本", "泰国", "印度", "法国", "英国", "德国", "其他"],
      filmList: [],
      avgByFid: {},
    }
  },

  mounted() {
    Promise.all([
      ListAllFilm(),
      GetFilmEvaluateAvgMap()
    ]).then(([filmsRes, avgRes]) => {
      this.filmList = Array.isArray(filmsRes?.data) ? filmsRes.data : []
      const m = avgRes?.data
      this.avgByFid = m && typeof m === 'object' && !Array.isArray(m) ? m : {}
    })
  },


  methods: {

    filmAvgDisplay(item) {
      if (!item || !item.id) return '暂无评分'
      const v = this.avgByFid[item.id]
      if (v === undefined || v === null || Number.isNaN(Number(v))) return '暂无评分'
      return Number(v).toFixed(1)
    },

    handleTypeSelect(item) {
      this.selectType = item;
      this.reloadFilmList()
    },

    handleRegionSelect(item) {
      this.selectRegion = item;
      this.reloadFilmList()
    },

    reloadFilmList() {
      FindFilmByRegionAndType(this.selectRegion, this.selectType).then(res => {
        this.filmList = Array.isArray(res?.data) ? res.data : []
      })
    },

  },

}
</script>

<style scoped>
@import "../assets/css/movie-list.css";

.films-page {
  padding: clamp(16px, 4vw, 24px) clamp(16px, 6vw, 120px) 48px;
  box-sizing: border-box;
  max-width: 1280px;
  margin: 0 auto;
}

.film-grid-wrap {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(128px, 1fr));
  gap: clamp(16px, 2.5vw, 24px);
  padding-top: 32px;
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
  background: #eceef2;
}

.poster-score {
  position: absolute;
  right: 8px;
  top: 8px;
  left: auto;
  bottom: auto;
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
  vertical-align: top;
}

.film-name {
  margin-top: 10px;
  padding: 0 10px 4px;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.04em;
  color: #e85d04;
  text-align: center;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>

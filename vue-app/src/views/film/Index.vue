<template>
  <div class="film-page">
    <section class="film-hero">
      <div class="film-hero__bg" aria-hidden="true" />
      <div class="film-hero__inner">
        <div class="film-hero__poster">
          <div class="poster-shell">
            <img
              v-if="film.cover"
              class="poster-shell__img"
              :src="film.cover"
              :alt="film.name || '电影海报'"
            >
          </div>
        </div>
        <div class="film-hero__meta">
          <h1 class="film-title">{{ film.name }}</h1>
          <div class="film-rate-row">
            <template v-if="avgScore != null">
              <el-rate
                :model-value="avgScore"
                disabled
                allow-half
                show-score
                text-color="#ff9900"
                score-template="{value}"
              />
            </template>
            <span v-else class="film-no-score">暂无评分</span>
          </div>
          <p class="film-line">电影类型：{{ formatFilmTypes(film.type) }}</p>
          <p class="film-line">{{ film.region }} / {{ film.duration }} 分钟</p>
          <p class="film-line">上映日期：{{ film.releaseTime }} 上映</p>
          <div class="film-actions">
            <router-link v-if="!isUpcomingFilm" :to="'/film/ticket?fid=' + filmId">
              <el-button type="primary" class="film-btn film-btn--buy">
                <el-icon class="film-btn__icon"><Coin /></el-icon>
                特惠购票
              </el-button>
            </router-link>
            <el-button
              v-else
              type="primary"
              class="film-btn film-btn--buy"
              disabled
            >
              即将上映
            </el-button>
            <el-button type="primary" plain class="film-btn film-btn--rate" @click="openComment">
              <el-icon class="film-btn__icon"><StarFilled /></el-icon>
              评分
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <div class="film-body">
      <router-view />
    </div>

    <el-dialog
      :show-close="false"
      title="电影评分"
      v-model="dialogVisible"
      width="30%"
    >
      <div class="block">
        <el-rate
          v-model="form.star"
          :colors="colors"
        />
      </div>
      <el-input
        style="margin-top: 40px"
        type="textarea"
        :rows="8"
        placeholder="请输入内容"
        v-model="form.comment"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitComment">确 认 提 交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { AddFilmEvaluate, FindFilmById, GetFilmEvaluateAvg } from "@/api/film";
import { Coin, StarFilled } from '@element-plus/icons-vue'
import { isUpcoming } from '@/utils/filmRelease'
import { formatFilmTypes } from '@/utils/filmType'

export default {
  components: { Coin, StarFilled },
  name: "Film",
  data() {
    return {
      value2: null,
      colors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      filmId: this.$route.query.fid,
      /** 来自 film_evaluate 均值，0.5 步长；无评价为 null */
      avgScore: null,
      list: [{}, {}, {}],
      dialogVisible: false,
      film: {},
      evaluateList: [],
      form: {
        fid: '',
        uid: '',
        star: 5,
        comment: '',
      },
    }
  },

  computed: {
    isUpcomingFilm() {
      return isUpcoming(this.film.releaseTime)
    },
  },

  methods: {
    formatFilmTypes,

    openComment() {
      if (!localStorage.getItem("uid")) {
        this.$confirm('系统还没有检测到您的登陆信息, 是否去登录?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$router.push("/login")
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消为电影评分'
          });
        });
      } else {
        this.dialogVisible = true
      }
    },

    submitComment() {
      this.form.fid = this.filmId;
      this.form.uid = localStorage.getItem("uid");
      const form = this.form
      AddFilmEvaluate(form).then(res => {
        this.dialogVisible = false;
        if (res.success) {
          this.loadAvgScore()
        }
      })
    },

    loadAvgScore() {
      if (!this.filmId) return
      GetFilmEvaluateAvg(this.filmId).then((res) => {
        if (res && res.success) {
          const v = res.data
          this.avgScore = v === null || v === undefined || Number.isNaN(Number(v)) ? null : Number(v)
        }
      })
    },

  },

  mounted() {
    FindFilmById(this.filmId).then(res => {
      this.film = (res && res.data) ? res.data : {}
    })
    this.loadAvgScore()
  }
}
</script>

<style scoped>
/* 详情页头：与主流票务站类似，海报固定 2:3，缩放窗口时裁切一致、不出现奇怪拉伸 */
.film-page {
  min-height: 100%;
  box-sizing: border-box;
}

.film-hero {
  position: relative;
  padding: clamp(20px, 4vw, 40px) clamp(16px, 4vw, 40px) clamp(32px, 5vw, 56px);
  overflow: hidden;
}

.film-hero__bg {
  position: absolute;
  inset: 0;
  background: var(--app-gradient-hero);
}

.film-hero__inner {
  position: relative;
  z-index: 1;
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: clamp(20px, 4vw, 40px);
}

.film-hero__poster {
  flex-shrink: 0;
}

.poster-shell {
  width: clamp(132px, 26vw, 240px);
  aspect-ratio: 2 / 3;
  border-radius: 12px;
  overflow: hidden;
  background: #1a1d28;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35);
}

.poster-shell__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  display: block;
  vertical-align: top;
}

.film-hero__meta {
  flex: 1 1 240px;
  min-width: 0;
  color: #fff;
  padding-bottom: 4px;
}

.film-title {
  margin: 0 0 12px;
  font-size: clamp(22px, 4vw, 32px);
  font-weight: 700;
  letter-spacing: 0.04em;
  line-height: 1.25;
}

.film-rate-row {
  margin-bottom: 8px;
}

.film-no-score {
  font-size: clamp(14px, 2.5vw, 18px);
  opacity: 0.92;
  letter-spacing: 0.08em;
}

.film-line {
  margin: 10px 0 0;
  font-size: 14px;
  opacity: 0.95;
  letter-spacing: 0.06em;
}

.film-actions {
  margin-top: clamp(20px, 3vw, 28px);
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.film-btn {
  min-width: 130px;
  letter-spacing: 0.12em;
}

.film-btn__icon {
  margin-right: 6px;
  font-size: 15px;
  vertical-align: middle;
}

.film-btn--buy {
  background: linear-gradient(135deg, var(--app-price-dark) 0%, var(--app-price) 55%, #f87171 100%) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 8px 22px rgba(239, 68, 68, 0.38);
}

.film-btn--buy:hover {
  filter: brightness(1.06);
}

.film-btn--rate {
  --el-button-text-color: rgba(255, 255, 255);
  --el-button-hover-text-color:rgba(255, 255, 255);
  --el-button-bg-color: rgba(255, 255, 255, 0.2);
}


.film-body {
  max-width: 1100px;
  margin: 0 auto;
  padding: clamp(24px, 4vw, 48px) clamp(16px, 4vw, 40px) 64px;
  letter-spacing: 0.06em;
  box-sizing: border-box;
}

:deep(.el-rate__icon) {
  font-size: 22px;
}

:deep(.el-rate__text) {
  padding-left: 8px;
  font-size: clamp(18px, 3vw, 26px);
  color: #fff !important;
}
</style>

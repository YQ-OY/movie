<template>
  <div class="activity-page">
    <div class="activity-page__inner">
      <header class="activity-hero">
        <div class="activity-hero__left">
          <div class="activity-hero__icon" aria-hidden="true">
            <el-icon :size="28"><Flag /></el-icon>
          </div>
          <div>
            <div class="activity-hero__tags">
              <el-tag type="danger" effect="dark" size="small" round>热门</el-tag>
              <el-tag type="warning" effect="plain" size="small" round>限时报名</el-tag>
            </div>
            <h1 class="activity-hero__title">活动中心</h1>
            <p class="activity-hero__sub">精选主题活动 · 在线报名</p>
          </div>
        </div>
        <div class="activity-hero__stats">
          <div class="hero-stat hero-stat--blue">
            <el-icon><Collection /></el-icon>
            <span class="hero-stat__num">{{ list.length }}</span>
            <span class="hero-stat__lbl">场活动</span>
          </div>
          <div class="hero-stat hero-stat--orange">
            <el-icon><StarFilled /></el-icon>
            <span class="hero-stat__lbl">会员专享</span>
          </div>
        </div>
      </header>

      <div
          v-loading="loading"
          element-loading-text="加载中…"
          class="activity-body"
      >
        <el-empty v-if="!loading && list.length === 0" description="暂无活动，敬请期待" />

        <el-timeline v-else class="activity-timeline">
          <el-timeline-item
              v-for="(item, index) in list"
              :key="item.id || index"
              :timestamp="''"
              placement="top"
              type="primary"
              hollow
          >
            <template #dot>
              <span class="timeline-dot" :class="'timeline-dot--' + (index % 3)"/>
            </template>
            <div class="timeline-row">
              <span class="timeline-date">{{ formatDateLabel(item.startTime) }}</span>
              <el-tag
                  :type="statusTag(item).type"
                  effect="light"
                  size="small"
                  round
                  class="timeline-status"
              >{{ statusTag(item).text }}</el-tag>
            </div>

            <el-card
                class="activity-card"
                shadow="hover"
                :style="{ animationDelay: Math.min(index * 35, 200) + 'ms' }"
            >
              <div class="activity-card__accent" :class="'activity-card__accent--' + (index % 3)"/>
              <div class="activity-card__main">
                <h3 class="activity-card__title">{{ titlePart(item.content) }}</h3>
                <div
                    v-if="descPart(item.content)"
                    class="activity-card__desc"
                >{{ descPart(item.content) }}</div>

                <div class="activity-card__meta">
                  <span class="meta-pill meta-pill--start">
                    <el-icon><Calendar /></el-icon>
                    开始 {{ item.startTime || '—' }}
                  </span>
                  <span class="meta-pill meta-pill--end">
                    <el-icon><Timer /></el-icon>
                    结束 {{ item.endTime || '—' }}
                  </span>
                  <span
                      v-if="item.number != null"
                      class="meta-pill meta-pill--quota"
                  >名额 {{ item.number }}</span>
                </div>
              </div>

              <div class="activity-card__footer">
                <el-button type="primary" round @click="handleRegister(item)">
                  <el-icon class="btn-icon"><Promotion /></el-icon>
                  报名参加
                </el-button>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>

<script>
import { ListAllActivity, SubmitActive } from '@/api/active'

export default {
  name: 'ActiveView',

  data() {
    return {
      list: [],
      loading: true,
      form: {
        aid: '',
        uid: '',
      },
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    load() {
      this.loading = true
      ListAllActivity()
          .then(res => {
            if (res.success) {
              this.list = res.data || []
            }
          })
          .finally(() => {
            this.loading = false
          })
    },

    titlePart(content) {
      if (!content || !String(content).trim()) {
        return '活动'
      }
      const s = String(content).trim()
      const i = s.indexOf('\n')
      return i < 0 ? s : s.slice(0, i).trim()
    },

    descPart(content) {
      if (!content) return ''
      const s = String(content).trim()
      const i = s.indexOf('\n')
      return i < 0 ? '' : s.slice(i + 1).trim()
    },

    formatDateLabel(t) {
      if (!t) return ''
      return String(t).slice(0, 10)
    },

    /** 与当前日期比较（按本地日 0 点） */
    statusTag(item) {
      const dayStart = (d) => {
        const x = new Date(d)
        x.setHours(0, 0, 0, 0)
        return x.getTime()
      }
      const parse = (s) => {
        if (!s) return null
        const d = new Date(String(s).replace(/-/g, '/'))
        return Number.isNaN(d.getTime()) ? null : dayStart(d)
      }
      const today = dayStart(new Date())
      const endDay = parse(item.endTime)
      const startDay = parse(item.startTime)
      if (endDay != null && today > endDay) {
        return { text: '已结束', type: 'info' }
      }
      if (startDay != null && today < startDay) {
        return { text: '未开始', type: 'warning' }
      }
      return { text: '进行中', type: 'success' }
    },

    handleRegister(item) {
      if (!localStorage.getItem('uid')) {
        this.$confirm('系统还没有检测到您的登陆信息, 是否去登录?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          this.$router.push('/login')
        }).catch(() => {})
      } else {
        this.form.aid = item.id
        this.form.uid = localStorage.getItem('uid')
        SubmitActive(this.form).then(res => {
          if (res.success) {
            this.$message({
              message: '活动报名成功！',
              type: 'success',
            })
          }
        })
      }
    },
  },
}
</script>

<style scoped>
.activity-page {
  min-height: calc(100vh - 140px);
  padding: 80px 8% 64px;
  box-sizing: border-box;
  background: #ffffff;
}

.activity-page__inner {
  max-width: 900px;
  margin: 0 auto;
}

.activity-hero {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px 24px;
  margin-bottom: 28px;
  padding: 18px 22px;
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid rgba(64, 158, 255, 0.2);
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.08), 0 2px 8px rgba(255, 140, 66, 0.06);
  transition: box-shadow 0.12s ease, transform 0.12s ease;
}

.activity-hero:hover {
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.12), 0 4px 12px rgba(255, 140, 66, 0.08);
}

.activity-hero__left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.activity-hero__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.activity-hero__icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 45%, #79bbff 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.45);
  transition: transform 0.12s ease;
}

.activity-hero__icon:hover {
  transform: scale(1.04);
}

.activity-hero__title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 3px;
  background: linear-gradient(90deg, #303133 0%, #409eff 55%, #e6a23c 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.activity-hero__sub {
  margin: 6px 0 0;
  font-size: 13px;
  color: #909399;
  letter-spacing: 1px;
}

.activity-hero__stats {
  display: flex;
  gap: 12px;
}

.hero-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 88px;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 12px;
  transition: transform 0.1s ease;
}

.hero-stat:hover {
  transform: translateY(-2px);
}

.hero-stat--blue {
  background: linear-gradient(145deg, #ecf5ff, #d9ecff);
  color: #409eff;
  border: 1px solid rgba(64, 158, 255, 0.25);
}

.hero-stat--orange {
  background: linear-gradient(145deg, #fff4e6, #fdebd0);
  color: #e6a23c;
  border: 1px solid rgba(230, 162, 60, 0.35);
}

.hero-stat .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.hero-stat__num {
  font-size: 20px;
  font-weight: 700;
  line-height: 1.2;
}

.hero-stat__lbl {
  margin-top: 2px;
  opacity: 0.9;
}

.activity-body {
  min-height: 200px;
}

.activity-body :deep(.el-loading-mask) {
  transition: opacity 0.15s ease;
}

.activity-timeline {
  padding-left: 2px;
}

.activity-timeline :deep(.el-timeline-item__tail) {
  border-left: 2px solid #c5dcf7;
  opacity: 0.95;
}

.timeline-dot {
  display: block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-sizing: border-box;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.06);
  transition: transform 0.12s ease;
}

.timeline-dot--0 {
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.timeline-dot--1 {
  background: linear-gradient(135deg, #e6a23c, #f0c78a);
}

.timeline-dot--2 {
  background: linear-gradient(135deg, #b37feb, #d3adf7);
}

.activity-timeline :deep(.el-timeline-item:hover) .timeline-dot {
  transform: scale(1.15);
}

.timeline-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.timeline-date {
  font-size: 14px;
  font-weight: 700;
  color: #303133;
  letter-spacing: 1px;
}

.timeline-status {
  font-weight: 600;
}

.activity-card {
  position: relative;
  border-radius: 14px;
  border: 1px solid rgba(235, 238, 245, 0.95);
  overflow: hidden;
  background: #ffffff;
  transition: border-color 0.12s ease, box-shadow 0.12s ease, transform 0.12s ease;
  animation: card-in 0.22s ease-out both;
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.activity-card:hover {
  border-color: rgba(64, 158, 255, 0.35);
  box-shadow: 0 8px 28px rgba(64, 158, 255, 0.12), 0 4px 12px rgba(230, 162, 60, 0.08);
  transform: translateY(-1px);
}

.activity-card__accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
}

.activity-card__accent--0 {
  background: linear-gradient(180deg, #409eff, #79bbff);
}

.activity-card__accent--1 {
  background: linear-gradient(180deg, #e6a23c, #f3d19e);
}

.activity-card__accent--2 {
  background: linear-gradient(180deg, #b37feb, #d3adf7);
}

.activity-card :deep(.el-card__body) {
  padding: 0;
}

.activity-card__main {
  padding: 18px 22px 14px 20px;
}

.activity-card__title {
  margin: 0 0 10px;
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  letter-spacing: 1px;
  line-height: 1.45;
}

.activity-card__desc {
  margin: 0 0 14px;
  padding: 12px 14px;
  font-size: 14px;
  color: #5c6b7a;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
  background: linear-gradient(90deg, #f8fafc 0%, #fff9f4 100%);
  border-radius: 10px;
  border-left: 3px solid #e6a23c;
}

.activity-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
  transition: transform 0.1s ease;
}

.meta-pill:hover {
  transform: scale(1.02);
}

.meta-pill .el-icon {
  font-size: 14px;
}

.meta-pill--start {
  background: #ecf5ff;
  color: #409eff;
  border: 1px solid rgba(64, 158, 255, 0.25);
}

.meta-pill--end {
  background: #f4f1ff;
  color: #8e6be8;
  border: 1px solid rgba(179, 127, 235, 0.25);
}

.meta-pill--quota {
  background: #fdf6ec;
  color: #c97c12;
  border: 1px solid rgba(230, 162, 60, 0.35);
}

.activity-card__footer {
  padding: 12px 20px;
  background: #fafafa;
  border-top: 1px solid #f0f2f5;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.activity-card__footer :deep(.el-button) {
  transition: transform 0.1s ease, box-shadow 0.1s ease;
}

.activity-card__footer :deep(.el-button:hover) {
  transform: scale(1.02);
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.35);
}

.btn-icon {
  margin-right: 4px;
  vertical-align: middle;
}
</style>

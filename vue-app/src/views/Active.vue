<template>
  <div class="activity-page app-page">
    <div class="activity-page__inner app-page__inner">
      <header class="activity-hero app-hero">
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
          <div class="hero-stat hero-stat--accent">
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
                  effect="dark"
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
.activity-page__inner {
  max-width: 900px;
}

.activity-hero.app-hero {
  background: rgba(24, 24, 24, 0.55);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
}

.activity-hero:hover {
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.3);
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
  background: var(--app-gradient-brand);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 14px var(--app-primary-shadow-strong);
  transition: transform 0.12s ease;
}

.activity-hero__icon:hover {
  transform: scale(1.04);
}

.activity-hero__title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 2px;
  color: var(--app-title);
}

.activity-hero__sub {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--app-text-muted);
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
  background: rgba(24, 24, 24, 0.52);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.hero-stat--blue {
  color: var(--app-primary);
}

.hero-stat--accent {
  color: var(--app-primary);
}

.hero-stat--accent .hero-stat__lbl {
  color: var(--app-text-secondary);
}

.hero-stat:hover {
  transform: translateY(-2px);
}

.hero-stat--blue .hero-stat__num {
  color: var(--app-highlight);
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
  border-left: 2px solid rgba(255, 255, 255, 0.12);
  opacity: 0.95;
}

.timeline-dot {
  display: block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.25);
  box-sizing: border-box;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.2);
  transition: transform 0.12s ease;
}

.timeline-dot--0 {
  background: linear-gradient(135deg, var(--app-primary), var(--app-primary-light));
}

.timeline-dot--1 {
  background: linear-gradient(135deg, var(--app-primary-light), #5ce07a);
}

.timeline-dot--2 {
  background: linear-gradient(135deg, #8b5cf6, #a78bfa);
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
  color: var(--app-title);
  letter-spacing: 1px;
}

.timeline-status {
  font-weight: 600;
}

.activity-card {
  position: relative;
  --el-card-bg-color: transparent;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  overflow: hidden;
  background: rgba(24, 24, 24, 0.58) !important;
  backdrop-filter: blur(16px) saturate(1.1);
  -webkit-backdrop-filter: blur(16px) saturate(1.1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
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
  border-color: rgba(0, 204, 54, 0.35) !important;
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.35);
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
  background: linear-gradient(180deg, var(--app-primary), var(--app-primary-light));
}

.activity-card__accent--1 {
  background: linear-gradient(180deg, var(--app-primary-light), #60a5fa);
}

.activity-card__accent--2 {
  background: linear-gradient(180deg, var(--app-primary-dark), var(--app-primary));
}

.activity-card :deep(.el-card__body) {
  padding: 0;
  background: transparent;
}

.activity-card__main {
  padding: 18px 22px 14px 20px;
}

.activity-card__title {
  margin: 0 0 10px;
  font-size: 18px;
  font-weight: 700;
  color: var(--app-title);
  letter-spacing: 0.04em;
  line-height: 1.45;
}

.activity-card__desc {
  margin: 0 0 14px;
  padding: 12px 14px;
  font-size: 14px;
  color: var(--app-text-secondary);
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 10px;
  border-left: 3px solid var(--app-primary);
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
  background: rgba(0, 204, 54, 0.12);
  color: var(--app-primary-light);
  border: 1px solid rgba(0, 204, 54, 0.25);
}

.meta-pill--end {
  background: rgba(139, 92, 246, 0.12);
  color: #c4b5fd;
  border: 1px solid rgba(139, 92, 246, 0.25);
}

.meta-pill--quota {
  background: rgba(0, 204, 54, 0.1);
  color: var(--app-primary);
  border: 1px solid rgba(0, 204, 54, 0.2);
}

.activity-card__footer {
  padding: 12px 20px;
  background: rgba(0, 0, 0, 0.18);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.activity-card__footer :deep(.el-button) {
  transition: transform 0.1s ease, box-shadow 0.1s ease;
}

.activity-card__footer :deep(.el-button:hover) {
  transform: scale(1.02);
  box-shadow: 0 4px 14px var(--app-primary-shadow-strong);
}

.btn-icon {
  margin-right: 4px;
  vertical-align: middle;
}
</style>

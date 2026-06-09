<template>
  <div class="film-list">

    <!-- 页面头部（与电影管理页面完全一致） -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">运营概览</div>
          <div class="page-subtitle">影院售票关键指标与趋势（数据来自订单、影片、用户汇总）</div>
        </div>
      </div>
    </div>

    <!-- 关键指标卡片区域 -->
    <div class="stats-grid">
      <div v-if="stats" class="kpi-wrapper">
        <div class="kpi-item kpi-item--blue">
          <div class="kpi-label">订单总数</div>
          <div class="kpi-value">{{ stats.orderTotal }}</div>
        </div>
        <div class="kpi-item kpi-item--green">
          <div class="kpi-label">已支付订单</div>
          <div class="kpi-value">{{ stats.orderPaid }}</div>
        </div>
        <div class="kpi-item kpi-item--orange">
          <div class="kpi-label">累计票房（元）</div>
          <div class="kpi-value">{{ stats.revenueTotal }}</div>
        </div>
        <div class="kpi-item kpi-item--purple">
          <div class="kpi-label">用户 / 影片</div>
          <div class="kpi-value kpi-value--sm">{{ stats.userTotal }} / {{ stats.filmTotal }}</div>
        </div>
      </div>
    </div>

    <!-- 图表卡片区域 -->
    <div class="table-card">
      <el-row :gutter="20" class="chart-row">
        <el-col :xs="24" :lg="10">
          <div class="chart-card">
            <div class="chart-card__header">订单状态分布</div>
            <div ref="pieRef" class="chart-box" />
          </div>
        </el-col>
        <el-col :xs="24" :lg="14">
          <div class="chart-card">
            <div class="chart-card__header">影片已支付票房 TOP（元）</div>
            <div ref="barRef" class="chart-box chart-box--tall" />
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <div class="chart-card">
            <div class="chart-card__header">近 14 日下单量趋势</div>
            <div ref="lineRef" class="chart-box chart-box--wide" />
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboard } from '../../api/stats'

export default {
  name: 'StatsDashboard',
  data() {
    return {
      stats: null,
      pieChart: null,
      barChart: null,
      lineChart: null,
    }
  },
  mounted() {
    this.load()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resizeCharts)
    this.disposeCharts()
  },
  methods: {
    load() {
      getDashboard()
        .then((res) => {
          if (res.success && res.data) {
            this.stats = res.data
            this.$nextTick(() => this.initCharts())
          }
        })
        .catch(() => { })
    },
    resizeCharts() {
      this.pieChart?.resize()
      this.barChart?.resize()
      this.lineChart?.resize()
    },
    disposeCharts() {
      this.pieChart?.dispose()
      this.barChart?.dispose()
      this.lineChart?.dispose()
      this.pieChart = this.barChart = this.lineChart = null
    },
    initCharts() {
      if (!this.stats) return
      this.disposeCharts()

      // 饼图
      const pie = echarts.init(this.$refs.pieRef)
      pie.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0, itemWidth: 12, itemHeight: 12 },
        color: ['#67C23A', '#F56C6C', '#E6A23C', '#909399'],
        series: [{
          type: 'pie',
          radius: ['42%', '68%'],
          avoidLabelOverlap: true,
          itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
          label: { formatter: '{b}\n{c} ({d}%)' },
          data: (this.stats.orderStatusPie || []).map((x) => ({ name: x.name, value: x.value })),
        }],
      })
      this.pieChart = pie

      // 条形图
      const names = (this.stats.topFilmsByRevenue || []).map((x) => x.name)
      const vals = (this.stats.topFilmsByRevenue || []).map((x) => Number(x.value))
      const bar = echarts.init(this.$refs.barRef)
      if (!names.length) {
        bar.setOption({
          graphic: {
            type: 'text',
            left: 'center',
            top: 'middle',
            style: { text: '暂无已支付票房数据', fill: '#909399', fontSize: 14 },
          },
        })
      } else {
        bar.setOption({
          tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
          grid: { left: 12, right: 24, top: 16, bottom: 8, containLabel: true },
          xAxis: { type: 'value', name: '元', nameLocation: 'middle', nameGap: 30 },
          yAxis: {
            type: 'category',
            data: names,
            inverse: true,
            axisLabel: { width: 100, overflow: 'truncate', fontSize: 12 },
          },
          series: [{
            type: 'bar',
            data: vals,
            barMaxWidth: 28,
            itemStyle: {
              borderRadius: [0, 6, 6, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#409EFF' },
                { offset: 1, color: '#79bbff' },
              ]),
            },
          }],
        })
      }
      this.barChart = bar

      // 折线图
      const days = (this.stats.ordersByDay || []).map((x) => x.name)
      const counts = (this.stats.ordersByDay || []).map((x) => Number(x.value))
      const line = echarts.init(this.$refs.lineRef)
      line.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 56, right: 28, top: 40, bottom: 28, containLabel: true },
        xAxis: {
          type: 'category',
          data: days,
          axisLabel: {
            rotate: 40,
            fontSize: 11,
            interval: 0,
            margin: 12,
          },
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          name: '单',
          nameGap: 22,
        },
        series: [{
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          data: counts,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64,158,255,0.35)' },
              { offset: 1, color: 'rgba(64,158,255,0.02)' },
            ]),
          },
          lineStyle: { width: 3, color: '#409EFF' },
          itemStyle: { color: '#409EFF', borderColor: '#fff', borderWidth: 2 },
        }],
      })
      this.lineChart = line
    },
  },
}
</script>

<style scoped>
/* ===== 全局复用 List.vue 的布局样式 ===== */
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

/* 关键指标卡片网格 */
.stats-grid {
  flex: 0 0 auto;
}

.kpi-wrapper {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.kpi-item {
  background: #ffffff;
  border-radius: 18px;
  padding: 18px 12px;
  text-align: center;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  transition: transform 0.2s;
}

.kpi-item:hover {
  transform: translateY(-2px);
}

.kpi-label {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 8px;
  font-weight: 600;
}

.kpi-value {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
}

.kpi-value--sm {
  font-size: 22px;
}

/* 不同卡片的渐变背景（保持与原有风格一致） */
.kpi-item--blue {
  background: linear-gradient(135deg, #ecf5ff 0%, #ffffff 80%);
}

.kpi-item--green {
  background: linear-gradient(135deg, #f0f9eb 0%, #ffffff 80%);
}

.kpi-item--orange {
  background: linear-gradient(135deg, #fdf6ec 0%, #ffffff 80%);
}

.kpi-item--purple {
  background: linear-gradient(135deg, #f4f1ff 0%, #ffffff 80%);
}

/* 图表卡片容器（复用 table-card 风格） */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 20px 20px 8px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #eef2f7;
  padding: 12px;
  transition: box-shadow 0.2s;
}

.chart-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
}

.chart-card__header {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  padding-bottom: 12px;
  border-bottom: 1px solid #eef2f7;
  margin-bottom: 12px;
}

.chart-box {
  height: 280px;
  width: 100%;
}

.chart-box--tall {
  height: 320px;
}

.chart-box--wide {
  height: 340px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .film-list {
    padding: 12px;
  }

  .kpi-wrapper {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .chart-box {
    height: 240px;
  }

  .chart-box--tall {
    height: 280px;
  }

  .chart-box--wide {
    height: 280px;
  }
}
</style>
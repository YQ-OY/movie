<template>
  <div class="film-list">

    <!-- 页面头部（与电影管理页面完全一致） -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">运营数据大屏</div>
          <div class="page-subtitle">整合影片、订单、营收、用户、场次等核心指标，实时掌握经营趋势与转化效率。</div>
        </div>
        <div class="search-form">
          <el-button type="primary" class="search-submit-btn" @click="exportReport">
            <el-icon>
              <Download />
            </el-icon><span>导出报表</span>
          </el-button>
          <div class="update-meta">
            <span>更新时间</span>
            <strong>{{ updatedAt || '未更新' }}</strong>
          </div>
        </div>
      </div>
    </div>

    <!-- KPI 卡片区域（复用 table-card 风格） -->
    <div class="stats-grid">
      <div v-for="item in kpiCards" :key="item.key" class="kpi-card" :class="`kpi-card--${item.color}`">
        <div class="kpi-card__label">
          <i :class="item.icon"></i>
          <span>{{ item.label }}</span>
        </div>
        <div class="kpi-card__value">{{ item.value }}</div>
        <div class="kpi-card__hint">{{ item.hint }}</div>
      </div>
    </div>

    <!-- 图表卡片容器（复用 table-card 风格） -->
    <div class="table-card">
      <div class="dashboard-grid">
        <!-- 左侧列：票房 TOP10 + 漏斗 -->
        <div class="dashboard-column dashboard-column--left">
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>票房 TOP10</h3>
                <p>按已支付订单聚合的影片票房排行</p>
              </div>
              <span class="chart-tag">Top 10</span>
            </div>
            <div ref="barRef" class="chart chart--bar" />
          </div>
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>购票转化漏斗</h3>
                <p>从浏览到支付的关键转化路径</p>
              </div>
              <span class="chart-tag">Funnel</span>
            </div>
            <div ref="funnelRef" class="chart chart--funnel" />
          </div>
        </div>

        <!-- 中间列：订单+营收趋势 + 热力图 -->
        <div class="dashboard-column dashboard-column--center">
          <div class="chart-card chart-card--highlight">
            <div class="chart-card__header">
              <div>
                <h3>近 7 天订单 + 营收</h3>
                <p>双轴联动展示订单量与票房收入变化</p>
              </div>
              <span class="chart-tag chart-tag--accent">Trend</span>
            </div>
            <div ref="lineRef" class="chart chart--line" />
          </div>
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>当日时段购票热力图</h3>
                <p>观察一天内购票高峰时段分布</p>
              </div>
              <span class="chart-tag">Heatmap</span>
            </div>
            <div ref="heatmapRef" class="chart chart--heatmap" />
          </div>
        </div>

        <!-- 右侧列：影片类型营收 + 订单状态环形图 -->
        <div class="dashboard-column dashboard-column--right">
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>影片类型营收</h3>
                <p>按影片类型统计支付营收占比</p>
              </div>
              <span class="chart-tag">Pie</span>
            </div>
            <div ref="typePieRef" class="chart chart--pie" />
          </div>
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>订单状态环形图</h3>
                <p>全量订单状态分布概览</p>
              </div>
              <span class="chart-tag">Ring</span>
            </div>
            <div ref="statusPieRef" class="chart chart--pie" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboard } from '../../api/stats'
import { Download } from '@element-plus/icons-vue'

export default {
  name: 'StatsDashboard',
  data() {
    return {
      loading: false,
      updatedAt: '',
      stats: null,
      barChart: null,
      funnelChart: null,
      lineChart: null,
      heatmapChart: null,
      typePieChart: null,
      statusPieChart: null,
    }
  },
  computed: {
    kpiCards() {
      const s = this.stats || {}
      return [
        { key: 'filmTotal', label: '总影片数', value: s.filmTotal ?? 0, hint: '当前入库影片总量', color: 'blue', icon: 'el-icon-film' },
        { key: 'todayArrangementTotal', label: '今日场次', value: s.todayArrangementTotal ?? 0, hint: '今日新增/上映场次', color: 'cyan', icon: 'el-icon-time' },
        { key: 'todayOrderTotal', label: '今日订单', value: s.todayOrderTotal ?? 0, hint: '今日创建订单数量', color: 'green', icon: 'el-icon-shopping-cart-2' },
        { key: 'todayRevenueTotal', label: '今日营收', value: this.formatMoney(s.todayRevenueTotal ?? 0), hint: '今日已支付票房', color: 'orange', icon: 'el-icon-money' },
        { key: 'userTotal', label: '总用户数', value: s.userTotal ?? 0, hint: '平台注册用户总量', color: 'purple', icon: 'el-icon-user' },
        { key: 'revenueTotal', label: '累计营收', value: this.formatMoney(s.revenueTotal ?? 0), hint: '历史累计支付金额', color: 'pink', icon: 'el-icon-data-line' },
      ]
    },
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
    formatMoney(value) {
      const n = Number(value || 0)
      return `¥${n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
    },
    load() {
      this.loading = true
      getDashboard()
        .then((res) => {
          if (res && res.success && res.data) {
            this.stats = res.data
            this.updatedAt = new Date().toLocaleString()
            this.$nextTick(() => this.initCharts())
          }
        })
        .finally(() => {
          this.loading = false
        })
    },
    exportReport() {
      const payload = this.stats || {}
      const rows = [
        ['指标', '数值'],
        ['总影片数', payload.filmTotal ?? 0],
        ['今日场次', payload.todayArrangementTotal ?? 0],
        ['今日订单', payload.todayOrderTotal ?? 0],
        ['今日营收', this.formatMoney(payload.todayRevenueTotal ?? 0)],
        ['总用户数', payload.userTotal ?? 0],
        ['累计营收', this.formatMoney(payload.revenueTotal ?? 0)],
      ]
      const csv = rows.map((row) => row.map((cell) => `"${String(cell).replace(/"/g, '""')}"`).join(',')).join('\n')
      const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `运营数据报表_${new Date().toISOString().slice(0, 10)}.csv`
      link.click()
      URL.revokeObjectURL(link.href)
    },
    resizeCharts() {
      this.barChart?.resize()
      this.funnelChart?.resize()
      this.lineChart?.resize()
      this.heatmapChart?.resize()
      this.typePieChart?.resize()
      this.statusPieChart?.resize()
    },
    disposeCharts() {
      this.barChart?.dispose()
      this.funnelChart?.dispose()
      this.lineChart?.dispose()
      this.heatmapChart?.dispose()
      this.typePieChart?.dispose()
      this.statusPieChart?.dispose()
      this.barChart = this.funnelChart = this.lineChart = this.heatmapChart = this.typePieChart = this.statusPieChart = null
    },
    initCharts() {
      if (!this.stats) return
      this.disposeCharts()
      this.initBar()
      this.initFunnel()
      this.initLine()
      this.initHeatmap()
      this.initTypePie()
      this.initStatusPie()
    },
    initBar() {
      const chart = echarts.init(this.$refs.barRef)
      const list = this.stats.topFilmsByRevenue || []
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: 12, right: 24, top: 12, bottom: 12, containLabel: true },
        xAxis: {
          type: 'value',
          axisLabel: { color: '#94a3b8' },
          splitLine: { lineStyle: { color: 'rgba(148,163,184,0.16)' } },
        },
        yAxis: {
          type: 'category',
          inverse: true,
          data: list.map((item) => item.name),
          axisLabel: { color: '#334155', width: 110, overflow: 'truncate' },
        },
        series: [{
          type: 'bar',
          data: list.map((item) => Number(item.value || 0)),
          barWidth: 14,
          itemStyle: {
            borderRadius: [0, 10, 10, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#3b82f6' },
              { offset: 1, color: '#22c55e' },
            ]),
          },
        }],
      })
      this.barChart = chart
    },
    initFunnel() {
      const chart = echarts.init(this.$refs.funnelRef)
      const list = this.stats.purchaseFunnel || []
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}' },
        series: [{
          type: 'funnel',
          left: '10%',
          top: 20,
          bottom: 10,
          width: '80%',
          min: 0,
          max: Math.max(...list.map((i) => Number(i.value || 0)), 1),
          sort: 'descending',
          label: { color: '#334155' },
          itemStyle: { borderColor: '#fff', borderWidth: 2 },
          data: list.map((item, index) => ({
            name: item.name,
            value: Number(item.value || 0),
            itemStyle: { color: ['#60a5fa', '#34d399', '#f59e0b', '#a78bfa'][index % 4] },
          })),
        }],
      })
      this.funnelChart = chart
    },
    initLine() {
      const chart = echarts.init(this.$refs.lineRef)
      const orders = this.stats.ordersByDay || []
      const revenue = this.stats.revenueByDay || []
      chart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { top: 0, textStyle: { color: '#64748b' } },
        grid: { left: 48, right: 24, top: 42, bottom: 24, containLabel: true },
        xAxis: {
          type: 'category',
          data: orders.map((item) => item.name),
          axisLabel: { color: '#64748b', rotate: 25 },
        },
        yAxis: [
          { type: 'value', name: '订单', axisLabel: { color: '#64748b' } },
          { type: 'value', name: '营收', axisLabel: { color: '#64748b', formatter: '¥{value}' } },
        ],
        series: [
          {
            name: '订单量',
            type: 'line',
            smooth: true,
            symbolSize: 8,
            data: orders.map((item) => Number(item.value || 0)),
            lineStyle: { width: 3, color: '#3b82f6' },
            itemStyle: { color: '#3b82f6' },
            areaStyle: { color: 'rgba(59,130,246,0.16)' },
          },
          {
            name: '营收',
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            symbolSize: 8,
            data: revenue.map((item) => Number(item.value || 0)),
            lineStyle: { width: 3, color: '#f59e0b' },
            itemStyle: { color: '#f59e0b' },
            areaStyle: { color: 'rgba(245,158,11,0.12)' },
          },
        ],
      })
      this.lineChart = chart
    },
    initHeatmap() {
      const chart = echarts.init(this.$refs.heatmapRef)
      const source = this.stats.hourlyTicketHeatmap || []
      const xData = source.map((item) => item.name)
      const values = source.map((item, idx) => [idx, 0, Number(item.value || 0)])
      chart.setOption({
        tooltip: {
          position: 'top',
          formatter: (params) => `${xData[params.data[0]] || ''}<br/>购票数：${params.data[2]}`,
        },
        grid: { left: 16, right: 16, top: 20, bottom: 36, containLabel: true },
        xAxis: {
          type: 'category',
          data: xData,
          splitArea: { show: true },
          axisLabel: { color: '#64748b' },
        },
        yAxis: {
          type: 'category',
          data: ['购票热度'],
          splitArea: { show: true },
          axisLabel: { color: '#64748b' },
        },
        visualMap: {
          min: 0,
          max: Math.max(...source.map((item) => Number(item.value || 0)), 1),
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: 0,
          textStyle: { color: '#64748b' },
        },
        series: [{
          name: '购票热度',
          type: 'heatmap',
          data: values,
          label: { show: false },
          emphasis: {
            itemStyle: { shadowBlur: 8, shadowColor: 'rgba(0, 0, 0, 0.25)' },
          },
          itemStyle: {
            borderRadius: 4,
          },
        }],
      })
      this.heatmapChart = chart
    },
    initTypePie() {
      const chart = echarts.init(this.$refs.typePieRef)
      const list = this.stats.revenueByFilmType || []
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}<br/>营收：¥{c}<br/>占比：{d}%' },
        legend: { bottom: 0, textStyle: { color: '#64748b' } },
        series: [{
          type: 'pie',
          radius: ['42%', '68%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: true,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { color: '#334155', formatter: '{b}\n¥{c}' },
          data: list.map((item, index) => ({
            name: item.name,
            value: Number(item.value || 0),
            itemStyle: { color: ['#60a5fa', '#34d399', '#f59e0b', '#f472b6', '#a78bfa', '#14b8a6'][index % 6] },
          })),
        }],
      })
      this.typePieChart = chart
    },
    initStatusPie() {
      const chart = echarts.init(this.$refs.statusPieRef)
      const list = this.stats.orderStatusPie || []
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}<br/>{c} 单<br/>{d}%' },
        legend: { bottom: 0, textStyle: { color: '#64748b' } },
        series: [{
          type: 'pie',
          radius: ['50%', '72%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: { borderColor: '#fff', borderWidth: 2 },
          label: { color: '#334155' },
          data: list.map((item, index) => ({
            name: item.name,
            value: Number(item.value || 0),
            itemStyle: { color: ['#38bdf8', '#f97316', '#22c55e', '#ef4444', '#8b5cf6'][index % 5] },
          })),
        }],
      })
      this.statusPieChart = chart
    },
  },
}
</script>

<style scoped>
/* ===== 整体布局（复用 List.vue 容器样式） ===== */
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

/* 页面头部（与 List.vue 完全一致） */
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

.search-submit-btn {
  border-radius: 6px;
}

.update-meta {
  background: #f8fafc;
  padding: 6px 16px;
  border-radius: 40px;
  border: 1px solid #e2e8f0;
  font-size: 13px;
  color: #334155;
}

.update-meta span {
  margin-right: 6px;
  color: #64748b;
}

/* KPI 卡片网格 */
.stats-grid {
  flex: 0 0 auto;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 4px;
}

.kpi-card {
  padding: 18px 16px;
  border-radius: 20px;
  color: #fff;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
}

.kpi-card__label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 700;
  opacity: 0.95;
}

.kpi-card__value {
  margin-top: 18px;
  font-size: 28px;
  font-weight: 900;
  line-height: 1;
}

.kpi-card__hint {
  margin-top: 14px;
  font-size: 12px;
  opacity: 0.88;
}

.kpi-card--blue {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
}

.kpi-card--cyan {
  background: linear-gradient(135deg, #0891b2 0%, #06b6d4 100%);
}

.kpi-card--green {
  background: linear-gradient(135deg, #16a34a 0%, #22c55e 100%);
}

.kpi-card--orange {
  background: linear-gradient(135deg, #d97706 0%, #f59e0b 100%);
}

.kpi-card--purple {
  background: linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%);
}

.kpi-card--pink {
  background: linear-gradient(135deg, #db2777 0%, #f472b6 100%);
}

/* 图表卡片容器（复用 table-card 风格） */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 18px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  overflow-y: auto;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1.05fr 1.55fr 1fr;
  gap: 16px;
  min-height: 0;
}

.dashboard-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chart-card {
  background: #ffffff;
  border: 1px solid #eef2f7;
  border-radius: 16px;
  padding: 12px;
  transition: box-shadow 0.2s;
}

.chart-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
}

.chart-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eef2f7;
}

.chart-card__header h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

.chart-card__header p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #64748b;
}

.chart-tag {
  flex-shrink: 0;
  padding: 4px 10px;
  border-radius: 40px;
  background: #eef2ff;
  color: #3b82f6;
  font-size: 11px;
  font-weight: 600;
}

.chart-tag--accent {
  background: #fff3e3;
  color: #d97706;
}

.chart {
  width: 100%;
}

.chart--bar {
  height: 340px;
}

.chart--funnel {
  height: 280px;
}

.chart--line {
  height: 360px;
}

.chart--heatmap {
  height: 280px;
}

.chart--pie {
  height: 280px;
}

/* 响应式布局（与 List.vue 风格统一） */
@media (max-width: 1400px) {
  .dashboard-grid {
    grid-template-columns: 1fr 1.2fr;
  }

  .dashboard-column--right {
    grid-column: span 2;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}

@media (max-width: 992px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-column--right {
    grid-column: auto;
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .film-list {
    padding: 12px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .page-header {
    padding: 14px 16px;
  }

  .page-title {
    font-size: 18px;
  }

  .chart--bar,
  .chart--line {
    height: 260px;
  }

  .chart--funnel,
  .chart--heatmap,
  .chart--pie {
    height: 220px;
  }
}
</style>
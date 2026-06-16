<template>
  <div class="film-list">
    <!-- 页面头部 全站统一header样式 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="search-form">
          <div class="update-meta">
            <span>数据更新时间</span>
            <strong>{{ updatedAt || '暂未加载' }}</strong>
          </div>
          <el-button type="primary" class="search-submit-btn" @click="exportReport">
            <el-icon>
              <Download />
            </el-icon>
            <span>导出运营报表</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- KPI指标卡片区域 -->
    <div class="stats-grid">
      <div v-for="item in kpiCards" :key="item.key" class="kpi-card" :class="`kpi-card--${item.color}`">
        <div class="kpi-card__label">
          <el-icon :size="20">
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.label }}</span>
        </div>
        <div class="kpi-card__value">{{ item.value }}</div>
        <div class="kpi-card__hint">{{ item.hint }}</div>
      </div>
    </div>

    <!-- 图表主容器 复用全站table-card毛玻璃卡片 -->
    <div class="table-card">
      <div class="dashboard-grid">
        <!-- 左列 -->
        <div class="dashboard-column dashboard-column--left">
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>票房TOP10影片</h3>
                <p>依据已支付订单金额排行</p>
              </div>
              <span class="chart-tag">TOP10</span>
            </div>
            <div ref="barRef" class="chart chart--bar" />
          </div>
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>购票转化漏斗</h3>
                <p>浏览-下单-支付完整转化链路</p>
              </div>
              <span class="chart-tag">Funnel</span>
            </div>
            <div ref="funnelRef" class="chart chart--funnel" />
          </div>
        </div>

        <!-- 中间核心列 -->
        <div class="dashboard-column dashboard-column--center">
          <div class="chart-card chart-card--highlight">
            <div class="chart-card__header">
              <div>
                <h3>近7天订单&营收趋势</h3>
                <p>双轴同步展示订单量与票房收入波动</p>
              </div>
              <span class="chart-tag chart-tag--accent">Trend</span>
            </div>
            <div ref="lineRef" class="chart chart--line" />
          </div>
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>时段购票热力分布</h3>
                <p>全天各时段购票热度高低</p>
              </div>
              <span class="chart-tag">Heatmap</span>
            </div>
            <div ref="heatmapRef" class="chart chart--heatmap" />
          </div>
        </div>

        <!-- 右侧占比图表 -->
        <div class="dashboard-column dashboard-column--right">
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>影片类型营收占比</h3>
                <p>各分类影片实际支付营收</p>
              </div>
              <span class="chart-tag">Pie</span>
            </div>
            <div ref="typePieRef" class="chart chart--pie" />
          </div>
          <div class="chart-card">
            <div class="chart-card__header">
              <div>
                <h3>订单状态环形分布</h3>
                <p>全部订单状态数量占比</p>
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
import { Download, Film, Clock, ShoppingCart, Money, User, DataLine } from '@element-plus/icons-vue'
import { downloadCsv } from '@/utils/exportCsv'

export default {
  name: 'StatsDashboard',
  components: { Download, Film, Clock, ShoppingCart, Money, User, DataLine },
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
        { key: 'filmTotal', label: '总影片数', value: s.filmTotal ?? 0, hint: '平台全部入库影片', color: 'blue', icon: 'Film' },
        { key: 'todayArrangementTotal', label: '今日场次', value: s.todayArrangementTotal ?? 0, hint: '今日可放映排片', color: 'cyan', icon: 'Clock' },
        { key: 'todayOrderTotal', label: '今日订单', value: s.todayOrderTotal ?? 0, hint: '今日创建订单总数', color: 'green', icon: 'ShoppingCart' },
        { key: 'todayRevenueTotal', label: '今日营收', value: this.formatMoney(s.todayRevenueTotal ?? 0), hint: '今日已支付票房金额', color: 'orange', icon: 'Money' },
        { key: 'userTotal', label: '注册用户', value: s.userTotal ?? 0, hint: '平台全部注册用户', color: 'purple', icon: 'User' },
        { key: 'revenueTotal', label: '累计营收', value: this.formatMoney(s.revenueTotal ?? 0), hint: '历史全部支付总额', color: 'pink', icon: 'DataLine' },
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
        ['总影片数', payload.filmTotal ?? 0],
        ['今日场次', payload.todayArrangementTotal ?? 0],
        ['今日订单', payload.todayOrderTotal ?? 0],
        ['今日营收(元)', payload.todayRevenueTotal ?? 0],
        ['注册用户总数', payload.userTotal ?? 0],
        ['累计总营收(元)', payload.revenueTotal ?? 0],
        ['近7天订单明细', (payload.ordersByDay || []).map(i => `${i.name}:${i.value}单`).join(' | ')],
        ['近7天营收明细', (payload.revenueByDay || []).map(i => `${i.name}:¥${i.value}`).join(' | ')],
        ['各类型影片营收', (payload.revenueByFilmType || []).map(i => `${i.name}:¥${i.value}`).join(' | ')],
      ]
      downloadCsv(
        `运营数据报表_${new Date().toISOString().slice(0, 10)}.csv`,
        ['统计指标', '数值'],
        rows
      )
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
      const charts = [this.barChart, this.funnelChart, this.lineChart, this.heatmapChart, this.typePieChart, this.statusPieChart]
      charts.forEach(c => c?.dispose())
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
          data: list.map(item => item.name),
          axisLabel: { color: '#334155', width: 110, overflow: 'truncate' },
        },
        series: [{
          type: 'bar',
          data: list.map(item => Number(item.value || 0)),
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
      const colorArr = ['#60a5fa', '#34d399', '#f59e0b', '#a78bfa']
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}' },
        series: [{
          type: 'funnel',
          left: '10%',
          top: 20,
          bottom: 10,
          width: '80%',
          min: 0,
          max: Math.max(...list.map(i => Number(i.value || 0)), 1),
          sort: 'descending',
          label: { color: '#334155' },
          itemStyle: { borderColor: '#fff', borderWidth: 2 },
          data: list.map((item, index) => ({
            name: item.name,
            value: Number(item.value || 0),
            itemStyle: { color: colorArr[index % colorArr.length] },
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
          data: orders.map(item => item.name),
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
            data: orders.map(item => Number(item.value || 0)),
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
            data: revenue.map(item => Number(item.value || 0)),
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
      const xData = source.map(item => item.name)
      const values = source.map((item, idx) => [idx, 0, Number(item.value || 0)])
      const maxVal = Math.max(...source.map(item => Number(item.value || 0)), 1)
      chart.setOption({
        tooltip: {
          position: 'top',
          formatter: params => `${xData[params.data[0]] || ''}<br/>购票数：${params.data[2]}`,
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
          max: maxVal,
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
          emphasis: { itemStyle: { shadowBlur: 8, shadowColor: 'rgba(0,0,0,0.25)' } },
          itemStyle: { borderRadius: 4 },
        }],
      })
      this.heatmapChart = chart
    },
    initTypePie() {
      const chart = echarts.init(this.$refs.typePieRef)
      const list = this.stats.revenueByFilmType || []
      const colorList = ['#60a5fa', '#34d399', '#f59e0b', '#f472b6', '#a78bfa', '#14b8a6']
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
            itemStyle: { color: colorList[index % colorList.length] },
          })),
        }],
      })
      this.typePieChart = chart
    },
    initStatusPie() {
      const chart = echarts.init(this.$refs.statusPieRef)
      const list = this.stats.orderStatusPie || []
      const colorList = ['#38bdf8', '#f97316', '#22c55e', '#ef4444', '#8b5cf6']
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
            itemStyle: { color: colorList[index % colorList.length] },
          })),
        }],
      })
      this.statusPieChart = chart
    },
  },
}
</script>

<style scoped>
/* 全局页面容器 全站统一布局基底 */
.film-list {
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
  padding: 20px;
  box-sizing: border-box;
  background: #fafbfc;
}

/* 顶部页面栏 与列表页完全统一样式 */
.page-header {
  flex: 0 0 auto;
  padding: 20px 24px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.7);
}

.page-search-bar {
  width: 100%;
}

.search-form {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  flex-wrap: wrap;
}

.update-meta {
  background: #f8fafc;
  padding: 8px 18px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  font-size: 13px;
  color: #334155;
}

.update-meta span {
  margin-right: 8px;
  color: #64748b;
}

/* 统一全站主按钮样式 */
.search-submit-btn {
  height: 42px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.22);
  transition: all 0.25s ease;
}

.search-submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(64, 158, 255, 0.28);
}

/* KPI指标卡片网格布局 */
.stats-grid {
  flex: 0 0 auto;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.kpi-card {
  padding: 20px 20px;
  border-radius: 20px;
  color: #ffffff;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.09);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.12);
}

.kpi-card__label {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 600;
  opacity: 0.96;
}

.kpi-card__value {
  margin-top: 20px;
  font-size: 30px;
  font-weight: 800;
  line-height: 1;
}

.kpi-card__hint {
  margin-top: 16px;
  font-size: 12px;
  opacity: 0.9;
}

/* KPI渐变配色 全站统一色系 */
.kpi-card--blue {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
}

.kpi-card--cyan {
  background: linear-gradient(135deg, #0891b2, #06b6d4);
}

.kpi-card--green {
  background: linear-gradient(135deg, #16a34a, #22c55e);
}

.kpi-card--orange {
  background: linear-gradient(135deg, #d97706, #f59e0b);
}

.kpi-card--purple {
  background: linear-gradient(135deg, #7c3aed, #8b5cf6);
}

.kpi-card--pink {
  background: linear-gradient(135deg, #db2777, #f472b6);
}

/* 外层大卡片容器 复用table-card毛玻璃 */
.table-card {
  flex: 1;
  min-height: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.7);
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
  min-height: 0;
}

/* 单张图表卡片 */
.chart-card {
  background: #ffffff;
  border: 1px solid #eef2f7;
  border-radius: 16px;
  padding: 16px;
  transition: all 0.25s ease;
}

.chart-card:hover {
  box-shadow: 0 8px 26px rgba(0, 0, 0, 0.05);
}

.chart-card--highlight {
  border-left: 4px solid #f59e0b;
}

.chart-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eef2f7;
}

.chart-card__header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.chart-card__header p {
  margin: 6px 0 0;
  font-size: 12px;
  color: #64748b;
}

.chart-tag {
  flex-shrink: 0;
  padding: 4px 12px;
  border-radius: 999px;
  background: #eef2ff;
  color: #3b82f6;
  font-size: 11px;
  font-weight: 600;
}

.chart-tag--accent {
  background: #fff3e3;
  color: #d97706;
}

/* 图表高度统一规范 */
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

/* 响应式适配 阶梯缩小布局 */
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
    gap: 12px;
  }

  .page-header {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .kpi-card {
    padding: 16px 16px;
  }

  .kpi-card__value {
    font-size: 24px;
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
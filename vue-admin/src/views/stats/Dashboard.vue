<template>
  <div class="stats-dashboard">
    <div class="page-head">
      <h2>运营概览</h2>
      <p class="page-head__sub">影院售票关键指标与趋势（数据来自订单、影片、用户汇总）</p>
    </div>

    <el-row v-if="stats" :gutter="16" class="kpi-row">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="kpi-card kpi-card--blue">
          <div class="kpi-label">订单总数</div>
          <div class="kpi-value">{{ stats.orderTotal }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="kpi-card kpi-card--green">
          <div class="kpi-label">已支付订单</div>
          <div class="kpi-value">{{ stats.orderPaid }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="kpi-card kpi-card--orange">
          <div class="kpi-label">累计票房（元）</div>
          <div class="kpi-value">{{ stats.revenueTotal }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="kpi-card kpi-card--purple">
          <div class="kpi-label">用户 / 影片</div>
          <div class="kpi-value kpi-value--sm">{{ stats.userTotal }} / {{ stats.filmTotal }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="chart-card__title">订单状态分布</span>
          </template>
          <div ref="pieRef" class="chart-box"/>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="chart-card__title">影片已支付票房 TOP（元）</span>
          </template>
          <div ref="barRef" class="chart-box chart-box--tall"/>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="chart-card__title">近 14 日下单量趋势</span>
          </template>
          <div ref="lineRef" class="chart-box chart-box--wide"/>
        </el-card>
      </el-col>
    </el-row>
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
        .catch(() => {})
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

      const pie = echarts.init(this.$refs.pieRef)
      pie.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        color: ['#909399', '#F56C6C', '#67C23A', '#E6A23C'],
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
          xAxis: { type: 'value', name: '元' },
          yAxis: { type: 'category', data: names, inverse: true, axisLabel: { width: 100, overflow: 'truncate' } },
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

      const days = (this.stats.ordersByDay || []).map((x) => x.name)
      const counts = (this.stats.ordersByDay || []).map((x) => Number(x.value))
      const line = echarts.init(this.$refs.lineRef)
      line.setOption({
        tooltip: { trigger: 'axis' },
        grid: {
          left: 56,
          right: 28,
          top: 40,
          bottom: 24,
          containLabel: true,
        },
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
.stats-dashboard {
  padding: 8px 4px 32px;
}

.page-head {
  margin-bottom: 20px;
}

.page-head h2 {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 2px;
  color: #303133;
}

.page-head__sub {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.kpi-row {
  margin-bottom: 16px;
}

.kpi-card {
  text-align: center;
  border-radius: 10px;
  border: none;
}

.kpi-card :deep(.el-card__body) {
  padding: 18px 12px;
}

.kpi-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}

.kpi-value--sm {
  font-size: 22px;
}

.kpi-card--blue {
  background: linear-gradient(135deg, #ecf5ff 0%, #fff 60%);
}

.kpi-card--green {
  background: linear-gradient(135deg, #f0f9eb 0%, #fff 60%);
}

.kpi-card--orange {
  background: linear-gradient(135deg, #fdf6ec 0%, #fff 60%);
}

.kpi-card--purple {
  background: linear-gradient(135deg, #f4f1ff 0%, #fff 60%);
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  border-radius: 10px;
}

.chart-card__title {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.chart-box {
  height: 300px;
  width: 100%;
}

.chart-box--tall {
  height: 320px;
}

.chart-box--wide {
  height: 340px;
}
</style>

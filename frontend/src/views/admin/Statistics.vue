<template>
  <div class="statistics-page">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="icon" style="background: #409eff"><el-icon><User /></el-icon></div>
        <div class="info">
          <span class="value">{{ overview.totalUsers || 0 }}</span>
          <span class="label">总用户数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background: #67c23a"><el-icon><Money /></el-icon></div>
        <div class="info">
          <span class="value">¥{{ overview.totalIncome || 0 }}</span>
          <span class="label">总收入</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background: #e6a23c"><el-icon><Calendar /></el-icon></div>
        <div class="info">
          <span class="value">{{ overview.paidCount || 0 }}</span>
          <span class="label">已支付预约</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background: #f56c6c"><el-icon><FirstAidKit /></el-icon></div>
        <div class="info">
          <span class="value">{{ overview.vaccinatedCount || 0 }}</span>
          <span class="label">已接种完成</span>
        </div>
      </div>
    </div>

    <!-- 第一行图表 -->
    <div class="charts-row">
      <div class="chart-card">
        <h3>预约趋势（近30天）</h3>
        <div ref="lineChartRef" style="height: 300px"></div>
      </div>
      <div class="chart-card">
        <h3>各社区接种统计</h3>
        <div ref="pieChartRef" style="height: 300px"></div>
      </div>
    </div>

    <!-- 第二行图表 -->
    <div class="charts-row">
      <div class="chart-card">
        <h3>疫苗接种排行（Top 10）</h3>
        <div ref="barChartRef" style="height: 300px"></div>
      </div>
      <div class="chart-card">
        <h3>用户增长趋势（近30天）</h3>
        <div ref="userGrowthChartRef" style="height: 300px"></div>
      </div>
    </div>

    <!-- 第三行图表 -->
    <div class="charts-row">
      <div class="chart-card full-width">
        <h3>收入趋势（近30天）</h3>
        <div ref="incomeChartRef" style="height: 300px"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'
import { User, Money, Calendar, FirstAidKit } from '@element-plus/icons-vue'

const lineChartRef = ref()
const pieChartRef = ref()
const barChartRef = ref()
const userGrowthChartRef = ref()
const incomeChartRef = ref()
const overview = ref({})

let charts = []

const initCharts = async () => {
  const [overviewRes, dateRes, communityRes, vaccineRes, userGrowthRes, incomeRes] = await Promise.all([
    request.get('/statistics/overview'),
    request.get('/statistics/by-date', { params: { days: 30 } }),
    request.get('/statistics/by-community'),
    request.get('/statistics/by-vaccine'),
    request.get('/statistics/user-growth', { params: { days: 30 } }),
    request.get('/statistics/income-by-date', { params: { days: 30 } })
  ])

  overview.value = overviewRes.data

  // 预约趋势折线图
  const lineChart = echarts.init(lineChartRef.value)
  charts.push(lineChart)
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dateRes.data.map(i => i.date) },
    yAxis: { type: 'value' },
    series: [{ name: '预约数量', data: dateRes.data.map(i => i.count), type: 'line', smooth: true, areaStyle: { opacity: 0.3 } }]
  })

  // 社区接种饼图
  const pieChart = echarts.init(pieChartRef.value)
  charts.push(pieChart)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: 10, top: 'center' },
    series: [{ type: 'pie', radius: ['40%', '70%'], center: ['40%', '50%'], data: communityRes.data.map(i => ({ name: i.community_name || '未知', value: i.count })), label: { show: false } }]
  })

  // 疫苗接种排行柱状图
  const barChart = echarts.init(barChartRef.value)
  charts.push(barChart)
  barChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'category', data: vaccineRes.data.map(i => i.vaccine_name || '未知'), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value' },
    series: [{ name: '接种数量', type: 'bar', data: vaccineRes.data.map(i => i.count), itemStyle: { color: '#5470c6' } }]
  })

  // 用户增长趋势折线图
  const userGrowthChart = echarts.init(userGrowthChartRef.value)
  charts.push(userGrowthChart)
  userGrowthChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: userGrowthRes.data.map(i => i.date) },
    yAxis: { type: 'value' },
    series: [{ name: '新增用户', data: userGrowthRes.data.map(i => i.count), type: 'line', smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#91cc75' } }]
  })

  // 收入趋势折线图
  const incomeChart = echarts.init(incomeChartRef.value)
  charts.push(incomeChart)
  incomeChart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}<br/>{a}: ¥{c}' },
    xAxis: { type: 'category', data: incomeRes.data.map(i => i.date) },
    yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
    series: [{ name: '收入', data: incomeRes.data.map(i => i.amount), type: 'line', smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#ee6666' } }]
  })
}

const handleResize = () => { charts.forEach(chart => chart.resize()) }

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(chart => chart.dispose())
})
</script>

<style scoped lang="scss">
.statistics-page {
  .stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 20px; }
  .stat-card {
    background: #fff; border-radius: 8px; padding: 20px; display: flex; align-items: center; gap: 15px;
    .icon { width: 50px; height: 50px; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 24px; }
    .info { .value { display: block; font-size: 24px; font-weight: bold; color: #333; } .label { font-size: 14px; color: #999; } }
  }
  .charts-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }
  .chart-card { background: #fff; border-radius: 8px; padding: 20px; &.full-width { grid-column: 1 / -1; } h3 { margin-bottom: 15px; font-size: 16px; color: #333; } }
}
@media (max-width: 1200px) {
  .statistics-page { .stat-cards { grid-template-columns: repeat(2, 1fr); } .charts-row { grid-template-columns: 1fr; } }
}
</style>

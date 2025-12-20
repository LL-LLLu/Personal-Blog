<template>
  <div class="visitor-analytics">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Visitor Analytics</h1>
        <p class="page-subtitle">Track where your visitors are coming from around the world</p>
      </div>
      <el-button type="primary" :loading="loading" @click="refreshData" class="refresh-btn">
        <el-icon class="mr-2"><Refresh /></el-icon>
        Refresh Data
      </el-button>
    </div>

    <!-- Statistics Cards -->
    <div class="stats-grid">
      <div class="stat-card stat-card-blue">
        <div class="stat-icon">
          <el-icon><Location /></el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ summary.totalLocations || 0 }}</span>
          <span class="stat-label">Total Locations</span>
        </div>
        <div class="stat-decoration"></div>
      </div>

      <div class="stat-card stat-card-emerald">
        <div class="stat-icon">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ summary.totalVisits || 0 }}</span>
          <span class="stat-label">Total Visits</span>
        </div>
        <div class="stat-decoration"></div>
      </div>

      <div class="stat-card stat-card-violet">
        <div class="stat-icon">
          <el-icon><Sunny /></el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ summary.todayVisits || 0 }}</span>
          <span class="stat-label">Today's Visits</span>
        </div>
        <div class="stat-decoration"></div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="content-grid">
      <!-- World Map -->
      <div class="map-card">
        <div class="card-header">
          <h3 class="card-title">
            <span class="title-icon">🌍</span>
            Global Visitor Distribution
          </h3>
        </div>
        <div ref="mapChartRef" class="map-container"></div>
      </div>

      <!-- Top Countries -->
      <div class="countries-card">
        <div class="card-header">
          <h3 class="card-title">
            <span class="title-icon">🏆</span>
            Top Countries
          </h3>
        </div>
        <div class="countries-list" v-if="summary.topCountries && summary.topCountries.length > 0">
          <div
            v-for="(country, index) in summary.topCountries"
            :key="index"
            class="country-item"
          >
            <div class="country-rank" :class="getRankClass(index)">
              {{ index + 1 }}
            </div>
            <div class="country-info">
              <span class="country-name">{{ country.country || 'Unknown' }}</span>
              <div class="country-bar">
                <div
                  class="country-bar-fill"
                  :style="{ width: getBarWidth(country.count) }"
                ></div>
              </div>
            </div>
            <span class="country-count">{{ formatNumber(country.count) }}</span>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-icon class="empty-icon"><Location /></el-icon>
          <p>No visitor data yet</p>
        </div>
      </div>
    </div>

    <!-- Location Details Table -->
    <div class="table-card">
      <div class="card-header">
        <h3 class="card-title">
          <span class="title-icon">📍</span>
          Location Details
        </h3>
      </div>
      <el-table :data="locations" class="modern-table" v-loading="loading">
        <el-table-column prop="locationName" label="Location" min-width="200">
          <template #default="{ row }">
            <div class="location-cell">
              <el-icon class="location-icon"><Location /></el-icon>
              <span>{{ row.locationName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="country" label="Country" width="150" />
        <el-table-column prop="province" label="Province" width="150" />
        <el-table-column prop="city" label="City" width="150" />
        <el-table-column prop="visitCount" label="Visits" width="120" sortable>
          <template #default="{ row }">
            <span class="visit-badge">{{ row.visitCount }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Recent Visitors Log -->
    <div class="table-card">
      <div class="card-header">
        <h3 class="card-title">
          <span class="title-icon">👥</span>
          Recent Visitors
        </h3>
      </div>
      <el-table :data="visitorLogs" class="modern-table" v-loading="logsLoading">
        <el-table-column prop="visitTime" label="Time" width="180">
          <template #default="{ row }">
            <span class="time-cell">{{ row.visitTime }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP Address" width="150">
          <template #default="{ row }">
            <code class="ip-cell">{{ row.ipAddress }}</code>
          </template>
        </el-table-column>
        <el-table-column label="Location" min-width="200">
          <template #default="{ row }">
            <span class="location-text">
              {{ [row.country, row.province, row.city].filter(Boolean).join(' → ') || 'Unknown' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="articleTitle" label="Article Visited" min-width="200">
          <template #default="{ row }">
            <span v-if="row.articleTitle" class="article-link">{{ row.articleTitle }}</span>
            <span v-else class="no-article">—</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="logsPagination.current"
          v-model:page-size="logsPagination.size"
          :total="logsPagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleLogsSizeChange"
          @current-change="handleLogsPageChange"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { Location, View, Sunny, Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getVisitorLocationStats, getVisitorLogs, getVisitorSummary } from '@/api/admin/visitor'

// Data
const loading = ref(false)
const logsLoading = ref(false)
const summary = ref({})
const locations = ref([])
const visitorLogs = ref([])
const logsPagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// Chart
const mapChartRef = ref(null)
let mapChart = null

// Computed
const maxCountryCount = computed(() => {
  if (!summary.value.topCountries?.length) return 1
  return Math.max(...summary.value.topCountries.map(c => c.count))
})

// Helper functions
const getRankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return 'rank-default'
}

const getBarWidth = (count) => {
  return `${(count / maxCountryCount.value) * 100}%`
}

const formatNumber = (num) => {
  if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K'
  return num
}

// Country name mapping for ECharts world map compatibility
const countryNameMap = {
  '中国': 'China',
  '美国': 'United States',
  '日本': 'Japan',
  '韩国': 'Korea',
  '英国': 'United Kingdom',
  '德国': 'Germany',
  '法国': 'France',
  '俄罗斯': 'Russia',
  '加拿大': 'Canada',
  '澳大利亚': 'Australia',
  '印度': 'India',
  '巴西': 'Brazil',
  '新加坡': 'Singapore',
  '马来西亚': 'Malaysia',
  '泰国': 'Thailand',
  '越南': 'Vietnam',
  '印度尼西亚': 'Indonesia',
  '菲律宾': 'Philippines',
  '台湾': 'Taiwan',
  '香港': 'Hong Kong',
  '澳门': 'Macao',
  'United States of America': 'United States',
  'Republic of Korea': 'Korea',
  'Russian Federation': 'Russia',
  'Viet Nam': 'Vietnam',
  'United Kingdom of Great Britain and Northern Ireland': 'United Kingdom'
}

// Initialize map chart
const initMapChart = () => {
  if (!mapChartRef.value) return

  mapChart = echarts.init(mapChartRef.value)

  const mapData = prepareMapData()

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      borderWidth: 1,
      padding: [12, 16],
      textStyle: {
        color: '#fff',
        fontSize: 14
      },
      formatter: (params) => {
        if (params.data) {
          return `<div style="font-weight: 600; margin-bottom: 4px;">${params.name}</div>
                  <div style="color: #a5b4fc;">Visits: <span style="color: #fff; font-weight: 600;">${params.data.value || 0}</span></div>`
        }
        return `<div style="font-weight: 600;">${params.name}</div>`
      }
    },
    visualMap: {
      min: 0,
      max: Math.max(...mapData.map(d => d.value), 100),
      left: 20,
      bottom: 20,
      text: ['High', 'Low'],
      textStyle: {
        color: '#64748b'
      },
      calculable: true,
      itemWidth: 12,
      itemHeight: 120,
      inRange: {
        color: ['#e0e7ff', '#a5b4fc', '#818cf8', '#6366f1', '#4f46e5']
      }
    },
    series: [
      {
        name: 'Visitors',
        type: 'map',
        map: 'world',
        roam: true,
        zoom: 1.2,
        scaleLimit: {
          min: 0.5,
          max: 10
        },
        itemStyle: {
          areaColor: '#f1f5f9',
          borderColor: '#e2e8f0',
          borderWidth: 0.5
        },
        emphasis: {
          label: {
            show: true,
            color: '#1e293b',
            fontWeight: 600
          },
          itemStyle: {
            areaColor: '#fbbf24',
            shadowColor: 'rgba(0, 0, 0, 0.2)',
            shadowBlur: 10
          }
        },
        select: {
          label: {
            show: true,
            color: '#1e293b'
          },
          itemStyle: {
            areaColor: '#6366f1'
          }
        },
        data: mapData
      }
    ]
  }

  mapChart.setOption(option)
}

// Prepare map data from locations
const prepareMapData = () => {
  const countryVisits = {}

  locations.value.forEach(loc => {
    let country = loc.country || 'Unknown'
    if (countryNameMap[country]) {
      country = countryNameMap[country]
    }

    if (!countryVisits[country]) {
      countryVisits[country] = 0
    }
    countryVisits[country] += loc.visitCount || 0
  })

  return Object.entries(countryVisits).map(([name, value]) => ({
    name,
    value
  }))
}

// Fetch visitor summary
const fetchSummary = async () => {
  try {
    const res = await getVisitorSummary()
    if (res.success) {
      summary.value = res.data
    }
  } catch (error) {
    console.error('Failed to fetch visitor summary:', error)
  }
}

// Fetch location statistics
const fetchLocations = async () => {
  loading.value = true
  try {
    const res = await getVisitorLocationStats()
    if (res.success) {
      locations.value = res.data || []
      await nextTick()
      updateMapChart()
    }
  } catch (error) {
    console.error('Failed to fetch visitor locations:', error)
  } finally {
    loading.value = false
  }
}

// Fetch visitor logs
const fetchVisitorLogs = async () => {
  logsLoading.value = true
  try {
    const res = await getVisitorLogs(logsPagination.value.current, logsPagination.value.size)
    if (res.success) {
      visitorLogs.value = res.data || []
      logsPagination.value.total = res.total || 0
    }
  } catch (error) {
    console.error('Failed to fetch visitor logs:', error)
  } finally {
    logsLoading.value = false
  }
}

// Update map chart with new data
const updateMapChart = () => {
  if (!mapChart) return

  const mapData = prepareMapData()

  mapChart.setOption({
    visualMap: {
      max: Math.max(...mapData.map(d => d.value), 100)
    },
    series: [{
      data: mapData
    }]
  })
}

// Refresh all data
const refreshData = () => {
  fetchSummary()
  fetchLocations()
  fetchVisitorLogs()
}

// Pagination handlers
const handleLogsSizeChange = (size) => {
  logsPagination.value.size = size
  fetchVisitorLogs()
}

const handleLogsPageChange = (page) => {
  logsPagination.value.current = page
  fetchVisitorLogs()
}

// Handle window resize
const handleResize = () => {
  mapChart?.resize()
}

// Lifecycle
onMounted(async () => {
  try {
    const response = await fetch('/map/world.json')
    const worldJson = await response.json()
    echarts.registerMap('world', worldJson)
    console.log('World map loaded successfully')
  } catch (error) {
    console.error('Failed to load world map:', error)
  }

  await nextTick()
  initMapChart()
  refreshData()

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  mapChart?.dispose()
})
</script>

<style scoped>
.visitor-analytics {
  padding: 0;
}

/* Page Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.refresh-btn {
  border-radius: 10px;
  padding: 12px 20px;
  font-weight: 500;
}

/* Statistics Cards */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  position: relative;
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -8px rgba(0, 0, 0, 0.15);
}

.stat-card-blue {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.stat-card-emerald {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-card-violet {
  background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  flex-shrink: 0;
}

.stat-content {
  display: flex;
  flex-direction: column;
  z-index: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: white;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}

.stat-decoration {
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

/* Content Grid */
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

/* Card Styles */
.map-card,
.countries-card,
.table-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 18px;
}

/* Map Container */
.map-container {
  width: 100%;
  height: 450px;
  padding: 16px;
}

/* Countries List */
.countries-list {
  padding: 8px 0;
}

.country-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  transition: background-color 0.15s ease;
}

.country-item:hover {
  background-color: #f8fafc;
}

.country-rank {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.rank-gold {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: white;
}

.rank-silver {
  background: linear-gradient(135deg, #94a3b8, #64748b);
  color: white;
}

.rank-bronze {
  background: linear-gradient(135deg, #d97706, #b45309);
  color: white;
}

.rank-default {
  background: #f1f5f9;
  color: #64748b;
}

.country-info {
  flex: 1;
  min-width: 0;
}

.country-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  margin-bottom: 6px;
}

.country-bar {
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
}

.country-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  border-radius: 2px;
  transition: width 0.5s ease;
}

.country-count {
  font-size: 14px;
  font-weight: 600;
  color: #6366f1;
  min-width: 40px;
  text-align: right;
}

.empty-state {
  padding: 48px 24px;
  text-align: center;
  color: #94a3b8;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}

/* Table Styles */
.table-card {
  margin-bottom: 24px;
}

.modern-table {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
  --el-table-row-hover-bg-color: #f8fafc;
}

.modern-table :deep(th) {
  font-weight: 600;
  color: #475569;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.modern-table :deep(td) {
  color: #334155;
  font-size: 14px;
}

.location-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.location-icon {
  color: #6366f1;
  font-size: 16px;
}

.visit-badge {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.time-cell {
  color: #64748b;
  font-size: 13px;
}

.ip-cell {
  font-family: 'SF Mono', Monaco, 'Courier New', monospace;
  font-size: 12px;
  background: #f1f5f9;
  padding: 4px 8px;
  border-radius: 4px;
  color: #475569;
}

.location-text {
  color: #334155;
}

.article-link {
  color: #6366f1;
  font-weight: 500;
}

.no-article {
  color: #cbd5e1;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
}

/* Dark mode support */
html.dark .page-title {
  color: #f1f5f9;
}

html.dark .page-subtitle {
  color: #94a3b8;
}

html.dark .map-card,
html.dark .countries-card,
html.dark .table-card {
  background: #1e293b;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

html.dark .card-header {
  border-bottom-color: #334155;
}

html.dark .card-title {
  color: #f1f5f9;
}

html.dark .country-item:hover {
  background-color: #334155;
}

html.dark .country-name {
  color: #f1f5f9;
}

html.dark .rank-default {
  background: #334155;
  color: #94a3b8;
}

html.dark .country-bar {
  background: #334155;
}

html.dark .modern-table {
  --el-table-bg-color: #1e293b;
  --el-table-tr-bg-color: #1e293b;
  --el-table-header-bg-color: #0f172a;
  --el-table-row-hover-bg-color: #334155;
  --el-table-border-color: #334155;
}

html.dark .modern-table :deep(th) {
  color: #94a3b8;
}

html.dark .modern-table :deep(td) {
  color: #e2e8f0;
}

html.dark .ip-cell {
  background: #334155;
  color: #e2e8f0;
}

html.dark .pagination-wrapper {
  border-top-color: #334155;
}
</style>

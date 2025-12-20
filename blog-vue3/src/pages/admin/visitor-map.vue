<template>
  <div>
    <!-- Page Header -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800 dark:text-white mb-2">
        Visitor Analytics
      </h1>
      <p class="text-gray-500 dark:text-gray-400">
        Track where your visitors are coming from around the world
      </p>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <el-card shadow="hover" class="stat-card">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-blue-100 dark:bg-blue-900">
            <el-icon class="text-2xl text-blue-600 dark:text-blue-400"><Location /></el-icon>
          </div>
          <div class="ml-4">
            <p class="text-sm text-gray-500 dark:text-gray-400">Total Locations</p>
            <p class="text-2xl font-bold text-gray-800 dark:text-white">{{ summary.totalLocations || 0 }}</p>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-green-100 dark:bg-green-900">
            <el-icon class="text-2xl text-green-600 dark:text-green-400"><View /></el-icon>
          </div>
          <div class="ml-4">
            <p class="text-sm text-gray-500 dark:text-gray-400">Total Visits</p>
            <p class="text-2xl font-bold text-gray-800 dark:text-white">{{ summary.totalVisits || 0 }}</p>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-purple-100 dark:bg-purple-900">
            <el-icon class="text-2xl text-purple-600 dark:text-purple-400"><Sunny /></el-icon>
          </div>
          <div class="ml-4">
            <p class="text-sm text-gray-500 dark:text-gray-400">Today's Visits</p>
            <p class="text-2xl font-bold text-gray-800 dark:text-white">{{ summary.todayVisits || 0 }}</p>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Main Content Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- World Map -->
      <el-card shadow="hover" class="lg:col-span-2">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="font-semibold text-gray-800 dark:text-white">Visitor World Map</span>
            <el-button type="primary" size="small" :loading="loading" @click="refreshData">
              <el-icon class="mr-1"><Refresh /></el-icon>
              Refresh
            </el-button>
          </div>
        </template>
        <div ref="mapChartRef" class="w-full h-[400px]"></div>
      </el-card>

      <!-- Top Countries -->
      <el-card shadow="hover">
        <template #header>
          <span class="font-semibold text-gray-800 dark:text-white">Top Countries</span>
        </template>
        <div v-if="summary.topCountries && summary.topCountries.length > 0">
          <div
            v-for="(country, index) in summary.topCountries"
            :key="index"
            class="flex items-center justify-between py-3 border-b border-gray-100 dark:border-gray-700 last:border-0"
          >
            <div class="flex items-center">
              <span class="w-6 h-6 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center text-xs font-bold text-blue-600 dark:text-blue-400 mr-3">
                {{ index + 1 }}
              </span>
              <span class="text-gray-700 dark:text-gray-300">{{ country.country || 'Unknown' }}</span>
            </div>
            <span class="font-semibold text-gray-800 dark:text-white">{{ country.count }}</span>
          </div>
        </div>
        <div v-else class="text-center text-gray-400 py-8">
          No visitor data yet
        </div>
      </el-card>
    </div>

    <!-- Location Details Table -->
    <el-card shadow="hover" class="mt-6">
      <template #header>
        <span class="font-semibold text-gray-800 dark:text-white">Location Details</span>
      </template>
      <el-table :data="locations" style="width: 100%" v-loading="loading">
        <el-table-column prop="locationName" label="Location" min-width="200" />
        <el-table-column prop="country" label="Country" width="150" />
        <el-table-column prop="province" label="Province" width="150" />
        <el-table-column prop="city" label="City" width="150" />
        <el-table-column prop="visitCount" label="Visits" width="120" sortable>
          <template #default="{ row }">
            <el-tag type="primary">{{ row.visitCount }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Recent Visitors Log -->
    <el-card shadow="hover" class="mt-6">
      <template #header>
        <span class="font-semibold text-gray-800 dark:text-white">Recent Visitors</span>
      </template>
      <el-table :data="visitorLogs" style="width: 100%" v-loading="logsLoading">
        <el-table-column prop="visitTime" label="Time" width="180" />
        <el-table-column prop="ipAddress" label="IP Address" width="150" />
        <el-table-column label="Location" min-width="200">
          <template #default="{ row }">
            {{ [row.country, row.province, row.city].filter(Boolean).join(' - ') || 'Unknown' }}
          </template>
        </el-table-column>
        <el-table-column prop="articleTitle" label="Article Visited" min-width="200">
          <template #default="{ row }">
            <span v-if="row.articleTitle">{{ row.articleTitle }}</span>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="flex justify-end mt-4">
        <el-pagination
          v-model:current-page="logsPagination.current"
          v-model:page-size="logsPagination.size"
          :total="logsPagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleLogsSizeChange"
          @current-change="handleLogsPageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
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

// Country name mapping for ECharts (Chinese to English for common countries)
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
  '澳门': 'Macao'
}

// Initialize map chart
const initMapChart = () => {
  if (!mapChartRef.value) return

  mapChart = echarts.init(mapChartRef.value)

  // Prepare data for the map
  const mapData = prepareMapData()

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        if (params.data) {
          return `${params.name}<br/>Visits: ${params.data.value || 0}`
        }
        return params.name
      }
    },
    visualMap: {
      min: 0,
      max: Math.max(...mapData.map(d => d.value), 100),
      left: 'left',
      top: 'bottom',
      text: ['High', 'Low'],
      calculable: true,
      inRange: {
        color: ['#e0f3ff', '#3b82f6', '#1e40af']
      }
    },
    series: [
      {
        name: 'Visitors',
        type: 'map',
        map: 'world',
        roam: true,
        emphasis: {
          label: {
            show: true
          },
          itemStyle: {
            areaColor: '#fbbf24'
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
    // Convert Chinese country names to English for ECharts world map
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
      // Update map after data is loaded
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
  // Load world map GeoJSON
  try {
    const response = await fetch('https://cdn.jsdelivr.net/npm/echarts@5/map/json/world.json')
    const worldJson = await response.json()
    echarts.registerMap('world', worldJson)
  } catch (error) {
    console.error('Failed to load world map:', error)
  }

  // Initialize chart and fetch data
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
.stat-card {
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}
</style>

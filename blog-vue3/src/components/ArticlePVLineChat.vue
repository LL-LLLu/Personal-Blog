<template>
  <div class="p-4 bg-white rounded-xl border border-gray-200 shadow-sm dark:bg-gray-800 dark:border-gray-700">
    <!-- Header with stats -->
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-2">
        <div class="w-3 h-3 bg-gradient-to-r from-blue-400 to-purple-500 rounded-full animate-pulse" />
        <h3 class="text-sm font-semibold text-slate-700 dark:text-white">
          Weekly Page Views
        </h3>
      </div>
      <div class="text-xs text-slate-500 dark:text-gray-400 bg-white dark:bg-gray-800 px-2 py-1 rounded-full border dark:border-gray-600">
        {{ totalViews }} views in last 7 days
      </div>
    </div>

    <!-- Chart container with modern styling to match calendar -->
    <div class="relative bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-700 p-6 rounded-xl shadow-sm">
      <!-- Loading state overlay -->
      <div
        v-if="!props.value || !props.value.pvDates"
        class="absolute inset-0 flex items-center justify-center bg-white dark:bg-gray-800 bg-opacity-95 z-10 rounded-xl backdrop-blur-sm"
      >
        <div class="flex flex-col items-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500" />
          <span class="text-sm text-gray-600 dark:text-gray-300 mt-3 font-medium">Loading chart data...</span>
        </div>
      </div>

      <!-- Chart with matching height and proper alignment -->
      <div class="w-full flex justify-center">
        <div
          id="lineChat"
          class="rounded-lg overflow-hidden"
          style="width: 100%; height: 240px;"
        />
      </div>

      <!-- Enhanced trend indicator -->
      <div class="flex items-center justify-between mt-6 text-sm text-gray-600 dark:text-gray-300">
        <div class="flex items-center space-x-3">
          <div class="flex items-center space-x-2">
            <div class="w-4 h-4 bg-gradient-to-r from-blue-400 to-blue-600 rounded-full shadow-sm" />
            <span class="font-medium">Page Views Trend</span>
          </div>
        </div>
        <div class="font-medium">
          <span>{{ averageViews }} avg daily views</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { watch, computed, onMounted, onUnmounted } from 'vue'
import { useDark } from '@vueuse/core'

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Object, // 类型为对象
        default: null // 默认为 null
    }
})

// Dark mode state
const isDark = useDark({
    storageKey: 'vueuse-color-scheme'
})

// Calculate total views for display
const totalViews = computed(() => {
    if (props.value && props.value.pvCounts) {
        return props.value.pvCounts.reduce((sum, count) => sum + count, 0)
    }
    return 0
})

// Calculate average daily views
const averageViews = computed(() => {
    if (props.value && props.value.pvCounts && props.value.pvCounts.length > 0) {
        const total = props.value.pvCounts.reduce((sum, count) => sum + count, 0)
        return Math.round(total / props.value.pvCounts.length)
    }
    return 0
})


// 初始化折线图
function initLineChat() {
    var chartDom = document.getElementById('lineChat');
    
    // Dispose existing chart instance if it exists
    if (chartInstance) {
        chartInstance.dispose();
    }
    
    // Initialize without fixed width to allow responsiveness
    var myChart = echarts.init(chartDom);
    chartInstance = myChart; // Store the instance
    var option;

    // 从 props.value 中获取日期集合和 pv 访问量集合
    const pvDates = props.value.pvDates
    const pvCounts = props.value.pvCounts

    const isDarkMode = isDark.value

    option = {
        backgroundColor: 'transparent',
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '4%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: pvDates, // x 轴数据
            boundaryGap: false,
            axisLine: {
                lineStyle: {
                    color: isDarkMode ? '#4b5563' : '#e2e8f0',
                }
            },
            axisLabel: {
                color: isDarkMode ? '#9ca3af' : '#64748b',
                margin: 15,
            },
            axisTick: {
                show: false
            }
        },
        yAxis: {
            type: 'value',
            splitLine: {
                lineStyle: {
                    color: isDarkMode ? '#374151' : '#f1f5f9',
                    type: 'dashed',
                }
            },
            axisLabel: {
                color: isDarkMode ? '#9ca3af' : '#64748b',
            }
        },
        series: [
            {
                data: pvCounts, // 设置 pv 访问量
                type: 'line',
                smooth: true,
                showSymbol: false,
                symbol: 'circle',
                symbolSize: 8,
                lineStyle: {
                    color: '#3b82f6',
                    width: 4,
                    shadowColor: 'rgba(59, 130, 246, 0.3)',
                    shadowBlur: 10,
                    shadowOffsetY: 4
                },
                itemStyle: {
                    color: '#3b82f6',
                    borderColor: '#fff',
                    borderWidth: 2,
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(59, 130, 246, 0.5)' },
                        { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
                    ])
                },
                emphasis: {
                    focus: 'series'
                }
            }
        ],
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            },
            backgroundColor: isDarkMode ? 'rgba(31, 41, 55, 0.95)' : 'rgba(255, 255, 255, 0.95)',
            borderColor: isDarkMode ? '#4b5563' : '#e5e7eb',
            textStyle: {
                color: isDarkMode ? '#f3f4f6' : '#1f2937'
            },
            padding: [10, 15],
            extraCssText: 'box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); border-radius: 8px;'
        }
    };

    option && myChart.setOption(option);
}

// Store chart instance globally for cleanup
let chartInstance = null

// Resize chart when window resizes
const handleResize = () => {
    if (chartInstance) {
        chartInstance.resize()
    }
}

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initLineChat 初始化折线图
watch(() => props.value, (newValue) => {
    if (newValue && newValue.pvDates && newValue.pvCounts) {
        initLineChat()
    }
}, { deep: true })

// Watch for dark mode changes and re-render chart
watch(isDark, () => {
    if (props.value && props.value.pvDates && props.value.pvCounts) {
        setTimeout(initLineChat, 50) // Re-render chart on dark mode change
    }
})

// Add resize listener when component mounts
onMounted(() => {
    window.addEventListener('resize', handleResize)
})

// Clean up when component unmounts
onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    if (chartInstance) {
        chartInstance.dispose()
    }
})
</script>
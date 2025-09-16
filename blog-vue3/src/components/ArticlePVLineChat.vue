<template>
    <div class="p-4 bg-white rounded-xl border border-gray-200 shadow-sm dark:bg-gray-800 dark:border-gray-700">
        <!-- Header with stats -->
        <div class="flex items-center justify-between mb-4">
            <div class="flex items-center space-x-2">
                <div class="w-3 h-3 bg-gradient-to-r from-blue-400 to-purple-500 rounded-full animate-pulse"></div>
                <h3 class="text-sm font-semibold text-slate-700 dark:text-white">Weekly Page Views</h3>
            </div>
            <div class="text-xs text-slate-500 dark:text-gray-400 bg-white dark:bg-gray-800 px-2 py-1 rounded-full border dark:border-gray-600">
                {{ totalViews }} views in last 7 days
            </div>
        </div>

        <!-- Chart container with modern styling to match calendar -->
        <div class="relative bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-700 p-6 rounded-xl shadow-sm">
            <!-- Loading state overlay -->
            <div v-if="!props.value || !props.value.pvDates" class="absolute inset-0 flex items-center justify-center bg-white dark:bg-gray-800 bg-opacity-95 z-10 rounded-xl backdrop-blur-sm">
                <div class="flex flex-col items-center">
                    <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                    <span class="text-sm text-gray-600 dark:text-gray-300 mt-3 font-medium">Loading chart data...</span>
                </div>
            </div>

            <!-- Chart with matching height and proper alignment -->
            <div class="w-full flex justify-center">
                <div id="lineChat" class="rounded-lg overflow-hidden" style="width: 100%; height: 240px;"></div>
            </div>

            <!-- Enhanced trend indicator -->
            <div class="flex items-center justify-between mt-6 text-sm text-gray-600 dark:text-gray-300">
                <div class="flex items-center space-x-3">
                    <div class="flex items-center space-x-2">
                        <div class="w-4 h-4 bg-gradient-to-r from-blue-400 to-blue-600 rounded-full shadow-sm"></div>
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

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Object, // 类型为对象
        default: null // 默认为 null
    }
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
    
    var myChart = echarts.init(chartDom, null, { width: 600 });
    chartInstance = myChart; // Store the instance
    var option;

    // 从 props.value 中获取日期集合和 pv 访问量集合
    const pvDates = props.value.pvDates
    const pvCounts = props.value.pvCounts

    option = {
        backgroundColor: 'transparent',
        grid: {
            left: 20,
            right: 20,
            top: 20,
            bottom: 40,
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: pvDates, // x 轴数据
            axisLine: {
                lineStyle: {
                    color: document.documentElement.classList.contains('dark') ? '#4b5563' : '#e2e8f0',
                    width: 2
                }
            },
            axisLabel: {
                color: document.documentElement.classList.contains('dark') ? '#d1d5db' : '#64748b',
                fontSize: 12,
                fontWeight: 500
            },
            axisTick: {
                show: false
            }
        },
        yAxis: {
            type: 'value',
            splitLine: {
                lineStyle: {
                    color: document.documentElement.classList.contains('dark') ? '#374151' : '#f1f5f9',
                    type: 'dashed',
                    width: 1
                }
            },
            axisLine: {
                show: false
            },
            axisLabel: {
                color: document.documentElement.classList.contains('dark') ? '#d1d5db' : '#64748b',
                fontSize: 12,
                fontWeight: 500
            },
            axisTick: {
                show: false
            }
        },
        series: [
            {
                data: pvCounts, // 设置 pv 访问量
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 8,
                lineStyle: {
                    color: '#3b82f6',
                    width: 4,
                    shadowColor: 'rgba(59, 130, 246, 0.3)',
                    shadowBlur: 8,
                    shadowOffsetY: 4
                },
                itemStyle: {
                    color: '#3b82f6',
                    borderColor: '#fff',
                    borderWidth: 3,
                    shadowColor: 'rgba(59, 130, 246, 0.5)',
                    shadowBlur: 8
                },
                areaStyle: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0, color: 'rgba(59, 130, 246, 0.4)'
                        }, {
                            offset: 0.7, color: 'rgba(59, 130, 246, 0.1)'
                        }, {
                            offset: 1, color: 'rgba(59, 130, 246, 0.02)'
                        }]
                    }
                },
                emphasis: {
                    itemStyle: {
                        shadowColor: 'rgba(59, 130, 246, 0.8)',
                        shadowBlur: 15
                    }
                }
            }
        ],
        tooltip: {
            trigger: 'axis',
            formatter: function(params) {
                const data = params[0]
                return `<div class="p-3">
                    <div class="font-semibold text-gray-900">${data.axisValue}</div>
                    <div class="text-sm text-gray-600 mt-1">${data.data.toLocaleString()} page views</div>
                </div>`
            },
            backgroundColor: document.documentElement.classList.contains('dark') ? 'rgba(31, 41, 55, 0.98)' : 'rgba(255, 255, 255, 0.98)',
            borderColor: document.documentElement.classList.contains('dark') ? '#4b5563' : '#e2e8f0',
            borderWidth: 1,
            borderRadius: 8,
            textStyle: {
                color: document.documentElement.classList.contains('dark') ? '#f3f4f6' : '#374151'
            },
            shadowColor: 'rgba(0, 0, 0, 0.15)',
            shadowBlur: 20,
            shadowOffsetY: 8
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
watch(() => document.documentElement.classList.contains('dark'), () => {
    if (props.value && props.value.pvDates && props.value.pvCounts) {
        setTimeout(initLineChat, 100) // Small delay to ensure DOM updates
    }
}, { immediate: false })

// Add resize listener when component mounts
onMounted(() => {
    window.addEventListener('resize', handleResize)

    // Watch for dark mode class changes on document element
    const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
                if (props.value && props.value.pvDates && props.value.pvCounts) {
                    setTimeout(initLineChat, 50) // Re-render chart on dark mode change
                }
            }
        })
    })

    observer.observe(document.documentElement, {
        attributes: true,
        attributeFilter: ['class']
    })

    // Store observer for cleanup
    window.chartObserver = observer
})

// Clean up when component unmounts
onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    if (chartInstance) {
        chartInstance.dispose()
    }
    if (window.chartObserver) {
        window.chartObserver.disconnect()
    }
})
</script>
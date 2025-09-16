<template>
    <div class="p-4 bg-white rounded-xl border border-gray-200 shadow-sm dark:bg-gray-800 dark:border-gray-700">
        <!-- Header with stats -->
        <div class="flex items-center justify-between mb-4">
            <div class="flex items-center space-x-2">
                <div class="w-3 h-3 bg-gradient-to-r from-green-400 to-emerald-500 rounded-full animate-pulse"></div>
                <h3 class="text-sm font-semibold text-slate-700 dark:text-white">Publishing Activity</h3>
            </div>
            <div class="text-xs text-slate-500 dark:text-gray-400 bg-white dark:bg-gray-800 px-2 py-1 rounded-full border dark:border-gray-600">
                {{ totalArticles }} articles in last 2 months
            </div>
        </div>

        <!-- Chart container with modern styling to match weekly page views -->
        <div class="relative bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-700 p-6 rounded-xl shadow-sm">
            <!-- Calendar chart -->
            <div class="w-full flex justify-center">
                <div id="calendar" class="rounded-lg overflow-hidden" style="width: 100%; height: 240px;"></div>
            </div>

            <!-- Enhanced activity indicator -->
            <div class="flex items-center justify-between mt-6 text-sm text-gray-600 dark:text-gray-300">
                <div class="flex items-center space-x-3">
                    <div class="flex items-center space-x-2">
                        <span class="legend-label">Less</span>
                        <div class="legend-dots">
                            <div class="legend-dot level-0"></div>
                            <div class="legend-dot level-1"></div>
                            <div class="legend-dot level-2"></div>
                            <div class="legend-dot level-3"></div>
                            <div class="legend-dot level-4"></div>
                        </div>
                        <span class="legend-label">More</span>
                    </div>
                </div>
                <div class="font-medium">
                    <span>{{ streakDays }} day streak</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { format, subMonths, differenceInDays } from 'date-fns'

// Exposed property values
const props = defineProps({
    value: { // Property name
        type: Object, // Type is object
        default: null // Default is null
    }
})

// Current date
const currentDate = new Date();
// Two months ago
const twoMonthsAgo = subMonths(currentDate, 2)

// Formatted start and end dates
const startDate = format(twoMonthsAgo, 'yyyy-MM-dd')
const endDate = format(currentDate, 'yyyy-MM-dd')

// Calendar heatmap data
const calendarData = []

// Chart instance for cleanup
let chartInstance = null

// Computed statistics
const totalArticles = computed(() => {
    if (!props.value) return 0
    return Object.values(props.value).reduce((sum, count) => sum + count, 0)
})

const streakDays = computed(() => {
    if (!props.value) return 0
    // Calculate current streak (simplified logic)
    const dates = Object.keys(props.value).sort().reverse()
    let streak = 0
    for (const date of dates) {
        if (props.value[date] > 0) {
            streak++
        } else {
            break
        }
    }
    return streak
})

// Format period display
const formatPeriod = () => {
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    const startMonth = monthNames[twoMonthsAgo.getMonth()]
    const endMonth = monthNames[currentDate.getMonth()]
    return `${startMonth} ${twoMonthsAgo.getFullYear()} - ${endMonth} ${currentDate.getFullYear()}`
}

// Initialize calendar heatmap
function initCalendar() {
    // Clear previous data
    calendarData.length = 0

    // Set incoming data to calendarData array
    let dataMap = props.value
    if (dataMap) {
        for (let key in dataMap) {
            calendarData.push([
                key,
                dataMap[key]
            ]);
        }
    }

    var chartDom = document.getElementById('calendar');
    if (!chartDom) return;

    // Dispose existing chart instance if it exists
    if (chartInstance) {
        chartInstance.dispose();
    }

    var myChart = echarts.init(chartDom, null, {
        width: chartDom.clientWidth,
        height: 220
    });
    chartInstance = myChart; // Store the instance

    // Detect dark mode
    const isDarkMode = document.documentElement.classList.contains('dark');

    var option = {
        backgroundColor: 'transparent',
        visualMap: {
            show: false,
            min: 0,
            max: Math.max(10, Math.max(...calendarData.map(d => d[1])) || 10),
            inRange: {
                color: isDarkMode
                    ? ['#111827', '#064e3b', '#065f46', '#047857', '#059669'] // Enhanced dark green contrast
                    : ['#ecfdf5', '#d1fae5', '#a7f3d0', '#6ee7b7', '#34d399'] // Enhanced light green contrast
            }
        },
        calendar: {
            range: [startDate, endDate],
            cellSize: ['auto', 11],
            left: '8%',
            right: '8%',
            top: 25,
            bottom: 25,
            width: '84%',
            height: 170,
            yearLabel: {
                show: true,
                fontSize: 13,
                fontWeight: '600',
                color: isDarkMode ? '#e5e7eb' : '#1f2937'
            },
            monthLabel: {
                show: true,
                fontSize: 11,
                fontWeight: '500',
                color: isDarkMode ? '#d1d5db' : '#374151'
            },
            dayLabel: {
                show: true,
                fontSize: 10,
                color: isDarkMode ? '#d1d5db' : '#374151'
            },
            itemStyle: {
                borderWidth: 1,
                borderColor: isDarkMode ? 'rgba(75, 85, 99, 0.6)' : 'rgba(243, 244, 246, 0.8)',
                borderRadius: 3,
                shadowBlur: 2,
                shadowColor: isDarkMode ? 'rgba(0, 0, 0, 0.3)' : 'rgba(0, 0, 0, 0.08)',
                opacity: 0.9
            }
        },
        series: {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: calendarData,
            emphasis: {
                itemStyle: {
                    borderColor: isDarkMode ? '#10b981' : '#059669',
                    borderWidth: 3,
                    shadowBlur: 16,
                    shadowColor: isDarkMode ? 'rgba(16, 185, 129, 0.8)' : 'rgba(5, 150, 105, 0.7)',
                    borderRadius: 4,
                    shadowOffsetX: 0,
                    shadowOffsetY: 2
                }
            },
            select: {
                itemStyle: {
                    borderColor: isDarkMode ? '#2E8735' : '#3EC749',
                    borderWidth: 2,
                    shadowBlur: 8,
                    shadowColor: isDarkMode ? 'rgba(16, 185, 129, 0.4)' : 'rgba(5, 150, 105, 0.4)'
                }
            }
        },
        tooltip: {
            trigger: 'item',
            formatter: function(params) {
                const date = params.data[0]
                const count = params.data[1]
                const dateObj = new Date(date)
                const formattedDate = dateObj.toLocaleDateString('en-US', {
                    month: 'short',
                    day: 'numeric',
                    year: 'numeric'
                })
                return `<div class="p-3">
                    <div class="font-semibold" style="color: ${isDarkMode ? '#f9fafb' : '#111827'}">${formattedDate}</div>
                    <div class="text-sm mt-1" style="color: ${isDarkMode ? '#d1d5db' : '#6b7280'}">${count === 0 ? 'No articles published' : count === 1 ? '1 article published' : `${count} articles published`}</div>
                </div>`
            },
            backgroundColor: isDarkMode ? 'rgba(17, 24, 39, 0.98)' : 'rgba(255, 255, 255, 0.98)',
            borderColor: isDarkMode ? '#374151' : '#e5e7eb',
            borderWidth: 1,
            borderRadius: 8,
            textStyle: {
                color: isDarkMode ? '#f9fafb' : '#111827'
            },
            shadowColor: isDarkMode ? 'rgba(0, 0, 0, 0.4)' : 'rgba(0, 0, 0, 0.15)',
            shadowBlur: 20,
            shadowOffsetY: 8
        }
    };

    option && myChart.setOption(option);
}

// Watch for dark mode changes and re-render calendar
const observer = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
        if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
            if (props.value) {
                setTimeout(initCalendar, 50) // Re-render calendar on dark mode change
            }
        }
    })
})

// Resize chart when window resizes
const handleResize = () => {
    if (chartInstance) {
        const chartDom = document.getElementById('calendar')
        if (chartDom) {
            chartInstance.resize({
                width: chartDom.clientWidth,
                height: 220
            })
        }
    }
}

// Watch property, monitor changes to props.value, once props.value changes, call initCalendar to initialize calendar heatmap
watch(() => props.value, () => {
    if (props.value) {
        initCalendar()
    }
})

onMounted(() => {
    // Add resize listener
    window.addEventListener('resize', handleResize)

    // Watch for dark mode class changes on document element
    observer.observe(document.documentElement, {
        attributes: true,
        attributeFilter: ['class']
    })
})

onUnmounted(() => {
    // Clean up
    window.removeEventListener('resize', handleResize)
    if (chartInstance) {
        chartInstance.dispose()
    }
    if (observer) {
        observer.disconnect()
    }
})

</script>

<style scoped>
.legend-label {
    font-size: 0.75rem;
    color: #6b7280;
    font-weight: 500;
}

.dark .legend-label {
    color: #9ca3af;
}

.legend-dots {
    display: flex;
    gap: 2px;
    margin: 0 8px;
}

.legend-dot {
    width: 8px;
    height: 8px;
    border-radius: 2px;
    transition: all 0.15s ease;
}

.legend-dot.level-0 {
    background-color: #ecfdf5;
    border: 1px solid #d1fae5;
}

.legend-dot.level-1 {
    background-color: #d1fae5;
}

.legend-dot.level-2 {
    background-color: #a7f3d0;
}

.legend-dot.level-3 {
    background-color: #6ee7b7;
}

.legend-dot.level-4 {
    background-color: #34d399;
}

.dark .legend-dot.level-0 {
    background-color: #111827;
    border-color: #064e3b;
}

.dark .legend-dot.level-1 {
    background-color: #064e3b;
}

.dark .legend-dot.level-2 {
    background-color: #065f46;
}

.dark .legend-dot.level-3 {
    background-color: #047857;
}

.dark .legend-dot.level-4 {
    background-color: #059669;
}

/* Modern tooltip */
:global(.apple-tooltip) {
    padding: 12px 16px !important;
    border-radius: 12px !important;
    backdrop-filter: blur(32px) saturate(180%) !important;
    border: 1px solid rgba(255, 255, 255, 0.2) !important;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12),
                0 1px 2px rgba(0, 0, 0, 0.08) !important;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif !important;
    transform: translateY(-2px) !important;
    animation: tooltipSlideIn 0.2s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

@keyframes tooltipSlideIn {
    from {
        opacity: 0;
        transform: translateY(4px) scale(0.95);
    }
    to {
        opacity: 1;
        transform: translateY(-2px) scale(1);
    }
}

:global(.apple-tooltip.light) {
    background: linear-gradient(145deg,
        rgba(255, 255, 255, 0.98) 0%,
        rgba(248, 250, 252, 0.98) 100%) !important;
    color: #1f2937 !important;
}

:global(.apple-tooltip.dark) {
    background: linear-gradient(145deg,
        rgba(17, 24, 39, 0.98) 0%,
        rgba(31, 41, 55, 0.98) 100%) !important;
    color: #f9fafb !important;
    border-color: rgba(75, 85, 99, 0.4) !important;
}

:global(.tooltip-date) {
    font-weight: 600 !important;
    font-size: 13px !important;
    margin-bottom: 4px !important;
    background: linear-gradient(135deg, #065f46 0%, #10b981 100%) !important;
    -webkit-background-clip: text !important;
    -webkit-text-fill-color: transparent !important;
    background-clip: text !important;
}

:global(.tooltip-count) {
    font-size: 12px !important;
    font-weight: 500 !important;
    opacity: 0.9 !important;
}

/* Modern responsive design */
@media (max-width: 768px) {
    .apple-calendar-container {
        padding: 20px;
        border-radius: 16px;
    }

    .calendar-chart {
        height: 200px;
        border-radius: 12px;
        padding: 6px;
    }

    .legend-container {
        gap: 6px;
        padding: 6px 10px;
        border-radius: 10px;
    }

    .legend-label {
        font-size: 0.6875rem;
    }

    .legend-dot {
        width: 8px;
        height: 8px;
        border-radius: 2px;
    }

    .calendar-title {
        font-size: 1.125rem;
    }

    :global(.apple-tooltip) {
        padding: 10px 14px !important;
        border-radius: 10px !important;
    }
}
</style>
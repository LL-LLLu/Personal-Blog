<template>
  <div class="p-4 bg-white rounded-xl border border-gray-200 shadow-sm dark:bg-gray-800 dark:border-gray-700">
    <!-- Header with stats -->
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-2">
        <div class="w-3 h-3 bg-gradient-to-r from-green-400 to-emerald-500 rounded-full animate-pulse" />
        <h3 class="text-sm font-semibold text-slate-700 dark:text-white">
          Publishing Activity
        </h3>
      </div>
      <div class="text-xs text-slate-500 dark:text-gray-400 bg-white dark:bg-gray-800 px-2 py-1 rounded-full border dark:border-gray-600">
        {{ totalArticles }} articles in last 2 months
      </div>
    </div>

    <!-- Chart container with modern styling to match weekly page views -->
    <div class="relative bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-700 p-6 rounded-xl shadow-sm">
      <!-- Calendar chart -->
      <div class="w-full flex justify-center">
        <div
          id="calendar"
          class="rounded-lg overflow-hidden"
          style="width: 100%; height: 240px;"
        />
      </div>

      <!-- Enhanced activity indicator -->
      <div class="flex items-center justify-between mt-6 text-sm text-gray-600 dark:text-gray-300">
        <div class="flex items-center space-x-3">
          <div class="flex items-center space-x-2">
            <span class="legend-label">Less</span>
            <div class="legend-dots">
              <div class="legend-dot level-0" />
              <div class="legend-dot level-1" />
              <div class="legend-dot level-2" />
              <div class="legend-dot level-3" />
              <div class="legend-dot level-4" />
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
import { computed, watch, onMounted, onUnmounted } from 'vue'
import { format, subMonths } from 'date-fns'

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

    // Initialize with responsive width
    var myChart = echarts.init(chartDom);
    chartInstance = myChart; // Store the instance

    // Detect dark mode
    const isDarkMode = document.documentElement.classList.contains('dark');

    var option = {
        backgroundColor: 'transparent',
        visualMap: {
            show: false,
            min: 0,
            max: Math.max(5, Math.max(...calendarData.map(d => d[1])) || 5),
            inRange: {
                color: isDarkMode
                    ? ['#1f2937', '#064e3b', '#065f46', '#047857', '#059669', '#10b981'] // Deeper greens for dark mode
                    : ['#f3f4f6', '#d1fae5', '#a7f3d0', '#6ee7b7', '#34d399', '#10b981'] // Fresher greens for light mode
            }
        },
        calendar: {
            range: [startDate, endDate],
            cellSize: ['auto', 13], // Slightly larger cells
            left: 'center',
            top: 30,
            bottom: 10,
            width: '90%', // Use percentage width
            height: 160,
            yearLabel: {
                show: false // Hide year label as it's implied or in header
            },
            monthLabel: {
                show: true,
                fontSize: 12,
                color: isDarkMode ? '#9ca3af' : '#64748b',
                nameMap: 'en',
                margin: 10
            },
            dayLabel: {
                show: true,
                firstDay: 1, // Start on Monday
                nameMap: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
                fontSize: 10,
                color: isDarkMode ? '#9ca3af' : '#94a3b8'
            },
            itemStyle: {
                borderWidth: 2,
                borderColor: isDarkMode ? '#1f2937' : '#fff', // Match container bg for gaps
                borderRadius: 2
            },
            splitLine: {
                show: false
            }
        },
        series: {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: calendarData,
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        },
        tooltip: {
            trigger: 'item',
            padding: [8, 12],
            backgroundColor: isDarkMode ? 'rgba(31, 41, 55, 0.95)' : 'rgba(255, 255, 255, 0.95)',
            borderColor: isDarkMode ? '#4b5563' : '#e5e7eb',
            textStyle: {
                color: isDarkMode ? '#f3f4f6' : '#1f2937'
            },
            formatter: function(params) {
                const date = new Date(params.data[0]);
                const count = params.data[1];
                const dateStr = date.toLocaleDateString('en-US', { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' });
                
                return `<div class="font-medium text-sm">
                          <div class="mb-1">${dateStr}</div>
                          <div><span class="font-bold">${count}</span> article${count !== 1 ? 's' : ''}</div>
                        </div>`;
            }
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
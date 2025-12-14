<template>
  <div class="p-6 bg-white rounded-2xl border border-gray-100 shadow-lg dark:bg-gray-800 dark:border-gray-700 transition-all duration-300 hover:shadow-xl">
    <!-- Header with stats -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 flex items-center justify-center bg-indigo-50 dark:bg-indigo-900/30 rounded-full">
          <svg class="w-5 h-5 text-indigo-500 dark:text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
        </div>
        <div>
          <h3 class="text-base font-bold text-gray-800 dark:text-white leading-tight">
            Publishing Activity
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 font-medium mt-0.5">
            {{ totalArticles }} articles in last 6 months
          </p>
        </div>
      </div>
      
      <div class="flex flex-col items-end">
         <div class="flex items-center space-x-1.5 bg-green-50 dark:bg-green-900/20 px-3 py-1.5 rounded-lg border border-green-100 dark:border-green-900/30">
            <span class="relative flex h-2.5 w-2.5">
              <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
              <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-500"></span>
            </span>
            <span class="text-sm font-bold text-green-700 dark:text-green-400">{{ streakDays }} day streak</span>
         </div>
      </div>
    </div>

    <!-- Chart container -->
    <div class="relative w-full overflow-hidden">
      <!-- Calendar chart -->
      <div class="w-full flex justify-center">
        <div
          id="calendar"
          class="w-full h-[200px]"
        />
      </div>

      <!-- Modern Legend -->
      <div class="flex items-center justify-end mt-4 space-x-4">
        <span class="text-xs font-medium text-gray-400 dark:text-gray-500">Less</span>
        <div class="flex items-center space-x-1">
          <div class="w-3 h-3 rounded-sm bg-gray-100 dark:bg-gray-700/50"></div>
          <div class="w-3 h-3 rounded-sm bg-indigo-100 dark:bg-indigo-900/40"></div>
          <div class="w-3 h-3 rounded-sm bg-indigo-300 dark:bg-indigo-700"></div>
          <div class="w-3 h-3 rounded-sm bg-indigo-500 dark:bg-indigo-500"></div>
          <div class="w-3 h-3 rounded-sm bg-indigo-700 dark:bg-indigo-400"></div>
        </div>
        <span class="text-xs font-medium text-gray-400 dark:text-gray-500">More</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, watch, onMounted, onUnmounted } from 'vue'
import { format, subMonths } from 'date-fns'
import { useDark } from '@vueuse/core'

// Exposed property values
const props = defineProps({
    value: { // Property name
        type: Object, // Type is object
        default: null // Default is null
    }
})

// Dark mode state
const isDark = useDark({
    storageKey: 'vueuse-color-scheme'
})

// Current date
const currentDate = new Date();
// 5 months ago for start (showing ~6 months total)
const startDateObj = subMonths(currentDate, 5)
// End of current month
const endDateObj = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0)

// Formatted start and end dates
const startDate = format(startDateObj, 'yyyy-MM-dd')
const endDate = format(endDateObj, 'yyyy-MM-dd')

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
    // Check if today has activity, if not, check yesterday to start streak
    const todayStr = format(new Date(), 'yyyy-MM-dd')
    
    // If no activity today, we can still have a streak if there was activity yesterday
    // But for "current streak", it usually implies consecutive days up to now.
    // Let's iterate backwards.
    
    // Simple check: strict consecutive days
    // Convert to set for O(1) lookup
    const activeDates = new Set(dates.filter(d => props.value[d] > 0))
    
    let checkDate = new Date()
    // Safety break to prevent infinite loops, check max 365 days
    for (let i = 0; i < 365; i++) {
        const checkStr = format(checkDate, 'yyyy-MM-dd')
        if (activeDates.has(checkStr)) {
            streak++
            checkDate.setDate(checkDate.getDate() - 1)
        } else {
             // If it's today and no activity, try yesterday (maybe user hasn't posted yet today)
             if (checkStr === todayStr && streak === 0) {
                 checkDate.setDate(checkDate.getDate() - 1)
                 continue
             }
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
    var myChart = echarts.init(chartDom, null, { renderer: 'svg' });
    chartInstance = myChart; // Store the instance

    // Use reactive dark mode state
    const isDarkMode = isDark.value;

    // Modern color palette
    // Light mode: Grey -> Indigo Fade
    const lightColors = ['#f3f4f6', '#c7d2fe', '#818cf8', '#6366f1', '#4f46e5']
    // Dark mode: Dark Grey -> Brighter Indigo for visibility
    const darkColors = ['#374151', '#4f46e5', '#6366f1', '#818cf8', '#a5b4fc']

    var option = {
        backgroundColor: 'transparent',
        visualMap: {
            show: false,
            min: 0,
            max: Math.max(4, Math.max(...calendarData.map(d => d[1])) || 4),
            inRange: {
                color: isDarkMode ? darkColors : lightColors
            }
        },
        calendar: {
            range: [startDate, endDate],
            cellSize: ['auto', 16], // Slightly smaller, tighter cells
            left: 'center',
            top: 25,
            bottom: 0,
            width: '95%',
            height: 'auto',
            yearLabel: { show: false },
            monthLabel: {
                show: true,
                fontSize: 12,
                color: isDarkMode ? '#9ca3af' : '#6b7280', // gray-400 : gray-500
                nameMap: 'en',
                margin: 8,
                fontWeight: 500
            },
            dayLabel: {
                show: true,
                firstDay: 1, // Start on Monday
                nameMap: ['Sun', '', 'Tue', '', 'Thu', '', 'Sat'],
                fontSize: 10,
                color: isDarkMode ? '#6b7280' : '#9ca3af', // Subtle day labels
                margin: 5
            },
            itemStyle: {
                borderWidth: 3, // Gap between cells
                borderColor: 'transparent', // Make gap transparent to show background
                borderRadius: 3 // Rounded corners for modern look
            },
            splitLine: { show: false }
        },
        series: {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: calendarData,
            emphasis: {
                itemStyle: {
                    shadowBlur: 5,
                    shadowColor: 'rgba(0, 0, 0, 0.2)',
                    borderColor: isDarkMode ? '#fff' : '#000',
                    borderWidth: 1
                }
            }
        },
        tooltip: {
            trigger: 'item',
            padding: [10, 14],
            backgroundColor: isDarkMode ? 'rgba(17, 24, 39, 0.95)' : 'rgba(255, 255, 255, 0.95)',
            borderColor: isDarkMode ? '#374151' : '#e5e7eb',
            borderWidth: 1,
            textStyle: {
                color: isDarkMode ? '#f3f4f6' : '#1f2937',
                fontFamily: 'sans-serif'
            },
            formatter: function(params) {
                const date = new Date(params.data[0]);
                const count = params.data[1];
                const dateStr = date.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
                
                return `<div class="flex flex-col gap-1">
                          <span class="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">${dateStr}</span>
                          <span class="text-sm font-bold flex items-center gap-2">
                            <span class="w-2 h-2 rounded-full ${count > 0 ? 'bg-indigo-500' : 'bg-gray-300'}"></span>
                            ${count} article${count !== 1 ? 's' : ''}
                          </span>
                        </div>`;
            }
        }
    };

    option && myChart.setOption(option);
}

// Watch for dark mode changes and re-render calendar
watch(isDark, () => {
    if (props.value) {
        setTimeout(initCalendar, 50) // Re-render calendar on dark mode change
    }
})

// Resize chart when window resizes
const handleResize = () => {
    if (chartInstance) {
        const chartDom = document.getElementById('calendar')
        if (chartDom) {
            chartInstance.resize({
                width: chartDom.clientWidth,
                height: 200
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
})

onUnmounted(() => {
    // Clean up
    window.removeEventListener('resize', handleResize)
    if (chartInstance) {
        chartInstance.dispose()
    }
})
</script>

<style scoped>
/* No extra styles needed, using Tailwind utility classes */
</style>
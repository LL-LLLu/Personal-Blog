<template>
    <!-- Calendar heatmap container -->
    <div id="calendar" class="overflow-x-auto w-full h-60 bg-white dark:bg-gray-800 rounded-lg"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { watch, onMounted, onUnmounted } from 'vue'
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
// Six months ago
const sixMonthsAgo = subMonths(currentDate, 6)

// Formatted start and end dates
const startDate = format(sixMonthsAgo, 'yyyy-MM-dd')
const endDate = format(currentDate, 'yyyy-MM-dd')

// Calendar heatmap data
const calendarData = []

// Chart instance for cleanup
let chartInstance = null

// Initialize calendar heatmap
function initCalendar() {
    // Clear previous data
    calendarData.length = 0

    // Set incoming data to calendarData array
    let dataMap = props.value
    for (let key in dataMap) {
        calendarData.push([
            key,
            dataMap[key]
        ]);
    }

    var chartDom = document.getElementById('calendar');

    // Dispose existing chart instance if it exists
    if (chartInstance) {
        chartInstance.dispose();
    }

    var myChart = echarts.init(chartDom, null, { width: 600 });
    chartInstance = myChart; // Store the instance

    // Detect dark mode
    const isDarkMode = document.documentElement.classList.contains('dark');

    var option = {
        backgroundColor: 'transparent',
        visualMap: {
            show: false,
            min: 0,
            max: 10,
            inRange: {
                color: isDarkMode
                    ? ['#1f2937', '#059669', '#047857', '#065f46'] // Dark mode colors
                    : ['#ebedf0', '#9be9a8', '#40c463', '#30a14e', '#216e39'] // Light mode colors (GitHub-like)
            }
        },
        calendar: { // Calendar display range: start date - end date
            range: [startDate, endDate],
            cellSize: ['auto', 13],
            yearLabel: {
                show: true,
                fontSize: 12,
                color: isDarkMode ? '#d1d5db' : '#586069'
            },
            monthLabel: {
                show: true,
                fontSize: 11,
                color: isDarkMode ? '#d1d5db' : '#586069'
            },
            dayLabel: {
                show: true,
                fontSize: 10,
                color: isDarkMode ? '#d1d5db' : '#586069'
            },
            itemStyle: {
                borderWidth: 1,
                borderColor: isDarkMode ? '#374151' : '#e1e4e8'
            }
        },
        series: {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: calendarData
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
        chartInstance.resize()
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
#calendar {
    transition: background-color 0.3s ease;
}
</style>
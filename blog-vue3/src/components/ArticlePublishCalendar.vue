<template>
    <!-- 日历热点图容器 -->
    <div id="calendar" class="overflow-x-auto w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { watch, onMounted, onUnmounted } from 'vue'
import { format, subMonths } from 'date-fns'
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

// 当前日期
const currentDate = new Date();
// 半年前
const sixMonthsAgo = subMonths(currentDate, 6)

// 格式化后的开始、结束日期
const startDate = format(sixMonthsAgo, 'yyyy-MM-dd')
const endDate = format(currentDate, 'yyyy-MM-dd')

// 日历热点数据
const myData = []
let myChart = null

// 初始化日历热点图
function initCalendar() {
    // 将传入的数据设置到 myDate 数组中
    let map = props.value
    myData.length = 0 // Clear array
    if (map) {
        for (let key in map) {
            myData.push([
                key,
                map[key]
            ]);
        }
    }

    var chartDom = document.getElementById('calendar');
    if (!chartDom) return;
    
    // Dispose if exists
    if (myChart) {
        myChart.dispose();
    }

    // Initialize with SVG renderer
    myChart = echarts.init(chartDom, null, { renderer: 'svg' });
    
    const isDarkMode = isDark.value

    var option = {
        visualMap: {
            show: false,
            min: 0,
            max: Math.max(5, Math.max(...myData.map(d => d[1])) || 5), // Dynamic max
            inRange: {
                color: isDarkMode 
                    ? ['#1f2937', '#0e4429', '#006d32', '#26a641', '#39d353'] // GitHub Dark Theme Colors
                    : ['#ebedf0', '#9be9a8', '#40c463', '#30a14e', '#216e39'] // GitHub Light Theme Colors
            }
        },
        calendar: { // 日历显示的范围：开始日期 - 结束日期
            range: [startDate, endDate],
            cellSize: ['auto', 16], // Cell size
            yearLabel: { show: false },
            dayLabel: {
                nameMap: 'en',
                color: isDarkMode ? '#8b949e' : '#24292f'
            },
            monthLabel: {
                nameMap: 'en',
                color: isDarkMode ? '#8b949e' : '#24292f'
            },
            itemStyle: {
                borderColor: 'transparent',
                borderWidth: 4
            },
            splitLine: { show: false }
        },
        series: {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: myData,
            itemStyle: {
                borderRadius: 2
            }
        },
        tooltip: {
            formatter: function (p) {
                const format = echarts.format.formatTime('yyyy-MM-dd', p.data[0]);
                return format + ': ' + p.data[1] + ' articles';
            }
        }
    };

    option && myChart.setOption(option);
}

// 侦听属性, 监听 props.value 的变化，一旦 props.value 发生变化，就调用 initCalendar 初始化日历热点图
watch(() => props.value, () => initCalendar())

// Watch dark mode change
watch(isDark, () => {
    // Small delay to ensure styles are updated
    setTimeout(() => {
        initCalendar()
    }, 100)
})

// Resize handler
const handleResize = () => {
    if (myChart) {
        myChart.resize()
    }
}

onMounted(() => {
    window.addEventListener('resize', handleResize)
    // Initialize if data is already available
    if (props.value) {
        initCalendar()
    }
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    if (myChart) {
        myChart.dispose()
    }
})

</script>
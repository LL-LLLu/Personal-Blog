<template>
  <div class="p-6 bg-white rounded-2xl border border-gray-100 shadow-lg dark:bg-gray-800 dark:border-gray-700 transition-all duration-300 hover:shadow-xl">
    <!-- Header with stats -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 flex items-center justify-center bg-indigo-50 dark:bg-indigo-900/30 rounded-full transition-colors">
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

    <!-- Calendar Grid Container -->
    <div class="w-full overflow-x-auto pb-2 scrollbar-hide">
      <div class="min-w-max flex flex-col gap-1">
        <!-- Month Labels -->
        <div class="flex text-xs text-gray-400 dark:text-gray-500 mb-2 pl-8">
            <div v-for="(month, index) in monthLabels" :key="index" :style="{ width: month.width + 'px' }">
                {{ month.label }}
            </div>
        </div>

        <div class="flex gap-1">
             <!-- Day of Week Labels -->
            <div class="flex flex-col gap-1 mr-2 text-[10px] text-gray-400 dark:text-gray-500 font-medium h-full justify-between py-1">
                <span>Mon</span>
                <span>Wed</span>
                <span>Fri</span>
            </div>

            <!-- The Grid -->
            <div class="flex gap-1">
                <div v-for="(week, wIndex) in calendarWeeks" :key="wIndex" class="flex flex-col gap-1">
                    <div 
                        v-for="(day, dIndex) in week" 
                        :key="dIndex"
                        class="w-3 h-3 rounded-[3px] transition-all duration-200 hover:scale-125 hover:z-10 cursor-pointer relative group"
                        :class="getColorClass(day.count)"
                    >
                         <!-- Native Tooltip-like popover on hover using group-hover -->
                        <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 w-max px-2 py-1 bg-gray-900 text-white text-xs rounded shadow-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none z-20 whitespace-nowrap">
                            <div class="font-semibold">{{ day.dateStr }}</div>
                            <div class="text-gray-300">{{ day.count }} articles</div>
                            <!-- Arrow -->
                            <div class="absolute top-full left-1/2 transform -translate-x-1/2 border-4 border-transparent border-t-gray-900"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
      </div>
    </div>

    <!-- Legend -->
    <div class="flex items-center justify-end mt-4 space-x-2 text-xs text-gray-400 dark:text-gray-500">
        <span>Less</span>
        <div class="w-3 h-3 rounded-[3px] bg-gray-100 dark:bg-gray-700/50"></div>
        <div class="w-3 h-3 rounded-[3px] bg-indigo-200 dark:bg-indigo-900/60"></div>
        <div class="w-3 h-3 rounded-[3px] bg-indigo-400 dark:bg-indigo-600"></div>
        <div class="w-3 h-3 rounded-[3px] bg-indigo-600 dark:bg-indigo-400"></div>
        <div class="w-3 h-3 rounded-[3px] bg-indigo-800 dark:bg-indigo-300"></div>
        <span>More</span>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { format, subMonths, startOfWeek, addDays, getDay, getMonth, startOfMonth, endOfMonth, differenceInCalendarWeeks } from 'date-fns'

// Exposed property values
const props = defineProps({
    value: { // Property name
        type: Object, // Type is object
        default: () => ({}) 
    }
})

// Computed statistics
const totalArticles = computed(() => {
    if (!props.value) return 0
    return Object.values(props.value).reduce((sum, count) => sum + count, 0)
})

const streakDays = computed(() => {
    if (!props.value) return 0
    const dates = Object.keys(props.value).sort().reverse()
    let streak = 0
    const todayStr = format(new Date(), 'yyyy-MM-dd')
    const activeDates = new Set(dates.filter(d => props.value[d] > 0))
    
    let checkDate = new Date()
    for (let i = 0; i < 365; i++) {
        const checkStr = format(checkDate, 'yyyy-MM-dd')
        if (activeDates.has(checkStr)) {
            streak++
            checkDate.setDate(checkDate.getDate() - 1)
        } else {
             if (checkStr === todayStr && streak === 0) {
                 checkDate.setDate(checkDate.getDate() - 1)
                 continue
             }
             break
        }
    }
    return streak
})

// --- Calendar Logic ---

// We want to show the last ~6 months, ending today.
// Or nicely aligned to weeks.
const today = new Date()
const endDate = today
// Go back 24 weeks (approx 6 months)
const weeksToShow = 26 
const startDate = addDays(startOfWeek(subMonths(today, 5), { weekStartsOn: 0 }), -((weeksToShow * 7) - differenceInCalendarWeeks(today, subMonths(today, 5)) * 7)) // Rough adjustment, let's just do fixed weeks
// Actually, let's just start 26 weeks ago from the start of this week
const gridStartDate = startOfWeek(addDays(today, -(weeksToShow * 7)), { weekStartsOn: 1 }) // Start on Monday?
// Let's stick to Sunday start for consistency with most cals, or Monday? Code above had Monday labels. Let's do Monday (1)
// Adjust gridStartDate to be a Monday
const realStartDate = startOfWeek(addDays(new Date(), -180), { weekStartsOn: 1 })


const calendarWeeks = computed(() => {
    const weeks = []
    let currentWeek = []
    let iterDate = new Date(realStartDate)
    const end = new Date() // Today

    // Generate full weeks until we pass today
    // We want a fixed grid size roughly? Or just up to today?
    // Let's fill up to the end of the current week to make it square-ish
    const gridEnd = addDays(startOfWeek(end, { weekStartsOn: 1 }), 6)

    while (iterDate <= gridEnd) {
        const dateStr = format(iterDate, 'yyyy-MM-dd')
        const count = props.value && props.value[dateStr] ? props.value[dateStr] : 0
        
        currentWeek.push({
            date: new Date(iterDate),
            dateStr: format(iterDate, 'MMM d, yyyy'),
            count: count
        })

        if (currentWeek.length === 7) {
            weeks.push(currentWeek)
            currentWeek = []
        }
        iterDate = addDays(iterDate, 1)
    }
    return weeks
})

const monthLabels = computed(() => {
    const labels = []
    let currentMonth = -1
    // Calculate width based on weeks belonging to a month
    // Each week is w-3 (12px) + gap-1 (4px) = 16px approx width
    const weekWidth = 16 
    
    calendarWeeks.value.forEach((week, index) => {
        // Check the month of the first day of the week (or the majority?)
        // Usually we label the month where the first day of the week falls, or just checking transitions
        const firstDay = week[0].date
        const m = getMonth(firstDay)
        
        if (m !== currentMonth) {
            labels.push({
                label: format(firstDay, 'MMM'),
                width: weekWidth,
                startIndex: index
            })
            currentMonth = m
        } else {
            // Add width to the last label
            if (labels.length > 0) {
                labels[labels.length - 1].width += weekWidth
            }
        }
    })
    return labels
})

function getColorClass(count) {
    if (count === 0) return 'bg-gray-100 dark:bg-gray-700/50'
    if (count <= 1) return 'bg-indigo-200 dark:bg-indigo-900/60'
    if (count <= 2) return 'bg-indigo-400 dark:bg-indigo-600'
    if (count <= 4) return 'bg-indigo-600 dark:bg-indigo-400'
    return 'bg-indigo-800 dark:bg-indigo-300'
}

</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}
.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
</style>
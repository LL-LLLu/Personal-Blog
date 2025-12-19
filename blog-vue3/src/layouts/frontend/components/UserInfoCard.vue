<template>
  <div class="w-full py-3 px-2 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700 transition-all duration-300">
    <!-- Header with minimize/expand button -->
    <div class="flex items-center justify-between w-full px-2 mb-2">
      <h3 class="text-base font-semibold text-gray-900 dark:text-white">
        Blogger Info
      </h3>
      <button
        class="p-1 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors duration-200" 
        :aria-label="isMinimized ? 'Expand blogger info' : 'Minimize blogger info'"
        @click="isMinimized = !isMinimized"
      >
        <svg
          class="w-4 h-4 text-gray-500 dark:text-gray-400 transition-transform duration-200" 
          :class="{ 'rotate-180': isMinimized }" 
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M19 9l-7 7-7-7"
          />
        </svg>
      </button>
    </div>
        
    <div
      v-show="!isMinimized"
      class="flex flex-col items-center"
    >
      <!-- Blogger avatar -->
      <div class="relative mb-3">
        <img
          class="w-12 h-12 rounded-full shadow"
          :src="blogSettingsStore.blogSettings.avatar"
        >    
        <span
          class="bottom-0 left-8 absolute w-3 h-3 bg-green-400 border-2 border-white dark:border-gray-800 rounded-full"
        />
      </div>
            
      <!-- Blogger nickname -->
      <h5 class="mb-2 text-lg font-medium text-gray-900 dark:text-white">
        {{ blogSettingsStore.blogSettings.author }}
      </h5>
            
      <!-- Introduction -->
      <span
        class="mb-4 text-sm text-gray-500 dark:text-gray-400 text-center px-2"
        data-tooltip-target="introduction-tooltip-bottom"
        data-tooltip-placement="bottom"
      >{{ blogSettingsStore.blogSettings.introduction }}</span>
                
      <div
        id="introduction-tooltip-bottom"
        role="tooltip"
        class="absolute z-10 invisible inline-block px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded shadow-sm opacity-0 tooltip dark:bg-gray-700"
      >
        Introduction
        <div
          class="tooltip-arrow"
          data-popper-arrow
        />
      </div>
            
      <!-- Statistics Cards - Responsive Layout -->
      <div class="w-full mb-4 space-y-2">
        <!-- Articles Statistics Card -->
        <div
          class="stats-card group cursor-pointer bg-gradient-to-r from-blue-50/80 to-indigo-50/80 dark:from-blue-900/10 dark:to-indigo-900/10 
                    border border-blue-200/50 dark:border-blue-800/30 rounded-xl p-2.5 hover:shadow-md hover:shadow-blue-500/10 
                    transform hover:scale-[1.01] transition-all duration-300 ease-out hover:border-blue-300 dark:hover:border-blue-600"
          @click="router.push('/archive/list')"
        >
          <div class="flex items-center justify-between">
            <div class="flex-1 min-w-0">
              <div class="flex items-baseline gap-1.5 mb-0.5">
                <CountTo
                  :value="statisticsInfo.articleTotalCount" 
                  custom-class="text-base sm:text-lg font-bold text-blue-600 dark:text-blue-400 group-hover:text-blue-700 dark:group-hover:text-blue-300 transition-colors"
                />
                <div class="w-1 h-1 bg-blue-500 rounded-full animate-pulse flex-shrink-0" />
              </div>
              <div class="text-[9px] sm:text-[10px] font-medium text-gray-600 dark:text-gray-400 uppercase tracking-wide truncate">
                Articles
              </div>
            </div>
            <div class="opacity-40 group-hover:opacity-70 transition-opacity flex-shrink-0 ml-2">
              <svg
                class="w-4 h-4 text-blue-500"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 015.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z" />
              </svg>
            </div>
          </div>
        </div>
                
        <!-- Categories Statistics Card -->
        <div
          class="stats-card group cursor-pointer bg-gradient-to-r from-emerald-50/80 to-green-50/80 dark:from-emerald-900/10 dark:to-green-900/10 
                    border border-emerald-200/50 dark:border-emerald-800/30 rounded-xl p-2.5 hover:shadow-md hover:shadow-emerald-500/10 
                    transform hover:scale-[1.01] transition-all duration-300 ease-out hover:border-emerald-300 dark:hover:border-emerald-600"
          @click="router.push('/category/list')"
        >
          <div class="flex items-center justify-between">
            <div class="flex-1 min-w-0">
              <div class="flex items-baseline gap-1.5 mb-0.5">
                <CountTo
                  :value="statisticsInfo.categoryTotalCount" 
                  custom-class="text-base sm:text-lg font-bold text-emerald-600 dark:text-emerald-400 group-hover:text-emerald-700 dark:group-hover:text-emerald-300 transition-colors"
                />
                <div class="w-1 h-1 bg-emerald-500 rounded-full animate-pulse flex-shrink-0" />
              </div>
              <div class="text-[9px] sm:text-[10px] font-medium text-gray-600 dark:text-gray-400 uppercase tracking-wide truncate">
                Categories
              </div>
            </div>
            <div class="opacity-40 group-hover:opacity-70 transition-opacity flex-shrink-0 ml-2">
              <svg
                class="w-4 h-4 text-emerald-500"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path d="M2 6a2 2 0 012-2h5l2 2h5a2 2 0 012 2v6a2 2 0 01-2 2H4a2 2 0 01-2-2V6z" />
              </svg>
            </div>
          </div>
        </div>
                
        <!-- Tags Statistics Card -->
        <div
          class="stats-card group cursor-pointer bg-gradient-to-r from-purple-50/80 to-violet-50/80 dark:from-purple-900/10 dark:to-violet-900/10 
                    border border-purple-200/50 dark:border-purple-800/30 rounded-xl p-2.5 hover:shadow-md hover:shadow-purple-500/10 
                    transform hover:scale-[1.01] transition-all duration-300 ease-out hover:border-purple-300 dark:hover:border-purple-600"
          @click="router.push('/tag/list')"
        >
          <div class="flex items-center justify-between">
            <div class="flex-1 min-w-0">
              <div class="flex items-baseline gap-1.5 mb-0.5">
                <CountTo
                  :value="statisticsInfo.tagTotalCount" 
                  custom-class="text-base sm:text-lg font-bold text-purple-600 dark:text-purple-400 group-hover:text-purple-700 dark:group-hover:text-purple-300 transition-colors"
                />
                <div class="w-1 h-1 bg-purple-500 rounded-full animate-pulse flex-shrink-0" />
              </div>
              <div class="text-[9px] sm:text-[10px] font-medium text-gray-600 dark:text-gray-400 uppercase tracking-wide truncate">
                Tags
              </div>
            </div>
            <div class="opacity-40 group-hover:opacity-70 transition-opacity flex-shrink-0 ml-2">
              <svg
                class="w-4 h-4 text-purple-500"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fill-rule="evenodd"
                  d="M17.707 9.293a1 1 0 010 1.414l-7 7a1 1 0 01-1.414 0l-7-7A.997.997 0 012 10V5a3 3 0 013-3h5c.256 0 .512.098.707.293l7 7zM5 6a1 1 0 100 2 1 1 0 000-2z"
                  clip-rule="evenodd"
                />
              </svg>
            </div>
          </div>
        </div>
                
        <!-- Total Visits Statistics Card -->
        <div
          class="stats-card bg-gradient-to-r from-amber-50/80 to-orange-50/80 dark:from-amber-900/10 dark:to-orange-900/10 
                    border border-amber-200/50 dark:border-amber-800/30 rounded-xl p-2.5 hover:shadow-md hover:shadow-amber-500/10 
                    transform hover:scale-[1.01] transition-all duration-300 ease-out hover:border-amber-300 dark:hover:border-amber-600"
        >
          <div class="flex items-center justify-between">
            <div class="flex-1 min-w-0">
              <div class="flex items-baseline gap-1.5 mb-0.5">
                <CountTo
                  :value="statisticsInfo.pvTotalCount" 
                  custom-class="text-base sm:text-lg font-bold text-amber-600 dark:text-amber-400 transition-colors"
                />
                <div class="w-1 h-1 bg-amber-500 rounded-full animate-pulse flex-shrink-0" />
              </div>
              <div class="text-[9px] sm:text-[10px] font-medium text-gray-600 dark:text-gray-400 uppercase tracking-wide truncate">
                Total Visits
              </div>
            </div>
            <div class="opacity-40 hover:opacity-70 transition-opacity flex-shrink-0 ml-2">
              <svg
                class="w-4 h-4 text-amber-500"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                <path
                  fill-rule="evenodd"
                  d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z"
                  clip-rule="evenodd"
                />
              </svg>
            </div>
          </div>
        </div>
      </div>
      <!-- Personal website links -->
      <div class="flex justify-center gap-1.5">
        <!-- Book Review -->
        <svg
          v-if="blogSettingsStore.blogSettings.githubHomepage"
          data-tooltip-target="bookreview-tooltip-bottom"
          data-tooltip-placement="bottom"
          class="icon mt-3 w-6 h-6 hover:scale-110 cursor-pointer"
          viewBox="0 0 1024 1024"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          width="200"
          height="200"
          @click="jump(blogSettingsStore.blogSettings.githubHomepage)"
        >
          <path
            d="M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z"
            fill="#8B5CF6"
          />
          <path
            d="M320 240h384c35.2 0 64 28.8 64 64v416c0 35.2-28.8 64-64 64H320c-35.2 0-64-28.8-64-64V304c0-35.2 28.8-64 64-64z"
            fill="#FFFFFF"
          />
          <path
            d="M352 320h320v64H352zM352 432h320v32H352zM352 512h320v32H352zM352 592h224v32H352z"
            fill="#8B5CF6"
          />
          <path
            d="M640 640h64v96l-32-24-32 24z"
            fill="#F59E0B"
          />
        </svg>
        <div
          id="bookreview-tooltip-bottom"
          role="tooltip"
          class="absolute z-10 invisible inline-block px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded shadow-sm opacity-0 tooltip dark:bg-gray-700"
        >
          Book Review
          <div
            class="tooltip-arrow"
            data-popper-arrow
          />
        </div>
        <!-- Purchase Tracker -->
        <svg
          v-if="blogSettingsStore.blogSettings.giteeHomepage"
          data-tooltip-target="purchasetracker-tooltip-bottom"
          data-tooltip-placement="bottom"
          class="icon mt-3 w-6 h-6 hover:scale-110 cursor-pointer"
          viewBox="0 0 1024 1024"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          width="200"
          height="200"
          @click="jump(blogSettingsStore.blogSettings.giteeHomepage)"
        >
          <path
            d="M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z"
            fill="#10B981"
          />
          <path
            d="M736 368H608l-32-80c-8-20-28-32-48-32H496c-20 0-40 12-48 32l-32 80H288c-24 0-48 24-48 48v288c0 24 24 48 48 48h448c24 0 48-24 48-48V416c0-24-24-48-48-48z"
            fill="#FFFFFF"
          />
          <path
            d="M512 448c-53 0-96 43-96 96s43 96 96 96 96-43 96-96-43-96-96-96z m0 144c-26.5 0-48-21.5-48-48s21.5-48 48-48 48 21.5 48 48-21.5 48-48 48z"
            fill="#10B981"
          />
          <path
            d="M448 512h128v32H448z"
            fill="#10B981"
          />
          <path
            d="M496 480h32v96h-32z"
            fill="#10B981"
          />
        </svg>
        <div
          id="purchasetracker-tooltip-bottom"
          role="tooltip"
          class="absolute z-10 invisible inline-block px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded shadow-sm opacity-0 tooltip dark:bg-gray-700"
        >
          Purchase Tracker
          <div
            class="tooltip-arrow"
            data-popper-arrow
          />
        </div>
        <!-- Trip Planner -->
        <svg
          v-if="blogSettingsStore.blogSettings.zhihuHomepage"
          data-tooltip-target="tripplanner-tooltip-bottom"
          data-tooltip-placement="bottom"
          class="icon mt-3 w-6 h-6 hover:scale-110 cursor-pointer"
          viewBox="0 0 1024 1024"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          width="200"
          height="200"
          @click="jump(blogSettingsStore.blogSettings.zhihuHomepage)"
        >
          <path
            d="M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z"
            fill="#0EA5E9"
          />
          <path
            d="M704 288l-128 64-64-128-64 128-128-64 32 160-160 64 160 64-32 160 128-64 64 128 64-128 128 64-32-160 160-64-160-64z"
            fill="#FFFFFF"
          />
          <path
            d="M512 416c-53 0-96 43-96 96s43 96 96 96 96-43 96-96-43-96-96-96z m0 144c-26.5 0-48-21.5-48-48s21.5-48 48-48 48 21.5 48 48-21.5 48-48 48z"
            fill="#0EA5E9"
          />
        </svg>
        <div
          id="tripplanner-tooltip-bottom"
          role="tooltip"
          class="absolute z-10 invisible inline-block px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded shadow-sm opacity-0 tooltip dark:bg-gray-700"
        >
          Trip Planner
          <div
            class="tooltip-arrow"
            data-popper-arrow
          />
        </div>
        <!-- Resume -->
        <svg
          v-if="blogSettingsStore.blogSettings.csdnHomepage"
          data-tooltip-target="resume-tooltip-bottom"
          data-tooltip-placement="bottom"
          class="icon mt-3 w-6 h-6 hover:scale-110 cursor-pointer"
          viewBox="0 0 1024 1024"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          width="200"
          height="200"
          @click="jump(blogSettingsStore.blogSettings.csdnHomepage)"
        >
          <path
            d="M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z"
            fill="#F97316"
          />
          <path
            d="M320 224h288l96 96v432c0 26.5-21.5 48-48 48H320c-26.5 0-48-21.5-48-48V272c0-26.5 21.5-48 48-48z"
            fill="#FFFFFF"
          />
          <path
            d="M608 224v96h96z"
            fill="#FDBA74"
          />
          <path
            d="M512 352c-44 0-80 36-80 80s36 80 80 80 80-36 80-80-36-80-80-80z"
            fill="#F97316"
          />
          <path
            d="M416 560h192c26.5 0 48 21.5 48 48v48H368v-48c0-26.5 21.5-48 48-48z"
            fill="#F97316"
          />
          <path
            d="M352 688h320v32H352z"
            fill="#F97316"
          />
        </svg>
        <div
          id="resume-tooltip-bottom"
          role="tooltip"
          class="absolute z-10 invisible inline-block px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded shadow-sm opacity-0 tooltip dark:bg-gray-700"
        >
          Resume
          <div
            class="tooltip-arrow"
            data-popper-arrow
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import { getStatisticsInfo } from '@/api/frontend/statistics'
import CountTo from '@/components/CountTo.vue'

// Router
const router = useRouter()

// Import blog settings store
const blogSettingsStore = useBlogSettingsStore()

// Statistics info (articles, categories, tags count, total visits)
const statisticsInfo = ref({})
// Minimize/expand state
const isMinimized = ref(false)
getStatisticsInfo().then(res => {
    if (res.success) {
        statisticsInfo.value = res.data
    }
})

const jump = (url) => {
    // Open new URL in new window
    window.open(url, '_blank');
} 
</script>

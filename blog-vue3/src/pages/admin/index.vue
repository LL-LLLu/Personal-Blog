<template>
    <main class="container p-4">
        <!-- Refresh indicator and manual refresh button -->
        <div class="flex justify-between items-center mb-4">
            <h1 class="text-xl font-bold text-gray-700 dark:text-white">Dashboard Statistics</h1>
            <div class="flex items-center space-x-3">
                <!-- Last updated time -->
                <span class="text-sm text-gray-500 dark:text-gray-400">
                    Last updated: {{ lastUpdatedTime }}
                </span>
                <!-- Manual refresh button -->
                <button 
                    @click="manualRefresh" 
                    :disabled="isRefreshing"
                    class="flex items-center px-3 py-1.5 text-sm bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
                >
                    <svg 
                        :class="['w-4 h-4 mr-1.5', { 'animate-spin': isRefreshing }]" 
                        fill="none" 
                        stroke="currentColor" 
                        viewBox="0 0 24 24"
                    >
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                    </svg>
                    {{ isRefreshing ? 'Refreshing...' : 'Refresh' }}
                </button>
            </div>
        </div>
        
        <!-- grid 表格布局，分为 4 列 -->
        <div class="grid grid-cols-4 gap-7">
            <!-- 文章数 -->
            <div class="col-span-4 md:col-span-1">
                <!-- 卡片 -->
                <div class="flex items-center w-full h-full px-5 py-7 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
                    <svg t="1699865769161" class="icon w-10 h-10" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6627" width="200" height="200"><path d="M0 0h1024v1024H0V0z" fill="#202425" opacity=".01" p-id="6628"></path><path d="M682.666667 341.333333a136.533333 136.533333 0 0 1 273.066666 0v546.133334a68.266667 68.266667 0 0 1-68.266666 68.266666h-204.8V341.333333z" fill="#FFAA44" p-id="6629"></path><path d="M68.266667 819.2a136.533333 136.533333 0 0 0 136.533333 136.533333h682.666667a68.266667 68.266667 0 0 1-68.266667-68.266666V204.8a136.533333 136.533333 0 0 0-136.533333-136.533333H204.8a136.533333 136.533333 0 0 0-136.533333 136.533333v614.4z" fill="#FF7744" p-id="6630"></path><path d="M187.733333 273.066667A51.2 51.2 0 0 1 238.933333 221.866667h68.266667a51.2 51.2 0 1 1 0 102.4H238.933333A51.2 51.2 0 0 1 187.733333 273.066667z m0 204.8A51.2 51.2 0 0 1 238.933333 426.666667h409.6a51.2 51.2 0 0 1 0 102.4H238.933333A51.2 51.2 0 0 1 187.733333 477.866667zM238.933333 631.466667a51.2 51.2 0 0 0 0 102.4h204.8a51.2 51.2 0 0 0 0-102.4H238.933333z" fill="#FFFFFF" p-id="6631"></path></svg>
                    <div class="ml-5">
                        <h2 class="mb-1 text-gray-800 dark:text-white">Articles</h2>
                        <CountTo :value="articleTotalCount" customClass="font-bold text-2xl text-gray-900 dark:text-white"></CountTo>
                    </div>
                </div>
            </div>

            <!-- 分类数 -->
            <div class="col-span-4 md:col-span-1">
                <!-- 卡片 -->
                <div
                    class="flex items-center w-full h-full px-5 py-7 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
                    <svg t="1699866300645" class="icon w-10 h-10" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="15660" width="200" height="200">
                        <path d="M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z" fill="#FF822B" p-id="15661">
                        </path>
                        <path
                            d="M324.408781 655.018925C505.290126 655.018925 651.918244 508.387706 651.918244 327.509463c0-152.138029-103.733293-280.047334-244.329811-316.853972C205.813923 52.463528 47.497011 213.017581 8.987325 415.981977 47.587706 553.880127 174.183098 655.018925 324.408781 655.018925z"
                            fill="#FFFFFF" fill-opacity=".2" p-id="15662"></path>
                        <path
                            d="M512 1024c282.766631 0 512-229.233369 512-512 0-31.765705-2.891385-62.853911-8.433853-93.018889C928.057169 336.0999 809.874701 285.26268 679.824375 285.26268c-269.711213 0-488.357305 218.645317-488.357305 488.357305 0 54.959576 9.084221 107.802937 25.822474 157.10377C300.626556 989.489417 402.283167 1024 512 1024z"
                            fill="#FFFFFF" fill-opacity=".15" p-id="15663"></path>
                        <path
                            d="M732.535958 756.566238c36.389596 0 65.889478-29.499882 65.889477-65.889478 0 36.389596 29.502983 65.889478 65.889478 65.889478-17.053747 0-65.889478 29.502983-65.889478 65.889477 0-36.386495-29.499882-65.889478-65.889477-65.889477zM159.685087 247.279334c25.686819 0 46.51022-20.8234 46.51022-46.51022 0 25.686819 20.8234 46.51022 46.510219 46.51022-12.03607 0-46.51022 20.8234-46.510219 46.510219 0-25.686819-20.8234-46.51022-46.51022-46.510219z"
                            fill="#FFFFFF" fill-opacity=".5" p-id="15664"></path>
                        <path
                            d="M206.195307 333.32324c8.562531 0 15.503407-6.940875 15.503406-15.503407 0 8.562531 6.940875 15.503407 15.503407 15.503407-4.012282 0-15.503407 6.940875-15.503407 15.503406 0-8.562531-6.940875-15.503407-15.503406-15.503406z"
                            fill="#FFFFFF" fill-opacity=".3" p-id="15665"></path>
                    <path
                        d="M536.641889 258.131718m56.587434 0l116.046099 0q56.587434 0 56.587433 56.587434l0 116.046099q0 56.587434-56.587433 56.587433l-116.046099 0q-56.587434 0-56.587434-56.587433l0-116.046099q0-56.587434 56.587434-56.587434Z"
                        fill="#FFFFFF" p-id="15666"></path>
                    <path
                        d="M536.641889 536.647316m56.587434 0l116.046099 0q56.587434 0 56.587433 56.587433l0 116.046099q0 56.587434-56.587433 56.587434l-116.046099 0q-56.587434 0-56.587434-56.587434l0-116.046099q0-56.587434 56.587434-56.587433Z"
                        fill="#FFFFFF" p-id="15667"></path>
                    <path
                        d="M258.131718 536.647316m56.587434 0l116.046099 0q56.587434 0 56.587433 56.587433l0 116.046099q0 56.587434-56.587433 56.587434l-116.046099 0q-56.587434 0-56.587434-56.587434l0-116.046099q0-56.587434 56.587434-56.587433Z"
                        fill="#FFFFFF" p-id="15668"></path>
                    <path
                        d="M258.131718 258.131718m56.587434 0l116.046099 0q56.587434 0 56.587433 56.587434l0 116.046099q0 56.587434-56.587433 56.587433l-116.046099 0q-56.587434 0-56.587434-56.587433l0-116.046099q0-56.587434 56.587434-56.587434Z"
                        fill="#FFFFFF" p-id="15669"></path>
                </svg>
                <div class="ml-5">
                    <h2 class="mb-1 text-gray-800 dark:text-white">Category</h2>
                    <CountTo :value="categoryTotalCount" customClass="font-bold text-2xl text-gray-900 dark:text-white"></CountTo>
                </div>
            </div>
        </div>

        <!-- 标签数 -->
        <div class="col-span-4 md:col-span-1">
                <!-- 卡片 -->
                <div
                    class="flex items-center w-full h-full px-5 py-7 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
                    <svg t="1699866473165" class="icon w-10 h-10" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="27760" width="200" height="200"><path d="M1023.910943 511.999898c5.11911 317.794367-194.116663 516.825376-511.91103 511.911029-317.794367 4.914346-516.825376-194.116663-511.91103-511.911029C-4.825463 194.20553 194.205546-4.825478 511.999913 0.088868c317.794367-4.914346 516.825376 194.116663 511.91103 511.91103z" fill="#FF5FB7" p-id="27761"></path><path d="M1022.477592 566.671996C1006.915497 340.40732 852.318366 201.16752 614.382119 204.85328c-254.112635-4.095288-413.624112 155.416189-409.528824 409.528824-3.890524 237.936247 135.554041 392.533378 361.613952 408.095473 268.036615-16.381153 439.629192-187.97373 456.010345-455.805581z" fill="#FF55AA" p-id="27762"></path><path d="M1015.515602 629.944199C985.824762 491.523456 876.480566 407.160519 716.764325 409.617692c-190.635668-3.071466-310.218084 116.51095-307.146618 307.146618-2.457173 159.716241 81.905765 269.060437 220.121743 298.751277 211.112109-34.400421 351.375731-174.664043 385.776152-385.571388z" fill="#FF4A9C" p-id="27763"></path><path d="M997.701098 705.707031C964.734028 646.734881 902.076118 613.153517 819.146531 614.382104c-127.1587-2.047644-206.812056 77.605712-204.764412 204.764412-1.433351 82.929587 32.352777 145.587497 91.120163 178.554567a431.233852 431.233852 0 0 0 292.198816-291.994052z" fill="#FF408E" p-id="27764"></path><path d="M273.654138 828.360914L778.193649 634.858545H245.806178v174.868808a20.476441 20.476441 0 0 0 27.84796 18.633561z" fill="#FFFFFF" p-id="27765"></path><path d="M750.345689 828.360914L245.806178 634.858545V255.634854a61.429324 61.429324 0 0 1 61.429323-61.429324h409.528824a61.429324 61.429324 0 0 1 61.429324 61.429324v552.863912a20.476441 20.476441 0 0 1-27.84796 19.862148z" fill="#FFB0E3" p-id="27766"></path><path d="M368.664825 276.111295m30.714661 0l225.240854 0q30.714662 0 30.714661 30.714662l0 0q0 30.714662-30.714661 30.714662l-225.240854 0q-30.714662 0-30.714661-30.714662l0 0q0-30.714662 30.714661-30.714662Z" fill="#FFFFFF" p-id="27767"></path></svg>
                <div class="ml-5">
                    <h2 class="mb-1 text-gray-800 dark:text-white">Tags</h2>
                    <CountTo :value="tagTotalCount" customClass="font-bold text-2xl text-gray-900 dark:text-white"></CountTo>
                </div>
            </div>
        </div>

        <!-- 总浏览量 -->
        <div class="col-span-4 md:col-span-1">
                <!-- 卡片 -->
                <div
                    class="flex items-center w-full h-full px-5 py-7 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
                    <svg t="1699866701804" class="icon w-10 h-10" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="37369" width="200" height="200"><path d="M365.3 921.9c-39.7 0-72.2-32.5-72.2-72.2v-591c0-39.7 32.5-72.2 72.2-72.2h459.5c39.7 0 72.2 32.5 72.2 72.2v590.5c0 39.7-32.5 72.2-72.2 72.2H365.3v0.5z" fill="#FFBA40" p-id="37370"></path><path d="M824.8 209c27.3 0 50.2 22.5 50.2 50.2v590.5c0 27.3-22.5 50.2-50.2 50.2H365.3c-27.3 0-50.2-22.5-50.2-50.2v-591c0-27.3 22.5-50.2 50.2-50.2h459.5m0-44.5H365.3c-52.1 0-94.7 42.6-94.7 94.7v590.5c0 52.1 42.6 94.7 94.7 94.7h459.5c52.1 0 94.7-42.6 94.7-94.7V258.7c0.4-52.1-42.1-94.7-94.7-94.7z" fill="#333333" p-id="37371"></path><path d="M365.3 839.1c-39.7 0-72.2-32.5-72.2-72.2V176c0-39.7 32.5-72.2 72.2-72.2h459.5c39.7 0 72.2 32.5 72.2 72.2v590.5c0 39.7-32.5 72.2-72.2 72.2H365.3v0.4z" fill="#FFBA40" p-id="37372"></path><path d="M824.8 126.3c27.3 0 50.2 22.5 50.2 50.2V767c0 27.3-22.5 50.2-50.2 50.2H365.3c-27.3 0-50.2-22.5-50.2-50.2V176c0-27.3 22.5-50.2 50.2-50.2h459.5m0-44.5H365.3c-52.1 0-94.7 42.6-94.7 94.7v590.5c0 52.1 42.6 94.7 94.7 94.7h459.5c52.1 0 94.7-42.6 94.7-94.7V176c0.4-52.1-42.1-94.7-94.7-94.7z" fill="#333333" p-id="37373"></path><path d="M564.7 332.8c-5.3 0-10-1.9-14.3-5.3L498.2 284l-52.1 43.5c-4.3 3.3-9.1 5.3-14.3 5.3-3.3 0-6.7-0.5-9.6-1.9-7.6-3.8-12.9-11.5-12.9-20.1V126.3c0-12.4 10-22.5 22.5-22.5h132.9c12.4 0 22.5 10 22.5 22.5v184.1c0 8.6-5.3 16.7-12.9 20.1-2.9 1.4-6.3 2.3-9.6 2.3z" fill="#27C18F" p-id="37374"></path><path d="M564.7 126.3v184.1l-66.5-55.5-66.5 55.5V126.3h133m0-45h-133c-24.9 0-44.9 20.1-44.9 44.9v184.1c0 17.2 10 33 25.8 40.6 6.2 2.9 12.4 4.3 19.1 4.3 10.5 0 20.6-3.3 28.7-10.5l37.8-31.6 37.8 31.6c8.1 6.7 18.6 10.5 28.7 10.5 6.2 0 12.9-1.4 19.1-4.3 15.8-7.2 25.8-23.4 25.8-40.6v-184c0-24.9-20.1-45-44.9-45z m110.4 725.3c-12.4 0-22.5-10-22.5-22.5V486.3c0-18.6-15.3-33.9-33.9-33.9s-33.9 15.3-33.9 33.9c0 12.4-10 22.5-22.5 22.5s-22.5-10-22.5-22.5c0-43.5 35.4-78.9 78.9-78.9s78.9 35.4 78.9 78.9v297.9c0 12.4-10.1 22.4-22.5 22.4z m-553.7 0c-12.4 0-22.5-10-22.5-22.5V486.3c0-43.5 35.4-78.9 78.9-78.9s78.9 35.4 78.9 78.9c0 12.4-10 22.5-22.5 22.5s-22.5-10-22.5-22.5c0-18.6-15.3-33.9-33.9-33.9s-33.9 15.3-33.9 33.9v297.9c0 12.4-10 22.4-22.5 22.4zM461.4 797.1c-12.4 0-22.5-10-22.5-22.5 0-22.5-18.2-40.6-40.6-40.6-22.5 0-40.6 18.2-40.6 40.6 0 12.4-10 22.5-22.5 22.5-12.4 0-22.5-10-22.5-22.5 0-47.3 38.2-85.6 85.6-85.6s85.6 38.2 85.6 85.6c0 12.4-10.1 22.5-22.5 22.5z" fill="#333333" p-id="37375"></path><path d="M225.7 921.9C155.9 921.9 99 865 99 795.2s56.9-126.7 126.7-126.7 126.7 56.9 126.7 126.7-56.9 126.7-126.7 126.7z" fill="#27C18F" p-id="37376"></path><path d="M225.7 690.9c57.4 0 104.2 46.4 104.2 104.2 0 57.4-46.4 104.2-104.2 104.2S121.4 853 121.4 795.2c0-57.4 46.9-104.3 104.3-104.3m0-44.4c-82.2 0-149.2 66.9-149.2 149.2s66.9 149.2 149.2 149.2S374.9 878 374.9 795.7s-67-149.2-149.2-149.2z" fill="#333333" p-id="37377"></path><path d="M570.9 921.9c-69.8 0-126.7-56.9-126.7-126.7s56.9-126.7 126.7-126.7 126.7 56.9 126.7 126.7-56.9 126.7-126.7 126.7z" fill="#27C18F" p-id="37378"></path><path d="M570.9 690.9c57.4 0 104.2 46.4 104.2 104.2 0 57.4-46.4 104.2-104.2 104.2-57.9 0-104.2-46.4-104.2-104.2 0.4-57.3 46.8-104.2 104.2-104.2m0-44.4c-82.2 0-149.2 66.9-149.2 149.2s66.9 149.2 149.2 149.2S720.1 878 720.1 795.7s-67-149.2-149.2-149.2z" fill="#333333" p-id="37379"></path></svg>
                <div class="ml-5">
                    <h2 class="mb-1 text-gray-800 dark:text-white">Total Visit</h2>
                    <CountTo :value="pvTotalCount" customClass="font-bold text-2xl text-gray-900 dark:text-white"></CountTo>
                </div>
            </div>
        </div>

        <!-- 文章发布热点图 -->
        <div class="col-span-4 md:col-span-2">
            <ArticlePublishCalendar :value="articlePublishInfo"></ArticlePublishCalendar>
        </div>

        <!-- 文章日 PV 访问量折线图 -->
        <div class="col-span-4 md:col-span-2">
            <ArticlePVLineChat :value="articlePVInfo"></ArticlePVLineChat>
        </div>
        </div>

    </main>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getBaseStatisticsInfo, getPublishArticleStatisticsInfo, getArticlePVStatisticsInfo } from '@/api/admin/dashboard'
import CountTo from '@/components/CountTo.vue'
import ArticlePublishCalendar from '@/components/ArticlePublishCalendar.vue'
import ArticlePVLineChat from '@/components/ArticlePVLineChat.vue'

// Article total count, default value is 0
const articleTotalCount = ref(0)
// Category total count
const categoryTotalCount = ref(0)
// Tag total count
const tagTotalCount = ref(0)
// PV total access count
const pvTotalCount = ref(0)

// Article publish statistics data by date
const articlePublishInfo = ref({})

// 近一周文章 PV 数据
const articlePVInfo = ref({})

// Refresh state
const isRefreshing = ref(false)
const lastUpdatedTime = ref(new Date().toLocaleTimeString())

// Refresh interval timer
let refreshInterval = null

// Function to fetch base statistics
const fetchBaseStatistics = () => {
    return getBaseStatisticsInfo().then(res => {
        if (res.success) {
            articleTotalCount.value = res.data.articleTotalCount
            categoryTotalCount.value = res.data.categoryTotalCount
            tagTotalCount.value = res.data.tagTotalCount
            pvTotalCount.value = res.data.pvTotalCount
        }
    })
}

// Function to fetch article publish statistics
const fetchPublishStatistics = () => {
    return getPublishArticleStatisticsInfo().then((res) => {
        if (res.success) {
            // Force update by creating a new object reference to trigger reactivity
            articlePublishInfo.value = { ...res.data }
        }
    })
}

// Function to fetch PV statistics
const fetchPVStatistics = () => {
    return getArticlePVStatisticsInfo().then((res) => {
        if (res.success) {
            // Force update by creating a new object reference
            articlePVInfo.value = { ...res.data }
        }
    })
}

// Function to refresh all statistics
const refreshAllStatistics = () => {
    fetchBaseStatistics()
    fetchPublishStatistics()
    fetchPVStatistics()
    // Update last updated time
    lastUpdatedTime.value = new Date().toLocaleTimeString()
}

// Manual refresh function
const manualRefresh = async () => {
    isRefreshing.value = true
    try {
        await Promise.all([
            fetchBaseStatistics(),
            fetchPublishStatistics(),
            fetchPVStatistics()
        ])
        lastUpdatedTime.value = new Date().toLocaleTimeString()
    } finally {
        setTimeout(() => {
            isRefreshing.value = false
        }, 500) // Small delay for better UX
    }
}

// Initial data fetch
onMounted(() => {
    // Fetch all data initially
    refreshAllStatistics()
    
    // Set up auto-refresh every 30 seconds (30000ms)
    // You can adjust this interval based on your needs
    refreshInterval = setInterval(() => {
        fetchPVStatistics() // Refresh PV statistics more frequently
        fetchBaseStatistics() // Also refresh base statistics
        lastUpdatedTime.value = new Date().toLocaleTimeString() // Update timestamp
    }, 30000) // 30 seconds
})

// Clean up interval on component unmount
onUnmounted(() => {
    if (refreshInterval) {
        clearInterval(refreshInterval)
    }
})

</script>
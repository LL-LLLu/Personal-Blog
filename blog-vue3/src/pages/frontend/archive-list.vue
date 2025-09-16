<template>
    <Header></Header>

    <!-- Main content area -->
    <main class="container max-w-screen-xl mx-auto p-4">
        <!-- Grid layout divided into 4 columns -->
        <div class="grid grid-cols-4 gap-7">
            <!-- Left main area, occupies 3 columns -->
            <div class="col-span-4 md:col-span-3 mb-3">
                <!-- Archive list -->
                <div v-for="(archive, index) in archives" :key="index" class="p-6 mb-6 border border-gray-200 rounded-xl bg-white dark:bg-gray-800 dark:border-gray-700 shadow-sm hover:shadow-md transition-all duration-200">
                    <div class="flex items-center mb-4 pb-3 border-b border-gray-100 dark:border-gray-700">
                        <div class="w-3 h-3 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full mr-3 animate-pulse"></div>
                        <time class="text-xl font-bold text-gray-900 dark:text-white tracking-wide">{{ archive.month }}</time>
                        <div class="ml-auto text-sm text-gray-500 dark:text-gray-400 font-medium">{{ archive.articles.length }} articles</div>
                    </div>
                    <ol class="space-y-2">
                        <li v-for="(article, index2) in archive.articles" :key="index2">
                            <a @click="goArticleDetailPage(article.id)" class="items-center block p-4 sm:flex hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded-lg cursor-pointer group transition-all duration-200 hover:shadow-sm">
                                <img class="w-28 h-16 mb-3 mr-4 rounded-lg sm:mb-0 object-cover group-hover:scale-105 transition-transform duration-200 shadow-sm"
                                    :src="article.cover"/>
                                <div class="flex-1">
                                    <h2 class="text-lg font-semibold text-gray-900 dark:text-white mb-2 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors line-clamp-2">
                                        {{ article.title }}
                                    </h2>
                                    <div class="flex items-center justify-between">
                                        <span class="inline-flex items-center text-sm font-medium text-gray-500 dark:text-gray-400">
                                            <svg class="inline w-4 h-4 mr-2 text-gray-400 dark:text-gray-500"
                                                aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                                                viewBox="0 0 20 20">
                                                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                                    stroke-width="2"
                                                    d="M5 1v3m5-3v3m5-3v3M1 7h18M5 11h10M2 3h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z" />
                                            </svg>
                                            {{ article.createDate }}
                                        </span>
                                        <svg class="w-5 h-5 text-gray-400 dark:text-gray-500 group-hover:text-blue-500 dark:group-hover:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                                        </svg>
                                    </div>
                                </div>
                            </a>
                        </li>
                    </ol>
                </div>

                <!-- Pagination -->
                <nav aria-label="Page navigation" class="mt-12 flex justify-center" v-if="pages > 1">
                    <div class="flex items-center space-x-1 bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-1 shadow-sm">
                        <!-- Previous page -->
                        <button @click="getArchives(current - 1)"
                            class="flex items-center justify-center w-10 h-10 rounded-lg text-gray-500 hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white transition-colors duration-200"
                            :class="[current > 1 ? 'cursor-pointer' : 'cursor-not-allowed opacity-50']"
                            :disabled="current <= 1">
                            <span class="sr-only">Previous</span>
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                            </svg>
                        </button>
                        
                        <!-- Page numbers -->
                        <button v-for="(pageNo, index) in pages" :key="index" 
                            @click="getArchives(pageNo)"
                            class="flex items-center justify-center w-10 h-10 rounded-lg font-medium transition-all duration-200"
                            :class="[
                                pageNo == current 
                                    ? 'bg-blue-600 text-white shadow-md shadow-blue-600/20' 
                                    : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer'
                            ]">
                            {{ index + 1 }}
                        </button>
                        
                        <!-- Next page -->
                        <button @click="getArchives(current + 1)"
                            class="flex items-center justify-center w-10 h-10 rounded-lg text-gray-500 hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white transition-colors duration-200"
                            :class="[current < pages ? 'cursor-pointer' : 'cursor-not-allowed opacity-50']"
                            :disabled="current >= pages">
                            <span class="sr-only">Next</span>
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                            </svg>
                        </button>
                    </div>
                </nav>

            </div>

            <!-- Right sidebar, occupies 1 column -->
            <aside class="col-span-4 md:col-span-1">
                <div class="sticky top-[5.5rem]">
                    <!-- Blogger info -->
                    <UserInfoCard></UserInfoCard>

                    <!-- Categories -->
                    <CategoryListCard></CategoryListCard>

                    <!-- Tags -->
                    <TagListCard></TagListCard>
                </div>
            </aside>
        </div>

    </main>

    <!-- Back to top button -->
    <ScrollToTopButton></ScrollToTopButton>

    <!-- Mobile User Info Popup Button -->
    <button @click="showUserInfoModal = true" 
        class="md:hidden fixed bottom-6 right-6 z-40 bg-gradient-to-r from-blue-500 to-indigo-600 
        hover:from-blue-600 hover:to-indigo-700 text-white p-4 rounded-full shadow-lg 
        hover:shadow-xl transform hover:scale-110 transition-all duration-300 ease-out"
        aria-label="Show user info">
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"></path>
        </svg>
    </button>

    <!-- Mobile User Info Modal -->
    <div v-if="showUserInfoModal" 
        class="md:hidden fixed inset-0 z-50 overflow-y-auto" 
        aria-labelledby="modal-title" 
        role="dialog" 
        aria-modal="true">
        <!-- Background overlay -->
        <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
            <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" 
                @click="showUserInfoModal = false"></div>

            <!-- Modal panel -->
            <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl 
                transform transition-all sm:my-8 sm:align-middle sm:max-w-sm sm:w-full sm:p-6 dark:bg-gray-800">
                <!-- Close button -->
                <div class="absolute top-4 right-4">
                    <button @click="showUserInfoModal = false" 
                        class="bg-white dark:bg-gray-800 rounded-full p-2 inline-flex items-center justify-center 
                        text-gray-400 hover:text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-700 
                        focus:outline-none focus:ring-2 focus:ring-inset focus:ring-indigo-500">
                        <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>
                
                <!-- User Info Content -->
                <div class="mt-4">
                    <UserInfoCard></UserInfoCard>
                </div>
            </div>
        </div>
    </div>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue'
import TagListCard from '@/layouts/frontend/components/TagListCard.vue'
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue'
import { getArchivePageList } from '@/api/frontend/archive'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Article archives
const archives = ref([])
// Current page number
const current = ref(1)
// Number of articles displayed per page
const size = ref(8)
// Total number of articles
const total = ref(0)
// Total number of pages
const pages = ref(0)
// Mobile user info modal visibility
const showUserInfoModal = ref(false)

function getArchives(currentNo) {
    // Check if prev/next page navigation is allowed: don't allow navigation when trying to go to previous page with page number < 1, or when trying to go to next page with page number > total pages
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    // Call pagination API to render data
    getArchivePageList({current: currentNo, size: size.value}).then((res) => {
        if (res.success) {
            archives.value = res.data
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
        }
    })
}
getArchives(current.value)

// Navigate to article detail page
const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId)
}
</script>

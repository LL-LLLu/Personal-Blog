<template>
    <Header></Header>

    <!-- Main content area -->
    <main class="container max-w-screen-xl mx-auto p-4">
        <!-- Grid layout with 4 columns -->
        <div class="grid grid-cols-4 gap-7">
            <!-- Left main area, spans 3 columns -->
            <div class="col-span-4 md:col-span-3 mb-3">
                <!-- Article list with 2-column grid layout -->
                <div class="grid grid-cols-2 gap-4">
                    <div v-for="(article, index) in articles" :key="index" class="col-span-2 md:col-span-1 animate__animated animate__fadeInUp">
                        <div class="bg-white hover:scale-[1.03] h-full border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700 transition-transform duration-300">
                            <!-- Article cover image -->
                            <a @click="goArticleDetailPage(article.id)" class="cursor-pointer">
                                <img class="rounded-t-lg h-48 w-full"
                                    :src="article.cover" />
                            </a>
                            <div class="p-5">
                                <!-- Tags -->
                                <div class="mb-3">
                                    <span v-for="(tag, tagIndex) in article.tags" :key="tagIndex" @click="goTagArticleListPage(tag.id, tag.name)"
                                        class="cursor-pointer bg-green-100 text-green-800 text-xs font-medium mr-2 px-2.5 py-0.5 rounded hover:bg-green-200 hover:text-green-900 dark:bg-green-900 dark:text-green-300">
                                        {{ tag.name }}
                                    </span>
                                </div>
                                <!-- Article title -->
                                <a @click="goArticleDetailPage(article.id)" class="cursor-pointer">
                                    <h2 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                                        <span class="hover:border-gray-600 hover:border-b-2 transition-all duration-200">{{ article.title }}</span>
                                    </h2>
                                </a>
                                <!-- Article summary -->
                                <p v-if="article.summary" class="mb-3 font-normal text-gray-500 dark:text-gray-400">{{ article.summary }}</p>
                                <!-- Article publish date and category -->
                                <p class="flex items-center font-normal text-gray-400 text-sm dark:text-gray-400">
                                    <!-- Publish date -->
                                    <svg class="inline w-3 h-3 mr-2 text-gray-400 dark:text-white" aria-hidden="true"
                                        xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                            stroke-width="2"
                                            d="M5 1v3m5-3v3m5-3v3M1 7h18M5 11h10M2 3h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z" />
                                    </svg>
                                    {{ article.createDate }}

                                    <!-- Category -->
                                    <svg class="inline w-3 h-3 ml-5 mr-2 text-gray-400 dark:text-white" aria-hidden="true"
                                        xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 18">
                                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                            stroke-width="2"
                                            d="M1 5v11a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1H1Zm0 0V2a1 1 0 0 1 1-1h5.443a1 1 0 0 1 .8.4l2.7 3.6H1Z" />
                                    </svg>
                                    <a @click="goCategoryArticleListPage(article.category.id, article.category.name)" class="cursor-pointer text-gray-400 hover:underline">{{ article.category.name }}</a>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Pagination -->
                <nav aria-label="Page navigation example" class="mt-10 flex justify-center" v-if="pages > 1">
                    <ul class="flex items-center -space-x-px h-10 text-base">
                        <!-- Previous page -->
                        <li>
                            <a @click="getArticles(current - 1)"
                                class="flex items-center justify-center px-4 h-10 ml-0 leading-tight text-gray-500 bg-white border border-gray-300 rounded-l-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                                :class="[current > 1 ? '' : 'cursor-not-allowed']"
                                >

                                <span class="sr-only">Prev</span>
                                <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                                    viewBox="0 0 6 10">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                        stroke-width="2" d="M5 1 1 5l4 4" />
                                </svg>
                            </a>
                        </li>
                        <!-- Page numbers -->
                        <li v-for="(pageNo, index) in pages" :key="index">
                            <a @click="getArticles(pageNo)"
                                class="flex items-center justify-center px-4 h-10 leading-tight border  dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                                :class="[pageNo == current ? 'text-gray-900 bg-gray-100 border-gray-400 hover:bg-gray-200 hover:text-gray-900 font-medium' : 'text-gray-500 border-gray-300 bg-white hover:bg-gray-100 hover:text-gray-700']"
                                >
                                {{ index + 1 }}
                            </a>
                        </li>
                        <!-- Next page -->
                        <li>
                            <a @click="getArticles(current + 1)"
                                class="flex items-center justify-center px-4 h-10 leading-tight text-gray-500 bg-white border border-gray-300 rounded-r-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                                :class="[current < pages ? '' : 'cursor-not-allowed']"
                                >
                                <span class="sr-only">Next</span>
                                <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                                    viewBox="0 0 6 10">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                        stroke-width="2" d="m1 9 4-4-4-4" />
                                </svg>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>


            <!-- Right sidebar, spans 1 column -->
            <aside class="col-span-4 md:col-span-1 animate__animated animate__fadeInUp">
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
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import TagListCard from '@/layouts/frontend/components/TagListCard.vue'
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue'
import { initTooltips } from 'flowbite'
import { onMounted, ref } from 'vue'
import { getArticlePageList } from '@/api/frontend/article'
import { useRouter } from 'vue-router'

const router = useRouter()

// Navigate to category article list page
const goCategoryArticleListPage = (id, name) => {
    // Pass parameters (category ID, category name) via query when navigating
    router.push({path: '/category/article/list', query: {id, name}})
}


// initialize components based on data attribute selectors
onMounted(() => {
    initTooltips();
})

// Articles collection
const articles = ref([])
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


function getArticles(currentNo) {
    // Check if prev/next page navigation is allowed: don't allow navigation when trying to go to previous page with page number < 1, or when trying to go to next page with page number > total pages
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    // Call pagination API to render data
    getArticlePageList({current: currentNo, size: size.value}).then((res) => {
        if (res.success) {
            articles.value = res.data
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
        }
    })
}
getArticles(current.value)

// Navigate to article detail page
const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId)
}

// Navigate to tag article list page
const goTagArticleListPage = (id, name) => {
    // Pass parameters (tag ID, tag name) via query when navigating
    router.push({path: '/tag/article/list', query: {id, name}})
}
</script>

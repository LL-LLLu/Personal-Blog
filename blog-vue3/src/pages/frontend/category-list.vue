<template>
    <Header></Header>

    <!-- Main content area -->
    <main class="container max-w-screen-xl mx-auto p-4 px-6">
        <!-- Grid layout divided into 4 columns -->
        <div class="grid grid-cols-4 gap-7">
            <!-- Left main area, occupies 3 columns -->
            <div class="col-span-4 md:col-span-3 mb-3">
                <!-- Category list -->
                <!-- <CategoryListCard></CategoryListCard> -->
                
                <div class="w-full p-5 pb-7 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
                    <!-- Category title -->
                    <h2 class="flex items-center mb-5 font-bold text-gray-900 uppercase dark:text-white">
                        <!-- Folder icon -->
                        <svg t="1698998570037" class="inline icon w-5 h-5 mr-2" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="21572" width="200" height="200">
                            <path
                                d="M938.666667 464.592593h-853.333334v-265.481482c0-62.577778 51.2-113.777778 113.777778-113.777778h128.948148c15.17037 0 28.444444 3.792593 41.718519 11.377778l98.607407 64.474074h356.503704c62.577778 0 113.777778 51.2 113.777778 113.777778v189.62963z"
                                fill="#3A69DD" p-id="21573"></path>
                            <path
                                d="M805.925926 398.222222h-587.851852v-125.155555c0-24.651852 20.859259-45.511111 45.511111-45.511111h496.82963c24.651852 0 45.511111 20.859259 45.511111 45.511111V398.222222z"
                                fill="#D9E3FF" p-id="21574"></path>
                            <path
                                d="M843.851852 417.185185h-663.703704v-98.607407c0-28.444444 22.755556-53.096296 53.096296-53.096297h559.407408c28.444444 0 53.096296 22.755556 53.096296 53.096297V417.185185z"
                                fill="#FFFFFF" p-id="21575"></path>
                            <path
                                d="M786.962963 938.666667h-549.925926c-83.437037 0-151.703704-68.266667-151.703704-151.703704V341.333333s316.681481 37.925926 430.45926 37.925926c189.62963 0 422.874074-37.925926 422.874074-37.925926v445.62963c0 83.437037-68.266667 151.703704-151.703704 151.703704z"
                                fill="#5F7CF9" p-id="21576"></path>
                            <path
                                d="M559.407407 512h-75.851851c-20.859259 0-37.925926-17.066667-37.925926-37.925926s17.066667-37.925926 37.925926-37.925926h75.851851c20.859259 0 37.925926 17.066667 37.925926 37.925926s-17.066667 37.925926-37.925926 37.925926z"
                                fill="#F9D523" p-id="21577"></path>
                        </svg>
                        Categories
                        <span v-if="categories && categories.length > 0"
                            class="ml-2 text-gray-600 font-normal dark:text-gray-300">( {{ categories.length }} )</span>
                    </h2>
                    
                    <!-- Category list -->
                    <div class="text-sm flex flex-wrap gap-3 font-medium text-gray-600 rounded-lg dark:border-gray-600 dark:text-white">
                        <a @click="goCategoryArticleListPage(category.id, category.name)"
                            v-for="(category, index) in categories" :key="index"
                            class="cursor-pointer inline-flex items-center px-4 py-2 text-sm font-medium text-center border rounded-lg 
                            hover:bg-gray-100 focus:ring-4 focus:outline-none focus:ring-gray-300 
                            dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700 dark:focus:ring-gray-800 dark:border-gray-700 dark:hover:text-white
                            transition-all duration-200">
                            {{ category.name }}
                            <span
                                class="inline-flex items-center justify-center w-4 h-4 ms-2 text-xs font-semibold text-sky-800 bg-sky-200 rounded-full">
                                {{ category.articlesTotal }}
                            </span>
                        </a>
                    </div>
                </div>
            </div>

            <!-- Right sidebar, occupies 1 column -->
            <aside class="col-span-4 md:col-span-1">
                <!-- Blogger info -->
                <UserInfoCard></UserInfoCard>

                <!-- Tags -->
                <TagListCard></TagListCard>
            </aside>
        </div>
    </main>

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
import { getCategoryList } from '@/api/frontend/category'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Navigate to category article list page
const goCategoryArticleListPage = (id, name) => {
    // Pass parameters via query when navigating (category ID, category name)
    router.push({ path: '/category/article/list', query: { id, name } })
}

// All categories
const categories = ref([])
// Mobile user info modal visibility
const showUserInfoModal = ref(false)
getCategoryList({}).then((res) => {
    if (res.success) {
        categories.value = res.data
    }
})
</script>

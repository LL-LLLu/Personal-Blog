<template>
    <Header></Header>

    <!-- Main content area -->
    <main class="container max-w-screen-xl mx-auto p-4 px-6">
        <!-- Grid layout divided into 4 columns -->
        <div class="grid grid-cols-4 gap-7">
            <!-- Left main area, occupies 3 columns -->
            <div class="col-span-4 md:col-span-3 mb-3">
                <!-- Tags -->
                <div v-if="tags && tags.length > 0"
                    class="w-full p-5 pb-7 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
                    <!-- Tag title -->
                    <h2 class="flex items-center mb-5 font-bold text-gray-900 uppercase dark:text-white">
                        <!-- Tag icon -->
                        <svg t="1698980289658" class="icon w-4 h-4 mr-2" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="13858" width="200" height="200">
                            <path
                                d="M646.4512 627.5584m-298.1888 0a298.1888 298.1888 0 1 0 596.3776 0 298.1888 298.1888 0 1 0-596.3776 0Z"
                                fill="#C7ACEF" p-id="13859"></path>
                            <path
                                d="M467.6096 962.5088c-34.4064 0-68.7616-13.1072-94.976-39.2704l-276.48-276.48c-52.3776-52.3776-52.3776-137.5744 0-189.9008L465.4592 87.552a105.216 105.216 0 0 1 76.8512-30.6176l308.6336 8.3456c55.3472 1.4848 100.096 46.0288 101.7856 101.376l9.5744 310.1696c0.8704 28.7744-10.2912 56.9344-30.6176 77.2608l-369.2032 369.2032c-26.112 26.112-60.4672 39.2192-94.8736 39.2192z m71.8848-844.1856c-11.4176 0-22.4768 4.5568-30.5664 12.6464L139.6224 500.2752c-28.416 28.416-28.416 74.6496 0 103.0144l276.48 276.48c28.416 28.416 74.6496 28.416 103.0144 0l369.2032-369.2032a43.4176 43.4176 0 0 0 12.6464-31.8976l-9.5744-310.1696c-0.7168-22.8864-19.2-41.2672-42.0352-41.8816l-308.6336-8.3456c-0.4608 0.0512-0.8192 0.0512-1.2288 0.0512z"
                                fill="#4F4F4F" p-id="13860"></path>
                            <path
                                d="M676.4032 445.5424c-62.208 0-112.8448-50.6368-112.8448-112.8448s50.6368-112.8448 112.8448-112.8448c62.208 0 112.8448 50.6368 112.8448 112.8448s-50.6368 112.8448-112.8448 112.8448z m0-164.1984c-28.3648 0-51.4048 23.04-51.4048 51.4048s23.04 51.4048 51.4048 51.4048c28.3648 0 51.4048-23.04 51.4048-51.4048s-23.0912-51.4048-51.4048-51.4048z"
                                fill="#4F4F4F" p-id="13861"></path>
                        </svg>
                        Tags
                        <span class="ml-2 text-gray-600 font-normal dark:text-gray-300">( {{ tags.length }} )</span>
                    </h2>
                    
                    <!-- Tag list -->
                    <div class="flex flex-wrap gap-3">
                        <a v-for="(tag, index) in tags" :key="index" @click="goTagArticleListPage(tag.id, tag.name)" class="cursor-pointer inline-flex items-center px-3.5 py-1.5 text-xs font-medium text-center border rounded-[12px]
            hover:bg-gray-100 focus:ring-4 focus:outline-none focus:ring-gray-300 
            dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700 dark:focus:ring-gray-800 
            dark:border-gray-700 dark:hover:text-white">
                            {{ tag.name }}
                            <span
                                class="inline-flex items-center justify-center w-4 h-4 ms-2 text-xs font-semibold text-sky-800 bg-sky-200 rounded-full">
                                {{ tag.articlesTotal }}
                            </span>
                        </a>
                    </div>

                </div>
            </div>

            <!-- Right sidebar, occupies 1 column -->
            <aside class="col-span-4 md:col-span-1">
                <!-- Blogger info -->
                <UserInfoCard></UserInfoCard>

                <!-- Categories -->
                <CategoryListCard></CategoryListCard>
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
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import { getTagList } from '@/api/frontend/tag'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// All tags
const tags = ref([])
// Mobile user info modal visibility
const showUserInfoModal = ref(false)
getTagList({}).then((res) => {
    if (res.success) {
        tags.value = res.data
    }
})

// Navigate to tag article list page
const goTagArticleListPage = (id, name) => {
    // Pass parameters via query when navigating (tag ID, tag name)
    router.push({path: '/tag/article/list', query: {id, name}})
}
</script>

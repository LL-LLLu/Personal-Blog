<template>
    <Header></Header>

    <div class="container mx-auto max-w-screen-xl mt-10 my-10 min-h-[500px]">
        <!-- Grid layout -->
        <div class="grid grid-cols-12 gap-8 items-start">
            <!-- Left sidebar: Catalog -->
            <div class="col-span-12 md:col-span-3 sticky top-[80px]">
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4 sticky top-[80px]">
                    <div class="font-bold text-lg mb-4 border-b pb-2 dark:border-gray-700 dark:text-white">Contents</div>
                    <!-- Catalog Tree -->
                    <el-tree
                        style="max-width: 600px"
                        :data="catalogs"
                        :props="defaultProps"
                        @node-click="handleNodeClick"
                        default-expand-all
                        :expand-on-click-node="false"
                        :highlight-current="true"
                    >
                         <template #default="{ node, data }">
                            <span class="custom-tree-node">
                                <span class="text-sm" :class="{'font-bold': data.level === 1}">{{ node.label }}</span>
                            </span>
                        </template>
                    </el-tree>
                </div>
            </div>

            <!-- Right content: Article Detail -->
            <div class="col-span-12 md:col-span-9 bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
                <!-- If an article is selected, show content -->
                <div v-if="article">
                     <!-- Title -->
                    <h1 class="text-3xl font-bold mb-4 dark:text-white">{{ article.title }}</h1>
                    <!-- Meta info -->
                    <div class="flex items-center text-gray-500 dark:text-gray-400 text-sm mb-6 space-x-4">
                        <span><el-icon class="mr-1"><Calendar /></el-icon> Posted on {{ article.createTime }}</span>
                        <span><el-icon class="mr-1"><View /></el-icon> {{ article.readNum }} Views</span>
                    </div>
                    
                    <!-- Content -->
                    <div class="article-content dark:text-gray-300" v-viewer>
                        <div v-html="article.content"></div>
                    </div>
                </div>
                <!-- If no article is selected (e.g., initial load or clicking a chapter), show Wiki summary/cover -->
                <div v-else class="text-center py-20">
                     <div v-if="wikiInfo">
                        <img :src="wikiInfo.cover" class="mx-auto rounded-lg shadow-md max-w-[400px] mb-8" />
                        <h2 class="text-2xl font-bold mb-4 dark:text-white">{{ wikiInfo.title }}</h2>
                        <p class="text-gray-600 dark:text-gray-400">{{ wikiInfo.summary }}</p>
                    </div>
                    <div v-else>
                        <el-empty description="Select an article from the catalog to read" />
                    </div>
                </div>
            </div>
        </div>
    </div>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getWikiCatalogs } from '@/api/admin/wiki' // Reuse the API we made
import { getArticleDetail } from '@/api/frontend/article' // Reuse frontend article API
import { getWikiDetail } from '@/api/frontend/wiki' // Need to create this
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css' // Or any other style

const route = useRoute()
const wikiId = route.params.wikiId

const catalogs = ref([])
const defaultProps = {
  children: 'children',
  label: 'title',
}

const article = ref(null)
const wikiInfo = ref(null)

// Fetch Wiki Info (Title, Cover, Summary)
const fetchWikiInfo = () => {
    // We need a public API for this, for now let's assume one exists or create it
    getWikiDetail(wikiId).then(res => {
        if(res.success) {
            wikiInfo.value = res.data
        }
    })
}

// Fetch Catalogs
const fetchCatalogs = () => {
    getWikiCatalogs(wikiId).then(res => {
        if (res.success) {
            // Transform data for el-tree if necessary, or just use it directly if it matches
            // The API returns { id, title, level, sort, children: [...] } which fits el-tree
            catalogs.value = res.data
        }
    })
}

// Handle Catalog Click
const handleNodeClick = (data) => {
    // Only fetch article if it's a Level 2 item (article) and has an articleId
    if (data.level === 2 && data.articleId) {
        getArticleDetail(data.articleId).then(res => {
            if (res.success) {
                article.value = res.data
                // Highlight code blocks
                setTimeout(() => {
                    document.querySelectorAll('pre code').forEach((el) => {
                        hljs.highlightElement(el);
                    });
                }, 100)
            }
        })
    }
}

onMounted(() => {
    fetchWikiInfo()
    fetchCatalogs()
})
</script>

<style scoped>
/* Add your article content styles here, similar to article-detail.vue */
.article-content :deep(h1) {
    font-size: 2em;
    font-weight: bold;
    margin-bottom: 1em;
}
.article-content :deep(h2) {
    font-size: 1.5em;
    font-weight: bold;
    margin-top: 1.5em;
    margin-bottom: 1em;
}
.article-content :deep(p) {
    margin-bottom: 1em;
    line-height: 1.8;
}
.article-content :deep(img) {
    max-width: 100%;
    border-radius: 8px;
    margin: 1em 0;
}
.article-content :deep(pre) {
    background-color: #f6f8fa;
    padding: 16px;
    border-radius: 8px;
    overflow-x: auto;
}
/* Dark mode styles can be added here */
.dark .el-tree {
    --el-tree-node-hover-bg-color: #374151; /* gray-700 */
    background: transparent;
    color: #e5e7eb; /* gray-200 */
}
</style>
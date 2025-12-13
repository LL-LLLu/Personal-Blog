<template>
    <div class="main min-h-screen flex flex-col">
        <WikiHeader :catalogs="catalogs"></WikiHeader>
        
        <main class="grow container max-w-screen-3xl mx-auto px-4 sm:px-6 md:px-8 py-4">
            <!-- Left sidebar: Catalog -->
            <div class="hidden lg:block fixed z-20 inset-0 top-[60px] right-auto w-[19rem] pb-10 pr-6 overflow-y-auto"
                :class="[isExpand ? 'left-[max(0px,calc(50%-45rem))] w-[20rem] pl-8' : 'left-0 w-0 pl-0 2xl:left-[max(0px,calc(50%-45rem))] 2xl:w-[19rem] 2xl:pl-8']">
                
                <div class="flex">
                    <!-- Wiki Catalog -->
                    <div class="grow" :class="[isExpand ? 'block' : 'hidden 2xl:block']">
                        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4 sticky top-[80px]">
                            <div 
                                class="font-bold text-lg mb-4 border-b pb-2 dark:border-gray-700 dark:text-white cursor-pointer hover:text-blue-500 transition-colors" 
                                @click="goWikiHome"
                            >
                                {{ wikiInfo?.title || 'Contents' }}
                            </div>
                            <!-- Catalog Tree -->
                            <el-tree
                                ref="treeRef"
                                node-key="id"
                                style="max-width: 600px"
                                :data="catalogs"
                                :props="defaultProps"
                                @node-click="handleNodeClick"
                                default-expand-all
                                :expand-on-click-node="false"
                                :highlight-current="true"
                                class="last:pb-[170px]"
                            >
                                <template #default="{ node, data }">
                                    <span class="custom-tree-node">
                                        <span class="text-sm" :class="{'font-bold': data.level === 1}">{{ node.label }}</span>
                                    </span>
                                </template>
                            </el-tree>
                        </div>
                    </div>

                    <!-- Collapse/Expand Toggle -->
                    <div class="hidden md:inline-block 2xl:hidden transition-all" @click="shrinkAndExpand">
                        <div id="left-toc-sidebar" class="left-toc-sidebar top-[60px]">
                            <span id="left-toc-sidebar-arrow"
                                class="arrow start flex items-center justify-center"
                                :class="[isExpand ? '-rotate-90' : 'rotate-90']">
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Main content: Article Detail -->
            <div class="transition-all duration-300 lg:pr-[19.5rem]" :class="[isExpand ? 'lg:pl-[20rem]' : 'lg:pl-0 2xl:pl-[20rem]']">
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-5">
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

                        <!-- Last updated -->
                        <div class="flex items-center text-gray-500 text-sm mt-5">
                            <svg class="icon inline-block w-4 h-4 mr-1" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7701" width="200" height="200"><path d="M200.405333 912.938667a90.965333 90.965333 0 0 1-89.6-93.866667v-614.4a93.866667 93.866667 0 0 1 89.6-93.866667h473.6a91.050667 91.050667 0 0 1 89.6 93.866667v183.466667a33.621333 33.621333 0 0 1-34.133333 34.133333 31.402667 31.402667 0 0 1-34.133333-34.133333v-183.466667a28.586667 28.586667 0 0 0-25.6-29.866667h-473.6c-17.066667 0-25.6 12.8-25.6 29.866667v614.4a28.373333 28.373333 0 0 0 25.6 29.866667h256a33.493333 33.493333 0 0 1 34.133333 34.133333 26.794667 26.794667 0 0 1-29.866667 29.866667z m354.133334-4.266667a47.872 47.872 0 0 1-34.133334-12.8 41.130667 41.130667 0 0 1-12.8-42.666667l12.8-102.4 234.666667-234.666666a51.584 51.584 0 0 1 68.266667 0l76.8 76.8a51.584 51.584 0 0 1 0 68.266666l-234.666667 234.666667-102.4 12.8z m29.866666-128l-8.533333 64 64-8.533333 209.066667-209.066667-55.466667-55.466667z m-341.333333-68.266667a33.664 33.664 0 0 1-34.133333-34.133333 31.36 31.36 0 0 1 34.133333-34.133333h187.733333a34.133333 34.133333 0 0 1 0 68.266666z m0-179.2a33.749333 33.749333 0 0 1-34.133333-34.133333 31.488 31.488 0 0 1 34.133333-34.133333h264.533333a34.133333 34.133333 0 0 1 0 68.266666z m0-179.2a33.536 33.536 0 0 1-34.133333-34.133333 31.488 31.488 0 0 1 34.133333-34.133333h384a34.133333 34.133333 0 1 1 0 68.266666z" p-id="7702" fill="#707070"></path></svg>
                            Last updated: {{ article.updateTime }}
                        </div>

                        <!-- Pre/Next Article -->
                        <nav class="flex flex-row mt-7" v-if="preNext">
                            <div class="basis-1/2">
                                <a v-if="preNext.preArticle" @click="goWikiArticleDetailPage(preNext.preArticle.articleId)" class="cursor-pointer flex flex-col h-full p-4 mr-3 text-base font-medium text-gray-500 bg-white border border-gray-300 rounded-lg hover:border-blue-500 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white transition-colors">
                                    <div>
                                        <svg class="inline w-3.5 h-3.5 mr-2 mb-1" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
                                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 5H1m0 0 4 4M1 5l4-4"></path>
                                        </svg>
                                        Previous
                                    </div>
                                    <div v-html="preNext.preArticle.articleTitle"></div>
                                </a>
                            </div>
                            <div class="basis-1/2">
                                <a v-if="preNext.nextArticle" @click="goWikiArticleDetailPage(preNext.nextArticle.articleId)" class="cursor-pointer flex flex-col h-full text-right p-4 text-base font-medium text-gray-500 bg-white border border-gray-300 rounded-lg hover:border-blue-500 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white transition-colors">
                                    <div>
                                        Next
                                        <svg class="inline w-3.5 h-3.5 ml-2 mb-1" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
                                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M1 5h12m0 0L9 1m4 4L9 9"></path>
                                        </svg>
                                    </div>
                                    <div v-html="preNext.nextArticle.articleTitle"></div>
                                </a>
                            </div>
                        </nav>
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

            <!-- Right sidebar: TOC -->
            <div class="fixed z-20 top-[60px] bottom-0 right-[max(0px,calc(50%-50rem))] w-[19.5rem] py-10 overflow-y-auto hidden xl:block">
                <WikiToc v-if="article" />
            </div>
        </main>

        <WikiFooter></WikiFooter>
    </div>
</template>

<script setup>
import WikiHeader from '@/layouts/frontend/components/WikiHeader.vue'
import WikiFooter from '@/layouts/frontend/components/WikiFooter.vue'
import WikiToc from '@/layouts/frontend/components/WikiToc.vue'
import { ref, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleDetail } from '@/api/frontend/article' // Reuse frontend article API
import { getWikiDetail, getWikiArticlePreNext, getWikiCatalogs } from '@/api/frontend/wiki' // Need to create this
import hljs from 'highlight.js'
import 'highlight.js/styles/tokyo-night-dark.css'
import { initTooltips, initAccordions } from 'flowbite'
import { useDark } from '@vueuse/core'

const route = useRoute()
const router = useRouter()
const wikiId = route.params.wikiId

const catalogs = ref([])
const treeRef = ref(null)
const defaultProps = {
  children: 'children',
  label: 'title',
}

const article = ref(null)
const wikiInfo = ref(null)
const preNext = ref(null)

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
            
            // Initialize Accordions after DOM update
            nextTick(() => {
                initAccordions()
            })
            
            // If URL has articleId, highlight the node
            if (route.query.articleId) {
                const catalogId = findCatalogIdByArticleId(catalogs.value, route.query.articleId)
                if (catalogId) {
                    nextTick(() => {
                        treeRef.value.setCurrentKey(catalogId)
                    })
                }
            }
        }
    })
}

// Find catalog ID by Article ID
const findCatalogIdByArticleId = (catalogs, articleId) => {
    for (const catalog of catalogs) {
        if (catalog.articleId == articleId) {
            return catalog.id
        }
        if (catalog.children && catalog.children.length > 0) {
            const id = findCatalogIdByArticleId(catalog.children, articleId)
            if (id) return id
        }
    }
    return null
}

// Navigate to Wiki Home (Overview)
const goWikiHome = () => {
    router.push({ path: '/wiki/' + wikiId })
    // Clear selection in tree
    if (treeRef.value) {
        treeRef.value.setCurrentKey(null)
    }
}

// Handle Catalog Click
const handleNodeClick = (data) => {
    // Only fetch article if it's a Level 2 item (article) and has an articleId
    if (data.level === 2 && data.articleId) {
        // Update URL, triggering watch
        router.push({ query: { ...route.query, articleId: data.articleId } })
    }
}

// Jump to article detail page (for pre/next links)
const goWikiArticleDetailPage = (articleId) => {
    router.push({ path: '/wiki/' + wikiId, query: { articleId } })
}

// Fetch Article Content
const fetchArticle = (articleId) => {
    getArticleDetail(articleId).then(res => {
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

    // Fetch Pre/Next Article
    getWikiArticlePreNext({ id: wikiId, articleId: articleId }).then(res => {
        if (res.success) {
            preNext.value = res.data
        }
    })
}

// Watch for articleId changes in URL
watch(() => route.query.articleId, (newArticleId) => {
    if (newArticleId) {
        fetchArticle(newArticleId)
    } else {
        article.value = null
        preNext.value = null
    }
})

// Sidebar expand/collapse state
const isExpand = ref(true)

// Toggle sidebar
const shrinkAndExpand = () => {
    isExpand.value = !isExpand.value
}

onMounted(() => {
    fetchWikiInfo()
    fetchCatalogs()
    // Initial load if articleId exists
    if (route.query.articleId) {
        fetchArticle(route.query.articleId)
    }
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

/* Background color set to white */
.main {
    background-color: #fff;
}

/* Dark theme background color */
.dark .main {
    background-color: #0d1117;
}

/* h1, h2, h3, h4, h5, h6 heading styles */
.article-content :deep(h1),
/* Sidebar collapse/expand styles */
.left-toc-sidebar {
    position: fixed;
    bottom: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2rem;
    transition: left .3s ease;
}

.left-toc-sidebar:hover {
    background: rgba(127, 127, 127, .05);
    cursor: pointer;
}

/* Arrow */
.left-toc-sidebar .arrow {
    display: inline-block;
    vertical-align: middle;
    width: 1em;
    height: 1em;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='rgba(0,0,0,0.5)' d='M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z'/%3E%3C/svg%3E");
    line-height: normal;
    transition: all .3s;
}

/* Arrow in dark mode */
html[class=dark] .left-toc-sidebar .arrow {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='rgba(255,255,255,0.5)' d='M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z'/%3E%3C/svg%3E");
}
</style>
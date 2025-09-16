import Index from '@/pages/frontend/index.vue'
import ArchiveList from '@/pages/frontend/archive-list.vue'
import CategoryList from '@/pages/frontend/category-list.vue'
import CategoryArticleList from '@/pages/frontend/category-article-list.vue'
import TagList from '@/pages/frontend/tag-list.vue'
import TagArticleList from '@/pages/frontend/tag-article-list.vue'
import ArticleDetail from '@/pages/frontend/article-detail.vue'
import NotFound from '@/pages/frontend/404.vue'
import Login from '@/pages/admin/login.vue'
import AdminIndex from '@/pages/admin/index.vue'
import AdminArticleList from '@/pages/admin/article-list.vue'
import AdminCategoryList from '@/pages/admin/category-list.vue'
import AdminTagList from '@/pages/admin/tag-list.vue'
import AdminBlogSettings from '@/pages/admin/blog-settings.vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import Admin from '@/layouts/admin/admin.vue'

// Define all routes here
const routes = [
    {
        path: '/', // Route path, homepage
        component: Index, // Corresponding component
        meta: { // Meta information
            title: 'Weblog Homepage' // Page title
        }
    },
    {
        path: '/archive/list', // Archive page
        component: ArchiveList,
        meta: {
            title: 'Weblog Archive Page'
        }
    },
    {
        path: '/category/list', // Category list page
        component: CategoryList,
        meta: {
            title: 'Weblog Category List Page'
        }
    },
    {
        path: '/category/article/list', // Category article page
        component: CategoryArticleList,
        meta: {
            title: 'Weblog Category Article Page'
        }
    },
    {
        path: '/tag/list', // Tag list page
        component: TagList,
        meta: {
            title: 'Weblog Tag List Page'
        }
    },
    {
        path: '/tag/article/list', // Tag article page
        component: TagArticleList,
        meta: {
            title: 'Weblog Tag Article Page'
        }
    },
    {
        path: '/article/:articleId', // Article detail page
        component: ArticleDetail,
        meta: {
            title: 'Weblog Article Detail Page'
        }
    },
    {
        path: '/login', // Login page
        component: Login,
        meta: {
            title: 'Weblog Login Page'
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFound,
        meta: {
            title: '404 Page'
        }
    },
    {
        path: "/admin", // Admin homepage
        component: Admin,
        // Routes using admin.vue layout should be placed as its children
        children: [
            {
                path: "/admin/index",
                component: AdminIndex,
                meta: {
                    title: 'Dashboard'
                }
            },
            {
                path: "/admin/article/list",
                component: AdminArticleList,
                meta: {
                    title: 'Article Management'
                }
            },
            {
                path: "/admin/category/list",
                component: AdminCategoryList,
                meta: {
                    title: 'Category Management'
                }
            },
            {
                path: "/admin/tag/list",
                component: AdminTagList,
                meta: {
                    title: 'Tag Management'
                }
            },
            {
                path: "/admin/blog/settings",
                component: AdminBlogSettings,
                meta: {
                    title: 'Blog Settings'
                }
            },
        ]
    }
]

// Create router
const router = createRouter({
    // Specify the history mode for the router, hash mode uses a hash symbol (#) in the URL
    history: createWebHashHistory(),
    // routes: routes (shorthand)
    routes, 
    // Scroll to top after each route change
    scrollBehavior() {
        return { top: 0 }
    }
})

// Export the router
export default router
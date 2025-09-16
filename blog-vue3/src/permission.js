import router from '@/router/index'
import { getToken } from '@/composables/cookie'
import { showMessage } from '@/composables/util'
import { showPageLoading, hidePageLoading } from '@/composables/util'
import { useBlogSettingsStore } from '@/stores/blogsettings'

// Global router beforeEach guard
router.beforeEach((to, from, next) => {
    console.log('==> Global router beforeEach guard')

    // Show page loading
    showPageLoading()
    
    let token = getToken()

    if (!token && to.path.startsWith('/admin')) { 
        // If the user wants to access the admin area (routes prefixed with /admin)
        // Not logged in, force redirect to login page
        showMessage('Please login first', 'warning')
        next({ path: '/login' })
    } else if (token && to.path == '/login') {
        // If the user is already logged in and tries to access the login page again
        showMessage('Please do not login repeatedly', 'warning')
        // Redirect to admin home page
        next({ path: '/admin/index' })
    } else if (!to.path.startsWith('/admin')) {
        // If accessing a route not prefixed with /admin
        // Import blog settings store
        let blogSettingsStore = useBlogSettingsStore()
        // Get blog settings and save to global state
        blogSettingsStore.getBlogSettings()
        next()
    } else {
        next()
    }
})

// Global router afterEach guard
router.afterEach((to, from) => {
    // Dynamically set page title
    let title = (to.meta.title ? to.meta.title : '') + ' - Weblog'
    document.title = title

    // Hide page loading
    hidePageLoading()
})
import '@/assets/main.css'
import 'animate.css';
import 'nprogress/nprogress.css'
// Import Element Plus dark theme CSS
import 'element-plus/theme-chalk/dark/css-vars.css'

import { createApp } from 'vue'
// Import global state management Pinia
import pinia from '@/stores'
import App from '@/App.vue'
// Import router
import router from '@/router'
// Import global route guards
import '@/permission'
// Import Element Plus icons
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// Image click zoom
import 'viewerjs/dist/viewer.css'
import VueViewer from 'v-viewer'

const app = createApp(App)

// Apply router
app.use(router)
// Apply Pinia
app.use(pinia)

// Register icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(VueViewer)

app.mount('#app')

<template>
    <!-- Outer container -->
    <el-container class="bg-gray-50 dark:bg-gray-900 min-h-screen">
    
        <!-- Left sidebar -->
        <el-aside :width='menuStore.menuWidth' class="transition-all duration-300">
            <AdminMenu></AdminMenu>
        </el-aside>
        
        <!-- Right main content area -->
        <el-container>
            <!-- Top bar container -->
            <el-header>
                <AdminHeader></AdminHeader>
            </el-header>
            
            <el-main class="bg-gray-50 dark:bg-gray-900 text-gray-800 dark:text-gray-300">
                <!-- Tab navigation bar -->
                <AdminTagList></AdminTagList>

                <!-- Main content (dynamically display different pages based on route) -->
                <router-view v-slot="{ Component }">
                    <Transition name="fade">
                        <!-- max specifies caching at most 10 components -->
                        <KeepAlive :max="10">
                            <component :is="Component"></component>
                        </KeepAlive>
                    </Transition>

                </router-view>
            </el-main>
            
            <!-- Bottom bar container -->
            <el-footer>
                <AdminFooter></AdminFooter>
            </el-footer>
        </el-container>
    </el-container>
</template>

<script setup>
// Import components
import AdminFooter from './components/AdminFooter.vue';
import AdminHeader from './components/AdminHeader.vue';
import AdminMenu from './components/AdminMenu.vue';
import AdminTagList from './components/AdminTagList.vue';

import { useMenuStore } from '@/stores/menu'
import { onMounted } from 'vue';

const menuStore = useMenuStore()

onMounted(() => {
    // Allow dark mode in admin - removed forced light mode
})
</script>

<style scoped>
.el-header {
    padding: 0!important;
}

.el-footer {
    padding: 0!important;
}

/* Content area transition animation: fade in and out effect */
/* When just starting to enter */
.fade-enter-from {
    /* Opacity */
    opacity: 0;
}

/* Just finished entering */
.fade-enter-to {
    opacity: 1;
}

/* Just starting to leave */
.fade-leave-from {
  opacity: 1;
}

/* Finished leaving */
.fade-leave-to {
  opacity: 0;
}

/* Leaving in progress */
.fade-leave-active {
    transition: all 0.3s;
}

/* Entering in progress */
.fade-enter-active {
    transition: all 0.3s;
    transition-delay: 0.3s;
}
</style>
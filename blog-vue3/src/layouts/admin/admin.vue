<template>
  <!-- Outer container: Full height, hidden overflow to prevent double scrollbars -->
  <el-container class="bg-gray-50 dark:bg-gray-900 h-screen overflow-hidden">
    <!-- Left sidebar -->
    <el-aside
      :width="menuStore.menuWidth"
      class="transition-all duration-300 h-full overflow-y-auto border-r border-gray-200 dark:border-gray-800"
    >
      <AdminMenu />
    </el-aside>
        
    <!-- Right main content area -->
    <el-container class="h-full flex flex-col">
      <!-- Top bar container: Fixed height -->
      <el-header class="flex-shrink-0">
        <AdminHeader />
      </el-header>
            
      <!-- Main content: Scrollable area -->
      <el-main class="flex-1 overflow-y-auto bg-gray-50 dark:bg-gray-900 text-gray-800 dark:text-gray-300 p-0">
        <!-- Tab navigation bar -->
        <AdminTagList />

        <!-- Content wrapper with padding -->
        <div class="p-4">
          <router-view v-slot="{ Component }">
            <Transition name="fade">
              <!-- max specifies caching at most 10 components -->
              <KeepAlive :max="10">
                <component :is="Component" />
              </KeepAlive>
            </Transition>
          </router-view>
        </div>
      </el-main>
            
      <!-- Bottom bar container -->
      <el-footer class="flex-shrink-0">
        <AdminFooter />
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
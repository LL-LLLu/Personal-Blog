<template>
    <!-- Fixed position, set to bottom-right corner using bottom-xx right-xx -->
    <div v-show="showScrollToTopBtn" @click="scrollToTop"
        class="border cursor-pointer fixed bottom-2 right-2 md:bottom-10 md:right-10 inline p-3 bg-white hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700 rounded transition-colors duration-200">
        <svg class="w-4 h-4 text-gray-500 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 10 14">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M5 13V1m0 0L1 5m4-4 4 4"></path>
        </svg>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

// Whether to show scroll to top button
const showScrollToTopBtn = ref(false)

// Add scroll listener
onMounted(() => window.addEventListener('scroll', handleScroll))

// Remove scroll listener
onBeforeUnmount(() => window.removeEventListener('scroll', handleScroll))

const handleScroll = () => {
    // Show back to top button if page scrolls more than 300px, otherwise hide
    showScrollToTopBtn.value = window.scrollY > 300
}

// Scroll to top
const scrollToTop = () => {
    window.scrollTo({
        top: 0, // Distance from top position
        behavior: 'smooth' // Smooth scroll effect
    });
}
</script>

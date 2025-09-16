<template>
    <!-- text-sm/[30px] means small text size, line height is 30px -->
    <div v-if="titles && titles.length > 0"
        class="sticky top-[5.5rem] text-sm/[30px] w-full p-5 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
        <!-- Table of contents title -->
        <h2 class="flex items-center mb-2 font-bold text-gray-900 uppercase dark:text-white">
            <!-- Table of contents icon -->
            <svg t="1699441758495" class="icon w-3.5 h-3.5 mr-2" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="4043" width="200" height="200">
                <path
                    d="M857.6 25.6a76.8 76.8 0 0 1 76.8 76.8v819.2a76.8 76.8 0 0 1-76.8 76.8H166.4a76.8 76.8 0 0 1-76.8-76.8V102.4a76.8 76.8 0 0 1 76.8-76.8h691.2z m-102.4 678.4H473.6l-2.2528 0.064a38.4 38.4 0 0 0 0 76.672L473.6 780.8h281.6l2.2528-0.064a38.4 38.4 0 0 0 0-76.672L755.2 704z m0-230.4H473.6l-2.2528 0.064a38.4 38.4 0 0 0 0 76.672L473.6 550.4h281.6l2.2528-0.064a38.4 38.4 0 0 0 0-76.672L755.2 473.6z m0-230.4H473.6l-2.2528 0.064a38.4 38.4 0 0 0 0 76.672L473.6 320h281.6l2.2528-0.064a38.4 38.4 0 0 0 0-76.672L755.2 243.2z"
                    fill="#6B57FE" p-id="4044"></path>
                <path
                    d="M281.6 691.2a51.2 51.2 0 1 1 0 102.4 51.2 51.2 0 0 1 0-102.4z m0-230.4a51.2 51.2 0 1 1 0 102.4 51.2 51.2 0 0 1 0-102.4z m0-230.4a51.2 51.2 0 1 1 0 102.4 51.2 51.2 0 0 1 0-102.4z"
                    fill="#FFBA00" p-id="4045"></path>
            </svg>
            Article Menu
        </h2>
        <div class="toc-wrapper" :class="[isDark ? 'dark' : '']">
            <ul class="toc">
                <!-- Second level headings -->
                <li v-for="(h2, index) in titles" :key="index" class="pl-5" :class="[h2.index == activeHeadingIndex ? 'active text-sky-600 border-l-2 border-sky-600 font-bold' : 'text-gray-500 font-normal']">
                    <span @click="scrollToView(h2.offsetTop)">{{ h2.text }}</span>
                    <!-- Third level headings -->
                    <ul v-if="h2.children && h2.children.length > 0">
                        <li v-for="(h3, index2) in h2.children" :key="index2" class="pl-5" :class="[h3.index == activeHeadingIndex ? 'active text-sky-600 border-l-2 border-sky-600 font-bold' : 'text-gray-500 font-normal']">
                            <span @click="scrollToView(h3.offsetTop)">{{ h3.text }}</span>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useDark } from '@vueuse/core'

// Reactive table of contents data
const titles = ref([])

// Whether it's dark mode
const isDark = useDark()
onMounted(() => {
    // Get parent div through .article-content style
    const container = document.querySelector('.article-content')

    // Use MutationObserver to monitor DOM changes
    const observer = new MutationObserver(mutationsList => {
        for (let mutation of mutationsList) {
            if (mutation.type === 'childList') {
                // Clear table of contents cache data first
                titles.value = []
                // Listen to all image loading events
                const images = container.querySelectorAll('img');
                images.forEach(img => {
                    img.addEventListener('load', () => {
                        // Recalculate title offsetTop after image loading is complete
                        initTocData(container)
                    })
                })

                // Add scroll event listener
                window.addEventListener('scroll', handleContentScroll);
            }
        }
    })

    // Configure to monitor changes in child nodes
    const config = { childList: true, subtree: true }
    // Start observing content changes in the article div
    observer.observe(container, config)
})

// Record the index of the currently selected heading
const activeHeadingIndex = ref(-1)
// Handle scroll events
function handleContentScroll() {
    // Current scroll position
    let scrollY = window.scrollY
    // Loop through table of contents
    titles.value.forEach(title => {
        // Get offset of each heading
        let offsetTop = title.offsetTop
        // If current position is greater than or equal to heading position, mark as selected and record the selected heading index
        if (scrollY >= offsetTop) {
            activeHeadingIndex.value = title.index
        }

        // Handle level 3 headings, same logic
        let children = title.children
        if (children && children.length > 0) {
            children.forEach(child => {
                let childOffsetTop = child.offsetTop
                if (scrollY >= childOffsetTop) {
                    activeHeadingIndex.value = child.index
                }
            })
        }
    })
}

// Remove scroll listener
onBeforeUnmount(() => window.removeEventListener('scroll', handleContentScroll))

// Scroll to specified position
function scrollToView(offsetTop) {
    window.scrollTo({ top: offsetTop, behavior: "smooth" });
}

// Initialize heading data
function initTocData(container) {
    // Only extract level 2 and level 3 headings
    let levels = ['h2', 'h3']
    let headings = container.querySelectorAll(levels)

    console.log(headings)

    // Store assembled table of contents heading data
    let titlesArr = []

    // Index
    let index = 1
    headings.forEach(heading => {
        // Heading level, h2 -> level 2; h3 -> level 3
        let headingLevel = parseInt(heading.tagName.substring(1))
        // Heading text
        let headingText = heading.innerText
        // Heading position (distance from top)
        let offsetTop = heading.offsetTop - 95

        if (headingLevel === 2) { // Level 2 heading
            titlesArr.push({
                index,
                level: headingLevel,
                text: headingText,
                offsetTop,
                children: [] // Child headings under level 2 heading
            })
        } else { // Level 3 heading
            // Parent heading
            let parentHeading = titlesArr[titlesArr.length - 1]
            // Set children of parent heading
            parentHeading.children.push({
                index,
                level: headingLevel,
                text: headingText,
                offsetTop
            })
        }
        // Index +1
        index++
    })

    // Set data
    titles.value = titlesArr
}
</script>

<style scoped>
/* Table of contents border line */
.toc {
    border-left: 2px solid #e5e7eb;
    position: relative;
}

/* Dark mode table of contents styles */
:deep(.dark .toc:before) {
    content: " ";
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    z-index: -1;
    width: 2px;
    background: #30363d;
}

:deep(.dark .toc li span) {
    color: #9e9e9e;
}

:deep(.dark .toc li .active) {
    color: rgb(2 132 199 / 1);
}

:deep(.dark .toc li span:hover) {
    color: rgb(2 132 199 / 1);
}
</style>

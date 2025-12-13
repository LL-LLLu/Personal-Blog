<template>
    <div class="mt-14">
        <h2 class="flex justify-center items-center mb-7 text-gray-500">All Comments<span>({{ total }})</span></h2>
        <!-- Card -->
        <div
            class="w-full px-5 py-10 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
            <!-- Comment publish form -->
            <form>
                <div class="flex gap-3">
                    <!-- Avatar -->
                    <div>
                        <svg class="w-10 h-10 text-gray-400 dark:text-gray-400" aria-hidden="true"
                            xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
                            <path
                                d="M10 0a10 10 0 1 0 10 10A10.011 10.011 0 0 0 10 0Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 13a8.949 8.949 0 0 1-4.951-1.488A3.987 3.987 0 0 1 9 13h2a3.987 3.987 0 0 1 3.951 3.512A8.949 8.949 0 0 1 10 18Z" />
                        </svg>
                    </div>
                    <!-- Name, Email, Website, Comment content -->
                    <div class="grow">
                        <!-- Name, Email, Website input fields -->
                        <div class="flex items-center gap-5 flex-row">
                            <div class="flex basis-1/3">
                                <span
                                    class="inline-flex border-e-0 items-center px-3 text-xs text-gray-900 bg-gray-100 border rounded-e-0 border-gray-300 rounded-s-md dark:bg-gray-600 dark:text-gray-400 dark:border-gray-600">
                                    Name
                                </span>
                                <input data-tooltip-target="nickname-tooltip-click" data-tooltip-trigger="click"
                                    type="text" id="nickname-input"
                                    class="rounded-none rounded-e-lg border text-gray-900 focus:ring-sky-500 focus:border-sky-500 block flex-1 min-w-0 w-full text-xs border-gray-300 p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-sky-500 dark:focus:border-sky-500"
                                    placeholder="Required">
                                <!-- Name Tooltip -->
                                <div id="nickname-tooltip-click" role="tooltip"
                                    class="absolute z-10 invisible inline-block px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded-md shadow-sm opacity-0 tooltip dark:bg-gray-700">
                                    Please enter your nickname
                                    <div class="tooltip-arrow" data-popper-arrow></div>
                                </div>
                            </div>
                            <div class="flex basis-1/3">
                                <span
                                    class="inline-flex border-e-0 items-center px-3 text-xs text-gray-900 bg-gray-100 border rounded-e-0 border-gray-300 rounded-s-md dark:bg-gray-600 dark:text-gray-400 dark:border-gray-600">
                                    Email
                                </span>
                                <input data-tooltip-target="mail-tooltip-click" data-tooltip-trigger="click" type="text"
                                    id="mail-input"
                                    class="rounded-none rounded-e-lg border text-gray-900 focus:ring-sky-500 focus:border-sky-500 block flex-1 min-w-0 w-full text-xs border-gray-300 p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-sky-500 dark:focus:border-sky-500"
                                    placeholder="Required">
                                <!-- Email Tooltip -->
                                <div id="mail-tooltip-click" role="tooltip"
                                    class="absolute z-10 invisible inline-block px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded-md shadow-sm opacity-0 tooltip dark:bg-gray-700">
                                    Replies will be sent to your email
                                    <div class="tooltip-arrow" data-popper-arrow></div>
                                </div>
                            </div>
                            <div class="flex basis-1/3">
                                <span
                                    class="inline-flex border-e-0 items-center px-3 text-xs text-gray-900 bg-gray-100 border rounded-e-0 border-gray-300 rounded-s-md dark:bg-gray-600 dark:text-gray-400 dark:border-gray-600">
                                    Website
                                </span>
                                <input data-tooltip-target="website-tooltip-click" data-tooltip-trigger="click"
                                    type="text" id="website-input"
                                    class="rounded-none rounded-e-lg border text-gray-900 focus:ring-sky-500 focus:border-sky-500 block flex-1 min-w-0 w-full text-xs border-gray-300 p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-sky-500 dark:focus:border-sky-500"
                                    placeholder="Optional">
                                <!-- Website Tooltip -->
                                <div id="website-tooltip-click" role="tooltip"
                                    class="absolute z-10 invisible inline-block px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded-md shadow-sm opacity-0 tooltip dark:bg-gray-700">
                                    Click avatar to visit your website (optional)
                                    <div class="tooltip-arrow" data-popper-arrow></div>
                                </div>
                            </div>
                        </div>

                        <!-- Comment content input -->
                        <div
                            class="w-full mt-4 mb-4 border border-gray-200 rounded-lg bg-gray-50 dark:bg-gray-700 dark:border-gray-600">
                            <div class="px-4 py-2 bg-white rounded-t-lg dark:bg-gray-800">
                                <label for="comment" class="sr-only">Your comment</label>
                                <textarea id="comment" rows="4"
                                    class="w-full px-0 text-sm text-gray-900 bg-white border-0 dark:bg-gray-800 focus:ring-0 dark:text-white dark:placeholder-gray-400"
                                    placeholder="Leave a friendly comment..." required></textarea>
                            </div>
                            <div class="flex items-center justify-between px-3 py-2 border-t dark:border-gray-600">
                                <div
                                    class="inline-flex items-center py-2.5 px-4 text-xs font-medium text-center text-white bg-sky-600 rounded-lg focus:ring-4 focus:ring-sky-200 dark:focus:ring-sky-900 hover:bg-sky-700 cursor-pointer">
                                    Send
                                </div>
                                <div class="flex ps-0 space-x-1 rtl:space-x-reverse sm:ps-2">
                                    <div type="button"
                                        class="inline-flex justify-center items-center p-2 text-gray-500 rounded cursor-pointer hover:text-gray-900 hover:bg-gray-100 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-600">
                                        <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                                            fill="none" viewBox="0 0 24 24">
                                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                                stroke-width="2"
                                                d="M15 9h0M9 9h0m12 3a9 9 0 1 1-18 0 9 9 0 0 1 18 0ZM7 13c0 1 .5 2.4 1.5 3.2a5.5 5.5 0 0 0 7 0c1-.8 1.5-2.2 1.5-3.2 0 0-2 1-5 1s-5-1-5-1Z" />
                                        </svg>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { initTooltips } from 'flowbite'

onMounted(() => {
    initTooltips()
})

// Total comments count
const total = ref(0)
</script>

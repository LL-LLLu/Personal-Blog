<template>
    <!-- Left: Tab navigation bar -->
    <div class="fixed top-[64px] h-[44px] px-2 right-0 z-50 flex items-center bg-white dark:bg-gray-900 transition-all duration-300 shadow border-b border-gray-200 dark:border-gray-800" :style="{left: menuStore.menuWidth}">
        <el-tabs v-model="activeTab" type="card" class="demo-tabs" @tab-remove="removeTab" @tab-change="tabChange" style="min-width: 10px;">
            <el-tab-pane v-for="item in tabList" :key="item.path" :label="item.title" :name="item.path" :closable="item.path != '/admin/index'">
            </el-tab-pane>
        </el-tabs>

        <!-- Right dropdown menu -->
        <span class="ml-auto flex items-center justify-center h-[32px] w-[32px]">
            <el-dropdown @command="handleCloseTab">
                <span class="el-dropdown-link text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white transition-colors">
                    <el-icon>
                        <arrow-down />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="closeOthers">Close Other</el-dropdown-item>
                        <el-dropdown-item command="closeAll">Close All</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </span>
    </div>
    <div class="h-[44px]"></div>
</template>

<script setup>
import { useTabList } from '@/composables/useTagList.js'

const { menuStore, activeTab, tabList, tabChange, removeTab, handleCloseTab } = useTabList()
</script>

<style>
.el-tabs__item {
    font-size: 12px;
    border: 1px solid #d8dce5!important;
    border-radius: 3px!important;
    color: #606266;
    transition: all 0.3s ease;
}

/* Dark mode for tab items */
.dark .el-tabs__item {
    border: 1px solid #4b5563!important;
    background-color: #374151!important;
    color: #d1d5db!important;
}

.el-tabs--card>.el-tabs__header .el-tabs__item {
    margin-left: 0.1rem!important;
    margin-right: 0.1rem!important;
}

.el-tabs__item.is-active {
    background-color: var(--el-color-primary)!important;
    color: #fff!important;
}

/* Dark mode for active tab items */
.dark .el-tabs__item.is-active {
    background-color: var(--el-color-primary)!important;
    color: #fff!important;
    border: 1px solid var(--el-color-primary)!important;
}

.el-tabs__item.is-active::before {
    content: "";
    background-color: #fff;
    display: inline-block;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    position: relative;
    margin-right: 4px;
}

.el-tabs {
    height: 32px;
}

.el-tabs__header {
    margin-bottom: 0;
}

.el-tabs--card>.el-tabs__header .el-tabs__nav {
    border: 0;
}

.el-tabs--card>.el-tabs__header .el-tabs__item {
    height: 32px;
    line-height: 32px;
    border: 0;
    background: #fff;
}

/* Dark mode for tab item backgrounds */
.dark .el-tabs--card>.el-tabs__header .el-tabs__item {
    background: #374151!important;
    color: #d1d5db!important;
}

.el-tabs--card>.el-tabs__header {
    border: 0;
}

.el-tabs__nav-prev, .el-tabs__nav-next {
    line-height: 35px;
    color: #606266;
}

/* Dark mode for navigation arrows */
.dark .el-tabs__nav-prev,
.dark .el-tabs__nav-next {
    color: #d1d5db!important;
}

/* Dark mode hover effects */
.dark .el-tabs__item:hover:not(.is-active) {
    background-color: #4b5563!important;
    color: #f3f4f6!important;
}

.is-disabled {
    cursor: not-allowed;
    color: #d1d5db;
}

/* Dark mode for disabled state */
.dark .is-disabled {
    color: #6b7280!important;
}

/* Dark mode for dropdown elements */
.dark .el-dropdown-link {
    color: #d1d5db!important;
}

.dark .el-dropdown-link:hover {
    color: #f3f4f6!important;
}

</style>
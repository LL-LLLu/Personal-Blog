<template>
    <div class="fixed overflow-y-auto bg-white dark:bg-gray-900 h-screen text-gray-800 dark:text-white menu-container transition-all duration-300 shadow-2xl border-r border-gray-200 dark:border-gray-800" :style="{ width: menuStore.menuWidth }">
        <!-- Top Logo, set height to 64px, same height as the Header on the right -->
        <div class="flex items-center justify-center h-[64px]">
            <img v-if="menuStore.menuWidth == '250px'" src="@/assets/weblog-logo.png" class="h-[60px]">
            <img v-else src="@/assets/weblog-logo-mini.png" class="h-[60px]">
        </div>

        <!-- Menu below -->
        <el-menu :default-active="defaultActive" @select="handleSelect" :collapse="isCollapse" :collapse-transition="false">
            <template v-for="(item, index) in menus" :key="index">
                <el-menu-item :index="item.path">
                    <el-icon>
                        <!-- Dynamic icon -->
                        <component :is="item.icon"></component>
                    </el-icon>
                    <span>{{ item.name }}</span>
                </el-menu-item>
            </template>
        </el-menu>

</div></template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMenuStore } from '@/stores/menu'

const menuStore = useMenuStore()

const route = useRoute()
const router = useRouter()

// Whether collapsed
const isCollapse = computed(() =>  !(menuStore.menuWidth == '250px'))

// Determine which menu is selected based on route path
const defaultActive = ref(route.path)

// Menu selection event
const handleSelect = (path) => {
    router.push(path)
}

const menus = [
    {
        'name': 'Dashboard',
        'icon': 'Monitor',
        'path': '/admin/index'
    },
    {
        'name': 'Article Management',
        'icon': 'Document',
        'path': '/admin/article/list',
    },
    {
        'name': 'Category Management',
        'icon': 'FolderOpened',
        'path': '/admin/category/list',
    },
    {
        'name': 'Tag Management',
        'icon': 'PriceTag',
        'path': '/admin/tag/list',
    },
    {
        'name': 'Blog Settings',
        'icon': 'Setting',
        'path': '/admin/blog/settings',
    },
]
</script>

<style>
.el-menu {
    background-color: rgb(30 41 59 / 1);
    border-right: 0;
}

.el-sub-menu__title {
    color: #fff;
}

.el-sub-menu__title:hover {
    background-color: #ffffff10;
}


.el-menu-item.is-active {
    background-color: #409eff10;
    color: #fff;
}

.el-menu-item.is-active:before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 2px;
    height: 100%;
    background-color: var(--el-color-primary);
}

.el-menu-item {
    color: #fff;
}

.el-menu-item:hover {
    background-color: #ffffff10;
}

</style>
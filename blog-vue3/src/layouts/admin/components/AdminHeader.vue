<template>
    <!-- Affix component, change sticky distance by setting offset attribute, default value is 0. -->
    <el-affix :offset="0">
        <!-- Set background white, height 64px, padding-right 4, border-bottom slate 100 -->
        <div class="bg-white dark:bg-gray-900 h-[64px] flex pr-4 border-b border-gray-200 dark:border-gray-800">
            <!-- Sidebar collapse/expand -->
            <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-gray-700 dark:text-gray-300"
                @click="handleMenuWidth">
                <el-icon>
                    <Fold v-if="menuStore.menuWidth == '250px'" />
                    <Expand v-else />
                </el-icon>
            </div>

            <!-- Right container -->
            <div class="ml-auto flex">
                <!-- Click to refresh page -->
                <el-tooltip class="box-item" effect="dark" content="Refresh" placement="bottom">
                    <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-gray-700 dark:text-gray-300"
                        @click="handleRefresh">
                        <el-icon>
                            <Refresh />
                        </el-icon>
                    </div>
                </el-tooltip>

                <!-- Click to go to frontend homepage -->
                <el-tooltip class="box-item" effect="dark" content="Go to Frontend" placement="bottom">
                    <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-gray-700 dark:text-gray-300"
                        @click="router.push('/')">
                        <el-icon>
                            <Monitor />
                        </el-icon>
                    </div>
                </el-tooltip>

                <!-- Dark Mode Toggle -->
                <el-tooltip class="box-item" effect="dark" :content="isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'" placement="bottom">
                    <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center"
                        @click="toggleDarkMode">
                        <button class="vt-switch vt-switch-appearance" type="button" role="switch" aria-label="Toggle dark mode">
                            <span class="vt-switch-check">
                                <span class="vt-switch-icon">
                                    <!-- Sun icon -->
                                    <svg xmlns="http://www.w3.org/2000/svg" aria-hidden="true" focusable="false"
                                        viewBox="0 0 24 24" class="vt-switch-appearance-sun"
                                        :class="[isDark ? 'hidden' : 'block']">
                                        <path d="M12,18c-3.3,0-6-2.7-6-6s2.7-6,6-6s6,2.7,6,6S15.3,18,12,18zM12,8c-2.2,0-4,1.8-4,4c0,2.2,1.8,4,4,4c2.2,0,4-1.8,4-4C16,9.8,14.2,8,12,8z"/>
                                        <path d="M12,4c-0.6,0-1-0.4-1-1V1c0-0.6,0.4-1,1-1s1,0.4,1,1v2C13,3.6,12.6,4,12,4z"/>
                                        <path d="M12,24c-0.6,0-1-0.4-1-1v-2c0-0.6,0.4-1,1-1s1,0.4,1,1v2C13,23.6,12.6,24,12,24z"/>
                                        <path d="M5.6,6.6c-0.3,0-0.5-0.1-0.7-0.3L3.5,4.9c-0.4-0.4-0.4-1,0-1.4s1-0.4,1.4,0l1.4,1.4c0.4,0.4,0.4,1,0,1.4C6.2,6.5,5.9,6.6,5.6,6.6z"/>
                                        <path d="M20.5,21.5c-0.3,0-0.5-0.1-0.7-0.3l-1.4-1.4c-0.4-0.4-0.4-1,0-1.4s1-0.4,1.4,0l1.4,1.4c0.4,0.4,0.4,1,0,1.4C21,21.4,20.7,21.5,20.5,21.5z"/>
                                        <path d="M3,13H1c-0.6,0-1-0.4-1-1s0.4-1,1-1h2c0.6,0,1,0.4,1,1S3.6,13,3,13z"/>
                                        <path d="M23,13h-2c-0.6,0-1-0.4-1-1s0.4-1,1-1h2c0.6,0,1,0.4,1,1S23.6,13,23,13z"/>
                                        <path d="M4,21.5c-0.3,0-0.5-0.1-0.7-0.3c-0.4-0.4-0.4-1,0-1.4l1.4-1.4c0.4-0.4,1-0.4,1.4,0s0.4,1,0,1.4l-1.4,1.4C4.5,21.4,4.2,21.5,4,21.5z"/>
                                        <path d="M18.4,6.6c-0.3,0-0.5-0.1-0.7-0.3c-0.4-0.4-0.4-1,0-1.4l1.4-1.4c0.4-0.4,1-0.4,1.4,0s0.4,1,0,1.4l-1.4,1.4C18.9,6.5,18.6,6.6,18.4,6.6z"/>
                                    </svg>
                                    <!-- Moon icon -->
                                    <svg xmlns="http://www.w3.org/2000/svg" aria-hidden="true" focusable="false"
                                        viewBox="0 0 24 24" class="vt-switch-appearance-moon"
                                        :class="[isDark ? 'block' : 'hidden']">
                                        <path d="M12.1,22c-0.3,0-0.6,0-0.9,0c-5.5-0.5-9.5-5.4-9-10.9c0.4-4.8,4.2-8.6,9-9c0.4,0,0.8,0.2,1,0.5c0.2,0.3,0.2,0.8-0.1,1.1c-2,2.7-1.4,6.4,1.3,8.4c2.1,1.6,5,1.6,7.1,0c0.3-0.2,0.7-0.3,1.1-0.1c0.3,0.2,0.5,0.6,0.5,1c-0.2,2.7-1.5,5.1-3.6,6.8C16.6,21.2,14.4,22,12.1,22zM9.3,4.4c-2.9,1-5,3.6-5.2,6.8c-0.4,4.4,2.8,8.3,7.2,8.7c2.1,0.2,4.2-0.4,5.8-1.8c1.1-0.9,1.9-2.1,2.4-3.4c-2.5,0.9-5.3,0.5-7.5-1.1C9.2,11.4,8.1,7.7,9.3,4.4z"/>
                                    </svg>
                                </span>
                            </span>
                        </button>
                    </div>
                </el-tooltip>

                <!-- Click for fullscreen -->
                <el-tooltip class="box-item" effect="dark" content="Fullscreen" placement="bottom">
                    <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-gray-700 dark:text-gray-300 mr-2"
                        @click="toggle">
                        <el-icon>
                            <FullScreen v-if="!isFullscreen" />
                            <Aim v-else />
                        </el-icon>
                    </div>
                </el-tooltip>

                <!-- Logged in user avatar -->
                <el-dropdown class="flex items-center justify-center" @command="handleCommand">
                    <span class="el-dropdown-link flex items-center justify-center text-gray-700 dark:text-gray-300 text-xs">
                        <!-- User Avatar -->
                        <el-avatar class="mr-2" :size="25"
                            :src="blogSettingsStore.blogSettings.avatar" />
                        {{ userStore.userInfo.username }}
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="updatePassword">Change Password</el-dropdown-item>
                            <el-dropdown-item command="logout">Log Out</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>

        <!-- Change Password -->
        <FormDialog ref="formDialogRef" title="Change Password" destroyOnClose @submit="onSubmit">
            <el-form ref="formRef" :rules="rules" :model="form">
                <el-form-item label="Username" prop="username" label-width="120px" size="large">
                    <!-- Input component -->
                    <el-input v-model="form.username" placeholder="Please Enter your Username" clearable disabled />
                </el-form-item>
                <el-form-item label="New Password" prop="password" label-width="120px" size="large">
                    <el-input type="password" v-model="form.password" placeholder="Please Enter new Password" clearable show-password />
                </el-form-item>
                <el-form-item label="Confirm new Password" prop="rePassword" label-width="120px" size="large">
                    <el-input type="password" v-model="form.rePassword" placeholder="Please Confirm new Password" clearable show-password />
                </el-form-item>
            </el-form>
        </FormDialog>
    </el-affix>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { useMenuStore } from '@/stores/menu'
import { useUserStore } from '@/stores/user'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import { useFullscreen } from '@vueuse/core'
import { useDark, useToggle } from '@vueuse/core'
import { updateAdminPassword } from '@/api/admin/user'
import { showMessage, showModel } from '@/composables/util'
import { useRouter } from 'vue-router'
import FormDialog from '@/components/FormDialog.vue'

const router = useRouter()

// isFullscreen indicates current fullscreen status; toggle for switching fullscreen mode
const { isFullscreen, toggle } = useFullscreen()

// Dark mode functionality
const isDark = useDark({
    selector: 'html',
    attribute: 'class',
    valueDark: 'dark',
    valueLight: '',
    storageKey: 'vueuse-color-scheme'
})

const toggleDarkMode = () => {
    console.log('Toggling dark mode, current state:', isDark.value)
    console.log('HTML class before toggle:', document.documentElement.className)
    isDark.value = !isDark.value
    console.log('New dark mode state:', isDark.value)
    console.log('HTML class after toggle:', document.documentElement.className)

    // Force update Element Plus classes
    if (isDark.value) {
        document.documentElement.classList.add('dark')
    } else {
        document.documentElement.classList.remove('dark')
    }
}

// Import menu Store
const menuStore = useMenuStore()
// Import user Store
const userStore = useUserStore()
// Import blog settings Store
const blogSettingsStore = useBlogSettingsStore()

// Initialize blog settings when component mounts
onMounted(() => {
    blogSettingsStore.getBlogSettings()
})

// Icon click event
const handleMenuWidth = () => {
    menuStore.handleMenuWidth()
}

// Refresh page
const handleRefresh = () => location.reload()

// Dialog display status
const formDialogRef = ref(false)

// Dropdown menu event handler
const handleCommand = (command) => {
    // Update password
    if (command == 'updatePassword') {
        // Show change password dialog
        formDialogRef.value.open()
    } else if (command == 'logout') { // Logout
        logout()
    }
}

// Logout
function logout() {
    showModel('Are you sure to log out?').then(() => {
        userStore.logout()
        showMessage('Successfully Logged Out!')
        // Navigate to login page
        router.push('/login')
    })
}

// Form reference
const formRef = ref(null)

// User password change form object
const form = reactive({
    username: userStore.userInfo.username || '',
    password: '',
    rePassword: ''
})

// Watch for changes in Pinia store values
watch(() => userStore.userInfo.username, (newValue, oldValue) => {
    // Handle the changed value here
    console.log('New Value:', newValue);
    console.log('Old Value:', oldValue);

    // You can perform any logic you need here
    // Reset the new value to the form object
    form.username = newValue
});

// Form validation rules
const rules = {
    username: [
        {
            required: true,
            message: 'Username Can not be null',
            trigger: 'blur'
        }
    ],
    password: [
        {
            required: true,
            message: 'Password can not be null',
            trigger: 'blur',
        },
    ],
    rePassword: [
        {
            required: true,
            message: 'Confirm Password is not null',
            trigger: 'blur',
        },
    ]
}

const onSubmit = () => {
    // First validate form fields
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('Verification not pass')
            return false
        }

        if (form.password != form.rePassword) { 
            showMessage('Two passwords are not the same!', 'warning')
            return
        }

        formDialogRef.value.showBtnLoading()
        // Call change password API
        updateAdminPassword(form).then((res) => {
            console.log(res)
            // Check if successful
            if (res.success == true) {
                showMessage('Password has been successfully reset, please login again!')
                // Logout
                userStore.logout()

                // Hide dialog
                formDialogRef.value.close()

                // Navigate to login page
                router.push('/login')
            } else {
                // Get error message from server
                let message = res.message
                // Show message
                showMessage(message, 'error')
            }
        }).finally(() => formDialogRef.value.closeBtnLoading())
    })
}

</script>

<style scoped>
.vt-switch {
    --vt-c-divider-light-1: rgba(60, 60, 60, .29);
    --vt-c-divider: var(--vt-c-divider-light-1);
    --vt-c-white-mute: #f1f1f1;
    --vt-c-bg-mute: var(--vt-c-white-mute);
    position: relative;
    border-radius: 11px;
    display: block;
    width: 40px;
    height: 22px;
    flex-shrink: 0;
    border: 1px solid var(--vt-c-divider);
    background-color: var(--vt-c-bg-mute);
    transition: border-color .25s, background-color .25s;
}

.dark .vt-switch {
    --vt-c-white-mute: #f1f1f1;
    background-color: #2f2f2f;
    transition: border-color .25s, background-color .25s;
}

.vt-switch-check {
    --vt-c-white: #ffffff;
    --vt-shadow-1: 0 1px 2px rgba(0, 0, 0, .04), 0 1px 2px rgba(0, 0, 0, .06);
    position: absolute;
    top: 1px;
    left: 1px;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background-color: var(--vt-c-white);
    box-shadow: var(--vt-shadow-1);
    transition: background-color .25s, transform .25s;
}

.dark .vt-switch-appearance .vt-switch-check {
    transform: translate(18px);
}

.dark .vt-switch-check {
    --vt-c-black: #1a1a1a;
    background-color: var(--vt-c-black);
}

.vt-switch-icon {
    position: relative;
    display: block;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    overflow: hidden;
}

.vt-switch-icon svg {
    position: absolute;
    top: 3px;
    left: 3px;
    width: 12px;
    height: 12px;
    fill: rgba(60, 60, 60, .7);
}

.dark .vt-switch-icon svg {
    fill: rgba(255, 255, 255, .87);
    transition: opacity .25s;
}

.dark .vt-switch-appearance-moon {
    opacity: 1;
}

.vt-switch-appearance-sun {
    opacity: 1;
}
</style>

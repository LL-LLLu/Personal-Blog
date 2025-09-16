<template>
    <!-- Using grid layout, specifying 2 columns and full screen height -->
    <div class="grid grid-cols-2 h-screen">
        <!-- Default span 2 columns, order for arrangement, md for non-mobile (PC) adaptation -->
        <div class="col-span-2 order-2 p-10 md:col-span-1 md:order-1 bg-slate-900">
            <!-- Set as flex layout, centered vertically and horizontally on screen, height 100% -->
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__bounceInLeft animate__fast">
                <h2 class="font-bold text-4xl mb-7 text-white">Weblog Login</h2>
                <p class="text-white">A front-end and back-end separated blog developed with Spring Boot + Mybatis Plus + Vue 3.2 + Vite 4.</p>
                <!-- Set image width to 1/2 of parent element -->
                <img src="@/assets/developer.png" class="w-1/2">
            </div>
        </div>
        <div class="flex flex-col col-span-2 order-1 md:col-span-1 md:order-2 bg-white dark:bg-gray-800">
            <!-- Day/Night switch, ml-auto aligns to right -->
            <label class="switch ml-auto mt-4 mr-4">
                <input type="checkbox" v-model="isLight" @click="toggleDark()">
                <span class="slider"></span>
            </label>
            
            <!-- flex-col for vertical arrangement of child elements -->
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__bounceInRight animate__fast">
                <!-- Large title, set font weight, size, bottom margin -->
                <h1 class="font-bold text-4xl mb-5 dark:text-white">Welcome Back</h1>
                <!-- Set flex layout, content centered vertically and horizontally, text color, and horizontal spacing between child contents -->
                <div class="flex items-center justify-center mb-7 text-gray-400 space-x-2 dark:text-gray-500">
                    <!-- Left horizontal line, height 1px, width 16, background color set -->
                    <span class="h-[1px] w-16 bg-gray-200 dark:bg-gray-700"></span>
                    <span>Login with username and password</span>
                    <!-- Right horizontal line -->
                    <span class="h-[1px] w-16 bg-gray-200 dark:bg-gray-700"></span>
                </div>
                <!-- Import Element Plus form component, set width to 5/6 for mobile, 2/5 for PC -->
                <el-form class="w-5/6 md:w-2/5" ref="formRef" :rules="rules" :model="form">
                    <el-form-item prop="username">
                        <!-- Input component -->
                        <el-input size="large" v-model="form.username" placeholder="Please enter username" :prefix-icon="User" clearable />
                    </el-form-item>
                    <el-form-item prop="password">
                        <!-- Password input component -->
                        <el-input size="large" type="password" v-model="form.password" placeholder="Please enter password"
                            :prefix-icon="Lock" clearable show-password />
                    </el-form-item>
                    <el-form-item>
                        <!-- Login button, width set to 100% -->
                        <el-button class="w-full mt-2" size="large" :loading="loading" type="primary" @click="onSubmit">Login</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
// Import user and lock icons from Element Plus
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/admin/user'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { showMessage} from '@/composables/util'
import { setToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'
import { useDark, useToggle } from '@vueuse/core'

// Define reactive form object
const form = reactive({
    username: '',
    password: ''
})

const userStore = useUserStore()

const router = useRouter()
// Login button loading state
const loading = ref(false)

// Form reference
const formRef = ref(null)

// Day/Night theme functionality
const isLight = ref(true)
const isDark = useDark({
  onChanged(dark) {
    // Update the DOM when theme changes
    console.log('onchange:' + dark)
    if (dark) {
        // Add class="dark" to html element
        document.documentElement.classList.add('dark');
        // Set switch value
        isLight.value = false
    } else {
        // Remove class="dark" from html element
        document.documentElement.classList.remove('dark');
        isLight.value = true
    }
  },
})
const toggleDark = useToggle(isDark)
// Form validation rules
const rules = {
    username: [
        {
            required: true,
            message: 'Username cannot be empty',
            trigger: 'blur'
        }
    ],
    password: [
        {
            required: true,
            message: 'Password cannot be empty',
            trigger: 'blur',
        },
    ]
}

const onSubmit = () => {
    console.log('Login')
    // First validate form fields
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('Form validation failed')
            return false
        }
        // Start loading
        loading.value = true

        // Call login API
        login(form.username, form.password).then((res) => {
            console.log(res)
            // Check if successful
            if (res.success == true) {
                // Show login success message
                showMessage('Login successful')

                // Store Token in Cookie
                let token = res.data.token
                setToken(token)

                // Get user info and store in global state
                userStore.setUserInfo()

                // Redirect to backend main home page
                router.push('/admin/index')
            } else {
                // Get error message returned from server
                let message = res.message
                // Show message
                showMessage(message, 'error')
            }
        })
        .finally(() => {
            // End loading
            loading.value = false
        })
    })
}

// Execute login event after pressing Enter key
function onKeyUp(e) {
    console.log(e)
    if (e.key == 'Enter') {
        onSubmit()
    }
}

// Add keyboard listener
onMounted(() => {
    console.log('Add keyboard listener')
    document.addEventListener('keyup', onKeyUp)
})

// Remove keyboard listener
onBeforeUnmount(() => {
    document.removeEventListener('keyup', onKeyUp)
})
</script>

<style scoped>
/* The switch - the box around the slider */
.switch {
  font-size: 14px;
  position: relative;
  display: inline-block;
  width: 3.5em;
  height: 2em;
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

/* The slider */
.slider {
  --background: #28096b;
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--background);
  transition: .5s;
  border-radius: 30px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 1.4em;
  width: 1.4em;
  border-radius: 50%;
  left: 10%;
  bottom: 15%;
  box-shadow: inset 8px -4px 0px 0px #fff000;
  background: var(--background);
  transition: .5s;
}

input:checked + .slider {
  background-color: #522ba7;
}

input:checked + .slider:before {
  transform: translateX(100%);
  box-shadow: inset 15px -4px 0px 15px #fff000;
}
</style>
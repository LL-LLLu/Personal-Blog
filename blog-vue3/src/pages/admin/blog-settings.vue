<template>
    <div>
        <!-- Card component, shadow="never" specifies that the card component has no shadow -->
        <el-card shadow="never">
            <el-form ref="formRef" :model="form" label-width="160px" :rules="rules">
                <el-form-item label="Blog Name" prop="name">
                    <el-input v-model="form.name" clearable />
                </el-form-item>
                <el-form-item label="Author Name" prop="author">
                    <el-input v-model="form.author" clearable />
                </el-form-item>
                <el-form-item label="Blog LOGO" prop="logo">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleLogoChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="form.logo" :src="form.logo" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="Author Avatar" prop="avatar">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleAvatarChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="Introduction" prop="introduction">
                    <el-input v-model="form.introduction" type="textarea" />
                </el-form-item>
                <!-- Enable GitHub access -->
                <el-form-item label="Enable GitHub Access">
                    <el-switch v-model="isGithubChecked" inline-prompt :active-icon="Check" :inactive-icon="Close"
                        @change="githubSwitchChange" />
                </el-form-item>
                <el-form-item label="GitHub Homepage URL" v-if="isGithubChecked">
                    <el-input v-model="form.githubHomepage" clearable placeholder="Enter GitHub homepage URL" />
                </el-form-item>

                <!-- Enable Gitee access -->
                <el-form-item label="Enable Gitee Access">
                    <el-switch v-model="isGiteeChecked" inline-prompt :active-icon="Check" :inactive-icon="Close"
                        @change="giteeSwitchChange" />
                </el-form-item>
                <el-form-item label="Gitee Homepage URL" v-if="isGiteeChecked">
                    <el-input v-model="form.giteeHomepage" clearable placeholder="Enter Gitee homepage URL" />
                </el-form-item>

                <!-- Enable Zhihu access -->
                <el-form-item label="Enable Zhihu Access">
                    <el-switch v-model="isZhihuChecked" inline-prompt :active-icon="Check" :inactive-icon="Close"
                        @change="zhihuSwitchChange" />
                </el-form-item>
                <el-form-item label="Zhihu Homepage URL" v-if="isZhihuChecked">
                    <el-input v-model="form.zhihuHomepage" clearable placeholder="Enter Zhihu homepage URL" />
                </el-form-item>

                <!-- Enable CSDN access -->
                <el-form-item label="Enable CSDN Access">
                    <el-switch v-model="isCSDNChecked" inline-prompt :active-icon="Check" :inactive-icon="Close"
                        @change="csdnSwitchChange" />
                </el-form-item>
                <el-form-item label="CSDN Homepage URL" v-if="isCSDNChecked">
                    <el-input v-model="form.csdnHomepage" clearable placeholder="Enter CSDN homepage URL" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :loading="btnLoading" @click="onSubmit">Save</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Check, Close } from '@element-plus/icons-vue'
import { getBlogSettingsDetail, updateBlogSettings } from '@/api/admin/blogsettings'
import { uploadFile } from '@/api/admin/file'
import { showMessage } from '@/composables/util'

// Whether to enable GitHub
const isGithubChecked = ref(false)
// Whether to enable Gitee
const isGiteeChecked = ref(false)
// Whether to enable Zhihu
const isZhihuChecked = ref(false)
// Whether to enable CSDN
const isCSDNChecked = ref(false)
// Whether to show save button loading state, default is false
const btnLoading = ref(false)

// Form reference
const formRef = ref(null)
// Form object
const form = reactive({
    name: '',
    author: '',
    logo: '',
    avatar: '',
    introduction: '',
    githubHomepage: '',
    giteeHomepage: '',
    zhihuHomepage: '',
    csdnHomepage: '',
})

// Form validation rules
const rules = {
    name: [{ required: true, message: 'Please enter blog name', trigger: 'blur' }],
    author: [{ required: true, message: 'Please enter author name', trigger: 'blur' }],
    logo: [{ required: true, message: 'Please upload blog LOGO', trigger: 'blur' }],
    avatar: [{ required: true, message: 'Please upload author avatar', trigger: 'blur' }],
    introduction: [{ required: true, message: 'Please enter introduction', trigger: 'blur' }],
}

// Listen to GitHub Switch change event
const githubSwitchChange = (checked) => {
    if (checked == false) {
        form.githubHomepage = ''
    }
}

// Listen to Gitee Switch change event
const giteeSwitchChange = (checked) => {
    if (checked == false) {
        form.giteeHomepage = ''
    }
}

// Listen to Zhihu Switch change event
const zhihuSwitchChange = (checked) => {
    if (checked == false) {
        form.zhihuHomepage = ''
    }
}

// Listen to CSDN Switch change event
const csdnSwitchChange = (checked) => {
    if (checked == false) {
        form.csdnHomepage = ''
    }
}

// Initialize blog settings data and render to page
function initBlogSettings() {
    getBlogSettingsDetail().then((e) => {
        if (e.success = true) {
            // Set form data
            form.name = e.data.name
            form.author = e.data.author
            form.logo = e.data.logo
            form.avatar = e.data.avatar
            form.introduction = e.data.introduction

            // Third-party platform information settings
            if (e.data.githubHomepage) {
                isGithubChecked.value = true
                form.githubHomepage = e.data.githubHomepage
            }

            if (e.data.giteeHomepage) {
                isGiteeChecked.value = true
                form.giteeHomepage = e.data.giteeHomepage
            }

            if (e.data.zhihuHomepage) {
                isZhihuChecked.value = true
                form.zhihuHomepage = e.data.zhihuHomepage
            }

            if (e.data.csdnHomepage) {
                isCSDNChecked.value = true
                form.csdnHomepage = e.data.csdnHomepage
            }
        }
    })
}
initBlogSettings()

// Upload logo image
const handleLogoChange = (file) => {
    // Form object
    let formData = new FormData()
    // Add file field and pass the file 
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // Response failed, show error message
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // Success: set logo link and show success message
        form.logo = e.data.url
        showMessage('Upload successful')
    })
}

// Upload author avatar
const handleAvatarChange = (file) => {
    // Form object
    let formData = new FormData()
    // Add file field and pass the file 
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // Response failed, show error message
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // Success: set author avatar link and show success message
        form.avatar = e.data.url
        showMessage('Upload successful')
    })
}

// Save current blog settings
const onSubmit = () => {
    // First validate form fields
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('Form validation failed')
            return false
        }

        // Show save button loading
        btnLoading.value = true
        updateBlogSettings(form).then((res) => {
            if (res.success == false) {
                // Get error message from server
                let message = res.message
                // Show error message
                showMessage(message, 'error')
                return
            }
            
            // Re-render page information
            initBlogSettings()
            showMessage('Save successful')
        }).finally(() => btnLoading.value = false) // Hide save button loading
    })
}

</script>

<style scoped>
.avatar-uploader .avatar {
    width: 100px;
    height: 100px;
    display: block;
}
</style>

<style>
/* Fix textarea :focus state border disappearing issue */
.el-textarea__inner:focus {
    outline: 0 !important;
    box-shadow: 0 0 0 1px var(--el-input-focus-border-color) inset !important;
}

.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
}
</style>
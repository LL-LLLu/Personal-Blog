<template>
  <div>
    <!-- Card component, shadow="never" specifies that the card component has no shadow -->
    <el-card shadow="never">
      <el-form
        ref="formRef"
        :model="form"
        label-width="160px"
        :rules="rules"
      >
        <el-form-item>
          <h2 class="font-bold text-base mb-1">
            Basic Settings
          </h2>
        </el-form-item>

        <el-form-item
          label="Blog Name"
          prop="name"
        >
          <el-input
            v-model="form.name"
            clearable
          />
        </el-form-item>
        <el-form-item
          label="Author Name"
          prop="author"
        >
          <el-input
            v-model="form.author"
            clearable
          />
        </el-form-item>
        <el-form-item
          label="Blog Logo"
          prop="logo"
        >
          <el-upload
            class="avatar-uploader"
            action="#"
            :on-change="handleLogoChange"
            :auto-upload="false"
            :show-file-list="false"
          >
            <img
              v-if="form.logo"
              :src="form.logo"
              class="avatar"
            >
            <el-icon
              v-else
              class="avatar-uploader-icon"
            >
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item
          label="Author Avatar"
          prop="avatar"
        >
          <el-upload
            class="avatar-uploader"
            action="#"
            :on-change="handleAvatarChange"
            :auto-upload="false"
            :show-file-list="false"
          >
            <img
              v-if="form.avatar"
              :src="form.avatar"
              class="avatar"
            >
            <el-icon
              v-else
              class="avatar-uploader-icon"
            >
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item
          label="Introduction"
          prop="introduction"
        >
          <el-input
            v-model="form.introduction"
            type="textarea"
          />
        </el-form-item>

        <!-- Divider -->
        <el-divider />

        <el-form-item>
          <h2 class="font-bold text-base mb-1">
            Third-party Platform Settings
          </h2>
        </el-form-item>

        <!-- Enable GitHub access -->
        <el-form-item label="Enable GitHub Access">
          <el-switch
            v-model="isGithubChecked"
            inline-prompt
            :active-icon="Check"
            :inactive-icon="Close"
            @change="githubSwitchChange"
          />
        </el-form-item>
        <el-form-item
          v-if="isGithubChecked"
          label="GitHub Homepage URL"
        >
          <el-input
            v-model="form.githubHomepage"
            clearable
            placeholder="Enter GitHub homepage URL"
          />
        </el-form-item>

        <!-- Enable Gitee access -->
        <el-form-item label="Enable Gitee Access">
          <el-switch
            v-model="isGiteeChecked"
            inline-prompt
            :active-icon="Check"
            :inactive-icon="Close"
            @change="giteeSwitchChange"
          />
        </el-form-item>
        <el-form-item
          v-if="isGiteeChecked"
          label="Gitee Homepage URL"
        >
          <el-input
            v-model="form.giteeHomepage"
            clearable
            placeholder="Enter Gitee homepage URL"
          />
        </el-form-item>

        <!-- Enable Zhihu access -->
        <el-form-item label="Enable Zhihu Access">
          <el-switch
            v-model="isZhihuChecked"
            inline-prompt
            :active-icon="Check"
            :inactive-icon="Close"
            @change="zhihuSwitchChange"
          />
        </el-form-item>
        <el-form-item
          v-if="isZhihuChecked"
          label="Zhihu Homepage URL"
        >
          <el-input
            v-model="form.zhihuHomepage"
            clearable
            placeholder="Enter Zhihu homepage URL"
          />
        </el-form-item>

        <!-- Enable CSDN access -->
        <el-form-item label="Enable CSDN Access">
          <el-switch
            v-model="isCSDNChecked"
            inline-prompt
            :active-icon="Check"
            :inactive-icon="Close"
            @change="csdnSwitchChange"
          />
        </el-form-item>
        <el-form-item
          v-if="isCSDNChecked"
          label="CSDN Homepage URL"
        >
          <el-input
            v-model="form.csdnHomepage"
            clearable
            placeholder="Enter CSDN homepage URL"
          />
        </el-form-item>

        <!-- Divider -->
        <el-divider />

        <el-form-item>
          <h2 class="font-bold text-base mb-1">
            Comment Settings
          </h2>
        </el-form-item>
        <el-form-item label="Sensitive Word Filter">
          <el-switch
            v-model="form.isCommentSensiWordOpen"
            inline-prompt
            :active-icon="Check"
            :inactive-icon="Close"
            @change="sensiWordSwitchChange"
          />
          <div class="flex items-center ml-3">
            <el-icon
              class="mr-2"
              color="#909399"
            >
              <InfoFilled />
            </el-icon>
            <el-text
              class="mx-1"
              type="info"
              size="small"
            >
              When enabled, the system automatically filters sensitive words in every comment
            </el-text>
          </div>
        </el-form-item>
        <el-form-item label="Enable Review">
          <el-switch
            v-model="form.isCommentExamineOpen"
            inline-prompt
            :active-icon="Check"
            :inactive-icon="Close"
            @change="examineSwitchChange"
          />
          <div class="flex items-center ml-3">
            <el-icon
              class="mr-2"
              color="#909399"
            >
              <InfoFilled />
            </el-icon>
            <el-text
              class="mx-1"
              type="info"
              size="small"
            >
              When enabled, comments need to be approved by the admin before being displayed
            </el-text>
          </div>
        </el-form-item>
        <el-form-item label="Admin Email">
          <el-input
            v-model="form.mail"
            clearable
            placeholder="Enter admin email address"
          />
          <div class="flex items-center">
            <el-icon
              class="mr-2"
              color="#909399"
            >
              <InfoFilled />
            </el-icon>
            <el-text
              class="mx-1"
              type="info"
              size="small"
            >
              Used to send email notifications to the admin when new comments are posted
            </el-text>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="btnLoading"
            @click="onSubmit"
          >
            Save
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Check, Close, InfoFilled } from '@element-plus/icons-vue'
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
    isCommentSensiWordOpen: true, // Whether to enable comment sensitive word filtering
    isCommentExamineOpen: false, // Whether to enable comment review
    mail: '' // Admin email
})

// Form validation rules
const rules = {
    name: [{ required: true, message: 'Please enter blog name', trigger: 'blur' }],
    author: [{ required: true, message: 'Please enter author name', trigger: 'blur' }],
    logo: [{ required: true, message: 'Please upload blog logo', trigger: 'blur' }],
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

// Comment sensitive word filter switch change event
const sensiWordSwitchChange = (checked) => form.isCommentSensiWordOpen = checked
// Comment review switch change event
const examineSwitchChange = (checked) => form.isCommentExamineOpen = checked

// Initialize blog settings data and render to page
function initBlogSettings() {
    getBlogSettingsDetail().then((e) => {
        if (e.success === true) {
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

            // Comment settings
            form.isCommentSensiWordOpen = e.data.isCommentSensiWordOpen
            form.isCommentExamineOpen = e.data.isCommentExamineOpen
            form.mail = e.data.mail
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

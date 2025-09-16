<template>
    <div>
        <!-- Header pagination query conditions, shadow="never" specifies that the card component has no shadow -->
        <el-card shadow="never" class="mb-5">
            <!-- Flex layout, content vertically centered -->
            <div class="flex items-center">
                <el-text>Article Title</el-text>
                <div class="ml-3 w-52 mr-5"><el-input v-model="searchArticleTitle" placeholder="Enter (fuzzy search)" /></div>

                <el-text>Create Date</el-text>
                <div class="ml-3 w-30 mr-5">
                    <!-- Date selection component (range selection) -->
                    <el-date-picker v-model="pickDate" type="daterange" range-separator="to" start-placeholder="Start date"
                        end-placeholder="End date" size="default" :shortcuts="shortcuts" @change="datepickerChange" />
                </div>

                <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">Search</el-button>
                <el-button class="ml-3" :icon="RefreshRight" @click="reset">Reset</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <!-- Write article button -->
            <div class="mb-5">
                <el-button type="primary" @click="isArticlePublishEditorShow = true">
                    <el-icon class="mr-1">
                        <EditPen />
                    </el-icon>
                    Write Article</el-button>
            </div>

            <!-- Pagination list -->
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="id" label="ID" width="50" />
                <el-table-column prop="title" label="Title" width="380" />
                <el-table-column prop="cover" label="Cover" width="180">
                    <template #default="scope">
                        <el-image style="width: 100px;" :src="scope.row.cover" />
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="Publish Time" width="180" />
                <el-table-column label="Action">
                    <template #default="scope">
                        <el-button size="small" @click="showArticleUpdateEditor(scope.row)">
                            <el-icon class="mr-1">
                                <Edit />
                            </el-icon>
                            Edit</el-button>
                        <el-button size="small" @click="goArticleDetailPage(scope.row.id)">
                            <el-icon class="mr-1">
                                <View />
                            </el-icon>
                            Preview</el-button>
                        <el-button type="danger" size="small" @click="deleteArticleSubmit(scope.row)">
                            <el-icon class="mr-1">
                                <Delete />
                            </el-icon>
                            Delete
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- Pagination -->
            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                    :small="false" :background="true" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="getTableData" />
            </div>

        </el-card>

        <!-- Write blog -->
        <el-dialog v-model="isArticlePublishEditorShow" :fullscreen="true" :show-close="false"
            :close-on-press-escape="false">
            <template #header="{ close, titleId, titleClass }">
                <!-- Affix component, fixed to the top -->
                <el-affix :offset="20" style="width: 100%;">
                    <!-- Specify flex layout, height 10, background color white -->
                    <div class="flex h-10 bg-white dark:bg-gray-900">
                        <!-- Bold font -->
                        <h4 class="font-bold dark:text-white">Write Article</h4>
                        <!-- Right-aligned -->
                        <div class="ml-auto flex">
                            <el-button @click="isArticlePublishEditorShow = false">Cancel</el-button>
                            <el-button type="primary" @click="publishArticleSubmit">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                Publish
                            </el-button>
                        </div>
                    </div>
                </el-affix>
            </template>
            <!-- label-position="top" specifies that the label element is on top -->
            <el-form :model="form" ref="publishArticleFormRef" label-position="top" size="large" :rules="rules">
                <el-form-item label="Title" prop="title">
                    <el-input v-model="form.title" autocomplete="off" size="large" maxlength="40" show-word-limit
                        clearable />
                </el-form-item>
                <el-form-item label="Content" prop="content">
                    <!-- Markdown editor -->
                    <MdEditor v-model="form.content" language="en-US" @onUploadImg="onUploadImg" editorId="publishArticleEditor"/>
                </el-form-item>
                <el-form-item label="Cover" prop="cover">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="form.cover" :src="form.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="Summary" prop="summary">
                    <!-- :rows="3" specifies that the textarea displays 3 rows by default -->
                    <el-input v-model="form.summary" :rows="3" type="textarea" placeholder="Please enter article summary" />
                </el-form-item>
                <el-form-item label="Category" prop="categoryId">
                    <el-select v-model="form.categoryId" clearable placeholder="---Please select---" size="large">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="Tags" prop="tags">
                    <span class="w-60">
                        <!-- Tag selection -->
                        <el-select v-model="form.tags" multiple filterable remote reserve-keyword placeholder="Please enter article tags"
                            remote-show-suffix allow-create default-first-option :remote-method="remoteMethod"
                            :loading="tagSelectLoading" size="large">
                            <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </span>
                </el-form-item>
            </el-form>
        </el-dialog>

        <!-- Edit blog -->
        <el-dialog v-model="isArticleUpdateEditorShow" :fullscreen="true" :show-close="false"
            :close-on-press-escape="false">
            <template #header="{ close, titleId, titleClass }">
                <!-- Affix component, fixed to the top -->
                <el-affix :offset="20" style="width: 100%;">
                    <!-- Specify flex layout, height 10, background color white -->
                    <div class="flex h-10 bg-white dark:bg-gray-900">
                        <!-- Bold font -->
                        <h4 class="font-bold dark:text-white">Edit Article</h4>
                        <!-- Right-aligned -->
                        <div class="ml-auto flex">
                            <el-button @click="isArticleUpdateEditorShow = false">Cancel</el-button>
                            <el-button type="primary" @click="updateSubmit">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                Save
                            </el-button>
                        </div>
                    </div>
                </el-affix>
            </template>
            <!-- label-position="top" specifies that the label element is on top -->
            <el-form :model="updateArticleForm" ref="updateArticleFormRef" label-position="top" size="large" :rules="rules">
                <el-form-item label="Title" prop="title">
                    <el-input v-model="updateArticleForm.title" autocomplete="off" size="large" maxlength="40"
                        show-word-limit clearable />
                </el-form-item>
                <el-form-item label="Content" prop="content">
                    <!-- Markdown editor -->
                    <MdEditor language="en-US" v-model="updateArticleForm.content" @onUploadImg="onUploadImg"
                        editorId="updateArticleEditor" />
                </el-form-item>
                <el-form-item label="Cover" prop="cover">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleUpdateCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="updateArticleForm.cover" :src="updateArticleForm.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="Summary" prop="summary">
                    <!-- :rows="3" specifies that the textarea displays 3 rows by default -->
                    <el-input v-model="updateArticleForm.summary" :rows="3" type="textarea" placeholder="Please enter article summary" />
                </el-form-item>
                <el-form-item label="Category" prop="categoryId">
                    <el-select v-model="updateArticleForm.categoryId" clearable placeholder="---Please select---" size="large">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="Tags" prop="tags">
                    <span class="w-60">
                        <!-- Tag selection -->
                        <el-select v-model="updateArticleForm.tags" multiple filterable remote reserve-keyword
                            placeholder="Please enter article tags" remote-show-suffix allow-create default-first-option
                            :remote-method="remoteMethod" :loading="tagSelectLoading" size="large">
                            <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </span>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { getArticlePageList, deleteArticle, publishArticle, getArticleDetail, updateArticle } from '@/api/admin/article'
import { uploadFile } from '@/api/admin/file'
import { getCategorySelectList } from '@/api/admin/category'
import { searchTags, getTagSelectList } from '@/api/admin/tag'
import moment from 'moment'
import { showMessage, showModel } from '@/composables/util'
import { MdEditor, en_US } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { useRouter } from 'vue-router'

const router = useRouter()

// Article title for fuzzy search
const searchArticleTitle = ref('')
// Date
const pickDate = ref('')

// Query conditions: start and end time
const startDate = reactive({})
const endDate = reactive({})

// Navigate to article detail page
const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId)
}

// Listen to date component change event and set start and end time to variables
const datepickerChange = (e) => {
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')

    console.log('Start time: ' + startDate.value + ', End time: ' + endDate.value)
}

const shortcuts = [
    {
        text: 'Last week',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        },
    },
    {
        text: 'Last month',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        },
    },
    {
        text: 'Last 3 months',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            return [start, end]
        },
    },
]

// Reset
const reset = () => {
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    searchArticleTitle.value = ''
}

// Table loading
const tableLoading = ref(false)
// Table data
const tableData = ref([])
// Current page number, default value 1
const current = ref(1)
// Total data amount, default value 0
const total = ref(0)
// Data amount displayed per page, default value 10
const size = ref(10)

// Get paginated data
function getTableData() {
    // Show table loading
    tableLoading.value = true
    // Call backend pagination interface and pass required parameters
    getArticlePageList({ current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, title: searchArticleTitle.value })
        .then((res) => {
            if (res.success == true) {
                tableData.value = res.data
                current.value = res.current
                size.value = res.size
                total.value = res.total
            }
        })
        .finally(() => tableLoading.value = false) // Hide table loading
}
getTableData()

// Event for changing the number of items displayed per page
const handleSizeChange = (chooseSize) => {
    console.log('Selected page size: ' + chooseSize)
    size.value = chooseSize
    getTableData()
}

// Delete article
const deleteArticleSubmit = (row) => {
    console.log(row)
    showModel('Are you sure you want to delete this article?').then(() => {
        deleteArticle(row.id).then((res) => {
            if (res.success == false) {
                // Get error message returned from server
                let message = res.message
                // Show error message
                showMessage(message, 'error')
                return
            }

            showMessage('Delete successful')
            // Refresh pagination data
            getTableData()
        })
    }).catch(() => {
        console.log('Cancelled')
    })
}

// Whether to show article publish dialog
const isArticlePublishEditorShow = ref(false)
// Publish article form reference
const publishArticleFormRef = ref(null)

// Form object
const form = reactive({
    id: null,
    title: '',
    content: 'Please enter content',
    cover: '',
    categoryId: null,
    tags: [],
    summary: ""
})

// Update article form object
const updateArticleForm = reactive({
    id: null,
    title: '',
    content: 'Please enter content',
    cover: '',
    categoryId: null,
    tags: [],
    summary: ""
})

// Form validation rules
const rules = {
    title: [
        { required: true, message: 'Please enter article title', trigger: 'blur' },
        { min: 1, max: 40, message: 'Article title should be between 1 and 40 characters', trigger: 'blur' },
    ],
    content: [{ required: true }],
    cover: [{ required: true }],
    categoryId: [{ required: true, message: 'Please select article category', trigger: 'blur' }],
    tags: [{ required: true, message: 'Please select article tags', trigger: 'blur' }],
}

// Upload article cover image
const handleCoverChange = (file) => {
    // Form data object
    let formData = new FormData()
    // Add file field and pass the file
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // If response fails, show error message
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // If successful, set cover link in form object and show success message
        form.cover = e.data.url
        showMessage('Upload successful')
    })
}

// Edit article: upload article cover image
const handleUpdateCoverChange = (file) => {
    // Form data object
    let formData = new FormData()
    // Add file field and pass the file
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // If response fails, show error message
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // If successful, set cover link in form object and show success message
        updateArticleForm.cover = e.data.url
        showMessage('Upload successful')
    })
}

// Editor image upload
const onUploadImg = async (files, callback) => {
    const res = await Promise.all(
        files.map((file) => {
            return new Promise((rev, rej) => {
                console.log('==> Editor starts uploading file...')
                let formData = new FormData()
                formData.append("file", file);
                uploadFile(formData).then((res) => {
                    console.log(res)
                    console.log('Access path: ' + res.data.url)
                    // Call callback function to display uploaded image
                    callback([res.data.url]);
                })
            });
        })
    );
}

// Article categories
const categories = ref([])
getCategorySelectList().then((e) => {
    console.log('Get category data')
    categories.value = e.data
})

// Tag select Loading status, default not shown
const tagSelectLoading = ref(false)
// Article tags
const tags = ref([])
// Render tag data
getTagSelectList().then(res => {
    tags.value = res.data
})

// Remote fuzzy query based on user input tag name
const remoteMethod = (query) => {
    console.log('Remote search: ' + tags.value)
    // If user's search keyword is not empty
    if (query) {
        // Show loading
        tagSelectLoading.value = true
        // Call tag fuzzy query interface
        searchTags(query).then((e) => {
            if (e.success) {
                // Set to tags variable
                tags.value = e.data
            }
        }).finally(() => tagSelectLoading.value = false) // Hide loading
    }
}

// Publish article
const publishArticleSubmit = () => {
    console.log('Submit md content: ' + form.content)
    // Validate form
    publishArticleFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        publishArticle(form).then((res) => {
            if (res.success == false) {
                // Get error message returned from server
                let message = res.message
                // Show error message
                showMessage(message, 'error')
                return
            }

            showMessage('Publish successful')
            // Hide publish article dialog
            isArticlePublishEditorShow.value = false
            // Clear form fields
            form.title = ''
            form.content = ''
            form.cover = ''
            form.summary = ''
            form.categoryId = null
            form.tags = []
            // Refresh pagination data
            getTableData()
        })
    })
}

// Whether to show edit article dialog
const isArticleUpdateEditorShow = ref(false)
// Edit article form reference
const updateArticleFormRef = ref(null)
// Edit article button click event
const showArticleUpdateEditor = (row) => {
    // Show edit article dialog
    isArticleUpdateEditorShow.value = true
    // Get article ID
    let articleId = row.id
    getArticleDetail(articleId).then((res) => {
        if (res.success) {
            // Set form data
            updateArticleForm.id = res.data.id
            updateArticleForm.title = res.data.title
            updateArticleForm.cover = res.data.cover
            updateArticleForm.content = res.data.content
            updateArticleForm.categoryId = res.data.categoryId
            updateArticleForm.tags = res.data.tagIds
            updateArticleForm.summary = res.data.summary
        }
    })
}

// Save article
const updateSubmit = () => {
    console.log('submit')
    updateArticleFormRef.value.validate((valid) => {
        // Validate form
        if (!valid) {
            return false
        }

        // Request update article interface
        updateArticle(updateArticleForm).then((res) => {
            if (res.success == false) {
                // Get error message returned from server
                let message = res.message
                // Show error message
                showMessage(message, 'error')
                return
            }

            showMessage('Save successful')
            // Hide edit dialog
            isArticleUpdateEditorShow.value = false
            // Refresh pagination data
            getTableData()
        })
    })
}

</script>

<style scoped>
/* Cover image styles */
.avatar-uploader .avatar {
    width: 200px;
    height: 100px;
    display: block;
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 200px;
    height: 100px;
    text-align: center;
}

/* Set select dropdown width */
.el-select--large {
    width: 600px;
}
</style>

<style>
.md-editor-footer {
    height: 40px;
}
</style>
<template>
    <div>
        <!-- Header pagination query conditions, shadow="never" specifies that the card component has no shadow -->
        <el-card shadow="never" class="mb-5">
            <!-- flex layout, content vertically centered -->
            <div class="flex items-center">
                <el-text>Wiki Title</el-text>
                <div class="ml-3 w-52 mr-5"><el-input v-model="searchWikiTitle" placeholder="Enter (fuzzy search)" clearable /></div>

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
            <!-- New Wiki button -->
            <div class="mb-5">
                <el-button type="primary" @click="addWikiBtnClick">
                    <el-icon class="mr-1">
                        <Plus />
                    </el-icon>
                    New Wiki</el-button>
            </div>

            <!-- Pagination list -->
            <el-table :data="tableData" border stripe v-loading="tableLoading" table-layout="auto">
                <el-table-column type="index" label="No." width="60" />
                <el-table-column prop="title" label="Title"  />
                <el-table-column prop="cover" label="Cover" >
                    <template #default="scope">
                        <el-image style="width: 100px;" :src="scope.row.cover" />
                    </template>
                </el-table-column>
                <el-table-column prop="isTop" label="Is Top" >
                    <template #default="scope">
                        <el-switch
                            @change="handleIsTopChange(scope.row)"
                            v-model="scope.row.isTop"
                            inline-prompt
                            :active-icon="Check"
                            :inactive-icon="Close"
                        />
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="Publish Time" />
                <el-table-column prop="isPublish" label="Is Publish" >
                    <template #default="scope">
                        <el-switch
                            @change="handleIsPublishChange(scope.row)"
                            v-model="scope.row.isPublish"
                            inline-prompt
                            :active-icon="Check"
                            :inactive-icon="Close"
                        />
                    </template>
                </el-table-column>
                <el-table-column fixed="right" label="Action" width="160">
                    <template #default="scope">
                        <el-tooltip class="box-item" effect="dark" content="Edit" placement="bottom">
                            <el-button size="small" @click="showEditWikiDialog(scope.row)" :icon="Edit" circle >
                            </el-button>
                        </el-tooltip>
                        
                        <el-tooltip class="box-item" effect="dark" content="Edit Catalog" placement="bottom">
                            <el-button size="small" :icon="Tickets" circle>
                            </el-button>
                        </el-tooltip>
                        
                        <el-tooltip class="box-item" effect="dark" content="Preview" placement="bottom">
                            <el-button size="small" :icon="View" circle>
                            </el-button>
                        </el-tooltip>
                            
                        <el-tooltip class="box-item" effect="dark" content="Delete" placement="bottom">
                            <el-button type="danger" size="small" @click="deleteWikiSubmit(scope.row)" :icon="Delete" circle>
                            </el-button>
                        </el-tooltip>
                        
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

        <!-- New Wiki -->
        <FormDialog ref="formDialogRef" title="New Wiki" destroyOnClose @submit="onSubmit">
            <el-form ref="formRef" :rules="rules" :model="form">
                <el-form-item label="Title" prop="title" label-width="80px" size="large">
                    <el-input v-model="form.title" placeholder="Please enter wiki title" maxlength="20" show-word-limit clearable/>
                </el-form-item>
                <el-form-item label="Cover" prop="cover" label-width="80px" size="large">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="form.cover" :src="form.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="Summary" prop="summary" label-width="80px" size="large">
                    <!-- :rows="3" specifies that the textarea displays 3 rows by default -->
                    <el-input v-model="form.summary" 
                        :rows="3" 
                        maxlength="30" show-word-limit type="textarea" placeholder="Please enter wiki summary" clearable />
                </el-form-item>
            </el-form>
        </FormDialog>

        <!-- Edit Wiki -->
        <FormDialog ref="editFormDialogRef" title="Edit Wiki" destroyOnClose @submit="onEditWikiSubmit">
            <el-form ref="editFormRef" :rules="rules" :model="editForm">
                <el-form-item label="Title" prop="title" label-width="80px" size="large">
                    <el-input v-model="editForm.title" placeholder="Please enter wiki title" maxlength="20" show-word-limit clearable/>
                </el-form-item>
                <el-form-item label="Cover" prop="cover" label-width="80px" size="large">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleUpdateCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="editForm.cover" :src="editForm.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="Summary" prop="summary" label-width="80px" size="large">
                    <!-- :rows="3" specifies that the textarea displays 3 rows by default -->
                    <el-input v-model="editForm.summary" 
                        :rows="3" 
                        maxlength="30" show-word-limit type="textarea" placeholder="Please enter wiki summary" clearable />
                </el-form-item>
            </el-form>
        </FormDialog>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, RefreshRight, Check, Close, Delete, Edit, Tickets, View, Plus } from '@element-plus/icons-vue'
import moment from 'moment'
import { getWikiPageList, addWiki, updateWikiIsTop, updateWikiIsPublish, deleteWiki, updateWiki } from '@/api/admin/wiki'
import FormDialog from '@/components/FormDialog.vue'
import { uploadFile } from '@/api/admin/file'
import { showMessage, showModel } from '@/composables/util'

// Dialog reference
const formDialogRef = ref(null)

// New Wiki button click event
const addWikiBtnClick = () => {
    formDialogRef.value.open()
}

// Form reference
const formRef = ref(null)
// Form object
const form = reactive({
    title: '',
    cover: '',
    summary: ''
})

// Form validation rules
const rules = {
    title: [
        { required: true, message: 'Please enter title', trigger: 'blur' },
        { min: 1, max: 20, message: 'Title must be between 1 and 20 characters', trigger: 'blur' },
    ],
    summary: [
        { required: true, message: 'Please enter summary', trigger: 'blur' },
        { min: 1, max: 30, message: 'Summary must be between 1 and 30 characters', trigger: 'blur' },
    ],
    cover: [{ required: true, message: 'Please upload cover', trigger: 'blur' }],
}

// Upload cover image
const handleCoverChange = (file) => {
    // Form object
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

        // Success: set cover link and show success message
        form.cover = e.data.url
        showMessage('Upload successful')
    })
}

// Update IsTop
const handleIsTopChange = (row) => {
    updateWikiIsTop({id: row.id, isTop: row.isTop}).then((res) => {
        // Re-request pagination to render list
        getTableData()

        if (res.success == false) {
            // Get error message
            let message = res.message
            // Show error
            showMessage(message, 'error')
            return
        }

        showMessage(row.isTop ? 'Pinned successfully' : "Unpinned")
    })
}

// Update IsPublish
const handleIsPublishChange = (row) => {
    updateWikiIsPublish({id: row.id, isPublish: row.isPublish}).then((res) => {
        // Re-request pagination
        getTableData()

        if (res.success == false) {
            // Get error message
            let message = res.message
            // Show error
            showMessage(message, 'error')
            return
        }

        showMessage(row.isPublish ? 'Published successfully' : "Unpublished")
    })
}

// Delete Wiki
const deleteWikiSubmit = (row) => {
    showModel('Are you sure you want to delete this wiki?').then(() => {
        deleteWiki(row.id).then((res) => {
            if (res.success == false) {
                // Get error message
                let message = res.message
                // Show error
                showMessage(message, 'error')
                return
            }

            showMessage('Deleted successfully')
            // Re-request pagination
            getTableData()
        })
    }).catch((e) => {
        console.log('Cancelled')
    })
}

// Edit Wiki Dialog Ref
const editFormDialogRef = ref(null)
// Show Edit Wiki Dialog
const showEditWikiDialog = (row) => {
    editFormDialogRef.value.open()
    editForm.id = row.id
    editForm.title = row.title
    editForm.cover = row.cover
    editForm.summary = row.summary
}

// Edit Form Ref
const editFormRef = ref(null)
// Edit Form Object
const editForm = reactive({
    id: null,
    title: '',
    cover: '',
    summary: ''
})

// Edit Wiki: Upload Cover
const handleUpdateCoverChange = (file) => {
    // Form object
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

        // Success: set cover link and show success message
        editForm.cover = e.data.url
        showMessage('Upload successful')
    })
}

// Submit Edit Form
const onEditWikiSubmit = () => {
    // Validate form fields
    editFormRef.value.validate((valid) => {
        if (!valid) {
            console.log('Form validation failed')
            return false
        }
        
        // Show loading on submit button
        editFormDialogRef.value.showBtnLoading()
        updateWiki(editForm).then((res) => {
            if (!res.success) {
                // Get error message from server
                let message = res.message
                // Show error message
                showMessage(message, 'error')
                return
            }

            showMessage('Updated successfully')
            // Clear form data
            editForm.id = null
            editForm.title = ''
            editForm.cover = ''
            editForm.summary = ''
            // Hide dialog
            editFormDialogRef.value.close()
            // Reload list data
            getTableData()
        }).finally(() => editFormDialogRef.value.closeBtnLoading()) // Hide loading
    })
}

// Submit form
const onSubmit = () => {
    // Validate form fields
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('Form validation failed')
            return false
        }
        
        // Show loading on submit button
        formDialogRef.value.showBtnLoading()
        addWiki(form).then((res) => {
            if (!res.success) {
                // Get error message from server
                let message = res.message
                // Show error message
                showMessage(message, 'error')
                return
            }

            showMessage('Added successfully')
            // Clear form data
            form.title = ''
            form.cover = ''
            form.summary = ''
            // Hide dialog
            formDialogRef.value.close()
            // Reload list data
            getTableData()
        }).finally(() => formDialogRef.value.closeBtnLoading()) // Hide loading
    })
}

// Fuzzy search wiki title
const searchWikiTitle = ref('')
// Date
const pickDate = ref('')

// Query conditions: start and end time
const startDate = reactive({})
const endDate = reactive({})

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
    searchWikiTitle.value = ''
}

// Table loading
const tableLoading = ref(false)
// Table data
const tableData = ref([])
// Current page number, default 1
const current = ref(1)
// Total data amount, default 0
const total = ref(0)
// Data amount displayed per page, default 10
const size = ref(10)


// Get paginated data
function getTableData() {
    // Show table loading
    tableLoading.value = true
    // Call backend pagination interface and pass required parameters
    getWikiPageList({ current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, title: searchWikiTitle.value })
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

// Page size change event
const handleSizeChange = (chooseSize) => {
    console.log('Selected page size: ' + chooseSize)
    size.value = chooseSize
    getTableData()
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
</style>
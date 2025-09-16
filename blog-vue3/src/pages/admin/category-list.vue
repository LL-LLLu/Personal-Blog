<template>
    <div>
<!-- Header pagination query conditions, shadow="never" specifies that the card component has no shadow -->
<el-card shadow="never" class="mb-5">
            <!-- Flex layout, content vertically centered, with responsive column layout -->
            <div class="flex flex-col md:flex-row items-start md:items-center space-y-4 md:space-y-0 md:space-x-4">
                <div class="w-full md:w-auto flex flex-col md:flex-row items-start md:items-center space-y-2 md:space-y-0 md:space-x-2">
                    <el-text class="whitespace-nowrap">Category Name</el-text>
                    <el-input v-model="searchCategoryName" placeholder="Please enter (fuzzy query)" class="w-full md:w-52" />
                </div>

                <div class="w-full md:w-auto flex flex-col md:flex-row items-start md:items-center space-y-2 md:space-y-0 md:space-x-2">
                    <el-text class="whitespace-nowrap">Creation Date</el-text>
                    <!-- Date selection component (range selection) -->
                    <el-date-picker 
                        v-model="pickDate" 
                        type="daterange" 
                        range-separator="to" 
                        start-placeholder="Start Time"
                        end-placeholder="End Time" 
                        size="default" 
                        :shortcuts="shortcuts" 
                        @change="datepickerChange"
                        class="w-full md:w-auto"
                    />
                </div>

                <div class="w-full md:w-auto flex space-x-2">
                    <el-button type="primary" :icon="Search" @click="getTableData" class="flex-grow md:flex-grow-0">Search</el-button>
                    <el-button :icon="RefreshRight" @click="reset" class="flex-grow md:flex-grow-0">Reset</el-button>
                </div>
            </div>
        </el-card>

        <el-card shadow="never">
            <!-- Add button -->
            <div class="mb-5">
                <el-button type="primary" @click="addCategoryBtnClick">
                    <el-icon class="mr-1">
                        <Plus />
                    </el-icon>
                    Add</el-button>
            </div>

            <!-- Pagination list -->
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="name" label="Category Name" width="180" />
                <el-table-column prop="articlesTotal" label="Article Count" width="120" align="center">
                    <template #default="scope">
                        <el-tag type="info" size="small">{{ scope.row.articlesTotal || 0 }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="Creation Time" width="180" />
                <el-table-column label="Operation" >
                    <template #default="scope">
                    <el-button type="danger" size="small" @click="deleteCategorySubmit(scope.row)">
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
                :small="false" :background="true" layout="total, sizes, prev, pager, next, jumper"
                :total="total" @size-change="handleSizeChange" @current-change="getTableData" />
            </div>

        </el-card>

    <!-- Add Category -->
    <FormDialog ref="formDialogRef" title="Add Article Category" destroyOnClose @submit="onSubmit">
        <el-form ref="formRef" :rules="rules" :model="form">
                    <el-form-item label="Category Name" prop="name" label-width="80px" size="large">
                        <el-input v-model="form.name" placeholder="Please enter the category name" maxlength="20" show-word-limit clearable/>
                    </el-form-item>
                </el-form>
    </FormDialog>

    </div>
</template>

<script setup>
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { ref, reactive } from 'vue'
import { getCategoryPageList, addCategory, deleteCategory } from '@/api/admin/category'
import moment from 'moment'
import { showMessage, showModel } from '@/composables/util'
import FormDialog from '@/components/FormDialog.vue'

// Pagination query category name
const searchCategoryName = ref('')
// Date
const pickDate = ref('')

// Query condition: start and end time
const startDate = reactive({})
const endDate = reactive({})

// Listen to date component change event and set start and end time to variables
const datepickerChange = (e) => {
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')

    console.log('Start Time: ' + startDate.value + ', End Time: ' + endDate.value)
}

const shortcuts = [
    {
        text: 'Last Week',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        },
    },
    {
        text: 'Last Month',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        },
    },
    {
        text: 'Last Three Months',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            return [start, end]
        },
    },
]

// Table loading Loading
const tableLoading = ref(false)
// Table data
const tableData = ref([])
// Current page, given a default value of 1
const current = ref(1)
// Total data, given a default value of 0
const total = ref(0)
// Data displayed per page, given a default value of 10
const size = ref(10)


// Get pagination data
function getTableData() {
    // Show table loading
    tableLoading.value = true
    // Call backend pagination interface, passing required parameters
    
    getCategoryPageList({current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, name: searchCategoryName.value})
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

// Page display quantity change event
const handleSizeChange = (chooseSize) => {
    console.log('Chosen page size' + chooseSize)
    size.value = chooseSize
    getTableData()
}

// Reset query conditions
const reset = () => {
    searchCategoryName.value = ''
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
}

// Dialog visibility
const formDialogRef = ref(null)

// Add category button click event
const addCategoryBtnClick = () => {
    formDialogRef.value.open()
}


// Form reference
const formRef = ref(null)

// Add article category form object
const form = reactive({
    name: ''
})

// Rules validation
const rules = {
    name: [
        {
            required: true,
            message: 'Category name cannot be empty',
            trigger: 'blur',
        },
        { min: 1, max: 20, message: 'The category name must be more than 1 character and less than 20 characters', trigger: 'blur' },
    ]
}

const onSubmit = () => {
    // First validate form fields
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('Form validation failed')
            return false
        }
        // 
        formDialogRef.value.showBtnLoading()
        addCategory(form).then((res) => {
            if (res.success == true) {
                showMessage('Added successfully')
                // Clear the category name in the form
                form.name = ''
                // Hide dialog
                formDialogRef.value.close()
                // Re-request pagination interface, render data
                getTableData()
            } else {
                // Get the error message returned by the server
                let message = res.message
                // Display error message
                showMessage(message, 'error')
            }
        }).finally(() => formDialogRef.value.closeBtnLoading())

    })
}

// Delete category
const deleteCategorySubmit = (row) => {
    console.log(row)
    showModel('Are you sure you want to delete this category?').then(() => {
        deleteCategory(row.id).then((res) => {
            if (res.success == true) {
                showMessage('Deleted successfully')
                // Re-request pagination interface, render data
                getTableData()
            } else {
                // Get the error message returned by the server
                let message = res.message
                // Display error message
                showMessage(message, 'error')
            }
        })
    }).catch(() => {
        console.log('Cancelled')
    })
}

</script>
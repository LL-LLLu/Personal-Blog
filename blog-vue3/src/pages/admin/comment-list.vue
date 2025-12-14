<template>
  <div>
    <!-- Header with search conditions, shadow="never" means no shadow on card -->
    <el-card
      shadow="never"
      class="mb-5"
    >
      <!-- flex layout, vertically centered -->
      <div class="flex items-center">
        <el-text>Router URL</el-text>
        <div class="ml-3 w-52 mr-5">
          <el-input
            v-model="searchRouterUrl"
            placeholder="Enter (fuzzy search)"
            clearable
          />
        </div>

        <el-text>Created Date</el-text>
        <div class="ml-3 w-30 mr-5">
          <!-- Date picker component (range selection) -->
          <el-date-picker
            v-model="pickDate"
            type="daterange"
            range-separator="to"
            start-placeholder="Start date"
            end-placeholder="End date"
            size="default"
            :shortcuts="shortcuts"
            @change="datepickerChange"
          />
        </div>

        <el-text>Status</el-text>
        <div class="ml-3 w-30 mr-5">
          <el-select
            v-model="status"
            placeholder="---Please select---"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>

        <el-button
          type="primary"
          class="ml-3"
          :icon="Search"
          @click="getTableData"
        >
          Search
        </el-button>
        <el-button
          class="ml-3"
          :icon="RefreshRight"
          @click="reset"
        >
          Reset
        </el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <!-- Pagination table -->
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        border
        stripe
        table-layout="auto"
      >
        <el-table-column
          type="index"
          label="#"
          width="60"
        />
        <el-table-column
          prop="routerUrl"
          label="Router"
        >
          <template #default="scope">
            <el-link
              type="primary"
              :href="'#' + scope.row.routerUrl"
              target="_blank"
            >
              {{ scope.row.routerUrl
              }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column
          prop="avatar"
          label="Avatar"
          width="60"
        >
          <template #default="scope">
            <el-avatar
              :size="40"
              :src="scope.row.avatar"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="nickname"
          label="Nickname"
        />
        <el-table-column
          prop="content"
          label="Comment Content"
        />
        <el-table-column
          prop="createTime"
          label="Published Time"
          width="200"
        />
        <el-table-column
          prop="status"
          label="Status"
        >
          <template #default="scope">
            <el-tag
              v-if="scope.row.status == 1"
              type="warning"
            >
              Pending
            </el-tag>
            <el-tag
              v-else-if="scope.row.status == 2"
              type="success"
            >
              Approved
            </el-tag>
            <el-tag
              v-else-if="scope.row.status == 3"
              type="danger"
            >
              Rejected
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          label="Actions"
          width="150"
        >
          <template #default="scope">
            <el-tooltip
              class="box-item"
              effect="dark"
              content="Details"
              placement="bottom"
            >
              <el-button
                size="small"
                :icon="Tickets"
                circle
                @click="showDetailDialog(scope.row)"
              />
            </el-tooltip>

            <el-tooltip
              v-if="scope.row.status == 1"
              class="box-item"
              effect="dark"
              content="Review"
              placement="bottom"
            >
              <el-button
                size="small"
                :icon="Edit"
                circle
                @click="showEditDetailDialog(scope.row)"
              />
            </el-tooltip>

            <el-tooltip
              class="box-item"
              effect="dark"
              content="Delete"
              placement="bottom"
            >
              <el-button
                type="danger"
                size="small"
                :icon="Delete"
                circle
                @click="deleteCommentSubmit(scope.row)"
              />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="mt-10 flex justify-center">
        <el-pagination
          v-model:current-page="current"
          v-model:page-size="size"
          :page-sizes="[10, 20, 50]"
          :small="false"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="getTableData"
        />
      </div>
    </el-card>

    <!-- Comment detail dialog -->
    <el-dialog
      v-model="detailDialogVisible"
      title="Comment Details"
      width="700"
    >
      <el-form
        :model="commentDetail"
        label-width="auto"
      >
        <el-form-item label="Router">
          <el-input
            v-model="commentDetail.routerUrl"
            disabled
          />
        </el-form-item>
        <el-form-item label="Avatar">
          <el-avatar
            :size="40"
            :src="commentDetail.avatar"
          />
        </el-form-item>
        <el-form-item label="Nickname">
          <el-input
            v-model="commentDetail.nickname"
            disabled
          />
        </el-form-item>

        <el-form-item label="Comment Content">
          <el-input
            v-model="commentDetail.content"
            type="textarea"
            disabled
          />
        </el-form-item>
        <el-form-item label="Website">
          <el-input
            v-model="commentDetail.website"
            disabled
          />
        </el-form-item>
        <el-form-item label="Email">
          <el-input
            v-model="commentDetail.mail"
            disabled
          />
        </el-form-item>
        <el-form-item label="Published Time">
          <el-input
            v-model="commentDetail.createTime"
            disabled
          />
        </el-form-item>
        <el-form-item label="Status">
          <el-tag
            v-if="commentDetail.status == 1"
            type="warning"
          >
            Pending
          </el-tag>
          <el-tag
            v-else-if="commentDetail.status == 2"
            type="success"
          >
            Approved
          </el-tag>
          <el-tag
            v-else-if="commentDetail.status == 3"
            type="danger"
          >
            Rejected
          </el-tag>
        </el-form-item>
        <el-form-item label="Reason">
          <el-input
            v-model="commentDetail.reason"
            type="textarea"
            disabled
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">
            Close
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Comment review dialog -->
    <FormDialog
      ref="editDialogRef"
      title="Review Comment"
      destroy-on-close
      @submit="onSubmit"
    >
      <el-form
        ref="formRef"
        :rules="rules"
        :model="form"
        label-width="auto"
      >
        <el-form-item
          label="Status"
          prop="status"
        >
          <el-radio-group v-model="form.status">
            <el-radio label="2">
              Approve
            </el-radio>
            <el-radio label="3">
              Reject
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="form.status == 3"
          label="Reason"
          prop="reason"
        >
          <el-input
            v-model="form.reason"
            type="textarea"
            placeholder="Please enter the reason for rejection"
            :rows="6"
          />
        </el-form-item>
      </el-form>
    </FormDialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { getCommentPageList, deleteComment, examineComment } from '@/api/admin/comment'
import { Search, RefreshRight, Delete, Edit, Tickets } from '@element-plus/icons-vue'
import { showMessage, showModel } from '@/composables/util'
import FormDialog from '@/components/FormDialog.vue'
import moment from 'moment'

// Fuzzy search router URL
const searchRouterUrl = ref('')
// Date picker value
const pickDate = ref('')

// Query conditions: start and end dates
const startDate = reactive({})
const endDate = reactive({})

// Listen to date picker change event and set start/end dates
const datepickerChange = (e) => {
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')

    console.log('Start date: ' + startDate.value + ', End date: ' + endDate.value)
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

// Currently selected comment status
const status = ref(null)
// Comment status select options
const statusOptions = [
    {
        value: 1,
        label: 'Pending',
    },
    {
        value: 2,
        label: 'Approved',
    },
    {
        value: 3,
        label: 'Rejected',
    },
]

// Reset function
const reset = () => {
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    searchRouterUrl.value = ''
    status.value = null
}

// Table loading state
const tableLoading = ref(false)
// Table data
const tableData = ref([])
// Current page number, default 1
const current = ref(1)
// Total data count, default 0
const total = ref(0)
// Items per page, default 10
const size = ref(10)

// Get pagination data
function getTableData() {
    // Show table loading
    tableLoading.value = true
    // Call backend pagination API with parameters
    getCommentPageList({
        current: current.value, size: size.value, startDate: startDate.value,
        endDate: endDate.value, routerUrl: searchRouterUrl.value, status: status.value
    })
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

// Delete comment
const deleteCommentSubmit = (row) => {
    showModel('Are you sure you want to delete this comment and its replies?').then(() => {
        deleteComment(row.id).then((res) => {
            if (!res.success) {
                // Get error message from server
                let message = res.message
                // Show error message
                showMessage(message, 'error')
                return
            }

            showMessage('Deleted successfully')
            // Refresh table data
            getTableData()
        })
    }).catch((e) => {
        console.log('Cancelled')
    })
}

// Comment detail dialog visibility
const detailDialogVisible = ref(false)
// Comment detail data
const commentDetail = ref({})

// Show comment detail dialog
const showDetailDialog = (row) => {
    detailDialogVisible.value = true
    commentDetail.value = row
}

// Form reference
const formRef = ref(null)
// Comment review form object
const form = reactive({
    id: null,
    status: '2', // Default status 2, means approved
    reason: ''
})

// Validation rules
const rules = {
    status: [
        {
            required: true,
            message: 'Status cannot be empty',
            trigger: 'blur',
        },
    ],
    reason: [
        {
            required: true,
            message: 'Reason cannot be empty',
            trigger: 'blur',
        },
    ]
}

// Comment review dialog reference
const editDialogRef = ref(null)

// Show comment review dialog
const showEditDetailDialog = (row) => {
    editDialogRef.value.open()
    // Set the comment ID to form object
    form.id = row.id
}

// Form submit handler
const onSubmit = () => {
    // Validate form fields first
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('Form validation failed')
            return false
        }

        // Show submit button loading
        editDialogRef.value.showBtnLoading()
        examineComment(form).then((res) => {
            if (!res.success) {
                // Get error message from server
                let message = res.message
                // Show error message
                showMessage(message, 'error')
                return
            }

            showMessage('Review completed')
            // Reset form content
            form.id = null
            form.status = '2'
            form.reason = ''
            // Hide dialog
            editDialogRef.value.close()
            // Refresh table data
            getTableData()
        }).finally(() => editDialogRef.value.closeBtnLoading()) // Hide submit button loading

    })
}
</script>

<template>
    <el-dialog v-model="dialogVisible" :title="title" :width="width"
    :destroy-on-close="destroyOnClose"
     :draggable="true" :close-on-click-modal="false"
        :close-on-press-escape="false">
        
        <!-- Add Catalog Button -->
        <div class="mb-5">
            <el-button type="primary">
                <el-icon class="mr-1">
                    <Plus />
                </el-icon>
                Add Catalog</el-button>
        </div>

        <!-- Catalog Content -->
        <div id="accordion-flush" data-accordion="collapse"
            data-active-classes="bg-white dark:bg-gray-900 text-gray-900 dark:text-white"
            data-inactive-classes="text-gray-500 dark:text-gray-400">
            <h2 id="accordion-flush-heading-1" v-for="(catalog, index) in catalogs" :key="index">
                <!-- Level 1 Catalog -->
                <button type="button"
                    class="hover:bg-gray-100 py-3 px-3 rounded-lg flex items-center w-full font-medium rtl:text-right 
                    text-gray-500 dark:border-gray-700 dark:text-gray-400"
                    data-accordion-target="#accordion-flush-body-1" aria-expanded="true"
                    aria-controls="accordion-flush-body-1">
                    <svg data-accordion-icon class="w-3 h-3 mr-2 rotate-180 shrink-0" aria-hidden="true"
                        xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 6">
                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M9 5 5 1 1 5" />
                    </svg>
                    <!-- Level 1 Title -->
                    <span class="flex items-center grow" v-html="catalog.title"></span>
                    
                    <!-- Operation Icons -->
                    <div class="hover:bg-gray-200 rounded py-2 px-2" @click.stop>
                        <el-dropdown @command="handleCommand">
                                <span class="el-dropdown-link flex items-center">
                                    <svg t="1705472726484" class="icon w-5 h-5 outline-none" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="5968" width="200" height="200">
                                        <path
                                            d="M512 298.6496a85.3504 85.3504 0 1 0 0-170.6496 85.3504 85.3504 0 0 0 0 170.6496z"
                                            fill="#707070" p-id="5969"></path>
                                        <path
                                            d="M512 512m-85.3504 0a85.3504 85.3504 0 1 0 170.7008 0 85.3504 85.3504 0 1 0-170.7008 0Z"
                                            fill="#707070" p-id="5970"></path>
                                        <path
                                            d="M512 896a85.3504 85.3504 0 1 0 0-170.7008 85.3504 85.3504 0 0 0 0 170.7008z"
                                            fill="#707070" p-id="5971"></path>
                                    </svg>
                                </span>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item>
                                            <el-icon>
                                                <EditPen />
                                            </el-icon>
                                            Rename
                                        </el-dropdown-item>
                                        <el-dropdown-item>
                                            <el-icon>
                                                <Plus />
                                            </el-icon>
                                            Add Article
                                        </el-dropdown-item>
                                        <el-dropdown-item>
                                            <el-icon>
                                                <Top />
                                            </el-icon>
                                            Move Up
                                        </el-dropdown-item>
                                        <el-dropdown-item>
                                            <el-icon>
                                                <Bottom />
                                            </el-icon>
                                            Move Down
                                        </el-dropdown-item>
                                        <el-dropdown-item divided>
                                            <el-icon>
                                                <DocumentRemove />
                                            </el-icon>
                                            Remove
                                        </el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                    </div>
                </button>

                <!-- Level 2 Catalog -->
                <ul v-if="catalog.children && catalog.children.length > 0">
                    <li v-for="(childCatalog, index2) in catalog.children" :key="index2" 
                    class="flex items-center ps-10 py-2 pe-3 rounded-lg hover:bg-gray-100"
                    >
                        <!-- Level 2 Title -->
                        <span v-html="childCatalog.title"></span>
                        <span class="grow"></span>
                        <!-- Rename -->
                        <span class="hover:bg-gray-200 rounded py-2 px-2 ml-2 mr-2 cursor-pointer">
                                <svg t="1705474417719" class="icon w-4 h-4" viewBox="0 0 1024 1024" version="1.1"
                                    xmlns="http://www.w3.org/2000/svg" p-id="8306" width="200" height="200">
                                    <path
                                        d="M402.24 753.12l417.984-417.952a35.552 35.552 0 0 0 0-50.304l-75.424-75.424a35.552 35.552 0 0 0-50.304 0L276.576 627.392l-8.992 134.72 134.688-8.992zM193.664 801.92l13.76-205.92L644.192 159.168a106.656 106.656 0 0 1 150.848 0l75.424 75.424a106.656 106.656 0 0 1 0 150.848L433.632 822.304l-205.92 13.728A32 32 0 0 1 193.6 801.92zM644.224 259.744l-50.272 50.24 125.696 125.76 50.272-50.304-125.696-125.696z"
                                        fill="#707070" p-id="8307"></path>
                                </svg>
                        </span>
                        <!-- Remove -->
                        <el-tooltip class="box-item" effect="dark" content="Remove" placement="right">
                                    <span class="hover:bg-gray-200 rounded py-2 px-2 cursor-pointer">
                                        <svg t="1705473517473" class="icon w-4 h-4" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="7174" width="200" height="200">
                                            <path
                                                d="M607.897867 768.043004c-17.717453 0-31.994625-14.277171-31.994625-31.994625L575.903242 383.935495c0-17.717453 14.277171-31.994625 31.994625-31.994625s31.994625 14.277171 31.994625 31.994625l0 351.94087C639.892491 753.593818 625.61532 768.043004 607.897867 768.043004z"
                                                fill="#707070" p-id="7175"></path>
                                            <path
                                                d="M415.930119 768.043004c-17.717453 0-31.994625-14.277171-31.994625-31.994625L383.935495 383.935495c0-17.717453 14.277171-31.994625 31.994625-31.994625 17.717453 0 31.994625 14.277171 31.994625 31.994625l0 351.94087C447.924744 753.593818 433.647573 768.043004 415.930119 768.043004z"
                                                fill="#707070" p-id="7176"></path>
                                            <path
                                                d="M928.016126 223.962372l-159.973123 0L768.043004 159.973123c0-52.980346-42.659499-95.983874-95.295817-95.983874L351.94087 63.989249c-52.980346 0-95.983874 43.003528-95.983874 95.983874l0 63.989249-159.973123 0c-17.717453 0-31.994625 14.277171-31.994625 31.994625s14.277171 31.994625 31.994625 31.994625l832.032253 0c17.717453 0 31.994625-14.277171 31.994625-31.994625S945.73358 223.962372 928.016126 223.962372zM319.946246 159.973123c0-17.545439 14.449185-31.994625 31.994625-31.994625l320.806316 0c17.545439 0 31.306568 14.105157 31.306568 31.994625l0 63.989249L319.946246 223.962372 319.946246 159.973123 319.946246 159.973123z"
                                                fill="#707070" p-id="7177"></path>
                                            <path
                                                d="M736.048379 960.010751 288.123635 960.010751c-52.980346 0-95.983874-43.003528-95.983874-95.983874L192.139761 383.591466c0-17.717453 14.277171-31.994625 31.994625-31.994625s31.994625 14.277171 31.994625 31.994625l0 480.435411c0 17.717453 14.449185 31.994625 31.994625 31.994625l448.096758 0c17.717453 0 31.994625-14.277171 31.994625-31.994625L768.215018 384.795565c0-17.717453 14.277171-31.994625 31.994625-31.994625s31.994625 14.277171 31.994625 31.994625l0 479.231312C832.032253 916.835209 789.028725 960.010751 736.048379 960.010751z"
                                                fill="#707070" p-id="7178"></path>
                                        </svg>
                                    </span>
                                </el-tooltip>
                    </li>
                </ul>
            </h2>
        </div>
    </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { Plus, EditPen, Top, Bottom, DocumentRemove } from '@element-plus/icons-vue'

// Dialog visibility
const dialogVisible = ref(false)

// Confirm button loading
const btnLoading = ref(false)
// Show loading
const showBtnLoading = () => btnLoading.value = true
// Hide loading
const closeBtnLoading = () => btnLoading.value = false

// Props
const props = defineProps({
    title: String,
    width: {
        type: String,
        default: '40%'
    },
    destroyOnClose:  {
        type: Boolean,
        default: false
    },
    confirmText: {
        type: String,
        default: 'Submit'
    }
})

// Mock Data
const catalogs = ref([
    {
        "id": 1894,
        "articleId": null,
        "title": "<svg t=\"1705912761689\" class=\"icon inline-block w-4 h-4 mr-2\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"22962\" width=\"200\" height=\"200\"><path d=\"M847.071607 84.271534a11.877883 11.877883 0 0 0-3.583844-8.49883 12.594652 12.594652 0 0 0-8.806017-3.583844H189.282621a12.594652 12.594652 0 0 0-8.75482 3.583844 11.877883 11.877883 0 0 0-3.635041 8.49883v844.712055l284.557221-197.418612a89.084125 89.084125 0 0 1 101.064404 0l284.557222 197.418612V84.271534z m-327.870538 706.017288a12.748245 12.748245 0 0 0-14.437772 0l-323.825913 224.707026a50.941784 50.941784 0 0 1-51.607355 3.583844A48.023511 48.023511 0 0 1 102.4 975.829551V84.271534C102.4 61.898107 111.564401 40.497438 127.845293 24.677327 144.126185 8.857215 166.243623 0 189.282621 0h645.399125c23.038998 0 45.156436 8.857215 61.437327 24.677327 16.280892 15.820112 25.445293 37.220781 25.445293 59.594207v891.50682a48.023511 48.023511 0 0 1-26.930028 42.852536 50.941784 50.941784 0 0 1-51.607355-3.635042l-323.825914-224.655828zM313.334824 337.137335h397.192322a36.708803 36.708803 0 0 0 37.220781-36.145628 36.708803 36.708803 0 0 0-37.220781-36.09443H313.334824a36.708803 36.708803 0 0 0-37.220781 36.09443c0 19.967131 16.690474 36.145628 37.220781 36.145628z m0 192.606021h397.192322a36.708803 36.708803 0 0 0 37.220781-36.09443 36.708803 36.708803 0 0 0-37.220781-36.145627H313.334824a36.708803 36.708803 0 0 0-37.220781 36.145627c0 19.967131 16.690474 36.09443 37.220781 36.09443z" fill="#707070" p-id="22963"></path></svg>Overview",
        "sort": 1,
        "level": 1,
        "editing": false,
        "children": [
            {
                "id": 1895,
                "articleId": 19,
                "title": "Build blog from scratch",
                "sort": 1,
                "level": 2,
                "editing": false
            }
        ]
    }
])

// Open
const open = (wikiId) => {
    dialogVisible.value = true
    console.log("Wiki ID: " + wikiId)
}
// Close
const close = () => dialogVisible.value = false

const handleCommand = (command) => {
    console.log('click command: ' + command)
}

// Expose methods
defineExpose({
    open,
    close,
    showBtnLoading,
    closeBtnLoading
})

</script>
import nprogress from "nprogress"
import 'element-plus/es/components/message-box/style/css'    

// 消息提示
export function showMessage(message = 'Reminder Content', type = 'success', customClass = '') {
    return ElMessage({
        type: type,
        message,
        customClass,
    })
}

// 显示页面加载 Loading
export function showPageLoading() {
    nprogress.start()
}

// 隐藏页面加载 Loading
export function hidePageLoading() {
    nprogress.done()
}

// 弹出确认框
export function showModel(content = 'Reminder Content', type = 'warning', title = '') {
    return ElMessageBox.confirm(
        content,
        title,
        {
            confirmButtonText: 'Confirm',
            cancelButtonText: 'Cancel',
            type,
        }
    )
}
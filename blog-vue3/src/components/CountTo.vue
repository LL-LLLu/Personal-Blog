<template>
    <!-- 显示 d.num 属性的四舍五入取整后的值（若不加四舍五入，滚动时会显示小数点后面的数字） -->
    <div :class="customClass">{{ d.num.toFixed(0) }}</div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import gsap from 'gsap'

// 总数值
const d = reactive({
    num: 0
})

// 对外暴露的属性值
const props = defineProps({
    value: { // 属性值名称
        type: Number, // 类型为数值
        default: 0 // 默认为 0
    },
    customClass: { // 自定义样式
        type: String, // 字符串类型
        default: '' // 默认为空
    }
})

function animateToValue() {
    // Scroll from 0 to the value specified by the value property, animation duration is 0.5s 
    gsap.to(d, {
        duration: 0.5,
        num: props.value
    })
}
animateToValue()

// Watch property, monitor changes to props.value, once props.value changes, call animateToValue function to execute animation
watch(() => props.value, () => animateToValue())
</script>
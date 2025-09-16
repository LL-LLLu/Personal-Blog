
<template>
  <router-view></router-view>
</template>

<script setup>

</script>

<style>
  /* Custom top loading bar color */
  #nprogress .bar {
    background: #409eff!important;
  }

  /* Dark mode body background color */
  .dark body {
    --tw-bg-opacity: 1;
    background-color: rgb(17 24 39 / var(--tw-bg-opacity));
  }
</style>
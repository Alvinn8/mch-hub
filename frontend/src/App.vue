<template>
  <div class="p-3">
    <div class="flex align-items-center justify-content-between">
      <router-link to="/" class="flex align-items-center gap-2">
        <span class="pi pi-server text-primary" style="font-size: 1.5rem"></span>
        <span class="text-lg font-bold">MCH Hub</span>
      </router-link>
      <div class="text-sm text-500">Future login area</div>
    </div>
    <router-view class="mt-3" />
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.text-primary {
  color: #38bdf8;
}
</style>

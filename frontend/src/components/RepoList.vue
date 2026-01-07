<template>
  <div class="grid">
    <Card v-for="repo in repos" :key="repo.id" class="m-3">
      <template #header>
        <router-link :to="`/${repo.ownerName}/${repo.name}`" class="text-xl font-bold no-underline">
          <span class="mr-3"> {{ repo.ownerName }}/{{ repo.name }} </span>
          <Button icon="pi pi-arrow-right" />
        </router-link>
      </template>
      <template #content>
        <p class="mt-2 mb-1 text-300" v-if="repo.description">
          {{ repo.description }}
        </p>
        <small class="text-500">Storage: {{ repo.storagePath }}</small>
      </template>
    </Card>
  </div>
</template>

<script setup lang="ts">
import Card from 'primevue/card';
import Button from 'primevue/button';
import type { Repository } from '../types';

defineProps<{
  repos: Repository[];
}>();
</script>

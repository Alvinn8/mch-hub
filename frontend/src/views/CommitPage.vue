<template>
  <div>
    <div class="surface-card p-3 mb-3 border-round shadow-1">
      <router-link
        :to="`/${ownerType}/${owner}/${repo}`"
        class="p-button p-component p-button-text"
      >
        <Button label="Back to repo" icon="pi pi-arrow-left" />
      </router-link>
      <div class="flex align-items-center mb-3">
        <div>
          <div class="text-sm text-500">Commit</div>
          <h2 class="m-0">{{ hash }}</h2>
          <div class="text-300">{{ commit?.message }}</div>
          <div class="text-500">{{ commit?.committedAt }}</div>
        </div>
      </div>
    </div>
    <div class="surface-ground border-round overflow-hidden" style="height: calc(100vh - 300px)">
      <iframe
        v-if="fileViewerUrl"
        :src="fileViewerUrl"
        style="border: none; width: 100%; height: 100%"
        title="External file manager"
      ></iframe>
      <div v-else class="p-3 text-500">
        Provide your file manager URL via env VITE_FILE_VIEWER_BASE.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { fetchCommit, fetchRepo } from '../api';
import type { Commit, EntityOwnerType, Repository } from '../types';
import Button from 'primevue/button';

const props = defineProps<{
  ownerType: EntityOwnerType;
  owner: string;
  repo: string;
  hash: string;
}>();

const commit = ref<Commit | null>(null);
const repoDetails = ref<Repository | null>(null);

const fileViewerUrl = computed(() => {
  const base = import.meta.env.VITE_FILE_VIEWER_BASE;
  if (!base || !repoDetails.value) return '';
  const repoPath = `${props.owner}/${props.repo}`;
  return `${base}?protocol=mch&repository=${encodeURIComponent(repoPath)}&commit=${encodeURIComponent(props.hash)}`;
});

const load = async () => {
  commit.value = (await fetchCommit(props.ownerType, props.owner, props.repo, props.hash)).data;
  repoDetails.value = (await fetchRepo(props.ownerType, props.owner, props.repo)).data;
};

onMounted(load);
watch(() => [props.ownerType, props.owner, props.repo, props.hash], load);
</script>

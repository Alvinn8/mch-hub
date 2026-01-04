<template>
  <div class="surface-card p-3 border-round shadow-1">
    <div class="flex align-items-center gap-2 mb-2">
      <span class="pi pi-database text-primary"></span>
      <div>
        <h2 class="m-0">{{ repoPath }}</h2>
        <div class="text-500">{{ repo?.description }}</div>
        <small class="text-500">Storage: {{ repo?.storagePath }}</small>
      </div>
    </div>
    <h3 class="mt-4">Commits</h3>
    <CommitList :commits="commits" :ownerType="ownerType" :owner="owner" :repo="repoName" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { fetchCommits, fetchRepo } from '../api';
import CommitList from '../components/CommitList.vue';
import type { Commit, EntityOwnerType, Repository } from '../types';

const props = defineProps<{
  ownerType: EntityOwnerType;
  owner: string;
  repo: string;
}>();

const repo = ref<Repository | null>(null);
const commits = ref<Commit[]>([]);
const repoName = computed(() => props.repo);
const repoPath = computed(() => `${props.owner}/${props.repo}`);

const load = async () => {
  repo.value = (await fetchRepo(props.ownerType, props.owner, props.repo)).data;
  commits.value = (await fetchCommits(props.ownerType, props.owner, props.repo)).data;
};

onMounted(load);
watch(() => [props.ownerType, props.owner, props.repo], load);
</script>

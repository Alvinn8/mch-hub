<template>
  <div>
    <PasswordPrompt v-if="needsAuth" :repo-path="repoPath" @authenticated="load" />
    <div v-else class="surface-card p-3 border-round shadow-1">
      <div class="flex align-items-center gap-2 mb-2">
        <span class="pi pi-database text-primary"></span>
        <div>
          <h2 class="m-0">{{ repoPath }}</h2>
          <div class="text-500">{{ repo?.description }}</div>
          <small class="text-500">Storage: {{ repo?.storagePath }}</small>
        </div>
      </div>
      <h3 class="mt-4">Commits</h3>
      <CommitList :commits="commits" :owner="owner" :repo="repoName" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { fetchCommits, fetchRepo } from '../api';
import CommitList from '../components/CommitList.vue';
import PasswordPrompt from '../components/PasswordPrompt.vue';
import type { Commit, Repository } from '../types';

const props = defineProps<{
  owner: string;
  repo: string;
}>();

const repo = ref<Repository | null>(null);
const commits = ref<Commit[]>([]);
const repoName = computed(() => props.repo);
const repoPath = computed(() => `${props.owner}/${props.repo}`);

const needsAuth = ref(false);

const load = async () => {
  try {
    const repoRes = await fetchRepo(props.owner, props.repo);
    repo.value = repoRes.data;
    const commitsRes = await fetchCommits(props.owner, props.repo);
    commits.value = commitsRes.data;
    needsAuth.value = false;
  } catch (e: any) {
    const status = e?.response?.status;
    const data = e?.response?.data;
    if (status === 401 && data && data.passwordRequired === true) {
      needsAuth.value = true;
    } else {
      console.error('Failed to load repository:', e);
    }
  }
};

onMounted(load);
watch(
  () => [props.owner, props.repo],
  async () => {
    needsAuth.value = false;
    await load();
  }
);
</script>

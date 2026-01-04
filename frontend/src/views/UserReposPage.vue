<template>
  <div class="surface-card p-3 border-round shadow-1">
    <div class="flex align-items-center gap-2 mb-2">
      <span class="pi pi-user text-primary"></span>
      <div>
        <h2 class="m-0">{{ user?.username }}</h2>
        <div class="text-500">{{ user?.displayName }}</div>
      </div>
    </div>
    <RepoList :repos="repos" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { fetchUser, fetchUserRepos } from '../api';
import RepoList from '../components/RepoList.vue';
import type { Repository, User } from '../types';

const props = defineProps<{ username: string }>();

const user = ref<User | null>(null);
const repos = ref<Repository[]>([]);

const load = async () => {
  user.value = (await fetchUser(props.username)).data;
  repos.value = (await fetchUserRepos(props.username)).data;
};

onMounted(load);
watch(() => props.username, load);
</script>

<template>
  <div class="surface-card p-3 border-round shadow-1">
    <div class="flex align-items-center gap-2 mb-2">
      <span class="pi pi-sitemap text-primary"></span>
      <div>
        <h2 class="m-0">{{ org?.displayName }}</h2>
        <div class="text-500">{{ org?.slug }}</div>
      </div>
    </div>
    <RepoList :repos="repos" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { fetchOrg, fetchOrgRepos } from '../api';
import RepoList from '../components/RepoList.vue';
import type { Organization, Repository } from '../types';

const props = defineProps<{ slug: string }>();

const org = ref<Organization | null>(null);
const repos = ref<Repository[]>([]);

const load = async () => {
  org.value = (await fetchOrg(props.slug)).data;
  repos.value = (await fetchOrgRepos(props.slug)).data;
};

onMounted(load);
watch(() => props.slug, load);
</script>

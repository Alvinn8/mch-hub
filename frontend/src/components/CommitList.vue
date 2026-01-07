<template>
  <div class="grid">
    <div v-for="commit in commits" :key="commit.id" class="col-12">
      <div class="surface-card p-3 border-round shadow-1 flex align-items-center gap-2">
        <router-link :to="commitLink(commit)">
          <Button label="View" />
        </router-link>
        <div>
          <div class="text-sm text-500 mb-1">{{ commit.hash }}</div>
          <div class="font-bold">{{ formatDate(commit.committedAt) }}</div>
          <div class="text-300" v-if="commit.message">{{ commit.message }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { DateTime } from 'luxon';
import type { Commit } from '../types';
import Button from 'primevue/button';

const props = defineProps<{
  commits: Commit[];
  owner: string;
  repo: string;
}>();

const formatDate = (value?: string) => {
  if (!value) return '';
  return DateTime.fromISO(value).toLocaleString(DateTime.DATETIME_MED);
};

const commitLink = (commit: Commit) => `/${props.owner}/${props.repo}/commit/${commit.hash}`;
</script>

<template>
  <div class="grid">
    <div class="col-12 md:col-6">
      <div class="surface-card p-3 border-round shadow-1">
        <div class="flex align-items-center gap-2 mb-2">
          <span class="pi pi-user text-primary"></span>
          <h3 class="m-0">Users</h3>
        </div>
        <ul class="list-none p-0 m-0" v-if="users.length">
          <li v-for="user in users" :key="user.id" class="mb-2">
            <router-link :to="`/users/${user.username}`" class="font-bold">{{
              user.username
            }}</router-link>
            <div class="text-500 text-sm">{{ user.displayName }}</div>
          </li>
        </ul>
        <div v-else class="text-500">No users yet.</div>
      </div>
    </div>
    <div class="col-12 md:col-6">
      <div class="surface-card p-3 border-round shadow-1">
        <div class="flex align-items-center gap-2 mb-2">
          <span class="pi pi-sitemap text-primary"></span>
          <h3 class="m-0">Organizations</h3>
        </div>
        <ul class="list-none p-0 m-0" v-if="orgs.length">
          <li v-for="org in orgs" :key="org.id" class="mb-2">
            <router-link :to="`/orgs/${org.slug}`" class="font-bold">{{
              org.displayName
            }}</router-link>
            <div class="text-500 text-sm">
              {{ org.description || org.slug }} ({{ org.memberCount }} members)
            </div>
          </li>
        </ul>
        <div v-else class="text-500">No organizations yet.</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { fetchUsers, fetchOrgs } from '../api';
import type { Organization, User } from '../types';

const users = ref<User[]>([]);
const orgs = ref<Organization[]>([]);

onMounted(async () => {
  users.value = (await fetchUsers()).data;
  orgs.value = (await fetchOrgs()).data;
});
</script>

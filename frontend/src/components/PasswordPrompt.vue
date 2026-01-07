<template>
  <div class="surface-card p-5 border-round shadow-1">
    <div class="flex align-items-center gap-3 mb-4">
      <span class="pi pi-lock text-primary text-2xl"></span>
      <div>
        <h2 class="m-0">Password Required</h2>
        <p class="m-0 text-500">This repository is password protected</p>
      </div>
    </div>

    <p class="text-500 mb-4">
      Enter the password to access <strong>{{ repoPath }}</strong
      >.
    </p>

    <form @submit.prevent="handleSubmit" class="flex flex-column gap-4">
      <div class="flex flex-column gap-2">
        <label for="username" class="font-semibold">Repository</label>
        <InputText
          id="username"
          v-model="username"
          name="username"
          autocomplete="username"
          readonly
          disabled
          class="text-500"
          :style="{ width: '240px' }"
        />
      </div>

      <div class="flex flex-column gap-2">
        <label for="password" class="font-semibold">Password</label>
        <Password
          id="password"
          v-model="passwordInput"
          name="password"
          toggle-mask
          :feedback="false"
          placeholder="Enter repository password"
          autocomplete="current-password"
          :style="{ width: '300px' }"
          @keyup.enter="handleSubmit"
        />
      </div>

      <div class="flex align-items-center gap-3 mt-2">
        <Button
          type="submit"
          label="Unlock Repository"
          icon="pi pi-lock-open"
          :loading="isLoading"
        />
        <Message v-if="errorMessage" severity="error" class="m-0">{{ errorMessage }}</Message>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Message from 'primevue/message';
import { setRepoPassword } from '../api';

interface Props {
  repoPath: string;
}

interface Emits {
  (e: 'authenticated'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const passwordInput = ref('');
const errorMessage = ref('');
const isLoading = ref(false);

const username = computed(() => {
  // Props can't be destructured in script setup, access via defineProps
  return props.repoPath;
});

const handleSubmit = async () => {
  if (!passwordInput.value.trim()) {
    errorMessage.value = 'Please enter a password';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';

  try {
    setRepoPassword(props.repoPath, passwordInput.value);
    // Emit event so parent can retry the load
    emit('authenticated');
    passwordInput.value = '';
  } catch (error) {
    errorMessage.value = 'An error occurred while authenticating.';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
:deep(.p-inputtext) {
  background-color: var(--surface-50);
  border-color: var(--surface-border);
}

:deep(.p-inputtext:disabled) {
  opacity: 0.7;
}
</style>

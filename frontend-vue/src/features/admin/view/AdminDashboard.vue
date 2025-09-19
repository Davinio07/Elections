<template>
  <div class="admin-dashboard">
    <h1>Admin Dashboard</h1>
    <p>Dit is de pagina voor de beheerder.</p>

    <button @click="testBackendConnection">Test Backend Connectie</button>

    <div v-if="backendMessage" class="message-box">
      <strong>Bericht van backend:</strong> {{ backendMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { pingBackend } from '@/features/admin/service/admin-api';

const backendMessage = ref<string>('');

async function testBackendConnection() {
  backendMessage.value = 'Pingen...';
  backendMessage.value = await pingBackend();
}
</script>

<style scoped>
.admin-dashboard {
  padding: 2rem;
  font-family: sans-serif;
}
.message-box {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #e0f2fe;
  border: 1px solid #7dd3fc;
  border-radius: 8px;
}
</style>

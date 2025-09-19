<template>
  <div class="admin-dashboard">
    <header class="dashboard-header">
      <h1>Admin Dashboard</h1>
      <p>Overzicht van de verkiezingsdata.</p>
    </header>

    <div v-if="stats.length" class="stats-grid">
      <div v-for="stat in stats" :key="stat.id" class="stat-card">
        <div class="stat-icon">{{ stat.icon }}</div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-title">{{ stat.title }}</div>
        </div>
      </div>
    </div>
    <div v-else class="loading-message">
      <p>Statistieken worden geladen...</p>
    </div>

    <div class="connection-test">
      <button @click="testBackendConnection">Test Backend Connectie</button>
      <div v-if="backendMessage" class="message-box">
        <strong>Bericht van backend:</strong> {{ backendMessage }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { pingBackend, getAdminStats, type AdminStat } from '../services/admin-api';

const backendMessage = ref<string>('');
const stats = ref<AdminStat[]>([]);

// Haal de statistieken op zodra de component wordt geladen
onMounted(async () => {
  stats.value = await getAdminStats();
});

async function testBackendConnection() {
  backendMessage.value = 'Pingen...';
  backendMessage.value = await pingBackend();
}
</script>

<style scoped>
.admin-dashboard {
  padding: 2rem;
  font-family: sans-serif;
  background-color: #f8fafc;
  min-height: 100vh;
}

.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  font-size: 2rem;
  font-weight: bold;
  color: #1e293b;
}

.dashboard-header p {
  color: #64748b;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background-color: white;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  transition: transform 0.2s ease-in-out;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 2.5rem;
  margin-right: 1rem;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: bold;
  color: #1e293b;
}

.stat-title {
  color: #64748b;
  font-size: 0.9rem;
}

.loading-message {
  color: #64748b;
}

.connection-test {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e2e8f0;
}

button {
  background-color: #3b82f6;
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #2563eb;
}

.message-box {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #e0f2fe;
  border: 1px solid #7dd3fc;
  border-radius: 8px;
  color: #0c4a6e;
}
</style>

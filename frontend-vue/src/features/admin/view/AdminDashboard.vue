<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getDashboardStats, type DashboardStat } from '@/features/admin/service/admin-api';

const stats = ref<DashboardStat[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    stats.value = await getDashboardStats();
  } catch (err) {
    error.value = 'Kan statistieken niet laden. Controleer de backend connectie.';
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="dashboard-container">
    <h1 class="page-title">Admin Dashboard</h1>
    <p class="page-subtitle">Welkom terug. Hier is een overzicht van de verkiezingsstatus.</p>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <span>Data ophalen...</span>
    </div>

    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-else class="stats-grid">
      <div v-for="stat in stats" :key="stat.id" class="stat-card">
        <div class="stat-icon">{{ stat.icon }}</div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stat.value }}</h3>
          <p class="stat-title">{{ stat.title }}</p>
        </div>
      </div>
    </div>

    <div class="actions-section">
      <h2>Snelle Acties</h2>
      <div class="actions-grid">
        <div class="action-card disabled">
          <h3>💬 Support Chat</h3>
          <p>Chat wachtrij beheren (Binnenkort)</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container { padding: 2rem; max-width: 1200px; margin: 0 auto; }
.page-title { font-size: 2rem; color: #1f2937; margin-bottom: 0.5rem; font-weight: 700; }
.page-subtitle { color: #6b7280; margin-bottom: 2rem; }

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 1rem;
  border: 1px solid #f3f4f6;
  transition: transform 0.2s;
}

.stat-card:hover { transform: translateY(-2px); }

.stat-icon {
  font-size: 2.5rem;
  background: #f0f9ff;
  width: 64px; height: 64px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 12px;
}

.stat-content { display: flex; flex-direction: column; }
.stat-value { font-size: 1.5rem; font-weight: 800; color: #111827; margin: 0; }
.stat-title { color: #6b7280; font-size: 0.9rem; margin: 0; }

/* Actions */
.actions-section h2 { margin-bottom: 1rem; font-size: 1.25rem; }
.action-card {
  background: #f9fafb; border: 1px dashed #d1d5db; padding: 1.5rem;
  border-radius: 8px; color: #9ca3af;
}

/* States */
.error-message { background: #fee2e2; color: #991b1b; padding: 1rem; border-radius: 8px; }
.loading-container { display: flex; gap: 1rem; align-items: center; color: #6b7280; }
.spinner {
  border: 3px solid #f3f3f3; border-top: 3px solid #3b82f6;
  border-radius: 50%; width: 20px; height: 20px; animation: spin 1s linear infinite;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
</style>

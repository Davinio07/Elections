<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getMunicipalityNames } from '../service/MunicipalityElectionResults_api';

const municipalities = ref<string[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

function goToMunicipalityDetails(municipalityName: string) {
  // Dit is de logica voor de klikfunctionaliteit.
  alert(`De detailpagina voor de gemeente ${municipalityName} komt binnenkort.`);
}

onMounted(async () => {
  try {
    municipalities.value = await getMunicipalityNames();
  } catch (err) {
    error.value = 'Fout bij het ophalen van de lijst met gemeenten.';
    console.error(err);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="municipality-view">
    <h1>Gemeentelijke Uitslagen</h1>
    <p class="subtitle">Klik op een gemeente om de uitslagen te bekijken.</p>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Lijst van gemeenten wordt geladen...</p>
    </div>
    <div v-else-if="error" class="error">
      {{ error }}
    </div>
    <div v-else class="municipality-grid">
      <div
        v-for="municipality in municipalities"
        :key="municipality"
        class="municipality-card"
        @click="() => goToMunicipalityDetails(municipality)"
      >
        <div class="municipality-icon">{{ municipality.charAt(0) }}</div>
        <h3 class="municipality-name">{{ municipality }}</h3>
      </div>
    </div>
    <p v-if="!loading && municipalities.length === 0 && !error">
      Geen gemeenten gevonden.
    </p>
  </div>
</template>

<style scoped>
.municipality-view {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

h1 {
  text-align: center;
  margin-bottom: 0.5rem;
  color: #1f2937;
  font-size: 2.5rem;
}

.subtitle {
  text-align: center;
  color: #6b7280;
  font-size: 1.125rem;
  margin-bottom: 2rem;
}

.loading {
  text-align: center;
  padding: 4rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading p {
  color: #6b7280;
  font-size: 1.125rem;
}

.error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: #dc2626;
  background: #fee2e2;
  border-radius: 8px;
  margin: 2rem;
}

.municipality-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.municipality-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  cursor: pointer;
  text-align: center;
  border: 1px solid #e5e7eb;
}

.municipality-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  border-color: #3b82f6;
}

.municipality-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #3b82f6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.municipality-name {
  margin: 0;
  font-size: 1rem;
  color: #1f2937;
  line-height: 1.4;
  word-wrap: break-word;
}
</style>

<template>
  <div>
    <h1>Gemeentelijke Uitslagen</h1>
    <p v-if="loading">Resultaten worden geladen...</p>
    <div v-if="error" class="error">
      {{ error }}
    </div>
    <table v-if="results.length > 0" class="results-table">
      <thead>
      <tr>
        <th>Gemeente</th>
        <th>Partij</th>
        <th>Aantal Stemmen</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(result, index) in results" :key="index">
        <td>{{ result.municipalityName || 'N/A' }}</td>
        <td>{{ result.partyName }}</td>
        <td>{{ result.validVotes }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getMunicipalityElectionResults, type MunicipalityResult } from '../service/MunicipalityElectionResults_api';

const results = ref<MunicipalityResult[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    results.value = await getMunicipalityElectionResults();
  } catch (err) {
    error.value = 'Fout bij het ophalen van de gemeentelijke uitslagen.';
    console.error(err);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.results-table {
  width: 100%;
  border-collapse: collapse;
}

.results-table th, .results-table td {
  border: 1px solid #ddd;
  padding: 8px;
}

.results-table th {
  background-color: #f2f2f2;
  text-align: left;
}

.error {
  color: red;
  font-weight: bold;
}
</style>

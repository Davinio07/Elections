<template>
  <div>
    <h1>Kieskring Details</h1>
    <p>Toont de uitslagen per partij voor een geselecteerde kieskring.</p>

    <div class="search-container" v-if="kieskringNames.length > 0">
      <label for="kieskring-select">Kies een kieskring:</label>
      <select id="kieskring-select" v-model="selectedKieskring" @change="fetchResultsForSelectedKieskring">
        <option v-for="name in kieskringNames" :key="name" :value="name">
          {{ name }}
        </option>
      </select>
    </div>

    <p v-if="loading">Resultaten worden geladen...</p>
    <div v-if="error" class="error">
      {{ error }}
    </div>

    <table v-if="results.length > 0" class="results-table">
      <thead>
      <tr>
        <th>Partij</th>
        <th>Aantal Stemmen</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(result, index) in results" :key="index">
        <td>{{ result.partyName }}</td>
        <td>{{ result.validVotes.toLocaleString() }}</td>
      </tr>
      </tbody>
    </table>
    <p v-if="!loading && results.length === 0 && !error">
      Selecteer een kieskring om de resultaten te zien.
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getKieskringNames, getResultsForKieskring, type KieskringResult } from '../service/KieskringDetails_api';

const results = ref<KieskringResult[]>([]);
const kieskringNames = ref<string[]>([]);
const selectedKieskring = ref<string | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

async function fetchResultsForSelectedKieskring() {
  if (!selectedKieskring.value) return;

  loading.value = true;
  error.value = null;
  try {
    results.value = await getResultsForKieskring(selectedKieskring.value);
  } catch (err) {
    error.value = `Fout bij het ophalen van de uitslagen voor ${selectedKieskring.value}.`;
    console.error(err);
    results.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  try {
    const names = await getKieskringNames();
    kieskringNames.value = names;

    if (names.length > 0) {
      selectedKieskring.value = names[0];
      await fetchResultsForSelectedKieskring();
    } else {
      loading.value = false;
    }
  } catch (err) {
    error.value = 'Fout bij het ophalen van de lijst met kieskringen.';
    console.error(err);
    loading.value = false;
  }
});
</script>

<style scoped>
.results-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
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

.search-container {
  margin-bottom: 1rem;
}

.search-container label {
  margin-right: 0.5rem;
  font-weight: bold;
}

.search-container select {
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ddd;
  min-width: 250px;
}
</style>

<template>
  <div>
    <h1>Gemeentelijke Uitslagen</h1>

    <div class="search-container" v-if="municipalityNames.length > 0">
      <label for="municipality-select">Kies een gemeente:</label>
      <select id="municipality-select" v-model="selectedMunicipality" @change="fetchResultsForSelectedMunicipality">
        <option v-for="name in municipalityNames" :key="name" :value="name">
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
      Selecteer een gemeente om de resultaten te zien.
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getMunicipalityNames, getResultsForMunicipality, type MunicipalityResult } from '../service/MunicipalityElectionResults_api';

const results = ref<MunicipalityResult[]>([]);
const municipalityNames = ref<string[]>([]);
const selectedMunicipality = ref<string | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

/**
 * A function to get the results for the municipality that is currently selected.
 * We only run this function if a municipality has been selected.
 */
async function fetchResultsForSelectedMunicipality() {
  if (!selectedMunicipality.value) return;

  loading.value = true;
  error.value = null; // Clear any old errors
  try {
    // Call the API function to get the results for this municipality
    results.value = await getResultsForMunicipality(selectedMunicipality.value);
  } catch (err) {
    // If there is an error, show a message and clear the results
    error.value = `Fout bij het ophalen van de uitslagen voor ${selectedMunicipality.value}.`;
    console.error(err);
    results.value = [];
  } finally {
    // When the data is loaded (or failed), stop the loading spinner
    loading.value = false;
  }
}

/**
 * This runs when the component first appears on the screen.
 * It gets the list of all municipalities.
 */
onMounted(async () => {
  try {
    // Get the list of names from the API
    const names = await getMunicipalityNames();
    municipalityNames.value = names;

    // If we got names back, automatically select the first one
    if (names.length > 0) {
      selectedMunicipality.value = names[0];
      // Then fetch the results for this first municipality
      await fetchResultsForSelectedMunicipality();
    } else {
      // If there are no names, we stop loading
      loading.value = false;
    }
  } catch (err) {
    // If getting the names fails, we show an error and stop loading
    error.value = 'Fout bij het ophalen van de lijst met gemeenten.';
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

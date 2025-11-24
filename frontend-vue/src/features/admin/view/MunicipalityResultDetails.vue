<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getResultsForMunicipality, type MunicipalityResultDto } from '../service/MunicipalityElectionResults_api';
import MunicipalityChart from '@/features/admin/components/MunicipalityChart.vue';

// 1. Get the municipality name from the URL
const route = useRoute();
const municipalityName = route.params.name as string;

// 2. Page State
// Updated the type here to match the new DTO name
const results = ref<MunicipalityResultDto[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

// 3. Helper to calculate total votes
const totalVotes = (data: MunicipalityResultDto[]) => {
  return data.reduce((sum, r) => sum + r.validVotes, 0);
};

// 4. Fetch data when component mounts
onMounted(async () => {
  loading.value = true;
  error.value = null;
  try {
    // Call API
    results.value = await getResultsForMunicipality(municipalityName);

    // Sort by votes (highest first)
    // TypeScript now knows 'a' and 'b' are MunicipalityResultDto because of the ref type above
    results.value.sort((a, b) => b.validVotes - a.validVotes);

    if (results.value.length === 0) {
      error.value = `Geen resultaten gevonden voor ${municipalityName}.`;
    }
  } catch (err) {
    console.error(err);
    error.value = "Fout bij het ophalen van de data. Probeer het later opnieuw.";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="page-container">

    <router-link
      to="/municipality-results"
      class="back-link"
    >
      ← Terug naar overzicht
    </router-link>

    <header class="header">
      <h1 class="title">Verkiezingsuitslag: {{ municipalityName }}</h1>
      <p v-if="!loading && !error" class="meta-info">
        Totaal uitgebrachte stemmen: <strong>{{ totalVotes(results).toLocaleString('nl-NL') }}</strong>
      </p>
    </header>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <span>Data laden...</span>
    </div>

    <div v-else-if="error" class="error-state">
      {{ error }}
    </div>

    <div v-else class="content-grid">

      <section class="chart-section">
        <h2 class="section-title">Grafische Weergave</h2>
        <MunicipalityChart :results="results" />
      </section>

      <section class="table-section">
        <h2 class="section-title">Gedetailleerde Cijfers</h2>
        <div class="table-wrapper">
          <table class="results-table">
            <thead>
              <tr>
                <th class="col-party">Partij</th>
                <th class="col-votes">Stemmen</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in results" :key="row.partyName" class="table-row">
                <td class="col-party">
                  <span class="rank">{{ index + 1 }}.</span> {{ row.partyName }}
                </td>
                <td class="col-votes">
                  {{ row.validVotes.toLocaleString('nl-NL') }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </div>
  </div>
</template>

<style scoped>
.page-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  font-family: system-ui, -apple-system, sans-serif;
}

.back-link {
  display: inline-block;
  margin-bottom: 1.5rem;
  color: #2563eb;
  text-decoration: none;
  font-weight: 500;
}
.back-link:hover { text-decoration: underline; }

.header { margin-bottom: 2rem; }
.title { font-size: 2rem; font-weight: 700; color: #1f2937; margin: 0 0 0.5rem 0; }
.meta-info { color: #6b7280; font-size: 1.1rem; }

/* Layout */
.content-grid { display: flex; flex-direction: column; gap: 3rem; }

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 1rem;
  border-left: 4px solid #2563eb;
  padding-left: 0.75rem;
}

/* Table Styles */
.table-wrapper {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.results-table { width: 100%; border-collapse: collapse; }
.results-table th, .results-table td { padding: 1rem; text-align: left; border-bottom: 1px solid #f3f4f6; }
.results-table th { background-color: #f9fafb; font-weight: 600; text-transform: uppercase; font-size: 0.75rem; color: #6b7280; letter-spacing: 0.05em; }
.col-votes { text-align: right; font-variant-numeric: tabular-nums; }
.rank { color: #9ca3af; margin-right: 0.5rem; font-weight: normal; }
.table-row:last-child td { border-bottom: none; }
.table-row:hover { background-color: #f9fafb; }

/* States */
.loading-state, .error-state { text-align: center; padding: 4rem; color: #6b7280; }
.error-state { color: #dc2626; background: #fef2f2; border-radius: 0.5rem; border: 1px solid #fecaca; }
.spinner {
  border: 3px solid #e5e7eb; border-top: 3px solid #2563eb;
  border-radius: 50%; width: 30px; height: 30px;
  animation: spin 1s linear infinite; margin: 0 auto 1rem auto;
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>

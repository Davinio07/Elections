<script setup lang="ts">
import {ref} from 'vue'
import {getNationalResults} from '@/features/admin/service/NationalElectionResults_api.ts'

interface NationalResult {
  partyName: string
  validVotes: number
}

const nationalResults = ref<NationalResult[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);

async function fetchNationalResults(electionId: string) {
  loading.value = true;
  error.value = null;
  nationalResults.value = [];
  try {
    nationalResults.value = await getNationalResults(electionId);
  } catch (err) {
    console.error(err);
    // Cast to Error and show the message or a default one
    error.value = (err instanceof Error) ? err.message : 'An unknown error occurred while fetching results.';
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <h1>Nationale verkiezingsresultaten</h1>

  <div style="margin-bottom: 10px;">
    <button @click="() => fetchNationalResults('TK2023')">TK2023</button>
    <button @click="() => fetchNationalResults('TK2024')">TK2024</button>
  </div>

  <div v-if="loading">Bezig met laden...</div>
  <div v-if="error" style="color: red;">{{ error }}</div>

  <table v-if="nationalResults.length" border="1" cellspacing="0" cellpadding="6">
    <thead>
    <tr>
      <th>Partij</th>
      <th>Stemmen</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="r in nationalResults" :key="r.partyName">
      <td>{{ r.partyName }}</td>
      <td>{{ r.validVotes.toLocaleString() }}</td>
    </tr>
    </tbody>
  </table>
</template>

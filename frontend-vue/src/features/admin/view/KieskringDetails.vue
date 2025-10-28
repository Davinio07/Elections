<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold">Kieskring Uitslagen</h1>
    <p class="text-gray-600">Toont de uitslagen per partij voor alle kieskringen.</p>

    <p v-if="loading" class="text-gray-500">Resultaten worden geladen...</p>
    <div v-if="error" class="text-red-600 font-semibold">{{ error }}</div>

    <div v-if="!loading && kieskringData.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="(kieskring, index) in kieskringData"
        :key="index"
        class="bg-white rounded-xl shadow p-4 hover:shadow-lg transition-shadow duration-200"
      >
        <div class="flex justify-between items-center mb-3">
          <h2 class="text-lg font-semibold">{{ kieskring.name }}</h2>
          <span class="text-sm bg-blue-100 text-blue-800 px-2 py-1 rounded-full">
            {{ kieskring.results.reduce((sum, r) => sum + r.validVotes, 0).toLocaleString() }} stemmen
          </span>
        </div>

        <div class="space-y-2">
          <!-- Top 5 parties -->
          <div
            v-for="(result, rIndex) in topParties(kieskring.results)"
            :key="rIndex"
            class="flex justify-between items-center"
          >
            <div class="flex flex-col">
              <div class="flex items-center space-x-2">
                <div class="w-3 h-3 rounded-full bg-green-500"></div>
                <span class="text-sm font-medium">{{ result.partyName }}</span>
              </div>
              <span class="text-xs text-gray-500">
                {{ partyPercentage(result.validVotes, kieskring.results) }}%
              </span>
            </div>
            <div class="text-sm font-medium">{{ result.validVotes.toLocaleString() }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getKieskringNames, getResultsForKieskring, type KieskringResult } from '../service/KieskringDetails_api';

interface KieskringData {
  name: string;
  results: KieskringResult[];
}

const kieskringData = ref<KieskringData[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

// Top 5 parties by votes
const topParties = (results: KieskringResult[]) => {
  return [...results]
    .sort((a, b) => b.validVotes - a.validVotes)
    .slice(0, 5);
};

// Calculate percentage for a party
const partyPercentage = (votes: number, allResults: KieskringResult[]) => {
  const totalVotes = allResults.reduce((sum, r) => sum + r.validVotes, 0);
  return ((votes / totalVotes) * 100).toFixed(1);
};

onMounted(async () => {
  try {
    const names = await getKieskringNames();
    const resultsArray = await Promise.all(
      names.map(async (name) => {
        const results = await getResultsForKieskring(name);
        return { name, results };
      })
    );
    kieskringData.value = resultsArray;
  } catch (err) {
    console.error(err);
    error.value = 'Fout bij het ophalen van de kieskringgegevens.';
  } finally {
    loading.value = false;
  }
});
</script>

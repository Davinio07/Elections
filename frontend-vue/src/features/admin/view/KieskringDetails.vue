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
// 1. Import the NEW function and interfaces
import {
  getAllKieskringResults,
  type KieskringDataDto,
  type KieskringResultDto
} from '../service/KieskringDetails_api';

// 2. Use the new interface for your ref
const kieskringData = ref<KieskringDataDto[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

// 3. Update topParties to use the new interface
const topParties = (results: KieskringResultDto[]) => {
  return [...results]
    .sort((a, b) => b.validVotes - a.validVotes)
    .slice(0, 5);
};

// 4. Update partyPercentage to use the new interface
const partyPercentage = (votes: number, allResults: KieskringResultDto[]) => {
  const totalVotes = allResults.reduce((sum, r) => sum + r.validVotes, 0);
  return ((votes / totalVotes) * 100).toFixed(1);
};

// 5. SIMPLIFY your onMounted hook
onMounted(async () => {
  try {
    // Just ONE API call!
    kieskringData.value = await getAllKieskringResults();
  } catch (err) {
    error.value = 'Fout bij het ophalen van de kieskringgegevens.';
  } finally {
    loading.value = false;
  }
});
</script>

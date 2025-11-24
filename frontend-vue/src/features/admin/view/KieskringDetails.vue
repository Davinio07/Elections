<template>
  <div class="space-y-6 p-6">
    <div class="flex flex-col md:flex-row justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Kieskring Uitslagen</h1>
        <p v-if="!filterName" class="text-gray-600 mt-1">Toont de uitslagen per partij voor alle kieskringen.</p>
        <p v-else class="text-gray-600 mt-1">
          Resultaten gefilterd voor: <span class="font-bold text-blue-600">{{ filterName }}</span>
        </p>
      </div>

      <button
        v-if="filterName"
        @click="clearFilter"
        class="mt-4 md:mt-0 px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg transition-colors text-sm font-medium"
      >
        ✕ Toon alle kieskringen
      </button>
    </div>

    <p v-if="loading" class="text-gray-500">Resultaten worden geladen...</p>
    <div v-if="error" class="text-red-600 font-semibold bg-red-50 p-4 rounded">{{ error }}</div>

    <div v-if="!loading && kieskringData.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="(kieskring, index) in kieskringData"
        :key="index"
        class="bg-white rounded-xl shadow p-4 hover:shadow-lg transition-shadow duration-200 border border-gray-100"
      >
        <div class="flex justify-between items-center mb-4 border-b border-gray-100 pb-3">
          <h2 class="text-lg font-bold text-gray-800">{{ kieskring.name }}</h2>
          <span class="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full font-medium">
            {{ kieskring.results.reduce((sum, r) => sum + r.validVotes, 0).toLocaleString() }} stemmen
          </span>
        </div>

        <div class="space-y-3">
          <div
            v-for="(result, rIndex) in topParties(kieskring.results)"
            :key="rIndex"
            class="flex justify-between items-center"
          >
            <div class="flex flex-col">
              <div class="flex items-center space-x-2">
                <div class="w-2 h-8 rounded-sm bg-blue-500"></div>
                <span class="text-sm font-semibold text-gray-700">{{ result.partyName }}</span>
              </div>
            </div>
            <div class="text-right">
              <div class="text-sm font-bold text-gray-900">{{ result.validVotes.toLocaleString() }}</div>
              <div class="text-xs text-gray-500">{{ partyPercentage(result.validVotes, kieskring.results) }}%</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!loading && kieskringData.length === 0" class="text-center py-10 text-gray-500">
      Geen kieskring gevonden met de naam "{{ filterName }}".
      <br>
      <span class="text-xs text-gray-400">(Check de console F12 voor beschikbare namen)</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  getAllKieskringResults,
  type KieskringDataDto,
  type KieskringResultDto
} from '../service/KieskringDetails_api';

const route = useRoute();
const router = useRouter();

const allKieskringData = ref<KieskringDataDto[]>([]);
const kieskringData = ref<KieskringDataDto[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const filterName = ref<string>('');

// Helpers
const topParties = (results: KieskringResultDto[]) => {
  return [...results].sort((a, b) => b.validVotes - a.validVotes).slice(0, 5);
};

const partyPercentage = (votes: number, allResults: KieskringResultDto[]) => {
  const totalVotes = allResults.reduce((sum, r) => sum + r.validVotes, 0);
  return totalVotes > 0 ? ((votes / totalVotes) * 100).toFixed(1) : '0.0';
};

// Clear filter function
const clearFilter = () => {
  filterName.value = '';
  kieskringData.value = allKieskringData.value;
  router.replace({ query: {} });
};

// Helper to apply filter locally (Robuuster gemaakt)
function applyFilter() {
  if (filterName.value) {
    const search = filterName.value.toLowerCase().trim();

    console.log(`🔍 Filteren op: "${search}"`);

    kieskringData.value = allKieskringData.value.filter(k => {
      const dataName = k.name.toLowerCase().trim();
      // Check op exacte match OF of de een in de ander zit (bijv "Kieskring Amsterdam" vs "Amsterdam")
      return dataName === search || dataName.includes(search) || search.includes(dataName);
    });

    if (kieskringData.value.length === 0) {
      console.warn("⚠️ Geen match gevonden! Beschikbare namen in data:", allKieskringData.value.map(k => k.name));
    }
  } else {
    kieskringData.value = allKieskringData.value;
  }
}

// Fetch Data
onMounted(async () => {
  try {
    loading.value = true;
    const data = await getAllKieskringResults();
    allKieskringData.value = data;

    console.log(`✅ ${data.length} resultaten geladen.`);

    if (route.query.name) {
      filterName.value = route.query.name as string;
      applyFilter();
    } else {
      kieskringData.value = data;
    }
  } catch (err) {
    error.value = 'Fout bij het ophalen van de kieskringgegevens.';
    console.error(err);
  } finally {
    loading.value = false;
  }
});

watch(
  () => route.query.name,
  (newName) => {
    filterName.value = (newName as string) || '';
    applyFilter();
  }
);
</script>

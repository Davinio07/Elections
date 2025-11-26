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

    <div v-if="!loading && constituencyData.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="(constituency, index) in constituencyData"
        :key="index"
        class="bg-white rounded-xl shadow p-4 hover:shadow-lg transition-shadow duration-200 border border-gray-100"
      >
        <div class="flex justify-between items-center mb-4 border-b border-gray-100 pb-3">
          <h2 class="text-lg font-bold text-gray-800">{{ constituency.name }}</h2>
          <span class="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full font-medium">
            {{ formatVotes(constituency.results) }} stemmen
          </span>
        </div>

        <div class="space-y-3">
          <div
            v-for="(result, rIndex) in topParties(constituency.results)"
            :key="rIndex"
            class="flex justify-between items-center"
          >
            <div class="flex flex-col">
              <div class="flex items-center space-x-2">
                <div class="mr-1 flex-shrink-0 w-6 h-6 flex items-center justify-center">
                  <img
                    v-if="getPartyLogo(result.partyName)"
                    :src="getPartyLogo(result.partyName)!"
                    :alt="result.partyName"
                    class="w-full h-full object-contain"
                  />

                  <div
                    v-else
                    class="w-5 h-5 rounded-full border border-gray-100 shadow-sm"
                    :style="{ backgroundColor: getPartyColor(result.partyName) }"
                  ></div>
                </div>
                <span class="text-sm font-semibold text-gray-700">{{ result.partyName }}</span>
              </div>
            </div>
            <div class="text-right">
              <div class="text-sm font-bold text-gray-900">{{ result.validVotes.toLocaleString() }}</div>
              <div class="text-xs text-gray-500">{{ partyPercentage(result.validVotes, constituency.results) }}%</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!loading && constituencyData.length === 0" class="text-center py-10 text-gray-500">
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
  getAllConstituencyResults,
  type ConstituencyDataDto,
  type ConstituencyResultDto
} from '../service/ConstituencyDetails_api';
import { getPartyColor, getPartyLogo } from '../service/partyService'; // NEW IMPORT

const route = useRoute();
const router = useRouter();

const allConstituencyData = ref<ConstituencyDataDto[]>([]);
const constituencyData = ref<ConstituencyDataDto[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const filterName = ref<string>('');

// Helper to sum votes
const formatVotes = (results: ConstituencyResultDto[]) => {
  return results.reduce((sum, r) => sum + r.validVotes, 0).toLocaleString();
}

const topParties = (results: ConstituencyResultDto[]) => {
  return [...results].sort((a, b) => b.validVotes - a.validVotes).slice(0, 5);
};

const partyPercentage = (votes: number, allResults: ConstituencyResultDto[]) => {
  const totalVotes = allResults.reduce((sum, r) => sum + r.validVotes, 0);
  return totalVotes > 0 ? ((votes / totalVotes) * 100).toFixed(1) : '0.0';
};

// Clear filter function
const clearFilter = () => {
  filterName.value = '';
  constituencyData.value = allConstituencyData.value;
  router.replace({ query: {} });
};

// Helper to apply filter locally (Robuuster gemaakt)
function applyFilter() {
  if (filterName.value) {
    const search = filterName.value.toLowerCase().trim();
    constituencyData.value = allConstituencyData.value.filter(k => {
      const dataName = k.name.toLowerCase().trim();
      // Check op exacte match OF of de een in de ander zit (bijv "Kieskring Amsterdam" vs "Amsterdam")
      return dataName === search || dataName.includes(search) || search.includes(dataName);
    });

    if (constituencyData.value.length === 0) {
      console.warn("⚠️ Geen match gevonden! Beschikbare namen in data:", allConstituencyData.value.map(k => k.name));
    }
  } else {
    constituencyData.value = allConstituencyData.value;
  }
}

onMounted(async () => {
  try {
    loading.value = true;
    // Calling the new service function
    const data = await getAllConstituencyResults();
    allConstituencyData.value = data;

    console.log(`✅ ${data.length} resultaten geladen.`);

    if (route.query.name) {
      filterName.value = route.query.name as string;
      applyFilter();
    } else {
      constituencyData.value = data;
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

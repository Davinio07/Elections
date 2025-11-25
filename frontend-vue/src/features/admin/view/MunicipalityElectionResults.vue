<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import {
  getAllMunicipalityResults,
  type MunicipalityDataDto,
  type MunicipalityResultDto
} from '../service/MunicipalityElectionResults_api';

const router = useRouter();

// State
const municipalities = ref<MunicipalityDataDto[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const searchQuery = ref('');

// Computed: Filter logic based on search input
const filteredMunicipalities = computed(() => {
  if (!searchQuery.value) return municipalities.value;
  const query = searchQuery.value.toLowerCase();
  return municipalities.value.filter(m =>
    m.name.toLowerCase().includes(query)
  );
});

// Helper: Calculate total votes for a municipality
const getTotalVotes = (results: MunicipalityResultDto[]) => {
  return results.reduce((sum, r) => sum + r.validVotes, 0);
};

// Helper: Get top 3 parties sorted by votes
const getTop3 = (results: MunicipalityResultDto[]) => {
  // Creating a copy with [...results] to prevent mutating the original array while sorting
  return [...results].sort((a, b) => b.validVotes - a.validVotes).slice(0, 3);
};

// Helper: Calculate percentage
const getPercentage = (votes: number, allResults: MunicipalityResultDto[]) => {
  const total = getTotalVotes(allResults);
  if (total === 0) return '0.0';
  return ((votes / total) * 100).toFixed(1);
};

// Helper: Format number (1000 -> 1.000)
const formatNumber = (num: number) => {
  return new Intl.NumberFormat('nl-NL').format(num);
};

// Helper: Give the top 3 distinct colors (Gold, Silver, Bronze-ish styles)
const getBarColor = (index: number) => {
  const colors = [
    '#3b82f6', // Blue-500 (Winner)
    '#9ca3af', // Gray-400 (2nd)
    '#d1d5db'  // Gray-300 (3rd)
  ];
  return colors[index] || '#e5e7eb';
};

// Navigation: Details Page
const goToDetail = (name: string) => {
  // Using encodeURIComponent to handle names with spaces safely
  router.push(`/municipality-results/${encodeURIComponent(name)}`);
};

// Navigation: Parties Page with Municipality pre-selected
const goToParties = (name: string) => {
  router.push({
    path: '/parties',
    query: { level: 'Gemeentes', name: name }
  });
};

// Fetch Data
onMounted(async () => {
  try {
    loading.value = true;
    // We use the new API function that fetches the aggregated data from the backend
    const data = await getAllMunicipalityResults();

    // Sort municipalities alphabetically by default
    municipalities.value = data.sort((a, b) => a.name.localeCompare(b.name));

  } catch (err) {
    console.error(err);
    error.value = 'Kon de gemeente resultaten niet ophalen. Probeer het later opnieuw.';
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="space-y-6 p-6">
    <div class="flex flex-col md:flex-row justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Gemeente Uitslagen</h1>
        <p v-if="!searchQuery" class="text-gray-600 mt-1">
          Overzicht van de verkiezingsuitslagen per gemeente.
        </p>
        <p v-else class="text-gray-600 mt-1">
          Resultaten gefilterd voor: <span class="font-bold text-blue-600">{{ searchQuery }}</span>
        </p>
      </div>

      <div class="mt-4 md:mt-0 relative">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Zoek een gemeente..."
          class="pl-4 pr-10 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none w-full md:w-64"
        />
        <span class="absolute right-3 top-2.5 text-gray-400">🔍</span>
      </div>
    </div>

    <p v-if="loading" class="text-gray-500 text-center py-10">
      Gemeente data wordt geladen...
    </p>

    <div v-if="error" class="text-red-600 font-semibold bg-red-50 p-4 rounded border border-red-200">
      {{ error }}
    </div>

    <div v-if="!loading && filteredMunicipalities.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="(muni, index) in filteredMunicipalities"
        :key="index"
        class="bg-white rounded-xl shadow p-4 hover:shadow-lg transition-shadow duration-200 border border-gray-100 cursor-pointer flex flex-col justify-between group"
        @click="goToDetail(muni.name)"
      >
        <div class="flex justify-between items-center mb-4 border-b border-gray-100 pb-3">
          <h2 class="text-lg font-bold text-gray-800 truncate" :title="muni.name">
            {{ muni.name }}
          </h2>
          <span class="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full font-medium whitespace-nowrap">
            {{ formatNumber(getTotalVotes(muni.results)) }} stemmen
          </span>
        </div>

        <div class="space-y-3">
          <div
            v-for="(result, rIndex) in getTop3(muni.results)"
            :key="rIndex"
            class="flex flex-col"
          >
            <div class="flex justify-between text-sm mb-1">
              <span class="font-semibold text-gray-700 truncate w-32">{{ result.partyName }}</span>
              <span class="text-gray-900 font-bold">
                {{ getPercentage(result.validVotes, muni.results) }}%
              </span>
            </div>

            <div class="w-full bg-gray-200 rounded-full h-2">
              <div
                class="h-2 rounded-full transition-all duration-500"
                :style="{ width: getPercentage(result.validVotes, muni.results) + '%', backgroundColor: getBarColor(rIndex) }"
              ></div>
            </div>
          </div>
        </div>

        <div class="mt-4 flex justify-between items-center border-t border-gray-50 pt-3">
          <button
            @click.stop="goToParties(muni.name)"
            class="text-xs font-medium text-green-700 hover:text-green-900 bg-green-50 hover:bg-green-100 px-3 py-1.5 rounded transition-colors border border-green-200"
          >
            📊 Bekijk Partijen
          </button>
          <span class="text-xs text-blue-500 font-medium group-hover:underline flex items-center">
              Details
              <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 ml-1" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
              </svg>
            </span>
        </div>
      </div>
    </div>

    <div v-else-if="!loading && filteredMunicipalities.length === 0" class="text-center py-10 text-gray-500">
      Geen gemeente gevonden met de naam "{{ searchQuery }}".
    </div>
  </div>
</template>

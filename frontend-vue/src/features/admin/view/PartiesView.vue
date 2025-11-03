<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { partyService, getPartyColor, type NationalResult, type PoliticalParty } from '../service/partyService';
import ComparisonChart from '../components/ComparisonChart.vue';

// --- Component State ---
const parties = ref<PoliticalParty[]>([]);
const nationalResults = ref<NationalResult[]>([]);
const loading = ref(true);
const error = ref(false);

const selectedParties = ref<PoliticalParty[]>([]);
const MAX_PARTIES = 2;

// --- Data Loading ---
const loadData = async () => {
  loading.value = true;
  error.value = false;
  try {
    const [partiesList, results] = await Promise.all([
      partyService.getParties('TK2023'),
      partyService.getNationalResults('TK2023')
    ]);
    parties.value = partiesList;
    nationalResults.value = results;
  } catch (err) {
    error.value = true;
    console.error('Error loading data:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
});

// --- Computed Properties ---

const selectedPartyCount = computed(() => selectedParties.value.length);

// Get the full NationalResult data for the selected parties
const comparisonData = computed<NationalResult[]>(() => {
  const selectedAppellations = selectedParties.value.map(p => p.registeredAppellation);


  return nationalResults.value
    .filter(result => {
      return selectedAppellations.some(appName => appName.startsWith(result.partyName));
    })
    .sort((a, b) => b.validVotes - a.validVotes);
});

// --- Helper Functions ---

const getInitials = (name: string): string => {
  const words = name.split(' ').filter(word => word.length > 0);
  if (words.length === 1) {
    return words[0].substring(0, 2).toUpperCase();
  }
  return words
    .slice(0, 2)
    .map(word => word[0])
    .join('')
    .toUpperCase();
};

const isSelected = (party: PoliticalParty): boolean => {
  return selectedParties.value.some(p => p.registeredAppellation === party.registeredAppellation);
};

const toggleParty = (party: PoliticalParty) => {
  const index = selectedParties.value.findIndex(
    p => p.registeredAppellation === party.registeredAppellation
  );

  if (index === -1) {
    if (selectedParties.value.length < MAX_PARTIES) {
      selectedParties.value.push(party);
    } else {
      console.warn(`Cannot select more than ${MAX_PARTIES} parties.`);
    }
  } else {
    selectedParties.value.splice(index, 1);
  }
};
</script>

<template>
  <div class="p-8 max-w-[1400px] mx-auto min-h-screen bg-gradient-to-br from-gray-50 to-gray-300">
    <h1 class="text-4xl sm:text-3xl text-2xl text-center mb-2 text-gray-900 font-bold">Politieke Partijen</h1>


    <div v-if="loading" class="text-center p-16 flex flex-col items-center gap-4">
      <div class="w-12 h-12 border-4 border-gray-200 border-t-blue-500 rounded-full animate-spin"></div>
      <p class="text-gray-600 text-lg">Partijen Laden...</p>
    </div>

    <div v-else-if="error" class="text-center p-8 text-xl text-red-700 bg-red-100 rounded-lg m-8">
      Failed to load parties. Please try again.
    </div>

    <div v-else>
      <!-- === COMPARISON PANEL === -->
      <div class="bg-white rounded-xl p-6 mb-8 shadow-md border border-gray-200 min-h-[200px]">
        <p class="text-xl font-semibold text-gray-900 mb-6 border-b border-dashed border-gray-200 pb-4">Vergelijking ({{ selectedPartyCount }} / {{ MAX_PARTIES }})</p>

        <div v-if="selectedPartyCount === 0" class="text-center p-8 text-gray-600 italic bg-gray-50 rounded-lg">
          Selecteer twee partijen uit de onderstaande lijst om hun nationale resultaten te vergelijken.
        </div>

        <div v-else class="grid grid-cols-1 gap-6 max-w-2xl mx-auto md:max-w-none">
          <ComparisonChart
            :parties="comparisonData"
            metric="validVotes"
            title="Stemmen"
          />
        </div>
      </div>
      <!-- === END COMPARISON PANEL === -->

      <p class="text-center text-gray-700 font-medium mb-6 text-base">{{ parties.length }} partijen geregistreerd voor TK2023</p>

      <div class="grid gap-4 grid-cols-1 sm:grid-cols-[repeat(auto-fill,minmax(200px,1fr))] md:gap-6 md:grid-cols-[repeat(auto-fill,minmax(250px,1fr))] lg:grid-cols-[repeat(auto-fill,minmax(280px,1fr))] animate-fadeIn">
        <div
          v-for="party in parties"
          :key="party.registeredAppellation"
          class="flex flex-col items-center gap-4 p-6 bg-white rounded-xl shadow-md transition-all duration-300 ease-in-out cursor-pointer border-l-4 hover:-translate-y-1 hover:shadow-lg"
          :class="{
            'ring-4 scale-102': isSelected(party),
            'opacity-60 cursor-not-allowed shadow-none hover:transform-none hover:shadow-md': !isSelected(party) && selectedPartyCount >= MAX_PARTIES
          }"
          :style="{
            'border-left-color': getPartyColor(party.registeredAppellation),
            'ring-color': getPartyColor(party.registeredAppellation)
          }"
          @click="toggleParty(party)"
        >
          <div
            class="w-[70px] h-[70px] rounded-full flex items-center justify-center text-white font-bold text-xl shadow-md"
            :style="{ 'background-color': getPartyColor(party.registeredAppellation) }"
          >
            {{ getInitials(party.registeredAppellation) }}
          </div>
          <h3 class="text-base text-gray-900 text-center leading-snug break-words w-full">{{ party.registeredAppellation }}</h3>
        </div>
      </div>
    </div>
  </div>
</template>

<style>

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.animate-fadeIn {
  animation: fadeIn 0.5s ease-in;
}
</style>


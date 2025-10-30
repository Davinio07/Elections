<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { partyService, getPartyColor, type NationalResult } from '../service/partyService';
import ComparisonChart from '../components/ComparisonChart.vue'; // Import the chart component

interface PoliticalParty {
  registeredAppellation: string;
}

// --- Component State ---
const parties = ref<PoliticalParty[]>([]);
const nationalResults = ref<NationalResult[]>([]); // To store all vote/seat data
const loading = ref(true);
const error = ref(false);

const selectedParties = ref<PoliticalParty[]>([]);
const MAX_PARTIES = 2;

// --- Data Loading ---
const loadData = async () => {
  loading.value = true;
  error.value = false;
  try {
    // Fetch both parties list and national results in parallel
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

// Get the count of selected parties
const selectedPartyCount = computed(() => selectedParties.value.length);

// Get the full NationalResult data for the selected parties
const comparisonData = computed<NationalResult[]>(() => {
  const selectedNames = selectedParties.value.map(p => p.registeredAppellation);

  return nationalResults.value
    .filter(result => selectedNames.includes(result.partyName))
    .sort((a, b) => b.seats - a.seats); // Sort by seats
});

// --- Helper Functions ---

// Get initials from party name for the icon
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

// Check if a party is currently selected
const isSelected = (party: PoliticalParty): boolean => {
  return selectedParties.value.some(p => p.registeredAppellation === party.registeredAppellation);
};

// Add or remove a party from the selectedParties list
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
  <div class="parties-view">
    <h1>Politieke Partijen</h1>
    <p class="subtitle">Select up to {{ MAX_PARTIES }} parties to compare their TK2023 results.</p>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Loading parties...</p>
    </div>

    <div v-else-if="error" class="error">
      Failed to load parties. Please try again.
    </div>

    <div v-else>
      <div class="comparison-panel">
        <p class="panel-title">Your Comparison ({{ selectedPartyCount }} / {{ MAX_PARTIES }})</p>

        <div v-if="selectedPartyCount === 0" class="default-state">
          Select two parties from the list below to compare their national results.
        </div>

        <div v-else class="comparison-charts-grid">
          <ComparisonChart
            :parties="comparisonData"
            metric="seats"
            title="Zetels (Seats)"
          />
          <ComparisonChart
            :parties="comparisonData"
            metric="votes"
            title="Stemmen (Votes)"
          />
        </div>
      </div>
      <p class="party-count">{{ parties.length }} parties registered for TK2023</p>

      <div class="party-grid">
        <div
          v-for="party in parties"
          :key="party.registeredAppellation"
          class="party-card"
          :class="{
            selected: isSelected(party),
            'selection-disabled': !isSelected(party) && selectedPartyCount >= MAX_PARTIES
          }"
          :style="{ borderLeftColor: getPartyColor(party.registeredAppellation) }"
          @click="toggleParty(party)"
        >
          <div
            class="party-icon"
            :style="{ backgroundColor: getPartyColor(party.registeredAppellation) }"
          >
            {{ getInitials(party.registeredAppellation) }}
          </div>
          <h3>{{ party.registeredAppellation }}</h3>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.parties-view {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

h1 {
  text-align: center;
  margin-bottom: 0.5rem;
  color: #1f2937;
  font-size: 2.5rem;
}

.subtitle {
  text-align: center;
  color: #6b7280;
  font-size: 1.125rem;
  margin-bottom: 2rem;
}

.party-count {
  text-align: center;
  color: #4b5563;
  font-weight: 500;
  margin-bottom: 1.5rem;
  font-size: 1rem;
}

.loading {
  text-align: center;
  padding: 4rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading p {
  color: #6b7280;
  font-size: 1.125rem;
}

.error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: #dc2626;
  background: #fee2e2;
  border-radius: 8px;
  margin: 2rem;
}

.party-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  animation: fadeIn 0.5s ease-in;
}

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

.party-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  border-left: 4px solid;
  cursor: pointer;
}

.party-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

/* --- Party Card Selection Feedback --- */

.party-card.selected {
  /* Dynamic border color is set via inline style, so we use a fallback for the shadow border */
  box-shadow: 0 0 0 3px var(--border-left-color, #10b981), 0 8px 16px rgba(0, 0, 0, 0.15);
  transform: scale(1.02);
}

/* Style for unselected cards when limit is reached */
.party-card.selection-disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.party-card.selection-disabled:hover {
  transform: none; /* Disable hover lift for disabled cards */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.party-card.selected:hover {
  transform: scale(1.03) translateY(-4px);
}

.party-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 1.25rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.party-card h3 {
  margin: 0;
  font-size: 1rem;
  color: #1f2937;
  text-align: center;
  line-height: 1.4;
  word-wrap: break-word;
}

/* --- Comparison Panel Styles --- */
.comparison-panel {
  background: #ffffff;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  min-height: 200px; /* Give it some space */
}

.panel-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1.5rem; /* More space */
  border-bottom: 1px dashed #e5e7eb;
  padding-bottom: 1rem; /* More space */
}

.default-state {
  text-align: center;
  padding: 2rem;
  color: #6b7280;
  font-style: italic;
  background: #f9fafb;
  border-radius: 8px;
}

.comparison-charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

/* --- Media Queries --- */

@media (max-width: 1024px) {
  .party-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .parties-view {
    padding: 1rem;
  }

  h1 {
    font-size: 2rem;
  }

  .party-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
  }

  /* Stack charts on mobile */
  .comparison-charts-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 1.75rem;
  }

  .party-grid {
    grid-template-columns: 1fr;
  }
}
</style>

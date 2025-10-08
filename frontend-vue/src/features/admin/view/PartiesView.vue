<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { partyService, getPartyColor } from '../service/partyService';

interface PoliticalParty {
  registeredAppellation: string;
}

const parties = ref<PoliticalParty[]>([]);
const loading = ref(true);
const error = ref(false);

const loadParties = async () => {
  try {
    parties.value = await partyService.getParties('TK2023');
  } catch (err) {
    error.value = true;
    console.error('Error loading parties:', err);
  } finally {
    loading.value = false;
  }
};

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

onMounted(() => {
  loadParties();
});
</script>

<template>
  <div class="parties-view">
    <h1>Politieke Partijen</h1>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Loading parties...</p>
    </div>

    <div v-else-if="error" class="error">
      Failed to load parties. Please try again.
    </div>

    <div v-else>
      <p class="party-count">{{ parties.length }} parties registered</p>

      <div class="party-grid">
        <div
          v-for="party in parties"
          :key="party.registeredAppellation"
          class="party-card"
          :style="{ borderLeftColor: getPartyColor(party.registeredAppellation) }"
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

  .party-icon {
    width: 60px;
    height: 60px;
    font-size: 1.125rem;
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

{
type: created file
fileName: src/views/HomeView.vue
content:
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getDashboardStats } from '@/features/admin/service/admin-api';

// State for the total votes counter
const totalVotes = ref<string>('...');
const loading = ref(true);

// Icons for the dashboard cards (using simple emoji or text for now, can be replaced with SVGs)
const cards = [
  {
    title: 'Landelijke Uitslag',
    description: 'Bekijk de zetelverdeling en resultaten op nationaal niveau.',
    link: '/NationalElectionResults',
    icon: '📊',
    color: 'bg-blue-50 text-blue-700',
    borderColor: 'border-blue-200'
  },
  {
    title: 'Gemeente Uitslagen',
    description: 'Zoom in op de resultaten per gemeente en zie lokale trends.',
    link: '/municipality-results',
    icon: 'api', // Material icon name or text
    color: 'bg-green-50 text-green-700',
    borderColor: 'border-green-200'
  },
  {
    title: 'Kandidaten',
    description: 'Zoek specifieke kandidaten op in onze uitgebreide database.',
    link: '/candidates',
    icon: 'people',
    color: 'bg-purple-50 text-purple-700',
    borderColor: 'border-purple-200'
  },
  {
    title: 'Partijen',
    description: 'Overzicht van alle deelnemende politieke partijen.',
    link: '/parties',
    icon: 'flag',
    color: 'bg-orange-50 text-orange-700',
    borderColor: 'border-orange-200'
  }
];

/**
 * Fetch basic stats on mount to make the page feel "live".
 * We reuse the admin API here because it already calculates the total votes.
 */
onMounted(async () => {
  try {
    const stats = await getDashboardStats();
    // Find the stat with id 1 (Total Votes) as defined in AdminService.java
    const voteStat = stats.find(s => s.id === 1);
    if (voteStat) {
      totalVotes.value = voteStat.value;
    } else {
      totalVotes.value = '-';
    }
  } catch (error) {
    console.error('Failed to load homepage stats', error);
    totalVotes.value = 'Onbekend';
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="min-h-screen bg-gray-50 font-sans">
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 lg:py-28 text-center">
        <h1 class="text-4xl tracking-tight font-extrabold text-gray-900 sm:text-5xl md:text-6xl">
          <span class="block xl:inline">Verkiezingsuitslagen</span>
          <span class="block text-blue-600 xl:inline">TK2023</span>
        </h1>
        <p class="mt-3 max-w-md mx-auto text-base text-gray-500 sm:text-lg md:mt-5 md:text-xl md:max-w-3xl">
          Transparant inzicht in de Nederlandse democratie. Bekijk resultaten van landelijk tot gemeentelijk niveau.
        </p>

        <div class="mt-8 flex justify-center items-center space-x-2 animate-fade-in-up">
          <span class="inline-flex items-center px-3 py-0.5 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
            Live Teller
          </span>
          <p class="text-gray-700 font-medium">
            <span v-if="loading" class="animate-pulse">Data laden...</span>
            <span v-else class="font-bold text-gray-900">{{ totalVotes }}</span> stemmen geteld
          </p>
        </div>

        <div class="mt-10 max-w-sm mx-auto sm:max-w-none sm:flex sm:justify-center gap-4">
          <router-link
            to="/NationalElectionResults"
            class="flex items-center justify-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 md:py-4 md:text-lg transition shadow-md"
          >
            Bekijk Uitslagen
          </router-link>
          <router-link
            to="/login"
            class="flex items-center justify-center px-8 py-3 border border-gray-300 text-base font-medium rounded-md text-blue-700 bg-white hover:bg-gray-50 md:py-4 md:text-lg transition shadow-sm"
          >
            Inloggen
          </router-link>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <h2 class="text-2xl font-bold text-gray-900 mb-8 text-center">Ontdek de data</h2>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <router-link
          v-for="card in cards"
          :key="card.title"
          :to="card.link"
          class="group block bg-white rounded-xl p-6 border transition-all duration-200 hover:shadow-lg hover:-translate-y-1"
          :class="[card.borderColor]"
        >
          <div
            class="w-12 h-12 rounded-lg flex items-center justify-center mb-4 text-2xl transition-colors"
            :class="[card.color]"
          >
            <span v-if="card.icon.length < 3">{{ card.icon }}</span>
            <i v-else class="material-icons">{{ card.icon }}</i>
          </div>

          <h3 class="text-lg font-bold text-gray-900 group-hover:text-blue-600 transition-colors">
            {{ card.title }}
          </h3>
          <p class="mt-2 text-sm text-gray-500 leading-relaxed">
            {{ card.description }}
          </p>

          <div class="mt-4 flex items-center text-sm font-medium text-blue-600 opacity-0 group-hover:opacity-100 transition-opacity">
            Ga naar pagina →
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Simple animation for the stats fade in */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in-up {
  animation: fadeInUp 0.8s ease-out forwards;
}
</style>
}

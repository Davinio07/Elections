<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-7xl mx-auto">

      <header class="mb-12 text-center">
        <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight sm:text-4xl">
          Provincies & Kieskringen
        </h1>
        <p class="mt-3 max-w-2xl mx-auto text-lg text-gray-500">
          Selecteer een provincie om de onderliggende kieskringen en gemeenten te verkennen.
        </p>
      </header>

      <div v-if="isLoading" class="flex flex-col items-center justify-center py-24">
        <div class="animate-spin rounded-full h-12 w-12 border-4 border-gray-200 border-t-blue-600"></div>
        <span class="mt-4 text-gray-500 font-medium">Provincies ophalen...</span>
      </div>

      <div v-else-if="error" class="max-w-lg mx-auto bg-white border-l-4 border-red-500 rounded-lg shadow-sm p-6">
        <div class="flex items-center">
          <div class="flex-shrink-0 text-red-500">
            <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
          </div>
          <div class="ml-3">
            <h3 class="text-sm font-medium text-red-800">Fout bij laden</h3>
            <div class="mt-1 text-sm text-red-700">{{ error.message }}</div>
          </div>
        </div>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        <div
          v-for="province in provinces"
          :key="province.province_id"
          class="bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-all duration-300 ease-in-out transform hover:-translate-y-1 flex flex-col overflow-hidden group"
          :class="{ 'ring-2 ring-blue-500 border-transparent': province.isOpen }"
        >
          <div
            @click="toggleProvince(province)"
            class="p-6 cursor-pointer bg-white transition-colors flex items-center justify-between relative overflow-hidden"
          >
            <div class="absolute inset-0 opacity-0 group-hover:opacity-5 transition-opacity" :class="getProvinceBg(province.province_id)"></div>

            <div class="flex items-center gap-4 relative z-10">
              <div class="w-1.5 h-10 rounded-full" :class="getProvinceColor(province.province_id)"></div>

              <div>
                <h2 class="text-lg font-bold text-gray-900 group-hover:text-blue-700 transition-colors">
                  {{ province.name }}
                </h2>

                <span
                  v-if="province.isOpen && province.kieskringen"
                  class="inline-flex items-center px-2 py-0.5 mt-1 rounded text-xs font-medium bg-blue-50 text-blue-700 animate-fade-in"
                >
                  {{ province.kieskringen.length }} kieskringen
                </span>

                <span
                  v-else
                  class="text-xs text-gray-400 mt-1 block font-medium group-hover:text-blue-500 transition-colors"
                >
                  {{ province.isOpen ? 'Laden...' : 'Klik om te bekijken' }}
                </span>
              </div>
            </div>

            <div
              class="w-9 h-9 rounded-full flex items-center justify-center transition-all duration-200"
              :class="province.isOpen ? 'bg-blue-100 text-blue-600 rotate-180' : 'bg-gray-50 text-gray-400 group-hover:bg-blue-50 group-hover:text-blue-500'"
            >
              <svg
                class="w-5 h-5 transition-transform duration-300"
                fill="none" viewBox="0 0 24 24" stroke="currentColor"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </div>
          </div>

          <transition
            name="collapse"
            @enter="startTransition"
            @after-enter="endTransition"
            @leave="startTransition"
            @after-leave="endTransition"
          >
            <div v-if="province.isOpen" class="border-t border-gray-100 bg-gray-50/50">

              <div v-if="province.isLoadingChildren" class="py-10 flex justify-center">
                <div class="flex items-center space-x-3 text-gray-400">
                  <div class="animate-spin rounded-full h-5 w-5 border-2 border-current border-t-transparent"></div>
                  <span class="text-sm">Kieskringen ophalen...</span>
                </div>
              </div>

              <ul v-else-if="province.kieskringen && province.kieskringen.length > 0" class="divide-y divide-gray-100">
                <li
                  v-for="kieskring in province.kieskringen"
                  :key="kieskring.id"
                  class="group/item"
                >
                  <div
                    class="px-6 py-4 hover:bg-white transition-colors bg-transparent"
                  >
                    <div class="flex items-center justify-between cursor-pointer" @click.stop="toggleKieskring(kieskring)">
                      <div class="flex items-center gap-3">
                        <div class="w-2 h-2 rounded-full bg-gray-300 group-hover/item:bg-blue-500 transition-colors shadow-sm"></div>
                        <span class="text-sm font-semibold text-gray-700 group-hover/item:text-gray-900">
                          {{ kieskring.name }}
                        </span>
                      </div>

                      <div class="flex items-center gap-3 opacity-0 group-hover/item:opacity-100 transition-opacity duration-200">
                        <button
                          @click.stop="goToResults(kieskring.name)"
                          class="text-xs font-medium text-gray-500 hover:text-blue-600 px-2 py-1 rounded hover:bg-blue-50 transition-colors"
                        >
                          Uitslag
                        </button>
                        <div class="h-3 w-px bg-gray-300"></div>
                        <button
                          @click.stop="goToParties(kieskring.name)"
                          class="text-xs font-medium text-gray-500 hover:text-green-600 px-2 py-1 rounded hover:bg-green-50 transition-colors"
                        >
                          Partijen
                        </button>
                      </div>
                    </div>

                    <transition
                      name="collapse"
                      @enter="startTransition"
                      @after-enter="endTransition"
                      @leave="startTransition"
                      @after-leave="endTransition"
                    >
                      <div v-if="kieskring.isOpen" class="mt-3 ml-5 pl-4 border-l-2 border-gray-200">
                        <div v-if="kieskring.isLoadingGemeentes" class="text-xs text-gray-400 py-2 italic pl-1">
                          Gemeenten laden...
                        </div>
                        <div v-else-if="kieskring.gemeentes && kieskring.gemeentes.length > 0" class="pt-1 pb-2">
                          <p class="text-[10px] uppercase tracking-widest text-gray-400 font-bold mb-2 pl-1">Gemeenten</p>
                          <div class="flex flex-wrap gap-2">
                            <button
                              v-for="gm in kieskring.gemeentes"
                              :key="gm.id"
                              @click.stop="goToMunicipalityParties(gm.name)"
                              class="px-3 py-1 bg-white border border-gray-200 text-gray-600 text-xs rounded-full shadow-sm hover:shadow hover:text-blue-600 hover:border-blue-200 transition-all active:scale-95"
                            >
                              {{ gm.name }}
                            </button>
                          </div>
                        </div>
                        <p v-else class="text-xs text-gray-400 italic py-2 pl-1">
                          Geen gemeenten gevonden.
                        </p>
                      </div>
                    </transition>
                  </div>
                </li>
              </ul>

              <p v-else class="text-center text-gray-400 italic py-8 text-sm">
                Geen data beschikbaar.
              </p>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  getProvinces,
  getConstituenciesForProvince,
  getMunicipalitiesForConstituency,
  type ProvinceDto,
  type KieskringDto,
  type GemeenteDto
} from "@/features/admin/service/ProvinceService";

const router = useRouter();

interface KieskringUI extends KieskringDto {
  isOpen: boolean;
  isLoadingGemeentes?: boolean;
  gemeentes?: GemeenteDto[];
}

interface ProvinceUI extends Omit<ProvinceDto, 'kieskringen'> {
  isOpen: boolean;
  kieskringen?: KieskringUI[];
}

const provinces = ref<ProvinceUI[]>([]);
const isLoading = ref<boolean>(true);
const error = ref<Error | null>(null);

function goToResults(kieskringName: string) {
  router.push({ path: '/kieskring-details', query: { name: kieskringName } });
}

function goToParties(kieskringName: string) {
  router.push({ path: '/parties', query: { level: 'Kieskringen', name: kieskringName } });
}

function goToMunicipalityParties(municipalityName: string) {
  router.push({ path: '/parties', query: { level: 'Gemeentes', name: municipalityName } });
}

onMounted(async () => {
  try {
    isLoading.value = true;
    const data = await getProvinces();
    provinces.value = data.map(p => ({ ...p, isOpen: false }));
  } catch (err) {
    console.error('Failed to load provinces:', err);
    error.value = err as Error;
  } finally {
    isLoading.value = false;
  }
});

async function toggleProvince(province: ProvinceUI) {
  province.isOpen = !province.isOpen;
  if (province.isOpen && !province.kieskringen) {
    try {
      province.isLoadingChildren = true;
      const results = await getConstituenciesForProvince(province.province_id);
      province.kieskringen = results.map(k => ({ ...k, isOpen: false }));
    } catch (err) {
      console.error(`Failed to load kieskringen for ${province.name}`, err);
    } finally {
      province.isLoadingChildren = false;
    }
  }
}

async function toggleKieskring(kieskring: KieskringUI) {
  kieskring.isOpen = !kieskring.isOpen;
  if (kieskring.isOpen && !kieskring.gemeentes) {
    try {
      kieskring.isLoadingGemeentes = true;
      const results = await getMunicipalitiesForConstituency(kieskring.id);
      kieskring.gemeentes = results;
    } catch (err) {
      console.error(`Failed to load gemeentes for ${kieskring.name}`, err);
    } finally {
      kieskring.isLoadingGemeentes = false;
    }
  }
}

const startTransition = (el: Element) => {
  const element = el as HTMLElement;
  element.style.height = '0';
  element.style.opacity = '0';
};

const endTransition = (el: Element) => {
  const element = el as HTMLElement;
  const height = element.scrollHeight;
  element.style.height = `${height}px`;
  element.style.opacity = '1';
  element.addEventListener('transitionend', () => {
    if (element.style.height !== '0px') element.style.height = 'auto';
  }, { once: true });
};

// Colors for the vertical bar
const getProvinceColor = (id: number) => {
  const colors = [
    'bg-red-500', 'bg-orange-500', 'bg-amber-500', 'bg-yellow-500',
    'bg-lime-500', 'bg-green-500', 'bg-emerald-500', 'bg-teal-500',
    'bg-cyan-500', 'bg-sky-500', 'bg-blue-500', 'bg-indigo-500'
  ];
  return colors[id % colors.length] || 'bg-gray-500';
};

// Subtle background hover effect matching the bar color
const getProvinceBg = (id: number) => {
  const colors = [
    'bg-red-500', 'bg-orange-500', 'bg-amber-500', 'bg-yellow-500',
    'bg-lime-500', 'bg-green-500', 'bg-emerald-500', 'bg-teal-500',
    'bg-cyan-500', 'bg-sky-500', 'bg-blue-500', 'bg-indigo-500'
  ];
  return colors[id % colors.length] || 'bg-gray-500';
};
</script>

<style scoped>
.collapse-enter-active,
.collapse-leave-active {
  transition: height 0.3s ease-in-out, opacity 0.3s ease-in-out;
  overflow: hidden;
}
.collapse-enter-from,
.collapse-leave-to {
  height: 0 !important;
  opacity: 0;
}

/* Fade in animation for the badge */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(2px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in {
  animation: fadeIn 0.3s ease-out forwards;
}
</style>

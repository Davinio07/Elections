<template>
  <div class="container mx-auto max-w-7xl p-6">
    <header class="mb-10 text-center">
      <h1 class="text-4xl font-extrabold text-gray-900 tracking-tight">Kieskringen & Gemeenten</h1>
      <p class="text-gray-500 mt-3 text-lg">Klik op een provincie en vervolgens op een kieskring om de gemeenten te zien.</p>
    </header>

    <div v-if="isLoading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <div v-else-if="error" class="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 rounded shadow-sm mx-auto max-w-2xl">
      <p class="font-bold">Foutmelding</p>
      <p>Kon de data niet laden: {{ error.message }}</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
      <div
        v-for="province in provinces"
        :key="province.province_id"
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 border border-gray-100 overflow-hidden flex flex-col"
        :class="{ 'ring-2 ring-blue-500 shadow-lg': province.isOpen }"
      >
        <div
          @click="toggleProvince(province)"
          class="p-6 cursor-pointer flex justify-between items-center bg-white hover:bg-gray-50 transition-colors min-h-[100px]"
        >
          <div class="flex items-center gap-4">
            <div
              class="w-12 h-12 rounded-full flex items-center justify-center text-white font-bold text-lg shadow-sm"
              :class="getProvinceColor(province.province_id)"
            >
              {{ province.name.substring(0, 2).toUpperCase() }}
            </div>
            <div>
              <h2 class="text-xl font-bold text-gray-800">{{ province.name }}</h2>
              <p class="text-sm text-gray-500" v-if="province.kieskringen">
                {{ province.kieskringen.length }} kieskringen
              </p>
            </div>
          </div>

          <div
            class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center transition-transform duration-300"
            :class="{ 'rotate-180 bg-blue-100 text-blue-600': province.isOpen }"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
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
          <div v-if="province.isOpen" class="bg-gray-50 border-t border-gray-100">
            <div class="p-4">
              <div v-if="province.isLoadingChildren" class="py-6 text-center text-gray-500 flex flex-col items-center">
                <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-500 mb-2"></div>
                <span class="text-sm">Kieskringen ophalen...</span>
              </div>

              <ul v-else-if="province.kieskringen && province.kieskringen.length > 0" class="space-y-3">
                <li
                  v-for="kieskring in province.kieskringen"
                  :key="kieskring.kieskring_id"
                  class="bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm transition-all"
                  :class="{ 'border-blue-300 ring-1 ring-blue-100': kieskring.isOpen }"
                >
                  <div
                    @click.stop="toggleKieskring(kieskring)"
                    class="flex justify-between items-center p-3 hover:bg-blue-50 cursor-pointer transition-colors"
                  >
                    <div class="flex items-center gap-3">
                      <span class="w-2 h-2 rounded-full bg-blue-400"></span>
                      <span class="font-medium text-gray-800">{{ kieskring.name }}</span>
                    </div>

                    <div class="flex items-center gap-2">
                      <button
                        @click.stop="goToResults(kieskring.name)"
                        class="px-3 py-1 text-xs font-medium text-blue-700 bg-blue-50 hover:bg-blue-100 border border-blue-200 rounded-md transition-colors flex items-center gap-1"
                      >
                        Kieskring results
                      </button>

                      <button
                        @click.stop="goToParties(kieskring.name)"
                        class="px-3 py-1 text-xs font-medium text-green-700 bg-green-50 hover:bg-green-100 border border-green-200 rounded-md transition-colors flex items-center gap-1"
                      >
                        Partijen
                      </button>

                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
                        class="w-4 h-4 text-gray-400 transition-transform duration-200"
                        :class="{ 'rotate-180 text-blue-500': kieskring.isOpen }"
                      >
                        <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
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
                    <div v-if="kieskring.isOpen" class="bg-blue-50/50 border-t border-gray-100 p-4">
                      <div v-if="kieskring.isLoadingGemeentes" class="text-xs text-center text-gray-500 py-2">
                        Gemeenten laden...
                      </div>
                      <div v-else-if="kieskring.gemeentes && kieskring.gemeentes.length > 0">
                        <p class="text-xs font-semibold text-gray-500 uppercase mb-2 tracking-wide">
                          {{ kieskring.gemeentes.length }} Gemeenten:
                        </p>
                        <div class="flex flex-wrap gap-2">
                          <span
                            v-for="gm in kieskring.gemeentes"
                            :key="gm.id"
                            class="px-3 py-1 bg-white border border-blue-100 text-blue-900 text-xs rounded-full shadow-sm hover:shadow hover:border-blue-300 transition-all cursor-default select-none"
                          >
                            {{ gm.name }}
                          </span>
                        </div>
                      </div>
                      <p v-else class="text-xs text-gray-500 italic text-center">
                        Geen gemeenten gevonden.
                      </p>
                    </div>
                  </transition>
                </li>
              </ul>

              <p v-else class="text-center text-gray-500 italic py-4 text-sm">
                Geen kieskringen gevonden.
              </p>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  getProvinces,
  getKieskringenForProvince,
  getGemeentesForKieskring,
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

// Navigate to kieskring details
function goToResults(kieskringName: string) {
  router.push({
    path: '/kieskring-details',
    query: { name: kieskringName }
  });
}

// NEW: Navigate to parties view with pre-selected kieskring
function goToParties(kieskringName: string) {
  router.push({
    path: '/parties',
    query: { level: 'Kieskringen', name: kieskringName }
  });
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
      const results = await getKieskringenForProvince(province.province_id);
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
      const results = await getGemeentesForKieskring(kieskring.kieskring_id);
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

const getProvinceColor = (id: number) => {
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
</style>

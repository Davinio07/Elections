<script setup lang="ts">
import { ref } from 'vue';
import {getProvinces} from "@/features/admin/service/ScaledElectionResults_api.ts";
interface Region {
  id: string | null;
  name: string;
  category: string | null;
  superiorCategory: string | null;
}

const regions = ref<Region[]>([]);
const selectedRegion = ref<Region | null>(null);

async function fetchRegions(electionId: string) {
  try {
    regions.value = await getProvinces(electionId);
    selectedRegion.value = null; // reset selection
  } catch (error) {
    console.error(error);
  }
}

function selectRegion(region: Region) {
  selectedRegion.value = region;
}
</script>

<template>
  <h1>Regio's van de verkiezing</h1>

  <!-- Buttons for different elections -->
  <button @click="() => fetchRegions('TK2023')">TK2023</button>
  <button @click="() => fetchRegions('TK2024')">TK2024</button>

  <!-- List of region names -->
  <ul v-if="regions.length">
    <li
      v-for="region in regions"
      :key="region.name"
      @click="selectRegion(region)"
      style="cursor: pointer; margin-bottom: 4px;"
    >
      {{ region.name }}
    </li>
  </ul>

  <!-- Show details when a region is selected -->
  <div v-if="selectedRegion" style="margin-top: 20px; padding: 10px; border: 1px solid #ccc; border-radius: 6px;">
    <h2>Details voor {{ selectedRegion.name }}</h2>
    <p><strong>ID:</strong> {{ selectedRegion.id || 'N/A' }}</p>
    <p><strong>Category:</strong> {{ selectedRegion.category || 'N/A' }}</p>
    <p><strong>Superior Category:</strong> {{ selectedRegion.superiorCategory || 'N/A' }}</p>
  </div>
</template>

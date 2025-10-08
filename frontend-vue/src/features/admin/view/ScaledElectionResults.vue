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

  <button @click="() => fetchRegions('TK2023')">TK2023</button>
  <button @click="() => fetchRegions('TK2024')">TK2024</button>

  <!-- Grid of boxes -->
  <div class="grid">
    <div
      v-for="region in regions"
      :key="region.name"
      class="region-box"
      @click="selectRegion(region)"
      :class="{ selected: selectedRegion?.name === region.name }"
    >
      <h3>{{ region.name }}</h3>
    </div>
  </div>

  <!-- Show selected region details below -->
  <div v-if="selectedRegion" class="details-box">
    <h2>Details voor {{ selectedRegion.name }}</h2>
    <p><strong>ID:</strong> {{ selectedRegion.id}}</p>
    <p><strong>Category:</strong> {{ selectedRegion.category}}</p>
    <p><strong>Superior Category:</strong> {{ selectedRegion.superiorCategory }}</p>
  </div>
</template>

<style scoped>
button {
  margin: 0 8px 16px 0;
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #2980b9;
}

.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.region-box {
  flex: 1 0 150px; /* grow/shrink, minimum width */
  background: #f5f5f5;
  padding: 16px;
  border-radius: 6px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.region-box:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.region-box.selected {
  border: 2px solid #3498db;
  background: #eaf4fc;
}

.details-box {
  margin-top: 24px;
  padding: 16px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background: #fafafa;
}
</style>

<script setup lang="ts">
import { ref } from 'vue';
import { getProvinces } from "@/features/admin/service/ScaledElectionResults_api.ts";

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
    selectedRegion.value = null;
  } catch (error) {
    console.error(error);
  }
}

function selectRegion(region: Region) {
  selectedRegion.value = region;
}
</script>

<template>
  <h1>Provinciale Verkiezingen (Kieskringen)</h1>

  <div class="buttons">
    <button @click="() => fetchRegions('TK2023')">Verkiezingen 2023</button>
    <button @click="() => fetchRegions('TK2024')">Verkiezingen 2024</button>
  </div>

  <div class="grid">
    <div
      v-for="region in regions"
      :key="region.id || region.name"
      class="region-box"
      @click="selectRegion(region)"
      :class="{ selected: selectedRegion?.name === region.name }"
    >
      {{ region.name }}
    </div>
  </div>

  <div v-if="selectedRegion" class="details-box">
    <h2>{{ selectedRegion.name }}</h2>
    <p>ID: {{ selectedRegion.id }}</p>
    <p>Category: {{ selectedRegion.category }}</p>
    <p>Superior: {{ selectedRegion.superiorCategory }}</p>
  </div>
</template>

<style scoped>
h1 {
  margin-bottom: 16px;
  font-weight: 500;
}

.buttons {
  margin-bottom: 16px;
}

button {
  margin-right: 8px;
  padding: 6px 14px;
  border: none;
  border-radius: 4px;
  background: #3498db;
  color: white;
  cursor: pointer;
}
button:hover {
  background: #2980b9;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
}

.region-box {
  padding: 20px;
  background: #f5f5f5;
  text-align: center;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.region-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.region-box.selected {
  border: 2px solid #3498db;
  background: #eaf4fc;
}

.details-box {
  margin-top: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #ddd;
}
</style>

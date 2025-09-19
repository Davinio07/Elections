<script setup lang="ts">
import { ref } from 'vue';
import {ScaledElectionResults} from "@/features/admin/service/ScaledElectionResults_api.ts";
const results = ref<string>('');

async function testBackend() {
  results.value = 'Ophalen...';
  results.value = await ScaledElectionResults();
}
</script>



<template>
  <h1> Dit is een voorbeeld:</h1>
  <button @click="testBackend"> Yooo </button>

  <div v-if="results">
    <strong>Resultaat:</strong> {{ results }}
  </div>
</template>

<style scoped>

</style>

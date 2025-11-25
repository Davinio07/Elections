<script setup lang="ts">
import { useNationalResult } from '@/features/admin/composables/useNationalResult.ts'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale
} from 'chart.js'

// Register Chart components
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

// Initialize the logic
const {
  availableElections,
  selectedElection,
  loading,
  error,
  totalSeats,
  majorityThreshold,
  partyCount,
  largestPartyData,
  largestPartyPercentage,
  chartData,
  chartOptions
} = useNationalResult()
</script>

<template>
  <div class="p-6 bg-gray-200 rounded min-h-screen">
    <h1 class="text-3xl font-bold text-gray-800 mb-6 border-b pb-2">Nationale Verkiezingsresultaten</h1>

    <div class="mb-6 flex gap-3 items-center">
      <label for="election-selector" class="font-medium text-gray-700">Selecteer Verkiezing:</label>
      <select
        id="election-selector"
        v-model="selectedElection"
        :disabled="loading"
        class="px-5 py-2 bg-white border border-gray-300 rounded-lg shadow-sm focus:ring-indigo-500 focus:border-indigo-500 transition duration-150 cursor-pointer"
      >
        <option
          v-for="electionId in availableElections"
          :key="electionId"
          :value="electionId"
        >
          {{ electionId }}
        </option>
      </select>

      <span v-if="loading" class="text-indigo-600 ml-3 flex items-center">
        Bezig met laden...
        <div class="animate-spin inline-block w-4 h-4 border-[2px] border-current border-t-transparent text-indigo-600 rounded-full ml-1" role="status"></div>
      </span>
    </div>

    <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mt-8" role="alert">
      <strong class="font-bold">Fout:</strong>
      <span class="block sm:inline">{{ error }}</span>
    </div>

    <div v-if="chartData.labels && chartData.labels.length && !loading" class="w-full mx-auto mt-8">
      <div class="flex flex-wrap justify-center items-stretch mb-8 bg-white p-6 rounded-xl border border-gray-100 text-center">
        <div class="flex-1 min-w-[200px] p-4 border-b md:border-b-0 md:border-r border-gray-200 flex flex-col justify-center items-center">
          <span class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-1">Totaal Zetels</span>
          <span class="text-4xl font-bold">{{ totalSeats }}</span>
        </div>

        <div class="flex-1 min-w-[200px] p-4 border-b md:border-b-0 md:border-r border-gray-200 flex flex-col justify-center items-center">
          <span class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-1">Meerderheid</span>
          <span class="text-4xl font-bold">{{ majorityThreshold }}</span>
        </div>

        <div class="flex-1 min-w-[200px] p-4 border-b md:border-b-0 md:border-r border-gray-200 flex flex-col justify-center items-center">
          <span class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-1">Aantal Partijen</span>
          <span class="text-4xl font-bold">{{ partyCount }}</span>
        </div>

        <div class="flex-1 min-w-[200px] p-4 flex flex-col justify-center items-center">
          <span class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-1">Grootste Partij</span>
          <div class="text-center text-xl sm:text-2xl font-bold text-gray-900 leading-tight">
            <span class="block text-xl font-bold">{{ largestPartyData.name }}</span>
            <span class="block text-2xl font-bold border-t border-gray-100 mt-1 pt-1">{{ largestPartyData.seats }} zetels</span>
            <span class="block text-sm font-medium text-gray-500">{{ largestPartyPercentage }}</span>
          </div>
        </div>
      </div>

      <div class="w-full mx-auto bg-white p-6 rounded-xl h-[1200px]">
        <h2 class="text-xl font-semibold text-gray-700 mb-4 text-center">Resultaten voor {{ selectedElection }}</h2>
        <Bar
          :data="chartData"
          :options="chartOptions"
          class="h-full"
        />
      </div>
    </div>

    <div v-else-if="!loading && !error" class="mt-8 text-gray-500 text-lg">
      Selecteer een verkiezing en bekijk de data.
    </div>
  </div>
</template>

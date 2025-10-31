<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue'
import { getNationalResults, getNationalSeats } from '@/features/admin/service/NationalElectionResults_api.ts'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
  type TooltipItem, type ChartOptions,
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

type NationalResult = {
  partyName: string
  validVotes: number
}

type NationalSeats = {
  [partyName: string]: number
}

// New State for Election Selection
const availableElections = ref(['TK2023', 'TK2021'])
const selectedElection = ref(availableElections.value[0]) // Default to the first (latest) one

const nationalResults = ref<NationalResult[]>([])
const nationalSeats = ref<NationalSeats>({})
const loading = ref(false)
const error = ref<string | null>(null)

/**
 * Fetches the election data from the API.
 *
 * @param electionId the selected election year
 */
async function fetchElectionData(electionId: string) {
  if (!electionId) return

  loading.value = true
  error.value = null
  nationalResults.value = []
  nationalSeats.value = {}

  try {
    const results = await getNationalResults(electionId)
    const seats = await getNationalSeats(electionId)

    // Type casting the results, assuming the API returns the expected types
    nationalResults.value = (results as NationalResult[]).filter(r => r.validVotes > 0) || []
    nationalSeats.value = (seats as NationalSeats) || {}
  } catch (err) {
    // Check if err has a message property, otherwise provide a fallback
    error.value = (err instanceof Error) ? err.message : ' Er is een onbekende fout opgetreden bij het ophalen van de data.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchElectionData(selectedElection.value)
})

watch(selectedElection, (newElectionId) => {
  fetchElectionData(newElectionId)
})

/**
 * Calculates the total number of seats distributed across all parties.
 * For the Dutch House of Representatives, this should typically be 150.
 *
 * @returns {number} The total number of seats (sum of all seat counts).
 */
const totalSeats = computed<number>(() => {
  return Object.values(nationalSeats.value).reduce((sum, seats) => sum + seats, 0)
})

/**
 * Calculates the absolute majority threshold.
 * This is defined as half of the total seats plus one ($\lfloor \text{TotalSeats}/2 \rfloor + 1$).
 *
 * @returns {number} The minimum number of seats required for an absolute majority (e.g., 76 for 150 seats).
 */
const majorityThreshold = computed<number>(() => {
  if (totalSeats.value === 0) return 0
  return Math.floor(totalSeats.value / 2) + 1
})

/**
 * Counts the number of parties that have won at least one seat in the election.
 *
 * @returns {number} The count of unique parties represented by seats.
 */
const partyCount = computed<number>(() => {
  return Object.keys(nationalSeats.value).length
})

// ... (bestaande code)

/**
 * Helper function to determine the largest party and its data.
 *
 * @returns {{ name: string, seats: number, totalSeats: number }} The largest party's data, or default values.
 */
function getLargestPartyData() {
  const tSeats = totalSeats.value
  if (tSeats === 0 || Object.keys(nationalSeats.value).length === 0) {
    return { name: 'Geen data', seats: 0, totalSeats: 0 }
  }

  let maxSeats = -1
  let largestPartyName = ''

  for (const [partyName, seats] of Object.entries(nationalSeats.value)) {
    if (seats > maxSeats) {
      maxSeats = seats
      largestPartyName = partyName
    }
  }

  return { name: largestPartyName, seats: maxSeats, totalSeats: tSeats }
}

/**
 * Returns the name of the party with the most seats.
 *
 * @returns {string} The name of the largest party.
 */
const largestPartyNameDisplay = computed<string>(() => {
  const data = getLargestPartyData()
  return data.name
})

/**
 * Returns the seat count of the party with the most seats.
 *
 * @returns {string} The number of seats, e.g., '37'.
 */
const largestPartySeatsDisplay = computed<string>(() => {
  const data = getLargestPartyData()
  return data.seats > 0 ? `${data.seats} zetels` : '0 zetels'
})

/**
 * Returns the percentage share of the largest party's seats.
 *
 * @returns {string} The percentage, e.g., '(24.67%)'.
 */
const largestPartyPercentageDisplay = computed<string>(() => {
  const data = getLargestPartyData()
  if (data.totalSeats === 0) return '(0.00%)'

  const fraction = (data.seats / data.totalSeats) * 100
  return `(${fraction.toFixed(2)}%)`
})



// Chart configuration
const chartData = computed(() => {
  if (!nationalResults.value.length) return { labels: [], datasets: [] };

  const totalVotes = nationalResults.value.reduce((sum, r) => sum + r.validVotes, 0)

  // Sort results by votes descending
  const sortedResults = [...nationalResults.value].sort((a, b) => b.validVotes - a.validVotes);

  const labels = sortedResults.map(r => r.partyName)

  // Calculate percentage of valid votes for each party
  const percentages = sortedResults.map(r =>
    totalVotes > 0 ? parseFloat(((r.validVotes / totalVotes) * 100).toFixed(2)) : 0
  )

  // Get vote and seat count for each party
  const votes = sortedResults.map(r => r.validVotes);
  const seats = labels.map(party => nationalSeats.value[party] || 0)

  // Define a set of 41 unique, highly contrasting colors.
  const backgroundColors = [
    '#FF6347', '#4682B4', '#3CB371', '#FFD700', '#DA70D6',
    '#00CED1', '#FFA07A', '#9370DB', '#8B0000', '#00BFFF',
    '#A0522D', '#7FFF00', '#BA55D3', '#BDB76B', '#1E90FF',
    '#FF4500', '#20B2AA', '#800000', '#00FF00', '#DC143C',
    '#008080', '#D2691E', '#48D1CC', '#8B4513', '#6A5ACD',
    '#F08080', '#006400', '#C71585', '#556B2F', '#9932CC',
    '#A9A9A9', '#FF8C00', '#B0E0E6', '#CD5C5C', '#696969',
    '#2F4F4F', '#000080', '#FFB6C1', '#8FBC8F', '#4169E1',
    '#B8860B'
  ];

  // Map parties to colors cyclically (If more than 41 parties, colors will repeat)
  const partyColors = labels.map((_, index) => backgroundColors[index % backgroundColors.length]);

  return {
    labels: labels,
    datasets: [
      {
        label: 'Stempercentage',
        data: percentages,
        backgroundColor: partyColors,
        borderColor: '#ffffff',
        borderWidth: 1,
        votesData: votes,
        seatsData: seats
      }
    ]
  }
})

// Chart options - Customized for a Horizontal Bar Chart
const chartOptions: ChartOptions<'bar'> = {
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'y' as const,
  plugins: {
    legend: {
      display: false,
    },
    title: {
      display: true,
      text: `Nationale verkiezingsresultaten: Stempercentage & Zetels (${selectedElection.value})`,
      font: {
        size: 18
      }
    },
    tooltip: {
      callbacks: {
        // Custom label to display both percentage and seats. Context is now strongly typed.
        label: function(context: TooltipItem<'bar'>) {
          const dataset = context.dataset as unknown as { votesData: number[], seatsData: number[] };
          const votes = dataset.votesData[context.dataIndex];
          const seats = dataset.seatsData[context.dataIndex];
          const percentage = context.formattedValue;

          const formattedVotes = votes.toLocaleString('nl-NL');

          return [
            ` Stempercentage: ${percentage}%`,
            ` Totaal Stemmen: ${formattedVotes}`,
            ` Aantal Zetels: ${seats}`
          ];
        },
        // Title callback context is an array of TooltipItems
        title: function(context: TooltipItem<'bar'>[]) {
          return context[0].label;
        }
      }
    }
  },
  scales: {
    x: {
      stacked: false,
      title: {
        display: true,
        text: 'Stempercentage (%)'
      },
      ticks: {
        //
        callback: function(value) {
          return value + '%';
        }
      }
    },
    y: {
      stacked: false
    }
  }
}
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
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

    <div v-if="nationalResults.length && !loading" class="w-full mx-auto mt-8">
      <div
        class="flex flex-wrap justify-center items-stretch mb-8 bg-white p-6 rounded-xl shadow-2xl border border-gray-100 text-center"
      >
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
            <span class="block text-xl font-bold">{{ largestPartyNameDisplay }}</span>
            <span class="block text-2xl font-bold border-t border-gray-100 mt-1 pt-1">{{ largestPartySeatsDisplay }}</span>
            <span class="block text-sm font-medium text-gray-500">{{ largestPartyPercentageDisplay }}</span>
          </div>
        </div>
      </div>

      <div class="w-full mx-auto bg-white p-6 rounded-xl shadow-2xl h-[1200px]">
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

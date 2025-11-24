<script setup lang="ts">
defineProps<{
  modelValue: string;
  options: string[];
}>();

const emit = defineEmits(['update:modelValue']);
</script>

<template>
  <div class="flex items-center justify-center p-1 bg-gray-200 rounded-lg">
    <button
      v-for="option in options"
      :key="option"
      class="flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors duration-200 ease-in-out focus:outline-none"
      :class="[
        modelValue === option
          ? 'bg-white text-blue-700 shadow'
          : 'text-gray-600 hover:bg-gray-100'
      ]"
      @click="emit('update:modelValue', option)"
    >
      {{ option }}
    </button>
  </div>
</template>

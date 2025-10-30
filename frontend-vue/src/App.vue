<template>
  <div id="app" class="min-h-screen flex flex-col">
    <header class="bg-white shadow sticky top-0 z-50">
      <div class="max-w-7xl mx-auto flex items-center justify-between h-16 px-6">
        <h1 class="text-xl font-semibold text-gray-800">Verkiezingen TK2023</h1>
        <nav class="flex space-x-6">
          <router-link
            to="/admin"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Admin</router-link>

          <router-link
            to="/municipality-results"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Gemeenten</router-link>

          <router-link
            to="/NationalElectionResults"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Landelijk</router-link>

          <router-link
            to="/candidates"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Kandidaten</router-link>

          <router-link
            to="/parties"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Partijen</router-link>

          <router-link
            to="/ScaledElectionResults"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Provinciaal</router-link>

          <router-link
            to="/kieskring-details"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Kieskring Details</router-link>

          <router-link
            to="/register"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Registreer</router-link>

          <router-link
            to="/login"
            class="text-gray-700 font-medium hover:text-blue-600 transition"
            active-class="text-blue-600 border-b-2 border-blue-600 pb-0.5"
          >Login</router-link>

        </nav>
      </div>
    </header>

    <main class="flex-1 max-w-7xl mx-auto w-full p-6">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
</script>

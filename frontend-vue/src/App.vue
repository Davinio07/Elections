<template>
  <div id="app">
    <header class="app-header">
      <div class="header-content">
        <h1 class="app-title">Verkiezingen TK2023</h1>
        <nav class="main-nav">
          <router-link to="/admin">Admin</router-link>
          <router-link to="/municipality-results">Gemeenten</router-link>
          <router-link to="/NationalElectionResults">Landelijk</router-link>
          <router-link to="/candidates">Kandidaten</router-link>
          <router-link to="/parties">ga naar partijen</router-link>
          <router-link to="/ScaledElectionResults">Provinciaal</router-link>
          <router-link to="/register">Registreer</router-link>
          <router-link to="/kieskring-details">Kieskring Details</router-link>
        </nav>
      </div>
    </header>
    <main class="main-content">
      <router-view />
    </main>
  </div>
  <div class="p-8 bg-gray-900 min-h-screen flex items-center justify-center">
    <h1 class="text-4xl font-bold text-white bg-blue-600 px-6 py-3 rounded-lg shadow-lg">
      🚀 Tailwind is Working!
    </h1>
  </div>
</template>


<style>
/* Globale Stylin voor je hele applicatie */
:root {
  --primary-color: #007bff;
  --secondary-color: #f8f9fa;
  --border-color: #dee2e6;
  --text-color: #212529;
  --header-bg: #ffffff;
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
}

body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  margin: 0;
  background-color: #f8f9fa;
  color: var(--text-color);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Header & Navigatie */
.app-header {
  background-color: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  padding: 0 2rem;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1280px;
  margin: 0 auto;
  height: 64px;
}

.app-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-color);
}

.main-nav {
  display: flex;
  gap: 1.5rem;
}

.main-nav a {
  text-decoration: none;
  font-weight: 500;
  color: var(--text-color);
  padding: 0.5rem 0;
  border-bottom: 2px solid transparent;
  transition: all 0.2s ease-in-out;
}

.main-nav a:hover {
  color: var(--primary-color);
}

.main-nav a.router-link-exact-active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

/* Hoofdinhoud van de pagina */
.main-content {
  flex: 1;
  max-width: 1280px;
  width: 100%;
  margin: 0 auto;
  padding: 2rem;
  box-sizing: border-box;
}

/* Algemene styling voor tabellen (werkt op al je pagina's) */
.results-table {
  width: 100%;
  border-collapse: collapse;
  background-color: #fff;
  box-shadow: var(--shadow-md);
  border-radius: 8px;
  overflow: hidden;
}

.results-table th,
.results-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.results-table thead th {
  background-color: var(--secondary-color);
  font-weight: 600;
  font-size: 0.875rem;
  text-transform: uppercase;
  color: #6c757d;
}

.results-table tbody tr:last-child td {
  border-bottom: none;
}

.results-table tbody tr:hover {
  background-color: #f1f3f5;
}

.error {
  color: #dc3545;
  background-color: #f8d7da;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #f5c6cb;
}
</style>
<script setup lang="ts">
</script>

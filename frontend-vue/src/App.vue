<template>
  <div id="app">
    <header>
      <nav>
        <router-link to="/admin">Ga naar Admin</router-link>
        <router-link to="/ScaledElectionResults">Ga naar de provinciale verkiezingsresultaten</router-link>
        <router-link to="/NationalElectionResults">Ga naar de landelijke verkiezingsresultaten</router-link>
        </nav>
    </header>
    <main>
      <router-view />
    </main>
  </div>
</template>

<style>
nav {
  padding: 1rem;
  background-color: #f1f5f9;
  border-bottom: 1px solid #cbd5e1;
}
nav a {
  margin-right: 1rem;
  font-weight: bold;
}
</style>

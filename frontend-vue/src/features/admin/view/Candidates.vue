<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getCandidates } from '@/features/admin/service/ScaledElectionResults_api.ts';

interface Candidate {
  id?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  initials?: string | null;
  prefix?: string | null;
  gender?: string | null;
  locality?: string | null;
  listNumber?: string | null;
  listName?: string | null;
  numberOnList?: string | null;
}

const electionId = ref<'TK2023' | 'TK2024'>('TK2023');
const loading = ref(false);
const error = ref('');
const candidates = ref<Candidate[]>([]);

function folderFor(): string | undefined {
  return undefined;
}

function displayName(c: Candidate): string {
  const firstOrInitials = (c.firstName?.trim() || c.initials?.trim() || '').trim();
  const prefix = c.prefix && c.prefix.trim() ? ` ${c.prefix.trim()}` : '';
  const last = c.lastName?.trim() || '';
  const name = `${firstOrInitials}${prefix} ${last}`.trim();
  return name || c.id || 'Onbekende kandidaat';
}

function candidateKey(c: Candidate, i: number): string {
  const parts = [
    c.id ?? '',
    c.listNumber ?? '',
    c.numberOnList ?? '',
    c.lastName ?? '',
    c.firstName ?? ''
  ].filter(Boolean);
  return parts.length ? parts.join('|') : `idx-${i}`;
}

async function load() {
  loading.value = true;
  error.value = '';
  candidates.value = [];
  try {
    candidates.value = await getCandidates(electionId.value, folderFor());
    if (!candidates.value.length) error.value = 'Geen kandidaten gevonden.';
  } catch (e) {
    error.value = 'Fout bij het ophalen van kandidaten.';
  } finally {
    loading.value = false;
  }
}

function onElectionChange() {
  load();
}

onMounted(load);
</script>

<template>
  <section class="page">
    <header class="bar">
      <h1>Kandidaten</h1>
      <div class="controls">
        <label>
          Verkiezing:
          <select v-model="electionId" @change="onElectionChange">
            <option value="TK2023">TK2023</option>
            <option value="TK2024">TK2024</option>
          </select>
        </label>
        <button @click="load" :disabled="loading">Ophalen</button>
      </div>
    </header>

    <div v-if="loading" class="state">Bezig met laden…</div>
    <div v-else-if="error" class="state error">{{ error }}</div>

    <div v-else class="grid">
      <article v-for="(c, i) in candidates" :key="candidateKey(c, i)" class="card">
        <div class="avatar" aria-hidden="true">
          {{ (displayName(c).charAt(0) || '?').toUpperCase() }}
        </div>

        <h2 class="name">{{ displayName(c) }}</h2>

        <ul class="meta">
          <li v-if="c.listName"><strong>Lijst:</strong> {{ c.listName }}</li>
          <li v-if="c.numberOnList"><strong>Plaats op lijst:</strong> {{ c.numberOnList }}</li>
          <li v-if="c.listNumber"><strong>Lijstnummer:</strong> {{ c.listNumber }}</li>
          <li v-if="c.locality"><strong>Woonplaats:</strong> {{ c.locality }}</li>
          <li v-if="c.gender"><strong>Geslacht:</strong> {{ c.gender }}</li>
          <li v-if="c.id"><strong>ID:</strong> {{ c.id }}</li>
        </ul>
      </article>
    </div>
  </section>
</template>

<style scoped>
.page { display: grid; gap: 1rem; padding: 1rem; }
.bar { display: grid; gap: .75rem; }
.controls { display: flex; gap: .5rem; flex-wrap: wrap; align-items: center; }
.controls select, .controls button {
  padding: .5rem .6rem; border: 1px solid #cbd5e1; border-radius: .5rem;
}
.controls button {
  border: 1px solid #0f172a; background: #0f172a; color: white; cursor: pointer;
}
.controls button[disabled] { opacity: .6; cursor: not-allowed; }
.state { padding: 1rem; color: #334155; }
.state.error { color: #b91c1c; }
.grid { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); }
.card {
  display: grid; gap: .5rem; padding: 1rem; border: 1px solid #e2e8f0; border-radius: .8rem;
  background: white; box-shadow: 0 1px 2px rgba(0,0,0,.03);
}
.avatar {
  width: 48px; height: 48px; border-radius: 999px; display: grid; place-items: center;
  font-weight: 700; border: 1px solid #cbd5e1;
}
.name { margin: 0; font-size: 1.1rem; }
.meta { list-style: none; padding: 0; margin: .25rem 0 0 0; display: grid; gap: .25rem; color: #334155; font-size: .95rem; }
</style>

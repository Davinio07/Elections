<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
// FIX 1: Import the shared type, which fixes the type assignment error
import { getCandidates, type CandidateData } from '@/features/admin/service/ScaledElectionResults_api.ts';

// FIX 2: Removed the old local interface definition.

const electionId = ref<'TK2023' | 'TK2024'>('TK2023');
const loading = ref(false);
const error = ref('');
// Use the imported type
const candidates = ref<CandidateData[]>([]);

// --- NEW: modal state ---
const showModal = ref(false);
const activeCandidate = ref<CandidateData | null>(null); // Use the imported type

function openCandidate(c: CandidateData) { // Use the imported type
  activeCandidate.value = c;
  showModal.value = true;
  // nextTick(() => try to focus the close button) — optional
}

function closeModal() {
  showModal.value = false;
  activeCandidate.value = null;
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape' && showModal.value) {
    e.preventDefault();
    closeModal();
  }
}

function folderFor(): string | undefined {
  return undefined;
}

function displayName(c: CandidateData): string { // Use the imported type
                                                 // Note: The new getCandidates mapping handles splitting the name into firstName and lastName
  const firstOrInitials = (c.firstName?.trim() || c.initials?.trim() || '').trim();
  const prefix = c.prefix && c.prefix.trim() ? ` ${c.prefix.trim()}` : '';
  const last = c.lastName?.trim() || '';
  const name = `${firstOrInitials}${prefix} ${last}`.trim();
  return name || c.id || 'Onbekende kandidaat';
}

function candidateKey(c: CandidateData, i: number): string { // Use the imported type
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
    // Call without electionId/folderName as the API is now simplified
    candidates.value = await getCandidates();
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

onMounted(() => {
  load();
  window.addEventListener('keydown', onKeydown);
});
onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown);
});
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

        <div class="actions">
          <button class="linklike" @click="openCandidate(c)" aria-haspopup="dialog">
            Meer info
          </button>
        </div>
      </article>
    </div>
  </section>

  <Teleport to="body">
    <div
      v-if="showModal"
      class="modal-backdrop"
      @click.self="closeModal"
    >
      <div
        class="modal"
        role="dialog"
        aria-modal="true"
        :aria-labelledby="activeCandidate ? 'dlg-title' : undefined"
      >
        <header class="modal-header">
          <h3 id="dlg-title" class="modal-title">
            {{ activeCandidate ? displayName(activeCandidate) : '' }}
          </h3>
          <button class="icon-btn" @click="closeModal" aria-label="Sluiten">✕</button>
        </header>

        <div class="modal-body">
          <ul class="details">
            <li v-if="activeCandidate?.listName"><strong>Lijst:</strong> {{ activeCandidate?.listName }}</li>
            <li v-if="activeCandidate?.listNumber"><strong>Lijstnummer:</strong> {{ activeCandidate?.listNumber }}</li>
            <li v-if="activeCandidate?.numberOnList"><strong>Plaats op lijst:</strong> {{ activeCandidate?.numberOnList }}</li>
            <li v-if="activeCandidate?.locality"><strong>Woonplaats:</strong> {{ activeCandidate?.locality }}</li>
            <li v-if="activeCandidate?.gender"><strong>Geslacht:</strong> {{ activeCandidate?.gender }}</li>
            <li v-if="activeCandidate?.id"><strong>ID:</strong> {{ activeCandidate?.id }}</li>
          </ul>
        </div>

        <footer class="modal-footer">
          <button class="primary" @click="closeModal">Sluiten</button>
        </footer>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
/* ... (existing styles) ... */
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
.actions { margin-top: .5rem; }
.linklike {
  background: transparent; border: none; padding: 0; color: #0f172a; cursor: pointer; font-weight: 600;
  text-decoration: underline;
}

/* --- NEW: modal styles --- */
.modal-backdrop {
  position: fixed; inset: 0; background: rgba(15, 23, 42, .5);
  display: grid; place-items: center; z-index: 1000;
}
.modal {
  width: min(640px, 92vw); background: #fff; border-radius: .9rem; border: 1px solid #e2e8f0;
  box-shadow: 0 10px 30px rgba(0,0,0,.25); display: grid; grid-template-rows: auto 1fr auto;
  max-height: 85vh;
}
.modal-header, .modal-footer { padding: .9rem 1rem; display: flex; align-items: center; justify-content: space-between; }
.modal-title { margin: 0; font-size: 1.15rem; }
.modal-body { padding: 0 1rem 1rem 1rem; overflow: auto; }
.icon-btn {
  border: none; background: transparent; font-size: 1.15rem; cursor: pointer; line-height: 1;
}
.primary {
  padding: .55rem .8rem; border-radius: .6rem; border: 1px solid #0f172a; background: #0f172a; color: #fff; cursor: pointer;
}
.details { list-style: none; padding: 0; margin: 0; display: grid; gap: .4rem; color: #334155; }
.details strong { color: #0f172a; }
</style>

<template>
  <div class="registration-page">
    <h1>Account Aanmaken</h1>
    <form @submit.prevent="handleRegister" class="registration-form">
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
      <div class="form-group">
        <label for="username">Gebruikersnaam</label>
        <input type="text" id="username" v-model="username" required :disabled="loading" />
      </div>
      <div class="form-group">
        <label for="email">E-mailadres</label>
        <input type="email" id="email" v-model="email" required :disabled="loading" />
      </div>
      <div class="form-group">
        <label for="password">Wachtwoord</label>
        <input type="password" id="password" v-model="password" required :disabled="loading" />
      </div>
      <button type="submit" class="submit-btn" :disabled="loading">
        {{ loading ? 'Bezig met registreren...' : 'Registreer' }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { registerUser } from '../service/user-api';
import { ApiError } from '@/services/api-client';

const username = ref('');
const email = ref('');
const password = ref('');
const loading = ref(false);
const errorMessage = ref<string | null>(null);
const successMessage = ref<string | null>(null);

async function handleRegister() {
  loading.value = true;
  errorMessage.value = null;
  successMessage.value = null;

  try {
    await registerUser({
      username: username.value,
      email: email.value,
      password: password.value,
    });
    successMessage.value = 'Registratie succesvol! Je kunt nu inloggen.';
    username.value = '';
    email.value = '';
    password.value = '';
  } catch (error) {
    if (error instanceof ApiError) {
      errorMessage.value = error.message;
    } else {
      errorMessage.value = 'Er is een onverwachte fout opgetreden.';
    }
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.registration-page {
  max-width: 500px;
  margin: 2rem auto;
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.registration-form .form-group {
  margin-bottom: 1rem;
}

.registration-form label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.registration-form input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: var(--primary-color, #007bff);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
}

.submit-btn:hover {
  background-color: #0056b3;
}

.submit-btn:disabled {
  background-color: #aaa;
  cursor: not-allowed;
}

.error-message {
  background-color: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.success-message {
  background-color: #d4edda;
  color: #155724;
  padding: 1rem;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
  margin-bottom: 1rem;
}
</style>

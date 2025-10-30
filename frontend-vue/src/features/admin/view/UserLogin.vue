<template>
  <div class="login-page">
    <h1>Inloggen</h1>
    <form @submit.prevent="handleLogin" class="login-form">
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
      <div class="form-group">
        <label for="username">Gebruikersnaam</label>
        <input type="text" id="username" v-model="username" required :disabled="loading" />
      </div>
      <div class="form-group">
        <label for="password">Wachtwoord</label>
        <input type="password" id="password" v-model="password" required :disabled="loading" />
      </div>
      <button type="submit" class="submit-btn" :disabled="loading">
        {{ loading ? 'Bezig met inloggen...' : 'Login' }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { loginUser } from '../service/auth-api';
import { ApiError } from '@/services/api-client';

const username = ref('');
const password = ref('');
const loading = ref(false);
const errorMessage = ref<string | null>(null);

async function handleLogin() {
  loading.value = true;
  errorMessage.value = null;

  try {
    const response = await loginUser({
      username: username.value,
      password: password.value,
    });

    localStorage.setItem('authToken', response.token);

    alert('Login succesvol!');

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
.login-page {
  max-width: 400px;
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

.login-form .form-group {
  margin-bottom: 1rem;
}

.login-form label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.login-form input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
}

.submit-btn:hover {
  background-color: #218838;
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
</style>

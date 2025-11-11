// src/services/auth-store.ts

import { ref, computed } from 'vue';

/**
 * We halen de token uit de localStorage.
 * Als de gebruiker de pagina ververst, checken we of er nog een token was.
 */
const authToken = ref(localStorage.getItem('authToken'));

/**
 * Dit is een 'computed' boolean die true is als er een token is,
 * en false als die er niet is. Onze app kijkt hiernaar.
 */
export const isLoggedIn = computed(() => !!authToken.value);

/**
 * Deze functie roepen we aan als de gebruiker succesvol inlogt.
 * Het slaat de token op in de localStorage EN in onze 'ref'.
 */
export function setToken(token: string) {
  localStorage.setItem('authToken', token);
  authToken.value = token;
}

/**
 * Deze functie roepen we aan bij het uitloggen.
 * Het verwijdert de token overal.
 */
export function clearToken() {
  localStorage.removeItem('authToken');
  authToken.value = null;
}

// src/services/api-client.ts

const API_BASE_URL = 'http://localhost:8080/api'; // Centrale basis-URL

/**
 * Een generieke functie om API-aanroepen te doen.
 * @param endpoint Het specifieke endpoint om aan te roepen (bv. '/admin/stats')
 * @param options Optionele fetch-configuratie (method, headers, body, etc.)
 * @returns De JSON-response van de API
 * @throws Een error als de netwerkrespons niet 'ok' is.
 */
async function apiClient<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json() as T;
  } catch (error) {
    console.error(`API call naar ${endpoint} mislukt:`, error);
    // Gooi de error opnieuw zodat de aanroepende functie deze kan afhandelen
    throw error;
  }
}

export default apiClient;

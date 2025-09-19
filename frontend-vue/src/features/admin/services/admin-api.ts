// src/features/admin/services/admin-api.ts
import apiClient from '@/services/api-client'; // Importeer de centrale client

// Interface voor de statistiekdata
export interface AdminStat {
  id: number;
  title: string;
  value: string;
  icon: string;
}

// Interface voor de ping response
interface PingResponse {
  message: string;
}

// Gebruik de apiClient voor de ping-functie
export async function pingBackend(): Promise<string> {
  try {
    const data = await apiClient<PingResponse>('/admin/ping');
    return data.message;
  } catch (error) {
    // De apiClient logt de error al, hier geven we een gebruiksvriendelijke melding terug
    return "Kon geen verbinding maken met de backend.";
  }
}

// Gebruik de apiClient voor de statistieken-functie
export async function getAdminStats(): Promise<AdminStat[]> {
  try {
    return await apiClient<AdminStat[]>('/admin/stats');
  } catch (error) {
    return []; // Retourneer een lege array bij een fout
  }
}

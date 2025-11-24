// Bestand: src/features/admin/service/admin-api.ts
import apiClient from '@/services/api-client';

// Interface voor de statistieken die we van de backend krijgen
export interface DashboardStat {
  id: number;
  title: string;
  value: string;
  icon: string;
}

export async function pingBackend(): Promise<string> {
  try {
    const data = await apiClient<{ message: string }>('/admin/ping');
    return data.message;
  } catch (error) {
    return "Kon geen verbinding maken met de backend.";
  }
}

// NIEUWE FUNCTIE
export async function getDashboardStats(): Promise<DashboardStat[]> {
  try {
    return await apiClient<DashboardStat[]>('/admin/stats');
  } catch (error) {
    console.error("Error fetching admin stats:", error);
    throw error;
  }
}

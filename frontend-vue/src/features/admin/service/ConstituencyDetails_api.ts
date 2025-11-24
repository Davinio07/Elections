import apiClient from '@/services/api-client';

// We updated the interfaces to match the new backend DTO structure
export interface ConstituencyResultDto {
  partyName: string;
  validVotes: number;
}

export interface ConstituencyDataDto {
  name: string;
  results: ConstituencyResultDto[];
}

/**
 * Fetches ALL constituency results (aggregated from municipalities).
 * We now point to the correct endpoint '/api/constituencies/results'.
 */
export async function getAllConstituencyResults(): Promise<ConstituencyDataDto[]> {
  try {
    const endpoint = '/constituencies/results';
    return await apiClient<ConstituencyDataDto[]>(endpoint);
  } catch (error) {
    console.error('API Error when fetching constituency results:', error);
    throw error;
  }
}

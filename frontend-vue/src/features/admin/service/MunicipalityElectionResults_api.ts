import apiClient from '@/services/api-client';

// We defined explicit interfaces here to match the DTOs from the backend.
// This keeps our data structured and type-safe.
export interface MunicipalityResultDto {
  partyName: string;
  validVotes: number;
}

export interface MunicipalityDataDto {
  name: string;
  results: MunicipalityResultDto[];
}

/**
 * Gets the election results for a single municipality (Detail page).
 * @param municipalityName The name of the city.
 */
export async function getResultsForMunicipality(municipalityName: string): Promise<MunicipalityResultDto[]> {
  try {
    const endpoint = `/elections/municipalities/${encodeURIComponent(municipalityName)}`;
    return await apiClient<MunicipalityResultDto[]>(endpoint);
  } catch (error) {
    console.error('API Error when fetching specific municipality:', error);
    throw error;
  }
}

/**
 * Gets ALL municipality results in a single API call (Overview page).
 * I added this to populate the cards on the main overview efficiently.
 */
export async function getAllMunicipalityResults(): Promise<MunicipalityDataDto[]> {
  try {
    // This endpoint was enabled in the backend fix feature/municipality-data-loading
    const endpoint = '/elections/municipalities/all-results';
    return await apiClient<MunicipalityDataDto[]>(endpoint);
  } catch (error) {
    console.error('API Error when fetching all municipality results:', error);
    throw error;
  }
}

/**
 * Gets just the names (if needed for dropdowns later).
 */
export async function getMunicipalityNames(): Promise<string[]> {
  try {
    // Note: The backend endpoint name is a bit weird (/regions/gemeenten), but it works.
    const response = await apiClient<{ name: string }[]>('/elections/TK2023/regions/gemeenten');
    return response.map((item) => item.name);
  } catch (error) {
    console.error('API Error when fetching municipality names:', error);
    throw error;
  }
}

import apiClient from '@/services/api-client';

export interface MunicipalityResult {
    municipalityName: string;
    partyName: string;
    validVotes: number;
}

/**
 * Gets the election results for a single municipality.
 * @param municipalityName The name of the city you want to get results for.
 * @returns A promise that gives us a list of results.
 */
export async function getResultsForMunicipality(municipalityName: string): Promise<MunicipalityResult[]> {
    try {
        // We use encodeURIComponent to handle names with spaces like "Den Haag"
        const endpoint = `/elections/municipalities/${encodeURIComponent(municipalityName)}`;
        const response = await apiClient<MunicipalityResult[]>(endpoint);
        return response;
    } catch (error) {
        // Show an error message if the call fails
        console.error('API Error when fetching results:', error);
        throw error;
    }
}

/**
 * Gets a list of all the municipality names.
 * @returns A promise that gives us an array of names.
 */
export async function getMunicipalityNames(): Promise<string[]> {
    try {
        const response = await apiClient<string[]>('/elections/municipalities/names');
        return response;
    } catch (error) {
        // Show an error message if the call fails
        console.error('API Error when fetching municipality names:', error);
        throw error;
    }
}
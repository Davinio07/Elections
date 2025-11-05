import apiClient from '@/services/api-client';

export interface KieskringResult {
    municipalityName: string;
    partyName: string;
    validVotes: number;
}

/**
 * Gets the election results for a single municipality.
 * @param municipalityName The name of the city you want to get results for.
 * @returns A promise that gives us a list of results.
 */
export async function getResultsForKieskring(municipalityName: string): Promise<KieskringResult[]> {
    try {
        const endpoint = `/elections/municipalities/${encodeURIComponent(municipalityName)}`;
        const response = await apiClient<KieskringResult[]>(endpoint);
        return response;
    } catch (error) {
        console.error('API Error when fetching results:', error);
        throw error;
    }
}

/**
 * Gets a list of all the municipality names.
 * @returns A promise that gives us an array of names.
 */

export async function getKieskringNames(): Promise<string[]> {
    try {
        const response = await apiClient<string[]>('/elections/municipalities/names');
        return response;
    } catch (error) {
        console.error('API Error when fetching municipality names:', error);
        throw error;
    }
}

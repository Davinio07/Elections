import apiClient from '@/services/api-client';

export interface MunicipalityResult {
    municipalityName: string;
    partyName: string;
    validVotes: number;
}

interface ElectionData {
    municipalityResults: MunicipalityResult[];
}

export async function getMunicipalityElectionResults(): Promise<MunicipalityResult[]> {
    try {
        const response = await apiClient<ElectionData>('/elections/results');

        return response.municipalityResults;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

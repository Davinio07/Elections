// src/service/KieskringDetails_api.ts
import apiClient from '@/services/api-client';

// ... (keep your old interfaces and functions for now) ...

// 1. Define interfaces that match your new DTOs
export interface KieskringResultDto {
  partyName: string;
  validVotes: number;
}

export interface KieskringDataDto {
  name: string;
  results: KieskringResultDto[];
}

/**
 * Gets ALL kieskring results in a single API call.
 * This is much faster than fetching one by one.
 * this is from the XML.
 */
export async function getAllKieskringResults(): Promise<KieskringDataDto[]> {
  try {
    const endpoint = '/elections/municipalities/all-results';
    const response = await apiClient<KieskringDataDto[]>(endpoint);
    return response;
  } catch (error) {
    console.error('API Error when fetching all results:', error);
    throw error;
  }
}
export async function getKiesKringNames(): Promise<KieskringDataDto[]> {
  try {
    const endpoint = '/elections/kieskring/names';
    const response = await apiClient<KieskringDataDto[]>(endpoint);
    return response;
  } catch (error) {
    console.error('API Error when fetching all results:', error);
    throw error;
  }
}

import apiClient from '@/services/api-client';

interface JpaCandidate {
  id: number;
  name: string; // Full name (e.g., "Dilan Yeşilgöz")
  residence: string; // Locality (e.g., "Amsterdam")
  gender: string;
  party: {
    name: string; // Party name (e.g., "VVD")
  }
}

export interface CandidateData { // <-- Export this as the canonical type
  id: number | string | null;
  firstName: string | null;
  lastName: string | null;
  locality: string | null; // Mapped from residence
  gender: string | null;
  listName: string | null; // Mapped from party.name

  // Optional fields from the original XML structure (set to null/undefined)
  initials?: string | null;
  prefix?: string | null;
  listNumber?: string | null;
  numberOnList?: string | null;
}

export async function ScaledElectionResults(): Promise<string> {
  try {
    const url = 'http://localhost:8080/api/ScaledElectionResults/Result'.trim();
    const response = await fetch(url, {
      credentials: "include"
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data.message;
  } catch (error) {
    console.error("Fout bij het ophalen van de backend:", error);
    return "Kon geen verbinding maken met de backend.";
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function getProvinces(electionId: string): Promise<any[]> {
  try {
    const url = `http://localhost:8080/api/elections/${electionId}/regions/kieskringen`;
    const response = await fetch(url, { method: 'GET', headers: { 'Accept': 'application/json' } });

    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    return await response.json();
  } catch (error) {
    console.error('Error fetching provinces:', error);
    return [];
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
// src/features/admin/service/ScaledElectionResults_api.ts

// ... (existing code for ScaledElectionResults and getProvinces)

// FIX: Updated function to use DB endpoint and map JPA fields to Vue fields
export async function getCandidates(): Promise<CandidateData[]> { // <-- Use CandidateData as return type
  try {
    // ... existing try block content ...
    // 1. Use the correct database endpoint (does not need electionId)
    const endpoint = `/elections/candidates/db`;

    // Fetch the raw JPA candidates list
    // <-- ENSURE THIS LINE IS PRESENT AND CORRECT -->
    const rawCandidates = await apiClient<JpaCandidate[]>(endpoint, { method: 'GET' });

    // 2. Map the JPA fields to the CandidateData fields expected by the component
    return rawCandidates.map(c => {
      // Simple heuristic to split full name: everything but the last word is first name
      const parts = c.name.split(' ');
      const lastName = parts.length > 1 ? parts[parts.length - 1] : c.name;
      const firstName = parts.length > 1 ? parts.slice(0, parts.length - 1).join(' ') : '';

      return {
        id: c.id,
        firstName: firstName,
        lastName: lastName,
        locality: c.residence, // Map JPA 'residence' to Vue 'locality'
        gender: c.gender,
        listName: c.party?.name, // Use party name as list name
        // Explicitly set unused fields to null to match the component's expectations:
        initials: null,
        prefix: null,
        listNumber: null,
        numberOnList: null,
      } as CandidateData;
    });

  } catch (error) {
    console.error('Error fetching candidates:', error);
    // Return an empty array on error, as expected by the calling component
    return [];
  }
}

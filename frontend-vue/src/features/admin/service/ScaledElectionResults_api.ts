import apiClient from '@/services/api-client';

interface JpaCandidate {
  id: number;
  name: string; // Full name (e.g., "Dilan Yeşilgöz")
  residence: string; // Locality (e.g., "Amsterdam")
  gender: string;
  partyIdForJson: number | null;   // Mapped from getPartyIdForJson()
  partyNameForJson: string | null; // Mapped from getPartyNameForJson()
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

export interface PartyFilterData {
  id: number;
  name: string;
}

export async function getAllPartiesForFilters(electionId: string): Promise<PartyFilterData[]> {
  try {
    // Calls the new backend endpoint
    const endpoint = `/elections/${electionId}/parties/db`;
    const rawParties = await apiClient<any[]>(endpoint, { method: 'GET' });

    // Map the full Party model to just ID and Name
    return rawParties.map(p => ({ id: p.id, name: p.name }));

  } catch (error) {
    console.error('Error fetching parties for filters:', error);
    return [];
  }
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
// --- UPDATED FUNCTION: getCandidates with filters ---
export async function getCandidates(partyId?: number | null, gender?: string | null): Promise<CandidateData[]> {
  try {
    let url = `/elections/candidates/db`;
    const params = new URLSearchParams();

    // Add filters to URL parameters if they exist
    if (partyId) {
      params.append('partyId', partyId.toString());
    }
    if (gender) {
      params.append('gender', gender);
    }

    if (params.toString()) {
      url += '?' + params.toString();
    }

    // Fetch with the constructed URL
    const rawCandidates = await apiClient<JpaCandidate[]>(url, { method: 'GET' });

    // ... (rest of the mapping logic remains the same) ...
    return rawCandidates.map(c => {
      // ... mapping logic ...
      const parts = c.name.split(' ');
      const lastName = parts.length > 1 ? parts[parts.length - 1] : c.name;
      const firstName = parts.length > 1 ? parts.slice(0, parts.length - 1).join(' ') : '';

      return {
        id: c.id,
        firstName: firstName,
        lastName: lastName,
        locality: c.residence,
        gender: c.gender,
        listName: c.partyNameForJson,
        listId: c.partyIdForJson,
        initials: null,
        prefix: null,
        listNumber: null,
        numberOnList: null,
      } as CandidateData;
    });

  } catch (error) {
    console.error('Error fetching candidates:', error);
    return [];
  }
}

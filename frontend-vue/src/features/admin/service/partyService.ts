/**
 * Interface representing a political party (basic info).
 */
export interface PoliticalParty {
  /** The officially registered name of the party. */
  registeredAppellation: string;

}

/**
 * Interface representing a party's national result.
 * This matches the Java model provided.
 */
export interface NationalResult {
  partyName: string;
  validVotes: number;
}

/**
 * The base URL for the elections API.
 */
const API_BASE_URL = 'http://localhost:8080/api/elections';

/**
 * A mapping of official party names to their designated hex color codes.
 * Based on common colors for Dutch political parties.
 */
export const partyColors: Record<string, string> = {
  'VVD': '#FF6600',
  'D66': '#00A03E',
  'GROENLINKS / Partij van de Arbeid (PvdA)': '#DC143C',
  'PVV (Partij voor de Vrijheid)': '#00529F',
  'CDA': '#00A54F',
  'SP (Socialistische Partij)': '#FF0000',
  'Forum voor Democratie': '#800020',
  'Partij voor de Dieren': '#006F3F',
  'ChristenUnie': '#00A7EB',
  'Volt': '#502379',
  'JA21': '#1C39BB',
  'Staatkundig Gereformeerde Partij (SGP)': '#FE7D00',
  'DENK': '#00CCCC',
  '50PLUS': '#8B008B',
  'BBB': '#5BB12F',
  'BIJ1': '#FFED00',
  'BVNL / Groep Van Haga': '#1E3A8A',
  'Nieuw Sociaal Contract': '#00A3E0',
  'Piratenpartij - De Groenen': '#660099',
  'Splinter': '#FF6B35',
  'Samen voor Nederland': '#FF8C00',
  'LP (Libertaire Partij)': '#FFD700',
  'LEF - Voor de Nieuwe Generatie': '#00CED1',
  'Nederland met een PLAN': '#4682B4',
  'PartijvdSport': '#32CD32',
  'Politieke Partij voor Basisinkomen': '#9370DB',
};

/**
 * Retrieves the hex color code for a given party name.
 *
 * @param {string} partyName - The registered appellation of the party.
 * @returns {string} The corresponding hex color code or a default gray if not found.
 */
export const getPartyColor = (partyName: string): string => {

  return partyColors[partyName] || '#6B7280'; // Default gray if not found
};

/**
 * Generic helper function for handling API requests.
 */
async function fetchFromAPI<T>(endpoint: string): Promise<T> {
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`);

    if (!response.ok) {
      let errorBody = 'Unknown error';
      try {
        errorBody = await response.text();
      } catch (textError) { /* ignore */ }
      console.error(`API Error: ${response.status} ${response.statusText}`, errorBody);
      throw new Error(`Failed to fetch data: ${response.status} ${response.statusText}`);
    }

    return await response.json();

  } catch (error) {
    console.error(`Error in fetchFromAPI (endpoint: ${endpoint}):`, error);
    if (error instanceof SyntaxError) {
      throw new Error('Failed to parse server response. Invalid JSON.');
    }
    if (error instanceof TypeError) {
      throw new Error('Network error: Could not connect to the API.');
    }
    if (error instanceof Error) {
      throw error;
    }
    throw new Error('An unknown error occurred.');
  }
}


/**
 * Service object for handling political party data.
 */
export const partyService = {
  /**
   * Fetches a list of political parties for a specific election.
   *
   * @param {string} electionId - The unique identifier for the election.
   * @returns {Promise<PoliticalParty[]>} A promise that resolves to an array of PoliticalParty objects.
   */
  async getParties(electionId: string): Promise<PoliticalParty[]> {
    return fetchFromAPI<PoliticalParty[]>(`/${electionId}/parties`);
  },

  /**
   * Fetches the national results (seats, votes, percentage) for all parties in an election.
   *
   * @param {string} electionId - The unique identifier for the election.
   * @returns {Promise<NationalResult[]>} A promise that resolves to an array of NationalResult objects.
   */
  async getNationalResults(electionId: string): Promise<NationalResult[]> {
    return fetchFromAPI<NationalResult[]>(`/${electionId}/national`);
  }
};


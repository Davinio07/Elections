// partyService.ts

interface PoliticalParty {
  registeredAppellation: string;
}

const API_BASE_URL = 'http://localhost:8080/api/elections';

// Official party colors based on Dutch political parties
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

export const getPartyColor = (partyName: string): string => {
  return partyColors[partyName] || '#6B7280'; // Default gray if not found
};

export const partyService = {
  async getParties(electionId: string): Promise<PoliticalParty[]> {
    const response = await fetch(`${API_BASE_URL}/${electionId}/parties`);
    if (!response.ok) {
      throw new Error('Failed to fetch parties');
    }
    return response.json();
  }
};

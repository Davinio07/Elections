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
export async function getCandidates(electionId: string, folderName?: string): Promise<any[]> {
  try {
    const base = `http://localhost:8080/api/elections/${encodeURIComponent(electionId)}/candidates`;
    const url = folderName ? `${base}?folderName=${encodeURIComponent(folderName)}` : base;
    const response = await fetch(url, { method: 'GET', headers: { 'Accept': 'application/json' } });

    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return await response.json();
  } catch (error) {
    console.error('Error fetching candidates:', error);
    return [];
  }
}



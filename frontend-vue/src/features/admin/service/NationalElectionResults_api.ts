export async function getNationalResults(electionId: string) {
  const url = `http://localhost:8080/api/elections/${electionId}/national`;
  const response = await fetch(url, { method: 'GET', headers: { 'Accept': 'application/json' } });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`HTTP Error ${response.status} (${response.statusText}). Response body: ${errorText.substring(0, 100)}...`);
  }
  return response.json();
}

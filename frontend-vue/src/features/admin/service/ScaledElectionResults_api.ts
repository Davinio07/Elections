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

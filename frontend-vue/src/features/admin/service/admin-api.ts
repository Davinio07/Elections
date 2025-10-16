export async function pingBackend(): Promise<string> {
    try {
        const response = await fetch('http://localhost:8080/api/admin/ping');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        return data.message;
    } catch (error) {
        console.error("Fout bij het pingen van de backend:", error);
        return "Kon geen verbinding maken met de backend.";
    }
}

## Endpoint-overview

| Route | Auth? | Methode | Request (input) | Response (output) | Uitleg |
| --- | --- | --- | --- | --- | --- |
| `/api/admin/ping` | --- | `GET` | --- | **200:** `{ message: "Backend is running" }`<br>**500:** `{ error: "Internal server error" }` | Controleert of de backend bereikbaar is. Wordt gebruikt om de verbinding te testen. |
| `/api/elections/results` | --- | `GET` | --- | **200:** `{ municipalityResults: [{ municipalityName, partyName, validVotes }] }`<br>**500:** `{ error: "Failed to fetch results" }` | Haalt alle verkiezingsresultaten per gemeente op. |
| `/api/elections/{electionId}/national` | --- | `GET` | URL-param `{ electionId }` | **200:** `{ nationalResults: [...] }`<br>**404:** `{ error: "Election not found" }`<br>**500:** `{ error: "Internal server error" }` | Haalt de nationale verkiezingsuitslag op voor het opgegeven verkiezings-ID. |
| `/api/ScaledElectionResults/Result` | --- | `GET` | --- | **200:** `{ message: "Scaled results fetched successfully" }`<br>**500:** `{ error: "Internal server error" }` | Haalt geschaalde verkiezingsresultaten op (geaggregeerde data). |
| `/api/elections/{electionId}/regions/kieskringen` | --- | `GET` | URL-param `{ electionId }` | **200:** `[ { regionId, regionName, ... } ]`<br>**404:** `{ error: "Regions not found" }`<br>**500:** `{ error: "Internal server error" }` | Haalt alle kieskringen (provincies/gebieden) op die horen bij een bepaalde verkiezing. |
| `/api/elections/{electionId}/candidates` | --- | `GET` | URL-param `{ electionId }`<br>Optioneel query-param `folderName` | **200:** `[ { candidateId, name, party, ... } ]`<br>**404:** `{ error: "Candidates not found" }`<br>**500:** `{ error: "Internal server error" }` | Haalt alle kandidaten op voor een specifieke verkiezing. Kan gefilterd worden op mapnaam via `folderName`. |

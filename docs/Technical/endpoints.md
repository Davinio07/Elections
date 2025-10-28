## Endpoint Overview

**Base URL:** `http://localhost:8080/api`

| Route | Auth? | Method | Request (input) | Response (output) | Description |
| --- | --- | --- | --- | --- | --- |
| `/admin/ping` | --- | `GET` | --- | **200:** `{ message: "Backend is running" }`<br>**500:** `{ error: "Internal server error" }` | Checks whether the backend server is reachable. Used for connection testing. |
| `/elections/results` | --- | `GET` | --- | **200:** `{ municipalityResults: [{ municipalityName, partyName, validVotes }] }`<br>**500:** `{ error: "Failed to fetch results" }` | Retrieves all election results per municipality. |
| `/elections/{electionId}/national` | --- | `GET` | URL param `{ electionId }` | **200:** `{ nationalResults: [...] }`<br>**404:** `{ error: "Election not found" }`<br>**500:** `{ error: "Internal server error" }` | Fetches the national election results for the given election ID. |
| `/ScaledElectionResults/Result` | --- | `GET` | --- | **200:** `{ message: "Scaled results fetched successfully" }`<br>**500:** `{ error: "Internal server error" }` | Retrieves aggregated (scaled) election results. |
| `/elections/{electionId}/regions/kieskringen` | --- | `GET` | URL param `{ electionId }` | **200:** `[ { regionId, regionName, ... } ]`<br>**404:** `{ error: "Regions not found" }`<br>**500:** `{ error: "Internal server error" }` | Fetches all electoral regions ("kieskringen") associated with a specific election. |
| `/elections/{electionId}/candidates` | --- | `GET` | URL param `{ electionId }`<br>Optional query param `folderName` | **200:** `[ { candidateId, name, party, ... } ]`<br>**404:** `{ error: "Candidates not found" }`<br>**500:** `{ error: "Internal server error" }` | Fetches all candidates for a specific election. Can be filtered by folder name using the `folderName` query parameter. |
| `/elections/{electionId}/parties` | --- | `GET` | URL param `{ electionId }` | **200:** `[ { registeredAppellation } ]`<br>**404:** `{ error: "Parties not found" }`<br>**500:** `{ error: "Internal server error" }` | Retrieves all political parties registered for a given election. |

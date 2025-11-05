const logger = {
  info: (message: string, context?: object) => console.log(`[INFO] ${message}`, context || ''),
  warn: (message: string, context?: object) => console.warn(`[WARN] ${message}`, context || ''),
  error: (message: string, context?: object) => console.error(`[ERROR] ${message}`, context || ''),
  debug: (message: string, context?: object) => console.debug(`[DEBUG] ${message}`, context || ''),
};

/**
 * Represents an HTTP-specific error.
 * @extends Error
 */
class HttpError extends Error {
  status: number;
  statusText: string;

  constructor(message: string, status: number, statusText: string) {
    super(message);
    this.name = 'HttpError';
    this.status = status;
    this.statusText = statusText;
  }
}

/**
 * Represents the data structure for a Party as returned by the DB endpoint.
 */
export interface PartyDTO {
  id: number;
  name: string;
  totalVotes: number;
  nationalSeats: number;
  votePercentage: number;
}

/**
 * Fetches the persisted party results (incl. votes, seats) for a given election ID.
 * This function makes a GET request to `/api/elections/{electionId}/parties/db`.
 *
 * Returns an array of `PartyDTO`. If the server responds with HTTP 204 (No Content),
 * an empty array is returned.
 *
 * @async
 * @function getPartiesFromDb
 * @param {string} electionId - The unique identifier of the election (e.g., "TK2023").
 * @returns {Promise<PartyDTO[]>} A promise that resolves to an array of party data.
 *
 * @throws {HttpError} When the response contains a known HTTP error status (4xx or 5xx).
 * @throws {Error} When a network or unexpected fetch error occurs.
 */
export async function getPartiesFromDb(electionId: string): Promise<PartyDTO[]> {
  const url = `http://localhost:8080/api/elections/${electionId}/parties/db`;
  logger.info(`Fetching persisted party data for election ID: ${electionId}`);
  logger.debug(`Request URL: ${url}`);

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: { 'Accept': 'application/json' },
    });

    // Handle successful but empty response
    if (response.status === 204) {
      logger.info(`Request successful but no party data found for election ID: ${electionId}`);
      return [];
    }

    // Handle non-successful responses
    if (!response.ok) {
      let errorMessage = 'An unexpected error occurred.';
      try {
        // Try to parse a JSON error body
        const errorBody = await response.json();
        errorMessage = errorBody.message || JSON.stringify(errorBody);
      } catch (e) {
        // Fallback to status text if JSON parsing fails
        errorMessage = response.statusText;
      }

      switch (response.status) {
        case 400:
          logger.warn(`Bad Request: The server could not process the request for ${electionId}.`, { details: errorMessage });
          throw new HttpError(`Invalid request: ${errorMessage}`, response.status, response.statusText);

        case 404:
          logger.warn(`Party data not found for ID: ${electionId}`);
          throw new HttpError(`No party data found for election ID '${electionId}'.`, response.status, response.statusText);

        case 500:
        case 502:
        case 503:
        case 504:
          logger.error(`A server error occurred: ${response.status}`, { details: errorMessage });
          throw new HttpError('The service is temporarily unavailable. Please try again later.', response.status, response.statusText);

        default:
          logger.error(`An unhandled HTTP error occurred: ${response.status}`, { details: errorMessage });
          throw new HttpError(`An unexpected error occurred. Status: ${response.status}`, response.status, response.statusText);
      }
    }

    // Handle successful response
    logger.info(`Successfully retrieved party data for election ID: ${electionId}`);
    return response.json();

  } catch (error) {
    // Re-throw known HttpErrors
    if (error instanceof HttpError) {
      throw error;
    }

    // Handle network errors or other unexpected errors
    logger.error('A network or unexpected error occurred during fetch.', { originalError: error });
    throw new Error('Failed to connect to the server. Please check your network connection.');
  }
}

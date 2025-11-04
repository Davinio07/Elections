const logger = {
  info: (message: string, context?: object) => console.log(`[INFO] ${message}`, context || ''),
  warn: (message: string, context?: object) => console.warn(`[WARN] ${message}`, context || ''),
  error: (message: string, context?: object) => console.error(`[ERROR] ${message}`, context || ''),
  debug: (message: string, context?: object) => console.debug(`[DEBUG] ${message}`, context || ''),
};

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
 * Fetches the national election results for a given election ID from the backend API.
 */
export async function getNationalResults(electionId: string): Promise<unknown> {
  const url = `http://localhost:8080/api/elections/${electionId}/national`;
  logger.info(`Fetching national results for election ID: ${electionId}`);
  logger.debug(`Request URL: ${url}`);

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: { 'Accept': 'application/json' },
    });

    // Handle successful but empty response first
    if (response.status === 204) {
      logger.info(`Request successful but no content for election ID: ${electionId}`);
      return null; // Or return [], depending on what the consumer expects
    }

    // Handle all other non-successful responses
    if (!response.ok) {
      let errorMessage = 'An unexpected error occurred.';
      // Try to parse error details from the response body if it's JSON
      try {
        const errorBody = await response.json();
        errorMessage = errorBody.message || JSON.stringify(errorBody);
      } catch (e) {
        // Body was not JSON or was empty, use status text as a fallback
        errorMessage = response.statusText;
      }

      switch (response.status) {
        case 400:
          logger.warn(`Bad Request: The server could not process the request for ${electionId}.`, { details: errorMessage });
          throw new HttpError(`Invalid request: ${errorMessage}`, response.status, response.statusText);

        case 401:
          logger.warn(`Unauthorized access attempt for election ID: ${electionId}.`);
          throw new HttpError('Authentication failed. Please log in.', response.status, response.statusText);

        case 403:
          logger.warn(`Forbidden access attempt for election ID: ${electionId}.`);
          throw new HttpError('You do not have permission to access this resource.', response.status, response.statusText);

        case 404:
          logger.warn(`Election results not found for ID: ${electionId}`);
          throw new HttpError(`No election results found for ID '${electionId}'.`, response.status, response.statusText);

        case 409:
          logger.warn(`Conflict detected for election ID: ${electionId}.`, { details: errorMessage });
          throw new HttpError(`Conflict: ${errorMessage}`, response.status, response.statusText);

        case 429:
          logger.warn(`Rate limit exceeded for client.`);
          throw new HttpError('You have made too many requests. Please wait a moment and try again.', response.status, response.statusText);

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

    // If the response is successful (2xx)
    logger.info(`Successfully retrieved results for election ID: ${electionId}`);
    return response.json();

  } catch (error) {
    if (error instanceof HttpError) {
      throw error; // Re-throw custom errors to be handled by the caller
    }

    logger.error('A network or unexpected error occurred during fetch.', { originalError: error });
    throw new Error('Failed to connect to the server. Please check your network connection.');
  }
}

/**
 * Fetches the national election seat counts for a given election ID from the backend API.
 */
export async function getNationalSeats(electionId: string): Promise<Record<string, number>> {
  const url = `http://localhost:8080/api/elections/${electionId}/seats`;
  logger.info(`Fetching national seat counts for election ID: ${electionId}`);
  logger.debug(`Request URL: ${url}`);

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {'Accept': 'application/json'},
    });

    // Handle no content
    if (response.status === 204) {
      logger.info(`Request successful but no seat data for election ID: ${electionId}`);
      return {};
    }

    // Handle non-successful responses
    if (!response.ok) {
      let errorMessage = 'An unexpected error occurred.';
      // Try to parse error details from the response body if it's JSON
      try {
        const errorBody = await response.json();
        errorMessage = errorBody.message || JSON.stringify(errorBody);
      } catch (e) {
        // Body was not JSON or was empty, use status text as a fallback
        errorMessage = response.statusText;
      }

      switch (response.status) {
        case 400:
          logger.warn(`Bad Request: The server could not process the seat request for ${electionId}.`, {details: errorMessage});
          throw new HttpError(`Invalid seat request: ${errorMessage}`, response.status, response.statusText);

        case 401:
          logger.warn(`Unauthorized access attempt for seat data for election ID: ${electionId}.`);
          throw new HttpError('Authentication failed. Please log in to view seat counts.', response.status, response.statusText);

        case 403:
          logger.warn(`Forbidden access attempt for seat data for election ID: ${electionId}.`);
          throw new HttpError('You do not have permission to access seat count data.', response.status, response.statusText);

        case 404:
          logger.warn(`Election seat data not found for ID: ${electionId}`);
          throw new HttpError(`No election seat counts found for ID '${electionId}'.`, response.status, response.statusText);

        case 409:
          logger.warn(`Conflict detected for seat data for election ID: ${electionId}.`, {details: errorMessage});
          throw new HttpError(`Conflict in seat data: ${errorMessage}`, response.status, response.statusText);

        case 429:
          logger.warn(`Rate limit exceeded for client when requesting seat data.`);
          throw new HttpError('You have made too many seat data requests. Please wait a moment and try again.', response.status, response.statusText);

        case 500:
        case 502:
        case 503:
        case 504:
          logger.error(`A server error occurred while fetching seats: ${response.status}`, {details: errorMessage});
          throw new HttpError('The seat service is temporarily unavailable. Please try again later.', response.status, response.statusText);

        default:
          logger.error(`An unhandled HTTP error occurred while fetching seats: ${response.status}`, {details: errorMessage});
          throw new HttpError(`An unexpected error occurred while fetching seats. Status: ${response.status}`, response.status, response.statusText);
      }
    }

    // Successful response
    logger.info(`Successfully retrieved seat counts for election ID: ${electionId}`);
    return response.json();

  } catch (error) {
    if (error instanceof HttpError) {
      throw error; // Re-throw custom errors
    }

    logger.error('A network or unexpected error occurred during fetch of seat data.', {originalError: error});
    throw new Error('Failed to connect to the server. Please check your network connection.');
  }
}

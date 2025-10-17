// src/services/api-client.ts

const API_BASE_URL = 'http://localhost:8080/api';

/**
 * @interface ApiErrorResponse
 * @description Describes the structure of a standard error object
 * that our Spring Boot backend sends when something goes wrong.
 */
interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

/**
 * @class ApiError
 * @description A custom error class for handling API-specific errors.
 * This helps us know exactly what kind of error we are dealing with.
 */
export class ApiError extends Error {
  /**
   * The HTTP status code from the server (e.g., 404, 500).
   * @type {number}
   */
  public readonly status: number;

  /**
   * The full error object from the backend, if available.
   * @type {ApiErrorResponse | null}
   */
  public readonly details: ApiErrorResponse | null;

  /**
   * Creates an instance of ApiError.
   * @param {string} message - The main error message to display.
   * @param {number} status - The HTTP status code.
   * @param {ApiErrorResponse | null} details - The detailed error object from the server.
   */
  constructor(message: string, status: number, details: ApiErrorResponse | null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.details = details;
  }
}

/**
 * A helper function to check if an unknown object has the structure of our API error.
 * @param {unknown} obj - The object we want to check.
 * @returns {boolean} - True if the object is an ApiErrorResponse, otherwise false.
 */
function isApiErrorResponse(obj: unknown): obj is ApiErrorResponse {
  return (
    typeof obj === 'object' &&
    obj !== null &&
    'message' in obj &&
    typeof (obj as ApiErrorResponse).message === 'string'
  );
}

/**
 * A central function for making all API calls to the backend.
 * It automatically adds the base URL and handles errors.
 * @template T - The expected type of the data in the successful response body.
 * @param {string} endpoint - The specific API endpoint to call (e.g., '/users').
 * @param {RequestInit} [options={}] - Optional settings for the request, like method, headers, or body.
 * @returns {Promise<T>} A promise that resolves with the data from the API.
 * @throws {ApiError} Throws a custom ApiError if the server response is not OK.
 */
async function apiClient<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  });

  if (!response.ok) {
    let errorDetails: unknown = null;
    try {
      errorDetails = await response.json();
    } catch (e) {
      // The body was not valid JSON, which is fine.
    }

    // Use our type guard to safely get the error message.
    const errorMessage = isApiErrorResponse(errorDetails)
      ? errorDetails.message
      : `HTTP error! status: ${response.status}`;

    const errorDetailsTyped = isApiErrorResponse(errorDetails) ? errorDetails : null;

    throw new ApiError(errorMessage, response.status, errorDetailsTyped);
  }

  // If the response is OK but has no content (like a 204 status), return null.
  const text = await response.text();
  return text ? (JSON.parse(text) as T) : (null as T);
}

export default apiClient;

// src/features/admin/service/auth-api.ts
import apiClient from '@/services/api-client';

interface LoginRequest {
  username: string;
  password?: string;
}

interface LoginResponse {
  token: string;
}

/**
 * Sends a login request to the backend.
 * @param {LoginRequest} credentials - The user's username and password.
 * @returns {Promise<LoginResponse>} A promise that resolves with the login response, containing the JWT.
 * @throws {ApiError} Throws an error if the login fails.
 */
export async function loginUser(credentials: LoginRequest): Promise<LoginResponse> {
  try {
    return await apiClient<LoginResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    });
  } catch (error) {
    throw error;
  }
}

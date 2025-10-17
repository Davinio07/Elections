// src/features/admin/service/user-api.ts
import apiClient, { ApiError } from '@/services/api-client';

/**
 * @interface UserData
 * @description Defines the structure of a user object.
 * This is used when sending data to the backend to create a user.
 */
export interface UserData {
  /**
   * The user's unique ID. This is optional because a user
   * does not have an ID before they are created.
   * @type {number | undefined}
   */
  id?: number;

  /**
   * The user's chosen username.
   * @type {string}
   */
  username: string;

  /**
   * The user's email address.
   * @type {string}
   */
  email: string;

  /**
   * The user's password. This is optional because we only send it
   * when creating the user, not when we get user data back from the server.
   * @type {string | undefined}
   */
  password?: string;
}

/**
 * Sends a request to the backend to register a new user.
 * @param {UserData} userData - An object containing the new user's username, email, and password.
 * @returns {Promise<UserData>} A promise that resolves with the new user's data if successful.
 * @throws {ApiError} Throws a custom ApiError if the registration fails.
 */
export async function registerUser(userData: UserData): Promise<UserData> {
  try {
    return await apiClient<UserData>('/users', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
  } catch (error) {
    // The error is re-thrown so the Vue component that called this function
    // can catch it and display a message to the user.
    throw error;
  }
}

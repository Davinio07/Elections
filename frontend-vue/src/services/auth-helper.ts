export function getJwtToken(): string | null {
  // Dit veronderstelt dat de JWT token is opgeslagen in localStorage na een succesvolle login
  return localStorage.getItem('authToken');
}

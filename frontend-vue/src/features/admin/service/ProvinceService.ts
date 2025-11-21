import apiClient from '@/services/api-client';

export interface GemeenteDto {
  id: number;
  name: string;
}

export interface KieskringDto {
  kieskring_id: number;
  name: string;
  // We extend this to hold the children
  gemeentes?: GemeenteDto[];
}

export interface ProvinceDto {
  province_id: number;
  name: string;
  kieskringen?: KieskringDto[];
  isLoadingChildren?: boolean;
}

/**
 * Fetches the list of all provinces.
 */
export async function getProvinces(): Promise<ProvinceDto[]> {
  return apiClient<ProvinceDto[]>('/provinces');
}

/**
 * Fetches the kieskringen for a specific province ID.
 */
export async function getKieskringenForProvince(provinceId: number): Promise<KieskringDto[]> {
  return apiClient<KieskringDto[]>(`/provinces/${provinceId}/kieskringen`);
}

/**
 * Fetches the gemeentes for a specific kieskring ID.
 */
export async function getGemeentesForKieskring(kieskringId: number): Promise<GemeenteDto[]> {
  return apiClient<GemeenteDto[]>(`/kieskring/${kieskringId}/gemeentes`);
}

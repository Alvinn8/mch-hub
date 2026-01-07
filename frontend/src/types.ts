export type RepoOwnerType = 'user' | 'org';

export interface User {
  id: number;
  username: string;
  displayName: string;
}

export interface Organization {
  id: number;
  slug: string;
  displayName: string;
  description?: string;
  memberCount?: number;
}

export interface Repository {
  id: number;
  ownerType: RepoOwnerType;
  ownerName: string;
  name: string;
  description?: string;
  storagePath?: string;
}

export interface Commit {
  id: number;
  hash: string;
  message?: string;
  committedAt?: string;
}

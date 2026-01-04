import axios, { type AxiosResponse } from 'axios';
import type { Commit, EntityOwnerType, Organization, Repository, User } from './types';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api'
});

type ApiResponse<T> = Promise<AxiosResponse<T>>;

export const fetchUsers = (): ApiResponse<User[]> => api.get('/users');
export const fetchOrgs = (): ApiResponse<Organization[]> => api.get('/orgs');
export const fetchUser = (username: string): ApiResponse<User> => api.get(`/users/${username}`);
export const fetchOrg = (slug: string): ApiResponse<Organization> => api.get(`/orgs/${slug}`);
export const fetchUserRepos = (username: string): ApiResponse<Repository[]> =>
  api.get(`/users/${username}/repos`);
export const fetchOrgRepos = (slug: string): ApiResponse<Repository[]> => api.get(`/orgs/${slug}/repos`);
export const fetchRepo = (
  ownerType: EntityOwnerType,
  owner: string,
  repo: string
): ApiResponse<Repository> => api.get(`/repos/${ownerType}/${owner}/${repo}`);
export const fetchCommits = (
  ownerType: EntityOwnerType,
  owner: string,
  repo: string
): ApiResponse<Commit[]> => api.get(`/repos/${ownerType}/${owner}/${repo}/commits`);
export const fetchCommit = (
  ownerType: EntityOwnerType,
  owner: string,
  repo: string,
  hash: string
): ApiResponse<Commit> => api.get(`/repos/${ownerType}/${owner}/${repo}/commits/${hash}`);

import axios, { type AxiosResponse } from 'axios';
import type { Commit, Organization, Repository, User } from './types';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api'
});

type ApiResponse<T> = Promise<AxiosResponse<T>>;

// In-memory repo password cache (do NOT persist)
const repoPasswords = new Map<string, string>();

export const getRepoPassword = (repoPath: string): string | undefined => {
  return repoPasswords.get(repoPath);
}

export const setRepoPassword = (repoPath: string, password: string) => {
  if (!repoPath) return;
  if (password) {
    repoPasswords.set(repoPath, password);
  } else {
    repoPasswords.delete(repoPath);
  }
};

const getAuthHeader = (repoPath: string) => {
  const pwd = repoPasswords.get(repoPath);
  if (!pwd) return undefined;
  // Basic auth: username is repoPath (owner/repo), password is repo password
  const token = btoa(`${repoPath}:${pwd}`);
  return { Authorization: `Basic ${token}` };
};

export const fetchUsers = (): ApiResponse<User[]> => api.get('/users');
export const fetchOrgs = (): ApiResponse<Organization[]> => api.get('/orgs');
export const fetchUser = (username: string): ApiResponse<User> => api.get(`/users/${username}`);
export const fetchOrg = (slug: string): ApiResponse<Organization> => api.get(`/orgs/${slug}`);
export const fetchUserRepos = (username: string): ApiResponse<Repository[]> =>
  api.get(`/users/${username}/repos`);
export const fetchOrgRepos = (slug: string): ApiResponse<Repository[]> => api.get(`/orgs/${slug}/repos`);
export const fetchRepo = (
  owner: string,
  repo: string
): ApiResponse<Repository> => {
  const repoPath = `${owner}/${repo}`;
  return api.get(`/repos/${owner}/${repo}`, { headers: getAuthHeader(repoPath) });
};
export const fetchCommits = (
  owner: string,
  repo: string
): ApiResponse<Commit[]> => {
  const repoPath = `${owner}/${repo}`;
  return api.get(`/repos/${owner}/${repo}/commits`, { headers: getAuthHeader(repoPath) });
};
export const fetchCommit = (
  owner: string,
  repo: string,
  hash: string
): ApiResponse<Commit> => {
  const repoPath = `${owner}/${repo}`;
  return api.get(`/repos/${owner}/${repo}/commits/${hash}`, { headers: getAuthHeader(repoPath) });
};

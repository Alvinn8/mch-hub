import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import HomePage from '../views/HomePage.vue';
import UserReposPage from '../views/UserReposPage.vue';
import OrgReposPage from '../views/OrgReposPage.vue';
import RepoPage from '../views/RepoPage.vue';
import CommitPage from '../views/CommitPage.vue';

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'home', component: HomePage },
  { path: '/users/:username', name: 'user', component: UserReposPage, props: true },
  { path: '/orgs/:slug', name: 'org', component: OrgReposPage, props: true },
  { path: '/:ownerType/:owner/:repo', name: 'repo', component: RepoPage, props: true },
  { path: '/:ownerType/:owner/:repo/commit/:hash', name: 'commit', component: CommitPage, props: true }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;

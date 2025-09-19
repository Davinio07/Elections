import { createRouter, createWebHistory } from 'vue-router';
import AdminDashboard from '@/features/admin/view/AdminDashboard.vue';
import ScaledElectionResults from "@/features/admin/view/ScaledElectionResults.vue";

const routes = [
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
  },
  {
    path: '/ScaledElectionResults',
    name: 'ScaledElectionResults',
    component: ScaledElectionResults,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

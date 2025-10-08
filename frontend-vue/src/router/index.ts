import { createRouter, createWebHistory } from 'vue-router';
import AdminDashboard from '@/features/admin/view/AdminDashboard.vue';
import ScaledElectionResults from "@/features/admin/view/ScaledElectionResults.vue";
import NationalElectionResults from "@/features/admin/view/NationalElectionResults.vue";

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
  {
    path: '/NationalElectionResults',
    name: 'NationalElectionResults',
    component: NationalElectionResults,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

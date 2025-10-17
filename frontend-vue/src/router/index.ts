import { createRouter, createWebHistory } from 'vue-router';
import AdminDashboard from '@/features/admin/view/AdminDashboard.vue';
import ScaledElectionResults from "@/features/admin/view/ScaledElectionResults.vue";
import KieskringDetails from "@/features/admin/view/KieskringDetails.vue";
import PartiesView from '@/features/admin/view/PartiesView.vue';
import NationalElectionResults from "@/features/admin/view/NationalElectionResults.vue";
import Candidates from '@/features/admin/view/Candidates.vue';
import MunicipalityElectionResults from '@/features/admin/view/MunicipalityElectionResults.vue';
import Registration from '@/features/admin/view/UserRegistration.vue';

const routes = [
  {
    path: '/register',
    name: 'Registration',
    component: Registration,
  },
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
    path: '/kieskring-details',
    name: 'KieskringDetails',
    component: KieskringDetails,
  },
  {
    path: '/parties',
    name: 'Parties',
    component: PartiesView,
  },
  {
    path: '/NationalElectionResults',
    name: 'NationalElectionResults',
    component: NationalElectionResults,
  },
  {
    path: '/candidates',
    name: 'Candidates',
    component: Candidates
  },
    {
    path: '/municipality-results',
    name: 'MunicipalityElectionResults',
    component: MunicipalityElectionResults,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

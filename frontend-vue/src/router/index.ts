import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/features/admin/view/HomeView.vue';
import AdminDashboard from '@/features/admin/view/AdminDashboard.vue';
import ScaledElectionResults from "@/features/admin/view/ScaledElectionResults.vue";
import KieskringDetails from "@/features/admin/view/ConstituencyDetails.vue";
import PartiesView from '@/features/admin/view/PartiesView.vue';
import NationalElectionResults from "@/features/admin/view/NationalElectionResults.vue";
import CandidateList from '@/features/admin/view/Candidates.vue';
import MunicipalityElectionResults from '@/features/admin/view/MunicipalityElectionResults.vue';
import Registration from '@/features/admin/view/UserRegistration.vue';
import UserLogin from '@/features/admin/view/UserLogin.vue';
import Province from "@/features/admin/view/Province.vue";
import ChatPage from '@/features/admin/view/ChatPage.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView,
  },
  {
    path: '/register',
    name: 'Registration',
    component: Registration,
  },
  {
  path: '/login',
  name: 'UserLogin',
  component: UserLogin,
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
    path: '/province',
    name: 'Province',
    component: Province,
  },
  {
    path: '/chat',
    name: 'Chat',
    component: ChatPage,
    meta: { requiresAuth: true }
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
    component: CandidateList
  },
    {
    path: '/municipality-results',
    name: 'MunicipalityElectionResults',
    component: MunicipalityElectionResults,
  },
  {
    // Dynamic route parameter :name catches "Amsterdam", "Urk", etc.
    path: '/municipality-results/:name',
    name: 'MunicipalityDetails',
    component: () => import('@/features/admin/view/MunicipalityResultDetails.vue'),
    props: true, // Passes :name as a prop to the component
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

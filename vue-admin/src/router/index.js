import { createRouter, createWebHistory } from 'vue-router'
import Layout from "@/components/layout/Layout"
import { workerDefaultHomePath, workerCanAccessPath } from '@/utils/workerPermissions'

const routes = [

    {
        path: '/login',
        name: 'Login',
        component: () => import("@/views/Login")
    },

    {
        path: '/',
        component: Layout,
        children: [
            {
                path: "",
                redirect: () =>
                    localStorage.getItem('role') === 'worker'
                        ? workerDefaultHomePath()
                        : '/film/list',
            },
            {
                path: '/film/list',
                component: () => import("@/views/film/List")
            },
            {
                path: '/film/arrange',
                component: () => import("@/views/film/Arrange")
            },
            {
                path: '/film/add',
                component: () => import("@/views/film/Add")
            },
            {
                path: '/film/poster',
                component: () => import("@/views/film/Poster")
            },

            {
                path: '/user/list',
                component: () => import("@/views/user/List")
            },

            {
                path: '/order/list',
                component: () => import("@/views/order/List")
            },
            {
                path: '/stats/dashboard',
                component: () => import("@/views/stats/Dashboard")
            },

            {
                path: '/worker/list',
                component: () => import("@/views/worker/List")
            },
            {
                path: '/worker/daily',
                component: () => import("@/views/worker/Daily")
            },
            {
                path: '/worker/add',
                component: () => import("@/views/worker/Add")
            },
            {
                path: '/api',
                component: () => import("@/views/Api")
            },
            {
                path: '/setting',
                component: () => import("@/views/Setting")
            },
            {
                path: '/403',
                component: () => import("@/views/error/403")
            },

            {
                path: '/404',
                component: () => import("@/views/error/404")
            },
        ]
    },

    {
        path: "/:pathMatch(.*)*",
        redirect: '/404'
    }

];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem("token")
    const isAuthenticated = token != null && token !== '' && token !== 'null'
    const publicPaths = ['/login', '/403', '/404']
    if (publicPaths.includes(to.path) || to.name === 'Login') {
        next()
        return
    }
    if (!isAuthenticated) {
        next({name: 'Login'})
        return
    }
    const role = localStorage.getItem('role')
    if (role === 'worker' && !workerCanAccessPath(to.path)) {
        next({ path: '/403', replace: true })
        return
    }
    next()
})

export default router

import { createRouter, createWebHistory } from 'vue-router'
import Layout from "@/components/layout/Layout"

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
                path: '/leaving',
                component: () => import("@/views/Leaving")
            },
            {
                path: '/evaluate',
                component: () => import("@/views/Evaluate")
            },
            {
                path: '/activity',
                component: () => import("@/views/Activity")
            },
            {
                path: '/phone',
                component: () => import("@/views/Phone")
            },
            {
                path: '/info',
                component: () => import("@/views/Info")
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
});

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
    next()
})

export default router

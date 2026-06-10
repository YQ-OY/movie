import { createRouter, createWebHistory } from 'vue-router'
import Layout from "@/components/layout/Layout"

const profileRedirect = (tab) => ({
  path: '/',
  query: { profile: tab },
})

const routes = [

    {
        path: '/login',
        name: 'Login',
        component: () => import("@/views/Login")
    },

    {
        path: '/register',
        name: 'Register',
        component: () => import("@/views/Register")
    },

    {
        path: '/pay',
        name: 'PayJump',
        component: () => import("@/views/pay/PayJump")
    },

    {
        path: '/pay/success',
        name: 'PaySuccess',
        component: () => import("@/views/pay/PaySuccess")
    },

    {
        path: '/',
        component: Layout,
        children: [
            {
                path: '',
                component: () => import("@/views/Home")
            },
            {
                path: '/film',
                component: () => import("@/views/film/Index"),
                children: [
                    {
                        path: 'info',
                        component: () => import("@/views/film/Info")
                    },
                    {
                        path: 'ticket',
                        component: () => import("@/views/film/Ticket")
                    }
                ]
            },
            {
                path: '/films',
                component: () => import("@/views/Films")
            },
            {
                path: '/active',
                component: () => import("@/views/Active")
            },
            {
                path: '/seat',
                component: () => import("@/views/Seat")
            },
            {
                path: '/leaving',
                component: () => import("@/views/Leaving")
            },
            {
                path: '/me',
                redirect: profileRedirect('setting'),
            },
            {
                path: '/me/cart',
                redirect: profileRedirect('cart'),
            },
            {
                path: '/me/order',
                redirect: profileRedirect('order'),
            },
            {
                path: '/me/setting',
                redirect: profileRedirect('setting'),
            },
        ]
    }

];

export default createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

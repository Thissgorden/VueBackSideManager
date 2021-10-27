import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import index from '../views/index'
import axios from "@/axios";

import store from '../store'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [{
            path: '/Index',
            name: 'Index',
            component: index
        },
            /*
          {
            path:'/user',
            component: () =>import('@/views/sys/UserManager')
          },
          {
            path:'/role',
            component: () =>import('@/views/sys/RoleManager')
          },
          {
            path:'/menu',
            component: () =>import('@/views/sys/Menu')
          },
             */
            {
                path: '/userCenter',
                name: 'usercenter',
                component: () => import('@/views/UserCenter')
            }
        ]
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/login.vue')
    },
    /*
    {
        path: '*',
        name: '404page',
        component: () => import('../views/404page')
    },
     */

]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

router.beforeEach((to, from, next) => {

    let hasRoute = store.state.menus.hasRoute

    if (!hasRoute) {

        axios.get("/sys/menu/nav", {
            headers: {
                Authorization: localStorage.getItem("token")
            }
        }).then(res => {

            //拿到动态生成的menuList
            store.commit("setMenuList", res.data.data.nav)

            //拿到权限
            store.commit("setLevel", res.data.data.authoritys)

            //动态绑定路由
            let newRoutes = router.options.routes

            res.data.data.nav.forEach(menu => {
                if (menu.children) {
                    menu.children.forEach(e => {

                        // 转成路由
                        let route = menuToRoute(e)

                        // 吧路由添加到路由管理中
                        if (route) {
                            newRoutes[0].children.push(route)
                        }

                    })
                }
            })


            router.addRoutes(newRoutes)

            hasRoute = true;
            store.commit("changeRouteStatus",hasRoute)
        })
    }
    next();
});

/**
 * 导航转路由
 * @param menu
 * @returns {null}
 */
const menuToRoute = (menu) => {
    if (menu.component === '' || menu.component === null) {
        return null;
    }
    let route = {
        name: menu.name,
        path: menu.path,
        meta: {
            icon: menu.icon,
            title: menu.title
        }
    }
    route.component = () => import('@/views/' + menu.component + '.vue')

    return route
}

export default router

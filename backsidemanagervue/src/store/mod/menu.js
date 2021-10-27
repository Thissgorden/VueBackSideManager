import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default {
    state: {
        menuList: [],
        level:[],

        hasRoute: false,

        editableTabsValue: 'Index',
        editableTabs: [{
            title: '首页',
            name: 'Index',
        }]
    },
    mutations: {
        setMenuList(state,menus){
            state.menuList = menus;
        },
        setLevel(state,level){
            state.level = level;
        },

        changeRouteStatus(state,hasRotue){
            state.hasRoute = hasRotue

            sessionStorage.setItem("hasRoute",hasRotue)
        },
        addTab(state,tab){

            let index = state.editableTabs.findIndex(e => e.name === tab.name);

            if(index === -1){



            state.editableTabs.push({
                title: tab.title,
                name: tab.name,
            });
        }

            state.editableTabsValue = tab.name;
        },
        resetState: (state) =>{
            state.menuList = ''
            state.level = ''
            state.hasRoute = ''
            state.editableTabs = []
            state.editableTabsValue = []
        }
    },
    actions: {},
    modules: {}
}


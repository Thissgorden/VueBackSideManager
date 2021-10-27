import Vue from 'vue'
import Vuex from 'vuex'
import menus from "./mod/menu"

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token:''
  },
  mutations: {
    SET_TOKEN: (state,token) => {
      state.token = token
      localStorage.setItem("token",token)
    }
  },
  actions: {
  },
  modules: {
    menus
  }
})

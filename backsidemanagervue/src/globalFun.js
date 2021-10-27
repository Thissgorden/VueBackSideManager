import Vue from 'vue'

Vue.mixin({
    methods:{
        hasAuth(perm){
            var authority = this.$store.state.menus.level
            console.log(authority)
            return authority.indexOf(perm) > -1
        }
    }
})
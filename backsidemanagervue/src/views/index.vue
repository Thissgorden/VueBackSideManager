<template>
  <div>
    <h2>欢迎使用后台管理系统</h2>
  </div>

</template>

<script>
export default {
  name:"index",
  data(){
    return{
      MenuList:{},
      Authority:{}
    }
  },
  created() {
    this.MenuList = this.$store.state.menus.menuList
    this.Authority = this.$store.state.menus.level
  }
}
</script>

<style scoped>

</style>
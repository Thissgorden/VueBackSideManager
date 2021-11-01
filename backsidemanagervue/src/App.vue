<template>
  <router-view/>
</template>

<script>
export default {
  name: "App",
  /*
  watch: {
    $route(to, from) {
      if (to.path !== '/login') {
          let obj = {

            name: to.name,
            title:to.meta.title,
          }
          this.$store.commit("addTab",obj)
      }
    }
  }*/
}


</script>

<style>

</style>

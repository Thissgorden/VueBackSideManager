<template>
  <el-tabs v-model="editableTabsValue" type="card" closable @tab-remove="removeTab" @tab-click="clickTab">
    <el-tab-pane
        v-for="(item, index) in editableTabs"
        :key="item.name"
        :label="item.title"
        :name="item.name"
    >
    </el-tab-pane>
  </el-tabs>
</template>

<script>
export default {
  data() {
    return {currentpage: '3123213'}

    //editableTabsValue: this.$store.state.menus.editableTabsValue,
    //editableTabs: this.$store.state.menus.editableTabs,
    //tabIndex: 2

  },
  computed: {
    editableTabs: {
      get() {
        return this.$store.state.menus.editableTabs
      },
      set(val) {
        this.$store.state.menus.editableTabs = val;
      },
    },
    editableTabsValue: {
      get() {
        this.currentpage = this.$store.state.menus.editableTabsValue
        return this.$store.state.menus.editableTabsValue;
      },
      set(val) {
        this.$store.state.menus.editableTabsValue = val;
      }
    }
  },
  methods: {

    removeTab(targetName) {
      let tabs = this.editableTabs;

      let activeName = this.$store.state.menus.editableTabsValue;
      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            let nextTab = tabs[index + 1] || tabs[index - 1];
            if (nextTab) {
              activeName = nextTab.name;
            }
          }
        });
      }
      this.$store.state.menus.editableTabsValue = activeName;
      this.$store.state.menus.editableTabs = tabs.filter(tab => tab.name !== targetName);
      this.currentpage = activeName;
      this.$router.push({name: activeName});
    },

    clickTab(target) {
      if (target.name !== this.currentpage)
        this.$router.push({name: target.name})
    }
  }
}
</script>
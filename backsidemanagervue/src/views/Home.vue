<template>
  <div>
    <el-container class="el-container">
      <el-aside width="200px">
        <Asidemenu></Asidemenu>
      </el-aside>
      <el-container>
        <el-header>
          <strong>GDLearn后台管理系统</strong>
          <div class="header-avatar">
            <el-avatar :src="userInfo.avatar"/>
            <el-dropdown>
              <span class="el-dropdown-link" v-html="userInfo.username">
                <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">

                <el-dropdown-item><router-link to="/userCenter">个人中心</router-link></el-dropdown-item>

                <el-dropdown-item @click.native="logout">退出</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-link href="" target="_blank">网站</el-link>
            <el-link href="" target="_blank">网站2</el-link>
          </div>
        </el-header>
        <el-main>
          <tabs></tabs>

          <div style="margin: 0 15px;">
            <router-view></router-view>
          </div>

        </el-main>

      </el-container>
    </el-container>
  </div>
</template>

<script>
import Asidemenu from "@/components/Asidemenu";
import tabs from "@/components/Tabs"

export default {
  name: "Home",
  components: {Asidemenu,tabs},
  data() {
    return {
      userInfo: {
        id: "",
        username: "123",
        avatar: ""
      }
    }
  },
  methods: {
    getUserinfo() {
      this.$axios.get("/sys/userInfo").then(res => {
        this.userInfo = res.data.data;
      });
    },
    logout(){
      this.$axios.post('/logout').then(()=>{
        localStorage.clear();
        sessionStorage.clear;
        this.$store.commit("resetState")

        this.$router.push("/login");
      });
    }
  },
  created() {
    this.getUserinfo();
  }
}
</script>

<style scoped>
.header-avatar {
  float: right;
  width: 210px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.el-menu-vertical-demo {
  height: 100%;
}

.el-dropdown-link {
  cursor: pointer;

}

.el-container {
  padding: 0;
  margin: 0;
  height: 97vh;
}

.el-header, .el-footer {
  background-color: #B3C0D1;
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-main{

  padding: 0px;
}

body > .el-container {
  margin-bottom: 40px;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}

a{
  text-decoration: none;
}
</style>
<template>
  <el-row type="flex" class="el-row" justify="center">

    <el-col id="loginLeft" :span="7">
      <h2>欢迎来到GDLearn后台管理系统</h2>
      <el-image :src="require('@/img/testimg1.jpg')"></el-image>

      <p>扶我起来我还能写</p>
      <p>演示图片，可以替换为二维码或者其他</p>
    </el-col>

    <el-col :span="1">

      <el-divider direction="vertical"></el-divider>

    </el-col>
    <el-col :span="8">

      <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="用户名" prop="username" style="width: 380px">
          <el-input v-model="loginForm.username"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password" style="width: 380px">
          <el-input type="password" v-model="loginForm.password"></el-input>
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <el-input v-model="loginForm.code" class="codeinput"></el-input>
          <el-image :src="captchaImg" @click="getCaptcha" class="codeimg"></el-image>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm('loginForm')">立即创建</el-button>
          <el-button @click="resetForm('loginForm')">重置</el-button>
        </el-form-item>
      </el-form>

    </el-col>

  </el-row>
</template>

<script>
import qs from 'qs'
export default {
  name: "login",
  data() {
    return {
      loginForm: {
        username: 'admin ',
        password: '11111',
        code: '55555',
        key:'55555'
      },
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur'}
        ],
        code: [
          {required: true, message: '请输入验证码', trigger: 'blur'},
          {min: 5, max: 5, message: '请输入正确的验证码', trigger: 'blur'}
        ]
      },
      captchaImg :''
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$axios.post('/login?'+qs.stringify(this.loginForm)).then(res =>{
            const jwt = res.headers['authorization']

            this.$store.commit('SET_TOKEN',jwt)



            this.$store.commit("addTab",{title: '首页', name: 'Index'})
            this.$router.push('/index')
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },

    /**
     * 获取验证码的方法
     */
    getCaptcha() {
      this.$axios.get('/captcha').then(res => {
        this.loginForm.key = res.data.data.key//存储返回的token
        this.captchaImg =res.data.data.base64Img
      });
    }
  },
  created() {
    this.getCaptcha();
  }
}
</script>

<style scoped>
.el-row {
  background-color: #fafafa;
  height: 97vh;
  display: flex;
  align-items: center;
}

#loginLeft {
  text-align: center;
  border-radius: 4px;
}

.codeimg {
  margin-left: 8px;
  width: 100px;
  float: left;
}

.codeinput {
  width: 90px;
  float: left;
}

</style>
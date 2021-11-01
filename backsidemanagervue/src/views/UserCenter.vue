<template>
  <div class="el-main">
    <h2>欢迎您，{{ this.$store.state.userInfo.username }}</h2>
    <el-form :model="numberValidateForm" :rules="rules" ref="numberValidateForm" label-width="100px"
             class="demo-ruleForm">
      <el-form-item
          label="原密码"
          prop="currentPass"
      >
        <el-input class="el-input" type="password" v-model="numberValidateForm.currentPass"></el-input>
      </el-form-item>

      <el-form-item
          label="新密码"
          prop="newPass"
      >
        <el-input class="el-input" type="password" v-model="numberValidateForm.newPass"
                  ></el-input>
      </el-form-item>
      <el-form-item
          label="确认新密码"
          prop="confirmPass"
      >
        <el-input class="el-input" type="password" v-model="numberValidateForm.confirmPass"
                  ></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm('numberValidateForm')">提交</el-button>
        <el-button @click="resetForm('numberValidateForm')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "UserCenter",
  data() {
    return {
      numberValidateForm: {
        currentPass: "",
        newPass: "",
        confirmPass: ""
      },
      rules: {
        currentPass: [
          {required: true, message: '请输入活动名称', trigger: 'blur'},
          {min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur'}
        ],
        newPass: [
          {required: true, message: '请输入活动名称', trigger: 'blur'},
          {min: 3, max: 15, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
        confirmPass: [
          {required: true, message: '请输入活动名称', trigger: 'blur'},
          {min: 3, max: 15, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
      }
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$axios.post("/sys/user/updatePass",this.numberValidateForm).then(res => {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 2000,
              onClose: () => {
                this.getRoleList()
              }
            })
          })
        } else {
          console.log('提交错误');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  },

}
</script>

<style scoped>
.el-main {
  padding: 100px 25%;
}

.el-input {
  width: 400px;
}
</style>
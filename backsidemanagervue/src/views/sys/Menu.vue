<template>
  <div>

    <el-form class="demo-form-inline">
      <el-form-item>
        <el-button type="primary" @click="createHandle">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table
        :data="tableData"
        style="width: 100%;margin-bottom: 20px;"
        row-key="id"
        stripe
        border
        default-expand-all
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column
          prop="name"
          label="名称"
          sortable
          width="180">
      </el-table-column>
      <el-table-column
          prop="perms"
          label="权限编码"
          sortable
          width="180">
      </el-table-column>

      <el-table-column
          prop="icon"
          label="图标">
      </el-table-column>

      <el-table-column
          prop="type"
          label="类型">
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.statu === 0">目录</el-tag>
          <el-tag size="small" type="success" v-else-if="scope.row.statu === 1">菜单</el-tag>
          <el-tag size="small" type="info" v-else-if="scope.row.statu === 2">按钮</el-tag>
        </template>
      </el-table-column>

      <el-table-column
          prop="path"
          label="菜单URL">
      </el-table-column>

      <el-table-column
          prop="component"
          label="菜单组件">
      </el-table-column>

      <el-table-column
          prop="orderNum"
          label="排序号">
      </el-table-column>

      <el-table-column
          prop="status"
          label="状态">
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.statu === 1" type="success">正常</el-tag>
          <el-tag size="small" v-else-if="scope.row.statu === 0" type="danger">禁用</el-tag>
        </template>
      </el-table-column>

      <el-table-column
          prop="control"
          label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="editHandle(scope.row.id)">编辑</el-button>
          <el-divider direction="vertical"></el-divider>
          <template>
            <el-popconfirm title="确定删除吗？" @confirm="delHandle(scope.row.id)">
              <el-button slot="reference" type="text">删除</el-button>
            </el-popconfirm>
          </template>

        </template>
      </el-table-column>

    </el-table>

    <!--    弹出对话框-->

    <el-dialog
        title="提示"
        :visible.sync="dialogVisible"
        width="600px"
        :before-close="handleClose">


      <el-form :model="editform" :rules="editFormRules" ref="editform" label-width="100px" class="demo-editform">

        <el-form-item label="上级菜单" prop="parentId">
          <el-select v-model="editform.parentId" placeholder="请选择活动区域">
            <template v-for="item in tableData">
              <el-option :label="item.name" :value="item.id"></el-option>
              <template v-for="child in item.children">
                <el-option :label="child.name" :value="child.id">
                  <span>{{ "-" + child.name }}</span>
                </el-option>
              </template>
            </template>
          </el-select>
        </el-form-item>

        <el-form-item label="菜单名称" prop="parentId">
          <el-input v-model="editform.name"></el-input>
        </el-form-item>

        <el-form-item label="权限编码" prop="perms">
          <el-input v-model="editform.perms"></el-input>
        </el-form-item>

        <el-form-item label="图标" prop="icon">
          <el-input v-model="editform.icon"></el-input>
        </el-form-item>

        <el-form-item label="菜单URL" prop="path">
          <el-input v-model="editform.path"></el-input>
        </el-form-item>

        <el-form-item label="菜单组件" prop="component">
          <el-input v-model="editform.component"></el-input>
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="editform.type">
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="状态" prop="statu">
          <el-radio-group v-model="editform.statu">
            <el-radio :label="0">禁用</el-radio>
            <el-radio :label="1">正常</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="排序号" prop="orderNum">
          <el-input-number v-model="editform.orderNum" @change="handleChange" :min="1" :max="10" label="描述文字"></el-input-number>
        </el-form-item>
      </el-form>


      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取消</el-button>
    <el-button type="primary" @click="submitForm('editform')">立即创建</el-button>
  </span>
    </el-dialog>

    <!--    对话框over-->

  </div>
</template>

<script>
export default {
  name: "Menu",
  data() {
    return {

      num: 1,

      dialogVisible: false,

      tableData: [{name:'数据加载中'}],
      editform: {},

      editFormRules: {
        parentId: [
          {required: true, message: '请选择上级菜单', trigger: 'blur'},
        ],
        name: [
          {required: true, message: '请输入名称', trigger: 'blur'}
        ],
        perms: [
          {required: true, message: '请输入权限编码', trigger: 'blur'}
        ],
        type: [
          {required: true, message: '请选择活动资源', trigger: 'blur'}
        ],
        orderNum: [
          {required: true, message: '请选择活动资源', trigger: 'blur'}
        ],
        statu: [
          {required: true, message: '请选择活动资源', trigger: 'blur'}
        ],
      },


    }
  },
  methods: {
    handleClose(done) {
      this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {
          });
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          //校验通过 提交                      通过二元表达式来检测是添加还是编辑
          this.$axios.post('/sys/menu/' + (this.editform.id?'update':'save'),this.editform).then(res =>{
            this.$message({
              message: '操作成功',
              type: 'success',
              duration:2000,
              onClose:()=>{
                this.getmenuTree()
              }
            });
          })
        } else {
          console.log('提交失败！请检查后重试');
          return false;
        }
        this.dialogVisible = false
      });
    },
    handleChange(value) {
      console.log(value);
    },
    getmenuTree() {
      this.$axios.get("/sys/menu/list").then(res => {
        this.tableData = res.data.data
      })
    },
    showdel() {
      this.delvisible = true;
    },
    hidedel() {
      this.delvisible = false;
      console.log(this.delvisible)
    },
    editHandle(id){
      this.$axios.get('/sys/menu/info/'+ id).then(res =>{
        this.editform = res.data.data

        this.dialogVisible = true;
      })
    },
    createHandle(){
      this.editform = {};
      this.dialogVisible = true;
    },
    delHandle(id){
      this.$axios.post('/sys/menu/delete' + id).then(res =>{
        this.$message({
          message: '操作成功',
          type: 'success',
          duration:2000,
          onClose:()=>{
            this.getmenuTree()
          }
        });
      })
    }
  },

  created() {
    this.getmenuTree();
  },
}
</script>

<style scoped>

</style>
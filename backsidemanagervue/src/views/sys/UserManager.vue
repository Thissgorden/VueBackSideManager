<template>
  <div>
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input v-model="searchForm.username" placeholder="名称" clearable style="width: 250px"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button @click="getCharList()">搜索</el-button>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="openHandler()" v-if="hasAuth('sys:user:save')">新增</el-button>
      </el-form-item>

      <el-form-item>
        <el-popconfirm title="确定批量删除吗？" v-if="hasAuth('sys:user:delete')" @confirm="delHandle(null)">
          <el-button type="danger" slot="reference" :disabled="delstatus">批量删除</el-button>
        </el-popconfirm>
      </el-form-item>
    </el-form>

    <el-table
        ref="multipleTable"
        :data="tableData"
        tooltip-effect="dark"
        style="width: 100%"
        stripe
        @selection-change="handleSelectionChange">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>

      <el-table-column
          label="头像"
          width="60"
          >
        <template slot-scope="scope">
          <el-avatar size="small" :src="scope.row.avatar"></el-avatar>
        </template>
      </el-table-column>

      <el-table-column
          prop="username"
          label="用户名"

          show-overflow-tooltip>
      </el-table-column>
      <el-table-column
          prop="code"
          label="角色名称"
          show-overflow-tooltip>
        <template scope="scope">
          <el-tag size="small" type="info" v-for="item in scope.row.roles">{{item.name}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column
          prop="email"
          label="邮箱"
          show-overflow-tooltip>
      </el-table-column>

      <el-table-column
          prop="phoneNum"
          label="手机号"
          show-overflow-tooltip>
      </el-table-column>

      <el-table-column
          prop="status"
          width="80"
          label="状态">
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.statu === 1" type="success">正常</el-tag>
          <el-tag size="small" v-else-if="scope.row.statu === 0" type="danger">禁用</el-tag>
        </template>
      </el-table-column>

      <el-table-column
          prop="created"
          label="创建日期"
          show-overflow-tooltip>
      </el-table-column>

      <el-table-column
          width="250"
          prop="control"
          label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="roleHandle(scope.row.id)">分配角色</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="text" @click="">重置密码</el-button>
          <el-divider direction="vertical" v-if="hasAuth('sys:user:edit')"></el-divider>
          <el-button type="text" v-if="hasAuth('sys:user:edit')" @click="editHandle(scope.row.id)">编辑</el-button>
          <el-divider direction="vertical" v-if="hasAuth('sys:user:delete')"></el-divider>
          <template>
            <el-popconfirm title="确定删除吗？" @confirm="delHandle(scope.row.id)">
              <el-button slot="reference" v-if="hasAuth('sys:user:delete')" type="text">删除</el-button>
            </el-popconfirm>
          </template>
        </template>
      </el-table-column>


    </el-table>


    <div style="margin-top: 20px;float: right">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="current"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>

    </div>

    <el-dialog
        title="提示"
        :visible.sync="dialogVisible"
        width="600px">

      <el-form :model="editForm" :rules="editFormRules" ref="editForm" label-width="100px">
        <el-form-item label="用户名" prop="username" label-width="100px">
          <el-input v-model="editForm.username" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email" label-width="100px">
          <el-input v-model="editForm.email" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phoneNum" label-width="100px">
          <el-input v-model="editForm.phoneNum" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="状态" prop="statu" label-width="100px">
          <el-radio-group v-model="editForm.statu" autocomplete="off">
            <el-radio :label=0>禁用</el-radio>
            <el-radio :label=1>正常</el-radio>
          </el-radio-group>
        </el-form-item>

      </el-form>


      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取消</el-button>
    <el-button type="primary" @click="submitForm('editForm')">提交</el-button>
  </span>
    </el-dialog>

    <el-form>
      <el-dialog
          title="分配权限"
          :visible.sync="roleDialog"
          width="600px">

        <el-tree
            :data="RoleTreeData"
            show-checkbox
            ref="RoleTree"
            :default-expand-all="true"
            node-key="id"
            :props="defaultProps">
        </el-tree>
        <span slot="footer" class="dialog-footer">
          <el-button @click="roleDialog = false">取 消</el-button>
          <el-button type="primary" @click="submitRoleForm(RoleForm)">确 定</el-button>
        </span>
      </el-dialog>
    </el-form>


  </div>
</template>

<script>
export default {
  name: "RoleManager",
  data() {
    return {
      total: 0,
      size: 10,
      current: 1,

      roleDialog: false,

      multipleSelection: [],

      dialogVisible: false,

      editFormRules: {
        username: [
          {required: true, message: '请输入名称', trigger: 'blur'}
        ],
        code: [
          {required: true, message: '请输入唯一编码', trigger: 'blur'}
        ],
        statu: [
          {required: true, message: '请选择状态', trigger: 'blur'}
        ],
      },

      defaultProps: {
        children: 'children',
        label: 'name'
      },
      RoleTreeData: [],

      RoleForm: [],

      editForm: {},
      searchForm: [],
      delstatus: true,
      tableData: [{
        date: '2016-05-03',
        name: '用户',
        code: 'normal',
        address: '上海市普陀区金沙江路 1518 弄',
        statu: 1
      }, {
        date: '2016-05-02',
        name: '管理员',
        code: 'admin',
        address: '上海市普陀区金沙江路 1518 弄',
        statu: 0
      },],

    }
  },
  methods: {
    /*
    toggleSelection(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
     */
    handleSelectionChange(val) {
      this.multipleSelection = val;

      this.delstatus = val.length === 0
    },
    handleSizeChange(val) {
      //console.log(`每页 ${val} 条`);
      this.size = val
      this.getRoleList()
    },
    handleCurrentChange(val) {
      //console.log(`当前页: ${val}`);
      this.current = val
      this.getRoleList()
    },

    openHandler() {
      this.editForm = {};
      this.dialogVisible = true;
    },
    editHandle(id) {
      this.$axios.get('/sys/user/info/' + id).then(res => {
        this.editForm = res.data.data
        this.dialogVisible = true
      })
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          //校验通过 提交                      通过二元表达式来检测是添加还是编辑
          this.$axios.post('/sys/user/' + (this.editForm.id ? 'update' : 'save'), this.editForm).then(res => {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 2000,
              onClose: () => {
                this.getRoleList()
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
    delHandle(id) {
      var ids = []
      if (id) {
        ids.push(id)
      } else {
        this.multipleSelection.forEach(row => {
          ids.push(row.id)
        })
      }
      this.$axios.post('/sys/user/delete', ids).then(res => {
        this.$message({
          message: '操作成功',
          type: 'success',
          duration: 2000,
          onClose: () => {
            this.getRoleList()
          }
        });
      })
    },
    //获取表格用户
    getCharList() {
      this.$axios.get('/sys/user/list',
          {
            params: {
              name: this.searchForm.name,
              current: this.current,
              size: this.size
            }
          }).then(res => {
        this.tableData = res.data.data.records
        this.size = res.data.data.size
        this.current = res.data.data.current
        this.total = res.data.data.total
      })
    },
    roleHandle(id) {
      this.roleDialog = true
      //获取可用角色信息
      this.$axios.get('/sys/role/list/'+ id).then(res =>{
        //this.$refs.permTree.setCheckedKeys(res.data.data.menuIds);

        this.RoleForm = res.data.data.records
      })

    },
    getRoleTree() {
      this.$axios.get('/sys/role/list').then(res => {
        this.RoleTreeData = res.data.data.records
      })
    },
    submitRoleForm(){
      //权限要给到某一个用户上边所以+RoleForm.id
      var menuIds = this.$refs.RoleTree.getCheckedKeys()
      this.$axios.post('/sys/user/role'+this.RoleForm.id,menuIds).then(res=>{
        this.$message({
          message: '操作成功',
          type: 'success',
          duration:2000,
          onClose:()=>{
            this.getRoleList()
          }
        });
      })

      this.roleDialog = false
    },
    resetChecked() {
      this.$refs.tree.setCheckedKeys([]);
    }
  },
  created() {
    this.getRoleTree()
    this.getCharList()
  }
}
</script>

<style scoped>

</style>
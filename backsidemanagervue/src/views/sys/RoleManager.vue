<template>
  <div>
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input v-model="searchForm.name" placeholder="名称" clearable style="width: 250px"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button @click="getRoleList()">搜索</el-button>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="openHandler()">新增</el-button>
      </el-form-item>

      <el-form-item>
        <el-popconfirm title="确定批量删除吗？" @confirm="delHandle(null)">
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
          prop="name"
          label="名称"
          width="120">
      </el-table-column>
      <el-table-column
          prop="code"
          label="唯一编码"
          width="120"
          show-overflow-tooltip>
      </el-table-column>
      <el-table-column
          prop="remark"
          label="描述"
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
          prop="control"
          label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="permHandle(scope.row.id)">分配权限</el-button>
          <el-divider direction="vertical"></el-divider>
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

      <el-form :model="editForm" :rules="editFormRules" ref="editForm" label-width="100px" class="demo-editform">
        <el-form-item label="角色名称" prop="name" label-width="100px">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>

        <el-form-item label="唯一编码" prop="code" label-width="100px">
          <el-input v-model="editForm.code"></el-input>
        </el-form-item>

        <el-form-item label="描述" prop="remark" label-width="100px">
          <el-input v-model="editForm.remark"></el-input>
        </el-form-item>

        <el-form-item label="状态" prop="statu" label-width="100px">
          <el-radio-group v-model="editForm.statu">
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
          :visible.sync="permDialog"
          width="600px">

        <el-tree
            :data="permTreeData"
            show-checkbox
            ref="permTree"
            :default-expand-all="true"
            node-key="id"
            :props="defaultProps">
        </el-tree>
        <span slot="footer" class="dialog-footer">
          <el-button @click="permDialog = false">取 消</el-button>
          <el-button type="primary" @click="submitPermForm(permForm)">确 定</el-button>
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

      permDialog: false,

      multipleSelection: [],

      dialogVisible: false,

      editFormRules: {
        name: [
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
      permTreeData: [],

      permForm: [],

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
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          //校验通过 提交                      通过二元表达式来检测是添加还是编辑
          this.$axios.post('/sys/role/' + (this.editForm.id ? 'update' : 'save'), this.editForm).then(res => {
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
      this.$axios.post('/sys/role/delete', ids).then(res => {
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
    getRoleList() {
      this.$axios.get('/sys/role/list',
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
    editHandle(id) {
      this.$axios.get('/sys/role/info/' + id).then(res => {
        this.editForm = res.data.data
        this.dialogVisible = true
      })
    },
    permHandle(id) {
      this.permDialog = true

      this.$axios.get('/sys/role/info/'+ id).then(res =>{
        this.$refs.permTree.setCheckedKeys(res.data.data.menuIds);

        this.permForm = res.data.data
      })

    },
    getPermTree() {
      this.$axios.get('/sys/menu/list').then(res => {
        this.permTreeData = res.data.data
      })
    },
    submitPermForm(){
      //权限要给到某一个用户上边所以+permForm.id
      var menuIds = this.$refs.permTree.getCheckedKeys()
      this.$axios.post('/sys/role/perm/'+this.permForm.id,menuIds).then(res=>{
        this.$message({
          message: '操作成功',
          type: 'success',
          duration:2000,
          onClose:()=>{
            this.getRoleList()
          }
        });
      })

      this.permDialog = false
    },
    resetChecked() {
      this.$refs.tree.setCheckedKeys([]);
    }
  },
  created() {
    this.getPermTree()
    this.getRoleList()
  }
}
</script>

<style scoped>

</style>
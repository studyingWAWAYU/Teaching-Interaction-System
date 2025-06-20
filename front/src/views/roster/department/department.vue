<template>
  <div class="department-manage-page">
    <!-- Top action bar -->
    <div class="top-bar">
      <div class="search-area">
        <Input
          v-model="searchKey"
          suffix="ios-search"
          @on-change="search"
          placeholder="Search by institute name"
          clearable
          style="width: 300px"
        />
      </div>
      <div class="action-buttons">
        <Button
          @click="add"
          type="primary"
          icon="md-add"
          size="small"
          shape="round"
          :disabled="!$route.meta.permTypes.includes('add')"
        >
          Add Institute
        </Button>
        <Button
          @click="delAll"
          icon="md-trash"
          type="error"
          size="small"
          shape="round"
          style="margin-left: 8px"
          :disabled="!$route.meta.permTypes.includes('delete')"
        >
          Delete
        </Button>
        <Button
          @click="getParentList"
          icon="md-refresh"
          type="info"
          size="small"
          shape="round"
          style="margin-left: 8px"
        >
          Refresh
        </Button>
        <Button
          @click="excelData"
          type="success"
          icon="md-paper-plane"
          size="small"
          shape="round"
          style="margin-left: 8px"
        >
          Export Users
        </Button>
        <i-switch v-model="strict" size="large" style="margin-left:8px">
          <span slot="open">Cascading</span>
          <span slot="close">Single Selection</span>
        </i-switch>
      </div>
    </div>

    <!-- Main content area -->
    <div class="main-content">
      <Row :gutter="16">
        <Col span="8">
          <Card>
            <div class="tree-container">
              <Alert show-icon type="success" v-show="form.id">
                Current Selection
                <span class="select-title">{{editTitle}}</span>
                <a class="select-clear" @click="cancelEdit"> Cancel Selection</a>
              </Alert>
              <div class="tree-bar" :style="{maxHeight: maxHeight}">
                <Tree 
                  ref="tree" 
                  :data="data" 
                  :load-data="loadData" 
                  show-checkbox 
                  @on-check-change="changeSelect" 
                  @on-select-change="selectTree" 
                  :check-strictly="!strict"
                ></Tree>
                <Spin size="large" fix v-if="loading"></Spin>
              </div>
            </div>
          </Card>
        </Col>
        <Col span="16">
          <Card>
            <Form ref="form" :model="form" :label-width="100" :rules="formValidate">
              <Row :gutter="16">
                <Col span="12">
                  <FormItem label="Parent Institute" prop="parentTitle">
                    <div style="display:flex;">
                      <Input v-model="form.parentTitle" readonly style="margin-right:10px;" />
                      <Poptip transfer trigger="click" placement="right-start" title="Select Parent Institute" width="250">
                        <Button icon="md-list">Select Institute</Button>
                        <div slot="content" style="position:relative;min-height:5vh">
                          <Tree :data="dataEdit" :load-data="loadData" @on-select-change="selectTreeEdit"></Tree>
                          <Spin size="large" fix v-if="loadingEdit"></Spin>
                        </div>
                      </Poptip>
                    </div>
                  </FormItem>
                </Col>
                <Col span="12">
                  <FormItem label="Institute Name" prop="title">
                    <Input v-model="form.title" />
                  </FormItem>
                </Col>
              </Row>
              <Row :gutter="16">
                <Col span="12">
                  <FormItem label="Institute Leader" prop="mainHeader">
                    <Select 
                      :loading="userLoading" 
                      not-found-text="No user data for this institute" 
                      v-model="form.mainHeader" 
                      multiple 
                      filterable 
                      placeholder="Select Institute Leader"
                    >
                      <Option v-for="item in users" :value="item.id" :key="item.id" :label="item.nickname">
                        <span style="margin-right:10px;">{{ item.nickname }}</span>
                        <span style="color:#ccc;">{{ item.username }}</span>
                      </Option>
                    </Select>
                  </FormItem>
                </Col>
                <Col span="12">
                  <FormItem label="Institute Supervisor" prop="viceHeader">
                    <Select 
                      :loading="userLoading" 
                      not-found-text="No user data for this institute" 
                      v-model="form.viceHeader" 
                      multiple 
                      filterable 
                      placeholder="Select Institute Supervisor"
                    >
                      <Option v-for="item in users" :value="item.id" :key="item.id" :label="item.nickname">
                        <span style="margin-right:10px;">{{ item.nickname }}</span>
                        <span style="color:#ccc;">{{ item.username }}</span>
                      </Option>
                    </Select>
                  </FormItem>
                </Col>
              </Row>
              <Row :gutter="16">
                <Col span="8">
                  <FormItem label="Sort Order" prop="sortOrder">
                    <Tooltip trigger="hover" placement="right" content="Smaller value comes first, supports decimals">
                      <InputNumber :max="1000" :min="0" v-model="form.sortOrder"></InputNumber>
                    </Tooltip>
                  </FormItem>
                </Col>
                <Col span="8">
                  <FormItem label="Status" prop="status">
                    <i-switch size="large" v-model="form.status" :true-value="0" :false-value="-1">
                      <span slot="open">Enabled</span>
                      <span slot="close">Disabled</span>
                    </i-switch>
                  </FormItem>
                </Col>
                <Col span="8">
                  <Form-item class="br">
                    <Button 
                      @click="submitEdit" 
                      :loading="submitLoading" 
                      type="success" 
                      shape="round" 
                      icon="ios-create-outline" 
                      size="small" 
                      :disabled="!$route.meta.permTypes.includes('edit')"
                    >
                      Save
                    </Button>
                  </Form-item>
                </Col>
              </Row>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>

    <!-- Institute user list -->
    <Divider dashed>Users in Institute</Divider>
    <Card>
      <Row>
        <Table 
          :loading="userLoading" 
          border 
          :columns="userColumns" 
          :data="userData" 
          ref="table" 
          sortable="custom" 
          @on-sort-change="changeSort" 
          @on-selection-change="changeSelect" 
          @on-row-click="rowClick" 
          :row-class-name="rowClassName"
        ></Table>
      </Row>
      <Row type="flex" justify="end" class="page">
        <Page 
          :current="searchForm.pageNumber" 
          :total="userTotal" 
          :page-size="searchForm.pageSize" 
          @on-change="changePage" 
          @on-page-size-change="changePageSize" 
          :page-size-opts="[10,20,50]" 
          size="small" 
          show-total 
          show-elevator 
          show-sizer
        ></Page>
      </Row>
    </Card>

    <!-- Add institute modal -->
    <Modal 
      :title="modalTitle" 
      v-model="addOrEditDepartmentModal" 
      :mask-closable="false" 
      :width="500" 
      on-text="Submit" 
      @on-ok="submitAdd"
    >
      <Form ref="formAdd" :model="formAdd" :label-width="85" :rules="formValidate">
        <Row :gutter="16" v-show="showParent">
          <Col span="24">
            <FormItem label="Parent Institute:">{{form.title}}</FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Institute Name" prop="title">
              <Input v-model="formAdd.title" />
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="12">
            <FormItem label="Sort Order" prop="sortOrder">
              <Tooltip trigger="hover" placement="right" content="Smaller value comes first, supports decimals">
                <InputNumber :max="1000" :min="0" v-model="formAdd.sortOrder"></InputNumber>
              </Tooltip>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem label="Status" prop="status">
              <i-switch size="large" v-model="formAdd.status" :true-value="0" :false-value="-1">
                <span slot="open">Enabled</span>
                <span slot="close">Disabled</span>
              </i-switch>
            </FormItem>
          </Col>
        </Row>
      </Form>
    </Modal>
  </div>
</template>

<script>
import {
  initDepartment,
  addDepartment,
  editDepartment,
  deleteDepartment,
  searchDepartment,
  getUserByDepartmentId,
  getMyUserListData
} from "./api.js";
export default {
  name: "department-manage",
  data() {
    return {
      userLoading: true,
      searchForm: {
        pageNumber: 1,
        pageSize: 10,
        sort: "createTime",
        order: "desc",
        departmentId: ""
      },
      userData: [],
      userTotal: 0,
      selectList: [],
      selectCount: 0,
      selectRow: {},
      loading: true,
      maxHeight: "500px",
      strict: true,
      userLoading: false,
      loadingEdit: true,
      addOrEditDepartmentModal: false,
      selectList: [],
      selectCount: 0,
      showParent: false,
      modalTitle: "",
      editTitle: "",
      searchKey: "",
      form: {
        id: "",
        title: "",
        parentId: "",
        parentTitle: "",
        sortOrder: 0,
        status: 0
      },
      formAdd: {},
      formValidate: {
        title: [{
          required: true,
          message: "Name cannot be empty",
          trigger: "blur"
        }],
        sortOrder: [{
          required: true,
          type: "number",
          message: "Sort order cannot be empty",
          trigger: "blur"
        }]
      },
      submitLoading: false,
      data: [],
      dataEdit: [],
      users: [],
      userColumns: [{
        type: "selection",
        width: 60,
        align: "center",
        fixed: "left"
      },
      {
        type: "index",
        width: 60,
        align: "center",
        fixed: "left"
      },
      {
        title: "User Name",
        key: "nickname",
        minWidth: 125,
        sortable: true,
        fixed: "left"
      },
      {
        title: "Login Account",
        key: "username",
        minWidth: 125,
        sortable: true
      },
      {
        title: "Avatar",
        key: "avatar",
        width: 80,
        align: "center",
        render: (h, params) => {
          return h("Avatar", {
            props: {
              src: params.row.avatar
            }
          });
        }
      },
      {
        title: "Affiliated Institute",
        key: "departmentTitle",
        minWidth: 120
      },
      {
        title: "Mobile",
        key: "mobile",
        minWidth: 125,
        sortable: true
      },
      {
        title: "Email",
        key: "email",
        minWidth: 180,
        sortable: true
      },
      {
        title: "Gender",
        key: "sex",
        width: 90,
        align: "center"
      },
      {
        title: "Type",
        key: "type",
        align: "center",
        width: 100,
        render: (h, params) => {
          if (params.row.type == 1) {
            return h("div", [
              h(
                "Tag", {
                  props: {
                    color: "magenta",
                    size: "medium"
                  }
                },
                "Administrator"
              ),
            ]);
          } else {
            return h("div", [
              h(
                "Tag", {
                  props: {
                    color: "blue",
                    size: "default"
                  }
                },
                "User"
              ),
            ]);
          }
        },
      },
      {
        title: "Status",
        key: "status",
        align: "center",
        width: 110,
        render: (h, params) => {
          return h("div", [
            h(
              "Tag", {
                props: {
                  color: (params.row.status == 0 ? "green" : "red"),
                  size: "medium"
                }
              },
              (params.row.status == 0 ? "Enabled" : "Disabled")
            ),
          ]);
        }
      }]
    };
  },
  methods: {
    init() {
      this.getParentList();
      this.getParentListEdit();
      this.getUserDataList();
    },
    getUserDataList() {
      var that = this;
      that.userLoading = true;
      getMyUserListData(that.searchForm).then(res => {
        that.userLoading = false;
        if (res.success) {
          that.userData = res.result.records;
          that.userTotal = res.result.total;
        }
      });
    },
    getParentList() {
      this.loading = true;
      initDepartment().then(res => {
        this.loading = false;
        if (res.success) {
          res.result.forEach(function(e) {
            if (e.isParent) {
              e.loading = false;
              e.children = [];
              e._loading = false;
            }
          });
          this.data = res.result;
        }
      });
    },
    getParentListEdit() {
      this.loadingEdit = true;
      initDepartment().then(res => {
        this.loadingEdit = false;
        if (res.success) {
          res.result.forEach(function(e) {
            if (e.isParent) {
              e.loading = false;
              e.children = [];
            }
          });
          let first = {
            id: "0",
            title: "First-level Institute"
          };
          res.result.unshift(first);
          this.dataEdit = res.result;
        }
      });
    },
    loadData(item, callback) {
      initDepartment({
        parentId: item.id
      }).then(res => {
        if (res.success) {
          res.result.forEach(function(e) {
            if (e.isParent) {
              e.loading = false;
              e.children = [];
              e._loading = false;
            }
          });
          callback(res.result);
        }
      });
    },
    search() {
      if (this.searchKey) {
        this.loading = true;
        searchDepartment({
          title: this.searchKey
        }).then(res => {
          this.loading = false;
          if (res.success) {
            this.data = res.result;
          }
        });
      } else {
        this.getParentList();
      }
    },
    excelData() {
      this.$refs.table.exportCsv({
        filename: "Institute User Export Result",
      });
    },
    selectTree(v) {
      if (v.length > 0) {
        for (let attr in v[0]) {
          if (v[0][attr] == null) {
            v[0][attr] = "";
          }
        }
        let str = JSON.stringify(v[0]);
        let data = JSON.parse(str);
        this.editTitle = data.title;
        this.userLoading = true;
        getUserByDepartmentId({
          departmentId: data.id
        }).then(res => {
          this.userLoading = false;
          if (res.success) {
            this.users = res.result;
            this.form = data;
          }
        });
        this.searchForm.departmentId = v[0].id;
        this.getUserDataList();
      } else {
        this.cancelEdit();
        this.searchForm.departmentId = "";
        this.getUserDataList();
      }
    },
    cancelEdit() {
      let data = this.$refs.tree.getSelectedNodes()[0];
      if (data) {
        data.selected = false;
      }
      this.$refs.form.resetFields();
      delete this.form.id;
      this.editTitle = "";
    },
    selectTreeEdit(v) {
      if (v.length > 0) {
        for (let attr in v[0]) {
          if (v[0][attr] == null) {
            v[0][attr] = "";
          }
        }
        let str = JSON.stringify(v[0]);
        let data = JSON.parse(str);
        this.form.parentId = data.id;
        this.form.parentTitle = data.title;
      }
    },
    handleReset() {
      this.$refs.form.resetFields();
      this.form.status = 0;
    },
    showSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    submitEdit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (!this.form.id) {
            this.$Message.warning("Please select an institute to modify first");
            return;
          }
          this.submitLoading = true;
          editDepartment(this.form).then(res => {
            this.submitLoading = false;
            if (res.success) {
              this.$Message.success("Edit successful");
              this.init();
              this.addOrEditDepartmentModal = false;
            }
          });
        }
      });
    },
    submitAdd() {
      this.$refs.formAdd.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          addDepartment(this.formAdd).then(res => {
            this.submitLoading = false;
            if (res.success) {
              this.$Message.success("Add successful");
              this.init();
              this.addOrEditDepartmentModal = false;
            }
          });
        }
      });
    },
    tableAdd(v) {
      this.form = v;
      this.add();
    },
    add() {
      if (this.form.id == "" || this.form.id == null) {
        this.addRoot();
        return;
      }
      this.modalTitle = "Add Sub-institute";
      this.showParent = true;
      this.formAdd = {
        parentId: this.form.id,
        sortOrder: 0,
        status: 0
      };
      this.addOrEditDepartmentModal = true;
    },
    addRoot() {
      this.modalTitle = "Add First-level Institute";
      this.showParent = false;
      this.formAdd = {
        parentId: 0,
        sortOrder: 0,
        status: 0
      };
      this.addOrEditDepartmentModal = true;
    },
    changeSelect(v) {
      this.selectCount = v.length;
      this.selectList = v;
    },
    remove(v) {
      this.selectCount = 1;
      this.selectList.push(v);
      this.delAll();
    },
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("You haven't selected any data to delete");
        return;
      }
      this.$Modal.confirm({
        title: "Confirm Delete",
        content: "Are you sure you want to delete the selected institutes?",
        loading: true,
        onOk: () => {
          let ids = "";
          this.selectList.forEach(function(e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          deleteDepartment({
            ids: ids
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Delete successful");
              this.selectList = [];
              this.selectCount = 0;
              this.cancelEdit();
              this.init();
            }
          });
        }
      });
    },
    rowClick(row, index) {
      this.selectRow = row;
    },
    rowClassName(row, index) {
      if (row.id == this.selectRow.id) {
        return "rowClassNameColor";
      }
      return "";
    },
  },
  mounted() {
    let height = document.documentElement.clientHeight;
    this.maxHeight = Number(height - 287) + "px";
    this.init();
  }
};
</script>

<style lang="less" scoped>
/* Global styles */
.department-manage-page {
  padding: 20px;
  background: #f8f9fa;
}

/* Top action bar */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 16px 20px;
  border-radius: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .search-area {
    flex: 1;
    margin-right: 20px;
  }

  .action-buttons {
    display: flex;
    gap: 8px;
  }

  @media (max-width: 768px) {
    flex-direction: column;
    align-items: flex-start;

    .search-area,
    .action-buttons {
      width: 100%;
    }

    .action-buttons {
      margin-top: 16px;
    }
  }
}

/* Main content area */
.main-content {
  margin-bottom: 16px;

  .tree-container {
    padding: 16px;
    background: #fff;
    border-radius: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .tree-bar {
    margin-top: 16px;
    overflow: auto;
    position: relative;
  }
}

/* Table styles */
.ivu-table {
  border: none;

  /* Header styles */
  thead th {
    background: #f9fafb;
    color: #333;
    font-weight: 500;
    border-bottom: 1px solid #eee;
    padding: 12px 8px;
  }

  /* Table row styles */
  tbody tr {
    &:hover {
      background: #f7fafc;
    }

    &.rowClassNameColor td {
      background: #e6f7ff !important;
      color: #1890ff !important;
    }
  }

  /* Table cell */
  td {
    border-bottom: 1px solid #eee;
    padding: 12px 8px;
  }
}

/* Button styles */
.ivu-btn {
  border-radius: 20px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

/* Pagination */
.page {
  padding: 16px 20px;
  background: #fff;
  border-radius: 20px;
  margin-top: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* Card styles */
.ivu-card {
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
}
</style>
<template>
  <div class="role-manage-page">
    <div class="top-bar">
      <div class="search-area">
        <Form
            ref="searchForm"
            :model="searchForm"
            inline
            class="search-form">
          <Form-item prop="name">
            <Input
                type="text"
                v-model="searchForm.name"
                clearable
                placeholder="Role Name"
                style="width: 200px" />
          </Form-item>
          <Form-item prop="description">
            <Input
                type="text"
                v-model="searchForm.description"
                clearable
                placeholder="Role Description"
                style="width: 200px" />
          </Form-item>
          <Form-item>
            <Button
                @click="init"
                type="success"
                icon="ios-search"
                size="small"
                shape="round"
                style="margin-left: 8px" >
              Search
            </Button>
            <Button
                @click="addRole"
                type="primary"
                icon="md-add"
                size="small"
                shape="round"
                style="margin-left: 8px">
              Add Role
            </Button>
          </Form-item>
        </Form>
      </div>
    </div>

    <!-- Data table -->
    <div class="table-wrapper">
      <Table
          :loading="loading"
          border
          stripe
          :height="height"
          :columns="columns"
          :data="data"
          ref="table"
          sortable="custom"
          @on-sort-change="changeSort"
      ></Table>
    </div>

    <!-- Pagination -->
    <div class="pagination">
      <Page
          :current="searchForm.pageNumber"
          :total="total"
          :page-size="searchForm.pageSize"
          @on-change="changePage"
          @on-page-size-change="changePageSize"
          :page-size-opts="[15,20,50]"
          size="small"
          show-total
          show-elevator
          show-sizer ></Page>
    </div>

    <!-- Role edit modal -->
    <Modal
        :title="modalTitle"
        v-model="roleModalVisible"
        :mask-closable="false"
        :width="500"
        ok-text="Save Role Data"
        @on-ok="submitRole"
        @on-cancel="cancelRole" >
      <Form ref="roleForm" :model="roleForm" :label-width="80" :rules="roleFormValidate">
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Role Name" prop="name">
              <Input v-model="roleForm.name" placeholder="Role name, it is recommended to start with ROLR_..." />
            </FormItem>
            <FormItem label="Remark" prop="description">
              <Input v-model="roleForm.description" />
            </FormItem>
          </Col>
        </Row>
      </Form>
    </Modal>

    <!-- Menu permission modal -->
    <Modal
        :title="modalTitle"
        v-model="permModalVisible"
        :mask-closable="false"
        :width="600"
        ok-text="Save Menu Permissions"
        @on-ok="submitPermEdit"
        @on-cancel="cancelPermEdit" >
      <div style="max-height:500px;overflow-y: scroll;">
        <Tree
            ref="tree"
            :data="permData"
            show-checkbox
            :render="renderContent"
            :check-strictly="true"></Tree>
        <Spin size="large" fix v-if="treeLoading"></Spin>
      </div>
    </Modal>
  </div>
</template>

<script>
import {
  getRoleList,
  getAllPermissionList,
  addRole,
  editRole,
  deleteRole,
  editRolePerm
} from "./api.js";
import util from "@/libs/util.js";

export default {
  name: "role-manage",
  data() {
    return {
      loading: true,
      treeLoading: true,
      submitPermLoading: false,
      modalType: 0,
      roleModalVisible: false,
      permModalVisible: false,
      modalTitle: "",
      height: 600,
      roleForm: {
        name: "",
        description: ""
      },
      roleFormValidate: {
        name: [{
          required: true,
          message: "Role name cannot be empty",
          trigger: "blur"
        }]
      },
      submitLoading: false,
      columns: [
        {
          title: "ID",
          key: "id",
          width: 80,
          align: "center"
        },
        {
          title: "Role Name",
          key: "name",
          minWidth: 150,
          sortable: true
        },
        {
          title: "Role Description",
          key: "description",
          minWidth: 200
        },
        {
          title: "Creation Time",
          key: "createTime",
          width: 180,
          render: (h, params) => {
            return h("span", this.formatDate(params.row.createTime));
          }
        },
        {
          title: "Actions",
          key: "action",
          align: "center",
          fixed: "right",
          width: 350,
          render: (h, params) => {
            var that = this;
            return h("div", [
              h(
                  "Button", {
                    props: {
                      type: "success",
                      size: "small",
                      ghost: true,
                      shape: "circle",
                      icon: "ios-brush",
                      disabled: !(that.$route.meta.permTypes && that.$route.meta.permTypes.includes("enable"))
                    },
                    style: {
                      marginRight: "5px"
                    },
                    on: {
                      click: () => {
                        this.editPerm(params.row);
                      }
                    }
                  },
                  "Menu Permissions"
              ),
              h(
                  "Button", {
                    props: {
                      type: "warning",
                      size: "small",
                      ghost: true,
                      shape: "circle",
                      icon: "md-color-wand",
                      disabled: !(that.$route.meta.permTypes && that.$route.meta.permTypes.includes("edit"))
                    },
                    style: {
                      marginRight: "5px"
                    },
                    on: {
                      click: () => {
                        this.edit(params.row);
                      }
                    }
                  },
                  "Edit"
              ),
              h(
                  "Button", {
                    props: {
                      type: "error",
                      size: "small",
                      ghost: true,
                      shape: "circle",
                      icon: "ios-undo",
                      disabled: !(that.$route.meta.permTypes && that.$route.meta.permTypes.includes("delete"))
                    },
                    on: {
                      click: () => {
                        this.remove(params.row);
                      }
                    }
                  },
                  "Delete"
              )
            ]);
          }
        }
      ],
      data: [],
      total: 0,
      permData: [],
      editRolePermId: "",
      searchForm: {
        pageNumber: 1,
        pageSize: 15,
        sort: "createTime",
        order: "desc",
        name: "",
        description: ""
      },
    };
  },
  methods: {
    init() {
      this.getRoleList();
      this.getPermList();
    },
    renderContent(h, { root, node, data }) {
      let icon = "";
      if (data.level == 0) {
        icon = "ios-navigate";
      } else if (data.level == 1) {
        icon = "md-list-box";
      } else if (data.level == 2) {
        icon = "md-list";
      } else if (data.level == 3) {
        icon = "md-radio-button-on";
      } else {
        icon = "md-radio-button-off";
      }
      return h(
          "span", {
            style: {
              display: "inline-block",
              cursor: "pointer"
            },
            on: {
              click: () => {
                data.checked = !data.checked;
              }
            }
          },
          [
            h("span", [
              h("Icon", {
                props: {
                  type: icon,
                  size: "16"
                },
                style: {
                  "margin-right": "8px",
                  "margin-bottom": "3px"
                }
              }),
              h("span", data.title)
            ])
          ]
      );
    },
    changePage(v) {
      this.searchForm.pageNumber = v;
      this.getRoleList();
    },
    changePageSize(v) {
      this.searchForm.pageSize = v;
      this.getRoleList();
    },
    changeSort(e) {
      this.searchForm.sort = e.key;
      this.searchForm.order = e.order;
      if (e.order === "normal") {
        this.searchForm.order = "";
      }
      this.getRoleList();
    },
    getRoleList() {
      this.loading = true;
      getRoleList(this.searchForm).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    getPermList() {
      this.treeLoading = true;
      getAllPermissionList().then(res => {
        if (res.success) {
          this.deleteDisableNode(res.result);
          this.permData = res.result;
          this.treeLoading = false;
        }
        this.treeLoading = false;
      });
    },
    deleteDisableNode(permData) {
      let that = this;
      permData.forEach(function (e) {
        if (e.status == -1) {
          e.title = "[Disabled] " + e.title;
          e.disabled = true;
        }
        if (e.children && e.children.length > 0) {
          that.deleteDisableNode(e.children);
        }
      });
    },
    cancelRole() {
      this.roleModalVisible = false;
    },
    submitRole() {
      this.$refs.roleForm.validate(valid => {
        if (valid) {
          if (this.modalType == 0) {
            this.submitLoading = true;
            addRole(this.roleForm).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("Operation successful");
                this.getRoleList();
                this.roleModalVisible = false;
              }
            });
          } else {
            this.submitLoading = true;
            delete this.roleForm.permissions;
            editRole(this.roleForm).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("Operation successful");
                this.getRoleList();
                this.roleModalVisible = false;
              }
            });
          }
        }
      });
    },
    addRole() {
      this.modalType = 0;
      this.modalTitle = "Add Role";
      this.$refs.roleForm.resetFields();
      delete this.roleForm.id;
      this.roleModalVisible = true;
    },
    edit(v) {
      this.modalType = 1;
      this.modalTitle = "Edit Role";
      this.$refs.roleForm.resetFields();
      for (let attr in v) {
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);
      let roleInfo = JSON.parse(str);
      this.roleForm = roleInfo;
      this.roleModalVisible = true;
    },
    remove(v) {
      this.$Modal.confirm({
        title: "Confirm Delete",
        content: "Are you sure you want to delete the role " + v.name + " ?",
        loading: true,
        onOk: () => {
          deleteRole({
            ids: v.id
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Delete successful");
              this.getRoleList();
            }
          });
        }
      });
    },
    editPerm(v) {
      this.editRolePermId = v.id;
      this.modalTitle = v.name + " Menu Permission Modification";
      let rolePerms = v.permissions || [];
      if (this.treeLoading) {
        this.$Message.warning("Menu permission data is loading, please click again later");
        return;
      }
      this.checkPermTree(this.permData, rolePerms);
      this.permModalVisible = true;
    },
    checkPermTree(permData, rolePerms) {
      let that = this;
      permData.forEach(function (p) {
        if (that.hasPerm(p, rolePerms) && p.status != -1) {
          p.checked = true;
        } else {
          p.checked = false;
        }
        if (p.children && p.children.length > 0) {
          that.checkPermTree(p.children, rolePerms);
        }
      });
    },
    hasPerm(p, rolePerms) {
      let flag = false;
      for (let i = 0; i < rolePerms.length; i++) {
        if (p.id == rolePerms[i].permissionId) {
          flag = true;
          break;
        }
      }
      if (flag) {
        return true;
      }
      return false;
    },
    selectedTreeAll(permData, select) {
      let that = this;
      permData.forEach(function (e) {
        e.checked = select;
        if (e.children && e.children.length > 0) {
          that.selectedTreeAll(e.children, select);
        }
      });
    },
    submitPermEdit() {
      this.submitPermLoading = true;
      let selectedNodes = this.$refs.tree.getCheckedNodes();
      let permIds = selectedNodes.map(e => e.id);

      editRolePerm({
        roleId: this.editRolePermId,
        permIds: permIds
      }).then(res => {
        this.submitPermLoading = false;
        if (res.success) {
          this.$Message.success("Operation successful");
          this.getRoleList();
          this.permModalVisible = false;
        }
      });
    },
    cancelPermEdit() {
      this.permModalVisible = false;
    },
    formatDate(date) {
      return util.formatDate(date, "yyyy-MM-dd hh:mm:ss");
    }
  },
  mounted() {
    this.height = window.innerHeight - this.$refs.table.$el.offsetTop - 160;
    this.init();
  }
};
</script>

<style lang="less" scoped>
.role-manage-page {
  padding: 20px;
  background: #f8f9fa;
}

.top-bar {
  height:100px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 16px 20px;
  border-radius: 15px;
  margin-top:10px;
  margin-bottom: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .search-area {
    flex: 1;
    margin-left:20px;
    margin-right: 20px;

    .search-form {
      display: flex;
      flex-wrap: wrap;
      align-items: center;

      .ivu-form-item {
        margin-right: 16px;
        margin-bottom: 0;
      }
    }
  }

  @media (max-width: 768px) {
    flex-direction: column;
    align-items: flex-start;

    .search-area {
      width: 100%;
    }
  }
}

.table-wrapper {
  background: #fff;
  border-radius: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;

  .ivu-table {
    border: none;

    thead th {
      background: #f9fafb;
      color: #333;
      font-weight: 500;
      border-bottom: 1px solid #eee;
      padding: 12px 8px;
    }

    tbody tr {
      &:hover {
        background: #f7fafc;
      }
    }

    td {
      border-bottom: 1px solid #eee;
      padding: 12px 8px;
    }

    .ivu-btn {
      border-radius: 4px;
      padding: 4px 8px;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  margin-top: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.ivu-btn {
  border-radius: 20px;
  transition: all 0.3s ease;
  height:30px;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

.ivu-modal-body {
  max-height: 500px;
  overflow: auto;
}
</style>
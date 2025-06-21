<template>
  <div class="customer-list-page">
    <!-- Top action bar: Search + Buttons -->
    <div class="top-bar">
      <div class="search-area">
        <Form
          ref="searchForm"
          :model="searchForm"
          inline
          class="search-form"
        >
          <Form-item prop="nickname">
            <Input
              type="text"
              v-model="searchForm.nickname"
              clearable
              placeholder="Search Name"
              style="width: 200px"
            />
          </Form-item>
          <Form-item prop="department">
            <department-choose
              @on-change="handleSelectDep"
              placeholder="User Department"
              style="width: 200px;"
              ref="dep"
            ></department-choose>
          </Form-item>
          <Form-item>
            <Button
              @click="handleSearch"
              type="primary"
              icon="ios-search"
              size="small"
              shape="round"
              style="margin-left: 8px"
            >
              Search
            </Button>
            <Button
              @click="handleReset"
              type="warning"
              icon="md-refresh"
              size="small"
              shape="round"
              style="margin-left: 8px"
            >
              Reset
            </Button>
          </Form-item>
        </Form>
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
           Add User
        </Button>
        <Button
          @click="importModalVisible = true"
          type="success"
          icon="md-paper-plane"
          size="small"
          shape="round"
          style="margin-left: 8px"
        >
          Import
        </Button>
        <Button
          @click="excelData"
          type="success"
          icon="md-paper-plane"
          size="small"
          shape="round"
          style="margin-left: 8px"
        >
          Export
        </Button>
        <Button
          type="info"
          @click="showFilterPanelFlag = !showFilterPanelFlag"
          icon="md-settings"
          size="small"
          shape="round"
          style="margin-left: 8px"
        >
          Column Filter
        </Button>
      </div>
    </div>

    <!-- Column filter panel -->
    <transition>
      <div v-show="showFilterPanelFlag" class="filter-panel">
        <CheckboxGroup v-model="selected">
          <div v-for="item in mycolumns" :key="item.key">
            <Checkbox :label="item.title" style="margin: 2px 5px"></Checkbox>
          </div>
        </CheckboxGroup>
      </div>
    </transition>

    <!-- Data table -->
    <div class="table-wrapper">
      <Table
        :loading="loading"
        border
        :height="height"
        :columns="columns"
        :data="data"
        sortable="custom"
        @on-sort-change="changeSort"
        @on-selection-change="showSelect"
        ref="table"
        @on-row-click="rowClick"
        :row-class-name="rowClassName"
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
        :page-size-opts="[20, 30, 50]"
        size="small"
        show-total
        show-elevator
        show-sizer
      ></Page>
    </div>

    <!-- Import data drawer -->
    <Drawer title="Import Data" closable v-model="importModalVisible" width="1000">
      <div style="display:flex;justify-content: space-between;align-items: center;">
        <Upload action :before-upload="beforeUploadImport" accept=".xls, .xlsx">
          <Button :loading="reading" icon="ios-cloud-upload-outline" style="margin-right:10px">
            Upload Excel File
          </Button>
          <span v-if="uploadfile.name">Current selected file: {{ uploadfile.name }}</span>
        </Upload>
        <Button @click="clearImportData" icon="md-trash">Clear Data</Button>
      </div>
      <Table :columns="importColumns" border :height="height" :data="importTableData" ref="importTable"></Table>
      <div class="drawer-footer">
        <div style="position:absolute;right:15px;display: inline-block">
          <Button @click="importModalVisible=false">Close</Button>
          <Button
            :loading="importLoading"
            :disabled="importTableData.length<=0"
            @click="importData"
            style="margin-left:8px"
            type="primary"
          >
            Confirm Import
            <span v-if="importTableData.length>0">{{importTableData.length}} items</span>
          </Button>
        </div>
      </div>
    </Drawer>

    <!-- Add/Edit user component -->
    <addEdit :data="form" :type="showType" v-model="showUser" @on-submit="getUserList" />
  </div>
</template>

<script>
import {
  getUserList,
  enableUser,
  disableUser,
  deleteUser,
  importUserData,
  resetUserPass
} from "./api.js";
import departmentChoose from "@/views/template/department-choose";
import excel from "@/libs/excel";
import addEdit from "./addEdit.vue";
import dict from "@/views/template/dict";

export default {
  name: "user-manage",
  components: {
    departmentChoose,
    addEdit,
    dict
  },
  data() {
    return {
      selected: [
        "ID",
        "User Name",
        "Login Account",
        "Avatar",
        "Department",
        "Phone",
        "Email",
        "Gender",
        "Type",
        "Status",
        "Create Time",
        "Actions",
      ],
      showFilterPanelFlag: false,
      usingTutorialsModal: false,
      height: 600,
      selectRow: {},
      showUser: false,
      showType: "0",
      loading: true,
      reading: false,
      importLoading: false,
      loadingExport: true,
      exportModalVisible: false,
      importModalVisible: false,
      drop: false,
      selectCount: 0,
      selectList: [],
      searchForm: {
        nickname: "",
        departmentId: "",
        pageNumber: 1,
        pageSize: 20,
        sort: "createTime",
        order: "desc"
      },
      form: {},
      columns: [
        {
          title: "ID",
          type: "index",
          width: 80,
          align: "center",
          fixed: "left"
        },
        {
          title: "User Name",
          key: "nickname",
          minWidth: 150,
          sortable: true,
          fixed: "left"
        },
        {
          title: "Login Account",
          key: "username",
          minWidth: 150,
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
          title: "Department",
          key: "departmentTitle",
          minWidth: 120
        },
        {
          title: "Phone",
          key: "mobile",
          minWidth: 125,
          sortable: true
        },
        {
          title: "Email",
          key: "email",
          minWidth: 200,
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
          width: 120,
          render: (h, params) => {
            if (params.row.type == 1) {
              return h("div", [
                h(
                  "span", {
                    style: {
                      color: "#ff9900",
                    },
                  },
                  "Administrator"
                ),
              ]);
            } else {
              return h("div", [
                "Regular User"
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
            if (params.row.status == 0) {
              return h("div", [
                h(
                  "span", {
                    style: {
                      color: "#3CB371",
                    },
                  },
                  "Active"
                ),
              ]);
            } else {
              return h("div", [
                h(
                  "span", {
                    style: {
                      color: "#ff9900",
                    },
                  },
                  "Disabled"
                ),
              ]);
            }
          },
        },
        {
          title: "Create Time",
          key: "createTime",
          sortable: true,
          sortType: "desc",
          width: 180
        },
        {
          title: "Actions",
          key: "action",
          width: 350,
          align: "center",
          fixed: "right",
          render: (h, params) => {
            var that = this;
            return h("div", [
              h(
                "Button", {
                  props: {
                    type: "primary",
                    size: "small",
                    ghost: true,
                    shape: "circle",
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
                    type: "warning",
                    size: "small",
                    ghost: true,
                    shape: "circle"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.resetPass(params.row);
                    }
                  }
                },
                "Reset Password"
              ),
              h(
                "Button", {
                  props: {
                    size: "small",
                    ghost: true,
                    type: (params.row.status == 0 ? "error" : "success"),
                    shape: "circle"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      if (params.row.status == 0) {
                        this.disable(params.row);
                      } else {
                        this.enable(params.row);
                      }
                    }
                  }
                },
                (params.row.status == 0 ? "Disable" : "Enable")
              ),
              h(
                "Button", {
                  props: {
                    type: "error",
                    size: "small",
                    ghost: true,
                    shape: "circle",
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
      filename: "User Data",
      importTableData: [],
      importColumns: [],
      uploadfile: {
        name: ""
      },
      data: [],
      total: 0
    };
  },
  methods: {
    init() {
      this.getUserList();
    },
    excelData() {
      this.$refs.table.exportCsv({
        filename: "Export Result",
      });
    },
    handleSelectDep(v) {
      this.searchForm.departmentId = v;
    },
    changePage(v) {
      this.searchForm.pageNumber = v;
      this.getUserList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.searchForm.pageSize = v;
      this.getUserList();
    },
    getUserList() {
      this.loading = true;
      getUserList(this.searchForm).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    handleSearch() {
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 20;
      this.getUserList();
    },
    handleReset() {
      this.$refs.searchForm.resetFields();
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.$refs.dep.clearSelect();
      this.searchForm.departmentId = "";
      this.getUserList();
    },
    changeSort(e) {
      this.searchForm.sort = e.key;
      this.searchForm.order = e.order;
      if (e.order == "normal") {
        this.searchForm.order = "";
      }
      this.getUserList();
    },
    resetPass(e) {
      this.$Modal.confirm({
        title: "Confirm Reset",
        content: "The password will be reset to 123456",
        loading: true,
        onOk: () => {
          resetUserPass({
            ids: e.id
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Operation successful");
              this.clearSelectAll();
              this.getUserList();
            }
          });
        }
      });
    },
    beforeUploadImport(file) {
      this.uploadfile = file;
      const fileExt = file.name
        .split(".")
        .pop()
        .toLocaleLowerCase();
      if (fileExt == "xlsx" || fileExt == "xls") {
        this.readFile(file);
        this.file = file;
      } else {
        this.$Message.error("Not an Excel file");
      }
      return false;
    },
    readFile(file) {
      this.reading = true;
      const reader = new FileReader();
      reader.readAsArrayBuffer(file);
      reader.onerror = e => {
        this.reading = false;
        this.$Message.error("File reading error");
      };
      reader.onload = e => {
        const data = e.target.result;
        const {
          header,
          results
        } = excel.read(data, "array");
        const tableTitle = header.map(item => {
          return {
            title: item,
            key: item,
            minWidth: 100,
            align: "center"
          };
        });
        this.importTableData = results;
        this.importColumns = tableTitle;
        this.reading = false;
        this.$Message.success("Data reading successful");
      };
    },
    clearImportData() {
      this.importTableData = [];
      this.importColumns = [];
      this.uploadfile = {};
    },
    importData() {
      this.importLoading = true;
      importUserData(this.importTableData).then(res => {
        this.importLoading = false;
        if (res.success) {
          this.importModalVisible = false;
          this.getUserList();
          this.$Modal.info({
            title: "Import Result",
            content: res.message
          });
        }
      });
    },
    add() {
      this.showType = "2";
      this.showUser = true;
    },
    edit(v) {
        // Deep copy data to avoid affecting original data
        const data = JSON.parse(JSON.stringify(v));
        
        // Process role data: convert single role object to roles array
        if (data.role) {
            data.roles = [data.role];
        } else {
            data.roles = []; // Ensure roles field exists and is an array
        }
        
        // Process other fields, convert null values to empty strings
        for (let key in data) {
            if (data[key] === null) {
                data[key] = "";
            }
        }
        
        // Special handling for department fields
        if (!data.departmentId) {
            data.departmentId = "";
            data.departmentTitle = "";
        }
        
        // Assign to form data
        this.form = data;
        this.showType = "1";
        this.showUser = true;
    },
    enable(v) {
      this.$Modal.confirm({
        title: "Confirm Enable",
        content: "Enable user " + v.username + " ?",
        loading: true,
        onOk: () => {
          enableUser({
            id: v.id
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Enable successful");
              this.getUserList();
            }
          });
        }
      });
    },
    disable(v) {
      this.$Modal.confirm({
        title: "Confirm Disable",
        content: "Disable user " + v.username + " ?",
        loading: true,
        onOk: () => {
          disableUser({
            id: v.id
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Disable successful");
              this.getUserList();
            }
          });
        }
      });
    },
    remove(v) {
      this.$Modal.confirm({
        title: "Confirm Delete",
        content: "Delete user " + v.username + " ?",
        loading: true,
        onOk: () => {
          deleteUser({
            ids: v.id
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Delete successful");
              this.getUserList();
            }
          });
        }
      });
    },
    showSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
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
    this.height = window.innerHeight - this.$refs.table.$el.offsetTop - 160;
    this.init();
    this.mycolumns = this.columns;
    let showcolumns = [];
    for (var i = 0; i < this.selected.length; i++) {
      var item = this.selected[i];
      for (var j = 0; j < this.columns.length; j++) {
        if (this.columns[j].title == item) {
          showcolumns.push(this.columns[j]);
        }
      }
    }
    this.columns = showcolumns;
  },
  watch: {
    selected: function (newcolumns) {
      let showcolumns = [];
      for (var i = 0; i < this.mycolumns.length; i++) {
        var item = this.mycolumns[i];
        if (item.title == undefined) showcolumns.push(item);
        else if (newcolumns.includes(item.title)) showcolumns.push(item);
      }
      this.columns = showcolumns;
    },
  }
};
</script>

<style lang="less" scoped>
/* Global styles */
.customer-list-page {
  padding: 20px;
  background: #f8f9fa;
}

/* Top action bar */
.top-bar {
  height:100px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-top:10px;
  margin-bottom: 25px;
  border-radius:20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .search-area {
    .search-form {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      margin-left:10px;

      .ivu-form-item {
        margin-right: 16px;
        margin-bottom: 0;
      }
    }
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

/* Table styles */
.table-wrapper {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;

  .ivu-table {
    border: none;

    /* Header styles */
    thead th {
      background: #f9fafb;
      color: #333;
      font-weight: 500;
      border-bottom: 1px solid #eee;
      padding: 12px 8px;
      height:60px;
    }

    /* Table row styles */
    tbody tr {
      &:hover {
        background: #f7fafc;
      }

      &.rowClassNameColor td {
        background: #e6f7ff !important;
        color: #1890ff !important;
        height:70px;
      }
    }

    /* Table cell */
    td {
      border-bottom: 1px solid #eee;
      padding: 12px 8px;
      height:60px;
    }

    /* Action column buttons */
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

/* Column filter panel */
.filter-panel {
  position: absolute;
  top: 120px;
  right: 20px;
  width: 200px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  z-index: 999;
  

  .ivu-checkbox-group {
    display: flex;
    flex-direction: column;
  }
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  margin-top: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* Button styles */
.ivu-btn {
  border-radius: 20px;
  transition: all 0.3s ease;
  height:30px;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}
</style>
<template>
  <div class="dict-manage-page">
    <!-- Top action bar -->
    <div class="top-bar">
      <div class="search-area">
        <Input
          v-model="searchKey"
          suffix="ios-search"
          @on-change="search"
          placeholder="Search dictionary"
          clearable
          style="width: 300px"
        />
      </div>
      <div class="action-buttons">
        <Button
          @click="refreshDict"
          type="success"
          icon="md-refresh"
          size="small"
          shape="round"
        >
          Refresh
        </Button>
        <Button
          @click="addDict"
          type="primary"
          icon="md-add"
          size="small"
          shape="round"
          :disabled="!$route.meta.permTypes.includes('add')"
        >
          Add Dictionary
        </Button>
        <Button
          @click="editDict"
          type="warning"
          icon="ios-brush"
          size="small"
          shape="round"
          style="margin-left: 8px"
          :disabled="!this.selectNode.id || !$route.meta.permTypes.includes('edit')"
        >
          Edit
        </Button>
        <Button
          @click="delDict"
          icon="md-trash"
          type="error"
          size="small"
          shape="round"
          style="margin-left: 8px"
          :disabled="!$route.meta.permTypes.includes('delete')"
        >
          Delete
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
              <Alert show-icon type="success" v-show="editTitle">
                Current Selection
                <span class="select-title">{{editTitle}}</span>
                <a class="select-clear" @click="cancelEdit"> Cancel Selection</a>
              </Alert>
              <div class="tree-bar" :style="{maxHeight: maxHeight}">
                <Tree 
                  ref="tree" 
                  :data="treeData" 
                  @on-select-change="selectTree" 
                  empty-text="No dictionary data"
                ></Tree>
                <Spin size="large" fix v-if="treeLoading"></Spin>
              </div>
            </div>
          </Card>
        </Col>
        <Col span="16">
          <Card>
            <Row :gutter="16" class="search-row">
              <Form ref="searchForm" :model="searchForm" inline :label-width="0">
                <Form-item label="">
                  <Input type="text" v-model="searchForm.title" placeholder="Data Name" clearable style="width: 120px" />
                </Form-item>
                <Form-item label="">
                  <Input type="text" v-model="searchForm.value" placeholder="Data Value" clearable style="width: 120px" />
                </Form-item>
                <Form-item label="">
                  <Input type="text" v-model="searchForm.description" placeholder="Data Description" clearable style="width: 120px" />
                </Form-item>
                <Form-item label="">
                  <Select v-model="searchForm.status" placeholder="Status" clearable style="width: 120px">
                    <Option value="0">Normal</Option>
                    <Option value="-1">Disabled</Option>
                  </Select>
                </Form-item>
                <Form-item class="br">
                  <Button @click="handleSearch" type="info" icon="ios-search" size="small" shape="round">Search Data</Button>
                  <Button @click="handleReset" type="warning" icon="md-refresh" size="small" shape="round" style="margin-left: 8px">Reset</Button>
                  <Button @click="add" type="success" icon="md-add" size="small" shape="round" style="margin-left: 8px">Add Data</Button>
                  <Button @click="delAll" type="error" icon="md-trash" size="small" shape="round" style="margin-left: 8px">Delete Data</Button>
                </Form-item>
                <Alert type="success" show-icon v-show="selectCount > 0" class="select-count-alert">
                  <span class="select-count">{{selectCount}}</span> items selected
                  <a class="select-clear" @click="clearSelectAll">Clear</a>
                </Alert>
              </Form>
            </Row>
            <Row>
              <Table 
                :loading="loading" 
                stripe 
                border 
                :columns="columns" 
                :data="data" 
                sortable="custom" 
                @on-sort-change="changeSort" 
                @on-selection-change="showSelect" 
                ref="table" 
                @on-row-click="rowClick" 
                :row-class-name="rowClassName"
              ></Table>
            </Row>
            <Row type="flex" justify="end" class="page">
              <Page 
                :current="searchForm.pageNumber" 
                :total="total" 
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
        </Col>
      </Row>
    </div>

    <!-- Dictionary modal -->
    <Modal 
      :title="dictModalTitle" 
      v-model="dictModalVisible" 
      :mask-closable="false" 
      :width="500" 
      on-text="Submit" 
      @on-ok="handelSubmitDict"
    >
      <Form ref="dictForm" :model="dictForm" :label-width="85" :rules="dictFormValidate">
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Dictionary Name" prop="title">
              <Input v-model="dictForm.title" />
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Dictionary Type" prop="type" class="block-tool">
              <Tooltip placement="right" :max-width="220" transfer content="English name is recommended and must be unique. Modify with caution if not a developer">
                <Input v-model="dictForm.type" />
              </Tooltip>
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Remark" prop="description">
              <Input v-model="dictForm.description" />
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="12">
            <FormItem label="Sort Order" prop="sortOrder">
              <Tooltip trigger="hover" placement="right" content="Smaller value comes first, supports decimals">
                <InputNumber :max="1000" :min="0" v-model="dictForm.sortOrder"></InputNumber>
              </Tooltip>
            </FormItem>
          </Col>
        </Row>
      </Form>
    </Modal>

    <!-- Data modal -->
    <Modal 
      :title="modalTitle" 
      v-model="modalVisible" 
      :mask-closable="false" 
      :width="500" 
      on-text="Submit" 
      @on-ok="handelSubmit"
    >
      <Form ref="form" :model="form" :label-width="80" :rules="formValidate">
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Name" prop="title">
              <Input v-model="form.title" />
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Data Value" prop="value">
              <Input v-model="form.value" />
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Remark" prop="description">
              <Input v-model="form.description" />
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="12">
            <FormItem label="Sort Order" prop="sortOrder">
              <Tooltip trigger="hover" placement="right" content="Smaller value comes first, supports decimals">
                <InputNumber :max="1000" :min="0" v-model="form.sortOrder"></InputNumber>
              </Tooltip>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem label="Status" prop="status">
              <i-switch size="large" v-model="form.status" :true-value="0" :false-value="-1">
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
  getAllDictList,
  addDict,
  editDict,
  deleteDict,
  searchDict,
  getAllDictDataList,
  addDictData,
  editDictData,
  deleteData
} from "./api.js";
export default {
  name: "dic-manage",
  data() {
    return {
      selected: [
        "Select",
        "Index",
        "Belonging Dictionary",
        "Key",
        "Value",
        "Description",
        "Sort",
        "Is Disabled",
        "Action"
      ],
      treeLoading: false, // Tree loading status
      maxHeight: "500px",
      loading: false, // Table loading status
      editTitle: "", // Edited node name
      searchKey: "", // Search tree
      expand: true,
      span: 18,
      expandIcon: "md-arrow-round-back",
      selectNode: {},
      treeData: [], // Tree data
      selectCount: 0, // Multi-select count
      selectList: [], // Multi-select data
      searchForm: {
        // Data object corresponding to the search box
        name: "",
        status: "",
        pageNumber: 1, // Current page number
        pageSize: 10, // Page size
        sort: "sortOrder", // Default sorting field
        order: "asc" // Default sorting method
      },
      modalType: 0, // Add or edit flag
      modalVisible: false, // Add or edit visibility
      dictModalVisible: false,
      dictModalTitle: "",
      modalTitle: "", // Add or edit title
      dictForm: {
        title: "",
        type: "",
        description: "",
        sortOrder: 0
      },
      form: {
        // Initial data for the add or edit form object
        title: "",
        value: "",
        status: 0,
        description: "",
        sortOrder: 0
      },
      dictFormValidate: {
        // Form validation rules
        title: [{
          required: true,
          message: "Cannot be empty",
          trigger: "blur"
        }],
        type: [{
          required: true,
          message: "Cannot be empty",
          trigger: "blur"
        }],
        sortOrder: [{
          required: true,
          type: "number",
          message: "Sort order cannot be empty",
          trigger: "blur"
        }]
      },
      formValidate: {
        // Form validation rules
        title: [{
          required: true,
          message: "Cannot be empty",
          trigger: "blur"
        }],
        value: [{
          required: true,
          message: "Cannot be empty",
          trigger: "blur"
        }],
        sortOrder: [{
          required: true,
          type: "number",
          message: "Sort order cannot be empty",
          trigger: "blur"
        }]
      },
      columns: [{
          title: "Select",
          type: "selection",
          minWidth: 60,
          align: "center"
        },
        {
          title: "Index",
          type: "index",
          minWidth: 90,
          align: "center"
        },
        {
          title: "Belonging Dictionary",
          key: "dictName",
          minWidth: 120,
          sortable: true
        },
        {
          title: "Key",
          key: "title",
          minWidth: 180,
          sortable: true
        },
        {
          title: "Value",
          key: "value",
          minWidth: 180,
          sortable: true
        },
        {
          title: "Description",
          key: "description",
          minWidth: 120,
          sortable: true
        },
        {
          title: "Sort",
          key: "sortOrder",
          minWidth: 90,
          sortable: true,
          sortType: "asc"
        },
        {
          title: "Status",
          key: "status",
          align: "center",
          minWidth: 120,
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
        },
        {
          title: "Creation Time",
          key: "createTime",
          minWidth: 180,
          sortable: true
        },
        {
          title: "Action",
          key: "action",
          width: 200,
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
                    icon: "ios-brush",
                    disabled: !(that.$route.meta.permTypes && that.$route.meta.permTypes.includes("delete"))
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
                    icon: "md-close",
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
      submitLoading: false, // Add or edit submission status
      data: [], // Form data
      total: 0, // Total form data
      selectRow: {}
    };
  },
  methods: {
    init() {
      // Get tree data
      this.getAllDict();
      // Get form data
      this.getDataList();
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
    getAllDict() {
      this.treeLoading = true;
      getAllDictList().then(res => {
        this.treeLoading = false;
        if (res.success) {
          this.treeData = res.result;
        }
      });
    },
    search() {
      // Search tree
      if (this.searchKey) {
        this.treeLoading = true;
        searchDict({
          key: this.searchKey
        }).then(res => {
          this.treeLoading = false;
          if (res.success) {
            this.treeData = res.result;
          }
        });
      } else {
        // Reload if empty
        this.getAllDict();
      }
    },
    selectTree(v) {
      if (v.length > 0) {
        this.$refs.dictForm.resetFields();
        // Convert null to ""
        for (let attr in v[0]) {
          if (v[0][attr] == null) {
            v[0][attr] = "";
          }
        }
        let str = JSON.stringify(v[0]);
        let data = JSON.parse(str);
        this.selectNode = data;
        this.dictForm = data;
        this.editTitle = data.title + "(" + data.type + ")";
        // Reload table
        this.searchForm.pageNumber = 1;
        this.searchForm.pageSize = 10;
        this.getDataList();
      } else {
        this.cancelEdit();
      }
    },
    cancelEdit() {
      let data = this.$refs.tree.getSelectedNodes()[0];
      if (data) {
        data.selected = false;
      }
      // Get all data after canceling selection
      this.selectNode = {};
      this.editTitle = "";
      this.getDataList();
    },
    changeSelect(v) {
      this.selectCount = v.length;
      this.selectList = v;
    },
    changeExpand() {
      this.expand = !this.expand;
      if (this.expand) {
        this.expandIcon = "md-arrow-round-back";
        this.span = 19;
      } else {
        this.expandIcon = "md-arrow-round-forward";
        this.span = 23;
      }
    },
    changePage(v) {
      this.searchForm.pageNumber = v;
      this.getDataList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.searchForm.pageSize = v;
      this.getDataList();
    },
    getDataList() {
      this.loading = true;
      if (this.selectNode.id) {
        this.searchForm.dictId = this.selectNode.id;
      } else {
        delete this.searchForm.dictId;
      }
      if (!this.searchForm.status) {
        this.searchForm.status = "";
      }
      getAllDictDataList(this.searchForm).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    handleSearch() {
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.getDataList();
    },
    handleReset() {
      this.$refs.searchForm.resetFields();
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      // Reload data
      this.getDataList();
    },
    changeSort(e) {
      this.searchForm.sort = e.key;
      this.searchForm.order = e.order;
      if (e.order == "normal") {
        this.searchForm.order = "";
      }
      this.getDataList();
    },
    showSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    refreshDict() {
      this.getAllDict();
      this.selectNode = {};
      this.editTitle = "";
      this.getDataList();
    },
    addDict() {
      this.modalType = 0;
      this.dictModalTitle = "Add Dictionary";
      this.$refs.dictForm.resetFields();
      this.dictModalVisible = true;
    },
    editDict() {
      this.modalType = 1;
      this.dictModalTitle = "Edit Dictionary";
      this.dictModalVisible = true;
    },
    delDict() {
      if (!this.selectNode.id) {
        this.$Message.warning("You haven't selected a dictionary to delete");
        return;
      }
      this.$Modal.confirm({
        title: "Confirm Delete",
        loading: true,
        content: "Are you sure you want to delete " + this.selectNode.title + " ?",
        onOk: () => {
          // Delete
          deleteDict({
            ids: this.selectNode.id
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Operation successful");
              this.refreshDict();
            }
          });
        }
      });
    },
    add() {
      if (!this.selectNode.id) {
        this.$Message.warning("Please select a dictionary category first");
        return;
      }
      this.modalType = 0;
      this.modalTitle = "Add data to dictionary " + this.editTitle;
      this.$refs.form.resetFields();
      this.modalVisible = true;
    },
    edit(v) {
      this.modalType = 1;
      this.modalTitle = "Edit data of dictionary " + this.editTitle;
      this.$refs.form.resetFields();
      // Convert null to ""
      for (let attr in v) {
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);
      let data = JSON.parse(str);
      this.form = data;
      this.modalVisible = true;
    },
    handelSubmitDict() {
      this.$refs.dictForm.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          if (this.modalType == 0) {
            // Add - avoid passing id and other data after editing, remember to delete
            delete this.dictForm.id;
            addDict(this.dictForm).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("Operation successful");
                this.getAllDict();
                this.dictModalVisible = false;
              }
            });
          } else if (this.modalType == 1) {
            // Edit
            editDict(this.dictForm).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("Operation successful");
                this.getAllDict();
                this.dictModalVisible = false;
              }
            });
          }
        }
      });
    },
    handelSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          if (this.modalType == 0) {
            // Add - avoid passing id and other data after editing, remember to delete
            delete this.form.id;
            this.form.dictId = this.selectNode.id;
            addDictData(this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("Operation successful");
                this.getDataList();
                this.modalVisible = false;
              }
            });
          } else if (this.modalType == 1) {
            // Edit
            editDictData(this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("Operation successful");
                this.getDataList();
                this.modalVisible = false;
              }
            });
          }
        }
      });
    },
    remove(v) {
      this.$Modal.confirm({
        title: "Confirm Delete",
        content: "Are you sure you want to delete " + v.title + " ?",
        loading: true,
        onOk: () => {
          // Delete
          deleteData({
            ids: v.id
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Operation successful");
              this.getDataList();
            }
          });
        }
      });
    },
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("You haven't selected any data to delete");
        return;
      }
      this.$Modal.confirm({
        title: "Confirm Delete",
        content: "Are you sure you want to delete the selected " + this.selectCount + " items?",
        loading: true,
        onOk: () => {
          let ids = "";
          this.selectList.forEach(function (e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          // Batch delete
          deleteData({
            ids: ids
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Operation successful");
              this.clearSelectAll();
              this.getDataList();
            }
          });
        }
      });
    }
  },
  mounted() {
    // Calculate height
    let height = document.documentElement.clientHeight;
    this.maxHeight = Number(height - 287) + "px";
    this.init();
  }
};
</script>

<style lang="less" scoped>
/* Global styles */
.dict-manage-page {
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
  height:30px;

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

/* Search row */
.search-row {
  margin-bottom: 16px;
  margin-left:20px;
  
  .br {
    margin-top: 16px;
  }

  .select-count-alert {
    margin-top: 16px;
    border-radius: 20px;
  }
}
</style>
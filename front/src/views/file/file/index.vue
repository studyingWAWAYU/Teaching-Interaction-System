<template>
  <div class="file-manage-page">
    <!-- Top action bar: Search + Buttons -->
    <div class="top-bar">
      <div class="search-area">
        <Form
          ref="searchForm"
          :model="searchForm"
          inline
          class="search-form"
        >
          <Form-item prop="name">
            <Input
              type="text"
              v-model="searchForm.name"
              clearable
              placeholder="Original File Name"
              style="width: 160px"
            />
          </Form-item>
          <Form-item prop="createBy">
            <Input
              type="text"
              v-model="searchForm.createBy"
              clearable
              placeholder="Uploader Account"
              style="width: 160px"
            />
          </Form-item>
          <Form-item prop="fkey">
            <Input
              type="text"
              v-model="searchForm.fkey"
              clearable
              placeholder="Stored File Name"
              style="width: 160px"
            />
          </Form-item>
          <Form-item>
            <DatePicker 
              v-model="selectDate" 
              type="daterange" 
              format="yyyy-MM-dd" 
              clearable 
              @on-change="selectDateRange" 
              placeholder="Select Date Range" 
              style="width: 160px"
            ></DatePicker>
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
            <Button
              @click="uploadVisible=true"
              type="success"
              icon="md-cloud-upload"
              size="small"
              shape="round"
              style="margin-left: 8px"
              :disabled="!$route.meta.permTypes.includes('add')"
            >
              Upload File
            </Button>
            <Button
              @click="fileSaveSettingModal=true"
              type="info"
              icon="md-settings"
              size="small"
              shape="round"
              style="margin-left: 8px"
              :disabled="!$route.meta.permTypes.includes('enable')"
            >
              Settings
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
        :columns="columns"
        :data="data"
        ref="table"
        sortable="custom"
        @on-sort-change="changeSort"
        @on-selection-change="changeSelect"
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
        :page-size-opts="[15, 20, 40]"
        size="small"
        show-total
        show-elevator
        show-sizer
      ></Page>
    </div>

    <!-- File upload drawer -->
    <Drawer 
      title="File Upload" 
      closable 
      v-model="uploadVisible" 
      width="500"
    >
      <Upload 
        action="/upload/file" 
        :headers="accessToken" 
        :on-success="handleSuccess" 
        :on-error="handleError" 
        :max-size="5120" 
        :on-exceeded-size="handleMaxSize" 
        :before-upload="beforeUpload" 
        multiple 
        type="drag" 
        ref="up"
      >
        <div style="padding: 20px 0">
          <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
          <p>Click here or drag files here to upload</p>
        </div>
      </Upload>
      <div class="drawer-footer">
        <Button @click="clearFiles">Clear Upload List</Button>
      </div>
    </Drawer>

    <!-- File rename modal -->
    <Modal 
      title="Rename File" 
      v-model="changeFileNameModal" 
      :mask-closable="false"
      :width="500"
      ok-text="Save"
      @on-ok="handleSubmit"
    >
      <Form ref="form" :model="form" :label-width="95" :rules="formValidate">
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Original File Name" prop="name">
              <Input v-model="form.name" style="width:100%" />
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Stored File Name" prop="fkey">
              <Input v-model="form.fkey" style="width:100%" />
            </FormItem>
          </Col>
        </Row>
      </Form>
    </Modal>

    <!-- File storage settings modal -->
    <Modal 
      title="File Storage Settings" 
      v-model="fileSaveSettingModal" 
      :mask-closable="false"
      :width="700"
      ok-text="Save"
      @on-ok="setFileSettingFx"
    >
      <Form ref="settingForm" :model="settingForm" :label-width="95">
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Storage Path">
              <Input 
                type="text" 
                v-model="filePath" 
                placeholder="e.g. C:\\oa-file" 
                style="width: 100%" 
                :disabled="changeLoading" 
              />
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="16">
          <Col span="24">
            <FormItem label="Preview URL">
              <Input 
                type="text" 
                v-model="fileView" 
                placeholder="e.g. 127.0.0.1:8080/wl/file/view" 
                style="width: 100%"
              >
                <Select 
                  v-model="fileHttp" 
                  slot="prepend" 
                  style="width: 80px" 
                  prop="http" 
                  :disabled="changeLoading"
                >
                  <Option value="http://">http://</Option>
                  <Option value="https://">https://</Option>
                </Select>
              </Input>
            </FormItem>
          </Col>
        </Row>
      </Form>
    </Modal>
  </div>
</template>

<script>
import {
  getFileListData,
  copyFile,
  renameFile,
  deleteFile,
  getOneSetting,
  setOneSetting
} from "./api.js";
import "viewerjs/dist/viewer.css";
import Viewer from "viewerjs";
var dp;

export default {
  name: "file-manage",
  data() {
    return {
      filePath: "",
      fileHttp: "",
      fileView: "",
      fileSaveSettingModal: false,
      accessToken: {},
      loading: false,
      drop: false,
      dropDownContent: "Expand",
      dropDownIcon: "ios-arrow-down",
      fileType: "all",
      changeFileNameModal: false,
      uploadVisible: false,
      picVisible: false,
      picTitle: "",
      searchForm: {
        name: "",
        fkey: "",
        type: "",
        pageNumber: 1,
        pageSize: 15,
        sort: "createTime",
        order: "desc",
        startDate: "",
        endDate: ""
      },
      selectDate: null,
      form: {
        name: "",
        fkey: ""
      },
      file: {},
      formValidate: {
        name: [{
          required: true,
          message: "Cannot be empty",
          trigger: "blur"
        }],
        fkey: [{
          required: true,
          message: "Cannot be empty",
          trigger: "blur"
        }]
      },
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      totalSize: "",
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          type: "index",
          width: 60,
          align: "center"
        },
        {
          title: "Original File Name",
          key: "name",
          minWidth: 130,
          sortable: true
        },
        {
          title: "Stored File Name",
          key: "fkey",
          minWidth: 165,
          sortable: true
        },
        {
          title: "Preview",
          key: "url",
          minWidth: 150,
          align: "center",
          render: (h, params) => {
            if (params.row.type.includes("image") > 0) {
              return h("img", {
                attrs: {
                  src: params.row.url
                },
                style: {
                  cursor: "zoom-in",
                  width: "80px",
                  height: "60px",
                  margin: "10px 0",
                  "object-fit": "contain"
                },
                on: {
                  click: () => {
                    this.showPic(params.row);
                  }
                }
              });
            } else if (params.row.type.includes("pdf") > 0) {
              return h("img", {
                attrs: {
                  src: require("@/assets/pdf.png")
                },
                style: {
                  cursor: "zoom-in",
                  width: "60px",
                  margin: "10px 0",
                  "object-fit": "contain"
                },
                on: {
                  click: () => {
                    this.showFile(params.row);
                  }
                }
              });
            } else if (
              params.row.type.includes("zip") > 0 ||
              params.row.type.includes("rar") > 0 ||
              params.row.type.includes("7z") > 0
            ) {
              return h("img", {
                attrs: {
                  src: require("@/assets/zip.png")
                },
                style: {
                  width: "60px",
                  margin: "10px 0",
                  "object-fit": "contain"
                },
                on: {
                  click: () => {
                    this.showFile(params.row);
                    this.$Message.warning("Please download and extract to access!");
                  }
                }
              });
            } else {
              return h("img", {
                attrs: {
                  src: require("@/assets/file.png")
                },
                style: {
                  width: "55px",
                  height: "60px",
                  margin: "10px 0",
                  "object-fit": "contain"
                },
                on: {
                  click: () => {
                    this.showFile(params.row);
                  }
                }
              });
            }
          }
        },
        {
          title: "File Type",
          key: "type",
          minWidth: 115,
          sortable: true
        },
        {
          title: "File Size",
          key: "size",
          minWidth: 115,
          sortable: true,
          render: (h, params) => {
            return h("div", [
              h(
                "Tag", {
                  props: {
                    color: (params.row.size > 1024 * 1024 * 5) ? "orange" : "green"
                  }
                },
                ((params.row.size * 1.0) / (1024 * 1024)).toFixed(2) + " MB"
              )
            ]);
          }
        },
        {
          title: "Uploader",
          key: "createBy",
          minWidth: 120,
          sortable: true,
          align: "center",
          render: (h, params) => {
            return h(
              "Tooltip", {
                props: {
                  placement: "top",
                  content: params.row.createBy
                }
              },
              [
                h(
                  "Tag", {
                    style: {
                      "margin-right": "8px"
                    },
                    props: {
                      type: "border"
                    }
                  },
                  params.row.nickname
                )
              ]
            );
          }
        },
        {
          title: "Upload Time",
          key: "createTime",
          minWidth: 180,
          sortable: true,
          sortType: "desc"
        },
        {
          title: "Actions",
          key: "action",
          align: "center",
          fixed: "right",
          minWidth: 310,
          render: (h, params) => {
            var that = this;
            return h("div", [
              h(
                "Button", {
                  props: {
                    type: "success",
                    size: "small",
                    ghost: true,
                    shape: "circle"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.download(params.row);
                    }
                  }
                },
                "Download"
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
                      this.rename(params.row);
                    }
                  }
                },
                "Rename"
              ),
              h(
                "Button", {
                  props: {
                    type: "info",
                    size: "small",
                    ghost: true,
                    shape: "circle"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.copy(params.row);
                    }
                  }
                },
                "Copy"
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
                  style: {
                    marginRight: "5px"
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
    };
  },
  methods: {
    init() {
      var that = this;
      this.getDataList();
      this.getFileSettingFx();
      this.accessToken = {
        accessToken: this.getStore("accessToken")
      };
    },
    getFileSettingFx() {
      var that = this;
      getOneSetting({
        id: "FILE_PATH"
      }).then(res => {
        if (res.success) {
          that.filePath = res.result.value
        }
      });
      getOneSetting({
        id: "FILE_HTTP"
      }).then(res => {
        if (res.success) {
          that.fileHttp = res.result.value
        }
      });
      getOneSetting({
        id: "FILE_VIEW"
      }).then(res => {
        if (res.success) {
          that.fileView = res.result.value
        }
      });
    },
    setFileSettingFx() {
      var that = this;
      setOneSetting({
        id: "FILE_PATH",
        value: that.filePath
      }).then(res => {
        if (res.success) {
          this.$Message.success("Storage path updated successfully");
        }
      });
      setOneSetting({
        id: "FILE_HTTP",
        value: that.fileHttp
      }).then(res => {
        if (res.success) {
          this.$Message.success("HTTP updated successfully");
        }
      });
      setOneSetting({
        id: "FILE_VIEW",
        value: that.fileView
      }).then(res => {
        if (res.success) {
          this.$Message.success("Preview URL updated successfully");
        }
      });
    },
    showPic(v) {
      let image = new Image();
      image.src = v.url;
      let viewer = new Viewer(image, {
        hidden: function () {
          viewer.destroy();
        }
      });
      viewer.show();
    },
    closeVideo() {
      dp.destroy();
    },
    dropDown() {
      if (this.drop) {
        this.dropDownContent = "Expand";
        this.dropDownIcon = "ios-arrow-down";
      } else {
        this.dropDownContent = "Collapse";
        this.dropDownIcon = "ios-arrow-up";
      }
      this.drop = !this.drop;
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
    changeSort(e) {
      this.searchForm.sort = e.key;
      this.searchForm.order = e.order;
      if (e.order == "normal") {
        this.searchForm.order = "";
      }
      this.getDataList();
    },
    selectDateRange(v) {
      if (v) {
        this.searchForm.startDate = v[0];
        this.searchForm.endDate = v[1];
      }
    },
    getDataList() {
      this.loading = true;
      getFileListData(this.searchForm).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    handleSearch() {
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 15;
      this.getDataList();
    },
    changeFileType() {
      let name = this.fileType;
      if (name == "all") {
        this.searchForm.type = "";
      } else if (name == "pic") {
        this.searchForm.type = "image";
      } else if (name == "video") {
        this.searchForm.type = "video";
      }
      this.handleSearch();
    },
    handleReset() {
      this.$refs.searchForm.resetFields();
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 15;
      this.selectDate = null;
      this.searchForm.startDate = "";
      this.searchForm.endDate = "";
      this.getDataList();
    },
    beforeUpload() {
      return true;
    },
    handleMaxSize(file) {
      this.$Notice.warning({
        title: "File Size Too Large",
        desc: "Selected file '" + file.name + "' is too large, must not exceed 5MB."
      });
    },
    handleSuccess(res, file) {
      if (res.success) {
        this.$Message.success("Upload file " + file.name + " successfully");
        this.getDataList();
      } else {
        this.$Message.error(res.message);
      }
    },
    handleError(error, file, fileList) {
      this.$Message.error(error.toString());
    },
    clearFiles() {
      this.$refs.up.clearFiles();
    },
    download(v) {
      window.open(
        v.url +
        "?attname=&response-content-type=application/octet-stream&filename=" +
        v.name
      );
    },
    showFile(v) {
      window.open(v.url + "?preview=true");
    },
    copy(v) {
      this.$Modal.confirm({
        title: "Confirm Copy",
        content: "Are you sure you want to copy file " + v.name + " ?",
        loading: true,
        onOk: () => {
          copyFile({
            id: v.id,
            key: v.fkey
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success(
                "File copied successfully, new file name is " + v.name + "_copy"
              );
              this.getDataList();
            }
          });
        }
      });
    },
    removeAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("You haven't selected any data to delete");
        return;
      }
      this.$Modal.confirm({
        title: "Confirm Delete",
        content: "Are you sure you want to delete the selected " + this.selectCount + " files?",
        loading: true,
        onOk: () => {
          let ids = "";
          this.selectList.forEach(function (e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          deleteFile({
            ids: ids
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Batch delete files successfully");
              this.clearSelectAll();
              this.getDataList();
            }
          });
        }
      });
    },
    remove(v) {
      this.$Modal.confirm({
        title: "Confirm Delete",
        content: "Are you sure you want to delete file " + v.name + " ?",
        loading: true,
        onOk: () => {
          deleteFile({
            ids: v.id
          }).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("Delete file " + v.name + " successfully");
              this.getDataList();
            }
          });
        }
      });
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          let params = {
            id: this.form.id,
            newKey: this.form.fkey,
            newName: this.form.name
          };
          renameFile(params).then(res => {
            this.submitLoading = false;
            if (res.success) {
              this.$Message.success("Operation successful");
              this.getDataList();
              this.changeFileNameModal = false;
            }
          });
        }
      });
    },
    rename(v) {
      for (let attr in v) {
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);
      let data = JSON.parse(str);
      this.form = data;
      this.changeFileNameModal = true;
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
      this.totalSize = "";
    },
    changeSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
      let size = 0;
      e.forEach(item => {
        size += item.size * 1.0;
      });
      this.totalSize = ((size * 1.0) / (1024 * 1024)).toFixed(2) + " MB";
    }
  },
  mounted() {
    this.height = window.innerHeight - this.$refs.table.$el.offsetTop - 160;
    this.init();
  }
};
</script>

<style lang="less" scoped>
/* Global styles */
.file-manage-page {
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

/* Table styles */
.table-wrapper {
  background: #fff;
  border-radius: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;

  .ivu-table {
    border: none;

    /* Table header styles */
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
    }

    /* Table cell styles */
    td {
      border-bottom: 1px solid #eee;
      padding: 12px 8px;
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

/* Pagination styles */
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
  margin-left:30px;
  border-radius: 20px;
  transition: all 0.3s ease;
  height:30px;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

/* Modal styles */
.ivu-modal-body {
  max-height: 500px;
  overflow: auto;
}

/* Drawer footer buttons */
.drawer-footer {
  margin-top: 16px;
  text-align: right;
}
</style>
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
          <Form-item prop="title">
            <Input
              type="text"
              v-model="searchForm.title"
              clearable
              placeholder="Title"
              style="width: 160px"
            />
          </Form-item>
          <Form-item prop="courseId">
            <Input
              type="text"
              v-model="searchForm.courseId"
              clearable
              placeholder="Course ID"
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
              placeholder="Update Time Range" 
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
            >
              Upload File
            </Button>
            <Button
              @click="batchDelete"
              type="error"
              icon="md-trash"
              size="small"
              shape="round"
              style="margin-left: 8px"
              :disabled="selectedRows.length === 0"
            >
              Batch Delete
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
        :data="filteredData"
        ref="table"
        stripe
        @on-selection-change="handleSelectionChange"
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
        :page-size-opts="[15, 20, 50]"
        size="small"
        show-total
        show-elevator
        show-sizer
      ></Page>
    </div>

    <!-- File upload drawer -->
    <Drawer 
      title="Upload Course File" 
      closable 
      v-model="uploadVisible" 
      width="500"
    >
      <Upload 
        action="#" 
        :on-success="handleUploadSuccess" 
        :on-error="handleUploadError" 
        :max-size="5120" 
        :on-exceeded-size="handleMaxSize" 
        :before-upload="beforeUpload" 
        multiple 
        type="drag" 
        ref="uploader"
      >
        <div style="padding: 20px 0">
          <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
          <p>Drag files here or click to upload</p>
          <p class="text-gray-500 text-sm mt-2">Max file size: 5MB</p>
        </div>
      </Upload>
      <div class="drawer-footer">
        <Button @click="clearUploadFiles">Clear Upload List</Button>
      </div>
    </Drawer>

    <!-- Rename file modal -->
    <Modal 
      title="Rename File" 
      v-model="renameModalVisible" 
      :mask-closable="false"
      :width="500"
      ok-text="Save"
      @on-ok="handleRenameSubmit"
    >
      <Form ref="renameForm" :model="renameForm" :label-width="95" :rules="renameFormRules">
        <FormItem label="Title" prop="title">
          <Input v-model="renameForm.title" style="width:100%" />
        </FormItem>
        <FormItem label="Course ID" prop="courseId">
          <Input v-model="renameForm.courseId" style="width:100%" />
        </FormItem>
      </Form>
    </Modal>

    <!-- File storage settings modal -->
    <Modal 
      title="File Storage Settings" 
      v-model="settingsModalVisible" 
      :mask-closable="false"
      :width="700"
      ok-text="Save"
      @on-ok="saveSettings"
    >
      <Form ref="settingsForm" :model="settingsForm" :label-width="120">
        <FormItem label="Storage Path">
          <Input 
            type="text" 
            v-model="settingsForm.storagePath" 
            placeholder="e.g. /data/course-files" 
            style="width: 100%" 
          />
        </FormItem>
        <FormItem label="Preview URL">
          <Input 
            type="text" 
            v-model="settingsForm.previewUrl" 
            placeholder="e.g. http://yourdomain.com/files/" 
            style="width: 100%"
          />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
export default {
  name: "CourseFileManage",
  data() {
    return {
      loading: false,
      searchForm: {
        title: "",
        courseId: "",
        pageNumber: 1,
        pageSize: 15,
        sort: "update_time",
        order: "desc",
        startDate: "",
        endDate: ""
      },
      selectDate: null,
      mockData: [
        { id: 1, title: "Introduction to Computer Science", course_id: "1", update_time: "2025-06-01", file_url: "/files/cs101-intro.pdf" },
        { id: 2, title: "Data Structures Lecture Notes", course_id: "1", update_time: "2025-06-02", file_url: "/files/ds-lecture-notes.pdf" },
        { id: 3, title: "Algorithms Assignment", course_id: "1", update_time: "2025-06-03", file_url: "/files/algorithms-assignment.docx" },
        { id: 4, title: "Database Systems Slides", course_id: "1", update_time: "2025-06-05", file_url: "/files/db-systems-slides.pptx" },
        { id: 5, title: "Machine Learning Tutorial", course_id: "2", update_time: "2025-06-07", file_url: "/files/ml-tutorial.pdf" },
        { id: 6, title: "Web Development Project", course_id: "2", update_time: "2025-06-08", file_url: "/files/web-dev-project.zip" },
        { id: 7, title: "Operating Systems Notes", course_id: "2", update_time: "2025-06-10", file_url: "/files/os-notes.pdf" },
        { id: 8, title: "Computer Networks Lab", course_id: "1", update_time: "2025-06-12", file_url: "/files/networks-lab.pdf" },
        { id: 9, title: "Software Engineering Syllabus", course_id: "1", update_time: "2025-06-14", file_url: "/files/se-syllabus.docx" },
        { id: 10, title: "Artificial Intelligence Readings", course_id: "3", update_time: "2025-06-15", file_url: "/files/ai-readings.pdf" },
        { id: 11, title: "Cybersecurity Handbook", course_id: "1", update_time: "2025-06-17", file_url: "/files/cybersecurity-handbook.pdf" },
        { id: 12, title: "Cloud Computing Overview", course_id: "2", update_time: "2025-06-18", file_url: "/files/cloud-computing-overview.pptx" },
        { id: 13, title: "Mobile App Development Guide", course_id: "2", update_time: "2025-06-19", file_url: "/files/mobile-dev-guide.pdf" },
        { id: 14, title: "Data Science Case Studies", course_id: "3", update_time: "2025-06-20", file_url: "/files/ds-case-studies.pdf" },
        
      ],
      total: 20,
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          title: "ID",
          key: "id",
          width: 80,
          align: "center"
        },
        {
          title: "Title",
          key: "title",
          minWidth: 150,
          sortable: true,
          render: (h, params) => {
            return h("span", {
              style: {
                fontWeight: "500"
              }
            }, params.row.title);
          }
        },
        {
          title: "Course ID",
          key: "course_id",
          width: 100,
          align: "center",
          sortable: true
        },
        {
          title: "Update Time",
          key: "update_time",
          width: 180,
          align: "center",
          sortable: true
        },
        {
          title: "File URL",
          key: "file_url",
          minWidth: 200,
          align: "center",
          render: (h, params) => {
            const url = params.row.file_url;
            if (!url) {
              return h("span", {
                style: {
                  color: "#999"
                }
              }, "No URL");
            }
            
            return h("div", [
              h("a", {
                attrs: {
                  href: url,
                  target: "_blank"
                },
                style: {
                  color: "#2d8cf0",
                  textDecoration: "underline",
                  cursor: "pointer"
                }
              }, url.length > 30 ? url.substring(0, 30) + "..." : url),
              h("Tooltip", {
                props: {
                  content: url,
                  placement: "top"
                }
              })
            ]);
          }
        },
        {
          title: "Actions",
          key: "action",
          align: "center",
          fixed: "right",
          width: 250,
          render: (h, params) => {
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
                      this.downloadFile(params.row);
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
                      this.openRenameModal(params.row);
                    }
                  }
                },
                "Rename"
              ),
              h(
                "Button", {
                  props: {
                    type: "error",
                    size: "small",
                    ghost: true,
                    shape: "circle"
                  },
                  on: {
                    click: () => {
                      this.deleteFile(params.row);
                    }
                  }
                },
                "Delete"
              )
            ]);
          }
        }
      ],
      selectedRows: [],
      uploadVisible: false,
      renameModalVisible: false,
      settingsModalVisible: false,
      renameForm: {
        id: "",
        title: "",
        courseId: ""
      },
      renameFormRules: {
        title: [{
          required: true,
          message: "Title cannot be empty",
          trigger: "blur"
        }],
        courseId: [{
          required: true,
          message: "Course ID cannot be empty",
          trigger: "blur"
        }]
      },
      settingsForm: {
        storagePath: "/data/course-files",
        previewUrl: "http://example.com/files/"
      }
    };
  },
  computed: {
    filteredData() {
      let data = [...this.mockData];
      
      // Filter by title
      if (this.searchForm.title) {
        data = data.filter(item => 
          item.title.toLowerCase().includes(this.searchForm.title.toLowerCase())
        );
      }
      
      // Filter by course ID
      if (this.searchForm.courseId) {
        data = data.filter(item => 
          item.course_id.toLowerCase().includes(this.searchForm.courseId.toLowerCase())
        );
      }
      
      // Filter by date range
      if (this.searchForm.startDate && this.searchForm.endDate) {
        data = data.filter(item => {
          const itemDate = new Date(item.update_time.split(' ')[0]);
          const startDate = new Date(this.searchForm.startDate);
          const endDate = new Date(this.searchForm.endDate);
          return itemDate >= startDate && itemDate <= endDate;
        });
      }
      
      // Sorting
      if (this.searchForm.sort) {
        data.sort((a, b) => {
          const valA = a[this.searchForm.sort];
          const valB = b[this.searchForm.sort];
          
          if (this.searchForm.order === 'asc') {
            return valA > valB ? 1 : -1;
          } else {
            return valA < valB ? 1 : -1;
          }
        });
      }
      
      // Pagination
      this.total = data.length;
      const start = (this.searchForm.pageNumber - 1) * this.searchForm.pageSize;
      const end = start + this.searchForm.pageSize;
      return data.slice(start, end);
    }
  },
  methods: {
    init() {
      this.getDataList();
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows;
    },
    getDataList() {
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },
    handleSearch() {
      this.searchForm.pageNumber = 1;
    },
    handleReset() {
      this.$refs.searchForm.resetFields();
      this.searchForm.pageNumber = 1;
      this.searchForm.startDate = "";
      this.searchForm.endDate = "";
      this.selectDate = null;
    },
    changePage(page) {
      this.searchForm.pageNumber = page;
    },
    changePageSize(size) {
      this.searchForm.pageSize = size;
      this.searchForm.pageNumber = 1;
    },
    selectDateRange(v) {
      if (v) {
        this.searchForm.startDate = v[0];
        this.searchForm.endDate = v[1];
      } else {
        this.searchForm.startDate = "";
        this.searchForm.endDate = "";
      }
    },
    beforeUpload() {
      return true;
    },
    handleMaxSize(file) {
      this.$Notice.warning({
        title: "File too large",
        desc: `File "${file.name}" exceeds 5MB limit`
      });
    },
    handleUploadSuccess(res, file) {
      this.$Message.success(`File "${file.name}" uploaded successfully (mock)`);
      this.uploadVisible = false;
    },
    handleUploadError(error, file) {
      this.$Message.error(`Failed to upload ${file.name}: ${error.toString()}`);
    },
    clearUploadFiles() {
      this.$refs.uploader.clearFiles();
    },
    downloadFile(row) {
      this.$Message.success(`Downloading "${row.title}" (mock)`);
      // In a real static implementation, you might want to actually download the file
      // window.open(row.file_url);
    },
    openRenameModal(row) {
      this.renameForm = {
        id: row.id,
        title: row.title,
        courseId: row.course_id
      };
      this.renameModalVisible = true;
    },
    handleRenameSubmit() {
      this.$refs.renameForm.validate(valid => {
        if (valid) {
          // Update the mock data
          const index = this.mockData.findIndex(item => item.id === this.renameForm.id);
          if (index !== -1) {
            this.mockData[index].title = this.renameForm.title;
            this.mockData[index].course_id = this.renameForm.courseId;
            this.$Message.success("File renamed successfully (mock)");
            this.renameModalVisible = false;
          }
        }
      });
    },
    deleteFile(row) {
      this.$Modal.confirm({
        title: "Confirm Deletion",
        content: `Are you sure you want to delete file "${row.title}"?`,
        onOk: () => {
          this.mockData = this.mockData.filter(item => item.id !== row.id);
          this.total = this.mockData.length;
          this.$Message.success("File deleted successfully (mock)");
        }
      });
    },
    batchDelete() {
      if (this.selectedRows.length === 0) {
        this.$Message.warning("Please select files to delete");
        return;
      }
      
      this.$Modal.confirm({
        title: "Confirm Batch Deletion",
        content: `Are you sure you want to delete ${this.selectedRows.length} selected files?`,
        onOk: () => {
          const ids = this.selectedRows.map(row => row.id);
          this.mockData = this.mockData.filter(item => !ids.includes(item.id));
          this.total = this.mockData.length;
          this.selectedRows = [];
          this.$Message.success("Files deleted successfully (mock)");
        }
      });
    },
    saveSettings() {
      this.$Message.success("Settings saved successfully (mock)");
      this.settingsModalVisible = false;
    }
  },
  mounted() {
    this.init();
  }
};
</script>

<style lang="less" scoped>
.file-manage-page {
  padding: 20px;
  background: #f8f9fa;
  
  .top-bar {
    background: #fff;
    padding: 16px 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    
    .search-area {
      width: 100%;
      
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
  }
  
  .table-wrapper {
    background: #fff;
    border-radius: 8px;
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
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
  
  .ivu-modal-body {
    max-height: 500px;
    overflow: auto;
  }
  
  .drawer-footer {
    margin-top: 16px;
    text-align: right;
  }
}
</style>
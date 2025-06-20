<template>
  <div class="course-manage-page">
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
              placeholder="Search File Title..."
              size="large"
            >
              <Icon type="ios-search" slot="prefix" />
            </Input>
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
              @click="showUploadModal"
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

    <!-- Upload File Modal -->
    <Modal 
      title="Upload Course File" 
      v-model="uploadModalVisible" 
      :mask-closable="false"
      @on-ok="handleUploadSubmit"
    >
      <Form :model="uploadForm" :label-width="120">
        <FormItem label="Course ID" prop="courseId">
          <InputNumber v-model="uploadForm.courseId" :min="1" style="width:100%" />
        </FormItem>
        <FormItem label="File Title" prop="title">
          <Input v-model="uploadForm.title" style="width:100%" />
        </FormItem>
        <FormItem label="Description" prop="description">
          <Input v-model="uploadForm.description" type="textarea" :rows="3" style="width:100%" />
        </FormItem>
        <FormItem label="File" prop="file">
          <Upload
            ref="upload"
            :before-upload="handleBeforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :format="['pdf','doc','docx','ppt','pptx','xls','xlsx','zip','rar']"
            :max-size="5120"
            action="/api/upload"
          >
            <Button icon="ios-cloud-upload-outline">Select File</Button>
          </Upload>
        </FormItem>
      </Form>
    </Modal>

    <!-- Edit File Modal -->
    <Modal 
      title="Edit File Info" 
      v-model="editModalVisible" 
      :mask-closable="false"
      @on-ok="handleEditSubmit"
    >
      <Form :model="editForm" :label-width="120">
        <FormItem label="ID" prop="id">
          <Input v-model="editForm.id" disabled style="width:100%" />
        </FormItem>
        <FormItem label="Course ID" prop="courseId">
          <InputNumber v-model="editForm.courseId" :min="1" style="width:100%" />
        </FormItem>
        <FormItem label="File Title" prop="title">
          <Input v-model="editForm.title" style="width:100%" />
        </FormItem>
        <FormItem label="Description" prop="description">
          <Input v-model="editForm.description" type="textarea" :rows="3" style="width:100%" />
        </FormItem>
        <FormItem label="File URL" prop="fileUrl">
          <Input v-model="editForm.fileUrl" disabled style="width:100%" />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
import { getCourseFiles, uploadCourseFile, updateCourseFile, deleteCourseFile } from '@/api/course';

export default {
  name: "CourseFileManagement",
  data() {
    return {
      loading: false,
      searchForm: {
        title: "",
        courseId: "",
        pageNumber: 1,
        pageSize: 10
      },
      fileData: [
        { id: 1, update_time: "2025-06-10 08:55:44", course_id: 1, title: "Java Program Design", description: null, file_url: null },
        { id: 2, update_time: "2025-06-10 08:55:44", course_id: 1, title: "Java Program Design", description: null, file_url: null },
        { id: 3, update_time: "2025-06-10 08:55:44", course_id: 2, title: "Python Program Design", description: null, file_url: null },
        { id: 4, update_time: "2025-06-10 08:55:44", course_id: 1, title: "Java Program Design", description: null, file_url: null }
      ],
      total: 4,
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
          title: "Course ID",
          key: "course_id",
          width: 150,
          align: "center",
          sortable: true
        },
        {
          title: "Title",
          key: "title",
          minWidth: 200,
          sortable: true
        },
        {
          title: "Description",
          key: "description",
          minWidth: 200,
          render: (h, params) => {
            return h("span", params.row.description || "-");
          }
        },
        {
          title: "File URL",
          key: "file_url",
          minWidth: 200,
          render: (h, params) => {
            if (!params.row.file_url) {
              return h("span", "-");
            }
            return h("a", {
              attrs: {
                href: params.row.file_url,
                target: "_blank"
              },
              style: {
                color: "#2d8cf0",
                textDecoration: "underline"
              }
            }, "View File");
          }
        },
        {
          title: "Actions",
          key: "action",
          align: "center",
          width: 200,
          render: (h, params) => {
            return h("div", [
              h(
                "Button", {
                  props: {
                    type: "primary",
                    size: "small",
                    icon: "md-create"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.editFile(params.row);
                    }
                  }
                }, "Edit"
              ),
              h(
                "Button", {
                  props: {
                    type: "error",
                    size: "small",
                    icon: "md-trash"
                  },
                  on: {
                    click: () => {
                      this.deleteFile(params.row);
                    }
                  }
                }, "Delete"
              )
            ]);
          }
        }
      ],
      selectedRows: [],
      uploadModalVisible: false,
      editModalVisible: false,
      uploadForm: {
        courseId: null,
        title: "",
        description: "",
        file: null
      },
      editForm: {
        id: "",
        courseId: null,
        title: "",
        description: "",
        fileUrl: ""
      }
    };
  },
  computed: {
    filteredData() {
      let data = [...this.fileData];
      
      // Filter by title
      if (this.searchForm.title) {
        data = data.filter(item => 
          item.title.toLowerCase().includes(this.searchForm.title.toLowerCase())
        );
      }
      
      // Filter by course ID
      if (this.searchForm.courseId) {
        data = data.filter(item => 
          item.course_id.toString() === this.searchForm.courseId.toString()
        );
      }
      
      return data;
    }
  },
  methods: {
    init() {
      this.getFileList();
    },
    getFileList() {
      this.loading = true;
      // In real app, you would call API here
      // getCourseFiles().then(res => {
      //   this.fileData = res.data;
      //   this.total = res.total;
      //   this.loading = false;
      // }).catch(() => {
      //   this.loading = false;
      // });
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },
    handleSearch() {
      this.searchForm.pageNumber = 1;
      this.getFileList();
    },
    handleReset() {
      this.$refs.searchForm.resetFields();
      this.searchForm.pageNumber = 1;
      this.getFileList();
    },
    showUploadModal() {
      this.uploadForm = {
        courseId: null,
        title: "",
        description: "",
        file: null
      };
      this.uploadModalVisible = true;
    },
    handleBeforeUpload(file) {
      this.uploadForm.file = file;
      return false; // Prevent auto upload
    },
    handleUploadSubmit() {
      if (!this.uploadForm.courseId || !this.uploadForm.title || !this.uploadForm.file) {
        this.$Message.warning("Please fill in all required fields");
        return;
      }
      
      // In real app, you would upload file here
      // const formData = new FormData();
      // formData.append('courseId', this.uploadForm.courseId);
      // formData.append('title', this.uploadForm.title);
      // formData.append('description', this.uploadForm.description);
      // formData.append('file', this.uploadForm.file);
      
      // uploadCourseFile(formData).then(res => {
      //   if (res.success) {
      //     this.$Message.success("File uploaded successfully");
      //     this.uploadModalVisible = false;
      //     this.getFileList();
      //   }
      // });
      
      // Mock success
      this.$Message.success("File uploaded successfully (mock)");
      this.uploadModalVisible = false;
      this.getFileList();
    },
    handleUploadSuccess(response, file, fileList) {
      // Handle upload success if using auto upload
    },
    handleUploadError(error, file, fileList) {
      this.$Message.error("File upload failed");
    },
    editFile(row) {
      this.editForm = {
        id: row.id,
        courseId: row.course_id,
        title: row.title,
        description: row.description || "",
        fileUrl: row.file_url || ""
      };
      this.editModalVisible = true;
    },
    handleEditSubmit() {
      if (!this.editForm.courseId || !this.editForm.title) {
        this.$Message.warning("Please fill in all required fields");
        return;
      }
      
      // In real app, you would call API here
      // updateCourseFile(this.editForm).then(res => {
      //   if (res.success) {
      //     this.$Message.success("File info updated successfully");
      //     this.editModalVisible = false;
      //     this.getFileList();
      //   }
      // });
      
      // Mock success
      this.$Message.success("File info updated successfully (mock)");
      this.editModalVisible = false;
      this.getFileList();
    },
    deleteFile(row) {
      this.$Modal.confirm({
        title: "Confirm Deletion",
        content: `Are you sure you want to delete file "${row.title}"?`,
        onOk: () => {
          // In real app: deleteCourseFile(row.id).then(...)
          this.fileData = this.fileData.filter(item => item.id !== row.id);
          this.total = this.fileData.length;
          this.$Message.success("File deleted successfully");
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
          // In real app: batchDeleteFiles(ids).then(...)
          this.fileData = this.fileData.filter(item => !ids.includes(item.id));
          this.total = this.fileData.length;
          this.selectedRows = [];
          this.$Message.success("Files deleted successfully");
        }
      });
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows;
    }
  },
  mounted() {
    this.init();
  }
};
</script>

<style lang="less" scoped>
.course-manage-page {
  padding: 20px;
  background: rgba(246,247,251, 0.8);
  border-radius: 20px;
  
  .top-bar {
    background: #fff;
    padding: 16px 20px;
    border-radius: 8px;
    margin-top:10px;
    margin-bottom: 30px;
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
  
  .ivu-btn {
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}
</style>
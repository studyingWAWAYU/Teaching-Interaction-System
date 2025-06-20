<template>
  <div class="course-discussion-page">
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
              placeholder="Search Discussion Topic..."
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
              @click="showCreateModal"
              type="success"
              icon="md-add"
              size="small"
              shape="round"
              style="margin-left: 8px"
            >
              New Topic
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

    <!-- Create Topic Modal -->
    <Modal 
      title="Create New Discussion Topic" 
      v-model="createModalVisible" 
      :mask-closable="false"
      @on-ok="handleCreateSubmit"
    >
      <Form :model="createForm" :label-width="120" :rules="formRules">
        <FormItem label="Course ID" prop="courseId">
          <InputNumber v-model="createForm.courseId" :min="1" style="width:100%" />
        </FormItem>
        <FormItem label="Topic Title" prop="title">
          <Input v-model="createForm.title" style="width:100%" />
        </FormItem>
        <FormItem label="Content" prop="content">
          <Input v-model="createForm.content" type="textarea" :rows="5" style="width:100%" />
        </FormItem>
        <FormItem label="Author" prop="author">
          <Input v-model="createForm.author" style="width:100%" />
        </FormItem>
      </Form>
    </Modal>

    <!-- Edit Topic Modal -->
    <Modal 
      title="Edit Discussion Topic" 
      v-model="editModalVisible" 
      :mask-closable="false"
      @on-ok="handleEditSubmit"
    >
      <Form :model="editForm" :label-width="120" :rules="formRules">
        <FormItem label="ID" prop="id">
          <Input v-model="editForm.id" disabled style="width:100%" />
        </FormItem>
        <FormItem label="Course ID" prop="courseId">
          <InputNumber v-model="editForm.courseId" :min="1" style="width:100%" />
        </FormItem>
        <FormItem label="Topic Title" prop="title">
          <Input v-model="editForm.title" style="width:100%" />
        </FormItem>
        <FormItem label="Content" prop="content">
          <Input v-model="editForm.content" type="textarea" :rows="5" style="width:100%" />
        </FormItem>
        <FormItem label="Author" prop="author">
          <Input v-model="editForm.author" style="width:100%" />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
import { getDiscussionTopics, createDiscussionTopic, updateDiscussionTopic, deleteDiscussionTopic } from '@/api/discussion';

export default {
  name: "CourseDiscussionManagement",
  data() {
    return {
      loading: false,
      searchForm: {
        title: "",
        courseId: "",
        pageNumber: 1,
        pageSize: 10
      },
      topicData: [
        { 
          id: 1, 
          created_time: "2025-06-10 08:55:44", 
          updated_time: "2025-06-10 08:55:44",
          course_id: 1, 
          title: "Java Homework Discussion", 
          content: "Any questions about this week's homework?", 
          author: "John Doe",
          replies: 5
        },
        { 
          id: 2, 
          created_time: "2025-06-11 10:30:22", 
          updated_time: "2025-06-11 10:30:22",
          course_id: 1, 
          title: "Final Project Ideas", 
          content: "Let's brainstorm some ideas for the final project", 
          author: "Jane Smith",
          replies: 12
        },
        { 
          id: 3, 
          created_time: "2025-06-12 14:15:33", 
          updated_time: "2025-06-12 14:15:33",
          course_id: 2, 
          title: "Python Libraries Discussion", 
          content: "Which Python libraries are most useful for data analysis?", 
          author: "Mike Johnson",
          replies: 8
        },
        { 
          id: 4, 
          created_time: "2025-06-13 09:20:11", 
          updated_time: "2025-06-13 09:20:11",
          course_id: 1, 
          title: "Exam Preparation", 
          content: "Tips for preparing for the upcoming exam", 
          author: "Sarah Williams",
          replies: 15
        }
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
          title: "Created Time",
          key: "created_time",
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
          title: "Author",
          key: "author",
          width: 150,
          align: "center"
        },
        {
          title: "Replies",
          key: "replies",
          width: 150,
          align: "center",
          sortable: true
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
                      this.editTopic(params.row);
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
                      this.deleteTopic(params.row);
                    }
                  }
                }, "Delete"
              )
            ]);
          }
        }
      ],
      selectedRows: [],
      createModalVisible: false,
      editModalVisible: false,
      createForm: {
        courseId: null,
        title: "",
        content: "",
        author: ""
      },
      editForm: {
        id: "",
        courseId: null,
        title: "",
        content: "",
        author: ""
      },
      formRules: {
        courseId: [
          { required: true, type: 'number', message: 'Please input course ID', trigger: 'blur' }
        ],
        title: [
          { required: true, message: 'Please input topic title', trigger: 'blur' },
          { max: 100, message: 'Title cannot exceed 100 characters', trigger: 'blur' }
        ],
        content: [
          { required: true, message: 'Please input topic content', trigger: 'blur' }
        ],
        author: [
          { required: true, message: 'Please input author name', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    filteredData() {
      let data = [...this.topicData];
      
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
      this.getTopicList();
    },
    getTopicList() {
      this.loading = true;
      // In real app, you would call API here
      // getDiscussionTopics().then(res => {
      //   this.topicData = res.data;
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
      this.getTopicList();
    },
    handleReset() {
      this.$refs.searchForm.resetFields();
      this.searchForm.pageNumber = 1;
      this.getTopicList();
    },
    showCreateModal() {
      this.createForm = {
        courseId: null,
        title: "",
        content: "",
        author: ""
      };
      this.createModalVisible = true;
    },
    handleCreateSubmit() {
      this.$refs.createForm.validate((valid) => {
        if (valid) {
          // In real app, you would call API here
          // createDiscussionTopic(this.createForm).then(res => {
          //   if (res.success) {
          //     this.$Message.success("Topic created successfully");
          //     this.createModalVisible = false;
          //     this.getTopicList();
          //   }
          // });
          
          // Mock success
          const newTopic = {
            id: this.topicData.length + 1,
            created_time: new Date().toISOString(),
            updated_time: new Date().toISOString(),
            course_id: this.createForm.courseId,
            title: this.createForm.title,
            content: this.createForm.content,
            author: this.createForm.author,
            replies: 0
          };
          this.topicData.unshift(newTopic);
          this.total = this.topicData.length;
          
          this.$Message.success("Topic created successfully (mock)");
          this.createModalVisible = false;
        }
      });
    },
    editTopic(row) {
      this.editForm = {
        id: row.id,
        courseId: row.course_id,
        title: row.title,
        content: row.content,
        author: row.author
      };
      this.editModalVisible = true;
    },
    handleEditSubmit() {
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          // In real app, you would call API here
          // updateDiscussionTopic(this.editForm).then(res => {
          //   if (res.success) {
          //     this.$Message.success("Topic updated successfully");
          //     this.editModalVisible = false;
          //     this.getTopicList();
          //   }
          // });
          
          // Mock success
          const index = this.topicData.findIndex(item => item.id === this.editForm.id);
          if (index !== -1) {
            this.topicData[index] = {
              ...this.topicData[index],
              title: this.editForm.title,
              content: this.editForm.content,
              author: this.editForm.author,
              updated_time: new Date().toISOString()
            };
          }
          
          this.$Message.success("Topic updated successfully (mock)");
          this.editModalVisible = false;
        }
      });
    },
    deleteTopic(row) {
      this.$Modal.confirm({
        title: "Confirm Deletion",
        content: `Are you sure you want to delete topic "${row.title}"?`,
        onOk: () => {
          // In real app: deleteDiscussionTopic(row.id).then(...)
          this.topicData = this.topicData.filter(item => item.id !== row.id);
          this.total = this.topicData.length;
          this.$Message.success("Topic deleted successfully");
        }
      });
    },
    batchDelete() {
      if (this.selectedRows.length === 0) {
        this.$Message.warning("Please select topics to delete");
        return;
      }
      
      this.$Modal.confirm({
        title: "Confirm Batch Deletion",
        content: `Are you sure you want to delete ${this.selectedRows.length} selected topics?`,
        onOk: () => {
          const ids = this.selectedRows.map(row => row.id);
          // In real app: batchDeleteTopics(ids).then(...)
          this.topicData = this.topicData.filter(item => !ids.includes(item.id));
          this.total = this.topicData.length;
          this.selectedRows = [];
          this.$Message.success("Topics deleted successfully");
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
.course-discussion-page {
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
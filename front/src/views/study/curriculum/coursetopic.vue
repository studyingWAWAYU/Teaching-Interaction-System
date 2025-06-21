<template>
  <div class="course-discussion-page">
    <!-- Top action bar: Search + Buttons -->
    <div class="top-bar">
      <div class="search-area">
        <form ref="searchForm" @submit.prevent>
          <div class="form-group">
            <input
                type="text"
                v-model="searchForm.title"
                class="search-input"
                placeholder="Search Discussion Topic..."
            >
            <span class="search-icon">🔍</span>
          </div>
          <div class="form-group">
            <input
                type="text"
                v-model="searchForm.courseId"
                class="search-input"
                placeholder="Course ID"
                style="width: 160px"
            >
          </div>
          <div class="form-actions">
            <button
                @click="handleSearch"
                class="btn primary-btn"
                style="margin-left: 8px"
            >
              <span class="btn-icon">🔍</span> Search
            </button>
            <button
                @click="handleReset"
                class="btn warning-btn"
                style="margin-left: 8px"
            >
              <span class="btn-icon">↻</span> Reset
            </button>
            <button
                @click="showCreateModal"
                class="btn success-btn"
                style="margin-left: 8px"
            >
              <span class="btn-icon">➕</span> New Topic
            </button>
            <button
                @click="batchDelete"
                class="btn error-btn"
                style="margin-left: 8px"
                :disabled="selectedRows.length === 0"
            >
              <span class="btn-icon">🗑️</span> Batch Delete
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Data table -->
    <div class="table-wrapper">
      <table class="data-table" :loading="loading">
        <thead>
        <tr>
          <th width="60" class="table-selection"></th>
          <th width="80" class="table-header">ID</th>
          <th width="180" class="table-header">Created Time</th>
          <th width="150" class="table-header">Course ID</th>
          <th class="table-header">Title</th>
          <th width="150" class="table-header">Author</th>
          <th width="150" class="table-header">Replies</th>
          <th width="200" class="table-header">Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="topic in filteredData" :key="topic.id" class="table-row" @click:td="!isSelectionColumn($event)">
        <td class="table-selection">
          <input type="checkbox" v-model="selectedRows" :value="topic" class="table-checkbox">
        </td>
        <td class="table-cell" width="80">{{ topic.id }}</td>
        <td class="table-cell" width="180">{{ topic.created_time }}</td>
        <td class="table-cell" width="150">{{ topic.course_id }}</td>
        <td class="table-cell">{{ topic.title }}</td>
        <td class="table-cell" width="150">{{ topic.author }}</td>
        <td class="table-cell" width="150">{{ topic.replies }}</td>
        <td class="table-cell actions-cell">
          <button class="action-btn edit-btn" @click="editTopic(topic)">
            <span class="btn-icon">✏️</span> Edit
          </button>
          <button class="action-btn delete-btn" @click="deleteTopic(topic)">
            <span class="btn-icon">🗑️</span> Delete
          </button>
        </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- Create Topic Modal -->
    <div v-show="createModalVisible" class="modal">
      <div class="modal-overlay" @click="createModalVisible = false"></div>
      <div class="modal-content">
        <div class="modal-header">
          <h3>Create New Discussion Topic</h3>
          <button @click="createModalVisible = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <form ref="createForm" @submit.prevent>
            <div class="form-group">
              <label for="create-courseId">Course ID</label>
              <input
                  type="number"
                  id="create-courseId"
                  v-model="createForm.courseId"
                  min="1"
                  class="form-input"
                  required
              >
            </div>
            <div class="form-group">
              <label for="create-title">Topic Title</label>
              <input
                  type="text"
                  id="create-title"
                  v-model="createForm.title"
                  class="form-input"
                  required
              >
            </div>
            <div class="form-group">
              <label for="create-content">Content</label>
              <textarea
                  id="create-content"
                  v-model="createForm.content"
                  rows="5"
                  class="form-textarea"
                  required
              ></textarea>
            </div>
            <div class="form-group">
              <label for="create-author">Author</label>
              <input
                  type="text"
                  id="create-author"
                  v-model="createForm.author"
                  class="form-input"
                  required
              >
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button @click="createModalVisible = false" class="btn">Cancel</button>
          <button @click="handleCreateSubmit" class="btn primary-btn">Create</button>
        </div>
      </div>
    </div>

    <!-- Edit Topic Modal -->
    <div v-show="editModalVisible" class="modal">
      <div class="modal-overlay" @click="editModalVisible = false"></div>
      <div class="modal-content">
        <div class="modal-header">
          <h3>Edit Discussion Topic</h3>
          <button @click="editModalVisible = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <form ref="editForm" @submit.prevent>
            <div class="form-group">
              <label for="edit-id">ID</label>
              <input
                  type="text"
                  id="edit-id"
                  v-model="editForm.id"
                  class="form-input"
                  disabled
              >
            </div>
            <div class="form-group">
              <label for="edit-courseId">Course ID</label>
              <input
                  type="number"
                  id="edit-courseId"
                  v-model="editForm.courseId"
                  min="1"
                  class="form-input"
                  required
              >
            </div>
            <div class="form-group">
              <label for="edit-title">Topic Title</label>
              <input
                  type="text"
                  id="edit-title"
                  v-model="editForm.title"
                  class="form-input"
                  required
              >
            </div>
            <div class="form-group">
              <label for="edit-content">Content</label>
              <textarea
                  id="edit-content"
                  v-model="editForm.content"
                  rows="5"
                  class="form-textarea"
                  required
              ></textarea>
            </div>
            <div class="form-group">
              <label for="edit-author">Author</label>
              <input
                  type="text"
                  id="edit-author"
                  v-model="editForm.author"
                  class="form-input"
                  required
              >
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button @click="editModalVisible = false" class="btn">Cancel</button>
          <button @click="handleEditSubmit" class="btn primary-btn">Update</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAllTopics, addTopics, updateTopics, deleteTopics } from '@/api/discussion';

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
      topicData: [],
      total: 0,
      columns: [
        { key: "id", title: "ID" },
        { key: "created_time", title: "Created Time" },
        { key: "course_id", title: "Course ID" },
        { key: "title", title: "Title" },
        { key: "author", title: "Author" },
        { key: "replies", title: "Replies" }
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
      formErrors: {
        create: {
          courseId: "",
          title: "",
          content: "",
          author: ""
        },
        edit: {
          courseId: "",
          title: "",
          content: "",
          author: ""
        }
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
      const { courseId } = this.searchForm;
      getAllTopics(courseId)
          .then(res => {
            this.topicData = res.data || [];
            this.total = res.total || this.topicData.length;
          })
          .catch(error => {
            console.error('Failed to fetch topics:', error);
            this.showMessage('error', 'Failed to load discussion topics');
          })
          .finally(() => {
            this.loading = false;
          });
    },
    handleSearch() {
      this.getTopicList();
    },
    handleReset() {
      this.searchForm = {
        title: "",
        courseId: "",
        pageNumber: 1,
        pageSize: 10
      };
      this.getTopicList();
    },
    showCreateModal() {
      this.createForm = {
        courseId: null,
        title: "",
        content: "",
        author: ""
      };
      this.clearFormErrors('create');
      this.createModalVisible = true;
    },
    clearFormErrors(formType) {
      this.formErrors[formType] = {
        courseId: "",
        title: "",
        content: "",
        author: ""
      };
    },
    validateCreateForm() {
      this.clearFormErrors('create');
      let isValid = true;

      if (!this.createForm.courseId) {
        this.formErrors.create.courseId = "Please input course ID";
        isValid = false;
      }

      if (!this.createForm.title) {
        this.formErrors.create.title = "Please input topic title";
        isValid = false;
      } else if (this.createForm.title.length > 100) {
        this.formErrors.create.title = "Title cannot exceed 100 characters";
        isValid = false;
      }

      if (!this.createForm.content) {
        this.formErrors.create.content = "Please input topic content";
        isValid = false;
      }

      if (!this.createForm.author) {
        this.formErrors.create.author = "Please input author name";
        isValid = false;
      }

      return isValid;
    },
    handleCreateSubmit() {
      if (this.validateCreateForm()) {
        const { courseId } = this.createForm;
        addTopics(courseId, this.createForm)
            .then(res => {
              if (res.success) {
                this.showMessage('success', "Topic created successfully");
                this.createModalVisible = false;
                this.getTopicList();
              } else {
                this.showMessage('error', res.message || 'Failed to create topic');
              }
            })
            .catch(error => {
              console.error('Failed to create topic:', error);
              this.showMessage('error', 'Failed to create discussion topic');
            });
      }
    },
    editTopic(row) {
      this.editForm = {
        id: row.id,
        courseId: row.course_id,
        title: row.title,
        content: row.content,
        author: row.author
      };
      this.clearFormErrors('edit');
      this.editModalVisible = true;
    },
    validateEditForm() {
      this.clearFormErrors('edit');
      let isValid = true;

      if (!this.editForm.courseId) {
        this.formErrors.edit.courseId = "Please input course ID";
        isValid = false;
      }

      if (!this.editForm.title) {
        this.formErrors.edit.title = "Please input topic title";
        isValid = false;
      } else if (this.editForm.title.length > 100) {
        this.formErrors.edit.title = "Title cannot exceed 100 characters";
        isValid = false;
      }

      if (!this.editForm.content) {
        this.formErrors.edit.content = "Please input topic content";
        isValid = false;
      }

      if (!this.editForm.author) {
        this.formErrors.edit.author = "Please input author name";
        isValid = false;
      }

      return isValid;
    },
    handleEditSubmit() {
      if (this.validateEditForm()) {
        const { id, courseId } = this.editForm;
        updateTopics(courseId, this.editForm)
            .then(res => {
              if (res.success) {
                this.showMessage('success', "Topic updated successfully");
                this.editModalVisible = false;
                this.getTopicList();
              } else {
                this.showMessage('error', res.message || 'Failed to update topic');
              }
            })
            .catch(error => {
              console.error('Failed to update topic:', error);
              this.showMessage('error', 'Failed to update discussion topic');
            });
      }
    },
    deleteTopic(row) {
      this.confirmDelete(`Are you sure you want to delete topic "${row.title}"?`, () => {
        const { courseId } = row;
        deleteTopics(courseId, { ids: [row.id] })
            .then(res => {
              if (res.success) {
                this.showMessage('success', "Topic deleted successfully");
                this.getTopicList();
              } else {
                this.showMessage('error', res.message || 'Failed to delete topic');
              }
            })
            .catch(error => {
              console.error('Failed to delete topic:', error);
              this.showMessage('error', 'Failed to delete discussion topic');
            });
      });
    },
    batchDelete() {
      if (this.selectedRows.length === 0) {
        this.showMessage('warning', "Please select topics to delete");
        return;
      }

      this.confirmDelete(`Are you sure you want to delete ${this.selectedRows.length} selected topics?`, () => {
        const courseId = this.selectedRows[0].course_id;
        const ids = this.selectedRows.map(row => row.id);

        deleteTopics(courseId, { ids })
            .then(res => {
              if (res.success) {
                this.showMessage('success', "Topics deleted successfully");
                this.getTopicList();
                this.selectedRows = [];
              } else {
                this.showMessage('error', res.message || 'Failed to delete topics');
              }
            })
            .catch(error => {
              console.error('Failed to delete topics:', error);
              this.showMessage('error', 'Failed to delete discussion topics');
            });
      });
    },
    handleSelectionChange(e) {
      const isChecked = e.target.checked;
      const topicId = parseInt(e.target.closest('tr').querySelector('td:nth-child(2)').textContent);

      if (isChecked) {
        this.selectedRows.push(this.topicData.find(topic => topic.id === topicId));
      } else {
        this.selectedRows = this.selectedRows.filter(topic => topic.id !== topicId);
      }
    },
    isSelectionColumn(event) {
      return event.target.closest('.table-selection') !== null;
    },
    showMessage(type, message) {
      const messageDiv = document.createElement('div');
      messageDiv.className = `message ${type}`;
      messageDiv.textContent = message;
      document.body.appendChild(messageDiv);

      setTimeout(() => {
        messageDiv.remove();
      }, 3000);
    },
    confirmDelete(message, callback) {
      const confirmDiv = document.createElement('div');
      confirmDiv.className = 'confirm-modal';
      confirmDiv.innerHTML = `
        <div class="confirm-overlay" @click="confirmDiv.remove()"></div>
        <div class="confirm-content">
          <h3>Confirm Deletion</h3>
          <p>${message}</p>
          <div class="confirm-actions">
            <button class="btn" @click="confirmDiv.remove()">Cancel</button>
            <button class="btn danger-btn" @click="${`() => {callback(); confirmDiv.remove();}`}">Delete</button>
          </div>
        </div>
      `;
      document.body.appendChild(confirmDiv);
    }
  },
  mounted() {
    this.init();
  }
};
</script>

<style lang="less" scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Arial', sans-serif;
}

body {
  background-color: #f5f7fa;
}


.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
}

.primary-btn {
  background-color: #4a90e2;
  color: white;
}

.primary-btn:hover {
  background-color: #357abd;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(74, 144, 226, 0.3);
}

.warning-btn {
  background-color: #f5a623;
  color: white;
}

.warning-btn:hover {
  background-color: #d98b0f;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(245, 166, 35, 0.3);
}

.success-btn {
  background-color: #7ed321;
  color: white;
}

.success-btn:hover {
  background-color: #62b016;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(126, 211, 33, 0.3);
}

.error-btn {
  background-color: #f56c6c;
  color: white;
}

.error-btn:hover {
  background-color: #e04040;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
}

.danger-btn {
  background-color: #e54d42;
  color: white;
}

.danger-btn:hover {
  background-color: #c1392b;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(229, 77, 66, 0.3);
}

.btn-icon {
  margin-right: 5px;
}


.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #333;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #4a90e2;
  box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

.form-textarea {
  resize: vertical;
}



.table-wrapper {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  margin-bottom: 20px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.table-header {
  background: #f9fafb;
  color: #333;
  font-weight: 500;
  border-bottom: 1px solid #eee;
  padding: 12px 8px;
  text-align: left;
}

.table-row {
  border-bottom: 1px solid #eee;
}

.table-row:hover {
  background-color: #f7fafc;
}

.table-cell {
  padding: 12px 8px;
  border-bottom: 1px solid #eee;
}

.table-selection {
  padding: 12px 8px;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.table-checkbox {
  width: 20px;
  height: 20px;
}

.actions-cell {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
}

.edit-btn {
  background-color: #4a90e2;
  color: white;
}

.delete-btn {
  background-color: #f56c6c;
  color: white;
}



.course-discussion-page {
  padding: 20px;
  background: rgba(246,247,251, 0.8);
  border-radius: 20px;
}

.top-bar {
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-area {
  width: 100%;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.form-group.search-group {
  margin-right: 16px;
  margin-bottom: 12px;
  position: relative;
}

.search-input {
  padding: 12px 36px 12px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 16px;
  width: 240px;
}

.search-input:focus {
  outline: none;
  border-color: #4a90e2;
  box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #909399;
}

.form-actions {
  display: flex;
  align-items: center;
}


.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background-color: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1;
}

.modal-header {
  padding: 16px 24px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #909399;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.confirm-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.confirm-content {
  position: relative;
  background-color: white;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1;
  padding: 24px;
}

.confirm-content h3 {
  font-size: 18px;
  color: #333;
  font-weight: 600;
  margin-bottom: 16px;
}

.confirm-content p {
  color: #606266;
  margin-bottom: 24px;
}

.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.message {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 24px;
  border-radius: 4px;
  color: white;
  font-weight: 500;
  z-index: 1001;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.success {
  background-color: #7ed321;
}

.error {
  background-color: #f56c6c;
}

.warning {
  background-color: #f5a623;
}
</style>
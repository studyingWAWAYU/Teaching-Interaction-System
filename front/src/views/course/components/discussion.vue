<template>
  <div class="discussion-content" @click="handleContentClick">
    <div class="discussion-header">
      <div class="search-box">
        <Input
          v-model="searchQuery"
          placeholder="Search keywords..."
          clearable
          size="large"
          @on-change="handleSearch">
          <Icon type="ios-search" slot="prefix" />
        </Input>
      </div>
      <div class="create-btn">
        <Button type="primary" @click="$emit('show-create-modal')">Create Topic</Button>
      </div>
    </div>

    <div class="discussion-main">
      <div class="topic-list">
        <div 
          class="topic-item" 
          v-for="(topic, index) in sortedDiscussions" 
          :key="topic.id"
          :class="{ active: selectedTopic === topic }"
          @click.stop="selectTopic(topic)">
          <div class="topic-brief">
            <h3 class="topic-title">{{ topic.title }}</h3>
            <div class="topic-meta">
              <span class="author">
                <Icon type="ios-person" size="small" />
                {{ getAuthorName(topic.createBy) }}
              </span>
              <span class="replies">
                <Icon type="ios-chatbubbles" :class="{ active: hasUserReplied(topic) }" />
                {{ topic.replyCount || 0 }}
              </span>
              <span class="likes" @click.stop="handleTopicLike(topic)">
                <Icon type="ios-thumbs-up" :class="{ active: topic.isLiked }" />
                {{ topic.likes || 0 }}
              </span>
              <span class="time">{{ formatTime(topic.updateTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="topic-detail" v-if="selectedTopic" @click.stop>
        <div class="detail-header">
            <h2 class="detail-title">{{ selectedTopic.title }}</h2>
            <div class="detail-meta">
              <span class="author">
                <Icon type="ios-person" size="small" />
                {{ getAuthorName(selectedTopic.createBy) }}
              </span>
              <span class="time">
                <Icon type="ios-time" />
                {{ formatTime(selectedTopic.updateTime) }}
              </span>
              <span class="content-likes" @click="handleTopicLike(selectedTopic)">
                <Icon type="ios-thumbs-up" :class="{ active: selectedTopic.isLiked }" />
                {{ selectedTopic.likes || 0 }}
              </span>
            </div>
        </div>
        
        <div class="detail-content">
          <div class="content-wrapper">
            {{ selectedTopic.description }}
          </div>
        </div>

        <div class="reply-section">
          <div class="reply-header">
            <div class="reply-title">
              <h3>Reply ({{ selectedTopic.replyCount || 0 }})</h3>
              <a class="reply-btn" @click="$emit('show-reply-modal', selectedTopic)">
                <Icon type="ios-chatbubbles" />
                Reply
              </a>
            </div>
          </div>

          <div class="reply-list">
            <div class="reply-item" v-for="(reply, rIndex) in sortedReplies" :key="reply.id">
              <div class="reply-avatar">
                <Avatar icon="ios-person" size="large" />
              </div>
              <div class="reply-content-wrapper">
                <div class="reply-header">
                  <div class="reply-info">
                    <span class="reply-author">{{ getAuthorName(reply.createBy) }}</span>
                    <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
              </div>
              <div class="reply-actions">
                <Button type="text" v-if="reply.createBy === currentUserId" @click="handleDeleteReply(reply)">
                  <Icon type="md-trash" />
                </Button>
                <span class="reply-likes" @click="handleReplyLike(reply)">
                  <Icon type="ios-thumbs-up" :class="{ active: reply.isLiked }" />
                  {{ reply.likes || 0 }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from 'js-cookie';
import { getAllTopics, getAllTopicsSorted, addTopics, updateTopics, deleteTopics } from '@/api/discussion';
import { getAllPosts, getAllPostsSorted, addPosts, deletePosts } from '@/api/discussion';
import { getAllUsers } from '@/views/roster/user/api';

export default {
  name: 'Discussion',
  props: {
    discussions: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      searchQuery: '',
      filteredDiscussions: [],
      selectedTopic: null,
      currentUserId: JSON.parse(Cookies.get('userInfo')).id,
      currentUser: JSON.parse(Cookies.get('userInfo')).nickname,
      courseId: null,
      loading: false,
      error: null,
      users: []
    }
  },
  computed: {
    sortedDiscussions() {
      return [...this.filteredDiscussions].sort((a, b) => (b.likes || 0) - (a.likes || 0));
    },
    sortedReplies() {
      if (!this.selectedTopic || !this.selectedTopic.replies) return [];
      return [...this.selectedTopic.replies].sort((a, b) => (b.likes || 0) - (a.likes || 0));
    }
  },
  created() {
    this.courseId = this.$route.params.id;
    this.initData();
  },
  watch: {
    discussions: {
      immediate: true,
      handler(newVal) {
        this.filteredDiscussions = [...newVal];
      }
    }
  },
  methods: {
    async loadUsers() {
      try {
        const res = await getAllUsers();
        if (res.success) {
          this.users = res.result;
        }
      } catch (error) {
        console.error('加载用户信息失败:', error);
      }
    },


    getAuthorName(userId) {
      if (userId === null || userId === undefined) {
        return 'Unknown User';
      }
      const user = this.users.find(u => u.id === userId);
      return user ? (user.nickname || user.username) : `User${userId}`;
    },

    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return '';
      const date = new Date(timeStr);
      return date.toLocaleString('zh-CN');
    },

    // 获取讨论列表
    async fetchCourseDiscussions() {
      try {
        this.loading = true;
        const response = await getAllTopicsSorted(this.courseId);
        if (response.success) {
          const topicsWithReplies = await Promise.all(
            response.result.map(async (topic) => {
              try {
                const postsResponse = await getAllPosts(topic.id);
                const replyCount = postsResponse.success ? postsResponse.result.length : 0;
                return {
                  ...topic,
                  replyCount,
                  replies: postsResponse.success ? postsResponse.result : []
                };
              } catch (error) {
                console.error(`获取主题 ${topic.id} 的回复失败:`, error);
                return {
                  ...topic,
                  replyCount: 0,
                  replies: []
                };
              }
            })
          );
          
          this.filteredDiscussions = topicsWithReplies;
          this.$emit('discussions-loaded', topicsWithReplies);
        } else {
          this.$Message.error('Failed to fetch discussion list.');
        }
      } catch (error) {
        this.error = error.message;
        this.$Message.error('Failed to fetch discussion list.');
      } finally {
        this.loading = false;
      }
    },

    // 创建讨论主题
    async createDiscussion(topicData) {
      try {
        const params = {
          course_id: this.courseId,
          title: topicData.title,
          description: topicData.content
        };
        
        const response = await addTopics(this.courseId, params);
        if (response.success) {
          this.$Message.success('Succeed to create discussion.');
          await this.fetchCourseDiscussions();
        } else {
          this.$Message.error('Failed to create discussion.');
        }
      } catch (error) {
        this.$Message.error('Failed to create discussion.');
      }
    },

    // 回复讨论
    async replyToDiscussion(topicId, replyData) {
      try {
        const params = {
          topicId: topicId,
          content: replyData.content
        };
        
        const response = await addPosts(topicId, params);
        if (response.success) {
          this.$Message.success('回复发布成功');
          await this.loadTopicReplies(topicId);
        } else {
          this.$Message.error('回复发布失败');
        }
      } catch (error) {
        this.$Message.error('回复发布失败');
      }
    },

    // 加载主题回复
    async loadTopicReplies(topicId) {
      try {
        const response = await getAllPosts(topicId);
        if (response.success) {
          // 临时调试信息
          console.log('Posts response:', response.result);
          response.result.forEach((post, index) => {
            console.log(`Post ${index}:`, {
              id: post.id,
              createBy: post.createBy,
              content: post.content,
              topicId: post.topicId
            });
          });
          
          const topic = this.filteredDiscussions.find(t => t.id === topicId);
          if (topic) {
            topic.replies = response.result;
            topic.replyCount = response.result.length;
          }
        }
      } catch (error) {
        console.error('加载回复失败:', error);
      }
    },

    handleSearch() {
      if (!this.searchQuery) {
        this.filteredDiscussions = [...this.discussions];
        return;
      }
      
      const query = this.searchQuery.toLowerCase();
      this.filteredDiscussions = this.discussions.filter(topic => {
        return topic.title.toLowerCase().includes(query) || 
               (topic.description && topic.description.toLowerCase().includes(query));
      });
    },
    
    selectTopic(topic) {
      this.selectedTopic = topic;
      this.loadTopicReplies(topic.id);
    },
    
    handleContentClick(event) {
      if (event.target.classList.contains('discussion-content') || 
          event.target.classList.contains('discussion-main')) {
        this.selectedTopic = null;
      }
    },
    
    handleTopicLike(topic) {
      // TODO: 实现点赞功能
      topic.isLiked = !topic.isLiked;
      topic.likes = (topic.likes || 0) + (topic.isLiked ? 1 : -1);
    },
    
    handleReplyLike(reply) {
      // TODO: 实现回复点赞功能
      reply.isLiked = !reply.isLiked;
      reply.likes = (reply.likes || 0) + (reply.isLiked ? 1 : -1);
    },
    
    async handleDeleteReply(reply) {
      this.$Modal.confirm({
        title: '确认删除',
        content: '确定要删除这条回复吗？',
        onOk: async () => {
          try {
            const response = await deletePosts(reply.topicId, { ids: [reply.id] });
            if (response.success) {
              this.$Message.success('回复已删除');
              await this.loadTopicReplies(reply.topicId);
            } else {
              this.$Message.error('删除回复失败');
            }
          } catch (error) {
            this.$Message.error('删除回复失败');
          }
        }
      });
    },
    
    hasUserReplied(topic) {
      if (!topic.replies) return false;
      return topic.replies.some(reply => reply.createBy === this.currentUserId);
    },

    async initData() {
      await this.loadUsers();
      await this.fetchCourseDiscussions();
    }
  }
}
</script>

<style lang="less" scoped>
.discussion-content {
  padding: 15px 20px;
  height: 100%;

  .discussion-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
    padding: 5px 15px;

    .search-box {
      width: 300px;
      
      :deep(.ivu-input-wrapper) {
        .ivu-input {
          border-radius: 25px;
          padding-left: 40px;
          height: 40px;
          font-size: 16px;
          
          &:focus {
            box-shadow: 0 0 0 2px rgba(45, 140, 240, 0.2);
          }
        }
        
        .ivu-input-prefix {
          left: 12px;
          color: #808695;
          font-size: 16px;
          line-height: 40px;
        }
      }
    }

    .create-btn {
      .ivu-btn {
        border-radius: 22px;
        height: 40px;
        padding: 0 20px;
        font-size: 15px;
      }
    }
  }

  .discussion-main {
    display: flex;
    gap: 20px;
    height: calc(100% - 80px);

    .topic-list {
      width: 300px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 22px;
      display: flex;
      flex-direction: column;
      overflow-y: auto;
      gap: 12px;

      .topic-item {
        border-radius: 22px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        padding: 15px 20px;
        cursor: pointer;
        border: 1px solid #f6f5f5;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
        }

        &.active {
          background: rgba(45, 140, 240, 0.1);
        }

        .topic-brief {
          .topic-title {
            font-size: 16px;
            color: #515a6e;
            margin-bottom: 8px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            width: 100%;
          }

          .topic-meta {
            display: flex;
            gap: 10px;
            color: #808695;
            font-size: 12px;
            flex-wrap: nowrap;

            span {
              display: flex;
              align-items: center;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              max-width: 80px;

              .ivu-icon {
                margin-right: 3px;
                flex-shrink: 0;
              }
            }

            .author {
              max-width: 80px;
            }

            .time {
              margin-left: 3px;
              max-width: 120px;
            }

            .likes {
              display: flex;
              align-items: center;
              cursor: pointer;

              .ivu-icon {
                font-size: 16px;
                transition: all 0.3s ease;

                &.active {
                  color: #2d8cf0;
                }
              }

              &:hover .ivu-icon {
                color: #2d8cf0;
              }
            }

            .replies {
              display: flex;
              align-items: center;
              gap: 4px;

              .ivu-icon {
                font-size: 16px;
                transition: all 0.3s ease;

                &.active {
                  color: #2d8cf0;
                }
              }
            }
          }
        }
      }
    }

    .topic-detail {
      flex: 1;
      background: #fff;
      border-radius: 22px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      padding: 20px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 20px;

      .detail-header {
        padding: 15px;

        .detail-title {
          font-size: 22px;
          font-weight: 600;
          color: #515a6e;
          margin-bottom: 12px;
        }

        .detail-meta {
          display: flex;
          align-items: center;
          gap: 15px;
          color: #808695;
          font-size: 13px;

          .content-likes {
            margin-left: auto;
            display: flex;
            align-items: center;
            gap: 4px;
            cursor: pointer;

            .ivu-icon {
              font-size: 20px;
              &.active {
                color: #2d8cf0;
              }
            }
            &:hover .ivu-icon {
              color: #2d8cf0;
            }
          }
        }
      }

      .detail-content {
        padding: 0 20px;

        .content-wrapper {
          border-radius: 22px;
          padding: 20px;
          background: #f8f8f8;
          font-size: 18px;
          margin-top: -20px;
        }
      }

      .reply-section {
        padding: 15px;

        .reply-header {
          margin-bottom: 20px;

          .reply-title {
            display: flex;
            justify-content: space-between;
            align-items: center;

            h3 {
              font-size: 18px;
              color: #515a6e;
              font-weight: 600;
              margin: 0;
            }

            .reply-btn {
              display: flex;
              align-items: center;
              color: #2d8cf0;
              font-size: 15px;
              cursor: pointer;
              padding: 4px 8px;
              border-radius: 22px;

              &:hover {
                background: rgba(45, 140, 240, 0.1);
              }

              .ivu-icon {
                margin-right: 4px;
              }
            }
          }
        }

        .reply-list {
          display: flex;
          flex-direction: column;
          gap: 10px;

          .reply-item {
            display: flex;
            gap: 16px;
            padding: 16px;
            background: rgba(248, 248, 248, 0.8);
            border-radius: 22px;

            &:hover {
              background: rgb(245, 245, 245);
            }

            .reply-avatar {
              margin-left: 8px;
            }

            .reply-content-wrapper {
              flex: 1;

              .reply-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 8px;

                .reply-info {
                  display: flex;
                  gap: 15px;
                  align-items: center;

                  .reply-author {
                    font-weight: 500;
                    color: #515a6e;
                    font-size: 15px;
                  }

                  .reply-time {
                    color: #808695;
                    font-size: 13px;
                    margin-top: 2px;
                  }
                }
              }

              .reply-content {
                color: #515a6e;
                line-height: 1.6;
                font-size: 14px;
              }
            }

            .reply-actions {
              flex-shrink: 0;
              display: flex;
              flex-direction: column;
              align-items: center;
              gap: 8px;
              padding-right: 14px;
              min-height: 60px;
              justify-content: center;

              .ivu-btn {
                padding: 4px 8px;
                color: #808695;

                .ivu-icon {
                  font-size: 18px;
                }

                &:hover {
                  color: #ed4014;
                  background-color: transparent !important;
                }
              }

              .reply-likes {
                display: flex;
                align-items: center;
                gap: 4px;
                cursor: pointer;
                color: #808695;

                .ivu-icon {
                  font-size: 16px;
                  transition: all 0.3s ease;

                  &.active {
                    color: #2d8cf0;
                  }
                }

                &:hover .ivu-icon {
                  color: #2d8cf0;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style> 
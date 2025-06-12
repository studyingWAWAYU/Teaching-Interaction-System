<template>
  <div class="discussion-content">
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

    <div class="discussion-list">
      <div class="discussion-item card-boxs" v-for="(topic, index) in filteredDiscussions" :key="index">
        <div class="topic-header">
          <div class="topic-info">
            <h3 class="topic-title">{{ topic.title }}</h3>
            <div class="topic-meta">
              <span class="author">
                <Icon type="ios-person" />
                {{ topic.author }}
              </span>
              <span class="time">
                <Icon type="ios-time" />
                {{ topic.time }}
              </span>
              <span class="replies">
                <Icon type="ios-chatbubbles" />
                {{ topic.replyCount }} replies
              </span>
            </div>
          </div>
          <div class="topic-actions">
            <Button type="text" @click="$emit('show-reply-modal', topic)">
              <Icon type="ios-chatbubble" />
              Reply
            </Button>
          </div>
        </div>
        <div class="topic-content">
          {{ topic.content }}
        </div>

        <div class="reply-list" v-if="topic.showReplies">
          <div class="reply-item" v-for="(reply, rIndex) in topic.replies" :key="rIndex">
            <div class="reply-header">
              <span class="reply-author">{{ reply.author }}</span>
              <span class="reply-time">{{ reply.time }}</span>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
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
      filteredDiscussions: []
    }
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
    handleSearch() {
      if (!this.searchQuery) {
        this.filteredDiscussions = [...this.discussions];
        return;
      }
      
      const query = this.searchQuery.toLowerCase();
      this.filteredDiscussions = this.discussions.filter(topic => {
        return topic.title.toLowerCase().includes(query) || 
               topic.content.toLowerCase().includes(query);
      });
    }
  }
}
</script>

<style lang="less" scoped>
.discussion-content {
  padding: 15px 20px;

  .discussion-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 35px;
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

        .ivu-input-suffix {
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

  .discussion-list {
    .discussion-item {
      margin-bottom: 20px;
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(10px);
      border-radius: 12px;
      padding: 20px;
      box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 2px 6px 16px rgba(0, 0, 0, 0.08);
      }

      .topic-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 15px;

        .topic-info {
          .topic-title {
            font-size: 18px;
            font-weight: 600;
            color: #515a6e;
            margin-bottom: 8px;
          }

          .topic-meta {
            display: flex;
            gap: 20px;
            color: #808695;
            font-size: 15px;

            span {
              display: flex;
              align-items: center;

              .ivu-icon {
                margin-right: 4px;
              }
            }
          }
        }

        .topic-actions {
          .ivu-btn {
            padding: 4px 8px;
            border-radius: 22px;
            transition: all 0.3s ease;

            &:hover {
              background: rgba(45, 140, 240, 0.1);
            }
          }
        }
      }

      .topic-content {
        color: #515a6e;
        line-height: 1.6;
        font-size: 15px;
        margin-bottom: 15px;
        padding: 15px;
        background: rgba(0, 0, 0, 0.03);
        border-radius: 16px;
      }

      .reply-list {
        background: rgba(0, 0, 0, 0.02);
        border-radius: 8px;
        padding: 15px;

        .reply-item {
          padding: 10px 0;
          border-bottom: 1px solid rgba(0, 0, 0, 0.05);

          &:last-child {
            border-bottom: none;
          }

          .reply-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;

            .reply-author {
              font-weight: 500;
              color: #515a6e;
            }

            .reply-time {
              color: #808695;
              font-size: 13px;
            }
          }

          .reply-content {
            color: #515a6e;
            line-height: 1.5;
            font-size: 15px;
          }
        }
      }
    }
  }
}

</style> 
<template>
  <div class="detail-content">
    <div class="section">
      <h2><Icon type="ios-book"/>Course Introduction</h2>
      <div class="card-boxs">
        <p style="font-size: 16px">{{ courseInfo.introduction }}</p>
      </div>
    </div>

    <div class="section">
      <div class="section-header">
        <h2><Icon type="ios-star"/>Course Feedback</h2>
        <Button type="primary" @click="showFeedbackForm = true" v-if="!showFeedbackForm">
          Add Feedback
        </Button>
      </div>

      <div class="card-boxs feedback-stats" v-if="reviews.length > 0">
        <div class="stats-content">
          <div class="stats-item">
            <span class="stats-number">{{ reviews.length }}</span>
            <span class="stats-label">Total Reviews</span>
          </div>
          <div class="stats-item">
            <span class="stats-number">{{ averageRating.toFixed(1) }}</span>
            <span class="stats-label">Average Rating</span>
          </div>
          <div class="stats-item">
            <Rate disabled :value="averageRating" class="average-stars" />
          </div>
        </div>
      </div>
      
<!--      添加评价表单-->
      <div class="card-boxs feedback-form" v-if="showFeedbackForm">
        <h3>Add Your Feedback</h3>
        <Form :model="feedbackForm" :label-width="80">
          <FormItem label="Rating">
            <Rate v-model="feedbackForm.rating" />
          </FormItem>
          <FormItem label="Content">
            <Input
              v-model="feedbackForm.content"
              type="textarea"
              :rows="4"
              placeholder="Share your thoughts about this course..."
            />
          </FormItem>
          <FormItem>
            <Button type="primary" @click="submitFeedback" :loading="submitting">
              Submit
            </Button>
            <Button @click="cancelFeedback" style="margin-left: 10px">
              Cancel
            </Button>
          </FormItem>
        </Form>
      </div>

      <div class="reviews">
        <div class="card-boxs review-item" v-for="review in reviews" :key="review.id">
          <div class="review-header">
            <span class="reviewer">{{ review.reviewerName }}</span>
            <Rate disabled :value="review.rating" class="stars" />
          </div>
          <p class="review-content">{{ review.content }}</p>
          <p class="review-time">{{ formatTime(review.createTime) }}</p>
        </div>

        <div class="card-boxs no-reviews" v-if="reviews.length === 0">
          <p>No feedback yet. Be the first to share your thoughts!</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { addFeedback } from '@/api/feedback'
import Cookies from 'js-cookie'

export default {
  name: 'CourseDetails',
  props: {
    courseInfo: {
      type: Object,
      required: true
    },
    reviews: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      showFeedbackForm: false,
      submitting: false,
      feedbackForm: {
        rating: 0,
        content: ''
      }
    }
  },
  computed: {
    averageRating() {
      if (this.reviews.length === 0) return 0;
      const totalRating = this.reviews.reduce((sum, review) => sum + (review.rating || 0), 0);
      return totalRating / this.reviews.length;
    }
  },
  methods: {
    async submitFeedback() {
      if (!this.feedbackForm.rating || !this.feedbackForm.content.trim()) {
        this.$Message.warning('Please provide both rating and content');
        return;
      }

      if (this.feedbackForm.rating < 1 || this.feedbackForm.rating > 5) {
        this.$Message.warning('Please provide a valid rating (1-5 stars)');
        return;
      }

      this.submitting = true;
      try {
        const params = {
          id: this.courseInfo.id,
          content: this.feedbackForm.content.trim(),
          rating: this.feedbackForm.rating
        };
        
        const res = await addFeedback(params);
        if (res.success) {
          this.$Message.success('Feedback submitted successfully!');
          this.cancelFeedback();
          this.$emit('refresh-reviews');
        } else {
          this.$Message.error(res.message || 'Failed to submit feedback');
        }
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.$Message.error('Please login to submit feedback');
        } else {
          this.$Message.error('Failed to submit feedback. Please try again.');
        }
      } finally {
        this.submitting = false;
      }
    },

    cancelFeedback() {
      this.showFeedbackForm = false;
      this.feedbackForm = {
        rating: 0,
        content: ''
      };
    },

    formatTime(time) {
      if (!time) return '';
      try {
        const date = new Date(time);
        return date.toLocaleString();
      } catch (error) {
        return time;
      }
    }
  }
}
</script>

<style lang="less" scoped>
.detail-content {
  padding: 25px 35px;

  .section {
    margin-bottom: 60px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h2 {
        font-size: 20px;
        color: #515a6e;
        margin: 0;
        display: flex;
        align-items: center;
        
        .ivu-icon {
          margin-right: 8px;
          color: #4293ec;
        }
      }

      .ivu-btn {
        border-radius: 22px;
        height: 36px;
      }
    }

    h2 {
      font-size: 20px;
      color: #515a6e;
      margin-bottom: 16px;
      display: flex;
      align-items: center;

      .ivu-icon {
        margin-right: 8px;
        color: #4293ec;
      }
    }
  }

  .card-boxs {
    background-color: rgba(246,247,251,0.8);
    backdrop-filter: blur(8px);
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    margin-bottom: 16px;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    }

    p {
      color: #515a6e;
      line-height: 1.8;
      margin: 0;
    }
  }

  .feedback-form {
    h3 {
      margin-bottom: 16px;
      color: #515a6e;
      font-size: 18px;
    }

    .ivu-form-item {
      margin-bottom: 16px;
    }

    .ivu-rate {
      font-size: 20px;
    }

    .ivu-input {
      border-radius: 8px;
    }

    .ivu-btn {
      border-radius: 22px;
      height: 36px;
    }
  }

  .feedback-stats {
    .stats-content {
      display: flex;
      align-items: center;
      justify-content: space-around;
      flex-wrap: wrap;
      gap: 20px;

      .stats-item {
        text-align: center;
        display: flex;
        flex-direction: column;
        align-items: center;

        .stats-number {
          font-size: 24px;
          font-weight: bold;
          color: #4293ec;
          margin-bottom: 4px;
        }

        .stats-label {
          font-size: 14px;
          color: #808695;
        }

        .average-stars {
          .ivu-rate-star {
            margin-right: 2px;
          }
        }
      }
    }
  }

  .no-reviews {
    text-align: center;
    color: #808695;
    font-style: italic;
  }

  .reviews {
    .review-item {
      margin-bottom: 16px;
      &:last-child {
        margin-bottom: 0;
      }

      .review-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 6px;

        .reviewer {
          font-weight: 550;
          color: #515a6e;
        }

        .stars {
          .ivu-rate-star {
            margin-right: 4px;
          }
        }
      }

      .review-content {
        color: #515a6e;
        font-size: 15px;
        margin-bottom: 10px;
      }

      .review-time {
        color: #808695;
        font-size: 12px;
        margin: 0;
      }
    }
  }
}
</style> 
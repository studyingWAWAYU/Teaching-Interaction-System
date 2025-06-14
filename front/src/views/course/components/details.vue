<template>
  <div class="detail-content">
    <div class="section">
      <h2><Icon type="ios-book"/>Course Introduction</h2>
      <div class="card-boxs">
        <p style="font-size: 16px">{{ courseInfo.introduction }}</p>
      </div>
    </div>

    <div class="section">
      <h2><Icon type="ios-bulb" />Course Objectives</h2>
      <div class="card-boxs objectives">
        <p style="white-space: pre-line">{{ courseInfo.objectives }}</p>
      </div>
    </div>

    <div class="section">
      <h2><Icon type="ios-star"/>Course Evaluation</h2>
      <div class="reviews">
        <div class="card-boxs review-item" v-for="review in reviews" :key="review.id">
          <div class="review-header">
            <span class="reviewer">{{ review.name }}</span>
            <Rate disabled :value="review.rating" class="stars" />
          </div>
          <p class="review-content">{{ review.content }}</p>
          <p class="review-time">{{ review.time }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
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
  }
}
</script>

<style lang="less" scoped>
.detail-content {
  padding: 25px 35px;

  .section {
    margin-bottom: 60px;

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
    background-color: rgba(246,247,251,0.8);  //rgba(255, 255, 255);
    backdrop-filter: blur(8px);
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;

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

  .objectives {
    p {
      margin-bottom: 12px;
      font-size: 16px;
      &:last-child {
        margin-bottom: 0;
      }
    }
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
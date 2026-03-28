<template>
  <div class="detail">
    <header class="detail-header">
      <button class="back-btn" @click="$router.back()">←</button>
      <div class="actions">
        <button>❤️</button>
        <button>💬</button>
        <button>📤</button>
      </div>
    </header>
    
    <div class="detail-content">
      <img :src="item.coverUrl" :alt="item.title" class="detail-image" />
      
      <div class="detail-info">
        <h1>{{ item.title }}</h1>
        <div class="meta">
          <span>by {{ item.nickname }}</span>
          <span>{{ item.category }}</span>
          <span>{{ item.createTime }}</span>
        </div>
        
        <p class="description">{{ item.description || '暂无描述' }}</p>
        
        <div v-if="item.tags?.length" class="tags">
          <span v-for="tag in item.tags" :key="tag.id" class="tag"># {{ tag.name }}</span>
        </div>
      </div>
      
      <div class="comments-section">
        <h3>评论 ({{ comments.length }})</h3>
        
        <div class="comment-input">
          <input v-model="newComment" placeholder="说点什么..." />
          <button @click="submitComment">发送</button>
        </div>
        
        <div class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <img :src="comment.avatar" :alt="comment.nickname" class="comment-avatar" />
            <div class="comment-body">
              <div class="comment-header">
                <span class="nickname">{{ comment.nickname }}</span>
                <span class="time">{{ comment.createTime }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const item = ref({
  id: 1,
  title: '电影推荐',
  nickname: '张三',
  category: '影视',
  coverUrl: 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=1200',
  description: '近期看过最好看的电影，强烈推荐！',
  tags: [{ id: 1, name: '电影' }, { id: 2, name: '推荐' }],
  createTime: '2026-03-20'
})

const comments = ref([
  { id: 1, nickname: '李四', avatar: 'https://i.pravatar.cc/40', content: '太棒了！', createTime: '2小时前' },
  { id: 2, nickname: '王五', avatar: 'https://i.pravatar.cc/41', content: '收藏了！', createTime: '3小时前' }
])

const newComment = ref('')

const submitComment = () => {
  if (!newComment.value.trim()) return
  // TODO: 调用API提交评论
  newComment.value = ''
}
</script>

<style lang="scss" scoped>
.detail {
  min-height: 100vh;
  background: #0a0a0a;
  color: #fff;
}

.detail-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(180deg, rgba(10,10,10,0.9) 0%, transparent 100%);
}

.back-btn {
  background: none;
  border: none;
  color: #fff;
  font-size: 24px;
  cursor: pointer;
}

.actions {
  display: flex;
  gap: 16px;
  
  button {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
  }
}

.detail-content {
  padding-top: 60px;
}

.detail-image {
  width: 100%;
  max-height: 60vh;
  object-fit: contain;
  background: #111;
}

.detail-info {
  padding: 24px 20px;
}

h1 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 12px;
}

.meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #888;
  margin-bottom: 16px;
}

.description {
  color: #ccc;
  line-height: 1.6;
  margin-bottom: 16px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  
  .tag {
    padding: 6px 12px;
    background: #1a1a1a;
    border-radius: 16px;
    font-size: 13px;
    color: #4da6ff;
  }
}

.comments-section {
  padding: 24px 20px;
  border-top: 1px solid #222;
  
  h3 {
    font-size: 16px;
    margin-bottom: 16px;
  }
}

.comment-input {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  
  input {
    flex: 1;
    padding: 12px 16px;
    background: #1a1a1a;
    border: none;
    border-radius: 24px;
    color: #fff;
    font-size: 14px;
    
    &::placeholder {
      color: #555;
    }
  }
  
  button {
    padding: 12px 20px;
    background: #fff;
    color: #0a0a0a;
    border: none;
    border-radius: 24px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
  }
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  gap: 8px;
  margin-bottom: 4px;
  
  .nickname {
    font-size: 14px;
    font-weight: 500;
  }
  
  .time {
    font-size: 12px;
    color: #666;
  }
}

.comment-content {
  font-size: 14px;
  color: #ccc;
  line-height: 1.5;
}
</style>

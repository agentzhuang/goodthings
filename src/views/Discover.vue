<template>
  <div class="discover">
    <!-- Hero Section -->
    <section class="hero">
      <img 
        class="hero-image" 
        :src="heroImage" 
        alt="Hero"
      />
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">须皓的好东西收藏网</h1>
        <p class="hero-subtitle">发现美好，记录生活</p>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-value">{{ stats.items }}</div>
            <div class="stat-label">收藏品</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.users }}</div>
            <div class="stat-label">用户</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.views }}</div>
            <div class="stat-label">浏览</div>
          </div>
        </div>
      </div>
      <div class="hero-scroll">
        <span>↓</span>
      </div>
    </section>

    <!-- Gallery Section -->
    <section class="gallery-section">
      <div class="section-header">
        <h2 class="section-title">发现</h2>
        <div class="view-toggle">
          <button 
            class="view-btn" 
            :class="{ active: viewMode === 'grid' }"
            @click="viewMode = 'grid'"
          >▦</button>
          <button 
            class="view-btn" 
            :class="{ active: viewMode === 'waterfall' }"
            @click="viewMode = 'waterfall'"
          >▤</button>
        </div>
      </div>
      
      <!-- 瀑布流画廊 -->
      <div class="gallery-waterfall" ref="galleryRef">
        <div 
          v-for="item in items" 
          :key="item.id" 
          class="gallery-item"
          @click="goToDetail(item.id)"
        >
          <img 
            :src="item.coverUrl" 
            :alt="item.title"
            :style="{ height: item.height || 'auto' }"
            loading="lazy"
          />
          <div v-if="item.mediaType === 2" class="gallery-item-play">▶</div>
          <div v-if="item.duration" class="gallery-item-duration">{{ formatDuration(item.duration) }}</div>
          <div class="gallery-item-overlay">
            <div class="gallery-item-title">{{ item.title }}</div>
            <div class="gallery-item-meta">{{ item.nickname }} · {{ item.category }}</div>
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="loading" class="loading">
        <span>加载中...</span>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const viewMode = ref('waterfall')
const loading = ref(false)
const galleryRef = ref(null)

const heroImage = ref('https://images.unsplash.com/photo-1494976388531-d1058494cdd8?w=1920')

const stats = ref({
  items: '1,234',
  users: '856',
  views: '12.5万'
})

const items = ref([
  { id: 1, title: '电影推荐', category: '影视', mediaType: 2, coverUrl: 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=600', height: '300px', duration: 154 },
  { id: 2, title: '瑞士雪山', category: '风景', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600', height: '400px' },
  { id: 3, title: '美妆分享', category: '美妆', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800', height: '350px' },
  { id: 4, title: '北极熊冲浪', category: '自然', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1530595467537-0b5996c41c2a?w=600', height: '380px' },
  { id: 5, title: '蓝白猫猫', category: '宠物', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600', height: '420px' },
  { id: 6, title: '插画作品', category: '艺术', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1579783902614-a373fb525018?w=600', height: '360px' },
  { id: 7, title: '帅照自拍', category: '生活', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600', height: '400px' },
  { id: 8, title: '拔河比赛', category: '运动', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1526631778880-38c9e2e8f7e5?w=800', height: '320px' },
  { id: 9, title: '鼬神手绘', category: '艺术', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600', height: '440px' },
  { id: 10, title: '建筑设计', category: '建筑', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1486718448742-163732cd1544?w=600', height: '390px' },
  { id: 11, title: '美食探店', category: '美食', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600', height: '350px' },
  { id: 12, title: '旅行日记', category: '旅行', mediaType: 1, coverUrl: 'https://images.unsplash.com/photo-1488646953014-85c44eff4e4e?w=600', height: '410px' }
])

onMounted(() => {
  // 模拟加载数据
  console.log('Discover mounted')
})

const formatDuration = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

const goToDetail = (id) => {
  router.push(`/detail/${id}`)
}
</script>

<style lang="scss" scoped>
.discover {
  min-height: 100vh;
  background: #0a0a0a;
  color: #fff;
}

/* Hero Section */
.hero {
  height: 100vh;
  max-height: 800px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  position: absolute;
  z-index: 0;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0,0,0,0.3) 0%, rgba(0,0,0,0.6) 100%);
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 600px;
  padding: 0 24px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  letter-spacing: -1px;
  margin-bottom: 16px;
  text-shadow: 0 2px 20px rgba(0,0,0,0.5);
}

.hero-subtitle {
  font-size: 16px;
  color: rgba(255,255,255,0.7);
  margin-bottom: 32px;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  margin-top: 4px;
}

.hero-scroll {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2;
  color: rgba(255,255,255,0.5);
  font-size: 24px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(8px); }
}

/* Gallery Section */
.gallery-section {
  padding: 40px 0 80px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding: 0 40px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
}

.view-toggle {
  display: flex;
  gap: 8px;
}

.view-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 10px;
  background: #1a1a1a;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  font-size: 16px;
  transition: all 0.2s;
  
  &.active {
    background: #fff;
    color: #0a0a0a;
  }
  
  &:hover {
    background: #333;
    &.active {
      background: #fff;
    }
  }
}

/* 瀑布流画廊 */
.gallery-waterfall {
  columns: 3;
  column-gap: 16px;
  padding: 0 40px;
}

.gallery-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  break-inside: avoid;
  margin-bottom: 16px;
  
  img {
    width: 100%;
    height: auto;
    display: block;
    transition: transform 0.4s ease;
  }
  
  &:hover img {
    transform: scale(1.03);
  }
}

.gallery-item-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 40%, rgba(0,0,0,0.8) 100%);
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 16px;
  
  .gallery-item:hover & {
    opacity: 1;
  }
}

.gallery-item-title {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 6px;
}

.gallery-item-meta {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.gallery-item-play {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #0a0a0a;
}

.gallery-item-duration {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0,0,0,0.6);
  color: #fff;
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 6px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #888;
}
</style>

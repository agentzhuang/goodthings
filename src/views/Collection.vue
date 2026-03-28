<template>
  <div class="collection">
    <header class="page-header">
      <h1>我的收藏夹</h1>
    </header>
    
    <div class="collections-list" v-if="collections.length">
      <div 
        v-for="col in collections" 
        :key="col.id" 
        class="collection-item"
        @click="goToCollection(col.id)"
      >
        <div class="collection-cover">
          <img :src="col.coverUrl || defaultCover" :alt="col.name" />
        </div>
        <div class="collection-info">
          <h3>{{ col.name }}</h3>
          <p>{{ col.itemCount }} 个收藏品</p>
        </div>
      </div>
    </div>
    
    <div v-else class="empty-state">
      <div class="empty-icon">📁</div>
      <p>还没有收藏夹</p>
      <button @click="$router.push('/upload')">创建收藏夹</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const defaultCover = 'https://images.unsplash.com/photo-1481595217927-2dc99f4a0589?w=300'

const collections = ref([
  { id: 1, name: '风景大片', itemCount: 12, coverUrl: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300' },
  { id: 2, name: '猫咪合集', itemCount: 8, coverUrl: 'https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?w=300' },
  { id: 3, name: '设计灵感', itemCount: 25, coverUrl: 'https://images.unsplash.com/photo-1486718448742-163732cd1544?w=300' }
])

const goToCollection = (id) => {
  console.log('go to collection', id)
}
</script>

<style lang="scss" scoped>
.collection {
  min-height: 100vh;
  background: #0a0a0a;
  color: #fff;
  padding: 80px 40px 40px;
}

.page-header {
  margin-bottom: 40px;
  
  h1 {
    font-size: 28px;
    font-weight: 600;
  }
}

.collections-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.collection-item {
  background: #1a1a1a;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-4px);
  }
}

.collection-cover {
  width: 100%;
  height: 180px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.collection-info {
  padding: 16px;
  
  h3 {
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 4px;
  }
  
  p {
    font-size: 13px;
    color: #888;
  }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  
  .empty-icon {
    font-size: 64px;
    margin-bottom: 16px;
  }
  
  p {
    color: #888;
    margin-bottom: 24px;
  }
  
  button {
    padding: 12px 32px;
    background: #fff;
    color: #0a0a0a;
    border: none;
    border-radius: 24px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
  }
}
</style>

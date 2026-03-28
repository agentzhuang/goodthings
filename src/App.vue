<template>
  <div id="app">
    <!-- 顶部导航 -->
    <header class="app-header">
      <router-link to="/" class="logo">须皓的好东西收藏网</router-link>
      <nav class="nav">
        <router-link to="/">发现</router-link>
        <router-link to="/collection">收藏</router-link>
        <router-link to="/upload">上传</router-link>
        <router-link to="/about">关于</router-link>
      </nav>
      <div class="actions">
        <div class="search-icon">🔍</div>
        <router-link to="/upload" class="upload-btn">+ 上传</router-link>
      </div>
    </header>

    <!-- 主内容 -->
    <main>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<style lang="scss">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  background: #0a0a0a;
  color: #fff;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
}

/* 顶部导航 */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 24px 40px;
  display: flex;
  align-items: center;
  background: linear-gradient(180deg, rgba(10,10,10,0.9) 0%, transparent 100%);
}

.logo {
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 2px;
  color: #fff;
  text-decoration: none;
  margin-right: auto;
}

.nav {
  display: flex;
  gap: 32px;
  margin-right: 40px;
  
  a {
    color: #888;
    text-decoration: none;
    font-size: 14px;
    transition: color 0.2s;
    
    &:hover, &.router-link-active {
      color: #fff;
    }
  }
}

.actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-icon {
  font-size: 20px;
  cursor: pointer;
  opacity: 0.7;
  
  &:hover {
    opacity: 1;
  }
}

.upload-btn {
  padding: 10px 24px;
  background: #fff;
  color: #0a0a0a;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    transform: scale(1.02);
    box-shadow: 0 4px 20px rgba(255,255,255,0.2);
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .app-header {
    padding: 16px 20px;
  }
  
  .logo {
    font-size: 16px;
  }
  
  .nav {
    display: none;
  }
}
</style>

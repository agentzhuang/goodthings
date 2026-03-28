<template>
  <div class="about">
    <header class="page-header">
      <h1>关于</h1>
    </header>
    
    <div class="about-content">
      <div class="about-intro">
        <div class="app-icon">🖼️</div>
        <h2>须皓的好东西收藏网</h2>
        <p class="version">V1.0.0</p>
        <p class="desc">发现美好，记录生活。极简画廊风格的图片/视频收藏平台。</p>
      </div>
      
      <div class="features">
        <h3>功能特点</h3>
        <ul>
          <li>
            <span class="icon">📷</span>
            <span>瀑布流展示，沉浸式浏览体验</span>
          </li>
          <li>
            <span class="icon">📁</span>
            <span>创建收藏夹，分类管理</span>
          </li>
          <li>
            <span class="icon">🏷️</span>
            <span>标签系统，快速找到内容</span>
          </li>
          <li>
            <span class="icon">💬</span>
            <span>评论互动，分享感受</span>
          </li>
        </ul>
      </div>
      
      <div class="tech-stack">
        <h3>技术栈</h3>
        <div class="tech-tags">
          <span class="tag">Vue 3</span>
          <span class="tag">Vite</span>
          <span class="tag">Spring Boot</span>
          <span class="tag">MySQL</span>
          <span class="tag">阿里云OSS</span>
        </div>
      </div>
      
      <div class="contact">
        <h3>联系方式</h3>
        <p>如有疑问或建议，欢迎反馈。</p>
        <p class="email">contact@goodthings.com</p>
      </div>
      
      <div class="copyright">
        <p>© 2026 须皓的好东西收藏网</p>
        <p>Aether AI</p>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.about {
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

.about-content {
  max-width: 600px;
  margin: 0 auto;
}

.about-intro {
  text-align: center;
  padding: 40px 0;
  
  .app-icon {
    font-size: 80px;
    margin-bottom: 16px;
  }
  
  h2 {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  
  .version {
    font-size: 13px;
    color: #888;
    margin-bottom: 16px;
  }
  
  .desc {
    color: #aaa;
    line-height: 1.6;
  }
}

.features, .tech-stack, .contact {
  background: #1a1a1a;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  
  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
  }
}

.features {
  ul {
    list-style: none;
    
    li {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 8px 0;
      
      .icon {
        font-size: 20px;
      }
    }
  }
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  
  .tag {
    padding: 6px 12px;
    background: #0a0a0a;
    border-radius: 16px;
    font-size: 13px;
  }
}

.contact {
  p {
    color: #888;
    margin-bottom: 8px;
  }
  
  .email {
    color: #4da6ff;
  }
}

.copyright {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid #222;
  
  p {
    font-size: 13px;
    color: #666;
    margin-bottom: 4px;
  }
}
</style>

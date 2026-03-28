<template>
  <div class="upload">
    <header class="page-header">
      <h1>上传</h1>
    </header>
    
    <div class="upload-area" @click="triggerFileInput">
      <input 
        type="file" 
        ref="fileInput" 
        accept="image/*,video/*"
        multiple
        @change="handleFileChange"
        style="display: none"
      />
      <div class="upload-icon">+</div>
      <p>点击上传图片或视频</p>
      <span>支持 JPG、PNG、GIF、MP4 等格式</span>
    </div>
    
    <div v-if="previewFiles.length" class="preview-section">
      <h3>已选择 {{ previewFiles.length }} 个文件</h3>
      <div class="preview-grid">
        <div v-for="(file, index) in previewFiles" :key="index" class="preview-item">
          <img v-if="file.type.startsWith('image')" :src="file.url" alt="Preview" />
          <video v-else :src="file.url" controls></video>
          <button class="remove-btn" @click="removeFile(index)">×</button>
        </div>
      </div>
    </div>
    
    <div v-if="previewFiles.length" class="form-section">
      <div class="form-item">
        <label>标题</label>
        <input v-model="title" placeholder="给收藏品起个标题" />
      </div>
      
      <div class="form-item">
        <label>描述</label>
        <textarea v-model="description" placeholder="说点什么..." rows="3"></textarea>
      </div>
      
      <div class="form-item">
        <label>标签</label>
        <input v-model="tagsInput" placeholder="用逗号分隔标签" />
      </div>
      
      <div class="form-item">
        <label>添加到收藏夹</label>
        <select v-model="selectedCollection">
          <option value="">不添加</option>
          <option v-for="col in collections" :key="col.id" :value="col.id">
            {{ col.name }}
          </option>
        </select>
      </div>
      
      <button class="submit-btn" @click="handleUpload" :disabled="uploading">
        {{ uploading ? '上传中...' : '发布' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const fileInput = ref(null)
const previewFiles = ref([])
const title = ref('')
const description = ref('')
const tagsInput = ref('')
const selectedCollection = ref('')
const uploading = ref(false)

const collections = ref([
  { id: 1, name: '风景大片' },
  { id: 2, name: '猫咪合集' },
  { id: 3, name: '设计灵感' }
])

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileChange = (e) => {
  const files = Array.from(e.target.files)
  files.forEach(file => {
    const url = URL.createObjectURL(file)
    previewFiles.value.push({ file, url })
  })
}

const removeFile = (index) => {
  URL.revokeObjectURL(previewFiles.value[index].url)
  previewFiles.value.splice(index, 1)
}

const handleUpload = async () => {
  if (!title.value) {
    alert('请输入标题')
    return
  }
  
  uploading.value = true
  
  // 模拟上传
  setTimeout(() => {
    uploading.value = false
    alert('发布成功！')
    previewFiles.value = []
    title.value = ''
    description.value = ''
    tagsInput.value = ''
  }, 1500)
}
</script>

<style lang="scss" scoped>
.upload {
  min-height: 100vh;
  background: #0a0a0a;
  color: #fff;
  padding: 80px 40px 40px;
}

.page-header {
  margin-bottom: 32px;
  
  h1 {
    font-size: 28px;
    font-weight: 600;
  }
}

.upload-area {
  border: 2px dashed #333;
  border-radius: 16px;
  padding: 60px 20px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.2s;
  
  &:hover {
    border-color: #666;
  }
}

.upload-icon {
  font-size: 48px;
  color: #666;
  margin-bottom: 16px;
}

p {
  font-size: 16px;
  margin-bottom: 8px;
}

span {
  font-size: 13px;
  color: #666;
}

.preview-section {
  margin-top: 32px;
  
  h3 {
    font-size: 16px;
    margin-bottom: 16px;
  }
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.preview-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  
  img, video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  background: rgba(0,0,0,0.7);
  border: none;
  border-radius: 50%;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}

.form-section {
  margin-top: 32px;
}

.form-item {
  margin-bottom: 24px;
  
  label {
    display: block;
    font-size: 14px;
    color: #888;
    margin-bottom: 8px;
  }
  
  input, textarea, select {
    width: 100%;
    padding: 12px 16px;
    background: #1a1a1a;
    border: 1px solid #333;
    border-radius: 8px;
    color: #fff;
    font-size: 14px;
    
    &::placeholder {
      color: #555;
    }
  }
  
  textarea {
    resize: vertical;
  }
  
  select {
    appearance: none;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23888' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 16px center;
  }
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: #fff;
  color: #0a0a0a;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}
</style>

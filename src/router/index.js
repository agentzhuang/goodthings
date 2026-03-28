import { createRouter, createWebHistory } from 'vue-router'
import Discover from '../views/Discover.vue'
import Collection from '../views/Collection.vue'
import Upload from '../views/Upload.vue'
import About from '../views/About.vue'
import Detail from '../views/Detail.vue'

const routes = [
  { path: '/', name: 'discover', component: Discover },
  { path: '/collection', name: 'collection', component: Collection },
  { path: '/upload', name: 'upload', component: Upload },
  { path: '/about', name: 'about', component: About },
  { path: '/detail/:id', name: 'detail', component: Detail, props: true }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

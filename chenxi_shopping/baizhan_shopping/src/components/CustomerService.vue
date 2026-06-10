<template>
  <div v-if="visible" class="customer-service-container">
    <div class="chat-header">
      <div class="header-title">
        <customer-service-outlined />
        <span>在线客服</span>
      </div>
      <div class="header-actions">
        <minus-outlined @click="minimize" class="action-btn" />
        <close-outlined @click="close" class="action-btn" />
      </div>
    </div>
    
    <div v-if="!minimized" class="chat-body">
      <div class="messages-container" ref="messagesContainer">
        <div v-for="message in messages" :key="message.id" class="message-item" :class="message.type">
          <div class="message-avatar">
            <user-outlined v-if="message.type === 'user'" />
            <robot-outlined v-else />
          </div>
          <div class="message-content">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>
        <div v-if="loading" class="message-item service">
          <div class="message-avatar">
            <robot-outlined />
          </div>
          <div class="message-content">
            <div class="message-text typing">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="input-container">
        <a-input 
          v-model:value="inputMessage" 
          placeholder="请输入您的问题..."
          @keyup.enter="sendMessage"
          :disabled="loading"
        />
        <a-button 
          type="primary" 
          @click="sendMessage"
          :loading="loading"
          :disabled="!inputMessage.trim()"
        >
          发送
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { CustomerServiceOutlined, CloseOutlined, MinusOutlined, UserOutlined, RobotOutlined } from '@ant-design/icons-vue'
import { Input as AInput, Button as AButton, message } from 'ant-design-vue'
import { axios } from '@/utils'

const visible = ref(false)
const minimized = ref(false)
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const messages = reactive([])

let messageId = 0

const initWelcomeMessage = () => {
  messages.push({
    id: ++messageId,
    type: 'service',
    content: '您好！我是智能客服助手，有什么可以帮助您的吗？',
    timestamp: new Date()
  })
}

const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const userMessage = inputMessage.value.trim()
  
  messages.push({
    id: ++messageId,
    type: 'user',
    content: userMessage,
    timestamp: new Date()
  })
  
  inputMessage.value = ''
  scrollToBottom()
  
  loading.value = true
  
  try {
    const userId = sessionStorage.getItem('userId') || '0'
    const response = await axios.get('/user/custcare/answer', {
      params: {
        message: userMessage
      },
      headers: {
        'userId': userId
      }
    })
    
    messages.push({
      id: ++messageId,
      type: 'service',
      content: response || '抱歉，我暂时无法理解您的问题，请稍后再试。',
      timestamp: new Date()
    })
    
  } catch (error) {
    console.error('客服API调用失败:', error)
    messages.push({
      id: ++messageId,
      type: 'service',
      content: '抱歉，服务暂时不可用，请稍后再试。',
      timestamp: new Date()
    })
    message.error('客服服务暂时不可用')
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const open = () => {
  visible.value = true
  minimized.value = false
  if (messages.length === 0) {
    initWelcomeMessage()
  }
  scrollToBottom()
}

const close = () => {
  visible.value = false
  minimized.value = false
}

const minimize = () => {
  minimized.value = !minimized.value
}

const handleOpenCustomerService = () => {
  open()
}

onMounted(() => {
  window.addEventListener('openCustomerService', handleOpenCustomerService)
})

onUnmounted(() => {
  window.removeEventListener('openCustomerService', handleOpenCustomerService)
})
</script>

<style scoped>
.customer-service-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 350px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: var(--primary-color);
  color: white;
  border-radius: 8px 8px 0 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.chat-body {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f5f5;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.message-item.user .message-avatar {
  background: var(--primary-color);
  color: white;
  margin-left: 8px;
}

.message-item.service .message-avatar {
  background: #1890ff;
  color: white;
  margin-right: 8px;
}

.message-content {
  max-width: 70%;
}

.message-text {
  padding: 8px 12px;
  border-radius: 12px;
  word-wrap: break-word;
  line-height: 1.4;
}

.message-item.user .message-text {
  background: var(--primary-color);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.service .message-text {
  background: white;
  color: #333;
  border: 1px solid #e1e1e1;
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  text-align: center;
}

.typing {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px !important;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #ccc;
  animation: typing 1.4s infinite;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

.input-container {
  display: flex;
  padding: 12px;
  border-top: 1px solid #e1e1e1;
  background: white;
  border-radius: 0 0 8px 8px;
  gap: 8px;
}

.input-container .ant-input {
  flex: 1;
}

.messages-container::-webkit-scrollbar {
  width: 4px;
}

.messages-container::-webkit-scrollbar-track {
  background: transparent;
}

.messages-container::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 2px;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: #999;
}
</style>

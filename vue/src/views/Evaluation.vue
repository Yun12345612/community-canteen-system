<template>
  <div class="evaluation-page">
    <h2>商家评价管理</h2>

    <!-- Tab切换 -->
    <el-tabs v-model="activePage" class="main-tabs">
      <!-- 订单评价Tab -->
      <el-tab-pane label="订单评价" name="order">
        <div class="toolbar">
          <el-input
            v-model="keyword"
            placeholder="搜索订单号/用户/评价内容"
            clearable
            style="max-width: 360px;"
          />
          <el-button type="primary" @click="openCreateDialog">新增评价</el-button>
        </div>
        
        <!-- 空状态提示 -->
        <el-empty 
          v-if="filteredEvaluationList.length === 0 && !loading"
          description="暂无评价数据"
          :image-size="200"
        />
        
        <!-- 评价列表表格 -->
        <el-table 
          v-else
          :data="filteredEvaluationList" 
          border 
          stripe
          v-loading="loading"
        >
          <!-- 订单号 -->
          <el-table-column label="订单号" prop="orderNo" width="150" align="center" />
          
          <!-- 用户信息 -->
          <el-table-column label="用户信息" width="200" align="center">
            <template #default="scope">
              <div class="user-info">
                <el-avatar 
                  v-if="scope.row.userAvatar" 
                  :size="32" 
                  :src="scope.row.userAvatar"
                  style="vertical-align: middle;"
                />
                <el-avatar v-else :size="32" style="vertical-align: middle;">
                  {{ scope.row.userName?.charAt(0) || '匿' }}
                </el-avatar>
                <div style="display: inline-block; margin-left: 10px; text-align: left;">
                  <div style="font-weight: 500;">{{ scope.row.userName || '匿名用户' }}</div>
                  <div style="font-size: 12px; color: #999;">ID: {{ scope.row.userId }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <!-- 评分 -->
          <el-table-column label="评分" width="120" align="center">
            <template #default="scope">
              <el-rate 
                v-model="scope.row.score" 
                disabled 
                :max="5"
                text-color="#ff9900"
              />
              <span style="color: #ff9900; margin-left: 8px;">{{ scope.row.score }}分</span>
            </template>
          </el-table-column>
          
          <!-- 评价内容 -->
          <el-table-column label="评价内容" prop="content" min-width="200">
            <template #default="scope">
              <div class="content-cell">{{ scope.row.content }}</div>
            </template>
          </el-table-column>
          
          <!-- 评价图片 -->
          <el-table-column label="评价图片" width="150">
            <template #default="scope">
              <div v-if="scope.row.imgUrls" class="image-list">
                <el-image
                  v-for="(img, index) in parseImageUrls(scope.row.imgUrls)"
                  :key="index"
                  :src="img"
                  :preview-src-list="parseImageUrls(scope.row.imgUrls)"
                  fit="cover"
                  style="width: 40px; height: 40px; margin-right: 5px; border-radius: 4px;"
                />
                <span v-if="parseImageUrls(scope.row.imgUrls).length">共{{ parseImageUrls(scope.row.imgUrls).length }}张</span>
              </div>
              <span v-else style="color: #999; font-size: 12px;">暂无图片</span>
            </template>
          </el-table-column>
          
          <!-- 评价时间 -->
          <el-table-column label="评价时间" width="180">
            <template #default="scope">
              {{ formatTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          
          <!-- 商家回复 -->
          <el-table-column label="商家回复" min-width="220">
            <template #default="scope">
              <div v-if="scope.row.merchantReply" class="reply-cell">
                <div class="reply-content">{{ scope.row.merchantReply }}</div>
                <div class="reply-time" v-if="scope.row.replyTime">
                  回复于 {{ formatTime(scope.row.replyTime) }}
                </div>
                <!-- 查看菜品评价按钮 -->
                <el-button 
                  type="info" 
                  size="small" 
                  plain
                  style="margin-top: 8px;"
                  @click.stop="viewDishEvaluations(scope.row)"
                >
                  查看菜品评价
                </el-button>
                <el-button
                  type="warning"
                  size="small"
                  plain
                  style="margin-top: 8px; margin-left: 8px;"
                  @click.stop="removeReply(scope.row)"
                >
                  删除回复
                </el-button>
              </div>
              <div v-else class="action-buttons">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click.stop="replyEvaluation(scope.row)"
                >
                  回复评价
                </el-button>
                <el-button 
                  type="info" 
                  size="small" 
                  plain
                  style="margin-left: 8px;"
                  @click.stop="viewDishEvaluations(scope.row)"
                >
                  查看菜品评价
                </el-button>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="180" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" plain @click.stop="openEditDialog(scope.row)">编辑</el-button>
              <el-button type="danger" size="small" plain @click.stop="removeEvaluation(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 菜品评价Tab -->
      <el-tab-pane label="菜品评价" name="dish">
        <div class="toolbar">
          <el-input
            v-model="dishReviewKeyword"
            placeholder="搜索菜品名称/用户/评价内容"
            clearable
            style="max-width: 360px;"
          />
        </div>

        <!-- 空状态提示 -->
        <el-empty 
          v-if="filteredDishReviewList.length === 0 && !dishReviewLoading"
          description="暂无菜品评价数据"
          :image-size="200"
        />

        <!-- 菜品评价列表表格 -->
        <el-table 
          v-else
          :data="filteredDishReviewList" 
          border 
          stripe
          v-loading="dishReviewLoading"
        >
          <!-- 菜品名称 -->
          <el-table-column label="菜品名称" prop="dishName" width="150" align="center" />
          
          <!-- 用户信息 -->
          <el-table-column label="用户信息" width="200" align="center">
            <template #default="scope">
              <div class="user-info">
                <el-avatar 
                  v-if="scope.row.userAvatar" 
                  :size="32" 
                  :src="scope.row.userAvatar"
                  style="vertical-align: middle;"
                />
                <el-avatar v-else :size="32" style="vertical-align: middle;">
                  {{ scope.row.userName?.charAt(0) || '匿' }}
                </el-avatar>
                <div style="display: inline-block; margin-left: 10px; text-align: left;">
                  <div style="font-weight: 500;">{{ scope.row.userName || '匿名用户' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <!-- 评分 -->
          <el-table-column label="评分" width="150" align="center">
            <template #default="scope">
              <el-rate 
                v-model="scope.row.score" 
                disabled 
                :max="5"
                text-color="#ff9900"
              />
              <span style="color: #ff9900; margin-left: 8px;">{{ scope.row.score }}分</span>
            </template>
          </el-table-column>
          
          <!-- 评价内容 -->
          <el-table-column label="评价内容" prop="content" min-width="250">
            <template #default="scope">
              <div class="content-cell">{{ scope.row.content || '用户未填写评价' }}</div>
            </template>
          </el-table-column>
          
          <!-- 评价时间 -->
          <el-table-column label="评价时间" width="180">
            <template #default="scope">
              {{ formatTime(scope.row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 1. 查看菜品评价对话框 -->
    <el-dialog
      v-model="dishEvalDialogVisible"
      title="菜品评价详情"
      width="600px"
    >
      <div v-loading="dishEvalLoading">
        <div v-if="currentDishEvaluations.length" class="dish-eval-list">
          <div v-for="dishEval in currentDishEvaluations"
               :key="dishEval.id || dishEval.dishId"
               class="dish-eval-card">
            <div class="dish-header">
              <span class="dish-name">{{ dishEval.dishName }}</span>
              <div style="flex: 1; display: flex; align-items: center;">
                <el-rate v-model="dishEval.score" disabled size="small" />
                <span class="score-badge">{{ dishEval.score }}分</span>
              </div>
            </div>
            <div class="dish-content">
              {{ dishEval.content || '未填写评价' }}
              <div v-if="dishEval.createTime" class="dish-time">
                评价时间：{{ formatTime(dishEval.createTime) }}
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-tip">
          该订单暂无菜品评价，请通过"菜品评价"按钮进行评价
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dishEvalDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 2. 回复评价对话框（标签页式） -->
    <el-dialog 
      v-model="replyDialogVisible" 
      title="评价详情与回复"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab">
        <!-- 标签页1：整体评价 -->
        <el-tab-pane label="整体评价" name="overall">
          <div class="tab-content">
            <div class="info-item">
              <strong>订单号：</strong>{{ currentOrderNo }}
            </div>
            <div class="info-item">
              <strong>用户：</strong>{{ currentUserName || '匿名用户' }}
            </div>
            <div class="info-item">
              <strong>整体评分：</strong>
              <el-rate v-model="currentOverallScore" disabled />
              <span class="score-text">{{ currentOverallScore }}分</span>
            </div>
            <div class="info-item">
              <strong>评价内容：</strong>
              <p class="eval-content">{{ currentEvaluationContent || '暂无评价内容' }}</p>
            </div>
            <div class="info-item" v-if="currentImgUrls && currentImgUrls.length">
              <strong>评价图片：</strong>
              <div class="image-preview">
                <el-image
                  v-for="(img, index) in currentImgUrls"
                  :key="index"
                  :src="img"
                  fit="cover"
                  style="width: 60px; height: 60px; margin-right: 10px; border-radius: 4px;"
                />
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 标签页2：菜品评价 -->
        <el-tab-pane label="菜品评价" name="dish">
          <div v-if="currentDishEvaluations.length" class="dish-eval-list">
            <div v-for="dishEval in currentDishEvaluations" 
                 :key="dishEval.id || dishEval.dishId"
                 class="dish-eval-card">
              <div class="dish-header">
                <span class="dish-name">{{ dishEval.dishName }}</span>
                <div style="flex: 1; display: flex; align-items: center;">
                  <el-rate v-model="dishEval.score" disabled size="small" />
                  <span class="score">{{ dishEval.score }}分</span>
                </div>
              </div>
              <div class="dish-content">
                {{ dishEval.content || '未填写评价' }}
                <div v-if="dishEval.createTime" class="dish-time">
                  评价时间：{{ formatTime(dishEval.createTime) }}
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-tip">
            该评价暂无菜品评价
          </div>
        </el-tab-pane>
        
        <!-- 标签页3：商家回复 -->
        <el-tab-pane label="商家回复" name="reply">
          <div class="reply-form">
            <p style="color: #666; margin-bottom: 10px;">请输入回复内容（最多200字）：</p>
            <el-input 
              v-model="replyContent" 
              type="textarea" 
              :rows="5" 
              placeholder="感谢您的评价，我们会继续改进..."
              maxlength="200"
              show-word-limit
            />
            <div style="margin-top: 15px; color: #999; font-size: 12px;">
              提示：您的回复将展示给所有用户查看，请使用礼貌用语。
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitReply" 
            :loading="submitting"
            :disabled="!replyContent.trim()"
          >
            提交回复
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑评价" width="640px" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="评分">
          <el-rate v-model="editForm.score" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="editForm.content" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="图片链接">
          <el-input v-model="editForm.imgUrls" placeholder="多个链接用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="createDialogVisible" title="新增评价" width="640px" :close-on-click-modal="false">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="订单ID">
          <el-input v-model="createForm.orderId" placeholder="必填" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="createForm.userId" placeholder="必填" />
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="createForm.score" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="createForm.content" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="图片链接">
          <el-input v-model="createForm.imgUrls" placeholder="多个链接用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitCreate">新增</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus'
import { 
  getMerchantEvaluationList, 
  replyMerchantEvaluation,
  getDishEvaluations,
  deleteMerchantReply,
  deleteMerchantEvaluation,
  updateMerchantEvaluation,
  createMerchantEvaluation,
  getAllDishReviews
} from '@/api/evaluation'

// ========== 状态变量 ==========
const activePage = ref('order')
const evaluationList = ref([])
const keyword = ref('')
const loading = ref(false)
const replyDialogVisible = ref(false)
const dishEvalDialogVisible = ref(false)
const activeTab = ref('overall')
const replyContent = ref('')
const currentEvaluationId = ref(null)
const currentOrderNo = ref('')
const currentUserName = ref('')
const currentOverallScore = ref(0)
const currentEvaluationContent = ref('')
const currentImgUrls = ref([])
const currentDishEvaluations = ref([])
const submitting = ref(false)

// 菜品评价缓存
const dishEvalCache = ref({})
const dishEvalLoading = ref(false)
const editDialogVisible = ref(false)
const createDialogVisible = ref(false)
const saving = ref(false)
const editForm = ref({ id: null, score: 5, content: '', imgUrls: '' })
const createForm = ref({ orderId: '', userId: '', score: 5, content: '', imgUrls: '' })

// 菜品评价列表
const dishReviewList = ref([])
const dishReviewKeyword = ref('')
const dishReviewLoading = ref(false)

const filteredEvaluationList = computed(() => {
  const kw = keyword.value.trim()
  if (!kw) return evaluationList.value
  return evaluationList.value.filter((item) => {
    const fields = [item.orderNo, item.userName, item.content].filter(Boolean).join(' ')
    return fields.includes(kw)
  })
})

const filteredDishReviewList = computed(() => {
  const kw = dishReviewKeyword.value.trim()
  if (!kw) return dishReviewList.value
  return dishReviewList.value.filter((item) => {
    const fields = [item.dishName, item.userName, item.content].filter(Boolean).join(' ')
    return fields.includes(kw)
  })
})

// 监听Tab切换
watch(activePage, (newVal) => {
  if (newVal === 'dish' && dishReviewList.value.length === 0) {
    loadDishReviews()
  }
})

// ========== 核心方法 ==========

// 1. 加载评价列表
const loadEvaluations = async () => {
  loading.value = true
  try {
    const merchantIdStr = localStorage.getItem('merchantId');
    
    if (!merchantIdStr) {
      const merchantInfo = JSON.parse(localStorage.getItem('merchantInfo') || '{}');
      const merchantId = merchantInfo.id || merchantInfo.merchantId || 7;
      
      const res = await getMerchantEvaluationList(merchantId);
      if (res.code === 200) {
        evaluationList.value = res.data || []
      } else {
        ElMessage.error(res.msg || '获取评价失败')
      }
    } else {
      const merchantId = Number(merchantIdStr);
      const res = await getMerchantEvaluationList(merchantId);
      if (res.code === 200) {
        evaluationList.value = res.data || []
      } else {
        ElMessage.error(res.msg || '获取评价失败')
      }
    }
  } catch (error) {
    console.error('【评价列表】加载失败:', error)
    ElMessage.error('获取评价失败，请检查网络')
  } finally {
    loading.value = false
  }
}

// 加载菜品评价列表
const loadDishReviews = async () => {
  dishReviewLoading.value = true
  try {
    const res = await getAllDishReviews()
    if (res.code === 200) {
      dishReviewList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取菜品评价失败')
    }
  } catch (error) {
    console.error('【菜品评价列表】加载失败:', error)
    ElMessage.error('获取菜品评价失败')
  } finally {
    dishReviewLoading.value = false
  }
}

const openEditDialog = (row) => {
  editForm.value = {
    id: row.id,
    score: row.score || 5,
    content: row.content || '',
    imgUrls: row.imgUrls || ''
  }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  saving.value = true
  try {
    const payload = {
      id: Number(editForm.value.id),
      score: editForm.value.score,
      content: editForm.value.content,
      imgUrls: editForm.value.imgUrls
    }
    await updateMerchantEvaluation(payload)
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    await loadEvaluations()
  } catch (e) {
    ElMessage.error('更新失败')
  } finally {
    saving.value = false
  }
}

const openCreateDialog = () => {
  createForm.value = { orderId: '', userId: '', score: 5, content: '', imgUrls: '' }
  createDialogVisible.value = true
}

const submitCreate = async () => {
  saving.value = true
  try {
    const payload = {
      orderId: String(createForm.value.orderId).trim(),
      userId: Number(createForm.value.userId),
      score: createForm.value.score,
      content: createForm.value.content,
      imgUrls: createForm.value.imgUrls
    }
    await createMerchantEvaluation(payload)
    ElMessage.success('新增成功')
    createDialogVisible.value = false
    await loadEvaluations()
  } catch (e) {
    ElMessage.error('新增失败')
  } finally {
    saving.value = false
  }
}

const removeEvaluation = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该评价？', '提示', { type: 'warning' })
    await deleteMerchantEvaluation(row.id)
    ElMessage.success('删除成功')
    await loadEvaluations()
  } catch (e) {
  }
}

const removeReply = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该回复？', '提示', { type: 'warning' })
    await deleteMerchantReply(row.id)
    ElMessage.success('删除回复成功')
    await loadEvaluations()
  } catch (e) {
  }
}

// 2. 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  if (replyContent.value.trim().length > 200) {
    ElMessage.warning('回复内容不能超过200字')
    return
  }
  
  submitting.value = true
  try {
    const res = await replyMerchantEvaluation({
      evaluationId: currentEvaluationId.value,
      merchantReply: replyContent.value.trim()
    })
    
    if (res.code === 200) {
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      replyContent.value = ''
      loadEvaluations()
    } else {
      ElMessage.error(res.msg || '回复失败')
    }
  } catch (error) {
    console.error('提交回复失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 3. 打开回复对话框
const replyEvaluation = async (evaluation) => {
  currentEvaluationId.value = evaluation.id
  currentOrderNo.value = evaluation.orderNo || '未知订单'
  currentUserName.value = evaluation.userName || '匿名用户'
  currentOverallScore.value = evaluation.score || 0
  currentEvaluationContent.value = evaluation.content || ''
  currentImgUrls.value = parseImageUrls(evaluation.imgUrls)
  currentDishEvaluations.value = []

  replyContent.value = evaluation.merchantReply || ''
  activeTab.value = 'overall'
  replyDialogVisible.value = true

  // 调用API获取菜品评价
  try {
    const res = await getDishEvaluations(evaluation.id)
    if (res.code === 200 && res.data) {
      currentDishEvaluations.value = res.data
    }
  } catch (error) {
    console.error('获取菜品评价失败:', error)
  }
}

// 4. 查看菜品评价（独立对话框）
const viewDishEvaluations = async (evaluation) => {
  currentOrderNo.value = evaluation.orderNo || '未知订单'
  currentDishEvaluations.value = []
  dishEvalDialogVisible.value = true
  dishEvalLoading.value = true

  try {
    // 调用后端API获取菜品评价
    const res = await getDishEvaluations(evaluation.id)
    if (res.code === 200 && res.data && res.data.length > 0) {
      currentDishEvaluations.value = res.data
    } else {
      currentDishEvaluations.value = []
    }
  } catch (error) {
    console.error('获取菜品评价失败:', error)
    currentDishEvaluations.value = []
    ElMessage.warning('获取菜品评价失败')
  } finally {
    dishEvalLoading.value = false
  }
}

// 5. 加载菜品评价（保留，但作为兜底方案）
const loadDishEvaluations = async (evaluationId) => {
  const evalIdNum = Number(evaluationId);
  
  if (dishEvalCache.value[evalIdNum]) {
    return dishEvalCache.value[evalIdNum]
  }
  
  try {
    const res = await getDishEvaluations(evalIdNum)
    
    if (res.code === 200 && res.data) {
      dishEvalCache.value[evalIdNum] = res.data
      return res.data
    } else {
      return []
    }
  } catch (error) {
    console.error('加载菜品评价失败:', error)
    throw error
  }
}

// ========== 工具方法 ==========

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return '--'
  try {
    const date = new Date(timestamp)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (error) {
    return '无效时间'
  }
}

// 解析图片URL
const parseImageUrls = (imgUrls) => {
  if (!imgUrls) return []
  try {
    if (imgUrls.startsWith('[')) {
      return JSON.parse(imgUrls)
    }
    if (imgUrls.includes(',')) {
      return imgUrls.split(',').filter(url => url.trim()).map(url => url.trim())
    }
    return [imgUrls]
  } catch (error) {
    console.error('解析图片URL失败:', error)
    return []
  }
}

// ========== 生命周期 ==========

onMounted(() => {
  loadEvaluations()
})
</script>

<style scoped>
.evaluation-page {
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 40px);
}

h2 {
  margin-bottom: 20px;
  color: #333;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.main-tabs {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: center;
}

.content-cell {
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  align-items: center;
}

.reply-cell {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
  border-left: 4px solid #67c23a;
}

.reply-content {
  color: #333;
  margin-bottom: 8px;
  line-height: 1.5;
}

.reply-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.action-buttons {
  display: flex;
  align-items: center;
}

/* 标签页样式 */
.tab-content {
  padding: 15px;
}

.info-item {
  margin-bottom: 15px;
  line-height: 1.6;
}

.info-item strong {
  display: inline-block;
  width: 80px;
  color: #333;
  font-weight: 600;
}

.score-text {
  margin-left: 10px;
  color: #ff9900;
  font-weight: bold;
}

.eval-content {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  margin-top: 5px;
  color: #666;
  line-height: 1.6;
}

.image-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
}

/* 菜品评价样式 */
.dish-eval-list {
  padding: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.dish-eval-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 12px;
  border-left: 4px solid #409eff;
  transition: all 0.3s;
}

.dish-eval-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.dish-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.dish-name {
  font-weight: bold;
  color: #333;
  min-width: 120px;
  font-size: 15px;
}

.score-badge, .score {
  background-color: #ff9900;
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  margin-left: 10px;
  font-weight: 500;
}

.dish-content {
  color: #666;
  line-height: 1.6;
  padding-left: 10px;
  font-size: 14px;
}

.dish-time {
  margin-top: 5px;
  color: #999;
  font-size: 12px;
}

.empty-tip {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 14px;
}

.reply-form {
  padding: 15px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 15px;
  border-top: 1px solid #e8e8e8;
}
</style>
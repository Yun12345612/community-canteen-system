<!-- src/views/merchant/DishManager.vue -->
<template>
  <div class="dish-manager-page">
    <!-- 顶部操作区 -->
    <div class="page-header">
      <h2>菜品管理</h2>
      <el-button type="primary" @click="openAddDialog">新增菜品</el-button>
    </div>

    <!-- 菜品列表区 -->
    <el-table
      :data="dishList"
      border
      stripe
      style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="id" label="菜品ID" align="center" width="80" />
      <el-table-column
        prop="dishName"
        label="菜品名称"
        align="center"
        min-width="120"
      />
      <el-table-column label="菜品图片" align="center" width="120">
        <template #default="scope">
          <div class="dish-image-cell">
            <el-image
              v-if="scope.row.coverImg"
              :src="scope.row.coverImg"
              :preview-src-list="[scope.row.coverImg]"
              fit="cover"
              class="dish-image"
              :hide-on-click-modal="true"
              :z-index="9999"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
            <div v-else class="no-image">
              <el-icon><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="categoryName"
        label="菜品分类"
        align="center"
        width="100"
      />
      <el-table-column
        prop="description"
        label="菜品描述"
        align="center"
        width="100"
      />
      <el-table-column
        prop="price"
        label="菜品价格"
        align="center"
        width="100"
        :formatter="formatPrice"
      />
      <el-table-column prop="stock" label="库存" align="center" width="80" />
      <!-- 菜品状态列 -->
      <el-table-column
        prop="status"
        label="菜品状态"
        align="center"
        width="100"
      >
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? "已上架" : "已下架" }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" align="center" width="180">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)"
            >编辑</el-button
          >
          <el-button
            size="small"
            :type="scope.row.status === 1 ? 'danger' : 'success'"
            @click="
              scope.row.status === 1
                ? handleTakeDown(scope.row.id)
                : handlePutOn(scope.row.id)
            "
          >
            {{ scope.row.status === 1 ? "下架" : "上架" }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑菜品弹窗（复用一个弹窗，通过flag区分） -->
    <el-dialog
      :title="isEdit ? '编辑菜品' : '新增菜品'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        :model="dishForm"
        :rules="formRules"
        ref="dishFormRef"
        label-width="100px"
      >
        <!-- 菜品ID：编辑时展示，新增时隐藏（无prop，禁用无需校验） -->
        <el-form-item v-if="isEdit" label="菜品ID">
          <el-input v-model="dishForm.id" disabled />
        </el-form-item>
        <el-form-item label="菜品名称" prop="dishName">
          <el-input v-model="dishForm.dishName" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="菜品分类" prop="categoryId">
          <el-select v-model="dishForm.categoryId" placeholder="请选择菜品分类">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品价格" prop="price">
          <el-input-number
            v-model="dishForm.price"
            :min="0.01"
            step="0.01"
            precision="2"
            placeholder="请输入菜品价格"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜品库存" prop="stock">
          <el-input-number
            v-model="dishForm.stock"
            :min="0"
            step="1"
            placeholder="请输入菜品库存"
            style="width: 100%"
          />
        </el-form-item>
        <!-- 添加图片上传组件 -->
        <el-form-item label="菜品图片" prop="coverImg">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :headers="uploadHeaders"
            accept=".jpg,.jpeg,.png,.gif,.webp"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <div v-else class="uploader-placeholder">
              <el-icon class="uploader-icon"><Plus /></el-icon>
              <div class="uploader-text">点击上传菜品图片</div>
              <div class="uploader-hint">建议尺寸：750x750px，2MB以内</div>
            </div>
          </el-upload>
          <div class="image-preview-hint" v-if="imageUrl">
            当前图片：<a :href="imageUrl" target="_blank">预览</a>
            <el-button type="text" size="small" @click="clearImage"
              >清除</el-button
            >
          </div>
        </el-form-item>
        <el-form-item label="菜品描述" prop="description">
          <el-input
            v-model="dishForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入菜品描述（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit"
            >确认{{ isEdit ? "修改" : "添加" }}</el-button
          >
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Picture } from "@element-plus/icons-vue";
// 导入菜品API
import {
  dishAdd,
  dishEdit,
  dishTakeDown,
  dishPutOn,
  dishGetMyList,
} from "@/api/dish";
// 导入分类API
import { categoryGetValidList } from "@/api/category";
// 导入文件上传API
import { uploadFile } from "@/api/upload";

// 菜品列表数据
const dishList = ref([]);
// 分类列表（过滤无效数据，避免下拉空白）
const categoryList = ref([]);
// 弹窗相关
const dialogVisible = ref(false);
const isEdit = ref(false); // 区分新增/编辑
// 表单相关
const dishFormRef = ref(null);

// 表单数据
const dishForm = reactive({
  id: 0,
  dishName: "",
  categoryId: 0,
  price: 0.0,
  stock: 0,
  description: "",
  coverImg: "",
  tasteTag: "",
  createTime: "",
  updateTime: "",
  status: 1,
});

// 图片上传相关
const imageUrl = ref(""); // 预览图片URL
const imageFile = ref(null); // 上传的文件对象
const uploadUrl = `/api/common/upload`; // 上传地址（走Vite代理）

// 上传请求头（需要token）
const uploadHeaders = computed(() => {
  const token = localStorage.getItem("merchantToken") || "";
  return {
    merchantToken: token,
  };
});

// 表单校验规则
const formRules = reactive({
  dishName: [{ required: true, message: "请输入菜品名称", trigger: "blur" }],
  categoryId: [
    { required: true, message: "请选择菜品分类", trigger: "change" },
  ],
  price: [
    { required: true, message: "请输入菜品价格", trigger: "blur" },
    {
      type: "number",
      min: 0.01,
      message: "价格必须大于0.01元",
      trigger: "blur",
    },
  ],
  stock: [{ required: true, message: "请输入菜品库存", trigger: "blur" }],
});

// 页面初始化加载 - 加载菜品列表和分类列表
onMounted(() => {
  getDishList();
  getCategoryList();
});

// 获取当前商家菜品列表
const getDishList = async () => {
  try {
    const res = await dishGetMyList({});
    // 过滤无效数据，确保列表数据合法性
    dishList.value = (res.data || []).filter((item) => item && item.id);
  } catch (err) {
    ElMessage.error("菜品列表加载失败：" + (err.message || "网络异常"));
  }
};

// 获取有效菜品分类列表
const getCategoryList = async () => {
  try {
    const res = await categoryGetValidList({});
    categoryList.value = (res.data || []).filter(
      (item) => item && item.id && item.categoryName
    );
  } catch (err) {
    ElMessage.error("菜品分类加载失败：" + (err.message || "网络异常"));
  }
};

// 打开新增菜品弹窗
const openAddDialog = () => {
  dialogVisible.value = true;
  isEdit.value = false;
  
  // 重置表单校验状态和字段值
  if (dishFormRef.value) {
    dishFormRef.value.resetFields();
    dishFormRef.value.clearValidate();
  }
  
  // 重置表单数据
  Object.assign(dishForm, {
    id: 0,
    dishName: "",
    categoryId: 0,
    price: 0.0,
    stock: 0,
    description: "",
    coverImg: "",
    tasteTag: "",
    status: 1,
  });
  
  // 重置图片预览
  imageUrl.value = "";
  imageFile.value = null;
};

// 打开编辑菜品弹窗
const openEditDialog = (row) => {
  // 前置校验：防止无效数据编辑
  if (!row || !row.id || row.id === 0) {
    ElMessage.warning("菜品数据无效，无法编辑");
    return;
  }
  
  dialogVisible.value = true;
  isEdit.value = true;
  
  const editData = {
    ...row,
    id: Number(row.id),
    categoryId: Number(row.categoryId) || 0,
    price: Number(row.price) || 0.0,
    stock: Number(row.stock) || 0,
    status: Number(row.status) || 1,
    coverImg: row.coverImg || "",
  };
  
  // 回显数据到表单
  Object.assign(dishForm, editData);
  
  // 回显图片预览
  if (dishForm.coverImg) {
    imageUrl.value = dishForm.coverImg;
  } else {
    imageUrl.value = "";
  }
  imageFile.value = null;
  
  // 清除表单原有校验状态
  if (dishFormRef.value) {
    dishFormRef.value.clearValidate();
  }
};

// 提交表单 - 新增/编辑通用
const handleSubmit = async () => {
  // 前端表单校验：拦截未完善的必填项
  try {
    await dishFormRef.value.validate();
  } catch (err) {
    ElMessage.warning("请完善表单必填项后再提交");
    return;
  }

  try {
    // 如果有新图片，先上传
    if (imageFile.value && !dishForm.coverImg) {
      const uploadRes = await uploadFile(imageFile.value);
      if (uploadRes.code === 200) {
        dishForm.coverImg = uploadRes.data;
      } else {
        ElMessage.error("图片上传失败");
        return;
      }
    }

    if (isEdit.value) {
      const editParams = {
        id: Number(dishForm.id),
        dishName: dishForm.dishName,
        categoryId: Number(dishForm.categoryId),
        price: dishForm.price,
        stock: Number(dishForm.stock),
        description: dishForm.description,
        coverImg: dishForm.coverImg || "",
        status: Number(dishForm.status),
      };
      
      if (!editParams.id || editParams.id === 0) {
        ElMessage.warning("菜品ID无效，无法修改");
        return;
      }
      
      await dishEdit(editParams);
      ElMessage.success("菜品修改成功");
    } else {
      const addParams = {
        dishName: dishForm.dishName,
        categoryId: Number(dishForm.categoryId),
        price: dishForm.price,
        stock: Number(dishForm.stock),
        description: dishForm.description,
        coverImg: dishForm.coverImg || "",
        status: 1,
      };
      
      await dishAdd(addParams);
      ElMessage.success("菜品添加成功，默认上架");
    }
    
    // 关闭弹窗并刷新列表，保持数据同步
    dialogVisible.value = false;
    getDishList();
  } catch (err) {
    // 区分编辑/新增的错误提示
    ElMessage.error(
      err.message || (isEdit.value ? "菜品修改失败" : "菜品添加失败")
    );
  }
};

// 菜品下架操作
const handleTakeDown = async (id) => {
  try {
    await ElMessageBox.confirm(
      "确认下架该菜品？下架后小程序端将无法展示",
      "操作确认",
      {
        type: "warning",
        confirmButtonText: "确认下架",
        cancelButtonText: "取消",
      }
    );
    await dishTakeDown({ dishId: id });
    ElMessage.success("菜品下架成功");
    getDishList(); // 刷新列表，更新状态
  } catch (err) {
    // 仅在非取消操作时提示错误
    if (err.type !== "cancel") {
      ElMessage.error("菜品下架失败：" + (err.message || "网络异常"));
    }
  }
};

// 菜品上架操作
const handlePutOn = async (id) => {
  try {
    await dishPutOn({ dishId: id });
    ElMessage.success("菜品上架成功");
    getDishList(); // 刷新列表，更新状态
  } catch (err) {
    ElMessage.error("菜品上架失败：" + (err.message || "网络异常"));
  }
};

// 图片上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith("image/");
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("只能上传图片格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过2MB!");
    return false;
  }

  // 本地预览
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = (e) => {
    imageUrl.value = e.target.result;
    imageFile.value = file;
  };

  return true;
};

// 上传成功处理
const handleUploadSuccess = (response) => {
  console.log("上传响应:", response);
  if (response.code === 200 && response.data) {
    dishForm.coverImg = response.data;
    ElMessage.success("图片上传成功");
  } else {
    ElMessage.error(response.msg || "图片上传失败");
    imageUrl.value = "";
    imageFile.value = null;
  }
};

// 清除图片
const clearImage = () => {
  imageUrl.value = "";
  imageFile.value = null;
  dishForm.coverImg = "";
};

// 价格格式化 - 保留2位小数，添加人民币符号，处理空值
const formatPrice = (row) => {
  if (!row.price || isNaN(Number(row.price))) return "¥0.00";
  return `¥${Number(row.price).toFixed(2)}`;
};
</script>

<style scoped>
.dish-manager-page {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
  background-color: #fff;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.dialog-footer {
  text-align: right;
}

/* 图片上传样式 */
:deep(.avatar-uploader) {
  width: 150px;
  height: 150px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  background-color: #fafafa;
  transition: border-color 0.3s, background-color 0.3s;
}

:deep(.avatar-uploader:hover) {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.uploader-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #8c939d;
}

:deep(.uploader-icon) {
  font-size: 32px;
  margin-bottom: 8px;
}

.uploader-text {
  font-size: 14px;
  margin-bottom: 4px;
}

.uploader-hint {
  font-size: 12px;
  color: #c0c4cc;
}

:deep(.avatar) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.image-preview-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.image-preview-hint a {
  color: #409eff;
  text-decoration: none;
  margin: 0 8px;
}

.image-preview-hint a:hover {
  text-decoration: underline;
}

/* 表格中的图片样式 */
.dish-image-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60px;
}

.dish-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.2s;
}

.dish-image:hover {
  transform: scale(1.05);
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

.image-error .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 12px;
}

.no-image .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

/* 表格列宽优化，防止文字换行 */
:deep(.el-table-column) {
  white-space: nowrap;
}

/* 适配输入框/选择器宽度，统一表单样式 */
:deep(.el-input-number),
:deep(.el-select),
:deep(.el-input) {
  width: 100%;
}
</style>

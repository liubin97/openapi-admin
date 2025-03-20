<template>
  <div class="api-management">
    <el-tabs v-model="activeTab" class="api-tabs">
      <el-tab-pane label="API列表" name="list">
        <div class="list-container">
          <div class="header">
          <el-row :gutter="20">
            <el-col :span="16">
              <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="API名称">
                  <el-input v-model="searchForm.apiName" placeholder="请输入API名称" clearable />
                </el-form-item>
                <el-form-item label="接口编号">
                  <el-input v-model="searchForm.apiCode" placeholder="请输入接口编号" clearable />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                    <el-option label="启用" :value="1" />
                    <el-option label="禁用" :value="0" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSearch">查询</el-button>
                  <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
              </el-form>
            </el-col>
            <el-col :span="8" style="text-align: right">
              <el-button type="primary" @click="handleAdd">新增</el-button>
            </el-col>
          </el-row>
        </div>
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="apiName" label="API名称" width="180" />
      <el-table-column prop="apiCode" label="接口编号" width="180" />
      <el-table-column prop="apiDesc" label="接口说明" />
      <el-table-column prop="version" label="版本号" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button link type="primary" @click="handleTest(row)">测试</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增API' : dialogType === 'edit' ? '编辑API' : '查看API'"
      width="50%"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        :disabled="dialogType === 'view'"
      >
        <el-form-item label="API名称" prop="apiName">
          <el-input v-model="form.apiName" placeholder="请输入API名称" />
        </el-form-item>
        <el-form-item label="接口编号" prop="apiCode">
          <el-input v-model="form.apiCode" placeholder="请输入接口编号" />
        </el-form-item>
        <el-form-item label="接口说明" prop="apiDesc">
          <el-input
            v-model="form.apiDesc"
            type="textarea"
            placeholder="请输入接口说明"
          />
        </el-form-item>
        <el-form-item label="版本号" prop="version">
          <el-input v-model="form.version" placeholder="请输入版本号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            v-if="dialogType !== 'view'"
            type="primary"
            @click="handleSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
        </div>
      </el-tab-pane>
      <el-tab-pane label="目录管理" name="directory">
        <api-directory />
      </el-tab-pane>
      <el-tab-pane label="API维护" name="maintenance">
        <api-maintenance />
      </el-tab-pane>
      <el-tab-pane label="API反馈" name="feedback">
        <api-feedback />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post, put, del } from '../utils/api'
import { useRouter } from 'vue-router'
import ApiDirectory from './api-management/ApiDirectory.vue'
import ApiMaintenance from './api-management/ApiMaintenance.vue'
import ApiFeedback from './api-management/ApiFeedback.vue'

const activeTab = ref('list')

onMounted(() => {
  fetchData()
})

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  apiName: '',
  apiCode: '',
  status: ''
})

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const resetSearch = () => {
  Object.assign(searchForm, {
    apiName: '',
    apiCode: '',
    status: ''
  })
  handleSearch()
}

const fetchData = async () => {
  try {
    // 构建查询参数
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      apiName: searchForm.apiName,
      apiCode: searchForm.apiCode,
      status: searchForm.status
    }
    
    const data = await get('/api/apis/page', params)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error(error)
  }
}

const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const router = useRouter()

const form = reactive({
  apiName: '',
  apiCode: '',
  apiDesc: '',
  version: '',
  status: 1
})

const rules = {
  apiName: [{ required: true, message: '请输入API名称', trigger: 'blur' }],
  apiCode: [{ required: true, message: '请输入接口编号', trigger: 'blur' }],
  apiDesc: [{ required: true, message: '请输入接口说明', trigger: 'blur' }],
  version: [{ required: true, message: '请输入版本号', trigger: 'blur' }]
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    apiName: '',
    apiCode: '',
    apiDesc: '',
    version: '',
    status: 1
  })
}

const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

const handleView = (row) => {
  dialogType.value = 'view'
  dialogVisible.value = true
  Object.assign(form, row)
}

const handleTest = (row) => {
  router.push(`/test-case-management/${row.id}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该API吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await del(`/api/apis/${row.id}`)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
        console.error('删除API失败：', error)
      }
    })
    .catch(() => {})
}

const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await post('/api/apis', form)
        } else {
          await put('/api/apis', form)
        }
        
        ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
        dialogVisible.value = false
        resetForm()
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
        console.error('保存API失败：', error)
      }
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}
</script>

<style scoped>
.api-management {
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin: 16px;
}

.header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.search-form {
  .el-form-item {
    margin-bottom: 16px;
  }
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
  margin-top: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  
  :deep(th.el-table__cell) {
    background-color: #fafafa;
    color: #606266;
    font-weight: 500;
  }
  
  :deep(.el-table__row) {
    transition: background-color 0.3s;
    
    &:hover {
      background-color: #f5f7fa;
    }
  }
}

.pagination {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.api-tabs {
  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
  }
  
  :deep(.el-tabs__item) {
    font-size: 15px;
    padding: 0 24px;
    height: 48px;
    line-height: 48px;
    transition: all 0.3s;
    
    &.is-active {
      font-weight: 500;
    }
    
    &:hover {
      color: var(--primary-color);
    }
  }
}

:deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
  
  .el-dialog__header {
    margin: 0;
    padding: 20px 24px;
    border-bottom: 1px solid #f0f0f0;
    
    .el-dialog__title {
      font-size: 16px;
      font-weight: 500;
    }
  }
  
  .el-dialog__body {
    padding: 24px;
  }
  
  .el-dialog__footer {
    padding: 16px 24px;
    border-top: 1px solid #f0f0f0;
  }
}
</style>
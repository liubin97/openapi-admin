<template>
  <div class="api-feedback">
    <div class="header">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="API名称">
              <el-input v-model="searchForm.apiName" placeholder="请输入API名称" clearable />
            </el-form-item>
            <el-form-item label="反馈类型">
              <el-select v-model="searchForm.type" placeholder="请选择反馈类型" clearable>
                <el-option label="问题反馈" value="issue" />
                <el-option label="功能建议" value="suggestion" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                <el-option label="待处理" value="pending" />
                <el-option label="处理中" value="processing" />
                <el-option label="已完成" value="completed" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="8" style="text-align: right">
          <el-button type="primary" @click="handleAdd">新增反馈</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="apiName" label="API名称" width="180" />
      <el-table-column prop="type" label="反馈类型" width="100">
        <template #default="{ row }">
          <el-tag
            :type="row.type === 'issue' ? 'danger' : row.type === 'suggestion' ? 'warning' : 'info'"
          >
            {{ row.type === 'issue' ? '问题反馈' : row.type === 'suggestion' ? '功能建议' : '其他' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="反馈内容" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag
            :type="row.status === 'pending' ? 'info' : row.status === 'processing' ? 'warning' : 'success'"
          >
            {{ row.status === 'pending' ? '待处理' : row.status === 'processing' ? '处理中' : '已完成' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitter" label="提交人" width="120" />
      <el-table-column prop="submitTime" label="提交时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="handleProcess(row)">处理</el-button>
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
      :title="dialogType === 'add' ? '新增反馈' : dialogType === 'edit' ? '编辑反馈' : dialogType === 'process' ? '处理反馈' : '查看反馈'"
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
        <el-form-item label="反馈类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择反馈类型">
            <el-option label="问题反馈" value="issue" />
            <el-option label="功能建议" value="suggestion" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="反馈内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入反馈内容"
          />
        </el-form-item>
        <el-form-item v-if="dialogType === 'process'" label="处理状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择处理状态">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogType === 'process'" label="处理结果" prop="result">
          <el-input
            v-model="form.result"
            type="textarea"
            :rows="4"
            placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="dialogType !== 'view'" type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post, put, del } from '../../utils/api'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  apiName: '',
  type: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const form = reactive({
  id: null,
  apiName: '',
  type: '',
  content: '',
  status: 'pending',
  result: '',
  submitter: '',
  submitTime: null
})

const rules = {
  apiName: [{ required: true, message: '请输入API名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }],
  status: [{ required: true, message: '请选择处理状态', trigger: 'change' }],
  result: [{ required: true, message: '请输入处理结果', trigger: 'blur' }]
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const resetSearch = () => {
  Object.assign(searchForm, {
    apiName: '',
    type: '',
    status: ''
  })
  handleSearch()
}

const fetchData = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const data = await get('/api/feedback/page', params)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error(error)
  }
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    id: null,
    apiName: '',
    type: '',
    content: '',
    status: 'pending',
    result: '',
    submitter: '',
    submitTime: null
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

const handleProcess = (row) => {
  dialogType.value = 'process'
  dialogVisible.value = true
  Object.assign(form, row)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该反馈记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await del(`/api/feedback/${row.id}`)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
        console.error('删除反馈记录失败：', error)
      }
    })
    .catch(() => {})
}

const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await post('/api/feedback', form)
        } else if (dialogType.value === 'edit') {
          await put('/api/feedback', form)
        } else if (dialogType.value === 'process') {
          await put('/api/feedback/process', form)
        }
        
        ElMessage.success(dialogType.value === 'add' ? '新增成功' : dialogType.value === 'edit' ? '编辑成功' : '处理成功')
        dialogVisible.value = false
        resetForm()
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
        console.error('保存反馈记录失败：', error)
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.api-feedback {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
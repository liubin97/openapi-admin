<template>
  <div class="api-maintenance">
    <div class="header">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="API名称">
              <el-input v-model="searchForm.apiName" placeholder="请输入API名称" clearable />
            </el-form-item>
            <el-form-item label="维护类型">
              <el-select v-model="searchForm.type" placeholder="请选择维护类型" clearable>
                <el-option label="更新" value="update" />
                <el-option label="修复" value="fix" />
                <el-option label="优化" value="optimize" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="8" style="text-align: right">
          <el-button type="primary" @click="handleAdd">新增记录</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="apiName" label="API名称" width="180" />
      <el-table-column prop="type" label="维护类型" width="100">
        <template #default="{ row }">
          <el-tag
            :type="row.type === 'fix' ? 'danger' : row.type === 'update' ? 'primary' : 'success'"
          >
            {{ row.type === 'fix' ? '修复' : row.type === 'update' ? '更新' : '优化' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="维护说明" />
      <el-table-column prop="maintainer" label="维护人" width="120" />
      <el-table-column prop="maintainTime" label="维护时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
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
      :title="dialogType === 'add' ? '新增维护记录' : dialogType === 'edit' ? '编辑维护记录' : '查看维护记录'"
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
        <el-form-item label="维护类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择维护类型">
            <el-option label="更新" value="update" />
            <el-option label="修复" value="fix" />
            <el-option label="优化" value="optimize" />
          </el-select>
        </el-form-item>
        <el-form-item label="维护说明" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入维护说明"
          />
        </el-form-item>
        <el-form-item label="维护人" prop="maintainer">
          <el-input v-model="form.maintainer" placeholder="请输入维护人" />
        </el-form-item>
        <el-form-item label="维护时间" prop="maintainTime">
          <el-date-picker
            v-model="form.maintainTime"
            type="datetime"
            placeholder="请选择维护时间"
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
  type: ''
})

const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const form = reactive({
  id: null,
  apiName: '',
  type: '',
  description: '',
  maintainer: '',
  maintainTime: null
})

const rules = {
  apiName: [{ required: true, message: '请输入API名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择维护类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入维护说明', trigger: 'blur' }],
  maintainer: [{ required: true, message: '请输入维护人', trigger: 'blur' }],
  maintainTime: [{ required: true, message: '请选择维护时间', trigger: 'change' }]
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const resetSearch = () => {
  Object.assign(searchForm, {
    apiName: '',
    type: ''
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
    const data = await get('/api/maintenance/page', params)
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
    description: '',
    maintainer: '',
    maintainTime: null
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

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该维护记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await del(`/api/maintenance/${row.id}`)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
        console.error('删除维护记录失败：', error)
      }
    })
    .catch(() => {})
}

const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await post('/api/maintenance', form)
        } else {
          await put('/api/maintenance', form)
        }
        
        ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
        dialogVisible.value = false
        resetForm()
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
        console.error('保存维护记录失败：', error)
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
.api-maintenance {
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
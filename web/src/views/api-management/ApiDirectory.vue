<template>
  <div class="api-directory">
    <div class="header">
      <el-button type="primary" @click="handleAdd">新增目录</el-button>
    </div>
    <el-tree
      ref="treeRef"
      :data="treeData"
      :props="defaultProps"
      node-key="id"
      default-expand-all
    >
      <template #default="{ node, data }">
        <span class="custom-tree-node">
          <span>{{ node.label }}</span>
          <span class="actions">
            <el-button link type="primary" @click="handleEdit(data)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(data)">删除</el-button>
          </span>
        </span>
      </template>
    </el-tree>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增目录' : '编辑目录'"
      width="30%"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="目录名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入目录名称" />
        </el-form-item>
        <el-form-item label="上级目录" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="defaultProps"
            placeholder="请选择上级目录"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post, put, del } from '../../utils/api'

const treeRef = ref(null)
const treeData = ref([])
const defaultProps = {
  children: 'children',
  label: 'name'
}

const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  parentId: null,
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入目录名称', trigger: 'blur' }]
}

const fetchData = async () => {
  try {
    const data = await get('/api/directory/tree')
    treeData.value = data || []
  } catch (error) {
    ElMessage.error('获取目录树失败')
    console.error(error)
  }
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    id: null,
    name: '',
    parentId: null,
    sort: 0
  })
}

const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (data) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, {
    id: data.id,
    name: data.name,
    parentId: data.parentId,
    sort: data.sort
  })
}

const handleDelete = (data) => {
  ElMessageBox.confirm('确认删除该目录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await del(`/api/directory/${data.id}`)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
        console.error('删除目录失败：', error)
      }
    })
    .catch(() => {})
}

const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await post('/api/directory', form)
        } else {
          await put('/api/directory', form)
        }
        
        ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
        dialogVisible.value = false
        resetForm()
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
        console.error('保存目录失败：', error)
      }
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.api-directory {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.actions {
  margin-left: 20px;
}
</style>
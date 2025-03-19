<template>
  <el-dialog
    :title="dialogType === 'add' ? '新增API版本' : dialogType === 'edit' ? '编辑API版本' : '查看API版本'"
    v-model="dialogVisible"
    width="50%"
    :close-on-click-modal="false"
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
          :rows="3"
          placeholder="请输入接口说明"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-switch
          v-model="form.status"
          :active-value="1"
          :inactive-value="0"
          active-text="启用"
          inactive-text="禁用"
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
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { post, put } from '../utils/api'

const props = defineProps({
  dialogType: {
    type: String,
    required: true,
    validator: (value) => ['add', 'edit', 'view'].includes(value)
  },
  visible: {
    type: Boolean,
    required: true
  },
  rowData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = ref(props.visible)
const formRef = ref(null)

const form = ref({
  apiName: '',
  apiCode: '',
  apiDesc: '',
  status: 1
})

const rules = {
  apiName: [{ required: true, message: '请输入API名称', trigger: 'blur' }],
  apiCode: [{ required: true, message: '请输入接口编号', trigger: 'blur' }],
  apiDesc: [{ required: true, message: '请输入接口说明', trigger: 'blur' }]
}

const initForm = () => {
  if (props.dialogType === 'add') {
    form.value = {
      apiName: '',
      apiCode: '',
      apiDesc: '',
      status: 1
    }
  } else {
    form.value = { ...props.rowData }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (props.dialogType === 'add') {
          await post('/api/versions', form.value)
        } else {
          await put(`/api/versions/${form.value.id}`, form.value)
        }
        
        ElMessage.success(props.dialogType === 'add' ? '新增成功' : '更新成功')
        emit('success')
        dialogVisible.value = false
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val
    if (val) {
      initForm()
    }
  }
)

watch(
  () => dialogVisible.value,
  (val) => {
    emit('update:visible', val)
  }
)
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
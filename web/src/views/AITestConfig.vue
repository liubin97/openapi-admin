<template>
  <div class="ai-test-config">
    <el-form :model="formData" label-width="120px">
      <el-form-item label="AI模型类型">
        <el-select v-model="formData.aiModelType" placeholder="请选择模型">
          <el-option label="GPT-4" value="GPT4" />
          <el-option label="文心一言" value="ERNIE" />
          <el-option label="通义千问" value="QWEN" />
        </el-select>
      </el-form-item>

      <el-form-item label="测试用例数量">
        <el-input-number v-model="formData.testCaseCount" :min="1" :max="100" />
      </el-form-item>

      <el-form-item label="测试间隔(分钟)">
        <el-input-number v-model="formData.testInterval" :min="30" :max="1440" />
      </el-form-item>

      <el-form-item label="定时任务">
        <el-input
          v-model="formData.cronExpression"
          placeholder="输入cron表达式 例如：0 0 2 * * ?"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSave">保存配置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  props: {
    apiData: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      formData: {
        aiModelType: 'GPT4',
        testCaseCount: 5,
        testInterval: 120,
        cronExpression: '0 0 2 * * ?'
      }
    }
  },
  methods: {
    handleSave() {
      this.$emit('save-config', {
        ...this.formData,
        apiId: this.apiData.id
      })
      this.$message.success('配置保存成功')
    }
  }
}
</script>

<style scoped>
.ai-test-config {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
</style>
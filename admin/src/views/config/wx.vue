<template>
  <div class="app-container">
    <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-width="300px">
      
          <el-form-item label="新品首发栏目商品显示数量" prop="mall_wx_index_new">
            <el-input v-model="dataForm.mall_wx_index_new"/>
          </el-form-item>
          <el-form-item label="人气推荐栏目商品显示数量" prop="mall_wx_index_hot">
            <el-input v-model="dataForm.mall_wx_index_hot"/>
          </el-form-item>
       
      <el-form-item>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="update">确定</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { listWx, updateWx } from "@/api/config";

export default {
  name: "ConfigWx",
  data() {
    return {
      dataForm: {}
    };
  },
  created() {
    this.init();
  },
  methods: {
    init: function() {
      listWx().then(response => {
        this.dataForm = response.data.data;
      });
    },
    cancel() {
      this.init();
    },
    update() {
      updateWx(this.dataForm)
        .then(response => {
          this.$notify.success({
            title: "成功",
            message: "配置成功"
          });
        })
        .catch(response => {
          this.$notify.error({
            title: "失败",
            message: response.data.errmsg
          });
        });
    }
  }
};
</script>

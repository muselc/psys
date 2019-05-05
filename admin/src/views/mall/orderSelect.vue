<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.expNo"
        clearable
        class="filter-item"
        style="width: 200px;"
        placeholder="请输入订单编号"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <!-- 查询结果 -->
    <div style="height: 300px;">
      <el-steps direction="vertical" :active="orderStatus">
        <el-step
          v-for="(trace,index) in traces"
          :description="trace.AcceptTime"
          :icon="trace.icon"
          :title="trace.AcceptStation"
          :key="index"
        ></el-step>
      </el-steps>
    </div>
  </div>
</template>

<script>
import { kd } from "@/api/order";
export default {
  data() {
    return {
      listQuery: {
        expNo: undefined
      },
      orderStatus: 1,
      traces: [],
      listLoading: true
    };
  },
  created() {
    if (this.$route.query.expNo) {
      this.listQuery.expNo = this.$route.query.expNo;
      this.getList();
    }
  },
  mounted() {
  },
  methods: {
    getList() {
      this.listLoading = true;
      kd(this.listQuery)
        .then(response => {
          this.traces = response.data.data.Traces;
          this.orderStatus = this.traces.length - 1;
          this.listLoading = false;
        })
        .catch(() => {
          this.traces = [];
          this.listLoading = true;
        });
    },
    handleFilter() {
      this.getList();
    }
  }
};
</script> 
    

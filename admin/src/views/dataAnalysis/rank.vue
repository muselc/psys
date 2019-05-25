<template>

  <div class="app-container">
    <div class="filter-container">
    <el-select v-model="listQuery.isSingle" placeholder="请选择类别">
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value">
    </el-option>
    </el-select>

    <el-select  v-model="listQuery.time" placeholder="请选择时间">
    <el-option
      v-for="items in optionss"
      :key="items.values"
      :label="items.labels"
      :value="items.values">
    </el-option>
     </el-select>
      <el-button
        v-permission="['POST /admin/goods/data']"
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleFilter"
      >查找</el-button>
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleDownload"
      >导出</el-button>
</div>
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%"
    >
      <el-table-column width="120px" align="center" label="rank">
        <template slot-scope="scope">
          <span>{{ scope.row.rank }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="name" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.newName }}</span>
        </template>
      </el-table-column>

      <el-table-column width="180px" align="center" label="num">
        <template slot-scope="scope">
          <span>{{ scope.row.total }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { goodsData } from "@/api/goods";

export default {
  name: "InlineEditTable",
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: "success",
        draft: "info",
        deleted: "danger"
      };
      return statusMap[status];
    }
  },
  data() {
    return {
      list: null,
      listLoading: true,
      listQuery: {
        isSingle: undefined,
        time: undefined
      },
       options: [{
          value: '3',
          label: '单品'
        }, {
          value: '2',
          label: '二级类目'
        }],
        optionss: [{
          values: '15',
          labels: '15天'
        }, {
          values: '30',
          labels: '30天'
        }],
        value: '',
        values: '',
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      goodsData(this.listQuery).then(response => {
        const items = response.data.data.list;
        this.list = response.data.data.list;
        this.list = items.map(v => {
          this.$set(v, "edit", false);
          v.originalTitle = v.title;
          return v;
        });
        this.listLoading = false;
      });
    },
    handleFilter() {
      this.getList();
    },
    handleDownload() {
      this.downloadLoading = true;
      import("@/vendor/Export2Excel").then(excel => {
        const tHeader = [
          "排名",
          "商品名",
          "数量",
        ];
        const filterVal = [
          "rank",
          "newName",
          "total"
        ];
        excel.export_json_to_excel2(tHeader, this.list, filterVal, "排行榜信息");
        this.downloadLoading = false;
      });
    }
  }
};
</script>

<style scoped>
.edit-input {
  padding-right: 100px;
}
.cancel-btn {
  position: absolute;
  right: 15px;
  top: 10px;
}
</style>

<template>
  <div class="dashboard-editor-container">

    <github-corner style="position: absolute; top: 0px; border: 0; right: 0;" />

    <panel-group  />

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData" />
    </el-row>

<el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <raddar-chart />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart />
        </div>
      </el-col>
    </el-row>

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
import GithubCorner from '@/components/GithubCorner'
import PanelGroup from './components/PanelGroup'
import LineChart from './components/LineChart'
import RaddarChart from './components/RaddarChart'
import PieChart from './components/PieChart'
import BarChart from './components/BarChart'
import TodoList from './components/TodoList'
import { info } from "@/api/dashboard";
import { goodsData } from "@/api/goods";

const lineChartData = {
  newVisitis: {
    expectedData: [100, 0, 161, 134, 105, 160, 165],
    actualData: [120, 82, 91, 154, 162, 140, 145]
  }
}

export default {
  name: 'DashboardAdmin',
  components: {
    GithubCorner,
    PanelGroup,
    LineChart
  },
  data() {
    return {
      listLoading: false,
      lineChartData: [],
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
    }
  },
  created(){
   this.initChart();
  },
  methods: {
    initChart(){
    info().then(response => {
      this.lineChartData = response.data.data.lineChartData.visiter
    });
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
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}
</style>

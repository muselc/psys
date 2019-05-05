<template>
  <div :id="id" :class="className" :style="{height:height,width:width}"/>
</template>

<script>
import { getAgeAndSexData } from "@/api/user";
import echarts from "echarts";
import resize from "./mixins/resize";
import bmap from "bmap";

require("echarts/extension/bmap/bmap");
export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: "chart"
    },
    id: {
      type: String,
      default: "chart"
    },
    width: {
      type: String,
      default: "200px"
    },
    height: {
      type: String,
      default: "200px"
    }
  },
  data() {
    return {
      chart: null
    };
  },
  mounted() {
    this.initChart();
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    initChart() {
      var sexData = [];
      var ageData = [];
      getAgeAndSexData()
        .then(response => {
          sexData = response.data.data.sex;
          ageData = response.data.data.age;
          this.chart = echarts.init(document.getElementById(this.id));
          var option = {
            title: {
              text: '用户年龄分布',
              x: 'center'
            },
            tooltip: {
              trigger: 'item',
              formatter: '{a} <br/>{b} : {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 'left'
            },
            series: [
              {
                name: '年龄',
                type: 'pie',
                radius: '55%',
                center: ['30%', '60%'],
                data: ageData,
                itemStyle: {
                  emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                }
              },
              {
                name: "性别",
                type: "pie",
                radius: "55%",
                center: ["70%", "60%"],
                data: sexData,
                itemStyle: {
                  emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: "rgba(0, 0, 0, 0.5)"
                  }
                }
              }
            ]
          };
          this.chart.setOption(option);
        })
        .catch(() => {});
    }
  }
};
</script>
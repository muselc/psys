<template>
  <div :id="id" :class="className" :style="{height:height,width:width}"/>
</template>
      
<script>
import { getAreaData } from "@/api/area";
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
      dataa: [],
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
      var data = [];
      getAreaData()
        .then(response => {
          data = response.data.data.area;
          this.chart = echarts.init(document.getElementById(this.id));
          var geoCoordMap = {
            北京市: [116.39, 39.92],
            上海: [121.48, 31.24],
            天津市: [117.21, 39.14],
            舟山: [122.207216, 29.985295],
            齐齐哈尔: [123.97, 47.33],
            盐城: [120.13, 33.38],
            赤峰: [118.87, 42.28],
            青岛: [120.33, 36.07],
            乳山: [121.52, 36.89],
            金昌: [102.188043, 38.520089],
            泉州: [118.58, 24.93]
          };
          var convertData = function(data) {
            var res = [];
            for (var i = 0; i < data.length; i++) {
              var geoCoord = geoCoordMap[data[i].name];
              if (geoCoord) {
                res.push({
                  name: data[i].name,
                  value: geoCoord.concat(data[i].value)
                });
              }
            }
            return res;
          };
          var option = {
            title: {
              text: "全国用户分布",
              left: "center"
            },
            tooltip: {
              trigger: "item"
            },
            bmap: {
              center: [104.114129, 37.550339],
              zoom: 5,
              roam: true,
              mapStyle: {
                styleJson: [
                  {
                    featureType: "water",
                    elementType: "all",
                    stylers: {
                      color: "#d1d1d1"
                    }
                  },
                  {
                    featureType: "land",
                    elementType: "all",
                    stylers: {
                      color: "#f3f3f3"
                    }
                  },
                  {
                    featureType: "railway",
                    elementType: "all",
                    stylers: {
                      visibility: "off"
                    }
                  },
                  {
                    featureType: "highway",
                    elementType: "all",
                    stylers: {
                      color: "#fdfdfd"
                    }
                  },
                  {
                    featureType: "highway",
                    elementType: "labels",
                    stylers: {
                      visibility: "off"
                    }
                  },
                  {
                    featureType: "arterial",
                    elementType: "geometry",
                    stylers: {
                      color: "#fefefe"
                    }
                  },
                  {
                    featureType: "arterial",
                    elementType: "geometry.fill",
                    stylers: {
                      color: "#fefefe"
                    }
                  },
                  {
                    featureType: "poi",
                    elementType: "all",
                    stylers: {
                      visibility: "off"
                    }
                  },
                  {
                    featureType: "green",
                    elementType: "all",
                    stylers: {
                      visibility: "off"
                    }
                  },
                  {
                    featureType: "subway",
                    elementType: "all",
                    stylers: {
                      visibility: "off"
                    }
                  },
                  {
                    featureType: "manmade",
                    elementType: "all",
                    stylers: {
                      color: "#d1d1d1"
                    }
                  },
                  {
                    featureType: "local",
                    elementType: "all",
                    stylers: {
                      color: "#d1d1d1"
                    }
                  },
                  {
                    featureType: "arterial",
                    elementType: "labels",
                    stylers: {
                      visibility: "off"
                    }
                  },
                  {
                    featureType: "boundary",
                    elementType: "all",
                    stylers: {
                      color: "#fefefe"
                    }
                  },
                  {
                    featureType: "building",
                    elementType: "all",
                    stylers: {
                      color: "#d1d1d1"
                    }
                  },
                  {
                    featureType: "label",
                    elementType: "labels.text.fill",
                    stylers: {
                      color: "#999999"
                    }
                  }
                ]
              }
            },
            series: [
              {
                name: "Top 5",
                type: "effectScatter",
                coordinateSystem: "bmap",
                data: convertData(
                  data
                    .sort(function(a, b) {
                      return parseInt(b.value) - parseInt(a.value);
                    })
                    .slice(0, 6)
                ),
                symbolSize: function(val) {
                  return val[2] / 100;
                },
                showEffectOn: "render",
                rippleEffect: {
                  brushType: "stroke"
                },
                hoverAnimation: true,
                label: {
                  normal: {
                    formatter: "{b}",
                    position: "right",
                    show: true
                  }
                },
                itemStyle: {
                  normal: {
                    color: "purple",
                    shadowBlur: 10,
                    shadowColor: "#333"
                  }
                },
                zlevel: 1
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
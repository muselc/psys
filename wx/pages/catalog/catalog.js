// pages/catalog/catalog.js
var util = require('../../utils/util.js');
var api = require('../../config/api.js');

Page({
  data: {
    categoryList: [],
    currentCategory: {},
    currentSubCategoryList: {},
    allList: {},
    scrollLeft: 0,
    scrollTop: 0,
    goodsCount: 0,
    scrollHeight: 0
  },
  onLoad: function (options) {
    this.getCatalog();
  },

  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.getCatalog();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  getCatalog: function () {
    //CatalogList
    let that = this;
    wx.showLoading({
      title: '加载中...',
    });
    util.request(api.PrimaryCategory).then(function (res) {
      that.setData({
        categoryList: res.data.primary,
        currentCategory: res.data.currentCategory,
        currentSubCategoryList: res.data.second
      });
      wx.hideLoading();
    });  
  },
  getCurrentCategory: function (id) {
    let that = this;
    util.request(api.SecondCurrentCategory, {
      id: id
    })
      .then(function (res) {
        that.setData({
          currentCategory: res.data.currentCategory,
          currentSubCategoryList: res.data.second
        });
      });
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  },

  switchCate: function (event) {
    if (this.data.currentCategory.id == event.currentTarget.dataset.id) {
      return false;
    }

    this.getCurrentCategory(event.currentTarget.dataset.id);
  }
})
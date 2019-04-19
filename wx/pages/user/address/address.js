// pages/user/address/address.js
var util = require("../../../utils/util.js");
var api = require("../../../config/api.js");
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    addressList:[],
    addressId: 0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.getAddress();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  
  getAddress(){
    let that = this;
    util.request(api.GetAddress,{
      userId: wx.getStorageSync('token').userId
    }).then(function(res) {
      if(res.errno === 0) {
        that.setData({
          addressList: res.data.addressList
        });
      }
    });
  },
  saveAddress() {
    if (app.globalData.hasLogin) {
      wx.navigateTo({
        url: '/pages/user/newaddress/newaddress'
      });
    } else {
      wx.navigateTo({
        url: '/pages/user/newaddress/newaddress'
      });
    }
  },
  delAddress: function(event) {
    let that = this;
    wx.showModal({
      title: '',
      content: '确定要删除该地址',
      success: function(res) {
        if(res.confirm) {
          let addressId = event.target.dataset.addressId;
          util.request(api.DeleteAddress,{
            id: addressId
          },'POST').then(function(res){
            if(res.errno === 0) {
              that.getAddress();
              wx.removeStorage({
                key: 'addressId',
                success: function(res) {},
              })
            }
          });
          console.log("用户点击确定");
        }
      }
    });
    return false;
  },
  detailAddress: function(event) {
    let addressId = event.currentTarget.dataset.addressId;
    wx.navigateTo({
      url: '/pages/user/newaddress/newaddress?id=' +addressId
    });
  },
  defaultAddress: function(event) {
    let that = this;
    util.request(api.SaveAddress,{
      id: event.currentTarget.dataset.addressId,
      isDefault: event.currentTarget.dataset.check
    },'POST').then(function (res) {
      if (res.errno === 0) {
        
      }
    });
  }

})
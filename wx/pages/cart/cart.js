var util = require('../../utils/util.js');
var api = require('../../config/api.js');
var user = require('../../utils/user.js');

var app = getApp();

Page({
  data: {
    cartGoods: [],
    cartInfo: {
      "cartCount": 0,
      "checkedCartCount": 0,
      "priceCount": 0.00
    },
    isEditCart: false,
    checkedAllStatus: true,
    editCartList: [],
    hasLogin: false
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
    if (app.globalData.hasLogin) {
      this.getCartList();
    }

    this.setData({
      hasLogin: app.globalData.hasLogin
    });

  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  },
  goLogin() {
    wx.navigateTo({
      url: "/pages/auth/login/login"
    });
  },

  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.getCartList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  getCartList: function () {
    let that = this;
    util.request(api.CartList + "?userId=" + wx.getStorageSync('token').userId).then(function (res) {
      if (res.errno === 0) {
        that.setData({
          cartGoods: res.data.cartList,
          cartInfo: res.data.cartInfo
        });

        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      }
    });
  },
  isCheckedAll: function () {
    //判断购物车商品已全选
    return this.data.cartGoods.every(function (element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });
  },
  doCheckedAll: function () {
    let checkedAll = this.isCheckedAll()
    this.setData({
      checkedAllStatus: this.isCheckedAll()
    });
  },
  checkedItem: function (event) {
    let itemIndex = event.target.dataset.itemIndex;
    let that = this;

    // let cartIds = [];
    // cartIds.push(that.data.cartGoods[itemIndex].id);
    let cartId = that.data.cartGoods[itemIndex].id;
  
    if (!this.data.isEditCart) {
      util.request(api.CartChecked + "?userId=" + wx.getStorageSync('token').userId, {
        cartId: cartId,
        isChecked: that.data.cartGoods[itemIndex].checked ? 0 : 1
      }, 'POST').then(function (res) {
        if (res.errno === 0) {
          that.setData({
            cartGoods: res.data.cartList,
            cartInfo: res.data.cartInfo
          });
        }

        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      });
    } else {
      //编辑状态
      let tmpCartData = this.data.cartGoods.map(function (element, index, array) {
        if (index == itemIndex) {
          element.checked = !element.checked;
        }

        return element;
      });

      that.setData({
        cartGoods: tmpCartData,
        checkedAllStatus: that.isCheckedAll(),
        'cartTotal.checkedGoodsCount': that.getCheckedGoodsCount()
      });
    }
  },
  getCheckedGoodsCount: function () {
    let checkedGoodsCount = 0;
    this.data.cartGoods.forEach(function (v) {
      if (v.checked === true) {
        checkedGoodsCount += v.number;
      }
    });
    return checkedGoodsCount;
  },
  checkedAll: function () {
    let that = this;

    if (!this.data.isEditCart) {
      var productIds = this.data.cartGoods.map(function (v) {
        return v.productId;
      });
      util.request(api.CartChecked, {
        productIds: productIds,
        isChecked: that.isCheckedAll() ? 0 : 1
      }, 'POST').then(function (res) {
        if (res.errno === 0) {
          console.log(res.data);
          that.setData({
            cartGoods: res.data.cartList,
            cartTotal: res.data.cartTotal
          });
        }

        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      });
    } else {
      //编辑状态
      let checkedAllStatus = that.isCheckedAll();
      let tmpCartData = this.data.cartGoods.map(function (v) {
        v.checked = !checkedAllStatus;
        return v;
      });

      that.setData({
        cartGoods: tmpCartData,
        checkedAllStatus: that.isCheckedAll(),
        'cartTotal.checkedGoodsCount': that.getCheckedGoodsCount()
      });
    }

  },
  editCart: function () {
    var that = this;
    if (this.data.isEditCart) {
      this.getCartList();
      this.setData({
        isEditCart: !this.data.isEditCart
      });
    } else {
      //编辑状态
      let tmpCartList = this.data.cartGoods.map(function (v) {
        v.checked = false;
        return v;
      });
      this.setData({
        editCartList: this.data.cartGoods,
        cartGoods: tmpCartList,
        isEditCart: !this.data.isEditCart,
        checkedAllStatus: that.isCheckedAll(),
        'cartInfo.checkedCart': that.getCheckedGoodsCount()
      });
    }

  },
  updateCart: function (productId, goodsSn, number, id) {
    let that = this;

    util.request(api.UpdateCart + "?userId=" + wx.getStorageSync('token').userId, {
      productId: productId,
      goodsSn: goodsSn,
      number: number,
      id: id
    }, 'POST').then(function (res) {
      that.setData({
        checkedAllStatus: that.isCheckedAll()
      });
    });

  },
  cutNumber: function (event) {

    let itemIndex = event.target.dataset.itemIndex;
    let cartItem = this.data.cartGoods[itemIndex];
    let number = (cartItem.number - 1 > 1) ? cartItem.number - 1 : 1;
    cartItem.number = number;
    this.setData({
      cartGoods: this.data.cartGoods
    });
    this.updateCart(cartItem.productId, cartItem.goodsSn, number, cartItem.id);
  },
  addNumber: function (event) {
    let itemIndex = event.target.dataset.itemIndex;
    let cartItem = this.data.cartGoods[itemIndex];
    let number = cartItem.number + 1;
    cartItem.number = number;
    this.setData({
      cartGoods: this.data.cartGoods
    });
    this.updateCart(cartItem.productId, cartItem.goodsSn, number, cartItem.id);

  },
  checkoutOrder: function () {
    //获取已选择的商品
    let that = this;

    var checkedGoods = this.data.cartGoods.filter(function (element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });

    if (checkedGoods.length <= 0) {
      return false;
    }

    // storage中设置了cartId，则是购物车购买
    try {
      wx.navigateTo({
        url: '../pay/pay'
      })
    } catch (e) { }

  },
  deleteCart: function () {
    //获取已选择的商品
    let that = this;

    let cartIds = this.data.cartGoods.filter(function (element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });

    if (cartIds.length <= 0) {
      return false;
    }

    cartIds = cartIds.map(function (element, index, array) {
      if (element.checked == true) {
        return element.id;
      }
    });


    util.request(api.CartDelete
      +"?userId="+wx.getStorageSync('token').userId, {
      cartIds: cartIds
    }, 'POST').then(function (res) {
      if (res.errno === 0) {
        let cartList = res.data.cartList.map(v => {
          v.checked = false;
          return v;
        });

        that.setData({
          cartGoods: cartList,
          cartInfo: res.data.cartInfo
        });
      }

      that.setData({
        checkedAllStatus: that.isCheckedAll()
      });
    });
  }
})
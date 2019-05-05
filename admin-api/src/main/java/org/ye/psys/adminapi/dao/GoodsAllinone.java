package org.ye.psys.adminapi.dao;

import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.GoodsSpecification;
import org.ye.psys.db.entity.GoodsStock;

public class GoodsAllinone {
    Goods goods;
    GoodsSpecification[] specifications;
    // 这里采用 Product 再转换到 GoodsProduct
    GoodsStock[] products;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsSpecification[] getSpecifications() {
        return specifications;
    }

    public void setSpecifications(GoodsSpecification[] specifications) {
        this.specifications = specifications;
    }

    public GoodsStock[] getProducts() {
        return products;
    }

    public void setProducts(GoodsStock[] products) {
        this.products = products;
    }
}

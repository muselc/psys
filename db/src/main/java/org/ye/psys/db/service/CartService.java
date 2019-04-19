package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.Cart;
import org.ye.psys.db.entity.CartExample;
import org.ye.psys.db.mapper.CartMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-04-03 08:55
 */
@Service
public class CartService {
    @Autowired
    private CartMapper cartMapper;

    public Cart isExit(Integer stockId, Integer userId, String goodsn) {
        CartExample example = new CartExample();
        example.or().andProductIdEqualTo(stockId).andUserIdEqualTo(userId).andGoodsSnEqualTo(goodsn).andDeletedEqualTo(false);
        return cartMapper.selectOneByExample(example);
    }

    public void add(Cart cart) {
        cart.setAddTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.insertSelective(cart);
    }

    public int updateById(Cart exitCart) {
        exitCart.setUpdateTime(LocalDateTime.now());
        return cartMapper.updateByPrimaryKey(exitCart);
    }

    public List<Cart> queryByUid(Integer userId) {
        CartExample example = new CartExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return cartMapper.selectByExample(example);
    }

    public int updateCheck(Integer cartId, Boolean isChecked) {
        CartExample example = new CartExample();
        example.or().andIdEqualTo(cartId).andDeletedEqualTo(false);
        Cart cart = new Cart();
        cart.setChecked(isChecked);
        cart.setUpdateTime(LocalDateTime.now());
        return cartMapper.updateByExampleSelective(cart, example);
    }

    public int delete(List<Integer> cartIds, Integer userId) {
        CartExample example = new CartExample();
        example.or().andUserIdEqualTo(userId).andIdIn(cartIds);
        return cartMapper.logicalDeleteByExample(example);
    }

    public List<Cart> queryByUidAndChecked(Integer userId) {
        CartExample example = new CartExample();
        example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true).andDeletedEqualTo(false);
        return cartMapper.selectByExample(example);
    }

    public Cart findById(Integer cartId) {
        return cartMapper.selectByPrimaryKey(cartId);
    }

    public void deleteById(Integer id) {
        cartMapper.deleteByPrimaryKey(id);
        return;
    }
}

package org.ye.psys.core.system;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-05-04 13:19
 */

public class SystemConfig {
    // 小程序相关配置
    public final static String MALL_WX_INDEX_NEW = "mall_wx_index_new";
    public final static String MALL_WX_INDEX_HOT = "mall_wx_index_hot";
    // 运费相关配置
    public final static String MALL_EXPRESS_FREIGHT_VALUE = "mall_express_freight_value";
    public final static String MALL_EXPRESS_FREIGHT_MIN = "mall_express_freight_min";
    // 订单相关配置
    public final static String MALL_ORDER_UNPAID = "mall_order_unpaid";
    public final static String MALL_ORDER_UNCONFIRM = "mall_order_unconfirm";
    public final static String MALL_ORDER_COMMENT = "mall_order_comment";


    //所有的配置均保存在该 HashMap 中
    private static Map<String, String> SYSTEM_CONFIGS = new HashMap<>();

    private static String getConfig(String keyName) {
        return SYSTEM_CONFIGS.get(keyName);
    }

    private static Integer getConfigInt(String keyName) {
        return Integer.parseInt(SYSTEM_CONFIGS.get(keyName));
    }

    private static Boolean getConfigBoolean(String keyName) {
        return Boolean.valueOf(SYSTEM_CONFIGS.get(keyName));
    }

    private static BigDecimal getConfigBigDec(String keyName) {
        return new BigDecimal(SYSTEM_CONFIGS.get(keyName));
    }

    public static Integer getNewLimit() {
        return getConfigInt(MALL_WX_INDEX_NEW);
    }

    public static Integer getHotLimit() {
        return getConfigInt(MALL_WX_INDEX_HOT);
    }

    public static BigDecimal getFreight() {
        return getConfigBigDec(MALL_EXPRESS_FREIGHT_VALUE);
    }

    public static BigDecimal getFreightLimit() {
        return getConfigBigDec(MALL_EXPRESS_FREIGHT_MIN);
    }

    public static Integer getOrderUnpaid() {
        return getConfigInt(MALL_ORDER_UNPAID);
    }

    public static Integer getOrderUnconfirm() {
        return getConfigInt(MALL_ORDER_UNCONFIRM);
    }

    public static Integer getOrderComment() {
        return getConfigInt(MALL_ORDER_COMMENT);
    }

    public static void setConfigs(Map<String, String> configs) {
        SYSTEM_CONFIGS = configs;
    }

    public static void updateConfigs(Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            SYSTEM_CONFIGS.put(entry.getKey(), entry.getValue());
        }
    }
}


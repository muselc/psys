package org.ye.psys.core.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.ye.psys.db.service.SystemService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-05-04 13:38
 * <p>
 * 系统启动服务，用于设置系统配置信息、检查系统状态及打印系统信息
 */
@Component
class SystemInitService {
    private SystemInitService systemInitService;


    @Autowired
    private Environment environment;

    @PostConstruct
    private void init() {
        systemInitService = this;
        initConfigs();
        SystemInfoPrinter.printInfo("mall 初始化信息", getSystemInfo());
    }


    private final static Map<String, String> DEFAULT_CONFIGS = new HashMap<>();

    static {
        // 小程序相关配置默认值
        DEFAULT_CONFIGS.put(SystemConfig.MALL_WX_INDEX_NEW, "6");
        DEFAULT_CONFIGS.put(SystemConfig.MALL_WX_INDEX_HOT, "6");
        // 运费相关配置默认值
        DEFAULT_CONFIGS.put(SystemConfig.MALL_EXPRESS_FREIGHT_VALUE, "8");
        DEFAULT_CONFIGS.put(SystemConfig.MALL_EXPRESS_FREIGHT_MIN, "88");
        // 订单相关配置默认值
        DEFAULT_CONFIGS.put(SystemConfig.MALL_ORDER_UNPAID, "30");
        DEFAULT_CONFIGS.put(SystemConfig.MALL_ORDER_UNCONFIRM, "7");
        DEFAULT_CONFIGS.put(SystemConfig.MALL_ORDER_COMMENT, "7");

    }

    @Autowired
    private SystemService systemService;

    private void initConfigs() {
        // 1. 读取数据库全部配置信息
        Map<String, String> configs = systemService.queryAll();

        // 2. 分析DEFAULT_CONFIGS
        for (Map.Entry<String, String> entry : DEFAULT_CONFIGS.entrySet()) {
            if (configs.containsKey(entry.getKey())) {
                continue;
            }

            configs.put(entry.getKey(), entry.getValue());
            systemService.addConfig(entry.getKey(), entry.getValue());
        }

        SystemConfig.setConfigs(configs);
    }

    private Map<String, String> getSystemInfo() {

        Map<String, String> infos = new LinkedHashMap<>();

        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 0, "系统信息");
        // 测试获取application-db.yml配置信息
//        infos.put("服务器端口", environment.getProperty("server.port"));
//        infos.put("数据库USER", environment.getProperty("spring.datasource.druid.username"));
//        infos.put("数据库地址", environment.getProperty("spring.datasource.druid.url"));
//        infos.put("调试级别", environment.getProperty("logging.level.org.psys.mall.wx"));

        // 测试获取application-core.yml配置信息
//        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 1, "模块状态");
//        infos.put("邮件", environment.getProperty("mall.notify.mail.enable"));
//        infos.put("短信", environment.getProperty("mall.notify.sms.enable"));
//        infos.put("模版消息", environment.getProperty("mall.notify.wx.enable"));
//        infos.put("快递信息", environment.getProperty("mall.express.enable"));
//        infos.put("快递鸟ID", environment.getProperty("mall.express.appId"));
//        infos.put("对象存储", environment.getProperty("mall.storage.active"));
//        infos.put("本地对象存储路径", environment.getProperty("mall.storage.local.storagePath"));
//        infos.put("本地对象访问地址", environment.getProperty("mall.storage.local.address"));
//        infos.put("本地对象访问端口", environment.getProperty("mall.storage.local.port"));

        // 微信相关信息
        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 2, "微信相关");
        infos.put("微信APP KEY", environment.getProperty("psys.wx.appId"));
        infos.put("微信APP-SECRET", environment.getProperty("psys.wx.appSecret"));

        //测试获取System表配置信息
        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 3, "系统设置");
        infos.put("首页显示记录数：NEW,HOT,BRAND,TOPIC,CatlogList,CatlogMore", SystemConfig.getNewLimit() + "," + SystemConfig.getHotLimit());

        return infos;
    }
}


package org.ye.psys.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Author liansongye
 * @create 2019-04-28 16:55
 */
@Component
@ConfigurationProperties(prefix = "psys.express")
@PropertySource(value = "application-core.properties")
public class Kd {
    //电商ID
    public String EBusinessID;
    //电商加密私钥，快递鸟提供，注意保管，不要泄漏
    public  String AppKey;

    public String getEBusinessID() {
        return EBusinessID;
    }

    public void setEBusinessID(String EBusinessID) {
        this.EBusinessID = EBusinessID;
    }

    public String getAppKey() {
        return AppKey;
    }

    public void setAppKey(String appKey) {
        AppKey = appKey;
    }
}

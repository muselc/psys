package org.ye.psys.adminapi.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ye.psys.core.util.UserTokenManager;
import org.ye.psys.db.entity.Area;
import org.ye.psys.db.entity.UserChart;
import org.ye.psys.db.service.AddressService;
import org.ye.psys.db.service.AreaService;
import org.ye.psys.db.service.UserChartService;
import org.ye.psys.db.service.UserService;

import javax.validation.constraints.Max;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-05-14 13:36
 */
@Component
public class AutoUpdateData {

    @Autowired
    private UserChartService userChartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private AreaService areaService;

    private static String[] Day_EN = {"day","Sun","Mon","Tues","Wed","Thur","Fri","Sat"};
    private static int[] Day = {0,7,1,2,3,4,5,6};

    //每日凌晨3点
//    @Scheduled(cron = "0 0 3 * * ?  ")
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void userChart(){
        int onLine = UserTokenManager.count();
        int total = userService.count();
        UserChart userChart = new UserChart();
        int nowDay = today();
        userChart.setId(Day[nowDay]);
        userChart.setDay(Day_EN[nowDay]);
        userChart.setOnline(onLine);
        userChart.setTotal(total);
        userChartService.update(userChart);
    }
    //每日凌晨3点
//    @Scheduled(cron = "0 0/1 * * * ?   ")
    public void defaultAddress(){
        List list = addressService.count();
        for (int i=0;i<list.size();i++){
            areaService.updateValueById(1,1);
        }
    }


    public int today(){
        Date today = new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(today);
        int weekday=c.get(Calendar.DAY_OF_WEEK);
        return weekday;
    }
}

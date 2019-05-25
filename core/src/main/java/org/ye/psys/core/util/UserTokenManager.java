package org.ye.psys.core.util;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UserTokenManager {

    public static Map<String, UserToken> tokenMap = new HashMap<>();
    public static Map<Integer, UserToken> idMap = new HashMap<>();
    //登录次数
    private static int oneDay;

    public static Integer getUserId(String token) {
        UserToken userToken = tokenMap.get(token);
        if (userToken == null) {
            return null;
        }

        if (userToken.getExpireTime().isBefore(LocalDateTime.now())) {
            tokenMap.remove(token);
            idMap.remove(userToken.getUserId());
            return null;
        }

        return userToken.getUserId();
    }


    public static UserToken generateToken(Integer id) {
        UserToken userToken = null;

        userToken = idMap.get(id);
        if(userToken != null) {
            tokenMap.remove(userToken.getToken());
            idMap.remove(id);
        }

        String token = CharUtil.getRandomString(32);
        if (tokenMap.containsKey(token)) {
            token = CharUtil.getRandomString(32);
        }

        LocalDateTime update = LocalDateTime.now();
        LocalDateTime expire = update.plusMinutes(15);

        userToken = new UserToken();
        userToken.setToken(token);
        userToken.setUpdateTime(update);
        userToken.setExpireTime(expire);
        userToken.setUserId(id);
        tokenMap.put(token, userToken);
        idMap.put(id, userToken);
        oneDay++;
        return userToken;
    }

    public static String getSessionKey(Integer userId) {
        UserToken userToken = idMap.get(userId);
        if (userToken == null) {
            return null;
        }

        if (userToken.getExpireTime().isBefore(LocalDateTime.now())) {
            tokenMap.remove(userToken.getToken());
            idMap.remove(userId);
            return null;
        }

        return userToken.getSessionKey();
    }

    public static void removeToken(Integer userId) {
        UserToken userToken = idMap.get(userId);
        String token = userToken.getToken();
        idMap.remove(userId);
        tokenMap.remove(token);
    }

    public static int online() {
        return idMap.size();
    }

    public static int oneDay() {
        return oneDay;
    }
    public static void oneDayReset(){
        oneDay = 0;
    }
}


package org.ye.psys.adminapi.shiro;

import com.alibaba.druid.util.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

public class AdminWebSessionManager extends DefaultWebSessionManager {

    public static final String LOGIN_TOKEN_KEY = "X-Litemall-Admin-Token";
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";


    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {

        String sessionId = WebUtils.toHttp(request).getHeader(LOGIN_TOKEN_KEY);

//        if (!StringUtils.isEmpty(sessionId)) {
//            if (redisTemplate.hasKey(sessionId + "")) {
//                return redisTemplate.opsForValue().get(sessionId);
//            }else {
//                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
//                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
//                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
//                //存sessioId
//                redisTemplate.opsForValue().set(sessionId, sessionId);
//                redisTemplate.expire(sessionId + "", 5, TimeUnit.MINUTES);
//                return sessionId;
//            }
//        } else {
//            return super.getSessionId(request, response);
//        }
         if (!StringUtils.isEmpty(sessionId)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sessionId;
         } else {
              return super.getSessionId(request, response);
         }
    }
}

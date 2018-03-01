package io.owslab.mailreceiver.intercept;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by khanhlvb on 1/23/18.
 */
public class NavMenuActiveInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url=request.getServletPath();
        request.setAttribute("currentMenu","dashboard");
        if(url.length()>2) {
            url=url.substring(1);
            String currentMenu = url;
            int index=url.indexOf("/");
            if(index>0) {
                currentMenu = url.substring(0, index);
                if(currentMenu.equals("admin")){
                    currentMenu = url.substring(index+1);
                    int index2=currentMenu.indexOf("/");
                    if(index2>0){
                        currentMenu = currentMenu.substring(0, index2);
                    }
                }
            }
            request.setAttribute("currentMenu",currentMenu);
        }
        return super.preHandle(request, response, handler);
    }
}
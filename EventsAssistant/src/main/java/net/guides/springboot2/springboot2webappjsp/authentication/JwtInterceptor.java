package net.guides.springboot2.springboot2webappjsp.authentication;

import net.guides.springboot2.springboot2webappjsp.controllers.Result;
import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    UserRepository userRepo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("get option,access");
            return true;
        }

        String token = request.getHeader("Authorization");
        System.out.println("Token, access " + token);

        /*
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

         */

        if (token != null){
            Result result = JwtUtil.getUserFaceIdByToken(request);

            if (result.getCode().equals("-1")) {
                System.out.println("Token is expired");
                return false;
            }

            try {
                //verify token
                boolean condition = JwtUtil.verify(token, result.getData().toString());
                if(condition){
                    System.out.println("Access permit!");
                    return true;
                } else {
                    System.out.println("Access denied!");
                    return false;
                }
            }catch (Exception e) {
                System.out.println("Wrong issue");
                response.sendError(500);
                e.printStackTrace();
                return false;
            }

        }

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if ( response.getStatus()==404) {
            System.out.println("404 not Found");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

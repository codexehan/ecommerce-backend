package codexe.han.order.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class MyAsyncHandlerInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
//		HandlerMethod handlerMethod = (HandlerMethod) handler;
        log.info("{}进入postHandle服务调用完成，返回结果给客户端",Thread.currentThread().getName());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if(null != ex){
            System.out.println("发生异常:"+ex.getMessage());
        }
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 拦截之后，重新写回数据，将原来的hello world换成如下字符串
        String resp = "my name is chhliu!";
        response.setContentLength(resp.length());
        response.getOutputStream().write(resp.getBytes());

        log.info("{} 进入afterConcurrentHandlingStarted方法", Thread.currentThread().getName());
    }

}

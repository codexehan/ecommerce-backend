package codexe.han.order.config;


import codexe.han.order.component.CustomInterceptor;
import codexe.han.order.component.MyAsyncHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by zhounan on 12/2/16.
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    @Lazy
    private CustomInterceptor customInterceptor;


    @Autowired
    @Lazy
    private MyAsyncHandlerInterceptor myAsyncHandlerInterceptor;




    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customInterceptor);
     //   registry.addInterceptor(myAsyncHandlerInterceptor);
    }
}

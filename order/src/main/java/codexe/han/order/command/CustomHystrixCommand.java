package codexe.han.order.command;

import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
public class CustomHystrixCommand extends HystrixCommand {
    private final String name;
    private final DeferredResult deferredResult;

    /**
     * 300qps
     * 同步
     * avg 5  throughput 333/s
     * 异步
     * queue size 100 core size10
     * avg 22  throughput 323.6/s
     *
     *
     * @param name
     * @param deferredResult
     */
    public CustomHystrixCommand(String name, DeferredResult deferredResult){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(name))
                .andCommandKey(HystrixCommandKey.Factory.asKey(name))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withMaxQueueSize(100)   //配置队列大小
                                .withCoreSize(10)    // 配置线程池里的线程数
                )
        );
 //       super(HystrixCommandGroupKey.Factory.asKey(name));
        this.name = name;
        this.deferredResult = deferredResult;
    }

    @Override
    protected Object run() throws Exception {
 //       log.info("{}进入hystrix command",Thread.currentThread().getName());
        int i=100000;
        while(i-->0){

        };
        deferredResult.setResult("Hello Hystrix Command");
 //       log.info("{}退出hystrix command",Thread.currentThread().getName());
        return "Hello Hystrix Command";
    }

    @Override
    protected String getFallback() {
        deferredResult.setResult("fallback: " + name);
        return "fallback: " + name;
    }
}

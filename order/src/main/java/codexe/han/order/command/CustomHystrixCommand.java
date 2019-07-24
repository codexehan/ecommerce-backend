package codexe.han.order.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
public class CustomHystrixCommand extends HystrixCommand {
    private final String name;
    private final DeferredResult deferredResult;
    public CustomHystrixCommand(String name, DeferredResult deferredResult){
        super(HystrixCommandGroupKey.Factory.asKey(name));
        this.name = name;
        this.deferredResult = deferredResult;
    }

    @Override
    protected Object run() throws Exception {
        log.info("{}进入hystrix command",Thread.currentThread().getName());
        Thread.sleep(950);
        log.info("{}退出hystrix command",Thread.currentThread().getName());
        deferredResult.setResult("Hello Hystrix Command");
        return "Hello Hystrix Command";
    }

    @Override
    protected String getFallback() {
        deferredResult.setResult("fallback: " + name);
        return "fallback: " + name;
    }
}

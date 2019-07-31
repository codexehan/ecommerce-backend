package codexe.han.order.controller;

import codexe.han.common.response.CodexeApiResponse;
import codexe.han.order.command.CustomHystrixCommand;
import codexe.han.order.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 1、circuitBreaker.enabled
 * 是否启用熔断器，默认是TURE。
 * 2、circuitBreaker.forceOpen
 * 熔断器强制打开，始终保持打开状态。默认值FLASE。
 * 3、circuitBreaker.forceClosed
 * 熔断器强制关闭，始终保持关闭状态。默认值FLASE。
 * 4、circuitBreaker.errorThresholdPercentage
 * 设定错误百分比，默认值50%，例如一段时间（10s）内有100个请求，其中有55个超时或者异常返回了，那么这段时间内的错误百分比是55%，大于了默认值50%，这种情况下触发熔断器-打开。
 * 5、circuitBreaker.requestVolumeThreshold
 * 默认值20.意思是至少有20个请求才进行errorThresholdPercentage错误百分比计算。比如一段时间（10s）内有19个请求全部失败了。错误百分比是100%，但熔断器不会打开，因为requestVolumeThreshold的值是20.
 * 6、circuitBreaker.sleepWindowInMilliseconds
 * 半开试探休眠时间，默认值5000ms。当熔断器开启一段时间之后比如5000ms，会尝试放过去一部分流量进行试探，确定依赖服务是否恢复。
 */
@RestController
@AllArgsConstructor
@Slf4j
public class TestController {
    private OrderService orderService;
    //同步执行

    /**
     http-nio-12065-exec-1 进入prehandler  容器线程
     hystrix-OrderController-1 test 进入
     hystrix-OrderController-1 test 退出
     Thread-42进入休眠线程
     http-nio-12065-exec-1进入postHandle服务调用完成，返回结果给客户端
     http-nio-12065-exec-1 进入postHandle
     http-nio-12065-exec-1 进入afterCompletion
     Thread-42退出休眠线程
     * @return
     */
    @HystrixCommand(fallbackMethod = "test2")
    @GetMapping("/test")
    public ResponseEntity test(){
        log.info("{} test 进入",Thread.currentThread().getName());
        Thread t = new Thread(()->{
            log.info("{}进入休眠线程",Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
                log.info("{}退出休眠线程",Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        log.info("{} test 退出",Thread.currentThread().getName());

        return CodexeApiResponse.builder().data("test end").build();
    }
    @GetMapping("/test2")
    public ResponseEntity test2() {
        log.info("test2 start");
        return CodexeApiResponse.builder().data("test2 end").build();
    }

    /**
     http-nio-12065-exec-3 进入prehandler
     hystrix-OrderController-2 test hystrix async 进入
     hystrix-OrderController-2 test hystrix async 退出
     http-nio-12065-exec-3 进入afterConcurrentHandlingStarted方法
     http-nio-12065-exec-3 进入afterConcurrentHandlingStarted
     MvcAsync1 进入callable线程
     http-nio-12065-exec-4 进入prehandler
     http-nio-12065-exec-4进入postHandle服务调用完成，返回结果给客户端
     http-nio-12065-exec-4 进入postHandle
     http-nio-12065-exec-4 进入afterCompletion
     * @return
     */
    //异步执行
    @HystrixCommand(fallbackMethod = "test2Async")
    @GetMapping("/test/async")
    public Callable<ResponseEntity> testHystrixAsync(){
        log.info("{} test hystrix async 进入", Thread.currentThread().getName());
        Callable call = new Callable() {
            @Override
            public Object call() throws Exception {
                log.info("{} 进入callable线程",Thread.currentThread().getName());
                return CodexeApiResponse.builder().data("test hystrix async end").build();
            }
        };

        log.info("{} test hystrix async 退出", Thread.currentThread().getName());

        return call;
    }

    @GetMapping("/test2/async")
    public Callable<ResponseEntity> test2Async(){
        log.info("{} test2 hystrix async 进入", Thread.currentThread().getName());
        Callable call = new Callable() {
            @Override
            public Object call() throws Exception {
                log.info("{} 进入callable线程",Thread.currentThread().getName());
                return CodexeApiResponse.builder().data("test2 hystrix2 async end").build();
            }
        };

        log.info("{} test2 hystrix async 退出", Thread.currentThread().getName());

        return call;
    }

    /**
     http-nio-12065-exec-1 进入prehandler
     http-nio-12065-exec-1进入test hystrix command
     http-nio-12065-exec-1退出test hystrix command
     http-nio-12065-exec-1 进入afterConcurrentHandlingStarted
     hystrix-testhc-1进入hystrix command
     hystrix-testhc-1退出hystrix command
     http-nio-12065-exec-2 进入prehandler
     http-nio-12065-exec-2 进入postHandle
     http-nio-12065-exec-2 进入afterCompletion
     Disconnected from the target VM, address: '127.0.0.1:57048', transport: 'socket'
     */
    @GetMapping("/test/hystrix_command")
    public DeferredResult<ResponseEntity> testHystrixCommand(){
        log.info("{}进入test hystrix command",Thread.currentThread().getName());
        DeferredResult deferredResult = new DeferredResult();
        deferredResult.onTimeout(()->{
            log.info("{} onTimeout",Thread.currentThread().getName());
            // 返回超时信息
            deferredResult.setErrorResult("time out!");
        });
        Future<String> hystrixCommandRes = new CustomHystrixCommand("testhc",deferredResult).queue();
        log.info("{}退出test hystrix command",Thread.currentThread().getName());
        return deferredResult;
    }



   /* @GetMapping("/test/hystrix_command_anotation")
    public DeferredResult<ResponseEntity> testHystrixCommandAnotation(){
        log.info("{}test hystrix command annotation", Thread.currentThread().getName());
        DeferredResult deferredResult  = new DeferredResult();
        //new CustomHystrixCommand("testhc",deferredResult).queue();
        this.orderService.testHystrixCommand(deferredResult);
        log.info("{}退出test hystrix command annotation",Thread.currentThread().getName());
        return deferredResult;
    }*/
   @HystrixCommand
    @GetMapping("/test/hystrix_command_anotation")
    public DeferredResult<ResponseEntity> testAsync(){
        log.info("{}test hystrix command annotation", Thread.currentThread().getName());
        DeferredResult deferredResult = new DeferredResult();
        new AsyncResult<ResponseEntity>() {
            @Override
            public ResponseEntity invoke() {
                log.info("{}进入test hystrix command annotation sub thread", Thread.currentThread().getName());
                deferredResult.setResult(CodexeApiResponse.builder().data("hystrix").build());
                log.info("{}退出test hystrix command annotation sub thread", Thread.currentThread().getName());
                return CodexeApiResponse.builder().data("hystrix").build();
            }
        };
        log.info("{}退出test hystrix command annotation",Thread.currentThread().getName());
        return deferredResult;
    }

  //  @HystrixCommand(fallbackMethod = "testDegrade1")
    @GetMapping("/test/degrade")
    public ResponseEntity testDegrade(){
        /*log.info("{}进入testDegrade",Thread.currentThread().getName());
        log.info("{}退出testDegrade",Thread.currentThread().getName());*/
        int i=100000;
        while(i-->0){

        }
        return CodexeApiResponse.builder().data("no degrade").build();
    }
    @GetMapping("/test/degrade2")
    public DeferredResult testDegrade2(){
        /*log.info("{}进入testDegrade",Thread.currentThread().getName());
        log.info("{}退出testDegrade",Thread.currentThread().getName());*/
        DeferredResult deferredResult = new DeferredResult();
        CustomHystrixCommand hystrixCommand = new CustomHystrixCommand("test",deferredResult);
        hystrixCommand.queue();
        return deferredResult;
    }

}

package codexe.han.order.controller;

import codexe.han.common.response.CodexeApiResponse;
import codexe.han.order.client.InventoryClient;
import codexe.han.order.command.CustomHystrixCommand;
import codexe.han.order.dto.OrderProductDTO;
import codexe.han.order.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.*;

@RestController
@AllArgsConstructor
@Slf4j
public class OrderController {

    private OrderService orderService;
    //proceed to checkout
    //need to login
    @PostMapping("/proceed/checkout")
    public ResponseEntity proceedCheckout(@RequestBody OrderProductDTO orderProductDTO){
        /**
         * 1.need to check login status
         * 2.need to check product validating time
         * 4.need to check whether can be delivered to the address
         * 3.need to check inventory
         *      enough: go to order page
         *      not enough: return error to remind
         */
        this.orderService.proceedCheckout(orderProductDTO);
        return CodexeApiResponse.builder().build();
    }

    @PostMapping("/continue/checkout")
    public ResponseEntity continueCheckout(@RequestBody OrderProductDTO orderProductDTO){
        /**
         * 1.check inventory
         * 2.pre block the inventory
         *
         * @Transactional
         *      update db
         *      invalidate the redis
         * return true -> create order
         * 先更新数据库，再invalidate缓存。不能保证缓存和数据库的强一致性
         * 先invalidate缓存，再更新数据库。需要将read缓存和update数据库异步串行执行，来保证数据库的强一致性
         *
         *  NOTE: during peak load ->
         *      mq - update db  异步更新db
         *      sub quantity in redis 扣减redis
         *      先发送db 再减redis 防止少卖情况的发生
         */
        this.orderService.continueCheckout(orderProductDTO);
        return CodexeApiResponse.builder().build();
    }

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

}
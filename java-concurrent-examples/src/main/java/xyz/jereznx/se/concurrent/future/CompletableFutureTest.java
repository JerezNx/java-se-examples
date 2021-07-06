package xyz.jereznx.se.concurrent.future;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author liqilin
 * @since 2021/6/20 15:58
 */
@SuppressWarnings("ALL")
@Slf4j
public class CompletableFutureTest {

    /**
     * runAsync 没有返回值
     * thenRun 不用上一步的结果，也不会产生结果
     * <p>
     * 一般方法会重载一个同名的，第二个参数是指定线程池。不指定的话用的是 ForkJoinPool 内置的共用的
     */
    @Test
    public void test() {
        final CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            log.info("start");
            sleep(1);
            log.info("finish");
        });
        cf.thenRun(() -> log.info("end"));
        sleep(2);
        CompletableFuture.runAsync(() -> log.info("custom thread"), Executors.newSingleThreadExecutor());
        sleep(1);
    }

    /**
     * supplyAsync 可以获取到返回值
     * CompletableFuture 可以重复消费
     * 一般方法有 xxx 和 xxxAsync
     * - xxx 指的是继续使用上一步的线程执行任务
     * - xxxAsync 指的是把任务丢进线程池中去执行
     */
    @Test
    public void test1() {
        final CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            log.info("start");
            sleep(1);
            log.info("finish");
            return 1;
        });
//        下面两个任务都用上面执行 supplyAsync 的线程，所以会隔1秒
        cf.thenAccept(res -> {
            sleep(1);
            log.info("res1:" + res);
        });
        cf.thenAccept(res -> {
            sleep(1);
            log.info("res2:" + res);
        });
        log.info("...");

        sleep(6);
    }

    /**
     * thenAccept 就是能拿到上一步的结果，但当前这步没有返回值
     */
    @Test
    public void test2() {
        final CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            log.info("start");
            sleep(1);
            log.info("finish");
            return 1;
        });
//        此时会另开1个线程执行，所以会同时打印
        cf.thenAcceptAsync(res -> {
            sleep(1);
            log.info("async res:" + res);
        });
        cf.thenAcceptAsync(res -> {
            sleep(1);
            log.info("async res:" + res);
        });
        sleep(4);
    }

    @Test
    public void test3() {
        final CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
            return 1;
        });
        final CompletableFuture<Integer> cf1 = cf.thenApply(res -> {
            log.info("start2");
            sleep(1);
            log.info("res2:{}", res);
            log.info("finish2");
            return 2;
        });
        cf1.thenAccept(res -> log.info("res3:{}", res));
        final CompletableFuture<Integer> cf2 = cf.thenApply(res -> {
            log.info("start3");
            sleep(1);
            log.info("res3:{}", res);
            log.info("finish3");
            return 2;
        });
        sleep(6);
    }

    @Test
    public void test4() {
        final CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
            return 1;
        });
        log.info("getNow:{}", cf.getNow(-1));
        sleep(2);
//        getNow 不会取消之前的任务
        log.info("getNow:{}", cf.getNow(-1));
    }

    @Test
    public void test5() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
            return 1;
        });
        final CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            log.info("start2");
            sleep(2);
            log.info("finish2");
            return 2;
        });
//        谁调用谁都一样
//        cf1 和 cf2 都执行完成后，再执行后续任务
        cf1.runAfterBoth(cf2, () -> log.info("runAfterBoth"));
//        cf1 和 cf2 只要有1个执行完成后，再执行后续任务
        cf1.runAfterEither(cf2, () -> log.info("runAfterEither"));
        sleep(4);
    }

    @Test
    public void test6() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
            return 1;
        });
        final CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            log.info("start2");
            sleep(2);
            log.info("finish2");
            return 2;
        });
//        cf1 和 cf2 随便1个执行完成后，再执行后续任务，能拿到先完成的返回值
        cf1.acceptEither(cf2, (res) -> log.info("acceptEither|res:{}", res));
        cf1.applyToEither(cf2, (res) -> {
            log.info("applyToEither|res:{}", res);
            return 3;
        });
//        cf1 和 cf2 都执行完成后，再执行后续任务，可同时拿到2个返回值
//        todo 感觉这边命令很不统一，runAfterBoth、acceptEither、thenAcceptBoth
//        todo 一会是after，一会是then，一会没有
        cf1.thenAcceptBoth(cf2, (res1, res2) -> {
            log.info("thenAcceptBoth|res1:{}", res1);
            log.info("thenAcceptBoth|res2:{}", res2);
        });
//        apply有both，作为替代是 thenCombine
        cf1.thenCombine(cf2, (res1, res2) -> {
            log.info("thenCombine|res1:{}", res1);
            log.info("thenCombine|res2:{}", res2);
            return 3;
        });

        sleep(4);
    }

    /**
     * whenComplete 可以同时拿结果或异常，但不能生成新的结果
     * 如果有异常，则流程都断了
     */
    @Test
    public void test7() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
            throw new RuntimeException("error");
//            return 1;
        });
        final CompletableFuture<Integer> cf2 = cf1.whenComplete((res, e) -> {
            log.info("res:{}", res);
            log.info("e:", e);
        });
//        如果cf1有异常，后续都不会执行
//        如果cf1 没有异常，则此处能拿到cf1的值
        cf2.thenAccept(res -> log.info("final res:{}", res));
        cf2.thenRun(() -> log.info("finish"));
        sleep(3);
    }

    /**
     * handle 可以同时拿结果或异常，且能生成新的结果
     * 就算有异常，也能继续往下走
     */
    @Test
    public void test8() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
            throw new RuntimeException("error");
//            return 1;
        });
        final CompletableFuture<Integer> cf2 = cf1.handle((res, e) -> {
//            如果上一步发生异常，则e就能获取到
            log.info("res:{}", res);
            log.info("e:", e);
            return 2;
        });
        cf1.thenAccept(res -> {
//            如果上一步操作发生异常，这步就不会执行了
            log.info("thenAccept:{}", res);
        });

        sleep(3);
    }

    @Test
    public void test9() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
//            throw new RuntimeException("error");
            return 1;
        });
//        cf1有异常就会执行cf2
        final CompletableFuture<Integer> cf2 = cf1.exceptionally(e -> {
            log.info("error:", e);
            return 2;
        });
//        如果cf1有异常，则这里res就是 cf2的值 2
//        如果cf1没异常，则这里的res就是 cf1的值 1
        cf2.thenAccept(res -> log.info("exceptionally: {}", res));

        sleep(2);
    }

    @Test
    public void test10() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
//            throw new RuntimeException("error");
            return 1;
        });
//        thenCompose 将上一步的结果作为参数
//        同时要求返回一个 CompletableFuture
//        有点Optional flatMap的意思
        final CompletableFuture<Integer> cf2 = cf1.thenCompose(res -> {
            return CompletableFuture.supplyAsync(() -> {
                log.info("thenCompose: {}", res);
                return 2;
            });
        });
    }

    @Test
    public void test11() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
            throw new RuntimeException("error");
//            return 1;
        });
        final CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            log.info("start2");
            sleep(2);
            log.info("finish2");
//            throw new RuntimeException("error");
            return 2;
        });
//        anyOf，多个中任意1个完成即执行。如果发生异常，会获取到包装的异常
        final CompletableFuture<Object> cfAny = CompletableFuture.anyOf(cf1, cf2);
        cfAny.whenComplete((res, e) -> {
            log.info("anyOf|res:{}", res);
            log.info("anyOf|e:", e);
        });
//        allOf，多个中所有都完成才执行。如果发生异常，会获取到包装的异常
        final CompletableFuture<Void> allOf = CompletableFuture.allOf(cf1, cf2);
        allOf.whenComplete((res, e) -> {
//            由于是void，拿不到所有结果
            log.info("allOf|res:{}", res);
//            但如果发生异常，是可以拿到的
            log.info("allOf|e:", e);
//            想拿结果的话，可以通过直接操作cf1、cf2
            try {
                log.info("allOf|get|value :{},{}", cf1.get(), cf2.get());
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
//            todo 这边调join不知道为何不会抛异常，是因为被 whenComplete 捕获了？
            log.info("allOf|join|value :{},{}", cf1.join(), cf2.join());
        });
        sleep(3);
    }

    @Test
    public void test12() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
            throw new RuntimeException("error");
//            return 1;
        });
//        get 和 join 都是阻塞当前线程，获取CompletableFuture 的结果
//        区别在于，get抛出的是受检异常(java.util.concurrent.ExecutionException)
//        而join是包装成非受检异常（java.util.concurrent.CompletionException）
//        简单说就是get需要try-catch，而join不需要
        try {
            System.out.println(cf1.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(cf1.join());
    }

    @Test
    public void test13() {
        final CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("start1");
            sleep(1);
            log.info("finish1");
//            throw new RuntimeException("error");
            return 1;
        });
        cf1.thenAccept(res -> log.info("{}", res));
//        当cf1还在执行的时候，强制给其一个结果并让其返回
        final boolean complete = cf1.complete(2);
        System.out.println(complete);
    }

    @Test
    public void test14() {
        CompletableFuture.completedFuture(1).join();
    }

    private static void sleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

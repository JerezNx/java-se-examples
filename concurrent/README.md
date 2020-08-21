[toc]
# concurrent
Java 多线程使用

## 一、多线程入门
### 1.java实现多线程的方式
#### 1.1 继承Thread类

```
public class MyThreadTest {

    public static void main(String[] args) {
        new MyThread().start();
        System.out.println("main");
    }
    static class MyThread extends Thread{
        @Override
        public void run() {
            System.out.println(this.getId()+" run");
        }
    }
}
```

#### 1.2 实现Runnable
```
public class MyRunnableTest  {

    public static void main(String[] args) {
        new Thread(new MyRunnable()).start();
        new Thread(()-> System.out.println("run by lambda")).start();
        System.out.println("main");
    }

    static class MyRunnable implements Runnable {
        public void run() {
            System.out.println("run");
        }
    }
}
```

#### 1.3 实现Callable

```
public class MyCallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final FutureTask<String> futureTask = new FutureTask<>(new MyCallable());
        new Thread(futureTask).start();
        System.out.println(futureTask.get());
        System.out.println("main");
    }

    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("run start");
            Thread.sleep(1000);
            System.out.println("run end");
            return "res";
        }
    }
}
```

#### 1.4 线程池

```
public class RunnableThreadPool {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        final Future<?> res = executorService.submit(new MyCallableTest.MyCallable());
//        shutdownNow 会立即停止线程池，不管是否有线程任务没执行完
        executorService.shutdownNow();
//        get会阻塞主线程
        System.out.println(res.get());
//        executorService.shutdownNow();
    }

}
```


```
public class CallableThreadPool {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<?> res = executorService.submit(new MyRunnableTest.MyRunnable());
//        如果还有线程在执行，shutdown会阻塞
        executorService.shutdown();
//        runnable么得返回值
        System.out.println(res.get());
    }

}
```


package xyz.jerez.se.jmx.main;

import com.sun.jdmk.comm.HtmlAdaptorServer;
import xyz.jerez.se.jmx.bean.Hello;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author liqilin
 * @since 2021/1/29 15:18
 */
public class HtmlAdapterAgent {

    public static void main(String[] args) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName helloName = new ObjectName("xyz.jerez.se.jmx.bean:name=hello");
        //create mbean and register mbean
        server.registerMBean(new Hello(), helloName);

//        代理，可通过页面访问：http://localhost:8082/
        ObjectName adapterName = new ObjectName("HelloAgent:name=htmladapter,port=8082");
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        server.registerMBean(adapter, adapterName);
        adapter.start();
    }

}

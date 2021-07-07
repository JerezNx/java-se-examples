package xyz.jereznx.se.vm.reference;

import java.util.Date;

/**
 * @author liqilin
 * @since 2021/7/7 19:29
 */
public class MyDate extends Date {

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("obj [Date: " + this.getTime() + "] is gc");
    }

    @Override
    public String toString() {
        return "Date: " + this.getTime();
    }

}

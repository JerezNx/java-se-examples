package xyz.jerez.net.common.httpclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;

/**
 * @author liqilin
 * @since 2020/11/24 13:33
 */
public class HttpClientUtil {

    public static void main(String[] args) {
        String res = sendGet("http://www.baidu.com");
//        String res = sendPost("http://www.baidu.com");
        System.out.println(res);
    }

    /**
     * 发送get请求
     *
     * @param urlStr 请求url,eg: http://www.baidu.com
     * @return 响应内容字符串
     */
    public static String sendGet(String urlStr) {
        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建GET请求方法实例对象
        GetMethod getMethod = new GetMethod(urlStr);
        // 设置post请求超时时间
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        getMethod.addRequestHeader("Content-Type", "application/json");
        String result = "";
        try {
            httpClient.executeMethod(getMethod);
//            result = getMethod.getResponseBodyAsString();
            result = new String(getMethod.getResponseBody(), "utf-8");
            getMethod.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送post请求
     *
     * @param urlStr 请求url,eg: http://www.baidu.com
     * @return 响应内容字符串
     */
    public static String sendPost(String urlStr) {
        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(urlStr);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
//        参见方法 org.apache.commons.httpclient.HttpMethodBase.getContentCharSet中 1094行
//        当返回的Header中没有指定charset时，就取这里设置的charset
        postMethod.getParams().setContentCharset("utf-8");
//        在header中设置编码只是设置的请求的编码
//        postMethod.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
        postMethod.addRequestHeader("Content-Type", "application/json");
        String result = null;
        try {
            httpClient.executeMethod(postMethod);
//            编码首先根据response header 获取
//            当返回的Header中没有指定charset时，取 params中设置的
            result = postMethod.getResponseBodyAsString();
//            result = new String(postMethod.getResponseBody(),"utf-8");
            postMethod.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

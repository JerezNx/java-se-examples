package xyz.jerez.net.http.connection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HttpURLConnection 是 URLConnection 的子类，提供更多的方法
 * @author liqilin
 * @since 2020/11/24 13:24
 */
public class HttpURLConnectionUtil {

    public static void main(String[] args) {
        String res = sendRequest("http://www.baidu.com","GET");
        System.out.println(res);
    }

    /**
     * 发送请求
     * @param urlStr 请求url,eg: http://www.baidu.com
     * @param requestType 请求方法类型，eg: GET,POST
     * @return 响应字符串
     */
    public static String sendRequest(String urlStr, String requestType) {
        HttpURLConnection con;
        BufferedReader buffer;
        StringBuffer resultBuffer;
        try {
            URL url = new URL(urlStr);
            //得到连接对象
            con = (HttpURLConnection) url.openConnection();
            //设置请求类型
            con.setRequestMethod(requestType);
            //设置请求需要返回的数据类型和字符集类型
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);
            //得到响应码
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //得到响应流
                InputStream inputStream = con.getInputStream();
                //将响应流转换成字符串
                resultBuffer = new StringBuffer();
                String line;
                buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = buffer.readLine()) != null) {
                    resultBuffer.append(line);
                }
                return resultBuffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}

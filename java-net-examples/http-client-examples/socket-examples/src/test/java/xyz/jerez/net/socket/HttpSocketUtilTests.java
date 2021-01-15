package xyz.jerez.net.socket;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import static xyz.jerez.net.socket.HttpSocketUtil.*;

/**
 * @author liqilin
 * @since 2020/11/24 17:41
 */
public class HttpSocketUtilTests {

    @Test
    public void testGet() throws IOException {
        String baiduUrlStr = "https://www.baidu.com";
        String getUrlStr = "http://localhost:8080/info";
        String res = sendGet(baiduUrlStr);
        System.out.println(res);
    }

    @Test
    public void testPostByForm() throws IOException {
        String postByFormUrlStr = "http://localhost:8080/postForm";
        String formContentType = "application/x-www-form-urlencoded";
        String formData = URLEncoder.encode("name", "utf-8") + "=" + URLEncoder.encode("lql", "utf-8") + "&" +
                URLEncoder.encode("num", "utf-8") + "=" + URLEncoder.encode("24", "utf-8");
        String res = sendPost(postByFormUrlStr, formData, formContentType);
        System.out.println(res);
    }

    @Test
    public void testPostByJson() throws IOException {
        String postByJsonUrlStr = "http://localhost:8080/postJson";
        String jsonContentType = "application/json";
        JSONObject jsonData = new JSONObject();
        jsonData.put("name", "lql");
        jsonData.put("num", 24);
        String res = sendPost(postByJsonUrlStr, jsonData.toJSONString(), jsonContentType);
        System.out.println(res);
    }

}

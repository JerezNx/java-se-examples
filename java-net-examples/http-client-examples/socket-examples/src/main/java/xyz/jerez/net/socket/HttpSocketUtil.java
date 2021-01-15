package xyz.jerez.net.socket;

import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author liqilin
 * @since 2020/11/24 13:57
 */
public class HttpSocketUtil {

    /**
     * 发送get 请求
     *
     * @param urlStr 请求路径
     * @return 返回数据文本
     * @throws IOException e
     */
    public static String sendGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpSocketClient client = new HttpSocketClient(url);
        return client.sendGet(url.getPath());
    }

    /**
     * 发送get 请求
     *
     * @param baseUrl 基础路径
     * @param path    请求路径
     * @return 返回数据文本
     * @throws IOException e
     */
    public static String sendGet(String baseUrl, String path) throws IOException {
        URL url = new URL(baseUrl);
        HttpSocketClient client = new HttpSocketClient(url);
        return client.sendGet(path);
    }

    /**
     * 发送post请求
     *
     * @param urlStr      请求路径
     * @param data        请求参数
     * @param contentType contentType
     * @return 返回数据
     * @throws IOException e
     */
    public static String sendPost(String urlStr, String data, String contentType) throws IOException {
        URL url = new URL(urlStr);
        HttpSocketClient client = new HttpSocketClient(url);
        return client.sendPost(url.getPath(), data, contentType);
    }

    static class HttpSocketClient {

        private static final String PROTOCOL_HTTP = "http";
        private static final String PROTOCOL_HTTPS = "https";

        private int port;
        private String host;
        private Socket socket;
        private BufferedWriter bufferedWriter;

        /**
         * c
         *
         * @param url url
         * @throws IOException
         */
        public HttpSocketClient(URL url) throws IOException {
            this(url.getProtocol(), url.getHost(), getPortOfUrl(url));
        }

        /**
         * c
         *
         * @param protocol 协议
         * @param host     主机
         * @param port     端口
         * @throws IOException e
         */
        public HttpSocketClient(String protocol, String host, int port) throws IOException {
            this.host = host;
            this.port = port;

            if (PROTOCOL_HTTP.equals(protocol)) {
                socket = new Socket(this.host, this.port);
            } else if (PROTOCOL_HTTPS.equals(protocol)) {
                socket = SSLSocketFactory.getDefault().createSocket(this.host, this.port);
            } else {
                throw new IllegalArgumentException("不支持的协议类型");
            }
        }

        /**
         * 从url获取port(不写时会是-1)
         *
         * @param url url,目前只写了http和https协议
         * @return 实际端口
         */
        public static int getPortOfUrl(URL url) {
            int defaultPort = ConditionUtil.getDefaultIfCondition(80, (port) -> PROTOCOL_HTTP.equals(url.getProtocol()), 443);
            int port = ConditionUtil.getDefaultIfCondition(url.getPort(), (p) -> -1 != p, defaultPort);
            return port;
        }

        /**
         * 发送get 请求
         *
         * @param path 请求路径
         * @return 返回数据文本
         * @throws IOException e
         */
        public String sendGet(String path) throws IOException {
//            path如果为空，转换为/
            path = ConditionUtil.getDefaultIfCondition(path, p -> !p.isEmpty(), "/");
            OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedWriter = new BufferedWriter(streamWriter);
            bufferedWriter.write("GET " + path + " HTTP/1.1\r\n");
            bufferedWriter.write("Host: " + this.host + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();

            return getResponseDataString();

           /* InputStream inputStream = socket.getInputStream();
            BufferedInputStream streamReader = new BufferedInputStream(inputStream);
            bufferedReader = new BufferedReader(new InputStreamReader(streamReader, "utf-8"));
            StringBuffer res = new StringBuffer();
            String line;
            int contentLength = 0;
            boolean startReadData = false;
            String contentLengthPrefix = "Content-Length: ";
            while ((line = bufferedReader.readLine()) != null) {
                res.append(line);
                if (line.startsWith(contentLengthPrefix)) {
                    contentLength = Integer.parseInt(line.substring(contentLengthPrefix.length()));
                    System.out.println("contentLength: " + contentLength);
                }

                System.out.println(line);
                if (!startReadData && "".equals(line)) {
                    System.out.println("====== 开始读数据 ======");
                    break;
                }
            }
            int len = 0;
            int readByteLength = 0;
            byte[] data = new byte[contentLength];
            while ((len = inputStream.read(data, readByteLength, Math.min(contentLength - readByteLength, 1024))) != -1) {
                readByteLength += len;
                System.out.println(new String(data, StandardCharsets.UTF_8));
                System.out.println(readByteLength);
                if (readByteLength >= contentLength) {
                    break;
                }
            }
            bufferedReader.close();
            bufferedWriter.close();
             socket.close();
            return new String(data, StandardCharsets.UTF_8);
            */
        }

        /**
         * 发送post请求
         *
         * @param path        请求路径
         * @param data        请求参数
         * @param contentType contentType
         * @return 返回数据
         * @throws IOException e
         */
        public String sendPost(String path, String data, final String contentType) throws IOException {
//            path如果为空，转换为/
            path = ConditionUtil.getDefaultIfCondition(path, p -> !p.isEmpty(), "/");
            OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            bufferedWriter = new BufferedWriter(streamWriter);
            bufferedWriter.write("POST " + path + " HTTP/1.1\r\n");
            bufferedWriter.write("Host: " + this.host + "\r\n");
            bufferedWriter.write("Content-Length: " + data.getBytes().length + "\r\n");
            bufferedWriter.write("Content-Type: " + contentType + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.write(data);

            bufferedWriter.write("\r\n");
            bufferedWriter.flush();

            return getResponseDataString();
        }

        /**
         * 解析获取返回数据文本
         *
         * @return data str
         * @throws IOException e
         */
        private String getResponseDataString() throws IOException {
            byte[] data = getResponseData();
//            todo 解析 response header中是否有charset
            return new String(data);
        }

        /**
         * 解析读取返回数据
         *
         * @return data byte
         * @throws IOException e
         */
        private byte[] getResponseData() throws IOException {
            InputStream inputStream = socket.getInputStream();
//            协议头
            StringBuilder header = new StringBuilder();
//            实际数据
            byte[] data = null;
            byte[] buf = new byte[1];
            int len = 0;
//            数据包的总长度
            int contentLength = 0;
//            已读的数据长度
            int hasReadCount = 0;
//            是否已开始读取数据
            boolean startReadData = false;
            while ((len = inputStream.read(buf)) != -1) {
//                还在读取协议头
                if (!startReadData) {
                    header.append((char) buf[0]);
//                    协议头两个换行即为结束
                    if (header.indexOf("\r\n\r\n") > 0) {
                        startReadData = true;
                        System.out.println(header);
                        String contentLengthPrefix = "Content-Length: ";
//                        解析读取 Content-Length
                        String headerText = header.substring(header.indexOf(contentLengthPrefix) + contentLengthPrefix.length());
                        contentLength = Integer.parseInt(headerText.substring(0, headerText.indexOf("\r\n")));
                        System.out.println("contentLength:" + contentLength);
//                        根据长度，初始化 data 数组
                        data = new byte[contentLength];
                    }
                } else {
//                    一个字节一个字节的读取，当然这里也可以在上面break，然后另开一个while，加个缓存数组批量读取
                    data[hasReadCount] = buf[0];
//                    计算已读的数据
                    hasReadCount += len;
//                    判断是否已经读完，即已读的数据包长度到达 contentLength
                    if (hasReadCount >= contentLength) {
                        break;
                    }
                }
            }

            inputStream.close();
            socket.close();
            return data;
        }
    }
}

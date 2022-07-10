package question5.bio;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author JellyfishMIX
 * @date 7/4/22 00:42
 */
@Slf4j
public class HttpClientUtil {
    /**
     * 发送 get 请求
     *
     * @param url url
     * @return 请求的返回结果
     */
    public static String doGet(String url) {
        // http client
        CloseableHttpClient httpClient = null;
        // 返回结果
        CloseableHttpResponse response = null;
        // 请求结果内容
        String result = null;
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建 httpGet 远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，无需
            // httpGet.setHeader("Authorization", "value");
            // 设置配置请求参数
            // 连接主机服务超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)
                    // 请求超时时间
                    .setConnectionRequestTimeout(35000)
                    // 数据读取超时时间
                    .setSocketTimeout(60000)
                    .build();
            // 为 httpGet 实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 请求结果为空则直接返回
            if (response == null) {
                log.info("[http get] 请求结果为空, url: {}", url);
                return null;
            }
            log.info("[http get] url: {}, response: {}", url, JSON.toJSONString(response));
            // 状态码
            int statusCode = response.getStatusLine().getStatusCode();
            // 打印 HTTP 下载的应答状态码、应答内容长度、内容类型、编码方式
            // 获取应答的所有头部属性
            log.info(String.format("[url=%s], 应答状态码=%d, 应答内容长度=%s, 内容类型=%s, 编码方式=%s",
                    url,
                    statusCode,
                    response.getFirstHeader("Content-Length"),
                    response.getFirstHeader("Content-Type"),
                    response.getFirstHeader("Content-Encoding")));
            // 获取返回数据
            HttpEntity entity = response.getEntity();
            // 将结果转换为字符串
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            log.error("[http get] url: {}, IO 异常:", url, e);
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("[http get] url: {}, response 关闭异常:", url, e);
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.error("[http get] url: {}, httpClient 关闭异常:", url, e);
                }
            }
        }
        return result;
    }
}

package question5.bio;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BIO 写法的 SocketServer
 *
 * @author JellyfishMIX
 * @date 7/1/22 21:55
 */
@Slf4j
public class SocketServer {
    /**
     * handlerPool
     */
    private static ThreadPoolExecutor handlerPool =
            new ThreadPoolExecutor(5, 10,
                    80, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000),
                    new CustomThreadFactory("socketServerPool"),
                    new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        socketServer.core();
    }

    private void core() {
        try {
            ServerSocket serverSocket = new ServerSocket(SocketConstant.PORT);
            log.info("[SocketServer 主方法] SocketServer 已启动");
            while (true) {
                // .accept() 方法会阻塞
                Socket client = serverSocket.accept();
                log.info("[SocketServer 主方法] 建立新连接");
                SocketServer.handlerPool.execute(() -> {
                    service(client);
                });
            }
        } catch (IOException e) {
            log.error("[SocketServer 主方法] IO 异常:", e);
        } catch (RejectedExecutionException e) {
            log.error("[SocketServer 主方法] 触发 handler 线程池拒绝策略", e);
        }
    }

    /**
     * 处理请求具体的 handler 方法
     *
     * @param client 与客户端的 socket 连接
     */
    private void service(Socket client) {
        // 判空校验
        if (client == null) {
            log.error("[SocketServer handler] socket 连接为空");
            return;
        }
        // 用于读取客户端的数据
        BufferedReader clientReader = null;
        // 用于回复客户端
        PrintStream outPrint = null;
        try {
            clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            outPrint = new PrintStream(client.getOutputStream());
            // while(true) 没有编写连接超时中断的功能，暂不处理
            while (true) {
                // 这里要注意和客户端输出流的写方法对应，否则会抛 EOFException
                String inputStr = clientReader.readLine();
                // 处理客户端内容
                System.out.format("从客户端接收到的信息: %s", inputStr);
                String responseStr = dealRequest(inputStr);
                outPrint.println(responseStr);
            }
        } catch (IOException e) {
            log.error("[SocketServer handler] IO 异常:", e);
        } finally {
            // 关闭资源
            log.info("[SocketServer handler] 关闭连接");
            // 方法开头已保证 client 不为空
            try {
                client.close();
            } catch (IOException e) {
                log.error("[SocketServer handler] IO 异常:", e);
            }
            if (clientReader != null) {
                try {
                    clientReader.close();
                } catch (IOException e) {
                    log.error("[SocketServer handler] IO 异常:", e);
                }
            }
            if (outPrint != null) {
                outPrint.close();
            }
        }
    }

    /**
     * 处理请求内容
     *
     * @param inputString 请求内容
     * @return 返回结果
     */
    private String dealRequest(String inputString) {
        String responseStr = null;
        // 判断 inputString 是否是 url 的格式
        UrlValidator urlValidator = new UrlValidator();
        if (!urlValidator.isValid(inputString)) {
            responseStr = "输入的字符串不是合法的 url";
            log.info("[SocketServer handler] {}", responseStr);
            return responseStr;
        }
        // 返回结果中的字符数量
        int charNum = 0;
        String result = HttpClientUtil.doGet(inputString);
        // 计算返回结果中的字符数量。请求结果内容为空，认定为字符数量为 0 就好了
        if (StringUtils.isNotBlank(result)) {
            charNum = result.length();
        }
        responseStr = "本次请求内容中，共有" + charNum + "个字符";
        log.info("[SocketServer handler] {}", responseStr);
        return responseStr;
    }
}

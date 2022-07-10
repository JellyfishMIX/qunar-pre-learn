package question5.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author JellyfishMIX
 * @date 7/3/22 13:25
 */
@Slf4j
public class SocketClient {

    public static void main(String[] args) {
        // 用于与 server 建立连接
        Socket server = null;
        // 用于读取服务端端数据
        BufferedReader inputReader = null;
        // 用于向服务器端发送数据
        PrintStream outPrint = null;
        try {
            server = new Socket(SocketConstant.HOST, SocketConstant.PORT);
            log.info("[SocketClient 主方法] 建立新连接");
            System.out.println("输入exit可以退出");
            inputReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            outPrint = new PrintStream(server.getOutputStream());
            Scanner inputScanner = new Scanner(System.in);
            while (true) {
                // 举例输入：https://www.jianshu.com/shakespeare/v2/notes/9043243df546/book
                String scannedString = inputScanner.nextLine();
                if (SocketConstant.CLIENT_EXIT.equals(scannedString)) {
                    break;
                }
                // 向服务端发送数据
                outPrint.println(scannedString);
                // 从服务端接收返回的数据
                String responseString = inputReader.readLine();
                System.out.println(responseString);
            }
        } catch (IOException e) {
            log.error("[SocketClient 与服务端数据交互] IO 异常:", e);
        } finally {
            // 资源关闭
            log.info("[SocketClient 主方法] 关闭连接");
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    log.error("[SocketClient 与服务端数据交互] socket 关闭异常:", e);
                }
            }
            if (inputReader !=null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    log.error("[SocketClient 与服务端数据交互] inputReader 关闭异常:", e);
                }
            }
            if (outPrint != null) {
                outPrint.close();
            }
        }
    }
}

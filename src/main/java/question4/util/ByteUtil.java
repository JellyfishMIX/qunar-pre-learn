package question4.util;

import com.google.common.annotations.Beta;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * byte 工具类
 *
 * @deprecated 未使用，暂时废弃
 * @beta 仅编写，未经过测试，不保证功能的可用
 * @author JellyfishMIX
 * @date 7/9/22 02:25
 */
@Slf4j
@Beta
public class ByteUtil {
    public List<String> bytesToStringList(byte[] bytes) {
        Object object = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream (bytes);
            ois = new ObjectInputStream (bis);
            object = ois.readObject();
        } catch (IOException e) {
            log.error("byte[] 转 List<String>，IO 异常: ", e);
        } catch (ClassNotFoundException e) {
            log.error("byte[] 转 List<String>，类转换异常: ", e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    log.error("byte[] 转 List<String>，IO 异常: ", e);
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("byte[] 转 List<String>，IO 异常: ", e);
                }
            }
        }
        return (List<String>) object;
    }

    public byte[] StringListToBytes(List<String> stringList) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(stringList);
            oos.flush();
            bytes = bos.toByteArray ();
        } catch (IOException e) {
            log.error("List<String> 转 byte[]，IO 异常: ", e);
        } finally {
            // 关闭流
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    log.error("List<String> 转 byte[], IO 异常: ", e);
                }
            }
            try {
                bos.close();
            } catch (IOException e) {
                log.error("List<String> 转 byte[], IO 异常: ", e);
            }
        }
        return bytes;
    }

    public static void main(String[] args) {
        ByteUtil byteUtil = new ByteUtil();
        List<String> stringList = byteUtil.bytesToStringList(new byte[]{});
        System.out.println(stringList);
    }
}

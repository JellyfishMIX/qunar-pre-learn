package question3.calculator;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import question3.vo.PropLine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 替换规则计算器
 *
 * @author JellyfishMIX
 * @date 7/5/22 21:16
 */
@Slf4j
public abstract class Calculator {
    /**
     * 存储着 prop 文件中解析出的元素
     */
    protected final ImmutableList<PropLine> propLineImmutableList;

    public Calculator(ImmutableList<PropLine> propLineImmutableList) {
        if (CollectionUtils.isEmpty(propLineImmutableList)) {
            String errorMsg = "[替换规则计算器] 解析出的文件元素集合不能为空";
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        this.propLineImmutableList = propLineImmutableList;
    }

    /**
     * 计算替换的字符串
     *
     * @param number 指定的数字
     * @return 计算出用于替换的字符串
     */
    public abstract String calculate(int number);

    /**
     * 加载并解析目标文件
     *
     * @return 解析得到的 propLineList
     */
    public static ImmutableList<PropLine> parsePropLine(String filename) {
        if (StringUtils.isBlank(filename)) {
            log.info("[替换规则计算器] 请输入文件名");
            return null;
        }

        BufferedReader bufferedReader = null;
        ImmutableList<PropLine> parsedResult = null;
        try {
            // Reader 按字符读取，BufferReader 自带 8kb 缓冲区
            bufferedReader = new BufferedReader(new FileReader(filename));

            List<PropLine> propLineList = new ArrayList<>();
            // 使用 bufferReader 逐行遍历
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                // 读取的一行转换成 propLine
                PropLine propLine = convertToPropLine(line);
                // 如果不是有效的，则不加入集合中
                if (!PropLine.valid(propLine)) {
                    log.error("[替换规则计算器] 读取到的文件中，本行不合法: {}", line);
                    continue;
                }
                propLineList.add(propLine);
            }

            // 换成不可变集合
            parsedResult = ImmutableList.copyOf(propLineList);
        } catch (IOException e) {
            log.error("[替换规则计算器] IO 异常", e);
        } finally {
            // 关闭资源
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.error("[替换规则计算器] IO 异常", ex);
                }
            }
        }
        if (parsedResult == null) {
            log.error("[替换规则计算器] 解析 prop 文件错误");
        }
        return parsedResult;
    }

    /**
     * 读取的一行转换成 propLine
     *
     * @param line 读取的一行
     * @return 转换成的 propLine。如果解析失败则返回 null
     */
    private static PropLine convertToPropLine(String line) {
        // 多个分隔符分隔。分割后例如：[5722, 少女中三人十]
        List<String> splitList = Splitter.on('\t').trimResults().omitEmptyStrings().splitToList(line);

        String indexStr = splitList.get(0);
        // 校验是否为数字
        if (!NumberUtils.isCreatable(indexStr)) {
            log.error("[替换规则计算器] 读取到的文件中，本行不合法，第一列不是数字: {}", line);
            return null;
        }
        int index = Integer.parseInt(indexStr);
        String text = splitList.get(1);
        // 校验文本内容是否合法，不合法则返回 null
        if (StringUtils.isBlank(text)) {
            log.error("[替换规则计算器] 读取到的文件中，本行不合法，第二列文本内容为空: {}", line);
            return null;
        }
        return new PropLine(index, text);
    }
}

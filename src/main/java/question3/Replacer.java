package question3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import question2.StatisticsEffectiveCode;
import question3.calculator.Calculator;
import question3.constant.CalculatorConstant;
import question3.calculator.CalculatorFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author JellyfishMIX
 * @date 7/5/22 21:19
 */
@Slf4j
public class Replacer {
    /**
     * 存储 Calculator 实现
     */
    private Map<String, Calculator> calculatorMap = Maps.newHashMap();
    /**
     * 输出的文件名
     */
    private List<String> outputList = Lists.newArrayList();

    /**
     * 初始化并加载 Calculator 实现
     *
     * @param propFilename prop 文件名，请输入带有目录的路径格式
     */
    public Replacer(String propFilename) {
        // 判空校验
        if (StringUtils.isBlank(propFilename)) {
            String errorMsg = "请输入文件名";
            log.info(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        // 创建 Calculator 实现
        propFilename = Calculator.class.getResource("/").getPath().concat(CalculatorConstant.PROP_FILENAME);
        Calculator naturalCalculator = CalculatorFactory.createCalculator(CalculatorConstant.NATURAL, propFilename);
        Calculator indexCalculator = CalculatorFactory.createCalculator(CalculatorConstant.INDEX, propFilename);
        Calculator charCalculator = CalculatorFactory.createCalculator(CalculatorConstant.CHAR, propFilename);
        Calculator charDescCalculator = CalculatorFactory.createCalculator(CalculatorConstant.CHAR_DESC, propFilename);
        // 加载至 calculatorMap 中
        calculatorMap.put(CalculatorConstant.NATURAL, naturalCalculator);
        calculatorMap.put(CalculatorConstant.INDEX, indexCalculator);
        calculatorMap.put(CalculatorConstant.CHAR, charCalculator);
        calculatorMap.put(CalculatorConstant.CHAR_DESC, charDescCalculator);
    }

    public static void main(String[] args) {
        // 目标路径，classpath
        String targetPath = StatisticsEffectiveCode.class.getResource("/").getPath();
        // 组装文件路径参数
        // prop 文件名
        String propFilename = targetPath.concat(CalculatorConstant.PROP_FILENAME);
        // template 文件名
        String templateFilename = targetPath.concat(CalculatorConstant.TEMPLATE_FILENAME);
        Replacer replacer = new Replacer(propFilename);
        // 输出文件名
        String outputFilename = targetPath.concat(CalculatorConstant.OUTPUT_FILENAME);
        // 开始处理
        replacer.scanAndDeal(templateFilename, outputFilename);
    }

    /**
     * 扫描文件并处理后输出到指定目录中
     *
     * @param outputFilename 输出文件名，请携带完整路径
     */
    private void scanAndDeal(String templateFilename, String outputFilename) {
        if (StringUtils.isBlank(templateFilename) || StringUtils.isBlank(outputFilename)) {
            log.info("template 文件名或输出文件名不能为空");
            return;
        }

        BufferedReader bufferedReader = null;
        try {
            // Reader 按字符读取，BufferReader 自带 8kb 缓冲区
            System.out.println(templateFilename);
            bufferedReader = new BufferedReader(new FileReader(templateFilename));

            // 使用 bufferReader 逐行遍历
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                // 进行替换处理
                String dealtLine = replace(line);
                outputList.add(dealtLine);
            }

            // 输出
            outputResult(outputFilename, outputList);
        } catch (IOException e) {
            log.error("[文本替换器] IO 异常", e);
        } finally {
            // 关闭资源
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.error("[文本替换器] IO 异常", ex);
                }
            }
        }
    }

    /**
     * 把入参字符串按规则替换。子串递归
     *
     * @param line 入参字符串
     * @return 替换完毕待字符串
     */
    private String replace(String line) {
        // 递归终止条件 1，空字符串直接返回即可
        if (StringUtils.isBlank(line)) {
            return line;
        }

        // 找占位符开始位置
        int placeholderIndex = line.indexOf(CalculatorConstant.PLACEHOLDER_STARTER);
        // 循环找占位符，直到找不到为止
        while (placeholderIndex != -1) {
            // 找到了占位符，找占位符的终结符位置
            int terminator = line.indexOf(CalculatorConstant.PLACEHOLDER_TERMINATOR, placeholderIndex);
            // 截取占位符
            String placeholder = line.substring(placeholderIndex, terminator + 1);
            String dealtLine = dealPlaceHolder(line, placeholder);

            // 重新寻找占位符开始位置
            placeholderIndex = dealtLine.indexOf(CalculatorConstant.PLACEHOLDER_STARTER);
            // 替换为处理后的 line
            line = dealtLine;
        }
        return line;
    }

    /**
     * 处理占位符
     *
     * @param line 待处理的字符串
     * @param placeholder 占位符
     * @return 指定占位符转换后的字符串
     */
    private String dealPlaceHolder(String line, String placeholder) {
        // 入参无需做判空校验，调用处已有校验
        // 解析占位符的 type
        int frontBracketIndex = placeholder.indexOf(CalculatorConstant.PLACEHOLDER_FRONT_BRACKET);
        String type = placeholder.substring(1, frontBracketIndex);
        // 解析占位符的 number
        String numberStr = placeholder.substring(frontBracketIndex + 1, placeholder.length() - 1);
        // 解析出错，直接返回
        if (!NumberUtils.isCreatable(numberStr)) {
            log.error("[文本替换器] 处理占位符错误，占位符缺少数字");
            return line;
        }
        // 占位符的 number 参数
        int number = Integer.parseInt(numberStr);
        Calculator calculator = calculatorMap.get(type);
        // 未获取到占位符类型对应的替换规则计算器，本处占位符不处理
        if (calculator == null) {
            log.error("[文本替换器] 未获取到占位符类型对应的替换规则计算器，本处占位符不处理, line: {}, placeholder: {}", line, placeholder);
            return line;
        }
        String text = calculator.calculate(number);
        // 进行 line 占位符的替换
        return line.replace(placeholder, text);
    }

    /**
     * 输出到指定路径
     *
     * @param outputFilename 指定输出路径
     * @param outputList 输出内容
     */
    private static void outputResult(String outputFilename, List<String> outputList) {
        // 判空校验
        if (StringUtils.isBlank(outputFilename) || CollectionUtils.isEmpty(outputList)) {
            log.info("输出路径或输出内容不能为空");
        }
        // 输出内容
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 结果输出至指定文件
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outputFilename));
            // 循环一行行输出
            for (String outputLine : outputList) {
                outputLine = outputLine.concat("\n");
                bufferedOutputStream.write(outputLine.getBytes());
            }
        } catch (IOException e) {
            log.error("[输出到指定路径] IO 异常", e);
        } finally {
            // 关闭资源
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    log.error("[输出到指定路径] IO 异常", e);
                }
            }
        }
    }
}

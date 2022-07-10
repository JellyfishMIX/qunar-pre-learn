package question2;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import question1.StatisticsRequest;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 统计指定文件中的代码有效行数
 *
 * @author JellyfishMIX
 * @date 6/28/22 19:30
 */
public class StatisticsEffectiveCode {
    public static Logger log = LoggerFactory.getLogger(StatisticsEffectiveCode.class);
    /**
     * 要读取的文件名
     */
    private final static String INPUT_FILE_NAME = "StringUtils.java";
    /**
     * 要输出的文件名
     */
    private final static String OUTPUT_FILE_NAME = "validLineCount.txt";
    /**
     * 是否处于多行注释计算模式
     */
    private static boolean multiCommentMod = false;

    public static void main(String[] args) {
        // 有效代码行数
        int effectiveCodeNum = statisticCode(INPUT_FILE_NAME);
        // 目标路径，结果输出到 classpath 中
        String targetPath = StatisticsEffectiveCode.class.getResource("/").getPath();
        // 结果输出到指定文件中
        outputResult(targetPath, OUTPUT_FILE_NAME, effectiveCodeNum);
    }

    /**
     * 主方法，返回有效代码行数
     * @param filename 指定的文件名
     *
     * @return 有效代码行数
     */
    private static int statisticCode(String filename)  {
        // 有效代码行数
        int effectiveCodeNum = 0;
        if (StringUtils.isBlank(filename)) {
            log.info("[统计有效代码行数] 请输入文件名");
            return effectiveCodeNum;
        }
        BufferedReader bufferedReader = null;
        try {
            // Reader 按字符读取，BufferReader 自带 8kb 缓冲区
            filename = StatisticsRequest.class.getResource("/").getPath().concat(filename);
            bufferedReader = new BufferedReader(new FileReader(filename));

            // 使用 bufferReader 逐行遍历
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                // 去掉开头和结尾的空格换行符等
                line = line.trim();
                // 校验空行，单行注释
                if (checkIfBlank(line) || checkIfSingleComment(line)) {
                    // 无效代码，略过统计
                    continue;
                }
                // 校验多行注释
                // 未处于多行注释计算模式，且以注释开头
                if (!checkIfMultiCommentMod() && checkIfStartWithCommentHead(line)) {
                    // 进入多行注释计算模式
                    onMultiCommentMod();
                    // 此行为注释代码，略过统计
                    continue;
                } else if (multiCommentMod) {
                    // 是否为注释结束行
                    if (checkIfEndWithCommentTail(line)) {
                        // 遇到注释结束行，退出多行注释计算模式
                        offMultiCommentMod();
                    }
                    // 此行为注释行，略过统计
                    continue;
                }
                // 有效代码行数 +1
                effectiveCodeNum++;
            }
        } catch (IOException e) {
            log.error("[统计有效代码行树] IO 异常", e);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.error("[统计有效代码行数] IO 异常", e);
                    ex.printStackTrace();
                }
            }
        }
        // 返回有效代码行数
        log.info("有效代码行数为：{}", effectiveCodeNum);
        return effectiveCodeNum;
    }

    /**
     * 校验是否为空行
     *
     * @param line line
     * @return true 空行，else 非空行
     */
    private static boolean checkIfBlank(String line) {
        boolean blankFlag = false;
        if (StringUtils.isBlank(line)) {
            blankFlag = true;
        }
        return blankFlag;
    }

    /**
     * 校验是否为单行注释
     *
     * @param line line
     * @return true 是单行注释, false 非单行注释
     */
    private static boolean checkIfSingleComment(String line) {
        boolean commentFlag = false;
        // 针对注释样式：//
        String singleCommentRegx = "//.*";
        commentFlag = Pattern.matches(singleCommentRegx, line);
        return commentFlag;
    }

    /**
     * 校验是否以注释结尾
     *
     * @param line 注释
     * @return true 以多行注释符开始, false 不是以多行注释符开始
     */
    private static boolean checkIfEndWithCommentTail(String line) {
        boolean commentFlag = false;
        // 针对注释结尾样式：*/
        String commentTailRegex = ".*\\*/";
        commentFlag = Pattern.matches(commentTailRegex, line);
        return commentFlag;
    }

    /**
     * 校验是否以注释开头
     *
     * @param line line
     * @return true 以多行注释终结符结尾, false 不是以多行注释终结符结尾
     */
    private static boolean checkIfStartWithCommentHead(String line) {
        boolean commentFlag = false;
        // 针对注释开头样式：/*
        String commentHeadRegex1 = "/\\*.*";
        // 针对注释开头样式：/**
        String commentHeadRegex2 = "/\\*\\*.*";
        commentFlag = Pattern.matches(commentHeadRegex1, line) || Pattern.matches(commentHeadRegex2, line);
        return commentFlag;
    }

    /**
     * 校验是否处于多行注释计算模式
     *
     * @return true 处于，false 不处于
     */
    private static boolean checkIfMultiCommentMod() {
        return multiCommentMod;
    }

    /**
     * 进入多行注释计算模式
     */
    private static void onMultiCommentMod() {
        multiCommentMod = true;
    }

    /**
     * 退出多行注释计算模式
     */
    private static void offMultiCommentMod() {
        multiCommentMod = false;
    }

    /**
     * 输出到指定路径
     *
     * @param targetPath 指定目录
     * @param filename 文件名
     * @param effectiveCodeNum 有效代码行数
     */
    private static void outputResult(String targetPath, String filename, int effectiveCodeNum) {
        // 判空校验
        if (StringUtils.isBlank(targetPath) || StringUtils.isBlank(filename)) {
            log.info("输出目录或文件名不能为空");
        }
        // 输出路径
        String realPath = targetPath.concat(filename);
        // 输出内容
        StringBuilder outputContentBuilder = new StringBuilder();
        outputContentBuilder.append("有效代码行数为: ").append(effectiveCodeNum);
        FileOutputStream fileOutputStream = null;
        try {
            // 结果输出至指定文件
            fileOutputStream = new FileOutputStream(realPath);
            fileOutputStream.write(outputContentBuilder.toString().getBytes());
        } catch (IOException e) {
            log.error("[输出到指定路径] IO 异常", e);
        } finally {
            // 关闭资源
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error("[输出到指定路径] IO 异常", e);
                }
            }
        }
    }
}

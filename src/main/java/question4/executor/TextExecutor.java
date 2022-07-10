package question4.executor;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import question4.vo.Command;
import question4.vo.TextCommand;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author JellyfishMIX
 * @date 7/8/22 21:56
 */
@Slf4j
public abstract class TextExecutor extends Executor {
    /**
     * 检查命令是否有效
     *
     * @param command 命令单元
     * @return true 有效，false 无效
     */
    @Override
    protected boolean valid(Command command) {
        if (!super.valid(command)) {
            // 父有效性检查失败，直接返回
            return false;
        }
        String filename = getFilename(command);
        // filename 和 input 不能同时为空
        if (StringUtils.isBlank(filename) && command.getInput() == null) {
            return false;
        }
        // TextExecutor 执行的 command 需要是 TextCommand
        return command instanceof TextCommand;
    }

    /**
     * 命令单元标准化。标准化的 Command, 标准化规则由子类 Executor 实现。
     *
     * @param command
     * @return 标准化是否成功。true 成功，false 失败
     */
    @Override
    protected boolean standardizeCommand(Command command) {
        String filename = getFilename(command);
        // 如果 filename 不为空，则读取数据转成 List<String>
        if (StringUtils.isNotBlank(filename)) {
            try {
                List<String> text = CharStreams.readLines(new BufferedReader(new FileReader(filename)));
                // System.out.println(text);
                // 设置进 command 的输入
                command.setInput(text);
            } catch (IOException e) {
                log.error("[命令单元标准化] IO 异常:", e);
                // 遇到异常，标准化失败
                return false;
            }
        }
        // filename 为空，则说明 input 不为空，无需处理
        return true;
    }

    /**
     * 执行命令具体方法，由子类实现
     *
     * @param command 命令单元
     * @return 输出数据
     */
    @Override
    protected abstract List<String> executeInternal(Command command);

    private String getFilename(Command command) {
        String filename = getFilenameInternal(command);
        if (StringUtils.isNotBlank(filename)) {
            // filename 不是 linux 且不是 windows 的绝对路径格式，则默认为 classpath 路径
            if (!filename.startsWith("/") && !filename.contains(":\\\\")) {
                // 目标路径，classpath
                String targetPath = this.getClass().getResource("/").getPath();
                filename = targetPath.concat(filename);
            }
        }
        return filename;
    }

    /**
     * 获取 filename 内部方法，这是默认的方法，子类可以重写自己的获取方法
     * @return
     */
    protected String getFilenameInternal(Command command) {
        List<String> arguments = command.getArguments();
        String filename = null;
        if (CollectionUtils.isNotEmpty(arguments)) {
            filename = arguments.get(arguments.size() - 1);

        }
        return filename;
    }
}

package question4.executor.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import question4.enums.CommandNameEnum;
import question4.executor.TextExecutor;
import question4.vo.Command;
import question4.vo.TextCommand;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JellyfishMIX
 * @date 7/9/22 12:37
 */
@Slf4j
public class GrepExecutor extends TextExecutor {
    /**
     * 命令类型
     */
    private static final String TYPE = CommandNameEnum.GREP.getName();

    /**
     * 执行命令具体方法
     *
     * @param command 命令单元
     * @return 输出流
     */
    @Override
    public List<String> executeInternal(Command command) {
        // 模版方法里有有效性检查，直接强转即可
        TextCommand textCommand = (TextCommand) command;
        // 判断命令类型和当前 executor 是否匹配
        if (!GrepExecutor.TYPE.equals(command.getName())) {
            log.error("[CatExecutor] 命令单元未分配到对应的 executor, command: {}", JSON.toJSONString(command));
            return null;
        }
        List<String> inputLines = textCommand.getInput();
        // 判空校验
        if (CollectionUtils.isEmpty(inputLines)) {
            return null;
        }
        List<String> arguments = command.getArguments();
        // 没有指定关键字，则返回 null
        if (CollectionUtils.isEmpty(arguments)) {
            return null;
        }
        final String keyword = arguments.get(0);
        // 过滤文本关键字
        List<String> result = inputLines.stream().filter((line) -> line.contains(keyword)).collect(Collectors.toList());
        return result;
    }

    /**
     * 获取 filename 内部方法，这是默认的方法，子类可以重写自己的获取方法
     *
     * @param command
     * @return
     */
    @Override
    protected String getFilenameInternal(Command command) {
        List<String> arguments = command.getArguments();
        String filename = null;
        if (CollectionUtils.isNotEmpty(arguments) && arguments.size() > 1) {
            filename = arguments.get(arguments.size() - 1);
        }
        return filename;
    }
}

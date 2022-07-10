package question4.executor.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import question4.enums.CommandNameEnum;
import question4.executor.TextExecutor;
import question4.vo.Command;
import question4.vo.TextCommand;

import java.util.List;

/**
 * @author JellyfishMIX
 * @date 7/9/22 13:26
 */
@Slf4j
public class WcExecutor extends TextExecutor {
    /**
     * 命令类型
     */
    private static final String TYPE = CommandNameEnum.WC.getName();

    /**
     * 执行命令具体方法
     *
     * @param command
     * @return 输出流
     */
    @Override
    public List<String> executeInternal(Command command) {
        // 模版方法里有有效性检查，直接强转即可
        TextCommand textCommand = (TextCommand) command;
        // 判断命令类型和当前 executor 是否匹配
        if (!WcExecutor.TYPE.equals(command.getName())) {
            log.error("[CatExecutor] 命令单元未分配到对应的 executor, command: {}", JSON.toJSONString(command));
            return null;
        }
        int lineNum = staticsLine(textCommand.getInput());
        return Lists.newArrayList(String.valueOf(lineNum));
    }

    /**
     * 统计行数
     *
     * @return 行数
     */
    private int staticsLine(List<String> inputLines) {
        // 判空校验
        if (CollectionUtils.isEmpty(inputLines)) {
            return 0;
        }
        return inputLines.size();
    }
}

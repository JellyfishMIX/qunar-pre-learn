package question4.executor.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import question4.vo.Command;
import question4.enums.CommandNameEnum;
import question4.vo.TextCommand;
import question4.executor.TextExecutor;

import java.util.List;

/**
 * @author JellyfishMIX
 * @date 7/7/22 20:17
 */
@Slf4j
public class CatExecutor extends TextExecutor {
    /**
     * 命令类型
     */
    private static final String TYPE = CommandNameEnum.CAT.getName();

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
        if (!CatExecutor.TYPE.equals(command.getName())) {
            log.error("[CatExecutor] 命令单元未分配到对应的 executor, command: {}", JSON.toJSONString(command));
            return null;
        }
        // 直接返回解析的文本内容即可
        return textCommand.getInput();
    }
}

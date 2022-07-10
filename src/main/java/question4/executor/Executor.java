package question4.executor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import question4.vo.Command;

/**
 * @author JellyfishMIX
 * @date 7/7/22 18:59
 */
@Slf4j
public abstract class Executor {
    /**
     * 执行命令，模版方法
     *
     * @param command 命令单元
     * @return 输出流
     */
    public Object execute(Command command) {
        // 检查命令是否有效
        if (!valid(command)) {
            // 命令单元无效，直接返回
            return null;
        }
        // 标准化
        boolean standardizeFlag = standardizeCommand(command);
        // 标准化失败，直接返回
        if (!standardizeFlag) {
            return null;
        }
        // 执行命令具体方法
        return executeInternal(command);
    }

    /**
     * 检查命令是否有效
     *
     * @param command 命令单元
     * @return true 有效，false 无效
     */
    protected boolean valid(Command command) {
        // 判断命令单元是否有效
        if (!Command.valid(command)) {
            log.error("[CatExecutor] 命令非法，command: {}", JSON.toJSONString(command));
            return false;
        }
        return true;
    }

    /**
     * 命令单元标准化。标准化的 Command, 标准化规则由子类 Executor 实现。
     *
     * @return 标准化是否成功。true 成功，false 失败
     */
    protected abstract boolean standardizeCommand(Command command);

    /**
     * 执行命令具体方法，由子类实现
     *
     * @param
     * @return 输出数据
     */
    protected abstract Object executeInternal(Command command);
}

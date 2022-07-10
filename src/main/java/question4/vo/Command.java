package question4.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 命令单元
 *
 * @author JellyfishMIX
 * @date 7/7/22 18:59
 */
@Data
public class Command {
    /**
     * 命令名称
     */
    private String name;
    /**
     * 命令选项。可以为空。
     */
    private List<String> options;
    /**
     * 命令参数。默认最后一个参数为 filename
     */
    private List<String> arguments;
    /**
     * 输入的数据
     */
    private Object input;
    /**
     * 命令类型
     */
    private String type;

    public Command(String name, List<String> options, List<String> arguments, String type) {
        this.name = name;
        this.options = options;
        this.arguments = arguments;
        this.type = type;
    }

    /**
     * 校验命令单元是否有效
     *
     * @return true 有效, false 无效
     */
    public static boolean valid(Command command) {
        // 命令名称不能为空
        if (command == null || StringUtils.isBlank(command.getName())) {
            return false;
        }
        return true;
    }
}

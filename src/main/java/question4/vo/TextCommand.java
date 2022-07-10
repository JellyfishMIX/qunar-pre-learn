package question4.vo;

import lombok.Getter;

import java.util.List;

/**
 * @author JellyfishMIX
 * @date 7/8/22 22:03
 */
@Getter
public class TextCommand extends Command {
    /**
     * 输入的数据
     */
    private List<String> input;

    public TextCommand(String name, List<String> options, List<String> arguments, String commandType) {
        super(name, options, arguments, commandType);
    }

    public void setInput(Object input) {
        this.input = (List<String>) input;
    }
}

package question4.enums;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 命令选项枚举
 *
 * @author JellyfishMIX
 * @date 7/7/22 21:33
 */
@Getter
public enum CommandOptionEnum {
    /**
     * wc 命令，选项 l，表示统计行数
     */
    WC_L("l");

    /**
     * 命令选项
     */
    private String argument;

    CommandOptionEnum(String argument) {
        this.argument = argument;
    }

    /**
     * wc 命令的选项列表
     */
    private static final List<String> WC_OPTION_LIST = Lists.newArrayList(WC_L.getArgument());

    /**
     * 各命令对应的选项列表
     */
    public static final Map<String, List<String>> NAME_OPTION_MAP = ImmutableMap.<String, List<String>>builder()
            .put(CommandNameEnum.WC.getName(), WC_OPTION_LIST)
            .build();
}

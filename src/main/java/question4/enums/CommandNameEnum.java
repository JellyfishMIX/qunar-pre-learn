package question4.enums;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 命令名称枚举
 *
 * @author JellyfishMIX
 * @date 7/7/22 20:28
 */
@Getter
public enum CommandNameEnum {
    /**
     * cat 命令
     */
    CAT("cat"),
    /**
     * grep 命令
     */
    GREP("grep"),
    /**
     * wc 命令
     */
    WC("wc");

    /**
     * 命令名称
     */
    private String name;

    CommandNameEnum(String name) {
        this.name = name;
    }

    /**
     * text 命令的选项列表
     */
    private static final List<String> TEXT_LIST = Lists.newArrayList(
            CAT.getName(),
            GREP.getName(),
            WC.getName()
    );

    /**
     * 命令类型对应的命令名称
     */
    public static final Map<String, List<String>> TYPE_NAME_MAP = ImmutableMap.<String, List<String>>builder()
            .put(CommandTypeEnum.TEXT.getType(), TEXT_LIST)
            .build();

    /**
     * 通过 name 获得 type
     *
     * @param name commandName
     * @return commandType
     */
    public static String getTypeByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (Map.Entry<String, List<String>> entry : TYPE_NAME_MAP.entrySet()) {
            List<String> typeNameList = entry.getValue();
            if (typeNameList.contains(name)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
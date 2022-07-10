package question4.enums;

import lombok.Getter;

/**
 * @author JellyfishMIX
 * @date 7/9/22 05:02
 */
@Getter
public enum CommandTypeEnum {
    TEXT("text");

    /**
     * 命令类型
     */
    private String type;

    CommandTypeEnum(String type) {
        this.type = type;
    }
}

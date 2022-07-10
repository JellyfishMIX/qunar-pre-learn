package question3.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * sdxl_prop 文件中的一行元素。仅在 Calculator 这个抽象类进行创建，子类可读不可修改，因此设计为不可变对象。
 *
 * @author JellyfishMIX
 * @date 7/5/22 23:38
 */
@Getter
@ToString
@AllArgsConstructor
public class PropLine {
    /**
     * 索引
     */
    private final int index;
    /**
     * 文本内容
     */
    private final String text;

    /**
     * 数据是否有效
     *
     * @return true 有效，false 无效
     */
    public static boolean valid(PropLine propLine) {
        if (propLine == null || StringUtils.isBlank(propLine.getText())) {
            return false;
        }
        return true;
    }
}

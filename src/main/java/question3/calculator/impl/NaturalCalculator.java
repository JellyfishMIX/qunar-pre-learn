package question3.calculator.impl;

import com.google.common.collect.ImmutableList;
import question3.vo.PropLine;
import question3.calculator.Calculator;

/**
 * 替换规则计算器-自然规则
 * 自然顺序即父类中的 propLineList 顺序，不需要做改动
 *
 * @author JellyfishMIX
 * @date 7/5/22 21:31
 */
public class NaturalCalculator extends Calculator {
    public NaturalCalculator(ImmutableList<PropLine> propLineImmutableList) {
        super(propLineImmutableList);
    }

    /**
     * 计算出用于替换的字符串。自然顺序按 propLineList 中的顺序返回即可。
     *
     * @param number 指定的数字
     * @return 用于替换的字符串
     */
    @Override
    public String calculate(int number) {
        PropLine propLine = propLineImmutableList.get(number);
        String text = null;
        if (propLine != null) {
            text = propLine.getText();
        }
        return text;
    }
}

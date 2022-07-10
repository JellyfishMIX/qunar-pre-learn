package question3.calculator.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import question3.calculator.Calculator;
import question3.vo.PropLine;

import java.util.List;

/**
 * 替换规则计算器-字符串排序
 *
 * @author JellyfishMIX
 * @date 7/6/22 19:39
 */
public class CharCalculator extends Calculator {
    /**
     * 排好序的 List
     */
    private List<PropLine> sortedList;

    public CharCalculator(ImmutableList<PropLine> propLineImmutableList) {
        super(propLineImmutableList);

        // 开始排序
        List<PropLine> propLineList = Lists.newArrayList(propLineImmutableList);
        propLineList.sort((a, b) -> a.getText().compareToIgnoreCase(b.getText()));
        this.sortedList = propLineList;
    }

    /**
     * 计算替换的字符串
     *
     * @param number 指定的数字
     * @return 计算出用于替换的字符串
     */
    @Override
    public String calculate(int number) {
        PropLine propLine = sortedList.get(number);
        String text = null;
        if (propLine != null) {
            text = propLine.getText();
        }
        return text;
    }
}

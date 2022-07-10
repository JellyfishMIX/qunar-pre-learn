package question3.calculator.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import question3.calculator.Calculator;
import question3.vo.PropLine;

import java.util.List;

/**
 * @author JellyfishMIX
 * @date 7/6/22 19:58
 */
public class CharDescCalculator extends Calculator {
    /**
     * 排好序的 List
     */
    private List<PropLine> sortedList;

    public CharDescCalculator(ImmutableList<PropLine> propLineImmutableList) {
        super(propLineImmutableList);

        // 开始排序
        List<PropLine> propLineList = Lists.newArrayList(propLineImmutableList);
        propLineList.sort((a, b) -> b.getText().compareToIgnoreCase(a.getText()));
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

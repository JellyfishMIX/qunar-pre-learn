package question3.calculator;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import question3.calculator.impl.CharCalculator;
import question3.calculator.impl.CharDescCalculator;
import question3.constant.CalculatorConstant;
import question3.vo.PropLine;
import question3.calculator.impl.IndexCalculator;
import question3.calculator.impl.NaturalCalculator;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Calculator 工厂类
 *
 * @author JellyfishMIX
 * @date 7/6/22 11:02
 */
@Slf4j
public class CalculatorFactory {
    /**
     * 定义一个容器，容纳所有 Calculator 产品
     */
    private static final ConcurrentHashMap<String, Calculator> instanceMap = new ConcurrentHashMap<>();
    /**
     * 定义一个容器，容纳所有的 propLineList
     */
    private static final ConcurrentHashMap<String, ImmutableList<PropLine>> propLineMap = new ConcurrentHashMap<>();

    public static Calculator createCalculator(String type, String filename) {
        // 判空校验
        if (StringUtils.isBlank(type) || StringUtils.isBlank(filename)) {
            log.error("[Calculator 工厂创建产品] type 或 filename 不能为空");
            return null;
        }

        Calculator product = null;
        // 加载的时候,如果在容器中已经存在那么就将类取出使用，否则新建，然后将新建的类放入到容器中
        String key = type + filename;
        if (instanceMap.containsKey(key)) {
            product = instanceMap.get(key);
        } else {
            product = createInstance(type, filename);
            instanceMap.put(key, product);
        }
        return product;
    }

    /**
     * 根据 type 和 filename 创建 Calculator 实例
     *
     * @param type type
     * @param filename filename
     * @return Calculator 实例
     */
    private static Calculator createInstance(String type, String filename) {
        // 调用处已经判空校验，此方法入口处无需判空校验
        // 寻找 filename 对应的 propLineList 是否已经初始化过
        ImmutableList<PropLine> propLineList = null;
        if (propLineMap.containsKey(filename)) {
            propLineList = propLineMap.get(filename);
        } else {
            propLineList = Calculator.parsePropLine(filename);
            // 解析结果不为空，则加入 map
            if (!CollectionUtils.isEmpty(propLineList)) {
                propLineMap.put(filename, propLineList);
            }
        }
        Calculator instance = null;
        // 根据类型创建具体实例
        switch (type) {
            case CalculatorConstant.NATURAL:
                instance = new NaturalCalculator(propLineList);
                break;
            case CalculatorConstant.INDEX:
                instance = new IndexCalculator(propLineList);
                break;
            case CalculatorConstant.CHAR:
                instance = new CharCalculator(propLineList);
                break;
            case CalculatorConstant.CHAR_DESC:
                instance = new CharDescCalculator(propLineList);
                break;
            default:
                throw new IllegalArgumentException("类型非法, type:".concat(type));
        }
        return instance;
    }
}

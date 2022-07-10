package question1;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 统计网络请求日志
 *
 * @author JellyfishMIX
 * @date 6/18/22 16:39
 */
@Slf4j
public class StatisticsRequest {
    /**
     * 要读取的文件名
     */
    private final static String FILE_NAME = "access.log";
    /**
     * 请求总数
     */
    private static int requestTotalNum = 0;
    /**
     * Get 请求数量
     */
    private static Integer getRequestNum = 0;
    /**
     * Post 请求数量
     */
    private static Integer postRequestNum = 0;
    /**
     * 统计词频
     */
    private static Multiset<String> frequencyMultiset = TreeMultiset.create();
    /**
     * 要统计的 top n 词频
     */
    private final static int TOP_N = 10;
    /**
     * 存储 top 10 词频的最小堆
     */
    private static PriorityQueue<Multiset.Entry<String>> frequencyMinHeap = new PriorityQueue<>(TOP_N, Comparator.comparingInt(Multiset.Entry::getCount));
    /**
     * 以 /AAA 纬度统计接口。key 是 AAA(AAA 即 firstPath)，Set<String> 往里 add 具体的 uri
     */
    private static Map<String, Set<String>> firstPathUriMap = new HashMap<>();

    public static void main(String[] args) {
        // log4j1 可以自动快速地使用缺省Log4j环境。本项目日志实现使用的 log4j2，因此使用 classpath 中自定义的配置。
        // BasicConfigurator.configure();
        StatisticsRequest.statisticsRequest(FILE_NAME);
    }

    /**
     * 统计请求总方法
     */
    private static void statisticsRequest(String filename) {
        if (StringUtils.isBlank(filename)) {
            log.info("[统计请求日志] 请输入文件名");
            return;
        }

        BufferedReader bufferedReader = null;
        try {
            // Reader 按字符读取，BufferReader 自带 8kb 缓冲区
            filename = StatisticsRequest.class.getResource("/").getPath().concat(filename);
            bufferedReader = new BufferedReader(new FileReader(filename));

            // 使用 bufferReader 逐行遍历
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                // 统计请求总数
                requestTotalNum++;
                // 统计 Get 请求和 Post 请求的数量
                statisticsGetAndPostRequest(line);
                // 统计请求频次 top 10 接口
                statisticTop10Interface(line);
                statisticInterfaceFirstPath(line);
            }
            // 使用最小堆计算 top 10 词频
            calculateTop10Interface();

            // 输出
            printStatisticResult();
        } catch (IOException e) {
            log.error("[统计请求日志] IO 异常", e);
        } finally {
            // 关闭资源
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.error("[统计请求日志] IO 异常", ex);
                }
            }
        }
    }

    /**
     * 输出统计结果
     */
    private static void printStatisticResult() {
        System.out.format("请求总数为：%d\n", requestTotalNum);
        System.out.format("get 请求总数为：%d\n", getRequestNum);
        System.out.format("post 请求总数为：%d\n", postRequestNum);
        // 基于堆的优先级队列仅保证第一个元素是最高/最低，没有方法获取排序形式的元素。如需输出排序形式的元素，请将队列转成数组，然后排序遍历。
        frequencyMinHeap.forEach(each -> {
            System.out.format("请求频次 top 10 接口：%s，次数为%d\n", each.getElement(), each.getCount());
        });
        // 输出以 /AAA 纬度统计的接口(AAA 即 firstPath)
        for (Map.Entry<String, Set<String>> firstPathEntry : firstPathUriMap.entrySet()) {
            Set<String> firstPathSet = firstPathEntry.getValue();
            if (firstPathSet == null) {
                System.out.format("%s 类别下，URI 的数量为：0\n", firstPathEntry.getKey());
                continue;
            }
            System.out.format("%s 类别下，URI 的数量为：%d，分别是：\n", firstPathEntry.getKey(), firstPathSet.size());
            firstPathSet.forEach(System.out::println);
        }
    }

    /**
     * 统计 Get 和 Post 请求的数量
     *
     * @param line line
     */
    private static void statisticsGetAndPostRequest(String line) {
        // 匹配串
        String getRegx = "GET.*";
        String postRegx = "POST.*";

        boolean isGetMatch = Pattern.matches(getRegx, line);
        boolean isPostMatch = Pattern.matches(postRegx, line);

        // get 和 post 请求计数
        if (isGetMatch) {
            getRequestNum++;
        } else if (isPostMatch) {
            postRequestNum++;
        } else {
            log.info("非常见请求，line: {}", line);
        }
    }

    /**
     * 统计请求频次 top 10 接口
     *
     * @param line line
     */
    private static void statisticTop10Interface(String line) {
        // 多个分隔符分隔。分割后：[GET /twell/querytwellDetailForMobile.htm, arg1=var1&arg2=var2]
        List<String> splitList = Splitter.on(CharMatcher.anyOf("?")).trimResults().omitEmptyStrings().splitToList(line);
        String uri = splitList.get(0);
        frequencyMultiset.add(uri);
    }

    /**
     * 使用最小堆计算 top 10 词频
     */
    private static void calculateTop10Interface() {
        // 遍历词频 entrySet
        int index = 0;
        for (Multiset.Entry<String> eachEntry : frequencyMultiset.entrySet()) {
            // 先放入 10 个元素
            if (index < TOP_N) {
                frequencyMinHeap.offer(eachEntry);
                // 堆中仅存储频次最大的 10 个元素
            } else {
                // 先 peek 一下，不 poll，因为不一定会替换堆顶
                Multiset.Entry<String> heapTopEntry = frequencyMinHeap.peek();
                // 避免空指针报错
                if (heapTopEntry == null) {
                    continue;
                }
                // 如果比堆顶大，则替换堆顶
                if (eachEntry.getCount() > heapTopEntry.getCount()) {
                    frequencyMinHeap.poll();
                    frequencyMinHeap.offer(eachEntry);
                }
            }
            // 已计算的元素个数
            index++;
        }
    }

    /**
     * 以 /AAA 纬度统计接口(AAA 即 firstPath)
     *
     * @param line line
     */
    private static void statisticInterfaceFirstPath(String line) {
        // 正则分隔，根据 uri 的 firstPath 判断类别。GET /sale/myQuery/queryBDCommissionByDay.json?arg1=var1&arg2=var2 -> [GET, sale, myQuery, queryBDCommissionByDay.json?arg1=var1&arg2=var2]
        List<String> firstPathSplitList = Splitter.onPattern("/").trimResults().omitEmptyStrings().splitToList(line);
        String firstPath = firstPathSplitList.get(1);

        // 分割出具体的 uri
        List<String> splitList = Splitter.on(CharMatcher.anyOf("?")).trimResults().omitEmptyStrings().splitToList(line);
        String uri = splitList.get(0);

        // 加入 firstPathUriMap。key 是 AAA(AAA 即 firstPath)，Set<String> 往里 add 具体的 uri
        Set<String> uriSet = firstPathUriMap.computeIfAbsent(firstPath, key -> new HashSet<>());
        uriSet.add(uri);
    }
}

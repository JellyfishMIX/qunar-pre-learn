package question4;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import question4.constant.CommandConstant;
import question4.enums.CommandNameEnum;
import question4.enums.CommandTypeEnum;
import question4.executor.Executor;
import question4.executor.impl.CatExecutor;
import question4.executor.impl.GrepExecutor;
import question4.executor.impl.WcExecutor;
import question4.vo.Command;
import question4.vo.TextCommand;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author JellyfishMIX
 * @date 7/7/22 18:58
 */
@Slf4j
public class Processor {
    /**
     * 存储 Executor 实现
     */
    private Map<String, Executor> executorMap = Maps.newHashMap();

    public Processor() {
        // 实例化 Executor
        Executor catExecutor = new CatExecutor();
        Executor grepExecutor = new GrepExecutor();
        Executor wcExecutor = new WcExecutor();
        // executor 的实现，加入 executorMap
        executorMap.put(CommandNameEnum.CAT.getName(), catExecutor);
        executorMap.put(CommandNameEnum.GREP.getName(), grepExecutor);
        executorMap.put(CommandNameEnum.WC.getName(), wcExecutor);
    }

    /**
     * 输入举例：cat log4j2.xml | grep root | wc -l
     *
     * @param args args
     */
    public static void main(String[] args) {
        Processor processor = new Processor();
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入exit可以退出");
        while (true) {
            String commandLine = scanner.nextLine();
            // 退出输入符号
            if (CommandConstant.EXIT.equals(commandLine)) {
                break;
            }
            // 略过空输入
            if (StringUtils.isBlank(commandLine)) {
                continue;
            }
            // 把命令行解析成 commandList
            List<Command> commandList = processor.parse(commandLine);
            // 执行命令
            List<String> output = processor.execute(commandList);
            // 命令输出
            if (CollectionUtils.isNotEmpty(output)) {
                output.forEach(System.out::println);
            }
        }
    }

    /**
     * 把命令行解析成 commandList
     *
     * @param commandLine 命令行。举例：wc -l log4j2.xml 或 cat log4j2.xml | grep root | wc -l
     * @return
     */
    private List<Command> parse(String commandLine) {
        if (StringUtils.isBlank(commandLine)) {
            return null;
        }
        // 按 | 解析单个命令字符串，解析结果例如: [cat log4j2.xml, grep root]
        List<String> commandStrList = Splitter.onPattern("\\|").trimResults().omitEmptyStrings().splitToList(commandLine);
        // System.out.println(commandStrList);
        List<Command> commandList = Lists.newArrayList();
        // 把每一个单个命令字符串，解析成单个命令
        for (String commandStr : commandStrList) {
            Command command = parseCommandStr(commandStr);
            // 未解析出命令单元，继续解析下一个
            if (command == null) {
                log.error("[命令行解析] 未解析出命令单元，单个命令字符串: {}", commandStr);
                continue;
            }
            commandList.add(command);
        }
        return commandList;
    }

    /**
     * 解析单个命令
     *
     * @return
     */
    private Command parseCommandStr(String commandStr) {
        // 判空校验
        if (StringUtils.isBlank(commandStr)) {
            return null;
        }
        List<String> commandPartList = Splitter.onPattern(" ").trimResults().omitEmptyStrings().splitToList(commandStr);
        // 命令中是否有 option
        boolean optionFlag = false;
        if (commandPartList.size() > 1) {
            String part1 = commandPartList.get(1);
            // 去除开始符
            if (StringUtils.isNotBlank(part1)) {
                optionFlag = part1.startsWith(CommandConstant.OPTION_START);
            }
        }
        // 命令单元的组成
        String commandName = commandPartList.get(0);
        List<String> options = null;
        List<String> arguments = null;
        // 命令参数起始的位置
        int argumentsStartIndex = 1;
        // 有命令选项时的解析处理
        if (optionFlag) {
            String optionStr = commandPartList.get(1);
            // 去除命令选项开始符
            final String subStr = optionStr.substring(1);
            // 本 commandPart 中，剩下的字符作为命令选项
            options = Stream.iterate(0, n -> n++).limit(subStr.length())
                    .map(n -> String.valueOf(subStr.charAt(n))).collect(Collectors.toList());
            // 命令参数起始位置设为 2
            argumentsStartIndex = 2;
        }
        // List 中剩下的都是 arguments
        if (commandPartList.size() > argumentsStartIndex) {
            arguments = Lists.newArrayList();
            for (int i = argumentsStartIndex; i < commandPartList.size(); i++) {
                arguments.add(commandPartList.get(i));
            }
        }
        // 根据命令名称，获得命令类型
        String commandType = CommandNameEnum.getTypeByName(commandName);
        Command command = null;
        // 根据命令类型，实例化命令单元
        if (CommandTypeEnum.TEXT.getType().equals(commandType)) {
            command = new TextCommand(commandName, options, arguments, commandType);
        } else {
            command = new Command(commandName, options, arguments, commandType);
        }
        return command;
    }

    /**
     * 执行命令
     *
     * @param commandList 命令组合，命令执行顺序为 List 中的顺序
     * @return 输出在控制台中的内容
     */
    private List<String> execute(List<Command> commandList) {
        // 判空校验
        if (CollectionUtils.isEmpty(commandList)) {
            return null;
        }
        // 中间输出
        Object output = null;
        // 执行所有 command
        for (Command command : commandList) {
            command.setInput(output);
            String commandName = command.getName();
            // 去预加载的 executorMap 中寻找 executor 的实例
            Executor executor = executorMap.get(commandName);
            if (executor != null) {
                output = executor.execute(command);
            } else {
                log.error("[命令执行器] 找不到对应的 executor, 中止执行, command: {}, commandList: {}", JSON.toJSONString(command), JSON.toJSONString(commandList));
                break;
            }
        }
        return (List<String>) output;
    }
}

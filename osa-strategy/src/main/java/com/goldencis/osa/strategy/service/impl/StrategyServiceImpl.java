package com.goldencis.osa.strategy.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.Dictionary;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.mapper.UserMapper;
import com.goldencis.osa.core.service.IDictionaryService;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import com.goldencis.osa.strategy.entity.*;
import com.goldencis.osa.strategy.mapper.StrategyCommandMapper;
import com.goldencis.osa.strategy.mapper.StrategyLogintimeMapper;
import com.goldencis.osa.strategy.mapper.StrategyMapper;
import com.goldencis.osa.strategy.service.IFileTypeService;
import com.goldencis.osa.strategy.service.IStrategyCommandService;
import com.goldencis.osa.strategy.service.IStrategyLogintimeService;
import com.goldencis.osa.strategy.service.IStrategyService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.util.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 策略表-定义策略基本信息 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Service
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy> implements IStrategyService {
    @Autowired
    StrategyMapper strategyMapper;

    @Autowired
    StrategyCommandMapper strategyCommandMapper;

    @Autowired
    StrategyLogintimeMapper strategyLogintimeMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IStrategyCommandService strategyCommandService;
    //字典表
    @Autowired
    IDictionaryService dictionaryService;

    //策略登录时间
    @Autowired
    IStrategyLogintimeService strategyLogintimeService;

    //字典表
    @Autowired
    IDictionaryService iDictionaryService;

    //文件类型表
    @Autowired
    IFileTypeService fileTypeService;

    @Override
    @Transactional(readOnly = true)
    public IPage<Strategy> getStrategyInPage(Map<String, String> params) {
        Page page = new Page();

        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        int total = strategyMapper.countStrategyInPage(paramMap);
        List<Strategy> strategies = strategyMapper.getStrategyInPage(paramMap);
        page.setTotal(total);
        page.setRecords(strategies);
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public Strategy findStrategyByGuid(String guid) {
        if (StringUtils.isEmpty(guid)) {
            throw new IllegalArgumentException("策略id不存在");
        }

        Strategy strategy = strategyMapper.findStrategyByGuid(guid);
        if (Objects.isNull(strategy)){
            throw new IllegalArgumentException("策略id不存在");
        }

        //策略命令--阻断会话命令
        List<StrategyCommand>  strategyCommandBlocks = strategyCommandService.findStrategyCommandBlockByStrategyGuid(guid);
        //策略命令--需审核会话命令
        List<StrategyCommand>  strategyCommandPendings = strategyCommandService.findStrategyCommandPendingByStrategyGuid(guid);
        //策略命令--禁止会话命令
        List<StrategyCommand>  strategyCommandProhibits = strategyCommandService.findStrategyCommandProhibitByStrategyGuid(guid);

        if (!ListUtils.isEmpty(strategyCommandBlocks)){
            strategy.setStrategyCommandBlock(strategyCommandBlocks.stream().map(strategyCommand -> strategyCommand.getCommandContent()).collect(Collectors.toList()));
        }
        if (!ListUtils.isEmpty(strategyCommandPendings)){
            strategy.setStrategyCommandPending(strategyCommandPendings.stream().map(strategyCommand -> strategyCommand.getCommandContent()).collect(Collectors.toList()));
        }
        if (!ListUtils.isEmpty(strategyCommandProhibits)){
            strategy.setStrategyCommandProhibit(strategyCommandProhibits.stream().map(strategyCommand -> strategyCommand.getCommandContent()).collect(Collectors.toList()));
        }

        //策略登录时间
        List<StrategyLogintime> strategyLogintimes = strategyLogintimeService.findStrategyLogintimesByStrategyGuid(guid);
        if (!ListUtils.isEmpty(strategyLogintimes)){
            List<StrategyLogintimeWeekday>  strategyLogintimeWeekdays = strategyLogintimes.stream().map(strategyLogintime -> new StrategyLogintimeWeekday(strategyLogintime.getDayType(),strategyLogintime.getStartHourtime(),strategyLogintime.getEndHourtime())).collect(Collectors.toList());
            strategy.setStrategyLoginTime(strategyLogintimeWeekdays);
        }

        List<FileType> fileTypeList = fileTypeService.getEnabledFileTypeList();
        //获取文件审计
        String fileMonStr = strategy.getFileMon();
        if (! TextUtils.isEmpty(fileMonStr)){
            FileMon fileMon = JSON.parseObject(fileMonStr, FileMon.class);
            if ( ! Objects.isNull(fileMon)){
                //设置回显
                if (! TextUtils.isEmpty(fileMon.getContent().getFileExt())){
                    fileTypeList.stream().forEach(fileType -> {
                        if ( Arrays.stream(fileMon.getContent().getFileExt().split(";")).filter(Objects::nonNull).anyMatch(s -> fileType.getName().equals(s))){
                            fileType.setChecked(true);
                        }
                    });
                }
            }
        }
        strategy.setFileMonTree(fileTypeList);
        return strategy;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveStrategy(Strategy strategy) {
        if (Objects.isNull(strategy)) {
            throw new IllegalArgumentException("策略信息为空");
        }

        verifyStrategyData(strategy);
        if (!checkStrategyNameDuplicate(strategy)){
           throw  new IllegalArgumentException("策略名称已存在，不允许重复");
        }
        strategy.setGuid(UUID.randomUUID().toString());
        strategy.setCreateTime(LocalDateTime.now());
        strategy.setCreateBy(SecurityUtils.getCurrentUser().getGuid());
        strategy.setUpdateTime(LocalDateTime.now());
        strategy.setUpdtaeBy(SecurityUtils.getCurrentUser().getGuid());

        //保存控制命令
        saveStrategyCommand(strategy);
        //保存登录管控
        saveStrategyLogintime(strategy);
        this.save(strategy);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void editStrategy(Strategy strategy) {
        if (Objects.isNull(strategy)) {
            throw new IllegalArgumentException("策略信息为空");
        }
        verifyStrategyData(strategy);
        if (baseMapper.selectById(strategy.getGuid()) == null) {
            throw new IllegalArgumentException("策略id不存在");
        }
        if (!checkStrategyNameDuplicate(strategy)){
            throw  new IllegalArgumentException("策略名称已存在，不允许重复");
        }

        strategy.setUpdateTime(LocalDateTime.now());
        strategy.setUpdtaeBy(SecurityUtils.getCurrentUser().getGuid());

        //保存控制命令
        deleteStrategyCommandByStrategyGuid(strategy.getGuid());
        saveStrategyCommand(strategy);
        //保存登录管控
        deleteStrategyLogintimeByStrategyGuid(strategy.getGuid());
        saveStrategyLogintime(strategy);
        this.updateById(strategy);
    }

    /**
     * 通过guid删除策略
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Strategy deleteStrategyByGuid(String strategyGuid) {
        Strategy strategy = baseMapper.selectById(strategyGuid);
        if (Objects.isNull(strategy)) {
            throw new IllegalArgumentException("策略id不存在");
        }

        //设置默认策略
        if ("1".equals(strategyGuid)){
            throw new IllegalArgumentException("默认策略不能被删除");
        }
        //查询策略是否有用户正在使用
        List<User> users = userMapper.findUserByStrategyId(strategyGuid);
        if (!ListUtils.isEmpty(users)) {
            throw new IllegalArgumentException("策略使用中，不能删除");
        }

        //删除指令
        deleteStrategyCommandByStrategyGuid(strategyGuid);
        //删除登录管控
        deleteStrategyLogintimeByStrategyGuid(strategyGuid);
        this.removeById(strategyGuid);
        return strategy;
    }

    /**
     * 通过guids 批量删除策略
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String deleteStrategiesByGuids(List<String> strategyIds) {
        if (ListUtils.isEmpty(strategyIds)) {
            throw new IllegalArgumentException("策略id为空");
        }

        StringBuffer strategyNames = new StringBuffer();
        for (String guid : strategyIds) {
            Strategy strategy = deleteStrategyByGuid(guid);
            strategyNames.append(strategy.getName() + ",");
        }
        strategyNames.deleteCharAt(strategyNames.length() - 1);
        return strategyNames.toString();
    }

    /**
     * 获取所有策略列表
     */
    @Override
    public List<Strategy> getStrategyAll() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    /**
     * 通过策略Guid删除控制命令
     */
    private void deleteStrategyCommandByStrategyGuid(String strategyGuid) {
        if (strategyGuid == null || "".equals(strategyGuid)) {
            throw new IllegalArgumentException("策略id为空");
        }
        strategyCommandMapper.deleteStrategyCommandByStrategyGuid(strategyGuid);
    }

    /**
     * 添加控制命令
     * 命令类型，1阻断会话命令，2待审核命令，3禁止执行命令
     */
    private void saveStrategyCommand(Strategy strategy) {
        if (Objects.isNull(strategy)) {
            throw new IllegalArgumentException("策略信息为空");
        }
        //阻碍命令
        flushStrategyCommand(strategy.getGuid(), ConstantsDto.STRATEGY_COMMAND_BLOCK, strategy.getStrategyCommandBlock());
        //待审核命令
        flushStrategyCommand(strategy.getGuid(), ConstantsDto.STRATEGY_COMMAND_PENDING, strategy.getStrategyCommandPending());
        //禁止命令
        flushStrategyCommand(strategy.getGuid(), ConstantsDto.STRATEGY_COMMAND_PROHIBIT, strategy.getStrategyCommandProhibit());
    }

    /**
     * 刷新数据库中的控制命令
     *
     * @param strategyId  策略id
     * @param type        命令类型
     * @param commandList 命令列表
     */
    private void flushStrategyCommand(String strategyId, int type, @Nullable List<String> commandList) {
        // 清空之前的命令
        strategyCommandService.remove(new QueryWrapper<StrategyCommand>().eq("strategy_id", strategyId).eq("type", type));
        // 如果没有新命令,不用保存
        if (CollectionUtils.isEmpty(commandList)) {
            return;
        }
        // 保存
        List<StrategyCommand> list = commandList.stream()
                .map(s -> {
                    if (StringUtils.isEmpty(s)) {
                        return null;
                    }
                    User currentUser = SecurityUtils.getCurrentUser();
                    return new StrategyCommand(type, s, strategyId, LocalDateTime.now(), Objects.isNull(currentUser) ? null : currentUser.getGuid());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        list.stream().forEach(strategyCommand -> strategyCommandMapper.insert(strategyCommand));
    }

    /**
     * 通过策略Guid删除登录管控
     */
    private void deleteStrategyLogintimeByStrategyGuid(String strategyGuid) {
        if (StringUtils.isEmpty(strategyGuid)) {
            throw new IllegalArgumentException("策略id为空");
        }
        strategyLogintimeMapper.deleteStrategyLogintimeByStrategyGuid(strategyGuid);
    }

    /**
     * 保存登录管控
     * [{"endTime":"13:30","stratTime":"07:30","type":1},{"endTime":"20:30","stratTime":"15:30","type":2},{"type":3},{"type":4},{"type":5},{"endTime":"09:30","stratTime":"08:30","type":6},{"type":7}]
     */
    private void saveStrategyLogintime(Strategy strategy) {
        if (Objects.isNull(strategy)) {
            throw new IllegalArgumentException("策略信息为空");
        }
        if (StringUtils.isEmpty(strategy.getStrategyLoginTime())) {
            return;
        }
        //废弃 7*24  时间条
//        if (strategy.getStrategyLoginTime().length() != 7 * 24) {
//            throw new IllegalArgumentException("登录时间数据不正确");
//        }
//        strategyLogintimeMapper.insert(new StrategyLogintime(strategy.getGuid(), strategy.getStrategyLoginTime(), LocalDateTime.now(), SecurityUtils.getCurrentUser().getGuid()));

        String strategyGuid = strategy.getGuid();
        strategy.getStrategyLoginTime().stream().map(slw -> new StrategyLogintime(strategyGuid, slw.getType(),slw.getStartTime(), slw.getEndTime(), LocalDateTime.now(), SecurityUtils.getCurrentUser().getGuid())).forEach(strategyLogintime -> strategyLogintimeMapper.insert(strategyLogintime));
    }

    /**
     * 当前时间点用户是否可以登录
     * true: 1、没有策略  2、有策略,没有登录时间限制  2、当前时间点可登录
     */
    @Override
    public boolean isUserLoginTime(String username, String datetime) {
        //用户名对应的用户是否存在
        User user = userMapper.findUserByUsername(username);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户名不存在");
        }
        //时间格式是否正确
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            simpleDateFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("时间参参数不对");
        }

        //1、没有策略
        String strategyId = user.getStrategy();
        if (StringUtils.isEmpty(strategyId)) {
            return true;
        }
        //2、有策略,没有登录时间限制
        List<StrategyLogintime> strategyLogintimes = strategyLogintimeMapper.findStrategyLogintimesByStrategyGuid(strategyId);
        if (ListUtils.isEmpty(strategyLogintimes)) {
            return true;
        }
//        String loginTimeZone = strategyLogintime.getTimeZone();
//        if (StringUtils.isEmpty(loginTimeZone)) {
//            return true;
//        }
//        if (StrategyUtils.isLoginTime(loginTimeZone, datetime)) {
//            return true;
//        }

        Integer weekDay = DateUtil.dayForWeek(datetime);
        if (Objects.isNull(weekDay)){
            throw new IllegalArgumentException("星期参数错误");
        }
        System.out.println("星期：--"+weekDay);
        StrategyLogintime strategyLogintime = strategyLogintimeMapper.findStrategyLogointimeByIdAndWeekday(strategyId,weekDay);
        LocalTime startTime = strategyLogintime.getStartHourtime();
        LocalTime endTime = strategyLogintime.getEndHourtime();
        if (startTime == null || endTime == null){
            return  false;
        }
        LocalTime currentTime = DateUtil. strDateTime2LocalTime(datetime);
        System.out.println("时间：startTime-->"+startTime + "；endTime-->" +endTime + "；endTime-->" + endTime);
        if (currentTime.isAfter(startTime) & currentTime.isBefore(endTime)){
            return true;
        }
        return false;
    }

    /**
     * 根据用户名username、指令 确定是何种命令
     * 1、用户没有策略、策略指令为空，返回普通指令 4
     * 2、用户有策略，判断何种指令并返回( 1阻断会话命令，2待审核命令，3禁止执行命令，4 正常、无关指令)
     */
    @Override
    public int getCommandTypeByUsername(String username, String inputCommand) {
        //用户名对应的用户是否存在
        User user = userMapper.findUserByUsername(username);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户名不存在");
        }
        //1、用户没有策略、策略指令为空，返回普通指令 4
        String strategyId = user.getStrategy();
        if (StringUtils.isEmpty(strategyId)) {
            return ConstantsDto.STRATEGY_COMMAND_NORMAL;
        }
        List<StrategyCommand> strategyCommands = strategyCommandMapper.findStrategyCommandsByStrategyGuid(strategyId);
        if (ListUtils.isEmpty(strategyCommands)) {
            return ConstantsDto.STRATEGY_COMMAND_NORMAL;
        }

        List<StrategyCommand>  strategyCommandBlock = strategyCommandService.findStrategyCommandBlockByStrategyGuid(strategyId);
        List<StrategyCommand>  strategyCommandProhibit = strategyCommandService.findStrategyCommandProhibitByStrategyGuid(strategyId);
        List<StrategyCommand>  strategyCommandPending = strategyCommandService.findStrategyCommandPendingByStrategyGuid(strategyId);

        //命令匹配优先级： 阻断命令 > 禁止执行命令 > 需审核命令
        //阻断会话
        if (!ListUtils.isEmpty(strategyCommandBlock)){
            if (strategyCommandBlock.stream().anyMatch(strategyCommand -> commandVerificationRule(strategyCommand, inputCommand))){
                return ConstantsDto.STRATEGY_COMMAND_BLOCK;
            }
        }
        //禁止执行命令
        if (!ListUtils.isEmpty(strategyCommandProhibit)){
            if (strategyCommandProhibit.stream().anyMatch(strategyCommand -> commandVerificationRule(strategyCommand, inputCommand))){
                return ConstantsDto.STRATEGY_COMMAND_PROHIBIT;
            }
        }
        //需审核命令
        if (!ListUtils.isEmpty(strategyCommandPending)){
            if (strategyCommandPending.stream().anyMatch(strategyCommand -> commandVerificationRule(strategyCommand, inputCommand))){
                return ConstantsDto.STRATEGY_COMMAND_PENDING;
            }
        }
        return ConstantsDto.STRATEGY_COMMAND_NORMAL;
    }

    /**
     * 命令验证规则
     */
    private boolean commandVerificationRule(StrategyCommand strategyCommand, String inputCommand) {
        return inputCommand.startsWith(strategyCommand.getCommandContent()) || inputCommand.contains(strategyCommand.getCommandContent());
    }

    @Override
    public List<ProtocolControl> getProtocolControl(String type, String guid) {
        List<Dictionary> dictionaries = dictionaryService.getDictionaryListByType(type);
        if (CollectionUtils.isEmpty(dictionaries)) {
            return new ArrayList<>(1);
        }
        Strategy strategy = null;
        if (!StringUtils.isEmpty(guid)) {
            strategy = this.getOne(new QueryWrapper<Strategy>().eq("guid", guid));
        }
        Strategy finalStrategy = strategy;
        return dictionaries.stream().map(new Function<Dictionary, ProtocolControl>() {
            @Override
            public ProtocolControl apply(Dictionary dictionary) {
                ProtocolControl control = new ProtocolControl(dictionary);
                if (Objects.isNull(finalStrategy)) {
                    return control;
                }
                String value = null;
                switch (control.getType()) {
                    case "SSH":
                        value = finalStrategy.getSsh();
                        break;
                    case "RDP":
                        value = finalStrategy.getRdp();
                        break;
                    case "HHLX":
                        value = finalStrategy.getSessionType();
                        break;
                    default:
                        break;
                }
                if (!StringUtils.isEmpty(value)) {
                    if (value.contains(control.getValue().toString())) {
                        control.setChecked(true);
                    }
                }
                return control;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public JSONObject findPolicyByGuid(String guid) {
        JSONObject result = new JSONObject();
        if (StringUtils.isEmpty(guid)){
            result.put("msg", "policyGuid can not be null!");
            return result;
        }

        Strategy strategy = baseMapper.selectById(guid);
        if (Objects.isNull(strategy)){
            result.put("msg", "policy does not exist!");
            return result;
        }

        String screenWatermark = strategy.getScreenWatermark();
        result.put("screenWatermark",JSON.parse(screenWatermark));
        String fileMon = strategy.getFileMon();
        result.put("fileMon",JSON.parse(fileMon));

//        if (!TextUtils.isEmpty(fileMon)){
//         String  fileMonStr =  Arrays.stream(fileMon.split(",")).filter(Objects::nonNull).map(new Function<String, String>() {
//                @Override
//                public String apply(String s) {
//                    return fileTypeService.getById(Integer.parseInt(s)).getName();
//                }
//            }).collect(Collectors.toList()).stream().map(w->w.toString()).collect(Collectors.joining(";"));
//            result.put("fileMon",fileMonStr);
//        }

        return result;
    }


    /**
     * 判断是否存在相同的 策略名字
     * false : 不可用
     * true : 可用
     */
    public boolean checkStrategyNameDuplicate(Strategy strategy) {
        Strategy preStrategy = strategyMapper.selectOne(new QueryWrapper<Strategy>().eq("name",strategy.getName()));
        //判断数据库是否有该记录，不存在即可用，返回true，如果有继续判断
        if (! Objects.isNull( preStrategy) ){
            //比较两个对象的id，若一致，是同一个对象没有改变名称的情况，返回可用true。
            if (preStrategy.getGuid().equals(strategy.getGuid())) {
                return true;
            }
            //若果不同，说明为两个用户，名称重复，不可用，返回false
            return false;
        }
        return true;
    }

    /**
     * 保存数据验证
     */
    void verifyStrategyData(Strategy strategy) {
        if (! com.goldencis.osa.common.utils.StringUtils.isInLength(strategy.getName(),30)){
            throw new IllegalArgumentException("策略名称最大长度为30");
        }
    }
}

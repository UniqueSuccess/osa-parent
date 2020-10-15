package com.goldencis.osa.asset.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.approval.entity.ApprovalDefinition;
import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;
import com.goldencis.osa.approval.handle.ApprovalType;
import com.goldencis.osa.approval.mapper.ApprovalDefinitionMapper;
import com.goldencis.osa.approval.mapper.ApprovalFlowInfoGrantedMapper;
import com.goldencis.osa.approval.service.IApprovalFlowService;
import com.goldencis.osa.asset.domain.GrantDetail;
import com.goldencis.osa.asset.domain.GrantDetailParam;
import com.goldencis.osa.asset.entity.*;
import com.goldencis.osa.asset.mapper.*;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetgroupService;
import com.goldencis.osa.asset.service.IGrantedService;
import com.goldencis.osa.asset.util.AssetConstans;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.SpringUtil;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.entity.Usergroup;
import com.goldencis.osa.core.mapper.UserMapper;
import com.goldencis.osa.core.mapper.UsergroupMapper;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.SetUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 设备授权表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-05
 */
@Service
public class GrantedServiceImpl extends ServiceImpl<GrantedMapper, Granted> implements IGrantedService {

    private final Logger logger = LoggerFactory.getLogger(GrantedServiceImpl.class);

    @Autowired
    GrantedMapper grantedMapper;

    @Autowired
    AssetgroupMapper assetgroupMapper;

    @Autowired
    AssetAccountMapper assetAccountMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UsergroupMapper usergroupMapper;

    @Autowired
    IApprovalFlowService approvalFlowService;

    @Autowired
    ApprovalDefinitionMapper approvalDefinitionMapper;

    @Autowired
    ApprovalFlowInfoGrantedMapper approvalFlowInfoGrantedMapper;

    @Autowired
    AssetMapper assetMapper;

    @Autowired
    AssetAssetgroupMapper assetAssetgroupMapper;
    @Autowired
    private IAssetgroupService assetgroupService;

    //使用时动态加载，SpringUtil.getBean加载
    IAssetService assetService;

    //设置目标设备数量（审批中用到）
    int relationAssetNum = 0;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String grantedAsset4User(String userJson, String grantedJson) {
        //解析设备组/设备账号
        List<Granted> granteds = this.analysisGranteds(grantedJson);
        //解析用户（组），并保存授权
        this.analysisUsers(granteds, userJson);
        //添加授权申请 审批
        saveApprovalFlowGranted(granteds);

        StringBuffer grantedContent = new StringBuffer();
        granteds.stream().forEach(granted -> grantedContent.append(getGrantedContentLog(granted) + "\n"));
        grantedContent.deleteCharAt(grantedContent.length() - 1);
        return grantedContent.toString();
    }

    /**
     * 根据用户guid获取设备账号ids
     *
     * @param guid 用户guid或者用户组guid
     * @param isUserGroup 是否是用户组
     */
    @Override
    public List<String> findAccountIdsByUserGuid(String guid, boolean isUserGroup) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(isUserGroup ? "usergroupGuid" : "userGuid", guid);
        paramMap.put("grantedType", "" + ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT);
        setGrantedListByStatusFilterMap(paramMap);
        return grantedMapper.findAccountIdsByUserGuid(paramMap);
    }

    /**
     * 根据用户guid获取设备组ids
     *
     * @param guid 用户guid或者用户组guid
     * @param isUserGroup 是否是用户组
     */
    @Override
    public List<String> findAssetgroupIdsByUserGuid(String guid, boolean isUserGroup) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(isUserGroup ? "usergroupGuid" : "userGuid", guid);
        paramMap.put("grantedType", "" + ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP);
        setGrantedListByStatusFilterMap(paramMap);
        List<String> groupList = grantedMapper.findAssetgroupIdsByUserGuid(paramMap);
        return groupList;
//        return ListUtils.isEmpty(groupList) ? null : groupList.stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * 获取授权设备账号列表
     */
    @Override
    public IPage<Granted> getAssestAccountsInPage(Map<String, String> params) {
        Page page = new Page();

        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        if ((!StringUtils.isEmpty(params.get("userGuid"))) && (!StringUtils.isEmpty(params.get("usergroupId")))) {
            throw new IllegalArgumentException("用户id、用户组id只能传一个");
        }
        paramMap.put("grantedType", ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT);

        setGrantedListByStatusFilterMap(paramMap);

        //统计用户总数量
        int total = grantedMapper.countAssestAccountsInPage(paramMap);
        //带参数的分页查询
        List<Granted> grantedAssestAccountsList = grantedMapper.getAssestAccountsInPage(paramMap);
        page.setTotal(total);
        page.setRecords(grantedAssestAccountsList);
        return page;
    }

    /**
     * 设置 列表展示的 审批类型
     * 只显示 待审批、已授权
     */
    void setGrantedListByStatusFilterMap(Map<String, Object> paramMap) {
        Set<Integer> statusSets = new HashSet<>();
        statusSets.add(ConstantsDto.APPROVAL_AUTHORIZED);
        statusSets.add(ConstantsDto.APPROVAL_PENDING);
        paramMap.put("statusSets", statusSets);
    }

    @Override
    public IPage<Granted> getAssestgroupsInPage(Map<String, String> params) {
        Page page = new Page();

        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        if ((!StringUtils.isEmpty(params.get("userGuid"))) && (!StringUtils.isEmpty(params.get("usergroupId")))) {
            throw new IllegalArgumentException("用户id、用户组id只能传一个");
        }
        paramMap.put("grantedType", ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP);
        setGrantedListByStatusFilterMap(paramMap);
        //统计用户总数量
        int total = grantedMapper.countAssestgroupsInPage(paramMap);
        //带参数的分页查询
        List<Granted> grantedAssestAccountsList = grantedMapper.getAssestgroupsInPage(paramMap);
        page.setTotal(total);
        page.setRecords(grantedAssestAccountsList);
        return page;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String deleteGrantedById(Integer grantedId) {
        Granted granted = baseMapper.selectById(grantedId);
        if (granted == null) {
            throw new IllegalArgumentException("授权id不存在");
        }
        if (ConstantsDto.APPROVAL_PENDING == granted.getStatus()) {
            throw new IllegalArgumentException("该授权处于待审批状态，请等待审批之后再操作");
        }
        //保存删除授权审批，1 更改审批状态为待审批； 2 设置是否删除Isdelete为 删除(1)；
        saveApprovalFlowGrantedDeleted(granted);
        granted.setStatus(ConstantsDto.APPROVAL_PENDING);
        granted.setIsdelete(1);
        this.updateById(granted);

        String grantedContent = getGrantedContentLog(granted);
        return grantedContent;
    }

    /**
     * 获取授权日志
     *
     * @param granted 授权
     * @return
     */
    String getGrantedContentLog(Granted granted) {
        String grantedContent = "";
        if (ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP == granted.getType()) {
            Assetgroup assetgroup = assetgroupMapper.selectById(granted.getAssetgroupId());
            grantedContent = (Objects.isNull(assetgroup)) ? "设备组不存在" : "设备组：" +assetgroup.getName();
        } else if (ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT == granted.getType()) {
            Asset asset = assetMapper.selectById(granted.getAssetId());
            AssetAccount assetAccount = assetAccountMapper.selectById(granted.getAccountId());
            grantedContent = ((Objects.isNull(asset)) ? "设备不存在" : "设备：" + asset.getName() ) + ((Objects.isNull(assetAccount)) ?  "; 设备账号不存在" :  "; 设备账号：" + assetAccount.getUsername());
        }
        grantedContent += (Objects.isNull(granted.getUsergroupId()) ? "; 被授权用户：" + userMapper.selectById(granted.getUserId()).getUsername() : "; 被授权用户组：" + usergroupMapper.selectById(granted.getUsergroupId()).getName());
        return grantedContent;
    }

    @Override
    public IPage<GrantedSignUser> getGrantedsByCurrentUser4SSOInPage(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        IPage<GrantedSignUser> page = new Page<>();
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        List<GrantedSignUser> grantedSignUsers = new ArrayList<>();

        String userGuid = SecurityUtils.getCurrentUser().getGuid();
        int total = 0;
        Set<Integer> accountIds = getUserAllAccountIdsByUserGuid(userGuid);
        if (accountIds.size() > 0) {
            paramMap.put("pageParams", params);
            paramMap.put("accoutIdsSet", accountIds);

            String assetTypeIds = (String) paramMap.get("assetTypeIds");
            if (!StringUtils.isEmpty(assetTypeIds)) {
                List<Integer> assetTypeIdes = Arrays.stream(assetTypeIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
                paramMap.put("assetTypeIdsList", assetTypeIdes);
            }
            total = grantedMapper.countGrantedSignUserInPage(paramMap);
            grantedSignUsers = grantedMapper.getGrantedSignUserInPage(paramMap);
        }
        page.setTotal(total);
        page.setRecords(grantedSignUsers);
        return page;
    }

    @Override
    public boolean checkCurrentUser4AssetAccout(Integer assetId, Integer assetAccountId) {
        String userGuid = SecurityUtils.getCurrentUser().getGuid();
        Set<Integer> accountIds = getUserAllAccountIdsByUserGuid(userGuid);
        if (!SetUtils.isEmpty(accountIds)) {
            Map<String, Object> map = new HashMap<>();
            map.put("assetId", assetId);
            map.put("assetAccountId", assetAccountId);
            map.put("accoutIdsSet", accountIds);
            int count = grantedMapper.checkUser4AssetAccout(map);
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String deleteGrantedAssetByAssetId(Integer id) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new IllegalArgumentException("设备不存在,id : " + id);
        }
        // 在用的应用程序发布器,不可删除
        if (Objects.isNull(assetService)) {
            assetService = SpringUtil.getBean(AssetServiceImpl.class);
        }
        if (assetService.checkPublish(asset.getId())) {
            throw new IllegalArgumentException("在用的应用程序发布器不可删除");
        }

        //是否有正在进行的审批
        if (approvalFlowService.isInApprovalByAssetIdAndApprovalType(id, null)) {
            throw new IllegalArgumentException("该设备存在审批流程，请等待流程结束后再操作");
        }
        // 存在授权的设备不允许删除
        // 这个地方存在一个问题,如果授权被拒绝,设备也不可删除
        List<Granted> granteds = grantedMapper.selectList(new QueryWrapper<Granted>().eq("asset_id", id));
        if (!ListUtils.isEmpty(granteds)) {
            //保存删除含授权的设备 审批
            saveApprovalFlowGrantedListDeleted(granteds);
        }
        return asset.getName();
    }

    @Override
    public void applyForDeleteAssetAccount(List<Integer> accountIdList) {
        if (CollectionUtils.isEmpty(accountIdList)) {
            return;
        }
        List<Granted> list = accountIdList.stream()
                .filter(Objects::nonNull)
                .map(accountId -> assetAccountMapper.selectById(accountId))
                .filter(account -> {
                    if (Objects.isNull(account)) {
                        logger.warn("设备账号不存在,不能提交删除的申请");
                        return false;
                    }
                    Integer accountId = account.getId();
                    if (approvalFlowService.isInApprovalByAssetIdAndApprovalType(account.getAssetId(), null)) {
                        logger.warn("该设备存在审批流程，请等待流程结束后再操作,assetId:{};accountId:{}", account.getAssetId(), accountId);
                        return false;
                    }
                    if (approvalFlowService.isInApprovalByAssetAccountIdAndApprovalType(accountId, null)) {
                        logger.warn("该账号存在审批流程，请等待流程结束后再操作,assetId:{};accountId:{}", account.getAssetId(), accountId);
                        return false;
                    }
                    return true;
                })
                .flatMap((Function<AssetAccount, Stream<Granted>>) account -> grantedMapper.selectList(new QueryWrapper<Granted>().eq("account_id", account.getId())).stream())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            logger.debug("授权列表为空?不应该走这行代码");
            return;
        }
        ApprovalDefinition approvalDefinition = approvalDefinitionMapper.selectById(ConstantsDto.APPROVAL_GRANTED_DELETE_ACCOUNT);
        if (Objects.isNull(approvalDefinition)) {
            logger.warn("审批授权类型不存在");
            return;
        }
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            logger.warn("获取当前登录用户失败");
            return;
        }
        String flowId = UUID.randomUUID().toString();
        //审批主表
        ApprovalFlow approvalFlow = new ApprovalFlow(flowId, approvalDefinition.getId(), approvalDefinition.getName(), 1, user.getGuid(), user.getUsername(), user.getName(), user.getUsername() + "--申请删除含授权的设备账号", LocalDateTime.now());
        //批量保存删除授权
        List<ApprovalFlowInfoGranted> collect = list.stream().map(granted -> {
            ApprovalFlowInfoGranted singleGranted = saveSingleGranted(approvalFlow, granted);
            granted.setStatus(ConstantsDto.APPROVAL_PENDING);
            granted.setIsdelete(1);
            grantedMapper.updateById(granted);
            return singleGranted;
        }).collect(Collectors.toList());
        approvalFlow.setApprovalFlowInfoGrantedList(collect);
        approvalFlow.setGrantedMethod(ConstantsDto.GRANTEDMETHOD_DELETE_ACCOUNT);
        approvalFlow.setRelationNum(1);
        approvalFlowService.saveApprovalFlowGranted(approvalFlow);
    }

    @Override
    public void applyForDeleteAssetGroup(@NotNull List<Integer> assetGroupIdList) {
        if (CollectionUtils.isEmpty(assetGroupIdList)) {
            return;
        }
        List<Granted> list = assetGroupIdList.stream()
                .filter(Objects::nonNull)
                .map(assetgroupService::getById)
                .filter(assetgroup -> {
                    if (Objects.isNull(assetgroup)) {
                        return false;
                    }
                    Integer assetgroupId = assetgroup.getId();
                    if (approvalFlowService.isInApprovalByAssetGroupIdAndApprovalType(assetgroupId, null)) {
                        logger.warn("该设备存在审批流程，请等待流程结束后再操作,assetgroupId:{}", assetgroupId);
                        return false;
                    }
                    return true;
                })
                .flatMap((Function<Assetgroup, Stream<Granted>>) assetgroup -> list(new QueryWrapper<Granted>().eq("assetgroup_id", assetgroup.getId())).stream())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            logger.debug("授权列表为空?不应该走这行代码");
            return;
        }
        ApprovalDefinition approvalDefinition = approvalDefinitionMapper.selectById(ApprovalType.DELETE_ASSET_GROUP.getMark());
        if (Objects.isNull(approvalDefinition)) {
            logger.warn("审批授权类型不存在");
            return;
        }
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            logger.warn("获取当前登录用户失败");
            return;
        }
        String flowId = UUID.randomUUID().toString();
        ApprovalFlow approvalFlow = new ApprovalFlow(flowId,
                approvalDefinition.getId(),
                approvalDefinition.getName(),
                1,
                user.getGuid(),
                user.getUsername(),
                user.getName(),
                user.getUsername() + "--申请删除含授权的设备组",
                LocalDateTime.now());
        //批量保存删除授权
        List<ApprovalFlowInfoGranted> collect = list.stream().map(granted -> {
            ApprovalFlowInfoGranted singleGranted = saveSingleGranted(approvalFlow, granted);
            granted.setStatus(ConstantsDto.APPROVAL_PENDING);
            granted.setIsdelete(1);
            grantedMapper.updateById(granted);
            return singleGranted;
        }).collect(Collectors.toList());
        approvalFlow.setApprovalFlowInfoGrantedList(collect);
        approvalFlow.setGrantedMethod(ConstantsDto.GRANTEDMETHOD_DELETE_ASSETGROUP);
        approvalFlow.setRelationNum(0);
        approvalFlowService.saveApprovalFlowGranted(approvalFlow);
    }

    /**
     * 根据用户id和授权状态获取授权集合
     *
     * @param userId 用户id
     * @param status 授权状态
     * @return 授权集合
     */
    @Override
    public List<Granted> findGrantedListByUserIdAndStatus(String userId, int status) {
        return baseMapper.findGrantedListByUserIdAndStatus(userId, status);
    }


    @Override
    public IPage<GrantDetail> getGrantDetailInPage(GrantDetailParam param) {
        List<GrantDetail> list = baseMapper.getGrantDetailInPage(param);
        Integer count = this.getGrantDetailCount(param);
        IPage<GrantDetail> page = new Page<>();
        page.setRecords(list);
        page.setTotal(count);
        return page;
    }

    /**
     * 获取授权详情数量
     *
     * @param param
     * @return
     */
    @Override
    public Integer getGrantDetailCount(GrantDetailParam param) {
        return baseMapper.getGrantDetailCount(param);
    }

    @Override
    public List<Integer> getAssetIdsByOperator(String guid) {
        return grantedMapper.getAssetIdsByOperator(guid);
    }

    @Override
    public List<Integer> getGroupIdsByOperator(String guid) {
        return grantedMapper.getGroupIdsByOperator(guid);
    }

    /**
     * 撤销授权
     *
     * @param grantedId 授权id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String revokeGrantedById(Integer grantedId) {
        if (Objects.isNull(grantedId)) {
            throw new IllegalArgumentException("授权id为空");
        }
        Granted granted = baseMapper.selectById(grantedId);
        if (granted == null) {
            throw new IllegalArgumentException("授权id不存在");
        }
        //只有待审批 才可以撤回
        if (ConstantsDto.APPROVAL_PENDING != granted.getStatus()) {
            throw new IllegalArgumentException("该授权不处于待审批状态，不能撤回");
        }

        //根据授权id 获取 审批对应的相关授权集合
        Set<Integer> grantedIds = approvalFlowService.getGrantedIdsFromFlowByGrantedId(grantedId);
        if (SetUtils.isEmpty(grantedIds)){
            throw new IllegalArgumentException("该授权没有审批记录，不能撤回");
        }
        //撤销日志
        StringBuffer grantedContent = new StringBuffer();
        grantedIds.stream().map(granted12 -> grantedMapper.selectById(granted12))
                .forEach(granted13 -> grantedContent.append(getGrantedContentLog(granted13) + "\n"));
        grantedContent.deleteCharAt(grantedContent.length() - 1);

        //isdelete是否删除：0默认不删除；1 删除
        int isDelete = granted.getIsdelete();
        if (isDelete == 0) {
            //待审批-- 添加  删除授权记录
            grantedIds.stream().forEach(grantedId1 -> grantedMapper.deleteById(grantedId1));
        } else if (isDelete == 1) {
            //待审批 -- 删除  修改授权记录状态为已授权
            grantedIds.stream()
                    .map(grantedId12 -> grantedMapper.selectById(grantedId12))
                    .forEach(granted1 -> {
                        granted1.setIsdelete(0);
                        granted1.setStatus(ConstantsDto.APPROVAL_AUTHORIZED);
                        grantedMapper.updateById(granted1);
                    });
        }
        //删除待审批的授权记录
        approvalFlowService.deleteApprovalFlowByGrantedId(grantedId);
        return grantedContent.toString();
    }

    /**
     * 获取用户所有设备账号的集合
     */
    private Set<Integer> getUserAllAccountIdsByUserGuid(String userGuid) {
        Map<String, String> map = new HashMap<>();
        map.put("userGuid", userGuid);
        map.put("grantedTypeAssetAccount", String.valueOf(ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT));
        map.put("grantedTypeAssetGroup", String.valueOf(ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP));
        map.put("grantedStatus", String.valueOf(ConstantsDto.APPROVAL_AUTHORIZED));

        //获取用户所有账号id
        Set<Integer> accountIds = new HashSet<>();

        //1、设备id、设备账号【授权表】
        Set<Granted> grantedSet = new HashSet<>();

        //1）根据用户guid获取授权设备账号资源
        List<Granted> userGrantedAssestAccounts = grantedMapper.getUserGrantedAssestAccounts(map);
        userGrantedAssestAccounts.stream().forEach(granted -> grantedSet.add(granted));

        //2）根据用户guid 获取用户组  授权设备账号
        List<Granted> usergroupGrantedAssestAccounts = grantedMapper.getUsergroupGrantedAssestAccounts(map);
        usergroupGrantedAssestAccounts.stream().forEach(granted -> grantedSet.add(granted));

        //根据授权id 转译 设备账号
        grantedSet.stream().forEach(granted -> {
            accountIds.add(granted.getAccountId());
        });

        //1、设备组 转 设备id【设备表】
        Set<Asset> assetSet = new HashSet<>();

        //3）根据用户guid 获取授权设备组的设备
        List<Asset> userGrantedAssets = grantedMapper.getUserGrantedAssestgroups(map);
        userGrantedAssets.stream().forEach(asset -> assetSet.add(asset));

        //4）根据用户guid 获取用户组  授权设备组的设备
        List<Asset> usergroupGrantedAssest = grantedMapper.getUsergroupGrantedAssestgroups(map);
        usergroupGrantedAssest.stream().forEach(asset -> assetSet.add(asset));

        //根据设备id 转译 单点登录
        assetSet.stream().forEach(asset -> {
            List<Integer> accountIdsByAssetId = grantedMapper.getAccountIdsByAssetId(asset.getId());
            accountIdsByAssetId.stream().forEach(accoutId -> accountIds.add(accoutId));
        });
        return accountIds;
    }

    //region start -- grantedAsset4User 用户、用户组 授权设备、设备组-------------------------------------------------------------------------------------------

    /**
     * 解析授权设备信息
     * {"grantedType": 1,"assetgroups": "","assetaccounts": "asset-3,asset-4"}
     */
    private List<Granted> analysisGranteds(String grantedJson) {
        if (grantedJson == null || "".equals(grantedJson)) {
            throw new IllegalArgumentException("授权设备（组）为空");
        }
        List<Granted> granteds = new ArrayList<>();

        AnalysisGrantedEntity analysisUserEntity = JSON.parseObject(grantedJson, AnalysisGrantedEntity.class);
        if (ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP == analysisUserEntity.getGrantedType()) {
            //设备组
            granteds = grantedAssetGroup(ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP, analysisUserEntity.getAssetgroups());
        } else if (ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT == analysisUserEntity.getGrantedType()) {
            //设备账号
            granteds = grantedAssetAccounts(ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT, analysisUserEntity.getAssetaccounts());
        }
        return granteds;
    }

    /**
     * 解析设备账号
     * {"grantedType": 3,"assetgroups": "","assetaccounts": "account-29,account-30"}
     */
    private List<Granted> grantedAssetAccounts(int grantedType, String assetAccounts) {
        if (assetAccounts == null || "".equals(assetAccounts)) {
            throw new IllegalArgumentException("设备账号不能为空");
        }
        List<Granted> granteds = new ArrayList<>();
        //1、去掉 设备账号 拼接前缀；2、筛选 设备账号id不为空情况； 3、1）判断设备账号存不存在 2）添加到集合
        Arrays.stream(assetAccounts.split(","))
                .map(s -> {
                    s = s.replace(AssetConstans.PREFIX_ACCOUNT, "");
                    return StringUtils.isEmpty(s) ? null : Integer.parseInt(s);
                })
                .filter(assetAccountId -> assetAccountId != null)
                .forEach(assetAccountId -> {
                    AssetAccount assetAccount = assetAccountMapper.selectById(assetAccountId);
                    if (Objects.isNull(assetAccount)) {
                        throw new IllegalArgumentException("设备账号不存在");
                    }
                    granteds.add(new Granted(grantedType, assetAccount.getAssetId(), assetAccountId));
                });
        return granteds;
    }

    /**
     * 解析授权设备组
     * {"grantedType": 1,"assetgroups": "group-1,group-7","assetaccounts": ""} 英文,分割
     */
    private List<Granted> grantedAssetGroup(int grantedType, String assetgroups) {
        if (StringUtils.isEmpty(assetgroups)) {
            throw new IllegalArgumentException("设备组为空");
        }
        List<Granted> granteds = new ArrayList<>();
        //1、去掉 设备组 拼接前缀；2、筛选 设备组id不为空情况； 3、1）判断设备组存不存在 2）添加到集合
        Arrays.stream(assetgroups.split(","))
                // 将值为null的,或者虚拟的节点,过滤掉
                .filter(s -> !StringUtils.isEmpty(s) && !s.endsWith(ConstantsDto.NIHILITY))
                .map(s -> {
                    s = s.replace(AssetConstans.PREFIX_GROUP, "");
                    return StringUtils.isEmpty(s) ? null : Integer.parseInt(s);
                })
                .filter(assetgroupId -> assetgroupId != null)
                .forEach(assetgroupId -> {
                    if (assetgroupMapper.selectById(assetgroupId) == null) {
                        throw new IllegalArgumentException("设备组不存在");
                    }
                    granteds.add(new Granted(grantedType, assetgroupId));
                });
        return granteds;
    }

    /**
     * 解析授权用户
     * {"userType": 1,"userId": "","usergroupId": ""}
     */
    private void analysisUsers(List<Granted> granteds, String userJson) {
        if (ListUtils.isEmpty(granteds)) {
            throw new IllegalArgumentException("授权设备（组）为空");
        }
        if (StringUtils.isEmpty(userJson)) {
            throw new IllegalArgumentException("用户（组）为空");
        }


        AnalysisUserEntity analysisUserEntity = JSON.parseObject(userJson, AnalysisUserEntity.class);
        if (ConstantsDto.GRANTED_USERTYPE_USER == analysisUserEntity.getUserType()) {
            //用户
            if (userMapper.selectById(analysisUserEntity.getUserId()) == null) {
                throw new IllegalArgumentException("用户id不存在");
            }
            granteds.stream().forEach(granted -> {
                granted.setUserId(analysisUserEntity.getUserId());
                checkGrantedExits(granted);
                saveGranted(granted);
            });
        } else if (ConstantsDto.GRANTED_USERTYPE_USERGROUP == analysisUserEntity.getUserType()) {
            //用户组 1、用户组id传参String,数据库为int ，需转换之后再判断
            Integer usergroupId = null;
            try {
                usergroupId = Integer.parseInt(analysisUserEntity.getUsergroupId());
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("用户组id不存在");
            }
            if (usergroupMapper.selectById(usergroupId) == null) {
                throw new IllegalArgumentException("用户组id不存在");
            }
            granteds.stream().forEach(granted -> {
                granted.setUsergroupId(Integer.parseInt(analysisUserEntity.getUsergroupId()));
                checkGrantedExits(granted);
                saveGranted(granted);
            });
        }

    }

    /**
     * 判断授权是否存在
     */
    private void checkGrantedExits(Granted granted) {
        Map<String, Object> map = new HashMap<>();
        if (granted.getUserId() != null) {
            map.put("userGuid", granted.getUserId());
        }
        if (granted.getUsergroupId() != null) {
            map.put("usergroupId", String.valueOf(granted.getUsergroupId()));
        }
        if (granted.getAccountId() != null) {
            map.put("assetId", granted.getAssetId());
            map.put("accountId", granted.getAccountId());
            map.put("grantedType", ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT);
        } else {
            map.put("assetgroupId", granted.getAssetgroupId());
            map.put("grantedType", ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP);
        }
        //过滤类型  只保留待审批、已授权
        setGrantedListByStatusFilterMap(map);
        Granted granted1 = grantedMapper.checkAccountExist(map);
        if (!Objects.isNull(granted1)) {
            throw new IllegalArgumentException("授权设备(组)信息已存在");
        }
    }

    /**
     * 保存授权信息
     */
    private void saveGranted(Granted granted) {
        //添加授权 1、设置是否删除Isdelete 为0（不删除）； 2 设置审批状态为 待审批；
        granted.setIsdelete(0);
        granted.setStatus(ConstantsDto.APPROVAL_PENDING);
        granted.setCreateBy(SecurityUtils.getCurrentUser().getGuid());
        granted.setCreateTime(LocalDateTime.now());
        baseMapper.insert(granted);
    }

    /**
     * 批量保存审批数据
     */
    private void saveApprovalFlowGranted(List<Granted> granteds) {
        ApprovalDefinition approvalDefinition = approvalDefinitionMapper.selectById(ConstantsDto.APPROVAL_GRANTED_ADD);
        if (Objects.isNull(approvalDefinition)) {
            throw new IllegalArgumentException("审批授权类型不存在");
        }
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("操作员不存在");
        }
        if (ListUtils.isEmpty(granteds)) {
            throw new IllegalArgumentException("授权数据为空");
        }
        //保存审批
        String flowId = UUID.randomUUID().toString();
        //1、审批主表
        ApprovalFlow approvalFlow = new ApprovalFlow(flowId, approvalDefinition.getId(), approvalDefinition.getName(), 1, user.getGuid(), user.getUsername(), user.getName(), user.getUsername() + "--申请添加授权", LocalDateTime.now());
        //2、审批授权从表
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = new ArrayList<>();
        granteds.stream().forEach(granted -> {
            approvalFlowInfoGranteds.add(saveSingleGranted(approvalFlow, granted));
        });
        approvalFlow.setApprovalFlowInfoGrantedList(approvalFlowInfoGranteds);
        //3、审批主表 设置目标设备数量
        setRalationAssetNum(approvalFlow, approvalFlowInfoGranteds);
        approvalFlow.setRelationNum(relationAssetNum);
        approvalFlowService.saveApprovalFlowGranted(approvalFlow);
    }
    //region end -- grantedAsset4User 用户、用户组 授权设备、设备组-------------------------------------------------------------------------------------------

    //region start --  删除授权 提出审批申请------------------------------------------------------------------------------------------------------------------

    /**
     * 保存删除授权审批
     */
    private void saveApprovalFlowGrantedDeleted(Granted granted) {
        ApprovalDefinition approvalDefinition = approvalDefinitionMapper.selectById(ConstantsDto.APPROVAL_GRANTED_DELETE);
        if (Objects.isNull(approvalDefinition)) {
            throw new IllegalArgumentException("审批授权类型不存在");
        }
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("操作员不存在");
        }
        String flowId = UUID.randomUUID().toString();
        //审批主表
        ApprovalFlow approvalFlow = new ApprovalFlow(flowId, approvalDefinition.getId(), approvalDefinition.getName(), 1, user.getGuid(), user.getUsername(), user.getName(), user.getUsername() + "--申请删除授权", LocalDateTime.now());
        //设置授权方法
        approvalFlow.setGrantedMethod(getGrantedMethod(granted));
        //设置审批从表
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = new ArrayList<>();
        approvalFlowInfoGranteds.add(saveSingleGranted(approvalFlow, granted));
        approvalFlow.setApprovalFlowInfoGrantedList(approvalFlowInfoGranteds);

        //设置目标设备数量
        setRalationAssetNum(approvalFlow, approvalFlowInfoGranteds);
        //设置主表目标设备数量
        approvalFlow.setRelationNum(relationAssetNum);
        approvalFlowService.saveApprovalFlowGranted(approvalFlow);
    }

    /**
     * 根据 审批从表，获取 目标设备数量
     *
     * @param approvalFlow             审批主表
     * @param approvalFlowInfoGranteds 审批从表
     */
    private void setRalationAssetNum(ApprovalFlow approvalFlow, List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds) {
        relationAssetNum = 0;
        switch (approvalFlow.getGrantedMethod()) {
            case ConstantsDto.GRANTEDMETHOD_USER_ASSET:
            case ConstantsDto.GRANTEDMETHOD_USERGROUP_ASSET:
                relationAssetNum = approvalFlowInfoGranteds.stream().map(approvalFlowInfoGranted -> approvalFlowInfoGranted.getAssetId()).collect(Collectors.toSet()).size();
                break;
            case ConstantsDto.GRANTEDMETHOD_USER_ASSETGROUP:
            case ConstantsDto.GRANTEDMETHOD_USERGROUP_ASSETGROUP:
                approvalFlowInfoGranteds.stream().map(approvalFlowInfoGranted -> approvalFlowInfoGranted.getAssetgroupId()).forEach(assetgroupId -> {
                    relationAssetNum += assetAssetgroupMapper.selectList(new QueryWrapper<AssetAssetgroup>().eq("assetgroup_id", assetgroupId)).size();
                });
                break;
        }
    }

    /**
     * 根据授权信息获取类型
     *
     * @param granted 授权信息
     */
    private int getGrantedMethod(Granted granted) {
        int grantedMethod = -1;
        //设备组
        if (ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP == granted.getType()) {
            if (!Objects.isNull(granted.getUserId())) {
                grantedMethod = ConstantsDto.GRANTEDMETHOD_USER_ASSETGROUP;
            } else {
                grantedMethod = ConstantsDto.GRANTEDMETHOD_USERGROUP_ASSETGROUP;
            }
        } else if (ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT == granted.getType()) {
            if (!Objects.isNull(granted.getUserId())) {
                grantedMethod = ConstantsDto.GRANTEDMETHOD_USER_ASSET;
            } else {
                grantedMethod = ConstantsDto.GRANTEDMETHOD_USERGROUP_ASSET;
            }
        }
        return grantedMethod;
    }

    /**
     * 保存删除含授权的设备 审批
     */
    private void saveApprovalFlowGrantedListDeleted(List<Granted> granteds) {
        ApprovalDefinition approvalDefinition = approvalDefinitionMapper.selectById(ConstantsDto.APPROVAL_GRANTED_DELETE_ASSET);
        if (Objects.isNull(approvalDefinition)) {
            throw new IllegalArgumentException("审批授权类型不存在");
        }
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("操作员不存在");
        }
        String flowId = UUID.randomUUID().toString();
        //审批主表
        ApprovalFlow approvalFlow = new ApprovalFlow(flowId, approvalDefinition.getId(), approvalDefinition.getName(), 1, user.getGuid(), user.getUsername(), user.getName(), user.getUsername() + "--申请删除含有授权的设备", LocalDateTime.now());
        //审批从表
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = new ArrayList<>();
        //批量保存删除授权
        granteds.stream().forEach(granted -> {
            approvalFlowInfoGranteds.add(saveSingleGranted(approvalFlow, granted));
            granted.setStatus(ConstantsDto.APPROVAL_PENDING);
            granted.setIsdelete(1);
            grantedMapper.updateById(granted);
        });
        approvalFlow.setApprovalFlowInfoGrantedList(approvalFlowInfoGranteds);
        approvalFlow.setGrantedMethod(ConstantsDto.GRANTEDMETHOD_DELETE_ASSET);
        approvalFlow.setRelationNum(1);
        approvalFlowService.saveApprovalFlowGranted(approvalFlow);
    }


    //region end --  删除授权 提出审批申请-----------------------------------------------------------------------------------------------------------------------------

    /**
     * 保存单条审批数据信息
     * 授权方式：1代表(设备->用户)，2代表(设备组->用户)，3代表(设备->用户组)，4代表(设备组->用户组)，5代表(删除设备)
     */
    private ApprovalFlowInfoGranted saveSingleGranted(ApprovalFlow approvalFlow, Granted granted) {
        ApprovalFlowInfoGranted aapprovalFlowInfoGranted = new ApprovalFlowInfoGranted();
        if (!Objects.isNull(granted.getUserId())) {
            //用户授权
            if (ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT == granted.getType()) {
                //保存设备账号
                approvalFlow.setGrantedMethod(ConstantsDto.GRANTEDMETHOD_USER_ASSET);
                aapprovalFlowInfoGranted = grantedMapper.getApprovalFlowInfoGrantedByAssetAccountUserId(granted.getId());
            } else if (ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP == granted.getType()) {
                //保存设备组 所有账号
                approvalFlow.setGrantedMethod(ConstantsDto.GRANTEDMETHOD_USER_ASSETGROUP);
                aapprovalFlowInfoGranted = grantedMapper.getApprovalFlowInfoGrantedByAssetgroupUserId(granted.getId());
                //设置所属设备组名字
                aapprovalFlowInfoGranted.setAssetgroupPname(assetgroupMapper.getPnameByAssetgroupId(aapprovalFlowInfoGranted.getAssetgroupId()));

            }
        } else {
            //用户组授权
            if (ConstantsDto.GRANTED_ASSETTYPE_ASSETACCOUNT == granted.getType()) {
                //保存设备账号
                approvalFlow.setGrantedMethod(ConstantsDto.GRANTEDMETHOD_USERGROUP_ASSET);
                aapprovalFlowInfoGranted = grantedMapper.getApprovalFlowInfoGrantedByAssetAccountUsergroupId(granted.getId());
                //设置用户组 所属组 名称
                Integer usergroupPid = aapprovalFlowInfoGranted.getUsergroupPid();
                if (!Objects.isNull(usergroupPid)) {
                    Usergroup usergroup = usergroupMapper.selectById(usergroupPid);
                    aapprovalFlowInfoGranted.setUsergroupPname(Objects.isNull(usergroup) ? "" : usergroup.getName());
                }
            } else if (ConstantsDto.GRANTED_ASSETTYPE_ASSETGROUP == granted.getType()) {
                //保存设备组 所有账号
                approvalFlow.setGrantedMethod(ConstantsDto.GRANTEDMETHOD_USERGROUP_ASSETGROUP);
                aapprovalFlowInfoGranted = grantedMapper.getApprovalFlowInfoGrantedByAssetgroupUsergroupId(granted.getId());

                //设置所属设备组名字
                aapprovalFlowInfoGranted.setAssetgroupPname(assetgroupMapper.getPnameByAssetgroupId(aapprovalFlowInfoGranted.getAssetgroupId()));
                //设置所属用户组名字
                Integer usergroupPid = aapprovalFlowInfoGranted.getUsergroupPid();
                if (!Objects.isNull(usergroupPid)) {
                    Usergroup usergroup = usergroupMapper.selectById(usergroupPid);
                    aapprovalFlowInfoGranted.setUsergroupPname(Objects.isNull(usergroup) ? "" : usergroup.getName());
                }
            }
        }
        return aapprovalFlowInfoGranted;
    }

}

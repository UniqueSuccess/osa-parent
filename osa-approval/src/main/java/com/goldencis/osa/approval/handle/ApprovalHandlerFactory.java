package com.goldencis.osa.approval.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-25 16:57
 **/
@Configuration
public class ApprovalHandlerFactory {

    @Autowired
    private List<IApprovalHandler> handlers;

    public Map<Integer, IApprovalHandler> map() {
        Map<Integer, IApprovalHandler> map = new ConcurrentHashMap<>();
        for (IApprovalHandler handler : handlers) {
            int mark = handler.type().getMark();
            if (Objects.nonNull(map.get(mark))) {
                throw new RuntimeException("重复的审批类型(ApprovalType) : " + mark);
            }
            map.put(mark, handler);
        }
        return map;
    }

    /**
     * 根据审批类型获取处理器
     * @see ApprovalType
     * @see com.goldencis.osa.common.constants.ConstantsDto#APPROVAL_COMMAND_ADD
     * @see com.goldencis.osa.common.constants.ConstantsDto#APPROVAL_GRANTED_ADD
     * @see com.goldencis.osa.common.constants.ConstantsDto#APPROVAL_GRANTED_DELETE
     * @see com.goldencis.osa.common.constants.ConstantsDto#APPROVAL_GRANTED_DELETE_ASSET
     * @see com.goldencis.osa.common.constants.ConstantsDto#APPROVAL_GRANTED_DELETE_ACCOUNT
     * @param type 审批类型
     * @return
     */
    public IApprovalHandler getHandlerByApprovalType(int type) {
        return map().get(type);
    }

}

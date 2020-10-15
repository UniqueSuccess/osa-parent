package com.goldencis.osa;

import com.goldencis.osa.approval.service.IApprovalFlowService;
import com.goldencis.osa.asset.service.IGrantedService;
import com.goldencis.osa.core.entity.Permission;
import com.goldencis.osa.core.entity.Resource;
import com.goldencis.osa.core.service.IPermissionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

/**
 * Created by limingchao on 2018/10/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AutoWireTest {

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IApprovalFlowService flowService;

    @Autowired
    private IGrantedService grantedService;

    @Test
    public void grantedTest() {
        List<Integer> aa = grantedService.getAssetIdsByOperator("d0413c23-5e3a-41e2-bcc2-c34d65c538c6");
        System.out.println(aa);
        List<Integer> ss = grantedService.getGroupIdsByOperator("d0413c23-5e3a-41e2-bcc2-c34d65c538c6");
        System.out.println(ss);
    }

    @Test
    public void flowTest() {
        System.out.println(flowService.isInApprovalByAssetIdAndApprovalType(25, null));
        System.out.println(flowService.isInApprovalByAssetIdAndApprovalType(25, 2));
        System.out.println(flowService.isInApprovalByAssetIdAndApprovalType(25, 3));
    }

    @Test
    public void autoWireTest() {
        System.out.println(permissionService);
        Resource resourceByResourceTypeAndId = permissionService.findResourceByResourceTypeAndId(1, 1);
        System.out.println(resourceByResourceTypeAndId);

        List<? extends Resource> resourceList = permissionService.findResourceListByResourceType(1);
        for (Resource resource : resourceList) {
            System.out.println(resource);
        }
    }

    @Test
    public void permissionServiceTest() {
        Permission permission = permissionService.findPermissionById(15);
        System.out.println(permission);
    }

    @Test
    public void uuidTest() {
        System.out.println(UUID.randomUUID().toString());
    }

}

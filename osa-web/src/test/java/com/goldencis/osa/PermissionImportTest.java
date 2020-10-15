package com.goldencis.osa;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.core.entity.Operation;
import com.goldencis.osa.core.entity.Permission;
import com.goldencis.osa.core.service.IOperationService;
import com.goldencis.osa.core.service.IPermissionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by limingchao on 2018/12/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PermissionImportTest {

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IOperationService operationService;

    @Test
    public void permissionImport() {
        List<Permission> addList = new ArrayList<>();
        List<Permission> list = permissionService.list(new QueryWrapper<Permission>().eq("resource_type", 2));

        List<Integer> operIds = list.stream().map(Permission::getResourceId).collect(Collectors.toList());

        List<Operation> all = operationService.list(null);
        for (Operation operation : all) {
            if (!operIds.contains(operation.getId())) {
                Permission permission = new Permission();
                permission.setResourceType(2);
                permission.setResourceId(operation.getId());
                addList.add(permission);
            }
        }

        permissionService.saveBatch(addList);
    }
}

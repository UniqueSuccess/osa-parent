package com.goldencis.osa;

import com.goldencis.osa.core.utils.ResourceType;
import org.junit.Test;

/**
 * Created by limingchao on 2018/10/12.
 */
public class EnumTest {

    @Test
    public void enumTest() {
        ResourceType navigation = ResourceType.valueOf("NAVIGATION");
        System.out.println(navigation.getValue());

        ResourceType resourceType = ResourceType.valueOf("22");
        System.out.println(resourceType.getValue());
    }

}

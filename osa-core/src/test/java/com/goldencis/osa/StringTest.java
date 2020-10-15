package com.goldencis.osa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldencis.osa.common.utils.IpUtil;
import com.goldencis.osa.core.entity.Department;
import com.goldencis.osa.core.entity.User;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by limingchao on 2018/10/16.
 */
public class StringTest {

    @Test
    public void scriptEngine() {
        ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
    }

    @Test
    public void httpMethodTest() {
        System.out.println(HttpMethod.GET.name());
        System.out.println(HttpMethod.GET.matches("POST11"));
        System.out.println(HttpMethod.GET.matches("POST"));
        System.out.println(HttpMethod.GET.matches("GET"));
    }

    @Test
    public void formatTest() {
        String template = "test : %s : %s";
        String[] args = new String[2];
        args[0] = "li";
        args[1] = "lmc";
        String format = String.format(template, args);
        System.out.println(format);

        String paramTest = "test : %ret : %ret";
        paramTest = paramTest.replaceAll("%ret", "%s");
        String convert = String.format(paramTest, args);
        System.out.println(convert);
    }

    @Test
    public void splitTest() {
        String str = "0.username";
        String[] split = str.split("\\.");
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
        }
    }

    @Test
    public void stringTest() {
        String str = "/osa/user/userList";

        int index = str.indexOf("/osa");
        System.out.println(index);

        String substring = str.substring(index + "/osa".length());
        System.out.println(substring);

    }

    @Test
    public void jsonTest() throws JsonProcessingException {
        User user = new User();
        user.setUsername("li");
        user.setGuid("abc");
        user.setName("lmc");

        Department department = new Department();
        department.setId(1);
        department.setName("dept");

        user.setDepartment(department);

        List<User> userList = new ArrayList<>();
        userList.add(user);

        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(userList);
        System.out.println(value);
    }

    @Test
    public void test() {
        String[] str = {"AAA", "BBB"};
        System.out.println(str.length);
        System.out.println(str);


        for (int i = 0; i < str.length; i++) {
            System.out.println(str[i]);
        }
    }

    @Test
    public void test2() {
        List<String> listString = new ArrayList<>();
        listString.add("AAA");
        listString.add("BBB");
        listString.add("CCC");

        List<String> lsit2 = Arrays.asList("AAA", "BBB", "CCC");
        System.out.println(lsit2.getClass());

        List<String> list3 = Arrays.asList(new String[]{"AAA", "BBB", "CCC"});
        System.out.println(list3.getClass());


        Object obj = listString;

        System.out.println(obj.getClass());

        if (obj instanceof ArrayList) {
            ArrayList list = (ArrayList) obj;
            System.out.println(list.size());

            for (Object o : list) {
                if (o instanceof String) {
                    String str = (String) o;
                    System.out.println(str);
                }
            }
        }
    }

    @Test
    public void test3() {
        List<String> strings = Arrays.asList(new String[]{"AAA", "BBB", "CCC"});
        Object obj = Arrays.asList(new String[]{"AAA", "BBB", "CCC"});
        System.out.println(obj.getClass());

        List<Object> objects = Arrays.asList(obj);

        System.out.println(objects.size());

        List<String> list = (List<String>) obj;
        System.out.println(list.size());
    }

    @Test
    public void encodin() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("F1FEA6DCDBE21260484F946A1BDFCB4D46C8253B320910E8AC2206EA302A7751");
        System.out.println(encode);

        String encode1 = encoder.encode("F1FEA6DCDBE21260484F946A1BDFCB4D46C8253B320910E8AC2206EA302A7751");
        System.out.println(encode1);

        boolean matches = encoder.matches("F1FEA6DCDBE21260484F946A1BDFCB4D46C8253B320910E8AC2206EA302A7751", encode);
        System.out.println(matches);

        boolean matches1 = encoder.matches("F1FEA6DCDBE21260484F946A1BDFCB4D46C8253B320910E8AC2206EA302A7751", encode1);
        System.out.println(matches1);
    }

    @Test
    public void urlMatch() {
        String url = "http://10.10.16.227/osa/system/systemConfig";
        System.out.println(IpUtil.getIpFromUrl(url) );

    }
}

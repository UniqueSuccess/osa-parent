package com.goldencis.osa.common.utils;

import org.dom4j.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by wangyi on 2018/9/26.
 */
public class ResourceUtils {
    public static List<String> queryXmlResourceDataList(String file, String path) {
        try {
            File xmlFile =  org.springframework.util.ResourceUtils.getFile(file);
            Document document =XmlUtil.getDocumentByFile(xmlFile);
            List<String> xml = XmlUtil.getAttributeValueListByTree(path, document);
            return xml;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

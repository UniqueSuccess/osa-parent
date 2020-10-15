package com.goldencis.osa.common.utils;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.xml.stream.XMLInputFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Joram Barrez
 */
public class XmlUtil {


    /**
     * 通过xml路径获取文档对象
     * @param filePath
     * @return
     */
    public static Document getDocument(String filePath) {
        return getDocumentByFile(new File(filePath));
    }

    /**
     * 通过xml文件获取文档对象
     * @param file
     * @return
     */
    public static Document getDocumentByFile(File file) {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 通过节点树，获取对应属性的值集合
     * @param tree /books/book/@show 类似于该例子，节点前面加"/"，属性前面加"@"
     * @param document 文档对象
     * @return
     */
    public static List<String> getAttributeValueListByTree(String tree, Document document) {
        List<String> valueList = new ArrayList<>();

        List list = document.selectNodes(tree);
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Attribute attribute = (Attribute) iter.next();
            valueList.add(attribute.getValue());
        }

        return valueList;
    }

    /**
     * 通过节点树，获取对应的属性集合，将属性值为preValue的属性值改为value
     * @param tree /books/book/@show 类似于该例子，节点前面加"/"，属性前面加"@"
     * @param value 新修改的值
     * @param preValue 之前的值
     * @param document 文档对象
     * @return
     */
    public static void updateAttributeByTree(String tree, String value, String preValue, Document document) {
        //先用xpath查找对象
        List list = document.selectNodes(tree);

        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Attribute attribute = (Attribute) iter.next();
            if (attribute.getValue().equals(preValue)) {
                attribute.setValue(value);
            }
        }
    }

    /**
     * 通过节点树，获取对应的属性集合，将属性值改为value
     * @param tree /books/book/@show 类似于该例子，节点前面加"/"，属性前面加"@"
     * @param value 新修改的值
     * @param document 文档对象
     * @return
     */
    public static void updateAttributeByTree(String tree, String value, Document document) {
        //先用xpath查找对象
        List list = document.selectNodes(tree);

        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Attribute attribute = (Attribute) iter.next();
            attribute.setValue(value);
        }
    }

    /**
     * 通过节点树，获取对应的标签集合
     * @param tree
     * @param document
     * @return
     */
    public static List<Element> getElementListByTree(String tree, Document document) {
        List<Element> elementList = new ArrayList<>();
        List list = document.selectNodes(tree);
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            elementList.add((Element) iter.next());
        }
        return elementList;
    }

    public static int writeDocumentIntoFile(Document document, String filePath) {
        int returnValue = 0;
        XMLWriter writer = null;
        try {
            //格式化输出,类型IE浏览一样
            OutputFormat format = OutputFormat.createPrettyPrint();
            //指定XML编码
            format.setEncoding("UTF-8");

            //将document中的内容写入文件中
            writer = new XMLWriter(new FileOutputStream(new File(filePath)), format);

            writer.write(document);
            writer.flush();
            //执行成功,需返回1
            returnValue = 1;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnValue;
    }

    public static XMLInputFactory createSafeXmlInputFactory() {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        if (xif.isPropertySupported(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES)) {
            xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
        }

        if (xif.isPropertySupported(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES)) {
            xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        }

        if (xif.isPropertySupported(XMLInputFactory.SUPPORT_DTD)) {
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        }
        return xif;
    }

}

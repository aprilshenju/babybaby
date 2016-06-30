package com.umeijia.wechat;

import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hadoop on 2016/6/29.
 */
public class WechatUtil {
    public static  Map<String,String> xmlToMap(HttpServletRequest  request) throws IOException, DocumentException {
        Map<String,String> map = new HashMap<>();
        SAXReader saxReader = new SAXReader();
        InputStream in = request.getInputStream();
        Document document = saxReader.read(in);
        Element root = document.getRootElement();
        List<Element> list = root.elements();
        for(Element e:list){
            map.put(e.getName(),e.getText());
        }
        in.close();
        return map;
    }

    public static String objectToXml(BaseMessage message){
        XStream xStream = new XStream();
        xStream.alias("xml",message.getClass());
        return xStream.toXML(message);
    }
}

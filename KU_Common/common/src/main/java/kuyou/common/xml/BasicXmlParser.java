package kuyou.common.xml;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * action :XML解析器[XmlPullParser封装]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-22 <br/>
 * </p>
 */
public abstract class BasicXmlParser {

    protected final String TAG = "com.kuyou.xmlparsedemo.xml > BasicXmlParser";

    protected abstract void parseItem(String tagName, XmlPullParser parser) throws Exception;

    protected abstract void onParseItemEnd(String tagName);

    public void parseAssetsFile(Context context, String fileName) throws Exception {
        parse(context.getResources().getAssets().open(fileName));
    }

    public void parse(String filePath) throws Exception {
        File xmlFile = new File(filePath);
        InputStream xmlInputStream = new FileInputStream(xmlFile);
        parse(xmlInputStream);
    }

    public void parse(InputStream is) throws Exception {
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        String tagName, tagConnect;
        while (type != XmlPullParser.END_DOCUMENT) {
            tagName = parser.getName();
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:
                    parseItem(tagName, parser);
                    break;
                //结束标签
                case XmlPullParser.END_TAG:
                    onParseItemEnd(tagName);
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
    }
}

package com.umeijia.util;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by Administrator on 2016/6/22.
 */
public class ThumbGenerateThread extends Thread {
    private Logger logger = Logger.getLogger("debug");
    private String imgPath =null;
    private String thumbDir = null;
    private JpegTool jpegTool = null;
    private final Double SCALE =0.2d;
    public ThumbGenerateThread(String imgPath,String thumbDir){
        this.imgPath = imgPath;
        this.thumbDir = thumbDir;
        jpegTool = new JpegTool();
    }

    @Override
    public void run() {
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1,imgPath.length());
        String dirPath = imgPath.substring(0,imgPath.lastIndexOf("/"));
        String thumbImgName = null;
        String thumbImgPath = null;
        File dir = new File(thumbDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        if(imgName.lastIndexOf(".jpg")==-1){//非jpg图片,直接拷贝
            thumbImgName = imgName.substring(0,imgName.lastIndexOf("."))+"_thumb"+imgName.substring(imgName.lastIndexOf("."),imgName.length());
            thumbImgPath = thumbDir+"/"+thumbImgName;
            FileUtils.copyFile(imgPath,thumbImgPath,true);
        }else{//jpg图片,压缩
            thumbImgName = imgName.substring(0,imgName.lastIndexOf(".jpg"))+"_thumb.jpg";
            thumbImgPath = thumbDir+"/"+thumbImgName;
            try {
                jpegTool.SetScale(SCALE);
                jpegTool.doFinal(imgPath,thumbImgPath);
            } catch (JpegTool.JpegToolException e) {
                logger.error("生成缩略图失败");
                e.printStackTrace();
            }
        }
    }
}

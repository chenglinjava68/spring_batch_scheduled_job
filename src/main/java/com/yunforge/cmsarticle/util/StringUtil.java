package com.yunforge.cmsarticle.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



public class StringUtil {
	
    /**
     * 获取字符串
     * 
     * @param ob
     * @return
     */
    public static String getString(Object ob) {
        if (ob == null) {
            return null;
        }
        return ob.toString();
    }
    /**
     * 
     * 作者:覃飞剑
     * 日期:2017年7月26日
     * @param period
     * @return
     * 返回:String
     * 说明:yyyyMMdd换成yyyy年MM月dd日字符串
     */
    public static String getDateStrByPeriod(String period) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(period);
            sdf = new SimpleDateFormat("yyyy年MM月dd日");
            return sdf.format(date);
        } catch(Exception e) {
            
        }
        return "";
    }
    /**
     * 
     * 作者:覃飞剑
     * 日期:2017年11月23日
     * @param period
     * @return
     * 返回:String
     * 说明:获取当前时间字符串yyyyMMddhhmiss
     */
    public static String getNowDataStr() {
    	SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
    	Date date = new Date();
    	return sdf.format(date);
    }
    /**
     * 
     * 作者:覃飞剑
     * 日期:2017年11月28日
     * @param date
     * @param format
     * @return
     * 返回:String
     * 说明: 根据date类型和foramt 获取日期字符串
     */
    public static String getTimeByDateAndFormat(Date date, String format) {
        String result = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            result = sdf.format(date);
         } catch (Exception e) {
             // TODO: handle exception
             return result;
         }
        return result;
    }
    /**
     * 
     * 作者:覃飞剑
     * 日期:2017年11月28日
     * @param str
     * @return
     * 返回:boolean
     * 说明:判断是否为空 null 空字符串都是空
     */
    public static boolean isEmpty(Object str) {
		if (str == null || "".equals(str.toString()) || "null".equals(str)) {
			return true;
		}
		return false;
	}
    /**
     * 
     * 作者:覃飞剑
     * 日期:2017年7月18日
     * @param path
     * 返回:void
     * 说明:删除指定目录文件
     */
    public static void deleteFiles(String path){
        File file = new File(path);
        //1級文件刪除
        if(!file.isDirectory()){
            file.delete();
        }else if(file.isDirectory()){
            //2級文件列表
            String []filelist = file.list();
            //獲取新的二級路徑
            for(int j=0;j<filelist.length;j++){
                File filessFile= new File(path+"\\"+filelist[j]);
                if(!filessFile.isDirectory()){
                    filessFile.delete();
                }else if(filessFile.isDirectory()){
                    //遞歸調用
                    deleteFiles(path+"\\"+filelist[j]);
                }
            }
            file.delete();
        }
     }
}

package cn.pokerfaceSad.jarvis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.content.Context;
import android.widget.Toast;

public class Test {
	public static void getProp(Context context){
		Properties prop;
		boolean b=false;
		String s="";
		int i=0;
		prop=loadConfig(context,context.getExternalFilesDir(null).getAbsolutePath()+"/JARVIS.properties");
		s=(String)prop.get("string");
		Toast.makeText(context, s,
				Toast.LENGTH_SHORT).show();
	}
	public static void setProp(Context context){
	Properties prop;
	boolean b=false;
	String s="";
	int i=0;
	prop=loadConfig(context,context.getExternalFilesDir(null).getAbsolutePath()+"/JARVIS.properties");
	if(prop==null){
		//�����ļ������ڵ�ʱ�򴴽������ļ� ��ʼ��������Ϣ
		prop=new Properties();
		prop.put("bool", "yes");
		prop.put("string", "aaaaaaaaaaaaaaaa");
		prop.put("int", "110");//Ҳ������ӻ����������� getʱ����Ҫǿ��ת���ɷ�װ����
		saveConfig(context,context.getExternalFilesDir(null).getAbsolutePath()+"/JARVIS.properties",prop);
	}
	prop.put("bool", "no");//put��������ֱ���޸�������Ϣ�������ظ����
	b=(((String)prop.get("bool")).equals("yes"))?true:false;//get�����Ķ���Object���� ����ǻ������� ��Ҫ�õ���װ��
	s=(String)prop.get("string");
	i=Integer.parseInt((String)prop.get("int"));
	saveConfig(context,context.getExternalFilesDir(null).getAbsolutePath()+"/JARVIS.properties",prop);
	}
	/** 
	 * ��ȡ�����ļ� 
	 * <p> 
	 * Title: loadConfig 
	 * <p> 
	 * <p> 
	 * Description: 
	 * </p> 
	 *  
	 * @param context 
	 * @param file 
	 * @return 
	 */  
	public static Properties loadConfig(Context context, String file) {  
	    Properties properties = new Properties();  
	    try {  
	        FileInputStream s = new FileInputStream(file);  
	        properties.load(s);  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	    return properties;  
	}  
	  
	/** 
	 * ���������ļ� 
	 * <p> 
	 * Title: saveConfig 
	 * <p> 
	 * <p> 
	 * Description: 
	 * </p> 
	 *  
	 * @param context 
	 * @param file 
	 * @param properties 
	 * @return 
	 */  
	public static boolean saveConfig(Context context, String file,  
	        Properties properties) {  
	    try {  
	        File fil = new File(file);  
	        if (!fil.exists())  
	            fil.createNewFile();  
	        FileOutputStream s = new FileOutputStream(fil);  
	        properties.store(s, "");  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return false;  
	    }  
	    return true;  
	}  
}

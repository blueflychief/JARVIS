package pokerface.Sad.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pokerface.Sad.connect.RPiClient;
import pokerface.Sad.util.Util;

public class ServiceImpl1 implements Service{
	
	static Logger logger = null;
	static{
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(ServiceImpl1.class);
	}
	
	@Override
	public String execOrder(String order) {
		String result = "ִ��ʧ��";
		RPiClient c1 = null;
		String methodName = null;
		Properties pro = null;
		Class cls = null;
		Method method = null;
		//������Ƽ��ع�����
		try {
			cls = Class.forName("pokerface.Sad.util.Util");
			pro = Util.getProperties();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		methodName = pro.getProperty(order); //�������ļ��л�ȡ�����Ӧ�ķ�����
		
		if(methodName!=null)
		{
			logger.info("���յ�"+order+"ָ��");
			logger.info("ִ��"+methodName+"����");
			//������Ƶ��÷���
			try {
				cls.getDeclaredMethod(methodName).invoke(cls);
				result = order+"ִ�гɹ�";
			} catch (IllegalAccessException | IllegalArgumentException  | InvocationTargetException e) {
				e.printStackTrace();
				Throwable cause = e.getCause();
				if(cause instanceof EmailException){
					result = "�ʼ�����ʧ��";
				}
			} catch (NoSuchMethodException e) {
				logger.error("�����쳣",e);
			} catch (SecurityException e) {
				logger.error("�����쳣",e);
			}
			
			order=null;
			methodName=null;
		}else{
			//û�ж�Ӧ����
			logger.info("���յ�"+order+"ָ��");
			logger.info("û�ж�Ӧ����");
			result = order+"ָ�����û�ж�Ӧ����";
		}
		return result;
	}

}

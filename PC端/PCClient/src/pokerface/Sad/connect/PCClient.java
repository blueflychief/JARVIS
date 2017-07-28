package pokerface.Sad.connect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import pokerface.Sad.service.Service;
import pokerface.Sad.service.ServiceImpl1;
import pokerface.Sad.util.Util;

public class PCClient extends Socket{
	
	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(PCClient.class);
	}
	
	
	private String serverIP = null;
	private Integer serverPort = null;
	public boolean keyBoardClosed = false;
	static String closeOrder = "closeClient";
	final static String heatbeatMsg = "heartbeating";
	public PCClient() throws FileNotFoundException, IOException {
		super();
		logger.debug("����PCClient����");
		Properties pro = Util.getProperties();
		this.serverIP = pro.getProperty("serverIP");
		this.serverPort = new Integer(pro.getProperty("serverPort"));
		this.connect(new InetSocketAddress(InetAddress.getByName(this.serverIP), this.serverPort));
		logger.debug("���ӵ�Server�ɹ�");
	}
	
	//����ʽ����
	public String receiveOrder() throws IOException{
		InputStream is = this.getInputStream();
		byte[] buf = new byte[1024];
		Integer len = null;
		String msg = null;
		while(true)
		{
			if((len=is.read(buf))!=-1)
			{
				msg = new String(buf, 0, len);
				//�˳�������Ϣ
				if(!isHeartMsg(msg))
					return msg;
				else
					logger.debug("���յ�������Ϣ");
			}
		}
	}
	//������ʽ�ж��Ƿ�Ϊ������Ϣ
	public static boolean isHeartMsg(String msg){
		Pattern p = Pattern.compile(PCClient.heatbeatMsg);
		Matcher m = p.matcher(msg);
		if(m.find())
		{
			return true;
		}
		else{
			return false;
		}		
	}
	public static void main(String[] args) {
		
		PCClient pcClient = null;
		String order = null;
		
		Service service = new ServiceImpl1(); //ͨ��Service��ʵ��������Ӧ����
		
		try {
				pcClient = new PCClient();
				if(pcClient.isConnected())
				{
					logger.info("connected to Server successfully");
				}
				new Thread(new ReceiveInput(pcClient)).start(); //���ռ�������Ĺر�ָ��
				while(true)
				{
					//����ʽ����
					order = pcClient.receiveOrder();
					logger.info("���յ�"+order+"ָ��");
					//ִ��ָ���ȡ�����Ϣ
					String result = service.execOrder(order);
					//�����������Server
					OutputStream os = pcClient.getOutputStream();
					os.write(result.getBytes("GBK"));
					os.flush();
				}
		} catch (IOException e) {
			if (pcClient.keyBoardClosed == true) {
				logger.info("���ӹر�");
			} else{
				logger.error("�����쳣",e);
			}
		}
	}
}
class ReceiveInput implements Runnable{
	
	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(ReceiveInput.class);
	}
	
	PCClient client = null;
	public ReceiveInput(PCClient client) {
		this.client = client;
	}
	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			while(true)
			{
				if(PCClient.closeOrder.equals(new String(br.readLine())))
				{
					this.client.close();
					this.client.keyBoardClosed = true;
					return;
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
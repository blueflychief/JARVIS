package pokerface.Sad.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pokerface.Sad.util.Util;

public class RPiServer extends ServerSocket{
	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(RPiServer.class);
	}
	final static String heatbeatMsg = "heartbeating";
	Socket rPiClient = null;  //rPi��Client
	boolean rPiConnectState = false;
	
	public RPiServer() throws IOException {
		//�������ļ��ж�ȡ�˿ڣ�������Server����
		super(new Integer(Util.getProperties().getProperty("rPiServerPort")));
		logger.debug("����RPiServer����ɹ�   Port:"+Util.getProperties().getProperty("rPiServerPort"));
	}
	
	//�ȴ�rPi����
	public void acceptRPi() throws IOException{
		logger.info("wait for rPi connect......");
		rPiClient = this.accept();
		logger.info("rPi :"+rPiClient.getInetAddress()+" connect");
		rPiConnectState = true;
	}
	
	//��ͻ��˶˷�������
	public void sendMsgToClient(Socket client,String msg) throws IOException{
		OutputStream os = client.getOutputStream();
		os.write(msg.getBytes("GBK"));
		os.flush();
		//os.close(); socket�����ܹر� 
	}
	
	//�ӿͻ��ٶ˽��ս����Ϣ
	public String getMsgFromClient(Socket client){
		InputStream is = null;
		byte[] buf = null;
		int Len = 0;
		try {
			is = client.getInputStream();
			buf = new byte[1024];
			Len = is.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String result = null;
		try {
			result = new String(buf,0,Len,"GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	
		return result;
	}
	//�رտͻ�������
	public void close(){
		if(this.rPiClient!=null)
		{
			try {
				this.rPiClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.close();
	}
	
	//��ͻ��˷���
	public boolean isConnected(Socket client){
        try{
        	logger.debug("��rPi����������Ϣ");
        	this.sendMsgToClient(client,Server.heatbeatMsg);
        	logger.debug("��rPi����������Ϣ�ɹ�");
        	return true;
        }catch(Exception e){
        	logger.debug("��rPi����������Ϣʧ��");
            return false;
        }
	}
}

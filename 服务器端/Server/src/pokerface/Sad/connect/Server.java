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

public class Server extends ServerSocket {

	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(Server.class);
	}

	final static String heatbeatMsg = "heartbeating";
	final static String pcStateCheckOrder = "pcStateCheck";
	final static String rPiStateCheckOrder = "rPiStateCheck";
	Socket pcClient = null; // PC��Client
	Socket webClient = null; // web��Client
	RPiServer rPiServer = new RPiServer(); //rPiServer
	boolean pcConnectState = false;

	public static void main(String[] args) throws IOException {
		Server s = new Server();
		logger.info("wait for Web part...");
		s.acceptWeb();// �ȴ�Web����
		// ������������
		logger.info("JARVIS Service Start Up Normally......");
		Thread webMonitorThread = new Thread(new WebOrderMonitor(s));
		webMonitorThread.start();
		Thread rPiMonitorThread = new Thread(new RPiStateMonitor(s.rPiServer));
		rPiMonitorThread.start();
		Thread pcMonitorThead = new Thread(new PCStateMonitor(s));
		pcMonitorThead.start();

	}

	public Server() throws IOException {
		// �������ļ��ж�ȡ�˿ڣ�������Server����
		super(new Integer(Util.getProperties().getProperty("serverPort")));
		logger.debug("����Server����ɹ�");
	}

	// �ȴ�PC����
	public void acceptPC() throws IOException {
		logger.info("wait for PC connect......");
		pcClient = this.accept();
		logger.info("PC :" + pcClient.getInetAddress() + " connect");
		pcConnectState = true;
	}

	// �ȴ�WebӦ������
	public void acceptWeb() throws IOException {
		webClient = this.accept();
		System.out.println("Web :" + webClient.getInetAddress() + " connect");
	}

	// ��ͻ��˶˷�������
	public void sendMsgToClient(Socket client, String msg) throws IOException {
		OutputStream os = client.getOutputStream();
		os.write(msg.getBytes("GBK"));
		os.flush();
		// os.close(); socket�����ܹر�
	}

	// �ӿͻ��˶˽��ս����Ϣ
	public String getMsgFromClient(Socket client) {
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
			result = new String(buf, 0, Len, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return result;
	}

	// ��Web���ֳ�����
	public String receiveOrder() throws IOException {
		InputStream is = this.webClient.getInputStream();
		byte[] buf = new byte[1024];
		Integer len = null;
		String msg = null;

		if ((len = is.read(buf)) != -1) {
			msg = new String(buf, 0, len);
			return msg;
		}
		return null;
	}

	public void close() {
		if (this.pcClient != null) {
			try {
				this.pcClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.close();
	}

	public boolean isConnected(Socket client) {
		try {
			/*
			 * �˷��ͽ������ݵķ�����Windows�����»�����쳣 this.pcClient.sendUrgentData(0xff);
			 */
			logger.debug("��PC�˷���������Ϣ");
			this.sendMsgToClient(client, Server.heatbeatMsg);
			logger.debug("��PC�˷���������Ϣ�ɹ�");
			return true;
		} catch (Exception e) {
			logger.debug("��PC�˷���������Ϣʧ��");
			return false;
		}
	}
}

// �ȴ�Web�����߳�
class WebOrderMonitor implements Runnable {

	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(Server.class);
	}

	Server server = null;

	public WebOrderMonitor(Server Server) {
		this.server = Server;
	}

	public void run() {
		logger.debug("WebOrderMonitor�߳�����");
		
		try {
			String order = null;

			while (true) {
				logger.debug("��������ָ��...");
				order = server.receiveOrder();
				logger.debug("���յ�"+order+"ָ��");
				/*
				 * �����յ���order�Ƿ�Ϊ���PC״ָ̬�� ������ֱ�ӷ���PC״̬
				 */
				String response = null;
				if (order.equals(Server.pcStateCheckOrder)) {
					logger.debug("����PC״̬");
					response = server.pcConnectState ? "PC����" : "PC������";
				} else if(order.equals(Server.rPiStateCheckOrder)) {
					logger.debug("����PC״̬");
					response = server.rPiServer.rPiConnectState ? "rPi����" : "rPi������";
				} else if(order.equals(Util.getProperties().getProperty("WOL"))){
					
					if (server.rPiServer.rPiConnectState == true) {
						logger.debug("rPi����");
						logger.debug("������͸�rPi��");
						server.rPiServer.sendMsgToClient(server.rPiServer.rPiClient, order);
						// ����PC�˷����Ľ����Ϣ
						logger.debug("�ȴ�rPi�˷���");
						response = server.rPiServer.getMsgFromClient(server.rPiServer.rPiClient);
						logger.debug("���յ�rPi�˷�����"+response);
					} else {
						logger.debug("rPi������");
						response = "rPi������";
					}
				} else {
					// �����յ�������ת����PC��
					if (server.pcConnectState == true) {
						logger.debug("PC����");
						logger.debug("������͸�PC��");
						server.sendMsgToClient(server.pcClient, order);
						// ����PC�˷����Ľ����Ϣ
						logger.debug("�ȴ�PC�˷���");
						response = server.getMsgFromClient(server.pcClient);
						logger.debug("���յ�PC�˷�����"+response);
					} else {
						logger.debug("PC������");
						response = "PC������";
					}
				}
				// �������Ϣת����web��
				logger.debug("��PC�˷������͵�web��");
				server.sendMsgToClient(server.webClient, response);
				logger.debug("���ͳɹ�");
			}
		} catch (IOException e) {
			logger.error("�����쳣",e);
		}
	}
}

/**
 * ���̸߳���PC�˵����Ӻ�״̬���
 * 
 */
class PCStateMonitor implements Runnable {

	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(Server.class);
	}

	Server Server = null;

	public PCStateMonitor(Server s) {
		this.Server = s;
	}

	public void run() {
		logger.debug("PCStateMonitor�߳�����");
		while (true) {
			try {
				// �ȴ�PC����
				this.Server.acceptPC();
			} catch (IOException e) {
				logger.error("PC�����쳣",e);
			}
			while (Server.isConnected(Server.pcClient)) {
				// ���PC���Ƿ�����
				logger.debug("PC������");
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					logger.error("�ж��쳣",e);
				}
			}
			logger.info("PC��������");
			this.Server.pcConnectState = false;
		}

	}
}

class RPiStateMonitor implements Runnable {

	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(Server.class);
	}

	RPiServer rPiServer = null;

	public RPiStateMonitor(RPiServer rPiServer) {
		this.rPiServer = rPiServer;
	}

	public void run() {
		logger.debug("RPiStateMonitor�߳�����");
		while (true) {
			try {
				// �ȴ�rPi����
				rPiServer.acceptRPi();
			} catch (IOException e) {
				logger.error("rPi�����쳣", e);
			}
			while (rPiServer.isConnected(rPiServer.rPiClient)) {
				logger.debug("rPi����");
				// ���rPi���Ƿ�����
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					logger.error("�ж��쳣", e);
				}
			}
			logger.info("RPi��������");
			rPiServer.rPiConnectState = false;
		}

	}
}
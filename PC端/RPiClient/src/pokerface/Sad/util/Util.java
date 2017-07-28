package pokerface.Sad.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Util {
	/**
	 *  ��ȡ�����ļ�
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Properties getProperties() throws FileNotFoundException,
			IOException {
		Properties pro = new Properties();
		pro.load(new FileInputStream("RPiClient.properties"));
		return pro;
	}

	/**
	 *  ��ȡ��ǰʱ��
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String getDate() throws FileNotFoundException, IOException {
		Properties pro = null;
		String date = null;
		pro = getProperties();
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				pro.getProperty("dateFormat"));
		date = sdf.format(d);
		return date;
	}

	/**
	 * WOL����PC
	 * @throws IOException
	 */
	public static void wakeOnLAN() throws IOException {
		int port = 1000;
		String macAddress = Util.getProperties().getProperty("macAddress"); //PC��mac��ַ
		String destIP = Util.getProperties().getProperty("destIP");// �㲥��ַ
		// ��� mac ��ַ,������ת��Ϊ������
		byte[] destMac = getMacBytes(macAddress);
		if (destMac == null) {
			return;
		}

		InetAddress destHost = InetAddress.getByName(destIP);
		// construct packet data
		byte[] magicBytes = new byte[102];
		// �����ݰ���ǰ6λ����0xFF�� "FF"�Ķ�����
		for (int i = 0; i < 6; i++) {
			magicBytes[i] = (byte) 0xFF;
		}

		// �ӵ�7��λ�ÿ�ʼ��mac��ַ����16��
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < destMac.length; j++) {
				magicBytes[6 + destMac.length * i + j] = destMac[j];
			}
		}
		// create packet
		DatagramPacket dp = null;
		dp = new DatagramPacket(magicBytes, magicBytes.length, destHost, port);
		DatagramSocket ds = new DatagramSocket();
		ds.send(dp);
		ds.close();
		System.out.println("ok");
	}

	private static byte[] getMacBytes(String macStr)
			throws IllegalArgumentException {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split("(\\:|\\-)");
		if (hex.length != 6) {
			throw new IllegalArgumentException("Invalid MAC address.");
		}
		try {
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) Integer.parseInt(hex[i], 16);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Invalid hex digit in MAC address.");
		}
		return bytes;
	}

}

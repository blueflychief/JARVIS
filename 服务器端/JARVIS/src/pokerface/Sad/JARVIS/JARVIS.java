package pokerface.Sad.JARVIS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JARVIS implements Servlet{
	
	Socket s = null;
	@Override
	public void init(ServletConfig config) throws ServletException {
		//����������ʱ�ʹ����˶��󣬲��ڴ�������ʱ������Server��Socket����
		
		//Socket����
		try {
			this.s = new Socket(InetAddress.getByName("127.0.0.1"),10001);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		//��ȡ����
		String order = req.getParameter("order");
		OutputStream os = s.getOutputStream();
		//������Server
		os.write(order.getBytes());
		os.flush();
		//����Serverת�����ķ�����Ϣ
		InputStream is = s.getInputStream();
		byte[] buf = new byte[1024];
		int Len = is.read(buf);
		String result = new String(buf,0,Len);
		res.setContentType("text/html;charset=GBK");
		PrintWriter PW = res.getWriter();
		
		PW.write("<html>");
		PW.write("<title>");
		PW.write("</title>");
		PW.write("<body align=\"center\">");
		PW.write("<font size=70px>"+result+"<br><br><font>");
		PW.write("</body>");
		PW.write("</html>");
		System.out.println(req.getRemoteAddr());
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {
		
	}
	

}


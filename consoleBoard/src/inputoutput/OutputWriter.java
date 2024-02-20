package inputoutput;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class OutputWriter extends Thread{
	private Socket socket = null;
	
	// 메뉴 입력하기
	Scanner sc = new Scanner(System.in); 
	
	public OutputWriter(Socket socket) { 
		this.socket = socket; 
	}
	
	@Override
	public void run() {
		try {
			// 클라이언트에서 Server로 메세지를 발송 
			OutputStream out = socket.getOutputStream(); 
			PrintWriter writer = new PrintWriter(out, true); 
			
			while(true) { 
				writer.println(sc.nextLine()); 
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		}			
	}
}

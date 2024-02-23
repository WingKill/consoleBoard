package client;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class OutputWriter extends Thread{
	private Socket socket = null;	
	
	public OutputWriter(Socket socket) { 
		this.socket = socket; 
	}
	
	@Override
	public void run() {	
		// 클라이언트에서 Server로 메세지를 발송 
		try (OutputStream out = socket.getOutputStream(); 
			 PrintWriter writer = new PrintWriter(out, true);
			 Scanner sc= new Scanner(System.in);){			
			// 메뉴 입력하기
			while(true) {
				String sendMessage = sc.nextLine();
				writer.println(sendMessage); 
				if(sendMessage.equals("F")) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			// 소켓 닫기는 해당 클래스에서만 진행한다.
            try {
            	if(socket != null && !socket.isClosed()) {
            		socket.close(); 
            	}                           	
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
	}
}

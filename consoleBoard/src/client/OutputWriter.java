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
		Scanner sc= new Scanner(System.in);
		try {
			// 클라이언트에서 Server로 메세지를 발송 
			OutputStream out = socket.getOutputStream(); 
			PrintWriter writer = new PrintWriter(out, true);
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
            try {
            	if(socket != null && !socket.isClosed()) {
            		socket.close(); // 소켓 닫기
            	}                           	
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		sc.close();
	}
}

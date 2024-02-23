package client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class InputReader extends Thread{
	private Socket socket = null;
	public InputReader(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		// Server에서 보낸 메세지를 클라이언트로 가져오기
		try (InputStream in = socket.getInputStream(); 
			 BufferedReader reader = new BufferedReader(new InputStreamReader(in));){			
			String readvalue;
			while((readvalue = reader.readLine()) != null ) {
				System.out.println(readvalue);				
			}
		} catch (SocketException se) {
	        // 소켓이 닫혔을 때 발생하는 예외 처리
	        System.out.println("서버와의 연결이 종료되었습니다.");
	    } catch (Exception e) {
			e.printStackTrace();
		}	
	}	
}

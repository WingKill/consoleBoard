package inputOutput;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class InputReader extends Thread{
	private Socket socket = null;
	public InputReader(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			// Server에서 보낸 메세지를 클라이언트로 가져오기			
			InputStream in = socket.getInputStream(); 

			BufferedReader reader = new BufferedReader(new InputStreamReader(in)); 			
			while(true) { 
				System.out.println(reader.readLine());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
}

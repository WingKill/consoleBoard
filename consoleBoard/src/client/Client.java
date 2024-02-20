package client;

import java.io.IOException;
import java.net.Socket;

public class Client {
	private static int socketPort = 7777;
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("localhost", socketPort); // 소켓 서버에 접속 
			System.out.println("-----[해당 서버에 접속합니다]-----"); // 접속 확인용
			
			// 서버에서 보낸 메세지 읽는 Thread
			Thread reader = new InputReader(socket);
			// 서버로 메세지 보내는 Thread
			Thread writer = new OutputWriter(socket); 

			reader.start(); // ListeningThread Start
			writer.start(); // WritingThread Start
		} catch (IOException e) {	
			e.printStackTrace(); 
		}
	}	
}

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	private static int socketPort = 7777;
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(socketPort);
			System.out.println("server open"); 
			// 소켓 서버가 종료될 때까지 무한루프
			while (true) {
				Socket userSocket = serverSocket.accept(); 
				Thread boards = new ServerBoards(userSocket);
				boards.start(); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        if (serverSocket != null) {
	            try {
	                serverSocket.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
}

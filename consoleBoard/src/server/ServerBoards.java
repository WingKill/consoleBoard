package server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import board.Board;
import dataBase.DataBase;

public class ServerBoards extends Thread{
	private Socket socket;
	
	
	public ServerBoards(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("서버 : " + socket.getInetAddress() + " IP의 클라이언트와 연결되었습니다"); // 연결 확인용

			// InputStream - 클라이언트에서 보낸 메세지 읽기
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			// OutputStream - 서버에서 클라이언트로 메세지 보내기
			OutputStream out = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(out, true);
			
			// 데이터베이스에 접근하여 List<Post> posts 목록 채우기
			DataBase database = new DataBase(1);
			String readValue; // Client에서 보낸 값을 저장하는 String 변수
			Board board = new Board(out);
			// 작성자를 알 수 있는 번호
			String writerName = socket.getInetAddress().toString();

			writer.println("[[게시판 프로그램]]");
			board.showList();
			while ((readValue = reader.readLine()) != null) { // 클라이언트가 메세지 입력시마다 수행
				//  | 1. 전체 목록  | 2. 새 글 쓰기 | 3. 내용 보기 | 4. 글 삭제 | 5. 종료 |
				 if (readValue.equals("1")) { // 목록
					board.showList();
				} else if (readValue.equals("2")) { // 새 글 쓰기
					writer.println("-------------------------------------------------------");
					writer.println(" | 제목 | :: ");
					String title = reader.readLine();
					writer.println("-------------------------------------------------------");
					writer.print(" | 작성자 | :: ");
					writer.println(writerName);
					writer.println("-------------------------------------------------------");
					writer.println(" | 내용 | :: ");
					String mainTexts = reader.readLine();
					board.addPost(2, title, writerName, mainTexts);
				} else if (readValue.equals("3")) { // 내용 보기 및 수정
					writer.println("| 내용을 확인할 글 번호 | :: ");
					String str = reader.readLine();
					int showNum = Integer.parseInt(str);
					board.showAndUpdatePost(showNum, writerName); // 내용 보기와 수정 여부 판별	
					if(board.getUpdatePage() != 0) {
						String updatePage = reader.readLine();
						switch(updatePage) {
						case "1":
							writer.println("-------------------------------------------------------");
							writer.println(" | 제목 | :: ");
							String title = reader.readLine();
							writer.println("-------------------------------------------------------");
							writer.print(" | 작성자 | :: ");
							writer.println(writerName);							
							writer.println("-------------------------------------------------------");
							writer.println(" | 내용 | :: ");
							String mainTexts = reader.readLine();
							
							break;
						case "2":
							writer.println("취소되었습니다.");
							board.showMenu();
							break;
						default :
							writer.println("부적절한 값을 넣었습니다. 목록으로 돌아갑니다.");
							board.showList();
							break;							
						}
					}
				} else if (readValue.equals("4")) { // 삭제
					writer.println(" | 삭제할 글 번호 | :: ");
					String str = reader.readLine();
					int delNum = Integer.parseInt(str);
					board.deletePost(delNum);
				} else if (readValue.equals("5")) {
					writer.println("종료합니다.");
					break;
				} else {
					writer.println("선택한 번호의 게시판 기능이 없습니다. 다시 고르십시오.");
					board.showMenu();
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		} 
	}

}

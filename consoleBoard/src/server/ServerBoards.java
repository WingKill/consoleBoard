package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import board.Board;

public class ServerBoards extends Thread{
	private Socket socket;
	private InputStream input;
	private BufferedReader reader;
	private OutputStream out;
	private PrintWriter writer;
	// 작성자를 알 수 있는 번호
	private String writerIP; 
	private Board board;
	
	// Client에서 보낸 값을 저장하는 String 변수
	private String readValue;
	
	public ServerBoards(Socket socket) {
		this.socket = socket;
	}
	
	// 입력 텍스트
	private void insertText() throws IOException {
		writer.println("-------------------------------------------------------");
		writer.println(" | 제목 | :: ");
		
		String title = reader.readLine();
		writer.println("-------------------------------------------------------");
		writer.println(" | 작성자 | :: ");

		String name = reader.readLine();
		writer.println("-------------------------------------------------------");
		writer.println(" | 내용 | :: ");

		String mainTexts = reader.readLine();
		writer.println();
		board.addPost("B", title, name, mainTexts);
	}
	
	// 수정 텍스트
	private void updateText() throws IOException {
		writer.println("| 수정할 글 번호 | :: ");
		String updatePageStr = reader.readLine();
		Integer updatePage = Integer.parseInt(updatePageStr);
		
		// 글을 작성한 작가가 같고, 게시글이 있다면		
		if(board.wantUpdatePost(updatePage).getWriterIP().equals(writerIP) && board.wantUpdatePost(updatePage) != null ) {
			writer.println("-------------------------------------------------------");
			writer.println(" | 제목 | :: ");
	
			String title = reader.readLine();
			writer.println();
			writer.println("-------------------------------------------------------");
			writer.println(" | 작성자 | :: ");
			String name = reader.readLine();	
			writer.println();
			writer.println("-------------------------------------------------------");
			writer.println(" | 내용 | :: ");		
			String mainTexts = reader.readLine();
			writer.println();
			board.updatePost(updatePage, title, name, writerIP, mainTexts);
		}else {
			writer.println("수정 권한이 없거나 잘못된 경로로 진입했습니다. 메뉴로 돌아갑니다.");
			board.showMenu();
		}
	}
	
	@Override
	public void run() {
		try {
			System.out.println("서버 : " + socket.getInetAddress() + " IP의 클라이언트와 연결되었습니다"); // 연결 확인용

			// 클라이언트에서 보낸 메세지 읽기
			input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
			
			// 서버에서 클라이언트로 메세지 보내기
			out = socket.getOutputStream();
			writer = new PrintWriter(out, true);		
			
			writerIP = socket.getInetAddress().toString();
			board = new Board(out,writerIP);

			writer.println("--<게시판 프로그램>--");
			board.showMenu();
			while ((readValue = reader.readLine()) != null) { // 클라이언트가 메세지 입력시마다 수행
				//  | A. 전체 목록  | B. 새 글 쓰기 | C. 내용 보기 | D. 작성한 글 수정 | E. 글 삭제 | F. 종료 |  
				 if (readValue.equals("A")) { // 목록
					board.showList();
				} else if (readValue.equals("B")) { // 새 글 쓰기
					insertText();
				} else if (readValue.equals("C")) { // 내용 보기 
					writer.println("| 내용을 확인할 글 번호 | :: ");
					String str = reader.readLine();
					int showNum = Integer.parseInt(str);
					board.showAndUpdatePost(showNum, writerIP); // 내용 보기와 수정 여부 판별			
				} else if (readValue.equals("D") || readValue.equals("Y")) { // 수정
					updateText(); 
				}else if (readValue.equals("N")) {
					writer.println("취소하셨습니다. 메뉴로 돌아갑니다.");
					board.showMenu();
				} else if (readValue.equals("E")) { // 삭제
					writer.println(" | 삭제할 글 번호 | :: ");
					String str = reader.readLine();
					int delNum = Integer.parseInt(str);
					board.deletePost(writerIP,delNum);
				} else if (readValue.equals("F")) {
					writer.println("종료합니다.");
					return;
				} else{
					writer.println("선택한 게시판 기능이 없습니다. 다시 고르십시오.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		} 
	}

}

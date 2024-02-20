package board;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import board.Post;
import dataBase.DataBase;

public class Board {	
	Scanner sc = new Scanner(System.in);
	private List<Post> list;
    private PrintWriter writer;
    // 수정 시 활용할, 업데이트 페이지 선택.
    private int updatePage = 0;
    // 게시글 추가, 수정, 삭제 시 활용할 Post형 변수
    private Post post;
    
    public Board(OutputStream out) {
    	writer = new PrintWriter(out, true);
    	// 데이터베이스 목록을 가져오기 위해 먼저 데이터베이스 쓰레드 실행.
    	resetList();
    }
               
    public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public int getUpdatePage() {
		return updatePage;
	}

	public void setUpdatePage(int updatePage) {
		this.updatePage = updatePage;
	}
	
	// 메뉴
	public void showMenu() {
		writer.println("-------------------------------------------------------");
        writer.println(" | 1. 전체 목록  | 2. 새 글 쓰기 | 3. 내용 보기 | 4. 글 삭제 | 5. 종료 |");
        writer.println("-------------------------------------------------------");
    }
	
	// 데이터베이스 쓰레드를 실행하고 리스트를 받는 메서드
	private void resetList() {
		// 데이터베이스 처리
		DataBase dataBase = new DataBase(1);
		dataBase.start();
		// 리스트 대입
		list = DataBase.getPostList();
	}
	
	// 리스트
	public void showList() {
		resetList();
		writer.println("-------------------------------------------------------");
		writer.println("  | 전체 번호 | 게시글 제목 | 게시글 작성자  | 게시글 작성일 |");
		writer.println("-------------------------------------------------------");
		if(list == null) {
			writer.println("  |         |             |          |          |");
		}else {
			for(Post post : list) {
				writer.println(post.getPostNum() + " | " 
							 + post.getTitle() + " | "
							 + post.getWriter() + " | "
							 + post.getWriteDate() + " | ");
			}
		}
		showMenu();	
		serverShowList();
	}
	
	// 현 시간을 정해진 패턴으로 표현하고, 이를 String으로 변환
	private String nowDateFormatStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(new Date());
		return now;
	}
	
	// 리스트에 게시글 추가하는 과정
	public void addPost(int page,String title, String writerName, String mainTexts) {
		post = new Post(title,writerName,mainTexts);
		// 작성 날짜에 대한 패턴 지정
		post.setWriteDate(nowDateFormatStr());
		
		list.add(post);
		// 데이터베이스 처리
		DataBase dataBase = new DataBase(2, post);
		dataBase.start();
		
		writer.println("글이 추가되었습니다.");
		
		// post 초기화
		post = new Post();
		showMenu();
		serverShowList();
	}
	
	// 내용을 확인할 글을 찾고 수정하는 과정
	public void showAndUpdatePost(int showNum, String writerName) {
		if (showNum <= 0 || showNum > list.size()) {
            writer.println("해당하는 번호에 대한 글이 없습니다. 메뉴로 되돌아갑니다.");
            showMenu();
        }
		post = list.get(showNum - 1);
		writer.println("-------------------------------------------------------");
        writer.println(" | " + post.getPostNum() + " : " + post.getTitle() + " | "); 
        writer.println("-------------------------------------------------------");
        writer.println("\t\t\t" +" | " + post.getWriter() + " | "+ post.getWriteDate() + " | ");
        writer.println("-------------------------------------------------------");
        writer.println(post.getMainText());
        if(writerName.equals(post.getWriter())) {
        	writer.println("-------------------------------------------------------");
        	writer.println("\t\t\t | 1. 수정 | 2. 취소 | ");
        	writer.println("-------------------------------------------------------");
        	// 이후 입력값을 받아 진행하는 과정은 외부에서 진행한 후 다른 메서드 호출
        	return;
        }else {
        	showMenu();
        }
	}
	
	// 글을 수정하는 과정
	public void updatePost(int page, String title, String writerName, String mainTexts) {
		if(post.getWriter().equals(writerName)) {
			post.setTitle(title);
			post.setMainText(mainTexts);
			DataBase dataBase = new DataBase(page,post);
			dataBase.start();
			writer.println("수정되었습니다.");
			System.out.println(post.getPostNum()+"번 글이 " + writerName + "에 의해 수정되었습니다.");
		}else {
			writer.println("정상적인 처리가 되지 않았습니다. 메뉴로 돌아갑니다.");
		}
		// 초기화
		post = new Post();
		
		showMenu();
		serverShowList();
	}
	
	// 글을 삭제하는 과정
	public void deletePost(int delNum) {
		for(Post post : list) {
			if(post.getPostNum() == delNum)
				// 삭제 대상인 post 분류
				this.post = post; 
		}
		
		if(post == null) {
			writer.println("삭제가 진행되지 못하는 상태입니다. 메뉴로 돌아갑니다.");
			showMenu();
			return;
		}
		// 리스트에서 먼저 없애주기.
		list.remove(post);
		// 데이터베이스에서도 없애주기.
		DataBase dataBase = new DataBase(3, post);
		dataBase.start();
		writer.println("삭제되었습니다.");
		showMenu();
		serverShowList();
	}
	
	// 서버에 보여주는 리스트
	private void serverShowList() {
		System.out.println("-------------------------------------------------------");
		System.out.println("  | 전체 번호 | 게시글 제목 | 게시글 작성자  | 게시글 작성일 |");
		System.out.println("-------------------------------------------------------");
		if(list == null) {
			System.out.println("  |         |             |          |          |");
		}else {
			for(Post post : list) {
				System.out.println(post.getPostNum() + " | " 
							 + post.getTitle() + " | "
							 + post.getWriter() + " | "
							 + post.getWriteDate() + " | ");
			}
		}
	}

}

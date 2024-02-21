package board;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import board.Post;
import dataBase.Database;

public class Board {	
	Scanner sc = new Scanner(System.in);
	public static int list_numbering = 0;
	private List<Post> list;
    private PrintWriter writer;
    
    // 수정 시 활용할, 업데이트 페이지 선택.
    private int updatePage = 0;
    // 게시글 추가, 수정, 삭제 시 활용할 Post형 변수
    private Post post;
    // 게시글 추가, 수정, 삭제 시 활용할,동일 대상 유무를 확인하는 용도로 쓰이는 문자열
    private String writerIP;

    public Board(OutputStream out, String writerIP) {
    	writer = new PrintWriter(out, true);
    	this.writerIP = writerIP;
    	// 데이터베이스 목록을 가져오기 위해 먼저 데이터베이스 쓰레드 실행.
    	resetList();
    }
              
    // setters and getters
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
	
	public String getWriterIP() {
		return writerIP;
	}

	public void setWriterIP(String writerIP) {
		this.writerIP = writerIP;
	}

	// 메뉴
	public void showMenu() {
		writer.println("--------------------------------------------------------------------");
        writer.println("| A. 전체 목록  | B. 새 글 쓰기 | C. 내용 보기 | D. 작성 글 수정 | E. 글 삭제 | F. 종료 |");
        writer.println("--------------------------------------------------------------------");
    }
	
	// 데이터베이스 쓰레드를 실행하고 리스트를 받는 메서드
	private void resetList() {
		// 데이터베이스 처리
		Database dataBase = new Database("A");
		dataBase.start();
		// 리스트 대입
		list = dataBase.getPostList();
		list_numbering = list.size();
		for(Post post : list) {
			if(list_numbering < post.getPostNum()) {
				list_numbering = post.getPostNum();
			}
		}		
	}
	
	// 리스트
	public void showList() {
		resetList();
		writer.println("-------------------------------------------------------");
		writer.println("  | 전체 번호 | 게시글 제목 | 게시글 작성자  | 게시글 작성일 |");
		writer.println("-------------------------------------------------------");
		for(Post post : list) {
			writer.println(post.getPostNum() + " | " 
						 + post.getTitle() + " | "
						 + post.getWriter() + " | "
						 + post.getWriteDate() + " | ");
		}
		showMenu();	
		System.out.println("게시글 목록 보기 과정이 진행되었습니다.");
	}
	
	// 현 시간을 정해진 패턴으로 표현하고, 이를 String으로 변환 - 시간을 String으로 변환. insert, update에서 사용.
	private String nowDateFormatStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(new Date());
		return now;
	}
	
	// 수정 전, 조건에 맞는 게시글 찾기 
	public Post wantUpdatePost(int updatePage) {
		Post post = null;
		for(Post wantedPost : list) {
			if(wantedPost.getPostNum() == updatePage)
				post = wantedPost;
		}
		return post; 
	}
	
	// 리스트에 게시글 추가하는 과정
	public void addPost(String page, String title, String name, String mainTexts) {
		post = new Post(title,name,mainTexts);
		// 작성 날짜에 대한 패턴 지정
		post.setWriteDate(nowDateFormatStr());
		post.setWriterIP(writerIP);
		
		list.add(post);
		// 데이터베이스 처리
		Database dataBase = new Database(page, post);
		dataBase.start();
		
		writer.println("글이 추가되었습니다.");
		
		// post 초기화
		post = new Post();
		// 리스트 초기화
		resetList();
		System.out.println("게시글 입력 과정이 진행되었습니다.");
		showMenu();
	}
	
	// 내용을 확인할 글을 찾고 수정하는 과정
	public void showAndUpdatePost(int showNum, String writerName) {
		if (showNum <= 0 || showNum > list.size() || list == null) {
            writer.println("해당하는 번호에 대한 글이 없습니다. 메뉴로 되돌아갑니다.");
            showMenu();
            return;
        }
		
		for(Post post : list) {
			if(post.getPostNum() == showNum) {
				this.post = post;
			}
		}		
		
		writer.println("-------------------------------------------------------");
        writer.println(" | " + post.getPostNum() + " | 제목 : " + post.getTitle() + " | "); 
        writer.println("-------------------------------------------------------");
        writer.println("\t\t\t" +" | 작성자 : " + post.getWriter() + " | "+ post.getWriteDate() + " | ");
        writer.println("--------------------------내용--------------------------");
        writer.println(post.getMainText());
        if(writerName.equals(post.getWriter())) {
        	writer.println("-------------------------------------------------------");
        	writer.println("\t\t\t | Y. 수정 | N. 취소 | ");
        	writer.println("-------------------------------------------------------");
        	// 이후 입력값을 받아 진행하는 과정은 외부에서 진행한 후 다른 메서드 호출
        	return;
        }else {
        	showMenu();
        }
	}
	
	// 글을 수정하는 과정
	public void updatePost(int page, String title, String name, String writerIP,String mainTexts) {
		if(post.getPostNum() == page && this.writerIP.equals(writerIP)) {
			post.setTitle(title);
			post.setMainText(mainTexts);
			// 날짜 까먹지 않기.
			post.setUpdateDate(nowDateFormatStr());
			Database dataBase = new Database("D",post);
			dataBase.start();
			writer.println("수정되었습니다.");
			System.out.println(post.getPostNum()+"번 글이 " + writerIP + "에 의해 수정되었습니다.");
		}else {
			writer.println("정상적인 처리가 되지 않았습니다. 메뉴로 돌아갑니다.");
		}		 
		// 초기화
		post = new Post();
		resetList();
		showMenu();
	}
	
	// 글을 삭제하는 과정
	public void deletePost(String writerIP, int delNum) {
		for(Post post : list) {
			if(post.getPostNum() == delNum)
				// 삭제 대상인 post 분류
				this.post = post; 
		}
		
		if(post == null || !this.writerIP.equals(writerIP)) {
			writer.println("삭제가 진행되지 못하는 상태입니다. 메뉴로 돌아갑니다.");
			showMenu();
			return;
		}
		
		// 데이터베이스에서도 없애주기.
		Database dataBase = new Database("E", post);
		dataBase.start();
		
		writer.println("삭제되었습니다.");
		resetList();
		System.out.println(writerIP + "에 의해" + delNum + "게시글 삭제 과정이 진행되었습니다.");
		showMenu();
	}	
}

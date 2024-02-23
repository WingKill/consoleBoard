package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import board.Post;

public class Database extends Thread{
	// 서버 측에서 접근하는 데이터베이스 정보
	private static final String url = "jdbc:oracle:thin:@localhost:1521:XE";
	/*
	 * JDBC URL은 여러 가지 형식을 가질 수 있다.
	 * "jdbc:oracle:thin:@localhost:1521:XE" 
	 * "jdbc:oracle:thin:@//localhost:1521/xe"
	 * 두 가지 url 모두, Oracle 데이터베이스에 접속하기 위한 URL이며, 호스트, 포트, 서비스 이름 또는 SID를 지정한다.
	 */	 
    private static final String user = "userdata";
    private static final String password = "data123";
    
    // 데이터베이스에서 가져온 게시글 데이터들
    private static List<Post> postList = new ArrayList<Post>();
    
    // 클라이언트에서 입력하는 접근 경로
    private String page = "";
    // 결과값을 가져오기.
    private int result = -1;
    
    // 페이지 
    private Post post;
    
    // 페이지만 입력
    public Database(String page) {
    	this.page = page;
    }
    
    // 입력, 수정, 삭제에 사용
    public Database(String page, Post post) {
		this.page = page;
		this.post = post;
	}
    
    public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<Post> getPostList() {
		return postList;
	}

	public void setPostList(List<Post> postList) {
		Database.postList = postList;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
		
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	// String 형을 Timestamp형으로 변환하여 리턴하는 메서드. oracle sql이므로 Timestamp형을 썼다.
	// 이 자바 클래스 내 사용하는 Date 클래스는 java.util.Date이고 이미 import했으므로, java.sql.Date로 바꾸려면 따로 써줘야 함.
	private Timestamp strFormatTs(String dateStr) throws ParseException {
		// 들어가 있는 문자열을 Date 객체로 변환한 뒤 statement에 대입
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(dateStr);
		// Oracle sql에 넣을 것이므로 Timestamp 클래스 활용 가능
		Timestamp timestamp = new Timestamp(date.getTime());
		return timestamp;
	}
 
	@Override
	public synchronized void run() {
		// try with resource - connection close를 따로 처리하지 않아도 자동 처리
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			// page 정보 - "| A. 전체 목록  | B. 새 글 쓰기 | C. 내용 보기 | D. 작성 글 수정 | E. 글 삭제 | F. 종료 |"
			String sql = null;
			if(page.equals("A")) {
				sql = "SELECT "
					+ "postnum, "
					+ "writer, "
					+ "title, "
					+ "main_text,to_char(write_date, 'YYYY-MM-DD HH24:MI:SS') AS writedate, "
					+ "WRITER_IP "
					+ "FROM post order by postnum desc";
			}else if(page.equals("B")) {
				sql = "insert into post(POSTNUM,TITLE,WRITER,MAIN_TEXT,WRITE_DATE, WRITER_IP) values (?, ?, ?, ?, ?, ?)";
			}else if(page.equals("D")) {
				sql = "update post set TITLE = ? , MAIN_TEXT = ? , update_date = ? where postnum = ?";
			}else if(page.equals("E")) {
				sql = "delete FROM post where postnum = ?";
			}
			statement = connection.prepareStatement(sql);
			switch(page) {
			case "A" : // 목록
				/*
				 * "SELECT postnum, writer, title, main_text,
				 * to_char(write_date, 'YYYY-MM-DD HH24:MI:SS') AS writedate,
				 * WRITER_IP FROM post order by postnum desc"
				 */ 
				// 쿼리 실행
				resultSet = statement.executeQuery();
				
				// 서버에서 오류 체크하기
				if(resultSet == null) {
					System.out.println("호출 발생. \n 목록이 없거나, 처리가 되지 않았거나, 오류가 발생한 것으로 간주됩니다.");
				}else {
					System.out.println("호출 발생. 출력할 목록이 있습니다.");
				}
				postList.clear();
				while(resultSet.next()) {
					int postNum = resultSet.getInt("postnum");
					String writer = resultSet.getString("writer");
					String title = resultSet.getString("title");
					String mainText = resultSet.getString("main_text");
					String writeDate = resultSet.getString("writedate");
					String writerIP = resultSet.getString("WRITER_IP");
					if(writerIP == null) {
						writerIP = "초기값 없음";
					}
					post = new Post(postNum,title,writer,mainText,writeDate, writerIP);
					postList.add(post);
				}					
				break;
			case "B" : // 입력
			/*	insert into post(POSTNUM,TITLE,WRITER,MAIN_TEXT,WRITE_DATE, WRITER_IP) values (?, ?, ?, ?, ?, ?) */
				statement.setInt(1, post.getPostNum());
				statement.setString(2, post.getTitle());
				statement.setString(3, post.getWriter());
				statement.setString(4, post.getMainText());
				// 기존에 만들어놓은 변환 메서드 사용
				statement.setTimestamp(5, strFormatTs(post.getWriteDate()));
				statement.setString(6, post.getWriterIP());
				result = statement.executeUpdate(); 
				message(page,result);
				break;
			case "D" : // 수정
				// "update post set TITLE = ? , MAIN_TEXT = ? , update_date = ? where postnum = ?"
				statement.setString(1,post.getTitle());
				statement.setString(2,post.getMainText());
				// 기존에 만들어놓은 변환 메서드 사용
				statement.setTimestamp(3, strFormatTs(post.getUpdateDate()));
				statement.setInt(4, post.getPostNum());
				
				statement.executeUpdate();
				message(page,result);
				break;
			case "E" : // 삭제
				// "delete FROM post where postnum = ?"
				statement.setInt(1, post.getPostNum());
				statement.executeUpdate();
				message(page,result);
				break;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	private void message(String page, int result) {
		switch(page) {
		case "B" :
			System.out.println("sql insert가 진행됐습니다.");
			msgOfResult(result);
			break;
		case "D" :
			System.out.println("sql update가 진행됐습니다.");
			msgOfResult(result);
			break;
		case "E" :
			System.out.println("sql delete가 진행됐습니다.");
			msgOfResult(result);
			break;
		}
		
		if(result >= 0) {
			
		}else {
			System.out.println();
		}
		this.result = -1;
	}
	
	private void msgOfResult(int result) {
		if(result > 0) {
			System.out.println("정상 작동");
		}else {
			System.out.println("작동 실패");
		}
	}
}

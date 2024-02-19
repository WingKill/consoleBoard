package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import board.Post;

public class DataBase extends Thread{
	// 서버 측에서 접근하는 데이터베이스 정보
	private static final String url = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String user = "userdata";
    private static final String password = "data123";
    // 클라이언트에서 입력하는 접근 경로
    private int page;
    // 데이터베이스에서 가져온 게시글 데이터들
    private static List<Post> postList = new ArrayList<Post>();
    // 페이지 
    private Post post;
    // 페이지 수만 입력
    public DataBase(int page) {
    	this.page = page;
    }
    
    // 입력, 수정, 삭제에 사용
    public DataBase(int page, Post post) {
		this.page = page;
		this.post = post;
	}
    
    public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public static List<Post> getPostList() {
		return postList;
	}

	public static void setPostList(List<Post> postList) {
		DataBase.postList = postList;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	
    
	@Override
	public synchronized void run() {
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			// page 정보 - 1 : 목록, 2 : 입력, 3 : 수정, 4 : 삭제
			String sql = null;
			if(page == 1) {
				sql = boardListQuery();
			}else if(page == 2) {
				sql = insertPostQuery();
			}else if(page == 3) {
				sql = updatePostQuery();
			}else if(page == 4) {
				sql = deletePostQuery();
			}
			PreparedStatement statement = connection.prepareStatement(sql);
			switch(page) {
			case 1 : // 목록
				ResultSet resultSet = statement.executeQuery(); // 쿼리 실행
				postList.clear();
				while(resultSet.next()) {
					int postNum = resultSet.getInt("postnum");
					String subject = resultSet.getString("writer");
					String writer = resultSet.getString("title");
					String mainText = resultSet.getString("main_text");
					String writeDate = resultSet.getString("writedate");
					
					post = new Post(postNum,subject,writer,mainText,writeDate);
					postList.add(post);
				}		
				break;
			case 2 : // 입력
			/*	"insert into "
				+  "post(POSTNUM,TITLE,WRITER,MAIN_TEXT,WRITE_DATE) "
				+  "values (?, ?, ?, ?, ?);" */
				statement.setInt(1, post.getPostNum());
				statement.setString(2, post.getTitle());
				statement.setString(3, post.getWriter());
				statement.setString(4, post.getMainText());
				// 들어가 있는 문자열을 Date 객체로 변환한 뒤 statement에 대입
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = sdf.parse(post.getWriteDate());
				// Oracle sql에 넣을 것이므로 Timestamp 클래스 활용 가능
				Timestamp timestamp = new Timestamp(date.getTime());
				statement.setTimestamp(5, timestamp);
				// 쿼리 실행
				statement.executeUpdate(); 
				break;
			case 3 : // 수정
				// "update post set TITLE = ? , MAIN_TEXT = ? , update_date = sysdate where postnum = ?;"
				statement.setString(1,post.getTitle());
				statement.setString(2,post.getMainText());
				statement.setInt(3, post.getPostNum());
				statement.executeUpdate();
				break;
			case 4 : // 삭제
				// "delete FROM post where postnum = ?;"
				statement.setInt(1, post.getPostNum());
				statement.executeUpdate();
				break;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	// 모든 작성글 목록 쿼리 
	private String boardListQuery() {
		return "SELECT "
				+ "postnum,"
				+ " writer,"
				+ " title,"
				+ " main_text,"
				+ " to_char(write_date, 'YYYY-MM-DD HH24:MI:SS') AS writedate "
			 + "FROM post order by postnum desc;";
	}
	
	// insert 쿼리
	private String insertPostQuery() {	
		return "insert into "
			+  "post(POSTNUM,TITLE,WRITER,MAIN_TEXT,WRITE_DATE) "
			+  "values (?, ?, ?, ?, sysdate);";
	}
	
	// update 쿼리
	private String updatePostQuery() {
		return "update post set TITLE = ? , MAIN_TEXT = ? , update_date = sysdate where postnum = 3;";
	}
	
	// delete 쿼리
	private String deletePostQuery() {
		return "delete FROM post where postnum = ?;";
	}
}

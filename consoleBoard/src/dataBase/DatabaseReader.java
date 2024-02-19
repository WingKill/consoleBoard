package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 서버에서 데이터베이스에 접근하기 위해 만드는 쓰레드. 예제.
public class DatabaseReader implements Runnable{
	private static final String url = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String username = "userdata";
    private static final String password = "data123";

    // 데이터베이스에서 데이터를 가져오는 메서드
    public static List<String> fetchData() {
        List<String> dataList = new ArrayList<>();

        try (
            // 데이터베이스 연결
            Connection conn = DriverManager.getConnection(url, username, password);
            // SQL 문 실행을 위한 PreparedStatement 생성
        	
            PreparedStatement pstmt = conn.prepareStatement("SELECT column1 FROM my_table order by desc");
            // SQL 결과 집합을 가져오기 위한 ResultSet 생성
            ResultSet rs = pstmt.executeQuery()
        ) {
            // 결과 집합에서 데이터 읽기
            while (rs.next()) {
                String column1Value = rs.getString("column1");
                dataList.add(column1Value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataList;
    }
    
    
	@Override
	public void run() {
		List<String> data = fetchData();
	}
}

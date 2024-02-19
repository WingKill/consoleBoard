package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import board.Post;

// 예제.
public class RetrievePostsExample {

    public static void main(String[] args) {
        // 오라클 데이터베이스 연결 정보
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "your_username";
        String password = "your_password";

        List<Post> posts = new ArrayList<>();

        // 데이터베이스 연결
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // 쿼리 준비
            String sql = "SELECT post_id, author, to_char(post_date, 'YYYY-MM-DD HH24:MI:SS') AS post_date FROM your_table_name";
            PreparedStatement statement = connection.prepareStatement(sql);

            // 쿼리 실행
            ResultSet resultSet = statement.executeQuery();

            // 결과 처리
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                String author = resultSet.getString("author");
                String date = resultSet.getString("post_date");

                Post post = new Post();
                post.setPostNum(postId);
                post.setWriter(author);
                post.setWriteDate(date);

                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

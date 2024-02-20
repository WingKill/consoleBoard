package board;

import dataBase.DataBase;

public class Post {
	private static int maxNum; // 최대 게시글 
	
	private int postNum;
	private String title;
	private String writer;
	private String mainText;
	private String writeDate;
	private String updateDate;
	
	// 게시글이 전혀 없는 상태일 때 호출해 줄 생성자
	public Post() {
		
	}
	
	// 데이터베이스에서 가져와 대입하는 생성자
	public Post(int postNum,String title,String writer,String mainText,String writeDate) {
		this.postNum = postNum;
		this.title = title;
		this.writer = writer;
		this.mainText = mainText;
		this.writeDate = writeDate;
	}
	
	
	// 게시글 작성 시 사용하는 생성자
	public Post(String title,String writer,String mainText) {
		maxNum = DataBase.getPostList().size();
		this.postNum = ++maxNum;
		this.title = title;
		this.writer = writer;
		this.mainText = mainText;
	}
	
	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public int getMaxNum() {
		return maxNum;
	}
	
	public void setMaxNum(int maxNum) {
		Post.maxNum = maxNum;
	}
	
	public int getPostNum() {
		return postNum;
	}

	public void setPostNum(int postNum) {
		this.postNum = postNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getMainText() {
		return mainText;
	}

	public void setMainText(String mainText) {
		this.mainText = mainText;
	}	
}

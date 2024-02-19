package com.board;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {
 
	private int bo_no;//게시글번호.
	private String title;//제목
	private String text;//내용
	private Date w_date;//작성일
	private Date u_date;//수정일
	private String id;
	private String name;
	private int cat;
	public Board(int bo_no, String title, String text, String id) {
		super();
		this.bo_no = bo_no;
		this.title = title;
		this.text = text;
		this.id = id;
	}
	public Board(int bo_no, String title, String text, String id,int cat) {
		super();
		this.bo_no = bo_no;
		this.title = title;
		this.text = text;
		this.id = id;
		this.cat = cat;
	}
	
	
}

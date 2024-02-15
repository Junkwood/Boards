package com.board;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reply {
	private int bo_no;
	private int rn;
	private String rep_text;
	private Date rep_date;
	private String id;
	private String name;
	
	
}

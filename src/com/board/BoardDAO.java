package com.board.yedam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {
	//필드
	Connection conn;
	PreparedStatement psmt;
	ResultSet rs;
	String sql;
	String id;
	
	void disconn() {
		try {
			if(conn!=null) {
			conn.close();
			}
			if(psmt!=null) {
				psmt.close();
			}
			if(rs !=null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//중복체크
	public boolean check(String id) {
		conn=DAO.getConn();
		sql="SELECT count(*) "
				+ "FROM IDS "
				+ "WHERE id = ? ";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			rs.next();
			
			if(rs.getInt(1) >= 1) {
				return true;
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//비밀번호 체크
	public boolean checkpass(String id, String pass) {
		conn=DAO.getConn();
		sql="SELECT id, password "
				+ "FROM IDS "
				+ "WHERE ID = ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			rs.next();
			
			if(rs.getString(2).equals(pass)) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//이름반환식
	public String namegiver(String id) {
		conn=DAO.getConn();
		sql="SELECT id, name "
				+ "FROM IDS "
				+ "WHERE ID = ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			rs.next();
			id= rs.getString(2);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	//회원가입
	public boolean regiID(IDs user) {
		conn = DAO.getConn();
		sql= "INSERT INTO IDS (id, password, name) "
				+ "VALUES(?,?,?)";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, user.getId());
			psmt.setString(2, user.getPass());
			psmt.setString(3, user.getName());
			int r = psmt.executeUpdate();
			if(r>0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//회원탈퇴
	public boolean delID(String id) {
		conn = DAO.getConn();
		sql = "DELETE ids "
				+ "WHERE id = ? ";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			int r = psmt.executeUpdate();
			if(r>0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//게시글 등록
	public boolean submit(Board bo) {
		conn = DAO.getConn();
		sql = "INSERT INTO board(bo_no, title, text, id, w_date, u_date) "
				+ "VALUES(?, ?, ?, ?, TO_CHAR(sysdate,'YY-MM-DD HH24:MI:SS'), TO_CHAR(sysdate,'YY-MM-DD HH24:MI:SS'))";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, bo.getBo_no());
			psmt.setString(2, bo.getTitle());
			psmt.setString(3, bo.getText());
			psmt.setString(4, bo.getId());
			int r = psmt.executeUpdate();//처리된 건수 반환
			if(r>0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//게시글번호 자동생성기
	public int gennum() {
		int i = 0;
		conn = DAO.getConn();
		sql = "SELECT bo_no "
				+ "FROM Board "
				+ "ORDER BY bo_no desc ";
		try {
			psmt=conn.prepareStatement(sql);
			rs=psmt.executeQuery();
			if(!rs.next()) {
				return 10001;
			}
			i = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i+1;
		
	}
	//목록보기
	public List<Board> getList(int page){
		conn = DAO.getConn();
		List<Board> list = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		sql="SELECT * "
				+ "FROM(SELECT rownum rn, a.* "
				+ "FROM (SELECT bo_no, title, name, c.id id, w_date, u_date "
				+ "FROM board c join ids i "
				+ "ON (c.id = i.id) "
				+ "ORDER BY bo_no) a) b "
				+ "WHERE b.rn>(?-1)*5 and b.rn<=(?)*5 ";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, page);
			psmt.setInt(2, page);
			rs=psmt.executeQuery();
		    while(rs.next()) {
		    	Board bo = new Board();
		    	bo.setBo_no(rs.getInt("bo_no"));
		    	bo.setId(rs.getString("id"));
		    	bo.setName(rs.getString("name"));
		    	bo.setTitle(rs.getString("title"));
				bo.setW_date(sdf.parse(rs.getString("w_date")));
				bo.setU_date(sdf.parse(rs.getString("u_date")));
				list.add(bo);
	    	   }
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	//전체 카운터
	public int getTotalCnt() {
		conn = DAO.getConn();
		sql="select count(*) as cnt"
				+ " From Board";
		try {
			psmt=conn.prepareStatement(sql);
			rs=psmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
}

package com.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoardDAO {
	//필드
	static Connection conn;
	static PreparedStatement psmt;
	static ResultSet rs;
	String sql;
	String id;
	Scanner scn = new Scanner(System.in);
	
	static void disconn() {
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
		sql="select count(*)\r\n"
				+ "from ids\r\n"
				+ "where id like ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			rs.next();
			
			if(rs.getInt(1) >= 1) {
				disconn();
				return true;
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	//비밀번호 체크
	public boolean checkpass(String id, String pass) {
		conn=DAO.getConn();
		sql="SELECT id, password "
				+ "FROM IDS "
				+ "WHERE ID Like ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			rs.next();
			
			if(rs.getString(2).equals(pass)) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	//이름반환식
	public String namegiver(String id) {
		conn=DAO.getConn();
		sql="SELECT id, name "
				+ "FROM IDS "
				+ "WHERE ID like ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			rs.next();
			id= rs.getString(2);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
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
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	//회원탈퇴
	public boolean delID(String id) {
		conn = DAO.getConn();
		sql = "DELETE ids "
				+ "WHERE id like ? ";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			int r = psmt.executeUpdate();
			if(r>0) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	//게시글 등록
	public boolean submit(Board bo) {
		conn = DAO.getConn();
		sql = "INSERT INTO board(bo_no, title, text, id, w_date, u_date, category_id) "
				+ "VALUES(?, ?, ?, ?, TO_CHAR(sysdate,'YY-MM-DD HH24:MI:SS'), TO_CHAR(sysdate,'YY-MM-DD HH24:MI:SS'), nvl(?,1))";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, bo.getBo_no());
			psmt.setString(2, bo.getTitle());
			psmt.setString(3, bo.getText());
			psmt.setString(4, bo.getId());
			if(bo.getCat()==0) {
				psmt.setString(5, null);
			}else {
				psmt.setInt(5, bo.getCat());
			}
			int r = psmt.executeUpdate();//처리된 건수 반환
			if(r>0) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
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
				disconn();
				return 10001;
			}
			i = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
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
		    	if(rs.getString("title").length()>18) {
		    		bo.setTitle(rs.getString("title").substring(1,18)+"...");
		    	}else {
		    		bo.setTitle(rs.getString("title"));
		    	}
				bo.setW_date(sdf.parse(rs.getString("w_date")));
				bo.setU_date(sdf.parse(rs.getString("u_date")));
				list.add(bo);
	    	   }
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		disconn();
		return list;
	}
	//게시글 보기
	public Board getText(int page){
		conn = DAO.getConn();
		List<Board> list = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		sql="SELECT * "
				+ "FROM(SELECT rownum rn, a.* "
				+ "FROM (SELECT bo_no, title, text, name, c.id id, w_date, u_date "
				+ "FROM board c join ids i "
				+ "ON (c.id = i.id)) a "
				+ "WHERE bo_no = ?) ";
		Board bo = new Board();
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, page);
			rs=psmt.executeQuery();
			rs.next();
	    	bo.setBo_no(rs.getInt("bo_no"));
	    	bo.setId(rs.getString("id"));
	    	bo.setName(rs.getString("name"));
	    	bo.setTitle(rs.getString("title"));
	    	bo.setText(rs.getString("text"));
			bo.setW_date(sdf.parse(rs.getString("w_date")));
			bo.setU_date(sdf.parse(rs.getString("u_date")));
			list.add(bo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		disconn();
		return bo;
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
		disconn();
		return -1;
	}
	//검색 카운터
	public int getTotalCnts(String input) {
		conn = DAO.getConn();
		sql="select count(*) as cnt"
				+ " From Board"
				+ " WHERE regexp_like( upper(title) , upper(?) )";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, input);
			rs=psmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return -1;
	}
	//목록카운터
		public int getTotalCntc(int cat_id) {
			conn = DAO.getConn();
			sql="select count(*) as cnt"
					+ " From Board"
					+ " WHERE category_id = ? ";
			try {
				psmt=conn.prepareStatement(sql);
				psmt.setInt(1, cat_id);
				rs=psmt.executeQuery();
				if(rs.next()) {
					return rs.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			disconn();
			return -1;
		}
	//댓글 작성
	public boolean subRe(int bo_no, String rep_text, String id) {
		conn = DAO.getConn();
		sql = "INSERT INTO reply(bo_no, rep_text, id, rep_date) "
				+ "VALUES(?, ?, ?, TO_CHAR(sysdate,'YY-MM-DD HH24:MI:SS'))";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, bo_no);
			psmt.setString(2, rep_text);
			psmt.setString(3, id);
			int r = psmt.executeUpdate();//처리된 건수 반환
			if(r>0) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	
	//댓글불러오기
	public List<Reply> getReply(int no) {
		conn = DAO.getConn();
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		List<Reply> li = new ArrayList<>();
		sql = "SELECT rownum rn, a.*\r\n"
				+ "				FROM (SELECT rep_text,\r\n"
				+ "				             rep_date,\r\n"
				+ "				             r.id,\r\n"
				+ "				             name,\r\n"
				+ "				             r.bo_no bo_no\r\n"
				+ "				     FROM reply r JOIN board b\r\n"
				+ "				             ON (b.bo_no = r.bo_no)\r\n"
				+ "				             JOIN ids d\r\n"
				+ "				             ON (r.id = d.id)\r\n"
				+ "				     ORDER BY rep_date)a\r\n"
				+ "				WHERE bo_no = ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, no);
			rs=psmt.executeQuery();
			while(rs.next()) {
				Reply re = new Reply();
				re.setBo_no(rs.getInt("bo_no"));
				re.setId(rs.getString("id"));
				re.setName(rs.getString("name"));
				re.setRep_date(sdf.parse(rs.getString("rep_date")));
				re.setRep_text(rs.getString("rep_text"));
				re.setRn(rs.getInt("rn"));
				li.add(re);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		disconn();
		return li;
	}
	//댓글삭제
	public boolean delRe(int bo_no,int rn) {
		conn = DAO.getConn();
		sql = "delete reply\r\n"
				+ "WHERE rep_date = (\r\n"
				+ "SELECT rep_date\r\n"
				+ "FROM(\r\n"
				+ "      SELECT rownum rn, a.*\r\n"
				+ "           FROM (SELECT rep_text, rep_date, r.id, name, r.bo_no bo_no\r\n"
				+ "                 FROM reply r JOIN board b\r\n"
				+ "                             ON (b.bo_no = r.bo_no)\r\n"
				+ "                            JOIN ids d\r\n"
				+ "                            ON (r.id = d.id)\r\n"
				+ "                ORDER BY rep_date)a\r\n"
				+ "    WHERE bo_no = ?)\r\n"
				+ "WHERE rn = ?)";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, bo_no);
			psmt.setInt(2, rn);
			int r = psmt.executeUpdate();//처리된 건수 반환
			if(r>0) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	//댓삭할시 아이디 일치여부 체커
	public boolean beforeDelete(String id,int rn, int bo_no) {
		conn = DAO.getConn();
		sql = "SELECT id\r\n"
				+ "FROM reply\r\n"
				+ "WHERE rep_date = (\r\n"
				+ "SELECT rep_date\r\n"
				+ "FROM(\r\n"
				+ "      SELECT rownum rn, a.*\r\n"
				+ "           FROM (SELECT rep_text, rep_date, r.id, name, r.bo_no bo_no\r\n"
				+ "                 FROM reply r JOIN board b\r\n"
				+ "                             ON (b.bo_no = r.bo_no)\r\n"
				+ "                            JOIN ids d\r\n"
				+ "                            ON (r.id = d.id)\r\n"
				+ "                ORDER BY rep_date)a\r\n"
				+ "    WHERE bo_no = ?)\r\n"
				+ "WHERE rn=?) ";
		
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, bo_no);
			psmt.setInt(2, rn);
			rs=psmt.executeQuery();
			rs.next();
			if(rs.getString("id").equals(id)) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	//게시글 삭제전 체커
	public boolean beforeDel(String id, int bo_no) {
		conn = DAO.getConn();
		sql = "SELECT id\r\n"
				+ "FROM board\r\n"
				+ "WHERE bo_no=?";
		
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, bo_no);
			rs=psmt.executeQuery();
			rs.next();
			if(rs.getString("id").equals(id)) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	//게시글 수정
	public boolean modText(int bo_no, String text) {
		conn = DAO.getConn();
		sql = "UPDATE board"
				+ " SET text = ?, u_date = TO_CHAR(sysdate,'YY-MM-DD HH24:MI:SS') "
				+ " WHERE bo_no = ? ";
		
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, text);
			psmt.setInt(2, bo_no);
			int r = psmt.executeUpdate();//처리된 건수 반환
			if(r>0) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	//게시글 삭제
	public boolean delete(int bo_no) {
		conn = DAO.getConn();
		sql = "DELETE board"
				+ " WHERE bo_no = ? ";
		
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, bo_no);
			int r = psmt.executeUpdate();//처리된 건수 반환
			if(r>0) {
				disconn();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return false;
	}
	public List<Board> search(String input, int page){
		conn = DAO.getConn();
		List<Board> list = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		sql="SELECT * \r\n"
				+ "FROM(SELECT rownum rn, a.*\r\n"
				+ "     FROM (SELECT bo_no, title, name, c.id id, w_date, u_date \r\n"
				+ "           FROM board c join ids i \r\n"
				+ "                         ON (c.id = i.id)\r\n"
				+ "            where regexp_like(upper(title), upper(?))\r\n"
				+ "            ORDER BY bo_no) a) b\r\n"
				+ "            WHERE b.rn>(?-1)*5 and b.rn<=(?)*5";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, input);
			psmt.setInt(2, page);
			psmt.setInt(3, page);
			rs=psmt.executeQuery();
		    while(rs.next()) {
		    	Board bo = new Board();
		    	bo.setBo_no(rs.getInt("bo_no"));
		    	bo.setId(rs.getString("id"));
		    	bo.setName(rs.getString("name"));
		    	if(rs.getString("title").length()>18) {
		    		bo.setTitle(rs.getString("title").substring(1,18)+"...");
		    	}else {
		    		bo.setTitle(rs.getString("title"));
		    	}
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
		disconn();
		return list;
	}
	//댓글갯수 체커
	public int reCheck(int bo_no) {
		conn = DAO.getConn();
		sql = "select count(*)"
				+ " from reply"
				+ " where bo_no = ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, bo_no);
			rs=psmt.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public boolean isExist(int page) {
		conn = DAO.getConn();
		sql = "select bo_no "
				+ " from board"
				+ " where bo_no = ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, page);
			rs=psmt.executeQuery();
			return rs.next();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//관리자 체커
	public boolean adminChecker(String id) {
		conn = DAO.getConn();
		int admin=0;
		sql = "SELECT admin "
				+ "FROM ids "
				+ "WHERE id like ?";
		
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, id);
			rs=psmt.executeQuery();
			rs.next();
			admin = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (admin == 1) {
			return true;
		}else {
			return false;
		}
	}
	//카테고리 리스트업
	public List<Category> getCatlist(){
		conn = DAO.getConn();
		List<Category> list = new ArrayList<>();
		sql="SELECT * \r\n"
				+ "FROM (SELECT rownum rn, a.*\r\n"
				+ "     FROM (SELECT category_id, category_name \r\n"
				+ "           FROM cat \r\n"
				+ "            ORDER BY category_id) a)\r\n";
		try {
			psmt=conn.prepareStatement(sql);
			rs=psmt.executeQuery();
		    while(rs.next()) {
		    	Category cat = new Category();
		    	cat.setCat_id(rs.getInt("rn"));
		    	cat.setCat_name(rs.getString("category_name"));
		    	list.add(cat);
	    	   }
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return list;
	}
	//목록 별 리스트.
	public List<Board> catList(int cat_id, int page){
		conn = DAO.getConn();
		List<Board> list = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		sql="SELEcT *\r\n"
				+ "FROM (SELECT rownum rn, a.*\r\n"
				+ "      FROM (SELECT bo_no, title, name, d.id, w_date, u_date\r\n"
				+ "            FROM board d join ids i\r\n"
				+ "                          ON (d.id = i.id)\r\n"
				+ "            where category_id = ?\r\n"
				+ "            order by bo_no) a) b\r\n"
				+ "WherE b.rn>(?-1)*5 and b.rn<=(?)*5";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, cat_id);
			psmt.setInt(2, page);
			psmt.setInt(3, page);
			rs=psmt.executeQuery();
		    while(rs.next()) {
		    	Board bo = new Board();
		    	bo.setBo_no(rs.getInt("bo_no"));
		    	bo.setId(rs.getString("id"));
		    	bo.setName(rs.getString("name"));
		    	if(rs.getString("title").length()>18) {
		    		bo.setTitle(rs.getString("title").substring(1,18)+"...");
		    	}else {
		    		bo.setTitle(rs.getString("title"));
		    	}
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
		disconn();
		return list;
	}
	//rownum catname 변환기
	public String cat_name(int cat_id) {
		conn = DAO.getConn();
		String cat_name=null;
		sql="SELECT * \r\n"
				+ "				FROM (SELECT rownum rn, a.*\r\n"
				+ "				     FROM (SELECT category_id, category_name \r\n"
				+ "				           FROM cat \r\n"
				+ "				            ORDER BY category_id) a)\r\n"
				+ "				WHERE rn = ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, cat_id);
			rs=psmt.executeQuery();
			rs.next();
	    	cat_name = rs.getString("category_name");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		disconn();
		return cat_name;
	}
	//카테고리 생성기
	public boolean catGen(String make, int no) {
		conn = DAO.getConn();
		sql="insert into cat(category_id,category_name) "
				+ "values(?,?)";
		
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, no);
			psmt.setString(2, make);
			rs=psmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//카테고리 넘버 생성기
	public int catnoGen() {
		conn = DAO.getConn();
		sql="SELECT category_id\r\n"
				+ "FROM cat\r\n"
				+ "order by category_id desc";
		
		try {
			psmt=conn.prepareStatement(sql);
			rs=psmt.executeQuery();
			rs.next();
			return rs.getInt(1)+1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public int getCatid(int no) {
		conn = DAO.getConn();
		sql="SELECT * \r\n"
				+ "FROM (SELECT rownum rn, a.*\r\n"
				+ "     FROM (SELECT category_id, category_name \r\n"
				+ "           FROM cat \r\n"
				+ "            ORDER BY category_id) a) "
				+ "WHERE rn = ?\r\n";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setInt(1, no);
			rs=psmt.executeQuery();
			rs.next();
			return rs.getInt("category_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}return -1;
	}
	public boolean delCat(String cat_name) {
		conn = DAO.getConn();
		sql="Delete cat "
				+ "WHERE category_name like ?";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setString(1, cat_name);
			rs=psmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}return false;
	}
}

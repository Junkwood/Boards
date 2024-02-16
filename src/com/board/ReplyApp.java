package com.board;

import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ReplyApp {
	public static void ReplyApp(String id, String pass, String enter, int bo_no) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		BoardDAO bDAO = new BoardDAO();
		Scanner scn = new Scanner(System.in);
		boolean run = true;

		while (run) {
			switch (enter) {
			case "reply":// 댓글작성
				System.out.println("댓글을 입력하세요");
				System.out.println(">>>");
				String reply = scn.nextLine();
				if (bDAO.subRe(bo_no, reply, id)) {
					System.out.println("댓글등록완료");
					run = false;
					break;
				} else {
					System.out.println("등록실패");
					continue;
				}
			case "delete":// delete
				System.out.println("삭제할 댓글의 번호를 입력하세요. 취소하시려면 취소를 입력하세요");
				int rn=-1; 
				try {
				rn = scn.nextInt();
				}catch(InputMismatchException e) {
					if(scn.nextLine().equals("취소")){
						run = false;
						break;
					}else {
						System.out.println("잘못입력하셨습니다.");
						break;
					}
				}
				if(rn<=0||rn>bDAO.reCheck(bo_no)) {
					System.out.println("입력하신 번호에 해당하는 댓글이 없습니다.");
					break;
				}
				if (!bDAO.beforeDelete(id, rn, bo_no) && !bDAO.beforeDel(id, bo_no)) {
					System.out.println("댓글 삭제는 게시글 작성자와 댓글 작성자만 가능합니다.");
					break;
				}
				;
				if (bDAO.delRe(bo_no, rn)) {
					System.out.println("댓글삭제완료");
					run = false;
					break;
				} else {
					System.out.println("삭제실패");
					run = false;
					break;
				}
			case "return":// return
			case "exit":// exit
				run = false;
				break;
			case "수정":
				run = false;
				break;
			default:
				System.out.println("잘못입력하셨습니다.");
				System.out.println("다시 입력해주세요.");
				System.out.println(">>>>");
				enter = scn.nextLine();
				break;
			}
		}
	}
}

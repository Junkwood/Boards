package com.board.yedam;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class BoardApp {
	public static void BoardApp(String id, String pass) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		BoardDAO bDAO = new BoardDAO();
		Scanner scn = new Scanner(System.in);
		boolean run = true;
		int menu = 0;
		String name = bDAO.namegiver(id);
		
		System.out.println(name+"님 환영합니다!");
		while(run) {
			String title;
			String text;
			String re_text;
			System.out.println("1.게시글등록 2.게시글목록 및 조회 3.수정 4.게시글삭제 5.로그아웃");
			System.out.println(">>");
			try {
			menu=scn.nextInt();scn.nextLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
			switch(menu) {
			case 1 : //게시글등록
				System.out.println("글제목을 입력하세요");
				title = scn.nextLine();
				System.out.println("내용을 입력하세요");
				System.out.println("===================================================");
				text=scn.nextLine();
				int a = bDAO.gennum();
				Board boa = new Board(a, title, text,id);
				if(bDAO.submit(boa)) {
					System.out.println("등록완료");
				}else {
					System.out.println("등록오류");
				}
				break;
			case 2://게시글 목록 및 조회
				int page=1;
				while(true) {
				System.out.println("게시판");
				System.out.println("게시글 번호  제목                       작성자   작성일");
				System.out.println("=======================================================");
				List<Board> list = bDAO.getList(page);
				System.out.printf(" ");
				for(Board bo : list) {
					System.out.printf("%6d %20s %10s %20s\n ",bo.getBo_no(),bo.getTitle(),bo.getName(),sdf.format(bo.getW_date()));
				}
				int totalCnt = bDAO.getTotalCnt();
				int lastPage = (int) Math.ceil(totalCnt/5.0);
				for(int i=1; i<=lastPage;i++) {
					System.out.printf("%3d",i);
				}
				System.out.println();
				System.out.println("넘어갈 페이지를 입력하세요");
				System.out.println("음수입력시 처음메뉴로 돌아가며 10001이상 입력시 해당 게시글 조회합니다.");
				System.out.println(">>");
				page=scn.nextInt();scn.nextLine();
				if(page<0) {
					break;
				}else if(page>10001) {
					
				}
				}
			case 3://게시글 수정
			case 4://게시글 삭제
			case 5://로그아웃
				System.out.println(name+"님 로그아웃 되셨습니다.");
				run=false;
				break;
			}
		}
			
	}
}

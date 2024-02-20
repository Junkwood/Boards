package com.board;

import java.text.SimpleDateFormat;
import java.util.Scanner;

public class loginApp {
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Scanner scn = new Scanner(System.in);
		BoardDAO bDAO = new BoardDAO();
		int menu = 0;
		boolean run = true;
		
		while(run) {
			String id;
			String pass;
			String name;
			int c;
			System.out.println("1.로그인 2.회원가입 3.회원탈퇴 4.종료");
			System.out.println("입력>>");
			try {
			menu = Integer.parseInt(scn.nextLine());
			} catch(Exception e) {
				System.out.println("잘못된 값을 입력하셨습니다.");
				continue;
			}
			if(menu<1||menu>4) {
				System.out.println("잘못된 값을 입력하셨습니다.");
				continue;
			}
			switch(menu) {
			case 1 : //로그인
				System.out.println("아이디를 입력하세요>>");
				id = scn.nextLine();
				if(!bDAO.check(id)) {
					System.out.println("입력하신 아이디는 존재하지 않는 아이디입니다.");
					continue;
				}
				c = 0;
				while(c!=3) {
					System.out.println("비밀번호를 입력하세요>>");
					pass = scn.nextLine();
					if(bDAO.checkpass(id, pass)) {
						name=bDAO.namegiver(id);
						BoardApp.BoardApp(id, pass);
						c = 0;
						break;
					}else {
					System.out.println("비밀번호가 일치하지 않습니다.");
					c++;
					if(c==3) {
						System.out.println("비밀번호를 3회를 오입력 하셨습니다. 기본메뉴로 돌아갑니다.");
						break;
					}
					continue;
					}
				}
				break;
			case 2 : //회원가입
				System.out.println("등록하실 아이디를 입력하세요>>");
				id = scn.nextLine();
				if(bDAO.check(id)) {
					System.out.println("이미 존재하는 아이디입니다. 다시 입력해주세요.");
					continue;
				}
				if(id.equals("")) {
					System.out.println("입력된 값이 없습니다.");
					continue;
				}
				System.out.println("등록하실 비밀번호를 입력하세요>>");
				pass = scn.nextLine();
				if(pass.equals("")) {
					System.out.println("입력된 값이 없습니다.");
					continue;
				}
				System.out.println("등록하실 이름 또는 닉네임을 입력하세요>>");
				name = scn.nextLine();
				if(name.equals("")) {
					System.out.println("입력된 값이 없습니다.");
					continue;
				}
				IDs user = new IDs(id,pass,name);
				if(bDAO.regiID(user)) {
					System.out.println("회원가입완료");
				}else {
					System.out.println("오류로인해 가입이 완료되지 않았습니다.");
				}
				break;
			case 3 : //회원탈퇴
				System.out.println("탈퇴하실 아이디를 입력하세요>>");
				id = scn.nextLine();
				if(!bDAO.check(id)) {
					System.out.println("입력하신 아이디는 존재하지 않는 아이디입니다.");
					continue;
				}
				c = 0;
				while(c!=3) {
					System.out.println("비밀번호를 입력하세요>>");
					pass = scn.nextLine();
					if(bDAO.checkpass(id, pass)) {
						if(bDAO.delID(id)) {
							System.out.println("탈퇴완료");
							break;
						}
						else {
							System.out.println("탈퇴 실패");
							break;
						}
					}else {
					System.out.println("비밀번호가 일치하지 않습니다.");
					c++;
					if(c==3) {
						System.out.println("비밀번호를 3회를 오입력 하셨습니다. 기본메뉴로 돌아갑니다.");
						break;
					}
					continue;
						}
					}
				break;
			case 4 : //종료
				System.out.println("프로그램이 종료되었습니다.");
				run=false;
				scn.close();
				}
		}
	}
  }


package com.board;

import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class BoardApp {
	public static void BoardApp(String id, String pass) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		BoardDAO bDAO = new BoardDAO();
		Scanner scn = new Scanner(System.in);
		boolean run = true;
		int menu = 0;
		int page = 1;
		String name = bDAO.namegiver(id);
		String enter = null;
		System.out.println(name + "님 환영합니다!");
		while (run) {
			String title;
			String text;
			String input = null;
			System.out.println("1.게시글등록 2.게시글목록 및 조회 3.수정 4.게시글삭제 5.로그아웃 6.로그아웃 및 종료");
			System.out.println(">>");
			try {
				menu = scn.nextInt();
				scn.nextLine();
			} catch (InputMismatchException i) {
				System.out.println("입력하신 값이 숫자가 아닙니다. 다시 입력해주세요.");
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
			switch (menu) {
			case 1: // 게시글등록
				System.out.println("글제목을 입력하세요");
				title = scn.nextLine();
				System.out.println("내용을 입력하세요");
				System.out.println("===================================================");
				text = scn.nextLine();
				int a = bDAO.gennum();
				Board boa = new Board(a, title, text, id);
				if (bDAO.submit(boa)) {
					System.out.println("등록완료");
				} else {
					System.out.println("등록오류");
				}
				break;
			case 2:// 게시글 목록 및 조회
				while (true) {
					page = 1;
					System.out.println(" ___________________________________________________________________");
					System.out.println("|______________________________게시판_________________________________|");
					System.out.println("게시글 번호     제목                       작성자          작성일");
					System.out.println("====================================================================");
					List<Board> list = bDAO.getList(page);
					System.out.printf(" ");
					for (Board bo : list) {
						System.out.printf("%-8d %-25s %-10s %20s\n ", bo.getBo_no(), bo.getTitle(), bo.getName(),
								sdf.format(bo.getW_date()));
					}
					int totalCnt = bDAO.getTotalCnt();
					int lastPage = (int) Math.ceil(totalCnt / 5.0);
					System.out.println("====================================================================");
					for (int i = 1; i <= lastPage; i++) {
						System.out.printf("%3d", i);
					}
					System.out.println();
					System.out.println("====================================================================");
					System.out.println("넘어갈 페이지를 입력하세요");
					System.out.println("음수입력시 처음메뉴로 돌아가며 10001이상 입력시 해당 게시글 조회합니다.");
					System.out.println(">>");
					page = scn.nextInt();
					scn.nextLine();
					if (page < 0) {
						break;
					} else if (page >= 10001) {
						Board bo = bDAO.getText(page);
						while (true) {
							int lc;
							System.out.println(
									"================================================================================");
							System.out.println("제목: " + bo.getTitle());
							System.out.printf("작성자: %-10s  작성일자: %20s  마지막수정된 날짜:%20s", bo.getName(),
									sdf.format(bo.getW_date()), sdf.format(bo.getU_date()));
							System.out.println();
							System.out.println(
									"================================================================================");
							System.out.println("내용");
							System.out.println(
									"================================================================================");
							if (bo.getText().length() > 50) {
								lc = (int) Math.ceil(bo.getText().length()) / 50;
								for (int i = 0; i < lc; i++) {
									System.out.println(bo.getText().substring(i * 50 + 1, i * 50 + 50));
								}
							} else {
								System.out.println(bo.getText());
							}
							System.out.println(
									"================================================================================");
							System.out.println("댓글");
							System.out.println(
									"================================================================================");

							List<Reply> li = bDAO.getReply(bo.getBo_no());

							for (Reply re : li) {
								if (re.getRep_text().length() > 30) {
									lc = (int) (Math.ceil(re.getRep_text().length()) / 50);
									System.out.printf("%3d. %s : %-30s 작성일자 : %s ", re.getRn(), re.getName(),
											re.getRep_text(), sdf.format(re.getRep_date()));
									System.out.println();
									for (int i = 1; i < lc; i++) {
										System.out.println(
												"          " + re.getRep_text().substring(i * 30 + 1, i * 30 + 30));
									}
								} else {
									System.out.printf("%3d. %s : %-30s 작성일자 : %s ", re.getRn(), re.getName(),
											re.getRep_text(), sdf.format(re.getRep_date()));
									System.out.println();
								}
								System.out.println(
										"================================================================================");
							}
							System.out.println("댓글을 작성하려면 reply, ");
							System.out.println("댓글을 삭제하려면 delete, ");
							System.out.println("이전단계로 돌아가려면 return, ");
							System.out.println("처음 메뉴로 돌아가시려면 exit를 입력하세요");
							enter = scn.nextLine();
							ReplyApp.ReplyApp(id, pass, enter, page);
							if (enter.equals("exit")) {
								run = false;
								break;
							} else if (enter.equals("return")) {
								page = 1;
								break;
							}
						}
						if (enter.equals("exit")) {
							break;
						}
					}
				}
				break;
			case 3:// 게시글 목록보기 + 수정
				page = 1;
				while (true) {
					System.out.println(" ___________________________________________________________________");
					System.out.println("|______________________________게시판_________________________________|");
					System.out.println("게시글 번호     제목                       작성자          작성일");
					System.out.println("====================================================================");
					List<Board> list = bDAO.getList(page);
					System.out.printf(" ");
					for (Board bo : list) {
						System.out.printf("%-8d %-25s %-10s %20s\n ", bo.getBo_no(), bo.getTitle(), bo.getName(),
								sdf.format(bo.getW_date()));
					}
					int totalCnt = bDAO.getTotalCnt();
					int lastPage = (int) Math.ceil(totalCnt / 5.0);
					System.out.println("====================================================================");
					for (int i = 1; i <= lastPage; i++) {
						System.out.printf("%3d", i);
					}
					System.out.println();
					System.out.println("====================================================================");
					System.out.println("게시글 수정메뉴입니다.");
					System.out.println("넘어갈 페이지를 입력하세요");
					System.out.println("문자를 입력하면 제목에 해당문자가 포함된 글을 검색합니다.");
					System.out.println("음수입력시 처음메뉴로 돌아가며 10001이상 입력시 해당 게시글 수정합니다.");
					System.out.println(">>");
					try {
						page = scn.nextInt();
					} catch (InputMismatchException e) {
						e.printStackTrace();
						page=0;
						input = scn.next();
					} finally {
						scn.nextLine();
					}
					if (page == 0) {
						page = 1;
						while (true) {
							System.out.println(" ___________________________________________________________________");
							System.out.println("|______________________________게시판_________________________________|");
							System.out.println("게시글 번호     제목                       작성자          작성일");
							System.out.println("====================================================================");
							List<Board> lists = bDAO.search(input, page);
							System.out.printf(" ");
							for (Board bo : lists) {
								System.out.printf("%-8d %-25s %-10s %20s\n ", bo.getBo_no(), bo.getTitle(),
										bo.getName(), sdf.format(bo.getW_date()));
							}
							int totalCnts = bDAO.getTotalCnts(input);
							if(totalCnts==0) {
								System.out.println("검색결과가 없습니다. 게시판 첫화면으로 돌아갑니다.");
								page=1;
								break;
							}
							int lastPages = (int) Math.ceil(totalCnts / 5.0);
							System.out.println("====================================================================");
							for (int i = 1; i <= lastPages; i++) {
								System.out.printf("%3d", i);
							}
							System.out.println();
							System.out.println("====================================================================");
							System.out.println("넘어갈 페이지를 입력하세요");
							System.out.println("게시판 첫화면으로 돌아가며 10001이상 입력시 해당 게시글 수정합니다.");
							System.out.println(">>");
							page = scn.nextInt();
							scn.nextLine();
							if (page < 0) {
								page=1;
								break;
							} else if (page >= 10001) {
								if (!bDAO.beforeDel(id, page)) {
									System.out.println("게시글 수정은 작성자만 가능합니다.");
									System.out.println(name + "님은 작성자가 아닙니다.");
									continue;
								}
								System.out.println("내용을 작성해주세요.");
								text = scn.nextLine();
								if (bDAO.modText(page, text)) {
									System.out.println("게시글 수정완료.");
									page = 1;
									continue;
								} else {
									System.out.println("게시글 수정실패.");
									page = 1;
									continue;
								}
							}
						}
					} else if (page < 0) {
						break;
					} else if (page >= 10001) {
						if (!bDAO.beforeDel(id, page)) {
							System.out.println("게시글 수정은 작성자만 가능합니다.");
							System.out.println(name + "님은 작성자가 아닙니다.");
							continue;
						}
						System.out.println("내용을 작성해주세요.");
						text = scn.nextLine();
						if (bDAO.modText(page, text)) {
							System.out.println("게시글 수정완료.");
							page = 1;
							continue;
						} else {
							System.out.println("게시글 수정실패.");
							page = 1;
							continue;
						}
					}
				}
				break;
			case 4:// 게시글 삭제
				page = 1;
				while (true) {
					System.out.println(" ___________________________________________________________________");
					System.out.println("|______________________________게시판_________________________________|");
					System.out.println("게시글 번호     제목                       작성자          작성일");
					System.out.println("====================================================================");
					List<Board> list = bDAO.getList(page);
					System.out.printf(" ");
					for (Board bo : list) {
						System.out.printf("%-8d %-25s %-10s %20s\n ", bo.getBo_no(), bo.getTitle(), bo.getName(),
								sdf.format(bo.getW_date()));
					}
					int totalCnt = bDAO.getTotalCnt();
					int lastPage = (int) Math.ceil(totalCnt / 5.0);
					System.out.println("====================================================================");
					for (int i = 1; i <= lastPage; i++) {
						System.out.printf("%3d", i);
					}
					page = 0;
					System.out.println();
					System.out.println("====================================================================");
					System.out.println("게시글 삭제메뉴입니다.");
					System.out.println("넘어갈 페이지를 입력하세요");
					System.out.println("음수입력시 처음메뉴로 돌아가며 10001이상 입력시 해당 게시글 수정합니다.");
					System.out.println(">>");

					page = scn.nextInt();
					scn.nextLine();
					if (page < 0) {
						break;
					} else if (page >= 10001) {
						if (!bDAO.beforeDel(id, page)) {
							System.out.println("게시글 삭제는 작성자만 가능합니다.");
							System.out.println(name + "님은 작성자가 아닙니다.");
							continue;
						}
						if (bDAO.delete(page)) {
							System.out.println("게시글 삭제완료.");
							page = 1;
							continue;
						} else {
							System.out.println("게시글 삭제실패.");
							page = 1;
							continue;
						}
					}
				}
				break;
			case 5:// 로그아웃
				System.out.println(name + "님 로그아웃 되셨습니다.");
				run = false;
				scn.close();
				break;
			case 6:// 로그아웃 및 종료
				System.out.println(name + "님 로그아웃이 완료되었습니다");
				System.out.println("프로그램이 종료되었습니다.");
				System.exit(0);
			}
		}

	}
}

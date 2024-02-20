package com.board;

import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class BoardApp {

	public static void BoardApp(String id, String pass) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		BoardDAO bDAO = new BoardDAO();
		Scanner scn = new Scanner(System.in);
		boolean run = true;
		String name = bDAO.namegiver(id);
		int page = 1;
		String enter = null;
		String title;
		String text;
		String input = null;
		System.out.println(name + "님 환영합니다!");
		boolean admin = bDAO.adminChecker(id);
		while (run) {
			if (page < 10001) {
				System.out.println(" ______________________________________________________________________");
				System.out.println("|______________________________전체 게시글_______________________________|");
				System.out.println(" 게시글 번호     제목                       작성자          작성일  page:" + page);
				System.out.println("=======================================================================");
				List<Board> list = bDAO.getList(page);
				System.out.println();
				System.out.printf(" ");
				for (Board bo : list) {
					System.out.printf("%-8d %-25s %-10s %20s\n ", bo.getBo_no(), bo.getTitle(), bo.getName(),
							sdf.format(bo.getW_date()));
				}
				System.out.println();
				int totalCnt = bDAO.getTotalCnt();
				int lastPage = (int) Math.ceil(totalCnt / 5.0);
				System.out.println("=======================================================================");
				for (int i = 1; i <= lastPage; i++) {
					System.out.printf("%3d", i);
				}
				System.out.println();
				System.out.println("=======================================================================");
				System.out.println("게시글을 보시려면 해당 게시글번호를 입력하세요.");
				System.out.println("페이지를 넘기시려면 넘어갈 페이지를 입력하세요.");
				System.out.println("게시판에 글을 올리시려면 \"등록\", 게시글을 삭제하시려면 \"삭제\"," + "\n게시글을 찾으시려면 \"검색\", 게시판 종류를 보시려면 \"목록\","
						+ "\n로그아웃을 원하시면 \"로그아웃\", 바로 종료하시려면 \"종료\"를 입력해주세요");
				System.out.println(">>");
				try {
					page = scn.nextInt();
				} catch (InputMismatchException e) {
					page = -1;
					input = scn.next();
				} finally {
					scn.nextLine();
				}
				if (page > lastPage && page < 10001) {
					System.out.println("입력하신 페이지가 존재하지 않습니다.");
					page = 1;
					continue;
				}
			}
			if (page < 0) {
				switch (input) {
				case "등록":
					System.out.println("게시글제목을 입력하세요");
					title = scn.nextLine();
					System.out.println("글내용을 입력하세요");
					System.out.println("======================================================");
					text = scn.nextLine();
					int a = bDAO.gennum();
					Board boa = new Board(a, title, text, id);
					if (bDAO.submit(boa)) {
						System.out.println("등록완료");
						page=1;
					} else {
						System.out.println("등록오류");
						page=1;
					}
					break;
				case "검색":
					page = 1;
					System.out.println("검색하려는 단어를 입력하세요.");
					input = scn.nextLine();
					while (true) {
						System.out.println(" ___________________________________________________________________");
						System.out.println("|______________________________검색결과_______________________________|");
						System.out
								.println(" 게시글 번호     제목                       작성자          작성일  page:" + page);
						System.out.println("=======================================================================");
						System.out.println();
						System.out.printf(" ");
						List<Board> lists = bDAO.search(input, page);
						for (Board bo : lists) {
							System.out.printf("%-8d %-25s %-10s %20s\n ", bo.getBo_no(), bo.getTitle(), bo.getName(),
									sdf.format(bo.getW_date()));
						}
						int totalCnts = bDAO.getTotalCnts(input);
						if (totalCnts == 0) {
							System.out.println("검색결과가 없습니다. 게시판 첫화면으로 돌아갑니다.");
							page = 1;
							break;
						}
						System.out.println();
						int lastPages = (int) Math.ceil(totalCnts / 5.0);
						System.out.println("=======================================================================");
						for (int i = 1; i <= lastPages; i++) {
							System.out.printf("%3d", i);
						}
						System.out.println();
						System.out.println("=======================================================================");
						System.out.println("넘어갈 페이지 또는 조회할 게시글 번호를 입력하세요");
						System.out.println("이전메뉴로 돌아가시려면 return을 입력하세요.");
						System.out.println(">>");
						try {
							page = scn.nextInt();
							scn.nextLine();
						} catch (InputMismatchException e) {
							enter = scn.nextLine();
							if (enter.equals("return")) {
								page = 1;
								break;
							}
							System.out.println("숫자를 입력해주세요.");
						}
						if (page >= 10001) {
							searcht(page, id, pass);
							page = 1;
							continue;
						}
						if (page > lastPages && page < 10001) {
							System.out.println("입력하신 페이지가 존재하지 않습니다.");
							page = 1;
							continue;
						}
					}
					break;
				case "삭제":
					System.out.println("삭제할 게시글 번호를 입력하세요.");
					System.out.println(">>");
					page=scn.nextInt();scn.nextLine();
					if (bDAO.beforeDel(id, page)||bDAO.adminChecker(id)) {
						if(bDAO.delete(page)) {
							System.out.println("삭제완료");
							page=1;
							continue;
						}else {
							System.out.println("삭제실패");
							page=1;
							continue;
						}
					}else{
						System.out.println("게시글 삭제는 관리자 또는 작성자만 가능합니다.");
						System.out.println(name + "님은 관리자 또는 작성자가 아닙니다.");
						continue;
					}
				case "목록":
					page = 1;
					int cat_id = -1;
					String make = null;
					while (true) {
						List<Category> clist = bDAO.getCatlist();
						System.out.println("게시판 종류");
						for (Category cat : clist) {
							System.out.println(cat.getCat_id() + ". " + cat.getCat_name());
						}
						System.out.println("번호를 입력하시면 해당 게시판으로 이동합니다.");
						System.out.println("이전메뉴로 돌아가시려면 return을 입력하세요.");
						if (admin) {
							System.out.println("카테고리를 생성하시려면 \"생성\"을 입력하세요.(관리자 전용)");
							
						}
						System.out.println(">>");
						try {
							cat_id = scn.nextInt();
							scn.nextLine();
						} catch (InputMismatchException e) {
							make = scn.nextLine();
						}
						if (cat_id < 0) {
							if(make.equals("return")) {
								enter=null;
								break;
							}
							if (make.equals("생성")&&admin) {
								System.out.println("생성할 카테고리명을 입력하세요.");
								System.out.println(">>>");
								make = scn.nextLine();
								int no = bDAO.catnoGen();
								if (bDAO.catGen(make, no)) {
									System.out.println("카테고리생성 완료");
									continue;
								} else {
									System.out.println("카테고리생성 실패");
									continue;
								}
							}else {
								System.out.println("잘못입력하셨습니다.");
								continue;
							}
						}
						String cat_name = bDAO.cat_name(cat_id);
						if (cat_name == null) {
							System.out.println("입력하신 번호는 리스트에 없는 번호입니다.");
							System.out.println("다시 확인 후 입력부탁드립니다.");
							continue;
						}
						while (true) {
							System.out.println(" ___________________________________________________________________");
							System.out.println(
									"|____________________________" + cat_name + "게시판_______________________________|");
							System.out.println(
									"|게시글 번호     제목                       작성자          작성일  page:" + page + " |");
							System.out.println("=======================================================================");
							System.out.println();
							System.out.printf(" ");
							List<Board> list = bDAO.catList(cat_id, page);
							for (Board bo : list) {
								System.out.printf("%-8d %-25s %-10s %20s\n ", bo.getBo_no(), bo.getTitle(),
										bo.getName(), sdf.format(bo.getW_date()));
							}
							int totalCnts = bDAO.getTotalCntc(cat_id);
							if (totalCnts == 0) {
								System.out.println(cat_name+"게시판에는 작성된 글이 없습니다.");
								page = 1;
							}
							System.out.println();
							int lastPages = (int) Math.ceil(totalCnts / 5.0);
							System.out.println("=======================================================================");
							for (int i = 1; i <= lastPages; i++) {
								System.out.printf("%3d", i);
							}
							System.out.println();
							System.out.println("=======================================================================");
							System.out.println("넘어갈 페이지 또는 조회할 게시글 번호를 입력하세요");
							System.out.println("게시글을 등록하려면 \"등록\"을 입력하세요.");
							System.out.println("이전메뉴로 돌아가시려면 return을 입력하세요.");
							System.out.println(">>");
							try {
								page = scn.nextInt();
								scn.nextLine();
							} catch (InputMismatchException e) {
								enter = scn.nextLine();
								if (enter.equals("return")) {
									page = 1;
									break;
								}else if(enter.equals("등록")) {
									System.out.println("등록할 게시글제목을 입력하세요");
									title = scn.nextLine();
									System.out.println("글내용을 입력하세요");
									System.out.println("======================================================");
									text = scn.nextLine();
									int b = bDAO.gennum();
									int c = bDAO.getCatid(cat_id);
									Board boar = new Board(b, title, text, id, c);
									if (bDAO.submit(boar)) {
										System.out.println("등록완료");
										continue;
									} else {
										System.out.println("등록오류");
										continue;
									}
								}
								System.out.println("숫자를 입력해주세요.");
							}
							if (page >= 10001) {
								searcht(page, id, pass);
								page = 1;
								continue;
							}
							if (page > lastPages && page < 10001) {
								System.out.println("입력하신 페이지가 존재하지 않습니다.");
								page = 1;
								continue;
							}
						}
						if(enter.equals("return")) {
							continue;
						}
						break;
					}
					break;
				case "로그아웃":
					System.out.println(name + "님 로그아웃 되셨습니다.");
					run = false;
					break;
				case "종료":
					System.out.println(name + "님 프로그램이 종료되었습니다.");
					System.exit(0);
				default : 
					System.out.println("잘못입력 하셨습니다.");
					page=1;
					continue;
				}
			} else if (page >= 10001) {
				if (!bDAO.isExist(page)) {
					System.out.println("입력하신 번호에 해당하는 게시글이 없습니다.");
					page = 1;
					continue;
				};
				while (true) {
					Board bo = bDAO.getText(page);
					int lc;
					System.out.println(
							"===================================================================================");
					System.out.println("제목: " + bo.getTitle());
					System.out.printf("작성자: %-10s  작성일자: %20s  마지막수정된 날짜:%20s", bo.getName(),
							sdf.format(bo.getW_date()), sdf.format(bo.getU_date()));
					System.out.println();
					System.out.println(
							"===================================================================================");
					System.out.println("내용");
					System.out.println(
							"===================================================================================");
					if (bo.getText().length() > 50) {
						lc = (int) Math.ceil(bo.getText().length()) / 50;
						for (int i = 0; i < lc; i++) {
							System.out.println(bo.getText().substring(i * 50 + 1, i * 50 + 50));
						}
					} else {
						System.out.println(bo.getText());
					}
					System.out.println(
							"===================================================================================");
					System.out.println("댓글");
					System.out.println(
							"===================================================================================");

					List<Reply> li = bDAO.getReply(bo.getBo_no());

					for (Reply re : li) {
						if (re.getRep_text().length() > 30) {
							lc = (int) (Math.ceil(re.getRep_text().length()) / 50);
							System.out.printf("%3d. %s : %-30s 작성일자 : %s ", re.getRn(), re.getName(), re.getRep_text(),
									sdf.format(re.getRep_date()));
							System.out.println();
							for (int i = 1; i < lc; i++) {
								System.out.println("          " + re.getRep_text().substring(i * 30 + 1, i * 30 + 30));
							}
						} else {
							System.out.printf("%3d. %s : %-30s 작성일자 : %s ", re.getRn(), re.getName(), re.getRep_text(),
									sdf.format(re.getRep_date()));
							System.out.println();
						}
						System.out.println(
								"===================================================================================");
					}
					System.out.println("게시글 내용을 수정하시려면 \"수정\", 게시글을 삭제하려면 \"삭제\" ");
					System.out.println("댓글을 작성하려면 reply, 댓글을 삭제하려면 delete, ");
					System.out.println("이전단계로 돌아가려면 return, ");
					System.out.println("로그아웃 하시려면 exit를 입력하세요");
					scn.reset();
					enter = scn.nextLine();
					if (enter.equals("수정")) {
						if (bDAO.beforeDel(id, page)||bDAO.adminChecker(id)) {
							System.out.println("내용을 작성해주세요.");
							text = scn.nextLine();
							if (bDAO.modText(page, text)) {
								System.out.println("게시글 수정완료.");
								continue;
							} else {
								System.out.println("게시글 수정실패.");
								continue;
							}
						}else {
							System.out.println("게시글 수정은 관리자 또는 작성자만 가능합니다.");
							System.out.println(name + "님은 관리자 또는 작성자가 아닙니다.");
							continue;
						}
					} else if (enter.equals("삭제")) {
						if (bDAO.beforeDel(id, page)||bDAO.adminChecker(id)) {
							if (bDAO.delete(page)) {
								System.out.println("게시글 삭제완료.");
								page = 1;
								break;
							} else {
								System.out.println("게시글 삭제실패.");
								continue;
							}
						}else {
							System.out.println("게시글 삭제는 관리자 또는 작성자만 가능합니다.");
							System.out.println(name + "님은 관리자 또는 작성자가 아닙니다.");
							continue;
						}
					} else if (enter.equals("reply")||enter.equals("delete")) {
						ReplyApp.ReplyApp(id, pass, enter, page);
						break;
					} else if (enter.equals("exit")) {
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

	}

	public static void searcht(int page, String id, String pass) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		BoardDAO bDAO = new BoardDAO();
		Scanner scn = new Scanner(System.in);
		String name = bDAO.namegiver(id);
		String enter = null;
		String text;
		if (!bDAO.isExist(page)) {
			System.out.println("입력하신 번호에 해당하는 게시글이 없습니다.");
			page = 1;
			return;
		}
		;
		while (true) {
			Board bo = bDAO.getText(page);
			int lc;
			System.out.println("===================================================================================");
			System.out.println("제목: " + bo.getTitle());
			System.out.printf("작성자: %-10s  작성일자: %20s  마지막수정된 날짜:%20s", bo.getName(), sdf.format(bo.getW_date()),
					sdf.format(bo.getU_date()));
			System.out.println();
			System.out.println("===================================================================================");
			System.out.println("내용");
			System.out.println("===================================================================================");
			if (bo.getText().length() > 50) {
				lc = (int) Math.ceil(bo.getText().length()) / 50;
				for (int i = 0; i < lc; i++) {
					System.out.println(bo.getText().substring(i * 50 + 1, i * 50 + 50));
				}
			} else {
				System.out.println(bo.getText());
			}
			System.out.println("===================================================================================");
			System.out.println("댓글");
			System.out.println("===================================================================================");

			List<Reply> li = bDAO.getReply(bo.getBo_no());

			for (Reply re : li) {
				if (re.getRep_text().length() > 30) {
					lc = (int) (Math.ceil(re.getRep_text().length()) / 50);
					System.out.printf("%3d. %s : %-30s 작성일자 : %s ", re.getRn(), re.getName(), re.getRep_text(),
							sdf.format(re.getRep_date()));
					System.out.println();
					for (int i = 1; i < lc; i++) {
						System.out.println("          " + re.getRep_text().substring(i * 30 + 1, i * 30 + 30));
					}
				} else {
					System.out.printf("%3d. %s : %-30s 작성일자 : %s ", re.getRn(), re.getName(), re.getRep_text(),
							sdf.format(re.getRep_date()));
					System.out.println();
				}
				System.out.println("===================================================================================");
			}
			System.out.println("게시글 내용을 수정하시려면 \"수정\", 게시글을 삭제하려면 \"삭제\" ");
			System.out.println("댓글을 작성하려면 reply, 댓글을 삭제하려면 delete, ");
			System.out.println("이전단계로 돌아가려면 return을 입력하세요. ");
			enter = scn.nextLine();
			if (enter.equals("수정")) {
				if (bDAO.beforeDel(id, page)||bDAO.adminChecker(id)) {
					System.out.println("내용을 작성해주세요.");
					text = scn.nextLine();
					if (bDAO.modText(page, text)) {
						System.out.println("게시글 수정완료.");
						continue;
					} else {
						System.out.println("게시글 수정실패.");
						continue;
					}
				}else {
					System.out.println("게시글 수정은 관리자 또는 작성자만 가능합니다.");
					System.out.println(name + "님은 관리자 또는 작성자가 아닙니다.");
					continue;
				}
			} else if (enter.equals("삭제")) {
				if (bDAO.beforeDel(id, page)||bDAO.adminChecker(id)) {
					if (bDAO.delete(page)) {
						System.out.println("게시글 삭제완료.");
						page = 1;
						break;
					} else {
						System.out.println("게시글 삭제실패.");
						continue;
					}
				}else {
					System.out.println("게시글 삭제는 관리자 또는 작성자만 가능합니다.");
					System.out.println(name + "님은 관리자 또는 작성자가 아닙니다.");
					continue;
				}
			} else if (enter.equals("reply")||enter.equals("delete")) {
				ReplyApp.ReplyApp(id, pass, enter, page);
			} else if (enter.equals("return")) {
				page = 1;
				break;
			}
		}

	}
}

package com.utill;
//페이징 처리를 해주는 클래스

/*
 dataCount : 전체 데이터의 갯수(34개)
 numPerPage : 한 화면에 표시할 데이터의 갯수(3개)
 currentPage : 현재 표시할 페이지(9페이지)
 totalPage : 전체 페이지 수(12페이지)
 listUrl : 링크 설정할 url -> list.jsp가 default값임
 numPerBlock :  1 ◀이전 6 7 8 9 10 다음▶ 12 (6~10까지 표시되는 페이지의 개수: 5개)
 currentPageSetup : 이전 버튼을 클릭하면 나오는 페이지
 page : page index 숫자
 */

public class MyPage {

	//전체 페이지의 갯수 구하기
	public int getPageCount(int numPerPage, int dataCount) {

		int pageCount=0;

		pageCount = dataCount / numPerPage; // 34/3
		//전체 페이지 개수 = 전체 데이터 개수 / 한 페이지에 나오게 하려는 데이터 개수
		//딱 떨어지지 않고 나머지 값이 생기면 1개의 페이지가 더 필요함

		//한 페이지에 3개의 게시글을 보이게 할건데 전체 게시글이 34개라면, 총 페이지는 12개가 되어야 한다. 
		//그렇게 때문에 34/3에 나머지가 남는다면 페이지 개수를 하나 늘린다.
		if(dataCount % numPerPage !=0) {
			pageCount++;
		}
		return pageCount;
	}
	
	//currentPage : 현재 페이지
	//totalPage : 전체페이지(getPageCount 메소드를 통해 구해진다.)
	//listUrl : 만든 페이징을 뿌릴 값
	//페이징 처리 메소드
	public String pageIndexList(int currentPage, int totalPage, String listUrl) {
		//페이징을 하기 위해 필요한 것 : int currentPage
		
		
		//◀이전 6 7 8 9 10 다음▶
		// 이전과 다음 안에 있는 숫자의 갯수
		int numPerBlock = 5;
		
		//currentPageSetup : 이전페이지 버튼을 누르면 나오는 값
		int currentPageSetup;
		
		//for문의 i와 같은 역할을 할 것임
		//시작하는 변수가 설정되어있어야 for문을 돌릴 수 있음
		//page변수가 이와 같은 역할을 함
		int page; 

		//StringBuffer : 쓰레기값을 방지하고 String보다 처리속도가 빠름.
		//StringBuilder도 사용이 가능함
		StringBuffer sb = new StringBuffer();

		//현재페이지가 0이고, 전체페이지가 0이면 데이터가 잘못 입력됐다는 의미.
		//그러면 리턴(멈춘다)
		if(currentPage==0 || totalPage==0) {
			return "";
		}

		//돌아가는 주소 생성
		//listUrl의 default값 : list.jsp
		//만약 검색을 했다면 [list.jsp?searchKey=선택값&searchValue=입력값]이 주소가 된다.
		if(listUrl.indexOf("?")!=-1) { //?값이 있으면
			listUrl = listUrl + "&";
			//list.jsp?searchKey=subject&searchValue=111&pageNum=3
			//?값이 있다면 기본 조건값이 있다는 것이기 때문에 그 이후에 또 다른 조건값은 &을 붙여줘야한다.
		} else {
			listUrl = listUrl + "?";
			//list.jsp?pageNum=3
			//?가 아직 없다면 뒤에 나와야할 기본주소값을 붙이지 않은 것임. 때문에 ?값을 줘야한다.
		}
		/*주소에 ?가 없지않으면(=있으면) 주소 끝에 &를 붙인다.(뒤에 페이징 조건을 걸기 위해서)
		주소에 ?가 없으면 ?를 붙여준다.list.jsp? 처럼*/
		
		
		/*
		 ◀이전 6 7 8 9 10 다음▶ 에서 currentPageSetup 는 이전을 눌렀을 때 뜨는 페이지에 해당한다.

		이전버튼페이지 = (현재페이지/페이지선택개수) * 페이지선택개수
		(현재페이지/페이지선택개수)는 변수가 int 타입이기 때문에 몫이 결과값이 된다.
		(페이지선택개수 : 여기서는 5)
		
		이전버튼페이지가 0보다 작으면 ◀이전 이 출력될 필요가 없다. (1 2 3 4 5 다음▶)
		이 경우에는 아래의 ◀이전 구하기 코드를 수행하지 않고 건너뛴다.
		 */
		/*코드 틀렸었어 멍청아!!!!!!!!!!!!!!!!! currentPageSetup을 currentPage로 함ㅜㅜㅠㅠㅠㅠㅠㅠ*/
		currentPageSetup = (currentPage/numPerBlock)*numPerBlock;

		//이전의 값을 구하는 공식
		//currentPage를 numPerBlock로 나눈 나머지 값이 
		//0이면 문제가 발생(다시 본인에게 돌아옴)
		/*
		 현재페이지를 페이지선택개수로 나눈 나머지가 0이면
		이전버튼페이지는 이전페이지에서 페이지선택개수(5)를 뺀 값이 된다.
		이 조건을 주지 않으면, 나머지가 0일때 이전버튼페이지는 현재페이지가 된다. 본인에게 돌아온다.
		(현재페이지가 10일때 이전버튼페이지는 10이 된다. -> 2(10/5의 몫)*5=10)
		 */
		if(currentPage % numPerBlock == 0) {
			currentPageSetup = currentPageSetup - numPerBlock;
		}

		//◀이전 구하기
		if(totalPage>numPerBlock && currentPageSetup>0) {

			//append = 누적 
			sb.append("<a href=\""+ listUrl + "pageNum="
					+ currentPageSetup + "\">◀이전</a>&nbsp;");
			//<a href="list.jsp?pageNum=5">◀이전</a>&nbsp;의 코딩
			//이전을 누르면 5페이지로 넘어간다.

		}

		// 1 2 3 4 5 다음▶
		//◀이전 6 7 8 9 10 다음▶를 구하는 공식
		//바로가기 페이지, 시작값을 정해야함
		page = currentPageSetup + 1; 
		//무조건 시작하는 페이지를 출력
		//이전버튼페이지는 0이 될 수도 있는데 (현재페이지가 1~4일 경우) 페이지는 0값이 나오면 안되니까 +1을 해주어야 한다.

		//내가 찍고자 하는 페이지가 전체 페이지보다 작거나 같고
		//내가 내보내려하는 번호보다 작거나 같을때까지 값을 찍는다.
		while(page<=totalPage && page <= (currentPageSetup + numPerBlock)) {
			//조건값이 true인 경우 = 남은 page가 더 있는 것이다.
			//남아있는 페이지가 있으면 계속 while문을 돌릴 것이다.
			/* page<=totalPage가 있는 이유? page가 전체 페이지보다 작아야한다.
			 11, 12 페이지의 경우 numPerBlock의 값이 
			 꽉 채워지지 않은채로 화면에 보인다.
			 즉 13, 14, 15의 값이 나왔을때 그 값을 출력하지 못하게하는 것이다.*/

			//내가 현재 보고 있는 페이지와 동일하면 링크를 연결하지 말고 색만 바꿔준다.
			//페이지를 이동할 필요가 없기 때문임
			if(page == currentPage) {
				//위 코딩과 계속 누적해서 보여줌
				sb.append("<font color=\"Fuchsia\">" +
						page + "</font>&nbsp;");
				//<font color="Fuchsia">9</font>&nbsp;
			} else {
				sb.append("<a href=\"" + listUrl + "pageNum="
						+ page + "\">" + page + "</a>&nbsp;");
				//<a href="list.jsp?pageNum=8">8</a>&nbsp;
			}

			page++;

		}
		/*
		시작값이 전체페이지보다 작거나 같고, 시작값이 이전버튼페이지+페이지선택개수보다 작거나 같으면 반복한다.
 		다음▶ 을 출력하기 위한 조건
 		여기서는 1~10페이지까지 해당된다.
		 */

		//◀이전 6 7 8 9 10 다음▶
		//◀이전 11 12
		//다음이 보이게 출력, but 마지막페이지에서는 다음이 안보이도록 하는 코드
		if(totalPage-currentPageSetup>numPerBlock) {
			//만약 이 조건이 성립 되지 않으면 else값이 없기 때문에 아무것도 출력이 되지 않는다.

			sb.append("<a href=\"" + listUrl + "pageNum="
					+ page + "\">다음▶</a>&nbsp;");
			//<a href="list.jsp?pageNum=11">다음▶</a>&nbsp;
		}

		return sb.toString();
		
	}	
	
	//자바스크립트로 페이징 처리
		public String pageIndexList(int currentPage, int totalPage) {
			
			int numPerBlock = 5;
			int currentPageSetup;
			
			int page;
			String strList = "";
			
			if(currentPage==0) {
				return "";
			}
			
			//표시할 첫페이지
			currentPageSetup = (currentPage/numPerBlock)*numPerBlock;
			if(currentPage%numPerBlock==0) {
				currentPageSetup = currentPageSetup - numPerBlock;
			}
			
			//◀이전
			if((totalPage>numPerBlock)&&(currentPageSetup>0)){
				strList = "<a onclick='listPage(" 
						+ currentPageSetup + ");'>◀이전</a>&nbsp;";
			}
			
			//페이지
			page = currentPageSetup + 1;
			
			while((page<=totalPage)&&(page<=currentPageSetup+numPerBlock)) {
				
				if(page==currentPage) {
					strList += "<font color='gray'>" + page + "</font>&nbsp;";
				}else {
					strList += "<a onclick='listPage(" +
							page + ");'>" + page + "</a>&nbsp;";
				}
				
				page++;
				
			}
			
			//다음▶
			if(totalPage-currentPageSetup>numPerBlock) {
				strList += "<a onclick='listPage(" + page + ");'>다음▶</a>&nbsp;";
			}
			
			return strList;
			
		}
		
		
		

	}

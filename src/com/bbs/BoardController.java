package com.bbs;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.util.dao.CommonDAO;
import com.utill.MyPage;

//보낼때(넣을때) 인코딩
//받을때 디코딩(equalsIgnoreCase가 위에 붙어있다.)

@Controller("bbs.boardController")
public class BoardController {
	
	/*iBatis의 DB를 액세스 할 것이기 때문에 dao가 필요함.
	dao의 객체를 생성해둔 장소는 CommonDAOImpl에 있음
	불러낸 dao를 CommonDAO에 집어넣어주면 dao에 객체가 들어가기 때문에 
	CommonDAO에 넣은 dao의 값을 사용할 수 있게 된다. */
	
	@Resource(name="dao")
	private CommonDAO dao;
	
	/*com.utildml myPage를 받아내는 일반적인 클래스임
	 @service로 객체를 생성하고 @resource로 값을 받아낸다*/
	
	@Resource(name="myPage")
	private MyPage myPage;
	
	//메소드 생성(/bbs/created.action주소값이 들어오면 사용됨)
	@RequestMapping(value="/bbs/created.action",
			method= {RequestMethod.GET,RequestMethod.POST})
	public String created(BoardCommand command,
			HttpServletRequest request) throws Exception{
		
		//command나 mode가 null이면 창만 띄우면 된다.
		if(command==null||command.getMode()==null||
				command.getMode().equals("")) {
			
			//mode라는 변수에 insert라는 문장을 넣을 것이다.
			request.setAttribute("mode", "insert");
			
			//보여지는 view에는 board/created를 띄운다.
			//mode의 insert를 가지고 created에게 간다.
			return "board/created";
		}
		
		int boardNumMax = dao.getIntValue("bbs.maxBoardNum");
		
		command.setBoardNum(boardNumMax + 1);
		command.setIpAddr(request.getRemoteAddr());
		
		//bbs.insertData에 command에 있는 데이터를 넣어준다.
		dao.insertData("bbs.insertData", command);
		
		//데이터를 입력했으면 redirect해준다.
		return "redirect:/bbs/list.action";
	}

	//redirect로 list를 받아오기 때문에 list메소드를 생성해야함 
	@RequestMapping(value="/bbs/list.action",
			method= {RequestMethod.GET,RequestMethod.POST})
	public String list(HttpServletRequest request) throws Exception{
		
		HttpSession session = request.getSession();
		String cp = request.getContextPath();
		
		int numPerPage = 5;
		int totalPage = 0;
		int totalDataCount = 0;
		
		String pageNum = request.getParameter("pageNum");
		
		//아래 수정,삭제 메소드에서 보낸 pageNum을 받을 것임.
		if(pageNum==null) {
			pageNum = (String)session.getAttribute("pageNum");
		}
		session.removeAttribute("pageNum");
		
		int currentPage = 1;
		
		if(pageNum!=null && !pageNum.equals("")) {
			currentPage = Integer.parseInt(pageNum);
		}
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue==null) {
			searchKey = "subject";
			searchValue = "";
		}
		
		if(request.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}

		Map<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("searchKey", searchKey);
		hMap.put("searchValue", searchValue);
		
		//전체 데이터를 불러옴
		totalDataCount = dao.getIntValue("bbs.dataCount", hMap);
		
		if(totalDataCount!=0) {
			totalPage = myPage.getPageCount(numPerPage, totalDataCount);
		}

		if(currentPage>totalPage) {
			currentPage = totalPage;
		}
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		hMap.put("start", start);
		hMap.put("end", end);
		
		List<Object> lists = 
				(List<Object>)dao.getListData("bbs.listData",hMap);
		
		//일렬번호 생성
		int listNum,n=0;
		Iterator<Object> it = lists.iterator();
		while(it.hasNext()) {
			
			//꺼내려는 데이터가 object이기 때문에 반드시 다운캐스팅 해줘야함
			BoardCommand vo = (BoardCommand)it.next();
			
			listNum = totalDataCount - (start + n - 1);
			vo.setListNum(listNum);
			
			n++;
			
		}
		
		//주소 정리
		String params = "";
		String urlList = "";
		String urlArticle = "";
		
		//검색을 한 경우
		if(!searchValue.equals("")) {
			//한글 인식 가능하게함
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			
			params = "searchKey=" + searchKey +
					"&searchValue=" + searchValue;
			
		}
		
		//검색을 하지 않은 경우  경로
		if(params.equals("")) {
			
			urlList = cp + "/bbs/list.action";
			urlArticle = cp + "/bbs/article.action?pageNum=" + currentPage;
			
		} else { //검색을 한 경우 경로
			
			urlList = cp + "/bbs/list.action?" + params;
			urlArticle = cp + "/bbs/article.action?pageNum=" +
					currentPage + "&" + params;
			
		}
		
		//model만들기
		request.setAttribute("lists", lists);
		request.setAttribute("urlArticle", urlArticle);
		request.setAttribute("pageNum", currentPage);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("totalDataCount", totalDataCount);
		request.setAttribute("pageIndexList",
				myPage.pageIndexList(currentPage, totalPage, urlList));
		
		//board의 list.jsp로 가라
		return "board/list";
	}

	@RequestMapping(value="/bbs/article.action",
			method= {RequestMethod.GET,RequestMethod.POST})
	public String article(HttpServletRequest request) throws Exception{
		
		int boardNum = Integer.parseInt(request.getParameter("boardNum"));
		String pageNum = request.getParameter("pageNum");
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue==null) {
			searchKey = "subject";
			searchValue = "";
		}
		
		if(request.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		}
		
		dao.updateData("bbs.hitCountUpdate", boardNum);
		
		BoardCommand dto = (BoardCommand)dao.getReadData("bbs.readData", boardNum);
		
		int lineSu = dto.getContent().split("\r\n").length;
		
		dto.setContent(dto.getContent().replaceAll("\r\n", "<br/>"));
		
		Map<String, Object> hMap = new HashMap<>();
		
		hMap.put("searchKey", searchKey);
		hMap.put("searchValue", searchValue);
		hMap.put("boardNum", boardNum);
		
		BoardCommand preReadData = 
				(BoardCommand)dao.getReadData("bbs.preReadData", hMap);
		
		int preBoardNum = 0;
		String preSubject = "";
		if(preReadData!=null) {
			preBoardNum = preReadData.getBoardNum();
			preSubject = preReadData.getSubject();
		}
		
		BoardCommand nextReadData = 
				(BoardCommand)dao.getReadData("bbs.nextReadData", hMap);
		
		int nextBoardNum = 0;
		String nextSubject = "";
		if(nextReadData!=null) {
			nextBoardNum = nextReadData.getBoardNum();
			nextSubject = nextReadData.getSubject();
		}
		
		String params = "pageNum=" + pageNum;
		if(!searchValue.equals("")) {
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			params += "&searchKey=" + searchKey +
					"&searchValue=" + searchValue;
					
		}
		
		request.setAttribute("dto", dto);
		request.setAttribute("preBoardNum", preBoardNum);
		request.setAttribute("preSubject", preSubject);
		request.setAttribute("nextBoardNum", nextBoardNum);
		request.setAttribute("nextSubject", nextSubject);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("params", params);
		request.setAttribute("lineSu", lineSu);
		
		return "board/article";
	}
	
	@RequestMapping(value="/bbs/deleted.action",
			method= {RequestMethod.GET,RequestMethod.POST})
	public String deleted(HttpServletRequest request) throws Exception{
		
		int boardNum = Integer.parseInt(request.getParameter("boardNum"));
		String pageNum = request.getParameter("pageNum");
		
		dao.deleteData("bbs.deleteData", boardNum);
		
		/*
		HttpSession session = request.getSession();
		session.setAttribute("pageNum", pageNum);
		
		return "redirect:/bbs/list.action";
		*/
		
		/*
		메소드의 매개변수부분에 httpSession session에 넣어주면된다.
		session = request.getSession();
		session.setAttribute("pageNum", pageNum);
		
		return "redirect:/bbs/list.action";
		 */
		return "redirect:/bbs/list.action?pageNum=" + pageNum;
	}	
	
	
	@RequestMapping(value="/bbs/updated.action",
			method= {RequestMethod.GET})
	public String updateForm(HttpServletRequest request) throws Exception{
		
		int boardNum = Integer.parseInt(request.getParameter("boardNum"));
		String pageNum = request.getParameter("pageNum");
		System.out.println(boardNum);
		BoardCommand dto =
				(BoardCommand)dao.getReadData("bbs.readData",boardNum);
		
		request.setAttribute("dto", dto);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("mode", "update");
		
		return "board/created";
	}	
	
	//실제로 수정이 되는 데이터
	@RequestMapping(value="/bbs/updated.action",
			method= {RequestMethod.POST})
	public String updateSubmit(BoardCommand command,
			HttpServletRequest request) throws Exception{
		
		dao.updateData("bbs.updateData", command);
		
		return "redirect:/bbs/list.action?pageNum=" + command.getPageNum();
		
	}
		
}

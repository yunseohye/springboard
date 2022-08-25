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
	
	@Resource(name="dao")
	private CommonDAO dao;
	
	@Resource(name="myPage")
	private MyPage myPage;
	
	@RequestMapping(value="/bbs/created.action",
			method= {RequestMethod.GET,RequestMethod.POST})
	public String created(BoardCommand command,
			HttpServletRequest request) throws Exception{
		
		if(command==null||command.getMode()==null||
				command.getMode().equals("")) {
			
			request.setAttribute("mode", "insert");
			
			return "board/created";
		}
		
		int boardNumMax = dao.getIntValue("bbs.maxBoardNum");
		
		command.setBoardNum(boardNumMax + 1);
		command.setIpAddr(request.getRemoteAddr());
		
		dao.insertData("bbs.insertData", command);
		
		return "redirect:/bbs/list.action";
	}

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
			
			BoardCommand vo = (BoardCommand)it.next();
			
			listNum = totalDataCount - (start + n - 1);
			vo.setListNum(listNum);
			
			n++;
			
		}
		
		//주소 정리
		String params = "";
		String urlList = "";
		String urlArticle = "";
		
		if(!searchValue.equals("")) {
			
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			
			params = "searchKey=" + searchKey +
					"&searchValue=" + searchValue;
			
		}
		
		
		
		if(params.equals("")) {
			
			urlList = cp + "/bbs/list.action";
			urlArticle = cp + "/bbs/article.action?pageNum=" + currentPage;
			
		} else {
			
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
		return "redirect:/bbs/list.action?pageNum=" + pageNum;
	}	
	
}

package com.anno;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 빈 객체 생성, 다른 패키지와 합쳤을 때 
 * 이름이 같으면 충돌이 날 수 있기 때문에
 * 패키지 명을 적어주어 구분을 짓는다.
 */

@Controller("anno.testController")
public class TestController {
	
	//GET방식 (value의 주소가 get방식으로 왔을 때만 띄워줘라)
	@RequestMapping(value="/demo/write.action", method= {RequestMethod.GET})
	public String write() throws Exception{
		
		return "anno/created";
		
	}

	@RequestMapping(value="/demo/write.action", method= {RequestMethod.POST})
	public String write_ok(TestCommand command,HttpServletRequest request) {
		
		String message = "아이디: " + command.getUserId();
		message += ", 이름: " + command.getUserName();
		
		request.setAttribute("message", message);
		
		return "anno/result";
		
	}
	
	
	@RequestMapping(value="/demo/save.action", 
			method= {RequestMethod.POST,RequestMethod.GET})
	public String save(TestCommand command,HttpServletRequest request) {
		
		if(command==null || command.getMode()==null || command.getMode().equals("")) {
			return "anno/test";
		}
		
		String message = "아이디: " + command.getUserId();
		message += ", 이름: " + command.getUserName();
		
		request.setAttribute("message", message);
		
		return "anno/result";
		
	}
	
	@RequestMapping(value="/demo/demo.action", 
			method= {RequestMethod.POST,RequestMethod.GET})
	public String demo(String userId, String userName, String mode,
			HttpServletRequest request) {
		
		if(mode==null || mode.equals("")) {
			return "anno/demo";
		}
		
		String message = "아이디: " + userId;
		message += ", 이름: " + userName;
		
		request.setAttribute("message", message);
		
		return "anno/result";
		
	}
	
}

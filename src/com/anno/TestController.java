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
		
		//처음에는 아무 값도 없기 때문에 창만 띄우면 된다.
		if(command==null || command.getMode()==null || command.getMode().equals("")) {
			return "anno/test";
		}
		
		//데이터가 들어가고 난 후 보여질 값 
		String message = "아이디: " + command.getUserId();
		message += ", 이름: " + command.getUserName();
		
		request.setAttribute("message", message);
		
		return "anno/result";
		
	}
	
	//하나하나 매개변수로 데이터를 받는 방식
	@RequestMapping(value="/demo/demo.action", 
			method= {RequestMethod.POST,RequestMethod.GET})
	public String demo(String userId, String userName, String mode,
			HttpServletRequest request) {
		
		//command를 매개변수로 적용하지 않았기 때문에 mode만 검사
		if(mode==null || mode.equals("")) {
			return "anno/demo";
		}
		
		//mode가 null이 아니면 데이터를 넣었다는 것임
		
		//사용자에게 직접 값을 받았기 때문에 command.get이 필요없음,
		//변수로 받았기 때문에 ()생략
		String message = "아이디: " + userId;
		message += ", 이름: " + userName;
		
		request.setAttribute("message", message);
		
		return "anno/result";
		
	}
	
}

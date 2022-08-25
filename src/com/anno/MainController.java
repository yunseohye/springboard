package com.anno;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("com.main")
//("com.anno.mainController") 
//-> 보통은 패키지명으로 컨트롤러의 이름을 지정해 다른 패키지와 충돌을 막는다.
@RequestMapping(value="/main.action")
public class MainController {
	//MainController com.anno.mainController = new MainController(); -> controller가 없었을때 사용법
	
	//Get방식으로 왔을 때만 실행한다.
	@RequestMapping(method=RequestMethod.GET)
	public String method() {
		
		return "/index";
	}
	
}

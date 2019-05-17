package cn.yunrui.intfirectrlsys.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/interfaceControl")
public class InterfaceControl {

	
	public void executionInterface() {
		
		System.out.println("接口调通了吗？？？");
	}
	
	
	
}

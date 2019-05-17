package cn.yunrui.intfirectrlsys.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//运维效率统计分析
@Controller
@RequestMapping("/YWXLTJControl")
public class YWXLTJControl {
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String wg(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/Efficiency_analysis";
	}

}

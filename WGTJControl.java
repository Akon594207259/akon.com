package cn.yunrui.intfirectrlsys.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//网格化数据统计 
/**
 * 
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/WGTJControl")
public class WGTJControl {
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String wg(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/WGTJ";
	}

}

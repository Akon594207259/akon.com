package cn.yunrui.intfirectrlsys.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//到期消防设备统计   /DQXFSBTJControl/init.htm

@Controller
@RequestMapping("/DQXFSBTJControl")
public class DQXFSBTJControl {


@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
public String wg(HttpServletRequest request, HttpServletResponse response) {
	return "ifcs/DQSBTJ";
}

}

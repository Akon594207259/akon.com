package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.CodeEntitywj;
import cn.yunrui.intfirectrlsys.service.IfcsMapService;
import cn.yunrui.intfirectrlsys.service.impl.DlygServiceImpl;

@Controller
@RequestMapping("/ifcsMap")
public class IfcsMapControl {

	private static boolean iflocal = true;

	@Resource(name = "ifcsMapService")
	private IfcsMapService ifcsMap;

	@Resource(name = "ifcsdlygservice")
	private DlygServiceImpl service;

	/*
	 * @RequestMapping(value = "/init.htm", method = RequestMethod.GET) public
	 * String init(HttpServletRequest request, HttpServletResponse response) {
	 * return "ifcs/TestReact"; }
	 */

	// 主页概览进入地图jsp
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String zymaporig(Model model) {

		Authentication auth = RequestContextSecurity.getAuthentication();
		ArrayList<CodeEntitywj> xfyhlist = service.getXfyhList2(
				auth.getBureauNo(), auth.getOrgNo());
		String rst = JSONArray.fromObject(xfyhlist).toString();
		model.addAttribute("xfyhlist", rst);
		System.out.println("xfyhlist" + rst);
		return "ifcs/TestReact";
	}

	/**
	 * 搜索用户=======================================================================================
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/mapList.htm")
	public void mapList(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		Authentication auth = RequestContextSecurity.getAuthentication();
		String buro = auth.getBureauNo();

		String name = request.getParameter("name");
		/* String sex = request.getParameter(buro); */
		List list = ifcsMap.mapList(name, buro);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}

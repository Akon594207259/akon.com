package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.util.Util;

@Controller
@RequestMapping("/homePage")
public class HomePage {

	private static boolean iflocal = Util.ifbd;

	/**
	 * 获取权限
	 */
	@RequestMapping(value = "/getPermissions1.htm")
	public void getPermissions(HttpServletRequest request,
			HttpServletResponse response) {
		Map srt = new HashMap();
		Authentication auth = RequestContextSecurity.getAuthentication();

		String bureauNo = "";
		String orgNo = "";
		if (auth != null) {
			bureauNo = auth.getBureauNo();
			orgNo = auth.getOrgNo();
		}

		bureauNo = (bureauNo == null) ? "" : bureauNo.trim();
		orgNo = (orgNo == null) ? "" : orgNo.trim();
		
		srt.put("bureauNo", bureauNo);
		srt.put("orgNo", orgNo);
		
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin","http://localhost:8888");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(srt)).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * public String getBureauNo() { String bureauNo = ""; Authentication auth =
	 * RequestContextSecurity.getAuthentication();
	 * 
	 * if(auth !=null){ bureauNo = auth.getBureauNo();
	 * 
	 * } return bureauNo; }
	 * 
	 * 
	 * public String getOrgNo() { String orgNo = ""; Authentication auth =
	 * RequestContextSecurity.getAuthentication();
	 * 
	 * if(auth !=null){ orgNo = auth.getOrgNo();
	 * 
	 * } return orgNo; }
	 */
}

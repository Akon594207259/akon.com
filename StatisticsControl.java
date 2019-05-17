package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.Header;
import cn.yunrui.intfirectrlsys.service.StatisticsService;
import cn.yunrui.intfirectrlsys.util.ExcelUtil;




@Controller
@RequestMapping("/statistics")
public class StatisticsControl {
	@SuppressWarnings("unused")
	private static boolean iflocal = true;

	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String init(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/statistics";
	}
	
	@SuppressWarnings("unused")
	@Resource(name = "statisticsservice")
	private StatisticsService service;
	
	// 查询所有的消防用户
		@RequestMapping(value = "/getXfyhListAtion.htm", method = RequestMethod.POST)
		public void getXfyhListAtion(HttpServletRequest request,
				HttpServletResponse response) {

			Authentication auth = RequestContextSecurity.getAuthentication();
			@SuppressWarnings("unchecked")
			ArrayList<CodeEntity> xfyhlist = (ArrayList<CodeEntity>) service
					.getXfyhListCommon(auth.getBureauNo(), auth.getOrgNo());
			String rst = JSONArray.fromObject(xfyhlist).toString();
			try {
				response.setContentType("application/json; charset=utf-8");

				if (iflocal) {
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}

				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	/**
	 * 按单位查询的导出
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/dwExportxls.htm", method = RequestMethod.GET)
	public void dwExportxls(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		String year = request.getParameter("year");
		String xfyh = request.getParameter("xfyh");
		String xcxz = request.getParameter("xcxz");
		String filename = "单位统计";	
		List<Header> header = this.getdwExportxlsHeader();
	
		@SuppressWarnings("rawtypes")
		List end_result = new ArrayList<ArrayList>();
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = service.getDWTJALL(year,xfyh,xcxz);
		 Map<String,Object> map = new HashMap<String, Object>();
		 map = list.get(0);
		
		@SuppressWarnings("rawtypes")
		ArrayList result = new ArrayList();
		 result.add(0,map.get("xfyh"));
		 result.add(1,map.get("yyzs"));
		 result.add(2,map.get("yyzxs"));
		 result.add(3,map.get("eyzs"));
		 result.add(4,map.get("eyzxs"));
		 result.add(5,map.get("syzs"));
		 result.add(6,map.get("syzxs"));
		 result.add(7,map.get("siyzs"));
		 result.add(8,map.get("siyzxs"));
		 result.add(9,map.get("wyzs"));
		 result.add(10,map.get("wyzxs"));
		 result.add(11,map.get("lyzs"));
		 result.add(12,map.get("lyzxs"));
		 result.add(13,map.get("qyzs"));
		 result.add(14,map.get("qyzxs"));
		 result.add(15,map.get("byzs"));
		 result.add(16,map.get("byzxs"));
		 result.add(17,map.get("jyzs"));
		 result.add(18,map.get("jyzxs"));
		 result.add(19,map.get("shiyzs"));
		 result.add(20,map.get("shiyzxs"));
		 result.add(21,map.get("syyzs"));
		 result.add(22,map.get("syyzxs"));
		 result.add(23,map.get("seyzs"));
		 result.add(24,map.get("seyzxs"));
		 
		 end_result.add(0,result);
		try {
			ExcelUtil.hbtoExcel(filename, header,end_result, false, response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 按单位查询的导出的获取头的方法
	 */
	public List<Header> getdwExportxlsHeader()
			throws Exception {	
		List<Header> header = new ArrayList<Header>();
		
		String[] name = {"消防用户","一月","","二月","","三月","","四月","","五月","","六月","","七月","","八月","","九月","","十月","","十一月","","十二月","",};
		String[] DataIndex = {"xfyh","yy","k1","ey","k2","sany","k3","siy","k4","wy","k5","ly","k6","qy","k7","by","k8","jy","k9","shiy","k10","shiyiy","k11","shiery","k12"};
		for (int i = 0; i < name.length; i++) {
			Header hd = new Header();
			hd.setName(name[i]);
			hd.setDataIndex(DataIndex[i]);
			hd.setWidth(40);
			header.add(hd);
		}
		
		return header;
		
	}
	
	
	/**
	 * 按人员导出
	 */
	@RequestMapping(value = "/ryExportxls.htm", method = RequestMethod.GET)
	public void ryExportxls(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		String date = request.getParameter("dateString");
		String xfyh = request.getParameter("xfyh");
		String xcxz = request.getParameter("xcxz");
		String filename = "人员统计";	
		List<Header> header = this.getryExportxlsHeader();
		List end_result = new ArrayList<ArrayList>();
		end_result  = service.ryExportxls(date,xfyh,xcxz);
		try {
			ExcelUtil.toExcel(filename, header, end_result, false, response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 按人员查询的导出的获取头的方法
	 */
	public List<Header> getryExportxlsHeader()
			throws Exception {	
		List<Header> header = new ArrayList<Header>();
		
		String[] name = {"序号","消防用户","巡视人员","总单数","已执行","未执行"};
		String[] DataIndex = {"xh","xfyh","xsry","zds","yzx","wzx"};
		for (int i = 0; i < name.length; i++) {
			Header hd = new Header();
			hd.setName(name[i]);
			hd.setDataIndex(DataIndex[i]);
			hd.setWidth(80);
			header.add(hd);
		}
		return header;
		
	}
	
	/**
	 * 自己写的获取年的方法给前台按单位查用
	 */
	@ResponseBody
	@RequestMapping(value = "/getYear.htm", produces = "application/json; charset=utf-8")
	public void getYear(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		list = Test.getYear();
		System.out.println("对没错这就是我要的年" + list);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(list).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 按人员查询出的数据
	 */
	@ResponseBody
	@RequestMapping(value = "/getRYTJALL.htm", produces = "application/json; charset=utf-8")
	public void getRYTJALL(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			request.setCharacterEncoding("utf-8");
			String date =  request.getParameter("dateString");
			String xcxz =  request.getParameter("xcxz");
			String xfyhId =  request.getParameter("xfyh");
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			
			String[] ss =date.split(",");
			for(int i=0,len=ss.length;i<len;i++){
			    System.out.println(ss[i].toString());
			}
			String start =ss[0];
			String end = ss[1];
			list = service.getRYTJALL( start,end, xfyhId,xcxz );
			
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(list).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 *单位查询出的数据
	 */
	@ResponseBody
	@RequestMapping(value = "/getDWTJALL.htm", produces = "application/json; charset=utf-8")
	public void getDWTJALL(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			request.setCharacterEncoding("utf-8");
				
			
			String year =  request.getParameter("year");
			String xfyhId = request.getParameter("xfyh");
			String xcxz = request.getParameter("xcxz");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			
			list = service.getDWTJALL(year,xfyhId,xcxz);
			System.out.println(list);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(list).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}

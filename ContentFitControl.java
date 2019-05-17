package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.Header;
import cn.yunrui.intfirectrlsys.domain.ifcs_fp_content_fix1;
import cn.yunrui.intfirectrlsys.service.ContentFitService;
import cn.yunrui.intfirectrlsys.util.ExcelUtil;


/**
 * 消防数据维护
 * @author songcheng 2018-05-23
 *
 */
@Controller
@RequestMapping("/contentFit")
public class ContentFitControl {
	private static boolean iflocal = true;
	
	@Resource(name = "contentFitServiceImpl")
	private ContentFitService contentFitService;
	
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String init(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/tree";
	}
	
	//导出
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/export.htm", method = RequestMethod.GET)
	public void exportxls(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		  Authentication auth = RequestContextSecurity.getAuthentication();
		    
		String  cf_id = request.getParameter("cf_id");
		String  cf_level = request.getParameter("cf_level");
		System.out.println(cf_id);
		String filename = "巡查树维护";	
		List<Header> header = this.getryExportxlsHeader(cf_level);
		@SuppressWarnings({ "rawtypes", "unused" })
		List end_result = new ArrayList<ArrayList>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> level2 = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> level3 = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> level4 = new ArrayList<Map<String,Object>>();
		//查出所有的数据
		list = contentFitService.exportxls();
		//根据级别分组
		for (int i = 0; i < list.size(); i++) {
		
			if (list.get(i).get("cf_level").equals("2")) {
					level2.add(list.get(i));
			}else if (list.get(i).get("cf_level").equals("3")) {
					level3.add(list.get(i));
			}else if (list.get(i).get("cf_level").equals("4")) {
					level4.add(list.get(i));
			}
		}
		//如果是整个巡查树的导出走这个
		if(cf_id.equals("0-0")){
		//爸爸找爷爷
		List<Map<String, Object>> chubu  = new ArrayList<Map<String,Object>>();
			int o = -1;
			for (int J = 0; J <level2.size(); J++) {
				
				for (int Q = 0; Q < level3.size(); Q++) {
					if (level3.get(Q).get("cf_father_id").equals(level2.get(J).get("cf_id")) ) {
						Map<String,Object> map =  new HashMap<String,Object>();
						map.put("id",level3.get(Q).get("cf_id"));
						map.put("name",level3.get(Q).get("cf_name"));
						map.put("fuName",level2.get(J).get("cf_name"));
						o+=1;
						chubu.add(o,map);
					}
				}
			}
			//孙子找爸爸跟爷爷，爸爸跟爷爷已经在一起了
			List<Map<String, Object>> end  = new ArrayList<Map<String,Object>>();
				int m = -1;
		
				for (int K = 0; K < chubu.size(); K++) {
				for (int i = 0; i < level4.size(); i++) {
					
					if (chubu.get(K).get("id").equals(level4.get(i).get("cf_father_id"))) {
						Map<String,Object> map =  new HashMap<String,Object>();
						map.put("xcnrs","巡查内容树");
						map.put("fuName",chubu.get(K).get("fuName"));
						map.put("name",chubu.get(K).get("name"));
						map.put("ziName",level4.get(i).get("cf_name"));
						m+=1;
						end.add(m,map);
					}
				}
			}
			//巡查内容树去重	
				for (int i = end.size(); i > 0 ; i--) {
					if (i > 1 ) {
						if (end.get(i-1).get("xcnrs").equals(end.get(i-2).get("xcnrs"))) {
							end.get(i-1).put("xcnrs","  ");
							}
						System.out.println(end);
					}
				}
			//父类名称去重	
				for (int i = end.size(); i > 0 ; i--) {
					if (i > 1) {
						if (end.get(i-1).get("fuName").equals(end.get(i-2).get("fuName"))) {
						end.get(i-1).put("fuName","  ");
						}
					System.out.println(end);
					}
				}
			//子类名称去重
				for (int i = end.size(); i > 0 ; i--) {
					if (i > 1 ) {
						if (end.get(i-1).get("name").equals(end.get(i-2).get("name"))) {
							end.get(i-1).put("name","  ");
							}
						System.out.println(end);
					}
				}
				
				@SuppressWarnings("rawtypes")
				List zz = new ArrayList<ArrayList>();
				for (int i = 0; i < end.size(); i++) {
					//把每个map放到list里面要按顺序
					@SuppressWarnings("rawtypes")
					List ls = new ArrayList(); 
					ls.add(0,end.get(i).get("xcnrs"));
					ls.add(1,end.get(i).get("fuName"));
					ls.add(2,end.get(i).get("name"));
					ls.add(3,end.get(i).get("ziName"));
					zz.add(ls);
				}
				System.out.println(zz);
		try {
			ExcelUtil.toExcel(filename, header, zz, false, response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//巡查大类的去重
		}else if (cf_level.equals("3")) {
			//先通过id找到他的儿子和孙子分别放在不同的集合中去
			//爸爸找爷爷
			String name =  contentFitService.getName(cf_id);
			List<Map<String, Object>> chubu  = new ArrayList<Map<String,Object>>();
						int o = -1;
					for (int Q = 0; Q < level3.size(); Q++) {
						if (level3.get(Q).get("cf_father_id").equals(cf_id)) {
							Map<String,Object> map =  new HashMap<String,Object>();
						
							map.put("id",level3.get(Q).get("cf_id"));
							map.put("name",level3.get(Q).get("cf_name"));
							o+=1;
							chubu.add(o,map);
						}
					}
				
				//孙子找爸爸跟爷爷，爸爸跟爷爷已经在一起了
				List<Map<String, Object>> end  = new ArrayList<Map<String,Object>>();
					int m = -1;
			
					for (int K = 0; K < chubu.size(); K++) {
					for (int i = 0; i < level4.size(); i++) {
						
						if (chubu.get(K).get("id").equals(level4.get(i).get("cf_father_id"))) {
							Map<String,Object> map =  new HashMap<String,Object>();
							map.put("xcdl",name);
							map.put("fuName",chubu.get(K).get("name"));
							map.put("name",level4.get(i).get("cf_name"));
							m+=1;
							end.add(m,map);
						}
					}
				}
					
					//巡查大类去重	
					for (int i = end.size(); i > 0 ; i--) {
						if (i > 1) {
							if (end.get(i-1).get("xcdl").equals(end.get(i-2).get("xcdl"))) {
							end.get(i-1).put("xcdl","  ");
							}
						System.out.println(end);
						}
					}
					//父名称去重
					for (int i = end.size(); i > 0 ; i--) {
						if (i > 1 ) {
							if (end.get(i-1).get("fuName").equals(end.get(i-2).get("fuName"))) {
								end.get(i-1).put("fuName","  ");
								}
							System.out.println(end);
						}
					}
		
					@SuppressWarnings("rawtypes")
					List zz = new ArrayList<ArrayList>();
					for (int i = 0; i < end.size(); i++) {
						//把每个map放到list里面要按顺序
						@SuppressWarnings("rawtypes")
						List ls = new ArrayList(); 
						ls.add(0,end.get(i).get("xcdl"));
						ls.add(1,end.get(i).get("fuName"));
						ls.add(2,end.get(i).get("name"));
						zz.add(ls);
					}
					System.out.println(zz);
			try {
				ExcelUtil.toExcel(filename, header, zz, false, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//巡查项的导出
		}	else if (cf_level.equals("4")) {
			//先通过id找到他的儿子和孙子分别放在不同的集合中去
			//爸爸找爷爷
			String name =  contentFitService.getName(cf_id);
		
				//孙子找爸爸跟爷爷，爸爸跟爷爷已经在一起了
				List<Map<String, Object>> end  = new ArrayList<Map<String,Object>>();
					int m = -1;
			
					
					for (int i = 0; i < level4.size(); i++) {
						
						if (level4.get(i).get("cf_father_id").equals(cf_id)) {
							Map<String,Object> map =  new HashMap<String,Object>();
							map.put("xcx",name);
							map.put("name",level4.get(i).get("cf_name"));
							m+=1;
							end.add(m,map);
						}
					}
				
					//父名称去重
					for (int i = end.size(); i > 0 ; i--) {
						if (i > 1 ) {
							if (end.get(i-1).get("xcx").equals(end.get(i-2).get("xcx"))) {
								end.get(i-1).put("xcx","  ");
								}
							System.out.println(end);
						}
					}
		
					@SuppressWarnings("rawtypes")
					List zz = new ArrayList<ArrayList>();
					for (int i = 0; i < end.size(); i++) {
						//把每个map放到list里面要按顺序
						@SuppressWarnings("rawtypes")
						List ls = new ArrayList(); 
						ls.add(0,end.get(i).get("xcx"));
						ls.add(1,end.get(i).get("name"));
						zz.add(ls);
					}
					System.out.println(zz);
			try {
				ExcelUtil.toExcel(filename, header, zz, false, response);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	/**
	 * 按人员查询的导出的获取头的方法
	 */
	public List<Header> getryExportxlsHeader(String cf_level)
			throws Exception {	
		
		List<Header> header = new ArrayList<Header>();
				
				//整个树导出时候的表头
			if (cf_level.equals("2")) {
			
				String[] name = {"巡查内容树","巡查大类","巡查小类","巡查项"};
				String[] DataIndex  = {"xcnrs","xcdl","xcxl","xcx"};
				for (int i = 0; i < name.length; i++) {
					Header hd = new Header();
					hd.setName(name[i]);
					hd.setDataIndex(DataIndex[i]);
					hd.setWidth(80);
					header.add(hd);
			}
				//巡查大类导出时的表头
		} else if (cf_level.equals("3")) {
			String[] name = {"巡查大类","巡查小类","巡查项"};
			String[] DataIndex  = {"xcdl","xcxl","xcx"};
			for (int i = 0; i < name.length; i++) {
				Header hd = new Header();
				hd.setName(name[i]);
				hd.setDataIndex(DataIndex[i]);
				hd.setWidth(80);
				header.add(hd);
			}	
			//巡查项导出时的表头
		}else if (cf_level.equals("4")) {
			String[] name = {"巡查小类","巡查项"};
			String[] DataIndex  = {"xcxl","xcx"};
			for (int i = 0; i < name.length; i++) {
				Header hd = new Header();
				hd.setName(name[i]);
				hd.setDataIndex(DataIndex[i]);
				hd.setWidth(80);
				header.add(hd);
			}	
		}
			
			
		return header;
		
	}
	
	/**
	 * 查询维护数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/queryContentFit.htm")
	public void queryContentFit(HttpServletRequest request,
			HttpServletResponse response) {
		
		String cf_name = request.getParameter("cf_name");
		String cf_father_id = request.getParameter("cf_father_id");
		List list = contentFitService.queryContentFit(cf_name,cf_father_id);
		
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString()).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	//搜索树支持模糊查询
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/search.htm" , method = RequestMethod.POST)
	public void search(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
	
			request.setCharacterEncoding("utf-8");

		String initName = request.getParameter("search");
		String name  = "%"+initName+"%";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		 list = contentFitService.search(name);
	
		System.out.println(list);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString()).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteContentFit.htm")
	public void deleteContentFit(HttpServletRequest request,
			HttpServletResponse response) {
		
		String cf_id = request.getParameter("cf_id");
		
        boolean srt = contentFitService.deleteContentFit(cf_id);
		
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(srt).toString()).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 编辑数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/updataContentFit.htm")
	public void updataContentFit(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}// 必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。
		response.setContentType("text/html;charset=utf-8");// 设置传过去的页面显示的编码
		String cf_id = request.getParameter("cf_id");
		String cf_name = request.getParameter("cf_name");
		
		ifcs_fp_content_fix1 fix = new ifcs_fp_content_fix1();
		fix.setCf_id(cf_id);
		fix.setCf_name(cf_name);
		
        boolean srt =contentFitService.updataContentFit(fix);
		
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(srt).toString()).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增数据
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/addContentFit.htm",produces = "application/json; charset=utf-8")
	public void addContentFit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		request.setCharacterEncoding("utf-8");
	
		
		String id = UUID.randomUUID().toString();
		String cf_id = id.substring(0, 8)+id.substring(9, 13)+id.substring(14, 18)+id.substring(24);
		String cf_level = request.getParameter("cf_level");
		String cf_father_id = request.getParameter("cf_father_id");
		String cf_name = request.getParameter("cf_name");
		
		if (cf_level == null || "".equals(cf_level) || cf_father_id == null || "".equals(cf_father_id) || cf_name == null || "".equals(cf_name)) {
			return ;
		}
		
		ifcs_fp_content_fix1 fix = new ifcs_fp_content_fix1();
		fix.setCf_id(cf_id);
		fix.setCf_level(cf_level);
		fix.setCf_father_id(cf_father_id);
		fix.setCf_name(cf_name);

		
		
        boolean srt = contentFitService.addContentFit(fix);
		
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(srt).toString()).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}

package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import cn.yunrui.intfirectrlsys.service.paiBanService;
import cn.yunrui.intfirectrlsys.util.ExcelUtil;


@Controller
@RequestMapping("/paiban")
public class paiBanControl {
	

	private static boolean iflocal = false;

	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String init(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/pbgl";
	}

	@Resource(name = "paibanservice")
	private paiBanService service;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/exportPaiban.htm", method = RequestMethod.GET)
	public void exportPaiban(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// HashMap<String,String> param = Util.getrequestParams(request);
		String date = request.getParameter("dateString");
		String dateString = date + "-%";
		String roomId = request.getParameter("roomId");
		String companyId = request.getParameter("companyId");
		String HGdata = request.getParameter("HGdata");
		String filename = date + "月值班表";
		List<Header> header = this.getexportPaibanHeader(date, HGdata);
		List zb_result = this.service.exportPaiban(dateString, roomId,
				companyId, HGdata);

		List<Map<String, Object>> ry = new ArrayList<Map<String, Object>>();
		ry = service.queryRY(roomId);

		List<Map<String, Object>> bc = new ArrayList<Map<String, Object>>();
		bc = service.queryClassId(roomId);

		List end_result = new ArrayList<ArrayList>();
		
		if (HGdata == "1" || HGdata.equals("1")) {
			for (int i = 0; i < ry.size(); i++) {
				
				//用来记录上一个拼接的值班日期用来跟当前的值班日期作比较相同说明值的是一个班次
				String datebj = "";
				//同样是记录上一个拼接的值班日期不过是比较与当前值班日期相差的天数，相差几天放几个空值
				String datebegin = date + "-00";
				//用来找到具体的拼接位置
				int num = 0;
				//用来计算具体的num
				int u = 0;
				String user_id = (String) ry.get(i).get("id");
				String user_name = (String) ry.get(i).get("name");
				// 用来获取李斯特中具体的人ID去判断是否相等
				ArrayList inside = new ArrayList();
				// 将每次拼接结果放入一个list中
				ArrayList pinjie = new ArrayList();
				// 每次拼接前的头
				pinjie.add(user_name);

				for (int j = 0; j < zb_result.size(); j++) {

					// 获取具体的人员ID的属性
					inside = (ArrayList) zb_result.get(j);
					// 获取集合中people_id
					String zb_peopleid = (String) inside.get(2);

					// 获取集合中的值班日期
					String dateOnduty = (String) inside.get(3);
					// 计算出的相差天数
					// 比较数据中的ID与值班人员ID是否相等相等加数据
					num = j-u-1 ;
					if (user_id == zb_peopleid || user_id.equals(zb_peopleid)) {
						int e = Test.day_difference(datebegin, dateOnduty);
						if (e >= 2) {
							for (int k = 0; k < e - 1; k++) {
								pinjie.add("");
							}
							u+=e;
							datebegin = (String) inside.get(3);
							
						}
						if (num >= 0 ) {
							
							if (dateOnduty == datebj || dateOnduty.equals(datebj) ) {
								
								pinjie.set(num+1 ,inside.get(4)+"/"+((List<ArrayList>) zb_result.get(j-1)).get(4));
								u+=1;
								datebegin = (String) inside.get(3);
								datebj = (String) inside.get(3);
							}else{
								pinjie.add(inside.get(4));
								
								datebegin = (String) inside.get(3);
								datebj = (String) inside.get(3);
							}
						
						}else{
							pinjie.add(inside.get(4));
							datebegin = (String) inside.get(3);
							datebj = (String) inside.get(3);
						
						}
						
					}else{
						u+=1;
					}

				}
		
				end_result.add(pinjie);
			}
			try {
				ExcelUtil.toExcel(filename, header, end_result, false, response);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {

			for (int i = 0; i < bc.size(); i++) {
				//用来找到具体的拼接位置
				int num = 0;
				//用来计算具体的num
				int u = 0;
				//记录同样的班次下下一个值班日期与上一个值班日期相同的次数根据不同的次数做不同的拼接
				int o = 0;
				//用来记录上一个拼接的值班日期用来跟当前的值班日期作比较相同说明值的是一个班次
				String datebj="";
				//同样是记录上一个拼接的值班日期不过是比较与当前值班日期相差的天数，相差几天放几个空值
				String datebegin = date + "-00";
				//获取具体的班次id
				String class_id = (String) bc.get(i).get("id");
				//获取具体的班次名称
				String class_name = (String) bc.get(i).get("className");
				// 用来获取李斯特中具体的班次ID去判断是否相等
				ArrayList inside = new ArrayList();
				// 将每次拼接结果放入一个list中
				ArrayList pinjie = new ArrayList();

				
				// 每次拼接前的头
				pinjie.add(class_name);
				
				for (int j = 0; j < zb_result.size(); j++) {
					
					
					// 获取具体的班次ID的属性
					inside = (ArrayList) zb_result.get(j);
					// 放入一个String中后面去做比较
					String bc_classId = (String) inside.get(2);

					String dateOnduty = (String) inside.get(3);
					
					
					num = j-u-1 ;
					// 比较数据中的ID与值班人员ID是否相等相等加数据
					if (class_id == bc_classId || class_id.equals(bc_classId)) {
						
						int e = Test.day_difference(datebegin, dateOnduty);
						if (e >= 2) {
							for (int k = 0; k < e - 1; k++) {
								pinjie.add("");
							}
							u+=e;
							datebegin = (String) inside.get(3);
							System.out.println("加空后datebegin的值有没有变" + datebegin);
						}
						
						if (num >= 0 ) {
								
							if (dateOnduty == datebj || dateOnduty.equals(datebj) ) {
								o += 1;//出现一个值班日期相等的情况o加1然后根据次数做拼接
								if (o == 2 ) {
									pinjie.set(num+1 , inside.get(4)+"/"+((List<ArrayList>) zb_result.get(j-1)).get(4)+"/"+((List<ArrayList>) zb_result.get(j-2)).get(4));	
								}else if(o == 3){
									pinjie.set(num+1 , inside.get(4)+"/"+((List<ArrayList>) zb_result.get(j-1)).get(4)+"/"+((List<ArrayList>) zb_result.get(j-2)).get(4)+"/"+((List<ArrayList>) zb_result.get(j-3)).get(4));
								}else{
									pinjie.set(num+1 , inside.get(4)+"/"+((List<ArrayList>) zb_result.get(j-1)).get(4));
								}
								
								u+=1;
								datebegin = (String) inside.get(3);
								datebj = (String) inside.get(3);
							
							}else{
								o = 0;//当没有连续的值班日期相等的情况时让o归零。
								pinjie.add(inside.get(4));
								
								datebegin = (String) inside.get(3);
								datebj = (String) inside.get(3);
							}
						}else {
							pinjie.add(inside.get(4));
							datebegin = (String) inside.get(3);
							datebj = (String) inside.get(3);
								
						}

					}else{
						u+=1;
					}

				}
				end_result.add(pinjie);
			}

			try {
				ExcelUtil
						.toExcel(filename, header, end_result, false, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 获取导出文件头
	public List<Header> getexportPaibanHeader(String date, String HGdata)
			throws Exception {

		if (HGdata == "1" || HGdata.equals("1")) {
			List<Header> header = new ArrayList<Header>();
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = Test.getZb_Column(date);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
			Date riqi = formatter.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(riqi);
			int d = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			int e = d + 1;

			String[] names = new String[e];
			names[0] = "值班人员";
			String[] index = new String[e];
			index[0] = "people";

			for (int i = 0; i < d; i++) {
				String K1 = list.get(i).get("day").toString();
				String K2 = list.get(i).get("week").toString();
				names[i + 1] = K1 + "号/" + K2;
				index[i + 1] += "zb_" + i;
			}

			for (int i = 0; i < names.length; i++) {
				Header hd = new Header();
				hd.setName(names[i]);
				hd.setDataIndex(index[i]);
				hd.setWidth(80);
				header.add(hd);
			}

			return header;

		} else {
			List<Header> header = new ArrayList<Header>();
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = Test.getZb_Column(date);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
			Date riqi = formatter.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(riqi);
			int d = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			int e = d + 1;

			String[] names = new String[e];
			names[0] = "班次类型";
			String[] index = new String[e];
			index[0] = "className";
			for (int i = 0; i < d; i++) {
				String K1 = list.get(i).get("day").toString();
				String K2 = list.get(i).get("week").toString();
				names[i + 1] = K1 + "号/" + K2;
				index[i + 1] += "zb_" + i;
			}

			for (int i = 0; i < names.length; i++) {
				Header hd = new Header();
				hd.setName(names[i]);
				hd.setDataIndex(index[i]);
				hd.setWidth(80);
				header.add(hd);
			}
			return header;
		}

	}

	// 插入班次
	@ResponseBody
	@RequestMapping(value = "/insertbc.htm", produces = "application/json; charset=utf-8")
	public void insertbc(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		String selectedRowKeys = request.getParameter("selectedRowKeys");// 这个是获取的当前班次类型的ID
		String xksID = request.getParameter("xksID");
		String xfyhID = request.getParameter("xfyhID");
		String classNumber = request.getParameter("classNumber");
		String className = request.getParameter("className");
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		String remark = request.getParameter("remark");

		int i = service.insertbc(selectedRowKeys, xksID, xfyhID, classNumber,
				className, start, end, remark);

		response.setContentType("text/html;charset=utf-8");

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(i).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/deleteClassType.htm")
	// 删除选中的班次类型
	public void deleteClassType(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String key = request.getParameter("key");
		String roomId = request.getParameter("roomId");
		String companyId = request.getParameter("companyId");
		int i = service.deleteClassType(key, roomId, companyId);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(i).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/deleteBC.htm")
	// 删除选中的班次
	public void deleteBC(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String key = request.getParameter("key");

		int i = service.deleteBC(key);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(i).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 通过 消防用户获取到该用户下的消防室信息
	 */
	@RequestMapping(value = "/getControlRoomAtion.htm")
	public void getControlRoomAtion(HttpServletRequest request,
			HttpServletResponse response) {

		String id = request.getParameter("id");
		@SuppressWarnings("unchecked")
		ArrayList<Map> xfyhlist = (ArrayList<Map>) service
				.getControlRoomCommon(Integer.valueOf(id));

		String rst = JSONArray.fromObject(xfyhlist).toString();
		System.out.println("传说中的消控室" + rst);
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

	// 查询所有的消防用户
	@RequestMapping(value = "/getXfyhListAtion.htm")
	public void getXfyhListAtion(HttpServletRequest request,
			HttpServletResponse response) {

		Authentication auth = RequestContextSecurity.getAuthentication();
		@SuppressWarnings("unchecked")
		ArrayList<CodeEntity> xfyhlist = (ArrayList<CodeEntity>) service
				.getXfyhListCommon(auth.getBureauNo(),auth.getOrgNo());
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

	// 查询出所属消控室的班次类型
	@ResponseBody
	@RequestMapping(value = "/queryBCLX.htm")
	public void queryBCLX(HttpServletRequest request,
			HttpServletResponse response) {
		String roomId = request.getParameter("roomId");
		String companyId = request.getParameter("companyId");

		List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
		hs = service.queryBCLX(roomId, companyId);
		System.out.println("前台打印" + hs);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(hs).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询出所属消控室的班次
	@ResponseBody
	@RequestMapping(value = "/queryBC.htm")
	public void queryBC(HttpServletRequest request, HttpServletResponse response) {
		String roomId = request.getParameter("roomId");
		String companyId = request.getParameter("companyId");
		String classTypeId = request.getParameter("selectedRowKeys");// 这个是获取的当前班次类型的ID

		List<Map<String, Object>> bc = new ArrayList<Map<String, Object>>();
		bc = service.queryBC(roomId, companyId, classTypeId);
		System.out.println("前台打印查询出的班次" + bc);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(bc).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 新增班次类型
	@ResponseBody
	@RequestMapping(value = "/insert.htm", produces = "application/json; charset=utf-8")
	public void insert(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setCharacterEncoding("utf-8");
		String classType = request.getParameter("classType");
		String roomId = request.getParameter("roomId");
		String companyId = request.getParameter("companyId");
		int i = service.insert(classType, roomId, companyId);

		response.setContentType("text/html;charset=utf-8");

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(i).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 调用后台方法拼接出按人员显示的日期表
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/getZb_column.htm", produces = "application/json; charset=utf-8")
	public void getZb_column(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ym = request.getParameter("dateString");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		list = Test.getZb_Column(ym);
		System.out.println("整合时看值班表的列数据" + list);
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

	// 查询按人员排班表
	@ResponseBody
	@RequestMapping(value = "/queryRYZB.htm", produces = "application/json; charset=utf-8")
	public void queryRYZB(HttpServletRequest request,
			HttpServletResponse response) throws ParseException {
		String roomId = request.getParameter("roomId");
		String companyId = request.getParameter("companyId");
		String riqi = request.getParameter("date");
		System.out.println(riqi);
		String date = riqi + "-%";
		System.out.println(date);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ry = new ArrayList<Map<String, Object>>();
		list = service.queryRYZB(roomId, companyId, date);
		ry = service.queryRY(roomId);

		// String aaa=
		// "[{\"people_id\":\"1\",\"people\":\"zhangsan\",\"zb_1\":\"bai\"}]";
		String jsona = "[";
		for (int i = 0; i < ry.size(); i++) {
			String user_id = (String) ry.get(i).get("id");
			String user_name = (String) ry.get(i).get("name");
			StringBuffer buf = new StringBuffer();

			jsona += ",{\"people_id\":\"" + user_id + "\",\"people\":\""
					+ user_name + "\"";

			for (int j = 0; j < list.size(); j++) {
				String zb_peopleid = (String) list.get(j).get("peopleid");

				if (user_id == zb_peopleid || user_id.equals(zb_peopleid)) {

					String key = "zb_"
							+ Test.getDate((String) list.get(j).get(
									"dataOnDuty"));
					String value = (String) list.get(j).get("className");
					jsona += ",\"" + key + "\":\"" + value+"   \\r\\n" + "\"";
				}
			}
			jsona += "}";
		}

		jsona += "]";
		System.out.println("按人员查询的值班表66" + jsona.replaceFirst(",", ""));
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			
			response.getWriter().write(
					JSONArray.fromObject(jsona.replaceFirst(",", ""))
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 查询按班次排班表
	@ResponseBody
	@RequestMapping(value = "/queryBCZB.htm", produces = "application/json; charset=utf-8")
	public void queryBCZB(HttpServletRequest request,
			HttpServletResponse response) throws ParseException {
		String roomId = request.getParameter("roomId");
		String companyId = request.getParameter("companyId");
		String riqi = request.getParameter("date");
		System.out.println(riqi);
		String date = riqi + "-%";
		System.out.println(date);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bc = new ArrayList<Map<String, Object>>();
		bc = service.queryClassId(roomId);
		list = service.queryBCZB(roomId, companyId, date);

		// String aaa=
		// "[{\"people_id\":\"1\",\"people\":\"zhangsan\",\"zb_1\":\"bai\"}]";
		String jsona = "[";
		for (int i = 0; i < bc.size(); i++) {
			String class_id = (String) bc.get(i).get("id");
			String class_name = (String) bc.get(i).get("className");
			StringBuffer buf = new StringBuffer();

			jsona += ",{\"class_id\":\"" + class_id + "\",\"className\":\""
					+ class_name + "\"";

			for (int j = 0; j < list.size(); j++) {
				String bc_classId = (String) list.get(j).get("classId");

				if (class_id == bc_classId || class_id.equals(bc_classId)) {

					String key = "zb_"
							+ Test.getDate((String) list.get(j).get(
									"dataOnDuty"));
					String value = (String) list.get(j).get("name");
					jsona += ",\"" + key + "\":\"" + value+"   \\r\\n" + "\"";
				}
			}
			jsona += "}";
		}

		jsona += "]";
		System.out.println("整合时看按班次查询的值班表666" + jsona.replaceFirst(",", ""));
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			// response.getWriter().write(JSONArray.fromObject(list).toString());
			// response.getWriter().write(JSONArray.fromObject(ry).toString());
			response.getWriter().write(
					JSONArray.fromObject(jsona.replaceFirst(",", ""))
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 查出所属消控室下的班次类型
	@ResponseBody
	@RequestMapping(value = "/query_class.htm")
	public void query_class(HttpServletRequest request,
			HttpServletResponse response) {
		String roomId = request.getParameter("roomId");

		List<Map<String, Object>> ry = new ArrayList<Map<String, Object>>();
		ry = service.queryClassId(roomId);
		System.out.println("前台打印查询出的班次" + ry);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(ry).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查出所属消控室下的所有值班人员
	@ResponseBody
	@RequestMapping(value = "/query_people.htm")
	public void query_people(HttpServletRequest request,
			HttpServletResponse response) {
		String roomId = request.getParameter("roomId");

		List<Map<String, Object>> ry = new ArrayList<Map<String, Object>>();
		ry = service.queryRY(roomId);
		System.out.println("看看这些人都长什么操行" + ry);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(ry).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	// 新增调换记录表并修改值班表
	@ResponseBody
	@RequestMapping(value = "/changeTheRecord.htm", produces = "application/json; charset=utf-8")
	public void changeTheRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		String roomId = request.getParameter("roomId");
		String switchedPeoples_id = request.getParameter("switchedPeoples_id");
		String companyId = request.getParameter("companyId");
		String changeTheDate = request.getParameter("changeTheDate");
		String class_id = request.getParameter("class_id");
		String changeThePeoples_name = request
				.getParameter("changeThePeoples_name");
		String switchedPeoples_name = request
				.getParameter("switchedPeoples_name");
		String inputTime = request.getParameter("inputTime");
		String Remark = request.getParameter("Remark");
		String classs_name = request.getParameter("class_name");
		String Reason = request.getParameter("Reason");

		int i = service.changeTheRecord(companyId, roomId, switchedPeoples_id,
				changeTheDate, class_id, changeThePeoples_name,
				switchedPeoples_name, inputTime, Remark, classs_name, Reason);

		response.setContentType("text/html;charset=utf-8");

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(i).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询出调换记录
	@ResponseBody
	@RequestMapping(value = "/querychangeTheRecord.htm")
	public void querychangeTheRecord(HttpServletRequest request,
			HttpServletResponse response) {

		String roomId = request.getParameter("roomId");
		
		String companyId = request.getParameter("companyId");
		
		String date = request.getParameter("date");
		
		String nianyue = date + "-%";
		List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
		hs = service.querychangeTheRecord(roomId, companyId, nianyue);
		System.out.println("前台打印" + hs);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(hs).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 查询所有的消防用户
		@RequestMapping(value = "/queryHasBC.htm", method = RequestMethod.POST)
		public void queryHasBC(HttpServletRequest request,
				HttpServletResponse response) {

			String key = request.getParameter("key");
			
		    String hs = service.queryHasBC(key);
			List<Map<String, Object>> hts = new ArrayList<Map<String, Object>>();
			Map<String, Object> a  = new HashMap<String, Object>();
			a.put("key",key);
			a.put("result",hs);
			hts.add(a);
			try {
				response.setContentType("application/json; charset=utf-8");

				if (iflocal) {
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}

				response.getWriter().write(JSONArray.fromObject(hts).toString());
				response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	// 插入按班次排列的值班表 插入的是人员
	@ResponseBody
	@RequestMapping(value = "/insert_BC_ZB.htm")
	public void mm(HttpServletRequest request,HttpServletResponse response){
		String xksID = request.getParameter("roomId");
		String class_id = request.getParameter("class_id");
		String xfyhID = request.getParameter("companyId");
		String str = request.getParameter("people_id");
		String dataOnDuty = request.getParameter("dataOnDuty");
		
		String[] people_id =str.split(",");
		
		for(int i=0,len=people_id.length;i<len;i++){
		    System.out.println(people_id[i].toString());
		}
		System.out.println("老衲倒要看看你到底是多长"+people_id.length);
		int o = service.insert_BC_ZB(xfyhID, xksID, people_id, dataOnDuty, class_id,str);
		System.out.println(o);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(o).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 插入按人员排列的值班表 插入的是班次
	@ResponseBody
	@RequestMapping(value = "/insert_RY_ZB.htm")
	public void insert_RY_ZB(HttpServletRequest request,HttpServletResponse response){
		String xksID = request.getParameter("roomId");
		String people_id = request.getParameter("people_id");
		String xfyhID = request.getParameter("companyId");
		String str = request.getParameter("class_id");
		String dataOnDuty = request.getParameter("dataOnDuty");
		
		String[] class_id =str.split(",");
		for(int i=0,len=class_id.length;i<len;i++){
		    System.out.println(class_id[i].toString());
		}
		System.out.println("老衲倒要看看你到底是多长"+class_id.length);
		int o = service.insert_RY_ZB(xfyhID, xksID, people_id, dataOnDuty, class_id,str);
		System.out.println(o);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(o).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

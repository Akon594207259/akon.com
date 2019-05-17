package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.service.ZongpintaiService;
import cn.yunrui.intfirectrlsys.util.Util;

import com.i380v.openservices.event.IAlarmDataService;
import com.i380v.openservices.measure.model.EventDataInfo;

@Controller
@RequestMapping("/zongpintai")
public class ZongpintaiControl {
	//总平台业务包路径     /zongpintai/init.htm
	////当月数据统计 当月已处理真实火警数量、误报火警数量、未处理真实火警数量、故障信息数量、其他信息数量
	private static boolean iflocal = false;
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String wg(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/ZPT";
	}
	@Resource(name="zongpintaiservice")
	private ZongpintaiService zongpintaiService;
	
	//////发生产库时解开
	@Resource(name = "intfirectrlsys_alarm.ds")
	private IAlarmDataService service4; 
///////////////////////////////////////////////


	@Resource(name="ifcs_jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	//当月已处理真实火警数量
	@RequestMapping(value = "/queryYCLHJSL.htm" ,method = RequestMethod.POST)
	public void getgetYCLHJSL(HttpServletRequest request,HttpServletResponse response) {
		//System.out.println("==============当月已处理真实火警数量");
		List rMessage = this.zongpintaiService.getYCLHJSL();
	        String rst = JSONArray.fromObject(rMessage).toString();  
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	//当月未处理真实火警数量
	@RequestMapping(value = "/queryWCLHJSL.htm")
	public void getgetWCLHJSL(HttpServletRequest request,HttpServletResponse response) {
		//System.out.println("==============当月未处理真实火警数量");
		List rMessage = this.zongpintaiService.getWCLHJSL();
	        String rst = JSONArray.fromObject(rMessage).toString();  
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	
	//当天火警数量
	@RequestMapping(value = "/queryDTHJSL.htm")
	public void getDTHJSL(HttpServletRequest request,HttpServletResponse response) {
		//System.out.println("==============当天火警数量");
		List rMessage = this.zongpintaiService.getDTHJSL();
	        String rst = JSONArray.fromObject(rMessage).toString();  
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	//当月火警数量
	@RequestMapping(value = "/queryDYHJSL.htm")
	public void getDYHJSL(HttpServletRequest request,HttpServletResponse response) {
		//System.out.println("==============当月火警数量");
		List rMessage = this.zongpintaiService.getDYHJSL();
	        String rst = JSONArray.fromObject(rMessage).toString();  
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	//当年火警数量
	@RequestMapping(value = "/queryDNHJSL.htm")
	public void getDNHJSL(HttpServletRequest request,HttpServletResponse response) {
		//System.out.println("==============当年火警数量");
		List rMessage = this.zongpintaiService.getDNHJSL();
	        String rst = JSONArray.fromObject(rMessage).toString();  
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	
	///发生产库时用到/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	 
	
	
	//故障信息数量  这个调的是接口 this.service4.getAlarmDataByTime(用户编号,Tue Mar 12 00:00:00 CST 2019,Tue Mar 12 00:00:00 CST 2019)
	
	@RequestMapping(value = "/queryGZXXSL.htm" ,method = RequestMethod.POST)
	public void getGZXXSL(HttpServletRequest request,HttpServletResponse response) throws ParseException {
		//System.out.println("==============故障信息数量");
		
		Authentication auth = RequestContextSecurity.getAuthentication();
	    String buro = auth.getBureauNo();
	    String org = auth.getOrgNo();
	    //System.err.println("看传参 buro:" + buro);

	    //System.err.println("看传参 org:" + org);
		String YHburo = request.getParameter("buro");
		//System.err.println("YHburo:" + YHburo);
		Date date = new Date(); 
		String a=Util.getcurYear()+"-"+Util.getcurMonth()+"-"+"01"+" 00:00:00";
		//System.out.println("当月第一天00000000000000000000000000000==== "+a);
		//System.out.println("当月第一天0000000000000000000000000==== "+Util.getcurYear());
		//System.out.println("当月第一天00000000000000000000000000==== "+Util.getcurMonth());
		//System.out.println("当月第一天00000000000000000000000000==== "+Util.getcurDay());
		SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date  date0 = formatter.parse(a);
		//System.out.println("当queryGZXXSL第2天 date0==== "+date0);
		//System.out.println("当queryGZXXSL第2天 date==== "+date);
		List rMessage = this.service4.getAlarmDataByTime(YHburo, date0, date);
	        String rst = JSONArray.fromObject(rMessage).toString();  
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	} 
	
	
	@RequestMapping(value = "/queryGZXXSL1.htm" ,method = RequestMethod.POST)
	public void getGZXXSL1(HttpServletRequest request,HttpServletResponse response) throws ParseException {
		//System.out.println("==============故障信息数量");
		
		Authentication auth = RequestContextSecurity.getAuthentication();
	    String buro = auth.getBureauNo();
	    String org = auth.getOrgNo();
	    //System.err.println("看传参 buro:" + buro);

	   // System.err.println("看传参 org:" + org);
		String YHburo = request.getParameter("buro");
		//System.err.println("YHburo:" + YHburo);
		Date date = new Date(); 
		String a=Util.getcurYear()+"-"+Util.getcurMonth()+"-"+"01"+" 00:00:00";
		//System.out.println("当月第一天1111111111111111111111111111111111==== "+a);
		//System.out.println("当月第一天111111111111111111111111111111111==== "+Util.getcurYear());
		//System.out.println("当月第一天111111111111111111111111111111111==== "+Util.getcurMonth());
		//System.out.println("当月第一天111111111111111111111111111111111==== "+Util.getcurDay());
		SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date  date0 = formatter.parse(a);
		//System.out.println("当queryGZXXSL第2天 date0==== "+date0);
		//System.out.println("当queryGZXXSL第2天 date==== "+date);
		
		List rMessage = this.service4.getAlarmDataByTime(buro, date0, date);
	        String rst = JSONArray.fromObject(rMessage).toString();  
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	


	//告警数据 调接口=+=====+++++++++++++++++++++++++++++++++++++++
//	@RequestMapping({"/getGJSJ.htm"})
//	  public void getGJSJ(HttpServletRequest request, HttpServletResponse response)
//	    throws UnsupportedEncodingException, ParseException
//	  {
//	    request.setCharacterEncoding("utf-8");
//	   
//
//	    Authentication auth = RequestContextSecurity.getAuthentication();
//	    String buro = auth.getBureauNo();
//	    String org = auth.getOrgNo();
//	   // List<String> subburos = new ArrayList();
//
//	    List<String> subburos =getSubburo("88000704");
//	   
//	    if (buro.equals(org))
//	      subburos = getSubburo(org);
//	    else {
//	      subburos.add(buro);
//	    }
//
//	    List list = new ArrayList();
//	    int hj = 0;
//	    int gz = 0;
//	    int fk = 0;
//	    int jg = 0;
//	    int pb = 0;
//	    int qt = 0;
//	   
//	    for (String str : subburos) {
//	    	String a=Util.getcurYear()+"-"+Util.getcurMonth()+"-"+"01"+" 00:00:00";
//	    	SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date  date0 = formatter.parse(a);
//			Date  date=new Date();
//	    	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期格式
//			 Date startTime=sdf2.parse(Util.getDate()+" "+"00:00:00");
//			
//			 Date endTime=sdf2.parse(Util.getDate()+" "+"23:59:59");
//			
//	      List list1 = this.service4.getAlarmDataByTime(str, startTime, endTime);
//	      
//	      if ((list1 != null) && (list1.size() >= 1)) {
//	        for (int i = 0; i < list1.size(); i++) {
//	          if ("17".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
//	            hj++;
//	          else if ("18".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
//	            gz++;
//	          else if ("19".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
//	            fk++;
//	          else if ("20".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
//	            jg++;
//	          else if ("21".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
//	            pb++;
//	          else {
//	            qt++;
//	          }
//	        }
//	      }
//	    }
//	    Map map = new HashMap();
//	    map.put("hj", Integer.valueOf(hj));
//	    map.put("gz", Integer.valueOf(gz));
//	    map.put("fk", Integer.valueOf(fk));
//	    map.put("jg", Integer.valueOf(jg));
//	    map.put("pb", Integer.valueOf(pb));
//	    map.put("qt", Integer.valueOf(qt));
//	    list.add(map);
//	    try {
//	      response.setContentType("application/json; charset=utf-8");
//	      if (iflocal) {
//	        response.setHeader("Access-Control-Allow-Origin", 
//	          "http://localhost:3000");
//	        response.setHeader("Access-Control-Allow-Credentials", "true");
//	      }
//	      response.getWriter().write(
//	        JSONArray.fromObject(JSONArray.fromObject(list).toString())
//	        .toString());
//	      response.getWriter().flush();
//	    } catch (IOException e) {
//	      e.printStackTrace();
//	    }
//	  } 
	
	  private List<String> getSubburo(String buro) {
		    String sql = "select DISTINCT subburo from cisp_dev.dev_powersystemresource where  subburo is not null and buro=?";
		    return this.jdbcTemplate.query(sql, new Object[] { buro }, 
		      new RowMapper()
		    {
		      public String mapRow(ResultSet rs, int i) throws SQLException {
		        return rs.getString(1);
		      }
		    });
		  } 
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  //测试 当月第一天到当前时间
	@RequestMapping(value = "/aaa.htm" ,method = RequestMethod.POST)
	public void getaaa(HttpServletRequest request,HttpServletResponse response) throws ParseException {
		
		
		String a=Util.getcurYear()+"-"+Util.getcurMonth()+"-"+"01"+" 00:00:00";
//		System.out.println("当月第一天==== "+a);
//		System.out.println("当月第一天==== "+Util.getcurYear());
//		System.out.println("当月第一天==== "+Util.getcurMonth());
//		System.out.println("当月第一天==== "+Util.getcurDay());
		SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date  date0 = formatter.parse(a);
		
	        
//	    System.out.println("当月 rst==== "+date0);
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	//// 查询代理商列表 SELECT *,a.org_no,b.jingdu,b.weidu from cisp_sys.bp_org a  ,zndw.tf_yxgl_register b where a.org_no = b.orgno and (b.orgno=88000704 or b.orgno=88001488) and (a.org_no = 88000704 or a.p_org_no =88000704 and a.org_type ='30')
	
	//  
	/*   select  a.ORG_NAME ,a.dwdz ,a.lxry,a.lxrsj ,ifnull(case when a.org_no = '88000704' then b.count1 else c.count1 end,0) as count1 from 
	(SELECT * from cisp_sys.bp_org a  ,zndw.tf_yxgl_register b 
			where a.org_no = b.orgno and
			(b.orgno=88000704 or b.orgno=88001488) and (a.org_no = 88000704 or a.p_org_no =88000704 and a.org_type ='30')) a 
			left join (	select COUNT(*) as count1,a.P_ORG_NO from cisp_sys.bp_org a,cisp_dev.dev_powersystemresource b ,cisp_dev.dev_FireProtectionUser  c where a.P_ORG_NO ='88000704' and a.ORG_TYPE='04'
			and a.org_no =b.SUBBURO and b.`STATUS`='20'  and b.CLASSNAME ='FireProtectionUser' and b.id =c.id group by a.P_ORG_NO) b on a.org_no = b.P_ORG_NO
			left join (select COUNT(*) as count1,a.P_ORG_NO from cisp_sys.bp_org a,cisp_dev.dev_powersystemresource b ,cisp_dev.dev_FireProtectionUser  c where a.P_ORG_NO ='88001488' and a.ORG_TYPE='04'
			and a.org_no =b.SUBBURO and b.`STATUS`='20'  and b.CLASSNAME ='FireProtectionUser' and b.id =c.id group by a.P_ORG_NO) c on a.org_no = c.P_ORG_NO  */
	
	@RequestMapping(value = "/dailishang.htm" ,method = RequestMethod.POST)
	public void getDLS(HttpServletRequest request,HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
		String name = request.getParameter("name");
		System.out.println("==============当name "+name);
		hs = zongpintaiService.mapList(name);
		String rst = JSONArray.fromObject(hs).toString();
	        
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	
	//代理商数量
		@RequestMapping(value = "/queryDLSSL.htm")
		public void getDLSSL(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getDLSSL();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
	
	//全部用户数量
		@RequestMapping(value = "/queryQBYHSL.htm")
		public void getQBYHSL(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getQBYHSL();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		//一般用户数量
		@RequestMapping(value = "/queryYBYHSL.htm")
		public void getYBYHSL(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getYBYHSL();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		
		//重点用户数量
		@RequestMapping(value = "/queryZDYHSL.htm")
		public void getZDYHSL(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getZDYHSL();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		
		//高危用户数量 
		@RequestMapping(value = "/queryGWYHSL.htm")
		public void getGWYHSL(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getGWYHSL();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		
		
		//当月火警处理数量排名 data: [5055,0]  倒序
		@RequestMapping(value = "/queryHJCLSLPM.htm")
		public void getHJCLSLPM(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getHJSLPM();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		//当月火警处理数量排名 data: [0,5055]  正序
		@RequestMapping(value = "/queryHJCLSLPM1.htm")
		public void getHJCLSLPM1(HttpServletRequest request,HttpServletResponse response) {
			List rMessage = this.zongpintaiService.getHJSLPM1();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		
		
		// 代理商名称  正序  SELECT a.org_name from cisp_sys.bp_org a  ,zndw.tf_yxgl_register b  where a.org_no = b.orgno and (b.orgno=88000704 or b.orgno=88001488) and (a.org_no = 88000704 or a.p_org_no =88000704 and a.org_type ='30')
		@RequestMapping(value = "/queryDLSMC.htm")
		public void getDLSMC(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getDLSMC();
			System.out.println("======rMessage======="+rMessage);
		        String rst = JSONArray.fromObject(rMessage).toString();  
		        System.out.println("======rst11======="+rst);
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		
	//[{"org_name":"诸暨恒源消防安全职业技能培训学校"},{"org_name":"方元安消防"}]
		// 代理商名称  倒序  SELECT a.org_name from cisp_sys.bp_org a  ,zndw.tf_yxgl_register b  where a.org_no = b.orgno and (b.orgno=88000704 or b.orgno=88001488) and (a.org_no = 88000704 or a.p_org_no =88000704 and a.org_type ='30')
		@RequestMapping(value = "/queryDLSMC1.htm")
		public void getDLSMC1(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getDLSMC1();
			System.out.println("======rMessage23======="+rMessage);
		        String rst = JSONArray.fromObject(rMessage).toString();  
		        System.out.println("======rst11222======="+rst);
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		
		//火警排名前五个
		
		@RequestMapping(value = "/getFireRanking.htm")
		public void getFireRanking(HttpServletRequest request,HttpServletResponse response) {
			 List rMessage = this.zongpintaiService.getFireRanking();
		        String rst = JSONArray.fromObject(rMessage).toString(); 
		        System.out.println("======rst  2======="+rst);
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		//告警中数量
		@RequestMapping(value = "/queryGJZSL.htm")
		public void getGJZSL(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getGJZSL();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		//处理中数量
		@RequestMapping(value = "/queryCLZSL.htm")
		public void getCLZSL(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getCLZSL();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		//已归档数量
		@RequestMapping(value = "/queryYGDSL.htm")
		public void getYGDSL(HttpServletRequest request,HttpServletResponse response) {
			//System.out.println("==============当年火警数量");
			List rMessage = this.zongpintaiService.getYGDSL();
		        String rst = JSONArray.fromObject(rMessage).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		
		//三级平台用户信息
		@RequestMapping(value = "/queryYHXX.htm")
		public void getYHXX(HttpServletRequest request,HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
			request.setCharacterEncoding("utf-8");
			List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
			String buro = request.getParameter("buro");
			String org = request.getParameter("org");
			System.out.println("==============当当当当当当当当当当buro "+buro);
			System.out.println("==============当当当当当当当当当当org "+org);
			hs = zongpintaiService.getYHXX(buro, org);
			String rst = JSONArray.fromObject(hs).toString();
		        
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
}

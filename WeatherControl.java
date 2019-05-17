package cn.yunrui.intfirectrlsys.action;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import cn.yunrui.intfirectrlsys.util.PinYinUtil;
//import cn.yunrui.intfirectrlsys.util.Util;



/**
 * 首页调接口 查天气信息  现改为直接前端调插件显示天气  心知天气网
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/WeatherControl")
public class WeatherControl {
	private static boolean iflocal = true;

//	private String url = "http://www.frigate-iot.com/data/login_chk.php";
//	private String charset = "utf-8";
//	private HttpClientUtil httpClientUtil = null;
//	public TestMain(){
//		httpClientUtil = new HttpClientUtil();
//	}
//	public static void main(String[] args){
//		TestMain main = new TestMain();
//		main.test();
//	}
	
//	@RequestMapping(value = "/test",method = { RequestMethod.POST, RequestMethod.GET })
//	@ResponseBody
//	public void test(HttpServletRequest req,HttpServletResponse res) throws IOException, ServletException{
//		//ModelAndView mav = new ModelAndView();
//		String httpOrgCreateTest = url;
//		String charset = "utf-8";
//		Map<String,String> createMap = new HashMap<String,String>();
//		createMap.put("name","lbxza");
//		createMap.put("pwd",Util.MD5("xza2018"));
//		String httpOrgCreateTestRtn = httpClientUtil.postHttpResponse(httpOrgCreateTest,createMap);
//		System.out.println("result:"+httpOrgCreateTestRtn);		
//		String cookies = HttpClientUtils.doPost(createMap, charset);
//		System.out.println(cookies);
//		//返回-1 是登陆成功！ 返回1 是错误。呃呃呃呃
//		if("1".equalsIgnoreCase(httpOrgCreateTestRtn)){
//			System.out.println("密码错误");
//		}else{
//			System.out.println("账号密码正确 ，登陆成功");
//			//判断是否登陆了   只有正确才进入   （可拓展性）
////			Map<String,Object> resultMap = new HashMap<String,Object>();
////			resultMap.put("name","lbxza");
////			resultMap.put("pwd",Util.MD5("xza2018"));
//			//Integer saveTime = 30*24*60*60;//设置有效时间为30天
//			//cookie.setMaxAge(saveTime);
//			//Cookie cookie = new Cookie("autoLogin","name+==lbxza");
//			Cookie cookie = new Cookie("PHPSESSID",cookies);
////			cookie.setMaxAge(0);
////			cookie.setPath("/");
//			res.addCookie(cookie);
////			 model.addFlashAttribute("name","lbxza");  
////			 model.addFlashAttribute("pwd",Util.MD5("xza2018"));  
////			 model.addFlashAttribute("PHPSESSID",cookies); 
//
//		}
//		//res.getDispatcherHeader("B.jsp").forward(req,res);
//		//http转发 带参 但会加前后缀 
//		req.getRequestDispatcher("http://www.frigate-iot.com/login/XWS/Index.php").forward(req,res);
//	
//		//转发  不带参数  可跳转
//		//res.sendRedirect("http://www.frigate-iot.com/login/XWS/Index.php");
//		//会自动带上前缀 和 后缀  
//		//ModelAndView mav = new ModelAndView("http://www.frigate-iot.com/login/XWS/Index.php");
//		//可以跳转  但是不带参数
//	    //	return  new ModelAndView(new RedirectView("http://www.frigate-iot.com/login/XWS/Index.php"));
//       
//	}
	

////	@Resource(name = "XyaLoginService")
////	private XyaLoginService XyaLoginService;
//	
//	
//	@RequestMapping(value = "/login")
//	@ResponseBody
//	public ModelAndView findtrain(HttpServletRequest request, HttpServletResponse response) throws IOException {

//		// PHPSESSID	941rgvnheg7fu6oe99fkau4p57
		 
//		
//		//PHPSESSID	941rgvnheg7fu6oe99fkau4p57
//		HttpSession session = request.getSession();
//		session.setAttribute("-1", -1);
//		
//		String name ="lbxza";
//		String pwd =Util.MD5("xza2018");
//	
//		mav.addObject(name,name);
//		mav.addObject(pwd,pwd);
//		
//		
//		
//		mav=new ModelAndView("http://www.frigate-iot.com/data/login_chk.php");
//		return mav;
//	}
////解析MD5密码的  
////		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
////		oldPassword = md5.encodePassword(oldPassword, userCode);


	@RequestMapping(value = "/test.htm",method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public void test(HttpServletRequest req,HttpServletResponse res) throws IOException, ServletException, ParserConfigurationException, SAXException{
		//http://pv.sohu.com/cityjson?ie=utf-8 （不稳定）
		//请求地址  可以根据用户的请求   查询用户的 地址 ip 等信息    http://ip.ws.126.net/ipquery 返回
		//var lo="浙江省", lc="杭州市"; var localAddress={city:"杭州市", province:"浙江省"}
		String apiUrl = String.format("http://ip.ws.126.net/ipquery");
		//开始请求
		URL url= new URL(apiUrl);
		URLConnection open = url.openConnection();
		InputStream input = open.getInputStream();
		//这里转换为String，带上包名，
		String result = org.apache.commons.io.IOUtils.toString(input,"GBK");
		System.out.println(result);
		String param1=result.substring(result.indexOf("localAddress")+13, result.length());
		
	    JSONObject datastream = JSONObject.fromObject(param1);
	    String hdAddress1 = datastream.getString("city");
	    System.out.println("截取后的字符串   "+hdAddress1);	
	    String straddress= hdAddress1.replace("市", "");
		System.out.println("获取到的城市是"+straddress); 
        String address= PinYinUtil.getPingYin(straddress);
		String addressUr3 = String.format("https://api.seniverse.com/v3/weather/now.json?key=fezovabxdjj60mgk&location="+address+"&language=zh-Hans&unit=c");
		URL url3= new URL(addressUr3);
		URLConnection open3 = url3.openConnection();
		InputStream input3 = open3.getInputStream();
		//这里转换为String，带上包名，
		String weather1 = org.apache.commons.io.IOUtils.toString(input3,"utf-8");
        JSONObject weather3 = JSONObject.fromObject(weather1);
        String results = weather3.getString("results");
        String resultsreplace = results.replace("[", "").replace("]", "");
        JSONObject results1 = JSONObject.fromObject(resultsreplace);
        String now = results1.getString("now");  
        JSONObject now1 = JSONObject.fromObject(now);
        String WE = now1.getString("text");  //天气
        String temp = now1.getString("temperature"); //温度
        //把风向 风速 和天气  包装成json 返回页面
		Map<String, Object> map=new HashMap<String, Object>();
		//map.put("WD", WD);
		//map.put("WS", WS);
		map.put("adress", straddress);
		map.put("WE", WE);
		map.put("temp", temp);
		System.out.println(map);
		/*res.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
		res.setContentType("application/x-download;charset=UTF-8");
		res.getWriter().write(JSONArray.fromObject(map).toString());
		res.getWriter().flush();*/
		String rst = JSONArray.fromObject(map).toString(); 
		try {
			res.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  res.setHeader("Access-Control-Allow-Credentials", "true");
			}
			res.getWriter().write(rst);
			res.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}



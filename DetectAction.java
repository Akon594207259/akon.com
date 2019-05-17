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
import org.springframework.web.bind.annotation.ResponseBody;
import cn.yunrui.intfirectrlsys.common.CommonMethods;
import cn.yunrui.intfirectrlsys.common.CommonValues;


import cn.yunrui.intfirectrlsys.domain.AutoOutFireInfo;
import cn.yunrui.intfirectrlsys.domain.DryFireFightingInfo;
import cn.yunrui.intfirectrlsys.domain.ELAEISInfo;
import cn.yunrui.intfirectrlsys.domain.FireDetectInfo;
import cn.yunrui.intfirectrlsys.domain.FireProofDoorInfo;
import cn.yunrui.intfirectrlsys.domain.FireProtectInfo;
import cn.yunrui.intfirectrlsys.domain.FireSystemInfo;
import cn.yunrui.intfirectrlsys.domain.FoamFireFightingInfo;
import cn.yunrui.intfirectrlsys.domain.GasFireExtinguishInfo;
import cn.yunrui.intfirectrlsys.domain.ResultMessage;
import cn.yunrui.intfirectrlsys.domain.SmokeControlInfo;
import cn.yunrui.intfirectrlsys.domain.TreeNode;
import cn.yunrui.intfirectrlsys.domain.WaterMistInfo;
import cn.yunrui.intfirectrlsys.domain.WaterSprayInfo;
import cn.yunrui.intfirectrlsys.service.DetectService;


@Controller
@RequestMapping("/detectfiree")
public class DetectAction {
private static boolean iflocal = false;
	
	@Resource(name="Detectservice") 
	private  DetectService detectService;
	
	
	@RequestMapping(value = "/init.htm",method = RequestMethod.GET)
	public String init(Model model){	
	    return "/ifcs/xxwh";
	}
	
	
	/**
	 * 获取树的根节点
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/treeheader.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
	@ResponseBody
	public void treeheader(HttpServletRequest request, HttpServletResponse response){	
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
	    TreeNode header = new TreeNode();
	    header = detectService.getTreeheader();
        ArrayList<TreeNode> hl =new ArrayList<TreeNode>();
        hl.add(header);
        String rst = JSONArray.fromObject(hl).toString();
        System.out.println("------------"+rst);		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 获取树的子节点
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/treecontent.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
	@ResponseBody
	public void treecontent(HttpServletRequest request, HttpServletResponse response){	
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		    String pid = request.getParameter("pid");
		    System.out.println(pid);
            List<TreeNode> treeList =new ArrayList<TreeNode>();
            treeList = detectService.getTreeChild(pid);
  	        String rst = JSONArray.fromObject(treeList).toString(); 
  	        System.out.print("树的信息"+rst);
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){				  
					  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
					  response.setHeader("Access-Control-Allow-Credentials", "true");
					}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	}
	
	/**
	 * 保存火灾探测报警系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addFireDetect.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addFireDetectInfo(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.fireDetectType+"3"+id;//3是树的深度
		String sysName = request.getParameter("sysName");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		String sysForm = request.getParameter("sysForm");
		String setArea = request.getParameter("setArea");
		//封装对象
		FireDetectInfo fdi = new FireDetectInfo();
		fdi.setId(id);
		fdi.setSysName(sysName);
		fdi.setSysFactory(sysFactory);
		fdi.setSysCompany(sysCompany);
		fdi.setSysDate(sysDate);
		fdi.setSysForm(sysForm);
		fdi.setSetArea(setArea);
		
		ResultMessage<String> rm = new ResultMessage<String>();
		rm = detectService.addFireDetect(fdi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存消防给水系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addFireProtect.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addFireProtect(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//用来封装对象
		FireProtectInfo fpi = new FireProtectInfo();
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.fireProtectType+"3"+id;
		String sysName = request.getParameter("sysName");//系统名称
		String type = request.getParameter("type");//系统类型
		String netForm = "";//管网形式
		
		fpi.setSysName(sysName);
		fpi.setType(type);
		
		if("00".equals(type)){
			//消防给水系统形式
			id += "1";
			String waterForm = request.getParameter("waterForm");//消防给水系统形式
			String sysRange = request.getParameter("sysRange");//范围
			String sysUsage = request.getParameter("sysUsage");//用途
			netForm = request.getParameter("netForm"); 
		    
		    fpi.setWaterForm(waterForm);
			fpi.setSysRange(sysRange);
			fpi.setSysUsage(sysUsage);
			fpi.setNetForm(netForm);
		}else if ("01".equals(type)) {
			//消防水源
			id += "2";
			String pipeRadius = request.getParameter("pipeRadius");//市政给水管管径
			String pipeCount = request.getParameter("pipeCount");//进水管数量
		    netForm = request.getParameter("netForm");
		    
		    fpi.setPipeRadius(Double.valueOf(pipeRadius));
			fpi.setPipeCount(Integer.valueOf(pipeCount));
			fpi.setNetForm(netForm);
		}else if("02".equals(type)){
			//消火栓系统
			id += "3";
			String outNetForm = request.getParameter("outNetForm");//室外消火栓管网形式
			String outHydrantRadius = request.getParameter("outHydrantRadius");//室外消火栓管径
			String outHydrantPressure = request.getParameter("outHydrantPressure");//室外消火栓压力
			String outHydrantCount = request.getParameter("outHydrantCount");//室外消火栓数量
			String outHydrantSetForm = request.getParameter("outHydrantSetForm");//室外消火栓设置形式
			String outHydrantFactory = request.getParameter("outHydrantFactory");//室外消火栓生产厂家
			String inHydrantNetForm = request.getParameter("inHydrantNetForm");//室内消火栓管网形式
			String inHydrantCount = request.getParameter("inHydrantCount");//室内消火栓数量
			String inHydrantFactory = request.getParameter("inHydrantFactory");//室内消火栓生产厂家
			
			fpi.setOutNetForm(outNetForm);
			fpi.setOutHydrantRadius(Double.valueOf(outHydrantRadius));
			fpi.setOutHydrantPressure(outHydrantPressure);
			fpi.setOutHydrantCount(Integer.valueOf(outHydrantCount));
			fpi.setOutHydrantSetForm(outHydrantSetForm);
			fpi.setOutHydrantFactory(outHydrantFactory);
			fpi.setInHydrantNetForm(inHydrantNetForm);
			fpi.setInHydrantCount(Integer.valueOf(inHydrantCount));
			fpi.setInHydrantFactory(inHydrantFactory);
		}
		fpi.setId(id);
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addFireProtect(fpi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存自动喷水灭火系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addAutoOutFire.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addAutoOutFire(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.autoOutFireType+"3" + id;
		String sysName = request.getParameter("sysName");
		String type = request.getParameter("type");
		String alarmCount = request.getParameter("alarmCount");
		String alarmProArea = request.getParameter("alarmProArea");
		if("01".equals(type)){
			id += "2";
		}else if ("02".equals(type)) {
			id += "3";
		}else if ("03".equals(type)) {
			id += "4";
		}else {
			id += "1";
		}
		//封装对象
		AutoOutFireInfo aofi = new AutoOutFireInfo(id,sysName,type,Integer.valueOf(alarmCount),alarmProArea);
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addAutoOutFire(aofi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存细水雾系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addWaterMist.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addWaterMist(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.waterMistType+"3" + id;
		String sysName = request.getParameter("sysName");
		String sysType = request.getParameter("sysType");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		//封装对象
		WaterMistInfo wmi = new WaterMistInfo(id,sysName,sysType,sysFactory,sysCompany,sysDate);
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addWaterMist(wmi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存水喷雾系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addWaterSpray.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addWaterSpray(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.waterSprayType +"3"+ id;
		String sysName = request.getParameter("sysName");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		//封装对象
		WaterSprayInfo wsi = new WaterSprayInfo(id,sysName,sysFactory,sysCompany,sysDate);
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addWaterSpray(wsi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存气体灭火系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addGasFireExtinguish.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addGasFireExtinguish(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.gasFireExtinguishType+"3" + id;
		String sysName = request.getParameter("sysName");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		String annihilator = request.getParameter("annihilator");
		String protectForm = request.getParameter("protectForm");
		String netForm = request.getParameter("netForm");
		String areaCount = request.getParameter("areaCount");
		String setArea = request.getParameter("setArea");
		//封装对象
		GasFireExtinguishInfo gfei = new GasFireExtinguishInfo(id,sysName,sysFactory,sysCompany,sysDate,annihilator,protectForm,netForm,Integer.valueOf(areaCount),setArea);
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addGasFireExtinguish(gfei);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存泡沫灭火系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addFoamFireFighting.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addFoamFireFighting(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.foamFireFightingType+"3" + id;
		String sysName = request.getParameter("sysName");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		String lMultiple = request.getParameter("lMultiple");
		String mMultiple = request.getParameter("mMultiple");
		String hMultiple = request.getParameter("hMultiple");
		String setForm = request.getParameter("setForm");
		String foamType = request.getParameter("foamType");
		String foamStorageCount = request.getParameter("foamStorageCount");
		String protectArea = request.getParameter("protectArea");
		String foamPumpCount = request.getParameter("foamPumpCount");
		//封装对象
		FoamFireFightingInfo fffi = new FoamFireFightingInfo(id,sysName,sysFactory,sysCompany,sysDate,lMultiple,mMultiple,hMultiple,
				setForm,foamType,Integer.valueOf(foamStorageCount),protectArea,Integer.valueOf(foamPumpCount));
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addFoamFireFighting(fffi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存干粉灭火系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addDryFireFighting.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addDryFireFighting(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.dryFireFightingType +"3"+id;
		String sysName = request.getParameter("sysName");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		String annihilator = request.getParameter("annihilatorType");
		String outfireForm = request.getParameter("outfireForm");
		String sysProtect = request.getParameter("sysProtect");
		String gasStorage = request.getParameter("gasStorage");
		//封装对象
		DryFireFightingInfo dffi = new DryFireFightingInfo(id,sysName,sysFactory,sysCompany,sysDate, 
				annihilator,outfireForm,sysProtect,gasStorage);
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addDryFireFighting(dffi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存防排烟系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addSmokeControl.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addSmokeControl(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.smokeControlType+"3" + id ;
		String sysName = request.getParameter("sysName");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		String protectArea = request.getParameter("protectArea");
		//封装对象
		SmokeControlInfo sci = new SmokeControlInfo(id,sysName,sysFactory,sysCompany,sysDate,protectArea);
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addSmokeControl(sci);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存应急照明及疏散指示系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addELAEIS.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addELAEIS(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.ELAEISType +"3"+ id;
		String sysName = request.getParameter("sysName");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		String controlForm = request.getParameter("controlForm");
		//封装对象
		ELAEISInfo elaeisi = new ELAEISInfo(id,sysName,sysFactory,sysCompany,sysDate,controlForm);
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addELAEIS(elaeisi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 保存防火门系统信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/addFireProofDoor.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void addFireProofDoor(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = CommonMethods.getUUID();
		id = CommonValues.fireProofDoorType +"3"+id;
		String sysName = request.getParameter("sysName");
		String sysFactory = request.getParameter("sysFactory");
		String sysCompany = request.getParameter("sysCompany");
		String sysDate = request.getParameter("sysDate");
		String openDoorCount = request.getParameter("openDoorCount");
		String closeDoorCount = request.getParameter("closeDoorCount");
		//封装对象
		FireProofDoorInfo fpdi = new FireProofDoorInfo(id,sysName,sysFactory,sysCompany,
				sysDate,Integer.valueOf(openDoorCount),Integer.valueOf(closeDoorCount));
		
		ResultMessage<?> rm = new ResultMessage<Object>();
		rm = detectService.addFireProofDoor(fpdi);
		System.out.println("------------"+rm.toJson());		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 查询消防系统信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/queryFireDetectInfo.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
	@ResponseBody
	public void queryFireSysInfo(HttpServletRequest request, HttpServletResponse response){	
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
        String id = request.getParameter("id");
        FireSystemInfo fs = new FireSystemInfo();
        fs = detectService.queryFireSysInfo(id);
	    System.out.println("------------"+fs.toJson());
	    try {
  			response.setContentType("application/json; charset=utf-8");
  			if(iflocal){				  
  				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
  				  response.setHeader("Access-Control-Allow-Credentials", "true");
  				}
  			response.getWriter().write(fs.toJson());
  			response.getWriter().flush();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}	
	}
	/**
	
	/**
	 * 修改消防系统信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/editFireSysInfo.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
	@ResponseBody
	public void editFireSys(HttpServletRequest request, HttpServletResponse response){	
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		ResultMessage<String> rMessage = new ResultMessage<String>();//返回信息
        String id = request.getParameter("id");
        String sysName = request.getParameter("sysName");
        String type = id.substring(0, 2);
        if (CommonValues.fireProtectType.equals(type)) {
        	//用来封装对象
    		FireProtectInfo fpi = new FireProtectInfo();
    		String fireProtectType = request.getParameter("type");//系统类型
    		fpi.setSysName(sysName);
    		fpi.setType(fireProtectType);
    		
    		if("00".equals(fireProtectType)){
    			//消防给水系统形式
    		    String waterForm = request.getParameter("waterForm");
    		    String sysRange = request.getParameter("sysRange");
    		    String sysUsage = request.getParameter("sysUsage");
    		    String netForm = request.getParameter("netForm"); 
    		    
    		    fpi.setWaterForm(waterForm);
    			fpi.setSysRange(sysRange);
    			fpi.setSysUsage(sysUsage);
    			fpi.setNetForm(netForm);
    		}else if ("01".equals(fireProtectType)) {
    			//消防水源
    			String pipeRadius = request.getParameter("pipeRadius");
    			String pipeCount = request.getParameter("pipeCount");
    			String netForm = request.getParameter("netForm");
    		    
    		    fpi.setPipeRadius(Double.valueOf(pipeRadius));
    			fpi.setPipeCount(Integer.valueOf(pipeCount));
    			fpi.setNetForm(netForm);
    		}else if("02".equals(fireProtectType)){
    			//消火栓系统
    			String outNetForm = request.getParameter("outNetForm");
    			String outHydrantRadius = request.getParameter("outHydrantRadius");
    			String outHydrantPressure = request.getParameter("outHydrantPressure");
    			String outHydrantCount = request.getParameter("outHydrantCount");
    			String outHydrantSetForm = request.getParameter("outHydrantSetForm");
    			String outHydrantFactory = request.getParameter("outHydrantFactory");
    			String inHydrantNetForm = request.getParameter("inHydrantNetForm");
    			String inHydrantCount = request.getParameter("inHydrantCount");
    			String inHydrantFactory = request.getParameter("inHydrantFactory");
    			
    			fpi.setOutNetForm(outNetForm);
    			fpi.setOutHydrantRadius(Double.valueOf(outHydrantRadius));
    			fpi.setOutHydrantPressure(outHydrantPressure);
    			fpi.setOutHydrantCount(Integer.valueOf(outHydrantCount));
    			fpi.setOutHydrantSetForm(outHydrantSetForm);
    			fpi.setOutHydrantFactory(outHydrantFactory);
    			fpi.setInHydrantNetForm(inHydrantNetForm);
    			fpi.setInHydrantCount(Integer.valueOf(inHydrantCount));
    			fpi.setInHydrantFactory(inHydrantFactory);
    		}
			rMessage = detectService.editFireProtect(id,fpi);
		}else if (CommonValues.autoOutFireType.equals(type)) {
			String autoType = request.getParameter("type");
			String alarmCount = request.getParameter("alarmCount");
			String alarmProArea = request.getParameter("alarmProArea");
			//封装对象
			AutoOutFireInfo aofi = new AutoOutFireInfo(id,sysName,autoType,Integer.valueOf(alarmCount),alarmProArea);
			rMessage = detectService.editAutoOutFire(id,aofi);
		}else {
			String sysFactory = request.getParameter("sysFactory");
    		String sysCompany = request.getParameter("sysCompany");
    		String sysDate = request.getParameter("sysDate");
    		if(CommonValues.fireDetectType.equals(type)){
    			String sysForm = request.getParameter("sysForm");
    			String setArea = request.getParameter("setArea");
    			//封装对象
    			FireDetectInfo fdi = new FireDetectInfo();
    			fdi.setSysName(sysName);
    			fdi.setSysFactory(sysFactory);
    			fdi.setSysCompany(sysCompany);
    			fdi.setSysDate(sysDate);
    			fdi.setSysForm(sysForm);
    			fdi.setSetArea(setArea);
            	rMessage = detectService.editFireDetect(id,fdi);
    		}else if (CommonValues.waterMistType.equals(type)) {
    			String sysType = request.getParameter("sysType");
    			//封装对象
    			WaterMistInfo wmi = new WaterMistInfo(id,sysName,sysType,sysFactory,sysCompany,sysDate);
    			rMessage =detectService.editWaterMist(id,wmi);
    		}else if (CommonValues.waterSprayType.equals(type)) {
    			//封装对象
    			WaterSprayInfo wsi = new WaterSprayInfo(id,sysName,sysFactory,sysCompany,sysDate);
    			rMessage = detectService.editWaterSpray(id,wsi);
    		}else if (CommonValues.gasFireExtinguishType.equals(type)) {
    			String annihilator = request.getParameter("annihilator");
    			String protectForm = request.getParameter("protectForm");
    			String netForm = request.getParameter("netForm");
    			String areaCount = request.getParameter("areaCount");
    			String setArea = request.getParameter("setArea");
    			//封装对象
    			GasFireExtinguishInfo gfei = new GasFireExtinguishInfo(id,sysName,sysFactory,sysCompany,sysDate,annihilator,protectForm,netForm,Integer.valueOf(areaCount),setArea);
    			rMessage =detectService.editGasFireExtinguish(id, gfei);
    		}else if (CommonValues.foamFireFightingType.equals(type)) {
    			String lMultiple = request.getParameter("lMultiple");
    			String mMultiple = request.getParameter("mMultiple");
    			String hMultiple = request.getParameter("hMultiple");
    			String setForm = request.getParameter("setForm");
    			String foamType = request.getParameter("foamType");
    			String foamStorageCount = request.getParameter("foamStorageCount");
    			String protectArea = request.getParameter("protectArea");
    			String foamPumpCount = request.getParameter("foamPumpCount");
    			//封装对象
    			FoamFireFightingInfo fffi = new FoamFireFightingInfo(id,sysName,sysFactory,sysCompany,sysDate,lMultiple,mMultiple,hMultiple,
    					setForm,foamType,Integer.valueOf(foamStorageCount),protectArea,Integer.valueOf(foamPumpCount));
    			rMessage = detectService.editFoamFireFighting(id,fffi);
    		}else if (CommonValues.dryFireFightingType.equals(type)) {
    			String annihilator = request.getParameter("annihilatorType");
    			String outfireForm = request.getParameter("outfireForm");
    			String sysProtect = request.getParameter("sysProtect");
    			String gasStorage = request.getParameter("gasStorage");
    			//封装对象
    			DryFireFightingInfo dffi = new DryFireFightingInfo(id,sysName,sysFactory,sysCompany,sysDate, 
    					annihilator,outfireForm,sysProtect,gasStorage);
    			rMessage = detectService.editDryFireFighting(id,dffi);
    		}else if (CommonValues.smokeControlType.equals(type)) {
    			
    			String protectArea = request.getParameter("protectArea");
    			//封装对象
    			SmokeControlInfo sci = new SmokeControlInfo(id,sysName,sysFactory,sysCompany,sysDate,protectArea);
    			rMessage =detectService.editSmokeControl(id,sci);
    		}else if (CommonValues.ELAEISType.equals(type)) {
    			
    			String controlForm = request.getParameter("controlForm");
    			//封装对象
    			ELAEISInfo elaeisi = new ELAEISInfo(id,sysName,sysFactory,sysCompany,sysDate,controlForm);
    			rMessage = detectService.editELAEIS(id,elaeisi);
    		}else if (CommonValues.fireProofDoorType.equals(type)) {
    			String openDoorCount = request.getParameter("openDoorCount");
    			String closeDoorCount = request.getParameter("closeDoorCount");
    			//封装对象
    			FireProofDoorInfo fpdi = new FireProofDoorInfo(id,sysName,sysFactory,sysCompany,
    					sysDate,Integer.valueOf(openDoorCount),Integer.valueOf(closeDoorCount));
    			rMessage = detectService.editFireProofDoor(id,fpdi);
    		}
		}
	    System.out.println("------------"+rMessage.toJson());
	    try {
  			response.setContentType("application/json; charset=utf-8");
  			if(iflocal){				  
  				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
  				  response.setHeader("Access-Control-Allow-Credentials", "true");
  				}
  			response.getWriter().write(rMessage.toJson());
  			response.getWriter().flush();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}	
	}
	
	/**
	 * 删除消防系统信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deleteFireSysInfo.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
	@ResponseBody
	public void deleteFireSys(HttpServletRequest request, HttpServletResponse response){	
		ResultMessage<String> rMessage = new ResultMessage<String>();//返回信息
        String id = request.getParameter("id");
        rMessage = detectService.deleteFireSys(id);
	    System.out.println("------------"+rMessage.toJson());
	    try {
  			response.setContentType("application/json; charset=utf-8");
  			if(iflocal){				  
  				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
  				  response.setHeader("Access-Control-Allow-Credentials", "true");
  				}
  			response.getWriter().write(rMessage.toJson());
  			response.getWriter().flush();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}	
	}
	

}

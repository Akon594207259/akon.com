/**
 * @author 杨德志
 * @date 2018-11-3  
 */
package cn.yunrui.intfirectrlsys.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sun.misc.BASE64Encoder;

import cn.yunrui.intfirectrlsys.domain.Attributes;
import cn.yunrui.intfirectrlsys.domain.Controller1;
import cn.yunrui.intfirectrlsys.domain.DeviceModel;
import cn.yunrui.intfirectrlsys.domain.FireAlarm;
import cn.yunrui.intfirectrlsys.domain.PowerData;
import cn.yunrui.intfirectrlsys.domain.Singledevice;
import cn.yunrui.intfirectrlsys.domain.ifcs_fp_content_fix1;
import cn.yunrui.intfirectrlsys.service.DutyRoomService;
import cn.yunrui.intfirectrlsys.service.EquipmentManagementService;
import cn.yunrui.intfirectrlsys.util.HttpClientUtil;
import cn.yunrui.intfirectrlsys.util.Util;

/**
 * @author yangdezhi
 * 
 */
@Controller
@RequestMapping("/EquipmentControl")
public class EquipmentManagement {

	private HttpClientUtil httpClientUtil = null;
	@Resource(name = "equipmentManagementService")
	private EquipmentManagementService service;

	public void findtrain() throws Exception {
		
		findtrain1();
		singleEquipment();
	}
	
	/**
	 * 设备列表
	 * 
	 */
	@RequestMapping(value = "/Equipment.htm")
	public void findtrain1() throws Exception {

		// 获取token
		String Acctoken = gettoken();
		// 获取签名getAuthorization(String Acctoken)
		String Authorization = getAuthorization(Acctoken);
		String httpDevicesUrl = "http://ex-api.jalasmart.com/api/v2/devices/5bdbc2fefdd73f197c373bad";
		String httpDevices = sendGet(httpDevicesUrl, Authorization);
		System.err.println("ddddddd个数据有什么问题:"+httpDevices);
		// Java集合
	 
		JSONObject datastream = JSONObject.fromObject(httpDevices);
	System.err.println("设个数据有什么问题:"+datastream);
		
		JSONArray JSONData = datastream.getJSONArray("Data");

		List list = new ArrayList();
		for (int i = 0; i < JSONData.size(); i++) {
			DeviceModel DeviceModel = new DeviceModel();
			DeviceModel.setDeviceID(JSONData.getJSONObject(i).getString(
					"DeviceID"));
			DeviceModel.setControllerID(JSONData.getJSONObject(i).getString(
					"ControllerID"));
			DeviceModel.setName(JSONData.getJSONObject(i).getString("Name"));
			DeviceModel.setCategoryID(JSONData.getJSONObject(i).getString(
					"CategoryID"));
			DeviceModel.setIcon(JSONData.getJSONObject(i).getString("Icon"));
			DeviceModel.setLan(JSONData.getJSONObject(i).getString("Lan"));
			DeviceModel.setConnect(JSONData.getJSONObject(i).getString(
					"Connect"));
			list.add(DeviceModel);

		}
		service.findtrain(list);
		singleEquipment();
	}

	/**
	 * 单个设备
	 * 
	 */
	public void singleEquipment() {

		String Acctoken = gettoken();
		String Authorization = getAuthorization1(Acctoken);

		String EquipmentNumber = "J2512113071192";
		String httpDevicesUrl = "http://ex-api.jalasmart.com/api/v2/devices/J2512113071192";
		String httpDevices = sendGet1(httpDevicesUrl, Authorization);
		JSONObject datastream = JSONObject.fromObject(httpDevices);
		JSONObject JSONData = datastream.getJSONObject("Data");

		Singledevice singledevice = new Singledevice();
		singledevice.setDeviceID(JSONData.getString("DeviceID"));
		singledevice.setCategoryID(JSONData.getString("CategoryID"));
		singledevice.setName(JSONData.getString("Name"));
		singledevice.setConnect(JSONData.getString("Connect"));
		singledevice.setControllerID(JSONData.getString("ControllerID"));
		singledevice.setLan(JSONData.getString("DeviceID"));
		
		
		JSONArray JSONList = JSONData.getJSONArray("Lines");
		List list = new ArrayList();
		for (int i = 0; i < JSONList.size(); i++) {
			Attributes attributes = new Attributes();
			attributes.setCurrent(JSONList.getJSONObject(i)
					.getString("Current"));
			attributes.setLeakValue(JSONList.getJSONObject(i).getString(
					"LeakValue"));
			attributes.setLineID(JSONList.getJSONObject(i).getString("LineID"));
			attributes.setLineNo(JSONList.getJSONObject(i).getString("LineNo"));
			attributes.setMax(JSONList.getJSONObject(i).getString("Max"));
			attributes.setModel(JSONList.getJSONObject(i).getString("Model"));
			attributes.setName(JSONList.getJSONObject(i).getString("Name"));
			attributes.setOver(JSONList.getJSONObject(i).getString("Over"));
			attributes.setPower(JSONList.getJSONObject(i).getString("Power"));
			attributes.setStatus(JSONList.getJSONObject(i).getString("Status"));
			attributes.setTemp(JSONList.getJSONObject(i).getString("Temp"));
			attributes.setUnder(JSONList.getJSONObject(i).getString("Under"));
			attributes.setVoltage(JSONList.getJSONObject(i)
					.getString("Voltage"));
			list.add(attributes);

		}
		service.singleEquipment(singledevice, list);
		kaiguan(JSONData.getString("ControllerID"));
		gaoJing();
		electricalData();
	}

	/**
	 * 
	 * 线路开关
	 */
	public void kaiguan(String ID) {
		// 鑾峰彇token
		String Acctoken = gettoken2();
		// 鑾峰彇绛惧悕getAuthorization(String Acctoken)
		// String Authorization = getAuthorization1(Acctoken);
		// 3.7 绾胯矾寮�叧// api/v2/status/{ControllerID } PUT
		// ControllerID 鏄牴鎹� 涓婁竴涓�鎺ュ彛 鏌ヨ 鍗曚釜璁惧淇℃伅 鐨勬帴鍙ｈ幏寰楃殑
		String ControllerID = ID; // 鐜板湪鍐欐鐨� 鍚庨潰鍐嶆敼
		String httpDevicesUrl = "http://ex-api.jalasmart.com/api/v2/status/5ac194d5a3ab15f438be83f0";
		// 绛惧悕
		String Nonce = createRandomString1(6);
		String TimeStamp = timeStamp1();
		String SignatureASCll = "ControllerID=5ac194d5a3ab15f438be83f0&Lines=[{\"LineNo\":1,\"Status\":0}]&Nonce="
				+ Nonce
				+ "&"
				+ "TimeStamp="
				+ TimeStamp
				+ "&UserID=5bdbc2fefdd73f197c373bad&Token=" + Acctoken + "";
		String Signature = MD5(SignatureASCll);
		String BASE64String = "{\"TimeStamp\":\"" + TimeStamp
				+ "\",\"UserID\":\"5bdbc2fefdd73f197c373bad\",\"Nonce\":\""
				+ Nonce + "\",\"Signature\":\"" + Signature + "\"}";
		String Authorization = getBase64(BASE64String);

		String jsonObj = "{\"ControllerID\":\"5ac194d5a3ab15f438be83f0\",\"Lines\":[{\"LineNo\": 1,\"Status\": 0}] }";
		// {"ControllerID":"5ac194d5a3ab15f438be83f0","Lines":[{"LineNo":
		// 1,"Status": 1}] }
		String httpDecices = doPutAuthorization(httpDevicesUrl, jsonObj,
				Authorization);

		System.err.println("开关线路：" + httpDecices);
	}
	
	
	/**
	 * 告警信息
	 */
	public void gaoJing(){
				
			String Acctoken = gettoken4();
			String Nonce =createRandomString(6);
		    String TimeStamp = timeStamp();
		    String  SignatureASCll ="ControllerID=5ac194d5a3ab15f438be83f0&Nonce="+Nonce+"&Page=50&" +
					"TimeStamp="+TimeStamp+"&UserID=5bdbc2fefdd73f197c373bad&Token="+Acctoken+"";  
		    String Signature = MD5(SignatureASCll);
		    String BASE64String = "{\"TimeStamp\":\""+TimeStamp+"\",\"UserID\":\"5bdbc2fefdd73f197c373bad\",\"Nonce\":\""+Nonce+"\",\"Signature\":\""+Signature+"\"}";
		    String  Authorization  = getBase64(BASE64String);
			String httpDevicesUrl = "http://ex-api.jalasmart.com/api/v2/messages/5ac194d5a3ab15f438be83f0/50";
			String httpDevices =sendGet4(httpDevicesUrl,Authorization);
			System.err.println("线路：" + httpDevices);
			
			// Java集合
			JSONObject datastream = JSONObject.fromObject(httpDevices);
			JSONArray JSONData = datastream.getJSONArray("Data");

			List list = new ArrayList();
			
            if(JSONData.size() != 0){
        		for (int i = 0; i < JSONData.size(); i++) {
        			FireAlarm fireAlarm = new FireAlarm();
    				 fireAlarm.setAddTime(JSONData.getJSONObject(i).getString("DeviceID"));
    				 fireAlarm.setAlarmSeverity(JSONData.getJSONObject(i).getString("AlarmSeverity"));
    				 fireAlarm.setCode(JSONData.getJSONObject(i).getString("Code"));
                     fireAlarm.setContent(JSONData.getJSONObject(i).getString("Content"));
                     fireAlarm.setControllerID(JSONData.getJSONObject(i).getString("ControllerID"));
                     fireAlarm.setData(JSONData.getJSONObject(i).getString("Data"));
                     fireAlarm.setLineNo(JSONData.getJSONObject(i).getString("LineNo"));
                     fireAlarm.setMessageID(JSONData.getJSONObject(i).getString("MessageID"));
    				list.add(fireAlarm);
    			}
            }
         
            service.gaoJing(list);
	}
	
	

    /**
     * 电能数据
     */
	
	 public void electricalData()
	  {
	    String DeviceID = "5b99b84a4c1d6e0b80b22534";
	    String Date = "2018-10-30";
	    String httpDevicesUrl = "http://ex-api.jalasmart.com/api/v2/energy/5b99b84a4c1d6e0b80b22534/2018-10-30";
	    String jsonObj = "{\"DeviceID\":\"5b99b84a4c1d6e0b80b22534\",\"Date\":\"2018-10-30\"}";

	    String Acctoken = gettoken();
	    String Nonce = createRandomString(6);
	    String TimeStamp = timeStamp();
	    String SignatureASCll = "Date=2018-10-30&DeviceID=5b99b84a4c1d6e0b80b22534&Nonce=" + Nonce + "&" + 
	      "TimeStamp=" + TimeStamp + "&UserID=5bdbc2fefdd73f197c373bad&Token=" + Acctoken;

	    String Signature = MD5(SignatureASCll);
	    String BASE64String = "{\"TimeStamp\":\"" + TimeStamp + "\",\"UserID\":\"5bdbc2fefdd73f197c373bad\",\"Nonce\":\"" + Nonce + "\",\"Signature\":\"" + Signature + "\"}";
	    String Authorization = getBase64(BASE64String);

	    String httpDecices = sendGetPram(httpDevicesUrl, jsonObj, Authorization);

	    JSONObject datastream = JSONObject.fromObject(httpDecices);

	    JSONArray JSONList1 = datastream.getJSONArray("Data");

	    for (int n = 0; n < JSONList1.size(); n++) {
	      Controller1 cont = new Controller1();
	      cont.setAddTime(JSONList1.getJSONObject(n).getString("AddTime"));
	      cont.setControllerID(JSONList1.getJSONObject(n).getString("ControllerID"));
	      cont.setEnergyID(JSONList1.getJSONObject(n).getString("EnergyID"));
	      cont.setInterval(JSONList1.getJSONObject(n).getString("Interval"));
	      cont.setTimeStamp(JSONList1.getJSONObject(n).getString("TimeStamp"));

	      JSONArray JSONList = JSONList1.getJSONObject(n).getJSONArray("Lines");
	      List list = new ArrayList();
	      for (int i = 0; i < JSONList.size(); i++) {
	        PowerData power = new PowerData();
	        power.setEnergy(JSONList.getJSONObject(i).getString("Energy"));
	        power.setFees(JSONList.getJSONObject(i).getString("Fees"));
	        power.setIcon(JSONList.getJSONObject(i).getString("Icon"));
	        power.setLineNo(JSONList.getJSONObject(i).getString("LineNo"));
	        power.setName(JSONList.getJSONObject(i).getString("Name"));
	        list.add(power);
	      }
	      this.service.electricalData(cont, list);
	    }
	  }

	
	
	 public static String sendGetPram(String url, String jsonObj ,String Authorization) {
	        String result = "";
	        BufferedReader in = null;
	        try {
	            String urlNameString = url;
	            URL realUrl = new URL(urlNameString);
	            // 鎵撳紑鍜孶RL涔嬮棿鐨勮繛鎺�
	            URLConnection connection = realUrl.openConnection();
	            // 璁剧疆閫氱敤鐨勮姹傚睘鎬�
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            connection.setRequestProperty("Accept-Charset", "utf-8");
	            connection.setRequestProperty("contentType", "utf-8");
	            
		        String  Authorization1 =Authorization.replaceAll("\r|\n", "");
		        connection.setRequestProperty("Authorization", Authorization1);
		        connection.setRequestProperty("Body", jsonObj);
		        
	            connection.connect();
	            Map<String, List<String>> map = connection.getHeaderFields();
	            for (String key : map.keySet()) {
	                System.out.println(key + "--->" + map.get(key));
	            }
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        return result;
	    }
	
	
	
	  public static String sendGet4(String url,String Authorization) {
	        String result = "";
	        BufferedReader in = null;
	        try {
	            String urlNameString = url;
	            URL realUrl = new URL(urlNameString);
	            URLConnection connection = realUrl.openConnection();
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            connection.setRequestProperty("Accept-Charset", "utf-8");
	            connection.setRequestProperty("contentType", "utf-8");
	            
	           String  Authorization1 =Authorization.replaceAll("\r|\n", "");
	            connection.setRequestProperty("Authorization", Authorization1);
	            connection.connect();
	            Map<String, List<String>> map = connection.getHeaderFields();
	            for (String key : map.keySet()) {
	                System.out.println(key + "--->" + map.get(key));
	            }
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        return result;
	    }
	
	//鑾峰彇token
	public String gettoken4(){  
		String   Acctoken ="6e3fabe1-3a13-46df-9e18-f7c7e3051c55";
		return Acctoken;
	}
	

	// 鑾峰彇token
	public String gettoken2() {

		String Acctoken = "6e3fabe1-3a13-46df-9e18-f7c7e3051c55";
		return Acctoken;
	}

	// Base64 鍔犲瘑
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	// 鑾峰彇鏃堕棿鎴�
	public static String timeStamp1() {
		long time = System.currentTimeMillis();
		String t = String.valueOf(time / 1000);
		return t;
	}

	// 鐢熸垚鎸囧畾闀垮害鐨勯殢鏈哄瓧绗︿覆
	public static synchronized String createRandomString1(int length) {
		if (length > 0) {
			int index = 0;
			char[] temp = new char[length];
			int num = random.nextInt();
			for (int i = 0; i < length % 5; i++) {
				temp[index++] = ch[num & 63];// 鍙栧悗闈㈠叚浣嶏紝璁板緱瀵瑰簲鐨勪簩杩涘埗鏄互琛ョ爜褰㈠紡瀛樺湪鐨勩�
				num >>= 6;// 63鐨勪簩杩涘埗涓�111111
				// 涓轰粈涔堣鍙崇Щ6浣嶏紵鍥犱负鏁扮粍閲岄潰涓�叡鏈�4涓湁鏁堝瓧绗︺�涓轰粈涔堣闄�鍙栦綑锛熷洜涓轰竴涓猧nt鍨嬭鐢�涓瓧鑺傝〃绀猴紝涔熷氨鏄�2浣嶃�
			}
			for (int i = 0; i < length / 5; i++) {
				num = random.nextInt();
				for (int j = 0; j < 5; j++) {
					temp[index++] = ch[num & 63];
					num >>= 6;
				}
			}
			return new String(temp, 0, length);
		} else if (length == 0) {
			return "";
		} else {
			throw new IllegalArgumentException();
		}
	}

	public String getAuthorization1(String Acctoken) {
		String Nonce = createRandomString(6);
		String TimeStamp = timeStamp();
		String SignatureASCll = "Nonce=" + Nonce + "&SN=J2512113071192&"
				+ "TimeStamp=" + TimeStamp
				+ "&UserID=5bdbc2fefdd73f197c373bad&Token=" + Acctoken + "";

		String Signature = MD5(SignatureASCll);
		String BASE64String = "{\"TimeStamp\":\"" + TimeStamp
				+ "\",\"UserID\":\"5bdbc2fefdd73f197c373bad\",\"Nonce\":\""
				+ Nonce + "\",\"Signature\":\"" + Signature + "\"}";
		String Authorization = getBase64(BASE64String);

		return Authorization;
	}

	// get 璇锋眰 闇�甯uthorization 鐨� 鍜�util 閲岄潰鐨勫苟涓嶄竴鏍�
	public static String sendGet1(String url, String Authorization) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 鎵撳紑鍜孶RL涔嬮棿鐨勮繛鎺�
			URLConnection connection = realUrl.openConnection();
			// 璁剧疆閫氱敤鐨勮姹傚睘鎬�
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("contentType", "utf-8");

			String Authorization1 = Authorization.replaceAll("\r|\n", "");
			connection.setRequestProperty("Authorization", Authorization1);
			// 寤虹珛瀹為檯鐨勮繛鎺�
			connection.connect();
			// 鑾峰彇鎵�湁鍝嶅簲澶村瓧娈�
			Map<String, List<String>> map = connection.getHeaderFields();
			// 閬嶅巻鎵�湁鐨勫搷搴斿ご瀛楁
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 瀹氫箟 BufferedReader杈撳叆娴佹潵璇诲彇URL鐨勫搷搴�
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		// 浣跨敤finally鍧楁潵鍏抽棴杈撳叆娴�
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 获取token
	public String gettoken() {
		// 获取token 30天
		// String tokenUrl =
		// "http://ex-api.jalasmart.com/api/v2/platform/token";
		// String tokenparam
		// ="{\"ID\":\"5bdbc2fefdd73f197c373bad\",\"Secret\":\"ec6a4db5095843938de346a03bf5fb9f\"}"
		// ;
		// String httpAcctoken = doPut(tokenUrl, tokenparam);
		// System.out.println("返回的token请求是"+httpAcctoken);
		// JSONObject datastream = JSONObject.fromObject(httpAcctoken);
		// String Acctoken = datastream.getString("Data");
		// System.out.println("返回的token字符串是"+Acctoken);
		String Acctoken = "6e3fabe1-3a13-46df-9e18-f7c7e3051c55";
		return Acctoken;
	}

	// 获取签名
	public String getAuthorization(String Acctoken) {
		// 根据token 加时间 userID等 获得签名

		// 签名算法如下：
		// 第一步：
		// 将待签名字符串按ASCll排序拼接为如下形式并进行MD5加密：
		// MD5(DeviceID=5971b3a143faee11ac72cd5a&
		// Nonce=#ew4Ex&
		// TimeStamp=1510118413&
		// UserID=5971b10e43faee239cb4abaa&
		// Token=4092a988-73e4-4250-acde-3b4ccfa645ea) =
		// =7252be41366e72f8f77fec289f9e0797
		// 最后得到：Signature

		// 按ASCll排序拼接 的字符
		// Nonce 长度为6的随机字符串，用于生成签名
		String Nonce = createRandomString(6);
		// TimeStamp=1510118413& 精确到秒的当前时间戳
		// String TimeStamp= Util.getDateTime();
		String TimeStamp = timeStamp();
		// DeviceID=5bdbc2fefdd73f197c373bad&

		String SignatureASCll = "Nonce=" + Nonce + "&" + "TimeStamp="
				+ TimeStamp + "&UserID=5bdbc2fefdd73f197c373bad&Token="
				+ Acctoken + "";
		System.out.println("SignatureASCll的值是" + SignatureASCll);

		String Signature = MD5(SignatureASCll);
		System.out.println("Signature的值是" + Signature);
		// 第二步：
		// 使用TimeStamp，Nonce，UserID以及第一步得到的Signature转化为JSON字符串后进 行BASE64加密：
		// BASE64({"TimeStamp":"1510118413","UserID":"5971b10e43faee239cb4abaa","Nonce":
		// "#ew4Ex","Signature":"7252be41366e72f8f77fec289f9e0797"})

		String BASE64String = "{\"TimeStamp\":\"" + TimeStamp
				+ "\",\"UserID\":\"5bdbc2fefdd73f197c373bad\",\"Nonce\":\""
				+ Nonce + "\",\"Signature\":\"" + Signature + "\"}";
		System.out.println("BASE64String的值是" + BASE64String);
		String Authorization = getBase64(BASE64String);
		System.out.println("Authorization的值是" + Authorization);
		return Authorization;
	}

	// 获取token时 调用的put 请求
	@SuppressWarnings("deprecation")
	public String doPut(String uri, String jsonObj) {
		// String SERVER_URL=" http://ex-api.jalasmart.com/";
		String resStr = null;
		HttpClient htpClient = new HttpClient();
		PutMethod putMethod = new PutMethod(uri);
		putMethod.addRequestHeader("Content-Type", "application/json");
		putMethod.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		putMethod.setRequestBody(jsonObj);
		try {
			int statusCode = htpClient.executeMethod(putMethod);
			// log.info(statusCode);
			if (statusCode != HttpStatus.SC_OK) {

				return null;
			}
			byte[] responseBody = putMethod.getResponseBody();
			resStr = new String(responseBody, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			putMethod.releaseConnection();
		}
		return resStr;
	}

	// 生成指定长度的随机字符串
	private static char ch[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', '0', '1' };// 最后又重复两个0和1，因为需要凑足数组长度为64
	// 生成指定长度的随机字符串
	private static Random random = new Random();

	// 生成指定长度的随机字符串
	public static synchronized String createRandomString(int length) {
		if (length > 0) {
			int index = 0;
			char[] temp = new char[length];
			int num = random.nextInt();
			for (int i = 0; i < length % 5; i++) {
				temp[index++] = ch[num & 63];// 取后面六位，记得对应的二进制是以补码形式存在的。
				num >>= 6;// 63的二进制为:111111
				// 为什么要右移6位？因为数组里面一共有64个有效字符。为什么要除5取余？因为一个int型要用4个字节表示，也就是32位。
			}
			for (int i = 0; i < length / 5; i++) {
				num = random.nextInt();
				for (int j = 0; j < 5; j++) {
					temp[index++] = ch[num & 63];
					num >>= 6;
				}
			}
			return new String(temp, 0, length);
		} else if (length == 0) {
			return "";
		} else {
			throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings("deprecation")
	public String doPutAuthorization(String uri, String jsonObj,
			String Authorization) {
		// String SERVER_URL=" http://ex-api.jalasmart.com/";
		String resStr = null;
		HttpClient htpClient = new HttpClient();
		PutMethod putMethod = new PutMethod(uri);
		putMethod.addRequestHeader("Content-Type", "application/json");
		String Authorization1 = Authorization.replaceAll("\r|\n", "");
		putMethod.addRequestHeader("Authorization", Authorization1);
		putMethod.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		putMethod.setRequestBody(jsonObj);
		try {
			int statusCode = htpClient.executeMethod(putMethod);
			// log.info(statusCode);
			if (statusCode != HttpStatus.SC_OK) {

				return null;
			}
			byte[] responseBody = putMethod.getResponseBody();
			resStr = new String(responseBody, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			putMethod.releaseConnection();
		}
		return resStr;
	}

	// get 请求 需要带Authorization 的 和 util 里面的并不一样
	public static String sendGet(String url, String Authorization) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("contentType", "utf-8");

			String Authorization1 = Authorization.replaceAll("\r|\n", "");
			connection.setRequestProperty("Authorization", Authorization1);
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 返回的token请求是{"$id":"1","Code":1,"Message":"success","Data":"6e3fabe1-3a13-46df-9e18-f7c7e3051c55"}
	// 返回的token字符串是6e3fabe1-3a13-46df-9e18-f7c7e3051c55
	// Signature的值是279576ba747df0e628674879c63d7b2d
	// BASE64String的值是"TimeStamp":"1510118413","UserID":"5bdbc2fefdd73f197c373bad","Nonce":"6kxdqG","Signature":"279576ba747df0e628674879c63d7b2d"
	// Authorization的值是IlRpbWVTdGFtcCI6IjE1MTAxMTg0MTMiLCJVc2VySUQiOiI1YmRiYzJmZWZkZDczZjE5N2MzNzNi
	// YWQiLCJOb25jZSI6IjZreGRxRyIsIlNpZ25hdHVyZSI6IjI3OTU3NmJhNzQ3ZGYwZTYyODY3NDg3
	// OWM2M2Q3YjJkIg==

	public static String timeStamp() {
		long time = System.currentTimeMillis();
		String t = String.valueOf(time / 1000);
		return t;
	}

	/**
	 * 对字符串进行md5加密
	 * 
	 * @param 杨德志
	 * @return
	 */
	private final static char hexDigits[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public final static String MD5(String s) {
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
}

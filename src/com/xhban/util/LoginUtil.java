package com.xhban.util;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.xhban.exception.CheckCodeObtainFailedException;
import com.xhban.exception.CourseInfoObtainFailedException;
import com.xhban.exception.LoadFailedException;
import com.xhban.exception.LoginFailedException;
import com.xhban.exception.PersonInfoObtainFailedException;

public class LoginUtil {
	/**
	 * �����¼���棬��ȡviewState,ͨ��session����viewState��loginCookie
	 * 
	 * @param session
	 * @throws LoadFailedException
	 * @throws Exception
	 * @throws Exception
	 */
	public static void doGet(CloseableHttpClient httpClient, CookieStore cookieStore, HttpSession session)
			throws LoadFailedException, Exception {
		String content = null;
		String viewStateStr = null;
		HttpGet httpGet = new HttpGet("http://jwc.weilylab.com:10086/");
		CloseableHttpResponse response = httpClient.execute(httpGet);
		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode == 200) {
			content = EntityUtils.toString(response.getEntity(), "gb2312");
			String regex = "STATE\"\\p{Blank}value=\".+";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(content);
			if (matcher.find()) {
				String middleStr = matcher.group(0).substring(14);
				int endMark = middleStr.indexOf("\"");
				viewStateStr = middleStr.substring(0, endMark);
				session.setAttribute("viewState", viewStateStr == null ? null : viewStateStr);// ����ViewState
				List<Cookie> cookies = cookieStore.getCookies();
				StringBuilder cookieValue = new StringBuilder();
				for (Cookie cookie : cookies) {
					cookieValue.append(cookie.getName() + "=" + cookie.getValue() + ";");
				}
				session.setAttribute("loginCookie", cookieValue);// ����loginCookie
			}
		} else {
			throw new LoadFailedException("����ϵͳʧ�ܣ� ����: " + stateCode);
		}
		response.close();
	}

	/**
	 * ��ȡ��֤��,ͨ��session������֤���Base64����
	 * 
	 * @param logincookie
	 * @param session
	 * @throws CheckCodeObtainFailedException
	 * @throws Exception
	 */
	public static void downloadCheckCode(CloseableHttpClient httpClient, String logincookie, HttpSession session)
			throws CheckCodeObtainFailedException, Exception {
		InputStream in = null;
		HttpGet httpGet = new HttpGet("http://jwc.weilylab.com:10086/CheckCode.aspx");
		httpGet.addHeader("Cookie", logincookie); // ����cookie������֤��
		String checkcodeBase64 = null;
		final CloseableHttpResponse response = httpClient.execute(httpGet);
		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode == 200) {
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				in = entity.getContent();
				byte[] buffer = new byte[in.available()];
				in.read(buffer, 0, in.available());
				checkcodeBase64 = Base64Util.encodeBufferBase64(buffer);
			}
			session.setAttribute("checkcodeBase64", checkcodeBase64 == null ? null : checkcodeBase64);// ������֤��
		} else {
			throw new CheckCodeObtainFailedException("��֤���ȡʧ��! ����: " + stateCode);
		}
		if (in != null) {
			in.close();
		}
		response.close();
	}

	/**
	 * ��¼
	 * 
	 * @param logincookie
	 * @param viewstate
	 * @param sno
	 * @param password
	 * @param checkcode
	 * @param session
	 * @throws LoginFailedException
	 * @throws Exception
	 */
	public static boolean doPost(CloseableHttpClient httpClient, String logincookie, String viewstate, String sno,
			String password, String checkcode) throws LoginFailedException, Exception {
		HttpPost httpPost = new HttpPost("http://jwc.weilylab.com:10086/default2.aspx");
		httpPost.setHeader("Cookie", logincookie);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("txtUserName", sno));
		params.add(new BasicNameValuePair("TextBox2", password));
		params.add(new BasicNameValuePair("txtSecretCode", checkcode));
		params.add(new BasicNameValuePair("__VIEWSTATE", viewstate));
		params.add(new BasicNameValuePair("RadioButtonList1", "%D1%A7%C9%FA"));
		params.add(new BasicNameValuePair("Button1", ""));
		params.add(new BasicNameValuePair("hidsc", ""));
		params.add(new BasicNameValuePair("lbLanguage", ""));
		params.add(new BasicNameValuePair("hidPdrs", ""));
		UrlEncodedFormEntity sendEntity = new UrlEncodedFormEntity(params, "gb2312");
		httpPost.setEntity(sendEntity);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode == 200) {
			String responseText = EntityUtils.toString(response.getEntity());
			if (responseText.contains("��ӭ����")) {
				return true;
			}
			//System.out.println(responseText);
			if (responseText.contains("���¼")) {
				String regex = "defer>alert(.*);";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(responseText);
				if (matcher.find()) {
					String[] messages = matcher.group(0).split("'");
					throw new LoginFailedException(messages[1]);
				}
			}
		} else {
			throw new LoginFailedException("��¼ʧ�� ����: " + stateCode);
		}
		response.close();
		return false;
	}

	/**
	 * ���������Ϣ
	 * 
	 * @param sno
	 * @param session
	 * @throws PersonInfoObtainFailedException
	 * @throws Exception
	 */
	public static String doRequestPersonInfo(CloseableHttpClient httpClient, String loginCookie, String sno)
			throws PersonInfoObtainFailedException, Exception {
		String personInfo = null;
		String address = "http://jwc.weilylab.com:10086/xsgrxx.aspx?xh=" + sno + "&gnmkdm=N121501";
		HttpGet httpGet = new HttpGet(address);
		httpGet.addHeader("Cookie", loginCookie);
		httpGet.addHeader("Referer", address);
		final CloseableHttpResponse response = httpClient.execute(httpGet);
		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode == 200) {
			personInfo = EntityUtils.toString(response.getEntity());

		} else {
			throw new PersonInfoObtainFailedException("������Ϣ��ȡʧ�� ����: " + stateCode);
		}
		response.close();
		return personInfo == null ? null : personInfo;
	}

	/**
	 * ����γ���Ϣ
	 * 
	 * @param sno
	 * @param session
	 * @throws CourseInfoObtainFailedException
	 * @throws Exception
	 */
	public static String doRequestCourseInfo(CloseableHttpClient httpClient, String loginCookie, String sno)
			throws CourseInfoObtainFailedException, Exception {
		String courseInfo = null;
		String address = "http://jwc.weilylab.com:10086/xskbcx.aspx?xh=" + sno + "&gnmkdm=N121603";
		HttpGet httpGet = new HttpGet(address);
		httpGet.addHeader("Cookie", loginCookie);
		httpGet.addHeader("Referer", address);
		final CloseableHttpResponse response = httpClient.execute(httpGet);
		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode == 200) {
			courseInfo = EntityUtils.toString(response.getEntity());
		} else {
			throw new CourseInfoObtainFailedException("�γ���Ϣ��ȡʧ�� ����: " + stateCode);
		}
		response.close();
		return courseInfo == null ? null : courseInfo;
	}

}

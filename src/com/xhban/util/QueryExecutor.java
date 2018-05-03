package com.xhban.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.xhban.exception.LoadFailedException;

public class QueryExecutor {

	// ����γ���Ϣ
	public static String doRequestCourseInfo(CloseableHttpClient httpClient, String sno) throws Exception {
		String courseInfo = "";
		String address = "http://jwc.weilylab.com:10086/xskbcx.aspx?xh=" + sno + "&gnmkdm=N121603";
		HttpGet httpGet = new HttpGet(address);
		httpGet.addHeader("Referer", address);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		int stateCode = response.getStatusLine().getStatusCode();
		// System.out.println(EntityUtils.toString(response.getEntity()));
		if (stateCode == 200) {
			System.out.println("�γ���Ϣ��ȡ�ɹ���");
			courseInfo = EntityUtils.toString(response.getEntity());
		} else {
			// System.out.println("�γ���Ϣ��ȡʧ�ܣ� �������: " + stateCode);
			courseInfo = "�γ���Ϣ��ȡʧ�ܣ� �������: " + stateCode;
		}
		response.close();
		return courseInfo;
	}

	// ���������Ϣ
	public static String doRequestPersonInfo(CloseableHttpClient httpClient, String sno) throws Exception {
		String personInfo = "";
		String address = "http://jwc.weilylab.com:10086/xsgrxx.aspx?xh=" + sno + "&gnmkdm=N121501";
		HttpGet httpGet = new HttpGet(address);
		httpGet.addHeader("Referer", address);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode == 200) {
			System.out.println("������Ϣ��ȡ�ɹ���");
			personInfo = EntityUtils.toString(response.getEntity());
			// System.out.println(EntityUtils.toString(response.getEntity()));
		} else {
			// System.out.println(EntityUtils.toString(response.getEntity()));
			// System.out.println("������Ϣ��ȡʧ�ܣ� �������: " + stateCode);
			personInfo = "������Ϣ��ȡʧ�ܣ� �������: " + stateCode;
		}
		response.close();
		return personInfo;
	}

	// �ύѧ������
	public static String doPost(CloseableHttpClient httpClient, String viewstate, String sno, String password,
			String checkcode) throws Exception {
		String content = "";
		HttpPost httpPost = new HttpPost("http://jwc.weilylab.com:10086/default2.aspx");
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
		content = EntityUtils.toString(response.getEntity());
		if (stateCode != 200) {
			// System.out.println("��¼ʧ�ܣ� �������: " + stateCode);
		}
		response.close();
		return content;
	}

	// ��ȡ��֤��
	public static void downloadCheckCode(CloseableHttpClient httpClient, String path) throws Exception {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		HttpGet httpGet2 = new HttpGet("http://jwc.weilylab.com:10086/CheckCode.aspx");
		httpGet2.addHeader("Cache-Control", "no-cache");
		InputStream in = null;
		CloseableHttpResponse response = httpClient.execute(httpGet2);
		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode == 200) {
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity2 = response.getEntity();
				in = entity2.getContent();
				int len;
				byte[] buffer = new byte[1024 * 20];
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.flush();
				// System.out.println("��֤���ȡ�ɹ�...");
				// System.out.println("��·��Ϊ:" + file.getAbsolutePath());
			}
		} else {
			System.out.println("��ȡ��֤��ʧ�ܣ� �������: " + stateCode);
		}
		if (response != null) {
			response.close();
		}
		if (out != null) {
			out.close();
		}
		if (in != null) {
			in.close();
		}
	}

	// �����¼ҳ,��ȡViewState
	public static String doGet(CloseableHttpClient httpClient, String path) throws Exception {
		HttpGet httpGet = new HttpGet("http://jwc.weilylab.com:10086/");
		CloseableHttpResponse response = httpClient.execute(httpGet);
		String content = "";
		String viewStateStr = "";
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
			}
			if (response != null) {
				response.close();
			}
		} else {
			throw new LoadFailedException("����ϵͳʧ��,����:"+stateCode);
		}
		response.close();
		return viewStateStr;
	}
}

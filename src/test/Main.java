package test;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import com.xhban.exception.LoginFailedException;
import com.xhban.util.LoginUtil;

public class Main {
	public static void main(String[] args) {
		CookieStore cookieStore = new BasicCookieStore();
		HttpClientBuilder builder = HttpClients.custom().disableAutomaticRetries().setDefaultCookieStore(cookieStore)
				.setMaxConnTotal(50).setRedirectStrategy(new LaxRedirectStrategy());
		CloseableHttpClient httpClient = builder.build();
		String sno = "";
		String password = "";
		String viewState = "dDwyODE2NTM0OTg7Oz7Rr5aOZLvAf0HUCJ4zZGKSQ1/f4A==";
		String loginCookie = "ASP.NET_SessionId=4rsiiaz3ry21bs55jgs12e55;BIGipServerjiaowu=295662538.20480.0000;";
		String checkcode = "qfvw";
		try {
			boolean state = LoginUtil.doPost(httpClient, loginCookie, viewState, sno, password, checkcode);
			if (state) {
				System.out.println("µÇÂ¼³É¹¦");
			} else {
				System.out.println("Ê§°Ü");
			}
		} catch (LoginFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

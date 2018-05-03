package com.xhban.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import com.xhban.exception.CheckCodeObtainFailedException;
import com.xhban.exception.CourseInfoObtainFailedException;
import com.xhban.exception.LoadFailedException;
import com.xhban.exception.LoginFailedException;
import com.xhban.exception.PersonInfoObtainFailedException;
import com.xhban.util.*;

@WebServlet("*.CourseQuery")
public class CourseQuery extends HttpServlet {
	private static final long serialVersionUID = 4L;

	public CourseQuery() {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		CookieStore cookieStore = new BasicCookieStore();
		HttpClientBuilder builder = HttpClients.custom().disableAutomaticRetries().setDefaultCookieStore(cookieStore)
				.setMaxConnTotal(50).setRedirectStrategy(new LaxRedirectStrategy());
		CloseableHttpClient httpClient = builder.build();
		response.setContentType("text/html; charset=gb2312");
		response.setHeader("Access-Control-Allow-Origin", "*");// 允许跨域访问
		request.setCharacterEncoding("gb2312");
		PrintWriter out = response.getWriter();
		String path = request.getServletPath();
		switch (path) {
		case "/Load.CourseQuery":
			try {
				LoginUtil.doGet(httpClient, cookieStore, request.getSession());
				// 获取viewState
				LoginUtil.doGet(httpClient, cookieStore, request.getSession());
				String viewState = request.getSession().getAttribute("viewState").toString();
				String loginCookie = request.getSession().getAttribute("loginCookie").toString();
				// 获取验证码
				LoginUtil.downloadCheckCode(httpClient, loginCookie, request.getSession());
				String checkcodeBase64 = request.getSession().getAttribute("checkcodeBase64").toString();
				if (checkcodeBase64 != null) {
					// 设置请求参数
					out.println("{\"state\":\"1\",\"viewState\":\"" + viewState + "\",\"loginCookie\":\"" + loginCookie
							+ "\",\"checkcode\":\"" + checkcodeBase64 + "\"}");
				}
			} catch (LoadFailedException e) {
				out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
			} catch (CheckCodeObtainFailedException e) {
				out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
			} catch (Exception e) {
				out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
			}
			break;
		// 登录
		case "/Login.CourseQuery":
			String sno = request.getParameter("sno");
			String password = request.getParameter("password");
			String checkcode = request.getParameter("checkcode");
			String viewState = request.getParameter("viewState");
			String loginCookie = request.getParameter("loginCookie");
			try {
				if (viewState != null && loginCookie != null) {
					boolean loginState = LoginUtil.doPost(httpClient, loginCookie, viewState, sno, password, checkcode);
					if (loginState) {
						out.print("{\"state\":\"1\",\"message\":\"登录成功\"}");
					} else {
						out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
					}
				}
			} catch (LoginFailedException ex) {
				out.print("{\"state\":\"2\",\"message\":\"" + ex.toString() + "\"}");
			} catch (Exception e) {
				out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
			}
			break;
		// 查看个人信息
		case "/person_info.CourseQuery":
			try {
				String loginCookie2 = request.getParameter("loginCookie");
				String sno2 = request.getParameter("sno");
				String personInfo = LoginUtil.doRequestPersonInfo(httpClient, loginCookie2, sno2);
				if (personInfo != null)
					out.print("{\"state\":\"1\",\"message\":\"" + personInfo + "\"}");
			} catch (PersonInfoObtainFailedException e) {
				out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
			} catch (Exception e) {

				out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
			}
			break;
		// 查看课程信息
		case "/course_info.CourseQuery":
			try {
				String loginCookie2 = request.getParameter("loginCookie");
				String sno2 = request.getParameter("sno");
				String courseInfo = LoginUtil.doRequestCourseInfo(httpClient, loginCookie2, sno2);
				if (courseInfo != null)
					out.print("{\"state\":\"1\",\"message\":\"" + courseInfo + "\"}");
			} catch (CourseInfoObtainFailedException e) {
				out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
			} catch (Exception e) {
				out.print("{\"state\":\"0\",\"message\":\"发生未知错误\"}");
			}
			break;
		default:
			httpClient.close();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}

package org.akquinet.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticatorServlet extends HttpServlet
{
	private static final long serialVersionUID = 2690042938888465169L;
	
	private static final String NO_SSL_REDIRECT_URL = null;	//TODO value for NO_SSL_REDIRECT_URL
	private static final String MAIN_SERVLET_URL = "/main.html";
	private static final String COCHISE_PROPERTIES_PASSKEY = "cochisePass";

	public AuthenticatorServlet()
	{
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if(!request.isSecure())
		{
			response.sendRedirect(NO_SSL_REDIRECT_URL);
		}
		
		String pass = request.getParameter("pw");
		
		if(pass != null && authenticate(pass))
		{
			String sessionInfo = Integer.toString(SessionManager.getDefault().openNewSession());
			
			Cookie c = new Cookie("dixi", sessionInfo);
			c.setSecure(true);
			response.addCookie(c);
			response.sendRedirect(MAIN_SERVLET_URL);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}
	
	private boolean authenticate(String pass)
	{
		return System.getProperty(COCHISE_PROPERTIES_PASSKEY).equals(pass);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
}

package org.akquinet.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/checkLogin")
public class AuthenticatorServlet extends HttpServlet
{
	private static final long serialVersionUID = 2690042938888465169L;
	
	private static final String MAIN_SERVLET_URL = "inspector.jsp";
	private static final String COCHISE_PROPERTIES_PASSKEY = "cochisePass";
	private static final String LOGIN_PAGE = "login.jsp";
	
	private static boolean _someoneLoggedIn = false;

	public AuthenticatorServlet()
	{
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String logout = request.getParameter("logout");
		if(logout != null)
		{
			_someoneLoggedIn = false;
			request.getSession().removeAttribute("loggedIn");
			request.getSession().invalidate();
			response.sendRedirect(response.encodeRedirectURL(LOGIN_PAGE));
			return;
		}
		
		String pass = request.getParameter("password");
		if(pass != null && authenticate(pass) && !_someoneLoggedIn) //there can only be one person logged in (this application is not parallelity-safe)
		{
			_someoneLoggedIn = true;
			Enumeration<String> attributes = request.getSession().getAttributeNames();
			while(attributes.hasMoreElements())
			{
				request.getSession().removeAttribute( attributes.nextElement() );
			}
			
			request.getSession().setAttribute("runId", CommonData.RUN_ID);
			request.getSession().setAttribute("loggedIn", true);
			response.sendRedirect(response.encodeRedirectURL(MAIN_SERVLET_URL));
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

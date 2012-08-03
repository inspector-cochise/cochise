package org.akquinet.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns="/checkLogin")
public class AuthenticatorServlet extends HttpServlet
{
	private static final long serialVersionUID = 2690042938888465169L;
	
	private static final String COCHISE_PROPERTIES_PASSKEY = "cochisePass";
	
	private static String _sessionId = null;

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
			_sessionId = null;
			request.getSession().invalidate();
			response.sendRedirect(response.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL));
			return;
		}
		
		String pass = request.getParameter("password");
		if(pass != null && authenticate(pass) && _sessionId == null) //there can only be one person logged in (this application is not parallelity-safe)
		{
			_sessionId = request.getSession().getId();
			Enumeration<String> attributes = request.getSession().getAttributeNames();
			while(attributes.hasMoreElements())
			{
				request.getSession().removeAttribute( attributes.nextElement() );
			}
			
			response.sendRedirect(response.encodeRedirectURL(CommonData.MAIN_SERVLET_URL));
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

	/**
	 * 
	 * @param pass
	 * @return returns true if the password is ok and false otherwise
	 */
	public static boolean authenticate(String pass)
	{
		if(System.getProperty(COCHISE_PROPERTIES_PASSKEY) == null)
		{
			return false;
		}
		else
		{
			return System.getProperty(COCHISE_PROPERTIES_PASSKEY).equals(pass);
		}
	}

	/**
	 * 
	 * @param sess
	 * @return returns true if the session is ok and false otherwise
	 */
	public static boolean authenticate(HttpSession sess)
	{
		return _sessionId != null && sess.getId().equals(_sessionId);
	}
}

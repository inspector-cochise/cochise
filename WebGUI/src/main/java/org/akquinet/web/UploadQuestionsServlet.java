package org.akquinet.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akquinet.audit.QuestionManager;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(urlPatterns="/" + CommonData.UPLOADSTATE_URL)
public class UploadQuestionsServlet extends HttpServlet
{
	private static final long serialVersionUID = 4923048479384459817L;
	private static final int BUFFER_SIZE = 2048;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (!AuthenticatorServlet.authenticate(req.getSession()))
		{
			resp.sendRedirect(resp.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL));
			return;
		}
		
		ResourceBundle labels = ResourceBundle.getBundle("site", Locale.getDefault());
		if(!ServletFileUpload.isMultipartContent(req))
		{
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, labels.getString("E1"));
			return;
		}
		
		
		FileItemFactory factory = new DiskFileItemFactory(10000, new File( System.getProperty("java.io.tmpdir") ));
		ServletFileUpload upload = new ServletFileUpload(factory);
		try
		{
			byte[] buf = new byte[BUFFER_SIZE];
			@SuppressWarnings("unchecked")
			List<FileItem> list = upload.parseRequest(req);
			for(FileItem i : list)
			{
				if(!i.isFormField())
				{
					ZipInputStream zipInStream = new ZipInputStream(i.getInputStream());
					ZipEntry entry;
					
					while((entry = zipInStream.getNextEntry()) != null)
					{
						if(entry.isDirectory())
						{
							resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, labels.getString("E1"));
							return;
						}
						else
						{
							FileOutputStream fileOutStream = new FileOutputStream(CommonData.CochiseDataPath + File.separator + entry.getName(), false);
							
							int n;
							while((n = zipInStream.read(buf, 0 , BUFFER_SIZE)) > -1)
							{
								fileOutStream.write(buf, 0, n);
							}
							fileOutStream.close();
							zipInStream.closeEntry();
						}
					}
					
					zipInStream.close();
				}
			}
		}
		catch (FileUploadException e)
		{
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		catch (IOException e)
		{
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		QuestionManager.getDefault().loadPersistedQuestions();
		resp.sendRedirect( resp.encodeRedirectURL(CommonData.MAIN_SERVLET_URL) );
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doPost(req, resp);
	}
}

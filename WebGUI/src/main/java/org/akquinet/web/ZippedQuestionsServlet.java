package org.akquinet.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/" + CommonData.ZIPSTATE_URL)
public class ZippedQuestionsServlet extends HttpServlet
{
	private static final long serialVersionUID = -7891156454723962456L;
	private static final int BUFFER_SIZE = 2048;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (req.getSession().getAttribute("loggedIn") == null
				|| req.getSession().getAttribute("runId") == null
				|| !req.getSession().getAttribute("runId").equals(CommonData.RUN_ID)
			)
		{
			resp.sendRedirect(resp.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL));
			return;
		}

		resp.setContentType("application/zip");
		resp.setHeader("Content-Disposition", "attachment; filename=QuestionData.zip");
		
		File folderToZip = new File(CommonData.CochiseDataPath);
		
		ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(resp.getOutputStream()));
		
		byte data[] = new byte[BUFFER_SIZE];

		for (File f : folderToZip.listFiles())
		{
			BufferedInputStream reader = null;
			try
			{
				reader = new BufferedInputStream(new FileInputStream(f), BUFFER_SIZE);
				zipOut.putNextEntry(new ZipEntry(f.getName()));
				
				int count;
				
				while ((count = reader.read(data, 0, BUFFER_SIZE)) != -1)
				{
					zipOut.write(data, 0, count);
				}
			}
			catch(IOException e)
			{
				//maybe a corrupted file or a file that is beeing created at the same time - ignore it
				continue;
			}
			finally
			{
				if(reader != null)
				{
					reader.close();
				}
			}
		}
		zipOut.flush();
		zipOut.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
}

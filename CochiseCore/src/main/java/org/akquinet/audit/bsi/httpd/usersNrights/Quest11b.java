package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.DevNullUserCommunicator;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Context;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.httpd.syntax.Statement;
import org.akquinet.util.ResourceWatcher;

@Automated
public class Quest11b extends ResourceWatcher implements YesNoQuestion
{
	private static final long serialVersionUID = -6193788543271447392L;
	
	private static final String _id = "Quest11b";
	private ConfigFile _conf;
	private transient ResourceBundle _labels;
	private transient UserCommunicator _uc = UserCommunicator.getDefault();

	private Boolean _lastAnswer = null;

	public Quest11b(ConfigFile conf)
	{
		this(conf, UserCommunicator.getDefault());
	}
	
	public Quest11b(ConfigFile conf, UserCommunicator uc)
	{
		_uc = uc;
		_conf = conf;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public synchronized boolean answer()
	{
		_lastAnswer = answer(_uc);
		return _lastAnswer;
	}

	private boolean answer(UserCommunicator uc)
	{
		uc.printHeading3( _labels.getString("name") );
		uc.printParagraph( _labels.getString("Q0") );
		uc.printExample( "<Directory />\n\tOrder Deny,Allow\n\tDeny from all\n</Directory>" );

		List<Statement> statList;
		List<Context> dirList;
		
		synchronized(_conf)
		{
			//first of all let's ensure, there are no "hidden" order/allow/deny directives like in
			//neg_contained3.conf (see JUnit tests)
			{
				for(String s : new String[] {"order", "allow", "deny"})
				{
					boolean r = checkForConditionallyActives(s, uc);
					if(!r)
					{
						printHidingParagraph(uc);
						return r;
					}
				}
			}
			
			statList = _conf.getHeadExpression().getStatements()._statements;
			dirList = createDirectoryRoot_List(statList);
		}
		
		if(dirList.isEmpty())
		{
			uc.printAnswer(false, _labels.getString("S8"));
		}

		boolean ret = false;
		for (Context dir : dirList)
		{
			if (dir.getSurroundingContexts().get(0) != null)
			{
				continue;
			}

			List<Directive> orderList = dir.getAllDirectives("Order");
			List<Directive> denyList = dir.getAllDirectives("Deny");
			List<Directive> allowList = dir.getAllDirectivesIgnoreCase("allow");
			if (allowList.size() == 0 &&
				orderList.size() == 1 &&
				denyList.size() == 1 &&
				orderList.get(0).getLinenumber() < denyList.get(0).getLinenumber())
			{
				Directive order = orderList.get(0);
				Directive deny = denyList.get(0);
				// fast evaluation ensures, that only one of these methods can
				// output an answer-message
				if (orderIsOK(order, uc) && denyIsOK(deny, uc))
				{
					uc.printAnswer(true, _labels.getString("S2"));
					ret = true;
				}
				else
				{
					ret = false;
					break;
				}
			}
			else if(allowList.size() > 0)
			{
				uc.printAnswer(false, _labels.getString("S1"));
				StringBuilder b = new StringBuilder();
				for (Directive directive : allowList)
				{
					b = b.append(directive.getContainingFile()).append(":").append(directive.getLinenumber()).append(": ").append(directive.getName()).append(" ").append(directive.getValue()).append('\n');
				}
				uc.printExample(b.toString());
				ret = false;
				break;
			}
			else if(orderList.size() == 0 || denyList.size() == 0)
			{
				uc.printAnswer(false, _labels.getString("S8"));
				ret = false;
				break;
			}
			else
			{
				uc.printAnswer(false, MessageFormat.format(_labels.getString("S3"), dir.getBeginLineNumber(), dir.getEndLineNumber(), dir.getContainingFile()));
				//_uc.printAnswer(false, "I found multiple and/or incorrectly sorted \"Order\", \"Deny\" or \"Allow\" directives betwenn lines "
				//		+ dir.getBeginLineNumber() + " and " + dir.getEndLineNumber() + ". Please make them unique, sort them and run me again.");
				ret = false;
				break;
			}
		}

		printHidingParagraph(uc);
		return ret;
	}

	private void printHidingParagraph(UserCommunicator uc)
	{
		uc.beginHidingParagraph(_labels.getString("S7"));
			uc.printParagraph(_labels.getString("P1"));
			uc.printExample("<VirtualHost *:80>\n" +
								"\t<some_other_context>\n" +
									"\t\t<Directory />\n" +
										"\t\t\tOrder Deny,Allow\n" +
										"\t\t\tDeny from all\n" +
									"\t\t</Directory>\n" +
								"\t</some_other_context>\n" +
							  "</VirtualHost>");
			uc.printParagraph(_labels.getString("P2"));
		uc.endHidingParagraph();
	}

	private boolean checkForConditionallyActives(String directiveType, UserCommunicator uc)
	{
		List<Directive> dirList = _conf.getAllDirectivesIgnoreCase(directiveType);
		for (Directive dir : dirList)
		{
			if(dir.getSurroundingContexts().size() > 1 &&
			   dir.getSurroundingContexts().get(0).getName().equalsIgnoreCase("Directory") &&
			   dir.getSurroundingContexts().get(0).getValue().trim().equals("/") &&
			   dir.getSurroundingContexts().get(1) != null
			   )
			{
				uc.printAnswer(false, MessageFormat.format(_labels.getString("S4"), dir.getLinenumber(), dir.getContainingFile()) );
				return false;
			}
		}
		return true;
	}

	private boolean denyIsOK(Directive deny, UserCommunicator uc)
	{
		if( ! deny.getSurroundingContexts().get(0).getName().equalsIgnoreCase("Directory") ||
			! deny.getSurroundingContexts().get(0).getValue().trim().equals("/")
		   )
		{
			return false;	//This shouldn't happen
		}
		
		if (deny.getValue().matches("( |\t)*from all( |\t)*"))
		{
			return true;
		}
		else
		{
			uc.printAnswer(false, _labels.getString("S5"));
			uc.println(deny.getContainingFile() + ":" + deny.getLinenumber() + ": " + deny.getName() + " " + deny.getValue());
			return false;
		}
	}

	private boolean orderIsOK(Directive order, UserCommunicator uc)
	{
		if( ! order.getSurroundingContexts().get(0).getName().equalsIgnoreCase("Directory") ||
			! order.getSurroundingContexts().get(0).getValue().trim().equals("/")
		   )
		{
			return false;	//This shouldn't happen
		}
		
		if (order.getValue().matches("( |\t)*[Dd]eny,[Aa]llow( |\t)*"))
		{
			return true;
		}
		else
		{
			uc.printAnswer(false, _labels.getString("S6"));
			uc.println(order.getContainingFile() + ":" + order.getLinenumber() + ": " + order.getName() + " " + order.getValue());
			return false;
		}
	}

	protected static List<Context> createDirectoryRoot_List(List<Statement> statList)
	{
		List<Context> conList = new LinkedList<Context>();
		for (Statement stat : statList)
		{
			if (stat instanceof Context)
			{
				Context con = (Context) stat;
				if (con.getName().equalsIgnoreCase("directory"))
				{
					if (con.getSurroundingContexts().size() == 1)
					{
						if (con.getValue().matches("( |\t)*/( |\t)*"))
						{
							conList.add(con);
						}
					}
				}
			}
		}
		return conList;
	}

	@Override
	public boolean isCritical()
	{
		return false;
	}

	@Override
	public String getID()
	{
		return _id;
	}

	@Override
	public int getBlockNumber()
	{
		return 2147483647;
	}

	@Override
	public int getNumber()
	{
		return 2147483647;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}
	
	@Override
	public void initialize() throws Exception
	{
		synchronized(_conf)
		{
			_conf.reparse();
		}
	}
	
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		_uc = UserCommunicator.getDefault();
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}
	
	@Override
	public String getName()
	{
		return _labels.getString("name");
	}

	@Override
	public void setUserCommunicator(UserCommunicator uc)
	{
		_uc = uc;
	}
	
	@Override
	public String getResourceId()
	{
		return "quest." + _id;
	}

	@Override
	public boolean resourceChanged()
	{
		try
		{
			initialize();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		//this is just a not so intelligent dummy-solution
		boolean answer = answer(new DevNullUserCommunicator());
		
		if(_lastAnswer != null && answer != _lastAnswer)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

package org.akquinet.audit.bsi.httpd.usersNrights;

import java.util.LinkedList;
import java.util.List;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Context;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.httpd.syntax.Statement;

public class Quest11b implements YesNoQuestion
{
	private static final String _id = "Quest11b";
	private ConfigFile _conf;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q2;

	public Quest11b(ConfigFile conf)
	{
		_conf = conf;
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, "----" + _id + "----");

		//first of all let's ensure, there are no "hidden" order/allow/deny directives like in
		//neg_contained3.conf (see JUnit tests)
		{
			for(String s : new String[] {"order", "allow", "deny"})
			{
				boolean r = checkForConditionallyActives(s);
				if(!r)
				{
					return r;
				}
			}
		}
		
		List<Statement> statList = _conf.getHeadExpression().getStatements()._statements;
		List<Context> dirList = createDirectoryRoot_List(statList);

		boolean ret = false;
		for (Context dir : dirList)
		{
			if (!dir.getDirectiveIgnoreCase("allow").isEmpty())
			{
				_console.printAnswer(_level, false, "Nobody should be able to access \"/\". Remove the following \"Allow\" directives:");
				List<Directive> allowList = dir.getDirectiveIgnoreCase("allow");
				for (Directive directive : allowList)
				{
					_console.println(_level, directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
				}
				return false;
			}

			if (dir.getSurroundingContexts().get(0) != null)
			{
				continue;
			}

			List<Directive> orderList = dir.getAllDirectives("Order");
			List<Directive> denyList = dir.getAllDirectives("Deny");
			if (orderList.size() == 1 &&
				denyList.size() == 1 &&
				orderList.get(0).getLinenumber() < denyList.get(0).getLinenumber())
			{
				Directive order = orderList.get(0);
				Directive deny = denyList.get(0);
				// fast evaluation ensures, that only one of these methods can
				// output an answer-message
				if (orderIsOK(order) && denyIsOK(deny))
				{
					_console.printAnswer(_level, true, "Access to \"/\" correctly blocked via mod_access.");
					ret = true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				_console.printAnswer(_level, false, "I found multiple and/or incorrectly sorted \"Order\" and \"Deny\" directives betwenn lines "
						+ dir.getBeginLineNumber() + " and " + dir.getEndLineNumber() + ". Please make them unique, sort them and run me again.");
				return false;
			}
		}

		return ret;
	}

	private boolean checkForConditionallyActives(String directiveType)
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
				_console.printAnswer(_level, false, "The directive in line " + dir.getLinenumber() + " may only be conditionally active. Move it in a <Directory /> Context not contained in any other context.");
				return false;
			}
		}
		return true;
	}

	private static boolean denyIsOK(Directive deny)
	{
		if( ! deny.getSurroundingContexts().get(0).getName().equalsIgnoreCase("Directory") ||
			! deny.getSurroundingContexts().get(0).getValue().trim().equals("/")
		   )
		{
			return false;
		}
		
		if (deny.getValue().matches("( |\t)*from all( |\t)*"))
		{
			return true;
		}
		else
		{
			_console.printAnswer(_level, false, "Nobody should be able to access \"/\". Correct the following directive to \"Deny from all\".");
			_console.println(_level, deny.getLinenumber() + ": " + deny.getName() + " " + deny.getValue());
			return false;
		}
	}

	private static boolean orderIsOK(Directive order)
	{
		if( ! order.getSurroundingContexts().get(0).getName().equalsIgnoreCase("Directory") ||
			! order.getSurroundingContexts().get(0).getValue().trim().equals("/")
		   )
		{
			return false;
		}
		
		if (order.getValue().matches("( |\t)*[Dd]eny,[Aa]llow( |\t)*"))
		{
			return true;
		}
		else
		{
			_console.printAnswer(_level, false, "Nobody should be able to access \"/\". Correct the following directive to \"Order Deny,Allow\".");
			_console.println(_level, order.getLinenumber() + ": " + order.getName() + " " + order.getValue());
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
}

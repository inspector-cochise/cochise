package org.akquinet.audit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InteractiveAskerTest
{
	@Before
	public final void setUp()
	{
		FormattedConsole.getDefault().setIgnore_WaitForUserToContinue(true);
	}
	
	@After
	public final void tearDown()
	{
		FormattedConsole.getDefault().setIgnore_WaitForUserToContinue(false);
	}
	
	@Test
	public final void testAskQuestions_noCritical()
	{
		List<YesNoQuestion> tmp = new ArrayList<YesNoQuestion>(1);		
		tmp.add(new TestQuestion(true));
		tmp.add(new TestQuestion(true));
		
		InteractiveAsker SUT = new InteractiveAsker(tmp);
		
		assertTrue(SUT.askQuestions());
		
		tmp.add(new TestQuestion(false));
		SUT = new InteractiveAsker(tmp);
		
		assertFalse(SUT.askQuestions());
	}

	@Test
	public final void testAskQuestions_Critical()
	{
		List<YesNoQuestion> tmp = new ArrayList<YesNoQuestion>(1);		
		tmp.add(new TestQuestion(true));
		tmp.add(new TestQuestion(false, true));
		tmp.add(new TestQuestion(true)
		{
			@Override
			public boolean answer()
			{
				fail("InteractiveAsker is evaluating question after failed critical question.");
				return _answer;
			}
		});
		
		InteractiveAsker SUT = new InteractiveAsker(tmp);
		
		assertFalse(SUT.askQuestions());
		
	}
	
	private class TestQuestion implements YesNoQuestion
	{
		public boolean _answer;
		public boolean _isCritical;
		public final String _ID = null;

		public TestQuestion(boolean answer)
		{
			this(answer, false);
		}
		
		
		public TestQuestion(boolean answer, boolean isCritical)
		{
			_answer = answer;
			_isCritical = isCritical;
		}
		
		@Override
		public boolean answer()
		{
			return _answer;
		}

		@Override
		public boolean isCritical()
		{
			return _isCritical;
		}

		@Override
		public String getID()
		{
			return _ID;
		}
		
	}

}

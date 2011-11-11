package org.akquinet.audit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.akquinet.audit.QuestionData.DataNotFoundException;
import org.akquinet.audit.ui.StdInRecorderNPlayer;
import org.akquinet.audit.ui.UserCommunicator;

public class Asker
{
	private Set<YesNoQuestion> _questions;
	private final UserCommunicator _uc = UserCommunicator.getDefault();
	private ResourceBundle _labels;
	private Properties _questionDataProps;
	private static final File _questionDataPropsFile = new File("qDat.properties");
	private StdInRecorderNPlayer _recorder;
	
	public Asker() throws IOException
	{
		_labels = ResourceBundle.getBundle("global", _uc.getLocale());
		_questionDataProps = new Properties();
		_recorder = new StdInRecorderNPlayer();
		System.setIn(_recorder);
		
		try
		{
			_questionDataProps.load( new FileInputStream(_questionDataPropsFile) );
		}
		catch (FileNotFoundException e)
		{
			_questionDataProps = new Properties();
			_questionDataPropsFile.createNewFile();
		}
	}
	
	public Asker(Collection<YesNoQuestion> questions) throws IOException
	{
		this();
		_questions = new HashSet<YesNoQuestion>(questions);
	}

	public Asker(YesNoQuestion[] questions) throws IOException
	{
		this();
		_questions = new HashSet<YesNoQuestion>();
		for (YesNoQuestion quest : questions)
		{
			_questions.add(quest);
		}
	}
	
	public void addQuestions(Collection<YesNoQuestion> questions) throws IOException
	{
		if(_questions == null)
		{
			_questions = new HashSet<YesNoQuestion>(questions);
		}
		else
		{
			_questions.addAll(questions);
		}
	}

	public void addQuestions(YesNoQuestion[] questions)
	{
		if(_questions == null)
		{
			_questions = new HashSet<YesNoQuestion>();
		}
		
		for (YesNoQuestion quest : questions)
		{
			_questions.add(quest);
		}
	}
	
	public void askPrologue(YesNoQuestion prologue)
	{
		QuestionData qDat;
		try
		{
			qDat = new QuestionData(prologue.getID(), _questionDataProps);
		}
		catch (DataNotFoundException e)
		{
			qDat = new QuestionData(prologue.getID(), "", false);
		}
		
		if(qDat._tape.length() > 0)
		{
			_recorder.play(qDat._tape);
		}
		else
		{
			_recorder.record();
		}
		
		prologue.answer();
		
		if(qDat._tape.length() > 0)
		{
			_recorder.stop();
		}
		else
		{
			_recorder.stop();
			qDat._tape = _recorder.save();
			qDat.saveToProperties(_questionDataProps);
		}
	}
	
	public boolean askQuestions()
	{
		List<YesNoQuestion> sorted = sortQuestions(_questions);
		boolean overallAns = true;
		
		for (YesNoQuestion quest : sorted)
		{
			boolean askAgain = true;
			boolean ans = false;
			
			_uc.markReport();
			while(askAgain)
			{
				_uc.resetReport();
				_uc.markReport();
				
				QuestionData qDat;
				try
				{
					qDat = new QuestionData(quest.getID(), _questionDataProps);
				}
				catch (DataNotFoundException e)
				{
					qDat = new QuestionData(quest.getID(), "", false);
				}
				
				if(qDat._answer && qDat._tape.length() > 0)
				{
					_recorder.play(qDat._tape);
				}
				else
				{
					_recorder.record();
				}
				
				ans = quest.answer();
				askAgain = !ans && _uc.askYesNoQuestion( _labels.getString("S11") , false);
				
				if(qDat._answer && qDat._tape.length() > 0)
				{
					_recorder.stop();
				}
				else
				{
					_recorder.stop();
					qDat._tape = _recorder.save();
					qDat._answer = ans;
					qDat.saveToProperties(_questionDataProps);
				}
				
				
				if(!askAgain)
				{
					_uc.waitForUserToContinue();
				}
			}
			overallAns &= ans;
			
			if(quest.isCritical() && !ans)
			{
				_uc.println( _labels.getString("S12") );
				_uc.waitForUserToContinue();
				break;
			}
		}
		
		try
		{
			_questionDataProps.store( new FileOutputStream(_questionDataPropsFile) , "");
		}
		catch (FileNotFoundException e)
		{
			_uc.reportError("Could not save question-data due to FileNotFoundException.");	//TODO move to properties (?)
			e.printStackTrace();
		}
		catch (IOException e)
		{
			_uc.reportError("Could not save question-data due to IOException.");	//TODO move to properties (?)
			e.printStackTrace();
		}
		
		return overallAns;
	}
	
	private List<YesNoQuestion> sortQuestions(Set<YesNoQuestion> questions)
	{
		List<YesNoQuestion> ret = new LinkedList<YesNoQuestion>();
		List<Set<YesNoQuestion>> blocks = new LinkedList<Set<YesNoQuestion>>();

		splitIntoBlocks(questions, blocks);
		
		sortBlocks(ret, blocks);
		
		return ret;
	}

	private void sortBlocks(List<YesNoQuestion> ret,
			List<Set<YesNoQuestion>> blocks)
	{
		for (Set<YesNoQuestion> block : blocks)
		{
			while(!block.isEmpty())
			{
				YesNoQuestion first = getNumberFirst(block);
				
				if(first.getRequirements().length > 0 && ! satisfied(first.getRequirements(), ret) )
				{
					List<YesNoQuestion> sList = satisfy(first, ret, block);
					
					block.removeAll(sList);
					ret.addAll(sList);
				}
				else
				{
					block.remove(first);
					ret.add(first);
				}
			}
		}
	}

	private void splitIntoBlocks(Set<YesNoQuestion> questions,
			List<Set<YesNoQuestion>> blocks)
	{
		List<YesNoQuestion> tmp = new LinkedList<YesNoQuestion>(questions);
		sortByBlockNumber(tmp);
		
		blocks.add(new HashSet<YesNoQuestion>());
		int blockNumber = tmp.get(0).getBlockNumber();
		while(!tmp.isEmpty())
		{
			YesNoQuestion first = tmp.get(0);
			int b = first.getBlockNumber();
			if(b != blockNumber)
			{
				blockNumber = b;
				blocks.add(new HashSet<YesNoQuestion>());
			}

			blocks.get(blocks.size()-1).add( first );
			tmp.remove(0);
		}
	}

	private void sortByBlockNumber(List<YesNoQuestion> tmp)
	{
		java.util.Collections.sort(tmp, new Comparator<YesNoQuestion>()
		{
			public int compare(YesNoQuestion q1, YesNoQuestion q2)
			{
				if(q1.getBlockNumber() < q2.getBlockNumber())
				{
					return -1;
				}
				else if(q1.getBlockNumber() > q2.getBlockNumber())
				{
					return 1;
				}
				else //if(q1.getBlockNumber() == q2.getBlockNumber())
				{
					return 0;
				}
			}
		});
	}

	private List<YesNoQuestion> satisfy(YesNoQuestion first,
										List<YesNoQuestion> alreadyHandled,
										Set<YesNoQuestion> block)
	{
		List<YesNoQuestion> alreadyHandledHere = new LinkedList<YesNoQuestion>();
		alreadyHandledHere.addAll(alreadyHandled);
		
		List<YesNoQuestion> ret = new LinkedList<YesNoQuestion>();

		List<YesNoQuestion> requirements = generateRequirementsList(first.getRequirements(), alreadyHandled, block);
		
		for (YesNoQuestion quest : requirements)
		{
			if(!alreadyHandledHere.contains(quest))
			{
				List<YesNoQuestion> s = satisfy(quest, alreadyHandledHere, block);
				alreadyHandledHere.addAll(s);
				ret.addAll(s);
			}
		}
		
		ret.add(first);
		return ret;
	}

	private List<YesNoQuestion> generateRequirementsList(String[] requirements,
														 List<YesNoQuestion> alreadyHandled,
														 Set<YesNoQuestion> block)
	{
		Arrays.sort(requirements);
		
		Set<YesNoQuestion> tmp = new HashSet<YesNoQuestion>(alreadyHandled);
		tmp.addAll(block);
		
		List<YesNoQuestion> ret = new LinkedList<YesNoQuestion>();
		
		for (String questName : requirements)
		{
			for (YesNoQuestion quest : tmp)
			{
				if(quest.getID().equals(questName))
				{
					ret.add(quest);
					break;
				}
			}
		}
		return ret;
	}

	private boolean satisfied(String[] requirements, List<YesNoQuestion> alreadyHandled)
	{
		boolean ret = true;
		
		for (String req : requirements)
		{
			boolean satisfied = false;
			for (YesNoQuestion quest : alreadyHandled)
			{
				satisfied |= quest.getID().equals(req);
				
				if(satisfied == false)	//speed improvement
				{
					return false;
				}
			}
			ret &= satisfied;
		}
		
		return ret;
	}

	private YesNoQuestion getNumberFirst(Set<YesNoQuestion> block)
	{
		YesNoQuestion ret = java.util.Collections.min(block, new Comparator<YesNoQuestion>()
		{
			public int compare(YesNoQuestion q1, YesNoQuestion q2)
			{
				if(q1.getNumber() < q2.getNumber())
				{
					return -1;
				}
				else if(q1.getNumber() > q2.getNumber())
				{
					return 1;
				}
				else //if(q1.getNumber() == q2.getNumber())
				{
					return 0;
				}
			}
		});
		
		return ret;
	}
}

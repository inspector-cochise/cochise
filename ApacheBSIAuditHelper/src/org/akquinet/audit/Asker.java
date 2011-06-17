package org.akquinet.audit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Asker
{
	private Set<YesNoQuestion> _questions;
	
	public boolean askQuestions()
	{
		//first draft:
		List<YesNoQuestion> sorted = sortQuestions(_questions);
		boolean overallAns = true;
		
		for (YesNoQuestion quest : sorted)
		{
			boolean ans = quest.answer();
			overallAns &= ans;
			
			if(quest.isCritical() && !ans)
			{
				//TODO show some message
				break;
			}
		}
		
		return overallAns;
	}
	
	private List<YesNoQuestion> sortQuestions(Set<YesNoQuestion> questions)
	{
		//first draft:
		List<YesNoQuestion> ret = new LinkedList<YesNoQuestion>();
		List<Set<YesNoQuestion>> blocks = new LinkedList<Set<YesNoQuestion>>();

		//TODO split into blocks
		
		
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
		
		return ret;
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
		
		return null;
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
		YesNoQuestion ret = block.iterator().next();
		for (YesNoQuestion quest : block)
		{
			if(ret.getNumber() > quest.getNumber())
			{
				ret = quest;
			}
		}
		return ret;
	}
}

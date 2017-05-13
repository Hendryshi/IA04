package Sudoku;

import java.util.ArrayList;
import java.util.List;


public class Cell
{
	List<Integer> possibleValues = new ArrayList<Integer>();
	int value;
	public Cell(int value)
	{
		this.value = value;
		if(value == 0)
		{
			for(int i=1; i<=9; i++)
			{
				possibleValues.add(i);
			}
		}
		
	}
	
	public Cell(int value,  List<Integer> possible)
	{
		possibleValues.addAll(possible);
		this.value = value;
	}
	
	public Cell()
	{
		
	}
	
	public int getValue()
	{
		return value;
	}
	
	public List<Integer> getPossibleValues()
	{
		return possibleValues;
	}
	
	public void setPossibleValues(List<Integer> list)
	{
		this.possibleValues.addAll(list);
	}
	
	public void updatePossibleValues(List<Integer> newPossibleValues)
	{
		List<Integer> result = new ArrayList<Integer>();
		int number = newPossibleValues.size();
		for (int i=0; i<number; i++)
		{
			if (possibleValues.contains(newPossibleValues.get(i)))
			{
				result.add(newPossibleValues.get(i));
			}
		}
		possibleValues = result;
	}
	
	public void setValue(int newValue)
	{
		value = newValue;
		if(!possibleValues.isEmpty())
		{
	//		parent.cellCompleted();
	//		possibleValues.removeAll(possibleValues);
		}
	}
	
	public void updateValue(int newValue)
	{
		if(newValue!=0)
		{
			value = newValue;
			if(!possibleValues.isEmpty())
			{
		//		parent.cellCompleted();
				possibleValues.removeAll(possibleValues);
			}
		}
	}
	
	public boolean hasValue()
	{
		return value != 0;
	}
}
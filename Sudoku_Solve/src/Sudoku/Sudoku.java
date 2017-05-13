package Sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import javafx.collections.ObservableList;

public class Sudoku {
	Integer remainingCells;
	Cell[][] grille; 
	
	public Sudoku(List<Integer> vals) 
	{
		remainingCells = Constants.DIMENSION * Constants.DIMENSION;
		grille = new Cell[Constants.DIMENSION][Constants.DIMENSION];
		for(int i=0; i<Constants.DIMENSION; i++)
		{
			for(int j=0; j<Constants.DIMENSION; j++)
			{
				System.out.println(vals.get(i*Constants.DIMENSION +j));
				grille[i][j] = new Cell(vals.get(i*Constants.DIMENSION +j));
			}
		}
	}
	
	
	public boolean isFinished()
	{
		int count = 0;
		for(int i=0; i<Constants.DIMENSION; i++)
		{
			for(int j=0; j<Constants.DIMENSION; j++)
			{
				if(grille[i][j].getValue() != 0)
				{
					count++;
				}
			}
		}
		return count == Constants.DIMENSION * Constants.DIMENSION;
	}
	
	public Cell[] getRow(int i)
	{
		Cell[] result = new Cell[9];
		for (int j=0; j<Constants.DIMENSION; j++)
		{
			result[j] = grille[i][j];
		}
		return result;
	}
	
	public Cell[] getColumn(int i)
	{
		Cell[] result = new Cell[9];
		for (int j=0; j<Constants.DIMENSION; j++)
		{
			result[j] = grille[j][i];
		}
		return result;
	}
	
	public Cell[] getSquare(int rowGroup,  int columnGroup)
	{
		Cell[] result = new Cell[9];
		for (int i = 0; i<3; i++)
		{
			for (int j = 0; j<3; j++)
			{
				result[3*i+j] = grille[3*rowGroup+i][3*columnGroup+j];
			}
		}
		return result;
	}
	
	public void setColumn(int number,  List<Cell> newColumn)
	{
		for (int j=0; j<Constants.DIMENSION; j++)
		{
			grille[j][number].updatePossibleValues(newColumn.get(j).getPossibleValues());
			grille[j][number].updateValue(newColumn.get(j).getValue());
		}
	}
	
	public void setRow(int number,  List<Cell> newRow)
	{
		
		for (int j=0; j<Constants.DIMENSION; j++)
		{
			grille[number][j].updatePossibleValues(newRow.get(j).getPossibleValues());
			grille[number][j].updateValue(newRow.get(j).getValue());
		}
	}
	
	public void setSquare(int rowGroup,  int columnGroup,  List<Cell> newSquare)
	{
		for (int i = 0; i<3; i++)
		{
			for (int j = 0; j<3; j++)
			{
				grille[3*rowGroup+i][3*columnGroup+j].updatePossibleValues(newSquare.get(3*i+j).getPossibleValues());
				grille[3*rowGroup+i][3*columnGroup+j].updateValue(newSquare.get(3*i+j).getValue());
			}
		}
	}
	
	public void printSudoku()
	{
		for(int i=0; i<Constants.DIMENSION; i++)
		{
			for(int j=0; j<Constants.DIMENSION; j++)
			{
				System.out.print(grille[i][j].getValue() +""+ grille[i][j].getPossibleValues() + " ");
			}
			System.out.print("\n");
		}
	}


	public String printResultat() {
		String v = "";
		for(int i=0; i<9;i++)
		{
			for(int j=0; j<9; j++)
			{
				if(v.isEmpty())
				{
					v = String.valueOf(grille[i][j].getValue());
				}else{
					v = v + "," + String.valueOf(grille[i][j].getValue());
				}		
			}
		}
		return v;
	}
}

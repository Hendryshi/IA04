package FxApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Jade.Agent.EnvironmentAgent;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SudokuController {
	
	SudokuView root;
	EnvironmentAgent agent;
	
	public SudokuController(SudokuView root)
	{
		this.root = root;
		root.setController(this);

	}
	
	public void setAgent(EnvironmentAgent agent)
	{
		this.agent = agent;
		agent.resultatProperty().addListener((obsv, oldv, newv) -> DisplayResultat(newv));
		System.out.println("Connecting -- Controller ---> Environnement");
	}
	
	public void sendToEnvi(VBox v)
	{
		List<Integer> vals = new ArrayList<Integer>();
		
		for(int i=0; i<9; i++)
		{
			HBox h = (HBox)v.getChildren().get(i);
			for (int j=0; j<9; j++)
			{
				TextField t = (TextField)h.getChildren().get(j);
				if(t.getText().isEmpty())
				{
					vals.add(0);
				}else{
					vals.add(Integer.parseInt(t.getText()));
				}
			}
		}
		agent.setSudoku(vals);
	}
	
	public void DisplayResultat(String v)
	{
		root.setResultat(v);	
	}
}

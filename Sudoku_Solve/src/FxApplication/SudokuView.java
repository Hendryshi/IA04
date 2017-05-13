package FxApplication;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SudokuView extends GridPane{
	
	SudokuController controller;
	VBox vbox;
	
	public void setController(SudokuController controller)
	{
		this.controller = controller;
	}
	
	public SudokuView()
	{
		vbox = new VBox();
		for (int col = 0; col < 9; col++) {
			HBox cell = new HBox();
            for (int row = 0; row < 9; row++) {
            	
                cell.getChildren().add(createTextField());
            }
            vbox.getChildren().add(cell);  
        }
		add(vbox,0,0);
		
		HBox hbox = new HBox();
		Button Solve = new Button("Solve");
		Button Reset = new Button ("Reset");
		hbox.getChildren().add(Solve);
		hbox.getChildren().add(Reset);
		hbox.setAlignment(Pos.CENTER);
		hbox.setSpacing(12.0);
		
		add(hbox,0,11);
		
		Solve.setOnAction(evt -> controller.sendToEnvi(getVbox()));
		Reset.setOnAction(evt -> reset());
	}
	
	public VBox getVbox()
	{
		return vbox;
	}

	public void setResultat(String v)
	{
		String[] list = v.split(",");

		for(int i=0; i<9; i++)
		{
			HBox h = (HBox)vbox.getChildren().get(i);
			for(int j=0; j<9; j++)
			{
				TextField t = (TextField)h.getChildren().get(j);
				t.setText(list[i*9 + j]);
			}
		}
	}
	public void reset()
	{
		for(int i=0; i<9; i++)
		{
			HBox h = (HBox)vbox.getChildren().get(i);
			for(int j=0; j<9; j++)
			{
				TextField t = (TextField)h.getChildren().get(j);
				t.setText("");
			}
		}
	}
	
	private TextField createTextField() {
		TextField textField = new TextField();
		textField.setAlignment(Pos.CENTER);
        // restrict input to integers:
        textField.setTextFormatter(new TextFormatter<Integer>(c -> {
            if (c.getControlNewText().matches("\\d?")) {
                return c ;
            } else {
                return null ;
            }
        }));
        return textField ;
    }
	
}

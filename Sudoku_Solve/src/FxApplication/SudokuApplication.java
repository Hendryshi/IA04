package FxApplication;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SudokuApplication extends Application{
	
	public ContainerController containerController;
	public static String SECONDARY_PROPERTIES_FILE = "properties/sec_prop";
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Sudoku Solve");
		SudokuView root = new SudokuView();
		SudokuController controller = new SudokuController(root);
		createAgentContainer(controller);
		Scene scene = new Scene(root,400,300);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public void createAgentContainer(SudokuController controller) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl(SECONDARY_PROPERTIES_FILE);
			ContainerController cc = rt.createAgentContainer(p);
			
			 AgentController ac = cc.createNewAgent("EnvironmentAgent",
					"Jade.Agent.EnvironmentAgent", new Object[]{controller});
			ac.start();
			
			containerController = cc;
				
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}

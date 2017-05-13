package Jade.system;

import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;


public class MainBoot {

public static String MAIN_PROPERTIES_FILE = "properties/main_prop";
	
	
	public static void main(String[] args) {

		Runtime rt = Runtime.instance();
		Profile p = null;
		ContainerController mc = null;
		try {
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			mc = rt.createMainContainer(p);
		} catch (ProfileException e) {
			e.printStackTrace();
		}		
	}
}

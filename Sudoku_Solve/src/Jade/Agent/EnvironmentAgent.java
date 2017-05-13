package Jade.Agent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.corba.se.impl.orbutil.closure.Constant;
//import com.sun.medialib.mlib.Constants;

import FxApplication.SudokuController;
import Sudoku.Cell;
import Sudoku.Sudoku;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EnvironmentAgent extends Agent {
	Sudoku sudoku;
	SudokuController controller;

	StringProperty resultat;
	
	public EnvironmentAgent()
	{
		super();
		resultat = new SimpleStringProperty();
	}
	
	public StringProperty resultatProperty()
	{
		return resultat;
	}
	
	protected void setup()
	{
		super.setup();
		EnregistrerAgent("su", "envi");
		controller = (SudokuController) getArguments()[0];
		controller.setAgent(this);
		System.out.println(getLocalName() + " agent ---> installed");
	}
	
	public void setSudoku(List<Integer> vals)
	{
		sudoku = new Sudoku(vals);
		ContainerController cc = this.getContainerController();
		AgentController ac;
		try {
			ac = cc.createNewAgent("Simulation", "Jade.Agent.SimulationAgent", null);
			ac.start();
			for(int i=1; i<=27; i++)
			{
				ac = cc.createNewAgent("Analyse"+i, "Jade.Agent.AnalyseAgent", null);
				ac.start();
			}
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		addBehaviour(new UpdateSudoku());
	}
	
	class UpdateSudoku extends CyclicBehaviour
	{
		int step = 0;
		int count = 0;
		@Override
		public void action() {
			switch (step){
			
			case 0 :
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage message = receive(mt);
				if (message != null)
				{
					if(message.getContent().equals("false"))
					{
						step++;
					}
				}
				else
				{				
					block();
				}
				break;
			case 1 :
				//Envoyer les cellules aux analystes
				ObjectMapper mapper = new ObjectMapper();
				String s = null;
				AID[] receiver = SearchReceiver("su", "analyse");
				for(int i=0; i<9; i++)
				{
					try {
						s = mapper.writeValueAsString(sudoku.getColumn(i));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(String.valueOf(i));
					message.setContent(s);
					message.addReceiver(receiver[i]);
					send(message);
				}		
				for(int i=0; i<9; i++)
				{
					try {
						s = mapper.writeValueAsString(sudoku.getRow(i));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(String.valueOf(i+9));
					message.setContent(s);
					message.addReceiver(receiver[i+9]);
					send(message);
				}	
				for(int i=0; i<=2; i++)
				{
					for(int j=0; j<=2; j++)
					{
						try {
							s = mapper.writeValueAsString(sudoku.getSquare(i,  j));
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						message = new ACLMessage(ACLMessage.REQUEST);
						message.setConversationId(String.valueOf(3*i+j+18));
						message.setContent(s);
						message.addReceiver(receiver[3*i+j+18]);
						send(message);
					}
				}
				System.out.println("send over : " + step);
				step++;
				break;
			case 2 :
				//Attendre que tous les analystes aient répondu et modifier le sudoku
				mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage answer = receive(mt);
				if (answer != null)
				{
					int conversation_id = Integer.parseInt(answer.getConversationId().trim());
					String sjson = answer.getContent();
					mapper = new ObjectMapper();
					try
					{
						List<Cell> cells = mapper.readValue(sjson,  TypeFactory.defaultInstance().constructCollectionType(List.class,   
								   Cell.class));
						
						if(conversation_id < 9)
						{ 
							sudoku.setColumn(conversation_id, cells);
							count++;
						}
						
						else if(conversation_id >=9 && conversation_id < 18)
						{
							sudoku.setRow(conversation_id-9, cells);							
							count++;
						}
						else if(conversation_id >= 18 && conversation_id < 27)
						{
							for(int i=0; i<=2; i++)
							{
								for(int j=0; j<=2; j++)
								{
									if(conversation_id == 3*i+j+18)
									{
										sudoku.setSquare(i, j, cells);
										count++;
										break;
									}
								}
							}
						}
					}
					catch(Exception ex) {
					
						ex.printStackTrace();
					}
				}
				else
				{
					block();
				}
				if(count == 27)
				{
					sudoku.printSudoku();
					addBehaviour(new CheckEndOfSimulation());
					count = 0;
					step = 0;
				}	
				break;
			}
		}	
	}
	
	class CheckEndOfSimulation extends OneShotBehaviour
	{
		@Override
		public void action() {
			if (sudoku.isFinished())
			{
				resultat.setValue(sudoku.printResultat());
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.setContent("Sudoku is resolved");
				AID[] receiver = SearchReceiver("su",  "simu");
				message.addReceiver(receiver[0]);
				send(message);
			}
		}
	}
	
	private void EnregistrerAgent(String type, String name)
	{
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dfad.addServices(sd);
		try {
			DFService.register(this,  dfad);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	private AID[] SearchReceiver(String type, String name) {
    	AID rec[] = null;
    	DFAgentDescription template = new DFAgentDescription();
    	ServiceDescription sd = new ServiceDescription();
    	sd.setType(type);
    	sd.setName(name);
    	template.addServices(sd);
    	try {
	    	DFAgentDescription[] result = DFService.search(this, template);
	    	if (result.length > 0){
	    		rec = new AID[result.length];
				for(int i = 0; i < result.length; i++){
					rec[i] = result[i].getName();
				}
	    	}
    	} 
    	catch(FIPAException fe) {
    	}
    	return rec;
    }
}

package Jade.Agent;

import java.util.ArrayList;
import java.util.List;

import Sudoku.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SimulationAgent extends Agent {
	Integer numberOfAnalysts = 0;
	boolean resolved = false;
	protected void setup()
	{
		EnregistrerAgent("su",  "simu");
		addBehaviour(new SimulationBehaviour());
	}
	
	class SimulationBehaviour extends SequentialBehaviour
	{	
		SimulationBehaviour()
		{
			addSubBehaviour(new WaitForSubscriptions());
			addSubBehaviour(new InitialiseSimulation());
			addSubBehaviour(new WaitForResolution());
		}
	}
	
	class WaitForSubscriptions extends Behaviour
	{
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = receive(mt);

			if(message != null) 
			{
				++numberOfAnalysts;
			}
			else 
			{
				block();
			}
		}

		@Override
		public boolean done() {
			return numberOfAnalysts == Constants.AGENTS;
		}
		
	}
	
	class InitialiseSimulation extends OneShotBehaviour
	{

		@Override
		public void action() {
			System.out.println("subscription over");
			this.myAgent.addBehaviour(new Ticker500ms(this.myAgent));
		}

	}
	
	class WaitForResolution extends Behaviour
	{
		
		@Override
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if (message != null)
			{
				System.out.println("Sudoku resolved!");
				resolved = true;
			}
			else
			{
				block();
			}
		}
		@Override
		public boolean done() {
			return resolved;
		}
		
	}
	
	class Ticker500ms extends TickerBehaviour
	{

		public Ticker500ms(Agent a) {
			super(a,  Constants.TIME_BETWEEN_TICKS);
		}

		@Override
		protected void onTick() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setContent(String.valueOf(resolved));
			AID[] receiver = SearchReceiver("su",  "envi");
			message.addReceiver(receiver[0]);
			send(message);
		}
		
	}
	
	
	private void EnregistrerAgent(String type,  String name)
	{
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dfad.addServices(sd);
		try {
			DFService.register(this,  dfad);
		//	System.out.println("agent " + name + " est enregistrer");
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	private AID[] SearchReceiver(String type,  String name) {
    	AID rec[] = null;
    	DFAgentDescription template = new DFAgentDescription();
    	ServiceDescription sd = new ServiceDescription();
    	sd.setType(type);
    	sd.setName(name);
    	template.addServices(sd);
    	try {
	    	DFAgentDescription[] result = DFService.search(this,  template);
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

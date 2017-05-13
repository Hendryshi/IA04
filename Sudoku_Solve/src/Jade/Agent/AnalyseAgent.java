package Jade.Agent;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import Sudoku.Cell;
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

public class AnalyseAgent extends Agent {
	boolean finish = false;
	
	protected void setup()
	{
		EnregistrerAgent("su", "analyse");
		addBehaviour(new Subscribe());
		addBehaviour(new AnalyseSu());
	}
	
	class Subscribe extends OneShotBehaviour
	{

		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
			AID[] receiver = SearchReceiver("su", "simu");
			message.addReceiver(receiver[0]);
			send(message);
		}
		
	}
	
	class AnalyseSu extends Behaviour
	{
		
		@Override
		public void action() {
			boolean flag=false;
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null)
			{
				String sjson = message.getContent();
				ObjectMapper mapper = new ObjectMapper();
				try{
				
					List<Cell> cells = mapper.readValue(sjson, TypeFactory.defaultInstance().constructCollectionType(List.class,   
							   Cell.class));
					
					cells = algorithm1(cells);
					cells = algorithm2(cells);
					cells = algorithm3(cells);
					cells = algorithm4(cells);
					ACLMessage reply = message.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					mapper = new ObjectMapper();
					String s = null;
					try {
						s = mapper.writeValueAsString(cells);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					reply.setContent(s);
					send(reply);
				}
				catch(Exception ex){
				
					ex.printStackTrace();
				}
			}
			
			else
			{
				block();
			}
			
		}

		@Override
		public boolean done() {
			return finish;
		}
		
		
	}
	
	private List<Cell> algorithm1(List<Cell> cells)
	{
		for(int i=0; i<cells.size(); i++)
		{
			if(!cells.get(i).hasValue())
			{
				List<Integer> possible = cells.get(i).getPossibleValues();
				if(possible.size()==1)
				{
					cells.get(i).updateValue(possible.get(0));
				}
			}
		}
		return cells;
	}
	
	private List<Cell> algorithm2(List<Cell> cells)
	{
		for(int i=0; i<cells.size(); i++)
		{
			if(cells.get(i).hasValue())
			{
				Integer value = cells.get(i).getValue();
				for(int j=0; j<cells.size(); j++)
				{
					List<Integer> possible = cells.get(j).getPossibleValues();
					possible.remove(value);
					cells.get(j).updatePossibleValues(possible);
				}
			}
		}
		return cells;
	}
	
	private List<Cell> algorithm3(List<Cell> cells)
	{
		int count = 0;
		for(int i=0; i<cells.size(); i++)
		{
			if(!cells.get(i).hasValue())
			{
				List<Integer> possible = cells.get(i).getPossibleValues();
				for(int j=0; j<possible.size(); j++)
				{
					int value = possible.get(j);
					count = 0;
					for(int k=0; k<cells.size(); k++)
					{
						if(k != i)
						{
							if(!cells.get(k).hasValue() && cells.get(k).getPossibleValues().contains(value))
							{
								break;
							}
						}
						count++;
					}
					if(count == cells.size())
					{
						cells.get(i).updateValue(value);
						break;
					}
				}
			}
		}
		return cells;
	}
	
	private List<Cell> algorithm4(List<Cell> cells)
	{
		for(int i=0; i<cells.size(); i++)
		{
			List<Integer> possible = cells.get(i).getPossibleValues();
			if(possible.size()==2)
			{
				for(int j=i+1; j<cells.size(); j++)
				{
					List<Integer> possible2 = cells.get(j).getPossibleValues();
					if(possible2.size()==2 && possible.containsAll(possible2))
					{
						for(int k=0; k<cells.size(); k++)
						{
							if(k!=i && k!=j)
							{
								List<Integer> possible3 = cells.get(k).getPossibleValues();
								possible3.removeAll(possible2);
								cells.get(k).updatePossibleValues(possible3);
							}
						}
						break;
					}
				}
			}
		}
		return cells;
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


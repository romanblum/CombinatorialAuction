import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.time.*;

import brown.agent.AbsCombinatorialProjectAgentV2;
import brown.exceptions.AgentCreationException;


public class CombAuctionAgent extends AbsCombinatorialProjectAgentV2 {

	public CombAuctionAgent(String host, int port)
			throws AgentCreationException {
		super(host, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onAuctionEnd(Set<Integer> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAuctionStart() {
		/*long tStart = System.currentTimeMillis();		
		
		while ((System.currentTimeMillis() - tStart)/1000 < 19) {
			
		}
		*/

		/*
		Map<Integer, Set<Integer>> modulo14 = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> every14 = new HashMap<Integer, Set<Integer>>();
		for (int i = 0; i < 14; i++) {
			modulo14.put(i, new HashSet<Integer>());
		}
		
		for (int i = 0; i < 98; i++) {
			Set<Integer> s = modulo14.get(i%14);
			s.add(i);
			modulo14.put(i%14,s);	
		}
		
		for (int i = 0; i < 14; i++) {
			System.out.println(this.queryValue(modulo14.get(i)));
		}
		System.out.println("RANDOM SAMPLES");
		Map<Set<Integer>, Double> m = this.queryXORs(100, 7, 0);
		Double sum = 0.0;
		Double max = 0.0;
		for (Double d : m.values()) {
			sum += d;
			max = (max < d) ? d : max;
		}
		System.out.println(sum / 100.0);
		System.out.println(max);
		*/
		
		Map<Integer, Set<Integer>> every14 = new HashMap<Integer, Set<Integer>>();
		for (int i = 0; i < 7; i++) {
			every14.put(i, new HashSet<Integer>());
		}
		
		for (int i = 0; i < 7; i++) {
			Set<Integer> s = new HashSet<Integer>();
			for (int j = 0; j < 14; j++) {
				s.add(14*i + j); 
			}
			every14.put(i,s);
		}
		
		Set<Integer> s = every14.get(2);
		System.out.println(this.queryValue(s));
		s.addAll(every14.get(3));
		System.out.println(this.queryValue(s));
		
		for (int i = 0; i < 7; i++) {
			System.out.println(this.queryValue(every14.get(i)));
		}
		System.out.println("RANDOM SAMPLES");
		Map<Set<Integer>, Double> m = this.queryXORs(100, 14, 0);
		Double sum = 0.0;
		Double max = 0.0;
		for (Double d : m.values()) {
			sum += d;
			max = (max < d) ? d : max;
		}
		System.out.println(sum / 100.0);
		System.out.println(max);
		
	}

	@Override
	public void onBidResults(double[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Integer> onBidRound() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		try {
			new CombAuctionAgent("localhost", 2121);
			while(true) {
			}
		} catch (AgentCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

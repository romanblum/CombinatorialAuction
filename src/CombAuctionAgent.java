import java.util.Set;

import brown.agent.AbsCombinatorialProjectAgentV2;
import brown.exceptions.AgentCreationException;


public class CombAuctionAgent extends AbsCombinatorialProjectAgentV2{

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
		// TODO Auto-generated method stub
		
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

}

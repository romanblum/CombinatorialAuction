import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.time.*;

import brown.agent.AbsCombinatorialProjectAgentV2;
import brown.exceptions.AgentCreationException;


public class CombAuctionAgent extends AbsCombinatorialProjectAgentV2 {
	
	ArrayList<Region> regions;
	Set<Set<Integer>> skeletons;
	
	Bundle alloc;
	SortedSet<DemandedItem> shared;
	double prices[] = new double[98];
	
	public CombAuctionAgent(String host, int port)
			throws AgentCreationException {
		super(host, port);
		regions = new ArrayList<Region>(14);
		alloc = new Bundle(new HashSet<Integer>(), 0.0, 0.0);
		shared = new TreeSet<DemandedItem>();
		
	}

	@Override
	public void onAuctionEnd(Set<Integer> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAuctionStart() {
		//Initialize regions
		
		long tStart = System.currentTimeMillis();	
		
		for (int i = 0; i < 14; i++) {
			ArrayList<Integer> region_items = new ArrayList<Integer>(7);
			ArrayList<Double> region_vals = new ArrayList<Double>(7);
			for (int j = 0; j < 7; j++) { //for each item in region
				Set<Integer> singleton = new HashSet<Integer>(); //
				singleton.add(i + 14*j);
				region_items.add(i + 14*j);
				region_vals.add(this.queryValue(singleton)); 
			}
			regions.add(new Region(region_items, region_vals, this.queryValue(new HashSet<Integer>(region_items))));
		}
		
		Set<Bundle> bestBundles = new HashSet<Bundle>();
		
		PriorityQueue<Bundle> skeletonQueue = new PriorityQueue<Bundle>(1000, Collections.reverseOrder());
		
		for (int i = 0; i < 7; i++) {
			Set<Integer> initialSet = new HashSet<Integer>();
			for (int j = 0; j < 14; j++) {
				initialSet.add(14*i + j);
			}
			double value = this.queryValue(initialSet);
//			if(i==0||i==3||i==6){
//				Bundle initial = new Bundle(initialSet, value, 0);
//				bestBundles.add(initial);
//			}
		}
		
		
		while ((System.currentTimeMillis() - tStart) < 18700) {
			// generate one random skeleton 
			Set<Integer> set = new HashSet<Integer>();
			for (int j = 0; j < 14; j++) {
				set.add(regions.get(j).getRandomItem());
			}
			
			double setValue = this.queryValue(set);
			
			//sample 100 times
			
			double sumSampleValues = 0;
			
			for (int i = 0; i< 3; i++){
				sumSampleValues += this.sampleValue(set);
			}
			
			double meanSampleValue = sumSampleValues/3;
			double difference = setValue - meanSampleValue;
			Bundle current = new Bundle(set, setValue, meanSampleValue);
			skeletonQueue.add(current);
		}
		System.out.println(skeletonQueue.size());
		int counter = 0;
		
		while ((System.currentTimeMillis() - tStart) < 19000 && counter<10) {
				Bundle currentBest = skeletonQueue.poll();
				Bundle altered = new Bundle(currentBest.getBundle(), currentBest.getValue(), 0);
				System.out.println(currentBest.getValue() + " : " + (currentBest.getValue()-currentBest.getPrice()));
//				System.out.println(altered.getValue());
				bestBundles.add(altered);
				counter++;
		}
		Set<Integer> itemSet = new HashSet<Integer>();
		for(Bundle bundle: bestBundles){
			itemSet.addAll(bundle.getBundle());
		}
		System.out.println(itemSet.toString());
		System.out.println(itemSet.size());

//		System.out.println("FIXED CHUNKS");
//		fixed_sets.forEach((key, value) -> {
//		    System.out.println("Bundle : " + key.toString() + " Value : " + value + " Sample Value : " + this.sampleValue(key) + " Sample Value : " + this.sampleValue(key));
//		});
//		System.out.println("RANDOM 14");
//		random_skeletons.forEach((key, value) -> {
//		    System.out.println("Bundle : " + key.toString() + " Value : " + value + " Sample Value : " + this.sampleValue(key) + " Sample Value : " + this.sampleValue(key));
//		});
	}

	@Override
	public void onBidResults(double[] demand) {
		prices = this.getPrices();
		Set<Integer> allocated_goods = new HashSet<Integer>();
		shared.clear();
		for (int i = 0; i < 98; i++) {
			if (demand[i] == 1.0) {
				allocated_goods.add(i);
			}
			
			if (demand[i] > 0.0) {
				shared.add(new DemandedItem(i, demand[i]));
			}
		}
		alloc = new Bundle(allocated_goods, this.queryValue(allocated_goods), this.getBundlePrice(allocated_goods));
	}

	@Override
	public Set<Integer> onBidRound() {
		long startT = System.currentTimeMillis();
		// things that might be worth doing:
			// we can acquire goods with less demand (that we previous bid on by)
			// Set<Integer> goods = shared.tailSet(new DemandedItem(-1, demand));
		// initialize bids we want to consider - this should be fast
		SortedSet<Bundle> possibleBids = new TreeSet<Bundle>();
		// iterate on other skeletons and add them to possible bids
		while (System.currentTimeMillis() - startT < 1750) {
			// do iteration
		}
		
		return (possibleBids.last().getValue()-possibleBids.last().getPrice()) < 0 ? new HashSet<Integer>() : possibleBids.last().getBundle();
	}
	
	private int getRegionID(Integer item) {
		return item % 14;
	}
	
	public static void main(String[] args) {
		try {
			new CombAuctionAgent("localhost", 2424);
			while(true) {
			}
		} catch (AgentCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}

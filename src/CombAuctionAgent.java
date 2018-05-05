import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
		/*long tStart = System.currentTimeMillis();		
		
		// fixed
		
		while ((System.currentTimeMillis() - tStart)/1000 < 15) {
			// generate one random skeleton
			// sample 100 times
			// compute 
		}
		
		while ((System.currentTimeMillis() - tStart)/1000 < 19) {
			sorting in here
			
			or just keep a priority queue or something in the above
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
		
		// Initialize regions
		for (int i = 0; i < 14; i++) {
			ArrayList<Integer> region_items = new ArrayList<Integer>(7);
			ArrayList<Double> region_vals = new ArrayList<Double>(7);
			for (int j = 0; j < 7; j++) {
				Set<Integer> singleton = new HashSet<Integer>();
				singleton.add(i + 14*j);
				region_items.add(i + 14*j);
				region_vals.add(this.queryValue(singleton)); 
			}
			regions.add(new Region(region_items, region_vals, this.queryValue(new HashSet<Integer>(region_items))));
		}
		
		// Fixed chunks
		Map<Set<Integer>, Double> fixed_sets = new HashMap<Set<Integer>, Double>();
		/*
		Set<Integer> one = new HashSet<Integer>();
		Set<Integer> one_two = new HashSet<Integer>();
		Set<Integer> two_three = new HashSet<Integer>();
		Set<Integer> three = new HashSet<Integer>();
		Set<Integer> two_four = new HashSet<Integer>();
		*/
		for (int i = 0; i < 7; i++) {
			Set<Integer> s = new HashSet<Integer>();
			for (int j = 0; j < 14; j++) {
				s.add(14*i + j);
			}
			fixed_sets.put(s, this.queryValue(s));
			/*
			if (i == 0) {
				one.addAll(s);
				one_two.addAll(s);
			}
			
			if (i == 1) {
				one_two.addAll(s);
				two_three.addAll(s);
				two_four.addAll(s);
			}
			
			if (i == 2) {
				three.addAll(s);
				two_three.addAll(s);
			}
			
			if (i == 3) {
				two_four.addAll(s);
			}
			*/
		}
		/*
		System.out.println("Bundle : " + one.toString() + " Value : " + this.queryValue(one));
		System.out.println("Bundle : " + one_two.toString() + " Value : " + this.queryValue(one_two));
		System.out.println("Bundle : " + three.toString() + " Value : " + this.queryValue(three));
		System.out.println("Bundle : " + two_three.toString() + " Value : " + this.queryValue(two_three));
		System.out.println("Bundle : " + two_four.toString() + " Value : " + this.queryValue(two_four));
		*/
		
		// Initialize random skeletons
		Map<Set<Integer>, Double> random_skeletons = new HashMap<Set<Integer>, Double>();
		for (int i = 0; i < 100; i++) {
			Set<Integer> s = new HashSet<Integer>();
			for (int j = 0; j < 14; j++) {
				s.add(regions.get(j).getRandomItem());
			}
			random_skeletons.put(s, this.queryValue(s));
		}
		
		
		
		
		System.out.println("FIXED CHUNKS");
		fixed_sets.forEach((key, value) -> {
		    System.out.println("Bundle : " + key.toString() + " Value : " + value + " Sample Value : " + this.sampleValue(key) + " Sample Value : " + this.sampleValue(key));
		});
		System.out.println("RANDOM 14");
		random_skeletons.forEach((key, value) -> {
		    System.out.println("Bundle : " + key.toString() + " Value : " + value + " Sample Value : " + this.sampleValue(key) + " Sample Value : " + this.sampleValue(key));
		});
		
		/*
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
		*/
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
		
		return possibleBids.last().bundle;
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

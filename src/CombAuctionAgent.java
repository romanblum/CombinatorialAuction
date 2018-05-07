import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import brown.agent.AbsCombinatorialProjectAgentV2;
import brown.agent.library.T1CombAgent;
import brown.agent.library.T2CombAgent;
import brown.exceptions.AgentCreationException;


public class CombAuctionAgent extends AbsCombinatorialProjectAgentV2 {
	
	ArrayList<Region> regions;
	SortedSet<Bundle> skeletons;
	SortedSet<Bundle> lastStrongSets;
	
	Set<Integer> last_demand;
	
	Map<Set<Integer>, Double> queries;
	
	Bundle alloc;
	SortedSet<DemandedItem> shared;
	double prices[] = new double[98];
	
	double[] swap_prob = new double[7];
	
	double bidderType;
	double cheapProb;
	double midProb;
	double expProb;
	double smartProb;
	
	int roundCount;
	int numSkeletons;
	double totalUtility = 0;
	
	Random r;
	
	public CombAuctionAgent(String host, int port)
			throws AgentCreationException {
		super(host, port);
		regions = new ArrayList<Region>(14);
		alloc = new Bundle(new HashSet<Integer>(), 0.0, 0.0);
		shared = new TreeSet<DemandedItem>();
		queries = new HashMap<Set<Integer>, Double>();
		skeletons = new TreeSet<Bundle>(Collections.reverseOrder());
		last_demand = new HashSet<Integer>();
		lastStrongSets = new TreeSet<Bundle>(Collections.reverseOrder());
		r = new Random();

		
		double p = .5;
		swap_prob[0] = p;
		for (int i = 1; i < 7; i++) {
			p = p/2.0;
			swap_prob[i] = swap_prob[i-1] + p;
		}
	}

	@Override
	public void onAuctionEnd(Set<Integer> end_goods) {
		//Data Per Round
		System.out.println("Goods Bought " + end_goods.toString());
		System.out.println("Price Paid: " + this.getBundlePrice(end_goods));
		System.out.println("Bundle Value: " + this.queryValue(end_goods));
		System.out.println("Auction Utility: " + (int)(this.queryValue(end_goods)-this.getBundlePrice(end_goods)));
		System.out.println("Number of Rounds: " + roundCount);
		totalUtility += (this.queryValue(end_goods)-this.getBundlePrice(end_goods));
		System.out.println("Total Current Utility: " + (int)totalUtility);
		
		
		//Clear Round Dependent Data
		skeletons.clear();
		last_demand.clear();
		queries.clear();
		lastStrongSets.clear();
		
		
		//File Writing for Data Testing
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
		    String stats = "Num Skeletons(" +numSkeletons+ ") Utility(" + (int)(this.queryValue(end_goods)-this.getBundlePrice(end_goods)) + ") Total Utility(" + (int)totalUtility + ") Rounds(" + roundCount +") \n";
		    
		    File file = new File("runStatsNew.txt");
		    
		    if(!file.exists()){
		        file.createNewFile();
		    }
		    
		    fw = new FileWriter(file.getAbsoluteFile(), true);
		    bw = new BufferedWriter(fw);
		    
		    bw.write(stats);
		    
		    System.out.println("Stats added.");
		    
		    
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} finally {
		    try{
		        if (bw!=null){
		            bw.close();
		        }
		        if (fw!=null){
		            fw.close();
		        }
		    } catch (IOException ex){
		        ex.printStackTrace();
		    }
		}
	}

	@Override
	public void onAuctionStart() {
		//Initialize regions
		
		long tStart = System.currentTimeMillis();
		roundCount = 0;
		
		for (int i = 0; i < 14; i++) {
			ArrayList<Integer> region_items = new ArrayList<Integer>(7);
			ArrayList<Double> region_vals = new ArrayList<Double>(7);
			for (int j = 0; j < 7; j++) { //for each item in region
				Set<Integer> singleton = new HashSet<Integer>(); //
				singleton.add(i + 14*j);
				region_items.add(i + 14*j);
				region_vals.add(this.queryValue(singleton)); 
			}
			regions.add(new Region(region_items, region_vals, null));
		}
		
		//Intialize queue of random 14 region skeletons
		PriorityQueue<Bundle> skeletonQueue = new PriorityQueue<Bundle>(500, Collections.reverseOrder());
		
		int i = 0;
		Set<Set<Integer>> batch = new HashSet<Set<Integer>>();
		while ((System.currentTimeMillis() - tStart) < 15000) {
			Set<Integer> set = new HashSet<Integer>();
			for (int j = 0; j < 14; j++) {
				set.add(regions.get(j).getRandomItem());
			}

			double setValue = this.queryValue(set);
			queries.put(set, setValue);
			batch.add(set);


			// generate one random skeleton 

			if (i == 40) {
				Map<Set<Integer>, Double> sampleVals = this.sampleValues(batch);
				for (int j = 0; i<2; i++){
					//sumSampleValues += this.sampleValue(set);
					System.out.println(batch.size());
					Map<Set<Integer>, Double> sampleVal = this.sampleValues(batch);
					sampleVal.forEach((bundle, val) -> {
						sampleVals.compute(bundle, (k,v) -> (v == null) ? val : val+v);
					});
				}

				// sampleVals.replaceAll((key, val) -> val/3);
				
				batch.forEach(bundle -> {
					skeletonQueue.add(new Bundle(bundle, queries.computeIfAbsent(bundle, k->this.queryValue(k)), sampleVals.get(bundle)));
				});
				i = 0;
				batch.clear();
			} else {
				i++;
			}
		}
	
		int counter = 0;
		numSkeletons = skeletonQueue.size();
		while ((System.currentTimeMillis() - tStart) < 19300 && counter<10) {
				Bundle currentBest = skeletonQueue.poll();
				bidderType += currentBest.getDemandPrice();
				currentBest.updatePrice(0.0);
				skeletons.add(currentBest);
				counter++;
		}
		System.out.println("Skeleton size : " + skeletons.size());
		
		bidderType = bidderType/10000.0;
		bidderType = bidderType < 0 ? 0.0 : bidderType > 1 ? 1.0 : bidderType;
		initializeSwapProbs();
		System.out.println("Begin Bidding:");

	}

	@Override
	public void onBidResults(double[] demand) {
		
		//get prices
		prices = this.getPrices();
		Set<Integer> allocated_goods = new HashSet<Integer>();
		
		
		//look for shared demanded items after clearing
		shared.clear();
		for (int i = 0; i < 98; i++) {
			if (demand[i] == 1.0) {
				allocated_goods.add(i);
			}
			if (demand[i] > 0.0) {
				shared.add(new DemandedItem(i, demand[i]));
			}
		}
		
		//create allocation bundle
		alloc = new Bundle(allocated_goods, queries.computeIfAbsent(allocated_goods, k->this.queryValue(k)), this.getBundlePrice(allocated_goods));
		
		//update skeleton prices
		skeletons.forEach(s -> {
			s.updatePrice(prices);
		});
	}

	@Override
	public Set<Integer> onBidRound() {
		long startT = System.currentTimeMillis();
		roundCount++;
		
		// Sorted set of possible bids in this round
		TreeSet<Bundle> possibleBids = new TreeSet<Bundle>(Collections.reverseOrder());
		
		// consider currently allocated bundle
		possibleBids.add(alloc);
		
		// consider bundle that we bid on last round unioned with currently allocated goods
		last_demand.addAll(alloc.getBundle());
		possibleBids.add(new Bundle(last_demand, queries.computeIfAbsent(last_demand,k->this.queryValue(k)), this.getBundlePrice(last_demand)));
		
		// for each skeleton consider the bundle unioned with our allocated goods
		for (Bundle s : skeletons) {
			Bundle merged = new Bundle(s, alloc);
			merged.updateValue(queries.computeIfAbsent(merged.getBundle(), k->this.queryValue(k)));
			merged.updatePrice(this.getBundlePrice(merged.getBundle()));
			possibleBids.add(merged);			
			
			Bundle alloc_swapped = new Bundle(s);
			
			for (Integer item : alloc.getBundle()) {
				int region = getRegionID(item);
				Optional<Integer> j = alloc_swapped.getRegionItem(region);
				if (j.isPresent()) {
					alloc_swapped.swap(j.get(), item);
				}	
			}
			
			alloc_swapped.addBundle(alloc);
			alloc_swapped.updateValue(queries.computeIfAbsent(alloc_swapped.getBundle(), k->this.queryValue(k)));
			alloc_swapped.updatePrice(this.getBundlePrice(alloc_swapped.getBundle()));
			possibleBids.add(alloc_swapped);
			
		}
		
		//add skeletons to strong sets
		lastStrongSets.addAll(skeletons);
		Iterator<Bundle> iter = lastStrongSets.iterator();
		Bundle b = new Bundle(iter.next());
		
		//search for advantageous item swaps for as long as time allows
		while (System.currentTimeMillis() - startT < 1750) {
			double swap_p = r.nextDouble();
			int num_swap = 0; 
			
			for (int i = 0; i < 7; i++) {
				if (swap_p < swap_prob[i]) {
					num_swap = i+1;
					break;
				}
			}
			
			for (int j = 0; j < num_swap; j++) {
				double p = r.nextDouble();
				int region = r.nextInt(14);
				Optional<Integer> opt_i = b.getRegionItem(region);
				if (!opt_i.isPresent()) {
					continue;
				}
				int i = opt_i.get();
				
				Integer swap = null;
				// cheap swap
				if (p <= cheapProb) {
					swap = regions.get(region).getCheapSwap(i);
				}
				// medium swap
				if (p > cheapProb && p <= midProb) {
					swap = regions.get(region).getMediumSwap(i);
				}
				// expensive swap
				if (p > midProb && p <= expProb) {
					swap = regions.get(region).getExpensiveSwap(i);
				}

				if (p > smartProb) {
					swap = regions.get(region).getBestDeal(prices);

				}
				b.swap(i, swap);
				
			}
			b.addBundle(alloc);
			b.updatePrice(this.getBundlePrice(b.getBundle()));
			b.updateValue(queries.computeIfAbsent(b.getBundle(), k->this.queryValue(k)));
			possibleBids.add(b);

			if (!iter.hasNext()) {
				iter = skeletons.iterator();	
			}
			b = new Bundle(iter.next());
			
		}
				
		lastStrongSets.clear();
		Iterator<Bundle> it = possibleBids.iterator();
		for (int i = 0; i < 10; i++) {
			Bundle good_b = it.next();
			lastStrongSets.add(good_b);
		}
		last_demand = (possibleBids.first().getDemandPrice()) < 0 ? new HashSet<Integer>() : possibleBids.first().getBundle();
		return last_demand;
	}
	
	private int getRegionID(Integer item) {
		return item % 14;
	}
	
	private void initializeSwapProbs() {
		
		//probabilities given our bidding type (how high our valuation is)
		smartProb = .25;
		cheapProb = .25;
		midProb = .25;
		expProb = .25;
		
		midProb = cheapProb + midProb;
		expProb = midProb+expProb;
		smartProb = expProb;
	}
	
	public static void main(String[] args) {
		try {
			new CombAuctionAgent("localhost", 2121);
			for (int i = 1; i <= 7; i++) {
				new T2CombAgent("localhost", 2121, "bot"+i);
			}
			while(true) {
			}
		} catch (AgentCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}

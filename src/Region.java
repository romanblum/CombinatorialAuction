import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class Region {

	ArrayList<Integer> items;
	ArrayList<Double> item_vals;
	Double totalVal;
	
	Random r;
	int region_size;
	
	public Region(ArrayList<Integer> items, ArrayList<Double> item_vals, Double totalVal) {
		this.items = items;
		this.item_vals = item_vals;
		this.totalVal = totalVal; 
		r = new Random();
		region_size = items.size();
		/*
		String regionString = "";
		for (int i = 0; i < region_size; i++) {
			regionString += item_vals.get(i).intValue() +" ";
		}
		regionString += "Value : " + (int) totalVal;
		System.out.println(regionString);
		*/
	}
	
	public Integer getRandomItem() {
		int ind =  r.nextInt(region_size);
		return items.get(ind);
	}
	
	public Integer getCheapSwap(Integer i) {
		if (i == items.get(0)) {
			return items.get(1);
		}
		if (i == items.get(1)) {
			return items.get(0);
		}
		return items.get((r.nextInt(2)));
	}
	
	public Integer getExpensiveSwap(Integer i) {
		if (i == items.get(2)) {
			return items.get(3);
		}
		if (i == items.get(3)) {
			return items.get(2);
		}
		return items.get((r.nextInt(2)) + 2);
	}
	
	public Integer getMediumSwap(Integer i) {
		Integer newItem = r.nextInt(3) + 4;
		if (items.get(newItem) == i) {
			return (newItem - 3 % 3) + 4;
		}
		return items.get(newItem);
	}
	
	public Integer getBestDeal(double[] prices) {
		Integer minItem = 0;
		double minPrice = Double.MAX_VALUE;
		for (int i = 0; i < items.size(); i++) {
			if ((item_vals.get(i) - prices[items.get(i)]) < minPrice) {
				minItem = items.get(i);
			}
		}
		return minItem;
	}
	
}

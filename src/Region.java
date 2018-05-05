import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class Region {

	ArrayList<Integer> items;
	ArrayList<Double> item_vals;
	double totalVal;
	
	Random r;
	int region_size;
	
	public Region(ArrayList<Integer> items, ArrayList<Double> item_vals, double totalVal) {
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
	
}

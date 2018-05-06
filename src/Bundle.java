import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class Bundle implements Comparable<Bundle> {
	Set<Integer> bundle;
	double bundle_val;
	double bundle_demand;
	
	public Bundle(Set<Integer> bundle, double bundle_val, double bundle_price) {
		this.bundle = bundle;
		this.bundle_val = bundle_val;
		this.bundle_demand = bundle_val - bundle_price;
	}
	
	public Bundle(Bundle b1, Bundle b2) {
		this.bundle = new HashSet<Integer>(b1.getBundle());
		this.bundle.addAll(b2.getBundle());
	}

	
	public Bundle(Bundle b) {
		this.bundle = new HashSet<Integer>(b.bundle);
		this.bundle_val = b.bundle_val;
		this.bundle_demand = b.bundle_demand;
	}

	public void updatePrice(double price) {
		this.bundle_demand = this.bundle_val - price;
	}
	
	public void updatePrice(double[] prices) {
		double bundle_price = bundle.stream().mapToDouble(x -> prices[x]).sum();
		this.bundle_demand = bundle_val - bundle_price;
	}

	@Override
	public int compareTo(Bundle o) {
		return (int) (this.bundle_demand - (o.bundle_demand));
	}
	
	public Set<Integer> getBundle(){
		return this.bundle;
	}
	
	public double getValue(){
		return this.bundle_val;
	}
	
	public void updateValue(double val) {
		this.bundle_val = val;
	}
	
	public double getDemandPrice() {
		return this.bundle_demand;
	}
	
	public double getPrice(){
		return this.bundle_val - this.bundle_demand;
	}
	
	// NOT SAFE - VALUE AND PRICE MUST BE UPDATED
	public void addBundle(Bundle b) {
		this.bundle.addAll(b.getBundle());
	}
	
	// NOT SAFE - VALUE AND PRICE MUST BE UPDATED
	public void addItems(Set<Integer> b) {
		this.bundle.addAll(b);
	}
	
	// NOTE SAFE - VALUE AND PRICE MUST BE UPDATED
		public void removeBundle(Bundle b) {
			this.bundle.removeAll(b.getBundle());
		}
	
	// NOTE SAFE - VALUE AND PRICE MUST BE UPDATED
	public void removeItems(Set<Integer> b) {
		this.bundle.removeAll(b);
	}
	
	public Optional<Integer> getRegionItem(Integer region) {
		return bundle.stream().filter(item -> (item%14)==region).findAny();
	}

	public void swap(Integer i, Integer swap) {
		bundle.remove(i);
		bundle.add(swap);	
	}
	
}

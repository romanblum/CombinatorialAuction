import java.util.Set;


public class Bundle implements Comparable<Bundle> {
	Set<Integer> bundle;
	double bundle_val;
	double bundle_price;
	
	public Bundle(Set<Integer> bundle, double bundle_val, double bundle_price) {
		this.bundle = bundle;
		this.bundle_val = bundle_val;
		this.bundle_price = bundle_price;
	}
	
	public void updatePrice(double price) {
		this.bundle_price = price;
	}
	
	public void updatePrice(double[] prices) {
		bundle_price = bundle.stream().mapToDouble(x -> prices[x]).sum();
	}

	@Override
	public int compareTo(Bundle o) {
		return (int) (this.bundle_val - this.bundle_price - (o.bundle_val - o.bundle_price));
	}
	
	public Set<Integer> getBundle(){
		return this.bundle;
	}
	
	public double getValue(){
		return this.bundle_val;
	}
	
	public double getPrice(){
		return this.bundle_price;
	}
	
	
}

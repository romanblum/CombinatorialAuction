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

	@Override
	public int compareTo(Bundle o) {
		return (int) (this.bundle_val - this.bundle_price - (o.bundle_val - o.bundle_price));
	}
	
	
}

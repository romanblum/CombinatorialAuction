
public class DemandedItem implements Comparable<DemandedItem> {
	Integer item;
	double demand;
	
	public DemandedItem(Integer item, double demand) {
		this.item = item;
		this.demand = demand;
	}
	
	@Override
	public int compareTo(DemandedItem o) {
		// TODO Auto-generated method stub
		return (int) (8 * (this.demand - o.demand));
	}
	
	
}

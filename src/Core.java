import java.util.*;

public class Core {
	int core_num;
	int cycle = 0;
	Queue<RequestEntry> dq = new LinkedList<RequestEntry>();
	Cache l1cache = new L1Cache();
	Cache l2piece = new L2Piece();
	boolean finished_all_requests = false;
	public int cycle_done = 0;
	public int total_requests_missed = 0;
	public int total_miss_penalty = 0;

	public Core(int core_num)
	{
		this.core_num = core_num;
	}

	public void add_request_entry(int delta, long address, int rw)
	{
		RequestEntry new_req = new RequestEntry(delta, address, rw, l1cache);
		dq.add(new_req);
	}

	public void print_dq()
	{
		for(RequestEntry re : dq) {
			print_request(re);
		}
	}

	public void print_request(RequestEntry re)
	{
		Debug.println("address:"+re.address+" rw:"+re.rw+" delta:"+re.delta);
	}

	public void do_cycle()
	{
		if (finished_all_requests) {
			return;
		} else if (dq.size() == 0) {
			Debug.println("Core "+core_num+" is completing last request at cycle "+cycle);
			cycle_done = cycle;
			finished_all_requests = true;
		}

		RequestEntry head = dq.peek();
		if (head == null) {
			return;
		}
		if (head.delta > 0) {
			head.delta--;
			if (head.delta == 0) {//ready to be issued
				boolean hit = l1cache.access(head);
				if (hit) {
					Debug.println("Cache hit for core "+core_num+" at cycle "+cycle+" for request:");
					print_request(head);
					dq.remove();
				} else {
					Debug.println("Cache miss for core "+core_num+" at cycle "+cycle+" for request:");
					print_request(head);
					head.cycle_issued = cycle;
				}
			}
		} else {
			if (cycle == 0 && head.delta <= 0) {//edge case where first cycle has req
				boolean hit = l1cache.access(head);
				if (hit) {
					Debug.println("Cache hit for core "+core_num+" at cycle "+cycle+" for request:");
					print_request(head);
					dq.remove();
				} else {
					Debug.println("Cache miss for core "+core_num+" at cycle "+cycle+" for request:");
					print_request(head);
					head.cycle_issued = cycle;
				}
			} else if (head.resolved) {
				//this req is now done
				process_resolved_request();
			}
		}

		cycle++;
	}

	public void process_resolved_request() {
		RequestEntry head = dq.remove();
		total_requests_missed++;
		int miss_penalty = cycle - head.cycle_issued;
		total_miss_penalty += miss_penalty;

		//debug printing
		Debug.println("\n");
		Debug.println("Request completed for core "+core_num+":");
		print_request(head);
		Debug.println("Cycle Completed: "+cycle+" Miss Penalty: "+miss_penalty);
	}

	public int get_dq_size()
	{
		return dq.size();
	}

}

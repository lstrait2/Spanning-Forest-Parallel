import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

class parallel_SF
{
	private static void countSF(final ArrayList<Graph.Edge> edges)
	{
		int inSF = 0;
		for(int i = 0; i < edges.size(); i++)
		{
			if(edges.get(i).inSF)
				inSF++;
		}
		System.out.println(inSF + " edges in spanning tree");

	}

	public static int[] splitNodesEvenly(int nodes, int threads)
        {
                int[] chunks = new int[threads];
                int nodesLeft = nodes;
                for(int threadsLeft = threads; threadsLeft > 0; threadsLeft--)
                {
                       int chunk = (nodesLeft + threadsLeft - 1) / threadsLeft;                       
		       chunks[threads - threadsLeft] = chunk;
                       nodesLeft -= chunk;
                }
                return chunks;
        }

	public static void main(String[] args) throws java.io.IOException, java.lang.InterruptedException
	{
		Graph g = Graph.readEdgeGraph(args[0]);
		final int NUM_THREADS = Integer.parseInt(args[1]);
		//TODO: replace with working split code
		int[] chunks = splitNodesEvenly(g.edges.size(), NUM_THREADS);
		
		Graph.Node[] u_ancestors = new Graph.Node[g.edges.size()];
		Graph.Node[] v_ancestors = new Graph.Node[g.edges.size()];
		ConcurrentHashMap<Graph.Node,Graph.Node> newAncestors = new ConcurrentHashMap<>();

		List<Thread> threads_SF = new ArrayList<Thread>();
		final long start_SF = System.currentTimeMillis();
		int curr_idx = 0;
		for(int i = 0; i < NUM_THREADS; i++)
		{
			Runnable task = new SFRunnable(curr_idx, curr_idx + chunks[i],g.edges,u_ancestors,v_ancestors,newAncestors);
			curr_idx = curr_idx + chunks[i];
			Thread worker = new Thread(task);
		
			worker.setName(String.valueOf(i));
			worker.start();
			threads_SF.add(worker); 
		}
		for(int i = 0; i < NUM_THREADS; i++)
			threads_SF.get(i).join();
		

		List<Thread> threads_SF2 = new ArrayList<Thread>();
		curr_idx = 0;
		for(int i = 0; i < NUM_THREADS; i++)
		{
			Runnable task = new SFRunnable2(curr_idx,curr_idx+chunks[i],u_ancestors,v_ancestors,g.edges);
			Thread worker = new Thread(task);
			curr_idx += chunks[i];
			worker.setName(String.valueOf(i));
			worker.start();
			threads_SF2.add(worker);
		}
		for(int i = 0; i < NUM_THREADS; i++)
			threads_SF2.get(i).join();
		
		final long end_SF = System.currentTimeMillis();
		System.out.println("SF time: " +( (double) (end_SF - start_SF))/1000 + " seconds");
		countSF(g.edges);

	}





}

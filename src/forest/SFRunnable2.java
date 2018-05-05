import java.util.*;
public class SFRunnable2 implements Runnable
{
	private ArrayList<Graph.Edge> edges;
	private int start_idx;
	private int end_idx;
	private Graph.Node[] u_ancestors;
	private Graph.Node[] v_ancestors;

	public SFRunnable2(int start, int end, Graph.Node[] u_ancestors, Graph.Node[] v_ancestors,ArrayList<Graph.Edge> edges)
	{
		this.start_idx = start;
		this.end_idx = end;
		this.edges = edges;
		this.u_ancestors = u_ancestors;
		this.v_ancestors = v_ancestors;
	}
	
	public void  run()
	{
		for(int i = this.start_idx; i < this.end_idx; i++)
		{
			buildSF(i);
		}
	}

	private void buildSF(int i)
        {
                final Graph.Node u_ancestor = u_ancestors[i];
                Graph.Edge edge = this.edges.get(i);
		if(u_ancestor != null)
		{
                	synchronized(u_ancestor)
                	{
                                u_ancestor.ancestor = v_ancestors[i];
                                edge.inSF = true;
			}

                }
        }
}

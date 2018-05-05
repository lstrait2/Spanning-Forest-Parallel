import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class SFRunnable implements Runnable
{
	private int start_idx;
	private int end_idx;
	private Graph.Node[] u_ancestors;
	private Graph.Node[] v_ancestors;
        private	ArrayList<Graph.Edge> edges;
	private ConcurrentHashMap<Graph.Node,Graph.Node> newAncestors;


	public SFRunnable(int start, int end, ArrayList<Graph.Edge> edges, Graph.Node[] u_ancestors, Graph.Node[] v_ancestors, ConcurrentHashMap<Graph.Node,Graph.Node> newAncestors)
	{
		this.start_idx = start;
		this.end_idx = end;
		this.edges = edges;
		this.u_ancestors = u_ancestors;
		this.v_ancestors = v_ancestors;
		this.newAncestors = newAncestors;
	}
	@Override
	public void run()
	{
		for(int i = this.start_idx; i < this.end_idx; i++)
		{
			Graph.Edge e = this.edges.get(i);
			buildSF(i,this.u_ancestors,this.v_ancestors,this.newAncestors);
		}
	}
	
	private Graph.Node ancestorOf(final Graph.Node u, ConcurrentHashMap<Graph.Node, Graph.Node> newAncestors)
	{
		Graph.Node newAncestor;
		if(u.ancestor != u)
		{
			return u.ancestor;
		}
		else if( (newAncestor = newAncestors.get(u)) != null)
		{
			return newAncestor;
		}
		return u;
	}

	private void buildSF(int i, Graph.Node[] u_ancestors, Graph.Node[] v_ancestors, ConcurrentHashMap<Graph.Node, Graph.Node> newAncestors)
	{

		Graph.Edge e = this.edges.get(i);

		if(e.u.index < e.v.index)
		{
			synchronized(e.v)
			{
				Graph.Node v_ancestor = e.v;
				synchronized(e.u)
				{
					Graph.Node u_ancestor = e.u;
					buildSF(e,i,u_ancestors,v_ancestors,newAncestors,u_ancestor,v_ancestor);
				}
			}

		}
		else
		{
			synchronized(e.u)
			{
				Graph.Node u_ancestor = e.u;
				synchronized(e.v)
				{
					Graph.Node v_ancestor = e.v;
					buildSF(e,i,u_ancestors,v_ancestors,newAncestors,u_ancestor,v_ancestor);
				}
			}
		}
	}


	private void buildSF(Graph.Edge e, int i, Graph.Node[] u_ancestors, Graph.Node[] v_ancestors, ConcurrentHashMap<Graph.Node,Graph.Node> newAncestors, Graph.Node u_ancestor, Graph.Node v_ancestor)
	{
		// locks acquired for u_ancestor and v_ancestor in previous call
		Graph.Node nextAncestor_u = ancestorOf(u_ancestor,newAncestors);
		Graph.Node nextAncestor_v = ancestorOf(v_ancestor,newAncestors);

		if(nextAncestor_u == u_ancestor && nextAncestor_v == v_ancestor)
		{
			if(u_ancestor == v_ancestor)
				return;

			if(u_ancestor.index < v_ancestor.index)
			{
				// swap nodes
				Graph.Node temp = u_ancestor;
				u_ancestor = v_ancestor;
				v_ancestor = temp;
			}
			
			u_ancestors[i] = u_ancestor;
			v_ancestors[i] = v_ancestor;
			newAncestors.put(u_ancestor,v_ancestor);
		}
		else
		{
			if(nextAncestor_u == u_ancestor)
			{
				synchronized(nextAncestor_v)
				{
					buildSF(e,i,u_ancestors,v_ancestors,newAncestors,u_ancestor,nextAncestor_v);
				}
			}
			
			else if(nextAncestor_v == v_ancestor)
			{
				synchronized(nextAncestor_u)
				{
					buildSF(e,i,u_ancestors,v_ancestors,newAncestors,nextAncestor_u,v_ancestor);
				}
			}
			else if(nextAncestor_u.index < nextAncestor_v.index)
			{
				synchronized(nextAncestor_v)
				{
					buildSF(e,i,u_ancestors,v_ancestors,newAncestors,u_ancestor,nextAncestor_v);
				}
			}
			else
			{
				synchronized(nextAncestor_u)
				{
					buildSF(e,i,u_ancestors,v_ancestors,newAncestors,nextAncestor_u,v_ancestor);
				}

			}
		}
	}
}

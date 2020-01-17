package algorithms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node;
import dataStructure.node_data;
import gameClient.Fruit;
/**
 * This empty class represents the set of graph-theory algorithms
 * which should be implemented as part of Ex2 - Do edit this class.
 * @author 
 *
 */
public class Graph_Algo implements graph_algorithms,Serializable
{
	private graph g;

	public Graph_Algo()
	{
		;
	}
	public Graph_Algo(graph g)
	{
		this.g = g;
	}
	@Override
	public void init(graph g)
	{
		this.g = g;

	}

	@Override
	public void init(String file_name) 
	{
		try
		{    
			FileInputStream file = new FileInputStream(file_name); 
			ObjectInputStream objInput = new ObjectInputStream(file); 
			g = (graph)objInput.readObject(); 
			objInput.close(); 
			file.close(); 
		} 
		catch(IOException e) 
		{ 
			e.printStackTrace();
		} 
		catch(ClassNotFoundException e) 
		{ 
			e.printStackTrace(); 
		} 
	}

	@Override
	public void save(String file_name) 
	{
		try
		{    
			FileOutputStream file = new FileOutputStream(file_name); 
			ObjectOutputStream out = new ObjectOutputStream(file); 

			out.writeObject(g); 
			out.close(); 
			file.close(); 

			System.out.println("Object has been serialized"); 
		}   
		catch(IOException ex) 
		{ 
			System.out.println("IOException is caught"); 
		} 

	}

	@Override
	public boolean isConnected()
	{
		if(g.getV().size()== 0 || g.getV().size() == 1)
		{
			return true;
		}
		
		int tag = 0;
		boolean flag = true;
		
		for (node_data nd : g.getV())
		{
			nd.setTag(0);
		}
		
		Queue<Integer> Q = new LinkedList<>();
		node_data first = g.getV().iterator().next();
		Q.add(first.getKey());
		while (!Q.isEmpty()) 
		{
			int peek = Q.peek();
			try 
			{
				for (edge_data edge : g.getE(peek)) 
				{
					if (g.getNode(edge.getDest()).getTag() == 0) 
					{
						g.getNode(edge.getDest()).setTag(1);
						tag++;
						Q.add(edge.getDest());
					}
				}
			} catch (Exception e) {flag = false;}
			
			Q.poll();
		}
		if (tag == g.nodeSize() && flag)
		{
			return true;
		}
		return false;
	}

	private void resetInfo()
	{
		for(Iterator<node_data> verIter=g.getV().iterator();verIter.hasNext();)
		{
			verIter.next().setInfo("not visit");
		}
	}
	
	public boolean isWay(int src,int dest)
	{
		boolean flag = false;
		g.getNode(src).setInfo("visit");
		
		try
		{
			for(Iterator<edge_data> iter = g.getE(src).iterator();iter.hasNext();) 
			{
				edge_data ed = iter.next();
				String temp  = g.getNode(ed.getDest()).getInfo();
		
				if(!temp.equals("visit"))
				{				
					if(ed.getDest() == dest)
					{
						return true;
					}
					else 
					{
						if(!flag && isWay(ed.getDest(),dest))
						{							
							flag =  true;
						}
					}
				}
			}
			this.resetInfo();
			return flag;
		} catch(Exception e) 
		{
			this.resetInfo();
			flag =  false;
		}
		return flag;
	}
	
	@Override
	public double shortestPathDist(int src, int dest) 
	{
		node_data minWnode = null;
		
		for(Iterator<node_data> iter=g.getV().iterator();iter.hasNext();) 
		{
			// init the first node to zero
			node_data nd=iter.next();
			if(nd.getKey()==src)
			{
				nd.setWeight(0);
			}
			else
			{
				// init the other nodes to inifinity
				nd.setWeight(Double.POSITIVE_INFINITY);
			}
		}

		if(!isWay(src,dest)) // if there is no way 
		{
			return Double.POSITIVE_INFINITY;
		}
		
		Collection<node_data> nodes = new ArrayList<node_data>();
		for (node_data node : g.getV()) 
		{
			nodes.add(node);
		}
		while(!nodes.isEmpty())
		{
			double minW = Double.POSITIVE_INFINITY; // return this value
			minWnode = null; // node with min weigth
			
			for(Iterator<node_data> iter = nodes.iterator();iter.hasNext();) 
			{ //find min node
				node_data nd = iter.next();
				if(nd.getWeight() < minW) 
				{
					minW = nd.getWeight();
					minWnode = nd;
				}
			}
			try // if the node doesn't have edge
			{
				edge_data ed = null;
				Collection<edge_data> edges = new ArrayList<edge_data>();
				for (edge_data edge : g.getE(minWnode.getKey())) 
				{
					edges.add(edge);
				}
				while (!edges.isEmpty())
				{
					boolean flag = false;
					ed = edges.iterator().next();
					for(Iterator<node_data> iter = nodes.iterator();iter.hasNext();) 
					{ //find the dest node
						node_data nd = iter.next();
						if(nd.getKey() == ed.getDest())
						{
							if(nd.getWeight() > (minWnode.getWeight()+ed.getWeight())) 
							{
								nd.setWeight(minWnode.getWeight()+ed.getWeight());
								nd.setInfo(minWnode.getKey()+"");
								nd.setTag(minWnode.getKey());
								edges.remove(ed);
								flag = true;
								break;
							}
						}
					}
					if (!flag)
					{
						edges.remove(ed);
					}
				}
				nodes.remove(minWnode);
			} catch (Exception e) {

			}

			if(minWnode.getKey() == dest) // check if we arrived to dest and finish the path
			{
				return minWnode.getWeight();
			}
		}
		return minWnode.getWeight();
	}

	@Override
	public List<node_data> shortestPath(int src, int dest)
	{
		List<node_data> node = new ArrayList<>();
		shortestPathDist(src, dest);
		node_data des = null, nd = null, prev = null, sr = null;
		for(Iterator<node_data> iter=g.getV().iterator();iter.hasNext();) 
		{
			nd=iter.next();
			if(nd.getKey() == src)
				sr = nd;
			if(nd.getKey() == dest)
			{
				node.add(nd);
				des = nd;
			}
		}
		nd = des;
		while (nd.getKey() != src) {
			for(Iterator<node_data> iter=g.getV().iterator();iter.hasNext();) 
			{
				prev = iter.next();
				if(prev.getKey() == nd.getTag())
				{
					node.add(prev);
					nd = prev;
					break;
				}
			}
		}
//		node.add(sr);
		Collections.reverse(node);
		return node;
	}

	@Override
	public List<node_data> TSP(List<Integer> targets) 
	{
		List<node_data> path = new ArrayList<node_data>();
		if (this.isConnected() == false)
		{
			return null;
		}
		int rand = (int) (Math.random() * targets.size());
		int src = targets.get(rand), dest = 0, j = 0;
		path.add(g.getNode(src));
		targets.remove(rand);
		double minWay = Double.POSITIVE_INFINITY;
		while (!targets.isEmpty())
		{
			for (int i=0; i<targets.size(); i++)
			{
				if(this.shortestPathDist(src, targets.get(i)) < minWay)
				{
					minWay = this.shortestPathDist(src, targets.get(i));
					dest = targets.get(i);
					j = i;
				}
			}
			path.addAll(this.shortestPath(src, dest));
			src = dest;
			targets.remove(j);
		}
		return path;
	}

	@Override
	public graph copy()
	{
		Graph_Algo copy = new Graph_Algo();
		String file_name = "copy.txt";
		this.save(file_name);
		copy.init(file_name);
		return copy.g;
	}
	
	//check on which edge the fruit is on
		public edge_data onEdge(Fruit f,DGraph g)
		{
			double EPSILON = 0.00005;
			for (node_data node : g.getV()) 
			{
				for (edge_data edge : g.getE(node.getKey())) 
				{
					double edgeLen = g.getNode(edge.getSrc()).getLocation().distance2D(g.getNode(edge.getDest()).getLocation());
					double srcTof = g.getNode(edge.getSrc()).getLocation().distance2D(f.getPos());
					double dstTof = g.getNode(edge.getDest()).getLocation().distance2D(f.getPos());
					
					if(((srcTof + dstTof) == (edgeLen - EPSILON)) || ((srcTof + dstTof) == (edgeLen + EPSILON)))
					{
						return edge;
					}
				}
			}
			return null;
		}
}
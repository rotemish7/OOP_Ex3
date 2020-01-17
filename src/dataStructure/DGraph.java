package dataStructure;

import java.awt.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Point3D;

public class DGraph extends Observable implements graph, Serializable
{
	HashMap<Integer,node_data> V = new HashMap<>();
	HashMap<Integer,HashMap<Integer,edge_data>> E = new HashMap<>();
	int MC;
	int ESize;
	//Empty constructor
	public DGraph()
	{
		;
	}

	public DGraph(HashMap<Integer,node_data>V,HashMap<Integer,HashMap<Integer,edge_data>>E,int mc,int esize)
	{
		this.V.putAll(V);
		this.E.putAll(E);
		this.MC = mc;
		this.ESize = esize;
	}

	public void init(String data)
	{
		try
		{
			JSONObject graph = new JSONObject(data);
			JSONArray nodes = graph.getJSONArray("Nodes");
			JSONArray edges = graph.getJSONArray("Edges");

			int i;
			int s;
			for(i = 0; i < nodes.length(); ++i) 
			{
				s = nodes.getJSONObject(i).getInt("id");
				String pos = nodes.getJSONObject(i).getString("pos");
				Point3D p = new Point3D(pos);
				this.addNode(new node(s, p));
			}

			for(i = 0; i < edges.length(); ++i) 
			{
				s = edges.getJSONObject(i).getInt("src");
				int d = edges.getJSONObject(i).getInt("dest");
				double w = edges.getJSONObject(i).getDouble("w");
				this.connect(s, d, w);
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public node_data getNode(int key) 
	{
		if(key < 0 && !this.V.containsKey(key))
		{
			return null;
		}
		return this.V.get(key);
	}

	@Override
	public edge_data getEdge(int src, int dest) 
	{
		if( !E.containsKey(src) && !E.containsValue(E.get(dest)))
		{
			return null;
		}
		return this.E.get(src).get(dest);
	}

	@Override
	public void addNode(node_data n) 
	{
		this.V.put(n.getKey(), n);
		MC++;
		setChanged();
		notifyObservers(n);
	}

	@Override
	public void connect(int src, int dest, double w) 
	{
		boolean b=true;

		if (src == dest && w <= 0) 
		{
			System.out.println("src and dest are the same");
			b = false;
			return;
		}
		if (b||(!(V.get(src) == null) && !(V.get(dest) == null))) 
		{
			if (E.containsKey(src))
			{
				if (E.get(src).get(dest) != null) 
				{
					System.out.println("The edge is already exsit");
					return;
					//throw new RuntimeException("The edge is already exist!");
				}
				else 
				{
					edge_data edge = new edge(src, dest, w);
					this.E.get(src).put(dest, edge);
					MC++;
					ESize++;
					setChanged();
					notifyObservers();
				}
			} 
			else 
			{
				edge_data edge = new edge(src, dest, w);
				E.put(src, new HashMap<>());
				this.E.get(src).put(dest, edge);
				MC++;
				ESize++;
				setChanged();
				notifyObservers();
			}
		} 
		else if(b)
		{
			System.out.println("src/dest does not exist");
			//throw new RuntimeException("src/dest does not exist!");
		}
	}

	@Override
	public Collection<node_data> getV() 
	{	Collection<node_data> copyV = this.V.values();
	return copyV;
	}

	@Override
	public Collection<edge_data> getE(int node_id) 
	{
		if(!E.containsKey(node_id)) {return null;}

		Collection<edge_data> copyE = this.E.get(node_id).values();
		return copyE;
	}

	@Override
	public node_data removeNode(int key) 
	{
		if(!V.containsKey(key))
		{
			return null;
		}
		int n_key;

		for (Entry<Integer, node_data> nodes : V.entrySet())
		{
			n_key = nodes.getKey();
			if(n_key != key && E.containsKey(n_key) && E.get(n_key).containsKey(key))
			{
				E.get(n_key).remove(key);
				ESize--;
				MC++;
			}
			if(n_key == key && E.containsKey(key))
			{
				MC = MC+E.get(key).size();
				ESize = ESize-E.get(key).size();
				E.remove(key);
			}
		}
		MC++;		
		setChanged();
		notifyObservers();
		return V.remove(key);

		//		node_data n = V.get(key);
		//		
		//		for (Iterator<node_data> iterator = this.getV().iterator(); iterator.hasNext();) {
		//			node_data j = (node_data) iterator.next();
		//			E.get(j.getKey()).remove(key);
		//		}
		//		
		//		if(this.E.get(key) != null)
		//		{
		//			this.ESize -= this.E.get(key).size();
		//		}
		//		V.remove(key);
		//		E.remove(key);
		//		MC++;
		//		return n;
		//		
	}

	@Override
	public edge_data removeEdge(int src, int dest)
	{
		if(!V.containsKey(src) || !V.containsKey(dest))
		{
			return null;
		}
		if( E.get(src).get(dest) == null)
		{
			return null;
		}

		edge_data e = E.get(src).get(dest);

		E.get(src).remove(dest);
		ESize--;
		MC++;
		setChanged();
		notifyObservers();
		return e;
	}

	@Override
	public int nodeSize() 
	{
		return V.size();
	}

	@Override
	public int edgeSize()
	{
		return this.ESize;
	}

	@Override
	public int getMC() 
	{
		return this.MC;
	}
}

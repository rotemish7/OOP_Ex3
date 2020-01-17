package dataStructure;

import java.io.Serializable;

public class edge implements edge_data,Serializable
{
	int src;
	int dest;
	double weight;
	String info;
	int tag;
	
	public edge()
	{
		;
	}
	
	public edge(int src,int dest,double w)
	{
		this.src = src;
		this.dest = dest;
		this.weight = w;
		this.info = null;
		this.tag = 0;
	}

	@Override
	public int getSrc() 
	{
		
		return this.src;
	}

	@Override
	public int getDest() 
	{
		return this.dest;
	}

	@Override
	public double getWeight() 
	{
		return this.weight;
	}

	@Override
	public String getInfo() 
	{
		
		return this.info;
	}

	@Override
	public void setInfo(String s)
	{
		this.info = s;
		
	}

	@Override
	public int getTag() 
	{
		return this.tag;
	}

	@Override
	public void setTag(int t)
	{
		this.tag = t;
	}

}

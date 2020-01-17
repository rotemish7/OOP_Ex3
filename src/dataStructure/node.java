package dataStructure;

import java.io.Serializable;

import oop_dataStructure.oop_node_data;
import utils.Point3D;

public class node implements node_data,Serializable
{
	int key;
	Point3D location;
	double weight;
	String info;
	int tag; // is the prev key
	
	public node(int key, Point3D p,double w)
	{
		this.key = key;
		this.location = p;
		this.weight = w;
		this.info = "not visit";
	}
	
	public node(int key,Point3D p)
	{
		this.key = key;
		this.location = p;
		this.weight = 0;
		this.info = "not visit";
	}
	
	@Override
	public int getKey()
	{
		return this.key;
	}

	@Override
	public Point3D getLocation() 
	{
		return this.location;
	}

	@Override
	public void setLocation(Point3D p) 
	{
		this.location = p;
	}

	@Override
	public double getWeight() 
	{
		return this.weight;
	}

	@Override
	public void setWeight(double w) 
	{
		this.weight = w;
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

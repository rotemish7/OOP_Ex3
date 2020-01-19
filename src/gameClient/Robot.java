package gameClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import utils.Point3D;

public class Robot 
{

	private int src;
	private Point3D pos;
	private int id;
	private int dest;
	private int value;
	private int speed;

	public Robot() {}
	
	public Robot(int src, Point3D pos, int id, int dest, int value, int speed)
	{
		this.src=src;
		this.pos=pos;
		this.id=id;
		this.dest=dest;
		this.value=value;
		this.speed=speed;
	}
	
	public Robot(String s) 
	{
		try 
		{
			JSONObject obj_JSONObject = new JSONObject(s);
			JSONObject JSON_Robot = obj_JSONObject.getJSONObject("Robot");
			int id = JSON_Robot.getInt("id"); // Extract the id of the robot
			this.id = id;
			int src = JSON_Robot.getInt("src"); // Extract the src of the robot
			this.src = src;
			int dest = JSON_Robot.getInt("dest"); // Extract the dest of the robot
			this.dest = dest;
			int speed = JSON_Robot.getInt("speed"); // Extract the speed of the robot
			this.speed = speed;
			int value = JSON_Robot.getInt("value"); // Extract the value of the robot
			this.value = value;
			String pos = JSON_Robot.getString("pos"); // Extract the coordinates to String
			this.pos = new Point3D(pos);

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static List<Robot> robotList()
	{
		List<Robot> ans = new ArrayList<Robot>();
		
		
		return ans;
	}

	public int getSrc()
	{
		return this.src;
	}

	public Point3D getPos()
	{
		return this.pos;
	}

	public int getId()
	{
		return this.id;
	}

	public int getDest()
	{
		return this.dest;
	}

	public int getValue()
	{
		return this.value;
	}

	public int getSpeed()
	{
		return this.speed;
	}
	
	public void setPos(Point3D p)
	{
		this.pos = p;
	}
}
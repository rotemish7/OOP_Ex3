package gameClient;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import utils.Point3D;

public class Fruit
{
	private double value;
	private int type;
	private Point3D pos;

	public Fruit(double value, int type, Point3D pos)
	{
		this.value=value;
		this.type=type;
		this.pos=pos;
	}

	public Fruit(String s) 
	{
		try 
		{
			JSONObject obj_JSONObject = new JSONObject(s);
			JSONObject JSON_Fruit = obj_JSONObject.getJSONObject("Fruit");
			String pos = JSON_Fruit.getString("pos");// Extract the coordinates to String
			this.pos = new Point3D(pos);
			double value = JSON_Fruit.getDouble("value"); // Extract the value of the fruit
			this.value = value;
			int type = JSON_Fruit.getInt("type"); // Extract the type of the fruit
			this.type = type;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static List<Fruit> fruitList()
	{
		List<Fruit> ans = new ArrayList<Fruit>();


		return ans;
	}

	public double getValue()
	{
		return this.value;
	}

	public int getType()
	{
		return this.type;
	}

	public Point3D getPos()
	{
		return this.pos;
	}

	public void setPos(Point3D p)
	{
		this.pos = p;
	}
}

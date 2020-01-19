package gameClient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;

public class MyGameGUI extends JFrame implements ActionListener , MouseListener , Observer, Runnable 
{
	private game_service game;
	private DGraph DG;
	private Graph_Algo GA;
	private JFrame frame;
	private double maxX = Double.NEGATIVE_INFINITY;
	private double maxY = Double.NEGATIVE_INFINITY;
	private double minX = Double.POSITIVE_INFINITY;
	private double minY = Double.POSITIVE_INFINITY;
	private List<Fruit> fruits= new LinkedList<Fruit>();
	private List<Robot> robots= new LinkedList<Robot>();
	public Collection<node_data> vertex;
	public Collection<edge_data> edges;
	private String typegame = null;;
	private int scenario; 
	private Thread t;


	/**
	 * 
	 * @param dgraph represents a DGraph 
	 * @param game represents a Game Service
	 */
	public MyGameGUI()
	{
		initMyGameGUI();
	
		t = new Thread(this);
//		t.start();
	}

	public void initMyGameGUI()
	{
		this.setSize(1000, 1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//choose scenario
		frame = new JFrame();
		String s = JOptionPane.showInputDialog(frame,"Enter manual or auto");

		if(s.equals("manual") || s.equals("auto"))
		{
			typegame = s;			
		}

		if(typegame.equals("manual") || typegame.equals("auto"))
		{
			String level = JOptionPane.showInputDialog(frame,"Enter level 0 - 23");
			try
			{
				scenario = Integer.parseInt(level);
			}catch(Exception e)
			{
				;
			}
		}

		game = Game_Server.getServer(scenario);

		//initialize the graph
		this.DG = new DGraph();
		this.GA = new Graph_Algo();
		DG.init(game.getGraph());
		GA.init(DG);
		this.windowScale();

		this.addMouseListener(this);

	}

	public void paint(Graphics g)
	{
		super.paint(g);
	
		drawGraph(g);
		
		fruits = creatFruits(game.getFruits());
		drawFruits(g);
		addRobots(game.move());
		drawRobots(g);
	}

	/**
	 * 
	 * @param g represents a Graphics GUI
	 */
	public void drawGraph(Graphics g)
	{
		Collection<node_data> v = DG.getV();
		java.util.Iterator<node_data> i =  v.iterator(); 

		while(i.hasNext())
		{
			node_data temp = (node_data) i.next();
			Point3D nodes_src =temp.getLocation();

			g.setColor(Color.BLUE);
			double x = scale(nodes_src.x(),this.minX,this.maxX,80,this.getWidth()-80);
			double y = scale(nodes_src.y(),this.minY,this.maxY,80,this.getHeight()-80);
			g.fillOval((int)x-4, (int)y-5,10,10);
			g.drawString(Integer.toString(temp.getKey()),(int)x-3,(int)y-7);

			this.edges = DG.getE(temp.getKey());

			if(this.edges == null) continue;
			java.util.Iterator<edge_data> j =  edges.iterator(); 


			while(j.hasNext())
			{
				//draw the edges line in Red
				g.setColor(Color.RED);

				edge temp1 = (edge) j.next();

				Point3D nodes_dest = DG.getNode(temp1.getDest()).getLocation();
				double nodeX = scale(DG.getNode(temp1.getDest()).getLocation().x(),minX,maxX,80,this.getWidth()-80);
				double nodeY = scale(DG.getNode(temp1.getDest()).getLocation().y(),minY,maxY,80,this.getHeight()-80);
				double tempX = scale(temp.getLocation().x(),minX,maxX,80,this.getWidth()-80);
				double tempY = scale(temp.getLocation().y(),minY,maxY,80,this.getHeight()-80);
				g.drawLine((int)tempX,(int) tempY,(int)nodeX,(int)nodeY);

				//draw a point of the edges direction in Yellow
				g.setColor(Color.YELLOW);
				double dir_of_edge_x= ((nodes_src.x() + 4*nodes_dest.x())/5);
				double dir_of_edge_y= ((nodes_src.y() + 4*nodes_dest.y())/5);
				double scale_dirX = scale(dir_of_edge_x,minX,maxX,80,this.getWidth()-80);
				double scale_dirY = scale(dir_of_edge_y,minY,maxY,80,this.getHeight()-80);
				g.fillOval((int)scale_dirX-3 , (int)scale_dirY-5,10,10);

				//draw the edge weight in Black
				double mid_of_edge_x= ((nodes_src.x()+nodes_dest.x())/2);
				double mid_of_edge_y= ((nodes_src.y()+nodes_dest.y())/2);
				g.setColor(Color.BLACK);
				double scale_midX = scale(mid_of_edge_x,minX,maxX,80,this.getWidth()-80);
				double scale_midY = scale(mid_of_edge_y,minY,maxY,80,this.getHeight()-80);
				DecimalFormat df = new DecimalFormat("#.00");
				g.drawString(df.format(temp1.getWeight()),(int)scale_midX, (int)scale_midY);
			}
		}
	}

	/**
	 * 
	 * @param fruits represents a list of all the fruits 
	 */
	public List<Fruit> creatFruits(List<String> fruits)
	{
		List<Fruit> ans = new ArrayList<Fruit>();

		for (String fruit : fruits) 
		{
			Fruit f = new Fruit(fruit);
			ans.add(f);
		}
		return ans;
	}


	/**
	 * 
	 * @param g
	 */
	public void drawFruits(Graphics g)
	{
		if(fruits.size() > 0)
		{
			for (int i = 0; i < fruits.size(); i++)
			{
				Fruit f = fruits.get(i);

				if(f.getType()==1)
				{
					//draw an apple
					ImageIcon apple  = new ImageIcon("apple.png");
					Image image2 = apple.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
					apple = new ImageIcon(image2);
					double x = scale( f.getPos().x(),minX,maxX,80,this.getWidth()-80);
					double y = scale( f.getPos().y(),minY,maxY,80,this.getHeight()-80);
					g.drawImage(image2, (int)x-15, (int)y-10, this);
				}
				else
				{
					//draw a banana
					ImageIcon banana  = new ImageIcon("banana.png");
					Image image3 = banana.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
					banana = new ImageIcon(image3);
					double x = scale( f.getPos().x(),minX,maxX,80,this.getWidth()-80);
					double y = scale( f.getPos().y(),minY,maxY,80,this.getHeight()-80);
					g.drawImage(image3, (int)x-15, (int)y-10, this);
				}
			}
		}
	}

	public void addRobots(List<String> log)
	{		
		if(log!=null)
		{

			for(int i=0;i<log.size();i++)
			{
				String robot_json = log.get(i);
				try 
				{
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");

					int id = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					int speed = ttt.getInt("speed");
					int value = ttt.getInt("value");
					String pos = ttt.getString("pos");
					Point3D p = new Point3D(pos);
					Robot r = new Robot(src,p,id,dest,value,speed);
					robots.add(r);
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}

		//repaint();
	}

	/**
	 * 
	 * @param g
	 */
	public void drawRobots(Graphics g)
	{
		List<String> str = game.getRobots();
//
//		for (int i = 0; i < str.size(); i++) 
//		{
//			try
//			{
//				JSONObject obj = new JSONObject(str.get(i));
//				JSONObject obj2 = obj.getJSONObject("Robot");
//			}
//				catch(Exception e)
//				{
//
//				}
//		}
			for (int i = 0; i < robots.size(); i++) 
			{
				Robot r = robots.get(i);
				ImageIcon pac  = new ImageIcon("pac1.png");
				Image image1 = pac.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
				pac = new ImageIcon(image1);
				double x = scale( r.getPos().x(),minX,maxX,80,this.getWidth()-80);
				double y = scale( r.getPos().y(),minY,maxY,80,this.getHeight()-80);
				g.drawImage(image1, (int)x-15, (int)y-10, this);
			}

		}

		/**
		 * 
		 * @param data
		 * @param r_min
		 * @param r_max
		 * @param t_min
		 * @param t_max
		 * @return
		 */
		private double scale(double data, double r_min, double r_max,double t_min, double t_max)		
		{

			double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
			return res;
		}

		/**
		 * find min and max x and y for scaling.
		 */
		public void windowScale()
		{
			for (node_data nodes : this.DG.getV()) 
			{
				if(nodes.getLocation().x() > maxX)
				{
					maxX = nodes.getLocation().x();
				}
				if(nodes.getLocation().x() < minX)
				{
					minX = nodes.getLocation().x();
				}
				if(nodes.getLocation().y() > maxY)
				{
					maxY = nodes.getLocation().y();
				}
				if(nodes.getLocation().y() < minY)
				{
					minY = nodes.getLocation().y();
				}
			}
		}
		
		    public void AutoSetRobot(game_service game,graph level_graph) throws JSONException
		    {
		        List<String> Temp_Fruit = game.getFruits();
		        String info = game.toString();
		        JSONObject line;
		        line = new JSONObject(info);
		        JSONObject ttt = line.getJSONObject("GameServer");
		        int rs = ttt.getInt("robots");
		        for (int i = 0; i < rs; i++) 
		        {
		            int maxFruit = Integer.MIN_VALUE;
		            int MaxFruitID = 0;
		            for (int j = 0; j < Temp_Fruit.size(); j++) 
		            {
		                Fruit f = new Fruit(Temp_Fruit, j);
		                if (f.getValue() > maxFruit) {
		                    maxFruit = f.getValue();
		                    MaxFruitID = j;
		                }
		            }
		            game.addRobot(new Game_Algo().getFruitEdge(MaxFruitID,game,level_graph).getSrc());
		            Temp_Fruit.remove(MaxFruitID);
		        }
		    }

		@Override
		public void run() 
		{


		}

		@Override
		public void update(Observable arg0, Object arg1) 
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

		}

	}

package gameClient;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.java.swing.plaf.windows.resources.windows;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import utils.Point3D;
/**
 * This class represents a simple example for using the GameServer API:
 * the main file performs the following tasks:
 * 1. Creates a game_service [0,23] (line 36)
 * 2. Constructs the graph from JSON String (lines 37-39)
 * 3. Gets the scenario JSON String (lines 40-41)
 * 4. Prints the fruits data (lines 49-50)
 * 5. Add a set of robots (line 52-53) // note: in general a list of robots should be added
 * 6. Starts game (line 57)
 * 7. Main loop (should be a thread) (lines 59-60)
 * 8. move the robot along the current edge (line 74)
 * 9. direct to the next edge (if on a node) (line 87-88)
 * 10. prints the game results (after "game over"): (line 63)
 *  
 * @author boaz.benmoshe
 *
 */
public class SimpleGameClient 
{
	private static MyGameGUI window;
	private game_service game;
	//	private DGraph DG;
	//	private Graph_Algo GA;
	private String typegame;
	private int scenario;
	public Collection<node_data> vertex;
	public Collection<edge_data> edges;

	public static void main(String[] a) 
	{
		//		int scenario_num = 2;
		//		game_service game = Game_Server.getServer(scenario_num);
		//		
		//		DGraph DG = new DGraph();
		//		DG.init(game.getGraph());
		//		
		//		MyGameGUI window = new MyGameGUI(DG,game);
		//		window.setVisible(true);
		//		
		test1();
	}

	//fruit type 1 from low to high apple
	//fruit type -1 from high to low banana
	public static void test1() 
	{
		//		//game scenario 
		int scenario_num = 2;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		//
		//
		//		//add robot to the graph-as the amount of the robots there is
		//		//game.addRobot(node_id);
		//
		//		//shows the scenario objects-robot-fruits how the graph look like
		String g = game.getGraph();
		//
		DGraph DG = new DGraph();
		DG.init(game.getGraph());




		new Thread( new Runnable() 
		{

			@Override
			public void run()
			{
				window = new MyGameGUI();
				window.setVisible(true);

			}
		}).start();

		String info = game.toString();
		JSONObject line;

		try
		{
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots");
			System.out.println(info);
			System.out.println(g);
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {System.out.println(f_iter.next());}	
			int src_node = 0;  // arbitrary node, you should start at one of the fruits
			for(int a = 0;a<rs;a++)
			{
				//System.out.println(game.addRobot(src_node+a));
				game.addRobot(src_node+a);
			}
		}
		catch (JSONException e) {e.printStackTrace();}

		//the clock count down ,begin the game begin

		//while the game is live
		//		while(game.isRunning())
		//		{
		//			//move robot to next node, only a neighbor node
		//			game.chooseNextEdge(robot_id, node_dest_id);

		//			//move the robot to the next destination, what we set in chooseNextEdge
		//			game.move();

		//			//to know if a robot got to his destination we can check is dest if dest == -1 
		//			//the robot arrived to his destination
		//			
		//			if(System.currentTimeMillis() - first >= 1000)
		//			{	
		//				//shows the time to end game
		//				System.out.println(game.timeToEnd()/1000);//shows in seconds
		//				first = System.currentTimeMillis();
		//			}
		//			
		//		}

		// should be a Thread!!!

		new Thread( new Runnable() {


			@Override
			public void run()
			{
				game.startGame();


				while(game.isRunning())
				{
					game.chooseNextEdge(0, 5);
					moveRobots(game, DG);
					//window.repaint();
				}
				String results = game.toString();
				System.out.println("Game Over: "+results);
				game.stopGame();

			}
		}).start();


	}

	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 * @param log
	 */
	private static void moveRobots(game_service game, graph gg)
	{
		List<String> robots = game.getRobots();
		List<String> fruits = game.getFruits();
		List<Fruit> f = MyGameGUI.creatFruits(fruits);
		double maxValue = 0;
		double minPath = Double.POSITIVE_INFINITY;
		Fruit nextFruit = null;
		Robot moveRobot = null;
		if(robots.size() == 1)
		{
			Robot r = new Robot(robots.get(0));
			while(!f.isEmpty())
			{
				int rand = (int)(Math.random()*f.size());
				Fruit randFruit = f.get(rand);
				edge_data ed = Graph_Algo.onEdge(randFruit, gg);
				double path = Graph_Algo.shortestPathDist(r.getSrc(), ed.getSrc());
				if(maxValue < randFruit.getValue() && path < minPath)
				{
					maxValue = randFruit.getValue();
					minPath = path;
					nextFruit = randFruit;
				}
				f.remove(rand);
			}
			game.chooseNextEdge(r.getId(), ed.getDest());
		}
		else
		{
			List<Robot> robot = null;
			for (String r : robots) 
			{
				Robot ro = new Robot(r);
				robot.add(ro);
			}
			while(!f.isEmpty())
			{
				int rand = (int)(Math.random()*f.size());
				Fruit randFruit = f.get(rand);
				edge_data ed = Graph_Algo.onEdge(randFruit, gg);
				for(int i=0; i<robot.size(); i++)
				{
					double path = Graph_Algo.shortestPathDist(ed.getSrc(), robot.get(i).getSrc());
					if(maxValue < randFruit.getValue() && path < minPath)
					{
						maxValue = randFruit.getValue();
						minPath = path;
						nextFruit = randFruit;
						moveRobot = robot.get(i);
					}
				}
				f.remove(rand);
			}
			game.chooseNextEdge(moveRobot.getId(), ed.getDest());
		}
	}
	//		List<String> log = game.move();

	//		if(window!=null)
	//		{
	//			window.addRobots(log);
	//		}
	//		
	//		
	//		if(log!=null) 
	//		{
	//			long t = game.timeToEnd();
	//			for(int i=0;i<log.size();i++)
	//			{
	//				String robot_json = log.get(i);
	//				try 
	//				{
	//					JSONObject line = new JSONObject(robot_json);
	//					JSONObject ttt = line.getJSONObject("Robot");
	//					int rid = ttt.getInt("id");
	//					int src = ttt.getInt("src");
	//					int dest = ttt.getInt("dest");
	//
	//					if(dest==-1) 
	//					{	
	//						dest = nextNode(gg, src);
	//						game.chooseNextEdge(rid, dest);
	//						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
	//						System.out.println(ttt);
	//					}
	//				} 
	//				catch (JSONException e) {e.printStackTrace();}
	//			}
	//		}
	//	}
	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNode(graph g, int src) 
	{
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		Iterator<edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}

	//manual move robots
	//	private static int nextNodeM(graph g, int src)
	//	{
	//		;
	//	}

}

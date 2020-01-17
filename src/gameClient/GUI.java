package gameClient;
import dataStructure.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import sun.security.provider.certpath.Vertex;
import utils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument.Iterator;

import com.sun.glass.events.KeyEvent;

import algorithms.Graph_Algo;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

import dataStructure.*;
public class GUI extends JFrame implements ActionListener , MouseListener , Observer, Runnable
{
	graph graph; 
	Graph_Algo ga; 
	int Choice= 0 ; 
	public Collection<node_data> vertex;
	public Collection<edge_data> edges; 
	JFrame frame;
	Graph_Algo G = new Graph_Algo(); 
	List<node_data> vToPaint = new ArrayList<>();
	double shortPD = 0;

	public GUI() {}
	public GUI(graph g)
	{
		this.graph = g; 
		this.G.init(g);
		initGUI();
		((java.util.Observable) graph).addObserver(this);

	}
	private void initGUI()
	{
		this.setSize(1000, 800);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//set the main frame logo to "G"
		ImageIcon img = new ImageIcon("g.png");
		this.setIconImage(img.getImage());

		//creating the menu bar
		JMenuBar MB=new JMenuBar();this.setJMenuBar(MB);
		JMenu File = new JMenu("File");
		JMenu algo = new JMenu("algo");

		//creating the bar tabs

		MB.add(File);
		MB.add(algo);

		//file tab\\
		JMenuItem Save = new JMenuItem("Save");     
		Save.addActionListener(this);
		KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
		Save.setAccelerator(ctrlS);

		JMenuItem Load = new JMenuItem("Load"); 		
		Load.addActionListener(this);
		KeyStroke ctrlL = KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
		Load.setAccelerator(ctrlL);

		JMenuItem gfactory = new JMenuItem("gfactory");
		gfactory.addActionListener(this);
		KeyStroke ctrlG = KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
		gfactory.setAccelerator(ctrlG);


		//algo tab
		JMenuItem ShortPath = new JMenuItem("Shortest Path"); 
		ShortPath.addActionListener(this);
		KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
		ShortPath.setAccelerator(ctrlP);

		JMenuItem ShortPathDist = new JMenuItem("Shortest Path Dist"); 
		ShortPath.addActionListener(this);
		KeyStroke ctrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
		ShortPathDist.setAccelerator(ctrlD);


		JMenuItem isConnected = new JMenuItem("isConnected"); 
		isConnected.addActionListener(this);
		KeyStroke ctrlI = KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
		isConnected.setAccelerator(ctrlI);


		JMenuItem TSP = new JMenuItem("TSP");			
		TSP.addActionListener(this);
		KeyStroke ctrlT = KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
		TSP.setAccelerator(ctrlT);


		//adding to tabs;
		//file
		File.add(Save);
		File.add(Load);
		File.add(gfactory);

		//algo 
		algo.add(ShortPath); 
		algo.add(TSP);
		algo.add(isConnected);
		algo.add(ShortPathDist);
	}

	public void paint(Graphics g)
	{
		super.paint(g);

		ImageIcon pac  = new ImageIcon("pac1.png");
		Image image1 = pac.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		pac = new ImageIcon(image1);
		g.drawImage(image1, 100, 200, this);
		
		ImageIcon apple  = new ImageIcon("apple.png");
		Image image2 = apple.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		apple = new ImageIcon(image2);
		g.drawImage(image2, 210, 280, this);
		
		ImageIcon banana  = new ImageIcon("banana.png");
		Image image3 = banana.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		banana = new ImageIcon(image3);
		g.drawImage(image3, 350, 220, this);

		Collection<node_data> v = graph.getV();
		java.util.Iterator<node_data> i =  v.iterator(); 

		while(i.hasNext())
		{
			node_data temp = (node_data) i.next();
			Point3D nodes_src =temp.getLocation();

			g.setColor(Color.BLUE);
			g.fillOval((int)nodes_src.ix()-3, (int)nodes_src.iy()-5,10,10);
			g.drawString(Integer.toString(temp.getKey()),(int)nodes_src.x()-7,(int)(nodes_src.y())-7);


			this.edges = graph.getE(temp.getKey());
			if(this.edges == null) continue;
			java.util.Iterator<edge_data> j =  edges.iterator(); 


			while(j.hasNext())
			{
				//draw the edges line in Red
				g.setColor(Color.RED);

				edge temp1 = (edge) j.next();

				Point3D nodes_dest = graph.getNode(temp1.getDest()).getLocation();
				g.drawLine(temp.getLocation().ix(), temp.getLocation().iy(),
						nodes_dest.ix(), nodes_dest.iy());
				//draw a point of the edges direction in Yellow
				g.setColor(Color.YELLOW);
				int dir_of_edge_x=(int) ((nodes_src.x() + 4*nodes_dest.x())/5);
				int dir_of_edge_y=(int) ((nodes_src.y() + 4*nodes_dest.y())/5);

				g.fillOval(dir_of_edge_x-3 , dir_of_edge_y-5,10,10);

				//draw the edge weight in Black
				int mid_of_edge_x=(int) ((nodes_src.x()+nodes_dest.x())/2);
				int mid_of_edge_y=(int) ((nodes_src.y()+nodes_dest.y())/2);
				g.setColor(Color.BLACK);
				g.drawString(Double.toString((int)temp1.getWeight()),mid_of_edge_x, mid_of_edge_y);
			}
		}

		//shortest path distance
		if(Choice == 1)
		{
			g.setColor(Color.BLACK);

			if(shortPD != 0)
			{
				g.drawString("The Path Weight is: "+ shortPD, 300, 300);
			}
		}

		//isConnected function
		if(Choice == 2) 
		{
			g.setColor(Color.BLACK);
			if(G.isConnected())
			{
				g.drawString("The graph is connected", 100, 700);
			}
			else
			{
				g.drawString("The graph is NOT connected", 100, 700);
			}
			Choice = 0;
		}

		if(Choice == 3)
		{
			if(vToPaint.size() != 0)
			{
				for (int j = 0; j < vToPaint.size()-1; j++) 
				{
					g.setColor(Color.GREEN);
					double x1 = vToPaint.get(j).getLocation().x();
					double y1 = vToPaint.get(j).getLocation().y();
					double x2 = vToPaint.get(j+1).getLocation().x();
					double y2 = vToPaint.get(j+1).getLocation().y();
					g.fillOval((int)x1,(int) y1, 10, 10);
					g.drawLine((int)x1,(int) y1,(int) x2,(int) y2);
				} 
			}
			Choice = 0;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand().equals("Save"))
		{
			FileDialog chooser = new FileDialog(frame, "Use a .png or .jpg extension", FileDialog.SAVE);
			chooser.setVisible(true);
			String filename =chooser.getDirectory()+ chooser.getFile();
			if (filename != null)
			{
				G.save(filename);
				System.out.println("Graph Saved");
			}
		}

		if(e.getActionCommand().equals("Load")) 
		{
			Choice = 0;
			repaint();
			FileDialog chooser = new FileDialog(frame, "Use a .png or .jpg extension", FileDialog.LOAD);
			chooser.setVisible(true);
			String filename = chooser.getDirectory()+chooser.getFile();
			if (filename != null)
			{
				this.graph= init(filename);
				this.G.init(graph);
				Choice= 0; 
				repaint();
			}
		}

		if(e.getActionCommand().equals("gfactory"))
		{
			Choice = 0;
			this.graph = graphFactory();
			this.G.init(graph);
			Choice=0;
			repaint();
		}

		if(e.getActionCommand().equals("Shortest Path")) 
		{
			Choice = 0;
			repaint();

			vToPaint.clear();
			JFrame f1 = new JFrame();
			String v1 = JOptionPane.showInputDialog(f1,"Enter source id");
			int v_src = 0;
			int v_dest = 0;

			if(v1!="") 
			{
				String v2 = JOptionPane.showInputDialog(f1,"Enter destination id");
				if(v2!="") 
				{
					try
					{
						v_src = Integer.parseInt(v1);
						v_dest = Integer.parseInt(v2);
						if(v_src != v_dest && graph.getNode(v_src) != null&& graph.getNode(v_dest) != null)
						{							
							vToPaint.addAll(G.shortestPath(v_src, v_dest));
						}

					} catch(Exception ex) {};
				}
			}

			Choice = 3;
			repaint();
		}

		if(e.getActionCommand().equals("TSP")) 
		{
			Choice = 0;
			repaint();

			vToPaint.clear();
			List<Integer> tsp = new ArrayList<>();
			JFrame f1 = new JFrame();
			String s_TSP = "";
			while(!s_TSP.equals("done") && tsp.size()<graph.getV().size())
			{
				s_TSP = JOptionPane.showInputDialog(f1,"Enter a vertix for the TSP List or done to apply");
				try
				{
					int v_TSP = Integer.parseInt(s_TSP);
					if(graph.getNode(v_TSP) == null )
					{
						System.out.println("not a vertix");
					}
					else if(tsp.contains(v_TSP))
					{
						System.out.println("already in the TSP list");
					}
					else
					{
						tsp.add(v_TSP);
					}
				} catch(Exception ex)
				{
					System.out.println("illegal input!");
				}
			}

			try
			{				
				vToPaint = G.TSP(tsp);
			}catch(Exception ex)
			{
				System.out.println("Empty list");
			}


			Choice =3;
			repaint();

		}

		if(e.getActionCommand().equals("isConnected"))
		{
			Choice = 2;
			repaint();

		}

		if(e.getActionCommand().equals("Shortest Path Dist"))
		{
			Choice = 0;
			repaint();

			vToPaint.clear();
			JFrame f1 = new JFrame();
			String v1 = JOptionPane.showInputDialog(f1,"Enter source vertix");
			int v_src = 0;
			int v_dest = 0;

			if(v1!="") 
			{
				String v2 = JOptionPane.showInputDialog(f1,"Enter destination vertix");
				if(v2!="") 
				{
					try
					{
						v_src = Integer.parseInt(v1);
						v_dest = Integer.parseInt(v2);
						if(v_src != v_dest && graph.getNode(v_src) != null&& graph.getNode(v_dest) != null)
						{							
							shortPD = G.shortestPathDist(v_src, v_dest);
						}

					} catch(Exception ex) {};
				}
			}

			Choice = 1;
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public graph init(String file_name) 
	{
		try
		{    
			FileInputStream file = new FileInputStream(file_name); 
			ObjectInputStream objInput = new ObjectInputStream(file); 
			graph = (graph)objInput.readObject(); 
			objInput.close(); 
			file.close(); 
			return graph;
		} 
		catch(IOException e) 
		{ 
			e.printStackTrace();
		} 
		catch(ClassNotFoundException e) 
		{ 
			e.printStackTrace(); 
		}
		return null; 

	}
	//create random graph
	public graph graphFactory()
	{
		graph gr = new DGraph();
		int size = 10;

		for (int i = 0; i < size; i++) 
		{
			double p1 = Math.random()*500+70;
			double p2 = Math.random()*500+70;
			gr.addNode(new node(i,new Point3D(p1,p2),0));
		}

		for (int i = 0; i < size*3; i++) 
		{
			int src = (int)(Math.random()*size);
			int dest = (int)(Math.random()*size);
			if(src == dest && gr.getEdge(src, dest) != null)
			{
				break;
			}
			else
			{				
				gr.connect(src,dest, Math.random()*20+1);
			}
		}
		return gr;
	}
	
	/**
	 * 
	 * @param data denote some data to be scaled
	 * @param r_min the minimum of the range of your data
	 * @param r_max the maximum of the range of your data
	 * @param t_min the minimum of the range of your desired target scaling
	 * @param t_max the maximum of the range of your desired target scaling
	 * @return
	 */
	private double scale(double data, double r_min, double r_max,double t_min, double t_max)		
	{
		
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}

	@Override
	public void run()
	{
		repaint();
	}
	@Override
	public void update(java.util.Observable o, Object arg) 
	{
		repaint();
		run();

	}
}

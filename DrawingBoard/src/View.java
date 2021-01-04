import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.ArrayList;

public class View {
	private ArrayList<Shape> model;
	private Frame frame;
	private ColorPanel colorpanel;
	private ToolPanel toolpanel;
	private DrawPanel mainpanel;
	View(ArrayList<Shape> model) {
		this.model = model;
		frame = new Frame();
		colorpanel = new ColorPanel();
		toolpanel = new ToolPanel();
		mainpanel = new DrawPanel(model);
		mainpanel.setFocusable(true);
		frame.add(colorpanel);
		frame.add(toolpanel);
		frame.add(mainpanel);
		frame.setTitle("제목 없음");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(820, 820);
		frame.setVisible(true);
	}
	void addFrameListener(ActionListener action) {
		frame.addFrameListener(action);
	}
	void addColorListener(ActionListener action) {
		colorpanel.addColorListener(action);
	}
	void addToolListener(ActionListener action) {
		toolpanel.addToolListener(action);
	}
	void addkeyListener(KeyListener K) {
		mainpanel.addKeyListener(K);
	}
	void setMainPanelML(MouseInputListener L) {
		mainpanel.setML(L);
	}
	void repaint() {
		mainpanel.repaint();
		colorpanel.repaint();
	}
	void setfillcol(Color color) {
		colorpanel.setfillcol(color);
	}
	void setbordercol(Color color) {
		colorpanel.setbordercol(color);
	}
	void settitle(String str) {
		frame.setTitle(str);
	}
}
class Frame extends JFrame {
	private Font font;
	private JMenuBar menubar;
	private JMenu menu[];
	private JMenuItem menuitem[][];
	private String menustr[];
	private String menuitemstr[][];
	Frame() {
		font = new Font("serif", Font.PLAIN, 25);
		menubar = new JMenuBar();
		menu = new JMenu[4];
		menuitem = new JMenuItem[3][7];
		menustr = new String[] { "  File  ", "  Draw  ", "Thickness" };
		menuitemstr = new String[][] {
			    { "New        ", "Open", "Save", "Exit" },
				{ "Pen", "Line", "Rect              ", "Oval", "Triangle", "Rhombus" },
				{ "1pt                        ", "2pt", "3pt", "4pt" }
				};
		for (int i = 0; i < menuitemstr.length; i++) {
			for (int j = 0; j < menuitemstr[i].length; j++)
				menuitem[i][j] = new JMenuItem(menuitemstr[i][j]);
		}
		for (int i = 0; i < menustr.length; i++) {
			menu[i] = new JMenu(menustr[i]);
			for (int j = 0; j < menuitemstr[i].length; j++) {
				menu[i].add(menuitem[i][j]);
				menu[i].setFocusable(false);
			}
			menu[i].setFont(font);
			menubar.add(menu[i]);
		}
			this.setLayout(null);
		menubar.setBounds(0, 0, 280, 70);
		this.add(menubar);
		
	}
	public void addFrameListener(ActionListener action) {
		for (int i = 0; i < menuitem.length; i++)
			for (int j = 0; menuitem[i][j]!=null; j++)
				menuitem[i][j].addActionListener(action);
	}
}
class ColorPanel extends JPanel {
	private Color fillcol, bordercol;
	private Font font;
	private JButton colbutton[];
	private String colorstr[];
	ColorPanel() {
		fillcol = Color.black;
		bordercol = Color.green;
		font = new Font("serif", Font.PLAIN, 12);
		colbutton = new JButton[3];
		colorstr = new String[]{ "FillCol", "X", "BorderCol" };
		
		for(int i=0;i<colbutton.length;i++) {
			colbutton[i] = new JButton(colorstr[i]);
			colbutton[i].setFont(font);
			this.add(colbutton[i]);
		}
		colbutton[0].setBounds(0, 0, 70, 20);
		colbutton[1].setBounds(70, 0, 50, 20);
		colbutton[2].setBounds(0, 35, 120, 20);
		colbutton[0].setFocusable(false);
		colbutton[1].setFocusable(false);
		colbutton[2].setFocusable(false);
		this.setLayout(null);
		this.setBounds(280, 0, 120, 70);
	}
	public void paintComponent(Graphics g) {
		g.setColor(fillcol);
		g.fillRect(0, 20, 120, 15);
		g.setColor(bordercol);
		g.fillRect(0, 55, 120, 15);
	}	
	public void setfillcol(Color color) {
		fillcol=color;
	}
	public void setbordercol(Color color) {
		bordercol=color;
	}
	public void addColorListener(ActionListener action) {
		for(int i=0;i<colbutton.length;i++)
			colbutton[i].addActionListener(action);
	}
}
class ToolPanel extends JPanel{
	private Font font;
	private JButton funcbutton[];
	private String funcstr[];
	ToolPanel(){
		font = new Font("serif", Font.PLAIN, 15);
		funcbutton = new JButton[8];
		funcstr = new String[]{ "Select", "Multi", "Delete", "Copy", "Front", "Back", "Group", "UnGroup"};
		
		for(int i=0, j=0;i<funcbutton.length;i++) {
			funcbutton[i] = new JButton(funcstr[i]);
			if(i%2==0)
				j=0;
			else
				j=35;
			funcbutton[i].setFont(font);
			funcbutton[i].setBounds((i/2)*100,j,100,35);
			funcbutton[i].setFocusable(false);
			this.add(funcbutton[i]);
			
		}
		this.setLayout(null);
		this.setBounds(400,0,400,70);
	}
	public void addToolListener(ActionListener action) {
		for(int i=0;i<funcbutton.length;i++) {
			funcbutton[i].addActionListener(action);
		}
	}
}
class DrawPanel extends JPanel {
	ArrayList<Shape> mylist = null;
	DrawPanel(ArrayList<Shape> input) {
		mylist = input;
		this.setBounds(0,90,800,700);
		this.setBackground(new Color(255, 255, 255));
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!mylist.isEmpty()) {
			for (Shape temp : mylist) {
				temp.draw((Graphics2D)g);
			}
			for (Shape temp : mylist) {
				temp.select((Graphics2D) g);
			}
		}
	}	
	void setML(MouseInputListener L) {
		MouseListener[] mouseListeners = this.getMouseListeners();
		for(MouseListener temp : mouseListeners) {
		 this.removeMouseListener(temp);
		}
		MouseMotionListener[] mouseMotionListeners = this.getMouseMotionListeners();
		for(MouseMotionListener temp : mouseMotionListeners) {
		 this.removeMouseMotionListener(temp);
		}
		this.addMouseListener(L);
		this.addMouseMotionListener(L);
	}
}
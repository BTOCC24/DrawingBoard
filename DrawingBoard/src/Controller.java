import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

public class Controller {
	private ArrayList<Shape> model;
	private View view; 
	private boolean multi = false;
	private int pinx[],piny[]; //rotate 후 resize하기 위한 점.
	private int stroke; //default 값
	private Color fillcol, bordercol; //default 값
	private Shape selected_shape;	
	Controller(ArrayList<Shape> model, View view) {
		this.model = model;
		this.view = view;
		fillcol = Color.black;
		bordercol = Color.green;
		stroke=1;
		pinx = new int[3];
		piny = new int[3];
		selected_shape = null;
		view.addFrameListener(new FrameBT());
		view.addColorListener(new ColorBT());
		view.addToolListener(new ToolBT());
		view.addkeyListener(new KeyL());
	}
	class FrameBT implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Pen") || e.getActionCommand().equals("Line") || e.getActionCommand().equals("Rect              ") || e.getActionCommand().equals("Oval") || e.getActionCommand().equals("Triangle") || e.getActionCommand().equals("Rhombus")) {
				resetselect();
				view.setMainPanelML(new CreateShapeML());
			}
			if (e.getActionCommand().equals("Pen")) {
				selected_shape = new Pen(-1,-1,-1,-1, stroke, bordercol);
			} else if (e.getActionCommand().equals("Line")) {
				selected_shape = new Line(-1,-1,-1,-1, stroke, bordercol);
			} else if (e.getActionCommand().equals("Rect              ")) {
				selected_shape = new Rect(-1,-1,-1,-1, stroke, bordercol, fillcol);
			} else if (e.getActionCommand().equals("Oval")) {
				selected_shape = new Oval(-1,-1,-1,-1, stroke, bordercol, fillcol);
			} else if (e.getActionCommand().equals("Triangle")) {
				selected_shape = new Triangle(-1,-1,-1,-1, stroke, bordercol, fillcol);
			} else if (e.getActionCommand().equals("Rhombus")) {
				selected_shape = new Rhombus(-1,-1,-1,-1, stroke, bordercol, fillcol);
			}else if (e.getActionCommand().equals("1pt                        ") || e.getActionCommand().equals("2pt") || e.getActionCommand().equals("3pt")|| e.getActionCommand().equals("4pt")) {
				if (selected_shape != null) 
					selected_shape.setstroke(((int)e.getActionCommand().charAt(0)-48)*2-1);
				else 
					stroke = ((int)e.getActionCommand().charAt(0)-48)*2-1;
			}else if(e.getActionCommand().equals("Save")) {
				FileIO temp = new FileIO();
				temp.save();
			}else if(e.getActionCommand().equals("Open")) {
				FileIO temp = new FileIO();
				temp.open();
			}else if (e.getActionCommand().equals("New        ")) {
				model.clear();
			}else if(e.getActionCommand().equals("Exit")) {
				System.exit(0);
			}
			view.repaint();
		}
	}
	class ColorBT implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("BorderCol")) {
				Color color = JColorChooser.showDialog(null, "Color", Color.YELLOW);
				if(color !=null) {
					if(selected_shape!=null) {selected_shape.setbordercol(color);}
					else {
						view.setbordercol(color);
						bordercol = color;
					}
				}	
			}else if(e.getActionCommand().equals("FillCol")) {
				Color color = JColorChooser.showDialog(null, "Color", Color.YELLOW);
				if(color !=null) {
					if(selected_shape!=null && selected_shape instanceof Shape2D) {
						Shape2D temp = (Shape2D)selected_shape;
						temp.nofill=false;
						temp.setfillcol(color);
						}
					else {
						view.setfillcol(color);
						fillcol = color;
					}
				}
			}else if(e.getActionCommand().equals("X")) {
				if(selected_shape!=null && selected_shape instanceof Shape2D) {
					Shape2D temp = (Shape2D)selected_shape;
					temp.nofill = !temp.nofill;
				}
			}
			view.repaint();
		}
	}
	class ToolBT implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Select")) {
				resetselect();
				multi = false;
				view.setMainPanelML(new TransformML());
			} else if (e.getActionCommand().equals("Delete")) {
				int size = model.size();
				for(int i=size-1;i>=0;i--) {
					if(model.get(i).select)
						model.remove(i);
				}
				selected_shape=null;
			} else if (e.getActionCommand().equals("Multi")) {
				multi = true;
				resetselect();
			} else if (e.getActionCommand().equals("Group")) {
				if (multi) {
					ArrayList<Shape> group = new ArrayList<Shape>();
					for (int i = 0; i < model.size(); i++) {
						if (model.get(i).select) {
							group.add(model.get(i));
							model.remove(i);
							i--;
						}
					}
					if (group.size() > 0) {
						model.add(new Group(group));
						selected_shape = model.get(model.size() - 1);
						selected_shape.select = true;
						view.repaint();
					}
				}
			} else if (e.getActionCommand().equals("UnGroup")) {
				if(!multi && selected_shape != null && selected_shape instanceof Group) {
					Group temp = (Group)selected_shape;
					for(int i=0;i<temp.getgroup().size();i++) {
						model.add(temp.getgroup().get(i));
						temp.getgroup().get(i).select=true;
						temp.getgroup().get(i).setangle(temp.angle+temp.getgroup().get(i).angle);
					}
					model.remove(selected_shape);
				}
			} else if (e.getActionCommand().equals("Copy")) {
				int size = model.size();
				for (int i = 0; i < size; i++) {
					if (model.get(i).select) {
						try {
							Shape temp = (Shape) model.get(i).clone();
							temp.move(temp.getxpos() + 10, temp.getypos() + 10);
							model.add(temp);
							model.get(i).select = false;
							model.get(model.size() - 1).select = true;
							selected_shape=model.get(model.size()-1);
						} catch (Exception P) {P.printStackTrace();};
					}
				}
			} else if (e.getActionCommand().equals("Front")) {
				for (int i = model.size() - 2; i >= 0; i--) 
					if (model.get(i).select) 
						Collections.swap(model, i, i + 1);
			} else if (e.getActionCommand().equals("Back")) {
				for (int i = 1; i < model.size(); i++) 
					if (model.get(i).select) 
						Collections.swap(model, i, i - 1);
			}
			view.repaint();
		}
	}
	abstract class MouseL implements MouseInputListener {
		public void mousePressed(MouseEvent e) {}
		public void mouseDragged(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
	}
	class CreateShapeML extends MouseL {
		public void mousePressed(MouseEvent e) {
			model.add(selected_shape);
			selected_shape.select=true;
			selected_shape.move(e.getX(), e.getY());
		}
		public void mouseDragged(MouseEvent e) {
			if(selected_shape instanceof Pen) {
				Pen temp = (Pen)selected_shape;
				temp.addpos(e.getX(),e.getY());
			}else {
				selected_shape.resize(e.getX()-selected_shape.getxpos(), e.getY()-selected_shape.getypos());
			}
			view.repaint();
		}
		public void mouseReleased(MouseEvent e) {
			view.setMainPanelML(new TransformML());
		}
	}
	class TransformML extends MouseL{
		public void mousePressed(MouseEvent e) {
			pinx[0] = e.getX();piny[0]=e.getY();
			for(int i=model.size()-1;i>=0;i--) {
				if(selected_shape != null && selected_shape.able_resize(e.getX(), e.getY())){
					pinx[1] = selected_shape.getwidth();piny[1] = selected_shape.getheight();
					view.setMainPanelML(new ResizeML());
					break;
				}else if (selected_shape != null && selected_shape.able_rotate(e.getX(),e.getY())) {
					view.setMainPanelML(new RotateML());
					break;
				}else if(model.get(i).inArea(e.getX(), e.getY())){
					if(!multi)
						resetselect();
					model.get(i).select=true;
					selected_shape = model.get(i);
					break;
				}else if(i==0) {
					resetselect();
				}
			}
			view.repaint();
		}
		public void mouseDragged(MouseEvent e) {
			if ((selected_shape) != null) {
				selected_shape.move(selected_shape.getxpos() + e.getX() - pinx[0],
						selected_shape.getypos() + e.getY() - piny[0]);
				pinx[0] = e.getX();
				piny[0] = e.getY();
				view.repaint();
			}
		}
	}
	class ResizeML extends MouseL{
		public void mouseDragged(MouseEvent e) {
			int dw = e.getX() - pinx[0];
			int dh = e.getY() - piny[0];
			int rw = (int)(dw*Math.cos(Math.toRadians(-selected_shape.angle))-(dh*Math.sin(Math.toRadians(-selected_shape.angle))));
			int rh = (int)(dw*Math.sin(Math.toRadians(-selected_shape.angle))+(dh*Math.cos(Math.toRadians(-selected_shape.angle))));
			selected_shape.resize(pinx[1] + rw, piny[1] + rh);
			view.repaint();
		}	
		public void mouseReleased(MouseEvent e) {
			view.setMainPanelML(new TransformML());
		}
	}
	class RotateML extends MouseL{
		public void mouseDragged(MouseEvent e) {
			pinx[0] = e.getX() - pinx[0];
			piny[0] = e.getY() - piny[0];
			selected_shape.setangle(360-((Math.atan2(e.getX() - selected_shape.getcenterX(), e.getY() - selected_shape.getcenterY())
							* 180 / Math.PI + 180) % 360));
			pinx[0] = e.getX();
			piny[0] = e.getY();
			view.repaint();
		}	
		public void mouseReleased(MouseEvent e) {
			view.setMainPanelML(new TransformML());
		}
	}		 
	class StickML extends MouseL{
		private Shape temp=null;
		private Point tempP1 = new Point(), tempP2 = new Point(), tempP3 = new Point(), setP = new Point();
		int px, py;

		public void mousePressed(MouseEvent e) {
			for (int i = model.size() - 1; i >= 0; i--) { // 위치될 도형 찾기
				if (model.get(i).inArea(e.getX(), e.getY()) && selected_shape!=null && model.get(i) != selected_shape) {
					temp = model.get(i);
					px = temp.getrotateP(e.getX(), e.getY(), temp.getcenterX(), temp.getcenterY(), -temp.getangle()).x;
					py = temp.getrotateP(e.getX(), e.getY(), temp.getcenterX(), temp.getcenterY(), -temp.getangle()).y;
					if (px > temp.getcenterX()) {
						if (py > temp.getcenterY()) {
							setP.x = temp.getxpos() + temp.getwidth() - selected_shape.getwidth();
							setP.y = temp.getypos() + temp.getheight();
						} else {
							setP.x = temp.getxpos() + temp.getwidth();
							setP.y = temp.getypos();
						}
					} else {
						if (py > temp.getcenterY()) {
							setP.x = temp.getxpos() - selected_shape.getwidth();
							setP.y = temp.getypos() + temp.getheight() - selected_shape.getheight();
						} else {
							setP.x = temp.getxpos();
							setP.y = temp.getypos() - selected_shape.getheight();
						}
					}
					break;
				}
			}

			if (selected_shape!=null && temp!=null && !multi) {
				selected_shape.setangle(0);
				tempP1.x = temp.getrotateP(setP.x, setP.y, temp.getcenterX(), temp.getcenterY(), temp.getangle()).x;//우측상단 돌린좌표
				tempP1.y = temp.getrotateP(setP.x, setP.y, temp.getcenterX(), temp.getcenterY(), temp.getangle()).y;
				selected_shape.move(tempP1.x,tempP1.y);
				tempP2.x = temp.getrotateP(tempP1.x+selected_shape.getwidth(), tempP1.y+selected_shape.getheight(), tempP1.x,tempP1.y, temp.getangle()).x;
				tempP2.y = temp.getrotateP(tempP1.x+selected_shape.getwidth(), tempP1.y+selected_shape.getheight(), tempP1.x,tempP1.y, temp.getangle()).y;
				tempP3.x = temp.getrotateP(tempP1.x, tempP1.y, (tempP1.x+tempP2.x)/2, (tempP1.y+tempP2.y)/2, -temp.getangle()).x;
				tempP3.y = temp.getrotateP(tempP1.x, tempP1.y, (tempP1.x+tempP2.x)/2, (tempP1.y+tempP2.y)/2, -temp.getangle()).y;
				selected_shape.move(tempP3.x, tempP3.y);
				selected_shape.setangle(temp.getangle());
				pinx[0] = e.getX();
				piny[0] = e.getY();
			}
			view.repaint();
		}
	}
	class KeyL implements KeyListener{
		public void keyPressed(KeyEvent K) {
			if(K.getKeyCode() == 17) {
				view.setMainPanelML(new StickML());
			}
		}
		public void keyReleased(KeyEvent K) {
			view.setMainPanelML(new TransformML());
		}
		public void keyTyped(KeyEvent K) {
		}
	}
	class FileIO{
		String name;
		FileIO(){	
		}
		void open() {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("C:\\"));
			int ret=chooser.showOpenDialog(null);
            if(ret!=JFileChooser.APPROVE_OPTION){
                return;
            }
			try {
				FileInputStream in = new FileInputStream(chooser.getSelectedFile().getName());
				ObjectInputStream iin = new ObjectInputStream(in);
				ArrayList<Shape> temp  = (ArrayList<Shape>)iin.readObject();
				model.clear();
				for(int i=0;i<temp.size();i++)
					model.add(temp.get(i));
				iin.close();
			}catch(Exception E) {E.printStackTrace();}
			view.settitle(chooser.getSelectedFile().getName());
		}

		void save() {
			JFrame imsi = new JFrame();
			JLabel label = new JLabel("  input filename to save");
			JButton button = new JButton("save");
			JTextField field = new JTextField(20);

			label.setBounds(0, 0, 200, 30);
			field.setBounds(0, 35, 200, 30);
			button.setBounds(0, 70, 200, 30);

			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					name = field.getText();
					field.setText("");
					if (name != "")
						name += ".ser";
					try {
						FileOutputStream out = new FileOutputStream(name);
						ObjectOutputStream oout = new ObjectOutputStream(out);
						resetselect();
						oout.writeObject(model);
						oout.close();
						imsi.setVisible(false);
					} catch (Exception P) {};
				}
			});
			imsi.add(label);
			imsi.add(button);
			imsi.add(field);

			imsi.setLayout(null);
			imsi.setSize(200, 150);
			imsi.setVisible(true);
			imsi.setLocation(200, 200);

		}
	}
	public void resetselect() {
		for(int i = 0; i<model.size();i++) {
			model.get(i).select=false;
		}
		selected_shape=null;
		view.repaint();
	}
}

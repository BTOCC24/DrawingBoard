import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

abstract public class Shape implements Cloneable, Serializable{
	protected boolean select;
	protected int xpos, ypos, width, height, stroke;
	protected double angle;
	protected Color bordercol;
	Shape(int xpos,int ypos,int width, int height, int stroke, Color bordercol){
		this.xpos=xpos;
		this.ypos=ypos;
		this.width =width;
		this.height=height;
		this.stroke=stroke;
		this.bordercol=bordercol;
		select=false;
		angle=0;
	}
	abstract boolean inArea(int x, int y);
	void draw(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(stroke));
		g2d.rotate(Math.toRadians(angle),getcenterX(),getcenterY());
	}
	void move(int x, int y) {
		xpos = x;
		ypos = y;
	}
	void resize(int w, int h) {
		if(w <=0) {
			width = 0;
		}else {
			width =w;
		}
		if (h <= 0) {
			height = 0;
		}else {
			height = h;
		}
	}
	void select(Graphics2D g2d) {
		if(select) {
			Stroke temp = g2d.getStroke();
			g2d.rotate(Math.toRadians(angle),getcenterX(),getcenterY());
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.RED);
			g2d.fillOval(xpos+width, ypos+height, 16, 16);
			g2d.drawRect(xpos - 5, ypos - 5, width + 10, height + 10);
			g2d.setColor(Color.BLUE);
			g2d.drawLine(xpos + width / 2, ypos, xpos + width / 2, ypos - 52);
			g2d.fillOval(xpos + width / 2 - 8, ypos - 60, 16, 16);
			g2d.rotate(Math.toRadians(-angle),getcenterX(),getcenterY());
			g2d.setStroke(temp);
		}
	}
	void setbordercol(Color color) {
		bordercol = color;
	}
	void setstroke(int n) {
		stroke = n;
	}
	void setangle(double angle) {
		this.angle = angle;
	}
	int getxpos() {
		return xpos;
	}
	int getypos() {
		return ypos;
	}
	int getwidth() {
		return width;
	}
	int getheight() {
		return height;
	}
	int getcenterX() {
		return xpos + width / 2;
	}
	int getcenterY() {
		return ypos + height / 2;
	}
	double getangle() {
		return angle;
	}
	Point getrotateP(int x,int y,int cx, int cy,double angle) {
		Point temp = new Point();
		temp.x=(int)((x-cx)*Math.cos(Math.toRadians(angle))-((y-cy)*Math.sin(Math.toRadians(angle)))+cx);
		temp.y=(int)((x-cx)*Math.sin(Math.toRadians(angle))+((y-cy)*Math.cos(Math.toRadians(angle)))+cy);
		return temp;
	}
	public Object clone() throws CloneNotSupportedException {return super.clone();}
	boolean able_resize(int x, int y) {
		int tempx=x;
		int tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		if(Math.pow(xpos+width+8-x, 2)+Math.pow(ypos+height+8-y, 2)<=64)
			return true;
		return false;
	}
	boolean able_rotate(int x, int y) {
		int tempx=x;
		int tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		if(Math.pow(xpos + width /2 -x, 2)+Math.pow(ypos - 52 - y, 2)<=64)
			return true;
		return false;		
	}
}
abstract class Shape2D extends Shape{
	protected boolean nofill;
	protected Color fillcol;
	Shape2D(int xpos,int ypos,int width,int height,int stroke,Color bordercol,Color fillcol){
		super(xpos,ypos,width,height,stroke,bordercol);
		this.fillcol=fillcol;
		nofill=false;
	}
	void setfillcol(Color color) {
		fillcol = color;
	}
	void setnofill() {
		nofill = !nofill;
	}
}

class Pen extends Shape{
	private ArrayList<Integer> original_xpos = new ArrayList<Integer>();
	private ArrayList<Integer> original_ypos = new ArrayList<Integer>();
	private ArrayList<Integer> listxpos = new ArrayList<Integer>();
	private ArrayList<Integer> listypos = new ArrayList<Integer>();
	private int original_width, original_height;
	Pen(int xpos,int ypos,int width, int height, int stroke, Color bordercol){
		super(xpos,ypos,width,height,stroke,bordercol);
		original_width = width;
		original_height = height;
	}
	boolean inArea(int x, int y) {
		int tempx=x, tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		for (int i = 0; i < listxpos.size() - 1; i++) {
			if (Math.pow((x - listxpos.get(i)), 2) + Math.pow((y - listypos.get(i)), 2) <= 100) {
				return true;
			}
		}
		return false;
	}
	void draw(Graphics2D g2d) {
		super.draw(g2d);
		g2d.setColor(bordercol);
		for(int i=0;i<listxpos.size()-2;i++) {
			g2d.drawLine(listxpos.get(i), listypos.get(i), listxpos.get(i+1), listypos.get(i+1));
		}
		g2d.rotate(Math.toRadians(-angle),getcenterX(),getcenterY());
	}
	void move(int x,int y) {
		for(int i=0;i<=listxpos.size()-1;i++) {
			listxpos.set(i, listxpos.get(i)+x-xpos);
			listypos.set(i, listypos.get(i)+y-ypos);
			original_xpos.set(i,original_xpos.get(i)+x-xpos);
			original_ypos.set(i,original_ypos.get(i)+y-ypos);
		}
		if(xpos == -1) {
			xpos=x;
			ypos=y;
			addpos(x,y);
		}
		xpos = x;
		ypos = y;
	}
	void resize(int w, int h) {
		int dist;
		double ratio;
		if(w <=0) {
			width = 0;
		}else {
			width =w;
		}
		if (h <= 0) {
			height = 0;
		}else {
			height = h;
		}
		for(int i=0; i<original_xpos.size();i++) {
			int temp = original_xpos.get(i);
			dist = temp - xpos;
			ratio = (double)dist*width/original_width;
			temp = (int)(xpos+Math.round(ratio));
			listxpos.set(i,temp);
			temp = original_ypos.get(i);
			dist = temp - ypos;
			ratio = (double)dist*height/original_height;
			temp = (int)(ypos+Math.round(ratio));
			listypos.set(i,temp);
		}
	}
	void addpos(int x,int y) {
		original_xpos.add(x);
		original_ypos.add(y);
		listxpos.add(x);
		listypos.add(y);
		if(xpos > x) {width+=xpos-x;xpos = x;}
		if(ypos > y) {height+=ypos-y;ypos = y;}
		if(x-xpos>width) {width=x-xpos;}
		if(y-ypos>height) {height=y-ypos;}
		original_width=width;
		original_height=height;
	}
}

class Line extends Shape{
	int lux,luy,rdx,rdy;
	Line(int xpos,int ypos,int width, int height, int stroke, Color bordercol){
		super(xpos,ypos,width,height,stroke,bordercol);
	}
	boolean inArea(int x, int y) {
		int tempx=x, tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		if (x > lux && x < rdx && y > luy && y < rdy) {
			double m = Math.abs((double) (height) / (width));
			double k = Math.abs((double) (y - ypos) / (x - xpos));
			if (Math.abs(m - k) < 0.2)
				return true;
			return false;
		}
		return false;
	}	
	void draw(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(stroke));
		g2d.rotate(Math.toRadians(angle),(lux+rdx)/2,(luy+rdy)/2);
		g2d.setColor(bordercol);
		g2d.drawLine(xpos,ypos,xpos+width,ypos+height);
		g2d.rotate(Math.toRadians(-angle),(lux+rdx)/2,(luy+rdy)/2);
	}
	void select(Graphics2D g2d) {
		if(select) {
			lux=xpos;
			luy=ypos;
			rdx=xpos+width;
			rdy=ypos+height;
			if(width<0) {
				lux=xpos+width;
				rdx=xpos;
			}
			if(height<0) {
				luy=ypos+height;
				rdy=ypos;
			}
			Stroke temp = g2d.getStroke();
			g2d.setStroke(new BasicStroke(3));
			g2d.rotate(Math.toRadians(angle),(lux+rdx)/2,(luy+rdy)/2);
			g2d.setColor(Color.RED);
			g2d.drawRect(lux, luy, rdx-lux, rdy-luy);
			g2d.fillOval(rdx, rdy, 16, 16);
			g2d.setColor(Color.BLUE);
			g2d.drawLine(lux+(rdx - lux)/2, luy, lux+(rdx - lux)/2, luy - 52);
			g2d.fillOval(lux + (rdx-lux)/2 - 8, luy - 60, 16, 16);
			g2d.rotate(Math.toRadians(-angle),(lux+rdx)/2,(luy+rdy)/2);
			g2d.setStroke(temp);
		}
	}
	void resize(int w, int h) {
		if(w<0)
			w=0;
		width = w;
		height = h;
	}	
	boolean able_resize(int x, int y) {
		int tempx,tempy;
		tempx = x;
		tempy = y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		if(Math.pow(rdx+8-x, 2)+Math.pow(rdy+8-y, 2)<=64)
			return true;
		return false;
	}
	boolean able_rotate(int x, int y) {
		int tempx,tempy;
		tempx=x;tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		if(Math.pow(lux + width /2 -x, 2)+Math.pow(luy - 52 - y, 2)<=64)
			return true;
		return false;		
	}
}

class Rect extends Shape2D{
	Rect(int xpos,int ypos,int width,int height,int stroke,Color bordercol,Color fillcol){
		super(xpos, ypos, width, height, stroke, bordercol, fillcol);
	}
	boolean inArea(int x, int y) {
		int tempx=x, tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		return (x > xpos && x < xpos + width && y > ypos && y < ypos + height);
	}
	void draw(Graphics2D g2d) {
		super.draw(g2d);
		if (!nofill) {
			g2d.setColor(fillcol);
			g2d.fillRect(xpos, ypos, width, height);
		}
		g2d.setColor(bordercol);
		g2d.drawRect(xpos, ypos, width, height);
		g2d.rotate(Math.toRadians(-angle),getcenterX(),getcenterY());
	}
}

class Oval extends Shape2D{	
	Oval(int xpos,int ypos,int width,int height,int stroke,Color bordercol,Color fillcol){
		super(xpos, ypos, width, height, stroke, bordercol, fillcol);
	}
	boolean inArea(int x, int y) {
		int tempx=x, tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		return (Math.pow((double) (x - getcenterX()), 2) / Math.pow((double) width / 2, 2)
				+ (Math.pow((double) (y - getcenterY()), 2) / Math.pow((double) height / 2, 2)) <= 1);
	}
	void draw(Graphics2D g2d) {
		super.draw(g2d);
		if (!nofill) {
			g2d.setColor(fillcol);
			g2d.fillOval(xpos, ypos, width, height);
		}
		g2d.setColor(bordercol);
		g2d.drawOval(xpos, ypos, width, height);
		g2d.rotate(Math.toRadians(-angle),getcenterX(),getcenterY());
	}
}

class Triangle extends Shape2D{
	private int xpoint[] = new int[3];
	private int ypoint[] = new int[3];
	Triangle(int xpos,int ypos,int width,int height,int stroke,Color bordercol,Color fillcol){
		super(xpos, ypos, width, height, stroke, bordercol, fillcol);
	}
	boolean inArea(int x, int y) {
		boolean val = false;
		int tempx=x, tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		double m, k;
		if (x <= xpos + (width / 2) && x >= xpos && y >= ypos && y <= ypos + height) {
			m = (double) (height) / (width / 2);
			k = (double) (ypos + height - y) / (x - xpos);
			if (m - k >= 0 && k > 0)
				val = true;
			else
				val = false;
		} else if (x >= xpos + (width / 2) && x <= xpos + width && y >= ypos && y <= ypos + height) {
			m = -(double) (height) / (width / 2);
			k = -(double) (ypos + height - y) / (xpos + width - x);
			if (m <= k)
				val = true;
			else
				val = false;
		}
		return val;
	}
	void draw(Graphics2D g2d) {
		super.draw(g2d);
		xpoint[0]=xpos; xpoint[1]=xpos+width/2; xpoint[2]=xpos+width;
		ypoint[0]=ypos+height; ypoint[1]=ypos; ypoint[2]=ypos+height;
		
		if (!nofill) {
			g2d.setColor(fillcol);
			g2d.fillPolygon(xpoint, ypoint, 3);
		}
		g2d.setColor(bordercol);
		g2d.drawPolygon(xpoint, ypoint, 3);
		g2d.rotate(Math.toRadians(-angle),getcenterX(),getcenterY());
	}
}

class Rhombus extends Shape2D{
	private int xpoint[] = new int[4];
	private int ypoint[] = new int[4];
	Rhombus(int xpos,int ypos,int width,int height,int stroke,Color bordercol,Color fillcol){
		super(xpos, ypos, width, height, stroke, bordercol, fillcol);
	}
	boolean inArea(int x, int y) {
		boolean val = false;
		int tempx=x, tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		double m = ((double) height / 2) / ((double) width / 2);
		double k;
		if (x >= xpos && x <= xpos + width && y >= ypos && y <= ypos + height) {
			if (x < xpos + width / 2) {
				if (y < ypos + height / 2) { // 4
					k = (double) (ypos + (double) height / 2 - y) / (x - xpos);
					if (m >= k && k >= 0)
						val = true;
					else
						val = false;
				} else { // 3
					m = -m;
					k = -(double) (ypos + height - y) / (xpos + width - x);
					if (m <= k)
						val = true;
					else
						val = false;
				}
			} else {
				if (y < ypos + (double) height / 2) { // 1
					m = -m;
					k = -(double) (ypos + (double) height / 2 - y) / (xpos + width - x);
					if (k <= 0 && k >= m)
						val = true;
					else
						val = false;
				} else { // 2
					k = (double) (ypos + height - y) / (x - (xpos + (double) width / 2));
					if (k >= m)
						val = true;
					else
						val = false;
				}
			}
		}
		return val;
	}
	void draw(Graphics2D g2d) {
		super.draw(g2d);
		xpoint[0] = xpos; xpoint[1] = xpos + width/2; xpoint[2] = xpos + width; xpoint[3] = xpos + width/2;
		ypoint[0] = ypos + height/2; ypoint[1] = ypos; ypoint[2] = ypos + height/2; ypoint[3] = ypos + height;
		if (!nofill) {
			g2d.setColor(fillcol);
			g2d.fillPolygon(xpoint, ypoint, 4);
		}
		g2d.setColor(bordercol);
		g2d.drawPolygon(xpoint, ypoint, 4);
		g2d.rotate(Math.toRadians(-angle),getcenterX(),getcenterY());
	}
}

class Group extends Shape2D{
	private ArrayList<Shape> group;
	
	private ArrayList<Shape> original_group;
	private int o_xpos,o_ypos,o_width,o_height;
	Group(ArrayList<Shape> group){
		super(0, 0, 0, 0, 0, null, null);
		this.group=group;
		original_group = new ArrayList<Shape>();
		Point[] temp = getboundary(group.get(0).getxpos(),group.get(0).getypos(),group.get(0).getwidth(),group.get(0).getheight(),group.get(0).angle);
		o_xpos = xpos = temp[0].x;
		o_ypos = ypos = temp[0].y;
		o_width = width = temp[1].x;
		o_height = height = temp[1].y;
		for(int i=1;i<group.size();i++) {
			int x1,y1,x2,y2;
			temp = getboundary(group.get(i).getxpos(),group.get(i).getypos(),group.get(i).getwidth(),group.get(i).getheight(),group.get(i).angle);
			x1=temp[0].x;
			y1=temp[0].y;
			x2=temp[1].x;
			y2=temp[1].y;
			if(x1<=xpos)
				o_xpos = xpos = x1;
			if (y1 <= ypos)
				o_ypos = ypos = y1;
			if (x2 > width)
				width = x2;
			if (y2 > height)
				height = y2;
			if(group.get(i).getxpos()<=xpos)
				o_xpos = xpos = group.get(i).getxpos();
			if (group.get(i).getypos() <= ypos)
				o_ypos = ypos = group.get(i).getypos();
			if (group.get(i).getxpos() + group.get(i).getwidth() > width)
				width = group.get(i).getxpos() + group.get(i).getwidth();
			if (group.get(i).getypos() + group.get(i).getheight() > height)
				height = group.get(i).getypos() + group.get(i).getheight();
		}
		o_width = width = width - xpos;
		o_height = height = height - ypos;
		select = true;
		for(int i=0;i<group.size();i++) {
			try {
			original_group.add((Shape)group.get(i).clone());
			}catch(Exception e) {}
		}
	}
	boolean inArea(int x, int y) {
		int tempx=x, tempy=y;
		x=(int)((tempx-getcenterX())*Math.cos(Math.toRadians(-angle))-((tempy-getcenterY())*Math.sin(Math.toRadians(-angle)))+getcenterX());
		y=(int)((tempx-getcenterX())*Math.sin(Math.toRadians(-angle))+((tempy-getcenterY())*Math.cos(Math.toRadians(-angle)))+getcenterY());
		
		for(int i=0;i<group.size();i++) {
			if(group.get(i).inArea(x, y))
				return true;
		}
		return false;
	}
	void draw(Graphics2D g2d) {
		super.draw(g2d);
		for(int i=0;i<group.size();i++)
			group.get(i).draw(g2d);
		g2d.rotate(Math.toRadians(-angle),getcenterX(),getcenterY());
	}
	void move(int x, int y) {
		for(int i=0;i<group.size();i++)
			group.get(i).move(x+group.get(i).xpos-xpos,y+group.get(i).ypos-ypos);
		xpos= x;
		ypos= y;
		
	}
	void resize(int w, int h) {
		if(w<1) {w=1;}
		if(h<1) {h=1;}
		for(int i=0;i<group.size();i++) {
			group.get(i).move(xpos+w*(original_group.get(i).getxpos() - o_xpos)/o_width,ypos+h*(original_group.get(i).getypos() - o_ypos)/o_height);
			group.get(i).resize(w*original_group.get(i).getwidth()/o_width, h*original_group.get(i).getheight()/o_height);
		}
		width=w;
		height=h;
	}
	Point[] getboundary(int x,int y,int w,int h,double angle) {
		int tx[] = new int[4], ty[]=new int[4];
		Point temp[] = new Point[2];
		temp[0] = new Point();
		temp[1] = new Point();
		
		tx[0]=getrotateP(x,y,x+w/2,y+h/2,angle).x;
		ty[0]=getrotateP(x,y,x+w/2,y+h/2,angle).y;
		tx[1]=getrotateP(x+w,y,x+w/2,y+h/2,angle).x;
		ty[1]=getrotateP(x+w,y,x+w/2,y+h/2,angle).y;
		tx[2]=getrotateP(x+w,y+h,x+w/2,y+h/2,angle).x;
		ty[2]=getrotateP(x+w,y+h,x+w/2,y+h/2,angle).y;
		tx[3]=getrotateP(x,y+h,x+w/2,y+h/2,angle).x;
		ty[3]=getrotateP(x,y+h,x+w/2,y+h/2,angle).y;
		
		temp[0].x=tx[0];temp[0].y=ty[0];temp[1].x=tx[0];temp[1].y=ty[0];
		for(int i=1;i<4;i++) {
			if(tx[i]<temp[0].x)
				temp[0].x=tx[i];
			if(ty[i]<temp[0].y)
				temp[0].y=ty[i];
			if(tx[i]>temp[1].x)
				temp[1].x=tx[i];
			if(ty[i]>temp[1].y)
				temp[1].y=ty[i];
		}
		return temp;
	}
	void setbordercol(Color color) {
		for(int i=0;i<group.size();i++)
			group.get(i).setbordercol(color);
	}
	void setfillcol(Color color) {
		for(int i=0;i<group.size();i++) {
			if(group.get(i) instanceof Shape2D) {
				Shape2D temp = (Shape2D)group.get(i);
				temp.nofill=false;
				temp.setfillcol(color);
			}
		}
	}
	public ArrayList<Shape> getgroup() {
		return group;
	}
	public Object clone() {
		ArrayList<Shape> temp = new ArrayList<Shape>();
		for(int i=0;i<group.size();i++) {
			try {
			temp.add((Shape)group.get(i).clone());
			}catch(Exception P) {};
		}
		Group imsi = new Group(temp);
		imsi.setangle(angle);
		return imsi; 
	}
}
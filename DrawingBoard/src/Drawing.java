import java.util.ArrayList;
public class Drawing {
	public static void main(String[] args) {
		ArrayList<Shape> model = new ArrayList<Shape>();
		View view = new View(model);
		Controller controller = new Controller(model,view);
	}
}

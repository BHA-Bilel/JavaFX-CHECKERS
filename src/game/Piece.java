package game;

import static game.GameApp.TILE_SIZE;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Piece extends StackPane {

	private int x, y;
	private PieceType type = PieceType.Normal;
	private final PieceColor color;
	private Text text;

	public Piece(PieceColor color, int x, int y) {
		this.color = color;
		this.x = x;
		this.y = y;
		Ellipse bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
		bg.setFill(Color.BLACK);

		bg.setStroke(Color.BLACK);
		bg.setStrokeWidth(TILE_SIZE * 0.03);

		bg.setTranslateX(bg.getTranslateX() + 5);
		bg.setTranslateY(bg.getTranslateY() + 5);

		Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
		if (color == PieceColor.RED)
			ellipse.setFill(Color.valueOf("#c40003"));
		else
			ellipse.setFill(Color.valueOf("#fff9f4"));

		ellipse.setStroke(Color.BLACK);
		ellipse.setStrokeWidth(TILE_SIZE * 0.03);

		getChildren().addAll(bg, ellipse);

	}

	public boolean isRed() {
		return color == PieceColor.RED;
	}

	public boolean isWhite() {
		return color == PieceColor.WHITE;
	}

	// GETTERS SETTERS

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public PieceType getType() {
		return type;
	}

	public void setType(PieceType type) {
		this.type = type;
		if (type == PieceType.King) {
			text = new Text("K");
			text.setFont(Font.font(30));
			getChildren().add(text);
		}
	}

	public PieceColor getPieceColor() {
		return color;
	}

}

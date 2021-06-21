package bg.checkers.game;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Piece extends StackPane {

    private int x, y;
    private PieceType type;
    private final PieceColor color;

    public Piece(Tile tile, PieceColor color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
        type = PieceType.Normal;
        Ellipse bg = new Ellipse();
        bg.radiusXProperty().bind(tile.widthProperty().divide(3));
        bg.radiusYProperty().bind(tile.heightProperty().divide(3));
        bg.setFill(Color.BLACK);
        bg.setStroke(Color.BLACK);
        bg.strokeWidthProperty().bind(tile.heightProperty().multiply(0.03));
        bg.translateXProperty().bind(tile.widthProperty().multiply(.05));
        bg.translateYProperty().bind(tile.heightProperty().multiply(.05));

        Ellipse ellipse = new Ellipse();
        ellipse.radiusXProperty().bind(tile.widthProperty().divide(3));
        ellipse.radiusYProperty().bind(tile.heightProperty().divide(3));
        ellipse.setFill(Paint.valueOf(color == PieceColor.RED ? "#c40003" : "#fff9f4"));
        ellipse.setStroke(Color.BLACK);
        ellipse.strokeWidthProperty().bind(tile.heightProperty().multiply(0.03));
        getChildren().addAll(bg, ellipse);
    }

    public void setKingType() {
        type = PieceType.King;
        Text text = new Text("K");
        text.setFont(Font.font(30));
        getChildren().add(text);
    }

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

    public PieceColor getPieceColor() {
        return color;
    }

}

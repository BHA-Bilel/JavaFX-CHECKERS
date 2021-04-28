package game;

import static game.GameApp.TILE_SIZE;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane {

    private final int x, y;
    private final Rectangle border;
    private Piece piece;
    private final Handler handler;
    private Paint defaultFill;

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        if (piece != null) {
            piece.setX(x);
            piece.setY(y);
            getChildren().add(piece);
        } else if (hasPiece()) {
            getChildren().remove(this.piece);
        }
        this.piece = piece;
    }

    public Tile(Handler handler, boolean light, int x, int y) {
        this.x = x;
        this.y = y;
        border = new Rectangle(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        this.handler = handler;
        border.setWidth(TILE_SIZE);
        border.setHeight(TILE_SIZE);
        border.setFill(light ? Color.valueOf("#feb") : Color.valueOf("#582"));
        getChildren().add(border);
        if (!light) {
            defaultFill = Color.valueOf("#582");
            setOnMousePressed(e -> {
                if (!handler.getGame().isPlayable() || !handler.getGame().isYourTurn()
                        || (hasPiece() && !handler.getGame().canImove(getPiece()))) {
                    return;
                }
                if (handler.getGame().getSelectedTile() == null) {
                    if (hasPiece()) {
                        handler.getGame().selectTile(this);
                        setSelected(true);
                    }
                } else if (handler.getGame().getSelectedTile() == this) {
                    handler.getGame().selectTile(null);
                    setSelected(false);
                } else {
                    if (hasPiece()) {
                        handler.getGame().getSelectedTile().setSelected(false);
                        handler.getGame().selectTile(null);
                        handler.getGame().selectTile(this);
                        setSelected(true);
                    }
                    if (!handler.getGame().isLegal(x, y))
                        return;
                    play(getX(), getY());
                }
            });
        }
    }

    private void setSelected(boolean isSelected) {
        if (isSelected) {
            border.setFill(Color.GOLD);
        } else {
            border.setFill(defaultFill);
        }
    }

    public void play(int x, int y) {
        Piece selectedPiece = handler.getGame().getSelectedTile().getPiece();
        handler.getGame().getSelectedTile().setSelected(false);
        boolean isAttackMove = handler.getGame().isAttackMove(x, y);
        if (isAttackMove) {
            int dx = (selectedPiece.getX() < x) ? -1 : 1;
            int dy = (selectedPiece.getY() < y) ? -1 : 1;
            handler.getGame().getBoard()[x + dx][y + dy].setPiece(null);
        }
        if ((y == 0 || y == 7) && selectedPiece.getType() != PieceType.King) {
            selectedPiece.setType(PieceType.King);
        }
        handler.getGame().getCSC().sendCoor(selectedPiece, x, y, isAttackMove);
        handler.getGame().getBoard()[selectedPiece.getX()][selectedPiece.getY()].setPiece(null);
        handler.getGame().getBoard()[x][y].setPiece(selectedPiece);
        if (handler.getGame().NothingHappened()) {
            handler.getGame().waitForYourTurn();
        }
        handler.getGame().setYourTurn(false);
    }

    public void play(Piece selectedPiece, int x, int y, boolean isAttackMove) {
        if (isAttackMove) {
            int dx = (selectedPiece.getX() < x) ? -1 : 1;
            int dy = (selectedPiece.getY() < y) ? -1 : 1;
            handler.getGame().getBoard()[x + dx][y + dy].setPiece(null);
        }
        handler.getGame().getBoard()[selectedPiece.getX()][selectedPiece.getY()].setPiece(null);
        handler.getGame().getBoard()[x][y].setPiece(selectedPiece);
        if ((y == 0 || y == 7) && selectedPiece.getType() != PieceType.King) {
            selectedPiece.setType(PieceType.King);
        }
        if (handler.getGame().NothingHappened()) {
            handler.getGame().setYourTurn(true);
        }
    }

    // GETTERS SETTERS

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setHighlighted(MoveType type, boolean highlight) {
        if (highlight) {
            if (type == MoveType.Attack)
                border.setFill(Color.RED);
            else
                border.setFill(Color.SILVER);
        } else {
            border.setFill(defaultFill);
        }
    }

}
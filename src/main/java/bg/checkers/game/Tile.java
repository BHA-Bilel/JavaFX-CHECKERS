package bg.checkers.game;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

public class Tile extends StackPane {

    private final GameApp gameApp;
    private final int x, y;
    private final Region border;
    private Piece piece;
    private Background default_bg, gold_bg, silver_bg, red_bg;

    public Tile(GameApp gameApp, boolean light, int x, int y) {
        this.gameApp = gameApp;
        this.x = x;
        this.y = y;
        border = new Region();
        border.setBackground(new Background(new BackgroundFill(Paint.valueOf(light ? "#feb" : "#582"),
                null, null)));
        border.prefWidthProperty().bind(gameApp.heightProperty().divide(8));
        border.prefHeightProperty().bind(gameApp.heightProperty().divide(8));
        getChildren().add(border);
        if (!light) {
            default_bg = new Background(new BackgroundFill(Paint.valueOf("#582"), null, null));
            gold_bg = new Background(new BackgroundFill(Paint.valueOf("gold"), null, null));
            silver_bg = new Background(new BackgroundFill(Paint.valueOf("silver"), null, null));
            red_bg = new Background(new BackgroundFill(Paint.valueOf("red"), null, null));
            setOnMousePressed(e -> {
                if (!gameApp.isPlayable() || !gameApp.isYourTurn()
                        || (hasPiece() && !gameApp.canImove(getPiece()))) {
                    return;
                }
                if (gameApp.getSelectedTile() == null) {
                    if (hasPiece()) {
                        gameApp.selectTile(this);
                        setSelected(true);
                    }
                } else if (gameApp.getSelectedTile() == this) {
                    gameApp.selectTile(null);
                    setSelected(false);
                } else {
                    if (hasPiece()) {
                        gameApp.getSelectedTile().setSelected(false);
                        gameApp.selectTile(null);
                        gameApp.selectTile(this);
                        setSelected(true);
                    }
                    if (!gameApp.isLegal(x, y))
                        return;
                    play(getX(), getY());
                }
            });
        }
    }

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

    private void setSelected(boolean isSelected) {
        if (isSelected) {
            border.setBackground(gold_bg);
        } else {
            border.setBackground(default_bg);
        }
    }

    public void play(int x, int y) {
        Piece selectedPiece = gameApp.getSelectedTile().getPiece();
        gameApp.getSelectedTile().setSelected(false);
        boolean isAttackMove = gameApp.isAttackMove(x, y);
        if (isAttackMove) {
            int dx = (selectedPiece.getX() < x) ? -1 : 1;
            int dy = (selectedPiece.getY() < y) ? -1 : 1;
            gameApp.getBoard()[x + dx][y + dy].setPiece(null);
        }
        if ((y == 0 || y == 7) && selectedPiece.getType() != PieceType.King) {
            selectedPiece.setKingType();
        }
        gameApp.sendCoor(selectedPiece, x, y, isAttackMove);
        gameApp.getBoard()[selectedPiece.getX()][selectedPiece.getY()].setPiece(null);
        gameApp.getBoard()[x][y].setPiece(selectedPiece);
        if (gameApp.NothingHappened()) {
            gameApp.waitForYourTurn();
        }
        gameApp.setYourTurn(false);
    }

    public void play(Piece selectedPiece, int x, int y, boolean isAttackMove) {
        if (isAttackMove) {
            int dx = (selectedPiece.getX() < x) ? -1 : 1;
            int dy = (selectedPiece.getY() < y) ? -1 : 1;
            gameApp.getBoard()[x + dx][y + dy].setPiece(null);
        }
        gameApp.getBoard()[selectedPiece.getX()][selectedPiece.getY()].setPiece(null);
        gameApp.getBoard()[x][y].setPiece(selectedPiece);
        if ((y == 0 || y == 7) && selectedPiece.getType() != PieceType.King) {
            selectedPiece.setKingType();
        }
        if (gameApp.NothingHappened()) {
            gameApp.setYourTurn(true);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setHighlighted(MoveType type, boolean highlight) {
        if (highlight) {
            if (type == MoveType.Attack)
                border.setBackground(red_bg);
            else
                border.setBackground(silver_bg);
        } else {
            border.setBackground(default_bg);
        }
    }

}
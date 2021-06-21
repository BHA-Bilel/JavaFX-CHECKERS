package bg.checkers.game;

import bg.checkers.lang.Language;
import bg.checkers.popup.MyAlert;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameApp extends GridPane {

    private int playerID;
    private final GameClient gameClient;

    List<Tile> NormalMoves = new ArrayList<>();
    List<Tile> AttackMoves = new ArrayList<>();
    private final Tile[][] board = new Tile[8][8];
    private boolean yourTurn, playable = false;
    private Tile selectedTile;
    private final String yourName, opName;
    public int parties_won, parties_lost;
    private MyAlert results_alert;

    public GameApp(Socket gameSocket, String name, String opName) {
        this.yourName = name;
        this.opName = opName;
        gameClient = new GameClient(gameSocket);
        gameClient.handShake();
        setAlignment(Pos.CENTER);
        createGUI();
    }

    private void createGUI() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Tile tile = new Tile(this, (x + y) % 2 == 0, x, y);
                GridPane.setHalignment(tile, HPos.CENTER);
                GridPane.setFillHeight(tile, true);
                add(tile, x, y);
                board[x][y] = tile;
                Piece piece = null;
                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = new Piece(tile, PieceColor.RED, x, y);
                }
                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = new Piece(tile, PieceColor.WHITE, x, y);
                }
                if (piece != null) {
                    tile.setPiece(piece);
                }
            }
        }
    }

    public boolean isLegal(int x, int y) {
        return NormalMoves.contains(board[x][y]) || AttackMoves.contains(board[x][y]);
    }

    public boolean NothingHappened() {
        int redPieces = 0, whitePieces = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[x][y].hasPiece()) {
                    if (board[x][y].getPiece().getPieceColor() == PieceColor.RED) {
                        redPieces++;
                    } else {
                        whitePieces++;
                    }
                }
            }
        }
        boolean youWon = false;

        if (playerID == 1 && whitePieces == 0 || playerID == 2 && redPieces == 0) {
            parties_won++;
            youWon = true;
            playable = false;
        } else if (playerID == 2 && whitePieces == 0 || playerID == 1 && redPieces == 0) {
            parties_lost++;
            playable = false;
            youWon = false;
        }
        if (!playable) {
            startNewGame(youWon);
            showResults(false);
            return false;
        }
        return true;
    }

    public void showResults(boolean shortcut) {
        if (results_alert != null && results_alert.isShowing())
            if (shortcut) return;
            else results_alert.close();
        Platform.runLater(() -> {
            String text = yourName + " : " + parties_won + "\n";
            text += opName + " : " + parties_lost + "\n";
            if (results_alert == null) results_alert = new MyAlert(Alert.AlertType.INFORMATION, Language.GR_H, text);
            else results_alert.update(text);
            results_alert.show();
        });
    }

    private void startNewGame(boolean youWon) {
        Platform.runLater(() -> {
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    Piece piece = null;
                    if (y <= 2 && (x + y) % 2 != 0) {
                        piece = new Piece(board[x][y], PieceColor.RED, x, y);
                    }
                    if (y >= 5 && (x + y) % 2 != 0) {
                        piece = new Piece(board[x][y], PieceColor.WHITE, x, y);
                    }
                    board[x][y].setPiece(null);
                    board[x][y].setPiece(piece);
                }
            }
            if (!youWon) {
                waitForYourTurn();
            }
            setYourTurn(youWon);
            setPlayable(true);
        });
    }

    public void selectTile(Tile selectedTile) {
        this.selectedTile = selectedTile;
        highlightPossibleMoves(selectedTile != null);
    }

    private void highlightPossibleMoves(boolean highlight) {
        if (highlight) {
            if (selectedTile.getPiece().getType() == PieceType.King) {
                // BOTTOM RIGHT
                int x = selectedTile.getX() + 1;
                int y = selectedTile.getY() + 1;
                while (x < 8 && y < 8 && !board[x][y].hasPiece()) {
                    NormalMoves.add(board[x][y]);
                    x++;
                    y++;
                }
                if (x < 7 && y < 7 && board[x][y].hasPiece()
                        && board[x][y].getPiece().getPieceColor() != selectedTile.getPiece().getPieceColor()) {
                    if (!board[x + 1][y + 1].hasPiece()) {
                        AttackMoves.add(board[x + 1][y + 1]);
                    }
                }
                // BOTTOM LEFT
                x = selectedTile.getX() - 1;
                y = selectedTile.getY() + 1;
                while (x > -1 && y < 8 && !board[x][y].hasPiece()) {
                    NormalMoves.add(board[x][y]);
                    x--;
                    y++;
                }
                if (x > 0 && y < 7 && board[x][y].hasPiece()
                        && board[x][y].getPiece().getPieceColor() != selectedTile.getPiece().getPieceColor()) {
                    if (!board[x - 1][y + 1].hasPiece()) {
                        AttackMoves.add(board[x - 1][y + 1]);
                    }
                }
                // TOP LEFT
                x = selectedTile.getX() - 1;
                y = selectedTile.getY() - 1;
                while (x > -1 && y > -1 && !board[x][y].hasPiece()) {
                    NormalMoves.add(board[x][y]);
                    x--;
                    y--;
                }
                if (x > 0 && y > 0 && board[x][y].hasPiece()
                        && board[x][y].getPiece().getPieceColor() != selectedTile.getPiece().getPieceColor()) {
                    if (!board[x - 1][y - 1].hasPiece()) {
                        AttackMoves.add(board[x - 1][y - 1]);
                    }
                }
                // TOP RIGHT
                x = selectedTile.getX() + 1;
                y = selectedTile.getY() - 1;
                while (y > -1 && x < 8 && !board[x][y].hasPiece()) {
                    NormalMoves.add(board[x][y]);
                    y--;
                    x++;
                }
                if (y > 0 && x < 7 && board[x][y].hasPiece()
                        && board[x][y].getPiece().getPieceColor() != selectedTile.getPiece().getPieceColor()) {
                    if (!board[x + 1][y - 1].hasPiece()) {
                        AttackMoves.add(board[x + 1][y - 1]);
                    }
                }
            } else {
                if (selectedTile.getPiece().getPieceColor() == PieceColor.RED) {
                    // BOTTOM RIGHT
                    int x = selectedTile.getX() + 1;
                    int y = selectedTile.getY() + 1;
                    if (x < 8 && y < 8 && !board[x][y].hasPiece()) {
                        NormalMoves.add(board[x][y]);
                    } else if (x < 7 && y < 7 && board[x][y].hasPiece()
                            && board[x][y].getPiece().getPieceColor() != selectedTile.getPiece().getPieceColor()) {
                        if (!board[x + 1][y + 1].hasPiece()) {
                            AttackMoves.add(board[x + 1][y + 1]);
                        }
                    }
                    // BOTTOM LEFT
                    x = selectedTile.getX() - 1;
                    y = selectedTile.getY() + 1;
                    if (x > -1 && y < 8 && !board[x][y].hasPiece()) {
                        NormalMoves.add(board[x][y]);
                    } else if (x > 0 && y < 7 && board[x][y].hasPiece()
                            && board[x][y].getPiece().getPieceColor() != selectedTile.getPiece().getPieceColor()) {
                        if (!board[x - 1][y + 1].hasPiece()) {
                            AttackMoves.add(board[x - 1][y + 1]);
                        }
                    }
                } else {
                    // TOP LEFT
                    int x = selectedTile.getX() - 1;
                    int y = selectedTile.getY() - 1;
                    if (x > -1 && y > -1 && !board[x][y].hasPiece()) {
                        NormalMoves.add(board[x][y]);
                    } else if (x > 0 && y > 0 && board[x][y].hasPiece()
                            && board[x][y].getPiece().getPieceColor() != selectedTile.getPiece().getPieceColor()) {
                        if (!board[x - 1][y - 1].hasPiece()) {
                            AttackMoves.add(board[x - 1][y - 1]);
                        }
                    }
                    // TOP RIGHT
                    x = selectedTile.getX() + 1;
                    y = selectedTile.getY() - 1;
                    if (y > -1 && x < 8 && !board[x][y].hasPiece()) {
                        NormalMoves.add(board[x][y]);
                    } else if (y > 0 && x < 7 && board[x][y].hasPiece()
                            && board[x][y].getPiece().getPieceColor() != selectedTile.getPiece().getPieceColor()) {
                        if (!board[x + 1][y - 1].hasPiece()) {
                            AttackMoves.add(board[x + 1][y - 1]);
                        }
                    }
                }
            }

            for (Tile tile : NormalMoves) {
                tile.setHighlighted(MoveType.Normal, true);
            }
            for (Tile tile : AttackMoves) {
                tile.setHighlighted(MoveType.Attack, true);
            }
        } else {
            for (Tile tile : NormalMoves) {
                tile.setHighlighted(MoveType.Normal, false);
            }
            for (Tile tile : AttackMoves) {
                tile.setHighlighted(MoveType.Attack, false);
            }
            NormalMoves.clear();
            AttackMoves.clear();
        }
    }

    public boolean isAttackMove(int x, int y) {
        return AttackMoves.contains(board[x][y]);
    }

    public void waitForYourTurn() {
        Thread t = new Thread(() -> {
            Object[] coor = gameClient.receive();
            int pieceX = (int) coor[0];
            int pieceY = (int) coor[1];
            Piece selectedPiece = board[pieceX][pieceY].getPiece();
            int toX = (int) coor[2];
            int toY = (int) coor[3];
            boolean isAttackMove = (boolean) coor[4];
            Platform.runLater(() -> board[pieceX][pieceY].play(selectedPiece, toX, toY, isAttackMove));
        });
        t.start();
    }

    public synchronized void closeGameApp() {
        gameClient.closeConn();
        Platform.runLater(() -> getChildren().clear());
    }

    public void sendCoor(Piece selectedPiece, int x, int y, boolean isAttackMove) {
        gameClient.sendCoor(selectedPiece, x, y, isAttackMove);
    }

    class GameClient {
        private Socket gameSocket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public GameClient(Socket gameSocket) {
            try {
                this.gameSocket = gameSocket;
                dataIn = new DataInputStream(gameSocket.getInputStream());
                dataOut = new DataOutputStream(gameSocket.getOutputStream());
            } catch (IOException ignore) {
            }
        }

        public void handShake() {
            try {
                playerID = dataIn.readInt();
                if (playerID == 1) {
                    yourTurn = true;
                    Thread t = new Thread(() -> {
                        try {
                            playable = dataIn.readBoolean();
                        } catch (IOException ignore) {
                        }
                    });
                    t.start();
                } else {
                    yourTurn = false;
                    playable = true;
                    waitForYourTurn();
                }
            } catch (IOException ignore) {
            }
        }

        public void sendCoor(Piece selectedPiece, int x, int y, boolean isAttackMove) {
            try {
                dataOut.writeInt(selectedPiece.getX());
                dataOut.writeInt(selectedPiece.getY());
                dataOut.writeInt(x);
                dataOut.writeInt(y);
                dataOut.writeBoolean(isAttackMove);
                dataOut.flush();
                selectTile(null);
            } catch (IOException ignore) {
            }
        }

        public Object[] receive() {
            Object[] coor = new Object[5];
            try {
                coor[0] = dataIn.readInt();
                coor[1] = dataIn.readInt();
                coor[2] = dataIn.readInt();
                coor[3] = dataIn.readInt();
                coor[4] = dataIn.readBoolean();
            } catch (IOException ignore) {
            }
            return coor;
        }

        public void closeConn() {
            try {
                dataOut.close();
                dataIn.close();
                gameSocket.close();
            } catch (IOException ignore) {
            }
        }
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean isPlayable) {
        this.playable = isPlayable;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public boolean canImove(Piece piece) {
        return (playerID == 1 && piece.getPieceColor() == PieceColor.RED
                || playerID == 2 && piece.getPieceColor() == PieceColor.WHITE);
    }

}

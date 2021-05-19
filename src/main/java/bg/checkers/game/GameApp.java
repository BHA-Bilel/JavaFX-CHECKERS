package bg.checkers.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import bg.checkers.room.RoomApp;
import shared.RoomPosition;

public class GameApp extends GridPane {

    public static final double TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private int playerID;
    private final ClientSideConnection gameClient;

    List<Tile> NormalMoves = new ArrayList<>();
    List<Tile> AttackMoves = new ArrayList<>();
    private final Tile[][] board = new Tile[WIDTH][HEIGHT];
    private final Handler handler;
    private boolean yourTurn, playable = false;
    private Tile selectedTile;
    private final String yourName, opName;
    public int parties_won, parties_lost;

    public GameApp(Socket gameSocket, String name, String opName) {
        this.yourName = name;
        this.opName = opName;
        gameClient = new ClientSideConnection(gameSocket);
        gameClient.handShake();
        handler = new Handler(this);
//        setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        Platform.runLater(() -> {
            getChildren().clear();
            setAlignment(Pos.CENTER);
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    Tile tile = new Tile(handler, (x + y) % 2 == 0, x, y);
                    GridPane.setHalignment(tile, HPos.CENTER);
                    board[x][y] = tile;
                    add(tile, x, y);

                    Piece piece = null;

                    if (y <= 2 && (x + y) % 2 != 0) {
                        piece = new Piece(PieceColor.RED, x, y);
                    }

                    if (y >= 5 && (x + y) % 2 != 0) {
                        piece = new Piece(PieceColor.WHITE, x, y);
                    }

                    if (piece != null) {
                        tile.setPiece(piece);
                    }
                }
            }
        });
    }

    public boolean isLegal(int x, int y) {
        return NormalMoves.contains(board[x][y]) || AttackMoves.contains(board[x][y]);
    }

    public boolean NothingHappened() {
        int redPieces = 0, whitePieces = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
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
            showResults();
        }
        return playable;
    }

    public void showResults() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game");
            alert.setHeaderText("Results");
            String text = yourName + " : " + parties_won + "\n";
            text += opName + " : " + parties_lost + "\n";
            alert.setContentText(text);
            alert.show();
        });
    }

    private void startNewGame(boolean youWon) {
        Platform.runLater(() -> {
            getChildren().clear();
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    Tile tile = new Tile(handler, (x + y) % 2 == 0, x, y);
                    GridPane.setHalignment(tile, HPos.CENTER);
                    add(tile, x, y);
                    board[x][y] = tile;

                    Piece piece = null;

                    if (y <= 2 && (x + y) % 2 != 0) {
                        piece = new Piece(PieceColor.RED, x, y);
                    }

                    if (y >= 5 && (x + y) % 2 != 0) {
                        piece = new Piece(PieceColor.WHITE, x, y);
                    }

                    if (piece != null) {
                        tile.setPiece(piece);
                    }
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
                        x++;
                        y++;
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
                        x--;
                        y++;
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
                        x--;
                        y--;
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
                        y--;
                        x++;
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

    class ClientSideConnection {
        private Socket gameSocket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection(Socket gameSocket) {
            try {
                this.gameSocket = gameSocket;
                dataIn = new DataInputStream(gameSocket.getInputStream());
                dataOut = new DataOutputStream(gameSocket.getOutputStream());
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

        public void handShake() {

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

    // GETTERS SETTERS

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

    public ClientSideConnection getCSC() {
        return gameClient;
    }

    public boolean canImove(Piece piece) {
        return (playerID == 1 && piece.getPieceColor() == PieceColor.RED
                || playerID == 2 && piece.getPieceColor() == PieceColor.WHITE);
    }

}

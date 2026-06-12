package com.akash.connectfour;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Controller implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    private static final double CELL_SIZE = 64.0;
    private static final double DISC_RADIUS = 26.0;
    private static final Color BOARD_COLOR = Color.web("#1E5FA8");
    private static final Color EMPTY_SLOT_COLOR = Color.web("#D9F7F0");
    private static final Color PLAYER_ONE_COLOR = Color.web("#E74C3C");
    private static final Color PLAYER_TWO_COLOR = Color.web("#F1C40F");

    @FXML
    private GridPane rootGridPane;

    @FXML
    private Pane insertedDiscsPane;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Button resetButton;

    private final ConnectFourBoard board = new ConnectFourBoard();
    private final Circle[][] discNodes = new Circle[ConnectFourBoard.ROWS][ConnectFourBoard.COLS];

    private int currentPlayer = 1;
    private boolean gameOver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildBoardView();
        resetButton.setOnAction(event -> resetGame());
        updatePlayerLabel();
        LOGGER.info("Connect 4 controller initialized");
    }

    private void buildBoardView() {
        insertedDiscsPane.getChildren().clear();

        double boardWidth = ConnectFourBoard.COLS * CELL_SIZE;
        double boardHeight = ConnectFourBoard.ROWS * CELL_SIZE;

        Rectangle background = new Rectangle(boardWidth, boardHeight, BOARD_COLOR);
        background.setArcWidth(16);
        background.setArcHeight(16);
        insertedDiscsPane.getChildren().add(background);

        for (int row = 0; row < ConnectFourBoard.ROWS; row++) {
            for (int column = 0; column < ConnectFourBoard.COLS; column++) {
                double centerX = (column * CELL_SIZE) + (CELL_SIZE / 2);
                double centerY = (row * CELL_SIZE) + (CELL_SIZE / 2);

                Circle slot = new Circle(centerX, centerY, DISC_RADIUS, EMPTY_SLOT_COLOR);
                insertedDiscsPane.getChildren().add(slot);

                Circle disc = new Circle(centerX, centerY, DISC_RADIUS, Color.TRANSPARENT);
                disc.setVisible(false);
                discNodes[row][column] = disc;
                insertedDiscsPane.getChildren().add(disc);
            }
        }

        for (int column = 0; column < ConnectFourBoard.COLS; column++) {
            final int selectedColumn = column;
            Rectangle clickArea = new Rectangle(
                    selectedColumn * CELL_SIZE,
                    0,
                    CELL_SIZE,
                    boardHeight
            );
            clickArea.setFill(Color.TRANSPARENT);
            clickArea.setOnMouseClicked(event -> handleColumnClick(selectedColumn, event));
            insertedDiscsPane.getChildren().add(clickArea);
        }

        insertedDiscsPane.setPrefSize(boardWidth, boardHeight);
        rootGridPane.setPrefSize(boardWidth + 240, boardHeight + 40);
    }

    private void handleColumnClick(int column, MouseEvent event) {
        if (gameOver) {
            return;
        }

        if (board.isColumnFull(column)) {
            showInfo("Column Full", "That column is already full. Choose another column.");
            return;
        }

        int row = board.dropDisc(column, currentPlayer);
        if (row < 0) {
            showInfo("Invalid Move", "Unable to place a disc in the selected column.");
            return;
        }

        animateDiscDrop(row, column, currentPlayer);

        if (board.hasWinningMove(row, column, currentPlayer)) {
            gameOver = true;
            showInfo("Game Over", playerName(currentPlayer) + " wins!");
            LOGGER.info(() -> "Player " + currentPlayer + " won the game");
            return;
        }

        if (board.isBoardFull()) {
            gameOver = true;
            showInfo("Game Over", "It's a draw!");
            LOGGER.info("Game ended in a draw");
            return;
        }

        currentPlayer = currentPlayer == 1 ? 2 : 1;
        updatePlayerLabel();
    }

    private void animateDiscDrop(int targetRow, int column, int player) {
        Circle disc = discNodes[targetRow][column];
        disc.setFill(colorForPlayer(player));
        disc.setVisible(true);

        double startY = -DISC_RADIUS;
        double endY = (targetRow * CELL_SIZE) + (CELL_SIZE / 2);
        disc.setCenterY(startY);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> disc.setCenterY(startY)),
                new KeyFrame(Duration.millis(180 + (targetRow * 45L)), event -> disc.setCenterY(endY))
        );
        timeline.play();
    }

    private void resetGame() {
        board.reset();
        gameOver = false;
        currentPlayer = 1;

        for (int row = 0; row < ConnectFourBoard.ROWS; row++) {
            for (int column = 0; column < ConnectFourBoard.COLS; column++) {
                Circle disc = discNodes[row][column];
                disc.setFill(Color.TRANSPARENT);
                disc.setVisible(false);
                disc.setCenterY((row * CELL_SIZE) + (CELL_SIZE / 2));
            }
        }

        updatePlayerLabel();
        LOGGER.info("Game reset");
    }

    private void updatePlayerLabel() {
        playerNameLabel.setText(playerName(currentPlayer));
    }

    private static String playerName(int player) {
        return player == 1 ? "Player One" : "Player Two";
    }

    private static Color colorForPlayer(int player) {
        return player == 1 ? PLAYER_ONE_COLOR : PLAYER_TWO_COLOR;
    }

    private static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}

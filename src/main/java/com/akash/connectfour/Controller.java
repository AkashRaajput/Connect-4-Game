package com.akash.connectfour;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Controller implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    private static final double CELL_SIZE = 72.0;
    private static final double DISC_RADIUS = 30.0;
    private static final double BOARD_PADDING = 14.0;
    private static final Color HOLE_COLOR = Color.web("#08111f");
    private static final Color FRAME_TOP = Color.web("#2563eb");
    private static final Color FRAME_BOTTOM = Color.web("#1e3a8a");
    private static final Color PLAYER_ONE_CORE = Color.web("#ef4444");
    private static final Color PLAYER_ONE_EDGE = Color.web("#991b1b");
    private static final Color PLAYER_TWO_CORE = Color.web("#facc15");
    private static final Color PLAYER_TWO_EDGE = Color.web("#ca8a04");

    @FXML
    private Pane insertedDiscsPane;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox playerOnePanel;

    @FXML
    private VBox playerTwoPanel;

    @FXML
    private Button resetButton;

    private final ConnectFourBoard board = new ConnectFourBoard();
    private final Circle[][] discNodes = new Circle[ConnectFourBoard.ROWS][ConnectFourBoard.COLS];
    private final Circle[] previewDiscs = new Circle[ConnectFourBoard.COLS];

    private int currentPlayer = 1;
    private boolean gameOver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildBoardView();
        resetButton.setOnAction(event -> resetGame());
        updatePlayerPanels();
        LOGGER.info("Connect 4 controller initialized");
    }

    private void buildBoardView() {
        insertedDiscsPane.getChildren().clear();

        double boardWidth = (ConnectFourBoard.COLS * CELL_SIZE) + (BOARD_PADDING * 2);
        double boardHeight = (ConnectFourBoard.ROWS * CELL_SIZE) + (BOARD_PADDING * 2);

        Rectangle frame = new Rectangle(boardWidth, boardHeight);
        frame.setArcWidth(24);
        frame.setArcHeight(24);
        frame.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, FRAME_TOP),
                new Stop(1, FRAME_BOTTOM)));
        frame.setEffect(boardShadow());

        Rectangle innerFrame = new Rectangle(boardWidth - 8, boardHeight - 8, Color.web("#172554"));
        innerFrame.setArcWidth(20);
        innerFrame.setArcHeight(20);
        innerFrame.setLayoutX(4);
        innerFrame.setLayoutY(4);

        insertedDiscsPane.getChildren().addAll(frame, innerFrame);

        for (int row = 0; row < ConnectFourBoard.ROWS; row++) {
            for (int column = 0; column < ConnectFourBoard.COLS; column++) {
                double centerX = BOARD_PADDING + (column * CELL_SIZE) + (CELL_SIZE / 2);
                double centerY = BOARD_PADDING + (row * CELL_SIZE) + (CELL_SIZE / 2);

                Circle slot = new Circle(centerX, centerY, DISC_RADIUS, HOLE_COLOR);
                slot.setEffect(innerHoleShadow());
                insertedDiscsPane.getChildren().add(slot);

                Circle disc = new Circle(centerX, centerY, DISC_RADIUS);
                disc.setVisible(false);
                discNodes[row][column] = disc;
                insertedDiscsPane.getChildren().add(disc);
            }
        }

        for (int column = 0; column < ConnectFourBoard.COLS; column++) {
            final int selectedColumn = column;

            Circle preview = new Circle(
                    BOARD_PADDING + (column * CELL_SIZE) + (CELL_SIZE / 2),
                    BOARD_PADDING + (DISC_RADIUS + 4),
                    DISC_RADIUS - 4
            );
            preview.setOpacity(0.35);
            preview.setVisible(false);
            preview.setMouseTransparent(true);
            previewDiscs[column] = preview;
            insertedDiscsPane.getChildren().add(preview);

            Rectangle clickArea = new Rectangle(
                    BOARD_PADDING + (column * CELL_SIZE),
                    BOARD_PADDING,
                    CELL_SIZE,
                    ConnectFourBoard.ROWS * CELL_SIZE
            );
            clickArea.setFill(Color.TRANSPARENT);
            clickArea.setOnMouseClicked(event -> handleColumnClick(selectedColumn));
            clickArea.setOnMouseEntered(event -> showColumnPreview(selectedColumn));
            clickArea.setOnMouseExited(event -> hideColumnPreview(selectedColumn));
            insertedDiscsPane.getChildren().add(clickArea);
        }

        insertedDiscsPane.setPrefSize(boardWidth, boardHeight);
    }

    private void handleColumnClick(int column) {
        if (gameOver) {
            return;
        }

        hideColumnPreview(column);

        if (board.isColumnFull(column)) {
            setStatus("That column is full. Try another one.");
            return;
        }

        int row = board.dropDisc(column, currentPlayer);
        if (row < 0) {
            setStatus("Unable to place a disc in that column.");
            return;
        }

        animateDiscDrop(row, column, currentPlayer);

        if (board.hasWinningMove(row, column, currentPlayer)) {
            gameOver = true;
            setStatus(playerDisplayName(currentPlayer) + " wins the match!");
            showInfo("Victory", playerDisplayName(currentPlayer) + " connected four in a row.");
            updatePlayerPanels();
            LOGGER.info(() -> "Player " + currentPlayer + " won the game");
            return;
        }

        if (board.isBoardFull()) {
            gameOver = true;
            setStatus("The board is full. It's a draw.");
            showInfo("Draw", "No empty spaces left. Well played.");
            updatePlayerPanels();
            LOGGER.info("Game ended in a draw");
            return;
        }

        currentPlayer = currentPlayer == 1 ? 2 : 1;
        updatePlayerPanels();
    }

    private void animateDiscDrop(int targetRow, int column, int player) {
        Circle disc = discNodes[targetRow][column];
        styleDisc(disc, player);
        disc.setVisible(true);

        double endY = BOARD_PADDING + (targetRow * CELL_SIZE) + (CELL_SIZE / 2);
        double startY = BOARD_PADDING - DISC_RADIUS;
        disc.setCenterY(startY);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(disc.centerYProperty(), startY)),
                new KeyFrame(
                        Duration.millis(220 + (targetRow * 55L)),
                        new KeyValue(disc.centerYProperty(), endY, Interpolator.EASE_OUT)
                )
        );
        timeline.play();
    }

    private void showColumnPreview(int column) {
        if (gameOver || board.isColumnFull(column)) {
            return;
        }
        previewDiscs[column].setFill(colorForPlayer(currentPlayer));
        previewDiscs[column].setVisible(true);
    }

    private void hideColumnPreview(int column) {
        previewDiscs[column].setVisible(false);
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
                disc.setCenterY(BOARD_PADDING + (row * CELL_SIZE) + (CELL_SIZE / 2));
            }
        }

        for (Circle preview : previewDiscs) {
            preview.setVisible(false);
        }

        updatePlayerPanels();
        LOGGER.info("Game reset");
    }

    private void updatePlayerPanels() {
        playerOnePanel.getStyleClass().remove("player-panel-active");
        playerTwoPanel.getStyleClass().remove("player-panel-active");

        if (gameOver) {
            setStatus(statusLabel.getText());
            return;
        }

        if (currentPlayer == 1) {
            playerOnePanel.getStyleClass().add("player-panel-active");
            setStatus("Player One's turn. Drop a crimson disc.");
        } else {
            playerTwoPanel.getStyleClass().add("player-panel-active");
            setStatus("Player Two's turn. Drop a gold disc.");
        }
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private static String playerDisplayName(int player) {
        return player == 1 ? "Player One" : "Player Two";
    }

    private static void styleDisc(Circle disc, int player) {
        Color core = player == 1 ? PLAYER_ONE_CORE : PLAYER_TWO_CORE;
        Color edge = player == 1 ? PLAYER_ONE_EDGE : PLAYER_TWO_EDGE;
        disc.setFill(new RadialGradient(0, 0, 0.35, 0.35, 0.75, true, CycleMethod.NO_CYCLE,
                new Stop(0, core.brighter()),
                new Stop(0.7, core),
                new Stop(1, edge)));
        disc.setEffect(discShadow());
    }

    private static Color colorForPlayer(int player) {
        return player == 1 ? PLAYER_ONE_CORE : PLAYER_TWO_CORE;
    }

    private static DropShadow boardShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(24);
        shadow.setOffsetY(8);
        shadow.setColor(Color.rgb(0, 0, 0, 0.45));
        return shadow;
    }

    private static DropShadow discShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8);
        shadow.setOffsetY(3);
        shadow.setColor(Color.rgb(0, 0, 0, 0.35));
        return shadow;
    }

    private static DropShadow innerHoleShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(6);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.55));
        return shadow;
    }

    private static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}

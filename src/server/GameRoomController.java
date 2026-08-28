package server;

import model.*;
import java.util.*;

public class GameRoomController {

    private final ArrayList<ServerConnection> clients = new ArrayList<>();
    private Room room;
    private boolean gameStarted;
    private boolean roundFinished;
    private Thread gameTimer;

    private int currentRound = 0;
    private final int TOTAL_ROUNDS = 4;

    // كل مجموعة تمثل الإجابات الممكنة للمرحلة المحددة بنفس الترتيب
    private final List<Set<String>> GameAnswers = List.of(
        Set.of("قبر", "بقر", "برق", "قرب", "رقب", "بقرب"), // أكثر من 3 كلمات صحيحة
    Set.of("عبر", "رعب", "عرب", "برع", "ربع"),
    Set.of("صقر", "رقص", "قصر", "قرص", "رصق", "نقص", "نصر", "قرن", "نقر"),
    Set.of("فرح", "حفر", "حرف", "رفح", "فحر", "فكر", "كفر", "حرك")
    );

    private final Map<Player, List<String>> playerWordsMap = new HashMap<>();

    public GameRoomController(Room room) {
        this.room = room;
        this.gameStarted = false;
        this.roundFinished = false;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public synchronized void addPlayer(ServerConnection client) {
        if (clients.isEmpty()) {
            Thread timer = new Thread(() -> {
                try {
                    Thread.sleep(30 * 1000);
                    if (!gameStarted && room.getPlayers().size() > 1) {
                        startGame();
                    }
                } catch (InterruptedException ex) {}
            });
            timer.start();
        }

        if (room.isAvailable()) {
            room.addPlayer(client.getPlayer());
            client.send(102, room.getRoomNumber() + "");
            clients.add(client);
            client.setRoom(room);

            sendToRoomPlayers(3, client.getPlayer().getUsername() + " joined room.");
            sendToRoomPlayers(202, getRoomPlayers());

           if (room.getPlayers().size() >= GameServer.NUMBER_OF_PLAYERS_PER_ROOM) {
    new Thread(() -> {
        try {
            Thread.sleep(2000); // انتظر 2 ثانية قبل بدء اللعبة
            startGame();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
}

        }
    }

    public synchronized void removePlayer(ServerConnection client) {
        clients.remove(client);
        room.removePlayer(client.getPlayer());

        if (gameStarted) {
            sendToRoomPlayers(203, getGamePlayersScores());
            if (clients.size() == 1) {
                endGame("عدد اللاعبين غير كافٍ للاستمرار.");
            }
        } else {
            sendToRoomPlayers(202, getRoomPlayers());
        }
    }

    public synchronized void startGame() {
        gameStarted = true;
        room.setAvailable(false);
        currentRound = 0;
        startRound();
    }

    private synchronized void startRound() {
        roundFinished = false;
        playerWordsMap.clear();

        for (ServerConnection client : clients) {
            playerWordsMap.put(client.getPlayer(), new ArrayList<>());

            // نرسل رقم المرحلة  
            client.send(4, String.valueOf(currentRound + 1));
        }

        sendToRoomPlayers(203, getGamePlayersScores());

        gameTimer = new Thread(() -> {
            try {
                Thread.sleep(30 * 1000);
                synchronized (this) {
                    if (!roundFinished) {
                        roundFinished = true;
                        advanceToNextRound();
                    }
                }
            } catch (InterruptedException e) {}
        });
        gameTimer.start();
    }

    private synchronized void advanceToNextRound() {
        currentRound++;
        if (currentRound < TOTAL_ROUNDS) {
            startRound();
        } else {
            endGame("اللعبة انتهت! لم يفز أحد. شكراً لمشاركتكم ♥");

        }
    }

    public synchronized void handleWordSubmission(ServerConnection client, String word) {
        word = word.trim().toLowerCase();
        Player player = client.getPlayer();
        List<String> submittedWords = playerWordsMap.get(player);

        if (submittedWords == null || submittedWords.contains(word)) {
            client.send(205, "false");
            return;
        }

        Set<String> validWords = GameAnswers.get(currentRound);

        if (validWords.contains(word)) {
            submittedWords.add(word);
            client.send(205, "true");

            if (submittedWords.size() == 3 && !roundFinished) {
                roundFinished = true;
                player.setScore(player.getScore() + 1);
                sendToRoomPlayers(203, getGamePlayersScores());
                gameTimer.interrupt();
                advanceToNextRound();
            }
        } else {
            client.send(205, "false");
        }
    }
public synchronized void endGame(String msg) {
    gameStarted = false;
    room.setAvailable(false);

    // ترتيب اللاعبين حسب السكور
    List<Player> sorted = new ArrayList<>();
    for (ServerConnection client : clients) {
        sorted.add(client.getPlayer());
    }

    sorted.sort((a, b) -> b.getScore() - a.getScore());

    int maxScore = sorted.isEmpty() ? 0 : sorted.get(0).getScore();

    if (maxScore == 0 || sorted.isEmpty()) {
        // في حالة عدم وجود فائز حقيقي
        for (ServerConnection client : clients) {
            client.send(6, "END_NO_WINNER:" + msg);
        }
        return;
    }

    // تحقق هل كل اللاعبين متعادلين بنفس السكور
    List<String> topPlayers = new ArrayList<>();
    for (Player player : sorted) {
        if (player.getScore() == maxScore) {
            topPlayers.add(player.getUsername());
        }
    }

    if (topPlayers.size() == sorted.size()) {
        // ✅ الكل متعادلين
        String drawList = String.join(",", topPlayers);
        for (ServerConnection client : clients) {
            client.send(6, "END_DRAW:" + drawList);
        }
    } else {
        // ✅ فيه فائزين طبيعيين (واحد أو توب 3)
        List<String> top3 = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            top3.add(sorted.get(i).getUsername());
        }

        String winnersList = String.join(",", top3);

        for (ServerConnection client : clients) {
            client.send(6, "END_WINNERS:" + winnersList);
        }
    }
}

    public synchronized void sendToRoomPlayers(int code, String msg) {
        for (ServerConnection client : clients) {
            client.send(code, msg);
        }
    }

    public synchronized String getRoomPlayers() {
        StringBuilder msg = new StringBuilder();
        for (ServerConnection client : clients) {
            msg.append(client.getPlayer().getUsername()).append("-");
        }
        return msg.toString();
    }

    public synchronized String getGamePlayersScores() {
        StringBuilder msg = new StringBuilder();
        for (ServerConnection client : clients) {
            msg.append("[").append(client.getPlayer().getScore()).append("] ")
               .append(client.getPlayer().getUsername()).append("-");
        }
        return msg.toString();
    }

    public synchronized void sendRoomPlayersScores() {
        sendToRoomPlayers(203, getGamePlayersScores());
    }

    public synchronized boolean isRoomAvailable() {
        return this.room.isAvailable();

    }}
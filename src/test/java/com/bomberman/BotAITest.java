package com.bomberman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BotAITest {

    static class DummyGame extends BombermanGame {
        public boolean moveCalled = false;
        @Override
        public boolean movePlayer(Player player, int dx, int dy) {
            moveCalled = true;
            player.x += dx;
            player.y += dy;
            return true;
        }
        @Override
        public void placeBomb(Player player) {}
    }

    private DummyGame game;
    private BotAI botAI;
    private BombermanGame.Player bot;
    private List<BombermanGame.Player> players;
    private List<BombermanGame.Bomb> bombs;
    private boolean[][] walls;
    private boolean[][] destructibleBlocks;

    @BeforeEach
    void setUp() {
        game = new DummyGame();
        botAI = new BotAI(game);
        bot = game.new Player(1, 1, 0, "Bot");
        players = new ArrayList<>();
        players.add(bot);
        bombs = new ArrayList<>();
        walls = new boolean[5][5];
        destructibleBlocks = new boolean[5][5];
    }

    @Test
    void testBotAICreation() {
        assertNotNull(botAI);
    }

    @Test
    void testBotMovesWhenInDanger() {
        // Place une bombe sur la position du bot
        bombs.add(game.new Bomb(1, 1, bot));
        botAI.updateBot(bot, players, bombs, walls, destructibleBlocks);
        assertTrue(game.moveCalled, "Le bot doit essayer de bouger pour éviter le danger.");
    }

    @Test
    void testBotNoMoveWhenSafe() {
        botAI.updateBot(bot, players, bombs, walls, destructibleBlocks);
        assertFalse(game.moveCalled, "Le bot ne doit pas bouger s'il n'est pas en danger.");
    }
}
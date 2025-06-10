package com.bomberman;

import java.util.*;
import com.bomberman.BombermanGame;

public class BotAI {

    private BombermanGame game;
    private static final int MAX_PATH_COST = 1000;

    public BotAI(BombermanGame game) {
        this.game = game;
    }

    private static class Node {
        int x, y, g, h;
        Node parent;

        Node(int x, int y, int g, int h, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        int f() {
            return g + h;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public void updateBot(BombermanGame.Player bot, List<BombermanGame.Player> players, List<BombermanGame.Bomb> bombs, boolean[][] walls, boolean[][] destructibleBlocks) {
        if (!bot.alive) return;

        int gridSize = walls.length;
        int[][] dangerMap = computeDangerMap(bombs, walls, destructibleBlocks, gridSize);

        // Priorité maximale: s'échapper d'une bombe ou d'une zone dangereuse
        if (dangerMap[bot.x][bot.y] > 0) {
            int[] safeMove = findSafeMove(bot, dangerMap, walls, destructibleBlocks, bombs, gridSize);
            if (safeMove != null) {
                game.movePlayer(bot, safeMove[0] - bot.x, safeMove[1] - bot.y);
                return;
            }
        }

        // Chercher une cible
        BombermanGame.Player target = findTarget(bot, players);

        // Si aucune cible n'est disponible, ne rien faire
        if (target == null) return;

        // Vérifier si on est adjacent au joueur cible
        if (isAdjacent(bot.x, bot.y, target.x, target.y)) {
            // Ne poser une bombe que si on peut s'échapper après
            if (canEscapeAfterBomb(bot, bombs, walls, destructibleBlocks, gridSize)) {
                game.placeBomb(bot);
                return;
            }
        }

        // Obtenir le chemin optimal avec détail des coûts
        PathResult pathResult = findOptimalPath(bot, target, walls, destructibleBlocks, bombs, dangerMap, gridSize);
        if (pathResult == null || pathResult.path.isEmpty()) return;

        // Si le premier pas est bloqué par un mur destructible, placer une bombe
        int nextX = pathResult.path.get(0)[0];
        int nextY = pathResult.path.get(0)[1];

        if (destructibleBlocks[nextX][nextY]) {
            if (canEscapeAfterBomb(bot, bombs, walls, destructibleBlocks, gridSize)) {
                game.placeBomb(bot);
                return;
            } else {
                int[] safeMove = findSafeMove(bot, dangerMap, walls, destructibleBlocks, bombs, gridSize);
                if (safeMove != null) {
                    game.movePlayer(bot, safeMove[0] - bot.x, safeMove[1] - bot.y);
                    return;
                }
            }
        }

        // Se déplacer vers la cible
        if (pathResult.path.size() > 0) {
            game.movePlayer(bot, nextX - bot.x, nextY - bot.y);
        }
    }

    // Classe pour stocker le résultat du path-finding avec des informations supplémentaires
    private static class PathResult {
        List<int[]> path;  // Le chemin à suivre
        int cost;          // Coût total du chemin (nombre de murs à casser)

        PathResult(List<int[]> path, int cost) {
            this.path = path;
            this.cost = cost;
        }
    }

    private BombermanGame.Player findTarget(BombermanGame.Player bot, List<BombermanGame.Player> players) {
        // Priorité au joueur humain s'il est vivant
        if (players.get(0).alive) {
            return players.get(0);
        }

        // Sinon, cibler le bot vivant le plus proche
        BombermanGame.Player nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (BombermanGame.Player player : players) {
            // Ignorer soi-même et les joueurs morts
            if (player == bot || !player.alive) continue;

            int distance = manhattanDistance(bot.x, bot.y, player.x, player.y);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    // Marque toutes les cases menacées par chaque bombe selon le rayon du propriétaire
    private int[][] computeDangerMap(List<BombermanGame.Bomb> bombs, boolean[][] walls, boolean[][] destructibleBlocks, int gridSize) {
        int[][] danger = new int[gridSize][gridSize];

        for (BombermanGame.Bomb bomb : bombs) {
            // Danger critique pour la case de la bombe elle-même
            danger[bomb.x][bomb.y] = 3; // Niveau de danger maximum

            // Rayon d'explosion du propriétaire de la bombe
            int radius = (bomb.owner != null) ? bomb.owner.explosionRadius : 2;

            // Marquer le danger dans les 4 directions (exactement comme la bombe explose)
            int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}};
            for (int[] dir : dirs) {
                for (int i = 1; i <= radius; i++) {
                    int nx = bomb.x + dir[0] * i;
                    int ny = bomb.y + dir[1] * i;

                    // Vérifier les limites de la grille
                    if (nx < 0 || ny < 0 || nx >= gridSize || ny >= gridSize) break;

                    // Arrêter à un mur indestructible
                    if (walls[nx][ny]) break;

                    // Marquer comme dangereuse avec un niveau de danger identique à la bombe
                    danger[nx][ny] = 3;  // Le même niveau que la bombe (zone d'évacuation obligatoire)

                    // Arrêter à un bloc destructible, mais le marquer comme dangereux
                    if (destructibleBlocks[nx][ny]) break;
                }
            }
        }
        return danger;
    }

    // Cherche un mouvement sûr adjacent
    private int[] findSafeMove(BombermanGame.Player bot, int[][] danger, boolean[][] walls, boolean[][] destructibleBlocks, List<BombermanGame.Bomb> bombs, int gridSize) {
        // Vérifier si le bot est sur une bombe
        boolean onBomb = false;
        for (BombermanGame.Bomb bomb : bombs) {
            if (bomb.x == bot.x && bomb.y == bot.y) {
                onBomb = true;
                break;
            }
        }

        // Recherche des mouvements possibles
        List<int[]> possibleMoves = new ArrayList<>();
        List<int[]> safeMoves = new ArrayList<>();
        int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        for (int[] dir : dirs) {
            int nx = bot.x + dir[0];
            int ny = bot.y + dir[1];

            // Vérifier les limites et obstacles
            if (nx < 0 || ny < 0 || nx >= gridSize || ny >= gridSize) continue;
            if (walls[nx][ny] || destructibleBlocks[nx][ny]) continue;

            // Vérifier s'il y a une bombe à cet endroit
            boolean hasBomb = false;
            for (BombermanGame.Bomb bomb : bombs) {
                if (bomb.x == nx && bomb.y == ny) {
                    hasBomb = true;
                    break;
                }
            }
            if (hasBomb) continue;

            // Si on est sur une bombe, tout mouvement est meilleur que rester
            if (onBomb) {
                possibleMoves.add(new int[]{nx, ny});
            }

            // Classement des mouvements par niveau de sécurité
            if (danger[nx][ny] == 0) {
                safeMoves.add(new int[]{nx, ny});
            } else if (danger[nx][ny] < danger[bot.x][bot.y]) {
                // Si le danger est moindre que la position actuelle, c'est une option
                possibleMoves.add(new int[]{nx, ny});
            }
        }

        // Priorité aux cases totalement sûres
        if (!safeMoves.isEmpty()) {
            return safeMoves.get(new Random().nextInt(safeMoves.size()));
        }

        // Sinon, prendre une case moins dangereuse que la position actuelle
        if (!possibleMoves.isEmpty()) {
            return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        }

        return null;  // Aucun mouvement sûr trouvé
    }

    // A* amélioré qui tient compte des murs destructibles comme un coût supplémentaire
    private PathResult findOptimalPath(BombermanGame.Player bot, BombermanGame.Player target, boolean[][] walls, boolean[][] destructibleBlocks, List<BombermanGame.Bomb> bombs, int[][] danger, int gridSize) {

        // Tableau pour marquer les nœuds visités
        boolean[][] closed = new boolean[gridSize][gridSize];

        // File de priorité pour l'algorithme A*
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::f));
        openSet.add(new Node(bot.x, bot.y, 0, manhattanDistance(bot.x, bot.y, target.x, target.y), null));

        // Map pour garder trace des meilleurs coûts
        Map<String, Integer> gScore = new HashMap<>();
        gScore.put(bot.x + "," + bot.y, 0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Si nous avons atteint la cible (ou une case adjacente à la cible)
            if (current.x == target.x && current.y == target.y ||
                    isAdjacent(current.x, current.y, target.x, target.y)) {
                return reconstructPath(current, bot.x, bot.y);
            }

            // Marquer comme visité
            closed[current.x][current.y] = true;

            // Explorer les voisins
            int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}};
            for (int[] dir : dirs) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                // Vérifier les limites
                if (nx < 0 || ny < 0 || nx >= gridSize || ny >= gridSize) continue;

                // Ignorer les murs indestructibles
                if (walls[nx][ny]) continue;

                // Ignorer les zones dangereuses sauf la cible
                if (danger[nx][ny] > 0 && !(nx == target.x && ny == target.y)) continue;

                // Ignorer les bombes sauf la cible
                boolean hasBomb = false;
                for (BombermanGame.Bomb bomb : bombs) {
                    if (bomb.x == nx && bomb.y == ny && !(nx == target.x && ny == target.y)) {
                        hasBomb = true;
                        break;
                    }
                }
                if (hasBomb) continue;

                // Si déjà visité
                if (closed[nx][ny]) continue;

                // Calculer le coût du mouvement: plus élevé pour traverser un mur destructible
                int moveCost = 1;
                if (destructibleBlocks[nx][ny]) {
                    moveCost = 50; // Coût élevé pour casser un mur
                }

                int tentativeG = current.g + moveCost;
                String key = nx + "," + ny;

                if (!gScore.containsKey(key) || tentativeG < gScore.get(key)) {
                    gScore.put(key, tentativeG);
                    int h = manhattanDistance(nx, ny, target.x, target.y);
                    Node neighbor = new Node(nx, ny, tentativeG, h, current);

                    boolean found = false;
                    for (Node n : openSet) {
                        if (n.equals(neighbor) && n.g <= neighbor.g) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return null; // Aucun chemin trouvé
    }

    // Vérifier si deux positions sont adjacentes
    private boolean isAdjacent(int x1, int y1, int x2, int y2) {
        // Vérifie si les positions sont adjacentes (distance de Manhattan = 1)
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) <= 1;
    }

    // Distance de Manhattan
    private int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    // Reconstruire le chemin à partir du nœud final
    private PathResult reconstructPath(Node finalNode, int startX, int startY) {
        List<int[]> path = new ArrayList<>();
        Node current = finalNode;
        int cost = finalNode.g;

        while (current != null && !(current.x == startX && current.y == startY)) {
            path.add(0, new int[]{current.x, current.y});
            current = current.parent;
        }

        return new PathResult(path, cost);
    }

    // Vérifie si le bot peut s'échapper après avoir posé une bombe
    private boolean canEscapeAfterBomb(BombermanGame.Player bot, List<BombermanGame.Bomb> bombs, boolean[][] walls, boolean[][] destructibleBlocks, int gridSize) {
        // Simule la pose d'une bombe
        List<BombermanGame.Bomb> simulatedBombs = new ArrayList<>(bombs);
        BombermanGame.Bomb fakeBomb = game.new Bomb(bot.x, bot.y, bot);
        simulatedBombs.add(fakeBomb);

        // Calculer la nouvelle carte de danger
        int[][] dangerAfterBomb = computeDangerMap(simulatedBombs, walls, destructibleBlocks, gridSize);

        // BFS pour trouver une zone sûre
        boolean[][] visited = new boolean[gridSize][gridSize];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{bot.x, bot.y});
        visited[bot.x][bot.y] = true;

        int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();

            // Si la position est sûre, on a trouvé une échappatoire
            if (dangerAfterBomb[pos[0]][pos[1]] == 0) {
                return true;
            }

            // Explorer les voisins
            for (int[] dir : dirs) {
                int nx = pos[0] + dir[0];
                int ny = pos[1] + dir[1];

                // Vérifier les limites et obstacles
                if (nx < 0 || ny < 0 || nx >= gridSize || ny >= gridSize) continue;
                if (walls[nx][ny] || destructibleBlocks[nx][ny] || visited[nx][ny]) continue;

                // Vérifier s'il y a une bombe
                boolean hasBomb = false;
                for (BombermanGame.Bomb bomb : simulatedBombs) {
                    if (bomb.x == nx && bomb.y == ny) {
                        hasBomb = true;
                        break;
                    }
                }
                if (hasBomb) continue;

                visited[nx][ny] = true;
                queue.add(new int[]{nx, ny});
            }
        }

        return false; // Aucune échappatoire trouvée
    }
}
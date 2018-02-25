package mdza.blockcapturegame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import mdza.games.*;

public class Game {

    public Game(Player playerOne) {
        this(playerOne, null, null, null, new BoardModeSingle());
    }
    
    public Game(Player playerOne, Player playerTwo) {
        this(playerOne, playerTwo, null, null, new BoardModeSingle());
    }
    
    
    public Game(Player playerOne, Player playerTwo, Player playerThree) {
        this(playerOne, playerTwo, playerThree, null, new BoardModeThree());
    }
    
    public Game(Player playerOne, Player playerTwo, 
            Player playerThree, Player playerFour) {
        this(playerOne, playerTwo, playerThree, playerFour, 
                new BoardModeFour());
    }
    
    private Game(Player playerOne, Player playerTwo, 
            Player playerThree, Player playerFour, Board board) {
        
        this.players = new HashMap<Token, Player>();       

        if (playerOne != null)  
            players.put(Token.RED, playerOne.setPlayerToken(Token.RED));   
        
        if (playerTwo == null) 
            players.put(Token.BLACK, getComputer().setPlayerToken(Token.BLACK));
        else
            players.put(Token.BLACK, playerTwo.setPlayerToken(Token.BLACK));
        
        if (playerThree != null) 
            players.put(Token.ORANGE, playerThree.setPlayerToken(Token.ORANGE)); 
        
        if (playerFour != null)
            players.put(Token.GREEN, playerFour.setPlayerToken(Token.GREEN)); 
        
        this.currentPlayer = players.get(Token.RED);
        
        this.board = board;
        this.exit = false;
    }
    
    public static class Player {
        public Player(String name)  { this(Token.EMPTY, name); }
        private Player(Token token) { this(token, "");         }
        private Player(Token token, String name) {
            this.token = token;
            this.name  = name;
        }

        public String getPlayerName()            { return name;        }
        public Token getPlayerToken()            { return token;       }
        public void setPlayerName(String name)   { this.name = name;   }
        private Player setPlayerToken(Token token) { 
            this.token = token; 
            return this;
        }

        private Token token;
        private String name;
    }

    private void move() throws Exception {
        int row = 0, col = 0; 
        Board board = getBoard();
        Player currentPlayer = getCurrentPlayer();
        Piece piece = null;
        
        if (board.isGameOver(currentPlayer.getPlayerToken())) {
            gameFinishedEvent();
            quit();
            return;
        }
        
        if (currentPlayer == getComputer()) {      
            try { 
                movePendingEvent(); 
                piece = board.getRandomValidMove(currentPlayer.getPlayerToken());
                if(piece != null) {
                    row = piece.getRow();
                    col = piece.getCol();
                }
            } catch(Exception e) {}

            
            try { Thread.sleep(1500); } 
            catch (InterruptedException e) {}
        } else {
            try {
                piece = movePendingEvent();
                while(piece == null)
                    piece = movePendingEvent();
                row = piece.getRow();
                col = piece.getCol();
            } catch (Exception e) { e.printStackTrace(); }
        } 
        
        if(board.isMoveValid(row, col, currentPlayer.getPlayerToken())) {
            board.setPiece(row, col, currentPlayer.getPlayerToken());
            moveReceivedEvent(piece);
            altCurrentPlayer();    
        } else { moveInvalidEvent(); }
    }
    
    public synchronized void play() {
        gameStartedEvent();
        do {
            try {
                move();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }while(!exit);       
    }
    
    // 
    // Getters
    //
    
    public Board getBoard() { return this.board; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public Token getCurrentPlayerToken() { return currentPlayer.getPlayerToken(); }
    public String getCurrentPlayerName() { return currentPlayer.getPlayerName(); }
    //public OthelloBoard getGameSession() { return this.logic; }
    public int getRows() { return this.board.getRows(); }
    public int getCols() { return this.board.getCols(); }
//    public Map getBoardLocationsMap() { return board.getBoardLocationsMap(); }  
    
    public Player[] getWinnerPlayer() { 
        int size = board.getWinnerToken().size();
        int i = 0;
        Player[] winners = new Player[size];
        Set<Token> tokens = board.getWinnerToken().keySet();
        Iterator<Token> it = tokens.iterator();
        
        while (it.hasNext()) {
            winners[i++] = players.get(it.next());
        }
        
        return winners;
    }
    
    // 
    // Listeners
    //
    
    public synchronized void addEventListener(TurnBasedGameEventListener listener) {
        this.listener = listener;
    }

    public synchronized void removeEventListener() {
        this.listener = null;
    }
    
    //
    // Game Events
    //

    private synchronized void gameStartedEvent() {
        GameEvent event = new GameEvent(this);
        if (listener != null)
            listener.handleGameStarted(event);
    }

    private synchronized void moveReceivedEvent(Piece p) {   
        GameEvent event = new GameEvent(this);
        if (listener != null)
            listener.handleMoveReceived(event, p);
    }

    private synchronized void playerChangedEvent() {
        GameEvent event = new GameEvent(this);
        if (listener != null) 
            listener.handlePlayerChanged(event);
    }

    private synchronized void gameFinishedEvent() {
        GameEvent event = new GameEvent(this);
        if (listener != null) 
        listener.handleGameFinished(event);     
        quit();
    }
    
    private synchronized Piece movePendingEvent() throws Exception {
        GameEvent event = new GameEvent(this);
        Piece piece = null;
        if (listener != null) 
            piece = (Piece) listener.handleMovePending(event);

        return piece;
    }
    
    private synchronized void moveInvalidEvent() {
        GameEvent event = new GameEvent(this);
        Piece piece = null;
        if (listener != null) 
            listener.handleInvalidMove(event);
    }
    
    private synchronized void invalidGameStateEvent() {
        GameEvent event = new GameEvent(this);
        if (listener != null)
            listener.handleInvalidGameState(event);
    }
    
    //
    // Misc
    //
    
    public void altCurrentPlayer() { 
        Token currentToken = currentPlayer.getPlayerToken();
        Token alternateToken = board.alternateToken(currentToken);
        currentPlayer = players.get(alternateToken);
    }
    
    public void quit() { this.exit = true; }
    
    public final Player getComputer() { 
        if (computer == null) { computer = new Player("Computer"); }
        return computer; 
    }
    
    // 
    // Member Fields
    //
    protected Player currentPlayer;
    private Map<Token, Player> players;
    private Board board;
    private TurnBasedGameEventListener listener;
    private boolean exit;
    private static Player computer;
}

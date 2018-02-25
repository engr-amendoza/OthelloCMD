package mdza.blockcapturegame;

import java.util.EventObject;
import java.util.Scanner;
import mdza.games.*;

public class Tester {

    public static void main(String[] args) {
        
        System.out.println("How many players?");
        int numOfPlayers = new Scanner(System.in).nextInt();
        
        Game.Player playerA = new Game.Player("Player 1");
        Game.Player playerB = new Game.Player("Player 2");
        Game.Player playerC = new Game.Player("Player 3");
        Game.Player playerD = new Game.Player("Player 4");
        Game game = null;
        
        switch(numOfPlayers) {
            case 1: game = new Game(playerA); break;
            case 2: game = new Game(playerA, playerB); break;
            case 3: game = new Game(playerA, playerB, playerC); break;
            default: game = new Game(playerA, playerB, playerC, playerD); break;
        }
            
        game.addEventListener(new TurnBasedGameEventListener() {
            @Override
            public void handleMoveReceived(EventObject e, Object o) {      
                Piece p = (Piece) o;
                for (int i=0; i<50; i++)
                    System.out.print("~");
                System.out.println();
                System.out.printf("The move received was %d %d\n", p.getRow(), p.getCol());
            }

            @Override
            public void handleGameFinished(EventObject e) {
                Game game = (Game) e.getSource();
                Board board = game.getBoard();
                ConsolePrint.printBoard(board);
                ConsolePrint.printWinner(board);
                //System.out.println("The winner is: " + board.getWinnerToken());
            }

            @Override
            public void handleGameStarted(EventObject e) {
                System.out.println("The game has started!");
            }

            @Override
            public Piece handleMovePending(EventObject e) {
                Game game = (Game) e.getSource();
                Board board = game.getBoard();
                Game.Player currentPlayer = game.getCurrentPlayer();
                Piece piece = null;
                System.out.printf("Current Player: %s\nColor: %s\n", currentPlayer.getPlayerName(), currentPlayer.getPlayerToken().toString());
                ConsolePrint.printValidMoves(board.getValidMoves(currentPlayer.getPlayerToken()));
                //ConsolePrint.printInstructions();
                ConsolePrint.printBoard(board);
                if(currentPlayer != game.getComputer())
                    piece = ConsolePrint.printGetPiece("YOUR TURN!");

                return piece;
            }

            @Override
            public void handlePlayerChanged(EventObject e) {
                Game game = (Game) e.getSource();
                System.out.println("CHANGED EVENT - Current Player is: " + game.getCurrentPlayerName());
            }

            @Override
            public void handleInvalidMove(EventObject e) {
                System.out.println("INVALID MOVE!");
            }

            @Override
            public void handleInvalidGameState(EventObject e) {
                System.out.println("Invalid command!");
            }
        
        });
        
        game.play();

    }
    
}

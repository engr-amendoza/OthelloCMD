package mdza.blockcapturegame;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class ConsolePrint {
    public static void printSurroundingLocations(Board board, int row, int col){
        Map locations = board.findSurroundingLocationsMap(row, col);
        
        System.out.println(String.format("~~ printSurroundingLocations (%s, %s) ~~move", row, col));
        ConsolePrint.printValidMoves(locations);
    }

    public static void printAllSurroundingLocations(Board logic){
        //System.out.println("~~~ printSurroundingLocations");
        Map locations = logic.findAllSurroundingLocationsMap();
        
        System.out.println("~~ printAllSurroundingLocations ~~");
        ConsolePrint.printValidMoves(locations);
    }
    
    public static void printBoard(Board board) {
        System.out.println("~~ Game Board --");
        int rows = board.getRows();
        int cols = board.getCols();
        
        // print colum headers
        System.out.print("  ");
        for(int i=0; i<cols; ++i)
            System.out.print(i + " ");
        System.out.println();
        
        // print board
        for(int i=0; i<rows; ++i) {
            System.out.print(i + " ");
            for(int k=0; k<cols; ++k) {

                Token col = (Token)
                        board.getBoardLocation(i,k);
                String piece = col.equals(Token.RED) ? "R" :
                               col.equals(Token.BLACK) ? "B" :
                               col.equals(Token.GREEN) ? "G" :
                               col.equals(Token.ORANGE) ? "O" : "*";
                System.out.print(piece + " ");
            }
            System.out.println();
        }
    }
    
    public static void printValidMoves(Map map) {
        System.out.println("~~ Valid Moves ~~");


        Set<Piece> keys = map.keySet();
        for(Piece pc : keys){
            System.out.println(pc);
        }
    }
    
    public static void printInstructions() {
        System.out.println("~~~~~ Enter a command ~~"
            + "\nmove 2 3  --> row=2, col=3"
            + "\nexit      --> end game");
    }
    
    public static void printWinner(Board board) {
        Map<Token, Integer> winners = board.getWinnerToken();
        Iterator it = winners.entrySet().iterator();
        int count = winners.size();
        
        if (count == 1) {
            Entry entry = (Entry) it.next();
            System.out.println("The winner is: " + (Token) entry.getKey());
        }
        else if (count > 1) {
           System.out.println("The winners are: ");
           while(it.hasNext()) {
                Entry entry = (Entry) it.next();
                System.out.println( (Token) entry.getKey());
           }
        }
        else
            System.out.println("Nobody won");
    }
    
    public static Piece printGetPiece(String s) {
        System.out.printf("~~ %s: Enter location as (i.e. row col) ~~\n", s);
        String cmd = new Scanner(System.in).nextLine();
        String[] result = cmd.split(" ");
        int row = Integer.parseInt(result[0]);
        int col = Integer.parseInt(result[1]);
        return new Piece(row, col);
    }
    

}

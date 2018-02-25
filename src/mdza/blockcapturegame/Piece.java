package mdza.blockcapturegame;
//import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Piece implements Comparable<Piece>/*Comparator<Piece>*/ {
    
    public Piece() { this(0,0); }
    public Piece(int row, int col) { setLocation(row, col); }
    
    public Piece setLocation(int row, int col) {
        this.col = col;
        this.row = row;
        return this;
    } 
    
    public int getRow() { return this.row; }
    public int getCol() { return this.col; }
    public String toString() { return String.format("ROW: %s\tCOL: %s", row, col); }

    @Override
    public int hashCode() {
        int hash = 1;

        hash = hash * 71 + row;
        hash = hash * 199 + col;
        
        return hash;
    }

    @Override
    public int compareTo(Piece o) {
        Piece p1 = this;
        Piece p2 = o;
        if(p1.row == p2.row)     { return p1.col - p2.col; }
        else if(p1.row < p2.row) { return -1;              }
        else                     { return 1;               }  
    }
    
    private int col;
    private int row;
}



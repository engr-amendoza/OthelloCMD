package mdza.blockcapturegame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

abstract public class Board {
    protected Board() {
        this(8, 8);
    }

    protected Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.boardLocations = new Token[rows][cols];
        this.usedLocationsMap = new TreeMap();
        initBoard();
    }

    public boolean isGameOver(Token currentToken) {
        return getValidMoves(currentToken).size() == 0;
    }
    
    public Token getBoardLocation(int row, int col) {
        return boardLocations[row][col];
    }

    public Map<Token, Integer> getWinnerToken() {
        Map<Token, Integer> counter = new HashMap();
        Map<Token, Integer> winners = new HashMap<>();

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                Token token = boardLocations[row][col];
                if(!counter.containsKey(token)) {
                    counter.put(token, 1);
                } else {
                    int value = counter.get(token) + 1;
                    counter.replace(token, value);
                }
            }  
        }       

        counter.remove(Token.EMPTY); 
        Entry<Token, Integer> maxEntry = null;
        Iterator it = counter.entrySet().iterator();

        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            if (maxEntry == null) {
                maxEntry = entry;
                winners.put( (Token) entry.getKey(), (Integer) entry.getValue());
            } else if (((int) entry.getValue() > (int)maxEntry.getValue())) {
                maxEntry = entry;
                winners.clear();
                winners.put( (Token) entry.getKey(), (Integer) entry.getValue());
            } else if ((int) entry.getValue() == (int)maxEntry.getValue()) {
                winners.put( (Token) entry.getKey(), (Integer) entry.getValue());
            }
        }
        return winners;
    }

    public synchronized boolean isMoveValid(int row, int col, Token token) {
        Map<Piece, Token> moves = this.getValidMoves(token);
        return moves.containsKey(new Piece(row, col));
    }

    public Piece getRandomValidMove(Token playerToken) {
        Map movesMap = getValidMoves(playerToken);
        int numOfMoves = movesMap.size();
        Random rand = new Random();
        int index = 0;

        if (numOfMoves == 0) { return null; }
        if (numOfMoves == 1) { index = 0;   }
        else { index = rand.nextInt(numOfMoves); }

        return (Piece) (movesMap.keySet().toArray()[index]);
    }

    public synchronized Map getValidMoves(Token playerToken) {
        Map<Piece, Token> locations = this.findAllSurroundingLocationsMap();
        Map<Piece, Token> validLocations = new TreeMap();
        Iterator it = locations.entrySet().iterator();

        while(it.hasNext()) {
            Entry piece = (Entry)it.next();
            Piece p = (Piece)piece.getKey();
            int row = p.getRow();
            int col = p.getCol();
            
            if(isDiagonalValid(row, col, playerToken, false)) 
                validLocations.put(new Piece(row, col), playerToken);

            if(isHorizontalValid(row, col, playerToken, false))
                validLocations.put(new Piece(row, col), playerToken);

            if(isVerticalValid(row, col, playerToken, false)) 
                validLocations.put(new Piece(row, col), playerToken);
        }

        return validLocations;
    }

    abstract public Token getOpponentToken(Token token);

    public Map getEmptyLocationsMap() {
        Map<Piece, Token> empty = new TreeMap();
        
        for(int row = 0; row < rows; ++row) {
            for(int col = 0; col < cols; ++col) {
                if(boardLocations[row][col] == Token.EMPTY) 
                    empty.put(new Piece(row, col), Token.EMPTY);
            }
        }

        return empty;
    }

    public Map findSurroundingLocationsMap(int row, int col) {
        Map<Piece, Token> surroundingLocation = new TreeMap();
        int startRow = row - 1 < 0?row:row - 1;
        int startCol = col - 1 < 0?col:col - 1;
        int endRow = row + 1 >= this.rows ? row : row + 1;
        int endCol = col + 1 >= this.cols ? col : col + 1;

        for(int sRow = startRow; sRow <= endRow; ++sRow) {
            for(int sCol = startCol; sCol <= endCol; ++sCol) {
                if(boardLocations[sRow][sCol] == Token.EMPTY) 
                    surroundingLocation.put(new Piece(sRow, sCol), Token.EMPTY);
            }
        }

        return surroundingLocation;
    }

    public Map findAllSurroundingLocationsMap() {
        Iterator it = this.usedLocationsMap.entrySet().iterator();
        TreeMap allSurroundingLocations = new TreeMap();

        while(it.hasNext()) {
            Entry pairs = (Entry)it.next();
            Piece tmp = (Piece)pairs.getKey();
            int row = tmp.getRow();
            int col = tmp.getCol();
            allSurroundingLocations.putAll(findSurroundingLocationsMap(col, row));
        }

        return allSurroundingLocations;
    }

    public int getRows() { return this.rows; }

    public int getCols() { return this.cols; }

    public boolean setPiece(int row, int col, Token playerToken) {
        setLocation(row, col, playerToken);
        this.isVerticalValid(row, col, playerToken, true);
        this.isHorizontalValid(row, col, playerToken, true);
        this.isDiagonalValid(row, col, playerToken, true);
        return true;
    }
    
    protected void setLocation(int row, int col, Token playerToken) {
        this.boardLocations[row][col] = playerToken;
        this.usedLocationsMap.put(new Piece(row, col), playerToken);
    }

   /* public static void gc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference(obj);
        obj = null;

        while(ref.get() != null) {
            System.gc();
        }

    }*/

    private void updateVertical(int startRow, int endRow, int col, Token playerToken, boolean upper) {
        if(upper) {
            for(int row = startRow; row > endRow - 1; --row) 
                setLocation(row, col, playerToken);
        } else {
            for(int row = startRow; row < endRow + 1; ++row) 
                setLocation(row, col, playerToken);
        }
    }

    private void updateHorizontal(int startCol, int endCol, int row, Token playerToken, boolean left) {
        int i;
        if(left) {
            for(i = startCol; i > endCol - 1; --i)
                setLocation(row, i, playerToken);
        } else {
            for(i = startCol; i < endCol + 1; ++i) 
                setLocation(row, i, playerToken);
        }
    }

    private void updateDiagonal(int startRow, int endRow, int startCol, int endCol, Token playerToken, boolean upper, boolean left) {
        int k = startRow;
        int i;
        if(upper && left) {
            for(i = startCol; i > endCol - 1; ++i) {
                setLocation(k, i, playerToken);
                ++k;
            }
        } else if(upper && !left) {
            for(i = startCol; i > endCol - 1; --i) {
                setLocation(k, i, playerToken);
                ++k;
            }
        } else if(!upper && left) {
            for(i = startCol; i < endCol + 1; ++i) {
                setLocation(k, i, playerToken);
                --k;
            }
        } else if(!upper && !left) {
            for(i = startCol; i < endCol + 1; --i) {
                setLocation(k, i, playerToken);
                --k;
            }
        }
    }

    private boolean isDiagonalValid(int row, int col, Token playerToken, boolean update) {
        Token opponentToken = this.getOpponentToken(playerToken);
        boolean upperLeft = false;
        boolean upperRight = false;
        boolean lowerLeft = false;
        boolean lowerRight = false;
        int endRow;
        int endCol;
      
        // 
        // Uppert left to lower right
        //
        
        endRow = row;
        endCol = col;
        while(endRow < rows - 1 && endCol < cols - 1) {
            endRow++;
            endCol++;

            if (boardLocations[endRow][endCol] == playerToken ||
                (endRow + 1 == rows) || (endCol + 1 == cols)) 
                break;

            if (boardLocations[endRow][endCol] == opponentToken && 
                boardLocations[endRow + 1][endCol + 1] == playerToken) {
                upperLeft = true;
                if(update) 
                    updateDiagonal(row, endRow, col, endCol, playerToken, true, true);

                break;
            }
        } 

        //
        // Upper Right
        //
        
        endRow = row;
        endCol = col;

        while(endCol > 0 && endRow < rows - 1) {
            ++endRow;
            --endCol;

            if (boardLocations[endRow][endCol] == playerToken ||
                (endRow + 1 == rows) || (endCol - 1 == -1))
                break;

            if (boardLocations[endRow][endCol] == opponentToken && 
                boardLocations[endRow + 1][endCol - 1] == playerToken) {
                upperRight = true;
                if(update) 
                    updateDiagonal(row, endRow, col, endCol, playerToken, true, false);
                break;
            }
        } 

        //
        // Lower Left
        //
        
        endRow = row;
        endCol = col;

        while(endRow > 0 && endCol < cols - 1) {
            --endRow;
            ++endCol;

            if (boardLocations[endRow][endCol] == playerToken ||
                (endRow - 1 == -1) || (endCol + 1 == rows)) 
                break;

            if (boardLocations[endRow][endCol] == opponentToken && 
                boardLocations[endRow - 1][endCol + 1] == playerToken) {
                lowerLeft = true;
                if(update) 
                    this.updateDiagonal(row, endRow, col, endCol, playerToken, false, true);

                break;
            }
        } 
        
        //
        // Lower Right
        //

        endRow = row;
        endCol = col;

        while(endRow > 0 && endCol > 0) {
            --endRow;
            --endCol;

            if (boardLocations[endRow][endCol] == playerToken ||
                (endRow - 1 == -1) || (endCol - 1 == -1)) 
                break;

            if (boardLocations[endRow][endCol] == opponentToken && 
                boardLocations[endRow - 1][endCol - 1] == playerToken) {
                lowerRight = true;
                if(update) {
                    this.updateDiagonal(row, endRow, col, endCol, playerToken, false, false);
                }
                break;
            }
        } 

        return upperLeft || upperRight || lowerLeft || lowerRight;
    }

    private boolean isHorizontalValid(int row, int col, Token playerToken, boolean update) {
        Token opponentToken = this.getOpponentToken(playerToken);
        boolean rightToLeft = false;
        boolean leftToRight = false;
        int endRow = row;
        int endCol = col;

        //
        // Left to Right
        //
        
        while (endCol < cols  - 1 && 
               boardLocations[endRow][endCol + 1] != Token.EMPTY) {
            ++endCol;
            if (boardLocations[endRow][endCol] == playerToken ||
                (endCol + 1 == cols)) 
                break;

            if (boardLocations[endRow][endCol] == opponentToken && 
                boardLocations[endRow][endCol + 1] == playerToken) {
                leftToRight = true;
                if(update) 
                    this.updateHorizontal(col, endCol, row, playerToken, false);
                
                break;
            }
        } 

        // 
        // Right to Left
        //
        
        endCol = col;

        while (endCol > 0 && 
               boardLocations[endRow][endCol - 1] != Token.EMPTY) {
            --endCol;
            if(boardLocations[endRow][endCol] == playerToken ||
               (endCol - 1 == -1)) 
                break;

            if (boardLocations[endRow][endCol] == opponentToken && 
                boardLocations[endRow][endCol - 1] == playerToken) {
                rightToLeft = true;
                if(update) 
                    this.updateHorizontal(col, endCol, row, playerToken, true);

                break;
            }
        } 

        return rightToLeft || leftToRight;
    }

    private boolean isVerticalValid(int row, int col, Token playerToken, boolean update) {
        Token opponentToken = this.getOpponentToken(playerToken);
        boolean topToBottom = false;
        boolean bottomToTop = false;
        int endRow = row;
        int endCol = col;

        //
        // Top to Bottom
        //
        
        while (endRow < rows - 1 &&
               boardLocations[endRow + 1][endCol] != Token.EMPTY) {
            ++endRow;
            if (boardLocations[endRow][endCol] == playerToken || 
                endRow + 1 == rows) 
                break;

            if (boardLocations[endRow][endCol] == opponentToken && 
                boardLocations[endRow + 1][endCol] == playerToken) {
                topToBottom = true;
                if(update) 
                    updateVertical(row, endRow, col, playerToken, false);

                break;
            }
        } 

        endRow = row;

        // 
        // Bottom to Top
        //
        
        while (endRow > 0 && 
               boardLocations[endRow - 1][endCol] != Token.EMPTY) {
            --endRow;
            if (boardLocations[endRow][endCol] == playerToken ||
                (endRow - 1 == -1)) 
                break;

            if(boardLocations[endRow][endCol] == opponentToken && 
               boardLocations[endRow - 1][endCol] == playerToken) {
                bottomToTop = true;
                if(update) 
                    updateVertical(row, endRow, col, playerToken, true);

                break;
            }
        } 

        return topToBottom || bottomToTop;
    }
    
    private void initBoard() {
        for(int i = 0; i < this.rows; ++i) {
            for(int k = 0; k < this.cols; ++k) 
                this.boardLocations[i][k] = Token.EMPTY;
        }
        initBoardPieces();
    }
    
    //
    // Abstract methods
    //
    
    abstract public Token alternateToken(Token token);
    abstract protected void initBoardPieces();
    
    //
    // Member fields
    //
    
    private int rows;
    private int cols;
    private Token[][] boardLocations;
    private Map<Piece, Token> usedLocationsMap;
}

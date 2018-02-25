package mdza.blockcapturegame;
public class BoardModeSingle extends Board {

    public BoardModeSingle() { super(); }

    @Override
    protected void initBoardPieces() {
        setLocation(3, 3, Token.RED);
        setLocation(3, 4, Token.BLACK);
        setLocation(4, 3, Token.BLACK);
        setLocation(4, 4, Token.RED);
    }
       
    @Override 
    public Token getOpponentToken(Token token) {
        return token == Token.RED ? Token.BLACK : Token.RED;
    }
    
    @Override public Token alternateToken(Token token) {
        return getOpponentToken(token);
    }
}

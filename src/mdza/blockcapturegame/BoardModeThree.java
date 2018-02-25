package mdza.blockcapturegame;
public class BoardModeThree extends Board {
    
    public BoardModeThree() { super(10,9); }

    @Override
    public Token getOpponentToken(Token token) {
        switch(token) {
            case RED: return Token.BLACK;
            case BLACK: return Token.ORANGE;
            case ORANGE: return Token.RED;
            default: return Token.EMPTY;
        }
    }

    @Override
    protected void initBoardPieces() {
        setLocation(4, 3, Token.RED);
        setLocation(4, 4, Token.BLACK);
        setLocation(4, 5, Token.ORANGE);
        setLocation(5, 3, Token.ORANGE);
        setLocation(5, 4, Token.RED);
        setLocation(5, 5, Token.BLACK);
    }

    @Override
    public Token alternateToken(Token token) {
        return getOpponentToken(token);
    }
}

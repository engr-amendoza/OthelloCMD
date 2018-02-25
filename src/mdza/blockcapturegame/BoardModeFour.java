package mdza.blockcapturegame;
public class BoardModeFour extends Board {

    public BoardModeFour() { super(10, 10); }

    @Override
    public Token getOpponentToken(Token token) {
        switch(token) {
            case RED: return Token.BLACK;
            case BLACK: return Token.ORANGE;
            case ORANGE: return Token.GREEN;
            case GREEN: return Token.RED;
            default: return Token.EMPTY;
        }
    }

    @Override
    protected void initBoardPieces() {
        setLocation(4, 3, Token.RED);
        setLocation(4, 4, Token.BLACK);
        setLocation(4, 5, Token.ORANGE);
        setLocation(4, 6, Token.GREEN);
        setLocation(5, 3, Token.ORANGE);
        setLocation(5, 4, Token.RED);
        setLocation(5, 5, Token.BLACK);
        setLocation(5, 6, Token.ORANGE);
    }

    @Override
    public Token alternateToken(Token token) {
        return getOpponentToken(token);
    }
}

package uniltiranyu.examples.games.squares;

import speco.array.Array;
import uniltiranyu.Action;
import uniltiranyu.AgentProgram;
import uniltiranyu.Percept;

public class Esteban implements AgentProgram {
	
	protected String color;
    protected int[][] board = null;
    protected int size = 0;
    protected int[] lastBox = new int[2];
    protected int phase = 0;
	
    public Esteban(String color) {
    	this.color = color;
    	this.lastBox[0] = 0;
    	this.lastBox[1] = 1;
	}
    
	@Override
	public Action compute(Percept p) {
		long time = (long)(200 * Math.random());
        try{
           Thread.sleep(time);
        }catch(Exception e){}
		if(size == 0)
        	// Gets the size of the board
        	size = Integer.parseInt((String)p.get(Squares.SIZE));
        if(board == null) {
        	board = new int[size][size];
        	for( int i=0; i<size; i++ ){
                board[i][0] = Board.LEFT;
                board[i][size-1] = Board.RIGHT;
            }
            for( int i=0; i<size; i++ ){
                board[0][i] |= Board.TOP;
                board[size-1][i] |= Board.BOTTOM;
            }
//            for(int i = 0;i < size;i++) {
//            	for(int j = 0;j < size;j++) {
//            		System.out.print(board[i][j] + " ");
//            	}
//            	System.out.println();
//            }
        } else {
        	
        }
        
        // Determines if it is the agents turn
        if( p.get(Squares.TURN).equals(color) ){
        	// Esteban turn
//        	System.out.println("Esteban turn");
        	Array<String> moves = new Array<String>();
        	switch(phase) {
        		case 0:
        			int y = 0, x = 0;
        			for(int i = lastBox[0];i < size;i++) {
        				for(int j = lastBox[1];j < size;j++) {
        					if(this.lines(i, j, p) == 1 || this.lines(i, j, p) == 0) {
        						System.out.println("Entra acá");
        						if(((String) p.get(i + ":" + j + ":" + Squares.RIGHT)).equals(Squares.FALSE)) {
        							if(j + 1 != size) {
	        							if(this.lines(i, j + 1, p) == 1 || this.lines(i, j + 1, p) == 0)
	        								moves.add(Squares.RIGHT);
        							}
        						}
        						if(((String) p.get(i + ":" + j + ":" + Squares.BOTTOM)).equals(Squares.FALSE)) {
        							if(i + 1 != size) {
	        							if(this.lines(i + 1, j, p) == 1 || this.lines(i + 1, j, p) == 0)
	        								moves.add(Squares.BOTTOM);
        							}
        						}
        						if(moves.size() != 0) { 
        							lastBox[1] = j + 1 % size;
        							lastBox[0] = j + 1 == size? i + 1 : i;
        							y = i;
        							x = j;
        							break;
        						}
        					}
        				}
        				if(moves.size() != 0) break;
        				if(i == 7) phase = 1;
        				lastBox[1] = 0;
        			}
        			try {
                		String move = moves.get((int) (Math.random() * moves.size()));
                        return new Action(y + ":" + x + ":" + move);
        			} catch (Exception e) {}
        			
        			break;
        		case 1:
        			// min-max
        			break;
        		default:
        			break;
        	}
        	return new Action(lastBox[0] + ":" + lastBox[1] + ":" + "");
        } else {
        	// Opponents turn
        }
		return new Action(Squares.PASS);
	}
	
	private int lines(int i, int j, Percept p){
		int c = ((String) p.get(i + ":" + j + ":" + Squares.LEFT)).equals(Squares.TRUE)? 1 : 0;
		c += ((String) p.get(i + ":" + j +":" + Squares.TOP)).equals(Squares.TRUE)? 1 : 0;
		c += ((String) p.get(i + ":" + j +":" + Squares.RIGHT)).equals(Squares.TRUE)? 1 : 0;
		c += ((String) p.get(i + ":" + j +":" + Squares.BOTTOM)).equals(Squares.TRUE)? 1 : 0;
		return c;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}
	
}

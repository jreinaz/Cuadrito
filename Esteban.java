package uniltiranyu.examples.games.squares;

import java.util.ArrayList;
import java.util.Arrays;

import speco.array.Array;
import uniltiranyu.Action;
import uniltiranyu.AgentProgram;
import uniltiranyu.Percept;

public class Esteban implements AgentProgram {
	
	protected String color;
    protected int[][] board = null;
    protected int[][] values = null;
    protected int size = 0;
    protected int[] lastBox = new int[2];
    protected int phase = 0;
    protected int cuenta;
    protected boolean recheck= true;
	
    public Esteban(String color) {
    	this.color = color;
    	this.lastBox[0] = 0;
    	this.lastBox[1] = 1;
	}
    
    private void actualizarTablero(Percept p) {
    	for(int i = 0;i < size;i++) {
    		for(int j = 0;j < size;j++) {
    			if(((String)p.get(i+":"+j+":"+Squares.LEFT)).equals(Squares.TRUE))
    				board[i][j] |= Board.LEFT;
    			if(((String)p.get(i+":"+j+":"+Squares.TOP)).equals(Squares.TRUE))
    				board[i][j] |= Board.TOP;
    			if(((String)p.get(i+":"+j+":"+Squares.BOTTOM)).equals(Squares.TRUE))
    				board[i][j] |= Board.BOTTOM;
    			if(((String)p.get(i+":"+j+":"+Squares.RIGHT)).equals(Squares.TRUE))
    				board[i][j] |= Board.RIGHT;
    		}
    	}
    }
    
	@Override
	public Action compute(Percept p) {
//		long time = (long)(200 * Math.random());
//        try{
//           Thread.sleep(time);
//        }catch(Exception e){}
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
        }
        
        // Determines if it is the agents turn
        if( p.get(Squares.TURN).equals(color) ){
        	// Esteban turn
        	Array<String> moves = new Array<String>();
        	switch(phase) {
        		case 0:
        			int y = 0, x = 0;
        			for(int i = lastBox[0];i < size;i++) {
        				for(int j = lastBox[1];j < size;j++) {
        					if(this.lines(i, j, p) < 2) {
        						if(((String) p.get(i + ":" + j + ":" + Squares.RIGHT)).equals(Squares.FALSE)) {
        							if(j + 1 != size) {
	        							if(this.lines(i, j + 1, p) <2)
	        								moves.add(Squares.RIGHT);
        							}
        						}
        						if(((String) p.get(i + ":" + j + ":" + Squares.BOTTOM)).equals(Squares.FALSE)) {
        							if(i + 1 != size) {
	        							if(this.lines(i + 1, j, p) <2)
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
        				if(i == size-1) phase = 2;
	    				lastBox[1] = 0;
        				if(moves.size() != 0) break;
        			}
        			try {
                		String move = moves.get((int) (Math.random() * moves.size()));
                        return new Action(y + ":" + x + ":" + move);
        			} catch (Exception e) {}
        			break;
        		case 1:
        			// revision
        			actualizarTablero(p);
        			phase = 2;
        			break; 	
		        	
        		case 2:
        			// min-max
        			actualizarTablero(p);
        			ArrayList<String> datos = new ArrayList<String>();
        			
        			for (int i = 0;i < size;i++) {
        				for (int j = 0;j < size;j++) {
        					System.out.println("\n" + i + " // " + j);
        					cuenta = 0;
        					values = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);

        					if (lines(i,j)==2)
	        					if(!((values[i][j] & Board.RIGHT)==Board.RIGHT)) {
	        						values[i][j] |= Board.RIGHT;
	        						values[i][j+1] |= Board.LEFT;
	        						check(i,j);
	        						if (recheck) check(i,j+1);
	        						recheck=true;
	        						datos.add(i+":"+j+":"+Squares.RIGHT+":"+cuenta);
	        						values = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
	        						cuenta = 0;
	    						}
	    						if(!((values[i][j] & Board.BOTTOM)==Board.BOTTOM)) {
	    							values[i][j] |= Board.BOTTOM;
	    							values[i+1][j] |= Board.TOP;
	    							check(i,j);
	    							if (recheck) check(i+1,j);
	    							recheck=true;
	    							datos.add(i+":"+j+":"+Squares.BOTTOM+":"+cuenta);
	    							values = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
	    							cuenta = 0;
	    						}
	    						if(!((values[i][j] & Board.LEFT)==Board.LEFT)) {
	    							values[i][j] |= Board.LEFT;
	    							values[i][j-1] |= Board.RIGHT;
	    							check(i,j);
	    							if (recheck) check(i,j-1);
	    							recheck=true;
	    							datos.add(i+":"+j+":"+Squares.LEFT+":"+cuenta);
	    							
	    							values = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
	    							cuenta = 0;
	    						}
	    						if(!((values[i][j] & Board.TOP)==Board.TOP)){
	    							values[i][j] |= Board.TOP;
	    							values[i-1][j] |= Board.BOTTOM;
	    							check(i,j);
	    							if (recheck) check(i-1,j);
	    							recheck=true;
	    							datos.add(i+":"+j+":"+Squares.TOP+":"+cuenta);
	    							values = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
	    							cuenta = 0;
	    						}
        				}
        			}
        			System.out.println(datos);
        			try{
        				return new Action(0 + ":" + 0 + ":" + Squares.TOP);
        			}catch (Exception e) {
        				
        			}

        			break;
        		default:
        			break;
        	}
        	return new Action(lastBox[0] + ":" + lastBox[1] + ":" + Squares.PASS);
        } else {
        	// Opponents turn
        }
        return new Action(lastBox[0] + ":" + lastBox[1] + ":" + Squares.PASS);
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
	
	protected int lines(int i, int j){
	      int c=(values[i][j] & Board.LEFT)==Board.LEFT?1:0;
	      c+=(values[i][j] & Board.TOP)==Board.TOP?1:0;
	      c+=(values[i][j] & Board.RIGHT)==Board.RIGHT?1:0;
	      c+=(values[i][j] & Board.BOTTOM)==Board.BOTTOM?1:0;
	      return c;
	}
	
	protected boolean closable(int i, int j){
        return lines(i,j)==3;
    }
    
    protected boolean closed(int i, int j){
        return lines(i,j)==4;
    }
    
    protected void check(int i, int j){ 
    	if(closed(i,j)) {
    		cuenta++;
    		recheck=false;
    	}else{
	    	if(closable(i,j)){
	        if( (values[i][j] & Board.LEFT)==0 ){
	            values[i][j] |= Board.LEFT;
	            values[i][j-1] |= Board.RIGHT;
	            System.out.print("L ");
	            cuenta++;
	            check(i,j-1);
	        }
	
	        if( (values[i][j] & Board.TOP)==0 ){
	            values[i][j] |= Board.TOP;
	            values[i-1][j] |= Board.BOTTOM;
	            System.out.print("T ");
	            cuenta++;
	            check(i-1,j);
	        }
	        
	        if( (values[i][j] & Board.RIGHT)==0 ){
	            values[i][j] |= Board.RIGHT;
	            values[i][j+1] |= Board.LEFT;
	            System.out.print("R ");
	            cuenta++;
	            check(i,j+1);
	        }
	
	        if( (values[i][j] & Board.BOTTOM)==0 ){
	            values[i][j] |= Board.BOTTOM;
	            values[i+1][j] |= Board.TOP;
	            System.out.print("B ");
	            cuenta++;
	            check(i+1,j);
	        }
    	}
      }
	}  
    
	
}

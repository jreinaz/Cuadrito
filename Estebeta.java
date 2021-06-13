package uniltiranyu.examples.games.squares;

import java.util.Arrays;

import speco.array.Array;
import uniltiranyu.Action;
import uniltiranyu.AgentProgram;
import uniltiranyu.Percept;

public class Estebeta implements AgentProgram {
	
	boolean xd = true;

	protected String color;
	protected int[][] board = null;
	protected int[][] values = null;
	protected int size = 0;
	protected int[] lastBox = new int[2];
	protected int phase = 0;
	protected int cuenta = 0;
	protected boolean recheck = true;
	protected Array<String> moves = new Array<>();
	
	protected int[] lastPosition = new int[2];
	protected boolean played = false;

	public Estebeta(String color) {
		this.color = color;
		this.lastBox[0] = 0;
		this.lastBox[1] = 1;
	}

	@Override
	public Action compute(Percept p) {

		if (size == 0) { // Gets the size of the board
			size = Integer.parseInt((String) p.get(Squares.SIZE));
			int x = (int) (size * 2);
			//phase = size > 4 ? 0:1;
			detenerse(x);
			// @ToDo probar size*1.5
		}
		if (board == null)
			createBoard();

		// Determines if it is the agents turn
		if (p.get(Squares.TURN).equals(color)) {
			// Esteban turn
//			System.out.println("Estebeta turno");
			played = false;
			while (!played) {
				switch (phase) {
				case 0:
					try {
						String jugada = phase0(p);
						//System.out.println(jugada);
						if (phase != 1 && played)
							return new Action(jugada);
					} catch (Exception e) {
					}
					break;
				case 1:
					// min-max
					// puntos=actualizarTablero(p);
					if(xd) {
						System.out.println("MOVES: ");
						for (String s:moves)
							System.out.print(s + " ");
						System.out.println();
					}
					xd=false;
					//phase = 2;
					break;
				case 2:
					actualizarTablero(p);
					Action move = phase2();
					moves.clear();
					try {
						return move;
					} catch (Exception e) {
					}
					break;
				default:
					break;
				}
			}
			return new Action(lastBox[0] + ":" + lastBox[1] + ":" + Squares.PASS);
		} else {
			// Opponents turn
		}
		return new Action(lastBox[0] + ":" + lastBox[1] + ":" + Squares.PASS);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

	protected void resetValues(int i, int j, String line) {
		recheck = true;
		moves.add(i + ":" + j + ":" + line + ":" + cuenta);
		values = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
		cuenta = 0;
	}

	protected void makeMove(int i, int j) {
		if ((values[i][j] & Board.RIGHT) != Board.RIGHT) {
			values[i][j] |= Board.RIGHT;
			values[i][j + 1] |= Board.LEFT;
			check(i, j);
			if (recheck)
				check(i, j + 1);
			resetValues(i, j, Squares.RIGHT);
		}
		if ((values[i][j] & Board.BOTTOM) != Board.BOTTOM) {
			values[i][j] |= Board.BOTTOM;
			values[i + 1][j] |= Board.TOP;
			check(i, j);
			if (recheck)
				check(i + 1, j);
			resetValues(i, j, Squares.BOTTOM);
		}
		if ((values[i][j] & Board.LEFT) != Board.LEFT) {
			values[i][j] |= Board.LEFT;
			values[i][j - 1] |= Board.RIGHT;
			check(i, j);
			if (recheck)
				check(i, j - 1);
			resetValues(i, j, Squares.LEFT);
		}
		if ((values[i][j] & Board.TOP) != Board.TOP) {
			values[i][j] |= Board.TOP;
			values[i - 1][j] |= Board.BOTTOM;
			check(i, j);
			if (recheck)
				check(i - 1, j);
			resetValues(i, j, Squares.TOP);
		}
	}

	protected Action phase2() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				cuenta = 0;
				values = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
				if (lines(i, j) == 2)
					makeMove(i, j);
			}
		}
		String min = Squares.PASS;
		int minMov = Integer.MAX_VALUE;
		for (String s : moves) {
			String[] aux = s.split(":");
			if (Integer.parseInt(aux[3]) < minMov) {
				minMov = Integer.parseInt(aux[3]);
				min = aux[0] + ":" + aux[1] + ":" + aux[2];
			}
		}
		if (!min.equals(Squares.PASS))
			played = true;
		return new Action(min);
	}

	protected String phase0(Percept p) {
		Array<String> jugadas = new Array<>();
		out: for (int i = lastBox[0]; i < size; i++) {
			for (int j = lastBox[1]; j < size; j++) {
				
				if (i >= lastPosition[0] && j >= lastPosition[1]) {
					if (jugadasDisponibles(i,j,p)<=5) {
						phase = 1; break out; 
					}
				
				}				 

				if (lines(i, j, p) < 2) {
					if (avalible(i, j, Squares.RIGHT, true, p)) {
						if (lines(i, j + 1, p) < 2)
							jugadas.add(i+":"+j+":"+Squares.RIGHT);
					}
					if (avalible(i, j, Squares.BOTTOM, false, p)) {
						if (lines(i + 1, j, p) < 2)
							jugadas.add(i+":"+j+":"+Squares.BOTTOM);
					}
					if (jugadas.size() != 0) {
						played = true;
						lastBox[1] = (j + 1) % size;
						lastBox[0] = j + 1 == size ? i + 1 : i;
						break out;
					}
				}
			}
			lastBox[1] = 0;
		}
		
		return jugadas.get((int)(Math.random()*jugadas.size()));
	}
	
	protected int jugadasDisponibles(int i, int j, Percept p) {
		moves.clear();
		Array<String> espejo = new Array<>();
		for (int a = i; a<size;a++) {
			for (int b = a==i? j:0;b<size;b++) {
				//System.out.println("a: "+a+" b: "+b);
				if (lines(a, b, p) < 2) {
					if (avalible(a, b, 0, p)) {
						int c = (b-1);
						if (lines(a, b - 1, p) < 2 && !espejo.contains(a + ":" + c + ":" + Squares.RIGHT)) {
							moves.add(a+":"+b+":"+Squares.LEFT);
							espejo.add(a+":"+(b-1)+":"+Squares.RIGHT);
						}
					}
					if (avalible(a, b, 1, p)) {
						int c = (a-1);
						if (lines(a - 1, b, p) < 2 && !espejo.contains(c + ":" + b + ":" + Squares.BOTTOM)) {
							moves.add(a+":"+b+":"+Squares.TOP);
							espejo.add((a-1)+":"+b+":"+Squares.BOTTOM);
						}
					}
					if (avalible(a, b, 2, p)) {
						int c = (b+1);
						if (lines(a, b + 1, p) < 2 && !espejo.contains((a) + ":" + c + ":" + Squares.LEFT)) {
							moves.add(a+":"+b+":"+Squares.RIGHT);
							espejo.add(a+":"+(b+1)+":"+Squares.LEFT);
						}
					}
					if (avalible(a, b, 3, p)) {
						int c = (a+1);
						if (lines(a + 1, b, p) < 2 && !espejo.contains(c + ":" + b + ":" + Squares.TOP)) {
							moves.add(a+":"+b+":"+Squares.BOTTOM);
							espejo.add((a+1)+":"+b+":"+Squares.TOP);
						}
					}
				}
			}
		}
		System.out.println(moves.size());
		System.out.println(espejo.size());
		System.out.println("ESPEJO: ");
		for (String s:espejo)
			System.out.print(s + " ");
		System.out.println();
		return moves.size();
	}

	protected boolean avalible(int y, int x, String line, boolean isRight, Percept p) {
		if (isRight)
			return ((String) p.get(y + ":" + x + ":" + line)).equals(Squares.FALSE) && x + 1 != size;
		return ((String) p.get(y + ":" + x + ":" + line)).equals(Squares.FALSE) && y + 1 != size;
	}
	
	protected boolean avalible(int y, int x, int dir, Percept p) {
		if (dir==0)
			return ((String) p.get(y + ":" + x + ":" + Squares.LEFT)).equals(Squares.FALSE) && x - 1 != size;//izquierda
		if (dir==1)
			return ((String) p.get(y + ":" + x + ":" + Squares.TOP)).equals(Squares.FALSE) && y - 1 != size;//arriba
		if (dir==2)
			return ((String) p.get(y + ":" + x + ":" + Squares.RIGHT)).equals(Squares.FALSE) && x + 1 != size;//derecha
		return ((String) p.get(y + ":" + x + ":" + Squares.BOTTOM)).equals(Squares.FALSE) && y + 1 != size;//abajo
	}

	protected int lines(int i, int j, Percept p) {
		int c = ((String) p.get(i + ":" + j + ":" + Squares.LEFT)).equals(Squares.TRUE) ? 1 : 0;
		c += ((String) p.get(i + ":" + j + ":" + Squares.TOP)).equals(Squares.TRUE) ? 1 : 0;
		c += ((String) p.get(i + ":" + j + ":" + Squares.RIGHT)).equals(Squares.TRUE) ? 1 : 0;
		c += ((String) p.get(i + ":" + j + ":" + Squares.BOTTOM)).equals(Squares.TRUE) ? 1 : 0;
		return c;
	}

	private int[] actualizarTablero(Percept p) {
		int[] puntos = new int[2];
		String s;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (((String) p.get(i + ":" + j + ":" + Squares.LEFT)).equals(Squares.TRUE))
					board[i][j] |= Board.LEFT;
				if (((String) p.get(i + ":" + j + ":" + Squares.TOP)).equals(Squares.TRUE))
					board[i][j] |= Board.TOP;
				if (((String) p.get(i + ":" + j + ":" + Squares.BOTTOM)).equals(Squares.TRUE))
					board[i][j] |= Board.BOTTOM;
				if (((String) p.get(i + ":" + j + ":" + Squares.RIGHT)).equals(Squares.TRUE))
					board[i][j] |= Board.RIGHT;
				s = (String) p.get(i + ":" + j);
				if (s.equals(color)) {
					board[i][j] |= Board.WHITE;
					puntos[0]++;
				} else if (!s.equals(Squares.SPACE)) {
					board[i][j] |= Board.BLACK;
					puntos[1]++;
				}
			}
		}
		return puntos;
	}

	protected void createBoard() {
		board = new int[size][size];
		for (int i = 0; i < size; i++) {
			board[i][0] = Board.LEFT;
			board[i][size - 1] = Board.RIGHT;
		}
		for (int i = 0; i < size; i++) {
			board[0][i] |= Board.TOP;
			board[size - 1][i] |= Board.BOTTOM;
		}
		// for(int i = 0;i < size;i++) {
		// for(int j = 0;j < size;j++) {
		// System.out.print(board[i][j] + " ");
		// }
		// System.out.println();
		// }
	}

	protected int lines(int i, int j) {
		int c = (values[i][j] & Board.LEFT) == Board.LEFT ? 1 : 0;
		c += (values[i][j] & Board.TOP) == Board.TOP ? 1 : 0;
		c += (values[i][j] & Board.RIGHT) == Board.RIGHT ? 1 : 0;
		c += (values[i][j] & Board.BOTTOM) == Board.BOTTOM ? 1 : 0;
		return c;
	}

	protected boolean closable(int i, int j) {
		return lines(i, j) == 3;
	}

	protected boolean closed(int i, int j) {
		return lines(i, j) == 4;
	}

	protected void check(int i, int j) {
		if (closed(i, j)) {
			cuenta++;
			recheck = false;
		} else {
			if (closable(i, j)) {
				if ((values[i][j] & Board.LEFT) == 0) {
					values[i][j] |= Board.LEFT;
					values[i][j - 1] |= Board.RIGHT;
					cuenta++;
					check(i, j - 1);
				}

				if ((values[i][j] & Board.TOP) == 0) {
					values[i][j] |= Board.TOP;
					values[i - 1][j] |= Board.BOTTOM;
					cuenta++;
					check(i - 1, j);
				}

				if ((values[i][j] & Board.RIGHT) == 0) {
					values[i][j] |= Board.RIGHT;
					values[i][j + 1] |= Board.LEFT;
					cuenta++;
					check(i, j + 1);
				}

				if ((values[i][j] & Board.BOTTOM) == 0) {
					values[i][j] |= Board.BOTTOM;
					values[i + 1][j] |= Board.TOP;
					cuenta++;
					check(i + 1, j);
				}
			}
		}
	}

	protected void detenerse(int a) {
		stop: for (int i = size - 1; i >= 0; i--) {
			for (int j = size - 1; j >= 0; j--) {
				cuenta++;

				if (cuenta == a) {
					lastPosition[0] = i;
					lastPosition[1] = j;
					break stop;
				}
			}
		}
	}

	protected void sacarJugadas() {
		int x, y;
		for (int i = 0; i < 20; i++) {
			while (true) {
				y = (int) (lastPosition[0] + (size - lastPosition[0]) * Math.random());
				x = (int) (size * Math.random());
				if (x == 8 || y == 8)
					continue;
				if ((values[y][x] & Board.LEFT) == 0) {
					moves.add(y + ":" + x + ":" + Squares.LEFT);
					break;
				}
				if ((values[y][x] & Board.TOP) == 0) {
					moves.add(y + ":" + x + ":" + Squares.TOP);
					break;
				}
				if ((values[y][x] & Board.BOTTOM) == 0) {
					moves.add(y + ":" + x + ":" + Squares.BOTTOM);
					break;
				}
				if ((values[y][x] & Board.RIGHT) == 0) {
					moves.add(y + ":" + x + ":" + Squares.RIGHT);
					break;
				}
			}
		}
	}

}

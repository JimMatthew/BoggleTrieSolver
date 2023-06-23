
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/*
 * This class uses a Trie to recursively search a boggle board to find all valid words
 * Instantiate with BoggleTrieSolver(). You must provide the dictionary as either HashSet<String> or List<String>
 * A boggle Board is to be represented as a 16 item long String Array, containing 1 letter per String
 * Call the solve method by passing a single board as a String[], or you can pass multiple boards 
 * as a List<String[]>. 
 * For single boards, the solve method will return a List<String>, a list of Strings, one String for each word found
 * For multiple boards, a List<List<String>> is returned, a List<String> for each board solved. 
 * NOTE: If no words are found, an empty List<String> is return
 * 
 * The function will begin recursively searching through all possible combinations of dice. 
 * Since this uses a trie, we can at each step check whether the branch we are on is a prefix to a known word
 * If it is not, than we can stop going any further down that path, as there is no possible word to be found
 * This substantially improves performance over a basic recursive DFS search because it eliminates exploring branches
 * that will never contain a valid word. This allows it to solve thousands of boards per second
 */
public class BoggleTrieSolver {
	
	private List<String> wordsFound = new ArrayList<String>();
	private String[][] board = new String[4][4];
	private int MaxWordLength = 16;
	private static TrieNodee root;
	static final int ALPHABET_SIZE = 26;
	
	
	public BoggleTrieSolver() {
		root = new TrieNodee();
	}
	
	public BoggleTrieSolver(HashSet<String> dictionary) {
		root = new TrieNodee();
		loadWordList(dictionary);
	}
	
	public BoggleTrieSolver(List<String> dictionary) {
		root = new TrieNodee();
		loadWordList(dictionary);
	}
	
    static class TrieNodee {
        TrieNodee[] children = new TrieNodee[ALPHABET_SIZE];
        boolean isEndOfWord;
         
        TrieNodee(){
            isEndOfWord = false;
            for (int i = 0; i < ALPHABET_SIZE; i++) {
            	children[i] = null;
            } 
        }
    };
      
    static boolean isEmpty(TrieNodee root) {
        for (int i = 0; i < ALPHABET_SIZE; i++)
            if (root.children[i] != null)
                return false;
        return true;
    }
    
    public void loadWordList(HashSet<String> set) {	
		for (String s : set) {
			insert(s);
		}
	}
    
    public void loadWordList(List<String> list) {
    	for (String s: list) {
    		insert(s);
    	}
    }
    
    public List<String> getWordsFound(){
		return wordsFound;
	}
	
    public List<String> solve(String[] board) {
    	if (board.length != 16) {
    		throw new IllegalArgumentException("Board Array must be 16 Strings long");
    	}
    	setBoard(board);
		long start = System.currentTimeMillis();
		Boolean[][] visited = new Boolean[4][4];
		resetVisited(visited);
		
		for (int i = 0; i < 4; i ++) {
			for (int j = 0; j < 4; j++) {
				solver(visited, "", i, j);
			}
		}
		long end = System.currentTimeMillis();
		double t = ((end-start)/1000.0);
		System.out.println("Time to solve using Trie: " + t + " seconds");	
		wordsFound = wordsFound.stream().distinct().collect(Collectors.toList());
		Collections.sort(wordsFound, new StringSort());
		return wordsFound;
	}
    
   
  //This method will accept a list of boards, and return a list of the words found for each board
    //List<String[]> boards is a List, where each String[] represents a board 
    //Returns a List, containing a List of Strings of words found for each board
    //Words in each List are deduplicated and sorted largest first. 
    
    public List<List<String>> solveList(List<String[]> boards) {
		List<List<String>> lls = new ArrayList<List<String>>();
		
		for (String[] sa : boards) {
			
			if (sa.length != 16) {
	    		throw new IllegalArgumentException("Board " + boards.indexOf(sa) + " does not contain 16 elements");
	    	}
			
			long start = System.currentTimeMillis();
			setBoard(sa);
			Boolean[][] visited = new Boolean[4][4];
			resetVisited(visited);
			
			for (int i = 0; i < 4; i ++) {
				
				for (int j = 0; j < 4; j++) {
					solver(visited, "", i, j);
				}
			}
			
			long end = System.currentTimeMillis();
			double t = ((end-start)/1000.0);
			System.out.println("Time to solve: " + t + " seconds");	
			wordsFound = wordsFound.stream().distinct().collect(Collectors.toList());
			Collections.sort(wordsFound, new StringSort());
			lls.add(wordsFound);
		}
		
		return lls;
	}
    
    //Recursive method used to find all words on a boggle board
    //This will perform a depth first search for all possible strings from starting pos, (row,col)
    //Since this is using a Trie, we can test our current string 'current' to see if it is a valid prefix
    //If it is not a valid prefix to any word, meaning there is nothing else on this branch, we can return and quit
    //searching as there is no valid word to be found from this position
    
    private void solver(Boolean[][] visited, String current, int row, int col) {
	    visited[row][col] = true;
	    current += board[row][col];
	    int level;
        int length = current.length();
        int index;
        TrieNodee pCrawl = root;
      
        for (level = 0; level < length; level++)  {
            index = current.charAt(level) - 'a';
            if (pCrawl.children[index] == null ) { //no more valid words on this branch
            	visited[row][col] = false;
            	return;
            }
            pCrawl = pCrawl.children[index];
        }
        if (pCrawl.isEndOfWord) {
        	wordsFound.add(current);
        }
	    
	    if (current.length() == MaxWordLength) {
	        visited[row][col] = false;
	        return;
	    }
	    int[] rows = { -1, 1, 0, 0, -1, 1, -1, 1 };
	    int[] cols = { 0, 0, -1, 1, -1, 1, 1, -1 };
	    
	    for (int i = 0; i < 8; i++) {
	        int newRow = row + rows[i];
	        int newCol = col + cols[i];
	        
	        if (isValidCell(newRow, newCol) && !visited[newRow][newCol]) {
	            solver(visited, current, newRow, newCol);
	        }
	    }
	    visited[row][col] = false;
	}
    
    private void setBoard(String[] s) {
		wordsFound = new ArrayList<String>();
		int c = 0;
		for (int i = 0; i < 4; i ++) {
			for (int j = 0; j < 4; j++) {
				board[i][j] = s[c];
				c++;
			}
		}
	}
    
    private static void insert(String key) {
    	if (key.equals(null)){
    		return;
    	}
        int level;
        int length = key.length();
        int index;
        TrieNodee pCrawl = root;
      
        for (level = 0; level < length; level++)  {
            index = key.charAt(level) - 'a';
            if (pCrawl.children[index] == null) {
            	pCrawl.children[index] = new TrieNodee();
            }
            pCrawl = pCrawl.children[index];
        }
        pCrawl.isEndOfWord = true;
    }
    
	private boolean isValidCell(int row, int col) {
	    return row >= 0 && row < 4 && col >= 0 && col < 4;
	}
	
    private void resetVisited(Boolean[][] b) {
		for (int i = 0; i < 4; i++) {
			for( int j = 0; j<4; j++) {
				b[i][j] = false;
			}
		}
	}
    
    private class StringSort implements Comparator<String>{
		public int compare(String a, String b){
			if(a.length()<b.length()){
				return 1;
			}
			// if size the same sort alphabetically
			if(a.length()==b.length()){
				  return a.compareTo(b);
			}
			if(a.length()>b.length()){
				return -1;
			}
		   return 1;
		}
	}
}

# Boggle

BoggleSolverTrie contains methods for solving Boggle boards. 
You must provide it a dictionary as a HashSet\<String> or List<String>. 

You then provide it a single board as a String[], or multiple boards as a List<String[]>, and it will return all words found on the board as List<String>, or all boards as List<List<String>>. 

It used a Trie data structure to hold the dictionary. The solve method uses a recursive depth first search to go through every possible combination of dice. 
Since this uses a Trie structure though, at every step we can check to see if we are either on the end of a word (and add it to the solved List), or if we are on a prefix to another word. 
If we are not on a valid prefix, we can return, and skip going any further down this branch, as there is no valid word to be found. 

This makes solving boggle boards very fast, with this approach, this method can solve 1000+ boards per second. 

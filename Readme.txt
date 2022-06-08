There are two solutions for this that I wanted to explore with you all

Java Solution

This solution utilizes A* search to find relevent word solutions. I like this solution, but one of the limitations of localized search algorithms is that
it can get stuck in one path due to a wonky heuristic. While this still will provide solutions, I wanted to dig in and see what other kinds of data structures might be out there for
this tyoe of problem. 

The args are currently disabled and I have a hard coded board in there, but if you would like you play with it, feel free to switch the commented out line in main

If I had more time to flesh this out, I would spend it in building out more tests around edge cases. For example, if we get a board that breaks the rules, or if we dont get the right sized board. 


Python Solution

While investigating how these types of problems are solved, I came across a data structure that I have not used in the past. That being a data Trie. a data trie breaks strings down into a k-ary
tree, and then iterates through the tree to find the word we are looking for. The reason this is so powerful is that child nodes are only strings that share the previous letters of that rest of the parent
branch. This allows us to greatly reduce our search time to find relevent words. Once this was implemented, all we need to do is build the data structure out by feeding it the words in our file, and then compare
possible words that can be built using our board. 

To execute this you can run python3 main.py <optional flags>

optional parameters for this are 
-- board (board letters) default is 'ota,mev,ihu,fsr' if unspecified 
-- dict (the dictionary file we are using) default is the words.txt file if unspecified 
-- len (the number of word solutions we are trying to solve for) default is 2 if unspecified 

sample output:

[['reformists'], ['shavous']]
[['vasiform'], ['mouthes']]
[['variform'], ['mouthes']]
[['favorites'], ['shamus']]
[['favorites'], ['southmost']]
[['favourites'], ['smash', 'shamash']]
[['favourites'], ['smith']]
[['favourites'], ['smashes']]
[['favourites'], ['smiths']]
[['favourites'], ['somaesthesis']]
[['favourites'], ['shmoes']]
[['favourites'], ['shamus']]
[['favourites'], ['shams']]
[['favourites'], ['smasher']]
[['favourites'], ['stroheim']]
[['favourites'], ['sisham']]
[['favourites'], ['sham']]
[['favourites'], ['southmost']]
[['favourites'], ['somaesthesia']]
[['farmhouses'], ['soviets']]
[['farmhouses'], ['sovietism']]
[['farmhouses'], ['soviet']]
[['farmhouses'], ['sovietise']]
[['fauvists'], ['stroheim']]
[['fauvist'], ['thermos', 'thermoses']]
[['fauvist'], ['thermoset']]
[['furthermost'], ['trivias']]
[['furthermost'], ['trivia']]
[['favoritism'], ['mouthes']]
[['favoritism'], ['mushes']]
[['favoritism'], ['musher']]
[['favouritism'], ['moshes']]
[['favouritism'], ['mouthes']]
[['favouritism'], ['mashes']]
[['favouritism'], ['mushes']]
[['favouritism'], ['masher']]
[['favouritism'], ['musher']]
[['sheaf'], ['favouritism']]
[['thereof'], ['favouritism']]
[['thereof'], ['fauvism']]
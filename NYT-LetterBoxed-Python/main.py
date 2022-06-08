import argparse
from typing import List, Set, Union
from collections import defaultdict

'''
a Trie data structure is a k-ary tree that is used for string comparisions over large data sets
child nodes of the tree are for words that share the previous letters of the word we are trying to find. Worst case search
is O(n) where we need to check every single value in a letter to find it. 
'''
class TrieDataStructure:
    def __init__(self, value: str, parent: Union['TrieDataStructure', None]):
        self.value = value
        self.parent = parent
        self.children = {}
        self.valid = False

    def get_word(self) -> str:
        if self.parent is not None:
            return self.parent.get_word() + self.value
        else:
            return self.value

class SolverLetterBoxed:
    def __init__(self, input_string: str, dictionary: str, len_threshold=3):
        self.input_string = input_string.lower()
        self.sides = {side for side in input_string.split(',')}
        self.puzzle_letters = {letter for side in self.sides for letter in side}
        self.len_threshold = len_threshold

        self.root = TrieDataStructure('', None)
        with open(dictionary) as f:
            for line in f.readlines():
                self.add_word(line.strip().lower())

        self.puzzle_words = self.find_words()
        self.puzzle_graph = defaultdict(lambda: defaultdict(lambda: defaultdict(list)))
        for word in self.puzzle_words:
            self.puzzle_graph[word[0]][word[-1]][frozenset(word)].append(word)

    def findPairedSolutions(self) -> List[List[str]]:
        all_solutions = []
        for first_letter in self.puzzle_letters:
            for last_letter in self.puzzle_letters:
                for letter_edge, edge_words in self.puzzle_graph[first_letter][last_letter].items():
                    all_solutions += self._find_solutions_inner([edge_words], letter_edge, last_letter)
        return all_solutions

    def recursiveStringIteration(self, node: TrieDataStructure, last_side: str) -> List[TrieDataStructure]:
        valid_nodes = [node] if node.valid else []
        if node.children:
            for next_side in self.sides - {last_side}:
                for next_letter in next_side:
                    if next_letter in node.children:
                        next_node = node.children[next_letter]
                        valid_nodes += self.recursiveStringIteration(next_node, next_side)
        return valid_nodes

    def add_word(self, word) -> None:
        node = self.root
        for char in word:
            if char not in node.children:
                node.children[char] = TrieDataStructure(char, node)
            node = node.children[char]
        node.valid = True

    def find_words(self) -> List[str]:
        all_valid_nodes = []
        for starting_side in self.sides:
            for starting_letter in starting_side:
                if starting_letter in self.root.children:
                    all_valid_nodes += self.recursiveStringIteration(self.root.children[starting_letter], starting_side)
        return [node.get_word() for node in all_valid_nodes]

    def _find_solutions_inner(self, path_words: List[List[str]], letters: Set[str], next_letter: str) -> List[List[List[str]]]:
        if len(letters) == 12:
            return [path_words]
        elif len(path_words) == self.len_threshold:
            return []

        solutions = []
        for last_letter in self.puzzle_graph[next_letter]:
            for letter_edge, edge_words in self.puzzle_graph[next_letter][last_letter].items():
                if letter_edge - letters:
                    solutions += self._find_solutions_inner(path_words + [edge_words], letters | letter_edge, last_letter)
        return solutions

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--board', default='ota,mev,ihu,fsr', type=str)
    parser.add_argument('--dict', default='words.txt', type=str)
    parser.add_argument('--len', default=2, type=int)
    args = parser.parse_args()

    puzzle = SolverLetterBoxed(args.board, args.dict, len_threshold=args.len)
    meta_solutions = puzzle.findPairedSolutions()
    full_count = 0
    for meta_solution in meta_solutions:
        count = 1
        print(meta_solution)
        for element in meta_solution:
            count *= len(element)
        full_count += count
    if len(meta_solutions) == 0:
        print("no board solutions found")



// This is the .h file you will edit and turn in.
// We have provided a minimal skeleton for you,
// but you must finish it as described in the spec.
// Also remove these comments here and add your own, as well as on the members.
// TODO: remove this comment header and replace it with your own

#ifndef _boggle_h
#define _boggle_h

#include <iostream>
#include <string>
#include "lexicon.h"
#include <set>
#include "grid.h"

using namespace std;

class Boggle {
public:
    const string DICTIONARY_FILE = "EnglishWords.dat";
    const int MIN_WORD_LENGTH = 4;
    const int BOARD_SIZE = 4;

    void resetGame();
    void resetVisitedPos();
    void fillTable(string& sGameBoard);
    void createRandomBoard();
    set<string> getCwords();
    void insetrToCwords(string &word);
    set<string> getPwords();
    void insetrToPwords(string &word);
    Lexicon getValidWords();
    void stringToUpper(string& string);
    string printBoard();
    void findPossibleWords();
    void findPossibleWordsFunc(int i, int j, string current);
    bool wIsgeneratableP(string input);
    bool IsgeneratableFunc(int i, int j, string input, string current);



private:
    Grid<char> gameBoard;
    Grid<bool> visitedPos;
    set<string> pWords, cWords;
    string errorMessage;
    Lexicon validWords = Lexicon(DICTIONARY_FILE);

};

#endif

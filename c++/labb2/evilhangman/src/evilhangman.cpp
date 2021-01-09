/**
*
*a program that is designed to play hangman.
* cheats by changing the word often in order to win.
*
*/
#include <iostream>
#include <string>
#include <fstream>
#include <map>
#include <vector>
#include <math.h>
#include <algorithm>
using namespace std;


const string ALPHABET  = "abcdefghijklmnopqrstuvwxyz";

/**
 *
 * gets inout from user (wordLength)
 *
*/
int getWordLen(const map<int, vector<string>> &WordMap){
    string StrWordLen;
    int wordLen;
    do{
        cout << "input a wordlength number?";
        cin >> StrWordLen;
        wordLen = atoi(StrWordLen.c_str()); // atoi does not throw error if value cannot be converted to int stoi does
    } while (WordMap.count(wordLen) != 1);

    return wordLen;
}

/**
 *
 * opens the file and makes a map containing string vectors based on word_length
 *
*/
void createWordMap(map<int, vector<string>>& WordMap){
    // open file
    string fileName = "dictionary.txt";
    std::fstream file;
    file.open(fileName);
    string line;

    // create wordMap
    while(!file.eof()){
        getline(file, line);
        if (line.length() == 0){ // if the length is 0 it is not a word, do not include it to WordMap
            continue; //
        }
        if(WordMap.count(line.length())){ // if there is already a key with line.length, then the word should be added to that vector
             WordMap.at(line.length()).push_back(line); // add words to vector with key line.len ()
        }else{
            vector<string> newVector;
            newVector.push_back(line); // add element at the end of the vector
            WordMap.insert(pair<int, vector<string>>(line.length(), newVector));
        }
    }
}

/**
 *
 * input from user number of guesses
 *
*/
int getNumberOfGuesses(){
    int numberOfGuesses;
    string strNumberOfGuesses;
    do{
        cout << "input number of guesses(choose number of guesses): ";
        cin >> strNumberOfGuesses;
        numberOfGuesses = atoi(strNumberOfGuesses.c_str());
    } while(!(numberOfGuesses > 0));
    return numberOfGuesses;

}


/**
 *
 * input from user char return guessed char
 *
*/
char getGuessedChar(vector<char> &AllGuessedChars, bool& isIncorrectGuess){
    char guessedChar;
    bool isValidAlphabet = false;
    isIncorrectGuess = false;
    do{
        cout << "guess a letter: ";
        cin >> guessedChar;
        for (unsigned j = 0; j < ALPHABET.length(); j++){ // valid char no =/?;.,
            if(ALPHABET.at(j) == guessedChar){
                isValidAlphabet = true;
                 break;
            }
        }
    }while(!isValidAlphabet && !guessedChar);

    if(isValidAlphabet && count(AllGuessedChars.begin(), AllGuessedChars.end(), guessedChar) == 0){
        AllGuessedChars.push_back(guessedChar);
        isIncorrectGuess = true;
    }

    return guessedChar;
}


/*
 *
 * find the biggest by the len of the second element in map
 *
*/
string findBiggest(map<string, vector<string>> &wordFamilies){
    string largestfamilyKey = "";
    int largestFamilySize = 0;
    for (auto itr = wordFamilies.begin(); itr != wordFamilies.end(); itr++){
        int size = (itr->second).size(); // the size of the second element
        if(size > largestFamilySize){
            largestfamilyKey = (itr->first);
            largestFamilySize = size;
        }
    }
    return largestfamilyKey;
}

/**
 *
 * divides words into word families and returns the largest word family
 *
*/
void getWordFamily(vector<string>& wordFamily, const char& guessedChar){
    map<string, vector<string>> wordFamilies;
    for(unsigned i = 0; i < wordFamily.size(); i++){
        string pattern;
        string word = wordFamily.at(i);
        for (unsigned j = 0; j < word.length(); j++){
            pattern += "-";
            if(word.at(j) == guessedChar){
                pattern+= guessedChar;
            }
        }
        if(wordFamilies.count(pattern)){
            wordFamilies.at(pattern).push_back(word);
        }else{
            vector<string> newWordFamily;
            newWordFamily.push_back(word);
            wordFamilies.insert(pair<string, vector<string>>(pattern, newWordFamily));
        }
    }

    wordFamily = wordFamilies.at(findBiggest(wordFamilies));
}




int main() {
    string playAgain = "yes";
    while(playAgain == "yes"){
        cout << "Welcome to Hangman." << endl;
        bool endOfGame = false;
        bool isIncorrectGuess;

        // a map with int as key and vector with words of the same len as key
        map<int, vector<string>> WordMap;
        createWordMap(WordMap);
        int wordLen = getWordLen(WordMap);
        vector<string> WordFamily = WordMap.at(wordLen);
        int numberOfGuesses = getNumberOfGuesses();
        string theWordThatNeedsToBeGuessed;
        // create spaces for different letters in the word
        for(int i=0; i< wordLen; i++){
            theWordThatNeedsToBeGuessed += "-";
        }
        vector<char> AllGuessedChars;
        string seeWordsLeft;
        do{
            cout << "do you want to see the words left(yes/no)? ";
            cin >> seeWordsLeft;
        } while(!(seeWordsLeft == "yes" || seeWordsLeft == "no"));

        //game loop
        while(!endOfGame){

            // info that prints out after each guess

            cout << "______________game information______________"<< endl;
            cout << "number of quesses : "<< numberOfGuesses << endl;
            cout << "all guessed letters are: ";
            for(unsigned i = 0; i < AllGuessedChars.size(); i++){
                cout << AllGuessedChars.at(i) << " ";
            }
            cout << endl;

            if(seeWordsLeft == "yes"){
                cout << "the words left are : " << endl;
                for (unsigned i = 0; i < WordFamily.size(); i++){
                    cout << WordFamily.at(i) << " ";
                }
                cout << endl;
            }
            cout << "guess the word: " << theWordThatNeedsToBeGuessed << endl;
            char guessedChar = getGuessedChar(AllGuessedChars, isIncorrectGuess);
            getWordFamily(WordFamily, guessedChar);
            bool isInWord = false;

            // all of the words has the same pattern so it is enough to check only one word
            string firstWord = WordFamily[0];
            for (unsigned i = 0; i < firstWord.length(); i++){
                if (firstWord[i] == guessedChar){
                    theWordThatNeedsToBeGuessed[i] = guessedChar;
                    isInWord = true;
                }
            }
            if(isIncorrectGuess && !isInWord){
                numberOfGuesses -= 1;
            }

            if(numberOfGuesses < 0){
                cout << "you lose! ";
                endOfGame = true;
            }
            if(theWordThatNeedsToBeGuessed.find("-") == std::string::npos ){
                cout << "you win the word was: " << theWordThatNeedsToBeGuessed << endl;;
                endOfGame = true;
            }

        }
        cout << "do you want to play again ? yes/ no ";
        cin >> playAgain;

    }
    return 0;
}

/**
  *finds a word chain between two words by changing
  * a letter at a time and check if it is a valid english word
 */
#include <fstream>
#include <iostream>
#include <string>
#include <queue>
#include <stack>
#include <set>
using namespace std;

const string ALPHABET  = "abcdefghijklmnopqrstuvwxyz";

/**
 * takes two words and finds a word chain between the two words and
 */

void openFile(set<string> &validWords){
    std::fstream file;
    file.open("dictionary.txt");

    string line;
    while(getline(file,line)){
        validWords.insert(line); // add the words to validWords set
    }
}

void getNeighbours(const string& w1, const set<string> &validWords, stack<string>& neighbours){
    for(unsigned i = 0; i < w1.length(); i++){// go through all indexs in w1
        string tempWord = w1;
        for (unsigned j = 0; j < ALPHABET.length(); j++){
            tempWord[i] = ALPHABET[j]; // change every index in tempword with a new alphabet
            if(validWords.count(tempWord) == 1){// check for valid word
                neighbours.push(tempWord);
            }
        }
    }
}

void WordChain(const string &w1, const string &w2){
    set<string> validWords; // all valid words
    set<string> repeatedWords; // all words that has been used
    openFile(validWords);
    stack<string> neighbours;

    queue<stack<string>> qu;
    stack<string> st;
    st.push(w1);
    repeatedWords.insert(w1); // add first word to set of all used words
    qu.push(st);
    while(!qu.empty()){
        stack<string> chain = qu.front(); // first stack in the queue
        qu.pop(); // delete the fisrt stack out of the queue
        if(chain.top() == w2){
            cout << "Chain from " << w2 << " back to " << w1 << " :"<< endl;
            while (!chain.empty()){
                 cout << chain.top()<< " ";
                 chain.pop();
            }
            cout << endl;
            cout << "Have a nice day" << endl;

        }else{
            getNeighbours(chain.top(), validWords, neighbours);
            while(!neighbours.empty()){
                string neighbour = neighbours.top();
                neighbours.pop();
                if(!repeatedWords.count(neighbour)){
                    stack<string> newStack = chain;
                    newStack.push(neighbour);
                    qu.push(newStack);
                    repeatedWords.insert(neighbour);
                }
            }
        }
    }
}


int main() {
    cout << "Welcome to TDDD86 Word Chain." << endl;
    cout << "If you give me two English words, I will transform the" << endl;
    cout << "first into the second by changing one letter at a time." << endl;
    cout << endl;

    cout << "Please type two words: ";
    string word1;
    string word2;
    cin >> word1 >> word2;
    WordChain(word1,word2);


    return 0;
}

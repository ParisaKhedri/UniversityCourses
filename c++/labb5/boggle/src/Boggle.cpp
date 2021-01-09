/*
 *
 *
*/

#include <sstream>
#include "Boggle.h"
#include "random.h"
#include "shuffle.h"
#include "strlib.h"
#include "lexicon.h"
#include "grid.h"

static const int NUM_CUBES = 16;      // the number of cubes in the game
static const int CUBE_SIDES = 6;      // the number of sides on each cube
static string CUBES[NUM_CUBES] = {    // the letters on all 6 sides of every cube
   "AAEEGN", "ABBJOO", "ACHOPS", "AFFKPS",
   "AOOTTW", "CIMOTU", "DEILRX", "DELRVY",
   "DISTTY", "EEGHNW", "EEINSU", "EHRTVW",
   "EIOSST", "ELRTTY", "HIMNQU", "HLNNRZ"
};


void Boggle::resetGame(){
    gameBoard = Grid<char>(BOARD_SIZE, BOARD_SIZE);
    visitedPos = Grid<bool>(BOARD_SIZE, BOARD_SIZE);
    resetVisitedPos();
    cWords.clear();
    pWords.clear();
}

void Boggle::resetVisitedPos(){
    for (int i = 0; i < BOARD_SIZE; i++){
        for (int j = 0; j < BOARD_SIZE; j++){
            visitedPos.set(i,j, false);
        }
    }
}


/*
 * fyller i tabbellen med inparam
*/
void Boggle::fillTable(string& sGameBoard){
    stringToUpper(sGameBoard);
    int ind = -1;
    for (int i = 0; i < BOARD_SIZE; i++){
        for (int j = 0; j < BOARD_SIZE; j++){
            gameBoard[i][j] = sGameBoard[ind+=1];

        }
    }
}


/*
 * skapar random board genom att först shuffle cubes och
 * välja ut 1 random char ut av varje 16 cube som finns
*/
void Boggle::createRandomBoard(){
    string sGameBoard;
    shuffle(CUBES, NUM_CUBES-1);
    for (int i = 0; i < NUM_CUBES; i++){
        char c = CUBES[i][randomInteger(0, CUBE_SIDES-1)];
        sGameBoard+=c;
    }
    fillTable(sGameBoard);

}

set<string> Boggle::getCwords(){
    return cWords;
}

void Boggle::insetrToCwords(string &word){
    cWords.insert(word);
}

set<string> Boggle::getPwords(){
    return pWords;
}

void Boggle::insetrToPwords(string &word){
    pWords.insert(word);
}

Lexicon Boggle::getValidWords(){
    return validWords;
}


void Boggle::stringToUpper(string& string){
    for(unsigned i = 0; i < string.length(); i++){
        if(!isupper(string[i])){
            string[i] = toupper(string[i]);
        }
    }
}


/*
 * skriver ut innehållet av gameboard griden
*/
string Boggle::printBoard(){
    string sGameBoard;
    int count = 0;
    for (int i = 0; i < BOARD_SIZE; i++){
        for (int j = 0; j < BOARD_SIZE; j++){
            sGameBoard += gameBoard[i][j];
            count++;
            if(count == BOARD_SIZE){
                sGameBoard += "\n";
                count = 0;
            }
        }
    }
    return sGameBoard;
}


/////////////////////////////////////////////////////////////////////////////////////////
/*
 * hittar alla ord som kan skapas av brädet o lägger in den i cwords set
*/
void Boggle::findPossibleWords(){
    resetVisitedPos();
    for (int i = 0; i < BOARD_SIZE; i++){
        for (int j = 0; j < BOARD_SIZE; j++){
            string c = "";
            resetVisitedPos();
            findPossibleWordsFunc(i, j, c);

        }
    }
}


void Boggle::findPossibleWordsFunc(int i, int j, string current){
    if(visitedPos[i][j]){
        return;
    }

    visitedPos[i][j] = true;
    current += gameBoard.get(i,j);

    if(!validWords.containsPrefix(current)){
        visitedPos[i][j] = false;
        return;
    }

    if(current.size() >= 4 && validWords.contains(current)){
        if(pWords.find(current) == pWords.end()){
            cWords.insert(current);
        }

    }
    for (int a = -1; a <= 1; a++){
        for (int b = -1; b <= 1; b++){
            if(!(a == 0 && b == 0) && gameBoard.inBounds(i+a, j+b) && visitedPos[i+a][j+b] != true){
                findPossibleWordsFunc(i+a,j+b, current);
            }
        }
    }
    visitedPos[i][j] = false;
}

//////////////////////////////////////////////////////////////////////////////////
/*
 * kollar om ett givet ord kan skapas på brädet
*/
bool Boggle::wIsgeneratableP(string input){
    for (int i = 0; i < BOARD_SIZE; i++){
        for (int j = 0; j < BOARD_SIZE; j++){
            if(gameBoard.get(i, j) == input[0]){
                resetVisitedPos();
                if(IsgeneratableFunc(i, j, input, string (1, input[0]))){ // skapa en str of len 1 första bokstav
                    return true;
                }
            }
        }
    }
    return false;
}


bool Boggle::IsgeneratableFunc(int i, int j, string input, string current){
    if(current == input){
         return true;
    }else{
        for (int a = -1; a <= 1; a++){
            for (int b = -1; b <= 1; b++){
                if(!(a == 0 && b == 0) && gameBoard.inBounds(i+a, j+b)){
                    visitedPos[i][j] == true;
                    if(visitedPos[i+a][j+b] != true && gameBoard.get(i+a, j+b) == input[current.size()]){
                        current += gameBoard.get(a+i, j+b);
                        if(IsgeneratableFunc(i+a,j+b,input, current)){
                            return true;
                        }
                    }
                    visitedPos[i][j] = false;
                }
            }
        }
    }
    return false;
}

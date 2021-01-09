/*
 *
*/
#include <cstdlib>
#include <iostream>
#include <iomanip>
#include <sstream>
#include "Boggle.h"
#include "bogglemain.h"
#include "strlib.h"
using namespace std;
// TODO: include any other header files you need

bool isPlayerTurn;
int pScore;
int cScore;

/*
 * för varje inmatat ord kontrollera att det
 * är minst fyra bokstäver långt, finns i den
 * engelska ordlistan, inte redan har inkluderats i
 * spelarens ordlista
 * och kan bildas pa brädet via närliggande kuber.
 * Meddelar användaren om något villkor inte håller.
*/
void checkPlayerGuess(string& input, Boggle &boggle){
    boggle.stringToUpper(input);
    if(input.size() > 0){
        if(input.size() < 4){
            cout << "too short len" << endl;
        }else if(!boggle.getValidWords().contains(input)){
            cout << "not a valid english word" << endl;
        }else if(boggle.getPwords().count(input)){
            cout << "alredy guessed" << endl;
        }else if(!boggle.wIsgeneratableP(input)){
            cout << "the word is not generatable" << endl;
        }else{
            boggle.insetrToPwords(input);
            cout << "You've found a new word! " << "\"" << input << "\"" << endl;
        }
    }
}

/*
 * kollar att userinpu ska ha 16 len samt vara en valid tecken
*/
bool checkBoardInput(string& userinput){
    if(userinput.length() != 16){
        cout << "That is not a valid 16-letter board String.Try again." << endl;
        return false;
    }else{
        for(unsigned i = 0; i < userinput.length(); i++){
            if (!(( userinput.at(i) >= 'a' && userinput.at(i) <= 'z') || (userinput.at(i) >= 'A' && userinput.at(i) <= 'Z'))){
                cout << "That is not a valid 16-letter board String.Try again." << endl;
                return false;
            }
        }
    }
    return true;
}

/*
 * tar in en 16 långt string o fyller i gameboard med den
*/
void createUserInputBoard(Boggle &boggle){
    string UserInput;
    do{
        cout << "Enter a 16 length string (a-z)" << endl;
        cin >> UserInput;
    }while(!checkBoardInput(UserInput));
    boggle.fillTable(UserInput);
}

/*
 * skriver ut primära infon
*/
void printInfo(Boggle &boggle){
    pScore = 0;
    cScore = 0;
    cout << boggle.printBoard() << endl;
    if(isPlayerTurn){
        string stringPwords = "{";
        for(string word : boggle.getPwords()){
            stringPwords += word + ", ";
            pScore += (word.length() - 3);
        }
        stringPwords += "}";
        cout << "Your words " <<"("<< boggle.getPwords().size() << ")" << stringPwords << endl;
        cout << "your score: " << pScore << endl;
    }else{
        string stringCwords = "{";
        for(string word : boggle.getCwords()){
            stringCwords += word + ", ";
            cScore += (word.length() - 3);
        }
        stringCwords += "}";
        cout << "My words " <<"("<< boggle.getCwords().size() << ")" << stringCwords << endl;
        cout << "My score: " << cScore << endl;
    }
}


void playerTurn(Boggle &boggle){
    string input;
    while(getline(cin, input)){
        cout << "type a word or press Enter to end your turn: ";
        getline(cin, input);
        checkPlayerGuess(input, boggle);
        if(input.empty()){
            break;
        }
        printInfo(boggle);
    }

    isPlayerTurn = false;
    cout << "it's my turn" << endl;
    boggle.findPossibleWords();
    if(boggle.getCwords().size() == 0 && boggle.getPwords().size() != 0){
        printInfo(boggle);
        cout << "WOW, you defeated me! Congratulations!";
        return;
    }else{
        printInfo(boggle);
        cout <<"Ha ha ha, I destroyed you. Better luck next time, puny human!";

    }

}


/*
 * Plays one game of Boggle using the given boggle game state object.
 */
void playOneGame(Boggle& boggle) {
    // game loop
    boggle.resetGame();
    isPlayerTurn = true;

    string ans;
    do{
        cout <<"Do you want to genarate a random board (y/n) ?" << endl;
        cin >> ans;
    }while (!(ans == "y" || ans == "n"));
    if(ans == "y"){
        boggle.createRandomBoard();
    }else if (ans == "n"){
        createUserInputBoard(boggle);
    }
    clearConsole();
    cout << "it's your turn! " << endl;
    printInfo(boggle);
    playerTurn(boggle);
}

/*
 * Erases all currently visible text from the output console.
 */
void clearConsole() {
#if defined(_WIN32) || defined(_WIN64)
    std::system("CLS");
#else
    // assume POSIX
    std::system("clear");
#endif
}

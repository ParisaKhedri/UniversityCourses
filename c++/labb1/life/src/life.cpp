/*
 * the game engine of the game of life
*/
#include <iostream>
#include "grid.h"
#include "lifeutil.h"
#include <string>
#include <fstream>
using namespace std;


/*
 * prints out the welcome message in console
*/
void welcome(){
    std::cout << "Welcome to the TDDD86 Game of Life,"  << std::endl;
    std::cout << "a simulation of the lifecycle of a bacteria colony."  << std::endl;
    std::cout << "Cells (X) live and die by the following rules:"  << std::endl;
    std::cout << "- A cell with 1 or fewer neighbours dies."  << std::endl;
    std::cout << "- Locations with 2 neighbours remain stable."  << std::endl;
    std::cout << "- Locations with 3 neighbours will create life."  << std::endl;
    std::cout << "- A cell with 4 or more neighbours dies."  << std::endl;
}


/*
 * creates a new line per row and adds the value o every column to it
 * print out the line
*/
void printGridInfo(const Grid<char> &grid){
    for(int i = 0; i < grid.numRows(); i++){
        string Line = "";
        for( int j = 0; j < grid.numCols(); j++){
            Line+= grid.get(i,j);
        }
        std::cout << Line << std::endl;
    }
}


/*
 * at a specific position check if there are some alive cell 'x'
*/
int numberOfX(const Grid<char> &grid, int row, int column){
    int x = 0;
    for (int i = -1; i <= 1; i++){
        for (int j = -1; j <= 1; j++){
            // the positon
            if((i == 0) & (j == 0)){
                continue;
                // not in bound crashes
            }else if(grid.inBounds(row+i, column+j) == false){
                continue;
                // in bound check if X
            }else if(grid.get(row+i, column+j) == 'X'){
                x+=1;
            }
        }
    }
    return x;
}


/*
 * prints out the next generation
*/
void tick(Grid<char> &grid){
    int rows = grid.numRows();
    int columns = grid.numCols();
    Grid <char> newGameBoard;
    newGameBoard = Grid<char>(rows,columns);
    for (int i = 0; i < rows; i++){
        for (int j = 0; j < columns; j++){
            // 2 neighbour the cell stays alive, (copy the value in newGameboard)
            if (numberOfX(grid, i, j) == 2){
                newGameBoard.set(i,j,grid.get(i,j));
                // 3 neighbour borns new cell
            }else if(numberOfX(grid, i, j) == 3){
                newGameBoard.set(i, j, 'X');
            }else{
                newGameBoard.set(i, j, '-');
            }
        }
    }
    //set the orginal grid to newGameboard
    grid = newGameBoard;
    printGridInfo(grid);

}

/*
 * tick the GameBoard show gameboard 100 ms clear console repeat
*/
void animate(Grid<char> &grid){
    while(true){
        tick(grid);
        pause(100);
        clearConsole();

    }
}


int main() {
    Grid<char> GameBoard;

    welcome();

    string fileName;
    cout << "Grid input file name?"; // input message
    cin >> fileName; // saves the input value

    std::fstream file;
    file.open(fileName); // opens the file


    string line; // temporary variable gets new value each time

    getline(file,line);
    int rows = stoi(line); // first line
    getline(file,line);
    int columns = stoi(line); // secound line

    GameBoard = Grid<char>(rows, columns);

    // saves each cell in GameBoard grid
    for (int i = 0; i < rows;i++){
        getline(file,line);
        for(int j = 0 ; j < columns; j++){
            GameBoard.set(i, j, line[j]);
        }
    }
    // prints the grid out in console
    printGridInfo(GameBoard);

    while(true){
        string answer;
        cout << "a)nimate, t)ick, q)uit?";
        cin >> answer;
        if ( answer == "a" ){
            animate(GameBoard);
        } else if(answer == "t"){
            tick(GameBoard);
        }else{
             std::cout << "Have a nice Life!"<< std::endl;
             break;
        }
    }

    return 0;
}

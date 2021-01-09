/*
 * tilelist
 */
#include "TileList.h"

TileList::TileList(){
    tileList = new Tile[maxSize];
}

TileList::~TileList(){
    delete[] tileList;
}

void TileList::addTile(Tile tile){
    if(size == maxSize){ // om det inte finns plats i arrayen
        size_t newSize = maxSize * 2;
        Tile* newTileList = new Tile[newSize];
        for(int i = 0; i < size; i++){
            newTileList[i] = tileList[i]; // kopierar inehållet till det nya tilelist

        }
        delete[] tileList;
        tileList = newTileList; //sätter taillist till nya tilelist vi skapade
        maxSize = newSize; // uppdatera maxSize
    }
    tileList[size] = tile; // lägg till inmatade tile i listan
    size +=1;
}

void TileList::drawAll(QGraphicsScene* scene) const{
    for(int i = 0; i < size; i++){
        tileList[i].draw(scene);
    }
}

int TileList::indexOfTopTile(int x, int y) const{
    int answer = -1;
    for(int i = size-1; i > -1; i--){ // gå genom listan baklänges
        if(tileList[i].contains(x,y)){
            answer = i;
            break;
        }
    }
    return answer;
}

void TileList::raise(int x, int y){
    int index = indexOfTopTile(x,y);
    if(index != -1){
        Tile theTile = tileList[index];
        for(int i = index; i < size; i++){ //gå genom lisatn börja på indexen
            tileList[i] = tileList[i+1]; // sätt element n+1 på platsen av element n
        }
        tileList[size-1] = theTile;
    }

}

void TileList::lower(int x, int y){
    int index = indexOfTopTile(x,y);
    if(index != -1){
        Tile theTile = tileList[index];
        for(int i = index; i > 0; i--){
            tileList[i] = tileList[i-1];
        }
        tileList[0] = theTile;

    }
}

void TileList::remove(int x, int y){
    int index = indexOfTopTile(x,y);
    if(index != -1){
        for(int i = index; i < size; i++){
            tileList[i] = tileList[i+1];
        }
    }
    size -= 1;
}

int TileList::numberOfElements(int x, int y) const{
    int numberofelement = 0; // har koll på hur många tile som ska tas bort
    for(int i = 0; i < size; i++){
        if(tileList[i].contains(x,y)){
            numberofelement += 1;
        }
    }
    return numberofelement;
}

void TileList::removeAll(int x, int y){
    int numberofelement = numberOfElements(x,y);
    for(int j = 0; j < numberofelement; j++){
        remove(x,y); // kallar på remove med samma x och y så många gånger som det finns element i positionen
    }

}

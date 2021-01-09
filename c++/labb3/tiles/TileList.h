/*
 *
 *headerfilen till TileList
 *
 */

#ifndef TILELIST_H
#define TILELIST_H

#include <QGraphicsScene>
#include "Tile.h"

class TileList {
public:
    /*
     *skapa ny lista
     */
    TileList();
    /*
     *ta bort listan
     */
    ~TileList();

    /*
     * lägger till tile i listan och förstorar listan vid behov
     */
    void addTile(Tile tile);
    /*
     *ritar alla tile i listan på QGraphicsScene
     * const modifierar tile-listan
     */
    void drawAll(QGraphicsScene* scene) const;
    /*
     * returnerar indexen av den första tile vid den inmatade positionen
     */
    int indexOfTopTile(int x, int y) const;
    /*
     *lägger första tilen i positionen x,y främst i listan (bakom andra tiles.)
     */
    void lower(int x, int y);
    /*
     * lägger tilen i positionen x,y sist i listan (främst av alla tiles)
     */
    void raise(int x, int y);
    /*
     * tar bort främsta tile som ligger på x,y positionen
     */
    void remove(int x, int y);
    /*
     * tar bort alla tile som ligger på positionen x,y
     */
    void removeAll(int x, int y);

private:
  Tile* tileList;
  int maxSize = 10;
  int size = 0;
  /*
   * hitta antal tile som finns på en viss x,y
   */
  int numberOfElements(int x, int y)const;
};

#endif // TILELIST_H

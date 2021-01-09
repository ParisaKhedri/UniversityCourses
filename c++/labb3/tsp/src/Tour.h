/*
 *
 */

#ifndef TOUR_H
#define TOUR_H

#include "Node.h"
#include "Point.h"

class Tour {
public:

    Tour();
    /*
     *skapar en tour
     */
    Tour(Point a, Point b, Point c, Point d);
    /*
     *free all memory used by list nodes
     */
    ~Tour();
    /*
     *print the tour to standard output
     */
    void show() const;
    /*
     *draw the tour on scene
     */
    void draw(QGraphicsScene* scene) const;
    /*
     *number of points on tour
     */
    int size() const;
    /*
     *return the total distance of the tour
     */
    double distance() const;
    /*
     *insert p using nearest neighbor heuristic
     */
    void insertNearest(Point p);
    /*
     *insert p using smallest increase heuristic
     */
    void insertSmallest(Point p);

private:
    Node *firstNode;

};

#endif // TOUR_H

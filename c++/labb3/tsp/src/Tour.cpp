/*
 *
 * skapar tour
 *
*/
#include <iostream>
#include "Tour.h"
#include "Node.h"
#include "Point.h"
#include <limits>
using namespace std;

Tour::Tour(){
    firstNode = nullptr; // tour i början ska ha längden noll
}


Tour::Tour(Point a, Point b, Point c, Point d){
    /*
     *             *a
     *      *b            *d
     *             *c
     */
    firstNode = new Node(a);
    firstNode->next = new Node(b,new Node(c, new Node(d, firstNode))); // cirkulär länkande lista (*ptr).next
}


Tour::~Tour(){
    if (firstNode == nullptr) {
           return;
    }else{
        Node* current = firstNode;
        do{
            Node* next = current->next;
            delete current;
            current = next;
        }while(current!= firstNode);
    }
}


void Tour::show() const{
    Node* current = firstNode;

    if (firstNode == nullptr) {
           return;
    }else{
        do{
            cout << current->point.toString() << endl;
            current = current->next;
        }while(current!= firstNode);
    }
}


void Tour::draw(QGraphicsScene *scene) const{
    scene->clear();
    Node* current = firstNode;

    if (firstNode == nullptr) {
           return;
    }else{
        do{
            current->point.drawTo(current->next->point, scene);
            current = current->next;
        }while(current!= firstNode);
    }
}


int Tour::size() const{
    Node* current = firstNode;
    int size = 0;

    if (firstNode == nullptr) {
           return 0;
    }else{
        do{
            size += 1;
            current = current->next;
        }while(current!= firstNode);
        return size;
    }
}


double Tour::distance() const{
    Node* current = firstNode;
    double distance = 0;
    if (firstNode == nullptr) {
           return 0;
    }else{
        do{
            distance += current->point.distanceTo(current->next->point);
            current = current->next;

        }while(current!= firstNode);
    }
    return distance;
}


void Tour::insertNearest(Point p){
    if(firstNode == nullptr){ // om ingen tour
        firstNode = new Node(p);
        firstNode->next = firstNode; // en tour av lengd 1
        return;
    }else{
        Node* current = firstNode;
        Node* nearestNode = current;
        double nearestDistance = p.distanceTo(current->point);

        do{
            double theDistance = p.distanceTo(current->next->point);
            if (theDistance < nearestDistance){
                nearestDistance = theDistance;
                nearestNode = current;
            }
            current = current->next;
        }while(current != firstNode);
        Node* newNode = new Node(p, nearestNode->next);
        nearestNode->next = newNode;
    }
}


void Tour::insertSmallest(Point p){
    if(firstNode == nullptr){
        firstNode = new Node(p);
        firstNode->next = firstNode;
        return;
    }else{
        Node* current = firstNode;
        Node* nearestNode = current;

        double minDistance = numeric_limits<double>::infinity(); // största tal som kan finnas
        do{
            /*
             *       *a   *b
             */
            double oldDist = current->point.distanceTo(current->next->point);

            /*
             *        *p
             *   *a        *b
             *
             */
            double newDist = current->point.distanceTo(p) + current->next->point.distanceTo(p);
            double difDistance = newDist - oldDist;
            if (difDistance < minDistance){
                minDistance = difDistance;
                nearestNode = current;
            }
            current = current->next;
        }while(current != firstNode);
        Node* newNode = new Node(p, nearestNode->next);
        nearestNode->next = newNode;
    }
}

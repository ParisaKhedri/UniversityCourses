/*
 *
 */

#include <QApplication>
#include <QGraphicsView>
#include <QGraphicsScene>
#include <fstream>
#include <iostream>
#include <algorithm>
#include <vector>
#include <chrono>
#include "Point.h"

// constants
static const int SCENE_WIDTH = 512;
static const int SCENE_HEIGHT = 512;


void render_points(QGraphicsScene* scene, const vector<Point>& points) {
    for(const auto& point : points) {
        point.draw(scene);
    }
}

void render_line(QGraphicsScene* scene, const Point& p1, const Point& p2) {
    p1.lineTo(scene, p2);
}

int main(int argc, char *argv[]) {
    QApplication a(argc, argv);

    // open file
    string filename = "input1600.txt";
    ifstream input;
    input.open(filename);

    // the vector of points
    vector<Point> points;

    // read points from file
    int N;
    int x;
    int y;

    input >> N;

    for (int i = 0; i < N; ++i) {
        input >> x >> y;
        points.push_back(Point(x, y));
    }
    input.close();

    // setup graphical window
    QGraphicsView *view = new QGraphicsView();
    QGraphicsScene *scene = new QGraphicsScene(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
    view->setScene(scene);
    // draw points to screen all at once
    render_points(scene, points);
    view->scale(1, -1); //screen y-axis is inverted
    view->resize(view->sizeHint());
    view->setWindowTitle("Brute Force Pattern Recognition");
    view->show();

    sort(points.begin(), points.end());
    auto begin = chrono::high_resolution_clock::now();

    for(auto pointIndex = 0; pointIndex < points.size(); pointIndex++){
        map<double, vector<Point>> lines; // slope some key vektor med alla som har samma slope
        Point Origo = points.at(pointIndex); // första pktn är alltid origo
        for(auto nextPointIndex = pointIndex+1; nextPointIndex < points.size(); nextPointIndex++){
            lines[Origo.slopeTo(points.at(nextPointIndex))].push_back(points.at(nextPointIndex)); // lägg till alla nextpoints med samma slope i vektorn
        }

        for(auto p : lines){
            if(p.second.size() > 2){ // minst 3 node första noden räknas med
                Point lastP = p.second.back(); // sista noden i vektorn
                render_line(scene, Origo, lastP); // drar linje mellan första och sista noden
                a.processEvents();
            }

        }

    }

    auto end = chrono::high_resolution_clock::now();
    cout << "Computing line segments took "
         << std::chrono::duration_cast<chrono::milliseconds>(end - begin).count()
         << " milliseconds." << endl;

    return a.exec(); // start Qt event loop
}

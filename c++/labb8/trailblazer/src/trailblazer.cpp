/*
 * labb8
*/

#include "costs.h"
#include "trailblazer.h"
#include "queue.h"
#include <vector>
#include "pqueue.h"
using namespace std;

/*
 * hittar path mellan start och end
*/
bool dfsBool(BasicGraph& graph, Vertex* start, Vertex* end, vector<Vertex*> &path){
    start->visited = true;
    start->setColor(GREEN);
    if (start == end) { // bas steg vi hittar en path från s till e
        path.push_back(start);
        return true;
    }else {
        for(Node* n: graph.getNeighbors(start)) {
            if (!n->visited) {
                if (dfsBool(graph, n, end, path)){// kallar på dfs med grannen som start node
                    path.push_back(start);// lägg till start i path
                    return true;
                }
            }
        }
        start->setColor(GRAY);
        return false;
    }
}

vector<Node *> depthFirstSearch(BasicGraph& graph, Vertex* start, Vertex* end) {
    graph.resetData();
    vector<Vertex*> path;
    dfsBool(graph, start, end, path);
    return path;

}

/*
 * hittar väg mellan start och end och returnerar en path som en vector med noder
*/

vector<Node *> breadthFirstSearch(BasicGraph& graph, Vertex* start, Vertex* end) {
    graph.resetData();

    vector<Vertex*> path;
    Queue<Vertex*>  queue;
    queue.enqueue(start);

    while(!queue.isEmpty()){
        Node* node = queue.dequeue();
        //set till visited
        node->visited = true;
        node->setColor(GREEN);
        if(node == end){ // bassteg hittat path
            while(node!= nullptr){
                path.push_back(node);
                node = node->previous;
            }
            // swap vector så den börjar från start till end
            std::vector<Vertex*> r(path.rbegin(), path.rend());
            path.swap(r);
            break;
        }else{
            for(Node* n: graph.getNeighbors(node)){
                if(!n->visited){
                    n->visited = true; // set till visited
                    n->previous = node;
                    n->setColor(YELLOW);
                    queue.enqueue(n);
                }
            }
        }
    }

    return path;
}


/*
 *hittar den minsta kostnaden från start till slut node
*/
vector<Node *> dijkstrasAlgorithm(BasicGraph& graph, Vertex* start, Vertex* end) {
    graph.resetData();

    vector<Vertex*> path;
    PriorityQueue<Vertex*> queue;

    for (Node* node : graph.getNodeSet()){
        node->cost = numeric_limits<double>::infinity();
        queue.enqueue(node, node->cost);
    }

    start->cost = 0;
    queue.changePriority(start, start->cost);

    // första ska ha prioritet 0
    queue.enqueue(start, 0);
    while(!queue.isEmpty()){
        //Removes and returns the highest priority value.
        Vertex* node = queue.dequeue();
        //set till visited
        node->visited = true;
        node->setColor(GREEN);

        if(node == end){ // bassteg hittat path
            while(node!= nullptr){
                path.push_back(node);
                node = node->previous;
            }
            // swap vector så den börjar från start till end
            vector<Vertex*> r(path.rbegin(), path.rend());
            path.swap(r);
            break;
        }else{
            for(Node* n: graph.getNeighbors(node)){
                if(!n->visited){
                    double cost = node->cost + graph.getArc(node, n)->cost;

                    if(cost < n->cost){
                       n->cost = cost;
                       n->previous = node;
                       n->setColor(YELLOW);
                       queue.changePriority(n, cost);
                    }
                }
            }
        }

    }

    return path;
}

/*
 *heuristic ser till att noder som leder åt rätt håll får lägre kostnad
 * och detta leder till en effektivare sökning då ointressanta noder behandlas inte
*/

vector<Node *> aStar(BasicGraph& graph, Vertex* start, Vertex* end) {
    graph.resetData();

    vector<Vertex*> path;
    PriorityQueue<Vertex*> queue;

    for (Node* node : graph.getNodeSet()){
        node->cost = numeric_limits<double>::infinity();
        queue.enqueue(node, node->cost);
    }

    start->cost = 0;
    queue.changePriority(start, start->cost);

    // första ska ha prioritet 0
    queue.enqueue(start, 0);
    while(!queue.isEmpty()){
        //Removes and returns the highest priority value.
        Vertex* node = queue.dequeue();
        //set till visited
        node->visited = true;
        node->setColor(GREEN);

        if(node == end){ // bassteg hittat path
            while(node!= nullptr){
                path.push_back(node);
                node = node->previous;
            }
            // swap vector så den börjar från start till end
            vector<Vertex*> r(path.rbegin(), path.rend());
            path.swap(r);
            break;
        }else{
            for(Node* n: graph.getNeighbors(node)){
                if(!n->visited){
                    double cost = node->cost + graph.getArc(node, n)->cost;

                    if(cost < n->cost){
                       n->cost = cost;
                       n->previous = node;
                       n->setColor(YELLOW);
                       queue.changePriority(n, cost + n->heuristic(end));
                    }
                }
            }
        }

    }

    return path;
}

/**
 * Copyright (C) David Wolfe, 1999.  All rights reserved.
 * Ported to Qt and adapted for TDDD86, 2015.
 */

#include "GameState.h"
#include "utilities.h"
#include "constants.h"
using namespace std;

GameState::GameState(){}

GameState::~GameState(){
    for (unsigned i = 0; i < robots.size(); i++){
        delete robots[i];
    }
}

/*
 * kopierar robots o hero från inparam till this
*/
GameState::GameState(const GameState &g){
    for(Robot* r: g.robots){
        this->robots.push_back(r->clone());
    }
    this->hero = g.getHero();
}

/*
 * kopiera inparam g i this (g och this ska inte vara samma)
 */
GameState& GameState::operator = (const GameState &g){
    if(this != &g){
        this->hero = g.hero;
        for (Robot* robot : this->robots){
            delete robot;
        }
        this->robots.clear();
        for(Robot *robot: g.robots){
            this->robots.push_back(robot->clone());
        }
    }

    return *this;

}

GameState::GameState(int numberOfRobots) {
    for (int i = 0; i < numberOfRobots; i++) {
        Robot *robot = new Robot;
        do {
            //delete robot;
            //robot = new Robot();
            robot->teleport();
        }while (!isEmpty (*robot));
        robots.push_back(robot);
    }
    teleportHero();
}

void GameState::draw(QGraphicsScene *scene) const {
    scene->clear();
    for (size_t i = 0; i < robots.size(); ++i)
        robots[i]->draw(scene);
    hero.draw(scene);
}

void GameState::teleportHero() {
    do hero.teleport();
    while (!isEmpty(hero));
}

void GameState::moveRobots() {
    for (unsigned int i = 0; i < robots.size(); i++)
        robots[i]->moveTowards(hero);
}

int GameState::countCollisions() {
    int numberDestroyed = 0;
    unsigned int i = 0;
    while (i < robots.size()) {
        if(robots[i]->getCollisionCount() == 1){
            bool hitJunk = junkAt (*robots[i]);
            bool collision = (countRobotsAt (*robots[i]) > 1);
            if (hitJunk || collision) {
                if (!hitJunk){
                    robots.push_back (new Junk(*robots[i]));
                }
                delete robots[i];
                robots[i] = robots[robots.size()-1];
                robots.pop_back();
                numberDestroyed++;
            }else i++;
        } else i++;
    }
    return numberDestroyed;
}

bool GameState::anyRobotsLeft() const {
    bool robotFound = false;
    for(unsigned i = 0; i < robots.size(); i++){
        if(robots[i]->getCollisionCount() == 1){
            robotFound = true;
            break;
        }
    }
    return robotFound;
}

bool GameState::heroDead() const {
    return !isEmpty(hero);
}

bool GameState::isSafe(const Unit& unit) const {
    for (unsigned int i = 0; i < robots.size(); i++)
        if (robots[i]->attacks(unit)) return false;
    if (junkAt(unit)) return false;
    return true;
}

void GameState::moveHeroTowards(const Unit& dir) {
    hero.moveTowards(dir);
}

Hero GameState::getHero() const {return hero;}

/*
 * Free of robots and junk only
 */
bool GameState::isEmpty(const Unit& unit) const {
    return (countRobotsAt(unit) == 0);
}

/*
 * Is there junk at unit?
 */
bool GameState::junkAt(const Unit& unit) const {
    for (size_t i = 0; i < robots.size(); ++i){
        if(robots[i]->getCollisionCount() == 0){
           if (robots[i]->at(unit)) return true;
        }
    }
    return false;
}

/*
 * How many robots are there at unit?
 */
int GameState::countRobotsAt(const Unit& unit) const {
    int count = 0;
    for (size_t i = 0; i < robots.size(); ++i) {
        if (robots[i]->at(unit) && robots[i]->getCollisionCount() == 1){
            count++;
        }
    }
    return count;
}

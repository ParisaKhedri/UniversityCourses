/**
 * Copyright (C) David Wolfe, 1999.  All rights reserved.
 * Ported to Qt and adapted for TDDD86, 2015.
 */

#include "Junk.h"
#include "Robot.h"
#include "constants.h"

Junk::Junk() : Robot() {}
Junk::Junk(const Unit& c) : Robot(c) {}

void Junk::draw(QGraphicsScene *scene) const {
    Point corner = asPoint();
    scene->addEllipse(QRectF(corner.x * UNIT_WIDTH, corner.y * UNIT_HEIGHT,
                             JUNK_RADIUS, JUNK_RADIUS), QPen(), QBrush(JUNK_COLOR));
}

/*
 * ska inte kunna röra sig
*/
void Junk::moveTowards(const Unit& u){

}


/*
 * ska inte attackera
*/
bool Junk::attacks(const Unit& u) const {
    return false;
}


unsigned Junk::getCollisionCount() {
    return 0;
}

Junk* Junk::clone() const{
    return (new Junk(*this));
}

/**
 * Copyright (C) David Wolfe, 1999.  All rights reserved.
 * Ported to Qt and adapted for TDDD86, 2015.
 */

#ifndef ROBOT_H
#define ROBOT_H

#include "Unit.h"
#include <QGraphicsScene>

class Robot : public Unit {
public:
    Robot();
    Robot(const Unit& c);
    virtual void draw(QGraphicsScene* scene) const;
    virtual unsigned getCollisionCount();
    virtual Robot* clone() const;
};

#endif // ROBOT_H

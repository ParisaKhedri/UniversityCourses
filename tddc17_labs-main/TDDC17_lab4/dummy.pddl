; Shakey's world domain

(define (domain Shakey)

(:requirements :strips :typing :equality :negative-preconditions)

(:types box robot light_switch door room)

(:predicates
    (wide ?d - door)                                    ; a door could be wide
    (linked ?roomX - room ?roomY - room ?d - door)          ; two rooms are linked through a door
    (switchedOn ?l - light_switch)                           ; a light-switch could be switched on
    (climedOn ?r - robot ?b - box)                 ; box can be climed on
    (isIn ?object ?roomX - room)                       ; object can be in different rooms
    (moveThroughDoor ?b - box ?d -door)
    (leadsToDarkRoom ?d - door ?roomX - room)
    (isNextTo ?b - box ?l - light_switch)
)
)
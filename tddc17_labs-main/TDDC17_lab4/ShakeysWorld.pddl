; Shakey's world domain

(define (domain Shakey)

(:requirements :strips :typing :equality :negative-preconditions)

(:types box robot light_switch door room)

(:predicates
    (wide ?d - door)                                    ; a door could be wide
    (linked ?roomX - room ?roomY - room ?d - door)          ; two rooms are linked through a door
    (switchedOn ?l - light_switch)                           ; a light-switch could be switched on
    (climedOn ?r - robot ?b - box)                 ; box can be climed on
    (robotIsIn ?r - robot ?rm - room)
    (boxIsIn ?b - box ?rm - room)
    ;(isIn ?object ?roomX - room)                       ; object can be in different rooms
    (moveThroughDoor ?b - box ?d -door)
    (leadsToDarkRoom ?d - door ?roomX - room)
    (isNextTo ?b - box ?l - light_switch)
    (lightIsIn ?l - light_switch ?rm - room)
)

; this action greets one thing by its name
(:action turn_on_light
    :parameters (?b - box ?r - robot ?l - light_switch ?rm - room)
    :precondition (and
        ; robot needs to be on box
        (climedOn?r ?b)
        (robotIsIn ?r ?rm)
        (boxIsIn ?b ?rm)
        ; light_switch needs to be off
        (not (switchedOn ?l))
        (lightIsIn ?l ?rm)
    )
    :effect (and
        ; light_switch is turned on
        (switchedOn ?l)
        (not (climedOn ?r ?b))
    )
)
(:action climb_on_box
    :parameters (?b - box ?r - robot ?l - light_switch ?rm - room)
    :precondition (and
        ; robot needs to be on box
        (isNextTo ?b ?l)
        (not (switchedOn ?l))
        (not (climedOn ?r ?b))
        (lightIsIn ?l ?rm)
        (robotIsIn ?r ?rm)
        (boxIsIn ?b ?rm)
    )
    :effect (and
        ; light_switch is turned on
        (climedOn ?r ?b)
    )
)
(:action move_box_to_light
    :parameters (?b - box ?r - robot ?l - light_switch ?rm - room)
    :precondition (and
        ; robot needs to be on box
        (not (switchedOn ?l))
        (lightIsIn ?l ?rm)
        (not (climedOn ?r ?b))
        (robotIsIn ?r ?rm)
        (boxIsIn ?b ?rm)
    )
    :effect (and
        ; light_switch is turned on
        (isNextTo ?b ?l)
    )
)
(:action move_box_to_other_room
    :parameters (?b - box ?r - robot ?lX - light_switch ?lY - light_switch ?rmX - room ?rmY - room ?d - door)
    :precondition (and
        (linked ?rmX ?rmY ?d)
        (lightIsIn ?lX ?rmX)
        (lightIsIn ?lY ?rmY)
        (switchedOn ?lX)
        (lightIsIn ?lX ?rmX)
        (boxIsIn ?b ?rmX)
        (not (climedOn ?r ?b))
        (wide ?d)
        (leadsToDarkRoom ?d ?rmY)
    )
    :effect (and
        (moveThroughDoor ?b  ?d)
        (boxIsIn ?b ?rmY)
        (robotIsIn ?r ?rmY)
        (not (leadsToDarkRoom ?d ?rmX))
    )
)

)

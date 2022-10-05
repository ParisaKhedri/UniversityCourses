; Hello World example problem

(define (problem turnLightsOn)

(:domain Shakey)

(:objects
    ; the only thing is the `world`
    l1 - light_switch
    l2 - light_switch
    l3 - light_switch
    door1 - door
    door2 - door
    door3 - door
    b - box
    r - robot
    room1 - room
    room2 - room
    room3 - room
)

(:init
    (linked room1 room2 door1)
    (linked room2 room3 door2)
    (linked room2 room3 door3)
    (wide door1)
    (wide door3)
    (not (switchedOn l1))
    (not (switchedOn l2))
    (not (switchedOn l3))
    
    (boxIsIn b room1)
    (robotIsIn r room1)

    (lightIsIn l1 room1)
    (lightIsIn l2 room2)
    (lightIsIn l3 room3)

    (leadsToDarkRoom door1 room2)
    (leadsToDarkRoom door2 room3)
    (leadsToDarkRoom door3 room3)

    (leadsToDarkRoom door1 room1)
    (leadsToDarkRoom door2 room2)
    (leadsToDarkRoom door3 room2)
)

(:goal
    (and
        ; The only goal is to reach a state where every light is switched on
        (switchedOn l1)
        (switchedOn l2)
        (switchedOn l3)
    )
)
)

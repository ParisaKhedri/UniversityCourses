from ev3 import Ev3
import queue as q
from Lucy_gui import GUI
import time
import random

Lucy = Ev3("169.254.111.41")
left_motor = Lucy.add_motor("B")
right_motor = Lucy.add_motor("C")
ir_sensor = Lucy.add_sensor(1,"IR")
color_sensor = Lucy.add_sensor(4,"color")
touch_sensor = Lucy.add_sensor(2,"touch")

init= 0
init_changed_direction = 1
i=0
middle_value = 0
line_value = 0
background_value =0
factor = 1
command = "Peaceful mode"
current_mode = "Peaceful mode"
command_queue =  q.Queue()
command_queue_go_stop =  q.Queue()
remote_value = 0
current_mode_2 = 'STOP'
command_2 = 'STOP'


def peaceful_mode():
    """
    The function will make the ev3 unit drive around randomly, 
    if the ir sensor detects something in its proximity,
    the ev3 unit will turn around and keep driving around randomly.
    """
    random1 = random.randint(-90,90)
    if left_motor.is_running() == False and right_motor.is_running() == False:
        #start motors
        left_motor.run_time_limited(100, 3000,"brake" ,False)
        right_motor.run_time_limited(100, 3000,"brake" ,False)
        Lucy.start_motors(["B","C"])
        time.sleep(3)
    
        left_motor.run_position_limited(100, random1*11,"brake" ,False)
        right_motor.run_position_limited(100, -random1*11,"brake" ,False)
        Lucy.start_motors(["B","C"])
       
    if ir_sensor.get_prox() < 15:
        random2 = random.randint(45,135)
        Lucy.play_wav("batman_theme_x")
        #turn randomly
        left_motor.run_position_limited(100, random2*11,"brake" ,False)
        right_motor.run_position_limited(100, -random2*11,"brake" ,False)
        Lucy.start_motors(["B","C"])
        time.sleep(1)
        

def manual_mode(command):
    """
    The function will put the ev3 unit in "manual mode".
    The ev3 unit will peform a task e.i "forward" when it gets the right command.
    Parameter:
    command(str): command is a string 
    """
    if left_motor.is_running() == False and right_motor.is_running() == False:
        if command == "forward":
            left_motor.run_position_limited(100, 100*11,"brake" ,False)
            right_motor.run_position_limited(100, 100*11,"brake" ,False)
            Lucy.start_motors(["B","C"])
        elif command == "backward":
            left_motor.run_position_limited(100, -100*11,"brake" ,False)
            right_motor.run_position_limited(100, -100*11,"brake" ,False)
            Lucy.start_motors(["B","C"])
        elif command == "spin_left":
            left_motor.run_position_limited(100, -45*11,"brake" ,False)
            right_motor.run_position_limited(100, 45*11,"brake" ,False)
            Lucy.start_motors(["B","C"])
        elif command == "spin_right":
            left_motor.run_position_limited(100, 45*11,"brake" ,False)
            right_motor.run_position_limited(100, -45*11,"brake" ,False)
            Lucy.start_motors(["B","C"])


def guard_mode():
    """
    The function will make the ev3 unit drive around inside an area enclosed by black lines.
    If the unit encounters an object in its proximity it will start a countdown.
    If the object is gone before the countdown hits zero, the unit returns gurding the area.
    If the object is present after the countdown, the robot will attack.
    If the object is gone after the attack,  the unit returns guarding the area.
    if the object i spresent after tyhe attack, the unit will turn around and find a corner to hide in. 
    """
    global current_mode
    warned = False
    random3 = random.randint(75,105)
    if color_sensor.get_color() == 1:
        left_motor.stop()
        right_motor.stop()
        left_motor.run_position_limited(100,random3*11,"brake",False)
        right_motor.run_position_limited(100,-random3*11,"brake",False)
        Lucy.start_motors(["B","C"])
        time.sleep(1)

    if left_motor.is_running() == False and right_motor.is_running() == False:
        #start motors
        left_motor.run_position_limited(50, 70,"brake" ,False)
        right_motor.run_position_limited(50, 70,"brake" ,False)
        Lucy.start_motors(["B","C"])
    
    if ir_sensor.get_prox() < 50 and warned != True:
        left_motor.stop()
        right_motor.stop()
        warned = True
        Lucy.play_wav("batman_theme_x")
        time.sleep(1)
        countdown_lst=["three", "two", "one",""]
        for element in countdown_lst:
            if ir_sensor.get_prox() < 50:
                warned = True
                Lucy.speak(element)
                time.sleep(1)
            else:
                warned = False
                left_motor.run_position_limited(100,random3*11,"brake",False)
                right_motor.run_position_limited(100,-random3*11,"brake",False)
                Lucy.start_motors(["B","C"])
                break
        time.sleep(3)

    if ir_sensor.get_prox() < 50 and warned == True:
        warned = False
        x=0
        Lucy.play_wav("watch_out")
        time.sleep(2)
        left_motor.run_forever(25,False)
        right_motor.run_forever(25,False)
        Lucy.start_motors(["B","C"])
        while (touch_sensor.is_pressed() == False and ir_sensor.get_prox() < 50) and x <50 and color_sensor.get_color() != 1 :
            print("halla")
            x+=1
        if color_sensor.get_color() == 1:
            left_motor.stop()
            right_motor.stop()
            left_motor.run_position_limited(100,random3*11,"brake",False)
            right_motor.run_position_limited(100,-random3*11,"brake",False)
            Lucy.start_motors(["B","C"])
            time.sleep(1)
        elif touch_sensor.is_pressed() == True:
            left_motor.stop()
            right_motor.stop()
            time.sleep(2)
            if ir_sensor.get_prox() < 50:
                left_motor.run_position_limited(100,random3*11,"brake",False)
                right_motor.run_position_limited(100,-random3*11,"brake",False)
                Lucy.start_motors(["B","C"])
                time.sleep(1)
                left_motor.run_forever(30,False)
                right_motor.run_forever(30,False)
                Lucy.start_motors(["B","C"])

                while ir_sensor.get_prox() > 20:
                    x+=1
                left_motor.stop()
                right_motor.stop()
                while ir_sensor.get_prox() < 65:
                    left_motor.run_time_limited(50,900, "brake", False)
                    right_motor.run_time_limited(-50, 900, "brake", False)
                    Lucy.start_motors(["B","C"])
                    time.sleep(0.95)
                left_motor.run_forever(25,False)
                right_motor.run_forever(25,False)
                Lucy.start_motors(["B","C"])

                while ir_sensor.get_prox() > 10:
                    x+=1
                left_motor.stop()
                right_motor.stop()

                left_motor.run_position_limited(50, -45*11, "brake", False)
                right_motor.run_position_limited(50, 45*11, "brake", False)
                Lucy.start_motors(["B","C"])
                current_mode = None
                        

def dance():
    """
    the function will make the ev3 unit perform its signature dance move
    """
    left_motor.run_time_limited(50, 1000,"brake" ,False)
    right_motor.run_time_limited(-50, 1000,"brake" ,False)
    Lucy.start_motors(["B","C"])
    time.sleep(1.5)
    left_motor.run_time_limited(-50, 1000,"brake" ,False)
    right_motor.run_time_limited(50, 1000,"brake" ,False)
    Lucy.start_motors(["B","C"])
    time.sleep(1.5)
    left_motor.stop()
    right_motor.stop()


def line_follower(factor):
    """
    The function wil make the ev3 unit follow a black line on the ground.
    If it encounters a 90 degree corner, the unit will make a sharp 90 degree turn.

    Parameter:
    factor (int): faktor is an int, either 1 or -1, it determines wich way the unit should follow the line.
    """
    global init, middle_value, line_value, background_value, init_changed_direction
    i=0
    speed = 30
    while i <1:
        i+=1
        if factor == -1 and init_changed_direction == 0:
            init_changed_direction = 1
            left_motor.run_position_limited(100, -90*11,"brake" ,False)
            right_motor.run_position_limited(100, 90*11,"brake" ,False)
            Lucy.start_motors(["B","C"])
            time.sleep(3)
            left_motor.run_position_limited(100, -250,"brake" ,False)
            right_motor.run_position_limited(100,- 250,"brake" ,False)
            Lucy.start_motors(["B","C"])
            time.sleep(3)

        if factor == 1 and init_changed_direction == 0:
            init_changed_direction = 1
            left_motor.run_position_limited(100, 90*11,"brake" ,False)
            right_motor.run_position_limited(100, -90*11,"brake" ,False)
            Lucy.start_motors(["B","C"])
            time.sleep(3)
            left_motor.run_position_limited(100, -250,"brake" ,False)
            right_motor.run_position_limited(100,- 250,"brake" ,False)
            Lucy.start_motors(["B","C"])
            time.sleep(3)

        if init == 0:
            init = 1
            line_value_lst= color_sensor.get_rgb()
            line_value = (line_value_lst[0]+line_value_lst[1]+line_value_lst[2])/3

            left_motor.run_time_limited(50*factor, 1000,"brake" ,False)
            right_motor.run_time_limited(-50*factor, 1000,"brake" ,False)
            Lucy.start_motors(["B","C"])
            time.sleep(1.1)

            background_value_lst = color_sensor.get_rgb()
            background_value = (background_value_lst[0]+background_value_lst[1]+background_value_lst[2])/3

            left_motor.run_time_limited(-50*factor, 1000,"brake" ,False)
            right_motor.run_time_limited(50*factor, 1000,"brake" ,False)
            Lucy.start_motors(["B","C"])
            time.sleep(1)

            middle_value = (background_value + line_value)//2-10
            left_motor.run_time_limited(-50*factor,150,"brake" ,False)
            right_motor.run_time_limited(50*factor,150,"brake" ,False)
            Lucy.start_motors(["B","C"])    
        elif init == 1:
            sensor_rgb = color_sensor.get_rgb()
            sensor_rgb = (sensor_rgb[0]+sensor_rgb[1]+sensor_rgb[2])/3
            error = middle_value - sensor_rgb
            u = int(abs(error)//2)
            if u > 50:
                u = 50
            i+=1
            if left_motor.is_running() == False and right_motor.is_running() == False:
                sensor_rgb_turn_list = color_sensor.get_rgb()
                sensor_rgb =(sensor_rgb_turn_list[0]+sensor_rgb_turn_list[1]+sensor_rgb_turn_list[2])/3
                if sensor_rgb <= line_value+3 :
                    left_motor.run_position_limited(100, 250,"brake" ,False)
                    right_motor.run_position_limited(100, 250,"brake" ,False)
                    Lucy.start_motors(["B","C"])
                    time.sleep(1)
                    left_motor.run_position_limited(100, -45*11*factor,"brake" ,False)
                    right_motor.run_position_limited(100, 45*11*factor,"brake" ,False)
                    Lucy.start_motors(["B","C"])
                    time.sleep(1)
                    sensor_rgb_turn_list = color_sensor.get_rgb()
                    sensor_rgb =(sensor_rgb_turn_list[0]+sensor_rgb_turn_list[1]+sensor_rgb_turn_list[2])/3
                    if sensor_rgb > middle_value+10:
                        left_motor.run_position_limited(100, 45*11*factor,"brake" ,False)
                        right_motor.run_position_limited(100, -45*11*factor,"brake" ,False)
                        Lucy.start_motors(["B","C"])
                        time.sleep(1)
                        left_motor.run_position_limited(-100, 240,"brake" ,False)
                        right_motor.run_position_limited(-100, 240,"brake" ,False)
                        Lucy.start_motors(["B","C"])
                        time.sleep(1)
                        left_motor.run_time_limited(-50*factor,200,"brake" ,False)
                        right_motor.run_time_limited(50*factor,200,"brake" ,False)
                        Lucy.start_motors(["B","C"])
                    else:
                        left_motor.run_time_limited(-50*factor,200,"brake" ,False)
                        right_motor.run_time_limited(50*factor,200,"brake" ,False)
                        Lucy.start_motors(["B","C"])
                        sensor_rgb = color_sensor.get_rgb()
                        error = middle_value - (sensor_rgb[0]+sensor_rgb[1]+sensor_rgb[2])/3
                        if error < 0:
                            left_motor.run_time_limited(speed + u*(factor),150,"brake" ,False)
                            right_motor.run_time_limited(speed - u*(factor),150,"brake" ,False)
                            Lucy.start_motors(["B","C"])
                        elif error >= 0:
                            left_motor.run_time_limited(speed - u*(factor),150,"brake" ,False)
                            right_motor.run_time_limited(speed + u*(factor),150,"brake" ,False)
                            Lucy.start_motors(["B","C"])
                elif error < 0:
                    left_motor.run_time_limited(speed + u*(factor),150,"brake" ,False)
                    right_motor.run_time_limited(speed - u*(factor),150,"brake" ,False)
                    Lucy.start_motors(["B","C"])
                elif error >= 0:
                    left_motor.run_time_limited(speed - u*(factor),150,"brake" ,False)
                    right_motor.run_time_limited(speed + u*(factor),150,"brake" ,False)
                    Lucy.start_motors(["B","C"])

main_window = GUI(command_queue,Lucy,command_queue_go_stop)

def run_mode(mode, command):
    """
    The function will execute the different "modes" depending on what "mode" it gets as input.

    Parameters:
    mode (str): A string determinating what function should be executed.
    command(srt): a string that will be sent with the function "manual_mode".
    """
    global factor, init_changed_direction, remote_value
    if mode == "manual mode":
        manual_mode(command) 
    elif mode == "Peaceful mode":
        peaceful_mode()   
    elif mode == "Guard mode":
        guard_mode()
    elif mode == "remote mode":
        if ir_sensor.get_remote()[0]==1:
            print("peace")
            dance()
            time.sleep(1)
            remote_value = 1
        elif ir_sensor.get_remote()[0]==2:
            print("guard")
            dance()
            time.sleep(1)
            remote_value = 2
        if remote_value == 1:
            peaceful_mode()
        elif remote_value == 2:
            guard_mode()
    elif mode == "Line follower":
        if command == "Change direction" and factor ==1:
            factor = -1
            init_changed_direction = 0
        elif command == "Change direction" and factor ==-1:
            factor = 1
            init_changed_direction =0
        line_follower(factor)


while True:
    time.sleep(1)
    command = None
    if not command_queue_go_stop.empty():
        command_2 = command_queue_go_stop.get()
    if not command_queue.empty():
        command = command_queue.get()
        if command == "quit":
            # Quit tkinter
            main_window.quit()
            # Exit the while loop
            break
        elif command == "Peaceful mode":
            current_mode = "Peaceful mode"
            Lucy.speak("peaceful mode")
            dance()
            time.sleep(2)
        elif command == "Guard mode":
            current_mode = "Guard mode"
            Lucy.speak("guard mode")
            dance()
            time.sleep(2)
        elif command == "remote mode":
            current_mode = "remote mode"
        elif command == "manual mode":
            current_mode = "manual mode"
            print("vi ar i manual mode")
        elif command == "Line follower":
            current_mode = "Line follower"
    if command_2 == 'GO':
        current_mode_2 = "GO"
    elif command_2 == 'STOP':
        current_mode_2 = "STOP"     
    if current_mode_2 == 'GO':
        run_mode(current_mode, command)
    main_window.update()
    
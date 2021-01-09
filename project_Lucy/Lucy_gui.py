from tkinter import *


def send_command(message,c_queue):
    """
    The function takes a message/command and puts it in a que
    Paramneters:
    message (str): A defined string from the other functions
    c_queue (queue.Queue): A que that will be filled with messages/commands.
    """
    print("command",message,"put into queue.")
    c_queue.put(message)


def send_command_2(message,command_queue_gs):
    """
    The function takes a message and puts it in a que.
    Paramneters:
    message (str): A defined string from the other functions.
    command_queue_gs (queue.Queue):  A que that will be filled with messages/commands.
    """
    print("command",message,"put into queue.")
    command_queue_gs.put(message)


def GUI(c_queue,Lucy,command_queue_gs):
    """
    The function starts a GUI with buttons and lables.
    Parameters:
    c_queue (queue.Queue):  A que that will be filled with messages/commands
    Lucy: Lucy is the Ev3 unit
    Command_queue_gs (queue.Queue):  A que that will be filled with messages/commands, commands regarding start ond stop of the ev3 unit
    """

    root = Tk()
    label = Label(root, text="Lucy!")
    label.pack()

    root.geometry("500x400")
    Button2 = Button(root, text="Forward", command= lambda message ="forward": send_command("forward",c_queue), fg = "white", bg = "grey")
    Button2.pack()

    Button4 = Button(root, text="Backward", command= lambda message ="backward": send_command("backward",c_queue), fg = "white", bg = "grey")
    Button4.pack()

    Button5 = Button(root, text="Spin right", command= lambda message ="spin_right": send_command("spin_right",c_queue), fg = "white", bg = "grey")
    Button5.pack()

    Button6 = Button(root, text="Spin left", command= lambda message ="spin_left": send_command("spin_left",c_queue), fg = "white", bg = "grey")
    Button6.pack()

    Button1 = Button(root, text="Quit", command= lambda message ="quit": send_command("quit",c_queue), fg = "black", bg = "red")
    Button1.pack()

    Button3 = Button(root, text="Change direction", command= lambda message ="Change direction": send_command("Change direction",c_queue), fg = "white", bg = "black")
    Button3.pack()


    def toggle_text():
        """
        toggle button text between modes
        """
        if button["text"] == "Peaceful mode":
            button.configure(fg="white",bg = "red")
            button["text"] = "Guard mode"
            send_command("Guard mode",c_queue)

        elif button["text"] == "Guard mode":
            button.configure(fg="white",bg = "grey")
            button["text"] = "manual mode"
            send_command("manual mode",c_queue)

        elif button["text"] == "manual mode":
            button.configure(fg="white",bg = "black")
            button["text"] = "Line follower"
            send_command("Line follower",c_queue)
        
        elif button["text"] == "Line follower":
            button.configure(fg="black",bg = "yellow")
            button["text"] = "remote mode"
            send_command("remote mode",c_queue)
        
        elif button["text"] == "remote mode":
            button.configure(fg="white",bg = "green")
            button["text"] = "Peaceful mode"
            send_command("Peaceful mode",c_queue)
            
    button = Button( text="Peaceful mode", width=12, command=toggle_text,fg="white",bg="green")
    button.pack(padx=100, pady=10)
 
    def toggle_text_2():
        """
        toggle button text between modes
        """

        if button_gs["text"] == "STOP":
            button_gs["text"] = "GO"
            send_command_2("GO", command_queue_gs)

        elif button_gs["text"] == "GO":
            button_gs["text"] = "STOP"
            send_command_2("STOP", command_queue_gs)

            
    button_gs = Button( text="STOP", width=12, command=toggle_text_2)
    button_gs.pack(padx=100, pady=10)
    
    e = Entry(root)
    e.pack()

    e.focus_set()
    
    def callback():
        """
        prints and makes the ev3 unit say the message from the entry field "e"
        """
        #print (e.get())
        Lucy.speak(e.get())
        

    b = Button(root, text="speak", width=10, command=callback,fg="white",bg="grey")
    b.pack()
    
    return root
    
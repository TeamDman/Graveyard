from tkinter import *
import time

wn = Tk()
wn.title('KeyDetect')

canvas = Canvas(wn, width=550, height=200)

counter = 0
start = 40
seconds = start

def format_time(amount):
    prefix = ""
    if amount < 0:
        prefix = "-"
        amount = amount * -1
    minutes = amount // 60
    seconds = amount % 60
    fix = ""
    if seconds < 10:
        fix = "0"
    return prefix + str(minutes) + ":" + fix + str(seconds) 

def tick():
    global seconds
    seconds = seconds - 1

def draw_text():
    global seconds
    global canvas
    if seconds < 0:
        canvas.create_text(20, 30, anchor=W, font="Purisa", text=format_time(0))
    else:
        canvas.create_text(20, 30, anchor=W, font="Purisa", text=format_time(seconds))

def draw_face():
    global canvas
    global counter
    global seconds
    # left eyeball
    canvas.create_oval(100, 20, 100+50, 20+50, outline="#f11", fill="#1f1", width=2)

    # right eyeball
    canvas.create_oval(200, 20, 200+50, 20+50, outline="#f11", fill="#1f1", width=2)

    # mouth
    canvas.create_line(
        100, 120,
        100, 120 - seconds,
        200, 120 - seconds,
        250, 120,
        smooth=1
    )


while True:
    if counter % 100 == 0 and seconds > (start*-1):
        tick()
        
    canvas.delete("all")
    draw_face()
    draw_text()
    canvas.pack()
    
    wn.update_idletasks()
    wn.update()
    
    counter = counter + 1
    time.sleep(0.01)
    

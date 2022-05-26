from tkinter import *

wn = Tk()
wn.title('KeyDetect')

canvas = Canvas(wn, width=550, height=200)
canvas.create_rectangle(0, 0, 100, 100, fill="blue", outline = 'blue')
canvas.pack()

left = 0
top = 0

def draw():
    canvas.delete("all")
    canvas.create_rectangle(left, top, left+100, top+100, fill="red", outline = 'blue')
    canvas.pack()

def down(e):
    global left
    if e.char == "d":
        left = left + 10
    elif e.char == "a":
        left = left - 10
    draw()

wn.bind('<KeyPress>', down)

wn.mainloop()

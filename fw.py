# list of fireworks
# each firework has:
# - Current position
# - Velocity
# - Fuse timer (-1 to disable explosion)
 
from tkinter import *
import time
import random
 
window = Tk()
window.title("Fireworks")
 
height = 550
width = 550
firework_size = 5
canvas = Canvas(window, width=550, height=height, bg="black")
 
fireworks = []
 
def create_firework(position, velocity, fuse, colour, lifespan=10):
    global fireworks
    fireworks.append({
        "position": position,
        "velocity": velocity,
        "fuse": fuse,
        "lifespan": lifespan,
        "colour": colour
    })
 
def add_vectors(a,b):
    return (a[0]+b[0], a[1]+b[1])
 
def is_in_bounds(world_point, rect):
    pointX, pointY = world_point
    (x1,y1),(x2,y2) = rect
    return x1 < pointX < x2 and y1 < pointY < y2
 
def is_on_screen(world_point):
    return is_in_bounds(world_point, ((0,0), (width,height)))
 
# update fireworks
def tick():
    global fireworks
    for firework in fireworks:
        # update position
        firework["position"] = add_vectors(
            firework["position"],
            firework["velocity"]
        )
 
        # apply gravity
        firework["velocity"] = add_vectors(
            firework["velocity"],
            (0, -2)
        )
 
        # update fuse
        firework["fuse"] = firework["fuse"] - 1
        if firework["fuse"] == 0:
            for _ in range(0,7):
                create_firework(
                    position=firework["position"],
                    velocity=(
                        random.randint(-10,10),
                        random.randint(-10,10),
                    ),
                    fuse=-1,
                    colour="#0F0",
                    lifespan=20
                )
            firework["lifespan"] = 0
 
        # update lifespan
        firework["lifespan"] = firework["lifespan"] - 1
 
    # prune fireworks not on screen
    fireworks = [v for v in fireworks if is_on_screen(v["position"])]
 
    # prune fireworks that have reached the end of their life
    fireworks = [v for v in fireworks if v["lifespan"] > 0]
 
 
# draw fireworks
def draw(canvas):
    global fireworks
    for firework in fireworks:
        pos = firework["position"]
        canvas.create_oval(
            pos[0],
            height - pos[1],
            pos[0]+firework_size,
            height - (pos[1]+firework_size),
            outline = firework["colour"],
            fill = firework["colour"],
            width = 2
        )
 
 
for _ in range(0,10):
    x = random.randint(0, width)
    create_firework(
        position=(x,0),
        velocity=(
            random.randint(0,10),
            random.randint(10,30),
        ),
        fuse=15,
        colour="#FFF",
        lifespan=50
    )
 
 
def onClick(e):
    create_firework(
        position=(e.x, height - e.y),
        velocity=(
            random.randint(0,10),
            random.randint(10,30),
        ),
        fuse=15,
        colour="#FFF",
        lifespan=50
    )
 
window.bind("<Button-1>", onClick)
 
counter = 0
tick_rate = 10
while True:
    if counter % tick_rate == 0:
        tick()
 
    canvas.delete("all")
    draw(canvas)
    canvas.pack()
 
    window.update_idletasks()
    window.update()
 
    counter = counter + 1
    time.sleep(0.01)
 
 

import os, sys, time, turtle, fileinput
#The turtle itself
t = turtle.Turtle(shape="classic")
#Stack Class
class Stack:
     def __init__(self):
         self.items = []

     def isEmpty(self):
         return self.items == []

     def push(self, item):
         self.items.append(item)

     def pop(self):
         return self.items.pop()

     def peek(self):
         return self.items[len(self.items)-1]

     def size(self):
         return len(self.items)

     def reverse(self):
         self.items.reverse()
#Check if the file exists. If not, throw an error and quit.
try:
    f = fileinput.input(sys.argv[1])
except:
    print "File not found!"
    sys.exit()
#Variable for the skip state and the stack itself
skip = False
s = Stack()
#Pre-populate the stack with 5 zeroes.
for i in range(0,5):
    s.push(0)

#Check every symbol and perform it's command
try:
    for line in f:
        for symbol in line:
            #If not skipped...
            if skip == False:
                #0-9 = push number to stack
                if symbol.isdigit():
                    s.push(int(symbol))
                #a-f = push 10-15 to the stack
                elif symbol in ['a','b','c','d','e','f']:
                    if symbol == "a":
                        s.push(10)
                    elif symbol == "b":
                        s.push(11)
                    elif symbol == "c":
                        s.push(12)
                    elif symbol == "d":
                        s.push(13)
                    elif symbol == "e":
                        s.push(14)
                    elif symbol == "f":
                        s.push(15)
                #+, pop Y and X, then add X and Y together and push the result
                elif symbol == "+":
                    y = s.pop()
                    x = s.pop()
                    s.push(x+y)
                #-, pop Y and X, then subtract Y from X and push the result
                elif symbol == "-":
                    y = s.pop()
                    x = s.pop()
                    s.push(x-y)
                #*, pop Y and X, then multiply X by Y and push the result
                elif symbol == "*":
                    y = s.pop()
                    x = s.pop()
                    s.push(x*y)
                #/, pop Y and X, then divide X by Y and push the result
                elif symbol == "/":
                    y = s.pop()
                    x = s.pop()
                    if y != 0:
                        s.push(x/y)
                    else:
                        s.push(0)
                #^, pop X and move forward by X pixels
                #v, same with backward
                #<, turn X degrees to the left
                #>, turn X degrees to the right
                #p$op
                elif symbol == "^":
                    x = s.pop()
                    t.forward(x)
                elif symbol == "v":
                    x = s.pop()
                    t.backward(x)
                elif symbol == "<":
                    x = s.pop()
                    t.left(x)
                elif symbol == ">":
                    x = s.pop()
                    t.right(x)
                #Skip the next instruction if the popped value is zero
                elif symbol == "?":
                    if s.pop() == 0:
                        skip = True
                #Discard the top item in the stack
                elif symbol == "~":
                    s.pop()
                #Swap the top two items in the stack
                elif symbol == "s":
                    x = s.pop()
                    y = s.pop()
                    s.push(x)
                    s.push(y)
                #Reverse the stack
                #p$stop
                elif symbol == "r":
                    s.reverse()
                #Push 1 if Y > X, Push 0 otherwise
                elif symbol == ")":
                    x = s.pop()
                    y = s.pop()
                    if y > x:
                        s.push(1)
                    else:
                        s.push(0)
                #Push 1 if Y < X, push 0 otherwise
                elif symbol == "(":
                    x = s.pop()
                    y = s.pop()
                    if y < x:
                        s.push(1)
                    else:
                        s.push(0)
                #Push 1 if Y == X, push 0 otherwise
                elif symbol == "=":
                    x = s.pop()
                    y = s.pop()
                    if y == x:
                        s.push(1)
                    else:
                        s.push(0)
                #Pen down
                elif symbol == ",":
                    t.pendown()
                #Pen up
                elif symbol == ".":
                    t.penup()
                #Pop A and B and goto location (B,A)
                elif symbol == "t":
                    a = s.pop()
                    b = s.pop()
                    t.goto(b, a)
            #If skipped, set skip to False so the next command can execute
            else:
                skip = False
except:
    sys.exit()

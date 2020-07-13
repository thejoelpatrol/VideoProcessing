from pypng.code import png
import random
from sys import argv
import time

glitch_probability = int(argv[2])
tag = time.time()

random.seed(25)

f = open(argv[1], 'rb')      # binary mode is important for python3
reader = png.Reader(file=f)
d = reader.read()

#newrows = d[2]

rowslist = list(d[2])
previous = None
newrows = []
for i, row in enumerate(rowslist):
    #undo = reader.undo_filter(0, row, previous)
    #if random.randint(0, 100) < GLITCH_PROBABILITY:
    if i < 50 or i == len(rowslist)/2: #or random.randint(0, 100) < glitch_probability:
        pivot = random.randint(0, 100)
        #if i < 100:
        #    pivot = random.randint(0, 100)    
        #else:
        #    pivot = random.randint(0, len(row[1])/4)
        newrow = bytearray()
        newrow.extend(row[1][:pivot])
        #newrow.append(0)
        newrow.append(row[1][pivot] ^ 0xff)
        newrow.extend(row[1][pivot+1:])
        newrows.append( (row[0], newrow) )
    else:
        newrows.append((row[0], row[1]))
    #previous = row


print("q??" + str(d))
outfile = open(argv[1], 'wb')
outpng = png.Writer(width=d[0], height=d[1], greyscale=d[3]['greyscale'], alpha=d[3]['alpha'],
                     bitdepth=d[3]['bitdepth'], interlace=d[3]['interlace'], planes=d[3]['planes'])
outpng.write(outfile, newrows)


f.close()
outfile.close()

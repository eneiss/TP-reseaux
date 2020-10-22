from sys import argv
from random import randint

if len(argv) != 3:
    print("Error : please input 2 integers")

else:
    min = min(int(argv[1]), int(argv[2]))
    max = max(int(argv[1]), int(argv[2]))
    print(f"Your random number is {randint(min, max)}")

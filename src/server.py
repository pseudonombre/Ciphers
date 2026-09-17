import sys
import subprocess
import importlib.util

if importlib.util.find_spec("wordsegment") is None:
    subprocess.check_call( [sys.executable, "-m", "pip", "install", "--quiet", "wordsegment"] )
import wordsegment
wordsegment.load()

while True:

    line = sys.stdin.readline()
    if not line: break
    line = line.rstrip("\n")
    if line == "EXIT": break

    function, argument = line.split("\t", 1)

    if function == "segment":
        result = " ".join(wordsegment.segment(argument))

    else:
        result = "ERROR: unknown function"

    print(result, flush=True)
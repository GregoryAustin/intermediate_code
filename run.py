from threading import Thread
import subprocess

def getRunTime(command):
	#print(runCommand([b"perf", b"stat", command]).communicate()[1].split(b"\n")[15].split(b" ")[7].replace(b",", b"."))
	return float(runCommand([b"perf", b"stat", command]).communicate()[1].split(b"\n")[15].split(b" ")[7].replace(b",", b"."))
	#return 0.0

# the stdout and stderr arguments cause the output to be hidden
def runCommand(command):
	return subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

def basic():
	totaltotal = 0.0
	total = 0.0
	for x in range(1, 101):
		#Chang the commands in here
		total += float(runCommand([b"perf", b"stat", b"java", b"-cp", b"Java", b"hello"]).communicate()[1].split(b"\n")[15].split(b" ")[7].replace(b",", b"."))
		print("Basic " + str(total) + " " + str(x))
		totaltotal += total
	print("Basic time over 100 executions: " + "{0:.10f}".format(totaltotal/100))

def optBasic():
	totaltotal = 0.0
	total = 0.0
	for x in range(1, 101):
		#Chang the commands in here
		total += float(runCommand([b"perf", b"stat", b"python", b"Python/hello.py"]).communicate()[1].split(b"\n")[15].split(b" ")[7].replace(b",", b"."))
		print("OptBasic " + str(total) + " " + str(x))
		totaltotal += total
	print("Optitmised Basic time over 100 executions: " + "{0:.10f}".format(totaltotal))


basic()
optBasic()

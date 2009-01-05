stats <- read.table("~/sandbox/calku/simulator.git/results.txt", header=TRUE)
stats["mrRatio"] <- stats["numMappers"] / stats["numReducers"]
stats["mIO"] <- stats["shuffleSizeMb"] / stats["inputSizeMb"]
stats["rIO"] <- stats["outputSizeMb"] / stats["shuffleSizeMb"]  
@echo off

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" ReplicaManager3.RM3

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" ReplicaManager3.Server.Montreal

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" ReplicaManager3.Server.Sherbrooke

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" ReplicaManager3.Server.Quebec

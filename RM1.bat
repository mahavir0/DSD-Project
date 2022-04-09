@echo off

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" ReplicaManager1.RM1

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" ReplicaManager1.Server.Montreal

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" ReplicaManager1.Server.Sherbrooke

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" ReplicaManager1.Server.Quebec

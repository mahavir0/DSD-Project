@echo off

start cmd.exe /c start orbd -ORBInitialPort 1050 -ORBInitialHost localhost

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" FrontEnd.FrontEnd -ORBInitialPort 1050 -ORBInitialHost localhost

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" Client.Client -ORBInitialPort 1050 -ORBInitialHost localhost

start cmd.exe /c java -Dfile.encoding=Cp1252 -classpath "D:\Concordia\COMP 6231 DSD\DSD-Project\bin" Sequencer.Sequencer
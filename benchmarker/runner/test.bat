@echo off
wsl ./bench %1 >> output.txt
type output.txt
exit 0
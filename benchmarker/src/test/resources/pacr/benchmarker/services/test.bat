@echo off

IF NOT EXIST %1 (
echo Repository %1 does not exist.
exit 1
)

type result.txt

REM exit 0
@echo off
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
if "%OS%"=="Windows_NT" GOTO WinNT
GOTO Win9x

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Deinstallationsprogramm fuer die Heimverwaltung.
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:WinNT
start javaw -Dswing.noxp=true -jar Uninstaller/uninstaller.jar > uninstall.log
goto END

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:Win9x
javaw -Dswing.noxp=true -jar Uninstaller/uninstaller.jar

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:END
::eof
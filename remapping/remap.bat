set inputName=%1%
set outputName=%2%

java -jar SpecialSource.jar -i %inputName% -o s1.jar --srg-in moj-obf.txt --reverse -l -L --progress-interval 50
java -jar SpecialSource.jar -i s1.jar -o s2.jar --srg-in obf-spigot.csrg -l -L --progress-interval 50
java -jar SpecialSource.jar -i s2.jar -o %outputName% --srg-in spigot-spigot-fields.csrg --reverse -l -L --progress-interval 50
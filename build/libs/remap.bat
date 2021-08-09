java -jar SpecialSource.jar -i RoyalGrenadier-1.1.2-SNAPSHOT-mojang-mapped.jar -o s1.jar --srg-in moj-obf.txt --reverse -l -L
java -jar SpecialSource.jar -i s1.jar -o s2.jar --srg-in obf-spigot.csrg -l -L
java -jar SpecialSource.jar -i s2.jar -o RoyalGrenadier-1.1.2-SNAPSHOT-spigot-mapped.jar --srg-in spigot-spigot-fields.csrg --reverse -l -L
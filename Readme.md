Для сборки fat jar'а использовать:

`./gradlew build`

Для запуска собранного jar'а использовать:

`java -jar ./build/libs/vcs_assignment.jar`

По умолчанию используется JDBC-имплементация crud-методов. Для использования JPA можно запустить jar со следующим параметром.

`java -jar ./build/libs/vcs_assignment.jar --service.class=JPA`

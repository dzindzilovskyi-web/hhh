Сборка мода (нужны Java 21 и Gradle 8.14+ или gradle wrapper):

  gradle build

Готовый мод: build/libs/dropall-1.0.0.jar -> положить в папку mods.
Если gradle не установлен: скачайте любой шаблон с https://fabricmc.net/develop (1.21.11),
скопируйте из него папку gradle/ и файлы gradlew, gradlew.bat в этот проект, затем ./gradlew build
Актуальные yarn_mappings и loader_version берите там же и вставляйте в gradle.properties.

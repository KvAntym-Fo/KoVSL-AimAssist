# KoVSL Aim Assist — Minecraft 1.21.4

Клиентский Fabric-мод для Minecraft 1.21.4.

## Что есть

- Aim Assist по бинду.
- Бинд можно изменить в Minecraft → Options → Controls.
- Сила Aim Assist: 0.0–100.0%, шаг 0.1.
- Дальность: 1.0–20.0 блоков.
- FOV: 1–180 градусов.
- Цели:
  - обычные мобы;
  - обычные игроки;
  - невидимые игроки;
  - голые невидимые игроки;
  - NPC.
- Настройки сохраняются в `config/kovsl_aimassist.json`.
- По умолчанию:
  - включение/выключение — Right Shift;
  - настройки — Right Ctrl.

## Сборка

Требуется Java 21.

Проект рассчитан на:
- Minecraft 1.21.4
- Fabric Loader 0.16.9+
- Fabric API 0.119.4+1.21.4

В IntelliJ IDEA открой папку проекта как Gradle-проект и запусти:

`gradle build`

Готовый JAR появится в:

`build/libs/`

Важно: в этой среде нет доступа к Maven/Fabric-серверам, поэтому я не могу здесь физически собрать и проверить JAR против Minecraft 1.21.4. Я подготовил полный исходный проект, который должен собираться через Fabric Loom после загрузки зависимостей.

## Установка

1. Установить Fabric Loader для Minecraft 1.21.4.
2. Установить Fabric API для 1.21.4.
3. Положить `kovsl-aimassist-1.0.0.jar` из `build/libs` в `.minecraft/mods`.
4. Запустить Minecraft 1.21.4 Fabric.
5. Изменить бинд при необходимости в Controls.
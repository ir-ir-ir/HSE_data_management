# Лабораторная работа 4

**ВАРИАНТ 13**

## *1. Реализовать хранимую процедуру, возвращающую текстовую строку, содержащую информацию о машинисте(идентификатор, фамилия, дата, локомотив и расчет последней поездки). Обработать ситуацию, когда поездок не было.*

**Реализация**

![image](https://github.com/user-attachments/assets/30f0f756-0c12-4e58-a042-b04236fe71bf)

**Пример работы**

![image](https://github.com/user-attachments/assets/25c270ba-e015-4f91-bb93-a8fd1d073fe5)

![image](https://github.com/user-attachments/assets/53aaf52c-b1dd-44b9-bc6f-11f09bcb5e86)

![image](https://github.com/user-attachments/assets/1d0074d8-a31b-42b0-8a93-08e417d5c53d)

## *2. Добавить таблицу, содержащую списки машинистов, допущенных к управлению каждым из локомотивов. При вводе поездки проверять права машиниста.*

**Создание дополнительной таблицы**

![image](https://github.com/user-attachments/assets/86fd5dd8-1c14-4ac5-bd7d-a61b128ad593)

![image](https://github.com/user-attachments/assets/a01db6e6-53a2-4ffd-a78e-eb97014e4ca8)

**Реализация**

![image](https://github.com/user-attachments/assets/4a7d022e-8357-4bde-8a83-fd4d2a54598e)

**Пример работы**

Машинист с id = 2 - Савин, вторым локомотивом могут управлять Судаков и Сергеев

![image](https://github.com/user-attachments/assets/9a8e9072-8474-42c9-870f-f89d3d3620a7)

![image](https://github.com/user-attachments/assets/e70e2507-fad7-4221-826b-e70df1409706)

У Судакова id = 3

![image](https://github.com/user-attachments/assets/cb8aa7d2-d899-4f93-9140-d0b34de473ac)

![image](https://github.com/user-attachments/assets/5195b022-0e78-400e-9b71-075fa78673a7)

Неверные вводные:

![image](https://github.com/user-attachments/assets/e4f8192d-3b6c-4f7e-962a-6b8f4fd0d353)

## *3. Реализовать триггер такой, что при вводе строки в таблице поездок, если расчет не указан, то он вычисляется* 

**Реализация**

![image](https://github.com/user-attachments/assets/17bb3ab2-ec5e-44fd-93fe-cf5f187b49de)

**Пример работы**

![image](https://github.com/user-attachments/assets/cf1de3d8-655e-49a1-92c5-b1450282ad47)

![image](https://github.com/user-attachments/assets/7ee15742-65dd-4001-9bce-0d3c13dcff58)

Цена товара 10.000, sum_of_money посчитано верно

![image](https://github.com/user-attachments/assets/43844335-95f5-4aba-8031-eb7f0c519207)

Неверные вводные:

![image](https://github.com/user-attachments/assets/98af80ed-401d-48bd-9b28-8e4b93d54577)

## *4. Создать представление (view), содержащее поля: рег. номер, дата, фамилия машиниста, налог, марка локомотива, название и количество груза, расчет. Обеспечить возможность изменения взимаего налога. При этом должна быть произведен перерасчет.*

**Реализация view**

![image](https://github.com/user-attachments/assets/4badeca8-59d4-47d5-b258-ae0d3003af18)

**Пример работы**

![image](https://github.com/user-attachments/assets/d168c5e6-867a-4872-bc10-f7048d4cdf53)

**Возможность изменять налог**

![image](https://github.com/user-attachments/assets/b31ab844-96bd-4e22-bcb9-2056eac5a0ec)

**Пример работы**

![image](https://github.com/user-attachments/assets/c46abc6a-f9a1-4c31-b316-822c9baaaacb)

![image](https://github.com/user-attachments/assets/28788c99-1dcc-451e-88d7-e66d0cb350d0)

![image](https://github.com/user-attachments/assets/a6979738-2075-4142-bcd6-c5d045c29394)

// расчет поездки не сделала, так как он не зависит от налога. Из исходных данных видно, что он вычисляется как произведение количества товара и цены товара

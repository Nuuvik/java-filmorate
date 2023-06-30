# java-filmorate
Template repository for Filmorate project.

### Модель базы данных представлена на ER-диаграмме

---
![Модель базы данных](db.png)

### Примеры запросов в базу данных

---

<details>
  <summary>Получить фильм с id=123</summary>

```sql
    SELECT *
    FROM films
    WHERE film_id = 123;
```

</details>  

<details>
  <summary>Получить пользователя с id=123</summary>

```sql
    SELECT *
    FROM users
    WHERE user_id = 123;
```

</details>  

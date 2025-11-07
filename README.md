## Запуск контейнера 
```bash
docker run -d --hostname my-rabbit --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```
## Открыть веб
По адресу http://localhost:15672/#/

# Task Framework

## Docker

1. Check docker version
    ```bash
   docker --version
   ```
   
2. Run task-demo App
    ```bash 
    docker build -t task-demo .
    ```
   
## Docker compose

1. Check docker-compose version
   ```bash
   docker-compose --version
   ```

2. Run containers (run Prometheus and Grafana)
   ```bash
   docker-compose up -d
   ```
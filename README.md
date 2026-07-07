# Climalert

Sistema de monitoreo climático con alertas por correo electrónico.

## Descripción

Climalert consulta periódicamente la API de WeatherAPI, persiste los registros en una base de datos H2 embebida, evalúa si se superan umbrales críticos de temperatura y humedad, y envía alertas por email con un sistema de limitación para evitar notificaciones repetitivas.

## Arquitectura

### Interface saliente (Outbound)

El sistema actúa como **consumidor** de la WeatherAPI. La comunicación es unidireccional (el sistema consulta, la API responde). El contrato está definido por los parámetros obligatorios de la API (API Key, ubicación).

### Polling (Pull-based)

WeatherAPI no ofrece Webhooks, por lo que se implementa un modelo de **Polling**. El sistema ejecuta dos tareas programadas que separan la ingesta del procesamiento:

| Tarea | Frecuencia | Responsabilidad |
|---|---|---|
| Extracción | Cada 5 minutos | Consultar WeatherAPI y persistir el registro |
| Análisis | Cada 1 minuto | Evaluar el último registro persistido y notificar si es crítico |

El desacople entre ambas tareas permite que la ingesta y el análisis evolucionen de forma independiente, y que un error en una no bloquee a la otra.

### Respaldo ante fallas de integración

La componente de integración (`WeatherService`) captura excepciones en la comunicación con la API externa. Si la consulta falla (timeout, error HTTP, red caída), el sistema registra el error y continúa, evitando que el scheduler quede bloqueado.

### Limitación de notificaciones (Rate Limiting)

Para evitar que una condición crítica prolongada genere correos repetidos cada minuto, el sistema implementa dos controles:

- **Cuota diaria**: máximo 3 alertas por día calendario.
- **Cool-down**: mínimo 4 horas entre una notificación y la siguiente.

Cada alerta enviada se persiste en la entidad `AlertNotification`, de modo que los límites se mantienen incluso si la aplicación se reinicia.

## Componentes

### Schedulers

- **`ClimaScheduler`** (`@Scheduled(fixedRate = 300000)`): ejecuta la extracción de datos climáticos cada 5 minutos.
- **`AlertaScheduler`** (`@Scheduled(fixedRate = 60000)`): ejecuta el análisis de condiciones críticas cada 1 minuto.

### Servicios

- **`WeatherService`**: cliente REST contra WeatherAPI. Construye la URL con API key y ubicación, ejecuta la llamada y mapea la respuesta al DTO `WeatherApiResponse`.
- **`AlertaService`**: contiene la lógica de umbrales (`temperatura > 35°C` y `humedad > 60%`) y el rate limiting (consulta `AlertNotificationRepository` antes de notificar).
- **`NotificacionService`**: construye y envía el correo electrónico vía SMTP.

### Entidades

- **`RegistroClima`**: persiste cada lectura de WeatherAPI (temperatura, humedad, ubicación, fecha de consulta).
- **`AlertNotification`**: persiste cada alerta enviada (temperatura, humedad, fecha de envío). Usada para controlar cuota diaria y cool-down.

## Configuración

El archivo `application.yml` soporta **variables de entorno** con valores por defecto. Copiar `application.example.yml` como `application.yml` y reemplazar los valores según el entorno, o bien setear las siguientes variables de entorno:

| Variable | Default | Descripción |
|---|---|---|
| `WEATHERAPI_API_KEY` | `(key del enunciado)` | API key de WeatherAPI |
| `WEATHERAPI_BASE_URL` | `https://api.weatherapi.com/v1` | Base URL de WeatherAPI |
| `WEATHERAPI_LOCATION` | `CABA` | Ubicación a consultar |
| `SMTP_HOST` | `smtp-relay.brevo.com` | Servidor SMTP |
| `SMTP_PORT` | `587` | Puerto SMTP |
| `SMTP_USERNAME` | `(usuario del enunciado)` | Usuario SMTP |
| `SMTP_PASSWORD` | `(password del enunciado)` | Password SMTP |
| `DB_URL` | `jdbc:h2:file:./data/climalertdb` | URL de conexión H2 |
| `CLIMALERT_DESTINATARIOS` | `emergencias@clima.com` | Emails destino separados por coma |

### application.example.yml

Se incluye un archivo `application.example.yml` con valores de ejemplo. Para usarlo:

```bash
cp src/main/resources/application.example.yml src/main/resources/application.yml
```

Luego editar los valores marcados como `su-*` según corresponda.

## Ejecución

### Requisitos

- Java 17+
- Maven (o usar el wrapper `mvnw`)

### Compilar y ejecutar

```bash
mvn clean package
java -jar target/climalert-1.0.0.jar
```

Por defecto arranca en `localhost:8080` y la consola H2 está disponible en `/h2-console`.

## Decisiones de diseño

- **Persistencia embebida (H2)**: no requiere infraestructura externa de base de datos. El archivo se almacena en `./data/climalertdb`.
- **Schedulers desacoplados**: la extracción (5 min) y el análisis (1 min) corren en schedulers separados. Esto evita que una demora en la ingesta retrase el análisis, y viceversa.
- **Rate limiting persistido**: los límites de notificación se guardan en la misma base de datos, por lo que sobreviven a reinicios de la aplicación. Elegido sobre una solución en memoria para mantener el historial de alertas y no perder el estado ante un reinicio.
- **Umbrales hardcodeados**: temperatura > 35°C y humedad > 60% son valores fijos definidos en `AlertaService`. Se priorizó simplicidad; una mejora futura sería externalizarlos a configuración.

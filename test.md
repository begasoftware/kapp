stateDiagram-v2
    [*] --> Idle

    %% --- Estados base ---
    Idle: Esperando interacción (sin toques/teclado)
    Attention: Mostrar frase llamativa
    Input: Captura palabra desde iPad
    Moderation: Moderación (opcional, ChatGPT/Llama local)
    UpdateCloud: Actualiza nube en pantalla LED
    Cooldown: Pausa 5 minutos (sin frases de atención)

    %% --- Flujo de atención ---
    Idle --> Attention : noInteractionTimeout
    Attention --> Idle : attentionDuration
    Attention --> Input : Usuario escribe palabra

    %% --- Entrada de palabras ---
    Idle --> Input : Usuario escribe palabra

    %% --- Moderación opcional ---
    Input --> Moderation : [moderationEnabled]\nEnviar a revisión
    Input --> UpdateCloud : [!moderationEnabled]\nPublicar directo

    state Moderation {
        [*] --> PreFilters
        PreFilters: Reglas rápidas (longitud, URLs, lista negra)
        PreFilters --> LLMCheck : si no hay bloqueo
        PreFilters --> Rejected : infringe reglas

        LLMCheck: Moderación LLM (ChatGPT u Ollama)
        LLMCheck --> Approved : aprobado
        LLMCheck --> Rejected : rechazado
        LLMCheck --> LogUncertain : incierto → guardar en log

        Approved --> [*]
        Rejected --> [*]
        LogUncertain: Guardar en log (no mostrar en pantalla)
        LogUncertain --> [*]
    }

    Moderation --> UpdateCloud : Approved
    Moderation --> Idle : Rejected | LogUncertain | timeoutModeration

    %% --- Actualización + cooldown ---
    UpdateCloud --> Cooldown
    state Cooldown {
        [*] --> ShowRecent
        ShowRecent: Mostrar última palabra destacada
        ShowRecent --> RotateRecent : 10s
        RotateRecent: Rotar últimas N palabras
        RotateRecent --> ShowRecent : 10s
    }
    Cooldown --> Idle : tras 5m
    Cooldown --> Input : Nueva palabra (interrumpe cooldown)

    %% --- Fin opcional ---
    Idle --> [*]

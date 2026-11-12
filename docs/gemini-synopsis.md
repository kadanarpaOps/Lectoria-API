# Configurar el asistente de sinopsis con Gemini

El asistente de historias usa Spring AI en el backend para llamar a Gemini Developer API. La clave nunca se envía al cliente. El endpoint requiere sesión y limita cada usuario a tres generaciones por minuto; la idea se limita a 2.000 caracteres y la generación a 180 tokens.

1. Abre [Google AI Studio API keys](https://aistudio.google.com/apikey), selecciona o crea un proyecto de Google Cloud y crea una clave para Gemini API.
2. En la configuración del backend, establece `GEMINI_API_KEY` como secreto y habilita la funcionalidad. No la guardes en el repositorio ni en variables `VITE_*`.

```text
LECTORIA_AI_ENABLED=true
LECTORIA_AI_MODEL_CHAT=google-genai
GEMINI_API_KEY=<clave-de-gemini>
GEMINI_MODEL=gemini-3.1-flash-lite
```

`GEMINI_MODEL` es opcional; el valor predeterminado es `gemini-3.1-flash-lite`. Revisa [los modelos disponibles](https://ai.google.dev/gemini-api/docs/models) y [los precios y límites](https://ai.google.dev/gemini-api/docs/pricing) de tu proyecto antes de usarlo. El nivel gratuito tiene límites y sus condiciones indican que el contenido puede usarse para mejorar los productos de Google; para contenido que no se use con ese fin, revisa el nivel de pago y sus términos. Configura límites de uso y alertas de facturación en Google Cloud.

Reinicia o redepliega el backend tras establecer las variables. La generación solo devuelve una propuesta editable: no crea ni guarda una historia. Solo se envían a Google la idea escrita para el asistente y las etiquetas elegidas.

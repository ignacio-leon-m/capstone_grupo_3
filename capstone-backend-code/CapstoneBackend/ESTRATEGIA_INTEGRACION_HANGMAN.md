# 🎮 Análisis y Estrategia de Integración - Hangman Game

## 🎯 REGLAS DE IMPLEMENTACIÓN DEL JUEGO HANGMAN ⭐⭐⭐

### **Reglas de Negocio Definidas**

#### 1️⃣ **Lista de Conceptos**
- El juego se basa en una **lista de conceptos** extraídos de PDFs por Gemini API
- Cada concepto debe tener una **pregunta asociada** que sirva como pista
- La pregunta debe ser **explícita** y **definir completamente** el concepto por sí misma

**Ejemplo**:
```json
{
  "concepto": "VARIABLE",
  "pregunta": "¿Cómo se llama el espacio en memoria que almacena un valor que puede cambiar durante la ejecución del programa?",
  "hint": "Espacio en memoria para almacenar un valor"
}
```

---

#### 2️⃣ **Mecánica de Juego**

##### **Representación del Concepto**
- El concepto oculto se representa con **guiones bajos** (`_`)
- Ejemplo: `RECURSIVIDAD` → `_ _ _ _ _ _ _ _ _ _ _`

##### **Aciertos**
- Cada **letra correcta** desbloquea la letra en **todas sus posiciones**
- Ejemplo: Usuario ingresa 'A' en `BANANA` → `_ A _ A _ A`

##### **Letras Repetidas (Ya Adivinadas)**
- Si el usuario intenta ingresar una letra **ya usada** (correcta o incorrecta):
  - **NO cuenta como equivocación**
  - Se envía mensaje: *"Ya has intentado esta letra"*
  - Se muestra lista de letras usadas

##### **Caracteres Especiales**
- Si el usuario ingresa un **carácter no alfabético** (números, símbolos):
  - **SÍ cuenta como equivocación** (-1 vida)
  - Se envía mensaje: *"Solo se permiten letras"*

---

#### 3️⃣ **Sistema de Vidas y Conceptos**

##### **Equivocaciones Totales**
- El jugador tiene **3 equivocaciones TOTALES** por partida
- **NO son 3 equivocaciones por concepto**, sino para **toda la partida**
- Al llegar a 3 errores: **GAME OVER**

##### **Conceptos por Partida**
- Cada partida incluye de **10 a 12 conceptos** a adivinar
- Los conceptos se presentan **uno a la vez**
- El jugador debe completar cada concepto antes de pasar al siguiente

##### **Progreso de la Partida**
```
Partida Iniciada: 12 conceptos | 3 vidas | Puntaje: 0
  ↓
Concepto 1: ACIERTO → Puntaje: 1 | Vidas: 3
Concepto 2: ACIERTO → Puntaje: 2 | Vidas: 3
Concepto 3: ERROR (no adivinado) → Puntaje: 2 | Vidas: 2
Concepto 4: ACIERTO → Puntaje: 3 | Vidas: 2
Concepto 5: ERROR → Puntaje: 3 | Vidas: 1
Concepto 6: ERROR → Puntaje: 3 | Vidas: 0 → GAME OVER
  ↓
Resultado: 3/6 conceptos correctos
```

---

#### 4️⃣ **Sistema de Puntuación**

##### **Puntos por Concepto**
- Cada concepto **completado correctamente**: **+1 punto**
- Conceptos **no adivinados**: **0 puntos** (nada resta)

##### **Puntuación Total**
- **Puntaje Total = Suma de conceptos aprobados**
- Ejemplo: 8 de 12 conceptos correctos = **8 puntos**

##### **NO se Restan Puntos**
- Las equivocaciones **NO restan puntos**
- Solo afectan el número de vidas restantes

**Fórmula**:
```javascript
puntajeTotal = conceptosCompletados * 1;
// NO hay multiplicadores ni bonificaciones
// Sistema simple: 1 concepto = 1 punto
```

---

#### 5️⃣ **Finalización y Análisis** ⭐

##### **Condiciones de Finalización**
El juego termina cuando:
1. **Se agotan las 3 vidas** (3 errores totales), O
2. **Se completan todos los conceptos** (10-12)

##### **Análisis Post-Juego**
Al finalizar, el sistema debe:

1. **Identificar conceptos erróneos**:
   ```sql
   SELECT c.palabra_concepto, c.hint, t.nombre AS tema
   FROM resultados_hangman rh
   JOIN conceptos c ON rh.id_concepto = c.id
   JOIN temas t ON c.id_tema = t.id
   WHERE rh.id_juego = :juegoId 
     AND rh.adivinado = FALSE;
   ```

2. **Generar explicación con IA**:
   - Enviar a Gemini API los conceptos fallados
   - Solicitar explicación pedagógica de cada concepto
   - Presentar al usuario con formato educativo

3. **Registro para aprendizaje adaptativo**:
   - Marcar temas de conceptos fallados
   - Priorizar esos temas en próximas partidas
   - Actualizar métricas del usuario

**Ejemplo de Análisis**:
```json
{
  "puntajeTotal": 8,
  "conceptosTotales": 12,
  "conceptosCorrectos": 8,
  "conceptosErroneos": 4,
  "vidasUsadas": 3,
  "resultadoPartida": "COMPLETADO",
  "analisisErrores": [
    {
      "concepto": "RECURSIVIDAD",
      "pregunta": "¿Qué técnica permite a una función llamarse a sí misma?",
      "tema": "Algoritmos Avanzados",
      "explicacion": "La recursividad es una técnica de programación donde una función se llama a sí misma para resolver un problema dividiéndolo en subproblemas más pequeños..."
    },
    {
      "concepto": "PUNTERO",
      "pregunta": "¿Cómo se llama la variable que almacena la dirección de memoria?",
      "tema": "Gestión de Memoria",
      "explicacion": "Un puntero es una variable especial que almacena la dirección de memoria de otra variable..."
    }
  ],
  "recomendacion": "Deberías repasar los temas: Algoritmos Avanzados, Gestión de Memoria"
}
```

---

### **Comparación con Implementación Actual**

| Aspecto | Implementación Actual | Nuevas Reglas |
|---------|----------------------|---------------|
| **Equivocaciones** | 6 intentos por palabra | **3 totales por partida** ⚠️ |
| **Conceptos** | 1 palabra a la vez | **10-12 conceptos por partida** |
| **Puntuación** | Compleja (tiempo, intentos) | **Simple: 1 punto por concepto** |
| **Letra repetida** | Se deshabilita botón | **Mensaje + NO cuenta error** |
| **Carácter especial** | No validado | **Cuenta como error** |
| **Análisis final** | Solo muestra resultado | **Explicación de errores con IA** ⭐ |

---

### **Lógica de Referencia (Código Python Adaptado)**

```python
# ADAPTACIÓN DE LA LÓGICA PROPORCIONADA

class HangmanGame:
    def __init__(self):
        self.max_lives = 3  # ⚠️ 3 TOTALES para toda la partida
        self.concepts_per_game = random.randint(10, 12)
        self.current_lives = self.max_lives
        self.used_letters = set()
        self.score = 0
        self.concepts_played = []
    
    def play_concept(self, concept, question):
        """Juega un concepto individual"""
        word_to_guess = ["_"] * len(concept)
        concept_letters = set()
        
        print(f"Pregunta: {question}")
        print(f"Concepto: {' '.join(word_to_guess)}")
        
        while True:
            # Verificar si se perdió
            if self.current_lives == 0:
                return False  # Concepto NO completado
            
            # Verificar si se ganó el concepto
            if "_" not in word_to_guess:
                self.score += 1  # +1 punto
                return True  # Concepto COMPLETADO
            
            # Obtener input del usuario
            guess = input("Ingresa una letra: ").strip().upper()
            
            # VALIDACIÓN 1: Vacío
            if len(guess) == 0:
                print("No has ingresado nada")
                self.current_lives -= 1
                continue
            
            # VALIDACIÓN 2: Más de un carácter
            if len(guess) > 1:
                print("Solo puedes ingresar una letra")
                self.current_lives -= 1
                continue
            
            # VALIDACIÓN 3: Carácter especial (no alfabético)
            if not guess.isalpha():
                print("Solo se permiten letras")
                self.current_lives -= 1  # ⚠️ SÍ cuenta como error
                continue
            
            # VALIDACIÓN 4: Letra ya usada
            if guess in self.used_letters:
                print(f"Ya has usado la letra '{guess}'")
                print(f"Letras usadas: {', '.join(sorted(self.used_letters))}")
                # ⚠️ NO cuenta como error
                continue
            
            # Agregar a letras usadas
            self.used_letters.add(guess)
            
            # VALIDACIÓN 5: Letra incorrecta
            if guess not in concept:
                print("¡Letra incorrecta!")
                self.current_lives -= 1
                print(f"Vidas restantes: {self.current_lives}")
                continue
            
            # ACIERTO: Llenar TODAS las posiciones de la letra
            print("¡Letra correcta!")
            for i, letter in enumerate(concept):
                if letter == guess:
                    word_to_guess[i] = guess
            
            print(f"Concepto: {' '.join(word_to_guess)}")
    
    def play_game(self, concepts):
        """Juega partida completa de 10-12 conceptos"""
        concepts_to_play = random.sample(concepts, self.concepts_per_game)
        
        for i, concept_data in enumerate(concepts_to_play, 1):
            print(f"\n--- Concepto {i}/{self.concepts_per_game} ---")
            print(f"Vidas: {self.current_lives} | Puntaje: {self.score}")
            
            completed = self.play_concept(
                concept_data['palabra'], 
                concept_data['pregunta']
            )
            
            self.concepts_played.append({
                'concepto': concept_data['palabra'],
                'completado': completed,
                'tema': concept_data['tema']
            })
            
            # Verificar GAME OVER
            if self.current_lives == 0:
                print("\n¡GAME OVER! Has agotado tus vidas")
                break
        
        # Análisis final
        self.analyze_results()
    
    def analyze_results(self):
        """Análisis de conceptos erróneos"""
        failed_concepts = [
            c for c in self.concepts_played 
            if not c['completado']
        ]
        
        print(f"\n=== RESULTADO FINAL ===")
        print(f"Puntaje: {self.score}/{len(self.concepts_played)}")
        print(f"Conceptos erróneos: {len(failed_concepts)}")
        
        if failed_concepts:
            print("\n--- Conceptos que debes repasar ---")
            for concept in failed_concepts:
                print(f"- {concept['concepto']} (Tema: {concept['tema']})")
                # Aquí llamaríamos a Gemini API para explicación
```

---

## 📋 Análisis del Juego Actual

### Estructura de Archivos
```
hangman-game/
├── index.html          - UI del juego
├── style.css           - Estilos
├── images/             - Assets visuales (SVGs + GIFs)
│   ├── hangman-0.svg a hangman-6.svg (7 estados)
│   ├── victory.gif
│   └── lost.gif
└── script/
    ├── script.js       - Lógica del juego (115 líneas)
    └── word-list.js    - Lista estática de palabras (263 líneas)
```

---

## 🔍 Análisis de la Implementación Actual

### ✅ Aspectos Positivos
1. **Juego Funcional**: Lógica de ahorcado completa
2. **UI Limpia**: Interfaz simple y funcional
3. **Estados Visuales**: 7 imágenes SVG para progreso
4. **Modularidad**: Código separado en funciones
5. **Eventos**: Sistema de eventos bien estructurado

### ⚠️ Limitaciones Actuales
1. **Datos Estáticos**: Lista hardcodeada de palabras genéricas (guitar, oxygen, pizza...)
2. **Sin Backend**: No se comunica con servidor
3. **Sin Persistencia**: No guarda métricas, puntajes ni progreso
4. **Sin Autenticación**: No hay concepto de usuario
5. **Sin Contexto Académico**: Palabras no relacionadas con materias
6. **Sin IA**: No hay aprendizaje adaptativo
7. **Standalone**: No integrado al ecosistema Brain Boost

---

## 🔄 FLUJO REAL DEL SISTEMA (Con IA de Gemini)

### **Flujo de Carga de Contenido por Profesor** ⭐⭐⭐

```
1. PROFESOR (App Android) 
   ↓ Selecciona asignatura y tema
   ↓ Carga archivo PDF (apuntes, material de estudio)
   ↓
2. APP MÓVIL → BFF: POST /api/content/upload-pdf
   {
     asignaturaId: UUID,
     temaId: UUID,
     archivo: PDF (multipart/form-data)
   }
   ↓
3. BFF → Content Service: Guarda PDF en MongoDB
   {
     archivoId: UUID,
     nombreArchivo: "apuntes_algoritmos.pdf",
     idAsignatura: UUID,
     idTema: UUID,
     fechaCarga: timestamp
   }
   ↓
4. Content Service → IA Service: POST /api/ia/procesar-pdf
   {
     archivoId: UUID,
     temaId: UUID,
     tipoExtraccion: ["conceptos", "preguntas"]
   }
   ↓
5. IA SERVICE (Procesamiento Automático con Gemini):
   
   a) Lee PDF desde MongoDB
   b) Extrae texto completo
   c) Envía a Google Gemini API con prompt:
   
      "Analiza este documento académico y extrae:
       1. Lista de conceptos clave (términos técnicos de 1-3 palabras)
       2. Para cada concepto, una definición/hint corta
       3. Preguntas de comprensión sobre el contenido
       
       Formato JSON:
       {
         'conceptos': [
           {'palabra': 'variable', 'hint': 'Espacio en memoria...'},
           {'palabra': 'recursividad', 'hint': 'Función que se llama...'}
         ],
         'preguntas': [
           {'texto': '¿Qué es...?', 'respuesta': '...', 'tema': '...'}
         ]
       }"
   
   d) Gemini procesa y retorna JSON estructurado
   
   e) IA Service valida y limpia respuesta
   
   ↓
6. IA Service → Content Service: Inserta en PostgreSQL
   
   INSERT INTO conceptos (palabra_concepto, hint, id_tema)
   VALUES ('variable', 'Espacio en memoria...', UUID_tema);
   
   INSERT INTO preguntas (texto, respuesta_correcta, id_tema, id_asignatura)
   VALUES ('¿Qué es una variable?', '...', UUID_tema, UUID_asig);
   
   ↓
7. Content Service → MongoDB: Marca archivo como procesado
   
   UPDATE archivos_procesados 
   SET estado = 'COMPLETADO', 
       conceptos_extraidos = 15,
       preguntas_generadas = 20
   
   ↓
8. Content Service → App Móvil: Respuesta exitosa
   {
     success: true,
     conceptosExtraidos: 15,
     preguntasGeneradas: 20,
     mensaje: "PDF procesado correctamente"
   }
```

### **Flujo del Juego Hangman (Consumiendo conceptos extraídos)** ⭐⭐⭐

```
1. ESTUDIANTE (App Android o Web)
   ↓ Selecciona asignatura
   ↓ Inicia juego Hangman
   ↓
2. App → BFF: POST /api/juegos/hangman/iniciar
   {
     asignaturaId: UUID,
     usuarioId: UUID
   }
   ↓
3. BFF → Scoring Service: Crea sesión de juego
   
   INSERT INTO juegos (id_usuario, id_asignatura, nombre_juego, intentos_restantes)
   VALUES (UUID_user, UUID_asig, 'Hangman', 6)
   RETURNING id AS juegoId;
   
   ↓
4. BFF → IA Service: GET /api/ia/conceptos-adaptativos
   {
     usuarioId: UUID,
     asignaturaId: UUID,
     limite: 10
   }
   ↓
5. IA Service: Análisis adaptativo
   
   a) Consulta tabla metricas:
      - Identifica temas con más errores del usuario
      - Calcula tasa de error por tema
   
   b) Prioriza conceptos de temas débiles:
   
      SELECT c.id, c.palabra_concepto, c.hint, t.nombre AS tema
      FROM conceptos c
      JOIN temas t ON c.id_tema = t.id
      WHERE t.id IN (
        -- Temas donde el usuario tiene más errores
        SELECT tema_id FROM analisis_debilidades_usuario
        WHERE usuario_id = UUID AND tasa_error > 0.5
      )
      ORDER BY RANDOM()
      LIMIT 10;
   
   c) Si es primera vez del usuario o pocos datos:
      - Retorna conceptos aleatorios del tema
   
   ↓
6. BFF → App: Lista de conceptos para jugar
   {
     juegoId: UUID,
     conceptos: [
       {id: UUID, palabra: 'recursividad', hint: 'Función que...'},
       {id: UUID, palabra: 'puntero', hint: 'Variable que...'}
     ]
   }
   ↓
7. USUARIO juega cada palabra
   ↓ Por cada letra intentada:
   
8. App → BFF: POST /api/juegos/hangman/intento
   {
     juegoId: UUID,
     conceptoId: UUID,
     letra: 'a',
     esCorrecta: true/false,
     tiempoMs: 1500
   }
   ↓
9. BFF → Scoring Service: Registra métrica
   
   INSERT INTO metricas_juego_hangman (
     id_juego, id_concepto, id_usuario,
     letra_intentada, es_correcta, tiempo_respuesta_ms
   ) VALUES (...);
   
   UPDATE juegos SET intentos_restantes = intentos_restantes - 1
   WHERE id = juegoId;
   
   ↓
10. Al completar palabra (victoria/derrota):
    
    App → BFF: POST /api/juegos/hangman/finalizar-palabra
    {
      juegoId: UUID,
      conceptoId: UUID,
      resultado: 'VICTORIA' / 'DERROTA',
      puntaje: 120
    }
    
    ↓
    Scoring Service:
    - Actualiza puntaje de la sesión
    - Registra en metricas si adivinó o no el concepto completo
    
    ↓
11. Al finalizar todas las palabras:
    
    App → BFF: PUT /api/juegos/hangman/{juegoId}/finalizar
    {
      puntajeTotal: 850,
      estadoPartida: 'COMPLETADO'
    }
    
    ↓
    Scoring Service:
    - UPDATE juegos SET fecha_fin = NOW(), puntaje = 850
    - UPDATE puntajes SET puntaje = puntaje + 850
    - Calcula nueva posición en ranking
```

---

## 🎯 Objetivos de Integración

### Transformar el Hangman en:
1. **Juego Académico**: Conceptos/términos de asignaturas del Duoc UC
2. **Conectado al Backend**: Comunicación con microservicios
3. **Personalizado**: Palabras basadas en temas débiles del estudiante
4. **Gamificado**: Sistema de puntajes y métricas
5. **Adaptativo**: Integrado con IA Service para contenido dinámico

---

## 🏗️ Estrategia de Integración

### Fase 1: Análisis y Mapeo
- [x] Analizar código actual del Hangman
- [ ] Identificar puntos de integración con Backend
- [ ] Definir estructura de datos requerida
- [ ] Mapear flujo de datos entre Frontend y Microservicios

### Fase 2: Adaptación de Datos
- [ ] Crear endpoint en Content Service para obtener conceptos
- [ ] Modificar `word-list.js` para cargar desde API
- [ ] Implementar modelo de datos compatible con tabla `conceptos`

### Fase 3: Integración con Backend
- [ ] Conectar con Auth Service (JWT)
- [ ] Conectar con Content Service (conceptos)
- [ ] Conectar con Scoring Service (métricas)
- [ ] Conectar con IA Service (conceptos adaptativos)

### Fase 4: Gamificación
- [ ] Implementar sistema de puntajes
- [ ] Registrar métricas de rendimiento
- [ ] Calcular tiempo de respuesta
- [ ] Actualizar puntaje total

### Fase 5: Aprendizaje Adaptativo
- [ ] Solicitar conceptos de temas débiles
- [ ] Priorizar palabras según análisis de IA
- [ ] Reforzar áreas problemáticas

---

## 📊 Mapeo de Datos

### Transformación de Datos con IA

#### **ANTES (word-list.js - Estático)**:
```javascript
{
  word: 'guitar',
  hint: 'A musical instrument with strings.'
}
```

#### **FLUJO AUTOMÁTICO CON GEMINI**:

**1. Profesor carga PDF** → `apuntes_algoritmos.pdf`

**2. Gemini API procesa y extrae**:
```json
{
  "conceptos": [
    {
      "palabra": "VARIABLE",
      "hint": "Espacio en memoria para almacenar un valor que puede cambiar"
    },
    {
      "palabra": "RECURSIVIDAD",
      "hint": "Técnica donde una función se llama a sí misma"
    },
    {
      "palabra": "PUNTERO",
      "hint": "Variable que almacena la dirección de memoria de otra variable"
    }
  ],
  "preguntas": [
    {
      "texto": "¿Qué es una variable en programación?",
      "respuesta": "Un espacio en memoria para almacenar un valor",
      "conceptos_relacionados": ["variable", "memoria", "tipo-dato"]
    }
  ]
}
```

**3. IA Service inserta en PostgreSQL**:
```sql
-- Tabla conceptos (AHORA CON HINT)
INSERT INTO conceptos (palabra_concepto, hint, id_tema)
VALUES 
  ('VARIABLE', 'Espacio en memoria para almacenar un valor', UUID_tema),
  ('RECURSIVIDAD', 'Técnica donde una función se llama a sí misma', UUID_tema),
  ('PUNTERO', 'Variable que almacena la dirección de memoria de otra variable', UUID_tema);

-- Tabla preguntas (relacionadas con conceptos)
INSERT INTO preguntas (texto, respuesta_correcta, id_tema, id_asignatura)
VALUES ('¿Qué es una variable...?', 'Un espacio en memoria...', UUID_tema, UUID_asig);
```

**4. Hangman consume desde API**:
```javascript
// GET /api/ia/conceptos-adaptativos?usuarioId=xxx&asignaturaId=yyy
{
  "juegoId": "uuid-sesion",
  "conceptos": [
    {
      "id": "uuid-concepto-1",
      "palabra": "recursividad",
      "hint": "Técnica donde una función se llama a sí misma",
      "tema": "Algoritmos Avanzados",
      "prioridad": "ALTA",  // Usuario tiene 75% error en este tema
      "dificultad": "intermedio"
    },
    {
      "id": "uuid-concepto-2",
      "palabra": "variable",
      "hint": "Espacio en memoria para almacenar un valor",
      "tema": "Conceptos Básicos",
      "prioridad": "BAJA",   // Usuario domina este tema
      "dificultad": "basico"
    }
  ]
}
```

---

## 🔗 Integración con Base de Datos

### Tablas Involucradas (MODIFICADAS para soportar extracción automática)

#### 1. **`conceptos`** ⭐⭐⭐ (ACTUALIZADA)
```sql
CREATE TABLE conceptos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    palabra_concepto VARCHAR(255) NOT NULL,
    hint TEXT,  -- Generado automáticamente por Gemini
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_tema UUID NOT NULL,
    FOREIGN KEY (id_tema) REFERENCES temas(id)
);
```
**Uso**: Fuente de palabras para Hangman, **SIEMPRE extraídas automáticamente por Gemini**

**Importante**: 
- ✅ Todos los conceptos vienen de IA (automatización total)
- ✅ No hay entrada manual de conceptos
- ✅ `hint` generado por Gemini basado en el contexto del PDF

#### 2. **`archivos_procesados`** ⭐⭐⭐ (NUEVA TABLA)
```sql
CREATE TABLE archivos_procesados (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_mongodb VARCHAR(500) NOT NULL,  -- Referencia al archivo en MongoDB
    id_asignatura UUID NOT NULL,
    id_tema UUID,
    id_usuario_carga UUID NOT NULL,  -- Profesor que cargó
    fecha_carga TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_procesamiento TIMESTAMP,
    estado VARCHAR(50) NOT NULL,  -- 'PENDIENTE', 'PROCESANDO', 'COMPLETADO', 'ERROR'
    conceptos_extraidos INT DEFAULT 0,
    preguntas_generadas INT DEFAULT 0,
    detalle_error TEXT,
    tiempo_procesamiento_ms INT,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    FOREIGN KEY (id_tema) REFERENCES temas(id),
    FOREIGN KEY (id_usuario_carga) REFERENCES usuarios(id)
);
```
**Uso**: Auditoría y tracking del procesamiento de PDFs con Gemini (COMPARTIDA POR TODOS LOS JUEGOS)

---

### 🎮 **Tablas Específicas por Juego** (Patrón Escalable)

#### 3. **`metricas_juego_hangman`** ⭐⭐⭐ (ESPECÍFICA HANGMAN)
```sql
CREATE TABLE metricas_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    letra_intentada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    posicion_letra INT,  -- Posición en la palabra (0-indexed)
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE
);

CREATE INDEX idx_metricas_hangman_juego ON metricas_juego_hangman(id_juego);
CREATE INDEX idx_metricas_hangman_usuario ON metricas_juego_hangman(id_usuario);
CREATE INDEX idx_metricas_hangman_concepto ON metricas_juego_hangman(id_concepto);

COMMENT ON TABLE metricas_juego_hangman IS 'Métricas específicas del juego Hangman - cada intento de letra';
```
**Uso**: Análisis granular de cada intento de letra en Hangman

#### 4. **`resultados_juego_hangman`** ⭐⭐ (ESPECÍFICA HANGMAN)
```sql
CREATE TABLE resultados_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    adivinado BOOLEAN NOT NULL,  -- TRUE si completó la palabra
    intentos_usados INT NOT NULL,  -- Cuántos errores tuvo en esta palabra
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    vidas_restantes INT,  -- Cuántas vidas quedaban al terminar esta palabra
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE,
    UNIQUE (id_juego, id_concepto)
);

CREATE INDEX idx_resultados_hangman_juego ON resultados_juego_hangman(id_juego);
CREATE INDEX idx_resultados_hangman_adivinado ON resultados_juego_hangman(adivinado);

COMMENT ON TABLE resultados_juego_hangman IS 'Resultado final de cada palabra jugada en Hangman';
```
**Uso**: Resultado final de cada palabra jugada en Hangman

---

#### 5. **`metricas_juego_crisscross`** ⭐⭐⭐ (ESPECÍFICA CRISS-CROSS - EJEMPLO FUTURO)
```sql
-- TABLA DE EJEMPLO PARA PRÓXIMO JUEGO: CRISS-CROSS PUZZLE
CREATE TABLE metricas_juego_crisscross (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    posicion_fila INT NOT NULL,  -- Posición en la grilla (fila)
    posicion_columna INT NOT NULL,  -- Posición en la grilla (columna)
    direccion VARCHAR(20),  -- 'HORIZONTAL', 'VERTICAL'
    letra_colocada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    tiempo_respuesta_ms INT,
    pista_usada BOOLEAN DEFAULT FALSE,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE
);

CREATE INDEX idx_metricas_crisscross_juego ON metricas_juego_crisscross(id_juego);
CREATE INDEX idx_metricas_crisscross_concepto ON metricas_juego_crisscross(id_concepto);

COMMENT ON TABLE metricas_juego_crisscross IS 'Métricas específicas del juego Criss-Cross Puzzle - cada letra colocada';
```

#### 6. **`resultados_juego_crisscross`** ⭐⭐ (ESPECÍFICA CRISS-CROSS - EJEMPLO FUTURO)
```sql
CREATE TABLE resultados_juego_crisscross (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    completado BOOLEAN NOT NULL,
    casillas_correctas INT,
    casillas_totales INT,
    pistas_usadas INT DEFAULT 0,
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE,
    UNIQUE (id_juego, id_concepto)
);

CREATE INDEX idx_resultados_crisscross_juego ON resultados_juego_crisscross(id_juego);

COMMENT ON TABLE resultados_juego_crisscross IS 'Resultado final de cada palabra en Criss-Cross Puzzle';
```

---

### 📊 **Patrón de Nomenclatura para Nuevos Juegos**

```
metricas_juego_{nombre_juego}       → Métricas granulares específicas del juego
resultados_juego_{nombre_juego}     → Resultados finales por concepto/palabra
```

**Ejemplos de futuros juegos**:
- ✅ `metricas_juego_hangman` + `resultados_juego_hangman` (Ahorcado)
- 🎯 `metricas_juego_crisscross` + `resultados_juego_crisscross` (Crucigrama)
- 🧩 `metricas_juego_memory` + `resultados_juego_memory` (Memorice)
- 🎲 `metricas_juego_trivia` + `resultados_juego_trivia` (Trivia/Quiz)
- 🔤 `metricas_juego_wordsearch` + `resultados_juego_wordsearch` (Sopa de letras)
- 🃏 `metricas_juego_flashcards` + `resultados_juego_flashcards` (Tarjetas)

**Ventajas de este patrón**:
✅ **Escalabilidad**: Cada juego tiene sus métricas específicas sin contaminar otras tablas
✅ **Performance**: Queries más rápidas (menos datos por tabla, índices específicos)
✅ **Flexibilidad**: Fácil agregar nuevos juegos sin modificar estructura existente
✅ **Extensibilidad**: Cada juego puede tener métricas únicas según su mecánica
✅ **Mantenibilidad**: Código más limpio y separación de responsabilidades
✅ **Compartición**: Tabla `juegos` se mantiene genérica y compartida por todos

**Tablas compartidas entre todos los juegos**:
- `conceptos` → Contenido académico (palabras, términos)
- `juegos` → Sesiones de juego genéricas
- `archivos_procesados` → PDFs procesados por Gemini
- `usuarios`, `asignaturas`, `temas` → Datos maestros

#### 3. **`metricas_hangman`** ⭐⭐⭐ (NUEVA TABLA ESPECÍFICA)
```sql
CREATE TABLE metricas_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    letra_intentada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    posicion_letra INT,  -- Posición en la palabra (0-indexed)
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id)
);
```
**Uso**: Análisis granular de cada intento en Hangman para aprendizaje adaptativo

#### 4. **`resultados_hangman`** ⭐⭐ (NUEVA TABLA)
```sql
CREATE TABLE resultados_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    adivinado BOOLEAN NOT NULL,  -- TRUE si completó la palabra
    intentos_usados INT NOT NULL,  -- Cuántos errores tuvo
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id),
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id)
);
```
**Uso**: Resultado final de cada palabra jugada

#### 5. **`temas`** (SIN CAMBIOS)
```sql
CREATE TABLE temas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    id_asignatura UUID NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    UNIQUE (nombre, id_asignatura)
);
```
**Uso**: Contexto de los conceptos

#### 6. **`juegos`** (SIN CAMBIOS MAYORES)
```sql
CREATE TABLE juegos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    nombre_juego VARCHAR(50),  -- "Hangman"
    intentos_restantes INT,
    estado_partida VARCHAR(50),  -- 'EN_CURSO', 'COMPLETADO', 'ABANDONADO'
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP,
    puntaje NUMERIC(10, 2),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);
```
**Uso**: Sesión completa del juego

---

## 🛠️ Modificaciones Requeridas

### 1. Backend - IA Service ⭐⭐⭐ (NUEVO - PRIORITARIO)

#### **Endpoint Principal**: `POST /api/ia/procesar-pdf`
```java
@PostMapping("/procesar-pdf")
public ResponseEntity<ResultadoProcesamientoDTO> procesarPDF(
    @RequestParam UUID archivoId,
    @RequestParam UUID temaId,
    @RequestParam(defaultValue = "conceptos,preguntas") String[] tiposExtraccion
) {
    /**
     * FLUJO:
     * 1. Obtiene PDF desde MongoDB por archivoId
     * 2. Extrae texto del PDF (Apache PDFBox o similar)
     * 3. Construye prompt para Gemini API
     * 4. Envía a Gemini con estructura JSON solicitada
     * 5. Valida y limpia respuesta de Gemini
     * 6. Inserta conceptos en PostgreSQL (tabla conceptos)
     * 7. Inserta preguntas en PostgreSQL (tabla preguntas)
     * 8. Actualiza estado en archivos_procesados
     * 9. Retorna resumen del procesamiento
     */
}
```

#### **Prompt Template para Gemini**:
```java
private String construirPromptGemini(String textoPDF, String nombreTema) {
    return """
        Eres un asistente pedagógico experto. Analiza el siguiente contenido académico
        del tema "%s" y extrae información estructurada.
        
        CONTENIDO:
        %s
        
        TAREAS:
        1. Identifica los conceptos técnicos clave (términos de 1-3 palabras)
        2. Para cada concepto, crea un hint/definición corta (máx 100 caracteres)
        3. Clasifica la dificultad: 'basico', 'intermedio', 'avanzado'
        4. Genera 5-10 preguntas de comprensión con sus respuestas
        
        FORMATO DE RESPUESTA (JSON estricto):
        {
          "conceptos": [
            {
              "palabra": "nombre-concepto",
              "hint": "definición corta para juego",
              "dificultad": "basico|intermedio|avanzado"
            }
          ],
          "preguntas": [
            {
              "texto": "¿Pregunta sobre el contenido?",
              "respuesta": "respuesta completa",
              "conceptos_relacionados": ["concepto1", "concepto2"]
            }
          ]
        }
        
        REGLAS:
        - Conceptos deben ser palabras técnicas reales del contenido
        - Hints deben ser claros y concisos (para juego de adivinanza)
        - Solo incluye conceptos relevantes y no triviales
        - Mínimo 10 conceptos, máximo 50
        - JSON válido sin comentarios
        """.formatted(nombreTema, textoPDF);
}
```

#### **DTO Response**:
```java
public class ResultadoProcesamientoDTO {
    private UUID archivoId;
    private String nombreArchivo;
    private int conceptosExtraidos;
    private int preguntasGeneradas;
    private String estado; // "COMPLETADO", "ERROR"
    private String mensajeError;
    private long tiempoProcesamiento; // milisegundos
    private List<ConceptoExtractadoDTO> conceptos;
}

public class ConceptoExtractadoDTO {
    private UUID id;
    private String palabra;
    private String hint;
    private String dificultad;
    private UUID temaId;
}
```

---

### 2. Backend - Content Service

#### **A. Endpoint de Carga de PDF**
```java
@PostMapping("/upload-pdf")
public ResponseEntity<UploadPDFResponseDTO> uploadPDF(
    @RequestParam("file") MultipartFile archivo,
    @RequestParam UUID asignaturaId,
    @RequestParam UUID temaId,
    @RequestHeader("Authorization") String token
) {
    // 1. Validar permisos (solo profesores de esa asignatura)
    // 2. Guardar PDF en MongoDB
    // 3. Registrar en tabla archivos_procesados (estado: PENDIENTE)
    // 4. Llamar asíncronamente a IA Service para procesar
    // 5. Retornar inmediatamente con archivoId
}
```

#### **B. Endpoint para Obtener Conceptos** (modificado)
```java
@GetMapping("/conceptos")
public ResponseEntity<List<ConceptoDTO>> getConceptos(
    @RequestParam UUID asignaturaId,
    @RequestParam(required = false) UUID temaId,
    @RequestParam(required = false) Integer limit
) {
    // Retorna conceptos extraídos por IA de esa asignatura/tema
    // Incluye hint generado por Gemini
}
```

#### **C. Query SQL Modificado**:
```sql
SELECT 
    c.id,
    c.palabra_concepto,
    c.hint,  -- Ahora viene de la tabla, generado por Gemini
    c.dificultad,
    c.extraido_por_ia,
    t.nombre AS tema_nombre,
    t.id AS tema_id,
    a.nombre AS asignatura_nombre,
    a.id AS asignatura_id
FROM conceptos c
JOIN temas t ON c.id_tema = t.id
JOIN asignaturas a ON t.id_asignatura = a.id
WHERE a.id = :asignatura_id
  AND (:tema_id IS NULL OR t.id = :tema_id)
  AND c.extraido_por_ia = TRUE  -- Solo conceptos de IA
ORDER BY c.fecha_creacion DESC
LIMIT :limit;
```

---

### 3. Backend - Scoring Service (Hangman específico)

#### **A. Endpoint: Registrar Intento de Letra**
```java
@PostMapping("/hangman/intento")
public ResponseEntity<IntentoResultDTO> registrarIntento(
    @RequestBody IntentoHangmanDTO intento
) {
    // INSERT INTO metricas_juego_hangman
    // UPDATE juegos SET intentos_restantes
}
```

#### **B. Endpoint: Finalizar Palabra**
```java
@PostMapping("/hangman/finalizar-palabra")
public ResponseEntity<ResultadoPalabraDTO> finalizarPalabra(
    @RequestBody FinalizarPalabraDTO datos
) {
    // INSERT INTO resultados_juego_hangman
    // Calcula puntaje según fórmula
    // UPDATE juegos SET puntaje += puntaje_palabra
}
```

#### **C. Endpoint: Finalizar Juego**
```java
@PutMapping("/hangman/{juegoId}/finalizar")
public ResponseEntity<ResumenJuegoDTO> finalizarJuego(
    @PathVariable UUID juegoId,
    @RequestBody FinalizarJuegoDTO datos
) {
    // UPDATE juegos SET fecha_fin, estado_partida, puntaje
    // UPDATE puntajes SET puntaje += puntaje_juego
    // Recalcula ranking
    // Retorna estadísticas completas
}
```

---

### 4. Frontend - Hangman Game

#### Modificaciones en `script.js`:

##### A. **Configuración de API**
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
const TOKEN = localStorage.getItem('jwt_token');

const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${TOKEN}`
};
```

##### B. **Cargar Conceptos desde API**
```javascript
async function loadConceptos(asignaturaId, temaId = null) {
    try {
        const params = new URLSearchParams({
            asignaturaId: asignaturaId,
            limit: 50
        });
        if (temaId) params.append('temaId', temaId);
        
        const response = await fetch(
            `${API_BASE_URL}/content/conceptos?${params}`,
            { headers }
        );
        
        if (!response.ok) throw new Error('Error al cargar conceptos');
        
        const conceptos = await response.json();
        return conceptos.map(c => ({
            word: c.palabra_concepto,
            hint: c.hint || `Concepto de ${c.tema_nombre}`,
            id: c.id,
            temaId: c.tema_id,
            temaNombre: c.tema_nombre
        }));
    } catch (error) {
        console.error('Error cargando conceptos:', error);
        // Fallback a lista estática
        return wordList;
    }
}
```

##### C. **Iniciar Sesión de Juego**
```javascript
async function iniciarJuego(asignaturaId) {
    const response = await fetch(`${API_BASE_URL}/scoring/juegos`, {
        method: 'POST',
        headers,
        body: JSON.stringify({
            idAsignatura: asignaturaId,
            nombreJuego: 'Hangman',
            intentosRestantes: 6
        })
    });
    
    const juego = await response.json();
    return juego.id;
}
```

##### D. **Registrar Intento**
```javascript
async function registrarIntento(juegoId, conceptoId, letra, esCorrecta, tiempo) {
    await fetch(`${API_BASE_URL}/scoring/hangman/intentos`, {
        method: 'POST',
        headers,
        body: JSON.stringify({
            idJuego: juegoId,
            idConcepto: conceptoId,
            letraIntentada: letra,
            esCorrecta: esCorrecta,
            tiempoMs: tiempo
        })
    });
}
```

##### E. **Finalizar Juego**
```javascript
async function finalizarJuego(juegoId, gano, puntaje) {
    await fetch(`${API_BASE_URL}/scoring/juegos/${juegoId}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify({
            estadoPartida: gano ? 'Completado' : 'Perdido',
            puntaje: puntaje,
            fechaFin: new Date().toISOString()
        })
    });
}
```

---

## 🎮 Sistema de Puntajes

### Fórmula de Puntuación
```javascript
function calcularPuntaje(palabra, intentosRestantes, tiempoSegundos) {
    const puntosBase = palabra.length * 10;
    const bonusIntentos = intentosRestantes * 5;
    const bonusTiempo = Math.max(0, 60 - tiempoSegundos) * 2;
    
    return puntosBase + bonusIntentos + bonusTiempo;
}

// Ejemplo:
// palabra: "variable" (8 letras)
// intentos restantes: 4
// tiempo: 30 segundos
// puntaje = (8 * 10) + (4 * 5) + (60 - 30) * 2
//         = 80 + 20 + 60 = 160 puntos
```

---

## 📋 Plan de Trabajo Paso a Paso (ACTUALIZADO)

### **FASE 1: Preparar Infraestructura de IA** ⭐ (NUEVO - PRIORITARIO)

#### PASO 1.1: Modificar Base de Datos
```sql
-- 1. Agregar campos a tabla conceptos
ALTER TABLE conceptos 
ADD COLUMN hint TEXT,
ADD COLUMN dificultad VARCHAR(20) DEFAULT 'basico',
ADD COLUMN extraido_por_ia BOOLEAN DEFAULT FALSE,
ADD COLUMN fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 2. Crear tabla archivos_procesados
CREATE TABLE archivos_procesados (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_mongodb VARCHAR(500) NOT NULL,
    id_asignatura UUID NOT NULL,
    id_tema UUID,
    id_usuario_carga UUID NOT NULL,
    fecha_carga TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_procesamiento TIMESTAMP,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    conceptos_extraidos INT DEFAULT 0,
    preguntas_generadas INT DEFAULT 0,
    detalle_error TEXT,
    tiempo_procesamiento_ms INT,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
    FOREIGN KEY (id_tema) REFERENCES temas(id),
    FOREIGN KEY (id_usuario_carga) REFERENCES usuarios(id)
);

-- 3. Crear tabla metricas_juego_hangman (patrón escalable)
CREATE TABLE metricas_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    letra_intentada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    posicion_letra INT,
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id)
);

-- 4. Crear tabla resultados_juego_hangman (patrón escalable)
CREATE TABLE resultados_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    adivinado BOOLEAN NOT NULL,
    intentos_usados INT NOT NULL,
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    vidas_restantes INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id),
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id)
);

-- 5. Crear índices para performance
CREATE INDEX idx_conceptos_tema ON conceptos(id_tema);
CREATE INDEX idx_conceptos_ia ON conceptos(extraido_por_ia);
CREATE INDEX idx_archivos_estado ON archivos_procesados(estado);
CREATE INDEX idx_metricas_hangman_usuario ON metricas_juego_hangman(id_usuario, fecha_hora DESC);
CREATE INDEX idx_resultados_hangman_juego ON resultados_juego_hangman(id_juego);

-- Comentarios explicativos
COMMENT ON TABLE metricas_juego_hangman IS 'Patrón: metricas_juego_{nombre} para cada nuevo juego';
COMMENT ON TABLE resultados_juego_hangman IS 'Patrón: resultados_juego_{nombre} para cada nuevo juego';
```

**✅ Verificación**: Ejecutar script SQL en PostgreSQL

---

#### PASO 1.2: Configurar MongoDB para PDFs
```javascript
// Colección en MongoDB para almacenar PDFs
{
  "_id": ObjectId("..."),
  "archivoId": "uuid-postgres-reference",
  "nombreArchivo": "apuntes_algoritmos.pdf",
  "contenidoBase64": "JVBERi0xLjQK...",  // O usar GridFS para archivos grandes
  "metadatos": {
    "tamanoBytes": 245678,
    "tipoMIME": "application/pdf",
    "idAsignatura": "uuid",
    "idTema": "uuid",
    "fechaCarga": ISODate("2025-11-05T...")
  },
  "estado": "ALMACENADO"
}
```

**✅ Verificación**: Configurar conexión MongoDB en Spring Boot

---

### **FASE 2: Implementar IA Service**

#### PASO 2.1: Crear Entidades y DTOs
```java
// 1. Entity: ArchivoProcesamientoEntity
// 2. DTO: ResultadoProcesamientoDTO
// 3. DTO: ConceptoExtractadoDTO
// 4. DTO: PreguntaGeneradaDTO
```

#### PASO 2.2: Implementar Cliente Gemini API
```java
@Service
public class GeminiAPIClient {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.url}")
    private String apiUrl;
    
    public GeminiResponseDTO procesarContenido(String prompt) {
        // 1. Construir request para Gemini
        // 2. Enviar POST a API
        // 3. Parsear respuesta JSON
        // 4. Validar estructura
        // 5. Retornar DTO
    }
}
```

#### PASO 2.3: Implementar Servicio de Procesamiento
```java
@Service
public class ProcesamientoPDFService {
    
    public ResultadoProcesamientoDTO procesarPDF(UUID archivoId) {
        // 1. Obtener PDF de MongoDB
        // 2. Extraer texto con PDFBox
        // 3. Construir prompt para Gemini
        // 4. Llamar a Gemini API
        // 5. Validar respuesta
        // 6. Insertar conceptos en PostgreSQL
        // 7. Insertar preguntas en PostgreSQL
        // 8. Actualizar archivos_procesados
        // 9. Retornar resultado
    }
}
```

#### PASO 2.4: Crear Endpoint REST
```java
@RestController
@RequestMapping("/api/ia")
public class IAController {
    
    @PostMapping("/procesar-pdf")
    public ResponseEntity<ResultadoProcesamientoDTO> procesarPDF(...) {
        // Implementación según diseño anterior
    }
    
    @GetMapping("/conceptos-adaptativos")
    public ResponseEntity<List<ConceptoDTO>> getConceptosAdaptativos(...) {
        // Análisis de métricas + priorización por temas débiles
    }
}
```

**✅ Verificación**: Probar con Postman usando PDF de prueba

---

### **FASE 3: Implementar Content Service para PDFs**

#### PASO 3.1: Endpoint de Carga
```java
@PostMapping("/upload-pdf")
public ResponseEntity<UploadPDFResponseDTO> uploadPDF(
    @RequestParam("file") MultipartFile archivo,
    @RequestParam UUID asignaturaId,
    @RequestParam UUID temaId
) {
    // 1. Validar formato PDF
    // 2. Validar permisos del profesor
    // 3. Guardar en MongoDB
    // 4. Registrar en archivos_procesados
    // 5. Llamar IA Service (asíncrono)
    // 6. Retornar archivoId
}
```

#### PASO 3.2: Endpoint de Consulta de Conceptos
```java
@GetMapping("/conceptos")
public ResponseEntity<List<ConceptoDTO>> getConceptos(...) {
    // Query SQL que incluye hint generado por IA
}
```

**✅ Verificación**: Flujo completo Profesor → Upload PDF → IA procesa → Conceptos en DB

---

### **FASE 4: Implementar Scoring Service para Hangman**

#### PASO 4.1: Endpoints CRUD para Hangman
```java
// POST /api/scoring/juegos/hangman/iniciar
// POST /api/scoring/hangman/intento
// POST /api/scoring/hangman/finalizar-palabra
// PUT /api/scoring/hangman/{juegoId}/finalizar
```

#### PASO 4.2: Fórmula de Puntajes
```java
public class PuntajeHangmanService {
    public int calcularPuntaje(
        String palabra, 
        int intentosRestantes, 
        long tiempoMs
    ) {
        int puntosBase = palabra.length() * 10;
        int bonusIntentos = intentosRestantes * 5;
        int bonusTiempo = (int) Math.max(0, (60000 - tiempoMs) / 1000) * 2;
        return puntosBase + bonusIntentos + bonusTiempo;
    }
}
```

**✅ Verificación**: Test unitarios de cálculo de puntajes

---

### **FASE 5: Modificar Frontend Hangman**

#### PASO 5.1: Configuración API (Kotlin - Android)
```kotlin
object HangmanAPI {
    private const val BASE_URL = "https://api.brainboost.duoc.cl"
    
    suspend fun obtenerConceptos(
        asignaturaId: String,
        token: String
    ): List<Concepto> {
        // Retrofit call a /api/ia/conceptos-adaptativos
    }
    
    suspend fun registrarIntento(
        juegoId: String,
        intentoData: IntentoHangman,
        token: String
    ): IntentoResult {
        // POST a /api/scoring/hangman/intento
    }
}
```

#### PASO 5.2: ViewModel para Hangman
```kotlin
class HangmanViewModel : ViewModel() {
    
    private val _conceptos = MutableLiveData<List<Concepto>>()
    val conceptos: LiveData<List<Concepto>> = _conceptos
    
    suspend fun cargarConceptos(asignaturaId: String) {
        val token = SessionManager.getToken()
        _conceptos.value = HangmanAPI.obtenerConceptos(asignaturaId, token)
    }
    
    suspend fun registrarIntento(letra: Char, esCorrecta: Boolean) {
        // Lógica de registro
    }
}
```

#### PASO 5.3: UI en Jetpack Compose o XML
```kotlin
@Composable
fun HangmanScreen(viewModel: HangmanViewModel) {
    // Implementar UI similar al HTML actual
    // Consumir conceptos desde API
    // Registrar métricas en tiempo real
}
```

**✅ Verificación**: Flujo completo desde app móvil

---

### **FASE 6: Testing e Integración**

#### PASO 6.1: Test de Procesamiento IA
- [ ] Cargar PDF de prueba (5 páginas)
- [ ] Verificar extracción de conceptos (mín 10)
- [ ] Validar hints generados (coherentes)
- [ ] Verificar inserción en PostgreSQL

#### PASO 6.2: Test de Juego Completo
- [ ] Iniciar sesión de juego
- [ ] Obtener conceptos adaptativos
- [ ] Jugar 5 palabras completas
- [ ] Verificar métricas en DB
- [ ] Validar puntaje acumulado

#### PASO 6.3: Test de Aprendizaje Adaptativo
- [ ] Usuario responde mal 5 veces concepto X
- [ ] Siguiente juego debe priorizar tema de X
- [ ] Verificar análisis de IA Service

**✅ Verificación**: UAT con usuarios reales

---

## 🎯 Cronograma Sugerido

| Fase | Duración | Responsable | Estado |
|------|----------|-------------|--------|
| FASE 1: Infraestructura BD | 2 días | Backend Team | ⏳ Pendiente |
| FASE 2: IA Service | 5 días | Backend + IA | ⏳ Pendiente |
| FASE 3: Content Service | 3 días | Backend Team | ⏳ Pendiente |
| FASE 4: Scoring Service | 3 días | Backend Team | ⏳ Pendiente |
| FASE 5: Frontend Hangman | 4 días | Mobile Team | ⏳ Pendiente |
| FASE 6: Testing | 3 días | QA + All Teams | ⏳ Pendiente |
| **TOTAL** | **20 días** (~4 semanas) | | |

---

## 🚀 DECISIÓN INMEDIATA REQUERIDA

Para comenzar HOY, necesito que elijas:

### Opción A: Empezar por la Base de Datos ✅ RECOMENDADO
- Ejecutamos los scripts SQL
- Modificamos tabla `conceptos`
- Creamos tablas nuevas
- Preparamos MongoDB
- **Ventaja**: Base sólida para todo lo demás

### Opción B: Empezar por IA Service
- Implementamos endpoint de procesamiento
- Configuramos Gemini API
- Probamos con PDF mock
- **Ventaja**: Validamos pronto que Gemini funciona

### Opción C: Hacer POC (Proof of Concept) Rápido
- Script Python simple que:
  1. Lee un PDF
  2. Llama a Gemini
  3. Imprime conceptos extraídos
- **Ventaja**: Validación técnica en 1 hora

**¿Qué opción prefieres para comenzar AHORA? 🚀**

---

## 🚨 Consideraciones Importantes (ACTUALIZADAS)

### 1. **Google Gemini API - Configuración** ⭐⭐⭐

#### **API Key y Configuración**
```properties
# application.properties
gemini.api.key=${GEMINI_API_KEY}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
gemini.model=gemini-pro
gemini.max.tokens=2048
gemini.temperature=0.7
```

#### **Límites de la API Gratuita**
- **Requests por minuto**: 60 RPM
- **Tokens por request**: 32,000 input / 2,048 output
- **Requests por día**: Ilimitados en tier gratuito

**Mitigación**:
```java
@Service
public class GeminiRateLimiter {
    private final RateLimiter rateLimiter = RateLimiter.create(1.0); // 1 req/segundo
    
    public GeminiResponseDTO callGeminiWithRetry(String prompt) {
        rateLimiter.acquire();
        try {
            return geminiClient.process(prompt);
        } catch (QuotaExceededException e) {
            // Esperar y reintentar
            Thread.sleep(60000);
            return callGeminiWithRetry(prompt);
        }
    }
}
```

---

### 2. **Procesamiento de PDFs - Apache PDFBox**

#### **Dependencia Maven**
```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.0</version>
</dependency>
```

#### **Extractor de Texto**
```java
@Service
public class PDFTextExtractor {
    
    public String extraerTexto(InputStream pdfStream) throws IOException {
        try (PDDocument document = PDDocument.load(pdfStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            
            String texto = stripper.getText(document);
            
            // Limpieza básica
            texto = texto.replaceAll("\\s+", " ").trim();
            
            // Validar tamaño (Gemini tiene límite de tokens)
            if (texto.length() > 30000) {
                // Truncar o dividir en chunks
                texto = texto.substring(0, 30000);
            }
            
            return texto;
        }
    }
}
```

---

### 3. **Validación de Respuesta de Gemini**

```java
@Service
public class GeminiResponseValidator {
    
    public void validar(GeminiResponseDTO response) throws ValidationException {
        if (response.getConceptos() == null || response.getConceptos().isEmpty()) {
            throw new ValidationException("No se extrajeron conceptos");
        }
        
        if (response.getConceptos().size() < 5) {
            throw new ValidationException("Muy pocos conceptos extraídos (mín 5)");
        }
        
        for (ConceptoDTO concepto : response.getConceptos()) {
            // Validar que palabra_concepto no esté vacía
            if (StringUtils.isBlank(concepto.getPalabra())) {
                throw new ValidationException("Concepto sin palabra");
            }
            
            // Validar que hint exista
            if (StringUtils.isBlank(concepto.getHint())) {
                throw new ValidationException("Concepto sin hint");
            }
            
            // Validar longitud de palabra (para Hangman)
            if (concepto.getPalabra().length() < 3) {
                continue; // Saltar palabras muy cortas
            }
            
            if (concepto.getPalabra().length() > 20) {
                continue; // Saltar palabras muy largas
            }
            
            // Validar que solo tenga letras (no números ni símbolos)
            if (!concepto.getPalabra().matches("[a-zA-ZáéíóúñÑ]+")) {
                continue;
            }
        }
    }
}
```

---

### 4. **Manejo de Errores en Procesamiento**

```java
@Service
public class ProcesamientoPDFService {
    
    @Async
    public CompletableFuture<ResultadoProcesamientoDTO> procesarPDFAsync(UUID archivoId) {
        ArchivoProcesamientoEntity archivo = repository.findById(archivoId)
            .orElseThrow(() -> new NotFoundException("Archivo no encontrado"));
        
        try {
            // Actualizar estado a PROCESANDO
            archivo.setEstado("PROCESANDO");
            repository.save(archivo);
            
            // 1. Extraer texto del PDF
            String textoPDF = pdfExtractor.extraerTexto(archivo);
            
            // 2. Llamar a Gemini
            GeminiResponseDTO response = geminiService.procesarContenido(
                construirPrompt(textoPDF, archivo.getTema())
            );
            
            // 3. Validar respuesta
            validator.validar(response);
            
            // 4. Insertar en base de datos
            int conceptosInsertados = insertarConceptos(response.getConceptos(), archivo);
            int preguntasInsertadas = insertarPreguntas(response.getPreguntas(), archivo);
            
            // 5. Actualizar estado a COMPLETADO
            archivo.setEstado("COMPLETADO");
            archivo.setConceptosExtraidos(conceptosInsertados);
            archivo.setPreguntasGeneradas(preguntasInsertadas);
            archivo.setFechaProcesamiento(LocalDateTime.now());
            
            repository.save(archivo);
            
            return CompletableFuture.completedFuture(
                new ResultadoProcesamientoDTO(archivo)
            );
            
        } catch (Exception e) {
            // Actualizar estado a ERROR
            archivo.setEstado("ERROR");
            archivo.setDetalleError(e.getMessage());
            repository.save(archivo);
            
            throw new ProcesamientoException("Error procesando PDF", e);
        }
    }
}
```

---

### 5. **Seguridad - Validación de Archivos**

```java
@Service
public class PDFValidator {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
        "application/pdf"
    );
    
    public void validarArchivo(MultipartFile file) throws ValidationException {
        // Validar tamaño
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("Archivo muy grande (máx 10 MB)");
        }
        
        // Validar tipo MIME
        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            throw new ValidationException("Solo se permiten archivos PDF");
        }
        
        // Validar extensión
        String filename = file.getOriginalFilename();
        if (!filename.toLowerCase().endsWith(".pdf")) {
            throw new ValidationException("Extensión inválida");
        }
        
        // Validar que sea un PDF real (magic bytes)
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            is.read(header);
            
            // PDF magic bytes: %PDF
            if (header[0] != 0x25 || header[1] != 0x50 || 
                header[2] != 0x44 || header[3] != 0x46) {
                throw new ValidationException("Archivo no es un PDF válido");
            }
        } catch (IOException e) {
            throw new ValidationException("Error validando archivo");
        }
    }
}
```

---

### 6. **Optimización - Caché de Conceptos**

```java
@Service
public class ConceptoCacheService {
    
    @Cacheable(value = "conceptos", key = "#asignaturaId + '_' + #temaId")
    public List<ConceptoDTO> getConceptosConCache(UUID asignaturaId, UUID temaId) {
        return conceptoRepository.findByAsignaturaAndTema(asignaturaId, temaId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "conceptos", allEntries = true)
    public void limpiarCacheConceptos() {
        // Se llama después de procesar un nuevo PDF
    }
}
```

---

### 7. **MongoDB - GridFS para Archivos Grandes**

```java
@Service
public class MongoDBFileStorage {
    
    @Autowired
    private GridFsTemplate gridFsTemplate;
    
    @Autowired
    private GridFsOperations gridFsOperations;
    
    public String guardarPDF(MultipartFile file, UUID archivoId) throws IOException {
        DBObject metadata = new BasicDBObject();
        metadata.put("archivoId", archivoId.toString());
        metadata.put("tipo", "PDF_CONTENIDO");
        metadata.put("fechaCarga", new Date());
        
        ObjectId fileId = gridFsTemplate.store(
            file.getInputStream(),
            file.getOriginalFilename(),
            file.getContentType(),
            metadata
        );
        
        return fileId.toString();
    }
    
    public InputStream obtenerPDF(UUID archivoId) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(
            new Query(Criteria.where("metadata.archivoId").is(archivoId.toString()))
        );
        
        if (file == null) {
            throw new NotFoundException("PDF no encontrado");
        }
        
        return gridFsOperations.getResource(file).getInputStream();
    }
}
```

---

## 📋 GUÍA DE IMPLEMENTACIÓN: CHECKLIST DETALLADO

### 🔷 **FASE 1: PREPARACIÓN DE BASE DE DATOS** (Día 1-2)

#### ✅ **Tarea 1.1: Modificar Tabla `conceptos`**
```sql
-- Script: 01_alter_conceptos.sql

ALTER TABLE conceptos 
ADD COLUMN hint TEXT,
ADD COLUMN dificultad VARCHAR(20),
ADD COLUMN extraido_por_ia BOOLEAN DEFAULT TRUE,
ADD COLUMN fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Índices para mejor performance
CREATE INDEX idx_conceptos_tema ON conceptos(id_tema);
CREATE INDEX idx_conceptos_dificultad ON conceptos(dificultad);
CREATE INDEX idx_conceptos_ia ON conceptos(extraido_por_ia);

COMMENT ON COLUMN conceptos.hint IS 'Pista corta generada por Gemini para juego Hangman';
COMMENT ON COLUMN conceptos.dificultad IS 'Nivel: basico, intermedio, avanzado';
COMMENT ON COLUMN conceptos.extraido_por_ia IS 'TRUE si fue generado por IA, FALSE si manual';
```

**Comando de ejecución**:
```bash
# Windows PowerShell
psql -U postgres -d brain_boost -f 01_alter_conceptos.sql

# Verificación
psql -U postgres -d brain_boost -c "\d+ conceptos"
```

**Checklist**:
- [ ] Script SQL creado
- [ ] Ejecutado en base de datos
- [ ] Columnas verificadas con `\d+ conceptos`
- [ ] Índices creados correctamente
- [ ] Respaldo de BD antes de modificar

---

#### ✅ **Tarea 1.2: Crear Tabla `archivos_procesados`**
```sql
-- Script: 02_create_archivos_procesados.sql

CREATE TABLE archivos_procesados (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_mongodb VARCHAR(500) NOT NULL,
    id_asignatura UUID NOT NULL,
    id_tema UUID,
    id_usuario_carga UUID NOT NULL,
    fecha_carga TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_procesamiento TIMESTAMP,
    estado VARCHAR(50) NOT NULL CHECK (estado IN ('PENDIENTE', 'PROCESANDO', 'COMPLETADO', 'ERROR')),
    conceptos_extraidos INT DEFAULT 0,
    preguntas_generadas INT DEFAULT 0,
    detalle_error TEXT,
    tiempo_procesamiento_ms INT,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_tema) REFERENCES temas(id) ON DELETE SET NULL,
    FOREIGN KEY (id_usuario_carga) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX idx_archivos_asignatura ON archivos_procesados(id_asignatura);
CREATE INDEX idx_archivos_estado ON archivos_procesados(estado);
CREATE INDEX idx_archivos_fecha ON archivos_procesados(fecha_carga DESC);

COMMENT ON TABLE archivos_procesados IS 'Auditoría de PDFs procesados por Gemini IA';
```

**Checklist**:
- [ ] Script SQL creado
- [ ] Tabla creada exitosamente
- [ ] Foreign keys validadas
- [ ] Check constraint funciona
- [ ] Índices creados

---

#### ✅ **Tarea 1.3: Crear Tabla `metricas_juego_hangman`**
```sql
-- Script: 03_create_metricas_juego_hangman.sql

CREATE TABLE metricas_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    letra_intentada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    posicion_letra INT,
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE
);

CREATE INDEX idx_metricas_hangman_juego ON metricas_juego_hangman(id_juego);
CREATE INDEX idx_metricas_hangman_usuario ON metricas_juego_hangman(id_usuario);
CREATE INDEX idx_metricas_hangman_concepto ON metricas_juego_hangman(id_concepto);
CREATE INDEX idx_metricas_hangman_fecha ON metricas_juego_hangman(fecha_hora DESC);

COMMENT ON TABLE metricas_juego_hangman IS 'Registro granular de cada intento de letra en Hangman (patrón escalable para otros juegos)';
```

**Checklist**:
- [ ] Script SQL creado
- [ ] Tabla creada exitosamente
- [ ] Índices creados
- [ ] Relaciones verificadas
- [ ] Patrón aplicable a otros juegos documentado

---

#### ✅ **Tarea 1.4: Crear Tabla `resultados_juego_hangman`**
```sql
-- Script: 04_create_resultados_juego_hangman.sql

CREATE TABLE resultados_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    adivinado BOOLEAN NOT NULL,
    intentos_usados INT NOT NULL,
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    vidas_restantes INT,  -- Vidas que quedaban al terminar esta palabra
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE,
    UNIQUE (id_juego, id_concepto)
);

CREATE INDEX idx_resultados_hangman_juego ON resultados_juego_hangman(id_juego);
CREATE INDEX idx_resultados_hangman_adivinado ON resultados_juego_hangman(adivinado);

COMMENT ON TABLE resultados_juego_hangman IS 'Resultado final de cada palabra en Hangman (patrón escalable para otros juegos)';
```

**Checklist**:
- [ ] Script SQL creado
- [ ] Tabla creada exitosamente
- [ ] UNIQUE constraint funciona
- [ ] Índices creados
- [ ] Campo `vidas_restantes` agregado

---

#### ✅ **Tarea 1.5: (OPCIONAL) Plantilla para Futuros Juegos**

**Archivo**: `05_PLANTILLA_nuevo_juego.sql`

```sql
-- PLANTILLA PARA CREAR NUEVOS JUEGOS
-- Ejemplo: Criss-Cross Puzzle

-- PASO 1: Crear tabla de métricas granulares
CREATE TABLE metricas_juego_{NOMBRE_JUEGO} (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    -- Columnas específicas del juego aquí (ej: posicion_fila, letra_colocada, etc.)
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE
);

-- PASO 2: Crear índices
CREATE INDEX idx_metricas_{NOMBRE_JUEGO}_juego ON metricas_juego_{NOMBRE_JUEGO}(id_juego);
CREATE INDEX idx_metricas_{NOMBRE_JUEGO}_usuario ON metricas_juego_{NOMBRE_JUEGO}(id_usuario);
CREATE INDEX idx_metricas_{NOMBRE_JUEGO}_concepto ON metricas_juego_{NOMBRE_JUEGO}(id_concepto);

-- PASO 3: Crear tabla de resultados finales
CREATE TABLE resultados_juego_{NOMBRE_JUEGO} (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    completado BOOLEAN NOT NULL,
    -- Columnas específicas del resultado (ej: intentos_usados, pistas_usadas, etc.)
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE,
    UNIQUE (id_juego, id_concepto)
);

-- PASO 4: Crear índices de resultados
CREATE INDEX idx_resultados_{NOMBRE_JUEGO}_juego ON resultados_juego_{NOMBRE_JUEGO}(id_juego);

-- PASO 5: Agregar comentarios
COMMENT ON TABLE metricas_juego_{NOMBRE_JUEGO} IS 'Métricas específicas del juego {NOMBRE_JUEGO}';
COMMENT ON TABLE resultados_juego_{NOMBRE_JUEGO} IS 'Resultados finales del juego {NOMBRE_JUEGO}';
```

**Ejemplo concreto: Criss-Cross Puzzle**
```sql
-- 05_create_tables_crisscross.sql

CREATE TABLE metricas_juego_crisscross (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    posicion_fila INT NOT NULL,
    posicion_columna INT NOT NULL,
    direccion VARCHAR(20),  -- 'HORIZONTAL', 'VERTICAL'
    letra_colocada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    tiempo_respuesta_ms INT,
    pista_usada BOOLEAN DEFAULT FALSE,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE
);

CREATE TABLE resultados_juego_crisscross (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    completado BOOLEAN NOT NULL,
    casillas_correctas INT,
    casillas_totales INT,
    pistas_usadas INT DEFAULT 0,
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE,
    UNIQUE (id_juego, id_concepto)
);

CREATE INDEX idx_metricas_crisscross_juego ON metricas_juego_crisscross(id_juego);
CREATE INDEX idx_resultados_crisscross_juego ON resultados_juego_crisscross(id_juego);

COMMENT ON TABLE metricas_juego_crisscross IS 'Métricas específicas de Criss-Cross Puzzle';
COMMENT ON TABLE resultados_juego_crisscross IS 'Resultados finales de Criss-Cross Puzzle';
```

**Checklist**:
- [ ] Plantilla documentada
- [ ] Patrón de nomenclatura claro
- [ ] Ejemplo concreto (Criss-Cross) documentado
- [ ] Estructura replicable para otros juegos

---

#### ✅ **Tarea 1.6: Configurar MongoDB (GridFS)**
```bash
# Conectar a MongoDB
mongosh

# Crear base de datos
use brain_boost_files

# Crear colección con validación
db.createCollection("archivos", {
   validator: {
      $jsonSchema: {
         bsonType: "object",
         required: ["archivoId", "tipo", "fechaCarga"],
         properties: {
            archivoId: {
               bsonType: "string",
               description: "UUID del archivo en PostgreSQL"
            },
            tipo: {
               enum: ["PDF_CONTENIDO"],
               description: "Tipo de archivo"
            },
            fechaCarga: {
               bsonType: "date"
            }
         }
      }
   }
})

# Verificar
db.archivos.find()
```

**Checklist**:
- [ ] MongoDB instalado y corriendo
- [ ] Base de datos creada
- [ ] Validación de schema configurada
- [ ] Conexión desde Spring Boot probada

---

### 🔷 **FASE 2: IMPLEMENTAR IA SERVICE** (Día 3-7)

#### ✅ **Tarea 2.1: Configurar Gemini API**

**Paso 1**: Obtener API Key
1. Ir a https://makersuite.google.com/app/apikey
2. Crear nuevo proyecto
3. Generar API Key
4. Guardar en archivo `.env` o variables de entorno

**Paso 2**: Configurar `application.properties`
```properties
# application-ia-service.properties

# Gemini API
gemini.api.key=${GEMINI_API_KEY}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
gemini.model=gemini-pro
gemini.max.tokens=2048
gemini.temperature=0.7
gemini.timeout.seconds=30

# Rate Limiting
gemini.rate.limit.requests.per.minute=60
gemini.rate.limit.retry.max.attempts=3
gemini.rate.limit.retry.delay.seconds=5

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/brain_boost_files
spring.data.mongodb.database=brain_boost_files

# PostgreSQL (conexión a BD principal)
spring.datasource.url=jdbc:postgresql://localhost:5432/brain_boost
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
```

**Checklist**:
- [ ] API Key obtenida
- [ ] Variables de entorno configuradas
- [ ] `application.properties` actualizado
- [ ] API Key funciona (test con curl)

---

#### ✅ **Tarea 2.2: Crear Modelo de Dominio**

**Archivo**: `src/main/kotlin/org/duocuc/iaservice/model/ConceptoExtractado.kt`
```kotlin
package org.duocuc.iaservice.model

import java.util.UUID

data class ConceptoExtractado(
    val palabra: String,
    val hint: String,
    val dificultad: Dificultad
)

enum class Dificultad {
    BASICO,
    INTERMEDIO,
    AVANZADO
}

data class PreguntaGenerada(
    val texto: String,
    val respuesta: String,
    val conceptosRelacionados: List<String>
)

data class RespuestaGemini(
    val conceptos: List<ConceptoExtractado>,
    val preguntas: List<PreguntaGenerada>
)

data class ResultadoProcesamiento(
    val archivoId: UUID,
    val nombreArchivo: String,
    val conceptosExtraidos: Int,
    val preguntasGeneradas: Int,
    val estado: EstadoProcesamiento,
    val mensajeError: String? = null,
    val tiempoProcesamiento: Long
)

enum class EstadoProcesamiento {
    PENDIENTE,
    PROCESANDO,
    COMPLETADO,
    ERROR
}
```

**Checklist**:
- [ ] Archivo creado
- [ ] Data classes compilando
- [ ] Enums definidos correctamente

---

#### ✅ **Tarea 2.3: Implementar Cliente Gemini**

**Archivo**: `src/main/kotlin/org/duocuc/iaservice/client/GeminiAPIClient.kt`
```kotlin
package org.duocuc.iaservice.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.util.concurrent.RateLimiter
import org.duocuc.iaservice.model.RespuestaGemini
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException

@Component
class GeminiAPIClient(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    @Value("\${gemini.api.key}") private val apiKey: String,
    @Value("\${gemini.api.url}") private val apiUrl: String,
    @Value("\${gemini.rate.limit.requests.per.minute}") private val rateLimit: Int
) {
    private val rateLimiter = RateLimiter.create(rateLimit / 60.0) // req/segundo
    
    fun procesarTexto(textoPDF: String, nombreTema: String): RespuestaGemini {
        // Rate limiting
        rateLimiter.acquire()
        
        val prompt = construirPrompt(textoPDF, nombreTema)
        val requestBody = mapOf(
            "contents" to listOf(
                mapOf("parts" to listOf(mapOf("text" to prompt)))
            ),
            "generationConfig" to mapOf(
                "temperature" to 0.7,
                "maxOutputTokens" to 2048
            )
        )
        
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("x-goog-api-key", apiKey)
        
        val entity = HttpEntity(requestBody, headers)
        
        try {
            val response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String::class.java
            )
            
            return parsearRespuesta(response.body!!)
            
        } catch (e: HttpClientErrorException) {
            throw GeminiAPIException("Error cliente: ${e.statusCode} - ${e.responseBodyAsString}")
        } catch (e: HttpServerErrorException) {
            throw GeminiAPIException("Error servidor: ${e.statusCode}")
        }
    }
    
    private fun construirPrompt(texto: String, tema: String): String {
        return """
Eres un asistente pedagógico experto. Analiza el siguiente contenido académico
del tema "$tema" y extrae información estructurada.

CONTENIDO:
${texto.take(30000)} 

TAREAS:
1. Identifica los conceptos técnicos clave (términos de 1-3 palabras)
2. Para cada concepto, crea un hint/definición corta (máx 100 caracteres)
3. Clasifica la dificultad: 'BASICO', 'INTERMEDIO', 'AVANZADO'
4. Genera 5-10 preguntas de comprensión con sus respuestas

FORMATO DE RESPUESTA (JSON estricto):
{
  "conceptos": [
    {
      "palabra": "NOMBRE-CONCEPTO",
      "hint": "definición corta para juego",
      "dificultad": "BASICO"
    }
  ],
  "preguntas": [
    {
      "texto": "¿Pregunta sobre el contenido?",
      "respuesta": "respuesta completa",
      "conceptosRelacionados": ["concepto1", "concepto2"]
    }
  ]
}

REGLAS:
- Conceptos en MAYÚSCULAS sin espacios (ej: VARIABLE, RECURSIVIDAD)
- Hints claros y concisos (para juego de adivinanza)
- Mínimo 10 conceptos, máximo 50
- JSON válido sin comentarios ni texto adicional
        """.trimIndent()
    }
    
    private fun parsearRespuesta(jsonResponse: String): RespuestaGemini {
        val jsonNode = objectMapper.readTree(jsonResponse)
        val textContent = jsonNode
            .path("candidates")
            .path(0)
            .path("content")
            .path("parts")
            .path(0)
            .path("text")
            .asText()
        
        // Extraer JSON del texto (puede venir con markdown)
        val jsonText = textContent
            .removePrefix("```json")
            .removeSuffix("```")
            .trim()
        
        return objectMapper.readValue(jsonText, RespuestaGemini::class.java)
    }
}

class GeminiAPIException(message: String) : RuntimeException(message)
```

**Checklist**:
- [ ] Cliente creado
- [ ] Rate limiter configurado
- [ ] Parseo de respuesta implementado
- [ ] Manejo de errores
- [ ] Test unitario creado

---

#### ✅ **Tarea 2.4: Implementar Servicio de Procesamiento**

**Archivo**: `src/main/kotlin/org/duocuc/iaservice/service/ProcesamientoPDFService.kt`
```kotlin
package org.duocuc.iaservice.service

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.duocuc.iaservice.client.GeminiAPIClient
import org.duocuc.iaservice.model.*
import org.duocuc.iaservice.repository.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.util.UUID

@Service
class ProcesamientoPDFService(
    private val geminiClient: GeminiAPIClient,
    private val gridFsTemplate: GridFsTemplate,
    private val conceptoRepository: ConceptoRepository,
    private val preguntaRepository: PreguntaRepository,
    private val archivoProcesadoRepository: ArchivoProcesadoRepository
) {
    
    @Transactional
    fun procesarPDF(archivoId: UUID, temaId: UUID): ResultadoProcesamiento {
        val tiempoInicio = System.currentTimeMillis()
        
        try {
            // 1. Actualizar estado a PROCESANDO
            actualizarEstado(archivoId, EstadoProcesamiento.PROCESANDO)
            
            // 2. Obtener PDF desde MongoDB
            val pdfStream = obtenerPDFDesdeMongoDB(archivoId)
            
            // 3. Extraer texto
            val textoPDF = extraerTextoPDF(pdfStream)
            
            // 4. Llamar a Gemini
            val nombreTema = obtenerNombreTema(temaId)
            val respuestaGemini = geminiClient.procesarTexto(textoPDF, nombreTema)
            
            // 5. Validar respuesta
            validarRespuestaGemini(respuestaGemini)
            
            // 6. Insertar conceptos en PostgreSQL
            val conceptosInsertados = insertarConceptos(respuestaGemini.conceptos, temaId)
            
            // 7. Insertar preguntas
            val preguntasInsertadas = insertarPreguntas(respuestaGemini.preguntas, temaId)
            
            // 8. Actualizar estado a COMPLETADO
            val tiempoFin = System.currentTimeMillis()
            actualizarResultado(
                archivoId,
                conceptosInsertados.size,
                preguntasInsertadas.size,
                tiempoFin - tiempoInicio
            )
            
            return ResultadoProcesamiento(
                archivoId = archivoId,
                nombreArchivo = obtenerNombreArchivo(archivoId),
                conceptosExtraidos = conceptosInsertados.size,
                preguntasGeneradas = preguntasInsertadas.size,
                estado = EstadoProcesamiento.COMPLETADO,
                tiempoProcesamiento = tiempoFin - tiempoInicio
            )
            
        } catch (e: Exception) {
            actualizarEstadoError(archivoId, e.message ?: "Error desconocido")
            throw ProcesamientoException("Error procesando PDF: ${e.message}", e)
        }
    }
    
    private fun extraerTextoPDF(pdfStream: InputStream): String {
        PDDocument.load(pdfStream).use { document ->
            val stripper = PDFTextStripper()
            return stripper.getText(document)
        }
    }
    
    private fun obtenerPDFDesdeMongoDB(archivoId: UUID): InputStream {
        val query = Query(Criteria.where("metadata.archivoId").is(archivoId.toString()))
        val file = gridFsTemplate.findOne(query)
            ?: throw NotFoundException("PDF no encontrado en MongoDB")
        
        return gridFsTemplate.getResource(file).inputStream
    }
    
    // ... resto de métodos privados
}

class ProcesamientoException(message: String, cause: Throwable?) : RuntimeException(message, cause)
class NotFoundException(message: String) : RuntimeException(message)
```

**Checklist**:
- [ ] Servicio creado
- [ ] Extracción de PDF funciona
- [ ] Integración con Gemini
- [ ] Transaccionalidad configurada
- [ ] Manejo de errores robusto

---

#### ✅ **Tarea 2.5: Crear Controlador REST**

**Archivo**: `src/main/kotlin/org/duocuc/iaservice/controller/IAController.kt`
```kotlin
package org.duocuc.iaservice.controller

import org.duocuc.iaservice.model.ResultadoProcesamiento
import org.duocuc.iaservice.service.ProcesamientoPDFService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/ia")
class IAController(
    private val procesamientoPDFService: ProcesamientoPDFService
) {
    
    @PostMapping("/procesar-pdf")
    fun procesarPDF(
        @RequestParam archivoId: UUID,
        @RequestParam temaId: UUID
    ): ResponseEntity<ResultadoProcesamiento> {
        val resultado = procesamientoPDFService.procesarPDF(archivoId, temaId)
        return ResponseEntity.ok(resultado)
    }
    
    @GetMapping("/estado-procesamiento/{archivoId}")
    fun getEstadoProcesamiento(@PathVariable archivoId: UUID): ResponseEntity<Map<String, Any>> {
        // Consultar tabla archivos_procesados
        // Retornar estado actual
        return ResponseEntity.ok(mapOf(
            "archivoId" to archivoId,
            "estado" to "COMPLETADO",
            "conceptos" to 15,
            "preguntas" to 10
        ))
    }
}
```

**Checklist**:
- [ ] Controlador creado
- [ ] Endpoint `/procesar-pdf` funciona
- [ ] Validaciones de parámetros
- [ ] Swagger documentado

---

### 🔷 **FASE 3: IMPLEMENTAR CONTENT SERVICE** (Día 8-10)

#### ✅ **Tarea 3.1: Endpoint Upload PDF**

**Archivo**: `src/main/kotlin/org/duocuc/contentservice/controller/ContentController.kt`
```kotlin
@PostMapping("/upload-pdf")
fun uploadPDF(
    @RequestParam("file") archivo: MultipartFile,
    @RequestParam asignaturaId: UUID,
    @RequestParam temaId: UUID,
    @AuthenticationPrincipal usuario: UserDetails
): ResponseEntity<UploadPDFResponseDTO> {
    
    // 1. Validar permisos (solo profesores)
    if (!usuario.authorities.any { it.authority == "ROLE_PROFESOR" }) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }
    
    // 2. Validar archivo PDF
    validarPDF(archivo)
    
    // 3. Guardar en MongoDB
    val mongoId = mongoDBFileStorage.guardarPDF(archivo, UUID.randomUUID())
    
    // 4. Registrar en PostgreSQL (archivos_procesados)
    val archivoRegistro = registrarArchivo(archivo, asignaturaId, temaId, usuario.username, mongoId)
    
    // 5. Llamar asíncronamente a IA Service
    procesamientoAsyncService.procesarPDFAsync(archivoRegistro.id, temaId)
    
    return ResponseEntity.ok(UploadPDFResponseDTO(
        archivoId = archivoRegistro.id,
        mensaje = "PDF cargado, procesamiento iniciado",
        estado = "PENDIENTE"
    ))
}
```

**Checklist**:
- [ ] Endpoint creado
- [ ] Validaciones implementadas
- [ ] MongoDB integrado
- [ ] Llamada asíncrona a IA Service
- [ ] Tests de integración

---

#### ✅ **Tarea 3.2: Endpoint Obtener Conceptos**

```kotlin
@GetMapping("/conceptos")
fun getConceptos(
    @RequestParam asignaturaId: UUID,
    @RequestParam(required = false) temaId: UUID?,
    @RequestParam(defaultValue = "20") limit: Int
): ResponseEntity<List<ConceptoDTO>> {
    val conceptos = conceptoRepository.findByAsignaturaAndTema(asignaturaId, temaId, limit)
    return ResponseEntity.ok(conceptos.map { it.toDTO() })
}
```

**Checklist**:
- [ ] Endpoint implementado
- [ ] Query optimizada con índices
- [ ] Paginación configurada
- [ ] DTOs mapeados

---

### 🔷 **FASE 4: IMPLEMENTAR SCORING SERVICE** (Día 11-13)

#### ✅ **Tarea 4.1: Endpoints Hangman**

```kotlin
@RestController
@RequestMapping("/api/scoring/hangman")
class HangmanScoringController(
    private val hangmanService: HangmanService
) {
    
    @PostMapping("/iniciar")
    fun iniciarJuego(@RequestBody dto: IniciarJuegoDTO): JuegoDTO {
        return hangmanService.iniciarJuego(dto.usuarioId, dto.asignaturaId)
    }
    
    @PostMapping("/intento")
    fun registrarIntento(@RequestBody dto: IntentoDTO): IntentoResultDTO {
        return hangmanService.registrarIntento(dto)
    }
    
    @PostMapping("/finalizar-palabra")
    fun finalizarPalabra(@RequestBody dto: FinalizarPalabraDTO): ResultadoPalabraDTO {
        return hangmanService.finalizarPalabra(dto)
    }
    
    @PutMapping("/{juegoId}/finalizar")
    fun finalizarJuego(
        @PathVariable juegoId: UUID,
        @RequestBody dto: FinalizarJuegoDTO
    ): ResultadoJuegoDTO {
        return hangmanService.finalizarJuego(juegoId, dto.puntajeTotal)
    }
}
```

**Checklist**:
- [ ] Controlador creado
- [ ] Servicios implementados
- [ ] Métricas registradas
- [ ] Puntajes calculados
- [ ] Tests unitarios

---

### 🔷 **FASE 5: MODIFICAR FRONTEND HANGMAN** (Día 14-17)

#### ✅ **Tarea 5.1: Integrar API de Conceptos**

**Modificar**: `hangman-game/script/script.js`

```javascript
// ANTES: Cargar desde word-list.js
let { word, hint } = wordList[Math.floor(Math.random() * wordList.length)];

// DESPUÉS: Cargar desde API
async function cargarConceptos(asignaturaId, usuarioId) {
    const response = await fetch(`${API_BASE_URL}/api/juegos/hangman/iniciar`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify({ asignaturaId, usuarioId })
    });
    
    const data = await response.json();
    return data.conceptos; // Lista de 10-12 conceptos
}

// Variables globales
let conceptosActuales = [];
let conceptoIndex = 0;
let juegoId = null;
let vidasTotales = 3;

async function iniciarPartida() {
    conceptosActuales = await cargarConceptos(ASIGNATURA_ID, USUARIO_ID);
    conceptoIndex = 0;
    vidasTotales = 3;
    cargarSiguientePalabra();
}

function cargarSiguientePalabra() {
    if (conceptoIndex >= conceptosActuales.length) {
        finalizarJuegoCompleto();
        return;
    }
    
    const concepto = conceptosActuales[conceptoIndex];
    currentWord = concepto.palabra.toUpperCase();
    hint.innerText = concepto.hint;
    conceptoIndex++;
    
    resetDisplay();
}
```

**Checklist**:
- [ ] Fetch API implementado
- [ ] Autenticación JWT agregada
- [ ] Manejo de errores
- [ ] Loading states
- [ ] Tests E2E

---

#### ✅ **Tarea 5.2: Sistema de 3 Vidas Totales**

```javascript
let vidasTotales = 3;
let erroresEnPalabraActual = 0;

function initGame(button) {
    currentWord = randomWord.word.toUpperCase();
    letrasUsadas.clear();
    erroresEnPalabraActual = 0;
    
    // Mostrar vidas totales restantes
    document.getElementById('vidas-totales').innerText = `Vidas: ${vidasTotales}`;
    
    guessedWord = currentWord.split("").map(() => "_");
    hangmanImage.src = `images/hangman-${6 - vidasTotales}.svg`; // Ajustar imagen global
    hintText.innerText = clue;
    incorrectCount = 0;
    guessesText.innerText = incorrectCount + " / 6";
    
    wordDisplay.innerHTML = guessedWord.map(letter => `<li class="letter">${letter}</li>`).join("");
    gameModal.classList.remove("show");
    
    // Habilitar botones
    keyboardDiv.querySelectorAll("button").forEach(btn => btn.disabled = false);
}

function initGame(button) {
    // Letra incorrecta
    if vidasTotales--;
    
    if (vidasTotales === 0) {
        // GAME OVER total
        gameOver(false);
        mostrarAnalisisFinal();
        return;
    }
    
    // Actualizar UI
    hangmanImage.src = `images/hangman-${3 - vidasTotales}.svg`;
}
```

**Checklist**:
- [ ] Lógica de 3 vidas implementada
- [ ] UI actualizada (mostrar vidas globales)
- [ ] Imágenes SVG ajustadas
- [ ] Game over total funcional

---

#### ✅ **Tarea 5.3: Registro de Métricas por Letra**

```javascript
async function registrarIntento(letra, esCorrecta) {
    const tiempoInicio = Date.now();
    
    await fetch(`${API_BASE_URL}/api/scoring/hangman/intento`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify({
            juegoId: juegoId,
            conceptoId: conceptosActuales[conceptoIndex - 1].id,
            letra: letra,
            esCorrecta: esCorrecta,
            tiempoMs: Date.now() - tiempoInicio
        })
    });
}

// Modificar initGame para llamar a registrarIntento
function initGame(button) {
    const clickedLetter = button.innerText.toLowerCase();
    
    if (currentWord.includes(clickedLetter)) {
        // Letra correcta
        registrarIntento(clickedLetter, true);
        // ... resto de lógica
    } else {
        // Letra incorrecta
        registrarIntento(clickedLetter, false);
        incorrectCount++;
        vidasTotales--;
        // ... resto de lógica
    }
}
```

**Checklist**:
- [ ] Registro de métricas implementado
- [ ] Tiempo de respuesta calculado
- [ ] Errores manejados (offline, timeout)

---

#### ✅ **Tarea 5.4: Análisis Final con IA**

```javascript
async function mostrarAnalisisFinal() {
    const response = await fetch(`${API_BASE_URL}/api/juegos/hangman/${juegoId}/analisis`);
    const analisis = await response.json();
    
    // Mostrar modal con resultados
    document.getElementById('puntaje-final').innerText = analisis.puntajeTotal;
    document.getElementById('conceptos-correctos').innerText = analisis.conceptosCorrectos;
    document.getElementById('conceptos-erroneos').innerText = analisis.conceptosErroneos;
    
    // Mostrar explicaciones de IA
    const listaErrores = document.getElementById('lista-errores');
    listaErrores.innerHTML = '';
    
    analisis.analisisErrores.forEach(error => {
        const div = document.createElement('div');
        div.className = 'error-card';
        div.innerHTML = `
            <h3>${error.concepto}</h3>
            <p><strong>Tema:</strong> ${error.tema}</p>
            <p><strong>Explicación:</strong> ${error.explicacion}</p>
        `;
        listaErrores.appendChild(div);
    });
    
    document.getElementById('modal-analisis').classList.add('show');
}
```

**Checklist**:
- [ ] Modal de análisis creado
- [ ] Integración con endpoint de análisis
- [ ] CSS para error cards
- [ ] Botón "Volver a jugar"

---

### 🔷 **FASE 6: TESTING Y VALIDACIÓN** (Día 18-20)

#### ✅ **Tests Backend**

```kotlin
@SpringBootTest
class IAServiceIntegrationTest {
    
    @Test
    fun `debe procesar PDF correctamente`() {
        val archivo = MockMultipartFile("test.pdf", pdfBytes)
        val resultado = procesamientoPDFService.procesarPDF(archivoId, temaId)
        
        assertThat(resultado.conceptosExtraidos).isGreaterThan(0)
        assertThat(resultado.estado).isEqualTo(EstadoProcesamiento.COMPLETADO)
    }
    
    @Test
    fun `debe manejar error de Gemini API`() {
        // Mock Gemini retornando error
        assertThrows<GeminiAPIException> {
            geminiClient.procesarTexto("texto inválido", "tema")
        }
    }
}
```

**Checklist**:
- [ ] Tests unitarios (80% coverage)
- [ ] Tests de integración
- [ ] Tests E2E (Postman/Newman)
- [ ] Load testing (JMeter)

---

#### ✅ **Tests Frontend**

```javascript
// test-hangman.js
describe('Hangman Game Integration', () => {
    it('should load concepts from API', async () => {
        const conceptos = await cargarConceptos(ASIGNATURA_ID, USUARIO_ID);
        expect(conceptos).toHaveLength(10);
    });
    
    it('should end game after 3 total mistakes', () => {
        vidasTotales = 1;
        initGame({ innerText: 'Z' }); // Letra incorrecta
        expect(document.querySelector('.game-modal').classList.contains('show')).toBe(true);
    });
});
```

**Checklist**:
- [ ] Unit tests (Jest)
- [ ] Integration tests (Cypress)
- [ ] UI tests (Selenium)

---

## 🎯 Estado Actual y Próximos Pasos

### Estado Actual
- ✅ Hangman funcional (standalone)
- ✅ UI completa
- ✅ Lógica de juego implementada
- ✅ Estructura de BD analizada
- ✅ Estrategia de integración definida
- ✅ **Reglas de juego documentadas** ⭐ NUEVO
- ✅ **Checklist detallado creado** ⭐ NUEVO

### Próximos Pasos (Comenzamos juntos)
1. **¿Modificamos primero el Backend o el Frontend?**
2. **¿Empezamos con datos estáticos o conectamos API directamente?**
3. **¿Qué prioridad: métricas básicas o aprendizaje adaptativo?**

---

## 💡 Preguntas para Decidir

1. **¿Qué microservicio atacamos primero?** (Content Service recomendado)
2. **✅ Tablas específicas por juego confirmadas** (`metricas_juego_hangman`, `resultados_juego_hangman`)
3. **✅ Campo `hint` agregado a `conceptos`** (generado por Gemini)
4. **¿Movemos Hangman a `/static` o creamos app separada?**
5. **¿Implementamos BFF para Hangman o llamadas directas?**
6. **¿Creamos tablas de ejemplo para Criss-Cross ahora o después?**

---

## 🎮 **NUEVO: Arquitectura Escalable Multi-Juego**

### Patrón de Nomenclatura Establecido:
```
✅ metricas_juego_{nombre}      → Cada acción individual del juego
✅ resultados_juego_{nombre}    → Resultado final por concepto/palabra

Juegos planeados:
- hangman      (Ahorcado) ← IMPLEMENTANDO AHORA
- crisscross   (Crucigrama)
- memory       (Memorice)
- trivia       (Quiz/Trivia)
- wordsearch   (Sopa de letras)
- flashcards   (Tarjetas de estudio)
```

### Ventajas Arquitectónicas:
✅ **Aislamiento**: Cada juego tiene sus propias métricas específicas  
✅ **Escalabilidad**: Agregar nuevo juego = crear 2 tablas nuevas  
✅ **Performance**: Queries más rápidas (tablas más pequeñas)  
✅ **Flexibilidad**: Cada juego puede tener métricas únicas  
✅ **Mantenimiento**: Código más limpio y organizado

---

**🚀 Esperando tu decisión para comenzar la integración paso a paso!**


# PIBIC SMA Workspace

Ambiente de simulação em **Sistemas Multiagentes (SMA)** para estudo empírico de aprendizado adaptativo, coordenado e cooperativo via **JADE**.

* **Pesquisa:** Iniciação Científica (PIBIC)
* **Adaptação:** Pedro Barbosa
* **Base Original:** *JADE Multi-Agent System for Game Simulation* (Tornike Sikharulidze)

---

## Componentes

* **`MainAgent.java`:** Coordena a simulação, valida regras e sincroniza estados e turnos.
* **`GUI.java`:** Interface gráfica para acompanhamento visual e logs em tempo real.
* **`RL_Agent.java`:** Agente com aprendizado por reforço via algoritmo **Q-Learning**.
* **`NN_Agent.java`:** Agente com tomada de decisão baseada em **Redes Neurais**.
* **`RandomAgent.java`:** Agente com ações aleatórias (linha de base/baseline).
* **`SOM.java`:** Módulo de agrupamento via **Mapas Auto-organizáveis de Kohonen**.

---

## Estrutura

```text
GUI_JADE_Tornike_Sikharulidze/GUI_JADE/
├── lib/jade.jar                    # Biblioteca JADE
├── MainAgent.java / GUI.java       # Orquestrador e interface
├── RL_Agent.java / NN_Agent.java   # Agentes com IA
├── RandomAgent.java / SOM.java     # Baseline e agrupamento
└── *.sh                            # Scripts de execução
```

---

## Como Executar

### Pré-requisitos
* Java JDK 8+
* Ambiente Linux/Bash

### Execução

Acesse o diretório, garanta permissão aos scripts e execute o cenário desejado:

```bash
cd GUI_JADE_Tornike_Sikharulidze/GUI_JADE
chmod +x *.sh
```

* **Interface padrão:**
  ```bash
  ./example.sh
  ```
* **Q-Learning vs. Random (Baseline):**
  ```bash
  ./RL_Agent_vs_RandomAgent.sh
  ```
* **Random vs. Random (Controle):**
  ```bash
  ./RandomAgent_vs_RandomAgent.sh
  ```

> **Recompilação (em caso de alterações no código):**
> ```bash
> javac -cp .:lib/jade.jar *.java
> ```
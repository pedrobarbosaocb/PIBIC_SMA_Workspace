# PIBIC SMA Workspace

Ambiente de simulação em **Sistemas Multiagentes (SMA)** para estudo empírico de aprendizado adaptativo, coordenado e cooperativo via **JADE**.

* **Pesquisa:** Iniciação Científica (PIBIC)
* **Adaptação:** Pedro Barbosa
* **Base Original:** *JADE Multi-Agent System for Game Simulation* (Tornike Sikharulidze)

---

## O Dilema do Prisioneiro

O ambiente modela o **Dilema do Prisioneiro**, um problema clássico da Teoria dos Jogos onde dois participantes escolhem simultaneamente entre **Cooperar (C)** ou **Trair/Desertar (D)** sem comunicação prévia. 

A dinâmica é regida pela seguinte matriz de recompensas (*payoff*):

| Jogador A \ Jogador B | Cooperar (C) | Trair (D) |
| :--- | :---: | :---: |
| **Cooperar (C)** | (2, 2) | (0, 4) |
| **Trair (D)** | (4, 0) | (0, 0) |

* **Cooperação mútua (C, C):** Ambos recebem recompensa moderada (2 pontos cada).
* **Traição unilateral (D, C ou C, D):** O desertor obtém o ganho máximo (4 pontos) e o cooperador fica sem pontuar (0 pontos).
* **Traição mútua (D, D):** Ambos anulam os ganhos e recebem 0 pontos.

O objetivo do experimento é avaliar se agentes dotados de algoritmos de aprendizado conseguem convergir para estratégias estáveis ou explorar o comportamento de oponentes com perfis distintos.

---

## Componentes

* **`MainAgent.java`:** Coordena a simulação, atua como juiz, valida rodadas e computa pontuações.
* **`GUI.java`:** Interface gráfica para acompanhamento visual e logs em tempo real.
* **`RL_Agent.java`:** Agente com aprendizado por reforço via **Q-Learning**.
* **`NN_Agent.java`:** Agente com tomada de decisão baseada em **Redes Neurais**.
* **`RandomAgent.java`:** Agente com escolhas puramente aleatórias (linha de base/baseline).
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
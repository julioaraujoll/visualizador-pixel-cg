# visualizador-pixel-cg
O presente projeto, desenvolvido em Java(Swing/AWT) tem como objetivo de demonstrar graficamente a ativação de um pixel na tela,
de forma iterativa, a partir da transformação das coordenadas x,y do usuário para NDCs e
de NDCs para coordenadas de dispositivos.Da mesma forma, dados de entrada gráficos são transformados de
coordenadas do dispositivo para NDCs, e depois para coordenadas do usuário.

O usuário pode definir a janela e a resolução do dispositivo. O programa calcula a posição do pixel e o acende em verde, a fim de destaque, na tela limpa em preto, usando o drawPixel()




| Procedimento   | Direção            | Onde está implementado |
|----------------|---------------------|-------------------------|
| `user_to_ndc`  | Mundo → NDC          | `CoordTransformViewer.userToNdc` |
| `inp_to_ndc`   | Mundo → NDC (alias)  | `CoordTransformViewer.inpToNdc`  |
| `ndc_to_user`  | NDC → Mundo (inversa)| `CoordTransformViewer.ndcToUser` |
| `ndc_to_dc`    | NDC → Dispositivo    | `CoordTransformViewer.ndcToDc`   |

Dois cenários de NDC são suportados: `[0,1] × [0,1]` e `[-1,1] × [-1,1]`
(coordenadas normalizadas centradas na origem).

### Fundamentação matemática

**Mundo → NDC**, com janela `[xmin,xmax] × [ymin,ymax]`:

- Cenário `[0,1]×[0,1]`:
  ```
  ndcx = (x - xmin) / (xmax - xmin)
  ndcy = (y - ymin) / (ymax - ymin)
  ```
- Cenário `[-1,1]×[-1,1]` (reescala o resultado acima com `2t - 1`):
  ```
  ndcx = 2·(x - xmin)/(xmax - xmin) - 1
  ndcy = 2·(y - ymin)/(ymax - ymin) - 1
  ```

**NDC → Mundo** (inversa da anterior):

- `[0,1]`: `x = xmin + ndcx·(xmax-xmin)`, `y = ymin + ndcy·(ymax-ymin)`
- `[-1,1]`: `x = xmin + (ndcx+1)/2·(xmax-xmin)`, análogo para `y`

**NDC → Dispositivo**, com resolução `ndh × ndv`:

1. Normaliza o NDC para `[0,1]`: `u = ndcx` (ou `(ndcx+1)/2` no cenário `[-1,1]`), idem para `v`.
2. Aplica:
   ```
   dcx = round(u · (ndh-1))
   dcy = round((1-v) · (ndv-1))   // eixo Y invertido: dispositivo cresce para baixo
   ```

A expressão final de `ndc_to_dc` é sempre `round(u·(n-1))`; o que muda entre
os dois cenários é apenas como `u`/`v` são obtidos a partir do NDC.


## Requisitos

- Java SE 8 ou superior (usa apenas `javax.swing` e `java.awt`, sem
  dependências externas).
- Ambiente com suporte a interface gráfica para exibir a janela.
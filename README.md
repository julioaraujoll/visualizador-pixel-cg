# visualizador-pixel-cg

Projeto em Java (Swing/AWT) que mostra na prática a transformação de
coordenadas do mundo para NDC e de NDC para dispositivo:

```
Mundo  --user_to_ndc-->  NDC  --ndc_to_dc-->  Dispositivo (pixel)
```

Você define a janela do mundo e a resolução do dispositivo, o programa
calcula o pixel correspondente e acende ele em verde numa tela preta
limpa (sem grade), usando `drawPixel()`.

Suporta dois cenários de NDC: `[0,1]x[0,1]` e `[-1,1]x[-1,1]`.

> Obs: a transformação inversa completa (dispositivo → NDC → mundo, tipo
> picking com clique do mouse) ainda não existe. O `ndc_to_user` que tem
> aqui é só pra conferir se a ida e volta bate.

## Arquivos

- `Main.java` — só dá o `setVisible` na janela
- `NdcRange.java` — enum com os dois cenários de NDC
- `DisplayPanel.java` — o "display": framebuffer + `drawPixel`
- `CoordTransformViewer.java` — as transformações (`userToNdc`,
  `inpToNdc`, `ndcToUser`, `ndcToDc`) e a interface

Javadoc completo em cada classe/método no próprio código.

## Fórmulas

Mundo → NDC, janela `[xmin,xmax] x [ymin,ymax]`:

```
# cenário [0,1]
ndcx = (x - xmin) / (xmax - xmin)
ndcy = (y - ymin) / (ymax - ymin)

# cenário [-1,1] -> mesma coisa, so reescala com 2t-1
ndcx = 2*(x - xmin)/(xmax - xmin) - 1
ndcy = 2*(y - ymin)/(ymax - ymin) - 1
```

NDC → Dispositivo, resolução `ndh x ndv`:

```
u = ndcx                (cenário [0,1])
u = (ndcx + 1) / 2       (cenário [-1,1])

dcx = round(u * (ndh - 1))
dcy = round((1 - u_y) * (ndv - 1))   # inverte Y, tela cresce pra baixo
```

## Rodar

```bash
cd src
javac *.java
java Main
```

Precisa de ambiente gráfico (não roda headless).

## Requisitos

Java 8+. Só `javax.swing`/`java.awt`, sem dependência externa.

## Licença

MIT — ver [LICENSE](LICENSE).
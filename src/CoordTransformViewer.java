import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * Transformacao de coordenadas: Mundo (janela do usuario) -> NDC -> Dispositivo.
 *
 * Procedimentos pedidos:
 *   - user_to_ndc / inp_to_ndc : mundo -> NDC        (mesma transformacao;
 *                                  "entrada do usuario" é a coordenada do mundo)
 *   - ndc_to_user              : NDC   -> mundo       (transformacao inversa)
 *   - ndc_to_dc                : NDC   -> dispositivo (pixel)
 *
 * Dois cenarios de NDC sao suportados: [0,1]x[0,1] e [-1,1]x[-1,1].
 */
public class CoordTransformViewer extends JFrame {

    // ---- Janela do mundo, definida pelo usuario (item 1 do enunciado) ----
    private double xmin, xmax, ymin, ymax;

    // ---- Resolucao do dispositivo (viewport / display) ----
    private int ndh, ndv;

    private NdcRange ndcRange = NdcRange.ZERO_UM;

    private DisplayPanel display;
    private JTextField txXmin, txXmax, txYmin, txYmax, txNdh, txNdv, txX, txY;
    private JComboBox<NdcRange> cbRange;
    private JLabel lblInfo;

    public CoordTransformViewer() {
        super("Transformacao de Coordenadas - Mundo -> NDC -> Dispositivo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // valores default da janela do mundo e do dispositivo
        xmin = -50; xmax = 50; ymin = -50; ymax = 50;
        ndh = 640;  ndv = 480;

        display = new DisplayPanel(ndh, ndv);
        add(display, BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

  // Transformações

    /** user_to_ndc: mundo -> NDC, de acordo com o cenario escolhido. */
    private double[] userToNdc(double x, double y) {
        double ndcx, ndcy;
        if (ndcRange == NdcRange.ZERO_UM) {
            ndcx = (x - xmin) / (xmax - xmin);
            ndcy = (y - ymin) / (ymax - ymin);
        } else { // [-1,1] x [-1,1]
            ndcx = 2.0 * (x - xmin) / (xmax - xmin) - 1.0;
            ndcy = 2.0 * (y - ymin) / (ymax - ymin) - 1.0;
        }
        return new double[]{ndcx, ndcy};
    }

    /** inp_to_ndc: alias de user_to_ndc (entrada do usuario -> NDC). */
    private double[] inpToNdc(double x, double y) {
        return userToNdc(x, y);
    }

    /** ndc_to_user: NDC -> mundo (transformacao inversa de user_to_ndc). */
    private double[] ndcToUser(double ndcx, double ndcy) {
        double x, y;
        if (ndcRange == NdcRange.ZERO_UM) {
            x = xmin + ndcx * (xmax - xmin);
            y = ymin + ndcy * (ymax - ymin);
        } else {
            x = xmin + (ndcx + 1.0) / 2.0 * (xmax - xmin);
            y = ymin + (ndcy + 1.0) / 2.0 * (ymax - ymin);
        }
        return new double[]{x, y};
    }

    /** ndc_to_dc: NDC -> coordenadas de dispositivo (pixel), com round(). */
    private int[] ndcToDc(double ndcx, double ndcy) {
        double u, v; // normalizados para [0,1], independente do cenario
        if (ndcRange == NdcRange.ZERO_UM) {
            u = ndcx;
            v = ndcy;
        } else {
            u = (ndcx + 1.0) / 2.0;
            v = (ndcy + 1.0) / 2.0;
        }
        int dcx = (int) Math.round(u * (ndh - 1));
        // eixo Y do dispositivo cresce para baixo -> inverte em relacao ao mundo
        int dcy = (int) Math.round((1.0 - v) * (ndv - 1));
        return new int[]{dcx, dcy};
    }

    // =========================================================
    //  INTERFACE
    // =========================================================

    private JPanel buildControlPanel() {
        JPanel grid = new JPanel(new GridLayout(0, 4, 6, 6));
        grid.setBorder(BorderFactory.createTitledBorder("Especificacoes (item 1 e 2)"));

        txXmin = new JTextField(String.valueOf(xmin));
        txXmax = new JTextField(String.valueOf(xmax));
        txYmin = new JTextField(String.valueOf(ymin));
        txYmax = new JTextField(String.valueOf(ymax));
        txNdh  = new JTextField(String.valueOf(ndh));
        txNdv  = new JTextField(String.valueOf(ndv));
        txX    = new JTextField("10");
        txY    = new JTextField("10");
        cbRange = new JComboBox<>(NdcRange.values());

        grid.add(new JLabel("xmin (janela mundo):")); grid.add(txXmin);
        grid.add(new JLabel("xmax:"));                 grid.add(txXmax);
        grid.add(new JLabel("ymin:"));                 grid.add(txYmin);
        grid.add(new JLabel("ymax:"));                 grid.add(txYmax);
        grid.add(new JLabel("ndh (largura disp.):"));  grid.add(txNdh);
        grid.add(new JLabel("ndv (altura disp.):"));   grid.add(txNdv);
        grid.add(new JLabel("Ponto x (mundo):"));      grid.add(txX);
        grid.add(new JLabel("Ponto y (mundo):"));      grid.add(txY);
        grid.add(new JLabel("Cenario NDC:"));          grid.add(cbRange);

        JButton btn = new JButton("Transformar e Exibir Pixel Ativo");
        btn.addActionListener(this::onTransformar);
        grid.add(btn);

        lblInfo = new JLabel(" ");
        grid.add(lblInfo);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private void onTransformar(ActionEvent e) {
        try {
            xmin = Double.parseDouble(txXmin.getText().trim());
            xmax = Double.parseDouble(txXmax.getText().trim());
            ymin = Double.parseDouble(txYmin.getText().trim());
            ymax = Double.parseDouble(txYmax.getText().trim());
            ndh  = Integer.parseInt(txNdh.getText().trim());
            ndv  = Integer.parseInt(txNdv.getText().trim());
            double x = Double.parseDouble(txX.getText().trim());
            double y = Double.parseDouble(txY.getText().trim());
            ndcRange = (NdcRange) cbRange.getSelectedItem();

            if (xmax <= xmin || ymax <= ymin || ndh <= 1 || ndv <= 1) {
                throw new NumberFormatException("valores fora de faixa");
            }

            display.resizeCanvas(ndh, ndv);
            display.clear();

            // ---- pipeline completo: mundo -> NDC -> dispositivo ----
            double[] ndc  = userToNdc(x, y);          // ou inpToNdc(x, y)
            int[] dc = ndcToDc(ndc[0], ndc[1]);
            double[] back = ndcToUser(ndc[0], ndc[1]); // verificacao (round-trip)

            // ativa o pixel unico na tela (sem "engrossar" o ponto)
            display.drawPixel(dc[0], dc[1], Color.GREEN);
            display.repaint();

            lblInfo.setText(String.format(
                "NDC=(%.4f, %.4f)   DC=(%d, %d)   volta p/ mundo=(%.3f, %.3f)",
                ndc[0], ndc[1], dc[0], dc[1], back[0], back[1]));

            revalidate();
            pack();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Verifique os valores numericos informados (xmax>xmin, ymax>ymin, ndh/ndv>1).",
                    "Entrada invalida", JOptionPane.ERROR_MESSAGE);
        }
    }
}

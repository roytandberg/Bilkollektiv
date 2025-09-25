import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

abstract class Bil {
    private final String BILNUMMER;
    private final int PRIS_PER_DAG;
    Bil neste, forgje;

    public Bil(String bilnummer, int prisPerDag) {
        this.BILNUMMER = bilnummer;
        this.PRIS_PER_DAG = prisPerDag;
    }

    int hentPrisPerDag() {
        return PRIS_PER_DAG;
    }

    public String toString() {
        return "Bilnummer: " + BILNUMMER + "\nPris per dag: " + PRIS_PER_DAG;
    }

    Bil finnBilR(Dialog dialog, boolean kunElektrisk) {
        if (kunElektrisk && !(this instanceof Elektrisk)) {
            return neste.finnBilR(dialog, kunElektrisk);
        }
        else {
            boolean ønsketBil = dialog.svarJaEllerNei("Er du interessert i denne bilen?\n" + this.toString());
            if (!ønsketBil) {
                return neste.finnBilR(dialog, kunElektrisk);
            }
        }
        return this;
        
    }
}

class Personbil extends Bil {
    private final int ANTALL_PASSASJERER;
    
    public Personbil(String bilnummer, int prisPerDag, int antallPassasjerer) {
        super(bilnummer, prisPerDag);
        this.ANTALL_PASSASJERER = antallPassasjerer;
    }

    @Override
    public String toString() {
        return super.toString() + "\nAntall passasjerer: " + ANTALL_PASSASJERER;
    }
}

class Varebil extends Bil {
    private final int LASTEVOLUM;

    public Varebil(String bilnummer, int prisPerDag, int lastevolum) {
        super(bilnummer, prisPerDag);
        this.LASTEVOLUM = lastevolum;
    }

    @Override
    public String toString() {
        return super.toString() + "\nLastevolum: " + LASTEVOLUM;
    }
}

interface Elektrisk {}

class ElektriskPersonbil extends Personbil implements Elektrisk {
    private final int BATTERI_STØRRELSE;

    public ElektriskPersonbil(String bilnummer, int prisPerDag, int antallPassasjerer, int batteriStørrelse) {
        super(bilnummer, prisPerDag, antallPassasjerer);
        this.BATTERI_STØRRELSE = batteriStørrelse;
    }

    @Override
    public String toString() {
        return super.toString() + "\nBatteristørrelse: " + BATTERI_STØRRELSE;
    }
}

class ElektriskVarebil extends Varebil implements Elektrisk {
    private final int BATTERI_STØRRELSE;

    public ElektriskVarebil(String bilnummer, int prisPerDag, int lastevolum, int batteriStørrelse) {
        super(bilnummer, prisPerDag, lastevolum);
        this.BATTERI_STØRRELSE = batteriStørrelse;
    }

    @Override
    public String toString() {
        return super.toString() + "\nBatteristørrelse: " + BATTERI_STØRRELSE;
    }
}

interface Dialog {
    boolean svarJaEllerNei(String spørsmål);
}

class TastaturDialog implements Dialog {
    Scanner scanner = new Scanner(System.in);

    @Override
    public boolean svarJaEllerNei(String spørsmål) {
        while (true) {
            System.out.println(spørsmål + " ");
            String svar = scanner.nextLine().trim().toLowerCase();
            if (svar.charAt(0) == 'j') return true;
            if (svar.charAt(0) == 'n') return false;
        }
    }
}

class Bilkollektiv {
    private final int AB;
    private Bil[] alleBilene;
    private Bil start, slutt;

    public Bilkollektiv(int antallBiler) {
        this.AB = antallBiler;
        alleBilene = new Bil[AB];
    }

    void lagBilPris() {
        for (int i = 0; i < AB; i++) {
            Bil denneBilen = alleBilene[i];
            if (start == null) {
                start = slutt = denneBilen;
                start.neste = slutt;
                slutt.forgje = start;
            }
            else {
                int dennePrisen = denneBilen.hentPrisPerDag();
                if (dennePrisen < start.hentPrisPerDag()) {
                    start.forgje = denneBilen;
                    denneBilen.neste = start;
                    start = denneBilen;
                }
                else if (dennePrisen >= slutt.hentPrisPerDag()) {
                    slutt.neste = denneBilen;
                    denneBilen.forgje = slutt;
                    slutt = denneBilen;
                }
                else {
                    Bil bilen = start.neste;
                    while (dennePrisen > bilen.hentPrisPerDag()) {
                        bilen = bilen.neste;
                    }
                    denneBilen.neste = bilen;
                    denneBilen.forgje = bilen.forgje;
                    bilen.forgje.neste = denneBilen;
                    bilen.forgje = denneBilen;
                }
            }
        }
    }

    void taUtBil(Bil b) {
        Bil denneBilen = start;
        while (denneBilen != b) {
            denneBilen = denneBilen.neste;
        }
        denneBilen.neste.forgje = denneBilen.forgje;
        denneBilen.forgje.neste = denneBilen.neste;
        denneBilen.neste = denneBilen.forgje = null;
    }


    Bil velgBil(Dialog d) {
        boolean kunElbil = d.svarJaEllerNei("Er du kun interessert i en elbil?");

        boolean ønsketBil = false;
        Bil denneBilen = start;
        while (!ønsketBil) {
            if (kunElbil) {
                while (!(denneBilen instanceof Elektrisk)) {
                    denneBilen = denneBilen.neste;
                }
            }
            ønsketBil = d.svarJaEllerNei("Er du interessert i denne bilen?\n" + denneBilen.toString());
            if (!ønsketBil) {
                denneBilen = denneBilen.neste;
            }
        }
        return denneBilen;
    }

    Bil velgBilR(Dialog d) {
        boolean kunElbil = d.svarJaEllerNei("Er du kun interessert i en elbil?");
        return start.finnBilR(d, kunElbil);
    }

    void visBilene() {
        System.out.println("Bilene er sortert: ");
        Bil b = start;
        while (b != null) {
            System.out.println(" " + b);
            b = b.neste;
        }
        System.out.println();
    }

    public static void main (String[] arg) {
        Bilkollektiv kol = new Bilkollektiv(3);
        kol.alleBilene[0] = new Personbil("AA00001", 350, 4);
        kol.alleBilene[1] = new ElektriskVarebil("AA00002", 745, 21, 50);
        kol.alleBilene[2] = new ElektriskPersonbil("AA00003", 310, 3, 45);
        kol.lagBilPris();
        kol.visBilene();
    
        Dialog d = new TastaturDialog();
        // Dialog d = new GUIDialog();
        for (int i = 1;  i <= 3;  ++i) {
            // Bil b = kol.velgBil(d);
            Bil b = kol.velgBilR(d);
            if (b == null)
            System.out.println("Ingen bil passet.");
            else
            System.out.println("Bil nr " + i + " er " + b + ".");
            kol.visBilene();
        }
        System.exit(0);
        }
    
}

class GUIDialog implements Dialog{
    JFrame vindu;
    JPanel panel;
    JLabel tekstfelt;
    JButton jaKnapp, neiKnapp;

    Thread hovedtråd = Thread.currentThread();
    boolean svaret = true;

    @Override
    public boolean svarJaEllerNei(String spørsmål) {
        if (vindu == null) {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.exit(1);
            }

            vindu = new JFrame("JA ELLER NEI?");
            vindu.setPreferredSize(new Dimension(500,500));
            vindu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            panel = new JPanel();
            vindu.add(panel);

            tekstfelt = new JLabel(spørsmål);
            panel.add(tekstfelt);

            class SvarJaNei implements ActionListener {
                boolean svar;

                SvarJaNei(boolean jn) {
                    svar = jn;
                }

                @Override
                public void actionPerformed(ActionEvent ae) {
                    svaret = svar;
                    hovedtråd.interrupt();
                }
            }
            jaKnapp = new JButton("JA");
            jaKnapp.addActionListener(new SvarJaNei(true));
            panel.add(jaKnapp);

            neiKnapp = new JButton("NEI");
            neiKnapp.addActionListener(new SvarJaNei(false));
            panel.add(neiKnapp);

            vindu.pack();
            vindu.setLocationRelativeTo(null);
            vindu.setVisible(true);
        }
        else {
            tekstfelt.setText(spørsmål);
        }

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            System.exit(1);
        }
        return svaret;
    }



}

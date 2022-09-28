package controller;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class ChartGUITest {
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Test Chart");
        RealTimeChartTest rtcp = new RealTimeChartTest("Random Data","Random Number", "Value");//Value will become
        frame.getContentPane().add(rtcp,new BorderLayout().CENTER);
        frame.pack();
        frame.setVisible(true);
        (new Thread(rtcp)).start();
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent windowevent)
            {
                System.exit(0);
            }

        });
    }
}

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import modelFSM.AnalyzeData;
import modelFSM.DerivCalc;
import modelFSM.DiscretizeData;
import modelFSM.ModelGen;
import modelFSM.data.ControlType;
import modelgen.data.property.*;

public class Main3 {

    public static void main(String[] args) {
        String groupSuffix = "_group";
        HashMap<String, ControlType> signals = new HashMap<String, ControlType>();

//        String fileName = "ABC_a_v1.csv";
//        signals.put("C", ControlType.OUTPUT);
//        signals.put("A", ControlType.INPUT);
//        signals.put("B", ControlType.INPUT);
        
        signals.put("p_$flow", ControlType.OUTPUT);
        signals.put("power_in", ControlType.INPUT);
        signals.put("gp_ack_bus[0]", ControlType.INPUT);
        String fileName = "buck_simple.csv";
        
        URL url = Main3.class.getClassLoader().getResource(fileName);
        
//        ModelGen modelGenerator = new ModelGen(url.getPath());
//        
//        for (String signal: signals.keySet()) {
//            modelGenerator.setSignalType(signal, signals.get(signal));
//        }
//
//        modelGenerator.processData();
        
        
        Property<ArrayList<String>> processorNames = new PropertyArrayList<String>("test property", String.class);
        processorNames.setValue(new ArrayList<String>());
        
        Property<Double> processorValue = new PropertyDouble("test property2");
        processorValue.setValue(new Double(4));
        
        Properties properties = new Properties();
        properties.put("test", processorNames);
        properties.put("test2", processorValue);
        
        for (IProperty p: properties.values())
            System.out.println(p.getName() + " " + p.getValue());
        
//        processorValue.setValue(processorNames.getValue());
        
        Object t1 = new Double(0);
        Object t2 = new ArrayList<Integer>();
        Object t3 = new ArrayList<String>();
        System.out.println(t1);
        
        Double temp = processorValue.getValue();
        ((ArrayList)t2).add(1);
        System.out.println(t2);
     //   t1 =  processorNames.getValue();
        System.out.println(t1);
        processorValue.setValue(t1);
        
        processorNames.setValue(t3);
        processorNames.setValue(t2);
        
        for (IProperty p: properties.values())
            System.out.println(p.getName() + " " + p.getValue());
        


//        DerivCalc derivCalc = new DerivCalc();
//        derivCalc.ReadFile(url.getPath());
//        derivCalc.CalcDerivSimple();
//        derivCalc.GroupDataSimple();
//        
//        AnalyzeData analyzeData = new AnalyzeData();
//        
//        ArrayList<Double> dataArrayGroupDouble = derivCalc.GetData(signalNameOut1 + groupSuffix);
//        ArrayList<Integer> dataArrayGroup = new ArrayList<Integer>();
//        
//        for (Double value: dataArrayGroupDouble) {
//            dataArrayGroup.add(value.intValue());
//        }
//        
//        analyzeData.addSignal(ControlType.OUTPUT, signalNameOut1, derivCalc.GetTime(), derivCalc.GetData(signalNameOut1), dataArrayGroup);
//        
//        dataArrayGroupDouble = derivCalc.GetData(signalNameIn1 + groupSuffix);
//        dataArrayGroup = new ArrayList<Integer>();
//        
//        for (Double value: dataArrayGroupDouble) {
//            dataArrayGroup.add(value.intValue());
//        }
//        
//        analyzeData.addSignal(ControlType.INPUT, signalNameIn1, derivCalc.GetTime(), derivCalc.GetData(signalNameIn1), dataArrayGroup);
//        
//        dataArrayGroupDouble = derivCalc.GetData(signalNameIn2 + groupSuffix);
//        dataArrayGroup = new ArrayList<Integer>();
//        
//        for (Double value: dataArrayGroupDouble) {
//            dataArrayGroup.add(value.intValue());
//        }
//        
////        analyzeData.addSignal(ControlType.INPUT, signalNameIn2, derivCalc.GetTime(), derivCalc.GetData(signalNameIn2), dataArrayGroup);
//        
//        analyzeData.addSignal(ControlType.INPUT, signalNameIn2, derivCalc.GetTime(), derivCalc.GetData(signalNameIn2));
//        
//        analyzeData.initDataRules(signalNameOut1);
//        analyzeData.analyzeDataRules(signalNameOut1);
        
//        System.out.println("DetectRuleCSC: " + analyzeData.detectRuleCSC(signalNameOut1));
//        System.out.println("ResolveCSC: " + analyzeData.resolveCSC(signalNameOut1));
//
//        System.out.println("DetectRuleCSC: " + analyzeData.detectRuleCSC(signalNameOut1));
//        System.out.println("ResolveCSC: " + analyzeData.resolveCSC(signalNameOut1));
//        
//        System.out.println("DetectRuleCSC: " + analyzeData.detectRuleCSC(signalNameOut1));
//        System.out.println("ResolveCSC: " + analyzeData.resolveCSC(signalNameOut1));
//
//        System.out.println("DetectRuleCSC: " + analyzeData.detectRuleCSC(signalNameOut1));
//        System.out.println("ResolveCSC: " + analyzeData.resolveCSC(signalNameOut1));
//        
//        System.out.println("DetectFullCSC: " + analyzeData.detectFullCSC(signalNameOut1));
//        System.out.println("ResolveCSC: " + analyzeData.resolveCSC(signalNameOut1));
//        
//        System.out.println("DetectFullCSC: " + analyzeData.detectFullCSC(signalNameOut1));
//        System.out.println("ResolveCSC: " + analyzeData.resolveCSC(signalNameOut1));
//        
//        System.out.println("DetectFullCSC: " + analyzeData.detectFullCSC(signalNameOut1));
        // TODO: final method to extract model rules
//        analyzeData.analyzeDataRules(signalNameOut1);
//        
//        GraphDraw graphDraw = new GraphDraw(derivCalc.GetTime(), derivCalc.GetData(signalNameIn1), 
//                derivCalc.GetData(signalNameIn1 + groupSuffix));
//        GraphDraw graphDraw2 = new GraphDraw(derivCalc.GetTime(), derivCalc.GetData(signalNameIn2), 
//                derivCalc.GetData(signalNameIn2 + groupSuffix));
//        GraphDraw graphDraw3 = new GraphDraw(derivCalc.GetTime(), derivCalc.GetData(signalNameOut1), 
//                derivCalc.GetData(signalNameOut1 + groupSuffix));
//        
//        JFrame f = new JFrame(fileName);
//        JScrollPane content = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        content.setBorder(null);
//
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//        content.setViewportView(panel);
//        panel.add(graphDraw);
//        panel.add(graphDraw2);
//        panel.add(graphDraw3);
//        
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.add(content, BorderLayout.CENTER);
//        f.setSize(1920,1080);
//        f.setLocation(200,200);
//        f.setVisible(true);
    }
    
}

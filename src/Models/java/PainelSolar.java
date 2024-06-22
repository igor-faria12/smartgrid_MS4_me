/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/smartgrid_v2/src/Models/dnl/PainelSolar.dnl
-1748133362
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;

import com.ms4systems.devs.core.message.Message;
import com.ms4systems.devs.core.message.MessageBag;
import com.ms4systems.devs.core.message.Port;
import com.ms4systems.devs.core.message.impl.MessageBagImpl;
import com.ms4systems.devs.core.model.impl.AtomicModelImpl;
import com.ms4systems.devs.core.simulation.Simulation;
import com.ms4systems.devs.core.simulation.Simulator;
import com.ms4systems.devs.core.simulation.impl.SimulationImpl;
import com.ms4systems.devs.extensions.PhaseBased;
import com.ms4systems.devs.extensions.StateVariableBased;
import com.ms4systems.devs.helpers.impl.SimulationOptionsImpl;
import com.ms4systems.devs.simviewer.standalone.SimViewer;

@SuppressWarnings("unused")
public class PainelSolar extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_MEDICAOPRODUCAO = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_QTDPLACASOLAR = 1;

    //ENDID
    //ID:SVAR:2
    private static final int ID_PRODMEDIAPORDIA = 2;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Producao medicaoProducao;
    protected int qtdPlacaSolar = 3;
    protected double prodMediaPorDia = 2.295;

    //ENDID
    String phase = "inicio";
    String previousPhase = null;
    Double sigma = 2.0;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<Producao> outProducao =
        addOutputPort("outProducao", Producao.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public PainelSolar() {
        this("PainelSolar");
    }

    public PainelSolar(String name) {
        this(name, null);
    }

    public PainelSolar(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        // Default state variable initialization
        qtdPlacaSolar = 3;
        prodMediaPorDia = 2.295;

        holdIn("inicio", 2.0);

    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("inicio")) {
            getSimulator().modelMessage("Internal transition from inicio");

            //ID:TRA:inicio
            holdIn("s1", 0.0);

            //ENDID
            return;
        }
        if (phaseIs("s1")) {
            getSimulator().modelMessage("Internal transition from s1");

            //ID:TRA:s1
            holdIn("inicio", 2.0);

            //ENDID
            return;
        }

        //passivate();
    }

    @Override
    public void externalTransition(double timeElapsed, MessageBag input) {
        currentTime += timeElapsed;
        // Subtract time remaining until next internal transition (no effect if sigma == Infinity)
        sigma -= timeElapsed;

        // Store prior data
        previousPhase = phase;
        previousSigma = sigma;

        // Fire state transition functions
    }

    @Override
    public void confluentTransition(MessageBag input) {
        // confluentTransition with internalTransition first (by default)
        internalTransition();
        externalTransition(0, input);
    }

    @Override
    public Double getTimeAdvance() {
        return sigma;
    }

    @Override
    public MessageBag getOutput() {
        MessageBag output = new MessageBagImpl();

        if (phaseIs("s1")) {

            // Output event code
            //ID:OUT:s1
            double nrProducao = qtdPlacaSolar * prodMediaPorDia;
            medicaoProducao = new Producao(nrProducao);
            output.add(outProducao, medicaoProducao);

            //ENDID
            // End output event code
        }
        return output;
    }

    // Custom function definitions

    // End custom function definitions
    public static void main(String[] args) {
        SimulationOptionsImpl options = new SimulationOptionsImpl(args, true);

        // Uncomment the following line to disable SimViewer for this model
        // options.setDisableViewer(true);

        // Uncomment the following line to disable plotting for this model
        // options.setDisablePlotting(true);

        // Uncomment the following line to disable logging for this model
        // options.setDisableLogging(true);
        PainelSolar model = new PainelSolar();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("PainelSolar Simulation", model, options);
            sim.startSimulation(0);
            sim.simulateIterations(Long.MAX_VALUE);
        } else { // Use SimViewer
            SimViewer viewer = new SimViewer();
            viewer.open(model, options);
        }
    }

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    // Getter/setter for medicaoProducao
    public void setMedicaoProducao(Producao medicaoProducao) {
        propertyChangeSupport.firePropertyChange("medicaoProducao",
            this.medicaoProducao, this.medicaoProducao = medicaoProducao);
    }

    public Producao getMedicaoProducao() {
        return this.medicaoProducao;
    }

    // End getter/setter for medicaoProducao

    // Getter/setter for qtdPlacaSolar
    public void setQtdPlacaSolar(int qtdPlacaSolar) {
        propertyChangeSupport.firePropertyChange("qtdPlacaSolar",
            this.qtdPlacaSolar, this.qtdPlacaSolar = qtdPlacaSolar);
    }

    public int getQtdPlacaSolar() {
        return this.qtdPlacaSolar;
    }

    // End getter/setter for qtdPlacaSolar

    // Getter/setter for prodMediaPorDia
    public void setProdMediaPorDia(double prodMediaPorDia) {
        propertyChangeSupport.firePropertyChange("prodMediaPorDia",
            this.prodMediaPorDia, this.prodMediaPorDia = prodMediaPorDia);
    }

    public double getProdMediaPorDia() {
        return this.prodMediaPorDia;
    }

    // End getter/setter for prodMediaPorDia

    // State variables
    public String[] getStateVariableNames() {
        return new String[] {
            "medicaoProducao", "qtdPlacaSolar", "prodMediaPorDia"
        };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { medicaoProducao, qtdPlacaSolar, prodMediaPorDia };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { Producao.class, Integer.class, Double.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_MEDICAOPRODUCAO:
                setMedicaoProducao((Producao) value);
                return;

            case ID_QTDPLACASOLAR:
                setQtdPlacaSolar((Integer) value);
                return;

            case ID_PRODMEDIAPORDIA:
                setProdMediaPorDia((Double) value);
                return;

            default:
                return;
        }
    }

    // Convenience functions
    protected void passivate() {
        passivateIn("passive");
    }

    protected void passivateIn(String phase) {
        holdIn(phase, Double.POSITIVE_INFINITY);
    }

    protected void holdIn(String phase, Double sigma) {
        this.phase = phase;
        this.sigma = sigma;
        getSimulator()
            .modelMessage("Holding in phase " + phase + " for time " + sigma);
    }

    protected static File getModelsDirectory() {
        URI dirUri;
        File dir;
        try {
            dirUri = PainelSolar.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                PainelSolar.class.getResource(".").toString());
        }
        boolean foundModels = false;
        while (dir != null && dir.getParentFile() != null) {
            if (dir.getName().equalsIgnoreCase("java") &&
                  dir.getParentFile().getName().equalsIgnoreCase("models")) {
                return dir.getParentFile();
            }
            dir = dir.getParentFile();
        }
        throw new RuntimeException(
            "Could not find Models directory from model path: " +
            dirUri.toASCIIString());
    }

    protected static File getDataFile(String fileName) {
        return getDataFile(fileName, "txt");
    }

    protected static File getDataFile(String fileName, String directoryName) {
        File modelDir = getModelsDirectory();
        File dir = new File(modelDir, directoryName);
        if (dir == null) {
            throw new RuntimeException("Could not find '" + directoryName +
                "' directory from model path: " + modelDir.getAbsolutePath());
        }
        File dataFile = new File(dir, fileName);
        if (dataFile == null) {
            throw new RuntimeException("Could not find '" + fileName +
                "' file in directory: " + dir.getAbsolutePath());
        }
        return dataFile;
    }

    protected void msg(String msg) {
        getSimulator().modelMessage(msg);
    }

    // Phase display
    public boolean phaseIs(String phase) {
        return this.phase.equals(phase);
    }

    public String getPhase() {
        return phase;
    }

    public String[] getPhaseNames() {
        return new String[] { "inicio", "s1" };
    }
}

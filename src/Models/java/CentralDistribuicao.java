/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/smartgrid/src/Models/dnl/CentralDistribuicao.dnl
2050320927
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
public class CentralDistribuicao extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_MEDICAOCONSUMO = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_MEDICAOPRODUCAO = 1;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Consumo medicaoConsumo;
    protected Producao medicaoProducao;

    //ENDID
    String phase = "s0";
    String previousPhase = null;
    Double sigma = Double.POSITIVE_INFINITY;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
    public final Port<Serializable> inConsumo =
        addInputPort("inConsumo", Serializable.class);

    //ENDID
    //ID:INP:1
    public final Port<Serializable> inProducao =
        addInputPort("inProducao", Serializable.class);

    //ENDID
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<Consumo> outConsumo =
        addOutputPort("outConsumo", Consumo.class);

    //ENDID
    //ID:OUTP:1
    public final Port<Producao> outProducao =
        addOutputPort("outProducao", Producao.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public CentralDistribuicao() {
        this("CentralDistribuicao");
    }

    public CentralDistribuicao(String name) {
        this(name, null);
    }

    public CentralDistribuicao(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        passivateIn("s0");

    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("s1")) {
            getSimulator().modelMessage("Internal transition from s1");

            //ID:TRA:s1
            holdIn("s2", 0.0);

            //ENDID
            return;
        }
        if (phaseIs("s2")) {
            getSimulator().modelMessage("Internal transition from s2");

            //ID:TRA:s2
            holdIn("null", Double.POSITIVE_INFINITY);

            //ENDID
            return;
        }
        if (phaseIs("s3")) {
            getSimulator().modelMessage("Internal transition from s3");

            //ID:TRA:s3
            passivateIn("s0");

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
        if (phaseIs("s0")) {
            if (input.hasMessages(inConsumo)) {
                ArrayList<Message<Serializable>> messageList =
                    inConsumo.getMessages(input);

                holdIn("s1", 0.0);

                // Fire state and port specific external transition functions
                //ID:EXT:s0:inConsumo
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < messageList.size(); i++) {
                    Consumo valueReceived =
                        (Consumo) messageList.get(i).getData();
                    double value = (double) valueReceived.getValue();
                    sb.append(value + ",");
                }
                sb.deleteCharAt(sb.length() - 1);
                System.out.println("Values: " + sb.toString());

                //ENDID
                // End external event code
                return;
            }
        }

        if (phaseIs("s1")) {
        }

        if (phaseIs("s2")) {
            if (input.hasMessages(inProducao)) {
                ArrayList<Message<Serializable>> messageList =
                    inProducao.getMessages(input);

                holdIn("s3", 0.0);

                return;
            }
        }
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
        CentralDistribuicao model = new CentralDistribuicao();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("CentralDistribuicao Simulation", model,
                    options);
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

    // Getter/setter for medicaoConsumo
    public void setMedicaoConsumo(Consumo medicaoConsumo) {
        propertyChangeSupport.firePropertyChange("medicaoConsumo",
            this.medicaoConsumo, this.medicaoConsumo = medicaoConsumo);
    }

    public Consumo getMedicaoConsumo() {
        return this.medicaoConsumo;
    }

    // End getter/setter for medicaoConsumo

    // Getter/setter for medicaoProducao
    public void setMedicaoProducao(Producao medicaoProducao) {
        propertyChangeSupport.firePropertyChange("medicaoProducao",
            this.medicaoProducao, this.medicaoProducao = medicaoProducao);
    }

    public Producao getMedicaoProducao() {
        return this.medicaoProducao;
    }

    // End getter/setter for medicaoProducao

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "medicaoConsumo", "medicaoProducao" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { medicaoConsumo, medicaoProducao };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { Consumo.class, Producao.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_MEDICAOCONSUMO:
                setMedicaoConsumo((Consumo) value);
                return;

            case ID_MEDICAOPRODUCAO:
                setMedicaoProducao((Producao) value);
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
            dirUri = CentralDistribuicao.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                CentralDistribuicao.class.getResource(".").toString());
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
        return new String[] { "s0", "s1", "s2", "s3" };
    }
}

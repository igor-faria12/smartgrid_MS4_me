/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/smartgrid_v2/src/Models/dnl/CentralEnergia.dnl
699552672
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;

// Custom library code
//ID:LIB:0
import java.text.DecimalFormat;

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

//ENDID
// End custom library code
@SuppressWarnings("unused")
public class CentralEnergia extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_MEDICAOCONSUMO = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_TARIFA = 1;

    //ENDID
    //ID:SVAR:2
    private static final int ID_DIA = 2;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Consumo medicaoConsumo;
    protected double tarifa = 0.71063;
    protected int dia = 1;

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
    // End input ports

    // Output ports
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public CentralEnergia() {
        this("CentralEnergia");
    }

    public CentralEnergia(String name) {
        this(name, null);
    }

    public CentralEnergia(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        // Default state variable initialization
        tarifa = 0.71063;
        dia = 1;

        passivateIn("s0");

    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

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

                passivateIn("s0");
                // Fire state and port specific external transition functions
                //ID:EXT:s0:inConsumo
                dia += 1;
                medicaoConsumo = (Consumo) messageList.get(0).getData();
                DecimalFormat numberFormat = new DecimalFormat("#.00");
                System.out.println("Consumo/Custo no dia " + dia + ": " +
                    numberFormat.format(medicaoConsumo.getValue()) + "/" +
                    (numberFormat.format(medicaoConsumo.getValue() * tarifa)));

                //ENDID
                // End external event code
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
        CentralEnergia model = new CentralEnergia();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("CentralEnergia Simulation", model, options);
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

    // Getter/setter for tarifa
    public void setTarifa(double tarifa) {
        propertyChangeSupport.firePropertyChange("tarifa", this.tarifa,
            this.tarifa = tarifa);
    }

    public double getTarifa() {
        return this.tarifa;
    }

    // End getter/setter for tarifa

    // Getter/setter for dia
    public void setDia(int dia) {
        propertyChangeSupport.firePropertyChange("dia", this.dia, this.dia = dia);
    }

    public int getDia() {
        return this.dia;
    }

    // End getter/setter for dia

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "medicaoConsumo", "tarifa", "dia" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { medicaoConsumo, tarifa, dia };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { Consumo.class, Double.class, Integer.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_MEDICAOCONSUMO:
                setMedicaoConsumo((Consumo) value);
                return;

            case ID_TARIFA:
                setTarifa((Double) value);
                return;

            case ID_DIA:
                setDia((Integer) value);
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
            dirUri = CentralEnergia.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                CentralEnergia.class.getResource(".").toString());
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
        return new String[] { "s0" };
    }
}

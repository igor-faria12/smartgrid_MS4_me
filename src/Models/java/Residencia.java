/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/smartgrid_v2/src/Models/dnl/Residencia.dnl
1289329420
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
public class Residencia extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_MEDICAOCONSUMO = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_MEDICAOPRODUCAO = 1;

    //ENDID
    //ID:SVAR:2
    private static final int ID_CONSUMOSALA = 2;

    //ENDID
    //ID:SVAR:3
    private static final int ID_CONSUMOQUARTO = 3;

    //ENDID
    //ID:SVAR:4
    private static final int ID_CONSUMOLAVANDERIA = 4;

    //ENDID
    //ID:SVAR:5
    private static final int ID_CONSUMOESCRITORIO = 5;

    //ENDID
    //ID:SVAR:6
    private static final int ID_CONSUMOCOZINHA = 6;

    //ENDID
    //ID:SVAR:7
    private static final int ID_CONSUMOBANHEIRO = 7;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Consumo medicaoConsumo;
    protected Producao medicaoProducao;
    protected double consumoSala = 1.65;
    protected double consumoQuarto = 4.62;
    protected double consumoLavanderia = 3.0;
    protected double consumoEscritorio = 2.08;
    protected double consumoCozinha = 4.66;
    protected double consumoBanheiro = 3.75;

    //ENDID
    String phase = "s0";
    String previousPhase = null;
    Double sigma = Double.POSITIVE_INFINITY;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
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

    public Residencia() {
        this("Residencia");
    }

    public Residencia(String name) {
        this(name, null);
    }

    public Residencia(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        // Default state variable initialization
        consumoSala = 1.65;
        consumoQuarto = 4.62;
        consumoLavanderia = 3.0;
        consumoEscritorio = 2.08;
        consumoCozinha = 4.66;
        consumoBanheiro = 3.75;

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
            if (input.hasMessages(inProducao)) {
                ArrayList<Message<Serializable>> messageList =
                    inProducao.getMessages(input);

                holdIn("s1", 0.0);
                // Fire state and port specific external transition functions
                //ID:EXT:s0:inProducao
                medicaoProducao = (Producao) messageList.get(0).getData();

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

        if (phaseIs("s1")) {

            // Output event code
            //ID:OUT:s1
            double nrConsumo =
                consumoSala + consumoLavanderia + consumoEscritorio +
                consumoQuarto + consumoCozinha + consumoBanheiro;
            medicaoConsumo = new Consumo(nrConsumo);
            output.add(outConsumo, medicaoConsumo);

            //ENDID
            // End output event code
        }
        if (phaseIs("s2")) {
            // Output event code
            //ID:OUT:s2
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
        Residencia model = new Residencia();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("Residencia Simulation", model, options);
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

    // Getter/setter for consumoSala
    public void setConsumoSala(double consumoSala) {
        propertyChangeSupport.firePropertyChange("consumoSala",
            this.consumoSala, this.consumoSala = consumoSala);
    }

    public double getConsumoSala() {
        return this.consumoSala;
    }

    // End getter/setter for consumoSala

    // Getter/setter for consumoQuarto
    public void setConsumoQuarto(double consumoQuarto) {
        propertyChangeSupport.firePropertyChange("consumoQuarto",
            this.consumoQuarto, this.consumoQuarto = consumoQuarto);
    }

    public double getConsumoQuarto() {
        return this.consumoQuarto;
    }

    // End getter/setter for consumoQuarto

    // Getter/setter for consumoLavanderia
    public void setConsumoLavanderia(double consumoLavanderia) {
        propertyChangeSupport.firePropertyChange("consumoLavanderia",
            this.consumoLavanderia, this.consumoLavanderia = consumoLavanderia);
    }

    public double getConsumoLavanderia() {
        return this.consumoLavanderia;
    }

    // End getter/setter for consumoLavanderia

    // Getter/setter for consumoEscritorio
    public void setConsumoEscritorio(double consumoEscritorio) {
        propertyChangeSupport.firePropertyChange("consumoEscritorio",
            this.consumoEscritorio, this.consumoEscritorio = consumoEscritorio);
    }

    public double getConsumoEscritorio() {
        return this.consumoEscritorio;
    }

    // End getter/setter for consumoEscritorio

    // Getter/setter for consumoCozinha
    public void setConsumoCozinha(double consumoCozinha) {
        propertyChangeSupport.firePropertyChange("consumoCozinha",
            this.consumoCozinha, this.consumoCozinha = consumoCozinha);
    }

    public double getConsumoCozinha() {
        return this.consumoCozinha;
    }

    // End getter/setter for consumoCozinha

    // Getter/setter for consumoBanheiro
    public void setConsumoBanheiro(double consumoBanheiro) {
        propertyChangeSupport.firePropertyChange("consumoBanheiro",
            this.consumoBanheiro, this.consumoBanheiro = consumoBanheiro);
    }

    public double getConsumoBanheiro() {
        return this.consumoBanheiro;
    }

    // End getter/setter for consumoBanheiro

    // State variables
    public String[] getStateVariableNames() {
        return new String[] {
            "medicaoConsumo", "medicaoProducao", "consumoSala", "consumoQuarto",
            "consumoLavanderia", "consumoEscritorio", "consumoCozinha",
            "consumoBanheiro"
        };
    }

    public Object[] getStateVariableValues() {
        return new Object[] {
            medicaoConsumo, medicaoProducao, consumoSala, consumoQuarto,
            consumoLavanderia, consumoEscritorio, consumoCozinha,
            consumoBanheiro
        };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] {
            Consumo.class, Producao.class, Double.class, Double.class,
            Double.class, Double.class, Double.class, Double.class
        };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_MEDICAOCONSUMO:
                setMedicaoConsumo((Consumo) value);
                return;

            case ID_MEDICAOPRODUCAO:
                setMedicaoProducao((Producao) value);
                return;

            case ID_CONSUMOSALA:
                setConsumoSala((Double) value);
                return;

            case ID_CONSUMOQUARTO:
                setConsumoQuarto((Double) value);
                return;

            case ID_CONSUMOLAVANDERIA:
                setConsumoLavanderia((Double) value);
                return;

            case ID_CONSUMOESCRITORIO:
                setConsumoEscritorio((Double) value);
                return;

            case ID_CONSUMOCOZINHA:
                setConsumoCozinha((Double) value);
                return;

            case ID_CONSUMOBANHEIRO:
                setConsumoBanheiro((Double) value);
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
            dirUri = Residencia.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                Residencia.class.getResource(".").toString());
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
        return new String[] { "s0", "s1", "s2" };
    }
}

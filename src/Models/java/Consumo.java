/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/smartgrid_v2/src/Models/dnl/CentralEnergia.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class Consumo implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:Consumo:0
    Double value;

    //ENDIF
    public Consumo() {
    }

    public Consumo(Double value) {
        this.value = value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return this.value;
    }

    public String toString() {
        String str = "Consumo";
        str += "\n\tvalue: " + this.value;
        return str;
    }
}

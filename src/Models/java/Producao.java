/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/smartgrid/src/Models/dnl/CentralDistribuicao.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class Producao implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:Producao:0
    Double value;

    //ENDIF
    public Producao() {
    }

    public Producao(Double value) {
        this.value = value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return this.value;
    }

    public String toString() {
        String str = "Producao";
        str += "\n\tvalue: " + this.value;
        return str;
    }
}

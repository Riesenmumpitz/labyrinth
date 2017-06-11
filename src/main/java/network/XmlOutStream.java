package network;


import generated.MazeCom;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class XmlOutStream extends UTFOutputStream {

    private Marshaller marshaller;

    public XmlOutStream(OutputStream out) {
        super(out);
        // Anlegen der JAXB-Komponenten
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MazeCom.class);
            this.marshaller = jaxbContext.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Versenden einer XML Nachricht
     *
     * @param mc
     */
    public void write(MazeCom mc) {
        // generierung des fertigen XML
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            this.marshaller.marshal(mc, byteArrayOutputStream);
            // Versenden des XML
            this.writeUTF8(new String(byteArrayOutputStream.toByteArray()));
            this.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e1) {
            e1.printStackTrace();
        }
    }

}

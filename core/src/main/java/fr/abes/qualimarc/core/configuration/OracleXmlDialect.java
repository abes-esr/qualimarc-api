package fr.abes.qualimarc.core.configuration;

import org.hibernate.dialect.OracleDialect;

@BaseXMLConfiguration
public class OracleXmlDialect extends OracleDialect {
    public OracleXmlDialect() {}


    @Override
    public boolean useInputStreamToInsertBlob() {
        //This forces the use of CLOB binding when inserting
        return false;
    }
}
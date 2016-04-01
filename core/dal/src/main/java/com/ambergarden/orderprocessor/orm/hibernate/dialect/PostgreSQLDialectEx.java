package com.ambergarden.orderprocessor.orm.hibernate.dialect;

import java.util.Properties;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

/**
 * When we use generated id value without sequence name configured,
 * hibernate will try to use the default sequence name: hibernate_sequence.
 * We have several options to deal with this case:
 * 1. Configure the sequence name. That is not possible since we're using
 * a base class for id generation, entity comparation etc.
 * 2. Create hibernate_sequence sequence in database. That's not efficient,
 * as others has claimed on net.
 * 3. Do not use sequence-based generator.
 *
 * So this well-known enhancement of PostgreSQLDialect has come:
 * http://grails.1312388.n4.nabble.com/One-hibernate-sequence-is-used-for-all-Postgres-tables-td1351722.html
 *
 * FIXME: PostgreSQLDialect is deprecated. Try to find its replacement.
 */
@SuppressWarnings("deprecation")
public class PostgreSQLDialectEx extends PostgreSQLDialect {

   /**
    * Get the native identifier generator class.
    *
    * @return TableNameSequenceGenerator.
    */
   @Override
   public Class<?> getNativeIdentifierGeneratorClass() {
       return TableNameSequenceGenerator.class;
   }

   /**
    * Creates a sequence per table instead of the default behavior of one
    * sequence.
    */
   public static class TableNameSequenceGenerator extends SequenceGenerator {

       /**
        * {@inheritDoc} If the parameters do not contain a
        * {@link SequenceGenerator#SEQUENCE} name, we assign one based on the
        * table name.
        */
       @Override
       public void configure(final Type type, final Properties params, final Dialect dialect) {
           if (params.getProperty(SEQUENCE) == null || params.getProperty(SEQUENCE).length() == 0) {
               StringBuilder sb = new StringBuilder();
               String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE);

               // default PostgreSQL generated sequence name for a SERIAL or BIGSERIAL
               //      ex: COMPONENT_ID_SEQ
               sb.append(tableName);
               sb.append("_id");
               sb.append("_seq");

               if (tableName != null) {
                   params.setProperty(SEQUENCE, sb.toString());
               }
           }
           super.configure(type, params, dialect);
       }
   }
}